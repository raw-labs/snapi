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

package com.rawlabs.snapi.truffle.emitter.compiler;

import java.util.*;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import org.bitbucket.inkytonik.kiama.relation.TreeRelation;
import org.bitbucket.inkytonik.kiama.util.Entity;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.common.source.Exp;
import com.rawlabs.snapi.frontend.common.source.IdnExp;
import com.rawlabs.snapi.frontend.common.source.SourceNode;
import com.rawlabs.snapi.frontend.rql2.*;
import com.rawlabs.snapi.frontend.rql2.api.EntryExtension;
import com.rawlabs.snapi.frontend.rql2.api.Rql2Arg;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.location_extension.TruffleLocationFromStringEntry;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.StatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.controlflow.ExpBlockNode;
import com.rawlabs.snapi.truffle.runtime.ast.controlflow.IfThenElseNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.binary.*;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.binary.DivNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.binary.ModNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.binary.MultNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.binary.SubNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.function.*;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.literals.*;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.option.OptionNoneNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.record.RecordProjNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.unary.NegNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.unary.NotNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.local.*;
import com.rawlabs.snapi.truffle.runtime.ast.local.ReadClosureVariableNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.local.ReadLocalVariableNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.local.WriteLocalVariableNodeGen;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.runtime.function.Function;
import scala.collection.JavaConverters;

public class SnapiTruffleEmitter extends TruffleEmitter {

    private final Tree tree;
    private final RawLanguage rawLanguage;
    private final ProgramContext programContext;
    private final SemanticAnalyzer analyzer;
    private final String uniqueId = UUID.randomUUID().toString().replace("-", "").replace("_", "");
    private int idnCounter = 0;
    private final HashMap<Entity, String> idnSlot = new HashMap<>();
    private final List<HashMap<Entity, String>> slotMapScope = new LinkedList<>();
    private final List<FrameDescriptor.Builder> frameDescriptorBuilderScope = new LinkedList<>();

    private int funcCounter = 0;
    private final HashMap<Entity, String> funcMap = new HashMap<>();
    private final HashMap<Entity, Integer> entityDepth = new HashMap<>();

    private static final EntryExtension[] entries = {
            new com.rawlabs.snapi.truffle.emitter.builtin.aws_extension.TruffleAwsV4SignedRequestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.byte_extension.TruffleByteFromEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleEmptyCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleBuildCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFilterCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleOrderByCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleTransformCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleDistinctCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleCountCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleTupleAvgCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMinCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMaxCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleSumCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFirstCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleLastCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleTakeCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleUnnestCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleFromCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleGroupCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleInternalJoinCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleInternalEquiJoinCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleUnionCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleExistsCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleZipCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.collection_extension.TruffleMkStringCollectionEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvReadEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.csv_extension.TruffleCsvParseEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.decimal_extension.TruffleDecimalFromEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.double_extension.TruffleDoubleFromEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.environment_extension.TruffleEnvironmentParameterEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorBuildWithTypeEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.error_extension.TruffleErrorGetEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.float_extension.TruffleFloatFromEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.function_extension.TruffleFunctionInvokeAfterEntry(),
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
            new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableUnsafeGetEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension.TruffleNullableTransformEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.nullable_tryable_extension.TruffleFlatMapNullableTryableEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.oracle_extension.TruffleOracleQueryEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.postgresql_extension.TrufflePostgreSQLQueryEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordBuildEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordConcatEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordFieldsEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordAddFieldEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordRemoveFieldEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.record_extension.TruffleRecordGetFieldByIndexEntry(),
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
            new com.rawlabs.snapi.truffle.emitter.builtin.environment_extension.TruffleEnvironmentSecretEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.environment_extension.TruffleEnvironmentScopesEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalToMillisEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.interval_extension.TruffleIntervalFromMillisEntry(),
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
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampFromDateEntry(),
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
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampFromUnixTimestampEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampToUnixTimestampEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampTimeBucketEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampSubtractEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampAddIntervalEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension.TruffleTimestampSubtractIntervalEntry(),
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
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringCountSubStringEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringStartsWithEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringEmptyEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleBase64EntryExtension(),
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringEncodeEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringDecodeEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.string_extension.TruffleStringLevenshteinDistanceEntry(),
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
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleOptionalValueArgsTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleRecordValueArgTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleShortValueArgTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStrictArgsColPassThroughTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStrictArgsTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleStringValueArgTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleTimestampValueArgTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleTimeValueArgTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarExpArgsTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarNullableStringExpTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarNullableStringValueTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.test_extension.TruffleVarValueArgsTestEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoDecodeEntry(),
            new com.rawlabs.snapi.truffle.emitter.builtin.kryo_extension.TruffleKryoEncodeEntry()
    };

    private static TruffleEntryExtension getEntry(String pkgName, String entName) {
        for (EntryExtension entry : entries) {
            if (entry.packageName().equals(pkgName) && entry.entryName().equals(entName)) {
                return (TruffleEntryExtension) entry;
            }
        }
        throw new RawTruffleInternalErrorException("Could not find entry for " + pkgName + "." + entName);
    }

    public SnapiTruffleEmitter(Tree tree, RawLanguage rawLanguage, ProgramContext programContext) {
        this.tree = tree;
        this.analyzer = tree.analyzer();
        this.rawLanguage = rawLanguage;
        this.programContext = programContext;
    }

    private Type tipe(Exp e) {
        return analyzer.tipe(e);
    }

    public RawLanguage getLanguage() {
        return this.rawLanguage;
    }

    private int getCurrentDepth() {
        return slotMapScope.size();
    }

    private void setEntityDepth(Entity e) {
        entityDepth.put(e, getCurrentDepth());
    }

    private int getEntityDepth(Entity e) {
        return entityDepth.get(e);
    }

    private String getIdnName(Entity entity) {
        return idnSlot.putIfAbsent(entity, String.format("idn%s_%d", uniqueId, ++idnCounter));
    }

    protected void addScope() {
        slotMapScope.add(0, new HashMap<>());
        frameDescriptorBuilderScope.add(0, FrameDescriptor.newBuilder());
    }

    protected FrameDescriptor dropScope() {
        slotMapScope.remove(0);
        FrameDescriptor.Builder frameDescriptorBuilder = frameDescriptorBuilderScope.remove(0);
        return frameDescriptorBuilder.build();
    }

    public FrameDescriptor.Builder getFrameDescriptorBuilder() {
        return frameDescriptorBuilderScope.get(0);
    }

    private void addSlot(Entity entity, String slot) {
        slotMapScope.get(0).put(entity, slot);
    }

    protected StatementNode emitMethod(Rql2Method m) {
        Entity entity = analyzer.entity().apply(m.i());
        FunProto fp = m.p();
        Function f = recurseFunProto(fp);
        ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(fp.ps()).stream()
                .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                .toArray(ExpressionNode[]::new);
        ExpressionNode node;
        boolean hasFreeVars = analyzer.freeVars(m).nonEmpty();
        node = new MethodNode(m.i().idn(), f, defaultArgs, hasFreeVars);
        int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
        addSlot(entity, Integer.toString(slot));
        return WriteLocalVariableNodeGen.create(node, slot, null);
    }

    private SlotLocation findSlot(Entity entity) {
        for (int depth = 0; ; depth++) {
            HashMap<Entity, String> curSlot = slotMapScope.get(depth);
            String slot = curSlot.get(entity);
            if (slot != null) {
                return new SlotLocation(depth, Integer.parseInt(slot));
            }
        }
    }

    private String getFuncIdn(Entity entity) {
        return funcMap.putIfAbsent(entity, String.format("func%s_%d", uniqueId, ++funcCounter));
    }

    private String getLambdaFuncIdn() {
        return String.format("func%s_%d", uniqueId, ++funcCounter);
    }

    private StatementNode recurseLetDecl(LetDecl ld) {
        return switch (ld) {
            case LetBind lb -> {
                Entity entity = analyzer.entity().apply(lb.i());
                Rql2Type rql2Type = (Rql2Type) tipe(lb.e());
                int slot = switch (rql2Type) {
                    case Rql2UndefinedType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case ExpType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2ByteType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Byte, getIdnName(entity), null);
                    case Rql2ShortType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Int, getIdnName(entity), null);
                    case Rql2IntType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Int, getIdnName(entity), null);
                    case Rql2LongType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Long, getIdnName(entity), null);
                    case Rql2FloatType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Float, getIdnName(entity), null);
                    case Rql2DoubleType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Double, getIdnName(entity), null);
                    case Rql2DecimalType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2BoolType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Boolean, getIdnName(entity), null);
                    case Rql2StringType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2DateType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2TimeType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2TimestampType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2IntervalType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2BinaryType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2IterableType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2ListType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case FunType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2RecordType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case Rql2LocationType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case PackageType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    case PackageEntryType ignored ->
                            getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                    default -> throw new RawTruffleInternalErrorException();
                };
                addSlot(entity, Integer.toString(slot));
                yield WriteLocalVariableNodeGen.create(recurseExp(lb.e()), slot, rql2Type);
            }
            case LetFun lf -> {
                Entity entity = analyzer.entity().apply(lf.i());
                Function f = recurseFunProto(lf.p());
                boolean hasFreeVars = analyzer.freeVars(lf).nonEmpty();
                ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(lf.p().ps()).stream()
                        .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                        .toArray(ExpressionNode[]::new);

                ExpressionNode node;
                // If the function has free variables it is a Closure
                if (hasFreeVars) {
                    node = new ClosureNode(f, defaultArgs);
                }
                // If the function has optional arguments it is a Method
                else {
                    node = new MethodNode(null, f, defaultArgs, hasFreeVars);
                }
                int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                addSlot(entity, Integer.toString(slot));
                yield WriteLocalVariableNodeGen.create(node, slot, null);
            }
            case LetFunRec lfr -> {
                Entity entity = analyzer.entity().apply(lfr.i());
                int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                addSlot(entity, Integer.toString(slot));
                Function f = recurseFunProto(lfr.p());
                ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(lfr.p().ps()).stream()
                        .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                        .toArray(ExpressionNode[]::new);
                RecClosureNode functionLiteralNode = new RecClosureNode(f, defaultArgs);
                yield WriteLocalVariableNodeGen.create(functionLiteralNode, slot, null);
            }
            default -> throw new RawTruffleInternalErrorException();
        };
    }

    private Function recurseFunProto(FunProto fp) {
        addScope();
        JavaConverters.asJavaCollection(fp.ps())
                .forEach(p -> setEntityDepth(analyzer.entity().apply(p.i())));

        ExpressionNode functionBody = recurseExp(fp.b().e());
        FrameDescriptor funcFrameDescriptor = dropScope();

        ProgramExpressionNode functionRootBody =
                new ProgramExpressionNode(rawLanguage, funcFrameDescriptor, functionBody);

        RootCallTarget rootCallTarget = functionRootBody.getCallTarget();

        String[] argNames =
                JavaConverters.asJavaCollection(fp.ps()).stream()
                        .map(p -> p.i().idn())
                        .toArray(String[]::new);

        return new Function(rootCallTarget, argNames);
    }

    // Used only for tests at the moment
    public ClosureNode recurseLambda(TruffleBuildBody truffleBuildBody) {
        addScope();
        ExpressionNode functionBody = truffleBuildBody.buildBody();
        FrameDescriptor funcFrameDescriptor = dropScope();

        ProgramExpressionNode functionRootBody = new ProgramExpressionNode(rawLanguage, funcFrameDescriptor, functionBody);

        RootCallTarget rootCallTarget = functionRootBody.getCallTarget();
        Function f = new Function(rootCallTarget, new String[]{"x"});
        return new ClosureNode(f, new ExpressionNode[]{null});
    }

    public ExpressionNode recurseExp(Exp in) {
        return switch (in) {
            case Exp ignored when tipe(in) instanceof PackageType || tipe(in) instanceof PackageEntryType ->
                    new ZeroedConstNode(Rql2ByteType.apply(new scala.collection.immutable.HashSet<Rql2TypeProperty>().seq()));
            case TypeExp typeExp -> new ZeroedConstNode((Rql2Type) typeExp.t());
            case NullConst ignored -> new OptionNoneNode();
            case BoolConst v -> new BoolNode(v.value());
            case ByteConst v -> new ByteNode(v.value());
            case ShortConst v -> new ShortNode(v.value());
            case IntConst v -> new IntNode(v.value());
            case LongConst v -> new LongNode(v.value());
            case FloatConst v -> new FloatNode(v.value());
            case DoubleConst v -> new DoubleNode(v.value());
            case DecimalConst v -> new DecimalNode(v.value());
            case StringConst v -> new StringNode(v.value());
            case TripleQuotedStringConst v -> new StringNode(v.value());
            case BinaryExp be -> switch (be.binaryOp()) {
                case And ignored -> new AndNode(recurseExp(be.left()), recurseExp(be.right()));
                case Or ignored -> new OrNode(recurseExp(be.left()), recurseExp(be.right()));
                case Plus ignored -> new PlusNode(recurseExp(be.left()), recurseExp(be.right()));
                case Sub ignored -> SubNodeGen.create(recurseExp(be.left()), recurseExp(be.right()));
                case Mult ignored -> MultNodeGen.create(recurseExp(be.left()), recurseExp(be.right()));
                case Mod ignored -> ModNodeGen.create(recurseExp(be.left()), recurseExp(be.right()));
                case Div ignored -> DivNodeGen.create(recurseExp(be.left()), recurseExp(be.right()));
                case Gt ignored -> new GtNode(recurseExp(be.left()), recurseExp(be.right()));
                case Ge ignored -> new GeNode(recurseExp(be.left()), recurseExp(be.right()));
                case Eq ignored -> new EqNode(recurseExp(be.left()), recurseExp(be.right()));
                case Neq ignored -> NotNodeGen.create(new EqNode(recurseExp(be.left()), recurseExp(be.right())));
                case Lt ignored -> new LtNode(recurseExp(be.left()), recurseExp(be.right()));
                case Le ignored -> new LeNode(recurseExp(be.left()), recurseExp(be.right()));
                default -> throw new RawTruffleInternalErrorException();
            };
            case BinaryConst bc -> new BinaryConstNode(bc.bytes());
            case LocationConst lc -> new LocationConstNode(lc.bytes(), lc.publicDescription());
            case UnaryExp ue -> switch (ue.unaryOp()) {
                case Neg ignored -> NegNodeGen.create(recurseExp(ue.exp()));
                case Not ignored -> NotNodeGen.create(recurseExp(ue.exp()));
                default -> throw new RawTruffleInternalErrorException();
            };
            case IdnExp ie -> {
                Entity entity = analyzer.entity().apply(ie.idn());
                yield switch (entity) {
                    case MethodEntity b -> {
                        SlotLocation slotLocation = findSlot(b);
                        yield slotLocation.depth() == 0 ? ReadLocalVariableNodeGen.create(slotLocation.slot(), null) :
                                ReadClosureVariableNodeGen.create(slotLocation.depth(), slotLocation.slot(), null);
                    }
                    case LetBindEntity b -> {
                        SlotLocation slotLocation = findSlot(b);
                        yield slotLocation.depth() == 0 ? ReadLocalVariableNodeGen.create(slotLocation.slot(), (Rql2Type) tipe(b.b().e())) :
                                ReadClosureVariableNodeGen.create(slotLocation.depth(), slotLocation.slot(), (Rql2Type) tipe(b.b().e()));
                    }
                    case LetFunEntity f -> {
                        SlotLocation slotLocation = findSlot(f);
                        yield slotLocation.depth() == 0 ? ReadLocalVariableNodeGen.create(slotLocation.slot(), null) :
                                ReadClosureVariableNodeGen.create(slotLocation.depth(), slotLocation.slot(), (Rql2Type) analyzer.idnType(f.f().i()));
                    }
                    case LetFunRecEntity f -> {
                        SlotLocation slotLocation = findSlot(f);
                        yield slotLocation.depth() == 0 ? ReadLocalVariableNodeGen.create(slotLocation.slot(), null) :
                                ReadClosureVariableNodeGen.create(slotLocation.depth(), slotLocation.slot(), (Rql2Type) analyzer.idnType(f.f().i()));
                    }
                    case FunParamEntity f -> {
                        int depth = getCurrentDepth() - getEntityDepth(f);
                        if (depth == 0) {
                            TreeRelation<SourceNode> p = tree.parent();
                            FunProto fpr = (FunProto) JavaConverters.asJavaCollection(p.apply(f.f()))
                                    .stream()
                                    .filter(n -> n instanceof FunProto)
                                    .findFirst()
                                    .orElseThrow();
                            List<FunParam> fp = JavaConverters.asJavaCollection(fpr.ps()).stream().map(fpar -> (FunParam) fpar).toList();
                            int idx = fp.indexOf(f.f());
                            yield new ReadParamNode(idx);
                        } else {
                            TreeRelation<SourceNode> p = tree.parent();
                            FunProto fpr = (FunProto) JavaConverters.asJavaCollection(p.apply(f.f()))
                                    .stream()
                                    .filter(n -> n instanceof FunProto)
                                    .findFirst()
                                    .orElseThrow();
                            List<FunParam> fp = JavaConverters.asJavaCollection(fpr.ps()).stream().map(fpar -> (FunParam) fpar).toList();
                            int idx = fp.indexOf(f.f());
                            yield new ReadParamClosureNode(depth, idx);
                        }
                    }
                    default -> throw new RawTruffleInternalErrorException("Unknown entity type");
                };
            }
            case IfThenElse ite -> new IfThenElseNode(recurseExp(ite.e1()), recurseExp(ite.e2()), recurseExp(ite.e3()));
            case Proj proj -> RecordProjNodeGen.create(recurseExp(proj.e()), new StringNode(proj.i()));
            case Let let -> {
                StatementNode[] decls = JavaConverters.asJavaCollection(let.decls()).stream().map(this::recurseLetDecl).toArray(StatementNode[]::new);
                yield new ExpBlockNode(decls, recurseExp(let.e()));
            }
            case FunAbs fa -> {
                Function f = recurseFunProto(fa.p());
                boolean hasFreeVars = analyzer.freeVars(fa).nonEmpty();
                ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(fa.p().ps()).stream()
                        .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                        .toArray(ExpressionNode[]::new);
                // If the function has free variables it is a Closure
                if (hasFreeVars) {
                    yield new ClosureNode(f, defaultArgs);
                }
                // If the function has optional arguments it is a Method
                else {
                    yield new MethodNode(null, f, defaultArgs, false);
                }
            }
            case FunApp fa when tipe(fa.f()) instanceof PackageEntryType -> {
                Type t = tipe(fa);
                PackageEntryType pet = (PackageEntryType) tipe(fa.f());
                TruffleEntryExtension e = getEntry(pet.pkgName(), pet.entName());
                yield e.toTruffle(
                        t,
                        JavaConverters.asJavaCollection(fa.args()).stream().map(a -> new Rql2Arg(a.e(), tipe(a.e()), a.idn())).toList(),
                        this
                );
            }
            case FunApp fa -> {
                String[] argNames = JavaConverters.asJavaCollection(fa.args()).stream().map(a -> a.idn().isDefined() ? a.idn().get() : null).toArray(String[]::new);
                ExpressionNode[] exps = JavaConverters.asJavaCollection(fa.args()).stream().map(a -> recurseExp(a.e())).toArray(ExpressionNode[]::new);
                yield new InvokeNode(recurseExp(fa.f()), argNames, exps);
            }
            default -> throw new RawTruffleInternalErrorException("Unknown expression type");
        };
    }
}
