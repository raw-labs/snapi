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
//package raw.runtime.truffle.runtime.iterable_old;
//
//import com.oracle.truffle.api.interop.InteropLibrary;
//import com.oracle.truffle.api.interop.UnknownIdentifierException;
//import com.oracle.truffle.api.interop.UnsupportedMessageException;
//import com.oracle.truffle.api.interop.UnsupportedTypeException;
//import java.util.Comparator;
//import raw.compiler.rql2.source.Rql2TypeWithProperties;
//import raw.runtime.truffle.RawLanguage;
//import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
//import raw.runtime.truffle.runtime.list.ObjectList;
//import raw.runtime.truffle.runtime.record.RecordObject;
//import raw.sources.api.SourceContext;
//
//// OffHeap GroupBy where the set of nested values is returned as a list
//public class OffHeapListGroupByKey extends OffHeapGroupByKey {
//
//  public OffHeapListGroupByKey(
//      Comparator<Object> keyCompare,
//      Rql2TypeWithProperties kType,
//      Rql2TypeWithProperties rowType,
//      RawLanguage language,
//      SourceContext context) {
//    super(keyCompare, kType, rowType, language, context, new ListGroupByRecordShaper(language));
//  }
//}
//
//// class ListGroupByRecordShaper extends GroupByRecordShaper {
////
////  private InteropLibrary records = null;
////
////  public ListGroupByRecordShaper(RawLanguage language) {
////    super(language);
////  }
////
////  public Object makeRow(Object key, Object[] values) {
////    RecordObject record = language.createRecord();
////    if (records == null) {
////      records = InteropLibrary.getFactory().getUncached(record);
////    }
////    try {
////      records.writeMember(record, "key", key);
////      records.writeMember(record, "group", new ObjectList(values));
////    } catch (UnsupportedMessageException
////        | UnknownIdentifierException
////        | UnsupportedTypeException e) {
////      throw new RawTruffleInternalErrorException(e);
////    }
////    return record;
////  }
//// }
