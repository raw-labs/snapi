from jinja2 import Environment, meta

class RawJinjaException(Exception):

   def __init__(self, message):
      self._message = message

   def message(self):
      return self._message


class Safe:

    def __init__(self, s):
        self._value = s

    def value(self):
        return self._value

def fix(s):
    print("Fixing: %s" % s)
    if isinstance(s, Safe):
        return s.value()
    else:
        return "'" + s.replace("'", "''") + "'"

def do_safe(s):
    return Safe(s)

env = Environment(finalize=fix, autoescape=False)
env.filters['safe'] = do_safe

def _apply(code, args):
   template = env.from_string(code)
   return template.render(args)

def apply(code, args):
   d = {key: args.get(key) for key in args.keySet()}
   return _apply(d)

def validate(code):
  tree = env.parse(code)
  return list(meta.find_undeclared_variables(tree))
