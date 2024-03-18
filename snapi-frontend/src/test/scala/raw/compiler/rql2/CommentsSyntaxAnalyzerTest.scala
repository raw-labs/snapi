package raw.compiler.rql2

import raw.compiler.base.source.BaseNode
import raw.compiler.rql2.antlr4.CommentsAntlrSyntaxAnalyzer
import raw.utils.RawTestSuite

import java.util

class CommentsSyntaxAnalyzerTest extends RawTestSuite {

  test("VisitorCommentsTest") { _ =>
   val s  =
     """// a cool comment
       |let
       | // another cool comment
       |  a = 1 // yet another cool comment
       |// and another cool comment
       |in
       |  // this is getting old
       |  a""".stripMargin
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val nodeComments = new  util.IdentityHashMap[BaseNode, NodeComments]()
    val parser = new CommentsAntlrSyntaxAnalyzer(positions, false, nodeComments)

    parser.parse(s)

    println(nodeComments)
  }
}
