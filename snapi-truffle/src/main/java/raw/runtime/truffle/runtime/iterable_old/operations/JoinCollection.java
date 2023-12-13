///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.runtime.truffle.runtime.iterable_old.operations;
//
//import com.oracle.truffle.api.interop.InteropLibrary;
//import com.oracle.truffle.api.interop.TruffleObject;
//import com.oracle.truffle.api.library.CachedLibrary;
//import com.oracle.truffle.api.library.ExportLibrary;
//import com.oracle.truffle.api.library.ExportMessage;
//import raw.compiler.rql2.source.Rql2TypeWithProperties;
//import raw.runtime.truffle.RawLanguage;
//import raw.runtime.truffle.runtime.function.Closure;
//import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
//import raw.runtime.truffle.runtime.generator.collection_old.CollectionAbstractGenerator;
//import raw.runtime.truffle.runtime.generator.collection_old.compute_next.operations.JoinComputeNext;
//import raw.runtime.truffle.runtime.iterable_old.IterableLibrary;
//import raw.sources.api.SourceContext;
//
//@ExportLibrary(IterableLibrary.class)
//@ExportLibrary(InteropLibrary.class)
//public final class JoinCollection implements TruffleObject {
//
//  final Object leftIterable;
//  final Object rightIterable;
//  final Closure predicate;
//  final Closure remap;
//  final Rql2TypeWithProperties rightType;
//  final SourceContext context;
//  final RawLanguage language;
//  private final Boolean reshapeBeforePredicate;
//
//  public JoinCollection(
//      Object leftIterable,
//      Object rightIterable,
//      Closure remap,
//      Closure predicate,
//      Rql2TypeWithProperties rightType,
//      Boolean reshapeBeforePredicate,
//      SourceContext context,
//      RawLanguage language) {
//    this.leftIterable = leftIterable;
//    this.rightIterable = rightIterable;
//    this.remap = remap;
//    this.predicate = predicate;
//    this.rightType = rightType;
//    this.context = context;
//    this.language = language;
//    this.reshapeBeforePredicate = reshapeBeforePredicate;
//  }
//
//  @ExportMessage
//  boolean isIterable() {
//    return true;
//  }
//
//  @ExportMessage
//  Object getGenerator() {
//    return new CollectionAbstractGenerator(
//        new JoinComputeNext(
//            leftIterable,
//            rightIterable,
//            remap,
//            predicate,
//            reshapeBeforePredicate,
//            rightType,
//            context,
//            language));
//  }
//  // InteropLibrary: Iterable
//
//  @ExportMessage
//  boolean hasIterator() {
//    return true;
//  }
//
//  private final GeneratorLibrary generatorLibrary =
//      GeneratorLibrary.getFactory().createDispatched(1);
//
//  @ExportMessage
//  Object getIterator(@CachedLibrary("this") IterableLibrary iterables) {
//    Object generator = iterables.getGenerator(this);
//    generatorLibrary.init(generator);
//    return generator;
//  }
//}
