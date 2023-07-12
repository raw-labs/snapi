package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.Node;

abstract public class AbstractFunction {

    protected final Function function;
    protected final InteropLibrary interop;
    protected final Node node;
    
    public AbstractFunction(Function function, Node node) {
        this.function = function;
        this.node = node;
        this.interop = InteropLibrary.getFactory().create(function);
    }
}
