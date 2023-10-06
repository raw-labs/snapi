package raw.tests;


        import org.graalvm.polyglot.Context;
        import org.graalvm.polyglot.Source;
        import org.junit.jupiter.api.Test;
//        import raw.compiler.rql2.truffle.Rql2TruffleCompilerService;

        import static org.junit.jupiter.api.Assertions.*;

public class MyTest {

    @Test
    public void testAdd() {

        Source sourceSnapi = null;
        try {
            sourceSnapi = Source.newBuilder("rql", "{a: 1+1}.a", "<stdin>").build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Context context = Context.newBuilder( "rql")
                .in(System.in)
                .out(System.out)
                .allowExperimentalOptions(true)
//                .option("rql.output-format", "json")
                .build();
        context.enter();
        try {
            int a = (int) context.eval(sourceSnapi).asInt();
            assert(a == 2);
        } finally {
            context.leave();
            context.close();
        }

//        Rql2TruffleCompilerService compiler = new Rql2TruffleCompilerService();
//        compiler.execute("{a: 1+1}.a", Option.empty());
//
//        Calculator calculator = new Calculator();
//        int result = calculator.add(3, 4);
//        assertEquals(7, result);
    }

}