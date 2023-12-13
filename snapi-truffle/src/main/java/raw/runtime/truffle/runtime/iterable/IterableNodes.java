package raw.runtime.truffle.runtime.iterable;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.AbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.*;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.runtime.iterable.operations.*;
import raw.runtime.truffle.runtime.iterable.sources.*;

public class IterableNodes {
  @NodeInfo(shortName = "Iterable.GetGenerator")
  @GenerateUncached
  public abstract static class GetGeneratorNode extends Node {

    public abstract Object execute(Object generator);

    @Specialization
    static Object getGenerator(ExpressionCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(CsvCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(CsvFromStringCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(IntRangeCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(JdbcQueryCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(JsonReadCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(LongRangeCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(ReadLinesCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(TimestampRangeCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(UnionCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(XmlParseCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(XmlReadCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(EmptyCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(
        FilterCollection collection, @Cached IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(collection.getParentIterable());
      return new AbstractGenerator(
          new FilterComputeNext(parentGenerator, collection.getPredicate()));
    }

    @Specialization
    static Object getGenerator(
        TakeCollection collection, @Cached IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(collection.getParentIterable());
      return new AbstractGenerator(
          new TakeComputeNext(parentGenerator, collection.getCachedCount()));
    }

    @Specialization
    static Object getGenerator(
        TransformCollection collection, @Cached IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(collection.getParentIterable());
      return new AbstractGenerator(
          new TransformComputeNext(parentGenerator, collection.getTransform()));
    }

    @Specialization
    static Object getGenerator(
        UnnestCollection collection, @Cached IterableNodes.GetGeneratorNode getGeneratorNode) {
      Object parentGenerator = getGeneratorNode.execute(collection.getParentIterable());
      return new AbstractGenerator(
          new UnnestComputeNext(parentGenerator, collection.getTransform()));
    }

    @Specialization
    static Object getGenerator(
        ZipCollection collection,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode1,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode2) {
      Object parentGenerator1 = getGeneratorNode1.execute(collection.getParentIterable1());
      Object parentGenerator2 = getGeneratorNode1.execute(collection.getParentIterable2());
      return new AbstractGenerator(
          new ZipComputeNext(parentGenerator1, parentGenerator2, collection.getLanguage()));
    }

    @Specialization
    static Object getGenerator(
        DistinctCollection collection,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached OffHeapNodes.OffHeapGroupByPutNode putNode,
        @Cached OffHeapNodes.OffHeapGeneratorNode generatorNode) {
      OffHeapDistinct index =
          new OffHeapDistinct(
              collection.getRowType(), collection.getLanguage(), collection.getContext());
      Object generator = getGeneratorNode.execute(collection.getIterable());
      try {
        initNode.execute(generator);
        while (hasNextNode.execute(generator)) {
          Object next = hasNextNode.execute(generator);
          putNode.execute(index, next, null);
        }
      } finally {
        closeNode.execute(generator);
      }
      return generatorNode.execute(index);
    }

    @Specialization
    static Object getGenerator(EquiJoinCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(
        GroupByCollection collection,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached OffHeapNodes.OffHeapGroupByPutNode putNode,
        @Cached OffHeapNodes.OffHeapGeneratorNode generatorNode,
        @CachedLibrary("collection.getKeyFun()") InteropLibrary keyFunLib) {
      OffHeapGroupByKey map =
          new OffHeapGroupByKey(
              collection.getKeyType(),
              collection.getRowType(),
              collection.getLanguage(),
              collection.getContext(),
              new RecordShaper(collection.getLanguage(), false));
      Object inputGenerator = getGeneratorNode.execute(collection.getIterable());
      try {
        initNode.execute(inputGenerator);
        while (hasNextNode.execute(inputGenerator)) {
          Object v = nextNode.execute(inputGenerator);
          Object key = keyFunLib.execute(collection.getKeyFun(), v);
          putNode.execute(map, key, v);
        }
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      } finally {
        closeNode.execute(inputGenerator);
      }
      return generatorNode.execute(map);
    }

    @Specialization
    static Object getGenerator(JoinCollection collection) {
      return collection.getGenerator();
    }

    @Specialization
    static Object getGenerator(
        OrderByCollection collection,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached OffHeapNodes.OffHeapGroupByPutNode putNode,
        @Cached OffHeapNodes.OffHeapGeneratorNode generatorNode,
        @CachedLibrary(limit = "8") InteropLibrary keyFunctionsLib) {
      Object generator = getGeneratorNode.execute(collection.getParentIterable());
      OffHeapGroupByKeys groupByKeys =
          new OffHeapGroupByKeys(
              collection.getKeyTypes(),
              collection.getRowType(),
              collection.getLanguage(),
              collection.getContext());
      try {
        initNode.execute(generator);
        while (hasNextNode.execute(generator)) {
          Object v = nextNode.execute(generator);
          int len = collection.getKeyFunctions().length;
          Object[] key = new Object[len];
          for (int i = 0; i < len; i++) {
            key[i] = keyFunctionsLib.execute(collection.getKeyFunctions()[i], v);
          }
          putNode.execute(groupByKeys, key, v);
        }
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RuntimeException(e);
      } finally {
        closeNode.execute(generator);
      }
      return generatorNode.execute(groupByKeys);
    }
  }
}
