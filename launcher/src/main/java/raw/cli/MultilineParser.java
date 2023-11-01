//package raw.cli;
//
//import org.jline.reader.ParsedLine;
//import org.jline.reader.impl.DefaultParser;
//import raw.client.api.CompilerService;
//
///**
// * A parser that allows for multi-line input.
// * Essentially, it checks if the parse error is in the last position. If so, we assume the program is incomplete so "keep
// * going" and ask for more input. If the error is not on the last position, the program is surely broken, so we stop early.
// * In jline3, as far as I understand, one throws EOFError exception if the parser is incomplete.
// */
//public class MultilineParser extends DefaultParser {
//
//    private CompilerService compilerService;
//
//    public MultilineParser(CompilerService compilerService) {
//        this.compilerService = compilerService;
//    }
//
//    @Override
//    ParsedLine parse(String line, int cursor, ParseContext context) {
//
//        compilerService.
//
//        compilerService.parse(line)
//
//
//      case Success(result: Rql2Program, _) =>
//        // If success, let's just check it is a program with an expression.
//        // If so, we keep going and ask for more input.
//        // This handles cases like:
//        // f(v: int) = v + 1
//        // ... where it isn't executable by itself.
//        if (result.me.isDefined) {
//          // A valid program with a top-level expression.
//          super.parse(line, cursor, context)
//        } else {
//          // No top-level expression found, e.g. 'f(v: int) = v + 1'
//          throw new CompilerParserException("no top-level expression found", new ErrorPosition(0, 0))
//        }
//      case err =>
//        val (message, rest) = err match {
//          case Failure(message, rest) => (message, rest)
//          case Error(message, rest) => (message, rest)
//        }
//
//        if (rest.atEnd) {
//          if (message.contains("end of source found")) {
//            // An incomplete program, so let's ask for more input.
//            throw new EOFError(-1, -1, "multi-line command")
//          } else {
//            // An invalid program but no point in processing it further, so let's accept and let it fail.
//            super.parse(line, cursor, context)
//          }
//        } else {
//          // An invalid program but no point in processing it further, so let's accept and let it fail.
//          super.parse(line, cursor, context)
//        }
//    }
//  }
//}