import raw_jinja

# def test_basic():
#     rawJinja = raw_jinja.RawJinja()
#     result = rawJinja.test()
#     print(result)
#     assert result == """SELECT **        2    AS v"""

import unittest

class TestJinja(unittest.TestCase):

    def test_upper(self):
        rawJinja = raw_jinja.RawJinja()
        code = """{% if False %}
SELECT {{ 1 }} AS v
{% else %}
SELECT ** {{     2 }} AS v
{% endif %}"""
        result = rawJinja._apply(code, {})
        unittest.TestCase.assertEqual(self, result, """SELECT **        2    AS v""")