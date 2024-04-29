package raw.compiler.snapi.truffle.builtin.jdbc;

import raw.compiler.rql2.source.Rql2AttrType;

class AttrWithIndex {
    public final int index;
    public final Rql2AttrType attr;

    public AttrWithIndex(int index, Rql2AttrType attr) {
        this.index = index;
        this.attr = attr;
    }
}