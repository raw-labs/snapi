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
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import raw.runtime.truffle.ast.io.csv.reader.CsvParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.xml.parser.RawTruffleXmlParser;
import raw.runtime.truffle.ast.osr.AuxiliarySlots;
import raw.runtime.truffle.ast.osr.OSRGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.*;
import raw.runtime.truffle.ast.osr.conditions.OSRCollectionFilterConditionNode;
import raw.runtime.truffle.ast.osr.conditions.OSRFromBodyConditionNode;
import raw.runtime.truffle.ast.osr.conditions.OSRHasNextConditionAuxNode;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvReaderRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlReaderRawTruffleException;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations.*;
import raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources.*;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.sources.EmptyCollection;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.TryableNullable;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;

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
      } catch (CsvParserRawTruffleException e) {
        // wrap any error with the stream location
        throw new CsvReaderRawTruffleException(
            e.getMessage(), computeNext.getStream(), e, thisNode);
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
      } catch (CsvParserRawTruffleException e) {
        // wrap any error with the stream location
        throw new CsvReaderRawTruffleException(
            e.getMessage(), computeNext.getStream(), e, thisNode);
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
      } catch (JsonReaderRawTruffleException e) {
        throw new JsonReaderRawTruffleException(
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
        @Cached(inline = false) @Cached.Shared("next2") GeneratorNodes.GeneratorNextNode nextNode,
        @Cached @Cached.Shared("hasNext2") GeneratorNodes.GeneratorHasNextNode hasNextNode,
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
        } catch (XmlParserRawTruffleException e) {
          throw new XmlReaderRawTruffleException(e, computeNext.getStream(), null);
        }
      }
    }

    @Specialization
    static Object next(Node node, EmptyComputeNext computeNext) {
      throw new BreakException();
    }

    public static LoopNode getFilterLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRCollectionFilterConditionNode(), new OSRCollectionFilterBodyNode()));
    }

    @Specialization
    static Object next(
        Node node,
        FilterComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getFilterLoopNode()",
                inline = false,
                allowUncached = true,
                neverDefault = true)
            LoopNode loopNode) {
      Frame frame = computeNext.getFrame();
      int resultSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.RESULT_SLOT);
      frame.setAuxiliarySlot(resultSlot, null);
      loopNode.execute(computeNext.getFrame());
      Object result = frame.getAuxiliarySlot(resultSlot);
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
        @Cached @Cached.Shared("hasNext1") GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached(inline = false) @Cached.Shared("next1") GeneratorNodes.GeneratorNextNode nextNode) {
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
        @Cached @Cached.Shared("hasNext1") GeneratorNodes.GeneratorHasNextNode hasNextNode,
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
        @Cached @Cached.Shared("hasNext1") GeneratorNodes.GeneratorHasNextNode hasNextNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close") GeneratorNodes.GeneratorCloseNode closeNode,
        @Cached @Cached.Shared("executeOne")
            FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode) {
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
          Object iterable = TryableNullable.getOrElse(functionResult, EmptyCollection.INSTANCE);
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

    @Specialization
    static Object next(
        Node node,
        ZipComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("hasNext1") GeneratorNodes.GeneratorHasNextNode hasNextNode1,
        @Cached @Cached.Shared("hasNext2") GeneratorNodes.GeneratorHasNextNode hasNextNode2,
        @Cached(inline = false) @Cached.Shared("next1") GeneratorNodes.GeneratorNextNode nextNode1,
        @Cached(inline = false) @Cached.Shared("next2") GeneratorNodes.GeneratorNextNode nextNode2,
        @CachedLibrary(limit = "5") InteropLibrary records) {
      try {
        if (hasNextNode1.execute(thisNode, computeNext.getParent1())
            && hasNextNode2.execute(thisNode, computeNext.getParent2())) {
          RecordObject record = computeNext.getLanguage().createRecord();
          records.writeMember(record, "_1", nextNode1.execute(thisNode, computeNext.getParent1()));
          records.writeMember(record, "_2", nextNode2.execute(thisNode, computeNext.getParent2()));
          return record;
        }
        throw new BreakException();
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException e) {
        throw new RawTruffleInternalErrorException(e);
      }
    }

    public static LoopNode getEquiJoinNextLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(new OSRFromBodyConditionNode(), new OSREquiJoinNextBodyNode()));
    }

    @Specialization
    static Object next(
        Node node,
        EquiJoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(value = "getEquiJoinNextLoopNode()", inline = false, allowUncached = true)
            LoopNode loopNode,
        @Cached FunctionExecuteNodes.FunctionExecuteTwo functionExecuteTwoNode) {

      assert (computeNext.getLeftMapGenerator() != null);
      assert (computeNext.getRightMapGenerator() != null);

      Frame frame = computeNext.getFrame();
      int shouldContinueSlot =
          frame
              .getFrameDescriptor()
              .findOrAddAuxiliarySlot(
                  raw.runtime.truffle.ast.osr.AuxiliarySlots.SHOULD_CONTINUE_SLOT);

      int computeNextSlot =
          frame
              .getFrameDescriptor()
              .findOrAddAuxiliarySlot(raw.runtime.truffle.ast.osr.AuxiliarySlots.COMPUTE_NEXT_SLOT);

      frame.setAuxiliarySlot(computeNextSlot, computeNext);
      frame.setAuxiliarySlot(shouldContinueSlot, true);

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
        throw new RawTruffleRuntimeException(e.getMessage(), e, node);
      }
    }

    public static LoopNode getJoinNextLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(new OSRFromBodyConditionNode(), new OSRJoinNextBodyNode()));
    }

    @Specialization
    static Object next(
        Node node,
        JoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getJoinNextLoopNode()",
                inline = false,
                allowUncached = true,
                neverDefault = true)
            LoopNode loopNode) {
      int shouldContinueSlot =
          computeNext
              .getFrame()
              .getFrameDescriptor()
              .findOrAddAuxiliarySlot(AuxiliarySlots.SHOULD_CONTINUE_SLOT);
      int computeNextSlot =
          computeNext
              .getFrame()
              .getFrameDescriptor()
              .findOrAddAuxiliarySlot(AuxiliarySlots.COMPUTE_NEXT_SLOT);
      int resultSlot =
          computeNext
              .getFrame()
              .getFrameDescriptor()
              .findOrAddAuxiliarySlot(AuxiliarySlots.RESULT_SLOT);
      computeNext.getFrame().setAuxiliarySlot(computeNextSlot, computeNext);
      computeNext.getFrame().setAuxiliarySlot(shouldContinueSlot, true);
      loopNode.execute(computeNext.getFrame());
      return computeNext.getFrame().getAuxiliarySlot(resultSlot);
    }
  }

  // ==================== ComputeNextNodes end ====================

  // ==================== InitNodes start =======================

  @NodeInfo(shortName = "Generator.Init")
  @GenerateUncached
  @GenerateInline
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
        TruffleInputStream truffleInputStream =
            new TruffleInputStream(computeNext.getLocation(), computeNext.getContext());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(
            initParser.execute(thisNode, computeNext.getStream(), computeNext.getSettings()));
      } catch (RawTruffleRuntimeException ex) {
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
      } catch (CsvReaderRawTruffleException ex) {
        CsvReaderRawTruffleException newEx =
            new CsvReaderRawTruffleException(
                ex.getMessage(), computeNext.getStream(), ex.getCause(), thisNode);
        closeParser.execute(thisNode, computeNext.getParser());
        throw newEx;
      } catch (RawTruffleRuntimeException ex) {
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
            new TruffleInputStream(computeNext.getLocationObject(), computeNext.getContext());
        computeNext.setStream(
            new TruffleCharInputStream(truffleInputStream, computeNext.getEncoding()));
        computeNext.setParser(initParser.execute(thisNode, computeNext.getStream()));
        // move from null to the first token
        nextToken.execute(thisNode, computeNext.getParser());
        // the first token is START_ARRAY so skip it
        nextToken.execute(thisNode, computeNext.getParser());
      } catch (JsonReaderRawTruffleException ex) {
        JsonReaderRawTruffleException newEx =
            new JsonReaderRawTruffleException(
                ex.getMessage(), computeNext.getParser(), computeNext.getStream(), ex, thisNode);
        closeParser.execute(thisNode, computeNext.getParser());
        throw newEx;
      } catch (RawTruffleRuntimeException ex) {
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
    static void init(Node node, XmlReadComputeNext computeNext) {
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
    static void init(Node node, EmptyComputeNext computeNext) {}

    @Specialization
    static void init(
        Node node,
        FilterComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode) {
      initNode.execute(thisNode, computeNext.getParent());
      Frame frame = computeNext.getFrame();
      int generatorSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT);
      int functionSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.FUNCTION_SLOT);

      frame.setAuxiliarySlot(generatorSlot, computeNext.getParent());
      frame.setAuxiliarySlot(functionSlot, computeNext.getPredicate());
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

    public static LoopNode getEquiJoinInitLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionAuxNode(), new OSRCollectionEquiJoinInitBodyNode()));
    }

    @Specialization
    static void init(
        Node node,
        EquiJoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(value = "getEquiJoinInitLoopNode()", inline = false, allowUncached = true)
            LoopNode loopNode1,
        @Cached(value = "getEquiJoinInitLoopNode()", inline = false, allowUncached = true)
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
        @Cached OffHeapNodes.OffHeapGeneratorNode offHeapGeneratorRight) {
      Frame frame = computeNext.getFrame();
      int mapSlot = frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.MAP_SLOT);
      int generatorSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT);
      int functionSlot =
          frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.FUNCTION_SLOT);
      // left side (get a generator, then fill a map, set leftMapGenerator to the map generator)
      OffHeapGroupByKey leftMap =
          new OffHeapGroupByKey(
              computeNext.getKeyType(),
              computeNext.getLeftRowType(),
              computeNext.getLanguage(),
              computeNext.getContext(),
              null);
      Object leftGenerator = getGenerator.execute(thisNode, computeNext.getLeftIterable());
      try {
        initLeftNode.execute(thisNode, leftGenerator);
        frame.setAuxiliarySlot(mapSlot, leftMap);
        frame.setAuxiliarySlot(generatorSlot, leftGenerator);
        frame.setAuxiliarySlot(functionSlot, computeNext.getLeftKeyF());
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
              computeNext.getLanguage(),
              computeNext.getContext(),
              null);
      Object rightGenerator = getGenerator.execute(thisNode, computeNext.getRightIterable());
      try {
        initRightNode.execute(thisNode, rightGenerator);
        frame.setAuxiliarySlot(mapSlot, rightMap);
        frame.setAuxiliarySlot(generatorSlot, rightGenerator);
        frame.setAuxiliarySlot(functionSlot, computeNext.getRightKeyF());
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
        throw new RawTruffleRuntimeException(e.getMessage(), e, node);
      }
    }

    public static LoopNode getJoinInitLoopNode() {
      return Truffle.getRuntime()
          .createLoopNode(
              new OSRGeneratorNode(
                  new OSRHasNextConditionAuxNode(), new OSRCollectionJoinInitBodyNode()));
    }

    @Specialization
    static void init(
        Node node,
        JoinComputeNext computeNext,
        @Bind("$node") Node thisNode,
        @Cached(
                value = "getJoinInitLoopNode()",
                inline = false,
                allowUncached = true,
                neverDefault = true)
            LoopNode loopNode,
        @Cached @Cached.Shared("getGenerator") IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached(inline = false) @Cached.Shared("init") GeneratorNodes.GeneratorInitNode initNode,
        @Cached @Cached.Shared("close1") GeneratorNodes.GeneratorCloseNode closeNode) {
      // initialize left
      computeNext.setLeftGen(getGeneratorNode.execute(thisNode, computeNext.getLeftIterable()));
      initNode.execute(thisNode, computeNext.getLeftGen());

      // save right to disk
      Object rightGen = getGeneratorNode.execute(thisNode, computeNext.getRightIterable());
      try (Output buffer = createOutput(computeNext, thisNode)) {
        initNode.execute(thisNode, rightGen);

        Frame frame = computeNext.getFrame();
        int generatorSlot =
            frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.GENERATOR_SLOT);
        int outputBufferSlot =
            frame.getFrameDescriptor().findOrAddAuxiliarySlot(AuxiliarySlots.OUTPUT_BUFFER_SLOT);
        int computeNextSlot =
            frame
                .getFrameDescriptor()
                .findOrAddAuxiliarySlot(
                    raw.runtime.truffle.ast.osr.AuxiliarySlots.COMPUTE_NEXT_SLOT);
        frame.setAuxiliarySlot(generatorSlot, rightGen);
        frame.setAuxiliarySlot(outputBufferSlot, buffer);
        frame.setAuxiliarySlot(computeNextSlot, computeNext);

        loopNode.execute(computeNext.getFrame());
      } finally {
        closeNode.execute(thisNode, rightGen);
      }
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
