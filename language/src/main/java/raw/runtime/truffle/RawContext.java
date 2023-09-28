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
import com.oracle.truffle.api.nodes.Node;
import java.io.OutputStream;
import java.util.Objects;

import com.typesafe.config.ConfigFactory;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.creds.api.Secret;
import raw.runtime.ParamValue;
import raw.runtime.ProgramEnvironment;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.InteractiveUser;
import raw.utils.RawSettings;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;
import scala.collection.immutable.Seq$;
import scala.collection.immutable.Set;


public final class RawContext {

  private final RawLanguage language;
  private final Env env;
  private OutputStream output;
  private SourceContext sourceContext;
  private AuthenticatedUser user;
  private String traceId;
  private RawSettings rawSettings;
  private ProgramEnvironment programEnvironment;

  public RawContext(RawLanguage language, Env env) {
    this.language = language;
    this.env = env;
    this.output = env.out();
    this.rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());

    // Set user from environment variable.
    String uid = Objects.toString(env.getEnvironment().get("RAW_USER"), "");
    this.user = new InteractiveUser(uid, uid, uid, (Seq<String>) Seq$.MODULE$.empty());

    // Set traceId from environment variable.
    String traceId = Objects.toString(env.getEnvironment().get("RAW_TRACE_ID"), "");
    this.traceId = traceId;

    // Create source context.
    CredentialsService credentialsService = CredentialsServiceProvider.apply(rawSettings);
    this.sourceContext = new SourceContext(user, credentialsService, rawSettings);

    // Create program environment.
    Set<String> scalaScopes = JavaConverters.asScalaSetConverter(java.util.Set.of(getScopes())).asScala().toSet();

    java.util.Map<String, String> javaOptions = new java.util.HashMap<>();
    javaOptions.put("output-format", env.getOptions().get(RawOptions.OUTPUT_FORMAT_KEY));

    Map<String, String> scalaOptions = JavaConverters.mapAsScalaMapConverter(javaOptions).asScala().toMap(scala.Predef.<scala.Tuple2<String, String>>conforms());

    Option<String> maybeTraceId = traceId != null ? Some.apply(traceId) : Option.empty();

    programEnvironment = new ProgramEnvironment(user, scalaScopes, scalaOptions, maybeTraceId);
  }

  public RawLanguage getLanguage() {
    return language;
  }

//  public Env getEnv() {
//    return env;
//  }

  public RawSettings getRawSettings() {
    return rawSettings;
  }

  public ProgramEnvironment getProgramEnvironment() {
    return programEnvironment;
  }

  public String getTraceId() {
    return traceId;
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

  public String[] getScopes() {
    String scopes = env.getEnvironment().get("RAW_SCOPES");
    if (scopes == null) {
      return new String[0];
    }
    return scopes.split(",");
  }

  public ParamValue getParam(String key) {
    // FIXME (msb): Use getPolyglotBindings?
    throw new IllegalArgumentException("Not implemented yet");
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
