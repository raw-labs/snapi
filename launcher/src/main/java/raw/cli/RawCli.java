package raw.cli;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;

public class RawCli {
  public static void main(String[] args) {
    Source sourceSnapi = null;
    try {
      sourceSnapi = Source.newBuilder("rql", "Environment.Parameter(type int, \"a\")", "<stdin>").build();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    Source sourcePython = null;
    try {
      sourcePython = Source.newBuilder("python", "def f(n): return n * 2\nf(1)", "<stdin>").build();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    Context context = Context.newBuilder("python", "rql")
        .in(System.in)
        .out(System.out)
//        .allowPolyglotAccess(PolyglotAccess.ALL)
        .allowExperimentalOptions(true)
//                .option("rql.output-format", "json")
        .build();

    context.getPolyglotBindings().putMember("a", 1);

    try {
      int a = (int) context.eval(sourceSnapi).asInt();
      System.out.println(a);
      int b = (int) context.eval(sourcePython).asInt();
      System.out.println(b);
      int c = a + b;
      System.out.println(c);
    } finally {
      context.close();
    }
  }
}