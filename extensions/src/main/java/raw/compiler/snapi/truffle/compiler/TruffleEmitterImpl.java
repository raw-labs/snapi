package raw.compiler.snapi.truffle.compiler;

import java.util.*;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import org.bitbucket.inkytonik.kiama.util.Entity;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.common.source.IdnExp;
import raw.compiler.rql2.MethodEntity;
import raw.compiler.rql2.ProgramContext;
import raw.compiler.rql2.SemanticAnalyzer;
import raw.compiler.rql2.Tree;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.expressions.binary.*;
import raw.runtime.truffle.ast.expressions.function.ClosureNode;
import raw.runtime.truffle.ast.expressions.literals.*;
import raw.runtime.truffle.ast.expressions.option.OptionNoneNode;
import raw.runtime.truffle.ast.expressions.unary.NegNodeGen;
import raw.runtime.truffle.ast.expressions.unary.NotNodeGen;
import raw.runtime.truffle.ast.local.WriteLocalVariableNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.function.Function;
import scala.collection.JavaConverters;

public class TruffleEmitterImpl implements TruffleEmitter {

  private final Tree tree;
  private final RawLanguage rawLanguage;
  private final ProgramContext programContext;
  private SemanticAnalyzer analyzer;
  private final String uniqueId = UUID.randomUUID().toString().replace("-", "").replace("_", "");
  private int idnCounter = 0;
  private HashMap<Entity, String> idnSlot = new HashMap<>();
  private List<HashMap<Entity, String>> slotMapScope = new LinkedList<>();
  private List<FrameDescriptor.Builder> frameDescriptorBuilderScope = new LinkedList<>();

  private int funcCounter = 0;
  private HashMap<Entity, String> funcMap = new HashMap<>();
  private HashMap<Entity, Integer> entityDepth = new HashMap<>();

  public TruffleEmitterImpl(Tree tree, RawLanguage rawLanguage, ProgramContext programContext) {
    this.tree = tree;
    this.analyzer = tree.analyzer();
    this.rawLanguage = rawLanguage;
    this.programContext = programContext;
  }

  private Type tipe(Exp e){
    return analyzer.tipe(e);
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

  private String getIdnName(Entity entity){
    return idnSlot.putIfAbsent(entity, String.format("idn%s_%d", uniqueId, ++idnCounter));
  }

  private void addScope() {
    slotMapScope.add(new HashMap<>());
    frameDescriptorBuilderScope.add(FrameDescriptor.newBuilder());
  }

  private FrameDescriptor dropScope() {
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

  private StatementNode emitMethod(Rql2Method m) {
    Entity entity = analyzer.entity().apply(m.i());
    FunProto fp = m.p();
    Function f = recurseFunProto(fp);
    ExpressionNode[] defaultArgs = JavaConverters.asJavaCollection(fp.ps()).stream()
            .map(p -> ((FunParam) p).e().isDefined() ? recurseExp(((FunParam) p).e().get()) : null)
            .toArray(ExpressionNode[]::new);
    ClosureNode functionLiteralNode = new ClosureNode(f, defaultArgs);
    int slot = getFrameDescriptorBuilder().addSlot(FrameSlotKind.Object, getIdnName(entity), null);
    addSlot(entity, Integer.toString(slot));
    return WriteLocalVariableNodeGen.create(functionLiteralNode,slot, null);
  }

  private SlotLocation findSlot(Entity entity){
    int depth = 0;
    HashMap<Entity, String> curSlot = slotMapScope.get(depth);
    String slot = curSlot.get(entity);
    while (slot == null) {
      slot = curSlot.get(entity);
      depth += 1;
      curSlot = slotMapScope.get(depth);
    }
    return new SlotLocation(depth, Integer.parseInt(slot));
  }

  private String getFuncIdn(Entity entity){
    return funcMap.putIfAbsent(entity, String.format("func%s_%d", uniqueId, ++funcCounter));
  }

  private String getLambdaFuncIdn(){
    return  String.format("func%s_%d", uniqueId, ++funcCounter);
  }

  private StatementNode recurseLetDecl(LetDecl ld) {
    return null;
  }

  private Function recurseFunProto(FunProto fp) {
    addScope();
    JavaConverters.asJavaCollection(fp.ps()).stream()
        .map(p -> (FunParam) p)
        .forEach(p -> setEntityDepth(analyzer.entity().apply(p.i())));

    ExpressionNode functionBody = recurseExp(fp.b().e());
    FrameDescriptor funcFrameDescriptor = dropScope();

    ProgramExpressionNode functionRootBody =
        new ProgramExpressionNode(rawLanguage, funcFrameDescriptor, functionBody);

    RootCallTarget rootCallTarget = functionRootBody.getCallTarget();

    String[] argNames =
        JavaConverters.asJavaCollection(fp.ps()).stream()
            .map(p -> ((FunParam) p).i().idn())
            .toArray(String[]::new);

    return new Function(rootCallTarget, argNames);
  }

  private ClosureNode recurseLambda(TruffleBuildBody truffleBuildBody) {

  }

  private ExpressionNode recurseExp(Exp in) {
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
      case IdnExp ie -> switch (analyzer.entity().apply(ie.idn())){
        case MethodEntity b -> // NOT FINISHED
      };
    };
  }
}
