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
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.collection.immutable.Set;
import scala.collection.immutable.Set$;

public final class RawContext {

  private final RawLanguage language;
  private final Env env;
  private final FunctionRegistry functionRegistry;
  private OutputStream output;
  private SourceContext sourceContext;
  private RuntimeContext runtimeContext;

  public RawContext(RawLanguage language, Env env) {
    this.language = language;
    this.env = env;
    this.output = env.out();
    this.functionRegistry = new FunctionRegistry();

    AuthenticatedUser authenticatedUser = null;
    RawSettings rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
    CredentialsService credentialsService = CredentialsServiceProvider.apply(rawSettings);

    this.sourceContext = new SourceContext(authenticatedUser, credentialsService, rawSettings);

    Map<String, String> emptyMap = Map$.MODULE$.empty();
    Map<String, String> updatedMap = emptyMap.$plus(new Tuple2<>("output-format", ""));
    ProgramEnvironment programEnvironment = new ProgramEnvironment(authenticatedUser,  (Set<String>) Set$.MODULE$.empty(), updatedMap, Option.empty());

    /*

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

  // FIXME (msb): Remove!
//  public void setOutput(OutputStream output) {
//    this.output = output;
//  }

  public SourceContext getSourceContext() {
    return sourceContext;
  }

  public Secret getSecret(String key) {
    return this.sourceContext
            .credentialsService()
            .getSecret(this.sourceContext.user(), key)
            .get();
  }

  public String[] getScopes() {
    String[] bl = new String[this.runtimeContext.scopes().size()];
    Iterator<String> it = this.runtimeContext.scopes().iterator();
    int i = 0;
    while (it.hasNext()) {
      bl[i] = it.next();
      i++;
    }
    return bl;
  }

  public ParamValue getParam(String key) {
    return this.runtimeContext.params(key).get();
  }

//  // FIXME (msb): Remove!
//  public void setRuntimeContext(RuntimeContext runtimeContext) {
//    this.runtimeContext = runtimeContext;
//  }

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
