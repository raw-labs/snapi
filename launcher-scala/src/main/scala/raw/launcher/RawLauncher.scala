package raw.launcher

import org.graalvm.polyglot.{Context, Source}

object RawLauncher extends App {

  println("Hello, world!")

  val sourceSnapi = Source.newBuilder("rql", "{a: 1+1}.a", "<stdin>").build()

  val sourcePython = Source.newBuilder("python", "def f(n): return n * 2\nf(1)", "<stdin>").build()
  val context = Context
    .newBuilder("python", "rql")
    .in(System.in)
    .out(System.out)
    .allowExperimentalOptions(true)
    //                .option("rql.output-format", "json")
    .build()

  try {
    val a = context.eval(sourceSnapi).asInt()
    println(a)
    val b = context.eval(sourcePython).asInt()
    println(b)
    val c = a + b
    println(c)
  } finally {
    context.close()
  }

}
