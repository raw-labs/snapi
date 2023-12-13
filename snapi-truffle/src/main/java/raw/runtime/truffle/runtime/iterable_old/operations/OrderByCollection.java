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
//import com.oracle.truffle.api.interop.*;
//import com.oracle.truffle.api.library.CachedLibrary;
//import com.oracle.truffle.api.library.ExportLibrary;
//import com.oracle.truffle.api.library.ExportMessage;
//import raw.compiler.rql2.source.Rql2TypeWithProperties;
//import raw.runtime.truffle.RawLanguage;
//import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
//import raw.runtime.truffle.runtime.iterable_old.IterableLibrary;
//import raw.runtime.truffle.runtime.iterable_old.OffHeapGroupByKeys;
//import raw.runtime.truffle.runtime.operators.OperatorNodes;
//import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;
//import raw.sources.api.SourceContext;
//
//@ExportLibrary(IterableLibrary.class)
//@ExportLibrary(InteropLibrary.class)
//public final class OrderByCollection implements TruffleObject {
//
//  final Object parentIterable;
//  final Object[] keyFunctions;
//
//  final int[] keyOrderings;
//  final Rql2TypeWithProperties[] keyTypes;
//  final Rql2TypeWithProperties rowType;
//  private final RawLanguage language;
//  private final SourceContext context;
//
//  public OrderByCollection(
//      Object iterable,
//      Object[] keyFunctions,
//      int[] keyOrderings,
//      Rql2TypeWithProperties[] keyTypes,
//      Rql2TypeWithProperties rowType,
//      RawLanguage language,
//      SourceContext context) {
//    this.parentIterable = iterable;
//    this.keyFunctions = keyFunctions;
//    this.keyOrderings = keyOrderings;
//    this.keyTypes = keyTypes;
//    this.rowType = rowType;
//    this.language = language;
//    this.context = context;
//  }
//
//  private final OperatorNodes.CompareNode compare =
//      OperatorNodesFactory.CompareNodeGen.getUncached();
//
//  private int compareKeys(Object[] keys1, Object[] keys2) {
//    // Keys are compared in order, until a difference is found.
//    // If all keys are equal, then the rows are equal.
//    // If keys are different, the comparison result is multiplied by the 'order' of the key to
//    // reflect the "ASC/DESC".
//    for (int i = 0; i < keys1.length; i++) {
//      int cmp = compare.execute(keys1[i], keys2[i]);
//      if (cmp != 0) {
//        return keyOrderings[i] * cmp;
//      }
//    }
//    return 0;
//  }
//
//  @ExportMessage
//  boolean isIterable() {
//    return true;
//  }
//
//  private Object[] computeKeys(Object v)
//      throws UnsupportedMessageException, UnsupportedTypeException, ArityException {
//    Object[] argumentValues = new Object[1];
//    argumentValues[0] = v;
//    Object[] key = new Object[keyFunctions.length];
//    for (int i = 0; i < keyFunctions.length; i++) {
//      key[i] = InteropLibrary.getUncached().execute(keyFunctions[i], argumentValues);
//    }
//    return key;
//  }
//
//  @ExportMessage
//  Object getGenerator(
//      @CachedLibrary(limit = "5") IterableLibrary iterables,
//      @CachedLibrary(limit = "5") GeneratorLibrary generators) {
//    Object generator = iterables.getGenerator(parentIterable);
//    OffHeapGroupByKeys groupByKeys =
//        new OffHeapGroupByKeys(this::compareKeys, keyTypes, rowType, language, context);
//    try {
//      generators.init(generator);
//      while (generators.hasNext(generator)) {
//        Object v = generators.next(generator);
//        Object[] key = computeKeys(v);
//        groupByKeys.put(key, v);
//      }
//    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
//      throw new RuntimeException(e);
//    } finally {
//      generators.close(generator);
//    }
//    return groupByKeys.generator();
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
