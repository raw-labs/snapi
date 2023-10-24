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
import org.bitbucket.inkytonik.kiama.relation.TreeRelation;
import org.bitbucket.inkytonik.kiama.util.Entity;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.common.source.IdnExp;
import raw.compiler.common.source.SourceNode;
import raw.compiler.rql2.*;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.source.*;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
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
import raw.runtime.truffle.ast.expressions.function.ClosureNode;
import raw.runtime.truffle.ast.expressions.function.InvokeNode;
import raw.runtime.truffle.ast.expressions.function.MethodNode;
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

    public SnapiTruffleEmitter(Tree tree, RawLanguage rawLanguage, ProgramContext programContext) {
        this.tree = tree;
        this.analyzer = tree.analyzer();
        this.rawLanguage = rawLanguage;
        this.programContext = programContext;
    }

    private Type tipe(Exp e) {
        return analyzer.tipe(e);
    }

    protected RawLanguage getLanguage() {
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

    private FrameDescriptor.Builder getFrameDescriptorBuilder() {
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
        ClosureNode functionLiteralNode = new MethodNode(m.i().idn(), f, defaultArgs);
        int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
        addSlot(entity, Integer.toString(slot));
        return WriteLocalVariableNodeGen.create(functionLiteralNode, slot, null);
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
                ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(lf.p().ps()).stream()
                        .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                        .toArray(ExpressionNode[]::new);
                ClosureNode functionLiteralNode = new ClosureNode(f, defaultArgs);
                int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                addSlot(entity, Integer.toString(slot));
                yield WriteLocalVariableNodeGen.create(functionLiteralNode, slot, null);
            }
            case LetFunRec lfr -> {
                Entity entity = analyzer.entity().apply(lfr.i());
                int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
                addSlot(entity, Integer.toString(slot));
                Function f = recurseFunProto(lfr.p());
                ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(lfr.p().ps()).stream()
                        .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                        .toArray(ExpressionNode[]::new);
                ClosureNode functionLiteralNode = new ClosureNode(f, defaultArgs);
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

    public ClosureNode recurseLambda(TruffleBuildBody truffleBuildBody) {
        addScope();
        ExpressionNode functionBody = truffleBuildBody.buildBody();
        FrameDescriptor funcFrameDescriptor = dropScope();

        ProgramExpressionNode functionRootBody = new ProgramExpressionNode(rawLanguage, funcFrameDescriptor, functionBody);

        RootCallTarget rootCallTarget = functionRootBody.getCallTarget();
        Function f = new Function(rootCallTarget, new String[]{"x"});
        return new ClosureNode(f, new ExpressionNode[]{});
    }


    public ExpressionNode recurseExp(Exp in) {
        return switch (in) {
            case Exp ignored when tipe(in) instanceof PackageType || tipe(in) instanceof PackageEntryType ->
                    new ZeroedConstNode(Rql2ByteType.apply(new scala.collection.immutable.HashSet<Rql2TypeProperty>().seq()));
            case TypeExp typeExp -> new ZeroedConstNode((Rql2Type) typeExp.t());
            case NullConst ignored -> new OptionNoneNode(tipe(in));
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
                    case MethodEntity b -> ReadLocalVariableNodeGen.create(findSlot(b).slot(), null);
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
                ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(fa.p().ps()).stream()
                        .map(p -> p.e().isDefined() ? recurseExp(p.e().get()) : null)
                        .toArray(ExpressionNode[]::new);
                yield new ClosureNode(f, defaultArgs);
            }
            case FunApp fa when tipe(fa.f()) instanceof PackageEntryType -> {
                Type t = tipe(fa);
                PackageEntryType pet = (PackageEntryType) tipe(fa.f());
                yield JavaConverters.asJavaCollection(programContext.getPackage(pet.pkgName()).get().getEntries(pet.entName()))
                        .stream()
                        .filter(e -> e instanceof raw.compiler.snapi.truffle.TruffleEntryExtension)
                        .map(e -> (raw.compiler.snapi.truffle.TruffleEntryExtension) e)
                        .map(e -> e.toTruffle(
                                t,
                                JavaConverters.asJavaCollection(fa.args()).stream().map(a -> new Rql2Arg(a.e(), tipe(a.e()), a.idn())).toList(),
                                this
                        ))
                        .findFirst()
                        .orElseThrow(() -> new RawTruffleInternalErrorException("Could not find entry"));
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
