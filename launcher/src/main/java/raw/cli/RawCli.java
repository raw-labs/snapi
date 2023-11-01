package raw.cli;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.*;

import java.io.IOException;
import java.io.PrintWriter;

public class RawCli {

    public static void main(String[] args) {

        try {
            Terminal terminal = TerminalBuilder.terminal();
            PrintWriter writer = terminal.writer();

            RawLauncher rawLauncher = new RawLauncher(writer);

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter(".exit", ".csv", ".json", ".help"))
//                    .parser(new MultilineParser(compilerService))
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "")
                    .variable(LineReader.LIST_MAX, 100)
                    .build();

            writer.println("Welcome to the Raw REPL! Type .help for more information.");
            writer.flush();

            boolean done = false;
            while (!done) {
                String line = reader.readLine("raw> ");
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                switch (line) {
                    case ".q" -> done = true;
                    case ".quit" -> done = true;
                    case ".exit" -> done = true;
                    case ".help" -> {
                        writer.println("Help is not implemented yet.");
                        writer.flush();
                    }
                    case ".csv" -> {
                        writer.println("CSV output is not implemented yet.");
                        writer.flush();
                    }
                    case ".json" -> {
                        writer.println("JSON output is not implemented yet.");
                        writer.flush();
                    }
                    default -> {
                        reader.getHistory().add(line);
                        rawLauncher.execute(line);
                    }
                }
            }
        } catch (EndOfFileException e) {
            // Exit gracefully and quietly.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
