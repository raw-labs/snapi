package raw.cli;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;

public class RawCli {
    public static void main(String[] args) {
        Source sourceSnapi = null;
        try {
            sourceSnapi = Source.newBuilder("rql", "{a: 1+1}.a", "<stdin>").build();
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
                .allowExperimentalOptions(true)
//                .option("rql.output-format", "json")
                .build();
        context.enter();
        try {
            int a = (int) context.eval(sourceSnapi).asInt();
            System.out.println(a);
            int b = (int) context.eval(sourcePython).asInt();
            System.out.println(b);
            int c = a + b;
            System.out.println(c);
        } finally {
            context.leave();
            context.close();
        }
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
        .allowPolyglotAccess(PolyglotAccess.ALL)
        .allowExperimentalOptions(true)
//                .option("rql.output-format", "json")
        .build();

    context.getPolyglotBindings().putMember("aValue", 14);

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
