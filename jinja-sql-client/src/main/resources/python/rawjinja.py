import java
from datetime import datetime, time
from jinja2 import Environment, meta, nodes
from jinja2.ext import Extension
from jinja2.exceptions import TemplateRuntimeError
from markupsafe import Markup

class RaiseExtension(Extension):
    # This is our keyword(s):
    tags = set(['raise'])

    # See also: jinja2.parser.parse_include()
    def parse(self, parser):
        # the first token is the token that started the tag. In our case we
        # only listen to "raise" so this will be a name token with
        # "raise" as value. We get the line number so that we can give
        # that line number to the nodes we insert.
        lineno = next(parser.stream).lineno

        # Extract the message from the template
        message_node = parser.parse_expression()

        return nodes.CallBlock(
            self.call_method('_raise', [message_node], lineno=lineno),
            [], [], [], lineno=lineno
        )

    def _raise(self, msg, caller):
        raise TemplateRuntimeError(msg)

javaLocalDateClass = java.type('java.time.LocalDate')
javaLocalTimeClass = java.type('java.time.LocalTime')
javaLocalDateTimeClass = java.type('java.time.LocalDateTime')

class RawDate(datetime):
    pass

class RawTimestamp(datetime):
    pass

class RawTime(time):
    pass

class RawJinjaException(Exception):

   def __init__(self, message):
      self._message = message

   def message(self):
      return self._message

def fix(val):
    if isinstance(val, Markup):
        return val
    elif isinstance(val, str):
        return "'" + val.replace("'", "''") + "'"
    elif isinstance(val, RawDate):
        return "make_date(%d,%d,%d)" % (val.year, val.month, val.day)
    elif isinstance(val, RawTime):
        return "make_time(%d,%d,%d + %d / 1000000.0)" % (val.hour, val.minute, val.second, val.microsecond)
    elif isinstance(val, RawTimestamp):
        return "make_timestamp(%d,%d,%d,%d,%d,%f)" % (val.year, val.month, val.day, val.hour, val.minute, val.second + val.microsecond / 1000000.0)
    else:
        return val

def flag_as_safe(s):
    return Markup(s)

def flag_as_identifier(s):
    return Markup('"' + s.replace('"', '""') + '"')

env = Environment(finalize=fix, autoescape=False, extensions=[RaiseExtension])
env.filters['safe'] = flag_as_safe
env.filters['identifier'] = flag_as_identifier

def _apply(code, args):
   template = env.from_string(code)
   return template.render(args)

def apply(code, args):
   d = {key: _toPython(args.get(key)) for key in args.keySet()}
   return _apply(code, d)

def _toPython(arg):
    if isinstance(arg, javaLocalDateClass):
        return RawDate(arg.getYear(), arg.getMonthValue(), arg.getDayOfMonth())
    if isinstance(arg, javaLocalTimeClass):
        return RawTime(arg.getHour(), arg.getMinute(), arg.getSecond(), int(arg.getNano() / 1000))
    if isinstance(arg, javaLocalDateTimeClass):
        return RawTimestamp(arg.getYear(), arg.getMonthValue(), arg.getDayOfMonth(),
                        arg.getHour(), arg.getMinute(), arg.getSecond(), int(arg.getNano()/ 1000))
    return arg

def validate(code):
  tree = env.parse(code)
  return list(meta.find_undeclared_variables(tree))

def metadata_comments(code):
    return [content for (line, tipe, content) in env.lex(code) if tipe == 'comment']
