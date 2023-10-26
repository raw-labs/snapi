package raw.tests;


        import org.graalvm.polyglot.Context;
        import org.graalvm.polyglot.Source;
        import org.graalvm.polyglot.Value;
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

        String source = """
def f(): return 2

def g(a=1, b=2): return v                
                
                
                """;

//        String source = """
//import ast
//
//class FunctionVisitor(ast.NodeVisitor):
//    def visit_FunctionDef(self, node):
//        print(f"Function name: {node.name}")
//        for arg in node.args.args:
//            arg_name = arg.arg
//            arg_type = ast.unparse(arg.annotation) if arg.annotation else "No annotation"
//            print(f"Argument: {arg_name}, Type annotation: {arg_type}")
//
//        # If you want to parse nested functions as well
//        self.generic_visit(node)
//
//def get_function_annotations(filename):
//    with open(filename, "r") as source:
//        tree = ast.parse(source.read(), filename=filename)
//        FunctionVisitor().visit(tree)
//
//# Test the function with the path to a Python file
//get_function_annotations("/Users/miguel/path_to_your_python_file.py")
//                """;

        Source sourcePython = null;
        try {
            sourcePython = Source.newBuilder("python", source, "<stdin>").build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        Context context = Context.newBuilder( "rql", "python")
                .in(System.in)
                .out(System.out)
                .allowExperimentalOptions(true)
                .allowAllAccess(true)
//                .option("rql.output-format", "json")
                .build();
        context.enter();
        try {
            Value v = context.eval(sourcePython);
            v.getMember("g").

            System.out.println("**** " + v.getMember("g").execute(4));
//            int a = (int) v.asInt();
//            System.out.println(a);
//            assert(a == 3);
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