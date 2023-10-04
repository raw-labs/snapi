package raw.tests;


import com.typesafe.config.ConfigFactory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.Test;
//        import raw.compiler.rql2.truffle.Rql2TruffleCompilerService;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.RawOptions;
import raw.sources.api.SourceContext;
import scala.Option;

import raw.utils.*;
import raw.runtime.*;
import raw.compiler.api.*;
import scala.Some;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;
import scala.collection.immutable.Seq$;
import scala.collection.immutable.Set;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MyInternalTest {

    @Test
    public void testSub() {
        RawSettings rawSettings =  new RawSettings(ConfigFactory.load(), ConfigFactory.empty());

        // Set user from environment variable.
        String uid = "uid";
        InteractiveUser user = new InteractiveUser(uid, uid, uid, (Seq<String>) Seq$.MODULE$.empty());

        ClassLoader classLoader = RawLanguage.class.getClassLoader();

        // Set traceId from environment variable.
        String traceId = "";

        // Set scopes from environment variable.
        String scopesStr = "";
        String[] scopes = (scopesStr == null || scopesStr.isEmpty()) ? new String[0] : scopesStr.split(",");

        // Create source context.
        CredentialsService credentialsService = CredentialsServiceProvider.apply(classLoader, rawSettings);
        SourceContext sourceContext = new SourceContext(user, credentialsService, rawSettings);

        // Create program environment.
        Set<String> scalaScopes =
                JavaConverters.asScalaSetConverter(java.util.Set.of(scopes)).asScala().toSet();

        java.util.Map<String, String> javaOptions = new java.util.HashMap<>();
        javaOptions.put("output-format", "json");

        Map<String, String> scalaOptions =
                JavaConverters.mapAsScalaMapConverter(javaOptions)
                        .asScala()
                        .toMap(scala.Predef.<scala.Tuple2<String, String>>conforms());

        Option<String> maybeTraceId = traceId != null ? Some.apply(traceId) : Option.empty();

        ProgramEnvironment programEnvironment = new ProgramEnvironment(user, scalaScopes, scalaOptions, maybeTraceId);

        CompilerService compilerService = CompilerServiceProvider.apply(rawSettings);
        try {
    /*
    user: AuthenticatedUser,
    scopes: Set[String],
    options: Map[String, String],
    maybeTraceId: Option[String] = None
     */
            compilerService.execute("1+1", Option.empty(), programEnvironment, Option.empty(), System.out);
        } catch (CompilerServiceException e) {
            throw new RuntimeException(e);
        } finally {
            //compilerService.close()
        }
    }

}