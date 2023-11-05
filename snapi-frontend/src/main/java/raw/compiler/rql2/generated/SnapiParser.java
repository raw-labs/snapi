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

// Generated from SnapiParser.g4 by ANTLR 4.13.1
package raw.compiler.rql2.generated;

import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SnapiParser extends Parser {
  static {
    RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION);
  }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
  public static final int TYPE_TOKEN = 1,
      BOOL_TOKEN = 2,
      STRING_TOKEN = 3,
      LOCATION_TOKEN = 4,
      BINARY_TOKEN = 5,
      BYTE_TOKEN = 6,
      SHORT_TOKEN = 7,
      INT_TOKEN = 8,
      LONG_TOKEN = 9,
      FLOAT_TOKEN = 10,
      DOUBLE_TOKEN = 11,
      DECIMAL_TOKEN = 12,
      DATE_TOKEN = 13,
      TIME_TOKEN = 14,
      INTERVAL_TOKEN = 15,
      TIMESTAMP_TOKEN = 16,
      RECORD_TOKEN = 17,
      COLLECTION_TOKEN = 18,
      LIST_TOKEN = 19,
      LIBRARY_TOKEN = 20,
      PACKAGE_TOKEN = 21,
      LET_TOKEN = 22,
      IN_TOKEN = 23,
      REC_TOKEN = 24,
      UNDEFINED_TOKEN = 25,
      IF_TOKEN = 26,
      THEN_TOKEN = 27,
      ELSE_TOKEN = 28,
      NULL_TOKEN = 29,
      BYTE = 30,
      SHORT = 31,
      INTEGER = 32,
      LONG = 33,
      FLOAT = 34,
      DOUBLE = 35,
      DECIMAL = 36,
      EQ_TOKEN = 37,
      NEQ_TOKEN = 38,
      LE_TOKEN = 39,
      LT_TOKEN = 40,
      GE_TOKEN = 41,
      GT_TOKEN = 42,
      PLUS_TOKEN = 43,
      MINUS_TOKEN = 44,
      MUL_TOKEN = 45,
      DIV_TOKEN = 46,
      MOD_TOKEN = 47,
      AND_TOKEN = 48,
      OR_TOKEN = 49,
      NOT_TOKEN = 50,
      TRUE_TOKEN = 51,
      FALSE_TOKEN = 52,
      STRING = 53,
      START_TRIPLE_QUOTE = 54,
      NON_ESC_IDENTIFIER = 55,
      ESC_IDENTIFIER = 56,
      WS = 57,
      LINE_COMMENT = 58,
      LEFT_PAREN = 59,
      RIGHT_PAREN = 60,
      COLON = 61,
      COMMA = 62,
      EQUALS = 63,
      RIGHT_ARROW = 64,
      DOT = 65,
      LEFT_CUR_BR = 66,
      RIGHT_CUR_BR = 67,
      LEFT_SQ_BR = 68,
      RIGHT_SQ_BR = 69,
      TRIPLE_QUOTED_STRING_CONTENT = 70,
      TRIPLE_QUOTE_END_2 = 71,
      TRIPLE_QUOTE_END_1 = 72,
      TRIPLE_QUOTE_END_0 = 73;
  public static final int RULE_prog = 0,
      RULE_stat = 1,
      RULE_method_dec = 2,
      RULE_fun_dec = 3,
      RULE_fun_proto = 4,
      RULE_fun_param = 5,
      RULE_attr = 6,
      RULE_type_attr = 7,
      RULE_fun_ar = 8,
      RULE_fun_args = 9,
      RULE_fun_arg = 10,
      RULE_fun_abs = 11,
      RULE_fun_proto_lambda = 12,
      RULE_tipe = 13,
      RULE_or_type = 14,
      RULE_record_type = 15,
      RULE_iterable_type = 16,
      RULE_list_type = 17,
      RULE_expr_type = 18,
      RULE_expr = 19,
      RULE_let = 20,
      RULE_let_left = 21,
      RULE_let_decl = 22,
      RULE_let_bind = 23,
      RULE_if_then_else = 24,
      RULE_lists = 25,
      RULE_lists_element = 26,
      RULE_records = 27,
      RULE_record_elements = 28,
      RULE_record_element = 29,
      RULE_number = 30,
      RULE_primitive_types = 31,
      RULE_compare_tokens = 32,
      RULE_bool_const = 33,
      RULE_ident = 34;

  private static String[] makeRuleNames() {
    return new String[] {
      "prog",
      "stat",
      "method_dec",
      "fun_dec",
      "fun_proto",
      "fun_param",
      "attr",
      "type_attr",
      "fun_ar",
      "fun_args",
      "fun_arg",
      "fun_abs",
      "fun_proto_lambda",
      "tipe",
      "or_type",
      "record_type",
      "iterable_type",
      "list_type",
      "expr_type",
      "expr",
      "let",
      "let_left",
      "let_decl",
      "let_bind",
      "if_then_else",
      "lists",
      "lists_element",
      "records",
      "record_elements",
      "record_element",
      "number",
      "primitive_types",
      "compare_tokens",
      "bool_const",
      "ident"
    };
  }

  public static final String[] ruleNames = makeRuleNames();

  private static String[] makeLiteralNames() {
    return new String[] {
      null,
      "'type'",
      "'bool'",
      "'string'",
      "'location'",
      "'binary'",
      "'byte'",
      "'short'",
      "'int'",
      "'long'",
      "'float'",
      "'double'",
      "'decimal'",
      "'date'",
      "'time'",
      "'interval'",
      "'timestamp'",
      "'record'",
      "'collection'",
      "'list'",
      "'library'",
      "'package'",
      "'let'",
      "'in'",
      "'rec'",
      "'undefined'",
      "'if'",
      "'then'",
      "'else'",
      "'null'",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "'=='",
      "'!='",
      "'<='",
      "'<'",
      "'>='",
      "'>'",
      "'+'",
      "'-'",
      "'*'",
      "'/'",
      "'%'",
      "'and'",
      "'or'",
      "'not'",
      "'true'",
      "'false'",
      null,
      null,
      null,
      null,
      null,
      null,
      "'('",
      "')'",
      "':'",
      "','",
      "'='",
      "'->'",
      "'.'",
      "'{'",
      "'}'",
      "'['",
      "']'",
      null,
      "'\"\"\"\"\"'",
      "'\"\"\"\"'"
    };
  }

  private static final String[] _LITERAL_NAMES = makeLiteralNames();

  private static String[] makeSymbolicNames() {
    return new String[] {
      null,
      "TYPE_TOKEN",
      "BOOL_TOKEN",
      "STRING_TOKEN",
      "LOCATION_TOKEN",
      "BINARY_TOKEN",
      "BYTE_TOKEN",
      "SHORT_TOKEN",
      "INT_TOKEN",
      "LONG_TOKEN",
      "FLOAT_TOKEN",
      "DOUBLE_TOKEN",
      "DECIMAL_TOKEN",
      "DATE_TOKEN",
      "TIME_TOKEN",
      "INTERVAL_TOKEN",
      "TIMESTAMP_TOKEN",
      "RECORD_TOKEN",
      "COLLECTION_TOKEN",
      "LIST_TOKEN",
      "LIBRARY_TOKEN",
      "PACKAGE_TOKEN",
      "LET_TOKEN",
      "IN_TOKEN",
      "REC_TOKEN",
      "UNDEFINED_TOKEN",
      "IF_TOKEN",
      "THEN_TOKEN",
      "ELSE_TOKEN",
      "NULL_TOKEN",
      "BYTE",
      "SHORT",
      "INTEGER",
      "LONG",
      "FLOAT",
      "DOUBLE",
      "DECIMAL",
      "EQ_TOKEN",
      "NEQ_TOKEN",
      "LE_TOKEN",
      "LT_TOKEN",
      "GE_TOKEN",
      "GT_TOKEN",
      "PLUS_TOKEN",
      "MINUS_TOKEN",
      "MUL_TOKEN",
      "DIV_TOKEN",
      "MOD_TOKEN",
      "AND_TOKEN",
      "OR_TOKEN",
      "NOT_TOKEN",
      "TRUE_TOKEN",
      "FALSE_TOKEN",
      "STRING",
      "START_TRIPLE_QUOTE",
      "NON_ESC_IDENTIFIER",
      "ESC_IDENTIFIER",
      "WS",
      "LINE_COMMENT",
      "LEFT_PAREN",
      "RIGHT_PAREN",
      "COLON",
      "COMMA",
      "EQUALS",
      "RIGHT_ARROW",
      "DOT",
      "LEFT_CUR_BR",
      "RIGHT_CUR_BR",
      "LEFT_SQ_BR",
      "RIGHT_SQ_BR",
      "TRIPLE_QUOTED_STRING_CONTENT",
      "TRIPLE_QUOTE_END_2",
      "TRIPLE_QUOTE_END_1",
      "TRIPLE_QUOTE_END_0"
    };
  }

  private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

  /** @deprecated Use {@link #VOCABULARY} instead. */
  @Deprecated public static final String[] tokenNames;

  static {
    tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }

      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
  }

  @Override
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
  }

  @Override
  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  @Override
  public String getGrammarFileName() {
    return "SnapiParser.g4";
  }

  @Override
  public String[] getRuleNames() {
    return ruleNames;
  }

  @Override
  public String getSerializedATN() {
    return _serializedATN;
  }

  @Override
  public ATN getATN() {
    return _ATN;
  }

  public SnapiParser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ProgContext extends ParserRuleContext {
    public StatContext stat() {
      return getRuleContext(StatContext.class, 0);
    }

    public TerminalNode EOF() {
      return getToken(SnapiParser.EOF, 0);
    }

    public ProgContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_prog;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).enterProg(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).exitProg(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitProg(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ProgContext prog() throws RecognitionException {
    ProgContext _localctx = new ProgContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_prog);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(70);
        stat();
        setState(71);
        match(EOF);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class StatContext extends ParserRuleContext {
    public StatContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_stat;
    }

    public StatContext() {}

    public void copyFrom(StatContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunDecExprStatContext extends StatContext {
    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public List<Method_decContext> method_dec() {
      return getRuleContexts(Method_decContext.class);
    }

    public Method_decContext method_dec(int i) {
      return getRuleContext(Method_decContext.class, i);
    }

    public FunDecExprStatContext(StatContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunDecExprStat(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunDecExprStat(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunDecExprStat(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunDecStatContext extends StatContext {
    public List<Method_decContext> method_dec() {
      return getRuleContexts(Method_decContext.class);
    }

    public Method_decContext method_dec(int i) {
      return getRuleContext(Method_decContext.class, i);
    }

    public FunDecStatContext(StatContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunDecStat(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunDecStat(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunDecStat(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StatContext stat() throws RecognitionException {
    StatContext _localctx = new StatContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_stat);
    int _la;
    try {
      int _alt;
      setState(86);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
        case 1:
          _localctx = new FunDecStatContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(76);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391091494908L) != 0)) {
              {
                {
                  setState(73);
                  method_dec();
                }
              }
              setState(78);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
          break;
        case 2:
          _localctx = new FunDecExprStatContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(82);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
            while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
              if (_alt == 1) {
                {
                  {
                    setState(79);
                    method_dec();
                  }
                }
              }
              setState(84);
              _errHandler.sync(this);
              _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
            }
            setState(85);
            expr(0);
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Method_decContext extends ParserRuleContext {
    public Method_decContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_method_dec;
    }

    public Method_decContext() {}

    public void copyFrom(Method_decContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class MethodDecContext extends Method_decContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public Fun_protoContext fun_proto() {
      return getRuleContext(Fun_protoContext.class, 0);
    }

    public MethodDecContext(Method_decContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterMethodDec(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitMethodDec(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitMethodDec(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Method_decContext method_dec() throws RecognitionException {
    Method_decContext _localctx = new Method_decContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_method_dec);
    try {
      _localctx = new MethodDecContext(_localctx);
      enterOuterAlt(_localctx, 1);
      {
        setState(88);
        ident();
        setState(89);
        fun_proto();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_decContext extends ParserRuleContext {
    public Fun_decContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_dec;
    }

    public Fun_decContext() {}

    public void copyFrom(Fun_decContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NormalFunContext extends Fun_decContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public Fun_protoContext fun_proto() {
      return getRuleContext(Fun_protoContext.class, 0);
    }

    public NormalFunContext(Fun_decContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNormalFun(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNormalFun(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNormalFun(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class RecFunContext extends Fun_decContext {
    public TerminalNode REC_TOKEN() {
      return getToken(SnapiParser.REC_TOKEN, 0);
    }

    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public Fun_protoContext fun_proto() {
      return getRuleContext(Fun_protoContext.class, 0);
    }

    public RecFunContext(Fun_decContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecFun(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecFun(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecFun(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_decContext fun_dec() throws RecognitionException {
    Fun_decContext _localctx = new Fun_decContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_fun_dec);
    try {
      setState(98);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case BOOL_TOKEN:
        case STRING_TOKEN:
        case LOCATION_TOKEN:
        case BINARY_TOKEN:
        case BYTE_TOKEN:
        case SHORT_TOKEN:
        case INT_TOKEN:
        case LONG_TOKEN:
        case FLOAT_TOKEN:
        case DOUBLE_TOKEN:
        case DECIMAL_TOKEN:
        case DATE_TOKEN:
        case TIME_TOKEN:
        case INTERVAL_TOKEN:
        case TIMESTAMP_TOKEN:
        case RECORD_TOKEN:
        case COLLECTION_TOKEN:
        case LIST_TOKEN:
        case UNDEFINED_TOKEN:
        case NON_ESC_IDENTIFIER:
        case ESC_IDENTIFIER:
          _localctx = new NormalFunContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(91);
            ident();
            setState(92);
            fun_proto();
          }
          break;
        case REC_TOKEN:
          _localctx = new RecFunContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(94);
            match(REC_TOKEN);
            setState(95);
            ident();
            setState(96);
            fun_proto();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_protoContext extends ParserRuleContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public TerminalNode EQUALS() {
      return getToken(SnapiParser.EQUALS, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public List<Fun_paramContext> fun_param() {
      return getRuleContexts(Fun_paramContext.class);
    }

    public Fun_paramContext fun_param(int i) {
      return getRuleContext(Fun_paramContext.class, i);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Fun_protoContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_proto;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFun_proto(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFun_proto(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFun_proto(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_protoContext fun_proto() throws RecognitionException {
    Fun_protoContext _localctx = new Fun_protoContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_fun_proto);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(100);
        match(LEFT_PAREN);
        setState(109);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391091494908L) != 0)) {
          {
            setState(101);
            fun_param();
            setState(106);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while (_la == COMMA) {
              {
                {
                  setState(102);
                  match(COMMA);
                  setState(103);
                  fun_param();
                }
              }
              setState(108);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
        }

        setState(111);
        match(RIGHT_PAREN);
        setState(114);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COLON) {
          {
            setState(112);
            match(COLON);
            setState(113);
            tipe(0);
          }
        }

        setState(116);
        match(EQUALS);
        setState(117);
        expr(0);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_paramContext extends ParserRuleContext {
    public Fun_paramContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_param;
    }

    public Fun_paramContext() {}

    public void copyFrom(Fun_paramContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunParamAttrExprContext extends Fun_paramContext {
    public AttrContext attr() {
      return getRuleContext(AttrContext.class, 0);
    }

    public TerminalNode EQUALS() {
      return getToken(SnapiParser.EQUALS, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public FunParamAttrExprContext(Fun_paramContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunParamAttrExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunParamAttrExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunParamAttrExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunParamAttrContext extends Fun_paramContext {
    public AttrContext attr() {
      return getRuleContext(AttrContext.class, 0);
    }

    public FunParamAttrContext(Fun_paramContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunParamAttr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunParamAttr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunParamAttr(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_paramContext fun_param() throws RecognitionException {
    Fun_paramContext _localctx = new Fun_paramContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_fun_param);
    try {
      setState(124);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
        case 1:
          _localctx = new FunParamAttrContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(119);
            attr();
          }
          break;
        case 2:
          _localctx = new FunParamAttrExprContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(120);
            attr();
            setState(121);
            match(EQUALS);
            setState(122);
            expr(0);
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class AttrContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public AttrContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_attr;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).enterAttr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).exitAttr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitAttr(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AttrContext attr() throws RecognitionException {
    AttrContext _localctx = new AttrContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_attr);
    try {
      setState(131);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(126);
            ident();
            setState(127);
            match(COLON);
            setState(128);
            tipe(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(130);
            ident();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Type_attrContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public Type_attrContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_type_attr;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterType_attr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitType_attr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitType_attr(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Type_attrContext type_attr() throws RecognitionException {
    Type_attrContext _localctx = new Type_attrContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_type_attr);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(133);
        ident();
        setState(134);
        match(COLON);
        setState(135);
        tipe(0);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_arContext extends ParserRuleContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public Fun_argsContext fun_args() {
      return getRuleContext(Fun_argsContext.class, 0);
    }

    public Fun_arContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_ar;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFun_ar(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFun_ar(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFun_ar(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_arContext fun_ar() throws RecognitionException {
    Fun_arContext _localctx = new Fun_arContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_fun_ar);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(137);
        match(LEFT_PAREN);
        setState(139);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 719476565759492094L) != 0)
            || _la == LEFT_CUR_BR
            || _la == LEFT_SQ_BR) {
          {
            setState(138);
            fun_args();
          }
        }

        setState(141);
        match(RIGHT_PAREN);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_argsContext extends ParserRuleContext {
    public List<Fun_argContext> fun_arg() {
      return getRuleContexts(Fun_argContext.class);
    }

    public Fun_argContext fun_arg(int i) {
      return getRuleContext(Fun_argContext.class, i);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Fun_argsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_args;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFun_args(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFun_args(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFun_args(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_argsContext fun_args() throws RecognitionException {
    Fun_argsContext _localctx = new Fun_argsContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_fun_args);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(143);
        fun_arg();
        setState(148);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == COMMA) {
          {
            {
              setState(144);
              match(COMMA);
              setState(145);
              fun_arg();
            }
          }
          setState(150);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_argContext extends ParserRuleContext {
    public Fun_argContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_arg;
    }

    public Fun_argContext() {}

    public void copyFrom(Fun_argContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NamedFunArgExprContext extends Fun_argContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TerminalNode EQUALS() {
      return getToken(SnapiParser.EQUALS, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public NamedFunArgExprContext(Fun_argContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNamedFunArgExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNamedFunArgExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNamedFunArgExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunArgExprContext extends Fun_argContext {
    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public FunArgExprContext(Fun_argContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunArgExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunArgExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunArgExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_argContext fun_arg() throws RecognitionException {
    Fun_argContext _localctx = new Fun_argContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_fun_arg);
    try {
      setState(156);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
        case 1:
          _localctx = new FunArgExprContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(151);
            expr(0);
          }
          break;
        case 2:
          _localctx = new NamedFunArgExprContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(152);
            ident();
            setState(153);
            match(EQUALS);
            setState(154);
            expr(0);
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_absContext extends ParserRuleContext {
    public Fun_absContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_abs;
    }

    public Fun_absContext() {}

    public void copyFrom(Fun_absContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunAbsContext extends Fun_absContext {
    public Fun_proto_lambdaContext fun_proto_lambda() {
      return getRuleContext(Fun_proto_lambdaContext.class, 0);
    }

    public FunAbsContext(Fun_absContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunAbs(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunAbs(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunAbs(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunAbsUnnamedContext extends Fun_absContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TerminalNode RIGHT_ARROW() {
      return getToken(SnapiParser.RIGHT_ARROW, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public FunAbsUnnamedContext(Fun_absContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunAbsUnnamed(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunAbsUnnamed(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunAbsUnnamed(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_absContext fun_abs() throws RecognitionException {
    Fun_absContext _localctx = new Fun_absContext(_ctx, getState());
    enterRule(_localctx, 22, RULE_fun_abs);
    try {
      setState(163);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case LEFT_PAREN:
          _localctx = new FunAbsContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(158);
            fun_proto_lambda();
          }
          break;
        case BOOL_TOKEN:
        case STRING_TOKEN:
        case LOCATION_TOKEN:
        case BINARY_TOKEN:
        case BYTE_TOKEN:
        case SHORT_TOKEN:
        case INT_TOKEN:
        case LONG_TOKEN:
        case FLOAT_TOKEN:
        case DOUBLE_TOKEN:
        case DECIMAL_TOKEN:
        case DATE_TOKEN:
        case TIME_TOKEN:
        case INTERVAL_TOKEN:
        case TIMESTAMP_TOKEN:
        case RECORD_TOKEN:
        case COLLECTION_TOKEN:
        case LIST_TOKEN:
        case UNDEFINED_TOKEN:
        case NON_ESC_IDENTIFIER:
        case ESC_IDENTIFIER:
          _localctx = new FunAbsUnnamedContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(159);
            ident();
            setState(160);
            match(RIGHT_ARROW);
            setState(161);
            expr(0);
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Fun_proto_lambdaContext extends ParserRuleContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public TerminalNode RIGHT_ARROW() {
      return getToken(SnapiParser.RIGHT_ARROW, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public List<Fun_paramContext> fun_param() {
      return getRuleContexts(Fun_paramContext.class);
    }

    public Fun_paramContext fun_param(int i) {
      return getRuleContext(Fun_paramContext.class, i);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Fun_proto_lambdaContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_proto_lambda;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFun_proto_lambda(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFun_proto_lambda(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFun_proto_lambda(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_proto_lambdaContext fun_proto_lambda() throws RecognitionException {
    Fun_proto_lambdaContext _localctx = new Fun_proto_lambdaContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_fun_proto_lambda);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(165);
        match(LEFT_PAREN);
        setState(174);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391091494908L) != 0)) {
          {
            setState(166);
            fun_param();
            setState(171);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while (_la == COMMA) {
              {
                {
                  setState(167);
                  match(COMMA);
                  setState(168);
                  fun_param();
                }
              }
              setState(173);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
        }

        setState(176);
        match(RIGHT_PAREN);
        setState(179);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COLON) {
          {
            setState(177);
            match(COLON);
            setState(178);
            tipe(0);
          }
        }

        setState(181);
        match(RIGHT_ARROW);
        setState(182);
        expr(0);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TipeContext extends ParserRuleContext {
    public TipeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_tipe;
    }

    public TipeContext() {}

    public void copyFrom(TipeContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunTypeWithParamsTypeContext extends TipeContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public TerminalNode RIGHT_ARROW() {
      return getToken(SnapiParser.RIGHT_ARROW, 0);
    }

    public List<TipeContext> tipe() {
      return getRuleContexts(TipeContext.class);
    }

    public TipeContext tipe(int i) {
      return getRuleContext(TipeContext.class, i);
    }

    public List<AttrContext> attr() {
      return getRuleContexts(AttrContext.class);
    }

    public AttrContext attr(int i) {
      return getRuleContext(AttrContext.class, i);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public FunTypeWithParamsTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunTypeWithParamsType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunTypeWithParamsType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunTypeWithParamsType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ExprTypeTypeContext extends TipeContext {
    public Expr_typeContext expr_type() {
      return getRuleContext(Expr_typeContext.class, 0);
    }

    public ExprTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterExprTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitExprTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitExprTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class RecordTypeTypeContext extends TipeContext {
    public Record_typeContext record_type() {
      return getRuleContext(Record_typeContext.class, 0);
    }

    public RecordTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecordTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecordTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecordTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class IterableTypeTypeContext extends TipeContext {
    public Iterable_typeContext iterable_type() {
      return getRuleContext(Iterable_typeContext.class, 0);
    }

    public IterableTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterIterableTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitIterableTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitIterableTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TypeWithParenTypeContext extends TipeContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public TypeWithParenTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterTypeWithParenType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitTypeWithParenType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitTypeWithParenType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ListTypeTypeContext extends TipeContext {
    public List_typeContext list_type() {
      return getRuleContext(List_typeContext.class, 0);
    }

    public ListTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterListTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitListTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitListTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class OrTypeFunTypeContext extends TipeContext {
    public List<TipeContext> tipe() {
      return getRuleContexts(TipeContext.class);
    }

    public TipeContext tipe(int i) {
      return getRuleContext(TipeContext.class, i);
    }

    public TerminalNode OR_TOKEN() {
      return getToken(SnapiParser.OR_TOKEN, 0);
    }

    public Or_typeContext or_type() {
      return getRuleContext(Or_typeContext.class, 0);
    }

    public TerminalNode RIGHT_ARROW() {
      return getToken(SnapiParser.RIGHT_ARROW, 0);
    }

    public OrTypeFunTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterOrTypeFunType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitOrTypeFunType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitOrTypeFunType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class OrTypeTypeContext extends TipeContext {
    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public TerminalNode OR_TOKEN() {
      return getToken(SnapiParser.OR_TOKEN, 0);
    }

    public Or_typeContext or_type() {
      return getRuleContext(Or_typeContext.class, 0);
    }

    public OrTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterOrTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitOrTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitOrTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PrimitiveTypeTypeContext extends TipeContext {
    public Primitive_typesContext primitive_types() {
      return getRuleContext(Primitive_typesContext.class, 0);
    }

    public PrimitiveTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterPrimitiveTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitPrimitiveTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitPrimitiveTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TypeAliasTypeContext extends TipeContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TypeAliasTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterTypeAliasType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitTypeAliasType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitTypeAliasType(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunTypeTypeContext extends TipeContext {
    public List<TipeContext> tipe() {
      return getRuleContexts(TipeContext.class);
    }

    public TipeContext tipe(int i) {
      return getRuleContext(TipeContext.class, i);
    }

    public TerminalNode RIGHT_ARROW() {
      return getToken(SnapiParser.RIGHT_ARROW, 0);
    }

    public FunTypeTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunTypeType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunTypeType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunTypeType(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TipeContext tipe() throws RecognitionException {
    return tipe(0);
  }

  private TipeContext tipe(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    TipeContext _localctx = new TipeContext(_ctx, _parentState);
    TipeContext _prevctx = _localctx;
    int _startState = 26;
    enterRecursionRule(_localctx, 26, RULE_tipe, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(214);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 19, _ctx)) {
          case 1:
            {
              _localctx = new TypeWithParenTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;

              setState(185);
              match(LEFT_PAREN);
              setState(186);
              tipe(0);
              setState(187);
              match(RIGHT_PAREN);
            }
            break;
          case 2:
            {
              _localctx = new PrimitiveTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(189);
              primitive_types();
            }
            break;
          case 3:
            {
              _localctx = new RecordTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(190);
              record_type();
            }
            break;
          case 4:
            {
              _localctx = new IterableTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(191);
              iterable_type();
            }
            break;
          case 5:
            {
              _localctx = new ListTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(192);
              list_type();
            }
            break;
          case 6:
            {
              _localctx = new TypeAliasTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(193);
              ident();
            }
            break;
          case 7:
            {
              _localctx = new FunTypeWithParamsTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(194);
              match(LEFT_PAREN);
              setState(197);
              _errHandler.sync(this);
              switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
                case 1:
                  {
                    setState(195);
                    tipe(0);
                  }
                  break;
                case 2:
                  {
                    setState(196);
                    attr();
                  }
                  break;
              }
              setState(206);
              _errHandler.sync(this);
              _la = _input.LA(1);
              while (_la == COMMA) {
                {
                  {
                    setState(199);
                    match(COMMA);
                    setState(202);
                    _errHandler.sync(this);
                    switch (getInterpreter().adaptivePredict(_input, 17, _ctx)) {
                      case 1:
                        {
                          setState(200);
                          tipe(0);
                        }
                        break;
                      case 2:
                        {
                          setState(201);
                          attr();
                        }
                        break;
                    }
                  }
                }
                setState(208);
                _errHandler.sync(this);
                _la = _input.LA(1);
              }
              setState(209);
              match(RIGHT_PAREN);
              setState(210);
              match(RIGHT_ARROW);
              setState(211);
              tipe(3);
            }
            break;
          case 8:
            {
              _localctx = new ExprTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(213);
              expr_type();
            }
            break;
        }
        _ctx.stop = _input.LT(-1);
        setState(230);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 21, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            if (_parseListeners != null) triggerExitRuleEvent();
            _prevctx = _localctx;
            {
              setState(228);
              _errHandler.sync(this);
              switch (getInterpreter().adaptivePredict(_input, 20, _ctx)) {
                case 1:
                  {
                    _localctx = new OrTypeFunTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(216);
                    if (!(precpred(_ctx, 10)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                    setState(217);
                    match(OR_TOKEN);
                    setState(218);
                    or_type();
                    setState(219);
                    match(RIGHT_ARROW);
                    setState(220);
                    tipe(11);
                  }
                  break;
                case 2:
                  {
                    _localctx = new FunTypeTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(222);
                    if (!(precpred(_ctx, 2)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                    setState(223);
                    match(RIGHT_ARROW);
                    setState(224);
                    tipe(3);
                  }
                  break;
                case 3:
                  {
                    _localctx = new OrTypeTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(225);
                    if (!(precpred(_ctx, 9)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                    setState(226);
                    match(OR_TOKEN);
                    setState(227);
                    or_type();
                  }
                  break;
              }
            }
          }
          setState(232);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 21, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Or_typeContext extends ParserRuleContext {
    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public TerminalNode OR_TOKEN() {
      return getToken(SnapiParser.OR_TOKEN, 0);
    }

    public Or_typeContext or_type() {
      return getRuleContext(Or_typeContext.class, 0);
    }

    public Or_typeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_or_type;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterOr_type(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitOr_type(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitOr_type(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Or_typeContext or_type() throws RecognitionException {
    Or_typeContext _localctx = new Or_typeContext(_ctx, getState());
    enterRule(_localctx, 28, RULE_or_type);
    try {
      setState(238);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 22, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(233);
            tipe(0);
            setState(234);
            match(OR_TOKEN);
            setState(235);
            or_type();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(237);
            tipe(0);
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Record_typeContext extends ParserRuleContext {
    public TerminalNode RECORD_TOKEN() {
      return getToken(SnapiParser.RECORD_TOKEN, 0);
    }

    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public List<Type_attrContext> type_attr() {
      return getRuleContexts(Type_attrContext.class);
    }

    public Type_attrContext type_attr(int i) {
      return getRuleContext(Type_attrContext.class, i);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Record_typeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_record_type;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecord_type(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecord_type(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecord_type(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Record_typeContext record_type() throws RecognitionException {
    Record_typeContext _localctx = new Record_typeContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_record_type);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(240);
        match(RECORD_TOKEN);
        setState(241);
        match(LEFT_PAREN);
        setState(242);
        type_attr();
        setState(247);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == COMMA) {
          {
            {
              setState(243);
              match(COMMA);
              setState(244);
              type_attr();
            }
          }
          setState(249);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(250);
        match(RIGHT_PAREN);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Iterable_typeContext extends ParserRuleContext {
    public TerminalNode COLLECTION_TOKEN() {
      return getToken(SnapiParser.COLLECTION_TOKEN, 0);
    }

    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public Iterable_typeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_iterable_type;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterIterable_type(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitIterable_type(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitIterable_type(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Iterable_typeContext iterable_type() throws RecognitionException {
    Iterable_typeContext _localctx = new Iterable_typeContext(_ctx, getState());
    enterRule(_localctx, 32, RULE_iterable_type);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(252);
        match(COLLECTION_TOKEN);
        setState(253);
        match(LEFT_PAREN);
        setState(254);
        tipe(0);
        setState(255);
        match(RIGHT_PAREN);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class List_typeContext extends ParserRuleContext {
    public TerminalNode LIST_TOKEN() {
      return getToken(SnapiParser.LIST_TOKEN, 0);
    }

    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public List_typeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_list_type;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterList_type(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitList_type(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitList_type(this);
      else return visitor.visitChildren(this);
    }
  }

  public final List_typeContext list_type() throws RecognitionException {
    List_typeContext _localctx = new List_typeContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_list_type);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(257);
        match(LIST_TOKEN);
        setState(258);
        match(LEFT_PAREN);
        setState(259);
        tipe(0);
        setState(260);
        match(RIGHT_PAREN);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Expr_typeContext extends ParserRuleContext {
    public TerminalNode TYPE_TOKEN() {
      return getToken(SnapiParser.TYPE_TOKEN, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public Expr_typeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_expr_type;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterExpr_type(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitExpr_type(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitExpr_type(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Expr_typeContext expr_type() throws RecognitionException {
    Expr_typeContext _localctx = new Expr_typeContext(_ctx, getState());
    enterRule(_localctx, 36, RULE_expr_type);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(262);
        match(TYPE_TOKEN);
        setState(263);
        tipe(0);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ExprContext extends ParserRuleContext {
    public ExprContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_expr;
    }

    public ExprContext() {}

    public void copyFrom(ExprContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class MulExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode MUL_TOKEN() {
      return getToken(SnapiParser.MUL_TOKEN, 0);
    }

    public MulExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterMulExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitMulExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitMulExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class AndExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode AND_TOKEN() {
      return getToken(SnapiParser.AND_TOKEN, 0);
    }

    public AndExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterAndExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitAndExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitAndExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class StringExprContext extends ExprContext {
    public TerminalNode STRING() {
      return getToken(SnapiParser.STRING, 0);
    }

    public StringExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterStringExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitStringExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitStringExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class MinusUnaryExprContext extends ExprContext {
    public TerminalNode MINUS_TOKEN() {
      return getToken(SnapiParser.MINUS_TOKEN, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public MinusUnaryExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterMinusUnaryExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitMinusUnaryExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitMinusUnaryExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NullExprContext extends ExprContext {
    public TerminalNode NULL_TOKEN() {
      return getToken(SnapiParser.NULL_TOKEN, 0);
    }

    public NullExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNullExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNullExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNullExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PlusExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode PLUS_TOKEN() {
      return getToken(SnapiParser.PLUS_TOKEN, 0);
    }

    public PlusExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterPlusExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitPlusExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitPlusExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NumberExprContext extends ExprContext {
    public NumberContext number() {
      return getRuleContext(NumberContext.class, 0);
    }

    public NumberExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNumberExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNumberExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNumberExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class CompareExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public Compare_tokensContext compare_tokens() {
      return getRuleContext(Compare_tokensContext.class, 0);
    }

    public CompareExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterCompareExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitCompareExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitCompareExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class PlusUnaryExprContext extends ExprContext {
    public TerminalNode PLUS_TOKEN() {
      return getToken(SnapiParser.PLUS_TOKEN, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public PlusUnaryExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterPlusUnaryExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitPlusUnaryExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitPlusUnaryExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ListExprContext extends ExprContext {
    public ListsContext lists() {
      return getRuleContext(ListsContext.class, 0);
    }

    public ListExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterListExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitListExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitListExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NotExprContext extends ExprContext {
    public TerminalNode NOT_TOKEN() {
      return getToken(SnapiParser.NOT_TOKEN, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public NotExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNotExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNotExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNotExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class RecordExprContext extends ExprContext {
    public RecordsContext records() {
      return getRuleContext(RecordsContext.class, 0);
    }

    public RecordExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecordExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecordExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecordExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class MinusExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode MINUS_TOKEN() {
      return getToken(SnapiParser.MINUS_TOKEN, 0);
    }

    public MinusExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterMinusExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitMinusExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitMinusExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class IdentExprContext extends ExprContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public IdentExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterIdentExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitIdentExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitIdentExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class BoolConstExprContext extends ExprContext {
    public Bool_constContext bool_const() {
      return getRuleContext(Bool_constContext.class, 0);
    }

    public BoolConstExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterBoolConstExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitBoolConstExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitBoolConstExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ProjectionExprContext extends ExprContext {
    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public TerminalNode DOT() {
      return getToken(SnapiParser.DOT, 0);
    }

    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public Fun_arContext fun_ar() {
      return getRuleContext(Fun_arContext.class, 0);
    }

    public ProjectionExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterProjectionExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitProjectionExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitProjectionExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class LetExprContext extends ExprContext {
    public LetContext let() {
      return getRuleContext(LetContext.class, 0);
    }

    public LetExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLetExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLetExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLetExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunAbsExprContext extends ExprContext {
    public Fun_absContext fun_abs() {
      return getRuleContext(Fun_absContext.class, 0);
    }

    public FunAbsExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunAbsExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunAbsExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunAbsExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunAppExprContext extends ExprContext {
    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public Fun_arContext fun_ar() {
      return getRuleContext(Fun_arContext.class, 0);
    }

    public FunAppExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunAppExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunAppExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunAppExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class OrExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode OR_TOKEN() {
      return getToken(SnapiParser.OR_TOKEN, 0);
    }

    public OrExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterOrExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitOrExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitOrExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class IfThenElseExprContext extends ExprContext {
    public If_then_elseContext if_then_else() {
      return getRuleContext(If_then_elseContext.class, 0);
    }

    public IfThenElseExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterIfThenElseExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitIfThenElseExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitIfThenElseExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ExprTypeExprContext extends ExprContext {
    public Expr_typeContext expr_type() {
      return getRuleContext(Expr_typeContext.class, 0);
    }

    public ExprTypeExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterExprTypeExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitExprTypeExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitExprTypeExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class DivExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode DIV_TOKEN() {
      return getToken(SnapiParser.DIV_TOKEN, 0);
    }

    public DivExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterDivExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitDivExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitDivExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class TrippleStringExprContext extends ExprContext {
    public TerminalNode START_TRIPLE_QUOTE() {
      return getToken(SnapiParser.START_TRIPLE_QUOTE, 0);
    }

    public TerminalNode TRIPLE_QUOTE_END_2() {
      return getToken(SnapiParser.TRIPLE_QUOTE_END_2, 0);
    }

    public TerminalNode TRIPLE_QUOTE_END_1() {
      return getToken(SnapiParser.TRIPLE_QUOTE_END_1, 0);
    }

    public TerminalNode TRIPLE_QUOTE_END_0() {
      return getToken(SnapiParser.TRIPLE_QUOTE_END_0, 0);
    }

    public List<TerminalNode> TRIPLE_QUOTED_STRING_CONTENT() {
      return getTokens(SnapiParser.TRIPLE_QUOTED_STRING_CONTENT);
    }

    public TerminalNode TRIPLE_QUOTED_STRING_CONTENT(int i) {
      return getToken(SnapiParser.TRIPLE_QUOTED_STRING_CONTENT, i);
    }

    public TrippleStringExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterTrippleStringExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitTrippleStringExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitTrippleStringExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ModExprContext extends ExprContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode MOD_TOKEN() {
      return getToken(SnapiParser.MOD_TOKEN, 0);
    }

    public ModExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterModExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitModExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitModExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ParenExprContext extends ExprContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public ParenExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterParenExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitParenExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitParenExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExprContext expr() throws RecognitionException {
    return expr(0);
  }

  private ExprContext expr(int _p) throws RecognitionException {
    ParserRuleContext _parentctx = _ctx;
    int _parentState = getState();
    ExprContext _localctx = new ExprContext(_ctx, _parentState);
    ExprContext _prevctx = _localctx;
    int _startState = 38;
    enterRecursionRule(_localctx, 38, RULE_expr, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(295);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 25, _ctx)) {
          case 1:
            {
              _localctx = new ParenExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;

              setState(266);
              match(LEFT_PAREN);
              setState(267);
              expr(0);
              setState(268);
              match(RIGHT_PAREN);
            }
            break;
          case 2:
            {
              _localctx = new LetExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(270);
              let();
            }
            break;
          case 3:
            {
              _localctx = new FunAbsExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(271);
              fun_abs();
            }
            break;
          case 4:
            {
              _localctx = new ExprTypeExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(272);
              expr_type();
            }
            break;
          case 5:
            {
              _localctx = new IfThenElseExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(273);
              if_then_else();
            }
            break;
          case 6:
            {
              _localctx = new NumberExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(274);
              number();
            }
            break;
          case 7:
            {
              _localctx = new BoolConstExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(275);
              bool_const();
            }
            break;
          case 8:
            {
              _localctx = new NullExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(276);
              match(NULL_TOKEN);
            }
            break;
          case 9:
            {
              _localctx = new TrippleStringExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(277);
              match(START_TRIPLE_QUOTE);
              setState(281);
              _errHandler.sync(this);
              _la = _input.LA(1);
              while (_la == TRIPLE_QUOTED_STRING_CONTENT) {
                {
                  {
                    setState(278);
                    match(TRIPLE_QUOTED_STRING_CONTENT);
                  }
                }
                setState(283);
                _errHandler.sync(this);
                _la = _input.LA(1);
              }
              setState(284);
              _la = _input.LA(1);
              if (!(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & 7L) != 0))) {
                _errHandler.recoverInline(this);
              } else {
                if (_input.LA(1) == Token.EOF) matchedEOF = true;
                _errHandler.reportMatch(this);
                consume();
              }
            }
            break;
          case 10:
            {
              _localctx = new StringExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(285);
              match(STRING);
            }
            break;
          case 11:
            {
              _localctx = new IdentExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(286);
              ident();
            }
            break;
          case 12:
            {
              _localctx = new ListExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(287);
              lists();
            }
            break;
          case 13:
            {
              _localctx = new RecordExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(288);
              records();
            }
            break;
          case 14:
            {
              _localctx = new MinusUnaryExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(289);
              match(MINUS_TOKEN);
              setState(290);
              expr(11);
            }
            break;
          case 15:
            {
              _localctx = new PlusUnaryExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(291);
              match(PLUS_TOKEN);
              setState(292);
              expr(10);
            }
            break;
          case 16:
            {
              _localctx = new NotExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(293);
              match(NOT_TOKEN);
              setState(294);
              expr(3);
            }
            break;
        }
        _ctx.stop = _input.LT(-1);
        setState(332);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 28, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            if (_parseListeners != null) triggerExitRuleEvent();
            _prevctx = _localctx;
            {
              setState(330);
              _errHandler.sync(this);
              switch (getInterpreter().adaptivePredict(_input, 27, _ctx)) {
                case 1:
                  {
                    _localctx = new DivExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(297);
                    if (!(precpred(_ctx, 9)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                    setState(298);
                    match(DIV_TOKEN);
                    setState(299);
                    expr(10);
                  }
                  break;
                case 2:
                  {
                    _localctx = new MulExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(300);
                    if (!(precpred(_ctx, 8)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                    setState(301);
                    match(MUL_TOKEN);
                    setState(302);
                    expr(9);
                  }
                  break;
                case 3:
                  {
                    _localctx = new ModExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(303);
                    if (!(precpred(_ctx, 7)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                    setState(304);
                    match(MOD_TOKEN);
                    setState(305);
                    expr(8);
                  }
                  break;
                case 4:
                  {
                    _localctx = new PlusExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(306);
                    if (!(precpred(_ctx, 6)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                    setState(307);
                    match(PLUS_TOKEN);
                    setState(308);
                    expr(7);
                  }
                  break;
                case 5:
                  {
                    _localctx = new MinusExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(309);
                    if (!(precpred(_ctx, 5)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                    setState(310);
                    match(MINUS_TOKEN);
                    setState(311);
                    expr(6);
                  }
                  break;
                case 6:
                  {
                    _localctx = new CompareExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(312);
                    if (!(precpred(_ctx, 4)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                    setState(313);
                    compare_tokens();
                    setState(314);
                    expr(5);
                  }
                  break;
                case 7:
                  {
                    _localctx = new AndExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(316);
                    if (!(precpred(_ctx, 2)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                    setState(317);
                    match(AND_TOKEN);
                    setState(318);
                    expr(3);
                  }
                  break;
                case 8:
                  {
                    _localctx = new OrExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(319);
                    if (!(precpred(_ctx, 1)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    setState(320);
                    match(OR_TOKEN);
                    setState(321);
                    expr(2);
                  }
                  break;
                case 9:
                  {
                    _localctx = new FunAppExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(322);
                    if (!(precpred(_ctx, 15)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 15)");
                    setState(323);
                    fun_ar();
                  }
                  break;
                case 10:
                  {
                    _localctx =
                        new ProjectionExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(324);
                    if (!(precpred(_ctx, 12)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                    setState(325);
                    match(DOT);
                    setState(326);
                    ident();
                    setState(328);
                    _errHandler.sync(this);
                    switch (getInterpreter().adaptivePredict(_input, 26, _ctx)) {
                      case 1:
                        {
                          setState(327);
                          fun_ar();
                        }
                        break;
                    }
                  }
                  break;
              }
            }
          }
          setState(334);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 28, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      unrollRecursionContexts(_parentctx);
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class LetContext extends ParserRuleContext {
    public TerminalNode LET_TOKEN() {
      return getToken(SnapiParser.LET_TOKEN, 0);
    }

    public Let_leftContext let_left() {
      return getRuleContext(Let_leftContext.class, 0);
    }

    public TerminalNode IN_TOKEN() {
      return getToken(SnapiParser.IN_TOKEN, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public LetContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_let;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).enterLet(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).exitLet(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLet(this);
      else return visitor.visitChildren(this);
    }
  }

  public final LetContext let() throws RecognitionException {
    LetContext _localctx = new LetContext(_ctx, getState());
    enterRule(_localctx, 40, RULE_let);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(335);
        match(LET_TOKEN);
        setState(336);
        let_left();
        setState(337);
        match(IN_TOKEN);
        setState(338);
        expr(0);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Let_leftContext extends ParserRuleContext {
    public List<Let_declContext> let_decl() {
      return getRuleContexts(Let_declContext.class);
    }

    public Let_declContext let_decl(int i) {
      return getRuleContext(Let_declContext.class, i);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Let_leftContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_let_left;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLet_left(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLet_left(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLet_left(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Let_leftContext let_left() throws RecognitionException {
    Let_leftContext _localctx = new Let_leftContext(_ctx, getState());
    enterRule(_localctx, 42, RULE_let_left);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(340);
        let_decl();
        setState(345);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == COMMA) {
          {
            {
              setState(341);
              match(COMMA);
              setState(342);
              let_decl();
            }
          }
          setState(347);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Let_declContext extends ParserRuleContext {
    public Let_bindContext let_bind() {
      return getRuleContext(Let_bindContext.class, 0);
    }

    public Fun_decContext fun_dec() {
      return getRuleContext(Fun_decContext.class, 0);
    }

    public Let_declContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_let_decl;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLet_decl(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLet_decl(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLet_decl(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Let_declContext let_decl() throws RecognitionException {
    Let_declContext _localctx = new Let_declContext(_ctx, getState());
    enterRule(_localctx, 44, RULE_let_decl);
    try {
      setState(350);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 30, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(348);
            let_bind();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(349);
            fun_dec();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Let_bindContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TerminalNode EQUALS() {
      return getToken(SnapiParser.EQUALS, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public Let_bindContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_let_bind;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLet_bind(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLet_bind(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLet_bind(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Let_bindContext let_bind() throws RecognitionException {
    Let_bindContext _localctx = new Let_bindContext(_ctx, getState());
    enterRule(_localctx, 46, RULE_let_bind);
    try {
      setState(362);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 31, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(352);
            ident();
            setState(353);
            match(EQUALS);
            setState(354);
            expr(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(356);
            ident();
            setState(357);
            match(COLON);
            setState(358);
            tipe(0);
            setState(359);
            match(EQUALS);
            setState(360);
            expr(0);
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class If_then_elseContext extends ParserRuleContext {
    public TerminalNode IF_TOKEN() {
      return getToken(SnapiParser.IF_TOKEN, 0);
    }

    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public TerminalNode THEN_TOKEN() {
      return getToken(SnapiParser.THEN_TOKEN, 0);
    }

    public TerminalNode ELSE_TOKEN() {
      return getToken(SnapiParser.ELSE_TOKEN, 0);
    }

    public If_then_elseContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_if_then_else;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterIf_then_else(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitIf_then_else(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitIf_then_else(this);
      else return visitor.visitChildren(this);
    }
  }

  public final If_then_elseContext if_then_else() throws RecognitionException {
    If_then_elseContext _localctx = new If_then_elseContext(_ctx, getState());
    enterRule(_localctx, 48, RULE_if_then_else);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(364);
        match(IF_TOKEN);
        setState(365);
        expr(0);
        setState(366);
        match(THEN_TOKEN);
        setState(367);
        expr(0);
        setState(368);
        match(ELSE_TOKEN);
        setState(369);
        expr(0);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class ListsContext extends ParserRuleContext {
    public TerminalNode LEFT_SQ_BR() {
      return getToken(SnapiParser.LEFT_SQ_BR, 0);
    }

    public TerminalNode RIGHT_SQ_BR() {
      return getToken(SnapiParser.RIGHT_SQ_BR, 0);
    }

    public Lists_elementContext lists_element() {
      return getRuleContext(Lists_elementContext.class, 0);
    }

    public ListsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_lists;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLists(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).exitLists(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLists(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ListsContext lists() throws RecognitionException {
    ListsContext _localctx = new ListsContext(_ctx, getState());
    enterRule(_localctx, 50, RULE_lists);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(371);
        match(LEFT_SQ_BR);
        setState(373);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 719476565759492094L) != 0)
            || _la == LEFT_CUR_BR
            || _la == LEFT_SQ_BR) {
          {
            setState(372);
            lists_element();
          }
        }

        setState(375);
        match(RIGHT_SQ_BR);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Lists_elementContext extends ParserRuleContext {
    public List<ExprContext> expr() {
      return getRuleContexts(ExprContext.class);
    }

    public ExprContext expr(int i) {
      return getRuleContext(ExprContext.class, i);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Lists_elementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_lists_element;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLists_element(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLists_element(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLists_element(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Lists_elementContext lists_element() throws RecognitionException {
    Lists_elementContext _localctx = new Lists_elementContext(_ctx, getState());
    enterRule(_localctx, 52, RULE_lists_element);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(377);
        expr(0);
        setState(382);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == COMMA) {
          {
            {
              setState(378);
              match(COMMA);
              setState(379);
              expr(0);
            }
          }
          setState(384);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class RecordsContext extends ParserRuleContext {
    public TerminalNode LEFT_CUR_BR() {
      return getToken(SnapiParser.LEFT_CUR_BR, 0);
    }

    public TerminalNode RIGHT_CUR_BR() {
      return getToken(SnapiParser.RIGHT_CUR_BR, 0);
    }

    public Record_elementsContext record_elements() {
      return getRuleContext(Record_elementsContext.class, 0);
    }

    public RecordsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_records;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecords(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecords(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecords(this);
      else return visitor.visitChildren(this);
    }
  }

  public final RecordsContext records() throws RecognitionException {
    RecordsContext _localctx = new RecordsContext(_ctx, getState());
    enterRule(_localctx, 54, RULE_records);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(385);
        match(LEFT_CUR_BR);
        setState(387);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 719476565759492094L) != 0)
            || _la == LEFT_CUR_BR
            || _la == LEFT_SQ_BR) {
          {
            setState(386);
            record_elements();
          }
        }

        setState(389);
        match(RIGHT_CUR_BR);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Record_elementsContext extends ParserRuleContext {
    public List<Record_elementContext> record_element() {
      return getRuleContexts(Record_elementContext.class);
    }

    public Record_elementContext record_element(int i) {
      return getRuleContext(Record_elementContext.class, i);
    }

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Record_elementsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_record_elements;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecord_elements(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecord_elements(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecord_elements(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Record_elementsContext record_elements() throws RecognitionException {
    Record_elementsContext _localctx = new Record_elementsContext(_ctx, getState());
    enterRule(_localctx, 56, RULE_record_elements);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(391);
        record_element();
        setState(396);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == COMMA) {
          {
            {
              setState(392);
              match(COMMA);
              setState(393);
              record_element();
            }
          }
          setState(398);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Record_elementContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public Record_elementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_record_element;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterRecord_element(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitRecord_element(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitRecord_element(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Record_elementContext record_element() throws RecognitionException {
    Record_elementContext _localctx = new Record_elementContext(_ctx, getState());
    enterRule(_localctx, 58, RULE_record_element);
    try {
      setState(404);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 36, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(399);
            ident();
            setState(400);
            match(COLON);
            setState(401);
            expr(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(403);
            expr(0);
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class NumberContext extends ParserRuleContext {
    public TerminalNode BYTE() {
      return getToken(SnapiParser.BYTE, 0);
    }

    public TerminalNode SHORT() {
      return getToken(SnapiParser.SHORT, 0);
    }

    public TerminalNode INTEGER() {
      return getToken(SnapiParser.INTEGER, 0);
    }

    public TerminalNode LONG() {
      return getToken(SnapiParser.LONG, 0);
    }

    public TerminalNode FLOAT() {
      return getToken(SnapiParser.FLOAT, 0);
    }

    public TerminalNode DECIMAL() {
      return getToken(SnapiParser.DECIMAL, 0);
    }

    public TerminalNode DOUBLE() {
      return getToken(SnapiParser.DOUBLE, 0);
    }

    public NumberContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_number;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNumber(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNumber(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNumber(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NumberContext number() throws RecognitionException {
    NumberContext _localctx = new NumberContext(_ctx, getState());
    enterRule(_localctx, 60, RULE_number);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(406);
        _la = _input.LA(1);
        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 136365211648L) != 0))) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Primitive_typesContext extends ParserRuleContext {
    public TerminalNode BOOL_TOKEN() {
      return getToken(SnapiParser.BOOL_TOKEN, 0);
    }

    public TerminalNode STRING_TOKEN() {
      return getToken(SnapiParser.STRING_TOKEN, 0);
    }

    public TerminalNode LOCATION_TOKEN() {
      return getToken(SnapiParser.LOCATION_TOKEN, 0);
    }

    public TerminalNode BINARY_TOKEN() {
      return getToken(SnapiParser.BINARY_TOKEN, 0);
    }

    public TerminalNode DATE_TOKEN() {
      return getToken(SnapiParser.DATE_TOKEN, 0);
    }

    public TerminalNode TIME_TOKEN() {
      return getToken(SnapiParser.TIME_TOKEN, 0);
    }

    public TerminalNode INTERVAL_TOKEN() {
      return getToken(SnapiParser.INTERVAL_TOKEN, 0);
    }

    public TerminalNode TIMESTAMP_TOKEN() {
      return getToken(SnapiParser.TIMESTAMP_TOKEN, 0);
    }

    public TerminalNode BYTE_TOKEN() {
      return getToken(SnapiParser.BYTE_TOKEN, 0);
    }

    public TerminalNode SHORT_TOKEN() {
      return getToken(SnapiParser.SHORT_TOKEN, 0);
    }

    public TerminalNode INT_TOKEN() {
      return getToken(SnapiParser.INT_TOKEN, 0);
    }

    public TerminalNode LONG_TOKEN() {
      return getToken(SnapiParser.LONG_TOKEN, 0);
    }

    public TerminalNode FLOAT_TOKEN() {
      return getToken(SnapiParser.FLOAT_TOKEN, 0);
    }

    public TerminalNode DOUBLE_TOKEN() {
      return getToken(SnapiParser.DOUBLE_TOKEN, 0);
    }

    public TerminalNode DECIMAL_TOKEN() {
      return getToken(SnapiParser.DECIMAL_TOKEN, 0);
    }

    public TerminalNode UNDEFINED_TOKEN() {
      return getToken(SnapiParser.UNDEFINED_TOKEN, 0);
    }

    public Primitive_typesContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_primitive_types;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterPrimitive_types(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitPrimitive_types(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitPrimitive_types(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Primitive_typesContext primitive_types() throws RecognitionException {
    Primitive_typesContext _localctx = new Primitive_typesContext(_ctx, getState());
    enterRule(_localctx, 62, RULE_primitive_types);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(408);
        _la = _input.LA(1);
        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 33685500L) != 0))) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Compare_tokensContext extends ParserRuleContext {
    public TerminalNode EQ_TOKEN() {
      return getToken(SnapiParser.EQ_TOKEN, 0);
    }

    public TerminalNode NEQ_TOKEN() {
      return getToken(SnapiParser.NEQ_TOKEN, 0);
    }

    public TerminalNode LE_TOKEN() {
      return getToken(SnapiParser.LE_TOKEN, 0);
    }

    public TerminalNode LT_TOKEN() {
      return getToken(SnapiParser.LT_TOKEN, 0);
    }

    public TerminalNode GE_TOKEN() {
      return getToken(SnapiParser.GE_TOKEN, 0);
    }

    public TerminalNode GT_TOKEN() {
      return getToken(SnapiParser.GT_TOKEN, 0);
    }

    public Compare_tokensContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_compare_tokens;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterCompare_tokens(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitCompare_tokens(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitCompare_tokens(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Compare_tokensContext compare_tokens() throws RecognitionException {
    Compare_tokensContext _localctx = new Compare_tokensContext(_ctx, getState());
    enterRule(_localctx, 64, RULE_compare_tokens);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(410);
        _la = _input.LA(1);
        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 8658654068736L) != 0))) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class Bool_constContext extends ParserRuleContext {
    public TerminalNode TRUE_TOKEN() {
      return getToken(SnapiParser.TRUE_TOKEN, 0);
    }

    public TerminalNode FALSE_TOKEN() {
      return getToken(SnapiParser.FALSE_TOKEN, 0);
    }

    public Bool_constContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_bool_const;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterBool_const(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitBool_const(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitBool_const(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Bool_constContext bool_const() throws RecognitionException {
    Bool_constContext _localctx = new Bool_constContext(_ctx, getState());
    enterRule(_localctx, 66, RULE_bool_const);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(412);
        _la = _input.LA(1);
        if (!(_la == TRUE_TOKEN || _la == FALSE_TOKEN)) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  @SuppressWarnings("CheckReturnValue")
  public static class IdentContext extends ParserRuleContext {
    public TerminalNode NON_ESC_IDENTIFIER() {
      return getToken(SnapiParser.NON_ESC_IDENTIFIER, 0);
    }

    public TerminalNode ESC_IDENTIFIER() {
      return getToken(SnapiParser.ESC_IDENTIFIER, 0);
    }

    public Primitive_typesContext primitive_types() {
      return getRuleContext(Primitive_typesContext.class, 0);
    }

    public TerminalNode LIST_TOKEN() {
      return getToken(SnapiParser.LIST_TOKEN, 0);
    }

    public TerminalNode RECORD_TOKEN() {
      return getToken(SnapiParser.RECORD_TOKEN, 0);
    }

    public TerminalNode COLLECTION_TOKEN() {
      return getToken(SnapiParser.COLLECTION_TOKEN, 0);
    }

    public IdentContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_ident;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterIdent(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener) ((SnapiParserListener) listener).exitIdent(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitIdent(this);
      else return visitor.visitChildren(this);
    }
  }

  public final IdentContext ident() throws RecognitionException {
    IdentContext _localctx = new IdentContext(_ctx, getState());
    enterRule(_localctx, 68, RULE_ident);
    try {
      setState(420);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case NON_ESC_IDENTIFIER:
          enterOuterAlt(_localctx, 1);
          {
            setState(414);
            match(NON_ESC_IDENTIFIER);
          }
          break;
        case ESC_IDENTIFIER:
          enterOuterAlt(_localctx, 2);
          {
            setState(415);
            match(ESC_IDENTIFIER);
          }
          break;
        case BOOL_TOKEN:
        case STRING_TOKEN:
        case LOCATION_TOKEN:
        case BINARY_TOKEN:
        case BYTE_TOKEN:
        case SHORT_TOKEN:
        case INT_TOKEN:
        case LONG_TOKEN:
        case FLOAT_TOKEN:
        case DOUBLE_TOKEN:
        case DECIMAL_TOKEN:
        case DATE_TOKEN:
        case TIME_TOKEN:
        case INTERVAL_TOKEN:
        case TIMESTAMP_TOKEN:
        case UNDEFINED_TOKEN:
          enterOuterAlt(_localctx, 3);
          {
            setState(416);
            primitive_types();
          }
          break;
        case LIST_TOKEN:
          enterOuterAlt(_localctx, 4);
          {
            setState(417);
            match(LIST_TOKEN);
          }
          break;
        case RECORD_TOKEN:
          enterOuterAlt(_localctx, 5);
          {
            setState(418);
            match(RECORD_TOKEN);
          }
          break;
        case COLLECTION_TOKEN:
          enterOuterAlt(_localctx, 6);
          {
            setState(419);
            match(COLLECTION_TOKEN);
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
    switch (ruleIndex) {
      case 13:
        return tipe_sempred((TipeContext) _localctx, predIndex);
      case 19:
        return expr_sempred((ExprContext) _localctx, predIndex);
    }
    return true;
  }

  private boolean tipe_sempred(TipeContext _localctx, int predIndex) {
    switch (predIndex) {
      case 0:
        return precpred(_ctx, 10);
      case 1:
        return precpred(_ctx, 2);
      case 2:
        return precpred(_ctx, 9);
    }
    return true;
  }

  private boolean expr_sempred(ExprContext _localctx, int predIndex) {
    switch (predIndex) {
      case 3:
        return precpred(_ctx, 9);
      case 4:
        return precpred(_ctx, 8);
      case 5:
        return precpred(_ctx, 7);
      case 6:
        return precpred(_ctx, 6);
      case 7:
        return precpred(_ctx, 5);
      case 8:
        return precpred(_ctx, 4);
      case 9:
        return precpred(_ctx, 2);
      case 10:
        return precpred(_ctx, 1);
      case 11:
        return precpred(_ctx, 15);
      case 12:
        return precpred(_ctx, 12);
    }
    return true;
  }

  public static final String _serializedATN =
      "\u0004\u0001I\u01a7\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"
          + "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"
          + "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"
          + "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"
          + "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"
          + "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"
          + "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"
          + "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"
          + "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"
          + "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"
          + "\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0001"
          + "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001K\b\u0001\n\u0001"
          + "\f\u0001N\t\u0001\u0001\u0001\u0005\u0001Q\b\u0001\n\u0001\f\u0001T\t"
          + "\u0001\u0001\u0001\u0003\u0001W\b\u0001\u0001\u0002\u0001\u0002\u0001"
          + "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"
          + "\u0003\u0001\u0003\u0003\u0003c\b\u0003\u0001\u0004\u0001\u0004\u0001"
          + "\u0004\u0001\u0004\u0005\u0004i\b\u0004\n\u0004\f\u0004l\t\u0004\u0003"
          + "\u0004n\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004s\b\u0004"
          + "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"
          + "\u0001\u0005\u0001\u0005\u0003\u0005}\b\u0005\u0001\u0006\u0001\u0006"
          + "\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u0084\b\u0006\u0001\u0007"
          + "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0003\b\u008c\b\b"
          + "\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u0093\b\t\n\t\f\t\u0096"
          + "\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u009d\b\n\u0001\u000b"
          + "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00a4\b\u000b"
          + "\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u00aa\b\f\n\f\f\f\u00ad\t\f\u0003"
          + "\f\u00af\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u00b4\b\f\u0001\f\u0001\f"
          + "\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"
          + "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00c6\b\r\u0001\r\u0001"
          + "\r\u0001\r\u0003\r\u00cb\b\r\u0005\r\u00cd\b\r\n\r\f\r\u00d0\t\r\u0001"
          + "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00d7\b\r\u0001\r\u0001\r\u0001"
          + "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"
          + "\r\u0005\r\u00e5\b\r\n\r\f\r\u00e8\t\r\u0001\u000e\u0001\u000e\u0001\u000e"
          + "\u0001\u000e\u0001\u000e\u0003\u000e\u00ef\b\u000e\u0001\u000f\u0001\u000f"
          + "\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u00f6\b\u000f\n\u000f"
          + "\f\u000f\u00f9\t\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"
          + "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011"
          + "\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0005\u0013\u0118\b\u0013\n\u0013\f\u0013\u011b\t\u0013\u0001"
          + "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"
          + "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u0128"
          + "\b\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"
          + "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"
          + "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"
          + "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"
          + "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"
          + "\u0013\u0001\u0013\u0003\u0013\u0149\b\u0013\u0005\u0013\u014b\b\u0013"
          + "\n\u0013\f\u0013\u014e\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"
          + "\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u0158"
          + "\b\u0015\n\u0015\f\u0015\u015b\t\u0015\u0001\u0016\u0001\u0016\u0003\u0016"
          + "\u015f\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"
          + "\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017"
          + "\u016b\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"
          + "\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0003\u0019\u0176\b\u0019"
          + "\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a"
          + "\u017d\b\u001a\n\u001a\f\u001a\u0180\t\u001a\u0001\u001b\u0001\u001b\u0003"
          + "\u001b\u0184\b\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001"
          + "\u001c\u0005\u001c\u018b\b\u001c\n\u001c\f\u001c\u018e\t\u001c\u0001\u001d"
          + "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0195\b\u001d"
          + "\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!"
          + "\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0003\"\u01a5"
          + "\b\"\u0001\"\u0000\u0002\u001a&#\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"
          + "\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BD\u0000\u0005"
          + "\u0001\u0000GI\u0001\u0000\u001e$\u0002\u0000\u0002\u0010\u0019\u0019"
          + "\u0001\u0000%*\u0001\u000034\u01ca\u0000F\u0001\u0000\u0000\u0000\u0002"
          + "V\u0001\u0000\u0000\u0000\u0004X\u0001\u0000\u0000\u0000\u0006b\u0001"
          + "\u0000\u0000\u0000\bd\u0001\u0000\u0000\u0000\n|\u0001\u0000\u0000\u0000"
          + "\f\u0083\u0001\u0000\u0000\u0000\u000e\u0085\u0001\u0000\u0000\u0000\u0010"
          + "\u0089\u0001\u0000\u0000\u0000\u0012\u008f\u0001\u0000\u0000\u0000\u0014"
          + "\u009c\u0001\u0000\u0000\u0000\u0016\u00a3\u0001\u0000\u0000\u0000\u0018"
          + "\u00a5\u0001\u0000\u0000\u0000\u001a\u00d6\u0001\u0000\u0000\u0000\u001c"
          + "\u00ee\u0001\u0000\u0000\u0000\u001e\u00f0\u0001\u0000\u0000\u0000 \u00fc"
          + "\u0001\u0000\u0000\u0000\"\u0101\u0001\u0000\u0000\u0000$\u0106\u0001"
          + "\u0000\u0000\u0000&\u0127\u0001\u0000\u0000\u0000(\u014f\u0001\u0000\u0000"
          + "\u0000*\u0154\u0001\u0000\u0000\u0000,\u015e\u0001\u0000\u0000\u0000."
          + "\u016a\u0001\u0000\u0000\u00000\u016c\u0001\u0000\u0000\u00002\u0173\u0001"
          + "\u0000\u0000\u00004\u0179\u0001\u0000\u0000\u00006\u0181\u0001\u0000\u0000"
          + "\u00008\u0187\u0001\u0000\u0000\u0000:\u0194\u0001\u0000\u0000\u0000<"
          + "\u0196\u0001\u0000\u0000\u0000>\u0198\u0001\u0000\u0000\u0000@\u019a\u0001"
          + "\u0000\u0000\u0000B\u019c\u0001\u0000\u0000\u0000D\u01a4\u0001\u0000\u0000"
          + "\u0000FG\u0003\u0002\u0001\u0000GH\u0005\u0000\u0000\u0001H\u0001\u0001"
          + "\u0000\u0000\u0000IK\u0003\u0004\u0002\u0000JI\u0001\u0000\u0000\u0000"
          + "KN\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000"
          + "\u0000MW\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000OQ\u0003\u0004"
          + "\u0002\u0000PO\u0001\u0000\u0000\u0000QT\u0001\u0000\u0000\u0000RP\u0001"
          + "\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SU\u0001\u0000\u0000\u0000"
          + "TR\u0001\u0000\u0000\u0000UW\u0003&\u0013\u0000VL\u0001\u0000\u0000\u0000"
          + "VR\u0001\u0000\u0000\u0000W\u0003\u0001\u0000\u0000\u0000XY\u0003D\"\u0000"
          + "YZ\u0003\b\u0004\u0000Z\u0005\u0001\u0000\u0000\u0000[\\\u0003D\"\u0000"
          + "\\]\u0003\b\u0004\u0000]c\u0001\u0000\u0000\u0000^_\u0005\u0018\u0000"
          + "\u0000_`\u0003D\"\u0000`a\u0003\b\u0004\u0000ac\u0001\u0000\u0000\u0000"
          + "b[\u0001\u0000\u0000\u0000b^\u0001\u0000\u0000\u0000c\u0007\u0001\u0000"
          + "\u0000\u0000dm\u0005;\u0000\u0000ej\u0003\n\u0005\u0000fg\u0005>\u0000"
          + "\u0000gi\u0003\n\u0005\u0000hf\u0001\u0000\u0000\u0000il\u0001\u0000\u0000"
          + "\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kn\u0001\u0000"
          + "\u0000\u0000lj\u0001\u0000\u0000\u0000me\u0001\u0000\u0000\u0000mn\u0001"
          + "\u0000\u0000\u0000no\u0001\u0000\u0000\u0000or\u0005<\u0000\u0000pq\u0005"
          + "=\u0000\u0000qs\u0003\u001a\r\u0000rp\u0001\u0000\u0000\u0000rs\u0001"
          + "\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tu\u0005?\u0000\u0000uv\u0003"
          + "&\u0013\u0000v\t\u0001\u0000\u0000\u0000w}\u0003\f\u0006\u0000xy\u0003"
          + "\f\u0006\u0000yz\u0005?\u0000\u0000z{\u0003&\u0013\u0000{}\u0001\u0000"
          + "\u0000\u0000|w\u0001\u0000\u0000\u0000|x\u0001\u0000\u0000\u0000}\u000b"
          + "\u0001\u0000\u0000\u0000~\u007f\u0003D\"\u0000\u007f\u0080\u0005=\u0000"
          + "\u0000\u0080\u0081\u0003\u001a\r\u0000\u0081\u0084\u0001\u0000\u0000\u0000"
          + "\u0082\u0084\u0003D\"\u0000\u0083~\u0001\u0000\u0000\u0000\u0083\u0082"
          + "\u0001\u0000\u0000\u0000\u0084\r\u0001\u0000\u0000\u0000\u0085\u0086\u0003"
          + "D\"\u0000\u0086\u0087\u0005=\u0000\u0000\u0087\u0088\u0003\u001a\r\u0000"
          + "\u0088\u000f\u0001\u0000\u0000\u0000\u0089\u008b\u0005;\u0000\u0000\u008a"
          + "\u008c\u0003\u0012\t\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008b\u008c"
          + "\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e"
          + "\u0005<\u0000\u0000\u008e\u0011\u0001\u0000\u0000\u0000\u008f\u0094\u0003"
          + "\u0014\n\u0000\u0090\u0091\u0005>\u0000\u0000\u0091\u0093\u0003\u0014"
          + "\n\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0093\u0096\u0001\u0000\u0000"
          + "\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000"
          + "\u0000\u0095\u0013\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000\u0000"
          + "\u0000\u0097\u009d\u0003&\u0013\u0000\u0098\u0099\u0003D\"\u0000\u0099"
          + "\u009a\u0005?\u0000\u0000\u009a\u009b\u0003&\u0013\u0000\u009b\u009d\u0001"
          + "\u0000\u0000\u0000\u009c\u0097\u0001\u0000\u0000\u0000\u009c\u0098\u0001"
          + "\u0000\u0000\u0000\u009d\u0015\u0001\u0000\u0000\u0000\u009e\u00a4\u0003"
          + "\u0018\f\u0000\u009f\u00a0\u0003D\"\u0000\u00a0\u00a1\u0005@\u0000\u0000"
          + "\u00a1\u00a2\u0003&\u0013\u0000\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3"
          + "\u009e\u0001\u0000\u0000\u0000\u00a3\u009f\u0001\u0000\u0000\u0000\u00a4"
          + "\u0017\u0001\u0000\u0000\u0000\u00a5\u00ae\u0005;\u0000\u0000\u00a6\u00ab"
          + "\u0003\n\u0005\u0000\u00a7\u00a8\u0005>\u0000\u0000\u00a8\u00aa\u0003"
          + "\n\u0005\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00aa\u00ad\u0001\u0000"
          + "\u0000\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000"
          + "\u0000\u0000\u00ac\u00af\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001\u0000"
          + "\u0000\u0000\u00ae\u00a6\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000"
          + "\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u00b3\u0005<\u0000"
          + "\u0000\u00b1\u00b2\u0005=\u0000\u0000\u00b2\u00b4\u0003\u001a\r\u0000"
          + "\u00b3\u00b1\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000"
          + "\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005@\u0000\u0000\u00b6"
          + "\u00b7\u0003&\u0013\u0000\u00b7\u0019\u0001\u0000\u0000\u0000\u00b8\u00b9"
          + "\u0006\r\uffff\uffff\u0000\u00b9\u00ba\u0005;\u0000\u0000\u00ba\u00bb"
          + "\u0003\u001a\r\u0000\u00bb\u00bc\u0005<\u0000\u0000\u00bc\u00d7\u0001"
          + "\u0000\u0000\u0000\u00bd\u00d7\u0003>\u001f\u0000\u00be\u00d7\u0003\u001e"
          + "\u000f\u0000\u00bf\u00d7\u0003 \u0010\u0000\u00c0\u00d7\u0003\"\u0011"
          + "\u0000\u00c1\u00d7\u0003D\"\u0000\u00c2\u00c5\u0005;\u0000\u0000\u00c3"
          + "\u00c6\u0003\u001a\r\u0000\u00c4\u00c6\u0003\f\u0006\u0000\u00c5\u00c3"
          + "\u0001\u0000\u0000\u0000\u00c5\u00c4\u0001\u0000\u0000\u0000\u00c6\u00ce"
          + "\u0001\u0000\u0000\u0000\u00c7\u00ca\u0005>\u0000\u0000\u00c8\u00cb\u0003"
          + "\u001a\r\u0000\u00c9\u00cb\u0003\f\u0006\u0000\u00ca\u00c8\u0001\u0000"
          + "\u0000\u0000\u00ca\u00c9\u0001\u0000\u0000\u0000\u00cb\u00cd\u0001\u0000"
          + "\u0000\u0000\u00cc\u00c7\u0001\u0000\u0000\u0000\u00cd\u00d0\u0001\u0000"
          + "\u0000\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000"
          + "\u0000\u0000\u00cf\u00d1\u0001\u0000\u0000\u0000\u00d0\u00ce\u0001\u0000"
          + "\u0000\u0000\u00d1\u00d2\u0005<\u0000\u0000\u00d2\u00d3\u0005@\u0000\u0000"
          + "\u00d3\u00d4\u0003\u001a\r\u0003\u00d4\u00d7\u0001\u0000\u0000\u0000\u00d5"
          + "\u00d7\u0003$\u0012\u0000\u00d6\u00b8\u0001\u0000\u0000\u0000\u00d6\u00bd"
          + "\u0001\u0000\u0000\u0000\u00d6\u00be\u0001\u0000\u0000\u0000\u00d6\u00bf"
          + "\u0001\u0000\u0000\u0000\u00d6\u00c0\u0001\u0000\u0000\u0000\u00d6\u00c1"
          + "\u0001\u0000\u0000\u0000\u00d6\u00c2\u0001\u0000\u0000\u0000\u00d6\u00d5"
          + "\u0001\u0000\u0000\u0000\u00d7\u00e6\u0001\u0000\u0000\u0000\u00d8\u00d9"
          + "\n\n\u0000\u0000\u00d9\u00da\u00051\u0000\u0000\u00da\u00db\u0003\u001c"
          + "\u000e\u0000\u00db\u00dc\u0005@\u0000\u0000\u00dc\u00dd\u0003\u001a\r"
          + "\u000b\u00dd\u00e5\u0001\u0000\u0000\u0000\u00de\u00df\n\u0002\u0000\u0000"
          + "\u00df\u00e0\u0005@\u0000\u0000\u00e0\u00e5\u0003\u001a\r\u0003\u00e1"
          + "\u00e2\n\t\u0000\u0000\u00e2\u00e3\u00051\u0000\u0000\u00e3\u00e5\u0003"
          + "\u001c\u000e\u0000\u00e4\u00d8\u0001\u0000\u0000\u0000\u00e4\u00de\u0001"
          + "\u0000\u0000\u0000\u00e4\u00e1\u0001\u0000\u0000\u0000\u00e5\u00e8\u0001"
          + "\u0000\u0000\u0000\u00e6\u00e4\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001"
          + "\u0000\u0000\u0000\u00e7\u001b\u0001\u0000\u0000\u0000\u00e8\u00e6\u0001"
          + "\u0000\u0000\u0000\u00e9\u00ea\u0003\u001a\r\u0000\u00ea\u00eb\u00051"
          + "\u0000\u0000\u00eb\u00ec\u0003\u001c\u000e\u0000\u00ec\u00ef\u0001\u0000"
          + "\u0000\u0000\u00ed\u00ef\u0003\u001a\r\u0000\u00ee\u00e9\u0001\u0000\u0000"
          + "\u0000\u00ee\u00ed\u0001\u0000\u0000\u0000\u00ef\u001d\u0001\u0000\u0000"
          + "\u0000\u00f0\u00f1\u0005\u0011\u0000\u0000\u00f1\u00f2\u0005;\u0000\u0000"
          + "\u00f2\u00f7\u0003\u000e\u0007\u0000\u00f3\u00f4\u0005>\u0000\u0000\u00f4"
          + "\u00f6\u0003\u000e\u0007\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000\u00f6"
          + "\u00f9\u0001\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000\u00f7"
          + "\u00f8\u0001\u0000\u0000\u0000\u00f8\u00fa\u0001\u0000\u0000\u0000\u00f9"
          + "\u00f7\u0001\u0000\u0000\u0000\u00fa\u00fb\u0005<\u0000\u0000\u00fb\u001f"
          + "\u0001\u0000\u0000\u0000\u00fc\u00fd\u0005\u0012\u0000\u0000\u00fd\u00fe"
          + "\u0005;\u0000\u0000\u00fe\u00ff\u0003\u001a\r\u0000\u00ff\u0100\u0005"
          + "<\u0000\u0000\u0100!\u0001\u0000\u0000\u0000\u0101\u0102\u0005\u0013\u0000"
          + "\u0000\u0102\u0103\u0005;\u0000\u0000\u0103\u0104\u0003\u001a\r\u0000"
          + "\u0104\u0105\u0005<\u0000\u0000\u0105#\u0001\u0000\u0000\u0000\u0106\u0107"
          + "\u0005\u0001\u0000\u0000\u0107\u0108\u0003\u001a\r\u0000\u0108%\u0001"
          + "\u0000\u0000\u0000\u0109\u010a\u0006\u0013\uffff\uffff\u0000\u010a\u010b"
          + "\u0005;\u0000\u0000\u010b\u010c\u0003&\u0013\u0000\u010c\u010d\u0005<"
          + "\u0000\u0000\u010d\u0128\u0001\u0000\u0000\u0000\u010e\u0128\u0003(\u0014"
          + "\u0000\u010f\u0128\u0003\u0016\u000b\u0000\u0110\u0128\u0003$\u0012\u0000"
          + "\u0111\u0128\u00030\u0018\u0000\u0112\u0128\u0003<\u001e\u0000\u0113\u0128"
          + "\u0003B!\u0000\u0114\u0128\u0005\u001d\u0000\u0000\u0115\u0119\u00056"
          + "\u0000\u0000\u0116\u0118\u0005F\u0000\u0000\u0117\u0116\u0001\u0000\u0000"
          + "\u0000\u0118\u011b\u0001\u0000\u0000\u0000\u0119\u0117\u0001\u0000\u0000"
          + "\u0000\u0119\u011a\u0001\u0000\u0000\u0000\u011a\u011c\u0001\u0000\u0000"
          + "\u0000\u011b\u0119\u0001\u0000\u0000\u0000\u011c\u0128\u0007\u0000\u0000"
          + "\u0000\u011d\u0128\u00055\u0000\u0000\u011e\u0128\u0003D\"\u0000\u011f"
          + "\u0128\u00032\u0019\u0000\u0120\u0128\u00036\u001b\u0000\u0121\u0122\u0005"
          + ",\u0000\u0000\u0122\u0128\u0003&\u0013\u000b\u0123\u0124\u0005+\u0000"
          + "\u0000\u0124\u0128\u0003&\u0013\n\u0125\u0126\u00052\u0000\u0000\u0126"
          + "\u0128\u0003&\u0013\u0003\u0127\u0109\u0001\u0000\u0000\u0000\u0127\u010e"
          + "\u0001\u0000\u0000\u0000\u0127\u010f\u0001\u0000\u0000\u0000\u0127\u0110"
          + "\u0001\u0000\u0000\u0000\u0127\u0111\u0001\u0000\u0000\u0000\u0127\u0112"
          + "\u0001\u0000\u0000\u0000\u0127\u0113\u0001\u0000\u0000\u0000\u0127\u0114"
          + "\u0001\u0000\u0000\u0000\u0127\u0115\u0001\u0000\u0000\u0000\u0127\u011d"
          + "\u0001\u0000\u0000\u0000\u0127\u011e\u0001\u0000\u0000\u0000\u0127\u011f"
          + "\u0001\u0000\u0000\u0000\u0127\u0120\u0001\u0000\u0000\u0000\u0127\u0121"
          + "\u0001\u0000\u0000\u0000\u0127\u0123\u0001\u0000\u0000\u0000\u0127\u0125"
          + "\u0001\u0000\u0000\u0000\u0128\u014c\u0001\u0000\u0000\u0000\u0129\u012a"
          + "\n\t\u0000\u0000\u012a\u012b\u0005.\u0000\u0000\u012b\u014b\u0003&\u0013"
          + "\n\u012c\u012d\n\b\u0000\u0000\u012d\u012e\u0005-\u0000\u0000\u012e\u014b"
          + "\u0003&\u0013\t\u012f\u0130\n\u0007\u0000\u0000\u0130\u0131\u0005/\u0000"
          + "\u0000\u0131\u014b\u0003&\u0013\b\u0132\u0133\n\u0006\u0000\u0000\u0133"
          + "\u0134\u0005+\u0000\u0000\u0134\u014b\u0003&\u0013\u0007\u0135\u0136\n"
          + "\u0005\u0000\u0000\u0136\u0137\u0005,\u0000\u0000\u0137\u014b\u0003&\u0013"
          + "\u0006\u0138\u0139\n\u0004\u0000\u0000\u0139\u013a\u0003@ \u0000\u013a"
          + "\u013b\u0003&\u0013\u0005\u013b\u014b\u0001\u0000\u0000\u0000\u013c\u013d"
          + "\n\u0002\u0000\u0000\u013d\u013e\u00050\u0000\u0000\u013e\u014b\u0003"
          + "&\u0013\u0003\u013f\u0140\n\u0001\u0000\u0000\u0140\u0141\u00051\u0000"
          + "\u0000\u0141\u014b\u0003&\u0013\u0002\u0142\u0143\n\u000f\u0000\u0000"
          + "\u0143\u014b\u0003\u0010\b\u0000\u0144\u0145\n\f\u0000\u0000\u0145\u0146"
          + "\u0005A\u0000\u0000\u0146\u0148\u0003D\"\u0000\u0147\u0149\u0003\u0010"
          + "\b\u0000\u0148\u0147\u0001\u0000\u0000\u0000\u0148\u0149\u0001\u0000\u0000"
          + "\u0000\u0149\u014b\u0001\u0000\u0000\u0000\u014a\u0129\u0001\u0000\u0000"
          + "\u0000\u014a\u012c\u0001\u0000\u0000\u0000\u014a\u012f\u0001\u0000\u0000"
          + "\u0000\u014a\u0132\u0001\u0000\u0000\u0000\u014a\u0135\u0001\u0000\u0000"
          + "\u0000\u014a\u0138\u0001\u0000\u0000\u0000\u014a\u013c\u0001\u0000\u0000"
          + "\u0000\u014a\u013f\u0001\u0000\u0000\u0000\u014a\u0142\u0001\u0000\u0000"
          + "\u0000\u014a\u0144\u0001\u0000\u0000\u0000\u014b\u014e\u0001\u0000\u0000"
          + "\u0000\u014c\u014a\u0001\u0000\u0000\u0000\u014c\u014d\u0001\u0000\u0000"
          + "\u0000\u014d\'\u0001\u0000\u0000\u0000\u014e\u014c\u0001\u0000\u0000\u0000"
          + "\u014f\u0150\u0005\u0016\u0000\u0000\u0150\u0151\u0003*\u0015\u0000\u0151"
          + "\u0152\u0005\u0017\u0000\u0000\u0152\u0153\u0003&\u0013\u0000\u0153)\u0001"
          + "\u0000\u0000\u0000\u0154\u0159\u0003,\u0016\u0000\u0155\u0156\u0005>\u0000"
          + "\u0000\u0156\u0158\u0003,\u0016\u0000\u0157\u0155\u0001\u0000\u0000\u0000"
          + "\u0158\u015b\u0001\u0000\u0000\u0000\u0159\u0157\u0001\u0000\u0000\u0000"
          + "\u0159\u015a\u0001\u0000\u0000\u0000\u015a+\u0001\u0000\u0000\u0000\u015b"
          + "\u0159\u0001\u0000\u0000\u0000\u015c\u015f\u0003.\u0017\u0000\u015d\u015f"
          + "\u0003\u0006\u0003\u0000\u015e\u015c\u0001\u0000\u0000\u0000\u015e\u015d"
          + "\u0001\u0000\u0000\u0000\u015f-\u0001\u0000\u0000\u0000\u0160\u0161\u0003"
          + "D\"\u0000\u0161\u0162\u0005?\u0000\u0000\u0162\u0163\u0003&\u0013\u0000"
          + "\u0163\u016b\u0001\u0000\u0000\u0000\u0164\u0165\u0003D\"\u0000\u0165"
          + "\u0166\u0005=\u0000\u0000\u0166\u0167\u0003\u001a\r\u0000\u0167\u0168"
          + "\u0005?\u0000\u0000\u0168\u0169\u0003&\u0013\u0000\u0169\u016b\u0001\u0000"
          + "\u0000\u0000\u016a\u0160\u0001\u0000\u0000\u0000\u016a\u0164\u0001\u0000"
          + "\u0000\u0000\u016b/\u0001\u0000\u0000\u0000\u016c\u016d\u0005\u001a\u0000"
          + "\u0000\u016d\u016e\u0003&\u0013\u0000\u016e\u016f\u0005\u001b\u0000\u0000"
          + "\u016f\u0170\u0003&\u0013\u0000\u0170\u0171\u0005\u001c\u0000\u0000\u0171"
          + "\u0172\u0003&\u0013\u0000\u01721\u0001\u0000\u0000\u0000\u0173\u0175\u0005"
          + "D\u0000\u0000\u0174\u0176\u00034\u001a\u0000\u0175\u0174\u0001\u0000\u0000"
          + "\u0000\u0175\u0176\u0001\u0000\u0000\u0000\u0176\u0177\u0001\u0000\u0000"
          + "\u0000\u0177\u0178\u0005E\u0000\u0000\u01783\u0001\u0000\u0000\u0000\u0179"
          + "\u017e\u0003&\u0013\u0000\u017a\u017b\u0005>\u0000\u0000\u017b\u017d\u0003"
          + "&\u0013\u0000\u017c\u017a\u0001\u0000\u0000\u0000\u017d\u0180\u0001\u0000"
          + "\u0000\u0000\u017e\u017c\u0001\u0000\u0000\u0000\u017e\u017f\u0001\u0000"
          + "\u0000\u0000\u017f5\u0001\u0000\u0000\u0000\u0180\u017e\u0001\u0000\u0000"
          + "\u0000\u0181\u0183\u0005B\u0000\u0000\u0182\u0184\u00038\u001c\u0000\u0183"
          + "\u0182\u0001\u0000\u0000\u0000\u0183\u0184\u0001\u0000\u0000\u0000\u0184"
          + "\u0185\u0001\u0000\u0000\u0000\u0185\u0186\u0005C\u0000\u0000\u01867\u0001"
          + "\u0000\u0000\u0000\u0187\u018c\u0003:\u001d\u0000\u0188\u0189\u0005>\u0000"
          + "\u0000\u0189\u018b\u0003:\u001d\u0000\u018a\u0188\u0001\u0000\u0000\u0000"
          + "\u018b\u018e\u0001\u0000\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000"
          + "\u018c\u018d\u0001\u0000\u0000\u0000\u018d9\u0001\u0000\u0000\u0000\u018e"
          + "\u018c\u0001\u0000\u0000\u0000\u018f\u0190\u0003D\"\u0000\u0190\u0191"
          + "\u0005=\u0000\u0000\u0191\u0192\u0003&\u0013\u0000\u0192\u0195\u0001\u0000"
          + "\u0000\u0000\u0193\u0195\u0003&\u0013\u0000\u0194\u018f\u0001\u0000\u0000"
          + "\u0000\u0194\u0193\u0001\u0000\u0000\u0000\u0195;\u0001\u0000\u0000\u0000"
          + "\u0196\u0197\u0007\u0001\u0000\u0000\u0197=\u0001\u0000\u0000\u0000\u0198"
          + "\u0199\u0007\u0002\u0000\u0000\u0199?\u0001\u0000\u0000\u0000\u019a\u019b"
          + "\u0007\u0003\u0000\u0000\u019bA\u0001\u0000\u0000\u0000\u019c\u019d\u0007"
          + "\u0004\u0000\u0000\u019dC\u0001\u0000\u0000\u0000\u019e\u01a5\u00057\u0000"
          + "\u0000\u019f\u01a5\u00058\u0000\u0000\u01a0\u01a5\u0003>\u001f\u0000\u01a1"
          + "\u01a5\u0005\u0013\u0000\u0000\u01a2\u01a5\u0005\u0011\u0000\u0000\u01a3"
          + "\u01a5\u0005\u0012\u0000\u0000\u01a4\u019e\u0001\u0000\u0000\u0000\u01a4"
          + "\u019f\u0001\u0000\u0000\u0000\u01a4\u01a0\u0001\u0000\u0000\u0000\u01a4"
          + "\u01a1\u0001\u0000\u0000\u0000\u01a4\u01a2\u0001\u0000\u0000\u0000\u01a4"
          + "\u01a3\u0001\u0000\u0000\u0000\u01a5E\u0001\u0000\u0000\u0000&LRVbjmr"
          + "|\u0083\u008b\u0094\u009c\u00a3\u00ab\u00ae\u00b3\u00c5\u00ca\u00ce\u00d6"
          + "\u00e4\u00e6\u00ee\u00f7\u0119\u0127\u0148\u014a\u014c\u0159\u015e\u016a"
          + "\u0175\u017e\u0183\u018c\u0194\u01a4";
  public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
