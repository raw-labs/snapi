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
//package raw.runtime.truffle.runtime.iterable_old.sources;
//
//import com.oracle.truffle.api.RootCallTarget;
//import com.oracle.truffle.api.interop.InteropLibrary;
//import com.oracle.truffle.api.interop.TruffleObject;
//import com.oracle.truffle.api.library.CachedLibrary;
//import com.oracle.truffle.api.library.ExportLibrary;
//import com.oracle.truffle.api.library.ExportMessage;
//import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
//import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
//import raw.runtime.truffle.runtime.generator.collection_old.CollectionAbstractGenerator;
//import raw.runtime.truffle.runtime.generator.collection_old.compute_next.sources.JdbcQueryComputeNext;
//import raw.runtime.truffle.runtime.iterable_old.IterableLibrary;
//import raw.runtime.truffle.runtime.primitives.LocationObject;
//import raw.sources.api.SourceContext;
//
//@ExportLibrary(IterableLibrary.class)
//@ExportLibrary(InteropLibrary.class)
//public class JdbcQueryCollection implements TruffleObject {
//
//  private final LocationObject dbLocation;
//  private final String query;
//  private final RootCallTarget rowParserCallTarget;
//
//  private final SourceContext context;
//  private final JdbcExceptionHandler exceptionHandler;
//
//  public JdbcQueryCollection(
//      LocationObject dbLocation,
//      String query,
//      SourceContext context,
//      RootCallTarget rowParserCallTarget,
//      JdbcExceptionHandler exceptionHandler) {
//    this.dbLocation = dbLocation;
//    this.query = query;
//    this.rowParserCallTarget = rowParserCallTarget;
//    this.context = context;
//    this.exceptionHandler = exceptionHandler;
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
//        new JdbcQueryComputeNext(
//            dbLocation, query, context, rowParserCallTarget, exceptionHandler));
//  }
//
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
