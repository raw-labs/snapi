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

class RawEnvironment:

    def __init__(self, scopes, getSecret):
        self.scopes = scopes
        self._getSecret = getSecret

    def secret(self,s):
        return self._getSecret(s)

class RawJinja:

    def __init__(self):
        self._env = Environment(finalize=lambda x: self.fix(x), autoescape=False, extensions=[RaiseExtension])
        self._env.filters['safe'] = self.flag_as_safe
        self._env.filters['identifier'] = self.flag_as_identifier
        # a default env to make sure 'environment' is predefined
        self._env.globals['environment'] = RawEnvironment(None, None)

    def fix(self, val):
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
        elif isinstance(val, list):
            items = ["(" + self.fix(i) + ")" for i in val]
            return "(VALUES " + ",".join(items) + ")"
        elif val is None:
            return "NULL"
        else:
            return val

    def flag_as_safe(self, s):
        return Markup(s)

    def flag_as_identifier(self, s):
        return Markup('"' + s.replace('"', '""') + '"')


    def _apply(self, code, args):
       template = self._env.from_string(code)
       return template.render(args)

    def apply(self, code, scopes, secret, args):
       d = {key: self._toPython(args.get(key)) for key in args.keySet()}
       print(dir(scopes))
       print(dir(secret))
       d['environment'] = RawEnvironment([s for s in scopes.iterator()], lambda s: secret.apply(s))
       return self._apply(code, d)

    def _toPython(self, arg):
        if isinstance(arg, javaLocalDateClass):
            return RawDate(arg.getYear(), arg.getMonthValue(), arg.getDayOfMonth())
        if isinstance(arg, javaLocalTimeClass):
            return RawTime(arg.getHour(), arg.getMinute(), arg.getSecond(), int(arg.getNano() / 1000))
        if isinstance(arg, javaLocalDateTimeClass):
            return RawTimestamp(arg.getYear(), arg.getMonthValue(), arg.getDayOfMonth(),
                            arg.getHour(), arg.getMinute(), arg.getSecond(), int(arg.getNano()/ 1000))
        return arg

    def validate(self, code):
      tree = self._env.parse(code)
      return list(meta.find_undeclared_variables(tree))

    def metadata_comments(self, code):
        return [content for (line, tipe, content) in self._env.lex(code) if tipe == 'comment']
