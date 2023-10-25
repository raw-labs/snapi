// Generated from Snapi.g4 by ANTLR 4.13.0
package antlr4_parser.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SnapiParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, TYPE_TOKEN=12, BOOL_TOKEN=13, STRING_TOKEN=14, LOCATION_TOKEN=15, 
		BINARY_TOKEN=16, BYTE_TOKEN=17, SHORT_TOKEN=18, INT_TOKEN=19, LONG_TOKEN=20, 
		FLOAT_TOKEN=21, DOUBLE_TOKEN=22, DECIMAL_TOKEN=23, DATE_TOKEN=24, TIME_TOKEN=25, 
		INTERVAL_TOKEN=26, TIMESTAMP_TOKEN=27, RECORD_TOKEN=28, COLLECTION_TOKEN=29, 
		LIST_TOKEN=30, LIBRARY_TOKEN=31, PACKAGE_TOKEN=32, LET_TOKEN=33, IN_TOKEN=34, 
		REC_TOKEN=35, UNDEFINED_TOKEN=36, IF_TOKEN=37, THEN_TOKEN=38, ELSE_TOKEN=39, 
		NULL_TOKEN=40, INTEGER=41, BYTE=42, SHORT=43, LONG=44, FLOAT=45, DOUBLE=46, 
		DECIMAL=47, EQ_TOKEN=48, NEQ_TOKEN=49, LE_TOKEN=50, LT_TOKEN=51, GE_TOKEN=52, 
		GT_TOKEN=53, PLUS_TOKEN=54, MINUS_TOKEN=55, MUL_TOKEN=56, DIV_TOKEN=57, 
		MOD_TOKEN=58, AND_TOKEN=59, OR_TOKEN=60, NOT_TOKEN=61, BOOL_CONST=62, 
		TRUE_TOKEN=63, FALSE_TOKEN=64, STRING=65, TRIPPLE_STRING=66, IDENT=67, 
		NON_ESC_IDENTIFIER=68, ESC_IDENTIFIER=69, WS=70, LINE_COMMENT=71;
	public static final int
		RULE_prog = 0, RULE_stat = 1, RULE_method_dec = 2, RULE_fun_dec = 3, RULE_fun_proto = 4, 
		RULE_fun_param = 5, RULE_attr = 6, RULE_type_attr = 7, RULE_fun_ar = 8, 
		RULE_fun_args = 9, RULE_fun_arg = 10, RULE_fun_abs = 11, RULE_type = 12, 
		RULE_record_type = 13, RULE_iterable_type = 14, RULE_list_type = 15, RULE_expr_type = 16, 
		RULE_expr = 17, RULE_let = 18, RULE_let_left = 19, RULE_let_decl = 20, 
		RULE_let_bind = 21, RULE_if_then_else = 22, RULE_lists = 23, RULE_lists_element = 24, 
		RULE_records = 25, RULE_record_elements = 26, RULE_record_element = 27, 
		RULE_number = 28, RULE_primitive_types = 29, RULE_compare_tokens = 30;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "stat", "method_dec", "fun_dec", "fun_proto", "fun_param", "attr", 
			"type_attr", "fun_ar", "fun_args", "fun_arg", "fun_abs", "type", "record_type", 
			"iterable_type", "list_type", "expr_type", "expr", "let", "let_left", 
			"let_decl", "let_bind", "if_then_else", "lists", "lists_element", "records", 
			"record_elements", "record_element", "number", "primitive_types", "compare_tokens"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'('", "','", "')'", "':'", "'->'", "'.'", "'['", "']'", 
			"'{'", "'}'", "'type'", "'bool'", "'string'", "'location'", "'binary'", 
			"'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'decimal'", 
			"'date'", "'time'", "'interval'", "'timestamp'", "'record'", "'collection'", 
			"'list'", "'library'", "'package'", "'let'", "'in'", "'rec'", "'undefined'", 
			"'if'", "'then'", "'else'", "'null'", null, null, null, null, null, null, 
			null, "'=='", "'!='", "'<='", "'<'", "'>='", "'>'", "'+'", "'-'", "'*'", 
			"'/'", "'%'", "'and'", "'or'", "'not'", null, "'true'", "'false'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"TYPE_TOKEN", "BOOL_TOKEN", "STRING_TOKEN", "LOCATION_TOKEN", "BINARY_TOKEN", 
			"BYTE_TOKEN", "SHORT_TOKEN", "INT_TOKEN", "LONG_TOKEN", "FLOAT_TOKEN", 
			"DOUBLE_TOKEN", "DECIMAL_TOKEN", "DATE_TOKEN", "TIME_TOKEN", "INTERVAL_TOKEN", 
			"TIMESTAMP_TOKEN", "RECORD_TOKEN", "COLLECTION_TOKEN", "LIST_TOKEN", 
			"LIBRARY_TOKEN", "PACKAGE_TOKEN", "LET_TOKEN", "IN_TOKEN", "REC_TOKEN", 
			"UNDEFINED_TOKEN", "IF_TOKEN", "THEN_TOKEN", "ELSE_TOKEN", "NULL_TOKEN", 
			"INTEGER", "BYTE", "SHORT", "LONG", "FLOAT", "DOUBLE", "DECIMAL", "EQ_TOKEN", 
			"NEQ_TOKEN", "LE_TOKEN", "LT_TOKEN", "GE_TOKEN", "GT_TOKEN", "PLUS_TOKEN", 
			"MINUS_TOKEN", "MUL_TOKEN", "DIV_TOKEN", "MOD_TOKEN", "AND_TOKEN", "OR_TOKEN", 
			"NOT_TOKEN", "BOOL_CONST", "TRUE_TOKEN", "FALSE_TOKEN", "STRING", "TRIPPLE_STRING", 
			"IDENT", "NON_ESC_IDENTIFIER", "ESC_IDENTIFIER", "WS", "LINE_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
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
	public String getGrammarFileName() { return "Snapi.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SnapiParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgContext extends ParserRuleContext {
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SnapiParser.EOF, 0); }
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			stat();
			setState(63);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatContext extends ParserRuleContext {
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
	 
		public StatContext() { }
		public void copyFrom(StatContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunDecExprStatContext extends StatContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<Method_decContext> method_dec() {
			return getRuleContexts(Method_decContext.class);
		}
		public Method_decContext method_dec(int i) {
			return getRuleContext(Method_decContext.class,i);
		}
		public FunDecExprStatContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunDecExprStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunDecExprStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunDecExprStat(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunDecStatContext extends StatContext {
		public List<Method_decContext> method_dec() {
			return getRuleContexts(Method_decContext.class);
		}
		public Method_decContext method_dec(int i) {
			return getRuleContext(Method_decContext.class,i);
		}
		public FunDecStatContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunDecStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunDecStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunDecStat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		StatContext _localctx = new StatContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_stat);
		int _la;
		try {
			int _alt;
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new FunDecStatContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IDENT) {
					{
					{
					setState(65);
					method_dec();
					}
					}
					setState(70);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new FunDecExprStatContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(74);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(71);
						method_dec();
						}
						} 
					}
					setState(76);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				setState(77);
				expr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Method_decContext extends ParserRuleContext {
		public Method_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_dec; }
	 
		public Method_decContext() { }
		public void copyFrom(Method_decContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MethodDecContext extends Method_decContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MethodDecContext(Method_decContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterMethodDec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitMethodDec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitMethodDec(this);
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
			setState(80);
			match(IDENT);
			setState(81);
			fun_proto();
			setState(82);
			match(T__0);
			setState(83);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fun_decContext extends ParserRuleContext {
		public Fun_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_dec; }
	 
		public Fun_decContext() { }
		public void copyFrom(Fun_decContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NormalFunContext extends Fun_decContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NormalFunContext(Fun_decContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNormalFun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNormalFun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNormalFun(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RecFunContext extends Fun_decContext {
		public TerminalNode REC_TOKEN() { return getToken(SnapiParser.REC_TOKEN, 0); }
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RecFunContext(Fun_decContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecFun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecFun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecFun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_decContext fun_dec() throws RecognitionException {
		Fun_decContext _localctx = new Fun_decContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_fun_dec);
		try {
			setState(96);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENT:
				_localctx = new NormalFunContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				match(IDENT);
				setState(86);
				fun_proto();
				setState(87);
				match(T__0);
				setState(88);
				expr(0);
				}
				break;
			case REC_TOKEN:
				_localctx = new RecFunContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(90);
				match(REC_TOKEN);
				setState(91);
				match(IDENT);
				setState(92);
				fun_proto();
				setState(93);
				match(T__0);
				setState(94);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fun_protoContext extends ParserRuleContext {
		public Fun_protoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_proto; }
	 
		public Fun_protoContext() { }
		public void copyFrom(Fun_protoContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunProtoWithTypeContext extends Fun_protoContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<Fun_paramContext> fun_param() {
			return getRuleContexts(Fun_paramContext.class);
		}
		public Fun_paramContext fun_param(int i) {
			return getRuleContext(Fun_paramContext.class,i);
		}
		public FunProtoWithTypeContext(Fun_protoContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunProtoWithType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunProtoWithType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunProtoWithType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunProtoWithoutTypeContext extends Fun_protoContext {
		public List<Fun_paramContext> fun_param() {
			return getRuleContexts(Fun_paramContext.class);
		}
		public Fun_paramContext fun_param(int i) {
			return getRuleContext(Fun_paramContext.class,i);
		}
		public FunProtoWithoutTypeContext(Fun_protoContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunProtoWithoutType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunProtoWithoutType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunProtoWithoutType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_protoContext fun_proto() throws RecognitionException {
		Fun_protoContext _localctx = new Fun_protoContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fun_proto);
		int _la;
		try {
			setState(124);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new FunProtoWithoutTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				match(T__1);
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(99);
					fun_param();
					setState(104);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__2) {
						{
						{
						setState(100);
						match(T__2);
						setState(101);
						fun_param();
						}
						}
						setState(106);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(109);
				match(T__3);
				}
				break;
			case 2:
				_localctx = new FunProtoWithTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(110);
				match(T__1);
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(111);
					fun_param();
					setState(116);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__2) {
						{
						{
						setState(112);
						match(T__2);
						setState(113);
						fun_param();
						}
						}
						setState(118);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(121);
				match(T__3);
				setState(122);
				match(T__4);
				setState(123);
				type(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fun_paramContext extends ParserRuleContext {
		public Fun_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_param; }
	 
		public Fun_paramContext() { }
		public void copyFrom(Fun_paramContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunParamAttrExprContext extends Fun_paramContext {
		public AttrContext attr() {
			return getRuleContext(AttrContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FunParamAttrExprContext(Fun_paramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunParamAttrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunParamAttrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunParamAttrExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunParamAttrContext extends Fun_paramContext {
		public AttrContext attr() {
			return getRuleContext(AttrContext.class,0);
		}
		public FunParamAttrContext(Fun_paramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunParamAttr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunParamAttr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunParamAttr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_paramContext fun_param() throws RecognitionException {
		Fun_paramContext _localctx = new Fun_paramContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_fun_param);
		try {
			setState(131);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new FunParamAttrContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(126);
				attr();
				}
				break;
			case 2:
				_localctx = new FunParamAttrExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(127);
				attr();
				setState(128);
				match(T__0);
				setState(129);
				expr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttrContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public AttrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterAttr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitAttr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitAttr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrContext attr() throws RecognitionException {
		AttrContext _localctx = new AttrContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_attr);
		try {
			setState(137);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(133);
				match(IDENT);
				setState(134);
				match(T__4);
				setState(135);
				type(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(136);
				match(IDENT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Type_attrContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Type_attrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_attr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterType_attr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitType_attr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitType_attr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_attrContext type_attr() throws RecognitionException {
		Type_attrContext _localctx = new Type_attrContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_type_attr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(IDENT);
			setState(140);
			match(T__4);
			setState(141);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fun_arContext extends ParserRuleContext {
		public Fun_argsContext fun_args() {
			return getRuleContext(Fun_argsContext.class,0);
		}
		public Fun_arContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_ar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_ar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_ar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_ar(this);
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
			setState(143);
			match(T__1);
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6971852744663504132L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 7L) != 0)) {
				{
				setState(144);
				fun_args();
				}
			}

			setState(147);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
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
			return getRuleContext(Fun_argContext.class,i);
		}
		public Fun_argsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_args(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_args(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_args(this);
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
			setState(149);
			fun_arg();
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(150);
				match(T__2);
				setState(151);
				fun_arg();
				}
				}
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fun_argContext extends ParserRuleContext {
		public Fun_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_arg; }
	 
		public Fun_argContext() { }
		public void copyFrom(Fun_argContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NamedFunArgExprContext extends Fun_argContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NamedFunArgExprContext(Fun_argContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNamedFunArgExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNamedFunArgExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNamedFunArgExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunArgExprContext extends Fun_argContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FunArgExprContext(Fun_argContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunArgExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunArgExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunArgExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_argContext fun_arg() throws RecognitionException {
		Fun_argContext _localctx = new Fun_argContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_fun_arg);
		try {
			setState(161);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new FunArgExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				expr(0);
				}
				break;
			case 2:
				_localctx = new NamedFunArgExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				match(IDENT);
				setState(159);
				match(T__0);
				setState(160);
				expr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Fun_absContext extends ParserRuleContext {
		public Fun_absContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_abs; }
	 
		public Fun_absContext() { }
		public void copyFrom(Fun_absContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunAbsContext extends Fun_absContext {
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FunAbsContext(Fun_absContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunAbs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunAbs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunAbs(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunAbsUnnamedContext extends Fun_absContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FunAbsUnnamedContext(Fun_absContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunAbsUnnamed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunAbsUnnamed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunAbsUnnamed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_absContext fun_abs() throws RecognitionException {
		Fun_absContext _localctx = new Fun_absContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_fun_abs);
		try {
			setState(170);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new FunAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(163);
				fun_proto();
				setState(164);
				match(T__5);
				setState(165);
				expr(0);
				}
				break;
			case IDENT:
				_localctx = new FunAbsUnnamedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(167);
				match(IDENT);
				setState(168);
				match(T__5);
				setState(169);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunTypeWithParamsTypeContext extends TypeContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<AttrContext> attr() {
			return getRuleContexts(AttrContext.class);
		}
		public AttrContext attr(int i) {
			return getRuleContext(AttrContext.class,i);
		}
		public FunTypeWithParamsTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunTypeWithParamsType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunTypeWithParamsType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunTypeWithParamsType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExprTypeTypeContext extends TypeContext {
		public Expr_typeContext expr_type() {
			return getRuleContext(Expr_typeContext.class,0);
		}
		public ExprTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterExprTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitExprTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitExprTypeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UndefinedTypeTypeContext extends TypeContext {
		public TerminalNode UNDEFINED_TOKEN() { return getToken(SnapiParser.UNDEFINED_TOKEN, 0); }
		public UndefinedTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterUndefinedTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitUndefinedTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitUndefinedTypeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RecordTypeTypeContext extends TypeContext {
		public Record_typeContext record_type() {
			return getRuleContext(Record_typeContext.class,0);
		}
		public RecordTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecordTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecordTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecordTypeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IterableTypeTypeContext extends TypeContext {
		public Iterable_typeContext iterable_type() {
			return getRuleContext(Iterable_typeContext.class,0);
		}
		public IterableTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterIterableTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitIterableTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitIterableTypeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypeWithParenTypeContext extends TypeContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TypeWithParenTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterTypeWithParenType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitTypeWithParenType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitTypeWithParenType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ListTypeTypeContext extends TypeContext {
		public List_typeContext list_type() {
			return getRuleContext(List_typeContext.class,0);
		}
		public ListTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterListTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitListTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitListTypeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimitiveTypeTypeContext extends TypeContext {
		public Primitive_typesContext primitive_types() {
			return getRuleContext(Primitive_typesContext.class,0);
		}
		public PrimitiveTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterPrimitiveTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitPrimitiveTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitPrimitiveTypeType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypeAliasTypeContext extends TypeContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public TypeAliasTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterTypeAliasType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitTypeAliasType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitTypeAliasType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunTypeTypeContext extends TypeContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public FunTypeTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunTypeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunTypeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunTypeType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_type, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				_localctx = new TypeWithParenTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(173);
				match(T__1);
				setState(174);
				type(0);
				setState(175);
				match(T__3);
				}
				break;
			case 2:
				{
				_localctx = new PrimitiveTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(177);
				primitive_types();
				}
				break;
			case 3:
				{
				_localctx = new UndefinedTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(178);
				match(UNDEFINED_TOKEN);
				}
				break;
			case 4:
				{
				_localctx = new TypeAliasTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(179);
				match(IDENT);
				}
				break;
			case 5:
				{
				_localctx = new RecordTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(180);
				record_type();
				}
				break;
			case 6:
				{
				_localctx = new IterableTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(181);
				iterable_type();
				}
				break;
			case 7:
				{
				_localctx = new ListTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(182);
				list_type();
				}
				break;
			case 8:
				{
				_localctx = new FunTypeWithParamsTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(183);
				match(T__1);
				setState(186);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
				case 1:
					{
					setState(184);
					type(0);
					}
					break;
				case 2:
					{
					setState(185);
					attr();
					}
					break;
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(188);
					match(T__2);
					setState(191);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
					case 1:
						{
						setState(189);
						type(0);
						}
						break;
					case 2:
						{
						setState(190);
						attr();
						}
						break;
					}
					}
					}
					setState(197);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(198);
				match(T__3);
				setState(199);
				match(T__5);
				setState(200);
				type(3);
				}
				break;
			case 9:
				{
				_localctx = new ExprTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(202);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(210);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new FunTypeTypeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(205);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(206);
					match(T__5);
					setState(207);
					type(3);
					}
					} 
				}
				setState(212);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Record_typeContext extends ParserRuleContext {
		public TerminalNode RECORD_TOKEN() { return getToken(SnapiParser.RECORD_TOKEN, 0); }
		public List<Type_attrContext> type_attr() {
			return getRuleContexts(Type_attrContext.class);
		}
		public Type_attrContext type_attr(int i) {
			return getRuleContext(Type_attrContext.class,i);
		}
		public Record_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecord_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecord_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecord_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Record_typeContext record_type() throws RecognitionException {
		Record_typeContext _localctx = new Record_typeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_record_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(RECORD_TOKEN);
			setState(214);
			match(T__1);
			setState(215);
			type_attr();
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(216);
				match(T__2);
				setState(217);
				type_attr();
				}
				}
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(223);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Iterable_typeContext extends ParserRuleContext {
		public TerminalNode COLLECTION_TOKEN() { return getToken(SnapiParser.COLLECTION_TOKEN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Iterable_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterable_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterIterable_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitIterable_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitIterable_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Iterable_typeContext iterable_type() throws RecognitionException {
		Iterable_typeContext _localctx = new Iterable_typeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_iterable_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			match(COLLECTION_TOKEN);
			setState(226);
			match(T__1);
			setState(227);
			type(0);
			setState(228);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class List_typeContext extends ParserRuleContext {
		public TerminalNode LIST_TOKEN() { return getToken(SnapiParser.LIST_TOKEN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_list_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterList_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitList_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitList_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final List_typeContext list_type() throws RecognitionException {
		List_typeContext _localctx = new List_typeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_list_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			match(LIST_TOKEN);
			setState(231);
			match(T__1);
			setState(232);
			type(0);
			setState(233);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Expr_typeContext extends ParserRuleContext {
		public TerminalNode TYPE_TOKEN() { return getToken(SnapiParser.TYPE_TOKEN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Expr_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterExpr_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitExpr_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitExpr_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expr_typeContext expr_type() throws RecognitionException {
		Expr_typeContext _localctx = new Expr_typeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_expr_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(TYPE_TOKEN);
			setState(236);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AndExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode AND_TOKEN() { return getToken(SnapiParser.AND_TOKEN, 0); }
		public AndExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterAndExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitAndExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitAndExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MulExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MUL_TOKEN() { return getToken(SnapiParser.MUL_TOKEN, 0); }
		public MulExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterMulExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitMulExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitMulExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringExprContext extends ExprContext {
		public TerminalNode STRING() { return getToken(SnapiParser.STRING, 0); }
		public StringExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterStringExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitStringExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitStringExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MinusUnaryExprContext extends ExprContext {
		public TerminalNode MINUS_TOKEN() { return getToken(SnapiParser.MINUS_TOKEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MinusUnaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterMinusUnaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitMinusUnaryExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitMinusUnaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NullExprContext extends ExprContext {
		public TerminalNode NULL_TOKEN() { return getToken(SnapiParser.NULL_TOKEN, 0); }
		public NullExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNullExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNullExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNullExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlusExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode PLUS_TOKEN() { return getToken(SnapiParser.PLUS_TOKEN, 0); }
		public PlusExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterPlusExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitPlusExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitPlusExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumberExprContext extends ExprContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public NumberExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNumberExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNumberExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNumberExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CompareExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Compare_tokensContext compare_tokens() {
			return getRuleContext(Compare_tokensContext.class,0);
		}
		public CompareExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterCompareExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitCompareExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitCompareExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlusUnaryExprContext extends ExprContext {
		public TerminalNode PLUS_TOKEN() { return getToken(SnapiParser.PLUS_TOKEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PlusUnaryExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterPlusUnaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitPlusUnaryExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitPlusUnaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ListExprContext extends ExprContext {
		public ListsContext lists() {
			return getRuleContext(ListsContext.class,0);
		}
		public ListExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterListExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitListExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitListExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExprContext extends ExprContext {
		public TerminalNode NOT_TOKEN() { return getToken(SnapiParser.NOT_TOKEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NotExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNotExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNotExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNotExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RecordExprContext extends ExprContext {
		public RecordsContext records() {
			return getRuleContext(RecordsContext.class,0);
		}
		public RecordExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecordExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecordExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecordExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MinusExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MINUS_TOKEN() { return getToken(SnapiParser.MINUS_TOKEN, 0); }
		public MinusExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterMinusExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitMinusExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitMinusExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BoolConstExprContext extends ExprContext {
		public TerminalNode BOOL_CONST() { return getToken(SnapiParser.BOOL_CONST, 0); }
		public BoolConstExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterBoolConstExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitBoolConstExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitBoolConstExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ProjectionExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public Fun_arContext fun_ar() {
			return getRuleContext(Fun_arContext.class,0);
		}
		public ProjectionExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterProjectionExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitProjectionExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitProjectionExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LetExprContext extends ExprContext {
		public LetContext let() {
			return getRuleContext(LetContext.class,0);
		}
		public LetExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLetExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLetExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLetExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunAbsExprContext extends ExprContext {
		public Fun_absContext fun_abs() {
			return getRuleContext(Fun_absContext.class,0);
		}
		public FunAbsExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunAbsExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunAbsExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunAbsExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunAppExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Fun_arContext fun_ar() {
			return getRuleContext(Fun_arContext.class,0);
		}
		public FunAppExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunAppExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunAppExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunAppExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OrExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode OR_TOKEN() { return getToken(SnapiParser.OR_TOKEN, 0); }
		public OrExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitOrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitOrExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfThenElseExprContext extends ExprContext {
		public If_then_elseContext if_then_else() {
			return getRuleContext(If_then_elseContext.class,0);
		}
		public IfThenElseExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterIfThenElseExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitIfThenElseExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitIfThenElseExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExprTypeExprContext extends ExprContext {
		public Expr_typeContext expr_type() {
			return getRuleContext(Expr_typeContext.class,0);
		}
		public ExprTypeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterExprTypeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitExprTypeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitExprTypeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DivExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode DIV_TOKEN() { return getToken(SnapiParser.DIV_TOKEN, 0); }
		public DivExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterDivExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitDivExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitDivExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TrippleStringExprContext extends ExprContext {
		public TerminalNode TRIPPLE_STRING() { return getToken(SnapiParser.TRIPPLE_STRING, 0); }
		public TrippleStringExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterTrippleStringExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitTrippleStringExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitTrippleStringExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ModExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MOD_TOKEN() { return getToken(SnapiParser.MOD_TOKEN, 0); }
		public ModExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterModExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitModExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitModExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExprContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterParenExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitParenExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitParenExpr(this);
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
		int _startState = 34;
		enterRecursionRule(_localctx, 34, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(239);
				match(T__1);
				setState(240);
				expr(0);
				setState(241);
				match(T__3);
				}
				break;
			case 2:
				{
				_localctx = new NumberExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(243);
				number();
				}
				break;
			case 3:
				{
				_localctx = new IfThenElseExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244);
				if_then_else();
				}
				break;
			case 4:
				{
				_localctx = new ListExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245);
				lists();
				}
				break;
			case 5:
				{
				_localctx = new RecordExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(246);
				records();
				}
				break;
			case 6:
				{
				_localctx = new BoolConstExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(247);
				match(BOOL_CONST);
				}
				break;
			case 7:
				{
				_localctx = new NullExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(248);
				match(NULL_TOKEN);
				}
				break;
			case 8:
				{
				_localctx = new TrippleStringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(249);
				match(TRIPPLE_STRING);
				}
				break;
			case 9:
				{
				_localctx = new StringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(250);
				match(STRING);
				}
				break;
			case 10:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(251);
				match(NOT_TOKEN);
				setState(252);
				expr(16);
				}
				break;
			case 11:
				{
				_localctx = new MinusUnaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(253);
				match(MINUS_TOKEN);
				setState(254);
				expr(12);
				}
				break;
			case 12:
				{
				_localctx = new PlusUnaryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(255);
				match(PLUS_TOKEN);
				setState(256);
				expr(11);
				}
				break;
			case 13:
				{
				_localctx = new FunAbsExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(257);
				fun_abs();
				}
				break;
			case 14:
				{
				_localctx = new LetExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(258);
				let();
				}
				break;
			case 15:
				{
				_localctx = new ExprTypeExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(259);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(297);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(295);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
					case 1:
						{
						_localctx = new AndExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(262);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(263);
						match(AND_TOKEN);
						setState(264);
						expr(16);
						}
						break;
					case 2:
						{
						_localctx = new OrExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(265);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(266);
						match(OR_TOKEN);
						setState(267);
						expr(15);
						}
						break;
					case 3:
						{
						_localctx = new CompareExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(268);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(269);
						compare_tokens();
						setState(270);
						expr(14);
						}
						break;
					case 4:
						{
						_localctx = new MulExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(272);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(273);
						match(MUL_TOKEN);
						setState(274);
						expr(11);
						}
						break;
					case 5:
						{
						_localctx = new DivExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(275);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(276);
						match(DIV_TOKEN);
						setState(277);
						expr(10);
						}
						break;
					case 6:
						{
						_localctx = new ModExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(278);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(279);
						match(MOD_TOKEN);
						setState(280);
						expr(9);
						}
						break;
					case 7:
						{
						_localctx = new PlusExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(281);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(282);
						match(PLUS_TOKEN);
						setState(283);
						expr(8);
						}
						break;
					case 8:
						{
						_localctx = new MinusExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(284);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(285);
						match(MINUS_TOKEN);
						setState(286);
						expr(7);
						}
						break;
					case 9:
						{
						_localctx = new FunAppExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(287);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(288);
						fun_ar();
						}
						break;
					case 10:
						{
						_localctx = new ProjectionExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(289);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(290);
						match(T__6);
						setState(291);
						match(IDENT);
						setState(293);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
						case 1:
							{
							setState(292);
							fun_ar();
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(299);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LetContext extends ParserRuleContext {
		public TerminalNode LET_TOKEN() { return getToken(SnapiParser.LET_TOKEN, 0); }
		public Let_leftContext let_left() {
			return getRuleContext(Let_leftContext.class,0);
		}
		public TerminalNode IN_TOKEN() { return getToken(SnapiParser.IN_TOKEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public LetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_let; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LetContext let() throws RecognitionException {
		LetContext _localctx = new LetContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_let);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			match(LET_TOKEN);
			setState(301);
			let_left();
			setState(302);
			match(IN_TOKEN);
			setState(303);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
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
			return getRuleContext(Let_declContext.class,i);
		}
		public Let_leftContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_let_left; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLet_left(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLet_left(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLet_left(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Let_leftContext let_left() throws RecognitionException {
		Let_leftContext _localctx = new Let_leftContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_let_left);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			let_decl();
			setState(310);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(306);
				match(T__2);
				setState(307);
				let_decl();
				}
				}
				setState(312);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Let_declContext extends ParserRuleContext {
		public Let_bindContext let_bind() {
			return getRuleContext(Let_bindContext.class,0);
		}
		public Fun_decContext fun_dec() {
			return getRuleContext(Fun_decContext.class,0);
		}
		public Let_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_let_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLet_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLet_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLet_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Let_declContext let_decl() throws RecognitionException {
		Let_declContext _localctx = new Let_declContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_let_decl);
		try {
			setState(315);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(313);
				let_bind();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(314);
				fun_dec();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Let_bindContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Let_bindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_let_bind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLet_bind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLet_bind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLet_bind(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Let_bindContext let_bind() throws RecognitionException {
		Let_bindContext _localctx = new Let_bindContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_let_bind);
		try {
			setState(326);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(317);
				match(IDENT);
				setState(318);
				match(T__0);
				setState(319);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(320);
				match(IDENT);
				setState(321);
				match(T__4);
				setState(322);
				type(0);
				setState(323);
				match(T__0);
				setState(324);
				expr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class If_then_elseContext extends ParserRuleContext {
		public TerminalNode IF_TOKEN() { return getToken(SnapiParser.IF_TOKEN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode THEN_TOKEN() { return getToken(SnapiParser.THEN_TOKEN, 0); }
		public TerminalNode ELSE_TOKEN() { return getToken(SnapiParser.ELSE_TOKEN, 0); }
		public If_then_elseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_then_else; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterIf_then_else(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitIf_then_else(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitIf_then_else(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_then_elseContext if_then_else() throws RecognitionException {
		If_then_elseContext _localctx = new If_then_elseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_if_then_else);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			match(IF_TOKEN);
			setState(329);
			expr(0);
			setState(330);
			match(THEN_TOKEN);
			setState(331);
			expr(0);
			setState(332);
			match(ELSE_TOKEN);
			setState(333);
			expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ListsContext extends ParserRuleContext {
		public Lists_elementContext lists_element() {
			return getRuleContext(Lists_elementContext.class,0);
		}
		public ListsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lists; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLists(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLists(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListsContext lists() throws RecognitionException {
		ListsContext _localctx = new ListsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_lists);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(T__7);
			setState(337);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6971852744663504132L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 7L) != 0)) {
				{
				setState(336);
				lists_element();
				}
			}

			setState(339);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
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
			return getRuleContext(ExprContext.class,i);
		}
		public Lists_elementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lists_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterLists_element(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitLists_element(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitLists_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Lists_elementContext lists_element() throws RecognitionException {
		Lists_elementContext _localctx = new Lists_elementContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_lists_element);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
			expr(0);
			setState(346);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(342);
				match(T__2);
				setState(343);
				expr(0);
				}
				}
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordsContext extends ParserRuleContext {
		public Record_elementsContext record_elements() {
			return getRuleContext(Record_elementsContext.class,0);
		}
		public RecordsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_records; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecords(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecords(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecords(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordsContext records() throws RecognitionException {
		RecordsContext _localctx = new RecordsContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_records);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			match(T__9);
			setState(351);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 6971852744663504132L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 7L) != 0)) {
				{
				setState(350);
				record_elements();
				}
			}

			setState(353);
			match(T__10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
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
			return getRuleContext(Record_elementContext.class,i);
		}
		public Record_elementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_elements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecord_elements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecord_elements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecord_elements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Record_elementsContext record_elements() throws RecognitionException {
		Record_elementsContext _localctx = new Record_elementsContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_record_elements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
			record_element();
			setState(360);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(356);
				match(T__2);
				setState(357);
				record_element();
				}
				}
				setState(362);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Record_elementContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Record_elementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecord_element(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecord_element(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecord_element(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Record_elementContext record_element() throws RecognitionException {
		Record_elementContext _localctx = new Record_elementContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_record_element);
		try {
			setState(367);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(363);
				match(IDENT);
				setState(364);
				match(T__4);
				setState(365);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(366);
				expr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public TerminalNode BYTE() { return getToken(SnapiParser.BYTE, 0); }
		public TerminalNode SHORT() { return getToken(SnapiParser.SHORT, 0); }
		public TerminalNode INTEGER() { return getToken(SnapiParser.INTEGER, 0); }
		public TerminalNode LONG() { return getToken(SnapiParser.LONG, 0); }
		public TerminalNode FLOAT() { return getToken(SnapiParser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(SnapiParser.DOUBLE, 0); }
		public TerminalNode DECIMAL() { return getToken(SnapiParser.DECIMAL, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 279275953455104L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Primitive_typesContext extends ParserRuleContext {
		public TerminalNode BOOL_TOKEN() { return getToken(SnapiParser.BOOL_TOKEN, 0); }
		public TerminalNode STRING_TOKEN() { return getToken(SnapiParser.STRING_TOKEN, 0); }
		public TerminalNode LOCATION_TOKEN() { return getToken(SnapiParser.LOCATION_TOKEN, 0); }
		public TerminalNode BINARY_TOKEN() { return getToken(SnapiParser.BINARY_TOKEN, 0); }
		public TerminalNode DATE_TOKEN() { return getToken(SnapiParser.DATE_TOKEN, 0); }
		public TerminalNode TIME_TOKEN() { return getToken(SnapiParser.TIME_TOKEN, 0); }
		public TerminalNode INTERVAL_TOKEN() { return getToken(SnapiParser.INTERVAL_TOKEN, 0); }
		public TerminalNode TIMESTAMP_TOKEN() { return getToken(SnapiParser.TIMESTAMP_TOKEN, 0); }
		public TerminalNode BYTE_TOKEN() { return getToken(SnapiParser.BYTE_TOKEN, 0); }
		public TerminalNode SHORT_TOKEN() { return getToken(SnapiParser.SHORT_TOKEN, 0); }
		public TerminalNode INT_TOKEN() { return getToken(SnapiParser.INT_TOKEN, 0); }
		public TerminalNode LONG_TOKEN() { return getToken(SnapiParser.LONG_TOKEN, 0); }
		public TerminalNode FLOAT_TOKEN() { return getToken(SnapiParser.FLOAT_TOKEN, 0); }
		public TerminalNode DOUBLE_TOKEN() { return getToken(SnapiParser.DOUBLE_TOKEN, 0); }
		public TerminalNode DECIMAL_TOKEN() { return getToken(SnapiParser.DECIMAL_TOKEN, 0); }
		public Primitive_typesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitive_types; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterPrimitive_types(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitPrimitive_types(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitPrimitive_types(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Primitive_typesContext primitive_types() throws RecognitionException {
		Primitive_typesContext _localctx = new Primitive_typesContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_primitive_types);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 268427264L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Compare_tokensContext extends ParserRuleContext {
		public TerminalNode EQ_TOKEN() { return getToken(SnapiParser.EQ_TOKEN, 0); }
		public TerminalNode NEQ_TOKEN() { return getToken(SnapiParser.NEQ_TOKEN, 0); }
		public TerminalNode LE_TOKEN() { return getToken(SnapiParser.LE_TOKEN, 0); }
		public TerminalNode LT_TOKEN() { return getToken(SnapiParser.LT_TOKEN, 0); }
		public TerminalNode GE_TOKEN() { return getToken(SnapiParser.GE_TOKEN, 0); }
		public TerminalNode GT_TOKEN() { return getToken(SnapiParser.GT_TOKEN, 0); }
		public Compare_tokensContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compare_tokens; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterCompare_tokens(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitCompare_tokens(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitCompare_tokens(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compare_tokensContext compare_tokens() throws RecognitionException {
		Compare_tokensContext _localctx = new Compare_tokensContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_compare_tokens);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(373);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 17732923532771328L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return type_sempred((TypeContext)_localctx, predIndex);
		case 17:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 15);
		case 2:
			return precpred(_ctx, 14);
		case 3:
			return precpred(_ctx, 13);
		case 4:
			return precpred(_ctx, 10);
		case 5:
			return precpred(_ctx, 9);
		case 6:
			return precpred(_ctx, 8);
		case 7:
			return precpred(_ctx, 7);
		case 8:
			return precpred(_ctx, 6);
		case 9:
			return precpred(_ctx, 5);
		case 10:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001G\u0178\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001C\b\u0001"+
		"\n\u0001\f\u0001F\t\u0001\u0001\u0001\u0005\u0001I\b\u0001\n\u0001\f\u0001"+
		"L\t\u0001\u0001\u0001\u0003\u0001O\b\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003a\b\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0005\u0004g\b\u0004\n\u0004\f\u0004j\t\u0004\u0003"+
		"\u0004l\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0005\u0004s\b\u0004\n\u0004\f\u0004v\t\u0004\u0003\u0004x\b\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004}\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u0084\b\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u008a\b\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0003"+
		"\b\u0092\b\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u0099\b\t"+
		"\n\t\f\t\u009c\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00a2\b\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u00ab\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0003\f\u00bb\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u00c0\b\f\u0005\f"+
		"\u00c2\b\f\n\f\f\f\u00c5\t\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003"+
		"\f\u00cc\b\f\u0001\f\u0001\f\u0001\f\u0005\f\u00d1\b\f\n\f\f\f\u00d4\t"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\r\u00db\b\r\n\r\f\r\u00de"+
		"\t\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0003\u0011\u0105\b\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u0126\b\u0011\u0005"+
		"\u0011\u0128\b\u0011\n\u0011\f\u0011\u012b\t\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0005\u0013\u0135\b\u0013\n\u0013\f\u0013\u0138\t\u0013\u0001\u0014\u0001"+
		"\u0014\u0003\u0014\u013c\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003"+
		"\u0015\u0147\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0003\u0017\u0152"+
		"\b\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0005"+
		"\u0018\u0159\b\u0018\n\u0018\f\u0018\u015c\t\u0018\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u0160\b\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0005\u001a\u0167\b\u001a\n\u001a\f\u001a\u016a\t\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0170\b\u001b\u0001"+
		"\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0000\u0002\u0018\"\u001f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<\u0000\u0003"+
		"\u0001\u0000)/\u0001\u0000\r\u001b\u0001\u000005\u0195\u0000>\u0001\u0000"+
		"\u0000\u0000\u0002N\u0001\u0000\u0000\u0000\u0004P\u0001\u0000\u0000\u0000"+
		"\u0006`\u0001\u0000\u0000\u0000\b|\u0001\u0000\u0000\u0000\n\u0083\u0001"+
		"\u0000\u0000\u0000\f\u0089\u0001\u0000\u0000\u0000\u000e\u008b\u0001\u0000"+
		"\u0000\u0000\u0010\u008f\u0001\u0000\u0000\u0000\u0012\u0095\u0001\u0000"+
		"\u0000\u0000\u0014\u00a1\u0001\u0000\u0000\u0000\u0016\u00aa\u0001\u0000"+
		"\u0000\u0000\u0018\u00cb\u0001\u0000\u0000\u0000\u001a\u00d5\u0001\u0000"+
		"\u0000\u0000\u001c\u00e1\u0001\u0000\u0000\u0000\u001e\u00e6\u0001\u0000"+
		"\u0000\u0000 \u00eb\u0001\u0000\u0000\u0000\"\u0104\u0001\u0000\u0000"+
		"\u0000$\u012c\u0001\u0000\u0000\u0000&\u0131\u0001\u0000\u0000\u0000("+
		"\u013b\u0001\u0000\u0000\u0000*\u0146\u0001\u0000\u0000\u0000,\u0148\u0001"+
		"\u0000\u0000\u0000.\u014f\u0001\u0000\u0000\u00000\u0155\u0001\u0000\u0000"+
		"\u00002\u015d\u0001\u0000\u0000\u00004\u0163\u0001\u0000\u0000\u00006"+
		"\u016f\u0001\u0000\u0000\u00008\u0171\u0001\u0000\u0000\u0000:\u0173\u0001"+
		"\u0000\u0000\u0000<\u0175\u0001\u0000\u0000\u0000>?\u0003\u0002\u0001"+
		"\u0000?@\u0005\u0000\u0000\u0001@\u0001\u0001\u0000\u0000\u0000AC\u0003"+
		"\u0004\u0002\u0000BA\u0001\u0000\u0000\u0000CF\u0001\u0000\u0000\u0000"+
		"DB\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EO\u0001\u0000\u0000"+
		"\u0000FD\u0001\u0000\u0000\u0000GI\u0003\u0004\u0002\u0000HG\u0001\u0000"+
		"\u0000\u0000IL\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000JK\u0001"+
		"\u0000\u0000\u0000KM\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000"+
		"MO\u0003\"\u0011\u0000ND\u0001\u0000\u0000\u0000NJ\u0001\u0000\u0000\u0000"+
		"O\u0003\u0001\u0000\u0000\u0000PQ\u0005C\u0000\u0000QR\u0003\b\u0004\u0000"+
		"RS\u0005\u0001\u0000\u0000ST\u0003\"\u0011\u0000T\u0005\u0001\u0000\u0000"+
		"\u0000UV\u0005C\u0000\u0000VW\u0003\b\u0004\u0000WX\u0005\u0001\u0000"+
		"\u0000XY\u0003\"\u0011\u0000Ya\u0001\u0000\u0000\u0000Z[\u0005#\u0000"+
		"\u0000[\\\u0005C\u0000\u0000\\]\u0003\b\u0004\u0000]^\u0005\u0001\u0000"+
		"\u0000^_\u0003\"\u0011\u0000_a\u0001\u0000\u0000\u0000`U\u0001\u0000\u0000"+
		"\u0000`Z\u0001\u0000\u0000\u0000a\u0007\u0001\u0000\u0000\u0000bk\u0005"+
		"\u0002\u0000\u0000ch\u0003\n\u0005\u0000de\u0005\u0003\u0000\u0000eg\u0003"+
		"\n\u0005\u0000fd\u0001\u0000\u0000\u0000gj\u0001\u0000\u0000\u0000hf\u0001"+
		"\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000il\u0001\u0000\u0000\u0000"+
		"jh\u0001\u0000\u0000\u0000kc\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000"+
		"\u0000lm\u0001\u0000\u0000\u0000m}\u0005\u0004\u0000\u0000nw\u0005\u0002"+
		"\u0000\u0000ot\u0003\n\u0005\u0000pq\u0005\u0003\u0000\u0000qs\u0003\n"+
		"\u0005\u0000rp\u0001\u0000\u0000\u0000sv\u0001\u0000\u0000\u0000tr\u0001"+
		"\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000ux\u0001\u0000\u0000\u0000"+
		"vt\u0001\u0000\u0000\u0000wo\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000"+
		"\u0000xy\u0001\u0000\u0000\u0000yz\u0005\u0004\u0000\u0000z{\u0005\u0005"+
		"\u0000\u0000{}\u0003\u0018\f\u0000|b\u0001\u0000\u0000\u0000|n\u0001\u0000"+
		"\u0000\u0000}\t\u0001\u0000\u0000\u0000~\u0084\u0003\f\u0006\u0000\u007f"+
		"\u0080\u0003\f\u0006\u0000\u0080\u0081\u0005\u0001\u0000\u0000\u0081\u0082"+
		"\u0003\"\u0011\u0000\u0082\u0084\u0001\u0000\u0000\u0000\u0083~\u0001"+
		"\u0000\u0000\u0000\u0083\u007f\u0001\u0000\u0000\u0000\u0084\u000b\u0001"+
		"\u0000\u0000\u0000\u0085\u0086\u0005C\u0000\u0000\u0086\u0087\u0005\u0005"+
		"\u0000\u0000\u0087\u008a\u0003\u0018\f\u0000\u0088\u008a\u0005C\u0000"+
		"\u0000\u0089\u0085\u0001\u0000\u0000\u0000\u0089\u0088\u0001\u0000\u0000"+
		"\u0000\u008a\r\u0001\u0000\u0000\u0000\u008b\u008c\u0005C\u0000\u0000"+
		"\u008c\u008d\u0005\u0005\u0000\u0000\u008d\u008e\u0003\u0018\f\u0000\u008e"+
		"\u000f\u0001\u0000\u0000\u0000\u008f\u0091\u0005\u0002\u0000\u0000\u0090"+
		"\u0092\u0003\u0012\t\u0000\u0091\u0090\u0001\u0000\u0000\u0000\u0091\u0092"+
		"\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093\u0094"+
		"\u0005\u0004\u0000\u0000\u0094\u0011\u0001\u0000\u0000\u0000\u0095\u009a"+
		"\u0003\u0014\n\u0000\u0096\u0097\u0005\u0003\u0000\u0000\u0097\u0099\u0003"+
		"\u0014\n\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0099\u009c\u0001\u0000"+
		"\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000"+
		"\u0000\u0000\u009b\u0013\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000"+
		"\u0000\u0000\u009d\u00a2\u0003\"\u0011\u0000\u009e\u009f\u0005C\u0000"+
		"\u0000\u009f\u00a0\u0005\u0001\u0000\u0000\u00a0\u00a2\u0003\"\u0011\u0000"+
		"\u00a1\u009d\u0001\u0000\u0000\u0000\u00a1\u009e\u0001\u0000\u0000\u0000"+
		"\u00a2\u0015\u0001\u0000\u0000\u0000\u00a3\u00a4\u0003\b\u0004\u0000\u00a4"+
		"\u00a5\u0005\u0006\u0000\u0000\u00a5\u00a6\u0003\"\u0011\u0000\u00a6\u00ab"+
		"\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005C\u0000\u0000\u00a8\u00a9\u0005"+
		"\u0006\u0000\u0000\u00a9\u00ab\u0003\"\u0011\u0000\u00aa\u00a3\u0001\u0000"+
		"\u0000\u0000\u00aa\u00a7\u0001\u0000\u0000\u0000\u00ab\u0017\u0001\u0000"+
		"\u0000\u0000\u00ac\u00ad\u0006\f\uffff\uffff\u0000\u00ad\u00ae\u0005\u0002"+
		"\u0000\u0000\u00ae\u00af\u0003\u0018\f\u0000\u00af\u00b0\u0005\u0004\u0000"+
		"\u0000\u00b0\u00cc\u0001\u0000\u0000\u0000\u00b1\u00cc\u0003:\u001d\u0000"+
		"\u00b2\u00cc\u0005$\u0000\u0000\u00b3\u00cc\u0005C\u0000\u0000\u00b4\u00cc"+
		"\u0003\u001a\r\u0000\u00b5\u00cc\u0003\u001c\u000e\u0000\u00b6\u00cc\u0003"+
		"\u001e\u000f\u0000\u00b7\u00ba\u0005\u0002\u0000\u0000\u00b8\u00bb\u0003"+
		"\u0018\f\u0000\u00b9\u00bb\u0003\f\u0006\u0000\u00ba\u00b8\u0001\u0000"+
		"\u0000\u0000\u00ba\u00b9\u0001\u0000\u0000\u0000\u00bb\u00c3\u0001\u0000"+
		"\u0000\u0000\u00bc\u00bf\u0005\u0003\u0000\u0000\u00bd\u00c0\u0003\u0018"+
		"\f\u0000\u00be\u00c0\u0003\f\u0006\u0000\u00bf\u00bd\u0001\u0000\u0000"+
		"\u0000\u00bf\u00be\u0001\u0000\u0000\u0000\u00c0\u00c2\u0001\u0000\u0000"+
		"\u0000\u00c1\u00bc\u0001\u0000\u0000\u0000\u00c2\u00c5\u0001\u0000\u0000"+
		"\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000"+
		"\u0000\u00c4\u00c6\u0001\u0000\u0000\u0000\u00c5\u00c3\u0001\u0000\u0000"+
		"\u0000\u00c6\u00c7\u0005\u0004\u0000\u0000\u00c7\u00c8\u0005\u0006\u0000"+
		"\u0000\u00c8\u00c9\u0003\u0018\f\u0003\u00c9\u00cc\u0001\u0000\u0000\u0000"+
		"\u00ca\u00cc\u0003 \u0010\u0000\u00cb\u00ac\u0001\u0000\u0000\u0000\u00cb"+
		"\u00b1\u0001\u0000\u0000\u0000\u00cb\u00b2\u0001\u0000\u0000\u0000\u00cb"+
		"\u00b3\u0001\u0000\u0000\u0000\u00cb\u00b4\u0001\u0000\u0000\u0000\u00cb"+
		"\u00b5\u0001\u0000\u0000\u0000\u00cb\u00b6\u0001\u0000\u0000\u0000\u00cb"+
		"\u00b7\u0001\u0000\u0000\u0000\u00cb\u00ca\u0001\u0000\u0000\u0000\u00cc"+
		"\u00d2\u0001\u0000\u0000\u0000\u00cd\u00ce\n\u0002\u0000\u0000\u00ce\u00cf"+
		"\u0005\u0006\u0000\u0000\u00cf\u00d1\u0003\u0018\f\u0003\u00d0\u00cd\u0001"+
		"\u0000\u0000\u0000\u00d1\u00d4\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001"+
		"\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u0019\u0001"+
		"\u0000\u0000\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d5\u00d6\u0005"+
		"\u001c\u0000\u0000\u00d6\u00d7\u0005\u0002\u0000\u0000\u00d7\u00dc\u0003"+
		"\u000e\u0007\u0000\u00d8\u00d9\u0005\u0003\u0000\u0000\u00d9\u00db\u0003"+
		"\u000e\u0007\u0000\u00da\u00d8\u0001\u0000\u0000\u0000\u00db\u00de\u0001"+
		"\u0000\u0000\u0000\u00dc\u00da\u0001\u0000\u0000\u0000\u00dc\u00dd\u0001"+
		"\u0000\u0000\u0000\u00dd\u00df\u0001\u0000\u0000\u0000\u00de\u00dc\u0001"+
		"\u0000\u0000\u0000\u00df\u00e0\u0005\u0004\u0000\u0000\u00e0\u001b\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e2\u0005\u001d\u0000\u0000\u00e2\u00e3\u0005"+
		"\u0002\u0000\u0000\u00e3\u00e4\u0003\u0018\f\u0000\u00e4\u00e5\u0005\u0004"+
		"\u0000\u0000\u00e5\u001d\u0001\u0000\u0000\u0000\u00e6\u00e7\u0005\u001e"+
		"\u0000\u0000\u00e7\u00e8\u0005\u0002\u0000\u0000\u00e8\u00e9\u0003\u0018"+
		"\f\u0000\u00e9\u00ea\u0005\u0004\u0000\u0000\u00ea\u001f\u0001\u0000\u0000"+
		"\u0000\u00eb\u00ec\u0005\f\u0000\u0000\u00ec\u00ed\u0003\u0018\f\u0000"+
		"\u00ed!\u0001\u0000\u0000\u0000\u00ee\u00ef\u0006\u0011\uffff\uffff\u0000"+
		"\u00ef\u00f0\u0005\u0002\u0000\u0000\u00f0\u00f1\u0003\"\u0011\u0000\u00f1"+
		"\u00f2\u0005\u0004\u0000\u0000\u00f2\u0105\u0001\u0000\u0000\u0000\u00f3"+
		"\u0105\u00038\u001c\u0000\u00f4\u0105\u0003,\u0016\u0000\u00f5\u0105\u0003"+
		".\u0017\u0000\u00f6\u0105\u00032\u0019\u0000\u00f7\u0105\u0005>\u0000"+
		"\u0000\u00f8\u0105\u0005(\u0000\u0000\u00f9\u0105\u0005B\u0000\u0000\u00fa"+
		"\u0105\u0005A\u0000\u0000\u00fb\u00fc\u0005=\u0000\u0000\u00fc\u0105\u0003"+
		"\"\u0011\u0010\u00fd\u00fe\u00057\u0000\u0000\u00fe\u0105\u0003\"\u0011"+
		"\f\u00ff\u0100\u00056\u0000\u0000\u0100\u0105\u0003\"\u0011\u000b\u0101"+
		"\u0105\u0003\u0016\u000b\u0000\u0102\u0105\u0003$\u0012\u0000\u0103\u0105"+
		"\u0003 \u0010\u0000\u0104\u00ee\u0001\u0000\u0000\u0000\u0104\u00f3\u0001"+
		"\u0000\u0000\u0000\u0104\u00f4\u0001\u0000\u0000\u0000\u0104\u00f5\u0001"+
		"\u0000\u0000\u0000\u0104\u00f6\u0001\u0000\u0000\u0000\u0104\u00f7\u0001"+
		"\u0000\u0000\u0000\u0104\u00f8\u0001\u0000\u0000\u0000\u0104\u00f9\u0001"+
		"\u0000\u0000\u0000\u0104\u00fa\u0001\u0000\u0000\u0000\u0104\u00fb\u0001"+
		"\u0000\u0000\u0000\u0104\u00fd\u0001\u0000\u0000\u0000\u0104\u00ff\u0001"+
		"\u0000\u0000\u0000\u0104\u0101\u0001\u0000\u0000\u0000\u0104\u0102\u0001"+
		"\u0000\u0000\u0000\u0104\u0103\u0001\u0000\u0000\u0000\u0105\u0129\u0001"+
		"\u0000\u0000\u0000\u0106\u0107\n\u000f\u0000\u0000\u0107\u0108\u0005;"+
		"\u0000\u0000\u0108\u0128\u0003\"\u0011\u0010\u0109\u010a\n\u000e\u0000"+
		"\u0000\u010a\u010b\u0005<\u0000\u0000\u010b\u0128\u0003\"\u0011\u000f"+
		"\u010c\u010d\n\r\u0000\u0000\u010d\u010e\u0003<\u001e\u0000\u010e\u010f"+
		"\u0003\"\u0011\u000e\u010f\u0128\u0001\u0000\u0000\u0000\u0110\u0111\n"+
		"\n\u0000\u0000\u0111\u0112\u00058\u0000\u0000\u0112\u0128\u0003\"\u0011"+
		"\u000b\u0113\u0114\n\t\u0000\u0000\u0114\u0115\u00059\u0000\u0000\u0115"+
		"\u0128\u0003\"\u0011\n\u0116\u0117\n\b\u0000\u0000\u0117\u0118\u0005:"+
		"\u0000\u0000\u0118\u0128\u0003\"\u0011\t\u0119\u011a\n\u0007\u0000\u0000"+
		"\u011a\u011b\u00056\u0000\u0000\u011b\u0128\u0003\"\u0011\b\u011c\u011d"+
		"\n\u0006\u0000\u0000\u011d\u011e\u00057\u0000\u0000\u011e\u0128\u0003"+
		"\"\u0011\u0007\u011f\u0120\n\u0005\u0000\u0000\u0120\u0128\u0003\u0010"+
		"\b\u0000\u0121\u0122\n\u0001\u0000\u0000\u0122\u0123\u0005\u0007\u0000"+
		"\u0000\u0123\u0125\u0005C\u0000\u0000\u0124\u0126\u0003\u0010\b\u0000"+
		"\u0125\u0124\u0001\u0000\u0000\u0000\u0125\u0126\u0001\u0000\u0000\u0000"+
		"\u0126\u0128\u0001\u0000\u0000\u0000\u0127\u0106\u0001\u0000\u0000\u0000"+
		"\u0127\u0109\u0001\u0000\u0000\u0000\u0127\u010c\u0001\u0000\u0000\u0000"+
		"\u0127\u0110\u0001\u0000\u0000\u0000\u0127\u0113\u0001\u0000\u0000\u0000"+
		"\u0127\u0116\u0001\u0000\u0000\u0000\u0127\u0119\u0001\u0000\u0000\u0000"+
		"\u0127\u011c\u0001\u0000\u0000\u0000\u0127\u011f\u0001\u0000\u0000\u0000"+
		"\u0127\u0121\u0001\u0000\u0000\u0000\u0128\u012b\u0001\u0000\u0000\u0000"+
		"\u0129\u0127\u0001\u0000\u0000\u0000\u0129\u012a\u0001\u0000\u0000\u0000"+
		"\u012a#\u0001\u0000\u0000\u0000\u012b\u0129\u0001\u0000\u0000\u0000\u012c"+
		"\u012d\u0005!\u0000\u0000\u012d\u012e\u0003&\u0013\u0000\u012e\u012f\u0005"+
		"\"\u0000\u0000\u012f\u0130\u0003\"\u0011\u0000\u0130%\u0001\u0000\u0000"+
		"\u0000\u0131\u0136\u0003(\u0014\u0000\u0132\u0133\u0005\u0003\u0000\u0000"+
		"\u0133\u0135\u0003(\u0014\u0000\u0134\u0132\u0001\u0000\u0000\u0000\u0135"+
		"\u0138\u0001\u0000\u0000\u0000\u0136\u0134\u0001\u0000\u0000\u0000\u0136"+
		"\u0137\u0001\u0000\u0000\u0000\u0137\'\u0001\u0000\u0000\u0000\u0138\u0136"+
		"\u0001\u0000\u0000\u0000\u0139\u013c\u0003*\u0015\u0000\u013a\u013c\u0003"+
		"\u0006\u0003\u0000\u013b\u0139\u0001\u0000\u0000\u0000\u013b\u013a\u0001"+
		"\u0000\u0000\u0000\u013c)\u0001\u0000\u0000\u0000\u013d\u013e\u0005C\u0000"+
		"\u0000\u013e\u013f\u0005\u0001\u0000\u0000\u013f\u0147\u0003\"\u0011\u0000"+
		"\u0140\u0141\u0005C\u0000\u0000\u0141\u0142\u0005\u0005\u0000\u0000\u0142"+
		"\u0143\u0003\u0018\f\u0000\u0143\u0144\u0005\u0001\u0000\u0000\u0144\u0145"+
		"\u0003\"\u0011\u0000\u0145\u0147\u0001\u0000\u0000\u0000\u0146\u013d\u0001"+
		"\u0000\u0000\u0000\u0146\u0140\u0001\u0000\u0000\u0000\u0147+\u0001\u0000"+
		"\u0000\u0000\u0148\u0149\u0005%\u0000\u0000\u0149\u014a\u0003\"\u0011"+
		"\u0000\u014a\u014b\u0005&\u0000\u0000\u014b\u014c\u0003\"\u0011\u0000"+
		"\u014c\u014d\u0005\'\u0000\u0000\u014d\u014e\u0003\"\u0011\u0000\u014e"+
		"-\u0001\u0000\u0000\u0000\u014f\u0151\u0005\b\u0000\u0000\u0150\u0152"+
		"\u00030\u0018\u0000\u0151\u0150\u0001\u0000\u0000\u0000\u0151\u0152\u0001"+
		"\u0000\u0000\u0000\u0152\u0153\u0001\u0000\u0000\u0000\u0153\u0154\u0005"+
		"\t\u0000\u0000\u0154/\u0001\u0000\u0000\u0000\u0155\u015a\u0003\"\u0011"+
		"\u0000\u0156\u0157\u0005\u0003\u0000\u0000\u0157\u0159\u0003\"\u0011\u0000"+
		"\u0158\u0156\u0001\u0000\u0000\u0000\u0159\u015c\u0001\u0000\u0000\u0000"+
		"\u015a\u0158\u0001\u0000\u0000\u0000\u015a\u015b\u0001\u0000\u0000\u0000"+
		"\u015b1\u0001\u0000\u0000\u0000\u015c\u015a\u0001\u0000\u0000\u0000\u015d"+
		"\u015f\u0005\n\u0000\u0000\u015e\u0160\u00034\u001a\u0000\u015f\u015e"+
		"\u0001\u0000\u0000\u0000\u015f\u0160\u0001\u0000\u0000\u0000\u0160\u0161"+
		"\u0001\u0000\u0000\u0000\u0161\u0162\u0005\u000b\u0000\u0000\u01623\u0001"+
		"\u0000\u0000\u0000\u0163\u0168\u00036\u001b\u0000\u0164\u0165\u0005\u0003"+
		"\u0000\u0000\u0165\u0167\u00036\u001b\u0000\u0166\u0164\u0001\u0000\u0000"+
		"\u0000\u0167\u016a\u0001\u0000\u0000\u0000\u0168\u0166\u0001\u0000\u0000"+
		"\u0000\u0168\u0169\u0001\u0000\u0000\u0000\u01695\u0001\u0000\u0000\u0000"+
		"\u016a\u0168\u0001\u0000\u0000\u0000\u016b\u016c\u0005C\u0000\u0000\u016c"+
		"\u016d\u0005\u0005\u0000\u0000\u016d\u0170\u0003\"\u0011\u0000\u016e\u0170"+
		"\u0003\"\u0011\u0000\u016f\u016b\u0001\u0000\u0000\u0000\u016f\u016e\u0001"+
		"\u0000\u0000\u0000\u01707\u0001\u0000\u0000\u0000\u0171\u0172\u0007\u0000"+
		"\u0000\u0000\u01729\u0001\u0000\u0000\u0000\u0173\u0174\u0007\u0001\u0000"+
		"\u0000\u0174;\u0001\u0000\u0000\u0000\u0175\u0176\u0007\u0002\u0000\u0000"+
		"\u0176=\u0001\u0000\u0000\u0000!DJN`hktw|\u0083\u0089\u0091\u009a\u00a1"+
		"\u00aa\u00ba\u00bf\u00c3\u00cb\u00d2\u00dc\u0104\u0125\u0127\u0129\u0136"+
		"\u013b\u0146\u0151\u015a\u015f\u0168\u016f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}