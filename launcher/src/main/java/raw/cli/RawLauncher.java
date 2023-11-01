/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.cli;

import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raw.client.api.*;
import raw.utils.AuthenticatedUser;
import raw.utils.InteractiveUser;
import raw.utils.RawException;
import raw.utils.RawSettings;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.immutable.*;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.util.HashMap;
import java.io.PrintWriter;

public class RawLauncher implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(RawLauncher.class);

    private final PrintWriter writer;
    private CompilerService compilerService;
    private final ProgramEnvironment env;

    public RawLauncher(PrintWriter writer) {
        this.writer = writer;

        RawSettings rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());

        this.compilerService = CompilerServiceProvider.apply("snapi", rawSettings);
        AuthenticatedUser user = new InteractiveUser("uid", "name", "email", (Seq<String>) Seq$.MODULE$.empty());

        HashMap<String, String> javaOptions = new HashMap<String, String>();
        javaOptions.put("output-format", "json");

        Map<String, String> scalaOptions =
                JavaConverters.mapAsScalaMapConverter(javaOptions)
                        .asScala()
                        .toMap(scala.Predef.<scala.Tuple2<String, String>>conforms());

        this.env = new ProgramEnvironment(user, Option.empty(), (Set<String>) Set$.MODULE$.empty(), scalaOptions, Option.empty());

    }

    public void execute(String query) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ExecutionResponse response = compilerService.execute(query, env, Option.empty(), baos);
            switch (response) {
                case ExecutionSuccess$ executionSuccess$ -> {
                    writer.println(baos);
                    writer.flush();
                }
                case ExecutionValidationFailure failure -> {
                    List<ErrorMessage> errors = failure.errors();
                    printErrorMessages(errors);
                }
                case ExecutionRuntimeFailure failure -> {
                    String error = failure.error();
                    printError(error);
                }
                case null, default -> throw new AssertionError("unknown response type: " + response.getClass());
            }
        } catch (RawException ex) {
            printError(ex.getMessage());
        }
    }

    private void printError(String message) {
        // TODO (msb): This is probably not the correct way to handle terminal colors with jline3?
        writer.print("\u001b[31m");
        writer.print(message);
        writer.println("\u001b[0m");
        writer.flush();
    }

    private void printErrorMessages(List<ErrorMessage> errors) {
        errors.foreach(err -> {
            String preamble = JavaConverters.asJavaCollection(err.positions())
                    .stream()
                    .map(p -> {
                        if (p.begin().equals(p.end())) {
                            return p.begin().line() + ":" + p.begin().column();
                        } else {
                            return p.begin().line() + ":" + p.begin().column() + "-" + p.end().line() + ":" + p.end().column();
                        }
                    })
                    .collect(java.util.stream.Collectors.joining("-"));
            printError(preamble + " -> " + err.message());
            return null;
        });
    }

    @Override
    public void close() {
        if (compilerService != null) {
            compilerService.stop();
            compilerService = null;
        }
    }
}
