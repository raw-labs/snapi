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

package raw.runtime.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.utils.TruffleCharInputStream;

import java.io.IOException;
import java.io.Reader;

public final class ParserOperations {

    private static final TruffleLogger LOG = TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);

    @NodeInfo(shortName = "Parser.Initialize")
    @GenerateUncached
    public abstract static class InitJsonParserNode extends Node {

        public abstract JsonParser execute(Object value);

        @Specialization
        @CompilerDirectives.TruffleBoundary
        JsonParser initParserFromString(String value, @Cached CloseJsonParserNode closeParser) {
            JsonParser parser = null;
            try {
                JsonFactory jsonFactory = new JsonFactory();
                jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE); // TODO (msb): Auto-disable or actually enable?
                parser = jsonFactory.createParser(value);
                parser.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
                return parser;
            } catch (IOException e) {
                JsonReaderRawTruffleException ex = new JsonReaderRawTruffleException();
                closeParser.execute(parser);
                throw ex;
            }
        }

        @Specialization
        @CompilerDirectives.TruffleBoundary
        JsonParser initParserFromStream(TruffleCharInputStream stream, @Cached CloseJsonParserNode closeParser) {
            JsonParser parser = null;
            try {
                JsonFactory jsonFactory = new JsonFactory();
                jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE); // TODO (msb): Auto-disable or actually enable?
                Reader reader = stream.getReader();
                parser = jsonFactory.createParser(reader);
                parser.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
                return parser;
            } catch (IOException e) {
                JsonReaderRawTruffleException ex = new JsonReaderRawTruffleException(parser, stream);
                closeParser.execute(parser);
                throw ex;
            }
        }

    }

    @NodeInfo(shortName = "Parser.Close")
    @GenerateUncached
    public abstract static class CloseJsonParserNode extends Node {

        public abstract void execute(JsonParser parser);

        @Specialization
        @CompilerDirectives.TruffleBoundary
        void closeParserSilently(JsonParser parser) {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (IOException e) {
                // Ignore but log
                LOG.severe(e.getMessage());
            }
        }
    }

    @NodeInfo(shortName = "Parser.NextToken")
    @GenerateUncached
    public abstract static class NextTokenJsonParserNode extends Node {

        public abstract void execute(JsonParser parser);

        @Specialization
        @CompilerDirectives.TruffleBoundary
        void nextToken(JsonParser parser) {
            try {
                parser.nextToken();
            } catch (IOException e) {
                throw new JsonReaderRawTruffleException(e.getMessage(), this);
            }
        }
    }

    @NodeInfo(shortName = "Parser.SkipNext")
    @GenerateUncached
    public abstract static class SkipNextJsonParserNode extends Node {

        public abstract void execute(JsonParser parser);

        @Specialization
        @CompilerDirectives.TruffleBoundary
        void skip(JsonParser parser) {
            try {
                parser.skipChildren(); // finish reading lists and records children (do nothing if not a list or record)
                parser.nextToken(); // swallow the next token (swallow closing braces, or int, float, etc.)
            } catch (IOException e) {
                throw new JsonReaderRawTruffleException(e.getMessage(), this);
            }
        }
    }

}






