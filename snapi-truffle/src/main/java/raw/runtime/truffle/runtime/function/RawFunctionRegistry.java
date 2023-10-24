package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.interop.TruffleObject;

public class RawFunctionRegistry {

  private final FunctionRegistryObject registry;

  public RawFunctionRegistry() {
    registry = new FunctionRegistryObject();
  }

  public Closure get(String name) {
    return registry.get(name);
  }

  public void register(String name, Closure closure) {
    registry.put(name, closure);
  }

  public TruffleObject asPolyglot() {
    return registry;
  }

}
