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

package raw.runtime.truffle;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import java.io.OutputStream;

import com.typesafe.config.ConfigFactory;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.creds.api.Secret;
import raw.runtime.ParamByte;
import raw.runtime.ParamValue;
import raw.runtime.ProgramEnvironment;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.function.FunctionRegistry;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.InteractiveUser;
import raw.utils.RawSettings;
import scala.Option;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.immutable.*;

public final class RawContext {

  private final RawLanguage language;
  private final Env env;
  private final FunctionRegistry functionRegistry;
  private OutputStream output;
  private SourceContext sourceContext;
  private RuntimeContext runtimeContext;
  private AuthenticatedUser user;

  public RawContext(RawLanguage language, Env env) {
    this.language = language;
    this.env = env;
    this.output = env.out();
    this.functionRegistry = new FunctionRegistry();

    AuthenticatedUser authenticatedUser = null;
    RawSettings rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
    CredentialsService credentialsService = CredentialsServiceProvider.apply(rawSettings);

    this.sourceContext = new SourceContext(authenticatedUser, credentialsService, rawSettings);

    env.getConfig();
    env.getEnvironment();

    // Set user from environment variable.
    String uid = env.getEnvironment().get("RAW_USER");
    this.user = new InteractiveUser(uid, uid, uid, (Seq<String>) Seq$.MODULE$.empty());


    Map<String, String> emptyMap = Map$.MODULE$.empty();
    Map<String, String> updatedMap = emptyMap.$plus(new Tuple2<>("output-format", ""));
    ProgramEnvironment programEnvironment = new ProgramEnvironment(authenticatedUser,  (Set<String>) Set$.MODULE$.empty(), updatedMap, Option.empty());

    /*

# json



--
for the test suite
        generate and run user program as-is, without changing the source code in anyway (not to mess up positions!)
        then test suite separately generates the writer program and passses the value in
        needs to return functions so that we eval the function and pass the original value + outputstream in there

// Eval the user code
val result = context.eval(new SnapiSource(userCode))
  // result is now a true interop object with the data

// Another separate eval to print the JSON to the output stream
context.executeVoid(new SnapiWriterSource("Json.Write"), typeStr, result, outputStream)
  // assumes there's a new Json.Write function that takes 2 arguments: the value and the output stream to write it to

Challenges:
1) Need to make all our data types interop
2) Need to create a new language for writer programs
--
main(os):
  print_json(os, 1)

Value f = context.eval("<src>")
f.executeVoid(outpustream)

---
1)
main() = 1

2)
main() = 1

$main() = print_json(main())
---
1)
main() = 1
main()

2)
main() = 1
main()
# json




--

    Rql2PRogram(

print_json(
)

    problems:
    - outputformat ===> put in AST
    - options: Map[String, String], ===> use getConfig
    - maybeArguments ===> use getPolyglotBindings?
    - user: AuthenticatedUser, ===> use ENV variable ===> Helper in RawContext
    - scopes: Set[String], ===> use ENV variable ===> Helper in RawContext
    - maybeTraceId: Option[String] = None ===> use getConfig ===> Add helper to RawContext


    but the issue is that it's not just RawContext.
    It's also RawLanguage!!!




    for outputformat, well, we can change the AST to say the outputformat (to say it is to write)
    at emit time, match that and generate the node. should work
      more tests should pass

    but tests with arguments should still fail.

    for maybeArguments
    do getPolyglotBindings perhaps?
    then change the implementation to read it from there. create nodes that read getPolyglotBindings or what not.

    check the 'arguments' thing

    for options, do getConfig

    then remove cloneWith stuff? still needed for scala2!!!

    ---

  next step is to rduce use of getRuntimeContext where it is not needed. to simplfy the issue.
  then I guess  Icould use env vars
  USER
  SCOPES_xx
  OPTIONS_xx -> no.. there's better for this
  TRACE_ID -> fine; can be env var
  arguments and output?


     */
//this.env.getPolyglotBindings()
    this.runtimeContext = new RuntimeContext(sourceContext, rawSettings, Option.empty(), programEnvironment);
//
//    this.getOutput()
//
//    env.getConfig()
//
//            env.out()
//
//    env.createTempDirectory()
//
//            env.getApplicationArguments()
//
//    String uid = env.
//    String name = uid;
//    String email = uid;
//    AuthenticatedUser authenticatedUser =  new InteractiveUser(uid, name, email, permissions);
//    CredentialsService credentialsService = CredentialsServiceProvider.get();
//    RawSettings rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
//    new SourceContext(user, credentialsService, rawSettings);
//
//    this.runtimeContext = new RuntimeContext(sourceContext, rawSettings, maybeArguments, environment);
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public Env getEnv() {
    return env;
  }

  public OutputStream getOutput() {
    return output;
  }

  public SourceContext getSourceContext() {
    return sourceContext;
  }

  public Secret getSecret(String key) {
    return this.sourceContext.credentialsService().getSecret(user, key).get();
  }

  public String[] getScopes() {
    String scopes =env.getEnvironment().get("RAW_SCOPES");
    if (scopes == null) {
      return new String[0];
    }
    return scopes.split(",");
  }

  public ParamValue getParam(String key) {
    return this.runtimeContext.params(key).get();
  }

  public FunctionRegistry getFunctionRegistry() {
    return functionRegistry;
  }

  private static final TruffleLanguage.ContextReference<RawContext> REFERENCE =
      TruffleLanguage.ContextReference.create(RawLanguage.class);

  public static RawContext get(Node node) {
    return REFERENCE.get(node);
  }

//  /**
//   * Returns an object that contains bindings that were exported across all used languages. To
//   * read or write from this object the {@link TruffleObject interop} API can be used.
//   */
//  public TruffleObject getPolyglotBindings() {
//    return (TruffleObject) env.getPolyglotBindings();
//  }
}
