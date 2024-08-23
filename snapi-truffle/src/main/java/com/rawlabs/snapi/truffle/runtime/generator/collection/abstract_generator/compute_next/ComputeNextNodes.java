/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.Rql2Context;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.CsvParserNodes;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonParserNodes;
import com.rawlabs.snapi.truffle.ast.io.xml.parser.TruffleXmlParser;
import com.rawlabs.snapi.truffle.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.ast.osr.bodies.*;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRCollectionFilterConditionNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRFromBodyConditionNode;
import com.rawlabs.snapi.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.BreakException;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvReaderTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonReaderTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.xml.XmlParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.xml.XmlReaderTruffleException;
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.StaticInitializers;
import com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.*;
import com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.*;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.sources.EmptyCollection;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.runtime.utils.IOUtils;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharInputStream;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleInputStream;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleStringCharStream;
import com.rawlabs.utils.core.RawSettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ComputeNextNodes {

  // ==================== ComputeNextNodes start ====================
  @NodeInfo(shortName = "Generator.ComputeNext")
  @GenerateUncached
  @GenerateInline
  public abstract static class NextNode extends Node {

    public abstract Object execute(Node node, Object computeNext);

    @Specialization
    static Object next(Node node, ExpressionComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(
        Node node,
        CsvReadComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(value = "computeNext.getRowParserCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true, inline = false)
            DirectCallNode rowParser) {
      if (computeNext.getParser().done()) {
        throw new BreakException();
      }
      try {
        return rowParser.call(computeNext.getParser());
      } catch (CsvParserTruffleException e) {
        // wrap any error with the stream location
        throw new CsvReaderTruffleException(e.getMessage(), computeNext.getStream(), e, thisNode);
      }
    }

    @Specialization
    static Object next(
        Node node,
        CsvReadFromStringComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(value = "computeNext.getRowParserCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true, inline = false)
            DirectCallNode rowParser) {
      if (computeNext.getParser().done()) {
        throw new BreakException();
      }
      try {
        return rowParser.call(computeNext.getParser());
      } catch (CsvParserTruffleException e) {
        // wrap any error with the stream location
        throw new CsvReaderTruffleException(e.getMessage(), computeNext.getStream(), e, thisNode);
      }
    }

    @Specialization
    static Object next(Node node, IntRangeComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(
        Node node,
        JdbcQueryComputeNext computeNext,
        @Cached(value = "computeNext.getRowParserCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true, inline = false)
            DirectCallNode rowParser) {
      boolean ok = computeNext.getRs().next();
      if (ok) {
        return rowParser.call(computeNext.getRs());
      } else {
        throw new BreakException();
      }
    }

    @Specialization
    static Object next(
        Node node,
        JsonReadComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached(value = "computeNext.getParseNextCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true, inline = false)
            DirectCallNode parseNextCallNode) {
      try {
        JsonToken token = currentToken.execute(thisNode, computeNext.getParser());
        if (token != JsonToken.END_ARRAY && token != null) {
          return parseNextCallNode.call(computeNext.getParser());
        } else {
          throw new BreakException();
        }
      } catch (JsonReaderTruffleException e) {
        throw new JsonReaderTruffleException(
            computeNext.getParser(), computeNext.getStream(), e, thisNode);
      }
    }

    @Specialization
    static Object next(Node node, LongRangeComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(Node node, ReadLinesComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(Node node, TimestampRangeComputeNext computeNext) {
      return computeNext.next();
    }

    @Specialization
    static Object next(
        Node node,
        UnionComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Exclusive GeneratorNodes.GeneratorNextNode nextNode,
        @Cached @Cached.Exclusive GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode) {
      Object currentGenerator;
      while (computeNext.getCurrentGenerator() == null) {
        if (computeNext.isTerminated()) {
          throw new BreakException();
        }
        Object iterable = computeNext.getIterable();
        computeNext.setCurrentGenerator(getGeneratorNode.execute(thisNode, iterable));
        currentGenerator = computeNext.getCurrentGenerator();
        initNode.execute(thisNode, currentGenerator);
        if (!hasNextNode.execute(thisNode, currentGenerator)) {
          closeNode.execute(thisNode, currentGenerator);
          computeNext.setCurrentGenerator(null);
        }
        computeNext.incrementIndex();
      }
      currentGenerator = computeNext.getCurrentGenerator();
      Object r = nextNode.execute(thisNode, currentGenerator);
      if (!hasNextNode.execute(thisNode, currentGenerator)) {
        closeNode.execute(thisNode, currentGenerator);
        computeNext.setCurrentGenerator(null);
      }
      return r;
    }

    @Specialization
    static Object next(
        Node node,
        XmlParseComputeNext computeNext,
        @Cached(value = "computeNext.getParseNextRootCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true, inline = false)
            DirectCallNode parseNextCallNode) {
      if (computeNext.getParser().onEndTag()) {
        throw new BreakException();
      } else {
        return parseNextCallNode.call(computeNext.getParser());
      }
    }

    @Specialization
    static Object next(
        Node node,
        XmlReadComputeNext computeNext,
        @Cached(value = "computeNext.getParseNextRootCallTarget()", allowUncached = true)
            RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)", allowUncached = true, inline = false)
            DirectCallNode parseNextCallNode) {
      if (computeNext.getParser().onEndTag()) {
        throw new BreakException();
      } else {
        try {
          return parseNextCallNode.call(computeNext.getParser());
        } catch (XmlParserTruffleException e) {
          throw new XmlReaderTruffleException(e, computeNext.getStream(), null);
        }
      }
    }

    @Specialization
    static Object next(Node node, EmptyComputeNext computeNext) {
      throw new BreakException();
    }

    public static LoopNode getFilterLoopNode(FilterComputeNext computeNext) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRCollectionFilterConditionNode(
                      computeNext.getGeneratorSlot(), computeNext.getResultSlot()),
                  new OSRCollectionFilterBodyNode(
                      computeNext.getGeneratorSlot(),
                      computeNext.getFunctionSlot(),
                      computeNext.getResultSlot())));
    }

    @Specialization(guards = "cachedComputeNext.hasSameSlots(computeNext)", limit = "8", unroll = 8)
    static Object next(
        Node node,
        FilterComputeNext computeNext,
        @Cached("computeNext") FilterComputeNext cachedComputeNext,
        @Cached(
                value = "getFilterLoopNode(cachedComputeNext)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode) {
      Frame frame = computeNext.getFrame();
      frame.setObject(computeNext.getResultSlot(), null);
      loopNode.execute(computeNext.getFrame());
      Object result = frame.getObject(computeNext.getResultSlot());
      if (result == null) {
        throw new BreakException();
      }
      return result;
    }

    @Specialization
    static Object next(
        Node node,
        TakeComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached(inline = false) @Cached.Exclusive GeneratorNodes.GeneratorNextNode nextNode) {
      if (computeNext.getCurrentCount() < computeNext.getTakeCount()
          && hasNextNode.execute(thisNode, computeNext.getParent())) {
        computeNext.incrementCurrentCount();
        return nextNode.execute(thisNode, computeNext.getParent());
      }
      throw new BreakException();
    }

    @Specialization
    static Object next(
        Node node,
        TransformComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached(inline = false) @Cached.Shared("next1") GeneratorNodes.GeneratorNextNode nextNode,
        @Cached @Cached.Shared("executeOne")
            FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode) {
      if (!hasNextNode.execute(thisNode, computeNext.getParent())) {
        throw new BreakException();
      }
      return functionExecuteOneNode.execute(
          thisNode,
          computeNext.getTransform(),
          nextNode.execute(thisNode, computeNext.getParent()));
    }

    @Specialization
    static Object next(
        Node node,
        UnnestComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("next1") GeneratorNodes.GeneratorNextNode nextNode,
        @Cached @Cached.Exclusive GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("executeOne")
            FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode,
        @Cached TryableNullableNodes.GetOrElseNode getOrElseNode) {
      Object next = null;

      while (next == null) {
        if (computeNext.getCurrentGenerator() == null) {
          if (!hasNextNode.execute(thisNode, computeNext.getParent())) {
            throw new BreakException();
          }
          Object functionResult = null;
          functionResult =
              functionExecuteOneNode.execute(
                  thisNode,
                  computeNext.getTransform(),
                  nextNode.execute(thisNode, computeNext.getParent()));
          // the function result could be tryable/nullable. If error/null,
          // we replace it by an empty collection.
          Object iterable =
              getOrElseNode.execute(thisNode, functionResult, EmptyCollection.INSTANCE);
          computeNext.setCurrentGenerator(getGeneratorNode.execute(thisNode, iterable));
          initNode.execute(thisNode, computeNext.getCurrentGenerator());
        }
        if (hasNextNode.execute(thisNode, computeNext.getCurrentGenerator())) {
          next = nextNode.execute(thisNode, computeNext.getCurrentGenerator());
        } else {
          closeNode.execute(thisNode, computeNext.getCurrentGenerator());
          computeNext.setCurrentGenerator(null);
        }
      }
      return next;
    }

    public static Rql2Language getRql2Language(Node node) {
      return Rql2Language.get(node);
    }

    @Specialization
    static Object next(
        Node node,
        ZipComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(value = "getRql2Language(thisNode)", allowUncached = true) Rql2Language language,
        @Cached @Cached.Exclusive GeneratorNodes.GeneratorHasNextNode hasNextNode1,
        @Cached @Cached.Exclusive GeneratorNodes.GeneratorHasNextNode hasNextNode2,
        @Cached(inline = false) @Cached.Exclusive GeneratorNodes.GeneratorNextNode nextNode1,
        @Cached(inline = false) @Cached.Exclusive GeneratorNodes.GeneratorNextNode nextNode2,
        @Cached RecordNodes.AddPropNode addPropNode1,
        @Cached RecordNodes.AddPropNode addPropNode2) {
      if (hasNextNode1.execute(thisNode, computeNext.getParent1())
          && hasNextNode2.execute(thisNode, computeNext.getParent2())) {
        Object record = language.createPureRecord();
        addPropNode1.execute(
            thisNode, record, "_1", nextNode1.execute(thisNode, computeNext.getParent1()), false);

        addPropNode2.execute(
            thisNode, record, "_2", nextNode2.execute(thisNode, computeNext.getParent2()), false);
        return record;
      }
      throw new BreakException();
    }

    public static LoopNode getEquiJoinNextLoopNode(EquiJoinComputeNext computeNext) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRFromBodyConditionNode(computeNext.getShouldContinueSlot()),
                  new OSREquiJoinNextBodyNode(
                      computeNext.getComputeNextSlot(), computeNext.getShouldContinueSlot())));
    }

    @Specialization(guards = "cachedComputeNext.hasSameSlots(computeNext)", limit = "8", unroll = 8)
    static Object next(
        Node node,
        EquiJoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached("computeNext") EquiJoinComputeNext cachedComputeNext,
        @Cached(
                value = "getEquiJoinNextLoopNode(cachedComputeNext)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode,
        @Cached FunctionExecuteNodes.FunctionExecuteTwo functionExecuteTwoNode) {

      assert (computeNext.getLeftMapGenerator() != null);
      assert (computeNext.getRightMapGenerator() != null);

      Frame frame = computeNext.getFrame();

      frame.setObject(computeNext.getComputeNextSlot(), computeNext);
      frame.setBoolean(computeNext.getShouldContinueSlot(), true);

      loopNode.execute(computeNext.getFrame());

      // record to return
      Object joinedRow = null;

      joinedRow =
          functionExecuteTwoNode.execute(
              thisNode,
              computeNext.getMkJoinedRecord(),
              computeNext.getLeftRows()[computeNext.getLeftIndex()],
              computeNext.getRightRows()[computeNext.getRightIndex()]);

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
    private static Input createInput(File file, Node node) {
      try {
        return new Input(new FileInputStream(file));
      } catch (FileNotFoundException e) {
        throw new TruffleRuntimeException(e.getMessage(), e, node);
      }
    }

    public static LoopNode getJoinNextLoopNode(JoinComputeNext computeNext) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRFromBodyConditionNode(computeNext.getShouldContinueSlot()),
                  new OSRJoinNextBodyNode(
                      computeNext.getComputeNextSlot(),
                      computeNext.getShouldContinueSlot(),
                      computeNext.getResultSlot())));
    }

    @Specialization(guards = "cachedComputeNext.hasSameSlots(computeNext)", limit = "8", unroll = 8)
    static Object next(
        Node node,
        JoinComputeNext computeNext,
        @Cached("computeNext") JoinComputeNext cachedComputeNext,
        @Cached(
                value = "getJoinNextLoopNode(cachedComputeNext)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode) {
      computeNext.getFrame().setObject(computeNext.getComputeNextSlot(), computeNext);
      computeNext.getFrame().setBoolean(computeNext.getShouldContinueSlot(), true);
      loopNode.execute(computeNext.getFrame());
      return computeNext.getFrame().getObject(computeNext.getResultSlot());
    }
  }

  // ==================== ComputeNextNodes end ====================

  // ==================== InitNodes start =======================

  @NodeInfo(shortName = "Generator.Init")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(StaticInitializers.class)
  public abstract static class InitNode extends Node {

    public abstract void execute(Node node, Object computeNext);

    @Specialization
    static void init(Node node, ExpressionComputeNext computeNext) {}

    @Specialization
    static void init(
        Node node,
        CsvReadComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("initCsv") CsvParserNodes.InitCsvParserNode initParser,
        @Cached @Cached.Shared("closeCsv") CsvParserNodes.CloseCsvParserNode closeParser) {
      try {
        TruffleInputStream truffleInputStream = new TruffleInputStream(computeNext.getLocation());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(
            initParser.execute(thisNode, computeNext.getStream(), computeNext.getSettings()));
      } catch (TruffleRuntimeException ex) {
        closeParser.execute(thisNode, computeNext.getParser());
        throw ex;
      }
      computeNext.getParser().skipHeaderLines();
    }

    @Specialization
    static void init(
        Node node,
        CsvReadFromStringComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("initCsv") CsvParserNodes.InitCsvParserNode initParser,
        @Cached @Cached.Shared("closeCsv") CsvParserNodes.CloseCsvParserNode closeParser) {
      try {
        computeNext.setParser(
            initParser.execute(thisNode, computeNext.getStream(), computeNext.getSettings()));
      } catch (CsvReaderTruffleException ex) {
        CsvReaderTruffleException newEx =
            new CsvReaderTruffleException(
                ex.getMessage(), computeNext.getStream(), ex.getCause(), thisNode);
        closeParser.execute(thisNode, computeNext.getParser());
        throw newEx;
      } catch (TruffleRuntimeException ex) {
        closeParser.execute(thisNode, computeNext.getParser());
        throw ex;
      }
      computeNext.getParser().skipHeaderLines();
    }

    @Specialization
    static void init(Node node, IntRangeComputeNext computeNext) {}

    @Specialization
    static void init(Node node, JdbcQueryComputeNext computeNext) {
      computeNext.init();
    }

    @Specialization
    static void init(
        Node node,
        JsonReadComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.InitJsonParserNode initParser,
        @Cached JsonParserNodes.CloseJsonParserNode closeParser,
        @Cached JsonParserNodes.NextTokenJsonParserNode nextToken) {
      try {
        TruffleInputStream truffleInputStream =
            new TruffleInputStream(computeNext.getLocationObject());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(initParser.execute(thisNode, computeNext.getStream()));
        // move from null to the first token
        nextToken.execute(thisNode, computeNext.getParser());
        // the first token is START_ARRAY so skip it
        nextToken.execute(thisNode, computeNext.getParser());
      } catch (JsonReaderTruffleException ex) {
        JsonReaderTruffleException newEx =
            new JsonReaderTruffleException(
                ex.getMessage(), computeNext.getParser(), computeNext.getStream(), ex, thisNode);
        closeParser.execute(thisNode, computeNext.getParser());
        throw newEx;
      } catch (TruffleRuntimeException ex) {
        closeParser.execute(thisNode, computeNext.getParser());
        throw ex;
      }
    }

    @Specialization
    static void init(Node node, LongRangeComputeNext computeNext) {}

    @Specialization
    static void init(Node node, ReadLinesComputeNext computeNext) {
      computeNext.init();
    }

    @Specialization
    static void init(Node node, TimestampRangeComputeNext computeNext) {}

    @Specialization
    static void init(Node node, UnionComputeNext computeNext) {}

    @Specialization
    static void init(Node node, XmlParseComputeNext computeNext) {
      try {
        computeNext.setStream(new TruffleStringCharStream(computeNext.getText()));
        computeNext.setParser(
            TruffleXmlParser.create(computeNext.getStream(), computeNext.getSettings()));
        // move from null to the first token
        int token = computeNext.getParser().nextToken(); // consume START_OBJECT
        computeNext.getParser().assertCurrentTokenIsStartTag(); // because it's the top level object
      } catch (TruffleRuntimeException ex) {
        if (computeNext.getParser() != null) computeNext.getParser().close();
        throw ex;
      }
    }

    @Specialization
    static void init(Node node, XmlReadComputeNext computeNext) {
      try {
        TruffleInputStream truffleInputStream =
            new TruffleInputStream(computeNext.getLocationObject());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(
            TruffleXmlParser.create(computeNext.getStream(), computeNext.getSettings()));
        // move from null to the first token
        int token = computeNext.getParser().nextToken(); // consume START_OBJECT
        computeNext.getParser().assertCurrentTokenIsStartTag(); // because it's the top level object
      } catch (TruffleRuntimeException ex) {
        if (computeNext.getParser() != null) computeNext.getParser().close();
        throw ex;
      }
    }

    @Specialization
    static void init(Node node, EmptyComputeNext computeNext) {}

    @Specialization
    static void init(
        Node node,
        FilterComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(thisNode, computeNext.getParent());
      Frame frame = computeNext.getFrame();
      frame.setObject(computeNext.getGeneratorSlot(), computeNext.getParent());
      frame.setObject(computeNext.getFunctionSlot(), computeNext.getPredicate());
    }

    @Specialization
    static void init(
        Node node,
        TakeComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(thisNode, computeNext.getParent());
    }

    @Specialization
    static void init(
        Node node,
        TransformComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(thisNode, computeNext.getParent());
    }

    @Specialization
    static void init(
        UnnestComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(thisNode, computeNext.getParent());
    }

    @Specialization
    static void init(
        Node node,
        ZipComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("init1") GeneratorNodes.GeneratorInitNode initNode1,
        @Cached(inline = false) @Cached.Shared("init2")
            GeneratorNodes.GeneratorInitNode initNode2) {
      initNode1.execute(thisNode, computeNext.getParent1());
      initNode2.execute(thisNode, computeNext.getParent2());
    }

    public static LoopNode getEquiJoinInitLoopNode(EquiJoinComputeNext computeNext) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionNode(computeNext.getGeneratorSlot()),
                  new OSRCollectionEquiJoinInitBodyNode(
                      computeNext.getGeneratorSlot(),
                      computeNext.getKeyFunctionSlot(),
                      computeNext.getMapSlot())));
    }

    @Specialization(guards = "cachedComputeNext.hasSameSlots(computeNext)", limit = "8", unroll = 8)
    static void init(
        Node node,
        EquiJoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached("computeNext") EquiJoinComputeNext cachedComputeNext,
        @Cached(
                value = "getEquiJoinInitLoopNode(cachedComputeNext)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode1,
        @Cached(
                value = "getEquiJoinInitLoopNode(cachedComputeNext)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode2,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGenerator,
        @Cached(inline = false) @Cached.Shared("init1")
            GeneratorNodes.GeneratorInitNode initLeftNode,
        @Cached(inline = false) @Cached.Shared("init2")
            GeneratorNodes.GeneratorInitNode initRightNode,
        @Cached @Cached.Shared("close1") GeneratorNodes.GeneratorCloseNode closeLeftNode,
        @SuppressWarnings("truffle-sharing") @Cached
            GeneratorNodes.GeneratorCloseNode closeRightNode,
        @Cached OffHeapNodes.OffHeapGeneratorNode offHeapGeneratorLeft,
        @Cached OffHeapNodes.OffHeapGeneratorNode offHeapGeneratorRight,
        @Cached(value = "getContextValues(thisNode)", dimensions = 1, allowUncached = true)
            long[] contextValues) {
      Frame frame = computeNext.getFrame();
      // left side (get a generator, then fill a map, set leftMapGenerator to the map generator)
      OffHeapGroupByKey leftMap =
          new OffHeapGroupByKey(
              computeNext.getKeyType(),
              computeNext.getLeftRowType(),
              null,
              contextValues[0],
              (int) contextValues[1],
              (int) contextValues[2]);

      Object leftGenerator = getGenerator.execute(thisNode, computeNext.getLeftIterable());
      try {
        initLeftNode.execute(thisNode, leftGenerator);
        frame.setObject(computeNext.getMapSlot(), leftMap);
        frame.setObject(computeNext.getGeneratorSlot(), leftGenerator);
        frame.setObject(computeNext.getKeyFunctionSlot(), computeNext.getLeftKeyF());
        loopNode1.execute(computeNext.getFrame());
      } finally {
        closeLeftNode.execute(thisNode, leftGenerator);
      }
      computeNext.setLeftMapGenerator(offHeapGeneratorLeft.execute(thisNode, leftMap));
      initLeftNode.execute(thisNode, computeNext.getLeftMapGenerator());

      // same with right side
      OffHeapGroupByKey rightMap =
          new OffHeapGroupByKey(
              computeNext.getKeyType(),
              computeNext.getRightRowType(),
              null,
              contextValues[0],
              (int) contextValues[1],
              (int) contextValues[2]);
      Object rightGenerator = getGenerator.execute(thisNode, computeNext.getRightIterable());
      try {
        initRightNode.execute(thisNode, rightGenerator);
        frame.setObject(computeNext.getMapSlot(), rightMap);
        frame.setObject(computeNext.getGeneratorSlot(), rightGenerator);
        frame.setObject(computeNext.getKeyFunctionSlot(), computeNext.getRightKeyF());
        loopNode2.execute(computeNext.getFrame());
      } finally {
        closeRightNode.execute(thisNode, rightGenerator);
      }
      computeNext.setRightMapGenerator(offHeapGeneratorRight.execute(thisNode, rightMap));
      initRightNode.execute(thisNode, computeNext.getRightMapGenerator());
    }

    @TruffleBoundary
    private static Output createOutput(JoinComputeNext computeNext, Node node) {
      try {
        return new Output(
            new FileOutputStream(computeNext.getDiskRight()),
            computeNext.getKryoOutputBufferSize());
      } catch (FileNotFoundException e) {
        throw new TruffleRuntimeException(e.getMessage(), e, node);
      }
    }

    public static LoopNode getJoinInitLoopNode(JoinComputeNext computeNext) {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionNode(computeNext.getGeneratorSlot()),
                  new OSRCollectionJoinInitBodyNode(
                      computeNext.getGeneratorSlot(),
                      computeNext.getComputeNextSlot(),
                      computeNext.getOutputBufferSlot())));
    }

    @Specialization(guards = "cachedComputeNext.hasSameSlots(computeNext)", limit = "8", unroll = 8)
    static void init(
        Node node,
        JoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached("computeNext") JoinComputeNext cachedComputeNext,
        @Cached(
                value = "getJoinInitLoopNode(cachedComputeNext)",
                inline = false,
                allowUncached = true)
            LoopNode loopNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close1") GeneratorNodes.GeneratorCloseNode closeNode) {

      RawSettings settings = Rql2Context.get(thisNode).getSettings();
      computeNext.setDiskRight(IOUtils.getScratchFile("cartesian.", ".kryo", settings).toFile());

      // save right to disk
      Object rightGen = getGeneratorNode.execute(thisNode, computeNext.getRightIterable());
      try (Output buffer = createOutput(computeNext, thisNode)) {
        initNode.execute(thisNode, rightGen);
        Frame frame = computeNext.getFrame();
        frame.setObject(computeNext.getGeneratorSlot(), rightGen);
        frame.setObject(computeNext.getOutputBufferSlot(), buffer);
        frame.setObject(computeNext.getComputeNextSlot(), computeNext);
        loopNode.execute(computeNext.getFrame());
      } finally {
        closeNode.execute(thisNode, rightGen);
      }
      // initialize left
      computeNext.setLeftGen(getGeneratorNode.execute(thisNode, computeNext.getLeftIterable()));
      initNode.execute(thisNode, computeNext.getLeftGen());
    }
  }

  // ==================== InitNodes end =======================

  // ==================== CloseNodes =======================

  @NodeInfo(shortName = "Generator.Close")
  @GenerateUncached
  @GenerateInline
  public abstract static class CloseNode extends Node {

    public abstract void execute(Node node, Object computeNext);

    @Specialization
    static void close(Node node, ExpressionComputeNext computeNext) {}

    @Specialization
    static void close(
        Node node,
        CsvReadComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("closeCsv") CsvParserNodes.CloseCsvParserNode closeParser) {
      closeParser.execute(thisNode, computeNext.getParser());
    }

    @Specialization
    static void close(
        Node node,
        CsvReadFromStringComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("closeCsv") CsvParserNodes.CloseCsvParserNode closeParser) {
      closeParser.execute(thisNode, computeNext.getParser());
    }

    @Specialization
    static void close(Node node, IntRangeComputeNext computeNext) {}

    @Specialization
    static void close(Node node, JdbcQueryComputeNext computeNext) {
      if (computeNext.getRs() != null) {
        computeNext.close();
      }
    }

    @Specialization
    static void close(
        Node node,
        JsonReadComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.CloseJsonParserNode closeParser) {
      closeParser.execute(thisNode, computeNext.getParser());
    }

    @Specialization
    static void close(Node node, LongRangeComputeNext computeNext) {}

    @Specialization
    static void close(Node node, ReadLinesComputeNext computeNext) {
      computeNext.close();
    }

    @Specialization
    static void close(Node node, TimestampRangeComputeNext computeNext) {}

    @Specialization
    static void close(
        Node node,
        UnionComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close")
            GeneratorNodes.GeneratorCloseNode closeNode) {
      if (computeNext.getCurrentGenerator() != null) {
        closeNode.execute(thisNode, computeNext.getCurrentGenerator());
      }
    }

    @Specialization
    static void close(Node node, XmlParseComputeNext computeNext) {
      if (computeNext.getParser() != null) computeNext.getParser().close();
    }

    @Specialization
    static void close(Node node, XmlReadComputeNext computeNext) {
      if (computeNext.getParser() != null) computeNext.getParser().close();
    }

    @Specialization
    static void close(Node node, EmptyComputeNext computeNext) {}

    @Specialization
    static void close(
        Node node,
        FilterComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close")
            GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(thisNode, computeNext.getParent());
    }

    @Specialization
    static void close(
        Node node,
        TakeComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close")
            GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(thisNode, computeNext.getParent());
    }

    @Specialization
    static void close(
        Node node,
        TransformComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close")
            GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(thisNode, computeNext.getParent());
    }

    @Specialization
    static void close(
        Node node,
        UnnestComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close")
            GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(thisNode, computeNext.getParent());
      if (computeNext.getCurrentGenerator() != null) {
        closeNode.execute(thisNode, computeNext.getCurrentGenerator());
      }
    }

    @Specialization
    static void close(
        Node node,
        ZipComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close1")
            GeneratorNodes.GeneratorCloseNode closeNode1,
        @Cached(inline = false) @Cached.Shared("close2")
            GeneratorNodes.GeneratorCloseNode closeNode2) {
      closeNode1.execute(thisNode, computeNext.getParent1());
      closeNode2.execute(thisNode, computeNext.getParent2());
    }

    @Specialization
    static void close(
        Node node,
        EquiJoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close1")
            GeneratorNodes.GeneratorCloseNode closeNode1,
        @Cached(inline = false) @Cached.Shared("close2")
            GeneratorNodes.GeneratorCloseNode closeNode2) {
      if (computeNext.getLeftMapGenerator() != null) {
        closeNode1.execute(thisNode, computeNext.getLeftMapGenerator());
        computeNext.setLeftMapGenerator(null);
      }
      if (computeNext.getRightMapGenerator() != null) {
        closeNode2.execute(thisNode, computeNext.getRightMapGenerator());
        computeNext.setRightMapGenerator(null);
      }
    }

    @Specialization
    static void close(
        Node node,
        JoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("close")
            GeneratorNodes.GeneratorCloseNode closeNode) {
      closeNode.execute(thisNode, computeNext.getLeftGen());
      if (computeNext.getKryoRight() != null) computeNext.getKryoRight().close();
    }
  }
  // ==================== CloseNodes end =======================
}
