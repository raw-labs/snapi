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

import raw.compiler.snapi.truffle.builtin.binary_extension.TruffleBinaryBase64Entry;

module raw.language.extensions {
  requires scala.library;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires org.graalvm.truffle;
  requires raw.language;

  exports raw.compiler.snapi.truffle;
  exports raw.compiler.snapi.truffle.compiler;
  exports raw.compiler.rql2.truffle;

  provides raw.compiler.api.CompilerServiceBuilder with
      raw.compiler.rql2.truffle.Rql2TruffleCompilerServiceBuilder;
  provides raw.compiler.common.CommonCompilerBuilder with
      raw.compiler.rql2.truffle.Rql2TruffleCompilerBuilder;
  provides raw.compiler.rql2.api.EntryExtension with
      raw.compiler.snapi.truffle.builtin.aws_extension.AwsV4SignedRequestEntry,
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
      raw.compiler.snapi.truffle.builtin.collection_extension.TruffleInternalEquiJoinCollectionEntry,
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
      raw.compiler.rql2.truffle.builtin.TruffleEmptyListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleBuildListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleGetListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFilterListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTransformListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTakeListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSumListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMaxListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMinListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFirstListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLastListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleCountListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFromListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleUnsafeFromListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleGroupListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleExistsListEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLocationBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLocationDescribeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLocationLsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLocationLlEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLongFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLongRangeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathAbsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMySQLQueryEntry,
      raw.compiler.rql2.truffle.builtin.TruffleNullableEmptyEntry,
      raw.compiler.rql2.truffle.builtin.TruffleNullableBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleNullableIsNullEntry,
      raw.compiler.rql2.truffle.builtin.TruffleNullableUnsafeGetEntry,
      raw.compiler.rql2.truffle.builtin.TruffleNullableTransformEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFlatMapNullableTryableEntry,
      raw.compiler.rql2.truffle.builtin.TruffleOracleQueryEntry,
      raw.compiler.rql2.truffle.builtin.TrufflePostgreSQLQueryEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRecordBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRecordConcatEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRecordFieldsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRecordAddFieldEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRecordRemoveFieldEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRecordGetFieldByIndexEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSnowflakeQueryEntry,
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
      raw.compiler.rql2.truffle.builtin.TruffleMathPiEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathRandomEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathPowerEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathAtn2Entry,
      raw.compiler.rql2.truffle.builtin.TruffleMathAcosEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathAsinEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathAtanEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathCeilingEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathCosEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathCotEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathDegreesEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathExpEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathLogEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathLog10Entry,
      raw.compiler.rql2.truffle.builtin.TruffleMathRadiansEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathSignEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathSinEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathSqrtEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathTanEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathSquareEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMathFloorEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpReadEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpGetEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpPostEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpPutEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpDeleteEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpHeadEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpPatchEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpOptionsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleHttpUrlEncode,
      raw.compiler.rql2.truffle.builtin.TruffleHttpUrlDecode,
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
      raw.compiler.snapi.truffle.builtin.s3_extension.TruffleS3BuildEntry;
}
