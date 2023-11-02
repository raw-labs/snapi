package raw.snapi.db

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.util.Positions
import raw.client.api.ProgramEnvironment
import raw.compiler.base.CompilerContext
import raw.compiler.common.source.SourceProgram
import raw.compiler.rql2.source.{FunBody, FunProto, Rql2Program, SourcePrettyPrinter}
import raw.compiler.rql2.{FrontendSyntaxAnalyzer, ProgramContext, TreeWithPositions}
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime.RuntimeContext
import raw.sources.api.SourceContext
import raw.utils.{InteractiveUser, RawSettings, Uid}

import scala.collection.mutable

object SnapiDbMain extends App with StrictLogging {

  def parseExample() = {
    val source = "1+1"

    val positions = new Positions
    val parser = new FrontendSyntaxAnalyzer(positions)

    val maybeResult = parser.parse(source)
    val result = maybeResult.right.get
    println(result)
  }

  def treeExample() = {
    val user = InteractiveUser(Uid("uid"), "name", "email", Seq.empty)

    val settings = new RawSettings
    val credentialsService = CredentialsServiceProvider()(settings)

    val sourceContext = new SourceContext(user, credentialsService, settings, None)

    val language = "rql2-truffle"
    val inferrer = InferrerServiceProvider()(sourceContext)
    val _compilerContext =
      new CompilerContext(language, user = user, inferrer = inferrer, sourceContext = sourceContext, None)(settings)
    val maybeArguments = None
    val scopes = Set.empty[String]
    val options = Map.empty[String, String]
    val maybeTraceId = None
    val programEnvironment = ProgramEnvironment(user, maybeArguments, scopes, options, maybeTraceId)

    val _runtimeContext = new RuntimeContext(sourceContext, settings, programEnvironment)
    val programContext = new ProgramContext {
      override def runtimeContext: RuntimeContext = _runtimeContext

      override def compilerContext: CompilerContext = _compilerContext
    }

    val source = "1+1"

    val tree = new TreeWithPositions(source)(programContext)
    println(tree.root)

    val analyzer = tree.analyzer
    val Rql2Program(decls, me) = tree.root

    println(analyzer.tipe(me.get))
  }

  def getTree(source: String) = {
    val user = InteractiveUser(Uid("uid"), "name", "email", Seq.empty)

    val settings = new RawSettings
    val credentialsService = CredentialsServiceProvider()(settings)

    val sourceContext = new SourceContext(user, credentialsService, settings, None)

    val language = "rql2-truffle"
    val inferrer = InferrerServiceProvider()(sourceContext)
    val _compilerContext =
      new CompilerContext(language, user = user, inferrer = inferrer, sourceContext = sourceContext, None)(settings)
    val maybeArguments = None
    val scopes = Set.empty[String]
    val options = Map.empty[String, String]
    val maybeTraceId = None
    val programEnvironment = ProgramEnvironment(user, maybeArguments, scopes, options, maybeTraceId)

    val _runtimeContext = new RuntimeContext(sourceContext, settings, programEnvironment)
    val programContext = new ProgramContext {
      override def runtimeContext: RuntimeContext = _runtimeContext

      override def compilerContext: CompilerContext = _compilerContext
    }

    new TreeWithPositions(source)(programContext)
  }

  final case class SemanticHole(pre: String, mid: String, suf: String)

  def getSemanticHoles(tree: TreeWithPositions): Seq[SemanticHole] = {
    val program = tree.root
    val source = tree.originalSource

    val semanticHoles = mutable.ArrayBuffer[SemanticHole]()

    everywhere(query[Any] {
      case f: FunBody =>
        val start = tree.positions.getStart(f).get
        val finish = tree.positions.getFinish(f).get

        // Extract prettyPrintedProgram until the start.line AND start.column

        val pre = source
          .split("\n")
          .take(start.line - 1)
          .zipWithIndex
          .map { case (l, i) => if (i == start.line - 1) l.take(start.column - 1) else l }
          .mkString("\n")

        val post = source
          .split("\n")
          .drop(finish.line)
          .zipWithIndex
          .map { case (l, i) => if (i == finish.line - 1) l.drop(finish.column) else l }
          .mkString("\n")

        val mid = source
          .split("\n")
          .slice(start.line - 1, finish.line)
          .zipWithIndex
          .map {
            case (l, i) =>
              if (i == start.line) l.drop(start.column) else if (i == finish.line) l.take(finish.column) else l
          }
          .mkString("\n")

        // Remove characters from last line of pre until start.column
        logger.debug(s"Pre is:\n```\n$pre\n```")
        logger.debug(s"Post is:\n```\n$post\n```")
        logger.debug(s"Mid is:\n```\n$mid\n```")

        semanticHoles.append(SemanticHole(pre, mid, post))
    })(program)

    semanticHoles
  }

  getSemanticHoles(getTree("""let f(v: int) =
    |  let x = v + 1
    |  in x * 2
    |in f(1)
    |""".stripMargin))

}
