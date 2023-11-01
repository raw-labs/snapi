package raw.cli;

import org.jline.terminal.Terminal;
import raw.utils.*;
import raw.client.api.*;
import com.typesafe.config.ConfigFactory;
import scala.Option;
import scala.collection.Seq;
import scala.collection.immutable.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.Parser.ParseContext;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import scala.collection.JavaConverters;

public class RawCli {

    private static final Logger logger = LoggerFactory.getLogger(RawCli.class);

    public static void main(String[] args) {

        Foo foo = new Foo();

        Terminal terminal = null;
        PrintWriter writer= null;
        try {
            terminal = TerminalBuilder.terminal();
            writer = terminal.writer();

            LineReader reader = LineReaderBuilder.builder().terminal(terminal).completer(new StringsCompleter(".exit", ".csv", ".json", ".help")).parser(new MultilineParser(compilerService)).variable(LineReader.SECONDARY_PROMPT_PATTERN, "") .variable(LineReader.LIST_MAX, 100).build();

            writer.println("Welcome to the Raw REPL! Type .help for more information.");
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        logger.info("Hello world");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PrintStream printStream = new PrintStream(outputStream);

        try {
            try {
                ExecutionResponse response = compilerService.execute(
                        "1+1",
                        env,
                        Option.empty(),
                        outputStream
                );

                if (response instanceof ExecutionSuccess$) {
                    logger.info("Execution was successful.");
                } else if (response instanceof ExecutionValidationFailure) {
                    ExecutionValidationFailure failure = (ExecutionValidationFailure) response;
                    List<ErrorMessage> errors = failure.errors();
                    logger.error("Validation failed with errors:");
                    for (ErrorMessage error : JavaConverters.asJavaCollection(errors)) {
                        logger.error(error.toString()); // Adjust based on the structure of ErrorMessage
                    }
                } else if (response instanceof ExecutionRuntimeFailure) {
                    ExecutionRuntimeFailure failure = (ExecutionRuntimeFailure) response;
                    String error = failure.error();
                    logger.error("Runtime failure: " + error);
                } else {
                    throw new AssertionError("unknown response type: " + response.getClass());
                }

//                printStream.flush();
                outputStream.flush();
//                outputStream.close();
                String output = outputStream.toString();
                logger.info("==> " + output);

            } catch (CompilerServiceException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            compilerService.stop();
        }
    }


//        Source sourceSnapi = null;
//        try {
//            sourceSnapi = Source.newBuilder("rql", "{a: 1+1}.a", "<stdin>").build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

//
//        Source sourcePython1 = null;
//        try {
//            sourcePython1 = Source.newBuilder("python", "1", "<stdin>").build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//
//        Source sourcePython = null;
//        try {
//            sourcePython = Source.newBuilder("python", """
//import json
//import sys
//def f(data):
//  json.dump(data, sys.stdout, indent=4)
//""", "<stdin>").build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        Context context = Context.newBuilder("python") //, "rql")
//                .in(System.in)
//                .out(System.out)
//                .allowExperimentalOptions(true)
////                .option("rql.output-format", "json")
//                .build();
//        context.enter();
//        try {
//            Value a = context.eval(sourcePython1);
//            context.eval(sourcePython);
//            Value w = context.getBindings("python").getMember("f");
//            long x0 = System.nanoTime();
//            w.execute(a);
//            long x1 = System.nanoTime();
//            w.execute(2);
//            long x2 = System.nanoTime();
//            w.execute(3);
//            long x3 = System.nanoTime();
//            w.execute(10);
//            long x4 = System.nanoTime();
//            System.out.println();
//            System.out.println(x1-x0);
//            System.out.println(x2-x1);
//            System.out.println(x3-x2);
//            System.out.println(x4-x3);
//
////            int a = (int) context.eval(sourceSnapi).asInt();
////            System.out.println(a);
////            int b = (int) context.eval(sourcePython).asInt();
////            System.out.println(b);
////            int c = a + b;
////            System.out.println(c);
//        } finally {
//            context.leave();
//            context.close();
//        }
//    }
//
////    Source sourcePython = null;
////    try {
////      sourcePython = Source.newBuilder("python", "def f(n): return n * 2\nf(1)", "<stdin>").build();
////    } catch (Exception e) {
////      e.printStackTrace();
////      return;
////    }
////    Context context = Context.newBuilder("python", "rql")
////        .in(System.in)
////        .out(System.out)
////        .allowPolyglotAccess(PolyglotAccess.ALL)
////        .allowExperimentalOptions(true)
//////                .option("rql.output-format", "json")
////        .build();
////
//////    context.getPolyglotBindings().putMember("aValue", 14);
////
////    try {
////      int a = (int) context.eval(sourceSnapi).asInt();
////      System.out.println(a);
////      int b = (int) context.eval(sourcePython).asInt();
////      System.out.println(b);
////      int c = a + b;
////      System.out.println(c);
////    } finally {
////      context.close();
////    }
////  }
}
