from jinja2 import Environment, meta

class RawJinjaException(Exception):

   def __init__(self, message):
      self._message = message

   def message(self):
      return self._message

env = Environment()

def apply(code, args):
   template = env.from_string(code)
   d = dict([(key, args.get(key)) for key in args.keySet()])
   return template.render(d)

def validate(code):
  tree = env.parse(code)
  return list(meta.find_undeclared_variables(tree))
