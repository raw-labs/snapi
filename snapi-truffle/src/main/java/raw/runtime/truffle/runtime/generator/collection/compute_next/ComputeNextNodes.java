package raw.runtime.truffle.runtime.generator.collection.compute_next;

import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ast.io.csv.reader.CsvParserNodes;
import raw.runtime.truffle.ast.io.jdbc.JdbcQuery;
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
import raw.runtime.truffle.runtime.generator.collection.AbstractGeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.compute_next.operations.*;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.*;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.sources.EmptyCollection;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.TryableNullable;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;

import static java.lang.Math.addExact;

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
        @Cached(value = "computeNext.getRowParserCallTarget()") RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)") DirectCallNode rowParser) {
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
        @Cached(value = "computeNext.getRowParserCallTarget()") RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)") DirectCallNode rowParser) {
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
        @Cached(value = "computeNext.getRowParserCallTarget()") RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)") DirectCallNode rowParser) {
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
        @Cached(value = "computeNext.getParseNextCallTarget()") RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)") DirectCallNode parseNextCallNode) {
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
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode) {
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
        @Cached(value = "computeNext.getParseNextRootCallTarget()") RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)") DirectCallNode parseNextCallNode) {
      if (computeNext.getParser().onEndTag()) {
        throw new BreakException();
      } else {
        return parseNextCallNode.call(computeNext.getParser());
      }
    }

    @Specialization
    static Object next(
        XmlReadComputeNext computeNext,
        @Cached(value = "computeNext.getParseNextRootCallTarget()") RootCallTarget cachedTarget,
        @Cached(value = "create(cachedTarget)") DirectCallNode parseNextCallNode) {
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

    @Specialization
    static Object next(
        FilterComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode,
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
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode) {
      if (computeNext.getCurrentCount() < computeNext.getTakeCount()
          && hasNextNode.execute(computeNext.getParent())) {
        computeNext.incrementCurrentCount();
        return nextNode.execute(computeNext.getParent());
      }
      throw new BreakException();
    }

    @Specialization
    static Object next(
        TransformComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode,
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

    @Specialization
    static Object next(
        UnnestComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode,
        @Cached IterableNodes.GetGeneratorNode getGeneratorNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode,
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
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode1,
        @Cached AbstractGeneratorNodes.AbstractGeneratorHasNextNode hasNextNode2,
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode1,
        @Cached AbstractGeneratorNodes.AbstractGeneratorNextNode nextNode2,
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
  }

  // ==================== ComputeNextNodes end ====================

  // ==================== InitNodes start =======================

  @NodeInfo(shortName = "Generator.Init")
  @GenerateUncached
  public abstract static class InitNode extends Node {

    public abstract Object execute(Object computeNext);

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
        FilterComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        TakeComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        TransformComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        UnnestComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode) {
      initNode.execute(computeNext.getParent());
    }

    @Specialization
    static void init(
        ZipComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode1,
        @Cached AbstractGeneratorNodes.AbstractGeneratorInitNode initNode2) {
      initNode1.execute(computeNext.getParent1());
      initNode2.execute(computeNext.getParent2());
    }
  }

  // ==================== InitNodes end =======================

  // ==================== CloseNodes =======================

  @NodeInfo(shortName = "Generator.Close")
  @GenerateUncached
  public abstract static class CloseNode extends Node {

    public abstract Object execute(Object computeNext);

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
        UnionComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode) {
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
        FilterComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
    }

    @Specialization
    static void close(
        TakeComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
    }

    @Specialization
    static void close(
        TransformComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
    }

    @Specialization
    static void close(
        UnnestComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode) {
      closeNode.execute(computeNext.getParent());
      if (computeNext.getCurrentGenerator() != null) {
        closeNode.execute(computeNext.getCurrentGenerator());
      }
    }

    @Specialization
    static void close(
        ZipComputeNext computeNext,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode1,
        @Cached AbstractGeneratorNodes.AbstractGeneratorCloseNode closeNode2) {
      closeNode1.execute(computeNext.getParent1());
      closeNode2.execute(computeNext.getParent2());
    }
  }
  // ==================== CloseNodes end =======================
}
