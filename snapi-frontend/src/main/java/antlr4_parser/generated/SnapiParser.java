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
		T__9=10, T__10=11, PRIMITIVE_TYPES=12, TYPE_TOKEN=13, BOOL_TOKEN=14, STRING_TOKEN=15, 
		LOCATION_TOKEN=16, BINARY_TOKEN=17, BYTE_TOKEN=18, SHORT_TOKEN=19, INT_TOKEN=20, 
		LONG_TOKEN=21, FLOAT_TOKEN=22, DOUBLE_TOKEN=23, DECIMAL_TOKEN=24, DATE_TOKEN=25, 
		TIME_TOKEN=26, INTERVAL_TOKEN=27, TIMESTAMP_TOKEN=28, RECORD_TOKEN=29, 
		COLLECTION_TOKEN=30, LIST_TOKEN=31, LIBRARY_TOKEN=32, PACKAGE_TOKEN=33, 
		LET_TOKEN=34, IN_TOKEN=35, REC_TOKEN=36, UNDEFINED_TOKEN=37, IF_TOKEN=38, 
		THEN_TOKEN=39, ELSE_TOKEN=40, NULL_TOKEN=41, INTEGER=42, BYTE=43, SHORT=44, 
		LONG=45, FLOAT=46, DOUBLE=47, DECIMAL=48, COMPARE_TOKENS=49, EQ_TOKEN=50, 
		NEQ_TOKEN=51, LE_TOKEN=52, LT_TOKEN=53, GE_TOKEN=54, GT_TOKEN=55, PLUS_TOKEN=56, 
		MINUS_TOKEN=57, MUL_TOKEN=58, DIV_TOKEN=59, MOD_TOKEN=60, AND_TOKEN=61, 
		OR_TOKEN=62, NOT_TOKEN=63, BOOL_CONST=64, TRUE_TOKEN=65, FALSE_TOKEN=66, 
		STRING=67, TRIPPLE_STRING=68, IDENT=69, NON_ESC_IDENTIFIER=70, ESC_IDENTIFIER=71, 
		WS=72, LINE_COMMENT=73;
	public static final int
		RULE_prog = 0, RULE_stat = 1, RULE_fun_dec = 2, RULE_fun = 3, RULE_normal_fun = 4, 
		RULE_rec_fun = 5, RULE_fun_proto = 6, RULE_fun_app = 7, RULE_fun_ar = 8, 
		RULE_fun_args = 9, RULE_fun_arg = 10, RULE_fun_abs = 11, RULE_type = 12, 
		RULE_fun_opt_params = 13, RULE_fun_params = 14, RULE_fun_param = 15, RULE_attr = 16, 
		RULE_record_type = 17, RULE_iterable_type = 18, RULE_list_type = 19, RULE_expr_type = 20, 
		RULE_expr = 21, RULE_let = 22, RULE_let_left = 23, RULE_let_decl = 24, 
		RULE_let_bind = 25, RULE_if_then_else = 26, RULE_lists = 27, RULE_lists_element = 28, 
		RULE_records = 29, RULE_record_elements = 30, RULE_record_element = 31, 
		RULE_number = 32;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "stat", "fun_dec", "fun", "normal_fun", "rec_fun", "fun_proto", 
			"fun_app", "fun_ar", "fun_args", "fun_arg", "fun_abs", "type", "fun_opt_params", 
			"fun_params", "fun_param", "attr", "record_type", "iterable_type", "list_type", 
			"expr_type", "expr", "let", "let_left", "let_decl", "let_bind", "if_then_else", 
			"lists", "lists_element", "records", "record_elements", "record_element", 
			"number"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "'('", "')'", "':'", "','", "'->'", "'.'", "'['", "']'", 
			"'{'", "'}'", null, "'type'", "'bool'", "'string'", "'location'", "'binary'", 
			"'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'decimal'", 
			"'date'", "'time'", "'interval'", "'timestamp'", "'record'", "'collection'", 
			"'list'", "'library'", "'package'", "'let'", "'in'", "'rec'", "'undefined'", 
			"'if'", "'then'", "'else'", "'null'", null, null, null, null, null, null, 
			null, null, "'=='", "'!='", "'<='", "'<'", "'>='", "'>'", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'and'", "'or'", "'not'", null, "'true'", "'false'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"PRIMITIVE_TYPES", "TYPE_TOKEN", "BOOL_TOKEN", "STRING_TOKEN", "LOCATION_TOKEN", 
			"BINARY_TOKEN", "BYTE_TOKEN", "SHORT_TOKEN", "INT_TOKEN", "LONG_TOKEN", 
			"FLOAT_TOKEN", "DOUBLE_TOKEN", "DECIMAL_TOKEN", "DATE_TOKEN", "TIME_TOKEN", 
			"INTERVAL_TOKEN", "TIMESTAMP_TOKEN", "RECORD_TOKEN", "COLLECTION_TOKEN", 
			"LIST_TOKEN", "LIBRARY_TOKEN", "PACKAGE_TOKEN", "LET_TOKEN", "IN_TOKEN", 
			"REC_TOKEN", "UNDEFINED_TOKEN", "IF_TOKEN", "THEN_TOKEN", "ELSE_TOKEN", 
			"NULL_TOKEN", "INTEGER", "BYTE", "SHORT", "LONG", "FLOAT", "DOUBLE", 
			"DECIMAL", "COMPARE_TOKENS", "EQ_TOKEN", "NEQ_TOKEN", "LE_TOKEN", "LT_TOKEN", 
			"GE_TOKEN", "GT_TOKEN", "PLUS_TOKEN", "MINUS_TOKEN", "MUL_TOKEN", "DIV_TOKEN", 
			"MOD_TOKEN", "AND_TOKEN", "OR_TOKEN", "NOT_TOKEN", "BOOL_CONST", "TRUE_TOKEN", 
			"FALSE_TOKEN", "STRING", "TRIPPLE_STRING", "IDENT", "NON_ESC_IDENTIFIER", 
			"ESC_IDENTIFIER", "WS", "LINE_COMMENT"
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
			setState(66);
			stat();
			setState(67);
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
		public List<Fun_decContext> fun_dec() {
			return getRuleContexts(Fun_decContext.class);
		}
		public Fun_decContext fun_dec(int i) {
			return getRuleContext(Fun_decContext.class,i);
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
		public List<Fun_decContext> fun_dec() {
			return getRuleContexts(Fun_decContext.class);
		}
		public Fun_decContext fun_dec(int i) {
			return getRuleContext(Fun_decContext.class,i);
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
			setState(82);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new FunDecStatContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==REC_TOKEN || _la==IDENT) {
					{
					{
					setState(69);
					fun_dec();
					}
					}
					setState(74);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new FunDecExprStatContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(78);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(75);
						fun_dec();
						}
						} 
					}
					setState(80);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				setState(81);
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
	public static class FunDecContext extends Fun_decContext {
		public FunContext fun() {
			return getRuleContext(FunContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FunDecContext(Fun_decContext ctx) { copyFrom(ctx); }
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

	public final Fun_decContext fun_dec() throws RecognitionException {
		Fun_decContext _localctx = new Fun_decContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_fun_dec);
		try {
			_localctx = new FunDecContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			fun();
			setState(85);
			match(T__0);
			setState(86);
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
		public FunContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun; }
	 
		public FunContext() { }
		public void copyFrom(FunContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NormalFunContext extends FunContext {
		public Normal_funContext normal_fun() {
			return getRuleContext(Normal_funContext.class,0);
		}
		public NormalFunContext(FunContext ctx) { copyFrom(ctx); }
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
	public static class RecFunContext extends FunContext {
		public Rec_funContext rec_fun() {
			return getRuleContext(Rec_funContext.class,0);
		}
		public RecFunContext(FunContext ctx) { copyFrom(ctx); }
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

	public final FunContext fun() throws RecognitionException {
		FunContext _localctx = new FunContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_fun);
		try {
			setState(90);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENT:
				_localctx = new NormalFunContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				normal_fun();
				}
				break;
			case REC_TOKEN:
				_localctx = new RecFunContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(89);
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
		public Normal_funContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normal_fun; }
	 
		public Normal_funContext() { }
		public void copyFrom(Normal_funContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NormalFunProtoContext extends Normal_funContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public Fun_protoContext fun_proto() {
			return getRuleContext(Fun_protoContext.class,0);
		}
		public NormalFunProtoContext(Normal_funContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterNormalFunProto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitNormalFunProto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitNormalFunProto(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Normal_funContext normal_fun() throws RecognitionException {
		Normal_funContext _localctx = new Normal_funContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_normal_fun);
		try {
			_localctx = new NormalFunProtoContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			match(IDENT);
			setState(93);
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
		public Rec_funContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rec_fun; }
	 
		public Rec_funContext() { }
		public void copyFrom(Rec_funContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RecFunProtoContext extends Rec_funContext {
		public TerminalNode REC_TOKEN() { return getToken(SnapiParser.REC_TOKEN, 0); }
		public Normal_funContext normal_fun() {
			return getRuleContext(Normal_funContext.class,0);
		}
		public RecFunProtoContext(Rec_funContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterRecFunProto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitRecFunProto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitRecFunProto(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rec_funContext rec_fun() throws RecognitionException {
		Rec_funContext _localctx = new Rec_funContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rec_fun);
		try {
			_localctx = new RecFunProtoContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(REC_TOKEN);
			setState(96);
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
		public Fun_paramsContext fun_params() {
			return getRuleContext(Fun_paramsContext.class,0);
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
		public Fun_paramsContext fun_params() {
			return getRuleContext(Fun_paramsContext.class,0);
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
		enterRule(_localctx, 12, RULE_fun_proto);
		int _la;
		try {
			setState(110);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new FunProtoWithoutTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				match(T__1);
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(99);
					fun_params();
					}
				}

				setState(102);
				match(T__2);
				}
				break;
			case 2:
				_localctx = new FunProtoWithTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(103);
				match(T__1);
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(104);
					fun_params();
					}
				}

				setState(107);
				match(T__2);
				setState(108);
				match(T__3);
				setState(109);
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
	public static class Fun_appContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
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
		enterRule(_localctx, 14, RULE_fun_app);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(IDENT);
			setState(113);
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
		enterRule(_localctx, 16, RULE_fun_ar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(T__1);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -9006638211753040636L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 57L) != 0)) {
				{
				setState(116);
				fun_args();
				}
			}

			setState(119);
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
		enterRule(_localctx, 18, RULE_fun_args);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			fun_arg();
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(122);
				match(T__4);
				setState(123);
				fun_arg();
				}
				}
				setState(128);
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
			setState(133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new FunArgExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				expr(0);
				}
				break;
			case 2:
				_localctx = new NamedFunArgExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				match(IDENT);
				setState(131);
				match(T__0);
				setState(132);
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
			setState(142);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new FunAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(135);
				fun_proto();
				setState(136);
				match(T__5);
				setState(137);
				expr(0);
				}
				break;
			case IDENT:
				_localctx = new FunAbsUnnamedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				match(IDENT);
				setState(140);
				match(T__5);
				setState(141);
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
		public Fun_opt_paramsContext fun_opt_params() {
			return getRuleContext(Fun_opt_paramsContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
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
		public TerminalNode PRIMITIVE_TYPES() { return getToken(SnapiParser.PRIMITIVE_TYPES, 0); }
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
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				_localctx = new TypeWithParenTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(145);
				match(T__1);
				setState(146);
				type(0);
				setState(147);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new PrimitiveTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(149);
				match(PRIMITIVE_TYPES);
				}
				break;
			case 3:
				{
				_localctx = new UndefinedTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(150);
				match(UNDEFINED_TOKEN);
				}
				break;
			case 4:
				{
				_localctx = new TypeAliasTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(151);
				match(IDENT);
				}
				break;
			case 5:
				{
				_localctx = new RecordTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(152);
				record_type();
				}
				break;
			case 6:
				{
				_localctx = new IterableTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(153);
				iterable_type();
				}
				break;
			case 7:
				{
				_localctx = new ListTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(154);
				list_type();
				}
				break;
			case 8:
				{
				_localctx = new FunTypeWithParamsTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(155);
				fun_opt_params();
				setState(156);
				match(T__5);
				setState(157);
				type(3);
				}
				break;
			case 9:
				{
				_localctx = new ExprTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(159);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(167);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new FunTypeTypeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(162);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(163);
					match(T__5);
					setState(164);
					type(3);
					}
					} 
				}
				setState(169);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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
	public static class Fun_opt_paramsContext extends ParserRuleContext {
		public Fun_opt_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_opt_params; }
	 
		public Fun_opt_paramsContext() { }
		public void copyFrom(Fun_opt_paramsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunOptParamsContext extends Fun_opt_paramsContext {
		public List<AttrContext> attr() {
			return getRuleContexts(AttrContext.class);
		}
		public AttrContext attr(int i) {
			return getRuleContext(AttrContext.class,i);
		}
		public FunOptParamsContext(Fun_opt_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunOptParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunOptParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunOptParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_opt_paramsContext fun_opt_params() throws RecognitionException {
		Fun_opt_paramsContext _localctx = new Fun_opt_paramsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_fun_opt_params);
		int _la;
		try {
			_localctx = new FunOptParamsContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			attr();
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(171);
				match(T__4);
				setState(172);
				attr();
				}
				}
				setState(177);
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
	public static class Fun_paramsContext extends ParserRuleContext {
		public Fun_paramsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun_params; }
	 
		public Fun_paramsContext() { }
		public void copyFrom(Fun_paramsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunParamsContext extends Fun_paramsContext {
		public List<Fun_paramContext> fun_param() {
			return getRuleContexts(Fun_paramContext.class);
		}
		public Fun_paramContext fun_param(int i) {
			return getRuleContext(Fun_paramContext.class,i);
		}
		public FunParamsContext(Fun_paramsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterFunParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitFunParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitFunParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Fun_paramsContext fun_params() throws RecognitionException {
		Fun_paramsContext _localctx = new Fun_paramsContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_fun_params);
		int _la;
		try {
			_localctx = new FunParamsContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			fun_param();
			setState(183);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(179);
				match(T__4);
				setState(180);
				fun_param();
				}
				}
				setState(185);
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
		enterRule(_localctx, 30, RULE_fun_param);
		try {
			setState(191);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new FunParamAttrContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				attr();
				}
				break;
			case 2:
				_localctx = new FunParamAttrExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				attr();
				setState(188);
				match(T__0);
				setState(189);
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
		public AttrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attr; }
	 
		public AttrContext() { }
		public void copyFrom(AttrContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AttrWithTypeContext extends AttrContext {
		public TerminalNode IDENT() { return getToken(SnapiParser.IDENT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public AttrWithTypeContext(AttrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).enterAttrWithType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SnapiListener ) ((SnapiListener)listener).exitAttrWithType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SnapiVisitor ) return ((SnapiVisitor<? extends T>)visitor).visitAttrWithType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrContext attr() throws RecognitionException {
		AttrContext _localctx = new AttrContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_attr);
		try {
			_localctx = new AttrWithTypeContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(IDENT);
			setState(194);
			match(T__3);
			setState(195);
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
			setState(197);
			match(RECORD_TOKEN);
			setState(198);
			match(T__1);
			setState(199);
			attr();
			setState(204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(200);
				match(T__4);
				setState(201);
				attr();
				}
				}
				setState(206);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
			setState(209);
			match(COLLECTION_TOKEN);
			setState(210);
			match(T__1);
			setState(211);
			type(0);
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
			setState(214);
			match(LIST_TOKEN);
			setState(215);
			match(T__1);
			setState(216);
			type(0);
			setState(217);
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
			setState(219);
			match(TYPE_TOKEN);
			setState(220);
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
		public Fun_appContext fun_app() {
			return getRuleContext(Fun_appContext.class,0);
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
	public static class PlusExprContext extends ExprContext {
		public TerminalNode PLUS_TOKEN() { return getToken(SnapiParser.PLUS_TOKEN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
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
	public static class CompareExprContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode COMPARE_TOKENS() { return getToken(SnapiParser.COMPARE_TOKENS, 0); }
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
		public TerminalNode MINUS_TOKEN() { return getToken(SnapiParser.MINUS_TOKEN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
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

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(223);
				match(T__1);
				setState(224);
				expr(0);
				setState(225);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new NumberExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(227);
				number();
				}
				break;
			case 3:
				{
				_localctx = new IfThenElseExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(228);
				if_then_else();
				}
				break;
			case 4:
				{
				_localctx = new ListExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(229);
				lists();
				}
				break;
			case 5:
				{
				_localctx = new RecordExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(230);
				records();
				}
				break;
			case 6:
				{
				_localctx = new BoolConstExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(231);
				match(BOOL_CONST);
				}
				break;
			case 7:
				{
				_localctx = new NullExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(232);
				match(NULL_TOKEN);
				}
				break;
			case 8:
				{
				_localctx = new TrippleStringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(233);
				match(TRIPPLE_STRING);
				}
				break;
			case 9:
				{
				_localctx = new StringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(234);
				match(STRING);
				}
				break;
			case 10:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(235);
				match(NOT_TOKEN);
				setState(236);
				expr(16);
				}
				break;
			case 11:
				{
				_localctx = new MinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(237);
				match(MINUS_TOKEN);
				setState(238);
				expr(12);
				}
				break;
			case 12:
				{
				_localctx = new PlusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(239);
				match(PLUS_TOKEN);
				setState(240);
				expr(11);
				}
				break;
			case 13:
				{
				_localctx = new FunAppExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(241);
				fun_app();
				}
				break;
			case 14:
				{
				_localctx = new FunAbsExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(242);
				fun_abs();
				}
				break;
			case 15:
				{
				_localctx = new LetExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(243);
				let();
				}
				break;
			case 16:
				{
				_localctx = new ExprTypeExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(279);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(277);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
					case 1:
						{
						_localctx = new AndExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(247);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(248);
						match(AND_TOKEN);
						setState(249);
						expr(16);
						}
						break;
					case 2:
						{
						_localctx = new OrExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(250);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(251);
						match(OR_TOKEN);
						setState(252);
						expr(15);
						}
						break;
					case 3:
						{
						_localctx = new CompareExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(253);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(254);
						match(COMPARE_TOKENS);
						setState(255);
						expr(14);
						}
						break;
					case 4:
						{
						_localctx = new MulExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(256);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(257);
						match(MUL_TOKEN);
						setState(258);
						expr(11);
						}
						break;
					case 5:
						{
						_localctx = new DivExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(259);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(260);
						match(DIV_TOKEN);
						setState(261);
						expr(10);
						}
						break;
					case 6:
						{
						_localctx = new ModExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(262);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(263);
						match(MOD_TOKEN);
						setState(264);
						expr(9);
						}
						break;
					case 7:
						{
						_localctx = new PlusExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(265);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(266);
						match(PLUS_TOKEN);
						setState(267);
						expr(8);
						}
						break;
					case 8:
						{
						_localctx = new MinusExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(268);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(269);
						match(MINUS_TOKEN);
						setState(270);
						expr(7);
						}
						break;
					case 9:
						{
						_localctx = new ProjectionExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(271);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(272);
						match(T__6);
						setState(273);
						match(IDENT);
						setState(275);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
						case 1:
							{
							setState(274);
							fun_ar();
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(281);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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
		enterRule(_localctx, 44, RULE_let);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			match(LET_TOKEN);
			setState(283);
			let_left();
			setState(284);
			match(IN_TOKEN);
			setState(285);
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
		enterRule(_localctx, 46, RULE_let_left);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			let_decl();
			setState(292);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(288);
				match(T__4);
				setState(289);
				let_decl();
				}
				}
				setState(294);
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
		enterRule(_localctx, 48, RULE_let_decl);
		try {
			setState(297);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(295);
				let_bind();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(296);
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
		enterRule(_localctx, 50, RULE_let_bind);
		try {
			setState(308);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(299);
				match(IDENT);
				setState(300);
				match(T__0);
				setState(301);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
				match(IDENT);
				setState(303);
				match(T__3);
				setState(304);
				type(0);
				setState(305);
				match(T__0);
				setState(306);
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
		enterRule(_localctx, 52, RULE_if_then_else);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			match(IF_TOKEN);
			setState(311);
			expr(0);
			setState(312);
			match(THEN_TOKEN);
			setState(313);
			expr(0);
			setState(314);
			match(ELSE_TOKEN);
			setState(315);
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
		enterRule(_localctx, 54, RULE_lists);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(317);
			match(T__7);
			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -9006638211753040636L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 57L) != 0)) {
				{
				setState(318);
				lists_element();
				}
			}

			setState(321);
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
		enterRule(_localctx, 56, RULE_lists_element);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(323);
			expr(0);
			setState(328);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(324);
				match(T__4);
				setState(325);
				expr(0);
				}
				}
				setState(330);
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
		enterRule(_localctx, 58, RULE_records);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(T__9);
			setState(333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -9006638211753040636L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 57L) != 0)) {
				{
				setState(332);
				record_elements();
				}
			}

			setState(335);
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
		enterRule(_localctx, 60, RULE_record_elements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			record_element();
			setState(342);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(338);
				match(T__4);
				setState(339);
				record_element();
				}
				}
				setState(344);
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
		enterRule(_localctx, 62, RULE_record_element);
		try {
			setState(349);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				match(IDENT);
				setState(346);
				match(T__3);
				setState(347);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(348);
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
		enterRule(_localctx, 64, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return type_sempred((TypeContext)_localctx, predIndex);
		case 21:
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
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001I\u0162\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0005\u0001G\b\u0001\n\u0001\f\u0001J\t\u0001\u0001\u0001"+
		"\u0005\u0001M\b\u0001\n\u0001\f\u0001P\t\u0001\u0001\u0001\u0003\u0001"+
		"S\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0003\u0003[\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0003\u0006"+
		"e\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006j\b\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006o\b\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0003\bv\b\b\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\t\u0005\t}\b\t\n\t\f\t\u0080\t\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0003\n\u0086\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u008f\b\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u00a1\b\f\u0001"+
		"\f\u0001\f\u0001\f\u0005\f\u00a6\b\f\n\f\f\f\u00a9\t\f\u0001\r\u0001\r"+
		"\u0001\r\u0005\r\u00ae\b\r\n\r\f\r\u00b1\t\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0005\u000e\u00b6\b\u000e\n\u000e\f\u000e\u00b9\t\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00c0\b\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u00cb\b\u0011\n\u0011"+
		"\f\u0011\u00ce\t\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00f6\b\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u0114\b\u0015"+
		"\u0005\u0015\u0116\b\u0015\n\u0015\f\u0015\u0119\t\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0005\u0017\u0123\b\u0017\n\u0017\f\u0017\u0126\t\u0017\u0001\u0018"+
		"\u0001\u0018\u0003\u0018\u012a\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u0135\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0003\u001b"+
		"\u0140\b\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0005\u001c\u0147\b\u001c\n\u001c\f\u001c\u014a\t\u001c\u0001\u001d\u0001"+
		"\u001d\u0003\u001d\u014e\b\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0005\u001e\u0155\b\u001e\n\u001e\f\u001e\u0158\t\u001e"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u015e\b\u001f"+
		"\u0001 \u0001 \u0001 \u0000\u0002\u0018*!\u0000\u0002\u0004\u0006\b\n"+
		"\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246"+
		"8:<>@\u0000\u0001\u0001\u0000*0\u0179\u0000B\u0001\u0000\u0000\u0000\u0002"+
		"R\u0001\u0000\u0000\u0000\u0004T\u0001\u0000\u0000\u0000\u0006Z\u0001"+
		"\u0000\u0000\u0000\b\\\u0001\u0000\u0000\u0000\n_\u0001\u0000\u0000\u0000"+
		"\fn\u0001\u0000\u0000\u0000\u000ep\u0001\u0000\u0000\u0000\u0010s\u0001"+
		"\u0000\u0000\u0000\u0012y\u0001\u0000\u0000\u0000\u0014\u0085\u0001\u0000"+
		"\u0000\u0000\u0016\u008e\u0001\u0000\u0000\u0000\u0018\u00a0\u0001\u0000"+
		"\u0000\u0000\u001a\u00aa\u0001\u0000\u0000\u0000\u001c\u00b2\u0001\u0000"+
		"\u0000\u0000\u001e\u00bf\u0001\u0000\u0000\u0000 \u00c1\u0001\u0000\u0000"+
		"\u0000\"\u00c5\u0001\u0000\u0000\u0000$\u00d1\u0001\u0000\u0000\u0000"+
		"&\u00d6\u0001\u0000\u0000\u0000(\u00db\u0001\u0000\u0000\u0000*\u00f5"+
		"\u0001\u0000\u0000\u0000,\u011a\u0001\u0000\u0000\u0000.\u011f\u0001\u0000"+
		"\u0000\u00000\u0129\u0001\u0000\u0000\u00002\u0134\u0001\u0000\u0000\u0000"+
		"4\u0136\u0001\u0000\u0000\u00006\u013d\u0001\u0000\u0000\u00008\u0143"+
		"\u0001\u0000\u0000\u0000:\u014b\u0001\u0000\u0000\u0000<\u0151\u0001\u0000"+
		"\u0000\u0000>\u015d\u0001\u0000\u0000\u0000@\u015f\u0001\u0000\u0000\u0000"+
		"BC\u0003\u0002\u0001\u0000CD\u0005\u0000\u0000\u0001D\u0001\u0001\u0000"+
		"\u0000\u0000EG\u0003\u0004\u0002\u0000FE\u0001\u0000\u0000\u0000GJ\u0001"+
		"\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000"+
		"IS\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000KM\u0003\u0004\u0002"+
		"\u0000LK\u0001\u0000\u0000\u0000MP\u0001\u0000\u0000\u0000NL\u0001\u0000"+
		"\u0000\u0000NO\u0001\u0000\u0000\u0000OQ\u0001\u0000\u0000\u0000PN\u0001"+
		"\u0000\u0000\u0000QS\u0003*\u0015\u0000RH\u0001\u0000\u0000\u0000RN\u0001"+
		"\u0000\u0000\u0000S\u0003\u0001\u0000\u0000\u0000TU\u0003\u0006\u0003"+
		"\u0000UV\u0005\u0001\u0000\u0000VW\u0003*\u0015\u0000W\u0005\u0001\u0000"+
		"\u0000\u0000X[\u0003\b\u0004\u0000Y[\u0003\n\u0005\u0000ZX\u0001\u0000"+
		"\u0000\u0000ZY\u0001\u0000\u0000\u0000[\u0007\u0001\u0000\u0000\u0000"+
		"\\]\u0005E\u0000\u0000]^\u0003\f\u0006\u0000^\t\u0001\u0000\u0000\u0000"+
		"_`\u0005$\u0000\u0000`a\u0003\b\u0004\u0000a\u000b\u0001\u0000\u0000\u0000"+
		"bd\u0005\u0002\u0000\u0000ce\u0003\u001c\u000e\u0000dc\u0001\u0000\u0000"+
		"\u0000de\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fo\u0005\u0003"+
		"\u0000\u0000gi\u0005\u0002\u0000\u0000hj\u0003\u001c\u000e\u0000ih\u0001"+
		"\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000"+
		"kl\u0005\u0003\u0000\u0000lm\u0005\u0004\u0000\u0000mo\u0003\u0018\f\u0000"+
		"nb\u0001\u0000\u0000\u0000ng\u0001\u0000\u0000\u0000o\r\u0001\u0000\u0000"+
		"\u0000pq\u0005E\u0000\u0000qr\u0003\u0010\b\u0000r\u000f\u0001\u0000\u0000"+
		"\u0000su\u0005\u0002\u0000\u0000tv\u0003\u0012\t\u0000ut\u0001\u0000\u0000"+
		"\u0000uv\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wx\u0005\u0003"+
		"\u0000\u0000x\u0011\u0001\u0000\u0000\u0000y~\u0003\u0014\n\u0000z{\u0005"+
		"\u0005\u0000\u0000{}\u0003\u0014\n\u0000|z\u0001\u0000\u0000\u0000}\u0080"+
		"\u0001\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000"+
		"\u0000\u0000\u007f\u0013\u0001\u0000\u0000\u0000\u0080~\u0001\u0000\u0000"+
		"\u0000\u0081\u0086\u0003*\u0015\u0000\u0082\u0083\u0005E\u0000\u0000\u0083"+
		"\u0084\u0005\u0001\u0000\u0000\u0084\u0086\u0003*\u0015\u0000\u0085\u0081"+
		"\u0001\u0000\u0000\u0000\u0085\u0082\u0001\u0000\u0000\u0000\u0086\u0015"+
		"\u0001\u0000\u0000\u0000\u0087\u0088\u0003\f\u0006\u0000\u0088\u0089\u0005"+
		"\u0006\u0000\u0000\u0089\u008a\u0003*\u0015\u0000\u008a\u008f\u0001\u0000"+
		"\u0000\u0000\u008b\u008c\u0005E\u0000\u0000\u008c\u008d\u0005\u0006\u0000"+
		"\u0000\u008d\u008f\u0003*\u0015\u0000\u008e\u0087\u0001\u0000\u0000\u0000"+
		"\u008e\u008b\u0001\u0000\u0000\u0000\u008f\u0017\u0001\u0000\u0000\u0000"+
		"\u0090\u0091\u0006\f\uffff\uffff\u0000\u0091\u0092\u0005\u0002\u0000\u0000"+
		"\u0092\u0093\u0003\u0018\f\u0000\u0093\u0094\u0005\u0003\u0000\u0000\u0094"+
		"\u00a1\u0001\u0000\u0000\u0000\u0095\u00a1\u0005\f\u0000\u0000\u0096\u00a1"+
		"\u0005%\u0000\u0000\u0097\u00a1\u0005E\u0000\u0000\u0098\u00a1\u0003\""+
		"\u0011\u0000\u0099\u00a1\u0003$\u0012\u0000\u009a\u00a1\u0003&\u0013\u0000"+
		"\u009b\u009c\u0003\u001a\r\u0000\u009c\u009d\u0005\u0006\u0000\u0000\u009d"+
		"\u009e\u0003\u0018\f\u0003\u009e\u00a1\u0001\u0000\u0000\u0000\u009f\u00a1"+
		"\u0003(\u0014\u0000\u00a0\u0090\u0001\u0000\u0000\u0000\u00a0\u0095\u0001"+
		"\u0000\u0000\u0000\u00a0\u0096\u0001\u0000\u0000\u0000\u00a0\u0097\u0001"+
		"\u0000\u0000\u0000\u00a0\u0098\u0001\u0000\u0000\u0000\u00a0\u0099\u0001"+
		"\u0000\u0000\u0000\u00a0\u009a\u0001\u0000\u0000\u0000\u00a0\u009b\u0001"+
		"\u0000\u0000\u0000\u00a0\u009f\u0001\u0000\u0000\u0000\u00a1\u00a7\u0001"+
		"\u0000\u0000\u0000\u00a2\u00a3\n\u0002\u0000\u0000\u00a3\u00a4\u0005\u0006"+
		"\u0000\u0000\u00a4\u00a6\u0003\u0018\f\u0003\u00a5\u00a2\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a9\u0001\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000"+
		"\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u0019\u0001\u0000\u0000"+
		"\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00aa\u00af\u0003 \u0010\u0000"+
		"\u00ab\u00ac\u0005\u0005\u0000\u0000\u00ac\u00ae\u0003 \u0010\u0000\u00ad"+
		"\u00ab\u0001\u0000\u0000\u0000\u00ae\u00b1\u0001\u0000\u0000\u0000\u00af"+
		"\u00ad\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0"+
		"\u001b\u0001\u0000\u0000\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b7\u0003\u001e\u000f\u0000\u00b3\u00b4\u0005\u0005\u0000\u0000\u00b4"+
		"\u00b6\u0003\u001e\u000f\u0000\u00b5\u00b3\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b9\u0001\u0000\u0000\u0000\u00b7\u00b5\u0001\u0000\u0000\u0000\u00b7"+
		"\u00b8\u0001\u0000\u0000\u0000\u00b8\u001d\u0001\u0000\u0000\u0000\u00b9"+
		"\u00b7\u0001\u0000\u0000\u0000\u00ba\u00c0\u0003 \u0010\u0000\u00bb\u00bc"+
		"\u0003 \u0010\u0000\u00bc\u00bd\u0005\u0001\u0000\u0000\u00bd\u00be\u0003"+
		"*\u0015\u0000\u00be\u00c0\u0001\u0000\u0000\u0000\u00bf\u00ba\u0001\u0000"+
		"\u0000\u0000\u00bf\u00bb\u0001\u0000\u0000\u0000\u00c0\u001f\u0001\u0000"+
		"\u0000\u0000\u00c1\u00c2\u0005E\u0000\u0000\u00c2\u00c3\u0005\u0004\u0000"+
		"\u0000\u00c3\u00c4\u0003\u0018\f\u0000\u00c4!\u0001\u0000\u0000\u0000"+
		"\u00c5\u00c6\u0005\u001d\u0000\u0000\u00c6\u00c7\u0005\u0002\u0000\u0000"+
		"\u00c7\u00cc\u0003 \u0010\u0000\u00c8\u00c9\u0005\u0005\u0000\u0000\u00c9"+
		"\u00cb\u0003 \u0010\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00cb\u00ce"+
		"\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd"+
		"\u0001\u0000\u0000\u0000\u00cd\u00cf\u0001\u0000\u0000\u0000\u00ce\u00cc"+
		"\u0001\u0000\u0000\u0000\u00cf\u00d0\u0005\u0003\u0000\u0000\u00d0#\u0001"+
		"\u0000\u0000\u0000\u00d1\u00d2\u0005\u001e\u0000\u0000\u00d2\u00d3\u0005"+
		"\u0002\u0000\u0000\u00d3\u00d4\u0003\u0018\f\u0000\u00d4\u00d5\u0005\u0003"+
		"\u0000\u0000\u00d5%\u0001\u0000\u0000\u0000\u00d6\u00d7\u0005\u001f\u0000"+
		"\u0000\u00d7\u00d8\u0005\u0002\u0000\u0000\u00d8\u00d9\u0003\u0018\f\u0000"+
		"\u00d9\u00da\u0005\u0003\u0000\u0000\u00da\'\u0001\u0000\u0000\u0000\u00db"+
		"\u00dc\u0005\r\u0000\u0000\u00dc\u00dd\u0003\u0018\f\u0000\u00dd)\u0001"+
		"\u0000\u0000\u0000\u00de\u00df\u0006\u0015\uffff\uffff\u0000\u00df\u00e0"+
		"\u0005\u0002\u0000\u0000\u00e0\u00e1\u0003*\u0015\u0000\u00e1\u00e2\u0005"+
		"\u0003\u0000\u0000\u00e2\u00f6\u0001\u0000\u0000\u0000\u00e3\u00f6\u0003"+
		"@ \u0000\u00e4\u00f6\u00034\u001a\u0000\u00e5\u00f6\u00036\u001b\u0000"+
		"\u00e6\u00f6\u0003:\u001d\u0000\u00e7\u00f6\u0005@\u0000\u0000\u00e8\u00f6"+
		"\u0005)\u0000\u0000\u00e9\u00f6\u0005D\u0000\u0000\u00ea\u00f6\u0005C"+
		"\u0000\u0000\u00eb\u00ec\u0005?\u0000\u0000\u00ec\u00f6\u0003*\u0015\u0010"+
		"\u00ed\u00ee\u00059\u0000\u0000\u00ee\u00f6\u0003*\u0015\f\u00ef\u00f0"+
		"\u00058\u0000\u0000\u00f0\u00f6\u0003*\u0015\u000b\u00f1\u00f6\u0003\u000e"+
		"\u0007\u0000\u00f2\u00f6\u0003\u0016\u000b\u0000\u00f3\u00f6\u0003,\u0016"+
		"\u0000\u00f4\u00f6\u0003(\u0014\u0000\u00f5\u00de\u0001\u0000\u0000\u0000"+
		"\u00f5\u00e3\u0001\u0000\u0000\u0000\u00f5\u00e4\u0001\u0000\u0000\u0000"+
		"\u00f5\u00e5\u0001\u0000\u0000\u0000\u00f5\u00e6\u0001\u0000\u0000\u0000"+
		"\u00f5\u00e7\u0001\u0000\u0000\u0000\u00f5\u00e8\u0001\u0000\u0000\u0000"+
		"\u00f5\u00e9\u0001\u0000\u0000\u0000\u00f5\u00ea\u0001\u0000\u0000\u0000"+
		"\u00f5\u00eb\u0001\u0000\u0000\u0000\u00f5\u00ed\u0001\u0000\u0000\u0000"+
		"\u00f5\u00ef\u0001\u0000\u0000\u0000\u00f5\u00f1\u0001\u0000\u0000\u0000"+
		"\u00f5\u00f2\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001\u0000\u0000\u0000"+
		"\u00f5\u00f4\u0001\u0000\u0000\u0000\u00f6\u0117\u0001\u0000\u0000\u0000"+
		"\u00f7\u00f8\n\u000f\u0000\u0000\u00f8\u00f9\u0005=\u0000\u0000\u00f9"+
		"\u0116\u0003*\u0015\u0010\u00fa\u00fb\n\u000e\u0000\u0000\u00fb\u00fc"+
		"\u0005>\u0000\u0000\u00fc\u0116\u0003*\u0015\u000f\u00fd\u00fe\n\r\u0000"+
		"\u0000\u00fe\u00ff\u00051\u0000\u0000\u00ff\u0116\u0003*\u0015\u000e\u0100"+
		"\u0101\n\n\u0000\u0000\u0101\u0102\u0005:\u0000\u0000\u0102\u0116\u0003"+
		"*\u0015\u000b\u0103\u0104\n\t\u0000\u0000\u0104\u0105\u0005;\u0000\u0000"+
		"\u0105\u0116\u0003*\u0015\n\u0106\u0107\n\b\u0000\u0000\u0107\u0108\u0005"+
		"<\u0000\u0000\u0108\u0116\u0003*\u0015\t\u0109\u010a\n\u0007\u0000\u0000"+
		"\u010a\u010b\u00058\u0000\u0000\u010b\u0116\u0003*\u0015\b\u010c\u010d"+
		"\n\u0006\u0000\u0000\u010d\u010e\u00059\u0000\u0000\u010e\u0116\u0003"+
		"*\u0015\u0007\u010f\u0110\n\u0001\u0000\u0000\u0110\u0111\u0005\u0007"+
		"\u0000\u0000\u0111\u0113\u0005E\u0000\u0000\u0112\u0114\u0003\u0010\b"+
		"\u0000\u0113\u0112\u0001\u0000\u0000\u0000\u0113\u0114\u0001\u0000\u0000"+
		"\u0000\u0114\u0116\u0001\u0000\u0000\u0000\u0115\u00f7\u0001\u0000\u0000"+
		"\u0000\u0115\u00fa\u0001\u0000\u0000\u0000\u0115\u00fd\u0001\u0000\u0000"+
		"\u0000\u0115\u0100\u0001\u0000\u0000\u0000\u0115\u0103\u0001\u0000\u0000"+
		"\u0000\u0115\u0106\u0001\u0000\u0000\u0000\u0115\u0109\u0001\u0000\u0000"+
		"\u0000\u0115\u010c\u0001\u0000\u0000\u0000\u0115\u010f\u0001\u0000\u0000"+
		"\u0000\u0116\u0119\u0001\u0000\u0000\u0000\u0117\u0115\u0001\u0000\u0000"+
		"\u0000\u0117\u0118\u0001\u0000\u0000\u0000\u0118+\u0001\u0000\u0000\u0000"+
		"\u0119\u0117\u0001\u0000\u0000\u0000\u011a\u011b\u0005\"\u0000\u0000\u011b"+
		"\u011c\u0003.\u0017\u0000\u011c\u011d\u0005#\u0000\u0000\u011d\u011e\u0003"+
		"*\u0015\u0000\u011e-\u0001\u0000\u0000\u0000\u011f\u0124\u00030\u0018"+
		"\u0000\u0120\u0121\u0005\u0005\u0000\u0000\u0121\u0123\u00030\u0018\u0000"+
		"\u0122\u0120\u0001\u0000\u0000\u0000\u0123\u0126\u0001\u0000\u0000\u0000"+
		"\u0124\u0122\u0001\u0000\u0000\u0000\u0124\u0125\u0001\u0000\u0000\u0000"+
		"\u0125/\u0001\u0000\u0000\u0000\u0126\u0124\u0001\u0000\u0000\u0000\u0127"+
		"\u012a\u00032\u0019\u0000\u0128\u012a\u0003\u0004\u0002\u0000\u0129\u0127"+
		"\u0001\u0000\u0000\u0000\u0129\u0128\u0001\u0000\u0000\u0000\u012a1\u0001"+
		"\u0000\u0000\u0000\u012b\u012c\u0005E\u0000\u0000\u012c\u012d\u0005\u0001"+
		"\u0000\u0000\u012d\u0135\u0003*\u0015\u0000\u012e\u012f\u0005E\u0000\u0000"+
		"\u012f\u0130\u0005\u0004\u0000\u0000\u0130\u0131\u0003\u0018\f\u0000\u0131"+
		"\u0132\u0005\u0001\u0000\u0000\u0132\u0133\u0003*\u0015\u0000\u0133\u0135"+
		"\u0001\u0000\u0000\u0000\u0134\u012b\u0001\u0000\u0000\u0000\u0134\u012e"+
		"\u0001\u0000\u0000\u0000\u01353\u0001\u0000\u0000\u0000\u0136\u0137\u0005"+
		"&\u0000\u0000\u0137\u0138\u0003*\u0015\u0000\u0138\u0139\u0005\'\u0000"+
		"\u0000\u0139\u013a\u0003*\u0015\u0000\u013a\u013b\u0005(\u0000\u0000\u013b"+
		"\u013c\u0003*\u0015\u0000\u013c5\u0001\u0000\u0000\u0000\u013d\u013f\u0005"+
		"\b\u0000\u0000\u013e\u0140\u00038\u001c\u0000\u013f\u013e\u0001\u0000"+
		"\u0000\u0000\u013f\u0140\u0001\u0000\u0000\u0000\u0140\u0141\u0001\u0000"+
		"\u0000\u0000\u0141\u0142\u0005\t\u0000\u0000\u01427\u0001\u0000\u0000"+
		"\u0000\u0143\u0148\u0003*\u0015\u0000\u0144\u0145\u0005\u0005\u0000\u0000"+
		"\u0145\u0147\u0003*\u0015\u0000\u0146\u0144\u0001\u0000\u0000\u0000\u0147"+
		"\u014a\u0001\u0000\u0000\u0000\u0148\u0146\u0001\u0000\u0000\u0000\u0148"+
		"\u0149\u0001\u0000\u0000\u0000\u01499\u0001\u0000\u0000\u0000\u014a\u0148"+
		"\u0001\u0000\u0000\u0000\u014b\u014d\u0005\n\u0000\u0000\u014c\u014e\u0003"+
		"<\u001e\u0000\u014d\u014c\u0001\u0000\u0000\u0000\u014d\u014e\u0001\u0000"+
		"\u0000\u0000\u014e\u014f\u0001\u0000\u0000\u0000\u014f\u0150\u0005\u000b"+
		"\u0000\u0000\u0150;\u0001\u0000\u0000\u0000\u0151\u0156\u0003>\u001f\u0000"+
		"\u0152\u0153\u0005\u0005\u0000\u0000\u0153\u0155\u0003>\u001f\u0000\u0154"+
		"\u0152\u0001\u0000\u0000\u0000\u0155\u0158\u0001\u0000\u0000\u0000\u0156"+
		"\u0154\u0001\u0000\u0000\u0000\u0156\u0157\u0001\u0000\u0000\u0000\u0157"+
		"=\u0001\u0000\u0000\u0000\u0158\u0156\u0001\u0000\u0000\u0000\u0159\u015a"+
		"\u0005E\u0000\u0000\u015a\u015b\u0005\u0004\u0000\u0000\u015b\u015e\u0003"+
		"*\u0015\u0000\u015c\u015e\u0003*\u0015\u0000\u015d\u0159\u0001\u0000\u0000"+
		"\u0000\u015d\u015c\u0001\u0000\u0000\u0000\u015e?\u0001\u0000\u0000\u0000"+
		"\u015f\u0160\u0007\u0000\u0000\u0000\u0160A\u0001\u0000\u0000\u0000\u001d"+
		"HNRZdinu~\u0085\u008e\u00a0\u00a7\u00af\u00b7\u00bf\u00cc\u00f5\u0113"+
		"\u0115\u0117\u0124\u0129\u0134\u013f\u0148\u014d\u0156\u015d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}