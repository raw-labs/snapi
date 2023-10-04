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
      raw.compiler.rql2.truffle.builtin.TruffleRegexReplaceEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRegexMatchesEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRegexFirstMatchInEntry,
      raw.compiler.rql2.truffle.builtin.TruffleRegexGroupsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleS3BuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleShortFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSnowflakeQueryEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSQLServerQueryEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringFromEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringReadEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringReplaceEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringReadLinesEntry,
      raw.compiler.rql2.truffle.builtin.TruffleSuccessBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampRangeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampTimeBucketEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTryTransformEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTryIsErrorEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTryIsSuccessEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTryFlatMapEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTryUnsafeGetEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTypeCastEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTypeProtectCastEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTypeEmptyEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTypeMatchEntry,
      raw.compiler.rql2.truffle.builtin.TruffleReadXmlEntry,
      raw.compiler.rql2.truffle.builtin.TruffleParseXmlEntry,
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
      raw.compiler.rql2.truffle.builtin.TruffleStringContainsEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringTrimEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringLTrimEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringRTrimEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringReverseEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringReplicateEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringUpperEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringLowerEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringSplitEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringLengthEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringSubStringEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringCountSubStringEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringStartsWithEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringEmptyEntry,
      raw.compiler.rql2.truffle.builtin.TruffleBase64EntryExtension,
      raw.compiler.rql2.truffle.builtin.TruffleStringEncodeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringDecodeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringLevenshteinDistanceEntry,
      raw.compiler.rql2.truffle.builtin.TruffleStringCapitalizeEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeParseEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeNowEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeHourEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeMinuteEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeSecondEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeMillisEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeSubtractEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeAddIntervalEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimeSubtractIntervalEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampBuildEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampFromDateEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampParseEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampNowEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampYearEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampMonthEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampDayEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampHourEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampMinuteEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampSecondEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampMillisEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampFromUnixTimestampEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampToUnixTimestampEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampSubtractEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampAddIntervalEntry,
      raw.compiler.rql2.truffle.builtin.TruffleTimestampSubtractIntervalEntry,
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
          TruffleBinaryBase64Entry,
      raw.compiler.snapi.truffle.builtin.binary_extension.TruffleBinaryReadEntry,
      raw.compiler.snapi.truffle.builtin.binary_extension.TruffleFromStringBinaryEntry;
}
