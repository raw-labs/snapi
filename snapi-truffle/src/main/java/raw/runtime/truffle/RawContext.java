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
import com.typesafe.config.ConfigFactory;
import java.io.Closeable;
import java.io.OutputStream;
import java.util.Objects;
import raw.client.api.*;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.creds.api.Secret;
import raw.inferrer.api.InferrerService;
import raw.inferrer.api.InferrerServiceProvider;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.RawFunctionRegistry;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.InteractiveUser;
import raw.utils.RawSettings;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;
import scala.collection.immutable.Seq$;
import scala.collection.immutable.Set;

public final class RawContext extends Closeable {

  private final RawLanguage language;
  private final Env env;
  private OutputStream output;
  private SourceContext sourceContext;
  private AuthenticatedUser user;
  private String traceId;
  private String[] scopes;
  private RawSettings rawSettings;
  private ProgramEnvironment programEnvironment;
  private InferrerService inferrer;
  private final RawFunctionRegistry functionRegistry;

  public RawContext(RawLanguage language, Env env) {
    this.language = language;
    this.env = env;
    this.output = env.out();

    // Set settings from environment variable if available.
    String rawSettingsString = Objects.toString(env.getEnvironment().get("RAW_SETTINGS"), "");
    if (rawSettingsString.isEmpty()) {
      this.rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
    } else {
      this.rawSettings = new RawSettings(rawSettingsString);
    }

    // Set user from environment variable.
    String uid = Objects.toString(env.getEnvironment().get("RAW_USER"), "");
    this.user = new InteractiveUser(uid, uid, uid, (Seq<String>) Seq$.MODULE$.empty());

    ClassLoader classLoader = RawLanguage.class.getClassLoader();

    // Set traceId from environment variable.
    String traceId = Objects.toString(env.getEnvironment().get("RAW_TRACE_ID"), "");
    this.traceId = traceId;

    // Set scopes from environment variable.
    String scopesStr = Objects.toString(env.getEnvironment().get("RAW_SCOPES"), "");
    this.scopes = (scopesStr == null || scopesStr.isEmpty()) ? new String[0] : scopesStr.split(",");

    // Create source context.
    CredentialsService credentialsService =
        CredentialsServiceProvider.apply(classLoader, this.rawSettings);
    this.sourceContext =
        new SourceContext(this.user, credentialsService, this.rawSettings, new Some<>(classLoader));

    // Create program environment.
    Set<String> scalaScopes =
        JavaConverters.asScalaSetConverter(java.util.Set.of(this.scopes)).asScala().toSet();

    java.util.Map<String, String> javaOptions = new java.util.HashMap<>();

    Map<String, String> scalaOptions =
        JavaConverters.mapAsScalaMapConverter(javaOptions)
            .asScala()
            .toMap(scala.Predef.<scala.Tuple2<String, String>>conforms());

    Option<String> maybeTraceId = traceId != null ? Some.apply(traceId) : Option.empty();

    // Arguments are unused by the runtime in case of Truffle.
    Option<Tuple2<String, RawValue>[]> maybeArguments = Option.empty();
    this.programEnvironment =
        new ProgramEnvironment(this.user, maybeArguments, scalaScopes, scalaOptions, maybeTraceId);

    // Initialize inferrer
    this.inferrer = InferrerServiceProvider.apply(classLoader, this.sourceContext);

    // The function registry holds snapi methods (top level functions). It is the data
    // structure that is used to extract a ref to a function from a piece of execute snapi.
    // Functions appear as polyglot bindings after the execution of the source code.
    this.functionRegistry = new RawFunctionRegistry();
  }

  public RawFunctionRegistry getFunctionRegistry() {
    return functionRegistry;
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public Env getEnv() {
    return env;
  }

  public RawSettings getRawSettings() {
    return rawSettings;
  }

  public ProgramEnvironment getProgramEnvironment() {
    return programEnvironment;
  }

  public String getTraceId() {
    return traceId;
  }

  public InferrerService getInferrer() {
    return inferrer;
  }

  public OutputStream getOutput() {
    return output;
  }

  public SourceContext getSourceContext() {
    return sourceContext;
  }

  public Secret getSecret(String key) {
    if (user == null) {
      throw new RawTruffleRuntimeException("User not set");
    }
    return sourceContext.credentialsService().getSecret(user, key).get();
  }

  public AuthenticatedUser getUser() {
    return user;
  }

  public String[] getScopes() {
    return scopes;
  }

  private static final TruffleLanguage.ContextReference<RawContext> REFERENCE =
      TruffleLanguage.ContextReference.create(RawLanguage.class);

  public static RawContext get(Node node) {
    return REFERENCE.get(node);
  }

  /**
   * Returns an object that contains bindings that were exported across all used languages. To read
   * or write from this object the {@link TruffleObject interop} API can be used.
   */
  public TruffleObject getPolyglotBindings() {
    return (TruffleObject) env.getPolyglotBindings();
  }

  @Override
  public void close() {
    inferrer.stop();
    sourceContext.credentialsService().stop();
  }
}
