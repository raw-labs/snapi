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

module raw.snapi.truffle {
  // Direct dependencies
  requires java.base;
  requires java.logging;
  requires jdk.unsupported;
  requires org.graalvm.truffle;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires java.xml;
  requires java.sql;
  requires scala.library;
  requires com.ctc.wstx;
  requires raw.utils;
  requires raw.client;
  requires raw.snapi.frontend;

  // Indirect dependencies
  requires kiama;
  requires com.fasterxml.jackson.scala;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires java.net.http;
  requires com.ibm.icu;
  requires spring.core;
  requires spring.jcl;
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;

  uses raw.creds.api.CredentialsServiceBuilder;
  uses raw.compiler.rql2.api.EntryExtension;
  uses raw.sources.jdbc.api.JdbcTableLocationBuilder;
  uses raw.compiler.rql2.api.PackageExtension;
  uses raw.client.api.CompilerServiceBuilder;
  uses raw.sources.filesystem.api.FileSystemLocationBuilder;
  uses raw.sources.bytestream.api.ByteStreamLocationBuilder;
  uses raw.sources.jdbc.api.JdbcLocationBuilder;
  uses raw.inferrer.api.InferrerServiceBuilder;
  uses raw.sources.jdbc.api.JdbcSchemaLocationBuilder;

  provides com.oracle.truffle.api.provider.TruffleLanguageProvider with
      raw.runtime.truffle.RawLanguageProvider;
  provides raw.compiler.rql2.api.EntryExtension with
      raw.compiler.snapi.truffle.builtin.aws_extension.TruffleAwsV4SignedRequestEntry,
      raw.compiler.snapi.truffle.builtin.byte_extension.TruffleByteFromEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleEmptyCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleBuildCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleFilterCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleOrderByCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleTransformCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleDistinctCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleCountCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleTupleAvgCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleMinCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleMaxCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleSumCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleFirstCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleLastCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleTakeCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleUnnestCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleFromCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleGroupCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleInternalJoinCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension
          .TruffleInternalEquiJoinCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleUnionCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleExistsCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleZipCollectionEntry,
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleMkStringCollectionEntry,
      raw.compiler.snapi.truffle.builtin.csv_extension.TruffleCsvReadEntry,
      raw.compiler.snapi.truffle.builtin.csv_extension.TruffleCsvParseEntry,
      raw.compiler.snapi.truffle.builtin.decimal_extension.TruffleDecimalFromEntry,
      raw.compiler.snapi.truffle.builtin.double_extension.TruffleDoubleFromEntry,
      raw.compiler.snapi.truffle.builtin.environment_extension.TruffleEnvironmentParameterEntry,
      raw.compiler.snapi.truffle.builtin.error_extension.TruffleErrorBuildEntry,
      raw.compiler.snapi.truffle.builtin.error_extension.TruffleErrorBuildWithTypeEntry,
      raw.compiler.snapi.truffle.builtin.error_extension.TruffleErrorGetEntry,
      raw.compiler.snapi.truffle.builtin.float_extension.TruffleFloatFromEntry,
      raw.compiler.snapi.truffle.builtin.function_extension.TruffleFunctionInvokeAfterEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleBuildIntervalEntry,
      raw.compiler.snapi.truffle.builtin.int_extension.TruffleIntFromEntry,
      raw.compiler.snapi.truffle.builtin.int_extension.TruffleIntRangeEntry,
      raw.compiler.snapi.truffle.builtin.json_extension.TruffleReadJsonEntry,
      raw.compiler.snapi.truffle.builtin.json_extension.TruffleParseJsonEntry,
      raw.compiler.snapi.truffle.builtin.json_extension.TrufflePrintJsonEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleEmptyListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleBuildListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleGetListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleFilterListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleTransformListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleTakeListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleSumListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleMaxListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleMinListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleFirstListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleLastListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleCountListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleFromListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleUnsafeFromListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleGroupListEntry,
      raw.compiler.snapi.truffle.builtin.list_extension.TruffleExistsListEntry,
      raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationBuildEntry,
      raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationDescribeEntry,
      raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationLsEntry,
      raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationLlEntry,
      raw.compiler.snapi.truffle.builtin.long_extension.TruffleLongFromEntry,
      raw.compiler.snapi.truffle.builtin.long_extension.TruffleLongRangeEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAbsEntry,
      raw.compiler.snapi.truffle.builtin.mysql_extension.TruffleMySQLQueryEntry,
      raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableEmptyEntry,
      raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableBuildEntry,
      raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableIsNullEntry,
      raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableUnsafeGetEntry,
      raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableTransformEntry,
      raw.compiler.snapi.truffle.builtin.nullable_tryable_extension
          .TruffleFlatMapNullableTryableEntry,
      raw.compiler.snapi.truffle.builtin.oracle_extension.TruffleOracleQueryEntry,
      raw.compiler.snapi.truffle.builtin.postgresql_extension.TrufflePostgreSQLQueryEntry,
      raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordBuildEntry,
      raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordConcatEntry,
      raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordFieldsEntry,
      raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordAddFieldEntry,
      raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordRemoveFieldEntry,
      raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordGetFieldByIndexEntry,
      raw.compiler.snapi.truffle.builtin.snowflake_extension.TruffleSnowflakeQueryEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateBuildEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateFromEpochDayEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateFromTimestampEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateParseEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateNowEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateYearEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateMonthEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateDayEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateSubtractEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateAddIntervalEntry,
      raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateSubtractIntervalEntry,
      raw.compiler.snapi.truffle.builtin.decimal_extension.TruffleDecimalRoundEntry,
      raw.compiler.snapi.truffle.builtin.environment_extension.TruffleEnvironmentSecretEntry,
      raw.compiler.snapi.truffle.builtin.environment_extension.TruffleEnvironmentScopesEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalToMillisEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalFromMillisEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalParseEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalYearsEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalMonthsEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalWeeksEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalDaysEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalHoursEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalMinutesEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalSecondsEntry,
      raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalMillisEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathPiEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathRandomEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathPowerEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAtn2Entry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAcosEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAsinEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAtanEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathCeilingEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathCosEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathCotEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathDegreesEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathExpEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathLogEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathLog10Entry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathRadiansEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSignEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSinEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSqrtEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathTanEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSquareEntry,
      raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathFloorEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpReadEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpGetEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpPostEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpPutEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpDeleteEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpHeadEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpPatchEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpOptionsEntry,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpUrlEncode,
      raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpUrlDecode,
      raw.compiler.snapi.truffle.builtin.xml_extension.TruffleReadXmlEntry,
      raw.compiler.snapi.truffle.builtin.xml_extension.TruffleParseXmlEntry,
      raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeCastEntry,
      raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeEmptyEntry,
      raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeMatchEntry,
      raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeProtectCastEntry,
      raw.compiler.snapi.truffle.builtin.binary_extension.TruffleBinaryBase64Entry,
      raw.compiler.snapi.truffle.builtin.binary_extension.TruffleBinaryReadEntry,
      raw.compiler.snapi.truffle.builtin.binary_extension.TruffleFromStringBinaryEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampBuildEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampFromDateEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampParseEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampNowEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampRangeEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampYearEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampMonthEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampDayEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampHourEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampMinuteEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampSecondEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampMillisEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampFromUnixTimestampEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampToUnixTimestampEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampTimeBucketEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampSubtractEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampAddIntervalEntry,
      raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampSubtractIntervalEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeBuildEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeParseEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeNowEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeHourEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeMinuteEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeSecondEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeMillisEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeSubtractEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeAddIntervalEntry,
      raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeSubtractIntervalEntry,
      raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryFlatMapEntry,
      raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryUnsafeGetEntry,
      raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryIsErrorEntry,
      raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryIsSuccessEntry,
      raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryTransformEntry,
      raw.compiler.snapi.truffle.builtin.success_extension.TruffleSuccessBuildEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringFromEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReadEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringContainsEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringTrimEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLTrimEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringRTrimEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReplaceEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReverseEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReplicateEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringUpperEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLowerEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringSplitEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLengthEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringSubStringEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringCountSubStringEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringStartsWithEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringEmptyEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleBase64EntryExtension,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringEncodeEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringDecodeEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLevenshteinDistanceEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReadLinesEntry,
      raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringCapitalizeEntry,
      raw.compiler.snapi.truffle.builtin.sqlserver_extension.TruffleSQLServerQueryEntry,
      raw.compiler.snapi.truffle.builtin.short_extension.TruffleShortFromEntry,
      raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexReplaceEntry,
      raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexMatchesEntry,
      raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexFirstMatchInEntry,
      raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexGroupsEntry,
      raw.compiler.snapi.truffle.builtin.s3_extension.TruffleS3BuildEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleByteValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleBoolValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleDateValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleDoubleValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleFloatValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleIntervalValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleIntValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleListValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleLongValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleMandatoryExpArgsEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleMandatoryValueArgsEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleOptionalExpArgsTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleOptionalValueArgsTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleRecordValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleShortValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleStrictArgsColPassThroughTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleStrictArgsTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleStringValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleTimestampValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleTimeValueArgTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarExpArgsTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarNullableStringExpTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarNullableStringValueTestEntry,
      raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarValueArgsTestEntry,
      raw.compiler.snapi.truffle.builtin.kryo_extension.TruffleKryoDecodeEntry,
      raw.compiler.snapi.truffle.builtin.kryo_extension.TruffleKryoEncodeEntry;

  exports raw.runtime.truffle;
  exports raw.runtime.truffle.boundary;
  exports raw.runtime.truffle.runtime.record;
  exports raw.runtime.truffle.runtime.operators;
  exports raw.runtime.truffle.runtime.function;
  exports raw.runtime.truffle.runtime.exceptions;
  exports raw.runtime.truffle.runtime.exceptions.xml;
  exports raw.runtime.truffle.runtime.exceptions.json;
  exports raw.runtime.truffle.runtime.exceptions.csv;
  exports raw.runtime.truffle.runtime.exceptions.binary;
  exports raw.runtime.truffle.runtime.exceptions.rdbms;
  exports raw.runtime.truffle.runtime.primitives;
  exports raw.runtime.truffle.runtime.list;
  exports raw.runtime.truffle.runtime.or;
  exports raw.runtime.truffle.runtime.generator.list;
  exports raw.runtime.truffle.runtime.generator.collection;
  exports raw.runtime.truffle.runtime.generator.collection.abstract_generator;
  exports raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next;
  exports raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;
  exports raw.runtime
      .truffle
      .runtime
      .generator
      .collection
      .abstract_generator
      .compute_next
      .operations;
  exports raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper;
  exports raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;
  exports raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap;
  exports raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by;
  exports raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by;
  exports raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct;
  exports raw.runtime.truffle.runtime.iterable;
  exports raw.runtime.truffle.runtime.iterable.operations;
  exports raw.runtime.truffle.runtime.iterable.list;
  exports raw.runtime.truffle.runtime.iterable.sources;
  exports raw.runtime.truffle.runtime.kryo;
  exports raw.runtime.truffle.utils;
  exports raw.runtime.truffle.ast;
  exports raw.runtime.truffle.ast.io.kryo;
  exports raw.runtime.truffle.ast.io.xml.parser;
  exports raw.runtime.truffle.ast.io.jdbc;
  exports raw.runtime.truffle.ast.io.json.reader;
  exports raw.runtime.truffle.ast.io.json.reader.parser;
  exports raw.runtime.truffle.ast.io.json.writer;
  exports raw.runtime.truffle.ast.io.json.writer.internal;
  exports raw.runtime.truffle.ast.io.csv.reader;
  exports raw.runtime.truffle.ast.io.csv.reader.parser;
  exports raw.runtime.truffle.ast.io.csv.writer;
  exports raw.runtime.truffle.ast.io.csv.writer.internal;
  exports raw.runtime.truffle.ast.io.binary;
  exports raw.runtime.truffle.ast.local;
  exports raw.runtime.truffle.ast.expressions.unary;
  exports raw.runtime.truffle.ast.expressions.iterable.collection;
  exports raw.runtime.truffle.ast.expressions.iterable.list;
  exports raw.runtime.truffle.ast.expressions.record;
  exports raw.runtime.truffle.ast.expressions.option;
  exports raw.runtime.truffle.ast.expressions.function;
  exports raw.runtime.truffle.ast.expressions.tryable;
  exports raw.runtime.truffle.ast.expressions.binary;
  exports raw.runtime.truffle.ast.expressions.literals;
  exports raw.runtime.truffle.ast.expressions.builtin.regex_package;
  exports raw.runtime.truffle.ast.expressions.builtin.type_package;
  exports raw.runtime.truffle.ast.expressions.builtin.environment_package;
  exports raw.runtime.truffle.ast.expressions.builtin.math_package;
  exports raw.runtime.truffle.ast.expressions.builtin.aws_package;
  exports raw.runtime.truffle.ast.expressions.builtin.http_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.short_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.double_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.long_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.decimal_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.float_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.int_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.byte_package;
  exports raw.runtime.truffle.ast.expressions.builtin.function_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.date_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.time_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package;
  exports raw.runtime.truffle.ast.expressions.builtin.string_package;
  exports raw.runtime.truffle.ast.expressions.builtin.location_package;
  exports raw.runtime.truffle.ast.expressions.builtin.binary_package;
  exports raw.runtime.truffle.ast.expressions.aggregation;
  exports raw.runtime.truffle.ast.controlflow;
  exports raw.runtime.truffle.runtime.exceptions.validation;
  exports raw.compiler.snapi.truffle.compiler;
  exports raw.compiler.rql2output.truffle.builtin;
}
