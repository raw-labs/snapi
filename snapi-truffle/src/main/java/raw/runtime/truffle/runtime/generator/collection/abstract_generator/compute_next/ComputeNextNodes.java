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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import raw.runtime.truffle.ast.io.csv.reader.CsvParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvReaderRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlReaderRawTruffleException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.*;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.*;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.sources.EmptyCollection;
import raw.runtime.truffle.runtime.kryo.KryoNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.TryableNullable;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;

public class ComputeNextNodes {

  // ==================== ComputeNextNodes start ====================
  @NodeInfo(shortName = "Generator.ComputeNext")
  @GenerateUncached
  public abstract static class NextNode extends Node {

    public abstract Object execute(Object computeNext);

    @Specialization
    static Object next(ExpressionComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(
        CsvReadComputeNext computeNext,
        @Cached(value = "computeNext.getRowParserCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true) DirectCallNode rowParser) {
      if (computeNext.getParser().done()) {
        throw new BreakException();
      }
      try {
        return rowParser.call(computeNext.getParser());
      } catch (CsvParserRawTruffleException e) {
        // wrap any error with the stream location
        throw new CsvReaderRawTruffleException(e.getMessage(), null, computeNext.getStream());
      }
    }

    @Specialization
    static Object next(
        CsvReadFromStringComputeNext computeNext,
        @Cached(value = "computeNext.getRowParserCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true) DirectCallNode rowParser) {
      if (computeNext.getParser().done()) {
        throw new BreakException();
      }
      try {
        return rowParser.call(computeNext.getParser());
      } catch (CsvParserRawTruffleException e) {
        // wrap any error with the stream location
        throw new CsvReaderRawTruffleException(e.getMessage(), null, computeNext.getStream());
      }
    }

    @Specialization
    static Object next(IntRangeComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(
        JdbcQueryComputeNext computeNext,
        @Cached(value = "computeNext.getRowParserCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true) DirectCallNode rowParser) {
      boolean ok = computeNext.getRs().next();
      if (ok) {
        return rowParser.call(computeNext.getRs());
      } else {
        throw new BreakException();
      }
    }

    @Specialization
    static Object next(
        JsonReadComputeNext computeNext,
        @Cached JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached(value = "computeNext.getParseNextCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true)
            DirectCallNode parseNextCallNode) {
      try {
        JsonToken token = currentToken.execute(computeNext.getParser());
        if (token != JsonToken.END_ARRAY && token != null) {
          return parseNextCallNode.call(computeNext.getParser());
        } else {
          throw new BreakException();
        }
      } catch (JsonReaderRawTruffleException e) {
        throw new JsonReaderRawTruffleException(e.getMessage(), computeNext.getStream());
      }
    }

    @Specialization
    static Object next(LongRangeComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(ReadLinesComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(TimestampRangeComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(
        UnionComputeNext computeNext,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      Object currentGenerator;
      while (computeNext.getCurrentGenerator() == null) {
        if (computeNext.isTerminated()) {
          throw new BreakException();
        }
        Object iterable = computeNext.getIterable();
        computeNext.setCurrentGenerator(getGeneratorNode.execute(iterable));
        currentGenerator = computeNext.getCurrentGenerator();
        initNode.execute(currentGenerator);
        if (!hasNextNode.execute(currentGenerator)) {
          closeNode.execute(currentGenerator);
          computeNext.setCurrentGenerator(null);
        }
        computeNext.incrementIndex();
      }
      currentGenerator = computeNext.getCurrentGenerator();
      Object r = nextNode.execute(currentGenerator);
      if (!hasNextNode.execute(currentGenerator)) {
        closeNode.execute(currentGenerator);
        computeNext.setCurrentGenerator(null);
      }
      return r;
    }

    @Specialization
    static Object next(
        XmlParseComputeNext computeNext,
        @Cached(value = "computeNext.getParseNextRootCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true)
            DirectCallNode parseNextCallNode) {
      if (computeNext.getParser().onEndTag()) {
        throw new BreakException();
      } else {
        return parseNextCallNode.call(computeNext.getParser());
      }
    }

    @Specialization
    static Object next(
        XmlReadComputeNext computeNext,
        @Cached(value = "computeNext.getParseNextRootCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true)
            DirectCallNode parseNextCallNode) {
      if (computeNext.getParser().onEndTag()) {
        throw new BreakException();
      } else {
        try {
          return parseNextCallNode.call(computeNext.getParser());
        } catch (XmlParserRawTruffleException e) {
          throw new XmlReaderRawTruffleException(e, computeNext.getStream(), null);
        }
      }
    }

    @Specialization
    static Object next(EmptyComputeNext computeNext) {
      throw new BreakException();
    }

    @Specialization(limit = "3")
    static Object next(
        FilterComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @CachedLibrary("computeNext.getPredicate()") InteropLibrary interops) {
      while (hasNextNode.execute(computeNext.getParent())) {
        Object v = nextNode.execute(computeNext.getParent());
        Boolean isPredicateTrue = null;
        try {
          isPredicateTrue =
              TryableNullable.handlePredicate(
                  interops.execute(computeNext.getPredicate(), v), false);
        } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
          throw new RawTruffleRuntimeException("failed to execute function");
        }
        if (isPredicateTrue) {
          return v;
        }
      }
      throw new BreakException();
    }

    @Specialization
    static Object next(
        TakeComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode) {
      if (computeNext.getCurrentCount() < computeNext.getTakeCount()
          && hasNextNode.execute(computeNext.getParent())) {
        computeNext.incrementCurrentCount();
        return nextNode.execute(computeNext.getParent());
      }
      throw new BreakException();
    }

    @Specialization(limit = "3")
    static Object next(
        TransformComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @CachedLibrary("computeNext.getTransform()") InteropLibrary interops) {
      if (!hasNextNode.execute(computeNext.getParent())) {
        throw new BreakException();
      }
      try {
        return interops.execute(
            computeNext.getTransform(), nextNode.execute(computeNext.getParent()));
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }
    }

    @Specialization(limit = "3")
    static Object next(
        UnnestComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode,
        @CachedLibrary("computeNext.getTransform()") InteropLibrary interops) {
      Object next = null;

      while (next == null) {
        if (computeNext.getCurrentGenerator() == null) {
          if (!hasNextNode.execute(computeNext.getParent())) {
            throw new BreakException();
          }
          Object functionResult = null;
          try {
            functionResult =
                interops.execute(
                    computeNext.getTransform(), nextNode.execute(computeNext.getParent()));
          } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
            throw new RawTruffleRuntimeException("failed to execute function");
          }
          // the function result could be tryable/nullable. If error/null,
          // we replace it by an empty collection.
          Object iterable = TryableNullable.getOrElse(functionResult, EmptyCollection.INSTANCE);
          computeNext.setCurrentGenerator(getGeneratorNode.execute(iterable));
          initNode.execute(computeNext.getCurrentGenerator());
        }
        if (hasNextNode.execute(computeNext.getCurrentGenerator())) {
          next = nextNode.execute(computeNext.getCurrentGenerator());
        } else {
          closeNode.execute(computeNext.getCurrentGenerator());
          computeNext.setCurrentGenerator(null);
        }
      }
      return next;
    }

    @Specialization
    static Object next(
        ZipComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode1,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode2,
        @Cached GeneratorNodes.GeneratorNextNode nextNode1,
        @Cached GeneratorNodes.GeneratorNextNode nextNode2,
        @CachedLibrary(limit = "5") InteropLibrary records) {
      try {
        if (hasNextNode1.execute(computeNext.getParent1())
            && hasNextNode2.execute(computeNext.getParent2())) {
          RecordObject record = computeNext.getLanguage().createRecord();
          records.writeMember(record, "_1", nextNode1.execute(computeNext.getParent1()));
          records.writeMember(record, "_2", nextNode2.execute(computeNext.getParent2()));
          return record;
        }
        throw new BreakException();
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException e) {
        throw new RawTruffleInternalErrorException(e);
      }
    }

    @Specialization(limit = "3")
    static Object next(
        EquiJoinComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached OperatorNodes.CompareNode compareKey,
        @CachedLibrary("computeNext.getMkJoinedRecord()") InteropLibrary interops) {

      assert (computeNext.getLeftMapGenerator() != null);
      assert (computeNext.getRightMapGenerator() != null);

      // keep iterating until we find matching keys
      while (computeNext.getLeftKey() == null || computeNext.getRightKey() == null) {
        if (computeNext.getLeftKey() == null) {
          if (hasNextNode.execute(computeNext.getLeftMapGenerator())) {
            computeNext.setLeftEntry(
                (Object[]) nextNode.execute(computeNext.getLeftMapGenerator()));
            computeNext.setLeftKey(computeNext.getLeftEntry()[0]);
          } else {
            throw new BreakException();
          }
        }

        if (computeNext.getRightKey() == null) {
          if (hasNextNode.execute(computeNext.getRightMapGenerator())) {
            computeNext.setRightEntry(
                (Object[]) nextNode.execute(computeNext.getRightMapGenerator()));
            computeNext.setRightKey(computeNext.getRightEntry()[0]);
          } else {
            throw new BreakException();
          }
        }

        int compare = compareKey.execute(computeNext.getLeftKey(), computeNext.getRightKey());
        // if keys aren't equal, reset the smallest of both (it will be read in the next
        // iteration and
        // will be larger)
        if (compare < 0) {
          computeNext.setLeftKey(null);
        } else if (compare > 0) {
          computeNext.setRightKey(null);
        } else {
          // keys are equal, prepare to do the cartesian product between both.
          // leftRows and rightRows are the arrays of rows with the same key.
          // We'll iterate over them to produce the cartesian product.
          computeNext.setLeftRows((Object[]) computeNext.getLeftEntry()[1]);
          computeNext.setRightRows((Object[]) computeNext.getRightEntry()[1]);
          computeNext.setLeftIndex(0);
          computeNext.setRightIndex(0);
          break;
        }
      }

      // record to return
      Object joinedRow = null;
      try {
        joinedRow =
            interops.execute(
                computeNext.getMkJoinedRecord(),
                computeNext.getLeftRows()[computeNext.getLeftIndex()],
                computeNext.getRightRows()[computeNext.getRightIndex()]);
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      }

      // move to the next right row
      computeNext.setRightIndex(computeNext.getRightIndex() + 1);
      if (computeNext.getRightIndex() == computeNext.getRightRows().length) {
        // right side is exhausted, move to the next left row.
        computeNext.setLeftIndex(computeNext.getLeftIndex() + 1);
        if (computeNext.getLeftIndex() < computeNext.getLeftRows().length) {
          // there are more left rows, reset the right side to perform another loop.
          computeNext.setRightIndex(0);
        } else {
          // left side is exhausted, we're done with the cartesian product
          // reset left and right keys to get new ones and restart the cartesian production
          // in the next call.
          computeNext.setLeftKey(null);
          computeNext.setRightKey(null);
        }
      }
      return joinedRow;
    }

    @TruffleBoundary
    private static Input createInput(File file) {
      try {
        return new Input(new FileInputStream(file));
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }

    @Specialization
    static Object next(
        JoinComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached KryoNodes.KryoReadNode kryoReadNode,
        @CachedLibrary(limit = "5") InteropLibrary interop) {
      Object row = null;

      while (row == null) {
        if (computeNext.getLeftRow() == null || computeNext.getRightRow() == null) {
          if (computeNext.getLeftRow() == null) {
            if (hasNextNode.execute(computeNext.getLeftGen())) {
              computeNext.setLeftRow(nextNode.execute(computeNext.getLeftGen()));
            } else {
              // end of left, nothing else to read
              throw new BreakException();
            }
          }
          if (computeNext.getKryoRight() == null) {
            computeNext.setKryoRight(createInput(computeNext.getDiskRight()));
            computeNext.setReadRight(0);
          }
          if (computeNext.getRightRow() == null) {
            if (computeNext.getReadRight() < computeNext.getSpilledRight()) {
              computeNext.setRightRow(
                  kryoReadNode.execute(
                      computeNext.getLanguage(),
                      computeNext.getKryoRight(),
                      computeNext.getRightRowType()));

              try {
                boolean pass;
                if (computeNext.getReshapeBeforePredicate()) {
                  row =
                      interop.execute(
                          computeNext.getRemap(),
                          computeNext.getLeftRow(),
                          computeNext.getRightRow());
                  pass =
                      TryableNullable.handlePredicate(
                          interop.execute(computeNext.getPredicate(), row), false);
                  if (!pass) row = null;
                } else {
                  pass =
                      TryableNullable.handlePredicate(
                          interop.execute(
                              computeNext.getPredicate(),
                              computeNext.getLeftRow(),
                              computeNext.getRightRow()),
                          false);
                  if (pass)
                    row =
                        interop.execute(
                            computeNext.getRemap(),
                            computeNext.getLeftRow(),
                            computeNext.getRightRow());
                }
              } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
                throw new RawTruffleRuntimeException("failed to execute function");
              }

              computeNext.setReadRight(computeNext.getReadRight() + 1);
              computeNext.setRightRow(null);
            } else {
              // end of right, reset currentLeft to make sure we try another round
              computeNext.setLeftRow(null);
              computeNext.getKryoRight().close();
              computeNext.setRightRow(null);
              computeNext.setKryoRight(null);
            }
          }
        }
      }
      return row;
    }
  }

  // ==================== ComputeNextNodes end ====================

  // ==================== InitNodes start =======================

  @NodeInfo(shortName = "Generator.Init")
  @GenerateUncached
  public abstract static class InitNode extends Node {

    public abstract void execute(Object computeNext);

    @Specialization
    static void init(ExpressionComputeNext computeNext) {}

    @Specialization
    static void init(
        CsvReadComputeNext computeNext,
        @Cached CsvParserNodes.InitCsvParserNode initParser,
        @Cached CsvParserNodes.CloseCsvParserNode closeParser) {
      try {
        TruffleInputStream truffleInputStream =
            new TruffleInputStream(computeNext.getLocation(), computeNext.getContext());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(
            initParser.execute(computeNext.getStream(), computeNext.getSettings()));
      } catch (RawTruffleRuntimeException ex) {
        closeParser.execute(computeNext.getParser());
        throw ex;
      }
      computeNext.getParser().skipHeaderLines();
    }

    @Specialization
    static void init(
        CsvReadFromStringComputeNext computeNext,
        @Cached CsvParserNodes.InitCsvParserNode initParser,
        @Cached CsvParserNodes.CloseCsvParserNode closeParser) {
      try {
        computeNext.setParser(
            initParser.execute(computeNext.getStream(), computeNext.getSettings()));
      } catch (CsvReaderRawTruffleException ex) {
        CsvReaderRawTruffleException newEx =
            new CsvReaderRawTruffleException(ex.getMessage(), null, computeNext.getStream());
        closeParser.execute(computeNext.getParser());
        throw newEx;
      } catch (RawTruffleRuntimeException ex) {
        closeParser.execute(computeNext.getParser());
        throw ex;
      }
      computeNext.getParser().skipHeaderLines();
    }

    @Specialization
    static void init(IntRangeComputeNext computeNext) {}

    @Specialization
    static void init(JdbcQueryComputeNext computeNext) {
      computeNext.init();
    }

    @Specialization
    static void init(
        JsonReadComputeNext computeNext,
        @Cached JsonParserNodes.InitJsonParserNode initParser,
        @Cached JsonParserNodes.CloseJsonParserNode closeParser,
        @Cached JsonParserNodes.NextTokenJsonParserNode nextToken) {
      try {
        TruffleInputStream truffleInputStream =
            new TruffleInputStream(computeNext.getLocationObject(), computeNext.getContext());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(initParser.execute(computeNext.getStream()));
        // move from null to the first token
        nextToken.execute(computeNext.getParser());
        // the first token is START_ARRAY so skip it
        nextToken.execute(computeNext.getParser());
      } catch (JsonReaderRawTruffleException ex) {
        JsonReaderRawTruffleException newEx =
            new JsonReaderRawTruffleException(
                ex.getMessage(), computeNext.getParser(), computeNext.getStream());
        closeParser.execute(computeNext.getParser());
        throw newEx;
      } catch (RawTruffleRuntimeException ex) {
        closeParser.execute(computeNext.getParser());
        throw ex;
      }
    }

    @Specialization
    static void init(LongRangeComputeNext computeNext) {}

    @Specialization
    static void init(ReadLinesComputeNext computeNext) {
      computeNext.init();
    }

    @Specialization
    static void init(TimestampRangeComputeNext computeNext) {}

    @Specialization
    static void init(UnionComputeNext computeNext) {}

    @Specialization
    static void init(XmlParseComputeNext computeNext) {
      try {
        computeNext.setStream(new RawTruffleStringCharStream(computeNext.getText()));
        computeNext.setParser(
            RawTruffleXmlParser.create(computeNext.getStream(), computeNext.getSettings()));
        // move from null to the first token
        int token = computeNext.getParser().nextToken(); // consume START_OBJECT
        computeNext.getParser().assertCurrentTokenIsStartTag(); // because it's the top level object
      } catch (RawTruffleRuntimeException ex) {
        if (computeNext.getParser() != null) computeNext.getParser().close();
        throw ex;
      }
    }

    @Specialization
    static void init(XmlReadComputeNext computeNext) {
      try {
        TruffleInputStream truffleInputStream =
            new TruffleInputStream(computeNext.getLocationObject(), computeNext.getContext());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(
            RawTruffleXmlParser.create(computeNext.getStream(), computeNext.getSettings()));
        // move from null to the first token
        int token = computeNext.getParser().nextToken(); // consume START_OBJECT
        computeNext.getParser().assertCurrentTokenIsStartTag(); // because it's the top level object
      } catch (RawTruffleRuntimeException ex) {
        if (computeNext.getParser() != null) computeNext.getParser().close();
        throw ex;
      }
    }

    @Specialization
    static void init(EmptyComputeNext computeNext) {}

    @Specialization
    static void init(
        FilterComputeNext computeNext, @Cached GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        TakeComputeNext computeNext, @Cached GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        TransformComputeNext computeNext, @Cached GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        UnnestComputeNext computeNext, @Cached GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        ZipComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorInitNode initNode1,
        @Cached GeneratorNodes.GeneratorInitNode initNode2) {
      initNode1.execute(computeNext.getParent1());
      initNode2.execute(computeNext.getParent2());
    }

    @Specialization(limit = "3")
    static void init(
        EquiJoinComputeNext computeNext,
        @Cached IterableNodes.GetGeneratorNode getGeneratorLeftNode,
        @Cached IterableNodes.GetGeneratorNode getGeneratorRightNode,
        @Cached GeneratorNodes.GeneratorInitNode initLeftNode,
        @Cached GeneratorNodes.GeneratorInitNode initRightNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextLeftNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextRightNode,
        @Cached GeneratorNodes.GeneratorNextNode nextLeftNode,
        @Cached GeneratorNodes.GeneratorNextNode nextRightNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeLeftNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeRightNode,
        @Cached OffHeapNodes.OffHeapGroupByPutNode putLeftNode,
        @Cached OffHeapNodes.OffHeapGroupByPutNode putRightNode,
        @Cached OffHeapNodes.OffHeapGeneratorNode offHeapGeneratorLeft,
        @Cached OffHeapNodes.OffHeapGeneratorNode offHeapGeneratorRight,
        @CachedLibrary("computeNext.getLeftKeyF()") InteropLibrary leftKeyFLib,
        @CachedLibrary("computeNext.getRightKeyF()") InteropLibrary rightKeyFLib) {
      // left side (get a generator, then fill a map, set leftMapGenerator to the map generator)
      OffHeapGroupByKey leftMap =
          new OffHeapGroupByKey(
              computeNext.getKeyType(),
              computeNext.getLeftRowType(),
              computeNext.getLanguage(),
              computeNext.getContext(),
              null);
      Object leftGenerator = getGeneratorLeftNode.execute(computeNext.getLeftIterable());
      try {
        initLeftNode.execute(leftGenerator);
        while (hasNextLeftNode.execute(leftGenerator)) {
          Object leftItem = nextLeftNode.execute(leftGenerator);
          Object leftKey = leftKeyFLib.execute(computeNext.getLeftKeyF(), leftItem);
          putLeftNode.execute(leftMap, leftKey, leftItem);
        }
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      } finally {
        closeLeftNode.execute(leftGenerator);
      }
      computeNext.setLeftMapGenerator(offHeapGeneratorLeft.execute(leftMap));
      initRightNode.execute(computeNext.getLeftMapGenerator());

      // same with right side
      OffHeapGroupByKey rightMap =
          new OffHeapGroupByKey(
              computeNext.getKeyType(),
              computeNext.getRightRowType(),
              computeNext.getLanguage(),
              computeNext.getContext(),
              null);
      Object rightGenerator = getGeneratorRightNode.execute(computeNext.getRightIterable());
      try {
        initRightNode.execute(rightGenerator);
        while (hasNextRightNode.execute(rightGenerator)) {
          Object rightItem = nextRightNode.execute(rightGenerator);
          Object rightKey = rightKeyFLib.execute(computeNext.getRightKeyF(), rightItem);
          putRightNode.execute(rightMap, rightKey, rightItem);
        }
      } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
        throw new RawTruffleRuntimeException("failed to execute function");
      } finally {
        closeRightNode.execute(rightGenerator);
      }
      computeNext.setRightMapGenerator(offHeapGeneratorRight.execute(rightMap));
      initRightNode.execute(computeNext.getRightMapGenerator());
    }

    @Specialization
    static void init(
        JoinComputeNext computeNext,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached GeneratorNodes.GeneratorInitNode initNode,
        @Cached GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode nextNode,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached KryoNodes.KryoWriteNode kryoWrite) {
      // initialize left
      computeNext.setLeftGen(getGeneratorNode.execute(computeNext.getLeftIterable()));
      initNode.execute(computeNext.getLeftGen());

      // save right to disk
      Output buffer;
      try {
        buffer =
            new Output(
                new FileOutputStream(computeNext.getDiskRight()),
                computeNext.getKryoOutputBufferSize());
      } catch (FileNotFoundException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
      Object rightGen = getGeneratorNode.execute(computeNext.getRightIterable());
      try {
        initNode.execute(rightGen);
        while (hasNextNode.execute(rightGen)) {
          Object row = nextNode.execute(rightGen);
          kryoWrite.execute(buffer, computeNext.getRightRowType(), row);
          computeNext.setSpilledRight(computeNext.getSpilledRight() + 1);
        }
      } finally {
        closeNode.execute(rightGen);
        buffer.close();
      }
    }
  }

  // ==================== InitNodes end =======================

  // ==================== CloseNodes =======================

  @NodeInfo(shortName = "Generator.Close")
  @GenerateUncached
  public abstract static class CloseNode extends Node {

    public abstract void execute(Object computeNext);

    @Specialization
    static void close(ExpressionComputeNext computeNext) {}

    @Specialization
    static void close(
        CsvReadComputeNext computeNext, @Cached CsvParserNodes.CloseCsvParserNode closeParser) {
      closeParser.execute(computeNext.getParser());
    }

    @Specialization
    static void close(
        CsvReadFromStringComputeNext computeNext,
        @Cached CsvParserNodes.CloseCsvParserNode closeParser) {
      closeParser.execute(computeNext.getParser());
    }

    @Specialization
    static void close(IntRangeComputeNext computeNext) {}

    @Specialization
    static void close(JdbcQueryComputeNext computeNext) {
      if (computeNext.getRs() != null) {
        computeNext.close();
      }
    }

    @Specialization
    static void close(
        JsonReadComputeNext computeNext, @Cached JsonParserNodes.CloseJsonParserNode closeParser) {
      closeParser.execute(computeNext.getParser());
    }

    @Specialization
    static void close(LongRangeComputeNext computeNext) {}

    @Specialization
    static void close(ReadLinesComputeNext computeNext) {
      computeNext.close();
    }

    @Specialization
    static void close(TimestampRangeComputeNext computeNext) {}

    @Specialization
    static void close(
        UnionComputeNext computeNext, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      if (computeNext.getCurrentGenerator() != null) {
        closeNode.execute(computeNext.getCurrentGenerator());
      }
    }

    @Specialization
    static void close(XmlParseComputeNext computeNext) {
      if (computeNext.getParser() != null) computeNext.getParser().close();
    }

    @Specialization
    static void close(XmlReadComputeNext computeNext) {
      if (computeNext.getParser() != null) computeNext.getParser().close();
    }

    @Specialization
    static void close(EmptyComputeNext computeNext) {}

    @Specialization
    static void close(
        FilterComputeNext computeNext, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
    }

    @Specialization
    static void close(
        TakeComputeNext computeNext, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
    }

    @Specialization
    static void close(
        TransformComputeNext computeNext, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
    }

    @Specialization
    static void close(
        UnnestComputeNext computeNext, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
      if (computeNext.getCurrentGenerator() != null) {
        closeNode.execute(computeNext.getCurrentGenerator());
      }
    }

    @Specialization
    static void close(
        ZipComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode1,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode2) {
      closeNode1.execute(computeNext.getParent1());
      closeNode2.execute(computeNext.getParent2());
    }

    @Specialization
    static void close(
        EquiJoinComputeNext computeNext,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode1,
        @Cached GeneratorNodes.GeneratorCloseNode closeNode2) {
      if (computeNext.getLeftMapGenerator() != null) {
        closeNode1.execute(computeNext.getLeftMapGenerator());
        computeNext.setLeftMapGenerator(null);
      }
      if (computeNext.getRightMapGenerator() != null) {
        closeNode2.execute(computeNext.getRightMapGenerator());
        computeNext.setRightMapGenerator(null);
      }
    }

    @Specialization
    static void close(
        JoinComputeNext computeNext, @Cached GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getLeftGen());
      if (computeNext.getKryoRight() != null) computeNext.getKryoRight().close();
    }
  }
  // ==================== CloseNodes end =======================
}
