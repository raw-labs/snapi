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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import raw.client.api.*;
import raw.inferrer.api.InferrerService;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.RawFunctionRegistry;
import raw.utils.AuthenticatedUser;
import raw.utils.RawSettings;
import scala.collection.JavaConverters;

public final class RawContext {

  private final RawLanguage language;
  private final Env env;
  private final RawSettings rawSettings;
  private final OutputStream output;
  private final ProgramEnvironment programEnvironment;
  private final RawFunctionRegistry functionRegistry;

  @CompilerDirectives.TruffleBoundary
  public RawContext(RawLanguage language, Env env) {
    this.language = language;
    this.env = env;
    this.output = env.out();

    String rawSettingsConfigString = env.getOptions().get(RawOptions.RAW_SETTINGS_KEY);
    // If settings were passed as Engine options, used those as our settings.
    // Otherwise, default to the settings from the language, which are obtained from the system.
    if (rawSettingsConfigString.isEmpty()) {
      this.rawSettings = language.getDefaultRawSettings();
    } else {
      // Settings were serialized as a string, so we need to parse them.
      // This is mostly required by the test suite.
      this.rawSettings = new RawSettings(rawSettingsConfigString);
    }

    // Set program environment.
    this.programEnvironment =
        ProgramEnvironment$.MODULE$.deserializeFromString(
            env.getEnvironment().get("RAW_PROGRAM_ENVIRONMENT"));

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

  public ProgramEnvironment getProgramEnvironment() {
    return programEnvironment;
  }

  public String getTraceId() {
    return programEnvironment.maybeTraceId().get();
  }

  public InferrerService getInferrer() {
    return language.getInferrer(getUser(), rawSettings);
  }

  public OutputStream getOutput() {
    return output;
  }

  public RawSettings getSettings() {
    return rawSettings;
  }

  @CompilerDirectives.TruffleBoundary
  public Map<String, String> getHttpHeaders(String name) {
    scala.Option<scala.collection.immutable.Map<String, String>> maybeHttpHeaders =
        programEnvironment.httpHeaders().get(name);
    if (maybeHttpHeaders.isEmpty()) {
      throw new RawTruffleRuntimeException("unknown http credential: " + name);
    }
    return JavaConverters.mapAsJavaMap(maybeHttpHeaders.get());
  }

  @CompilerDirectives.TruffleBoundary
  public boolean existsSecret(String key) {
    return programEnvironment.secrets().contains(key);
  }

  @CompilerDirectives.TruffleBoundary
  public String getSecret(String key) {
    scala.Option<String> maybeSecret = programEnvironment.secrets().get(key);
    if (maybeSecret.isEmpty()) {
      throw new RawTruffleRuntimeException("unknown secret: " + key);
    }
    return maybeSecret.get();
  }

  @CompilerDirectives.TruffleBoundary
  public boolean existsJdbcCredential(String name) {
    return programEnvironment.jdbcServers().contains(name);
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcLocation getJdbcLocation(String name) {
    scala.Option<JdbcLocation> maybeJdbcLocation = programEnvironment.jdbcServers().get(name);
    if (maybeJdbcLocation.isEmpty()) {
      throw new RawTruffleRuntimeException("unknown database credential: " + name);
    }
    return maybeJdbcLocation.get();
  }

  @CompilerDirectives.TruffleBoundary
  public boolean existsS3Credential(String bucket) {
    return programEnvironment.s3Credentials().contains(bucket);
  }

  @CompilerDirectives.TruffleBoundary
  public S3Credential getS3Credential(String bucket) {
    scala.Option<S3Credential> maybeCred = programEnvironment.s3Credentials().get(bucket);
    if (maybeCred.isEmpty()) {
      throw new RawTruffleRuntimeException("unknown S3 bucket: " + bucket);
    }
    return maybeCred.get();
  }

  @CompilerDirectives.TruffleBoundary
  public AuthenticatedUser getUser() {
    return programEnvironment.user();
  }

  @CompilerDirectives.TruffleBoundary
  public String[] getScopes() {
    Set<String> javaScopes = JavaConverters.setAsJavaSet(programEnvironment.scopes());
    return javaScopes.toArray(new String[0]);
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
}
