import java
from datetime import datetime
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
    elif isinstance(val, datetime):
        return "DATE '%d-%02d-%02d'" % (val.year, val.month, val.day)
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
        return datetime(arg.getYear(), arg.getMonthValue(), arg.getDayOfMonth())
    return arg

def validate(code):
  tree = env.parse(code)
  return list(meta.find_undeclared_variables(tree))

def metadata_comments(code):
    return [content for (line, tipe, content) in env.lex(code) if tipe == 'comment']
