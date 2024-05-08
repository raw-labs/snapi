from jinja2 import Environment, meta
from markupsafe import Markup

class RawJinjaException(Exception):

   def __init__(self, message):
      self._message = message

   def message(self):
      return self._message


def fix(s):
    if isinstance(s, Markup):
        return s
    elif isinstance(s, str):
        return "'" + s.replace("'", "''") + "'"
    else:
        return s

def flag_as_safe(s):
    return Markup(s)

def flag_as_identifier(s):
    return Markup('"' + s.replace('"', '""') + '"')

env = Environment(finalize=fix, autoescape=False)
env.filters['safe'] = flag_as_safe
env.filters['identifier'] = flag_as_identifier

def _apply(code, args):
   template = env.from_string(code)
   pythonArgs = args
   for (arg, v) in args.items():
       print(arg, v.)
   return template.render(pythonArgs)

def apply(code, args):
   d = {key: args.get(key) for key in args.keySet()}
   return _apply(code, d)

def validate(code):
  tree = env.parse(code)
  return list(meta.find_undeclared_variables(tree))

def metadata_comments(code):
    return [content for (line, tipe, content) in env.lex(code) if tipe == 'comment']
