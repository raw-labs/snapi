package raw.snapi.db

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.client.api.ProgramEnvironment
import raw.compiler.base.CompilerContext
import raw.compiler.rql2.source.Rql2Program
import raw.compiler.rql2.{FrontendSyntaxAnalyzer, ProgramContext, TreeWithPositions}
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime.RuntimeContext
import raw.sources.api.SourceContext
import raw.utils.{InteractiveUser, RawSettings, Uid}

object SnapiDbMain extends App {

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

  parseExample()

  treeExample()

}
