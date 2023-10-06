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

import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.typesafe.config.ConfigFactory;
import java.io.OutputStream;
import java.util.Objects;
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
import scala.Tuple2;
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
  private String[] scopes;
  private RawSettings rawSettings;
  private ProgramEnvironment programEnvironment;

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
    CredentialsService credentialsService = CredentialsServiceProvider.apply(classLoader, rawSettings);
    this.sourceContext = new SourceContext(user, credentialsService, rawSettings);

    // Create program environment.
    Set<String> scalaScopes =
        JavaConverters.asScalaSetConverter(java.util.Set.of(scopes)).asScala().toSet();

    java.util.Map<String, String> javaOptions = new java.util.HashMap<>();
    javaOptions.put("output-format", env.getOptions().get(RawOptions.OUTPUT_FORMAT_KEY));

    Map<String, String> scalaOptions =
        JavaConverters.mapAsScalaMapConverter(javaOptions)
            .asScala()
            .toMap(scala.Predef.<scala.Tuple2<String, String>>conforms());

    Option<String> maybeTraceId = traceId != null ? Some.apply(traceId) : Option.empty();

    // Read back arguments
//    TruffleObject polyglotBindings = (TruffleObject) env.getPolyglotBindings();
//    InteropLibrary interop = LibraryFactory.resolve(InteropLibrary.class).getUncached();
//    Object keys = interop.getMembers(polyglotBindings);
//    long size = interop.getArraySize(keys);
//
//    for (long i = 0; i < size; i++) {
//      Object key = interop.readArrayElement(keys, i);
//      Object value = interop.readMember(polyglotBindings, key.toString());
//      ParamValue v = paramValueOf(value);
//
//      System.out.println("Key: " + key + ", Value: " + value);
//    }
    Option<Tuple2<String, ParamValue>[]> maybeArguments = Option.empty();

    programEnvironment = new ProgramEnvironment(user, maybeArguments, scalaScopes, scalaOptions, maybeTraceId);
  }

  ParamValue paramValueOf(Object o) {
    throw new IllegalArgumentException("Not implemented");
//    return switch (o) {
//      case Integer i -> i.doubleValue();
//      case Float f -> f.doubleValue();
//      case String s -> Double.parseDouble(s);
//      default -> 0d;
//    };
//    value match {
//      case ParamNull() => null
//      case ParamByte(v) => v
//      case ParamShort(v) => v
//      case ParamInt(v) => v
//      case ParamLong(v) => v
//      case ParamFloat(v) => v
//      case ParamDouble(v) => v
//      case ParamBool(v) => v
//      case ParamString(v) => v
//      case ParamDecimal(v) => new DecimalObject(v)
//      case ParamDate(v) => new DateObject(v)
//      case ParamTime(v) => new TimeObject(v)
//      case ParamTimestamp(v) => new TimestampObject(v)
//      case ParamInterval(v) => IntervalObject.fromDuration(v)
//    }
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
}
