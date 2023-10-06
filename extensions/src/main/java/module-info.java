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
      raw.compiler.rql2.truffle.builtin.AwsV4SignedRequestEntry,
      raw.compiler.rql2.truffle.builtin.TruffleByteFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleEmptyCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleBuildCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFilterCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleOrderByCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTransformCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDistinctCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleCountCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTupleAvgCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMinCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMaxCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSumCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFirstCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleLastCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTakeCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleUnnestCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFromCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleGroupCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleInternalJoinCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleInternalEquiJoinCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleUnionCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleExistsCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleZipCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleMkStringCollectionEntry,
      raw.compiler.rql2.truffle.builtin.TruffleCsvReadEntry,
      raw.compiler.rql2.truffle.builtin.TruffleCsvParseEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDecimalFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDoubleFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleEnvironmentParameterEntry,
      raw.compiler.rql2.truffle.builtin.TruffleErrorBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleErrorBuildWithTypeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleErrorGetEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFloatFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleFunctionInvokeAfterEntry,
      raw.compiler.rql2.truffle.builtin.TruffleBuildIntervalEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntRangeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleReadJsonEntry,
      raw.compiler.rql2.truffle.builtin.TruffleParseJsonEntry,
      raw.compiler.rql2.truffle.builtin.TrufflePrintJsonEntry,
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
      raw.compiler.rql2.truffle.builtin.TruffleS3BuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSnowflakeQueryEntry,
      //      raw.compiler.rql2.truffle.builtin.TruffleFromStringBinaryEntryExtension,
      //      raw.compiler.rql2.truffle.builtin.TruffleBinaryReadEntry,
      //      raw.compiler.rql2.truffle.builtin.TruffleBinaryBase64Entry,
      raw.compiler.rql2.truffle.builtin.TruffleDateBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateFromEpochDayEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateFromTimestampEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateParseEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateNowEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateYearEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateMonthEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateDayEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateSubtractEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateAddIntervalEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDateSubtractIntervalEntry,
      raw.compiler.rql2.truffle.builtin.TruffleDecimalRoundEntry,
      raw.compiler.rql2.truffle.builtin.TruffleEnvironmentSecretEntry,
      raw.compiler.rql2.truffle.builtin.TruffleEnvironmentScopesEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalToMillisEntryExtension,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalFromMillisEntryExtension,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalParseEntryExtension,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalYearsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalMonthsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalWeeksEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalDaysEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalHoursEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalMinutesEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalSecondsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleIntervalMillisEntry,
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
      TruffleBinaryBase64Entry,
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
      raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexGroupsEntry;
}
