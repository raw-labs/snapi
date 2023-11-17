package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.tests.CompilerTestContext

trait BrokenCodeTest extends CompilerTestContext {

  test("""let a in a""")(_ shouldNot run)

}

