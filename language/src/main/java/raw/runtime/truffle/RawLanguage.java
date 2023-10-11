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

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import org.graalvm.options.OptionDescriptors;
import raw.compiler.api.*;
import raw.runtime.Entrypoint;
import raw.runtime.ProgramEnvironment;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.utils.RawSettings;
import scala.Option;

@TruffleLanguage.Registration(
    id = RawLanguage.ID,
    name = "RQL",
    version = RawLanguage.VERSION,
    defaultMimeType = RawLanguage.MIME_TYPE,
    characterMimeTypes = RawLanguage.MIME_TYPE)
@ProvidedTags({
  StandardTags.CallTag.class,
  StandardTags.StatementTag.class,
  StandardTags.RootTag.class,
  StandardTags.RootBodyTag.class,
  StandardTags.ExpressionTag.class,
  StandardTags.ReadVariableTag.class,
  StandardTags.WriteVariableTag.class
})
public final class RawLanguage extends TruffleLanguage<RawContext> {

  public static final String ID = "rql";
  public static final String VERSION = "0.10";
  public static final String MIME_TYPE = "application/x-rql";

  @Override
  protected final RawContext createContext(Env env) {
    return new RawContext(this, env);
  }

  private static final LanguageReference<RawLanguage> REFERENCE =
      LanguageReference.create(RawLanguage.class);

  public static RawLanguage get(Node node) {
    return REFERENCE.get(node);
  }

  // FIXME (msb): Why is this here?
  public RecordObject createRecord() {
    return new RecordObject();
  }

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return RawOptions.OPTION_DESCRIPTORS;
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    /*

  @Child private InteropLibrary bindings = insert(InteropLibrary.getFactory().createDispatched(1));

  private Object getParam(String key) {
    TruffleObject polyglotBindings = RawContext.get(this).getPolyglotBindings();
    assert bindings.hasMembers(polyglotBindings);
    try {
      return bindings.readMember(polyglotBindings, key);
    } catch (UnsupportedMessageException | UnknownIdentifierException e) {
      throw new RuntimeException(e);
    }
  }
  uncached version
     */

    ClassLoader classLoader = RawLanguage.class.getClassLoader();

    RawContext context = RawContext.get(null);
    RawSettings rawSettings = context.getRawSettings();
    ProgramEnvironment programEnvironment = context.getProgramEnvironment();

    String source = request.getSource().getCharacters().toString();

    CompilerService compilerService = CompilerServiceProvider.apply(classLoader, rawSettings);

    CompilationResponse compilationResponse =
        compilerService.compile(source,  programEnvironment, this);

    throw new RawTruffleRuntimeException();

    if (compilationResponse instanceof CompilationFailure) {
      // FIXME (msb): Return all errors, not just head.
      String result = ((CompilationFailure) compilationResponse).errors().head().toString();
      throw new RawTruffleRuntimeException(result);
    }
    Entrypoint entrypoint = ((CompilationSuccess) compilationResponse).entrypoint();
    RootNode rootNode = (RootNode) entrypoint.target();
    return rootNode.getCallTarget();
  }

  @Override
  protected Object getScope(RawContext context) {
    return super.getScope(context);
  }

}
