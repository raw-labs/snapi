package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

public class FilterComputeNext {
    private final Object parent;
    private final Object predicate;

    public FilterComputeNext(Object parent, Object predicate) {
        this.parent = parent;
        this.predicate = predicate;
    }

    public Object getParent() {
        return parent;
    }

    public Object getPredicate() {
        return predicate;
    }
}
