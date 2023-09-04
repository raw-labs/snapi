// Generated from Snapi.g4 by ANTLR 4.13.0
package raw.compiler.rql2.antlr4.generated;
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
		INTERVAL_TOKEN=26, TIMESTAMP_TOKEN=27, REGEX_TOKEN=28, RECORD_TOKEN=29, 
		COLLECTION_TOKEN=30, LIST_TOKEN=31, LIBRARY_TOKEN=32, PACKAGE_TOKEN=33, 
		LET_TOKEN=34, IN_TOKEN=35, REC_TOKEN=36, UNDEFINED_TOKEN=37, IF_TOKEN=38, 
		THEN_TOKEN=39, ELSE_TOKEN=40, NULL_TOKEN=41, INTEGER=42, BYTE=43, SHORT=44, 
		LONG=45, FLOAT=46, DOUBLE=47, DECIMAL=48, EQ_TOKEN=49, NEQ_TOKEN=50, LE_TOKEN=51, 
		LT_TOKEN=52, GE_TOKEN=53, GT_TOKEN=54, PLUS_TOKEN=55, MINUS_TOKEN=56, 
		MUL_TOKEN=57, DIV_TOKEN=58, MOD_TOKEN=59, AND_TOKEN=60, OR_TOKEN=61, NOT_TOKEN=62, 
		TRUE_TOKEN=63, FALSE_TOKEN=64, STRING=65, TRIPPLE_STRING=66, NON_ESC_IDENTIFIER=67, 
		ESC_IDENTIFIER=68, WS=69, LINE_COMMENT=70;
	public static final int
		RULE_prog = 0, RULE_stat = 1, RULE_fun_dec = 2, RULE_fun = 3, RULE_normal_fun = 4, 
		RULE_rec_fun = 5, RULE_fun_proto = 6, RULE_fun_params = 7, RULE_fun_param = 8, 
		RULE_attr = 9, RULE_fun_app = 10, RULE_fun_ar = 11, RULE_fun_args = 12, 
		RULE_fun_arg = 13, RULE_fun_abs = 14, RULE_type = 15, RULE_pure_type = 16, 
		RULE_record_type = 17, RULE_iterable_type = 18, RULE_list_type = 19, RULE_expr_type = 20, 
		RULE_typealias_type = 21, RULE_fun_type = 22, RULE_expr = 23, RULE_let = 24, 
		RULE_let_left = 25, RULE_let_decl = 26, RULE_let_bind = 27, RULE_if_then_else = 28, 
		RULE_lists = 29, RULE_lists_element = 30, RULE_records = 31, RULE_record_elements = 32, 
		RULE_record_element = 33, RULE_ident = 34, RULE_number = 35, RULE_number_type = 36, 
		RULE_premetive_type = 37, RULE_temporal_type = 38, RULE_compare_tokens = 39, 
		RULE_bool_const = 40;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "stat", "fun_dec", "fun", "normal_fun", "rec_fun", "fun_proto", 
			"fun_params", "fun_param", "attr", "fun_app", "fun_ar", "fun_args", "fun_arg", 
			"fun_abs", "type", "pure_type", "record_type", "iterable_type", "list_type", 
			"expr_type", "typealias_type", "fun_type", "expr", "let", "let_left", 
			"let_decl", "let_bind", "if_then_else", "lists", "lists_element", "records", 
			"record_elements", "record_element", "ident", "number", "number_type", 
			"premetive_type", "temporal_type", "compare_tokens", "bool_const"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'('", "')'", "','", "':'", "'->'", "'.'", "'['", "']'", 
			"'{'", "'}'", "'type'", "'bool'", "'string'", "'location'", "'binary'", 
			"'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'decimal'", 
			"'date'", "'time'", "'interval'", "'timestamp'", "'regex'", "'record'", 
			"'collection'", "'list'", "'library'", "'package'", "'let'", "'in'", 
			"'rec'", "'undefined'", "'if'", "'then'", "'else'", "'null'", null, null, 
			null, null, null, null, null, "'=='", "'!='", "'<='", "'<'", "'>='", 
			"'>'", "'+'", "'-'", "'*'", "'/'", "'%'", "'and'", "'or'", "'not'", "'true'", 
			"'false'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"TYPE_TOKEN", "BOOL_TOKEN", "STRING_TOKEN", "LOCATION_TOKEN", "BINARY_TOKEN", 
			"BYTE_TOKEN", "SHORT_TOKEN", "INT_TOKEN", "LONG_TOKEN", "FLOAT_TOKEN", 
			"DOUBLE_TOKEN", "DECIMAL_TOKEN", "DATE_TOKEN", "TIME_TOKEN", "INTERVAL_TOKEN", 
			"TIMESTAMP_TOKEN", "REGEX_TOKEN", "RECORD_TOKEN", "COLLECTION_TOKEN", 
			"LIST_TOKEN", "LIBRARY_TOKEN", "PACKAGE_TOKEN", "LET_TOKEN", "IN_TOKEN", 
			"REC_TOKEN", "UNDEFINED_TOKEN", "IF_TOKEN", "THEN_TOKEN", "ELSE_TOKEN", 
			"NULL_TOKEN", "INTEGER", "BYTE", "SHORT", "LONG", "FLOAT", "DOUBLE", 
			"DECIMAL", "EQ_TOKEN", "NEQ_TOKEN", "LE_TOKEN", "LT_TOKEN", "GE_TOKEN", 
			"GT_TOKEN", "PLUS_TOKEN", "MINUS_TOKEN", "MUL_TOKEN", "DIV_TOKEN", "MOD_TOKEN", 
			"AND_TOKEN", "OR_TOKEN", "NOT_TOKEN", "TRUE_TOKEN", "FALSE_TOKEN", "STRING", 
			"TRIPPLE_STRING", "NON_ESC_IDENTIFIER", "ESC_IDENTIFIER", "WS", "LINE_COMMENT"
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
			setState(82);
			stat();
			setState(83);
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
	public static class FunDecContext extends StatContext {
		public List<Fun_decContext> fun_dec() {
			return getRuleContexts(Fun_decContext.class);
		}
		public Fun_decContext fun_dec(int i) {
			return getRuleContext(Fun_decContext.class,i);
		}
		public FunDecContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunDec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunDec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunDec(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunDecExprContext extends StatContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<Fun_decContext> fun_dec() {
			return getRuleContexts(Fun_decContext.class);
		}
		public Fun_decContext fun_dec(int i) {
			return getRuleContext(Fun_decContext.class,i);
		}
		public FunDecExprContext(StatContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunDecExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunDecExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunDecExpr(this);
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
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new FunDecContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 36)) & ~0x3f) == 0 && ((1L << (_la - 36)) & 6442450945L) != 0)) {
					{
					{
					setState(85);
					fun_dec();
					}
					}
					setState(90);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new FunDecExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(94);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(91);
						fun_dec();
						}
						} 
					}
					setState(96);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				setState(97);
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
	public static class Fun_decContext extends ParserRuleContext {
		public FunContext fun() {
			return getRuleContext(FunContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Fun_decContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_dec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_dec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_dec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_dec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_decContext fun_dec() throws RecognitionException {
		Fun_decContext _localctx = new Fun_decContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_fun_dec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			fun();
			setState(101);
			match(T__0);
			setState(102);
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
	public static class FunContext extends ParserRuleContext {
		public Normal_funContext normal_fun() {
			return getRuleContext(Normal_funContext.class,0);
		}
		public Rec_funContext rec_fun() {
			return getRuleContext(Rec_funContext.class,0);
		}
		public FunContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunContext fun() throws RecognitionException {
		FunContext _localctx = new FunContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_fun);
		try {
			setState(106);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NON_ESC_IDENTIFIER:
			case ESC_IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(104);
				normal_fun();
				}
				break;
			case REC_TOKEN:
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
				rec_fun();
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
	public static class Normal_funContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public Normal_funContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normal_fun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNormal_fun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNormal_fun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNormal_fun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Normal_funContext normal_fun() throws RecognitionException {
		Normal_funContext _localctx = new Normal_funContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_normal_fun);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			ident();
			setState(109);
			fun_proto();
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
	public static class Rec_funContext extends ParserRuleContext {
		public TerminalNode REC_TOKEN() { return getToken(SnapiParser.REC_TOKEN, 0); }
		public Normal_funContext normal_fun() {
			return getRuleContext(Normal_funContext.class,0);
		}
		public Rec_funContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rec_fun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRec_fun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRec_fun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRec_fun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rec_funContext rec_fun() throws RecognitionException {
		Rec_funContext _localctx = new Rec_funContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rec_fun);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(REC_TOKEN);
			setState(112);
			normal_fun();
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
		public Fun_paramsContext fun_params() {
			return getRuleContext(Fun_paramsContext.class,0);
		}
		public Fun_protoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_proto; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_proto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_proto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_proto(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_protoContext fun_proto() throws RecognitionException {
		Fun_protoContext _localctx = new Fun_protoContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_fun_proto);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(T__1);
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NON_ESC_IDENTIFIER || _la==ESC_IDENTIFIER) {
				{
				setState(115);
				fun_params();
				}
			}

			setState(118);
			match(T__2);
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
	public static class Fun_paramsContext extends ParserRuleContext {
		public List<Fun_paramContext> fun_param() {
			return getRuleContexts(Fun_paramContext.class);
		}
		public Fun_paramContext fun_param(int i) {
			return getRuleContext(Fun_paramContext.class,i);
		}
		public Fun_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_params(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_params(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_params(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_paramsContext fun_params() throws RecognitionException {
		Fun_paramsContext _localctx = new Fun_paramsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_fun_params);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			fun_param();
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(121);
				match(T__3);
				setState(122);
				fun_param();
				}
				}
				setState(127);
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
	public static class Fun_paramContext extends ParserRuleContext {
		public AttrContext attr() {
			return getRuleContext(AttrContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Fun_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_param(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_paramContext fun_param() throws RecognitionException {
		Fun_paramContext _localctx = new Fun_paramContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_fun_param);
		try {
			setState(133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(128);
				attr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(129);
				attr();
				setState(130);
				match(T__0);
				setState(131);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
		enterRule(_localctx, 18, RULE_attr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			ident();
			setState(136);
			match(T__4);
			setState(137);
			type();
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
	public static class Fun_appContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Fun_arContext fun_ar() {
			return getRuleContext(Fun_arContext.class,0);
		}
		public Fun_appContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_app; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_app(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_app(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_app(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_appContext fun_app() throws RecognitionException {
		Fun_appContext _localctx = new Fun_appContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_fun_app);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			ident();
			setState(140);
			fun_ar();
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
		enterRule(_localctx, 22, RULE_fun_ar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			match(T__1);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503038584382548732L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(143);
				fun_args();
				}
			}

			setState(146);
			match(T__2);
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
		enterRule(_localctx, 24, RULE_fun_args);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			fun_arg();
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(149);
				match(T__3);
				setState(150);
				fun_arg();
				}
				}
				setState(155);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Fun_absContext fun_abs() {
			return getRuleContext(Fun_absContext.class,0);
		}
		public Fun_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_arg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_arg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_arg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_argContext fun_arg() throws RecognitionException {
		Fun_argContext _localctx = new Fun_argContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_fun_arg);
		try {
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
				ident();
				setState(158);
				match(T__0);
				setState(159);
				expr(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(161);
				fun_abs();
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
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Fun_absContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_abs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_abs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_abs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_abs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_absContext fun_abs() throws RecognitionException {
		Fun_absContext _localctx = new Fun_absContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_fun_abs);
		try {
			setState(172);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				fun_proto();
				setState(165);
				match(T__5);
				setState(166);
				expr(0);
				}
				break;
			case NON_ESC_IDENTIFIER:
			case ESC_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(168);
				ident();
				setState(169);
				match(T__5);
				setState(170);
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
		public List<Pure_typeContext> pure_type() {
			return getRuleContexts(Pure_typeContext.class);
		}
		public Pure_typeContext pure_type(int i) {
			return getRuleContext(Pure_typeContext.class,i);
		}
		public List<TerminalNode> OR_TOKEN() { return getTokens(SnapiParser.OR_TOKEN); }
		public TerminalNode OR_TOKEN(int i) {
			return getToken(SnapiParser.OR_TOKEN, i);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_type);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			pure_type();
			setState(179);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(175);
					match(OR_TOKEN);
					setState(176);
					pure_type();
					}
					} 
				}
				setState(181);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
	public static class Pure_typeContext extends ParserRuleContext {
		public Premetive_typeContext premetive_type() {
			return getRuleContext(Premetive_typeContext.class,0);
		}
		public TerminalNode UNDEFINED_TOKEN() { return getToken(SnapiParser.UNDEFINED_TOKEN, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Record_typeContext record_type() {
			return getRuleContext(Record_typeContext.class,0);
		}
		public Iterable_typeContext iterable_type() {
			return getRuleContext(Iterable_typeContext.class,0);
		}
		public List_typeContext list_type() {
			return getRuleContext(List_typeContext.class,0);
		}
		public Fun_typeContext fun_type() {
			return getRuleContext(Fun_typeContext.class,0);
		}
		public Expr_typeContext expr_type() {
			return getRuleContext(Expr_typeContext.class,0);
		}
		public Pure_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pure_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterPure_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitPure_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitPure_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pure_typeContext pure_type() throws RecognitionException {
		Pure_typeContext _localctx = new Pure_typeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_pure_type);
		try {
			setState(190);
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
			case REGEX_TOKEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(182);
				premetive_type();
				}
				break;
			case UNDEFINED_TOKEN:
				enterOuterAlt(_localctx, 2);
				{
				setState(183);
				match(UNDEFINED_TOKEN);
				}
				break;
			case NON_ESC_IDENTIFIER:
			case ESC_IDENTIFIER:
				enterOuterAlt(_localctx, 3);
				{
				setState(184);
				ident();
				}
				break;
			case RECORD_TOKEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(185);
				record_type();
				}
				break;
			case COLLECTION_TOKEN:
				enterOuterAlt(_localctx, 5);
				{
				setState(186);
				iterable_type();
				}
				break;
			case LIST_TOKEN:
				enterOuterAlt(_localctx, 6);
				{
				setState(187);
				list_type();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 7);
				{
				setState(188);
				fun_type();
				}
				break;
			case TYPE_TOKEN:
				enterOuterAlt(_localctx, 8);
				{
				setState(189);
				expr_type();
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
	public static class Record_typeContext extends ParserRuleContext {
		public TerminalNode RECORD_TOKEN() { return getToken(SnapiParser.RECORD_TOKEN, 0); }
		public List<AttrContext> attr() {
			return getRuleContexts(AttrContext.class);
		}
		public AttrContext attr(int i) {
			return getRuleContext(AttrContext.class,i);
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
		enterRule(_localctx, 34, RULE_record_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(RECORD_TOKEN);
			setState(193);
			match(T__1);
			setState(194);
			attr();
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(195);
				match(T__3);
				setState(196);
				attr();
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
			match(T__2);
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
		enterRule(_localctx, 36, RULE_iterable_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			match(COLLECTION_TOKEN);
			setState(205);
			match(T__1);
			setState(206);
			type();
			setState(207);
			match(T__2);
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
		enterRule(_localctx, 38, RULE_list_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(LIST_TOKEN);
			setState(210);
			match(T__1);
			setState(211);
			type();
			setState(212);
			match(T__2);
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
		enterRule(_localctx, 40, RULE_expr_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(TYPE_TOKEN);
			setState(215);
			type();
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
	public static class Typealias_typeContext extends ParserRuleContext {
		public TerminalNode TYPE_TOKEN() { return getToken(SnapiParser.TYPE_TOKEN, 0); }
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public Typealias_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typealias_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterTypealias_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitTypealias_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitTypealias_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Typealias_typeContext typealias_type() throws RecognitionException {
		Typealias_typeContext _localctx = new Typealias_typeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_typealias_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(TYPE_TOKEN);
			setState(218);
			type();
			setState(219);
			match(T__0);
			setState(220);
			type();
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
	public static class Fun_typeContext extends ParserRuleContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public Fun_paramsContext fun_params() {
			return getRuleContext(Fun_paramsContext.class,0);
		}
		public Fun_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFun_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFun_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFun_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_typeContext fun_type() throws RecognitionException {
		Fun_typeContext _localctx = new Fun_typeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_fun_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			match(T__1);
			setState(225);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(223);
				fun_params();
				}
				break;
			case 2:
				{
				setState(224);
				type();
				}
				break;
			}
			setState(227);
			match(T__2);
			setState(228);
			match(T__5);
			setState(229);
			type();
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
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public If_then_elseContext if_then_else() {
			return getRuleContext(If_then_elseContext.class,0);
		}
		public ListsContext lists() {
			return getRuleContext(ListsContext.class,0);
		}
		public RecordsContext records() {
			return getRuleContext(RecordsContext.class,0);
		}
		public Bool_constContext bool_const() {
			return getRuleContext(Bool_constContext.class,0);
		}
		public TerminalNode NULL_TOKEN() { return getToken(SnapiParser.NULL_TOKEN, 0); }
		public TerminalNode TRIPPLE_STRING() { return getToken(SnapiParser.TRIPPLE_STRING, 0); }
		public TerminalNode STRING() { return getToken(SnapiParser.STRING, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode NOT_TOKEN() { return getToken(SnapiParser.NOT_TOKEN, 0); }
		public TerminalNode MINUS_TOKEN() { return getToken(SnapiParser.MINUS_TOKEN, 0); }
		public TerminalNode PLUS_TOKEN() { return getToken(SnapiParser.PLUS_TOKEN, 0); }
		public Fun_appContext fun_app() {
			return getRuleContext(Fun_appContext.class,0);
		}
		public LetContext let() {
			return getRuleContext(LetContext.class,0);
		}
		public Expr_typeContext expr_type() {
			return getRuleContext(Expr_typeContext.class,0);
		}
		public TerminalNode AND_TOKEN() { return getToken(SnapiParser.AND_TOKEN, 0); }
		public TerminalNode OR_TOKEN() { return getToken(SnapiParser.OR_TOKEN, 0); }
		public Compare_tokensContext compare_tokens() {
			return getRuleContext(Compare_tokensContext.class,0);
		}
		public TerminalNode MUL_TOKEN() { return getToken(SnapiParser.MUL_TOKEN, 0); }
		public TerminalNode DIV_TOKEN() { return getToken(SnapiParser.DIV_TOKEN, 0); }
		public TerminalNode MOD_TOKEN() { return getToken(SnapiParser.MOD_TOKEN, 0); }
		public Fun_arContext fun_ar() {
			return getRuleContext(Fun_arContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitExpr(this);
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
		int _startState = 46;
		enterRecursionRule(_localctx, 46, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(232);
				number();
				}
				break;
			case 2:
				{
				setState(233);
				if_then_else();
				}
				break;
			case 3:
				{
				setState(234);
				lists();
				}
				break;
			case 4:
				{
				setState(235);
				records();
				}
				break;
			case 5:
				{
				setState(236);
				bool_const();
				}
				break;
			case 6:
				{
				setState(237);
				match(NULL_TOKEN);
				}
				break;
			case 7:
				{
				setState(238);
				match(TRIPPLE_STRING);
				}
				break;
			case 8:
				{
				setState(239);
				match(STRING);
				}
				break;
			case 9:
				{
				setState(240);
				match(T__1);
				setState(241);
				expr(0);
				setState(242);
				match(T__2);
				}
				break;
			case 10:
				{
				setState(244);
				bool_const();
				}
				break;
			case 11:
				{
				setState(245);
				ident();
				}
				break;
			case 12:
				{
				setState(246);
				match(NOT_TOKEN);
				setState(247);
				expr(15);
				}
				break;
			case 13:
				{
				setState(248);
				match(MINUS_TOKEN);
				setState(249);
				expr(11);
				}
				break;
			case 14:
				{
				setState(250);
				match(PLUS_TOKEN);
				setState(251);
				expr(10);
				}
				break;
			case 15:
				{
				setState(252);
				fun_app();
				}
				break;
			case 16:
				{
				setState(253);
				let();
				}
				break;
			case 17:
				{
				setState(254);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(290);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(288);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(257);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(258);
						match(AND_TOKEN);
						setState(259);
						expr(15);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(260);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(261);
						match(OR_TOKEN);
						setState(262);
						expr(14);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(263);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(264);
						compare_tokens();
						setState(265);
						expr(13);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(267);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(268);
						match(MUL_TOKEN);
						setState(269);
						expr(10);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(270);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(271);
						match(DIV_TOKEN);
						setState(272);
						expr(9);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(273);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(274);
						match(MOD_TOKEN);
						setState(275);
						expr(8);
						}
						break;
					case 7:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(276);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(277);
						match(PLUS_TOKEN);
						setState(278);
						expr(7);
						}
						break;
					case 8:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(279);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(280);
						match(MINUS_TOKEN);
						setState(281);
						expr(6);
						}
						break;
					case 9:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(282);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(283);
						match(T__6);
						setState(284);
						ident();
						setState(286);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
						case 1:
							{
							setState(285);
							fun_ar();
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(292);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
		enterRule(_localctx, 48, RULE_let);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(LET_TOKEN);
			setState(294);
			let_left();
			setState(295);
			match(IN_TOKEN);
			setState(296);
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
		enterRule(_localctx, 50, RULE_let_left);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			let_decl();
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(299);
				match(T__3);
				setState(300);
				let_decl();
				}
				}
				setState(305);
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
		enterRule(_localctx, 52, RULE_let_decl);
		try {
			setState(308);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(306);
				let_bind();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(307);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
		enterRule(_localctx, 54, RULE_let_bind);
		try {
			setState(320);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(310);
				ident();
				setState(311);
				match(T__0);
				setState(312);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(314);
				ident();
				setState(315);
				match(T__4);
				setState(316);
				type();
				setState(317);
				match(T__0);
				setState(318);
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
		enterRule(_localctx, 56, RULE_if_then_else);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(IF_TOKEN);
			setState(323);
			expr(0);
			setState(324);
			match(THEN_TOKEN);
			setState(325);
			expr(0);
			setState(326);
			match(ELSE_TOKEN);
			setState(327);
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
		enterRule(_localctx, 58, RULE_lists);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			match(T__7);
			setState(331);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503038584382548732L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(330);
				lists_element();
				}
			}

			setState(333);
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
		enterRule(_localctx, 60, RULE_lists_element);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			expr(0);
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(336);
				match(T__3);
				setState(337);
				expr(0);
				}
				}
				setState(342);
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
		enterRule(_localctx, 62, RULE_records);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			match(T__9);
			setState(345);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -4503038584382548732L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 31L) != 0)) {
				{
				setState(344);
				record_elements();
				}
			}

			setState(347);
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
		enterRule(_localctx, 64, RULE_record_elements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			record_element();
			setState(354);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(350);
				match(T__3);
				setState(351);
				record_element();
				}
				}
				setState(356);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
		enterRule(_localctx, 66, RULE_record_element);
		try {
			setState(362);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(357);
				ident();
				setState(358);
				match(T__4);
				setState(359);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(361);
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
	public static class IdentContext extends ParserRuleContext {
		public TerminalNode NON_ESC_IDENTIFIER() { return getToken(SnapiParser.NON_ESC_IDENTIFIER, 0); }
		public TerminalNode ESC_IDENTIFIER() { return getToken(SnapiParser.ESC_IDENTIFIER, 0); }
		public IdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ident; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitIdent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitIdent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentContext ident() throws RecognitionException {
		IdentContext _localctx = new IdentContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_ident);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			_la = _input.LA(1);
			if ( !(_la==NON_ESC_IDENTIFIER || _la==ESC_IDENTIFIER) ) {
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
		enterRule(_localctx, 70, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 558551906910208L) != 0)) ) {
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
	public static class Number_typeContext extends ParserRuleContext {
		public TerminalNode BYTE_TOKEN() { return getToken(SnapiParser.BYTE_TOKEN, 0); }
		public TerminalNode SHORT_TOKEN() { return getToken(SnapiParser.SHORT_TOKEN, 0); }
		public TerminalNode INT_TOKEN() { return getToken(SnapiParser.INT_TOKEN, 0); }
		public TerminalNode LONG_TOKEN() { return getToken(SnapiParser.LONG_TOKEN, 0); }
		public TerminalNode FLOAT_TOKEN() { return getToken(SnapiParser.FLOAT_TOKEN, 0); }
		public TerminalNode DOUBLE_TOKEN() { return getToken(SnapiParser.DOUBLE_TOKEN, 0); }
		public TerminalNode DECIMAL_TOKEN() { return getToken(SnapiParser.DECIMAL_TOKEN, 0); }
		public Number_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNumber_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNumber_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNumber_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Number_typeContext number_type() throws RecognitionException {
		Number_typeContext _localctx = new Number_typeContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_number_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(368);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16646144L) != 0)) ) {
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
	public static class Premetive_typeContext extends ParserRuleContext {
		public TerminalNode BOOL_TOKEN() { return getToken(SnapiParser.BOOL_TOKEN, 0); }
		public TerminalNode STRING_TOKEN() { return getToken(SnapiParser.STRING_TOKEN, 0); }
		public TerminalNode LOCATION_TOKEN() { return getToken(SnapiParser.LOCATION_TOKEN, 0); }
		public TerminalNode BINARY_TOKEN() { return getToken(SnapiParser.BINARY_TOKEN, 0); }
		public Number_typeContext number_type() {
			return getRuleContext(Number_typeContext.class,0);
		}
		public Temporal_typeContext temporal_type() {
			return getRuleContext(Temporal_typeContext.class,0);
		}
		public TerminalNode REGEX_TOKEN() { return getToken(SnapiParser.REGEX_TOKEN, 0); }
		public Premetive_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_premetive_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterPremetive_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitPremetive_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitPremetive_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Premetive_typeContext premetive_type() throws RecognitionException {
		Premetive_typeContext _localctx = new Premetive_typeContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_premetive_type);
		try {
			setState(377);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOL_TOKEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(370);
				match(BOOL_TOKEN);
				}
				break;
			case STRING_TOKEN:
				enterOuterAlt(_localctx, 2);
				{
				setState(371);
				match(STRING_TOKEN);
				}
				break;
			case LOCATION_TOKEN:
				enterOuterAlt(_localctx, 3);
				{
				setState(372);
				match(LOCATION_TOKEN);
				}
				break;
			case BINARY_TOKEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(373);
				match(BINARY_TOKEN);
				}
				break;
			case BYTE_TOKEN:
			case SHORT_TOKEN:
			case INT_TOKEN:
			case LONG_TOKEN:
			case FLOAT_TOKEN:
			case DOUBLE_TOKEN:
			case DECIMAL_TOKEN:
				enterOuterAlt(_localctx, 5);
				{
				setState(374);
				number_type();
				}
				break;
			case DATE_TOKEN:
			case TIME_TOKEN:
			case INTERVAL_TOKEN:
			case TIMESTAMP_TOKEN:
				enterOuterAlt(_localctx, 6);
				{
				setState(375);
				temporal_type();
				}
				break;
			case REGEX_TOKEN:
				enterOuterAlt(_localctx, 7);
				{
				setState(376);
				match(REGEX_TOKEN);
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
	public static class Temporal_typeContext extends ParserRuleContext {
		public TerminalNode DATE_TOKEN() { return getToken(SnapiParser.DATE_TOKEN, 0); }
		public TerminalNode TIME_TOKEN() { return getToken(SnapiParser.TIME_TOKEN, 0); }
		public TerminalNode INTERVAL_TOKEN() { return getToken(SnapiParser.INTERVAL_TOKEN, 0); }
		public TerminalNode TIMESTAMP_TOKEN() { return getToken(SnapiParser.TIMESTAMP_TOKEN, 0); }
		public Temporal_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_temporal_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterTemporal_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitTemporal_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitTemporal_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Temporal_typeContext temporal_type() throws RecognitionException {
		Temporal_typeContext _localctx = new Temporal_typeContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_temporal_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 251658240L) != 0)) ) {
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
		enterRule(_localctx, 78, RULE_compare_tokens);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(381);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 35465847065542656L) != 0)) ) {
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
	public static class Bool_constContext extends ParserRuleContext {
		public TerminalNode TRUE_TOKEN() { return getToken(SnapiParser.TRUE_TOKEN, 0); }
		public TerminalNode FALSE_TOKEN() { return getToken(SnapiParser.FALSE_TOKEN, 0); }
		public Bool_constContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool_const; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterBool_const(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitBool_const(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitBool_const(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bool_constContext bool_const() throws RecognitionException {
		Bool_constContext _localctx = new Bool_constContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_bool_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(383);
			_la = _input.LA(1);
			if ( !(_la==TRUE_TOKEN || _la==FALSE_TOKEN) ) {
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
		case 23:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 14);
		case 1:
			return precpred(_ctx, 13);
		case 2:
			return precpred(_ctx, 12);
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
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001F\u0182\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001W"+
		"\b\u0001\n\u0001\f\u0001Z\t\u0001\u0001\u0001\u0005\u0001]\b\u0001\n\u0001"+
		"\f\u0001`\t\u0001\u0001\u0001\u0003\u0001c\b\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0003\u0003k\b\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0003\u0006u\b\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007|\b\u0007\n\u0007\f\u0007"+
		"\u007f\t\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u0086\b"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0091\b\u000b\u0001\u000b\u0001\u000b\u0001\f"+
		"\u0001\f\u0001\f\u0005\f\u0098\b\f\n\f\f\f\u009b\t\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0003\r\u00a3\b\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003"+
		"\u000e\u00ad\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u00b2"+
		"\b\u000f\n\u000f\f\u000f\u00b5\t\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010"+
		"\u00bf\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0005\u0011\u00c6\b\u0011\n\u0011\f\u0011\u00c9\t\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00e2\b\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0100\b\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u011f"+
		"\b\u0017\u0005\u0017\u0121\b\u0017\n\u0017\f\u0017\u0124\t\u0017\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0005\u0019\u012e\b\u0019\n\u0019\f\u0019\u0131\t\u0019"+
		"\u0001\u001a\u0001\u001a\u0003\u001a\u0135\b\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0003\u001b\u0141\b\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001d"+
		"\u0001\u001d\u0003\u001d\u014c\b\u001d\u0001\u001d\u0001\u001d\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0005\u001e\u0153\b\u001e\n\u001e\f\u001e\u0156"+
		"\t\u001e\u0001\u001f\u0001\u001f\u0003\u001f\u015a\b\u001f\u0001\u001f"+
		"\u0001\u001f\u0001 \u0001 \u0001 \u0005 \u0161\b \n \f \u0164\t \u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0003!\u016b\b!\u0001\"\u0001\"\u0001#\u0001"+
		"#\u0001$\u0001$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003"+
		"%\u017a\b%\u0001&\u0001&\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0000\u0001"+
		".)\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468:<>@BDFHJLNP\u0000\u0006\u0001\u0000CD\u0001"+
		"\u0000*0\u0001\u0000\u0011\u0017\u0001\u0000\u0018\u001b\u0001\u00001"+
		"6\u0001\u0000?@\u0197\u0000R\u0001\u0000\u0000\u0000\u0002b\u0001\u0000"+
		"\u0000\u0000\u0004d\u0001\u0000\u0000\u0000\u0006j\u0001\u0000\u0000\u0000"+
		"\bl\u0001\u0000\u0000\u0000\no\u0001\u0000\u0000\u0000\fr\u0001\u0000"+
		"\u0000\u0000\u000ex\u0001\u0000\u0000\u0000\u0010\u0085\u0001\u0000\u0000"+
		"\u0000\u0012\u0087\u0001\u0000\u0000\u0000\u0014\u008b\u0001\u0000\u0000"+
		"\u0000\u0016\u008e\u0001\u0000\u0000\u0000\u0018\u0094\u0001\u0000\u0000"+
		"\u0000\u001a\u00a2\u0001\u0000\u0000\u0000\u001c\u00ac\u0001\u0000\u0000"+
		"\u0000\u001e\u00ae\u0001\u0000\u0000\u0000 \u00be\u0001\u0000\u0000\u0000"+
		"\"\u00c0\u0001\u0000\u0000\u0000$\u00cc\u0001\u0000\u0000\u0000&\u00d1"+
		"\u0001\u0000\u0000\u0000(\u00d6\u0001\u0000\u0000\u0000*\u00d9\u0001\u0000"+
		"\u0000\u0000,\u00de\u0001\u0000\u0000\u0000.\u00ff\u0001\u0000\u0000\u0000"+
		"0\u0125\u0001\u0000\u0000\u00002\u012a\u0001\u0000\u0000\u00004\u0134"+
		"\u0001\u0000\u0000\u00006\u0140\u0001\u0000\u0000\u00008\u0142\u0001\u0000"+
		"\u0000\u0000:\u0149\u0001\u0000\u0000\u0000<\u014f\u0001\u0000\u0000\u0000"+
		">\u0157\u0001\u0000\u0000\u0000@\u015d\u0001\u0000\u0000\u0000B\u016a"+
		"\u0001\u0000\u0000\u0000D\u016c\u0001\u0000\u0000\u0000F\u016e\u0001\u0000"+
		"\u0000\u0000H\u0170\u0001\u0000\u0000\u0000J\u0179\u0001\u0000\u0000\u0000"+
		"L\u017b\u0001\u0000\u0000\u0000N\u017d\u0001\u0000\u0000\u0000P\u017f"+
		"\u0001\u0000\u0000\u0000RS\u0003\u0002\u0001\u0000ST\u0005\u0000\u0000"+
		"\u0001T\u0001\u0001\u0000\u0000\u0000UW\u0003\u0004\u0002\u0000VU\u0001"+
		"\u0000\u0000\u0000WZ\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000"+
		"XY\u0001\u0000\u0000\u0000Yc\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000"+
		"\u0000[]\u0003\u0004\u0002\u0000\\[\u0001\u0000\u0000\u0000]`\u0001\u0000"+
		"\u0000\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_a\u0001"+
		"\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000ac\u0003.\u0017\u0000bX\u0001"+
		"\u0000\u0000\u0000b^\u0001\u0000\u0000\u0000c\u0003\u0001\u0000\u0000"+
		"\u0000de\u0003\u0006\u0003\u0000ef\u0005\u0001\u0000\u0000fg\u0003.\u0017"+
		"\u0000g\u0005\u0001\u0000\u0000\u0000hk\u0003\b\u0004\u0000ik\u0003\n"+
		"\u0005\u0000jh\u0001\u0000\u0000\u0000ji\u0001\u0000\u0000\u0000k\u0007"+
		"\u0001\u0000\u0000\u0000lm\u0003D\"\u0000mn\u0003\f\u0006\u0000n\t\u0001"+
		"\u0000\u0000\u0000op\u0005$\u0000\u0000pq\u0003\b\u0004\u0000q\u000b\u0001"+
		"\u0000\u0000\u0000rt\u0005\u0002\u0000\u0000su\u0003\u000e\u0007\u0000"+
		"ts\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000"+
		"\u0000vw\u0005\u0003\u0000\u0000w\r\u0001\u0000\u0000\u0000x}\u0003\u0010"+
		"\b\u0000yz\u0005\u0004\u0000\u0000z|\u0003\u0010\b\u0000{y\u0001\u0000"+
		"\u0000\u0000|\u007f\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000"+
		"}~\u0001\u0000\u0000\u0000~\u000f\u0001\u0000\u0000\u0000\u007f}\u0001"+
		"\u0000\u0000\u0000\u0080\u0086\u0003\u0012\t\u0000\u0081\u0082\u0003\u0012"+
		"\t\u0000\u0082\u0083\u0005\u0001\u0000\u0000\u0083\u0084\u0003.\u0017"+
		"\u0000\u0084\u0086\u0001\u0000\u0000\u0000\u0085\u0080\u0001\u0000\u0000"+
		"\u0000\u0085\u0081\u0001\u0000\u0000\u0000\u0086\u0011\u0001\u0000\u0000"+
		"\u0000\u0087\u0088\u0003D\"\u0000\u0088\u0089\u0005\u0005\u0000\u0000"+
		"\u0089\u008a\u0003\u001e\u000f\u0000\u008a\u0013\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0003D\"\u0000\u008c\u008d\u0003\u0016\u000b\u0000\u008d"+
		"\u0015\u0001\u0000\u0000\u0000\u008e\u0090\u0005\u0002\u0000\u0000\u008f"+
		"\u0091\u0003\u0018\f\u0000\u0090\u008f\u0001\u0000\u0000\u0000\u0090\u0091"+
		"\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0093"+
		"\u0005\u0003\u0000\u0000\u0093\u0017\u0001\u0000\u0000\u0000\u0094\u0099"+
		"\u0003\u001a\r\u0000\u0095\u0096\u0005\u0004\u0000\u0000\u0096\u0098\u0003"+
		"\u001a\r\u0000\u0097\u0095\u0001\u0000\u0000\u0000\u0098\u009b\u0001\u0000"+
		"\u0000\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000"+
		"\u0000\u0000\u009a\u0019\u0001\u0000\u0000\u0000\u009b\u0099\u0001\u0000"+
		"\u0000\u0000\u009c\u00a3\u0003.\u0017\u0000\u009d\u009e\u0003D\"\u0000"+
		"\u009e\u009f\u0005\u0001\u0000\u0000\u009f\u00a0\u0003.\u0017\u0000\u00a0"+
		"\u00a3\u0001\u0000\u0000\u0000\u00a1\u00a3\u0003\u001c\u000e\u0000\u00a2"+
		"\u009c\u0001\u0000\u0000\u0000\u00a2\u009d\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a1\u0001\u0000\u0000\u0000\u00a3\u001b\u0001\u0000\u0000\u0000\u00a4"+
		"\u00a5\u0003\f\u0006\u0000\u00a5\u00a6\u0005\u0006\u0000\u0000\u00a6\u00a7"+
		"\u0003.\u0017\u0000\u00a7\u00ad\u0001\u0000\u0000\u0000\u00a8\u00a9\u0003"+
		"D\"\u0000\u00a9\u00aa\u0005\u0006\u0000\u0000\u00aa\u00ab\u0003.\u0017"+
		"\u0000\u00ab\u00ad\u0001\u0000\u0000\u0000\u00ac\u00a4\u0001\u0000\u0000"+
		"\u0000\u00ac\u00a8\u0001\u0000\u0000\u0000\u00ad\u001d\u0001\u0000\u0000"+
		"\u0000\u00ae\u00b3\u0003 \u0010\u0000\u00af\u00b0\u0005=\u0000\u0000\u00b0"+
		"\u00b2\u0003 \u0010\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b2\u00b5"+
		"\u0001\u0000\u0000\u0000\u00b3\u00b1\u0001\u0000\u0000\u0000\u00b3\u00b4"+
		"\u0001\u0000\u0000\u0000\u00b4\u001f\u0001\u0000\u0000\u0000\u00b5\u00b3"+
		"\u0001\u0000\u0000\u0000\u00b6\u00bf\u0003J%\u0000\u00b7\u00bf\u0005%"+
		"\u0000\u0000\u00b8\u00bf\u0003D\"\u0000\u00b9\u00bf\u0003\"\u0011\u0000"+
		"\u00ba\u00bf\u0003$\u0012\u0000\u00bb\u00bf\u0003&\u0013\u0000\u00bc\u00bf"+
		"\u0003,\u0016\u0000\u00bd\u00bf\u0003(\u0014\u0000\u00be\u00b6\u0001\u0000"+
		"\u0000\u0000\u00be\u00b7\u0001\u0000\u0000\u0000\u00be\u00b8\u0001\u0000"+
		"\u0000\u0000\u00be\u00b9\u0001\u0000\u0000\u0000\u00be\u00ba\u0001\u0000"+
		"\u0000\u0000\u00be\u00bb\u0001\u0000\u0000\u0000\u00be\u00bc\u0001\u0000"+
		"\u0000\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00bf!\u0001\u0000\u0000"+
		"\u0000\u00c0\u00c1\u0005\u001d\u0000\u0000\u00c1\u00c2\u0005\u0002\u0000"+
		"\u0000\u00c2\u00c7\u0003\u0012\t\u0000\u00c3\u00c4\u0005\u0004\u0000\u0000"+
		"\u00c4\u00c6\u0003\u0012\t\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c6"+
		"\u00c9\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7"+
		"\u00c8\u0001\u0000\u0000\u0000\u00c8\u00ca\u0001\u0000\u0000\u0000\u00c9"+
		"\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cb\u0005\u0003\u0000\u0000\u00cb"+
		"#\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005\u001e\u0000\u0000\u00cd\u00ce"+
		"\u0005\u0002\u0000\u0000\u00ce\u00cf\u0003\u001e\u000f\u0000\u00cf\u00d0"+
		"\u0005\u0003\u0000\u0000\u00d0%\u0001\u0000\u0000\u0000\u00d1\u00d2\u0005"+
		"\u001f\u0000\u0000\u00d2\u00d3\u0005\u0002\u0000\u0000\u00d3\u00d4\u0003"+
		"\u001e\u000f\u0000\u00d4\u00d5\u0005\u0003\u0000\u0000\u00d5\'\u0001\u0000"+
		"\u0000\u0000\u00d6\u00d7\u0005\f\u0000\u0000\u00d7\u00d8\u0003\u001e\u000f"+
		"\u0000\u00d8)\u0001\u0000\u0000\u0000\u00d9\u00da\u0005\f\u0000\u0000"+
		"\u00da\u00db\u0003\u001e\u000f\u0000\u00db\u00dc\u0005\u0001\u0000\u0000"+
		"\u00dc\u00dd\u0003\u001e\u000f\u0000\u00dd+\u0001\u0000\u0000\u0000\u00de"+
		"\u00e1\u0005\u0002\u0000\u0000\u00df\u00e2\u0003\u000e\u0007\u0000\u00e0"+
		"\u00e2\u0003\u001e\u000f\u0000\u00e1\u00df\u0001\u0000\u0000\u0000\u00e1"+
		"\u00e0\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2"+
		"\u00e3\u0001\u0000\u0000\u0000\u00e3\u00e4\u0005\u0003\u0000\u0000\u00e4"+
		"\u00e5\u0005\u0006\u0000\u0000\u00e5\u00e6\u0003\u001e\u000f\u0000\u00e6"+
		"-\u0001\u0000\u0000\u0000\u00e7\u00e8\u0006\u0017\uffff\uffff\u0000\u00e8"+
		"\u0100\u0003F#\u0000\u00e9\u0100\u00038\u001c\u0000\u00ea\u0100\u0003"+
		":\u001d\u0000\u00eb\u0100\u0003>\u001f\u0000\u00ec\u0100\u0003P(\u0000"+
		"\u00ed\u0100\u0005)\u0000\u0000\u00ee\u0100\u0005B\u0000\u0000\u00ef\u0100"+
		"\u0005A\u0000\u0000\u00f0\u00f1\u0005\u0002\u0000\u0000\u00f1\u00f2\u0003"+
		".\u0017\u0000\u00f2\u00f3\u0005\u0003\u0000\u0000\u00f3\u0100\u0001\u0000"+
		"\u0000\u0000\u00f4\u0100\u0003P(\u0000\u00f5\u0100\u0003D\"\u0000\u00f6"+
		"\u00f7\u0005>\u0000\u0000\u00f7\u0100\u0003.\u0017\u000f\u00f8\u00f9\u0005"+
		"8\u0000\u0000\u00f9\u0100\u0003.\u0017\u000b\u00fa\u00fb\u00057\u0000"+
		"\u0000\u00fb\u0100\u0003.\u0017\n\u00fc\u0100\u0003\u0014\n\u0000\u00fd"+
		"\u0100\u00030\u0018\u0000\u00fe\u0100\u0003(\u0014\u0000\u00ff\u00e7\u0001"+
		"\u0000\u0000\u0000\u00ff\u00e9\u0001\u0000\u0000\u0000\u00ff\u00ea\u0001"+
		"\u0000\u0000\u0000\u00ff\u00eb\u0001\u0000\u0000\u0000\u00ff\u00ec\u0001"+
		"\u0000\u0000\u0000\u00ff\u00ed\u0001\u0000\u0000\u0000\u00ff\u00ee\u0001"+
		"\u0000\u0000\u0000\u00ff\u00ef\u0001\u0000\u0000\u0000\u00ff\u00f0\u0001"+
		"\u0000\u0000\u0000\u00ff\u00f4\u0001\u0000\u0000\u0000\u00ff\u00f5\u0001"+
		"\u0000\u0000\u0000\u00ff\u00f6\u0001\u0000\u0000\u0000\u00ff\u00f8\u0001"+
		"\u0000\u0000\u0000\u00ff\u00fa\u0001\u0000\u0000\u0000\u00ff\u00fc\u0001"+
		"\u0000\u0000\u0000\u00ff\u00fd\u0001\u0000\u0000\u0000\u00ff\u00fe\u0001"+
		"\u0000\u0000\u0000\u0100\u0122\u0001\u0000\u0000\u0000\u0101\u0102\n\u000e"+
		"\u0000\u0000\u0102\u0103\u0005<\u0000\u0000\u0103\u0121\u0003.\u0017\u000f"+
		"\u0104\u0105\n\r\u0000\u0000\u0105\u0106\u0005=\u0000\u0000\u0106\u0121"+
		"\u0003.\u0017\u000e\u0107\u0108\n\f\u0000\u0000\u0108\u0109\u0003N\'\u0000"+
		"\u0109\u010a\u0003.\u0017\r\u010a\u0121\u0001\u0000\u0000\u0000\u010b"+
		"\u010c\n\t\u0000\u0000\u010c\u010d\u00059\u0000\u0000\u010d\u0121\u0003"+
		".\u0017\n\u010e\u010f\n\b\u0000\u0000\u010f\u0110\u0005:\u0000\u0000\u0110"+
		"\u0121\u0003.\u0017\t\u0111\u0112\n\u0007\u0000\u0000\u0112\u0113\u0005"+
		";\u0000\u0000\u0113\u0121\u0003.\u0017\b\u0114\u0115\n\u0006\u0000\u0000"+
		"\u0115\u0116\u00057\u0000\u0000\u0116\u0121\u0003.\u0017\u0007\u0117\u0118"+
		"\n\u0005\u0000\u0000\u0118\u0119\u00058\u0000\u0000\u0119\u0121\u0003"+
		".\u0017\u0006\u011a\u011b\n\u0001\u0000\u0000\u011b\u011c\u0005\u0007"+
		"\u0000\u0000\u011c\u011e\u0003D\"\u0000\u011d\u011f\u0003\u0016\u000b"+
		"\u0000\u011e\u011d\u0001\u0000\u0000\u0000\u011e\u011f\u0001\u0000\u0000"+
		"\u0000\u011f\u0121\u0001\u0000\u0000\u0000\u0120\u0101\u0001\u0000\u0000"+
		"\u0000\u0120\u0104\u0001\u0000\u0000\u0000\u0120\u0107\u0001\u0000\u0000"+
		"\u0000\u0120\u010b\u0001\u0000\u0000\u0000\u0120\u010e\u0001\u0000\u0000"+
		"\u0000\u0120\u0111\u0001\u0000\u0000\u0000\u0120\u0114\u0001\u0000\u0000"+
		"\u0000\u0120\u0117\u0001\u0000\u0000\u0000\u0120\u011a\u0001\u0000\u0000"+
		"\u0000\u0121\u0124\u0001\u0000\u0000\u0000\u0122\u0120\u0001\u0000\u0000"+
		"\u0000\u0122\u0123\u0001\u0000\u0000\u0000\u0123/\u0001\u0000\u0000\u0000"+
		"\u0124\u0122\u0001\u0000\u0000\u0000\u0125\u0126\u0005\"\u0000\u0000\u0126"+
		"\u0127\u00032\u0019\u0000\u0127\u0128\u0005#\u0000\u0000\u0128\u0129\u0003"+
		".\u0017\u0000\u01291\u0001\u0000\u0000\u0000\u012a\u012f\u00034\u001a"+
		"\u0000\u012b\u012c\u0005\u0004\u0000\u0000\u012c\u012e\u00034\u001a\u0000"+
		"\u012d\u012b\u0001\u0000\u0000\u0000\u012e\u0131\u0001\u0000\u0000\u0000"+
		"\u012f\u012d\u0001\u0000\u0000\u0000\u012f\u0130\u0001\u0000\u0000\u0000"+
		"\u01303\u0001\u0000\u0000\u0000\u0131\u012f\u0001\u0000\u0000\u0000\u0132"+
		"\u0135\u00036\u001b\u0000\u0133\u0135\u0003\u0004\u0002\u0000\u0134\u0132"+
		"\u0001\u0000\u0000\u0000\u0134\u0133\u0001\u0000\u0000\u0000\u01355\u0001"+
		"\u0000\u0000\u0000\u0136\u0137\u0003D\"\u0000\u0137\u0138\u0005\u0001"+
		"\u0000\u0000\u0138\u0139\u0003.\u0017\u0000\u0139\u0141\u0001\u0000\u0000"+
		"\u0000\u013a\u013b\u0003D\"\u0000\u013b\u013c\u0005\u0005\u0000\u0000"+
		"\u013c\u013d\u0003\u001e\u000f\u0000\u013d\u013e\u0005\u0001\u0000\u0000"+
		"\u013e\u013f\u0003.\u0017\u0000\u013f\u0141\u0001\u0000\u0000\u0000\u0140"+
		"\u0136\u0001\u0000\u0000\u0000\u0140\u013a\u0001\u0000\u0000\u0000\u0141"+
		"7\u0001\u0000\u0000\u0000\u0142\u0143\u0005&\u0000\u0000\u0143\u0144\u0003"+
		".\u0017\u0000\u0144\u0145\u0005\'\u0000\u0000\u0145\u0146\u0003.\u0017"+
		"\u0000\u0146\u0147\u0005(\u0000\u0000\u0147\u0148\u0003.\u0017\u0000\u0148"+
		"9\u0001\u0000\u0000\u0000\u0149\u014b\u0005\b\u0000\u0000\u014a\u014c"+
		"\u0003<\u001e\u0000\u014b\u014a\u0001\u0000\u0000\u0000\u014b\u014c\u0001"+
		"\u0000\u0000\u0000\u014c\u014d\u0001\u0000\u0000\u0000\u014d\u014e\u0005"+
		"\t\u0000\u0000\u014e;\u0001\u0000\u0000\u0000\u014f\u0154\u0003.\u0017"+
		"\u0000\u0150\u0151\u0005\u0004\u0000\u0000\u0151\u0153\u0003.\u0017\u0000"+
		"\u0152\u0150\u0001\u0000\u0000\u0000\u0153\u0156\u0001\u0000\u0000\u0000"+
		"\u0154\u0152\u0001\u0000\u0000\u0000\u0154\u0155\u0001\u0000\u0000\u0000"+
		"\u0155=\u0001\u0000\u0000\u0000\u0156\u0154\u0001\u0000\u0000\u0000\u0157"+
		"\u0159\u0005\n\u0000\u0000\u0158\u015a\u0003@ \u0000\u0159\u0158\u0001"+
		"\u0000\u0000\u0000\u0159\u015a\u0001\u0000\u0000\u0000\u015a\u015b\u0001"+
		"\u0000\u0000\u0000\u015b\u015c\u0005\u000b\u0000\u0000\u015c?\u0001\u0000"+
		"\u0000\u0000\u015d\u0162\u0003B!\u0000\u015e\u015f\u0005\u0004\u0000\u0000"+
		"\u015f\u0161\u0003B!\u0000\u0160\u015e\u0001\u0000\u0000\u0000\u0161\u0164"+
		"\u0001\u0000\u0000\u0000\u0162\u0160\u0001\u0000\u0000\u0000\u0162\u0163"+
		"\u0001\u0000\u0000\u0000\u0163A\u0001\u0000\u0000\u0000\u0164\u0162\u0001"+
		"\u0000\u0000\u0000\u0165\u0166\u0003D\"\u0000\u0166\u0167\u0005\u0005"+
		"\u0000\u0000\u0167\u0168\u0003.\u0017\u0000\u0168\u016b\u0001\u0000\u0000"+
		"\u0000\u0169\u016b\u0003.\u0017\u0000\u016a\u0165\u0001\u0000\u0000\u0000"+
		"\u016a\u0169\u0001\u0000\u0000\u0000\u016bC\u0001\u0000\u0000\u0000\u016c"+
		"\u016d\u0007\u0000\u0000\u0000\u016dE\u0001\u0000\u0000\u0000\u016e\u016f"+
		"\u0007\u0001\u0000\u0000\u016fG\u0001\u0000\u0000\u0000\u0170\u0171\u0007"+
		"\u0002\u0000\u0000\u0171I\u0001\u0000\u0000\u0000\u0172\u017a\u0005\r"+
		"\u0000\u0000\u0173\u017a\u0005\u000e\u0000\u0000\u0174\u017a\u0005\u000f"+
		"\u0000\u0000\u0175\u017a\u0005\u0010\u0000\u0000\u0176\u017a\u0003H$\u0000"+
		"\u0177\u017a\u0003L&\u0000\u0178\u017a\u0005\u001c\u0000\u0000\u0179\u0172"+
		"\u0001\u0000\u0000\u0000\u0179\u0173\u0001\u0000\u0000\u0000\u0179\u0174"+
		"\u0001\u0000\u0000\u0000\u0179\u0175\u0001\u0000\u0000\u0000\u0179\u0176"+
		"\u0001\u0000\u0000\u0000\u0179\u0177\u0001\u0000\u0000\u0000\u0179\u0178"+
		"\u0001\u0000\u0000\u0000\u017aK\u0001\u0000\u0000\u0000\u017b\u017c\u0007"+
		"\u0003\u0000\u0000\u017cM\u0001\u0000\u0000\u0000\u017d\u017e\u0007\u0004"+
		"\u0000\u0000\u017eO\u0001\u0000\u0000\u0000\u017f\u0180\u0007\u0005\u0000"+
		"\u0000\u0180Q\u0001\u0000\u0000\u0000\u001cX^bjt}\u0085\u0090\u0099\u00a2"+
		"\u00ac\u00b3\u00be\u00c7\u00e1\u00ff\u011e\u0120\u0122\u012f\u0134\u0140"+
		"\u014b\u0154\u0159\u0162\u016a\u0179";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}