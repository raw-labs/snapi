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

// Generated from SnapiParser.g4 by ANTLR 4.13.0
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
    RuntimeMetaData.checkVersion("4.13.0", RuntimeMetaData.VERSION);
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
      NULLABLE_TOKEN = 70,
      TRYABLE_TOKEN = 71,
      DOLLAR_TOKEN = 72,
      TRIPLE_QUOTED_STRING_CONTENT = 73,
      TRIPLE_QUOTE_END_2 = 74,
      TRIPLE_QUOTE_END_1 = 75,
      TRIPLE_QUOTE_END_0 = 76;
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
      RULE_multiple_commas = 22,
      RULE_let_decl = 23,
      RULE_let_bind = 24,
      RULE_if_then_else = 25,
      RULE_lists = 26,
      RULE_lists_element = 27,
      RULE_records = 28,
      RULE_record_elements = 29,
      RULE_record_element = 30,
      RULE_signed_number = 31,
      RULE_number = 32,
      RULE_primitive_types = 33,
      RULE_string_literal = 34,
      RULE_triple_string_literal = 35,
      RULE_compare_tokens = 36,
      RULE_bool_const = 37,
      RULE_ident = 38,
      RULE_package_idn_exp = 39,
      RULE_nullable_tryable = 40;

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
      "multiple_commas",
      "let_decl",
      "let_bind",
      "if_then_else",
      "lists",
      "lists_element",
      "records",
      "record_elements",
      "record_element",
      "signed_number",
      "number",
      "primitive_types",
      "string_literal",
      "triple_string_literal",
      "compare_tokens",
      "bool_const",
      "ident",
      "package_idn_exp",
      "nullable_tryable"
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
      "'@null'",
      "'@try'",
      "'$'",
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
      "NULLABLE_TOKEN",
      "TRYABLE_TOKEN",
      "DOLLAR_TOKEN",
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
        setState(82);
        stat();
        setState(83);
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
      setState(98);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
        case 1:
          _localctx = new FunDecStatContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(88);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391091494908L) != 0)) {
              {
                {
                  setState(85);
                  method_dec();
                }
              }
              setState(90);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
          break;
        case 2:
          _localctx = new FunDecExprStatContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(94);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
            while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
              if (_alt == 1) {
                {
                  {
                    setState(91);
                    method_dec();
                  }
                }
              }
              setState(96);
              _errHandler.sync(this);
              _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
            }
            setState(97);
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
        setState(100);
        ident();
        setState(101);
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
      setState(110);
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
            setState(103);
            ident();
            setState(104);
            fun_proto();
          }
          break;
        case REC_TOKEN:
          _localctx = new RecFunContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(106);
            match(REC_TOKEN);
            setState(107);
            ident();
            setState(108);
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

    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public TerminalNode COLON() {
      return getToken(SnapiParser.COLON, 0);
    }

    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
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
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(112);
        match(LEFT_PAREN);
        setState(121);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391091494908L) != 0)) {
          {
            setState(113);
            fun_param();
            setState(118);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 4, _ctx);
            while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
              if (_alt == 1) {
                {
                  {
                    setState(114);
                    match(COMMA);
                    setState(115);
                    fun_param();
                  }
                }
              }
              setState(120);
              _errHandler.sync(this);
              _alt = getInterpreter().adaptivePredict(_input, 4, _ctx);
            }
          }
        }

        setState(124);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COMMA) {
          {
            setState(123);
            match(COMMA);
          }
        }

        setState(126);
        match(RIGHT_PAREN);
        setState(129);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COLON) {
          {
            setState(127);
            match(COLON);
            setState(128);
            tipe(0);
          }
        }

        setState(131);
        match(EQUALS);
        setState(132);
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
      setState(139);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
        case 1:
          _localctx = new FunParamAttrContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(134);
            attr();
          }
          break;
        case 2:
          _localctx = new FunParamAttrExprContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(135);
            attr();
            setState(136);
            match(EQUALS);
            setState(137);
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
      setState(146);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(141);
            ident();
            setState(142);
            match(COLON);
            setState(143);
            tipe(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(145);
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
        setState(148);
        ident();
        setState(149);
        match(COLON);
        setState(150);
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
        setState(152);
        match(LEFT_PAREN);
        setState(154);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 719476565759492094L) != 0)
            || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 69L) != 0)) {
          {
            setState(153);
            fun_args();
          }
        }

        setState(156);
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
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(158);
        fun_arg();
        setState(163);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(159);
                match(COMMA);
                setState(160);
                fun_arg();
              }
            }
          }
          setState(165);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
        }
        setState(167);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COMMA) {
          {
            setState(166);
            match(COMMA);
          }
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
      setState(174);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 13, _ctx)) {
        case 1:
          _localctx = new FunArgExprContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(169);
            expr(0);
          }
          break;
        case 2:
          _localctx = new NamedFunArgExprContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(170);
            ident();
            setState(171);
            match(EQUALS);
            setState(172);
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
      setState(181);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
        case 1:
          _localctx = new FunAbsContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(176);
            fun_proto_lambda();
          }
          break;
        case 2:
          _localctx = new FunAbsUnnamedContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(177);
            ident();
            setState(178);
            match(RIGHT_ARROW);
            setState(179);
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
  public static class Fun_proto_lambdaContext extends ParserRuleContext {
    public Fun_proto_lambdaContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fun_proto_lambda;
    }

    public Fun_proto_lambdaContext() {}

    public void copyFrom(Fun_proto_lambdaContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunProtoLambdaMultiParamContext extends Fun_proto_lambdaContext {
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

    public FunProtoLambdaMultiParamContext(Fun_proto_lambdaContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunProtoLambdaMultiParam(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunProtoLambdaMultiParam(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunProtoLambdaMultiParam(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class FunProtoLambdaSingleParamContext extends Fun_proto_lambdaContext {
    public Fun_paramContext fun_param() {
      return getRuleContext(Fun_paramContext.class, 0);
    }

    public TerminalNode RIGHT_ARROW() {
      return getToken(SnapiParser.RIGHT_ARROW, 0);
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

    public FunProtoLambdaSingleParamContext(Fun_proto_lambdaContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterFunProtoLambdaSingleParam(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitFunProtoLambdaSingleParam(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitFunProtoLambdaSingleParam(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Fun_proto_lambdaContext fun_proto_lambda() throws RecognitionException {
    Fun_proto_lambdaContext _localctx = new Fun_proto_lambdaContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_fun_proto_lambda);
    int _la;
    try {
      setState(209);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case LEFT_PAREN:
          _localctx = new FunProtoLambdaMultiParamContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(183);
            match(LEFT_PAREN);
            setState(192);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391091494908L) != 0)) {
              {
                setState(184);
                fun_param();
                setState(189);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                  {
                    {
                      setState(185);
                      match(COMMA);
                      setState(186);
                      fun_param();
                    }
                  }
                  setState(191);
                  _errHandler.sync(this);
                  _la = _input.LA(1);
                }
              }
            }

            setState(194);
            match(RIGHT_PAREN);
            setState(197);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if (_la == COLON) {
              {
                setState(195);
                match(COLON);
                setState(196);
                tipe(0);
              }
            }

            setState(199);
            match(RIGHT_ARROW);
            setState(200);
            expr(0);
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
          _localctx = new FunProtoLambdaSingleParamContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(201);
            fun_param();
            setState(204);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if (_la == COLON) {
              {
                setState(202);
                match(COLON);
                setState(203);
                tipe(0);
              }
            }

            setState(206);
            match(RIGHT_ARROW);
            setState(207);
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
  public static class NullableTryableTypeContext extends TipeContext {
    public TipeContext tipe() {
      return getRuleContext(TipeContext.class, 0);
    }

    public Nullable_tryableContext nullable_tryable() {
      return getRuleContext(Nullable_tryableContext.class, 0);
    }

    public NullableTryableTypeContext(TipeContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNullableTryableType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNullableTryableType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNullableTryableType(this);
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
        setState(244);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 24, _ctx)) {
          case 1:
            {
              _localctx = new TypeWithParenTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;

              setState(212);
              match(LEFT_PAREN);
              setState(213);
              tipe(0);
              setState(214);
              match(RIGHT_PAREN);
            }
            break;
          case 2:
            {
              _localctx = new PrimitiveTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(216);
              primitive_types();
            }
            break;
          case 3:
            {
              _localctx = new RecordTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(217);
              record_type();
            }
            break;
          case 4:
            {
              _localctx = new IterableTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(218);
              iterable_type();
            }
            break;
          case 5:
            {
              _localctx = new ListTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(219);
              list_type();
            }
            break;
          case 6:
            {
              _localctx = new TypeAliasTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(220);
              ident();
            }
            break;
          case 7:
            {
              _localctx = new FunTypeWithParamsTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(221);
              match(LEFT_PAREN);
              setState(224);
              _errHandler.sync(this);
              switch (getInterpreter().adaptivePredict(_input, 20, _ctx)) {
                case 1:
                  {
                    setState(222);
                    tipe(0);
                  }
                  break;
                case 2:
                  {
                    setState(223);
                    attr();
                  }
                  break;
              }
              setState(233);
              _errHandler.sync(this);
              _alt = getInterpreter().adaptivePredict(_input, 22, _ctx);
              while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                if (_alt == 1) {
                  {
                    {
                      setState(226);
                      match(COMMA);
                      setState(229);
                      _errHandler.sync(this);
                      switch (getInterpreter().adaptivePredict(_input, 21, _ctx)) {
                        case 1:
                          {
                            setState(227);
                            tipe(0);
                          }
                          break;
                        case 2:
                          {
                            setState(228);
                            attr();
                          }
                          break;
                      }
                    }
                  }
                }
                setState(235);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 22, _ctx);
              }
              setState(237);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if (_la == COMMA) {
                {
                  setState(236);
                  match(COMMA);
                }
              }

              setState(239);
              match(RIGHT_PAREN);
              setState(240);
              match(RIGHT_ARROW);
              setState(241);
              tipe(3);
            }
            break;
          case 8:
            {
              _localctx = new ExprTypeTypeContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(243);
              expr_type();
            }
            break;
        }
        _ctx.stop = _input.LT(-1);
        setState(262);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 26, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            if (_parseListeners != null) triggerExitRuleEvent();
            _prevctx = _localctx;
            {
              setState(260);
              _errHandler.sync(this);
              switch (getInterpreter().adaptivePredict(_input, 25, _ctx)) {
                case 1:
                  {
                    _localctx = new OrTypeFunTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(246);
                    if (!(precpred(_ctx, 10)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                    setState(247);
                    match(OR_TOKEN);
                    setState(248);
                    or_type();
                    setState(249);
                    match(RIGHT_ARROW);
                    setState(250);
                    tipe(11);
                  }
                  break;
                case 2:
                  {
                    _localctx = new FunTypeTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(252);
                    if (!(precpred(_ctx, 2)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                    setState(253);
                    match(RIGHT_ARROW);
                    setState(254);
                    tipe(3);
                  }
                  break;
                case 3:
                  {
                    _localctx =
                        new NullableTryableTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(255);
                    if (!(precpred(_ctx, 11)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                    setState(256);
                    nullable_tryable();
                  }
                  break;
                case 4:
                  {
                    _localctx = new OrTypeTypeContext(new TipeContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_tipe);
                    setState(257);
                    if (!(precpred(_ctx, 9)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                    setState(258);
                    match(OR_TOKEN);
                    setState(259);
                    or_type();
                  }
                  break;
              }
            }
          }
          setState(264);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 26, _ctx);
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
      setState(270);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 27, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(265);
            tipe(0);
            setState(266);
            match(OR_TOKEN);
            setState(267);
            or_type();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(269);
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
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(272);
        match(RECORD_TOKEN);
        setState(273);
        match(LEFT_PAREN);
        setState(274);
        type_attr();
        setState(279);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 28, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(275);
                match(COMMA);
                setState(276);
                type_attr();
              }
            }
          }
          setState(281);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 28, _ctx);
        }
        setState(283);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COMMA) {
          {
            setState(282);
            match(COMMA);
          }
        }

        setState(285);
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
        setState(287);
        match(COLLECTION_TOKEN);
        setState(288);
        match(LEFT_PAREN);
        setState(289);
        tipe(0);
        setState(290);
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
        setState(292);
        match(LIST_TOKEN);
        setState(293);
        match(LEFT_PAREN);
        setState(294);
        tipe(0);
        setState(295);
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
        setState(297);
        match(TYPE_TOKEN);
        setState(298);
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
  public static class StringLiteralExprContext extends ExprContext {
    public String_literalContext string_literal() {
      return getRuleContext(String_literalContext.class, 0);
    }

    public StringLiteralExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterStringLiteralExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitStringLiteralExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitStringLiteralExpr(this);
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
  public static class SignedNumberExprContext extends ExprContext {
    public Signed_numberContext signed_number() {
      return getRuleContext(Signed_numberContext.class, 0);
    }

    public SignedNumberExprContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterSignedNumberExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitSignedNumberExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitSignedNumberExpr(this);
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
  public static class PackageIdnExpContext extends ExprContext {
    public Package_idn_expContext package_idn_exp() {
      return getRuleContext(Package_idn_expContext.class, 0);
    }

    public PackageIdnExpContext(ExprContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterPackageIdnExp(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitPackageIdnExp(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitPackageIdnExp(this);
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
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(323);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 30, _ctx)) {
          case 1:
            {
              _localctx = new ParenExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;

              setState(301);
              match(LEFT_PAREN);
              setState(302);
              expr(0);
              setState(303);
              match(RIGHT_PAREN);
            }
            break;
          case 2:
            {
              _localctx = new PackageIdnExpContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(305);
              package_idn_exp();
            }
            break;
          case 3:
            {
              _localctx = new LetExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(306);
              let();
            }
            break;
          case 4:
            {
              _localctx = new FunAbsExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(307);
              fun_abs();
            }
            break;
          case 5:
            {
              _localctx = new ExprTypeExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(308);
              expr_type();
            }
            break;
          case 6:
            {
              _localctx = new IfThenElseExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(309);
              if_then_else();
            }
            break;
          case 7:
            {
              _localctx = new SignedNumberExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(310);
              signed_number();
            }
            break;
          case 8:
            {
              _localctx = new BoolConstExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(311);
              bool_const();
            }
            break;
          case 9:
            {
              _localctx = new NullExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(312);
              match(NULL_TOKEN);
            }
            break;
          case 10:
            {
              _localctx = new StringLiteralExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(313);
              string_literal();
            }
            break;
          case 11:
            {
              _localctx = new IdentExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(314);
              ident();
            }
            break;
          case 12:
            {
              _localctx = new ListExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(315);
              lists();
            }
            break;
          case 13:
            {
              _localctx = new RecordExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(316);
              records();
            }
            break;
          case 14:
            {
              _localctx = new MinusUnaryExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(317);
              match(MINUS_TOKEN);
              setState(318);
              expr(19);
            }
            break;
          case 15:
            {
              _localctx = new PlusUnaryExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(319);
              match(PLUS_TOKEN);
              setState(320);
              expr(18);
            }
            break;
          case 16:
            {
              _localctx = new NotExprContext(_localctx);
              _ctx = _localctx;
              _prevctx = _localctx;
              setState(321);
              match(NOT_TOKEN);
              setState(322);
              expr(5);
            }
            break;
        }
        _ctx.stop = _input.LT(-1);
        setState(385);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 33, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            if (_parseListeners != null) triggerExitRuleEvent();
            _prevctx = _localctx;
            {
              setState(383);
              _errHandler.sync(this);
              switch (getInterpreter().adaptivePredict(_input, 32, _ctx)) {
                case 1:
                  {
                    _localctx = new DivExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(325);
                    if (!(precpred(_ctx, 17)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 17)");
                    setState(326);
                    match(DIV_TOKEN);
                    setState(327);
                    expr(18);
                  }
                  break;
                case 2:
                  {
                    _localctx = new MulExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(328);
                    if (!(precpred(_ctx, 15)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 15)");
                    setState(329);
                    match(MUL_TOKEN);
                    setState(330);
                    expr(16);
                  }
                  break;
                case 3:
                  {
                    _localctx = new ModExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(331);
                    if (!(precpred(_ctx, 13)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 13)");
                    setState(332);
                    match(MOD_TOKEN);
                    setState(333);
                    expr(14);
                  }
                  break;
                case 4:
                  {
                    _localctx = new MinusExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(334);
                    if (!(precpred(_ctx, 11)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                    setState(335);
                    match(MINUS_TOKEN);
                    setState(336);
                    expr(12);
                  }
                  break;
                case 5:
                  {
                    _localctx = new PlusExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(337);
                    if (!(precpred(_ctx, 9)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                    setState(338);
                    match(PLUS_TOKEN);
                    setState(339);
                    expr(10);
                  }
                  break;
                case 6:
                  {
                    _localctx = new CompareExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(340);
                    if (!(precpred(_ctx, 7)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                    setState(341);
                    compare_tokens();
                    setState(342);
                    expr(8);
                  }
                  break;
                case 7:
                  {
                    _localctx = new AndExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(344);
                    if (!(precpred(_ctx, 4)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                    setState(345);
                    match(AND_TOKEN);
                    setState(346);
                    expr(5);
                  }
                  break;
                case 8:
                  {
                    _localctx = new OrExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(347);
                    if (!(precpred(_ctx, 2)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                    setState(348);
                    match(OR_TOKEN);
                    setState(349);
                    expr(3);
                  }
                  break;
                case 9:
                  {
                    _localctx = new FunAppExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(350);
                    if (!(precpred(_ctx, 23)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 23)");
                    setState(351);
                    fun_ar();
                  }
                  break;
                case 10:
                  {
                    _localctx =
                        new ProjectionExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(352);
                    if (!(precpred(_ctx, 20)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 20)");
                    setState(353);
                    match(DOT);
                    setState(354);
                    ident();
                    setState(356);
                    _errHandler.sync(this);
                    switch (getInterpreter().adaptivePredict(_input, 31, _ctx)) {
                      case 1:
                        {
                          setState(355);
                          fun_ar();
                        }
                        break;
                    }
                  }
                  break;
                case 11:
                  {
                    _localctx = new DivExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(358);
                    if (!(precpred(_ctx, 16)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 16)");
                    setState(359);
                    match(DIV_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 12:
                  {
                    _localctx = new MulExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(361);
                    if (!(precpred(_ctx, 14)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 14)");
                    setState(362);
                    match(MUL_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 13:
                  {
                    _localctx = new ModExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(364);
                    if (!(precpred(_ctx, 12)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                    setState(365);
                    match(MOD_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 14:
                  {
                    _localctx = new MinusExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(367);
                    if (!(precpred(_ctx, 10)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                    setState(368);
                    match(MINUS_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 15:
                  {
                    _localctx = new PlusExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(370);
                    if (!(precpred(_ctx, 8)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                    setState(371);
                    match(PLUS_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 16:
                  {
                    _localctx = new CompareExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(373);
                    if (!(precpred(_ctx, 6)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                    setState(374);
                    compare_tokens();
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 17:
                  {
                    _localctx = new AndExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(377);
                    if (!(precpred(_ctx, 3)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                    setState(378);
                    match(AND_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
                case 18:
                  {
                    _localctx = new OrExprContext(new ExprContext(_parentctx, _parentState));
                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                    setState(380);
                    if (!(precpred(_ctx, 1)))
                      throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                    setState(381);
                    match(OR_TOKEN);
                    notifyErrorListeners("Missing right expression");
                  }
                  break;
              }
            }
          }
          setState(387);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 33, _ctx);
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
        setState(388);
        match(LET_TOKEN);
        setState(389);
        let_left();
        setState(390);
        match(IN_TOKEN);
        setState(391);
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

    public Multiple_commasContext multiple_commas() {
      return getRuleContext(Multiple_commasContext.class, 0);
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
      int _alt;
      setState(411);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 36, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(393);
            let_decl();
            setState(398);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 34, _ctx);
            while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
              if (_alt == 1) {
                {
                  {
                    setState(394);
                    match(COMMA);
                    setState(395);
                    let_decl();
                  }
                }
              }
              setState(400);
              _errHandler.sync(this);
              _alt = getInterpreter().adaptivePredict(_input, 34, _ctx);
            }
            setState(401);
            multiple_commas();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(403);
            let_decl();
            notifyErrorListeners("Missing ','");
            setState(408);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108086391108272124L) != 0)) {
              {
                {
                  setState(405);
                  let_decl();
                }
              }
              setState(410);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
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
  public static class Multiple_commasContext extends ParserRuleContext {
    public List<TerminalNode> COMMA() {
      return getTokens(SnapiParser.COMMA);
    }

    public TerminalNode COMMA(int i) {
      return getToken(SnapiParser.COMMA, i);
    }

    public Multiple_commasContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_multiple_commas;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterMultiple_commas(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitMultiple_commas(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitMultiple_commas(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Multiple_commasContext multiple_commas() throws RecognitionException {
    Multiple_commasContext _localctx = new Multiple_commasContext(_ctx, getState());
    enterRule(_localctx, 44, RULE_multiple_commas);
    int _la;
    try {
      setState(423);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 39, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(413);
            match(COMMA);
            setState(415);
            _errHandler.sync(this);
            _la = _input.LA(1);
            do {
              {
                {
                  setState(414);
                  match(COMMA);
                }
              }
              setState(417);
              _errHandler.sync(this);
              _la = _input.LA(1);
            } while (_la == COMMA);
            notifyErrorListeners("Occurence of extra commas");
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(421);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if (_la == COMMA) {
              {
                setState(420);
                match(COMMA);
              }
            }
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
  public static class Let_declContext extends ParserRuleContext {
    public Let_declContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_let_decl;
    }

    public Let_declContext() {}

    public void copyFrom(Let_declContext ctx) {
      super.copyFrom(ctx);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class LetBindContext extends Let_declContext {
    public Let_bindContext let_bind() {
      return getRuleContext(Let_bindContext.class, 0);
    }

    public LetBindContext(Let_declContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLetBind(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLetBind(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLetBind(this);
      else return visitor.visitChildren(this);
    }
  }

  @SuppressWarnings("CheckReturnValue")
  public static class LetFunDecContext extends Let_declContext {
    public Fun_decContext fun_dec() {
      return getRuleContext(Fun_decContext.class, 0);
    }

    public LetFunDecContext(Let_declContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterLetFunDec(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitLetFunDec(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitLetFunDec(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Let_declContext let_decl() throws RecognitionException {
    Let_declContext _localctx = new Let_declContext(_ctx, getState());
    enterRule(_localctx, 46, RULE_let_decl);
    try {
      setState(427);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 40, _ctx)) {
        case 1:
          _localctx = new LetBindContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(425);
            let_bind();
          }
          break;
        case 2:
          _localctx = new LetFunDecContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(426);
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
    enterRule(_localctx, 48, RULE_let_bind);
    try {
      setState(449);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 41, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(429);
            ident();
            setState(430);
            match(EQUALS);
            setState(431);
            expr(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(433);
            ident();
            setState(434);
            match(COLON);
            setState(435);
            tipe(0);
            setState(436);
            match(EQUALS);
            setState(437);
            expr(0);
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(439);
            ident();
            setState(440);
            match(EQUALS);
            notifyErrorListeners("Missing expression binding");
          }
          break;
        case 4:
          enterOuterAlt(_localctx, 4);
          {
            setState(443);
            ident();
            setState(444);
            match(COLON);
            setState(445);
            tipe(0);
            setState(446);
            match(EQUALS);
            notifyErrorListeners("Missing expression binding");
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
    enterRule(_localctx, 50, RULE_if_then_else);
    try {
      setState(469);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 42, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(451);
            match(IF_TOKEN);
            setState(452);
            expr(0);
            setState(453);
            match(THEN_TOKEN);
            setState(454);
            expr(0);
            setState(455);
            match(ELSE_TOKEN);
            setState(456);
            expr(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(458);
            match(IF_TOKEN);
            setState(459);
            expr(0);
            setState(460);
            match(THEN_TOKEN);
            setState(461);
            expr(0);
            setState(462);
            match(ELSE_TOKEN);
            notifyErrorListeners("Missing else expression");
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(465);
            match(IF_TOKEN);
            setState(466);
            expr(0);
            notifyErrorListeners("Missing then body");
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
    enterRule(_localctx, 52, RULE_lists);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(471);
        match(LEFT_SQ_BR);
        setState(473);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 719476565759492094L) != 0)
            || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 69L) != 0)) {
          {
            setState(472);
            lists_element();
          }
        }

        setState(475);
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
    enterRule(_localctx, 54, RULE_lists_element);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(477);
        expr(0);
        setState(482);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 44, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(478);
                match(COMMA);
                setState(479);
                expr(0);
              }
            }
          }
          setState(484);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 44, _ctx);
        }
        setState(486);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COMMA) {
          {
            setState(485);
            match(COMMA);
          }
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
    enterRule(_localctx, 56, RULE_records);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(488);
        match(LEFT_CUR_BR);
        setState(490);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 719476565759492094L) != 0)
            || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 69L) != 0)) {
          {
            setState(489);
            record_elements();
          }
        }

        setState(492);
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
    enterRule(_localctx, 58, RULE_record_elements);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(494);
        record_element();
        setState(499);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 47, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(495);
                match(COMMA);
                setState(496);
                record_element();
              }
            }
          }
          setState(501);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 47, _ctx);
        }
        setState(503);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == COMMA) {
          {
            setState(502);
            match(COMMA);
          }
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
    enterRule(_localctx, 60, RULE_record_element);
    try {
      setState(510);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 49, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(505);
            ident();
            setState(506);
            match(COLON);
            setState(507);
            expr(0);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(509);
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
  public static class Signed_numberContext extends ParserRuleContext {
    public NumberContext number() {
      return getRuleContext(NumberContext.class, 0);
    }

    public TerminalNode MINUS_TOKEN() {
      return getToken(SnapiParser.MINUS_TOKEN, 0);
    }

    public TerminalNode PLUS_TOKEN() {
      return getToken(SnapiParser.PLUS_TOKEN, 0);
    }

    public Signed_numberContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_signed_number;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterSigned_number(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitSigned_number(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitSigned_number(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Signed_numberContext signed_number() throws RecognitionException {
    Signed_numberContext _localctx = new Signed_numberContext(_ctx, getState());
    enterRule(_localctx, 62, RULE_signed_number);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(513);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == PLUS_TOKEN || _la == MINUS_TOKEN) {
          {
            setState(512);
            _la = _input.LA(1);
            if (!(_la == PLUS_TOKEN || _la == MINUS_TOKEN)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
          }
        }

        setState(515);
        number();
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
    enterRule(_localctx, 64, RULE_number);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(517);
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
    enterRule(_localctx, 66, RULE_primitive_types);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(519);
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
  public static class String_literalContext extends ParserRuleContext {
    public TerminalNode STRING() {
      return getToken(SnapiParser.STRING, 0);
    }

    public Triple_string_literalContext triple_string_literal() {
      return getRuleContext(Triple_string_literalContext.class, 0);
    }

    public String_literalContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_string_literal;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterString_literal(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitString_literal(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitString_literal(this);
      else return visitor.visitChildren(this);
    }
  }

  public final String_literalContext string_literal() throws RecognitionException {
    String_literalContext _localctx = new String_literalContext(_ctx, getState());
    enterRule(_localctx, 68, RULE_string_literal);
    try {
      setState(523);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case STRING:
          enterOuterAlt(_localctx, 1);
          {
            setState(521);
            match(STRING);
          }
          break;
        case START_TRIPLE_QUOTE:
          enterOuterAlt(_localctx, 2);
          {
            setState(522);
            triple_string_literal();
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
  public static class Triple_string_literalContext extends ParserRuleContext {
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

    public Triple_string_literalContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_triple_string_literal;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterTriple_string_literal(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitTriple_string_literal(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitTriple_string_literal(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Triple_string_literalContext triple_string_literal() throws RecognitionException {
    Triple_string_literalContext _localctx = new Triple_string_literalContext(_ctx, getState());
    enterRule(_localctx, 70, RULE_triple_string_literal);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(525);
        match(START_TRIPLE_QUOTE);
        setState(529);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == TRIPLE_QUOTED_STRING_CONTENT) {
          {
            {
              setState(526);
              match(TRIPLE_QUOTED_STRING_CONTENT);
            }
          }
          setState(531);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(532);
        _la = _input.LA(1);
        if (!(((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & 7L) != 0))) {
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
    enterRule(_localctx, 72, RULE_compare_tokens);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(534);
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
    enterRule(_localctx, 74, RULE_bool_const);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(536);
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
    enterRule(_localctx, 76, RULE_ident);
    try {
      setState(544);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case NON_ESC_IDENTIFIER:
          enterOuterAlt(_localctx, 1);
          {
            setState(538);
            match(NON_ESC_IDENTIFIER);
          }
          break;
        case ESC_IDENTIFIER:
          enterOuterAlt(_localctx, 2);
          {
            setState(539);
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
            setState(540);
            primitive_types();
          }
          break;
        case LIST_TOKEN:
          enterOuterAlt(_localctx, 4);
          {
            setState(541);
            match(LIST_TOKEN);
          }
          break;
        case RECORD_TOKEN:
          enterOuterAlt(_localctx, 5);
          {
            setState(542);
            match(RECORD_TOKEN);
          }
          break;
        case COLLECTION_TOKEN:
          enterOuterAlt(_localctx, 6);
          {
            setState(543);
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

  @SuppressWarnings("CheckReturnValue")
  public static class Package_idn_expContext extends ParserRuleContext {
    public TerminalNode DOLLAR_TOKEN() {
      return getToken(SnapiParser.DOLLAR_TOKEN, 0);
    }

    public TerminalNode PACKAGE_TOKEN() {
      return getToken(SnapiParser.PACKAGE_TOKEN, 0);
    }

    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public String_literalContext string_literal() {
      return getRuleContext(String_literalContext.class, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public Package_idn_expContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_package_idn_exp;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterPackage_idn_exp(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitPackage_idn_exp(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitPackage_idn_exp(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Package_idn_expContext package_idn_exp() throws RecognitionException {
    Package_idn_expContext _localctx = new Package_idn_expContext(_ctx, getState());
    enterRule(_localctx, 78, RULE_package_idn_exp);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(546);
        match(DOLLAR_TOKEN);
        setState(547);
        match(PACKAGE_TOKEN);
        setState(548);
        match(LEFT_PAREN);
        setState(549);
        string_literal();
        setState(550);
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
  public static class Nullable_tryableContext extends ParserRuleContext {
    public TerminalNode LEFT_PAREN() {
      return getToken(SnapiParser.LEFT_PAREN, 0);
    }

    public Nullable_tryableContext nullable_tryable() {
      return getRuleContext(Nullable_tryableContext.class, 0);
    }

    public TerminalNode RIGHT_PAREN() {
      return getToken(SnapiParser.RIGHT_PAREN, 0);
    }

    public TerminalNode NULLABLE_TOKEN() {
      return getToken(SnapiParser.NULLABLE_TOKEN, 0);
    }

    public TerminalNode TRYABLE_TOKEN() {
      return getToken(SnapiParser.TRYABLE_TOKEN, 0);
    }

    public Nullable_tryableContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_nullable_tryable;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).enterNullable_tryable(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof SnapiParserListener)
        ((SnapiParserListener) listener).exitNullable_tryable(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof SnapiParserVisitor)
        return ((SnapiParserVisitor<? extends T>) visitor).visitNullable_tryable(this);
      else return visitor.visitChildren(this);
    }
  }

  public final Nullable_tryableContext nullable_tryable() throws RecognitionException {
    Nullable_tryableContext _localctx = new Nullable_tryableContext(_ctx, getState());
    enterRule(_localctx, 80, RULE_nullable_tryable);
    try {
      setState(562);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 54, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(552);
            match(LEFT_PAREN);
            setState(553);
            nullable_tryable();
            setState(554);
            match(RIGHT_PAREN);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(556);
            match(NULLABLE_TOKEN);
            setState(557);
            match(TRYABLE_TOKEN);
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(558);
            match(TRYABLE_TOKEN);
            setState(559);
            match(NULLABLE_TOKEN);
          }
          break;
        case 4:
          enterOuterAlt(_localctx, 4);
          {
            setState(560);
            match(NULLABLE_TOKEN);
          }
          break;
        case 5:
          enterOuterAlt(_localctx, 5);
          {
            setState(561);
            match(TRYABLE_TOKEN);
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
        return precpred(_ctx, 11);
      case 3:
        return precpred(_ctx, 9);
    }
    return true;
  }

  private boolean expr_sempred(ExprContext _localctx, int predIndex) {
    switch (predIndex) {
      case 4:
        return precpred(_ctx, 17);
      case 5:
        return precpred(_ctx, 15);
      case 6:
        return precpred(_ctx, 13);
      case 7:
        return precpred(_ctx, 11);
      case 8:
        return precpred(_ctx, 9);
      case 9:
        return precpred(_ctx, 7);
      case 10:
        return precpred(_ctx, 4);
      case 11:
        return precpred(_ctx, 2);
      case 12:
        return precpred(_ctx, 23);
      case 13:
        return precpred(_ctx, 20);
      case 14:
        return precpred(_ctx, 16);
      case 15:
        return precpred(_ctx, 14);
      case 16:
        return precpred(_ctx, 12);
      case 17:
        return precpred(_ctx, 10);
      case 18:
        return precpred(_ctx, 8);
      case 19:
        return precpred(_ctx, 6);
      case 20:
        return precpred(_ctx, 3);
      case 21:
        return precpred(_ctx, 1);
    }
    return true;
  }

  public static final String _serializedATN =
      "\u0004\u0001L\u0235\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"
          + "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"
          + "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"
          + "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"
          + "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"
          + "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"
          + "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"
          + "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"
          + "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"
          + "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"
          + "\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"
          + "#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"
          + "(\u0007(\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001W"
          + "\b\u0001\n\u0001\f\u0001Z\t\u0001\u0001\u0001\u0005\u0001]\b\u0001\n\u0001"
          + "\f\u0001`\t\u0001\u0001\u0001\u0003\u0001c\b\u0001\u0001\u0002\u0001\u0002"
          + "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"
          + "\u0001\u0003\u0001\u0003\u0003\u0003o\b\u0003\u0001\u0004\u0001\u0004"
          + "\u0001\u0004\u0001\u0004\u0005\u0004u\b\u0004\n\u0004\f\u0004x\t\u0004"
          + "\u0003\u0004z\b\u0004\u0001\u0004\u0003\u0004}\b\u0004\u0001\u0004\u0001"
          + "\u0004\u0001\u0004\u0003\u0004\u0082\b\u0004\u0001\u0004\u0001\u0004\u0001"
          + "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003"
          + "\u0005\u008c\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"
          + "\u0006\u0003\u0006\u0093\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"
          + "\u0007\u0001\b\u0001\b\u0003\b\u009b\b\b\u0001\b\u0001\b\u0001\t\u0001"
          + "\t\u0001\t\u0005\t\u00a2\b\t\n\t\f\t\u00a5\t\t\u0001\t\u0003\t\u00a8\b"
          + "\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00af\b\n\u0001\u000b"
          + "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00b6\b\u000b"
          + "\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u00bc\b\f\n\f\f\f\u00bf\t\f\u0003"
          + "\f\u00c1\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u00c6\b\f\u0001\f\u0001\f"
          + "\u0001\f\u0001\f\u0001\f\u0003\f\u00cd\b\f\u0001\f\u0001\f\u0001\f\u0003"
          + "\f\u00d2\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"
          + "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00e1\b\r\u0001\r\u0001"
          + "\r\u0001\r\u0003\r\u00e6\b\r\u0005\r\u00e8\b\r\n\r\f\r\u00eb\t\r\u0001"
          + "\r\u0003\r\u00ee\b\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00f5"
          + "\b\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"
          + "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\r\u0105\b\r\n\r\f\r\u0108"
          + "\t\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003"
          + "\u000e\u010f\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"
          + "\u000f\u0005\u000f\u0116\b\u000f\n\u000f\f\u000f\u0119\t\u000f\u0001\u000f"
          + "\u0003\u000f\u011c\b\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"
          + "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011"
          + "\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u0144\b\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0003\u0013\u0165\b\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
          + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u0180\b\u0013"
          + "\n\u0013\f\u0013\u0183\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"
          + "\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u018d"
          + "\b\u0015\n\u0015\f\u0015\u0190\t\u0015\u0001\u0015\u0001\u0015\u0001\u0015"
          + "\u0001\u0015\u0001\u0015\u0005\u0015\u0197\b\u0015\n\u0015\f\u0015\u019a"
          + "\t\u0015\u0003\u0015\u019c\b\u0015\u0001\u0016\u0001\u0016\u0004\u0016"
          + "\u01a0\b\u0016\u000b\u0016\f\u0016\u01a1\u0001\u0016\u0001\u0016\u0003"
          + "\u0016\u01a6\b\u0016\u0003\u0016\u01a8\b\u0016\u0001\u0017\u0001\u0017"
          + "\u0003\u0017\u01ac\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"
          + "\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"
          + "\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"
          + "\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u01c2\b\u0018"
          + "\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"
          + "\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"
          + "\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"
          + "\u0003\u0019\u01d6\b\u0019\u0001\u001a\u0001\u001a\u0003\u001a\u01da\b"
          + "\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0005"
          + "\u001b\u01e1\b\u001b\n\u001b\f\u001b\u01e4\t\u001b\u0001\u001b\u0003\u001b"
          + "\u01e7\b\u001b\u0001\u001c\u0001\u001c\u0003\u001c\u01eb\b\u001c\u0001"
          + "\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u01f2"
          + "\b\u001d\n\u001d\f\u001d\u01f5\t\u001d\u0001\u001d\u0003\u001d\u01f8\b"
          + "\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003"
          + "\u001e\u01ff\b\u001e\u0001\u001f\u0003\u001f\u0202\b\u001f\u0001\u001f"
          + "\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001\"\u0001\"\u0003\"\u020c"
          + "\b\"\u0001#\u0001#\u0005#\u0210\b#\n#\f#\u0213\t#\u0001#\u0001#\u0001"
          + "$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003"
          + "&\u0221\b&\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001(\u0001"
          + "(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(\u0233"
          + "\b(\u0001(\u0000\u0002\u001a&)\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"
          + "\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNP\u0000"
          + "\u0006\u0001\u0000+,\u0001\u0000\u001e$\u0002\u0000\u0002\u0010\u0019"
          + "\u0019\u0001\u0000JL\u0001\u0000%*\u0001\u000034\u0272\u0000R\u0001\u0000"
          + "\u0000\u0000\u0002b\u0001\u0000\u0000\u0000\u0004d\u0001\u0000\u0000\u0000"
          + "\u0006n\u0001\u0000\u0000\u0000\bp\u0001\u0000\u0000\u0000\n\u008b\u0001"
          + "\u0000\u0000\u0000\f\u0092\u0001\u0000\u0000\u0000\u000e\u0094\u0001\u0000"
          + "\u0000\u0000\u0010\u0098\u0001\u0000\u0000\u0000\u0012\u009e\u0001\u0000"
          + "\u0000\u0000\u0014\u00ae\u0001\u0000\u0000\u0000\u0016\u00b5\u0001\u0000"
          + "\u0000\u0000\u0018\u00d1\u0001\u0000\u0000\u0000\u001a\u00f4\u0001\u0000"
          + "\u0000\u0000\u001c\u010e\u0001\u0000\u0000\u0000\u001e\u0110\u0001\u0000"
          + "\u0000\u0000 \u011f\u0001\u0000\u0000\u0000\"\u0124\u0001\u0000\u0000"
          + "\u0000$\u0129\u0001\u0000\u0000\u0000&\u0143\u0001\u0000\u0000\u0000("
          + "\u0184\u0001\u0000\u0000\u0000*\u019b\u0001\u0000\u0000\u0000,\u01a7\u0001"
          + "\u0000\u0000\u0000.\u01ab\u0001\u0000\u0000\u00000\u01c1\u0001\u0000\u0000"
          + "\u00002\u01d5\u0001\u0000\u0000\u00004\u01d7\u0001\u0000\u0000\u00006"
          + "\u01dd\u0001\u0000\u0000\u00008\u01e8\u0001\u0000\u0000\u0000:\u01ee\u0001"
          + "\u0000\u0000\u0000<\u01fe\u0001\u0000\u0000\u0000>\u0201\u0001\u0000\u0000"
          + "\u0000@\u0205\u0001\u0000\u0000\u0000B\u0207\u0001\u0000\u0000\u0000D"
          + "\u020b\u0001\u0000\u0000\u0000F\u020d\u0001\u0000\u0000\u0000H\u0216\u0001"
          + "\u0000\u0000\u0000J\u0218\u0001\u0000\u0000\u0000L\u0220\u0001\u0000\u0000"
          + "\u0000N\u0222\u0001\u0000\u0000\u0000P\u0232\u0001\u0000\u0000\u0000R"
          + "S\u0003\u0002\u0001\u0000ST\u0005\u0000\u0000\u0001T\u0001\u0001\u0000"
          + "\u0000\u0000UW\u0003\u0004\u0002\u0000VU\u0001\u0000\u0000\u0000WZ\u0001"
          + "\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000"
          + "Yc\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000[]\u0003\u0004\u0002"
          + "\u0000\\[\u0001\u0000\u0000\u0000]`\u0001\u0000\u0000\u0000^\\\u0001\u0000"
          + "\u0000\u0000^_\u0001\u0000\u0000\u0000_a\u0001\u0000\u0000\u0000`^\u0001"
          + "\u0000\u0000\u0000ac\u0003&\u0013\u0000bX\u0001\u0000\u0000\u0000b^\u0001"
          + "\u0000\u0000\u0000c\u0003\u0001\u0000\u0000\u0000de\u0003L&\u0000ef\u0003"
          + "\b\u0004\u0000f\u0005\u0001\u0000\u0000\u0000gh\u0003L&\u0000hi\u0003"
          + "\b\u0004\u0000io\u0001\u0000\u0000\u0000jk\u0005\u0018\u0000\u0000kl\u0003"
          + "L&\u0000lm\u0003\b\u0004\u0000mo\u0001\u0000\u0000\u0000ng\u0001\u0000"
          + "\u0000\u0000nj\u0001\u0000\u0000\u0000o\u0007\u0001\u0000\u0000\u0000"
          + "py\u0005;\u0000\u0000qv\u0003\n\u0005\u0000rs\u0005>\u0000\u0000su\u0003"
          + "\n\u0005\u0000tr\u0001\u0000\u0000\u0000ux\u0001\u0000\u0000\u0000vt\u0001"
          + "\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wz\u0001\u0000\u0000\u0000"
          + "xv\u0001\u0000\u0000\u0000yq\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000"
          + "\u0000z|\u0001\u0000\u0000\u0000{}\u0005>\u0000\u0000|{\u0001\u0000\u0000"
          + "\u0000|}\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u0081\u0005"
          + "<\u0000\u0000\u007f\u0080\u0005=\u0000\u0000\u0080\u0082\u0003\u001a\r"
          + "\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000"
          + "\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0084\u0005?\u0000\u0000"
          + "\u0084\u0085\u0003&\u0013\u0000\u0085\t\u0001\u0000\u0000\u0000\u0086"
          + "\u008c\u0003\f\u0006\u0000\u0087\u0088\u0003\f\u0006\u0000\u0088\u0089"
          + "\u0005?\u0000\u0000\u0089\u008a\u0003&\u0013\u0000\u008a\u008c\u0001\u0000"
          + "\u0000\u0000\u008b\u0086\u0001\u0000\u0000\u0000\u008b\u0087\u0001\u0000"
          + "\u0000\u0000\u008c\u000b\u0001\u0000\u0000\u0000\u008d\u008e\u0003L&\u0000"
          + "\u008e\u008f\u0005=\u0000\u0000\u008f\u0090\u0003\u001a\r\u0000\u0090"
          + "\u0093\u0001\u0000\u0000\u0000\u0091\u0093\u0003L&\u0000\u0092\u008d\u0001"
          + "\u0000\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\r\u0001\u0000"
          + "\u0000\u0000\u0094\u0095\u0003L&\u0000\u0095\u0096\u0005=\u0000\u0000"
          + "\u0096\u0097\u0003\u001a\r\u0000\u0097\u000f\u0001\u0000\u0000\u0000\u0098"
          + "\u009a\u0005;\u0000\u0000\u0099\u009b\u0003\u0012\t\u0000\u009a\u0099"
          + "\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009c"
          + "\u0001\u0000\u0000\u0000\u009c\u009d\u0005<\u0000\u0000\u009d\u0011\u0001"
          + "\u0000\u0000\u0000\u009e\u00a3\u0003\u0014\n\u0000\u009f\u00a0\u0005>"
          + "\u0000\u0000\u00a0\u00a2\u0003\u0014\n\u0000\u00a1\u009f\u0001\u0000\u0000"
          + "\u0000\u00a2\u00a5\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000\u0000"
          + "\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a7\u0001\u0000\u0000"
          + "\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a8\u0005>\u0000\u0000"
          + "\u00a7\u00a6\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000"
          + "\u00a8\u0013\u0001\u0000\u0000\u0000\u00a9\u00af\u0003&\u0013\u0000\u00aa"
          + "\u00ab\u0003L&\u0000\u00ab\u00ac\u0005?\u0000\u0000\u00ac\u00ad\u0003"
          + "&\u0013\u0000\u00ad\u00af\u0001\u0000\u0000\u0000\u00ae\u00a9\u0001\u0000"
          + "\u0000\u0000\u00ae\u00aa\u0001\u0000\u0000\u0000\u00af\u0015\u0001\u0000"
          + "\u0000\u0000\u00b0\u00b6\u0003\u0018\f\u0000\u00b1\u00b2\u0003L&\u0000"
          + "\u00b2\u00b3\u0005@\u0000\u0000\u00b3\u00b4\u0003&\u0013\u0000\u00b4\u00b6"
          + "\u0001\u0000\u0000\u0000\u00b5\u00b0\u0001\u0000\u0000\u0000\u00b5\u00b1"
          + "\u0001\u0000\u0000\u0000\u00b6\u0017\u0001\u0000\u0000\u0000\u00b7\u00c0"
          + "\u0005;\u0000\u0000\u00b8\u00bd\u0003\n\u0005\u0000\u00b9\u00ba\u0005"
          + ">\u0000\u0000\u00ba\u00bc\u0003\n\u0005\u0000\u00bb\u00b9\u0001\u0000"
          + "\u0000\u0000\u00bc\u00bf\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000"
          + "\u0000\u0000\u00bd\u00be\u0001\u0000\u0000\u0000\u00be\u00c1\u0001\u0000"
          + "\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000\u0000\u00c0\u00b8\u0001\u0000"
          + "\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000"
          + "\u0000\u0000\u00c2\u00c5\u0005<\u0000\u0000\u00c3\u00c4\u0005=\u0000\u0000"
          + "\u00c4\u00c6\u0003\u001a\r\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c5"
          + "\u00c6\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7"
          + "\u00c8\u0005@\u0000\u0000\u00c8\u00d2\u0003&\u0013\u0000\u00c9\u00cc\u0003"
          + "\n\u0005\u0000\u00ca\u00cb\u0005=\u0000\u0000\u00cb\u00cd\u0003\u001a"
          + "\r\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd\u0001\u0000\u0000"
          + "\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u00cf\u0005@\u0000\u0000"
          + "\u00cf\u00d0\u0003&\u0013\u0000\u00d0\u00d2\u0001\u0000\u0000\u0000\u00d1"
          + "\u00b7\u0001\u0000\u0000\u0000\u00d1\u00c9\u0001\u0000\u0000\u0000\u00d2"
          + "\u0019\u0001\u0000\u0000\u0000\u00d3\u00d4\u0006\r\uffff\uffff\u0000\u00d4"
          + "\u00d5\u0005;\u0000\u0000\u00d5\u00d6\u0003\u001a\r\u0000\u00d6\u00d7"
          + "\u0005<\u0000\u0000\u00d7\u00f5\u0001\u0000\u0000\u0000\u00d8\u00f5\u0003"
          + "B!\u0000\u00d9\u00f5\u0003\u001e\u000f\u0000\u00da\u00f5\u0003 \u0010"
          + "\u0000\u00db\u00f5\u0003\"\u0011\u0000\u00dc\u00f5\u0003L&\u0000\u00dd"
          + "\u00e0\u0005;\u0000\u0000\u00de\u00e1\u0003\u001a\r\u0000\u00df\u00e1"
          + "\u0003\f\u0006\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00df\u0001"
          + "\u0000\u0000\u0000\u00e1\u00e9\u0001\u0000\u0000\u0000\u00e2\u00e5\u0005"
          + ">\u0000\u0000\u00e3\u00e6\u0003\u001a\r\u0000\u00e4\u00e6\u0003\f\u0006"
          + "\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e5\u00e4\u0001\u0000\u0000"
          + "\u0000\u00e6\u00e8\u0001\u0000\u0000\u0000\u00e7\u00e2\u0001\u0000\u0000"
          + "\u0000\u00e8\u00eb\u0001\u0000\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000"
          + "\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea\u00ed\u0001\u0000\u0000"
          + "\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00ec\u00ee\u0005>\u0000\u0000"
          + "\u00ed\u00ec\u0001\u0000\u0000\u0000\u00ed\u00ee\u0001\u0000\u0000\u0000"
          + "\u00ee\u00ef\u0001\u0000\u0000\u0000\u00ef\u00f0\u0005<\u0000\u0000\u00f0"
          + "\u00f1\u0005@\u0000\u0000\u00f1\u00f2\u0003\u001a\r\u0003\u00f2\u00f5"
          + "\u0001\u0000\u0000\u0000\u00f3\u00f5\u0003$\u0012\u0000\u00f4\u00d3\u0001"
          + "\u0000\u0000\u0000\u00f4\u00d8\u0001\u0000\u0000\u0000\u00f4\u00d9\u0001"
          + "\u0000\u0000\u0000\u00f4\u00da\u0001\u0000\u0000\u0000\u00f4\u00db\u0001"
          + "\u0000\u0000\u0000\u00f4\u00dc\u0001\u0000\u0000\u0000\u00f4\u00dd\u0001"
          + "\u0000\u0000\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000\u00f5\u0106\u0001"
          + "\u0000\u0000\u0000\u00f6\u00f7\n\n\u0000\u0000\u00f7\u00f8\u00051\u0000"
          + "\u0000\u00f8\u00f9\u0003\u001c\u000e\u0000\u00f9\u00fa\u0005@\u0000\u0000"
          + "\u00fa\u00fb\u0003\u001a\r\u000b\u00fb\u0105\u0001\u0000\u0000\u0000\u00fc"
          + "\u00fd\n\u0002\u0000\u0000\u00fd\u00fe\u0005@\u0000\u0000\u00fe\u0105"
          + "\u0003\u001a\r\u0003\u00ff\u0100\n\u000b\u0000\u0000\u0100\u0105\u0003"
          + "P(\u0000\u0101\u0102\n\t\u0000\u0000\u0102\u0103\u00051\u0000\u0000\u0103"
          + "\u0105\u0003\u001c\u000e\u0000\u0104\u00f6\u0001\u0000\u0000\u0000\u0104"
          + "\u00fc\u0001\u0000\u0000\u0000\u0104\u00ff\u0001\u0000\u0000\u0000\u0104"
          + "\u0101\u0001\u0000\u0000\u0000\u0105\u0108\u0001\u0000\u0000\u0000\u0106"
          + "\u0104\u0001\u0000\u0000\u0000\u0106\u0107\u0001\u0000\u0000\u0000\u0107"
          + "\u001b\u0001\u0000\u0000\u0000\u0108\u0106\u0001\u0000\u0000\u0000\u0109"
          + "\u010a\u0003\u001a\r\u0000\u010a\u010b\u00051\u0000\u0000\u010b\u010c"
          + "\u0003\u001c\u000e\u0000\u010c\u010f\u0001\u0000\u0000\u0000\u010d\u010f"
          + "\u0003\u001a\r\u0000\u010e\u0109\u0001\u0000\u0000\u0000\u010e\u010d\u0001"
          + "\u0000\u0000\u0000\u010f\u001d\u0001\u0000\u0000\u0000\u0110\u0111\u0005"
          + "\u0011\u0000\u0000\u0111\u0112\u0005;\u0000\u0000\u0112\u0117\u0003\u000e"
          + "\u0007\u0000\u0113\u0114\u0005>\u0000\u0000\u0114\u0116\u0003\u000e\u0007"
          + "\u0000\u0115\u0113\u0001\u0000\u0000\u0000\u0116\u0119\u0001\u0000\u0000"
          + "\u0000\u0117\u0115\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000"
          + "\u0000\u0118\u011b\u0001\u0000\u0000\u0000\u0119\u0117\u0001\u0000\u0000"
          + "\u0000\u011a\u011c\u0005>\u0000\u0000\u011b\u011a\u0001\u0000\u0000\u0000"
          + "\u011b\u011c\u0001\u0000\u0000\u0000\u011c\u011d\u0001\u0000\u0000\u0000"
          + "\u011d\u011e\u0005<\u0000\u0000\u011e\u001f\u0001\u0000\u0000\u0000\u011f"
          + "\u0120\u0005\u0012\u0000\u0000\u0120\u0121\u0005;\u0000\u0000\u0121\u0122"
          + "\u0003\u001a\r\u0000\u0122\u0123\u0005<\u0000\u0000\u0123!\u0001\u0000"
          + "\u0000\u0000\u0124\u0125\u0005\u0013\u0000\u0000\u0125\u0126\u0005;\u0000"
          + "\u0000\u0126\u0127\u0003\u001a\r\u0000\u0127\u0128\u0005<\u0000\u0000"
          + "\u0128#\u0001\u0000\u0000\u0000\u0129\u012a\u0005\u0001\u0000\u0000\u012a"
          + "\u012b\u0003\u001a\r\u0000\u012b%\u0001\u0000\u0000\u0000\u012c\u012d"
          + "\u0006\u0013\uffff\uffff\u0000\u012d\u012e\u0005;\u0000\u0000\u012e\u012f"
          + "\u0003&\u0013\u0000\u012f\u0130\u0005<\u0000\u0000\u0130\u0144\u0001\u0000"
          + "\u0000\u0000\u0131\u0144\u0003N\'\u0000\u0132\u0144\u0003(\u0014\u0000"
          + "\u0133\u0144\u0003\u0016\u000b\u0000\u0134\u0144\u0003$\u0012\u0000\u0135"
          + "\u0144\u00032\u0019\u0000\u0136\u0144\u0003>\u001f\u0000\u0137\u0144\u0003"
          + "J%\u0000\u0138\u0144\u0005\u001d\u0000\u0000\u0139\u0144\u0003D\"\u0000"
          + "\u013a\u0144\u0003L&\u0000\u013b\u0144\u00034\u001a\u0000\u013c\u0144"
          + "\u00038\u001c\u0000\u013d\u013e\u0005,\u0000\u0000\u013e\u0144\u0003&"
          + "\u0013\u0013\u013f\u0140\u0005+\u0000\u0000\u0140\u0144\u0003&\u0013\u0012"
          + "\u0141\u0142\u00052\u0000\u0000\u0142\u0144\u0003&\u0013\u0005\u0143\u012c"
          + "\u0001\u0000\u0000\u0000\u0143\u0131\u0001\u0000\u0000\u0000\u0143\u0132"
          + "\u0001\u0000\u0000\u0000\u0143\u0133\u0001\u0000\u0000\u0000\u0143\u0134"
          + "\u0001\u0000\u0000\u0000\u0143\u0135\u0001\u0000\u0000\u0000\u0143\u0136"
          + "\u0001\u0000\u0000\u0000\u0143\u0137\u0001\u0000\u0000\u0000\u0143\u0138"
          + "\u0001\u0000\u0000\u0000\u0143\u0139\u0001\u0000\u0000\u0000\u0143\u013a"
          + "\u0001\u0000\u0000\u0000\u0143\u013b\u0001\u0000\u0000\u0000\u0143\u013c"
          + "\u0001\u0000\u0000\u0000\u0143\u013d\u0001\u0000\u0000\u0000\u0143\u013f"
          + "\u0001\u0000\u0000\u0000\u0143\u0141\u0001\u0000\u0000\u0000\u0144\u0181"
          + "\u0001\u0000\u0000\u0000\u0145\u0146\n\u0011\u0000\u0000\u0146\u0147\u0005"
          + ".\u0000\u0000\u0147\u0180\u0003&\u0013\u0012\u0148\u0149\n\u000f\u0000"
          + "\u0000\u0149\u014a\u0005-\u0000\u0000\u014a\u0180\u0003&\u0013\u0010\u014b"
          + "\u014c\n\r\u0000\u0000\u014c\u014d\u0005/\u0000\u0000\u014d\u0180\u0003"
          + "&\u0013\u000e\u014e\u014f\n\u000b\u0000\u0000\u014f\u0150\u0005,\u0000"
          + "\u0000\u0150\u0180\u0003&\u0013\f\u0151\u0152\n\t\u0000\u0000\u0152\u0153"
          + "\u0005+\u0000\u0000\u0153\u0180\u0003&\u0013\n\u0154\u0155\n\u0007\u0000"
          + "\u0000\u0155\u0156\u0003H$\u0000\u0156\u0157\u0003&\u0013\b\u0157\u0180"
          + "\u0001\u0000\u0000\u0000\u0158\u0159\n\u0004\u0000\u0000\u0159\u015a\u0005"
          + "0\u0000\u0000\u015a\u0180\u0003&\u0013\u0005\u015b\u015c\n\u0002\u0000"
          + "\u0000\u015c\u015d\u00051\u0000\u0000\u015d\u0180\u0003&\u0013\u0003\u015e"
          + "\u015f\n\u0017\u0000\u0000\u015f\u0180\u0003\u0010\b\u0000\u0160\u0161"
          + "\n\u0014\u0000\u0000\u0161\u0162\u0005A\u0000\u0000\u0162\u0164\u0003"
          + "L&\u0000\u0163\u0165\u0003\u0010\b\u0000\u0164\u0163\u0001\u0000\u0000"
          + "\u0000\u0164\u0165\u0001\u0000\u0000\u0000\u0165\u0180\u0001\u0000\u0000"
          + "\u0000\u0166\u0167\n\u0010\u0000\u0000\u0167\u0168\u0005.\u0000\u0000"
          + "\u0168\u0180\u0006\u0013\uffff\uffff\u0000\u0169\u016a\n\u000e\u0000\u0000"
          + "\u016a\u016b\u0005-\u0000\u0000\u016b\u0180\u0006\u0013\uffff\uffff\u0000"
          + "\u016c\u016d\n\f\u0000\u0000\u016d\u016e\u0005/\u0000\u0000\u016e\u0180"
          + "\u0006\u0013\uffff\uffff\u0000\u016f\u0170\n\n\u0000\u0000\u0170\u0171"
          + "\u0005,\u0000\u0000\u0171\u0180\u0006\u0013\uffff\uffff\u0000\u0172\u0173"
          + "\n\b\u0000\u0000\u0173\u0174\u0005+\u0000\u0000\u0174\u0180\u0006\u0013"
          + "\uffff\uffff\u0000\u0175\u0176\n\u0006\u0000\u0000\u0176\u0177\u0003H"
          + "$\u0000\u0177\u0178\u0006\u0013\uffff\uffff\u0000\u0178\u0180\u0001\u0000"
          + "\u0000\u0000\u0179\u017a\n\u0003\u0000\u0000\u017a\u017b\u00050\u0000"
          + "\u0000\u017b\u0180\u0006\u0013\uffff\uffff\u0000\u017c\u017d\n\u0001\u0000"
          + "\u0000\u017d\u017e\u00051\u0000\u0000\u017e\u0180\u0006\u0013\uffff\uffff"
          + "\u0000\u017f\u0145\u0001\u0000\u0000\u0000\u017f\u0148\u0001\u0000\u0000"
          + "\u0000\u017f\u014b\u0001\u0000\u0000\u0000\u017f\u014e\u0001\u0000\u0000"
          + "\u0000\u017f\u0151\u0001\u0000\u0000\u0000\u017f\u0154\u0001\u0000\u0000"
          + "\u0000\u017f\u0158\u0001\u0000\u0000\u0000\u017f\u015b\u0001\u0000\u0000"
          + "\u0000\u017f\u015e\u0001\u0000\u0000\u0000\u017f\u0160\u0001\u0000\u0000"
          + "\u0000\u017f\u0166\u0001\u0000\u0000\u0000\u017f\u0169\u0001\u0000\u0000"
          + "\u0000\u017f\u016c\u0001\u0000\u0000\u0000\u017f\u016f\u0001\u0000\u0000"
          + "\u0000\u017f\u0172\u0001\u0000\u0000\u0000\u017f\u0175\u0001\u0000\u0000"
          + "\u0000\u017f\u0179\u0001\u0000\u0000\u0000\u017f\u017c\u0001\u0000\u0000"
          + "\u0000\u0180\u0183\u0001\u0000\u0000\u0000\u0181\u017f\u0001\u0000\u0000"
          + "\u0000\u0181\u0182\u0001\u0000\u0000\u0000\u0182\'\u0001\u0000\u0000\u0000"
          + "\u0183\u0181\u0001\u0000\u0000\u0000\u0184\u0185\u0005\u0016\u0000\u0000"
          + "\u0185\u0186\u0003*\u0015\u0000\u0186\u0187\u0005\u0017\u0000\u0000\u0187"
          + "\u0188\u0003&\u0013\u0000\u0188)\u0001\u0000\u0000\u0000\u0189\u018e\u0003"
          + ".\u0017\u0000\u018a\u018b\u0005>\u0000\u0000\u018b\u018d\u0003.\u0017"
          + "\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018d\u0190\u0001\u0000\u0000"
          + "\u0000\u018e\u018c\u0001\u0000\u0000\u0000\u018e\u018f\u0001\u0000\u0000"
          + "\u0000\u018f\u0191\u0001\u0000\u0000\u0000\u0190\u018e\u0001\u0000\u0000"
          + "\u0000\u0191\u0192\u0003,\u0016\u0000\u0192\u019c\u0001\u0000\u0000\u0000"
          + "\u0193\u0194\u0003.\u0017\u0000\u0194\u0198\u0006\u0015\uffff\uffff\u0000"
          + "\u0195\u0197\u0003.\u0017\u0000\u0196\u0195\u0001\u0000\u0000\u0000\u0197"
          + "\u019a\u0001\u0000\u0000\u0000\u0198\u0196\u0001\u0000\u0000\u0000\u0198"
          + "\u0199\u0001\u0000\u0000\u0000\u0199\u019c\u0001\u0000\u0000\u0000\u019a"
          + "\u0198\u0001\u0000\u0000\u0000\u019b\u0189\u0001\u0000\u0000\u0000\u019b"
          + "\u0193\u0001\u0000\u0000\u0000\u019c+\u0001\u0000\u0000\u0000\u019d\u019f"
          + "\u0005>\u0000\u0000\u019e\u01a0\u0005>\u0000\u0000\u019f\u019e\u0001\u0000"
          + "\u0000\u0000\u01a0\u01a1\u0001\u0000\u0000\u0000\u01a1\u019f\u0001\u0000"
          + "\u0000\u0000\u01a1\u01a2\u0001\u0000\u0000\u0000\u01a2\u01a3\u0001\u0000"
          + "\u0000\u0000\u01a3\u01a8\u0006\u0016\uffff\uffff\u0000\u01a4\u01a6\u0005"
          + ">\u0000\u0000\u01a5\u01a4\u0001\u0000\u0000\u0000\u01a5\u01a6\u0001\u0000"
          + "\u0000\u0000\u01a6\u01a8\u0001\u0000\u0000\u0000\u01a7\u019d\u0001\u0000"
          + "\u0000\u0000\u01a7\u01a5\u0001\u0000\u0000\u0000\u01a8-\u0001\u0000\u0000"
          + "\u0000\u01a9\u01ac\u00030\u0018\u0000\u01aa\u01ac\u0003\u0006\u0003\u0000"
          + "\u01ab\u01a9\u0001\u0000\u0000\u0000\u01ab\u01aa\u0001\u0000\u0000\u0000"
          + "\u01ac/\u0001\u0000\u0000\u0000\u01ad\u01ae\u0003L&\u0000\u01ae\u01af"
          + "\u0005?\u0000\u0000\u01af\u01b0\u0003&\u0013\u0000\u01b0\u01c2\u0001\u0000"
          + "\u0000\u0000\u01b1\u01b2\u0003L&\u0000\u01b2\u01b3\u0005=\u0000\u0000"
          + "\u01b3\u01b4\u0003\u001a\r\u0000\u01b4\u01b5\u0005?\u0000\u0000\u01b5"
          + "\u01b6\u0003&\u0013\u0000\u01b6\u01c2\u0001\u0000\u0000\u0000\u01b7\u01b8"
          + "\u0003L&\u0000\u01b8\u01b9\u0005?\u0000\u0000\u01b9\u01ba\u0006\u0018"
          + "\uffff\uffff\u0000\u01ba\u01c2\u0001\u0000\u0000\u0000\u01bb\u01bc\u0003"
          + "L&\u0000\u01bc\u01bd\u0005=\u0000\u0000\u01bd\u01be\u0003\u001a\r\u0000"
          + "\u01be\u01bf\u0005?\u0000\u0000\u01bf\u01c0\u0006\u0018\uffff\uffff\u0000"
          + "\u01c0\u01c2\u0001\u0000\u0000\u0000\u01c1\u01ad\u0001\u0000\u0000\u0000"
          + "\u01c1\u01b1\u0001\u0000\u0000\u0000\u01c1\u01b7\u0001\u0000\u0000\u0000"
          + "\u01c1\u01bb\u0001\u0000\u0000\u0000\u01c21\u0001\u0000\u0000\u0000\u01c3"
          + "\u01c4\u0005\u001a\u0000\u0000\u01c4\u01c5\u0003&\u0013\u0000\u01c5\u01c6"
          + "\u0005\u001b\u0000\u0000\u01c6\u01c7\u0003&\u0013\u0000\u01c7\u01c8\u0005"
          + "\u001c\u0000\u0000\u01c8\u01c9\u0003&\u0013\u0000\u01c9\u01d6\u0001\u0000"
          + "\u0000\u0000\u01ca\u01cb\u0005\u001a\u0000\u0000\u01cb\u01cc\u0003&\u0013"
          + "\u0000\u01cc\u01cd\u0005\u001b\u0000\u0000\u01cd\u01ce\u0003&\u0013\u0000"
          + "\u01ce\u01cf\u0005\u001c\u0000\u0000\u01cf\u01d0\u0006\u0019\uffff\uffff"
          + "\u0000\u01d0\u01d6\u0001\u0000\u0000\u0000\u01d1\u01d2\u0005\u001a\u0000"
          + "\u0000\u01d2\u01d3\u0003&\u0013\u0000\u01d3\u01d4\u0006\u0019\uffff\uffff"
          + "\u0000\u01d4\u01d6\u0001\u0000\u0000\u0000\u01d5\u01c3\u0001\u0000\u0000"
          + "\u0000\u01d5\u01ca\u0001\u0000\u0000\u0000\u01d5\u01d1\u0001\u0000\u0000"
          + "\u0000\u01d63\u0001\u0000\u0000\u0000\u01d7\u01d9\u0005D\u0000\u0000\u01d8"
          + "\u01da\u00036\u001b\u0000\u01d9\u01d8\u0001\u0000\u0000\u0000\u01d9\u01da"
          + "\u0001\u0000\u0000\u0000\u01da\u01db\u0001\u0000\u0000\u0000\u01db\u01dc"
          + "\u0005E\u0000\u0000\u01dc5\u0001\u0000\u0000\u0000\u01dd\u01e2\u0003&"
          + "\u0013\u0000\u01de\u01df\u0005>\u0000\u0000\u01df\u01e1\u0003&\u0013\u0000"
          + "\u01e0\u01de\u0001\u0000\u0000\u0000\u01e1\u01e4\u0001\u0000\u0000\u0000"
          + "\u01e2\u01e0\u0001\u0000\u0000\u0000\u01e2\u01e3\u0001\u0000\u0000\u0000"
          + "\u01e3\u01e6\u0001\u0000\u0000\u0000\u01e4\u01e2\u0001\u0000\u0000\u0000"
          + "\u01e5\u01e7\u0005>\u0000\u0000\u01e6\u01e5\u0001\u0000\u0000\u0000\u01e6"
          + "\u01e7\u0001\u0000\u0000\u0000\u01e77\u0001\u0000\u0000\u0000\u01e8\u01ea"
          + "\u0005B\u0000\u0000\u01e9\u01eb\u0003:\u001d\u0000\u01ea\u01e9\u0001\u0000"
          + "\u0000\u0000\u01ea\u01eb\u0001\u0000\u0000\u0000\u01eb\u01ec\u0001\u0000"
          + "\u0000\u0000\u01ec\u01ed\u0005C\u0000\u0000\u01ed9\u0001\u0000\u0000\u0000"
          + "\u01ee\u01f3\u0003<\u001e\u0000\u01ef\u01f0\u0005>\u0000\u0000\u01f0\u01f2"
          + "\u0003<\u001e\u0000\u01f1\u01ef\u0001\u0000\u0000\u0000\u01f2\u01f5\u0001"
          + "\u0000\u0000\u0000\u01f3\u01f1\u0001\u0000\u0000\u0000\u01f3\u01f4\u0001"
          + "\u0000\u0000\u0000\u01f4\u01f7\u0001\u0000\u0000\u0000\u01f5\u01f3\u0001"
          + "\u0000\u0000\u0000\u01f6\u01f8\u0005>\u0000\u0000\u01f7\u01f6\u0001\u0000"
          + "\u0000\u0000\u01f7\u01f8\u0001\u0000\u0000\u0000\u01f8;\u0001\u0000\u0000"
          + "\u0000\u01f9\u01fa\u0003L&\u0000\u01fa\u01fb\u0005=\u0000\u0000\u01fb"
          + "\u01fc\u0003&\u0013\u0000\u01fc\u01ff\u0001\u0000\u0000\u0000\u01fd\u01ff"
          + "\u0003&\u0013\u0000\u01fe\u01f9\u0001\u0000\u0000\u0000\u01fe\u01fd\u0001"
          + "\u0000\u0000\u0000\u01ff=\u0001\u0000\u0000\u0000\u0200\u0202\u0007\u0000"
          + "\u0000\u0000\u0201\u0200\u0001\u0000\u0000\u0000\u0201\u0202\u0001\u0000"
          + "\u0000\u0000\u0202\u0203\u0001\u0000\u0000\u0000\u0203\u0204\u0003@ \u0000"
          + "\u0204?\u0001\u0000\u0000\u0000\u0205\u0206\u0007\u0001\u0000\u0000\u0206"
          + "A\u0001\u0000\u0000\u0000\u0207\u0208\u0007\u0002\u0000\u0000\u0208C\u0001"
          + "\u0000\u0000\u0000\u0209\u020c\u00055\u0000\u0000\u020a\u020c\u0003F#"
          + "\u0000\u020b\u0209\u0001\u0000\u0000\u0000\u020b\u020a\u0001\u0000\u0000"
          + "\u0000\u020cE\u0001\u0000\u0000\u0000\u020d\u0211\u00056\u0000\u0000\u020e"
          + "\u0210\u0005I\u0000\u0000\u020f\u020e\u0001\u0000\u0000\u0000\u0210\u0213"
          + "\u0001\u0000\u0000\u0000\u0211\u020f\u0001\u0000\u0000\u0000\u0211\u0212"
          + "\u0001\u0000\u0000\u0000\u0212\u0214\u0001\u0000\u0000\u0000\u0213\u0211"
          + "\u0001\u0000\u0000\u0000\u0214\u0215\u0007\u0003\u0000\u0000\u0215G\u0001"
          + "\u0000\u0000\u0000\u0216\u0217\u0007\u0004\u0000\u0000\u0217I\u0001\u0000"
          + "\u0000\u0000\u0218\u0219\u0007\u0005\u0000\u0000\u0219K\u0001\u0000\u0000"
          + "\u0000\u021a\u0221\u00057\u0000\u0000\u021b\u0221\u00058\u0000\u0000\u021c"
          + "\u0221\u0003B!\u0000\u021d\u0221\u0005\u0013\u0000\u0000\u021e\u0221\u0005"
          + "\u0011\u0000\u0000\u021f\u0221\u0005\u0012\u0000\u0000\u0220\u021a\u0001"
          + "\u0000\u0000\u0000\u0220\u021b\u0001\u0000\u0000\u0000\u0220\u021c\u0001"
          + "\u0000\u0000\u0000\u0220\u021d\u0001\u0000\u0000\u0000\u0220\u021e\u0001"
          + "\u0000\u0000\u0000\u0220\u021f\u0001\u0000\u0000\u0000\u0221M\u0001\u0000"
          + "\u0000\u0000\u0222\u0223\u0005H\u0000\u0000\u0223\u0224\u0005\u0015\u0000"
          + "\u0000\u0224\u0225\u0005;\u0000\u0000\u0225\u0226\u0003D\"\u0000\u0226"
          + "\u0227\u0005<\u0000\u0000\u0227O\u0001\u0000\u0000\u0000\u0228\u0229\u0005"
          + ";\u0000\u0000\u0229\u022a\u0003P(\u0000\u022a\u022b\u0005<\u0000\u0000"
          + "\u022b\u0233\u0001\u0000\u0000\u0000\u022c\u022d\u0005F\u0000\u0000\u022d"
          + "\u0233\u0005G\u0000\u0000\u022e\u022f\u0005G\u0000\u0000\u022f\u0233\u0005"
          + "F\u0000\u0000\u0230\u0233\u0005F\u0000\u0000\u0231\u0233\u0005G\u0000"
          + "\u0000\u0232\u0228\u0001\u0000\u0000\u0000\u0232\u022c\u0001\u0000\u0000"
          + "\u0000\u0232\u022e\u0001\u0000\u0000\u0000\u0232\u0230\u0001\u0000\u0000"
          + "\u0000\u0232\u0231\u0001\u0000\u0000\u0000\u0233Q\u0001\u0000\u0000\u0000"
          + "7X^bnvy|\u0081\u008b\u0092\u009a\u00a3\u00a7\u00ae\u00b5\u00bd\u00c0\u00c5"
          + "\u00cc\u00d1\u00e0\u00e5\u00e9\u00ed\u00f4\u0104\u0106\u010e\u0117\u011b"
          + "\u0143\u0164\u017f\u0181\u018e\u0198\u019b\u01a1\u01a5\u01a7\u01ab\u01c1"
          + "\u01d5\u01d9\u01e2\u01e6\u01ea\u01f3\u01f7\u01fe\u0201\u020b\u0211\u0220"
          + "\u0232";
  public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
