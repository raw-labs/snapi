package raw.cli;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class RawCli {
    public static void main(String[] args) {
//        Source sourceSnapi = null;
//        try {
//            sourceSnapi = Source.newBuilder("rql", "{a: 1+1}.a", "<stdin>").build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }


        Source sourcePython1 = null;
        try {
            sourcePython1 = Source.newBuilder("python", "1", "<stdin>").build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Source sourcePython = null;
        try {
            sourcePython = Source.newBuilder("python", """
import json
import sys
def f(data):
  json.dump(data, sys.stdout, indent=4)
""", "<stdin>").build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Context context = Context.newBuilder("python") //, "rql")
                .in(System.in)
                .out(System.out)
                .allowExperimentalOptions(true)
//                .option("rql.output-format", "json")
                .build();
        context.enter();
        try {
            Value a = context.eval(sourcePython1);
            context.eval(sourcePython);
            Value w = context.getBindings("python").getMember("f");
            long x0 = System.nanoTime();
            w.execute(a);
            long x1 = System.nanoTime();
            w.execute(2);
            long x2 = System.nanoTime();
            w.execute(3);
            long x3 = System.nanoTime();
            w.execute(10);
            long x4 = System.nanoTime();
            System.out.println();
            System.out.println(x1-x0);
            System.out.println(x2-x1);
            System.out.println(x3-x2);
            System.out.println(x4-x3);

//            int a = (int) context.eval(sourceSnapi).asInt();
//            System.out.println(a);
//            int b = (int) context.eval(sourcePython).asInt();
//            System.out.println(b);
//            int c = a + b;
//            System.out.println(c);
        } finally {
            context.leave();
            context.close();
        }
    }

//    Source sourcePython = null;
//    try {
//      sourcePython = Source.newBuilder("python", "def f(n): return n * 2\nf(1)", "<stdin>").build();
//    } catch (Exception e) {
//      e.printStackTrace();
//      return;
//    }
//    Context context = Context.newBuilder("python", "rql")
//        .in(System.in)
//        .out(System.out)
//        .allowPolyglotAccess(PolyglotAccess.ALL)
//        .allowExperimentalOptions(true)
////                .option("rql.output-format", "json")
//        .build();
//
////    context.getPolyglotBindings().putMember("aValue", 14);
//
//    try {
//      int a = (int) context.eval(sourceSnapi).asInt();
//      System.out.println(a);
//      int b = (int) context.eval(sourcePython).asInt();
//      System.out.println(b);
//      int c = a + b;
//      System.out.println(c);
//    } finally {
//      context.close();
//    }
//  }
}
