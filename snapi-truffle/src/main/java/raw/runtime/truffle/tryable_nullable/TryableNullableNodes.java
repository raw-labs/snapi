package raw.runtime.truffle.tryable_nullable;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.NullObject;

public class TryableNullableNodes {
  @NodeInfo(shortName = "TryableNodes.IsNull")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(Nullable.class)
  public abstract static class IsNullNode extends Node {

    public abstract boolean execute(Node node, Object value);

    @Specialization(guards = "isNull(value)")
    static boolean exec(Node node, NullObject value) {
      return true;
    }

    @Specialization(guards = "!isNull(value)")
    static boolean exec(Node node, Object value) {
      return false;
    }
  }

  @NodeInfo(shortName = "TryableNodes.IsError")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(Tryable.class)
  public abstract static class IsErrorNode extends Node {

    public abstract boolean execute(Node node, Object value);

    @Specialization(guards = "isError(value)")
    static boolean exec(Node node, ErrorObject value) {
      return true;
    }

    @Specialization(guards = "!isError(value)")
    static boolean exec(Node node, Object value) {
      return false;
    }
  }

  @NodeInfo(shortName = "TryableNodes.GetFailure")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(Tryable.class)
  public abstract static class GetErrorNode extends Node {

    public abstract String execute(Node node, Object value);

    @Specialization(guards = "isError(value)")
    static String exec(Node node, ErrorObject value) {
      return value.getMessage();
    }

    @Specialization(guards = "!isError(value)")
    static String exec(Node node, Object value) {
      throw new RawTruffleRuntimeException("not a failure");
    }
  }

  @NodeInfo(shortName = "TryableNodes.HandlePredicate")
  @GenerateUncached
  @GenerateInline
  @ImportStatic({Tryable.class, Nullable.class})
  public abstract static class HandlePredicateNode extends Node {

    public abstract boolean execute(Node node, Object value, boolean defaultValue);

    @Specialization(guards = "isError(value)")
    static boolean exec(Node node, ErrorObject value, boolean defaultValue) {
      return defaultValue;
    }

    @Specialization(guards = "isNull(value)")
    static boolean exec(Node node, NullObject value, boolean defaultValue) {
      return defaultValue;
    }

    @Specialization(guards = {"!isError(value)", "!isNull(value)"})
    static boolean exec(Node node, boolean value, boolean defaultValue) {
      return value;
    }
  }

  @NodeInfo(shortName = "TryableNodes.GetOrElse")
  @GenerateUncached
  @GenerateInline
  @ImportStatic({Tryable.class, Nullable.class})
  public abstract static class GetOrElseNode extends Node {

    public abstract Object execute(Node node, Object value, Object defaultValue);

    @Specialization(guards = "isError(value)")
    static Object exec(Node node, ErrorObject value, Object defaultValue) {
      return defaultValue;
    }

    @Specialization(guards = "isNull(value)")
    static Object exec(Node node, NullObject value, Object defaultValue) {
      return defaultValue;
    }

    @Specialization(guards = {"!isError(value)", "!isNull(value)"})
    static Object exec(Node node, Object value, Object defaultValue) {
      return value;
    }
  }

  @NodeInfo(shortName = "TryableNodes.IsValue")
  @GenerateUncached
  @GenerateInline
  @ImportStatic({Tryable.class, Nullable.class})
  public abstract static class IsValueNode extends Node {

    public abstract boolean execute(Node node, Object value);

    @Specialization(guards = {"!isError(value)", "!isNull(value)"})
    static boolean exec(Node node, Object value) {
      return true;
    }

    @Specialization(guards = "isError(value)")
    static boolean exec(Node node, ErrorObject value) {
      return false;
    }

    @Specialization(guards = "isNull(value)")
    static boolean exec(Node node, NullObject value) {
      return false;
    }

  }
}
