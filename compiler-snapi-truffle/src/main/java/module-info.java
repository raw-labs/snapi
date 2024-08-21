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

import com.rawlabs.compiler.api.CompilerServiceBuilder;
import com.rawlabs.compiler.snapi.truffle.emitter.builtin.location_extension.TruffleLocationFromStringEntry;

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
  requires raw.utils.core;
  requires raw.compiler.protocol;
  requires raw.client;
  requires raw.utils.sources;
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
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;

  uses com.rawlabs.compiler.snapi.rql2.api.EntryExtension;
  uses com.rawlabs.compiler.snapi.rql2.api.PackageExtension;
  uses CompilerServiceBuilder;

  provides com.oracle.truffle.api.provider.TruffleLanguageProvider with
      com.rawlabs.compiler.snapi.truffle.runtime.RawLanguageProvider;
  provides com.rawlabs.compiler.snapi.rql2.api.EntryExtension with
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.aws_extension
          .TruffleAwsV4SignedRequestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.byte_extension.TruffleByteFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleEmptyCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleBuildCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleFilterCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleOrderByCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleTransformCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleDistinctCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleCountCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleTupleAvgCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleMinCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleMaxCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleSumCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleFirstCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleLastCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleTakeCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleUnnestCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleFromCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleGroupCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleInternalJoinCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleInternalEquiJoinCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleUnionCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleExistsCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleZipCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension
          .TruffleMkStringCollectionEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvReadEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvParseEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.double_extension.TruffleDoubleFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.environment_extension
          .TruffleEnvironmentParameterEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.error_extension
          .TruffleErrorBuildWithTypeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.error_extension.TruffleErrorGetEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.float_extension.TruffleFloatFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.function_extension
          .TruffleFunctionInvokeAfterEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleBuildIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.int_extension.TruffleIntFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.int_extension.TruffleIntRangeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.json_extension.TruffleReadJsonEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.json_extension.TruffleParseJsonEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.json_extension.TrufflePrintJsonEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleEmptyListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleBuildListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleGetListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleFilterListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleTransformListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleTakeListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleSumListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleMaxListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleMinListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleFirstListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleLastListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleCountListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleFromListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleUnsafeFromListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleGroupListEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension.TruffleExistsListEntry,
      TruffleLocationFromStringEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.location_extension
          .TruffleLocationDescribeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.location_extension.TruffleLocationLsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.location_extension.TruffleLocationLlEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.long_extension.TruffleLongFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.long_extension.TruffleLongRangeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathAbsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.mysql_extension.TruffleMySQLQueryEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.nullable_extension
          .TruffleNullableEmptyEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.nullable_extension
          .TruffleNullableBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.nullable_extension
          .TruffleNullableIsNullEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.nullable_extension
          .TruffleNullableUnsafeGetEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.nullable_extension
          .TruffleNullableTransformEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.nullable_tryable_extension
          .TruffleFlatMapNullableTryableEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.oracle_extension.TruffleOracleQueryEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.postgresql_extension
          .TrufflePostgreSQLQueryEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.record_extension.TruffleRecordBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.record_extension.TruffleRecordConcatEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.record_extension.TruffleRecordFieldsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.record_extension
          .TruffleRecordAddFieldEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.record_extension
          .TruffleRecordRemoveFieldEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.record_extension
          .TruffleRecordGetFieldByIndexEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.snowflake_extension
          .TruffleSnowflakeQueryEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension
          .TruffleDateFromEpochDayEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension
          .TruffleDateFromTimestampEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateParseEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateNowEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateYearEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateMonthEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateDayEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateSubtractEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension.TruffleDateAddIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.date_extension
          .TruffleDateSubtractIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalRoundEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.environment_extension
          .TruffleEnvironmentSecretEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.environment_extension
          .TruffleEnvironmentScopesEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalToMillisEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalFromMillisEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalParseEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalYearsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalMonthsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalWeeksEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalDaysEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalHoursEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalMinutesEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalSecondsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.interval_extension
          .TruffleIntervalMillisEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathPiEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathRandomEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathPowerEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathAtn2Entry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathAcosEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathAsinEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathAtanEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathCeilingEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathCosEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathCotEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathDegreesEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathExpEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathLogEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathLog10Entry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathRadiansEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathSignEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathSinEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathSqrtEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathTanEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathSquareEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.math_extension.TruffleMathFloorEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpReadEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpGetEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPostEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPutEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpDeleteEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpHeadEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPatchEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpOptionsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpUrlEncode,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension.TruffleHttpUrlDecode,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.xml_extension.TruffleReadXmlEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.xml_extension.TruffleParseXmlEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.type_extension.TruffleTypeCastEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.type_extension.TruffleTypeEmptyEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.type_extension.TruffleTypeMatchEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.type_extension.TruffleTypeProtectCastEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.binary_extension.TruffleBinaryBase64Entry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.binary_extension.TruffleBinaryReadEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.binary_extension
          .TruffleFromStringBinaryEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampFromDateEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampParseEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampNowEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampRangeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampYearEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampMonthEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampDayEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampHourEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampMinuteEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampSecondEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampMillisEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampFromUnixTimestampEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampToUnixTimestampEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampTimeBucketEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampSubtractEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampAddIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampSubtractIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeParseEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeNowEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeHourEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeMinuteEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSecondEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeMillisEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSubtractEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension.TruffleTimeAddIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension
          .TruffleTimeSubtractIntervalEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.try_extension.TruffleTryFlatMapEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.try_extension.TruffleTryUnsafeGetEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.try_extension.TruffleTryIsErrorEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.try_extension.TruffleTryIsSuccessEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.try_extension.TruffleTryTransformEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.success_extension.TruffleSuccessBuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringReadEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringContainsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringTrimEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringLTrimEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringRTrimEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringReplaceEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringReverseEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringReplicateEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringUpperEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringLowerEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringSplitEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringLengthEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringSubStringEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringCountSubStringEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringStartsWithEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringEmptyEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleBase64EntryExtension,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringEncodeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension.TruffleStringDecodeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringLevenshteinDistanceEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringReadLinesEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringCapitalizeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.sqlserver_extension
          .TruffleSQLServerQueryEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.short_extension.TruffleShortFromEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexReplaceEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexMatchesEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.regex_extension
          .TruffleRegexFirstMatchInEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexGroupsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.s3_extension.TruffleS3BuildEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleByteValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleBoolValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleDateValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleDoubleValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleFloatValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleIntervalValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension.TruffleIntValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleListValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleLongValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleMandatoryExpArgsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleMandatoryValueArgsEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleOptionalExpArgsTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleOptionalValueArgsTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleRecordValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleShortValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleStrictArgsColPassThroughTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension.TruffleStrictArgsTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleStringValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleTimestampValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleTimeValueArgTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension.TruffleVarExpArgsTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleVarNullableStringExpTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleVarNullableStringValueTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension
          .TruffleVarValueArgsTestEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoDecodeEntry,
      com.rawlabs.compiler.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoEncodeEntry;

  exports com.rawlabs.compiler.snapi.truffle.runtime;
  exports com.rawlabs.compiler.snapi.truffle.runtime.boundary;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.record;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.operators;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.function;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.xml;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.json;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.csv;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.binary;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.rdbms;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.data_structures.treemap;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.list;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.or;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.list;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .abstract_generator;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .abstract_generator
      .compute_next;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .abstract_generator
      .compute_next
      .sources;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .abstract_generator
      .compute_next
      .operations;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .record_shaper;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .input_buffer;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .off_heap;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .off_heap
      .group_by;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .off_heap
      .order_by;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .off_heap
      .distinct;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.operations;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.list;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.sources;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.kryo;
  exports com.rawlabs.compiler.snapi.truffle.runtime.utils;
  exports com.rawlabs.compiler.snapi.truffle.runtime.tryable_nullable;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.kryo;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.xml.parser;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.jdbc;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader.parser;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer.internal;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.csv.reader;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.csv.reader.parser;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.csv.writer;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.csv.writer.internal;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.io.binary;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.local;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.unary;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.collection;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.list;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.record;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.option;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.function;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.tryable;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.binary;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.literals;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.regex_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.type_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.environment_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.math_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.aws_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.http_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.short_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.double_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.long_package;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .ast
      .expressions
      .builtin
      .numeric
      .decimal_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.float_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.int_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.numeric.byte_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.function_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.date_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.time_package;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .ast
      .expressions
      .builtin
      .temporals
      .interval_package;
  exports com.rawlabs
      .compiler
      .snapi
      .truffle
      .runtime
      .ast
      .expressions
      .builtin
      .temporals
      .timestamp_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.string_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.location_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.binary_package;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.aggregation;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.controlflow;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.osr;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.bodies;
  exports com.rawlabs.compiler.snapi.truffle.runtime.ast.osr.conditions;
  exports com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.validation;
  exports com.rawlabs.compiler.snapi.truffle.emitter.compiler;
  exports com.rawlabs.compiler.snapi.truffle.emitter.output;
  exports com.rawlabs.compiler.snapi.truffle.emitter.builtin;
  exports com.rawlabs.compiler.snapi.truffle.emitter;
}
