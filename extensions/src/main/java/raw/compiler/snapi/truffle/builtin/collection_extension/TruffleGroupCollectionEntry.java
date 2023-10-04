package raw.compiler.snapi.truffle.builtin.collection_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.GroupCollectionEntry;
import raw.compiler.rql2.source.*;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionGroupByNodeGen;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TruffleGroupCollectionEntry extends GroupCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {

    Rql2IterableType iterable = (Rql2IterableType) type;
    Rql2RecordType record = (Rql2RecordType) iterable.innerType();
    Rql2AttrType[] atts =
        JavaConverters.asJavaCollection(record.atts()).toArray(Rql2AttrType[]::new);

    Rql2TypeWithProperties keyType =
        (Rql2TypeWithProperties)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("key"))
                .findFirst()
                .orElse(Rql2AttrType.apply("key", new Rql2UndefinedType(new HashSet<>())))
                .tipe();

    Rql2IterableType iterableValueType =
        (Rql2IterableType)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("group"))
                .findFirst()
                .orElse(Rql2AttrType.apply("key", new Rql2UndefinedType(new HashSet<>())))
                .tipe();

    Rql2TypeWithProperties valueType = (Rql2TypeWithProperties) iterableValueType.innerType();

    return CollectionGroupByNodeGen.create(
        args.get(0).getExprNode(), args.get(1).getExprNode(), keyType, valueType);
  }
}
