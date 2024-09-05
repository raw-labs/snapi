/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.emitter;

import com.rawlabs.snapi.frontend.snapi.extensions.EntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationFromStringEntry;

public class SnapiExtension {
  public static final EntryExtension[] entries = {
    new com.rawlabs.snapi.truffle.emitter.builtin.aws_extension.TruffleAwsV4SignedRequestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.byte_extension.TruffleByteFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleEmptyCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleBuildCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleFilterCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleOrderByCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleTransformCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleDistinctCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleCountCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleTupleAvgCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMinCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMaxCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleSumCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleFirstCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleLastCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleTakeCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleUnnestCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFromCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleGroupCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleInternalJoinCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleInternalEquiJoinCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleUnionCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleExistsCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleZipCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension
        .TruffleMkStringCollectionEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvReadEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvParseEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.double_extension.TruffleDoubleFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.environment_extension
        .TruffleEnvironmentParameterEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildWithTypeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorGetEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.float_extension.TruffleFloatFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.function_extension
        .TruffleFunctionInvokeAfterEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleBuildIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.int_extension.TruffleIntFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.int_extension.TruffleIntRangeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.json_extension.TruffleReadJsonEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.json_extension.TruffleParseJsonEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.json_extension.TrufflePrintJsonEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleEmptyListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleBuildListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleGetListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleFilterListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleTransformListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleTakeListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleSumListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleMaxListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleMinListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleFirstListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleLastListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleCountListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleFromListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleUnsafeFromListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleGroupListEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.list_extension.TruffleExistsListEntry(),
    new TruffleLocationFromStringEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationDescribeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationLsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationLlEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.long_extension.TruffleLongFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.long_extension.TruffleLongRangeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAbsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.mysql_extension.TruffleMySQLQueryEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableEmptyEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableIsNullEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension
        .TruffleNullableUnsafeGetEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension
        .TruffleNullableTransformEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.nullable_tryable_extension
        .TruffleFlatMapNullableTryableEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.oracle_extension.TruffleOracleQueryEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.postgresql_extension
        .TrufflePostgreSQLQueryEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordConcatEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordFieldsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordAddFieldEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordRemoveFieldEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.record_extension
        .TruffleRecordGetFieldByIndexEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.snowflake_extension.TruffleSnowflakeQueryEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateFromEpochDayEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateFromTimestampEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateParseEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateNowEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateYearEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateMonthEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateDayEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateSubtractEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateAddIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.date_extension.TruffleDateSubtractIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalRoundEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.environment_extension
        .TruffleEnvironmentSecretEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.environment_extension
        .TruffleEnvironmentScopesEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalToMillisEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension
        .TruffleIntervalFromMillisEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalParseEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalYearsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalMonthsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalWeeksEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalDaysEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalHoursEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalMinutesEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalSecondsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalMillisEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathPiEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathRandomEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathPowerEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAtn2Entry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAcosEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAsinEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathAtanEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathCeilingEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathCosEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathCotEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathDegreesEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathExpEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathLogEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathLog10Entry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathRadiansEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSignEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSinEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSqrtEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathTanEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathSquareEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.math_extension.TruffleMathFloorEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpReadEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpGetEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPostEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPutEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpDeleteEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpHeadEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpPatchEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpOptionsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpUrlEncode(),
    new com.rawlabs.snapi.truffle.emitter.builtin.http_extension.TruffleHttpUrlDecode(),
    new com.rawlabs.snapi.truffle.emitter.builtin.xml_extension.TruffleReadXmlEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.xml_extension.TruffleParseXmlEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeCastEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeEmptyEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeMatchEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.type_extension.TruffleTypeProtectCastEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.binary_extension.TruffleBinaryBase64Entry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.binary_extension.TruffleBinaryReadEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.binary_extension.TruffleFromStringBinaryEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampFromDateEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampParseEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampNowEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampRangeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampYearEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampMonthEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampDayEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampHourEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampMinuteEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampSecondEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampMillisEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampFromUnixTimestampEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampToUnixTimestampEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampTimeBucketEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampSubtractEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampAddIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension
        .TruffleTimestampSubtractIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeParseEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeNowEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeHourEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeMinuteEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSecondEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeMillisEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSubtractEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeAddIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.time_extension.TruffleTimeSubtractIntervalEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryFlatMapEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryUnsafeGetEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryIsErrorEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryIsSuccessEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.try_extension.TruffleTryTransformEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.success_extension.TruffleSuccessBuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReadEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringContainsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringTrimEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLTrimEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringRTrimEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReplaceEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReverseEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReplicateEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringUpperEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLowerEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringSplitEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLengthEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringSubStringEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension
        .TruffleStringCountSubStringEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringStartsWithEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringEmptyEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleBase64EntryExtension(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringEncodeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringDecodeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension
        .TruffleStringLevenshteinDistanceEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringReadLinesEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringCapitalizeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.sqlserver_extension.TruffleSQLServerQueryEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.short_extension.TruffleShortFromEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexReplaceEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexMatchesEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexFirstMatchInEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.regex_extension.TruffleRegexGroupsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.s3_extension.TruffleS3BuildEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleByteValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleBoolValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleDateValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleDoubleValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleFloatValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleIntervalValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleIntValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleListValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleLongValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleMandatoryExpArgsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleMandatoryValueArgsEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleOptionalExpArgsTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension
        .TruffleOptionalValueArgsTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleRecordValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleShortValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension
        .TruffleStrictArgsColPassThroughTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStrictArgsTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStringValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension
        .TruffleTimestampValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleTimeValueArgTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarExpArgsTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension
        .TruffleVarNullableStringExpTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension
        .TruffleVarNullableStringValueTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarValueArgsTestEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoDecodeEntry(),
    new com.rawlabs.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoEncodeEntry()
  };
}
