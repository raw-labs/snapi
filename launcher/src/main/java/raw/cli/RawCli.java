package raw.cli;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.util.HashMap;
import java.util.Map;

public class RawCli {
    public static void main(String[] args) {
        Source sourceSnapi = null;
        try {
            sourceSnapi = Source.newBuilder("rql", "let a = 1+1 in a * 2", "<stdin>").build();
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

        Map<String, String> options = new HashMap<>();
        Context context = Context.newBuilder("python", "rql")
                .in(System.in)
                .out(System.out)
                .allowExperimentalOptions(true)
                .options(options)
                .build();

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