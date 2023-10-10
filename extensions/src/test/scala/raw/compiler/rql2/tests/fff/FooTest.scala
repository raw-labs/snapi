package raw.compiler.rql2.tests.fff

//import raw.compiler.rql2.source._
import raw.compiler.rql2.source.Rql2StringType
import raw.compiler.rql2.tests.TruffleCompilerTestContext

class FooTest extends TruffleCompilerTestContext {

//  test(""" let x: string = "Hello" in x """) { it => it should run}

//  test("1") { it => it should run}


//  test("1/0") { it => it should run}

//  test(
//    """if true then 1 else 1/0""") { it => it should run}


  test("""Decimal.From(1.13) """) { it =>
    it should run
  }


//  test("let x: int = 1 in x") { it =>
//    it should run
////    it should typeAs("int")
//////    it should astTypeAs(Rql2IntType())
////    it should evaluateTo("1")
//  }

}
