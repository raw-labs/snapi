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

package raw.compiler.snapi.truffle.compiler;

import java.util.*;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.nodes.Node;
import org.bitbucket.inkytonik.kiama.relation.TreeRelation;
import org.bitbucket.inkytonik.kiama.util.Entity;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.common.source.IdnExp;
import raw.compiler.common.source.SourceNode;
import raw.compiler.rql2.*;
import raw.compiler.rql2.api.EntryExtension;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.source.*;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarNullableStringExpTestEntry;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.controlflow.ExpBlockNode;
import raw.runtime.truffle.ast.controlflow.IfThenElseNode;
import raw.runtime.truffle.ast.expressions.binary.*;
import raw.runtime.truffle.ast.expressions.binary.DivNodeGen;
import raw.runtime.truffle.ast.expressions.binary.ModNodeGen;
import raw.runtime.truffle.ast.expressions.binary.MultNodeGen;
import raw.runtime.truffle.ast.expressions.binary.SubNodeGen;
import raw.runtime.truffle.ast.expressions.function.*;
import raw.runtime.truffle.ast.expressions.literals.*;
import raw.runtime.truffle.ast.expressions.option.OptionNoneNode;
import raw.runtime.truffle.ast.expressions.record.RecordProjNodeGen;
import raw.runtime.truffle.ast.expressions.unary.NegNodeGen;
import raw.runtime.truffle.ast.expressions.unary.NotNodeGen;
import raw.runtime.truffle.ast.local.*;
import raw.runtime.truffle.ast.local.ReadClosureVariableNodeGen;
import raw.runtime.truffle.ast.local.ReadLocalVariableNodeGen;
import raw.runtime.truffle.ast.local.WriteLocalVariableNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.function.Function;
import scala.Option;
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
            new raw.compiler.snapi.truffle.builtin.aws_extension.TruffleAwsV4SignedRequestEntry(),
            new raw.compiler.snapi.truffle.builtin.byte_extension.TruffleByteFromEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleEmptyCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleBuildCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleFilterCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleOrderByCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleTransformCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleDistinctCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleCountCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleTupleAvgCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleMinCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleMaxCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleSumCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleFirstCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleLastCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleTakeCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleUnnestCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleFromCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleGroupCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleInternalJoinCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleInternalEquiJoinCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleUnionCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleExistsCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleZipCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.collection_extension.TruffleMkStringCollectionEntry(),
            new raw.compiler.snapi.truffle.builtin.csv_extension.TruffleCsvReadEntry(),
            new raw.compiler.snapi.truffle.builtin.csv_extension.TruffleCsvParseEntry(),
            new raw.compiler.snapi.truffle.builtin.decimal_extension.TruffleDecimalFromEntry(),
            new raw.compiler.snapi.truffle.builtin.double_extension.TruffleDoubleFromEntry(),
            new raw.compiler.snapi.truffle.builtin.environment_extension.TruffleEnvironmentParameterEntry(),
            new raw.compiler.snapi.truffle.builtin.error_extension.TruffleErrorBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.error_extension.TruffleErrorBuildWithTypeEntry(),
            new raw.compiler.snapi.truffle.builtin.error_extension.TruffleErrorGetEntry(),
            new raw.compiler.snapi.truffle.builtin.float_extension.TruffleFloatFromEntry(),
            new raw.compiler.snapi.truffle.builtin.function_extension.TruffleFunctionInvokeAfterEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleBuildIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.int_extension.TruffleIntFromEntry(),
            new raw.compiler.snapi.truffle.builtin.int_extension.TruffleIntRangeEntry(),
            new raw.compiler.snapi.truffle.builtin.json_extension.TruffleReadJsonEntry(),
            new raw.compiler.snapi.truffle.builtin.json_extension.TruffleParseJsonEntry(),
            new raw.compiler.snapi.truffle.builtin.json_extension.TrufflePrintJsonEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleEmptyListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleBuildListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleGetListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleFilterListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleTransformListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleTakeListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleSumListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleMaxListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleMinListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleFirstListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleLastListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleCountListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleFromListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleUnsafeFromListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleGroupListEntry(),
            new raw.compiler.snapi.truffle.builtin.list_extension.TruffleExistsListEntry(),
            new raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationDescribeEntry(),
            new raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationLsEntry(),
            new raw.compiler.snapi.truffle.builtin.location_extension.TruffleLocationLlEntry(),
            new raw.compiler.snapi.truffle.builtin.long_extension.TruffleLongFromEntry(),
            new raw.compiler.snapi.truffle.builtin.long_extension.TruffleLongRangeEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAbsEntry(),
            new raw.compiler.snapi.truffle.builtin.mysql_extension.TruffleMySQLQueryEntry(),
            new raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableEmptyEntry(),
            new raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableIsNullEntry(),
            new raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableUnsafeGetEntry(),
            new raw.compiler.snapi.truffle.builtin.nullable_extension.TruffleNullableTransformEntry(),
            new raw.compiler.snapi.truffle.builtin.nullable_tryable_extension.TruffleFlatMapNullableTryableEntry(),
            new raw.compiler.snapi.truffle.builtin.oracle_extension.TruffleOracleQueryEntry(),
            new raw.compiler.snapi.truffle.builtin.postgresql_extension.TrufflePostgreSQLQueryEntry(),
            new raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordConcatEntry(),
            new raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordFieldsEntry(),
            new raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordAddFieldEntry(),
            new raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordRemoveFieldEntry(),
            new raw.compiler.snapi.truffle.builtin.record_extension.TruffleRecordGetFieldByIndexEntry(),
            new raw.compiler.snapi.truffle.builtin.snowflake_extension.TruffleSnowflakeQueryEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateFromEpochDayEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateFromTimestampEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateParseEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateNowEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateYearEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateMonthEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateDayEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateSubtractEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateAddIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.date_extension.TruffleDateSubtractIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.decimal_extension.TruffleDecimalRoundEntry(),
            new raw.compiler.snapi.truffle.builtin.environment_extension.TruffleEnvironmentSecretEntry(),
            new raw.compiler.snapi.truffle.builtin.environment_extension.TruffleEnvironmentScopesEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalToMillisEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalFromMillisEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalParseEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalYearsEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalMonthsEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalWeeksEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalDaysEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalHoursEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalMinutesEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalSecondsEntry(),
            new raw.compiler.snapi.truffle.builtin.interval_extension.TruffleIntervalMillisEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathPiEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathRandomEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathPowerEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAtn2Entry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAcosEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAsinEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathAtanEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathCeilingEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathCosEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathCotEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathDegreesEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathExpEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathLogEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathLog10Entry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathRadiansEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSignEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSinEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSqrtEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathTanEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathSquareEntry(),
            new raw.compiler.snapi.truffle.builtin.math_extension.TruffleMathFloorEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpReadEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpGetEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpPostEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpPutEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpDeleteEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpHeadEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpPatchEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpOptionsEntry(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpUrlEncode(),
            new raw.compiler.snapi.truffle.builtin.http_extension.TruffleHttpUrlDecode(),
            new raw.compiler.snapi.truffle.builtin.xml_extension.TruffleReadXmlEntry(),
            new raw.compiler.snapi.truffle.builtin.xml_extension.TruffleParseXmlEntry(),
            new raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeCastEntry(),
            new raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeEmptyEntry(),
            new raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeMatchEntry(),
            new raw.compiler.snapi.truffle.builtin.type_extension.TruffleTypeProtectCastEntry(),
            new raw.compiler.snapi.truffle.builtin.binary_extension.TruffleBinaryBase64Entry(),
            new raw.compiler.snapi.truffle.builtin.binary_extension.TruffleBinaryReadEntry(),
            new raw.compiler.snapi.truffle.builtin.binary_extension.TruffleFromStringBinaryEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampFromDateEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampParseEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampNowEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampRangeEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampYearEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampMonthEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampDayEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampHourEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampMinuteEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampSecondEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampMillisEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampFromUnixTimestampEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampToUnixTimestampEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampTimeBucketEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampSubtractEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampAddIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.timestamp_extension.TruffleTimestampSubtractIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeParseEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeNowEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeHourEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeMinuteEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeSecondEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeMillisEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeSubtractEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeAddIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.time_extension.TruffleTimeSubtractIntervalEntry(),
            new raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryFlatMapEntry(),
            new raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryUnsafeGetEntry(),
            new raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryIsErrorEntry(),
            new raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryIsSuccessEntry(),
            new raw.compiler.snapi.truffle.builtin.try_extension.TruffleTryTransformEntry(),
            new raw.compiler.snapi.truffle.builtin.success_extension.TruffleSuccessBuildEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringFromEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReadEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringContainsEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringTrimEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLTrimEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringRTrimEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReplaceEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReverseEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReplicateEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringUpperEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLowerEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringSplitEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLengthEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringSubStringEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringCountSubStringEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringStartsWithEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringEmptyEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleBase64EntryExtension(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringEncodeEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringDecodeEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringLevenshteinDistanceEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringReadLinesEntry(),
            new raw.compiler.snapi.truffle.builtin.string_extension.TruffleStringCapitalizeEntry(),
            new raw.compiler.snapi.truffle.builtin.sqlserver_extension.TruffleSQLServerQueryEntry(),
            new raw.compiler.snapi.truffle.builtin.short_extension.TruffleShortFromEntry(),
            new raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexReplaceEntry(),
            new raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexMatchesEntry(),
            new raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexFirstMatchInEntry(),
            new raw.compiler.snapi.truffle.builtin.regex_extension.TruffleRegexGroupsEntry(),
            new raw.compiler.snapi.truffle.builtin.s3_extension.TruffleS3BuildEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleByteValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleBoolValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleDateValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleDoubleValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleFloatValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleIntervalValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleIntValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleListValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleLongValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleMandatoryExpArgsEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleMandatoryValueArgsEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleOptionalExpArgsTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleOptionalValueArgsTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleRecordValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleOrValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleShortValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleStrictArgsColPassThroughTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleStrictArgsTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleStringValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleTimestampValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleTimeValueArgTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarExpArgsTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarNullableStringExpTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarNullableStringValueTestEntry(),
            new raw.compiler.snapi.truffle.builtin.test_extension.TruffleVarValueArgsTestEntry(),
            new raw.compiler.snapi.truffle.builtin.kryo_extension.TruffleKryoDecodeEntry(),
            new raw.compiler.snapi.truffle.builtin.kryo_extension.TruffleKryoEncodeEntry()
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
                    case Rql2OrType ignored ->
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
