package raw.launcher

import raw.runtime.ProgramEnvironment
import raw.compiler.api.CompilerServiceProvider
import raw.utils.{RawSettings, InteractiveUser, Uid}

object RawCompilerLauncher extends App {

  println("Hello, world!")

  val rawSettings = new RawSettings()

  val compilerService = CompilerServiceProvider.apply(None)(rawSettings)
  try {
    /*
    user: AuthenticatedUser,
    scopes: Set[String],
    options: Map[String, String],
    maybeTraceId: Option[String] = None
     */
    compilerService.execute("1,1", None, ProgramEnvironment(InteractiveUser(Uid("uid"), "name", "email", Seq.empty), Set.empty, Map.empty, None), None, System.out)
  } finally {
    //compilerService.close()
  }
//
//  val sourceSnapi = Source.newBuilder("rql", "{a: 1+1}.a", "<stdin>").build()
//
//  val sourcePython = Source.newBuilder("python", "def f(n): return n * 2\nf(1)", "<stdin>").build()
//  val context = Context
//    .newBuilder("python", "rql")
//    .in(System.in)
//    .out(System.out)
//    .allowExperimentalOptions(true)
//    //                .option("rql.output-format", "json")
//    .build()
//
//  try {
//    val a = context.eval(sourceSnapi).asInt()
//    println(a)
//    val b = context.eval(sourcePython).asInt()
//    println(b)
//    val c = a + b
//    println(c)
//  } finally {
//    context.close()
//  }

}
