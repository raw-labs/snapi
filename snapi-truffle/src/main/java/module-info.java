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
import com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationFromStringEntry;

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
      com.rawlabs.snapi.truffle.runtime.RawLanguageProvider;
  provides com.rawlabs.compiler.snapi.rql2.api.EntryExtension with
      com.rawlabs.snapi.truffle.emitter.builtin.aws_extension.TruffleAwsV4SignedRequestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.byte_extension.TruffleByteFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleEmptyCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleBuildCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFilterCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleOrderByCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
          .TruffleTransformCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleDistinctCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleCountCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleTupleAvgCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMinCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMaxCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleSumCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFirstCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleLastCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleTakeCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleUnnestCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFromCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleGroupCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
          .TruffleInternalJoinCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
          .TruffleInternalEquiJoinCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleUnionCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleExistsCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleZipCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMkStringCollectionEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvReadEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvParseEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.double_extension.TruffleDoubleFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.environment_extension
          .TruffleEnvironmentParameterEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildWithTypeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorGetEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.float_extension.TruffleFloatFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.function_extension.TruffleFunctionInvokeAfterEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleBuildIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.int_extension.TruffleIntFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.int_extension.TruffleIntRangeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.json_extension.TruffleReadJsonEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.json_extension.TruffleParseJsonEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.json_extension.TrufflePrintJsonEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleEmptyListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleBuildListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleGetListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleFilterListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleTransformListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleTakeListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleSumListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleMaxListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleMinListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleFirstListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleLastListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleCountListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleFromListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleUnsafeFromListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleGroupListEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleExistsListEntry,
      TruffleLocationFromStringEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationDescribeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationLsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationLlEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.long_extension.TruffleLongFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.long_extension.TruffleLongRangeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAbsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.mysql_extension.TruffleMySQLQueryEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableEmptyEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableIsNullEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableUnsafeGetEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableTransformEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.nullable_tryable_extension
          .TruffleFlatMapNullableTryableEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.oracle_extension.TruffleOracleQueryEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.postgresql_extension.TrufflePostgreSQLQueryEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordConcatEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordFieldsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordAddFieldEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordRemoveFieldEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordGetFieldByIndexEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.snowflake_extension.TruffleSnowflakeQueryEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateFromEpochDayEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateFromTimestampEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateParseEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateNowEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateYearEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateMonthEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateDayEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateSubtractEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateAddIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateSubtractIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalRoundEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.environment_extension.TruffleEnvironmentSecretEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.environment_extension.TruffleEnvironmentScopesEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalToMillisEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalFromMillisEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalParseEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalYearsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalMonthsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalWeeksEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalDaysEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalHoursEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalMinutesEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalSecondsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalMillisEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathPiEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathRandomEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathPowerEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAtn2Entry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAcosEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAsinEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAtanEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathCeilingEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathCosEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathCotEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathDegreesEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathExpEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathLogEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathLog10Entry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathRadiansEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSignEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSinEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSqrtEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathTanEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSquareEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathFloorEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpReadEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpGetEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPostEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPutEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpDeleteEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpHeadEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPatchEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpOptionsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpUrlEncode,
      com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpUrlDecode,
      com.rawlabs.snapi.truffle.emitter.builtin.xml_extension.TruffleReadXmlEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.xml_extension.TruffleParseXmlEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeCastEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeEmptyEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeMatchEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeProtectCastEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.binary_extension.TruffleBinaryBase64Entry,
      com.rawlabs.snapi.truffle.emitter.builtin.binary_extension.TruffleBinaryReadEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.binary_extension.TruffleFromStringBinaryEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampFromDateEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampParseEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampNowEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampRangeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampYearEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampMonthEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampDayEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampHourEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampMinuteEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampSecondEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampMillisEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampFromUnixTimestampEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampToUnixTimestampEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampTimeBucketEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampSubtractEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampAddIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
          .TruffleTimestampSubtractIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeParseEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeNowEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeHourEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeMinuteEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSecondEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeMillisEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSubtractEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeAddIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSubtractIntervalEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryFlatMapEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryUnsafeGetEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryIsErrorEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryIsSuccessEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryTransformEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.success_extension.TruffleSuccessBuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReadEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringContainsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringTrimEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLTrimEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringRTrimEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReplaceEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReverseEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReplicateEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringUpperEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLowerEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringSplitEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLengthEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringSubStringEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringCountSubStringEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringStartsWithEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringEmptyEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleBase64EntryExtension,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringEncodeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringDecodeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension
          .TruffleStringLevenshteinDistanceEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReadLinesEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringCapitalizeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.sqlserver_extension.TruffleSQLServerQueryEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.short_extension.TruffleShortFromEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexReplaceEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexMatchesEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexFirstMatchInEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexGroupsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.s3_extension.TruffleS3BuildEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleByteValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleBoolValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleDateValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleDoubleValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleFloatValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleIntervalValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleIntValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleListValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleLongValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleMandatoryExpArgsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleMandatoryValueArgsEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleOptionalExpArgsTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleOptionalValueArgsTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleRecordValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleShortValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension
          .TruffleStrictArgsColPassThroughTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStrictArgsTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStringValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleTimestampValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleTimeValueArgTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarExpArgsTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarNullableStringExpTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension
          .TruffleVarNullableStringValueTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarValueArgsTestEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoDecodeEntry,
      com.rawlabs.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoEncodeEntry;

  exports com.rawlabs.snapi.truffle.runtime;
  exports com.rawlabs.snapi.truffle.runtime.boundary;
  exports com.rawlabs.snapi.truffle.runtime.runtime.record;
  exports com.rawlabs.snapi.truffle.runtime.runtime.operators;
  exports com.rawlabs.snapi.truffle.runtime.runtime.function;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions.xml;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions.json;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions.csv;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions.binary;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions.rdbms;
  exports com.rawlabs.snapi.truffle.runtime.runtime.data_structures.treemap;
  exports com.rawlabs.snapi.truffle.runtime.runtime.primitives;
  exports com.rawlabs.snapi.truffle.runtime.runtime.list;
  exports com.rawlabs.snapi.truffle.runtime.runtime.or;
  exports com.rawlabs.snapi.truffle.runtime.runtime.generator.list;
  exports com.rawlabs.snapi.truffle.runtime.runtime.generator.collection;
  exports com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.abstract_generator;
  exports com.rawlabs
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .abstract_generator
      .compute_next;
  exports com.rawlabs
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
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .record_shaper;
  exports com.rawlabs
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .input_buffer;
  exports com.rawlabs
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .off_heap;
  exports com.rawlabs
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
      .snapi
      .truffle
      .runtime
      .runtime
      .generator
      .collection
      .off_heap_generator
      .off_heap
      .distinct;
  exports com.rawlabs.snapi.truffle.runtime.runtime.iterable;
  exports com.rawlabs.snapi.truffle.runtime.runtime.iterable.operations;
  exports com.rawlabs.snapi.truffle.runtime.runtime.iterable.list;
  exports com.rawlabs.snapi.truffle.runtime.runtime.iterable.sources;
  exports com.rawlabs.snapi.truffle.runtime.runtime.kryo;
  exports com.rawlabs.snapi.truffle.runtime.utils;
  exports com.rawlabs.snapi.truffle.runtime.tryable_nullable;
  exports com.rawlabs.snapi.truffle.runtime.ast;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.kryo;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.jdbc;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.json.reader;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.parser;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.json.writer;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.internal;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.csv.reader;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.csv.reader.parser;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.csv.writer;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.csv.writer.internal;
  exports com.rawlabs.snapi.truffle.runtime.ast.io.binary;
  exports com.rawlabs.snapi.truffle.runtime.ast.local;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.unary;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.collection;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.list;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.record;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.option;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.function;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.tryable;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.binary;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.literals;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.regex_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.type_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.environment_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.math_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.aws_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.http_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.short_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.double_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.long_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.decimal_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.float_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.int_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.byte_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.function_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.date_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.time_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.interval_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.string_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.location_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.binary_package;
  exports com.rawlabs.snapi.truffle.runtime.ast.expressions.aggregation;
  exports com.rawlabs.snapi.truffle.runtime.ast.controlflow;
  exports com.rawlabs.snapi.truffle.runtime.ast.osr;
  exports com.rawlabs.snapi.truffle.runtime.ast.osr.bodies;
  exports com.rawlabs.snapi.truffle.runtime.ast.osr.conditions;
  exports com.rawlabs.snapi.truffle.runtime.runtime.exceptions.validation;
  exports com.rawlabs.snapi.truffle.emitter.compiler;
  exports com.rawlabs.snapi.truffle.emitter.output;
  exports com.rawlabs.snapi.truffle.emitter.builtin;
  exports com.rawlabs.snapi.truffle.emitter;
}
