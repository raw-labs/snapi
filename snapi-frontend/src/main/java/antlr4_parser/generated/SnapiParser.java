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
		RULE_rec_fun = 5, RULE_fun_proto = 6, RULE_fun_params = 7, RULE_fun_param = 8, 
		RULE_attr = 9, RULE_fun_app = 10, RULE_fun_ar = 11, RULE_fun_args = 12, 
		RULE_fun_arg = 13, RULE_fun_abs = 14, RULE_type = 15, RULE_record_type = 16, 
		RULE_iterable_type = 17, RULE_list_type = 18, RULE_expr_type = 19, RULE_expr = 20, 
		RULE_let = 21, RULE_let_left = 22, RULE_let_decl = 23, RULE_let_bind = 24, 
		RULE_if_then_else = 25, RULE_lists = 26, RULE_lists_element = 27, RULE_records = 28, 
		RULE_record_elements = 29, RULE_record_element = 30, RULE_number = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "stat", "fun_dec", "fun", "normal_fun", "rec_fun", "fun_proto", 
			"fun_params", "fun_param", "attr", "fun_app", "fun_ar", "fun_args", "fun_arg", 
			"fun_abs", "type", "record_type", "iterable_type", "list_type", "expr_type", 
			"expr", "let", "let_left", "let_decl", "let_bind", "if_then_else", "lists", 
			"lists_element", "records", "record_elements", "record_element", "number"
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
			setState(64);
			stat();
			setState(65);
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
			setState(80);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new FunDecStatContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==REC_TOKEN || _la==IDENT) {
					{
					{
					setState(67);
					fun_dec();
					}
					}
					setState(72);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new FunDecExprStatContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(73);
						fun_dec();
						}
						} 
					}
					setState(78);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				setState(79);
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
			setState(82);
			fun();
			setState(83);
			match(T__0);
			setState(84);
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
			setState(88);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENT:
				_localctx = new NormalFunContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(86);
				normal_fun();
				}
				break;
			case REC_TOKEN:
				_localctx = new RecFunContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(87);
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
			setState(90);
			match(IDENT);
			setState(91);
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
			setState(93);
			match(REC_TOKEN);
			setState(94);
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
			setState(108);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new FunProtoWithoutTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				match(T__1);
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(97);
					fun_params();
					}
				}

				setState(100);
				match(T__2);
				}
				break;
			case 2:
				_localctx = new FunProtoWithTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(101);
				match(T__1);
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(102);
					fun_params();
					}
				}

				setState(105);
				match(T__2);
				setState(106);
				match(T__3);
				setState(107);
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
		enterRule(_localctx, 14, RULE_fun_params);
		int _la;
		try {
			_localctx = new FunParamsContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			fun_param();
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(111);
				match(T__4);
				setState(112);
				fun_param();
				}
				}
				setState(117);
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
		enterRule(_localctx, 16, RULE_fun_param);
		try {
			setState(123);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new FunParamAttrContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(118);
				attr();
				}
				break;
			case 2:
				_localctx = new FunParamAttrExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(119);
				attr();
				setState(120);
				match(T__0);
				setState(121);
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
		enterRule(_localctx, 18, RULE_attr);
		try {
			_localctx = new AttrWithTypeContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(IDENT);
			setState(126);
			match(T__3);
			setState(127);
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
		enterRule(_localctx, 20, RULE_fun_app);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(IDENT);
			setState(130);
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
			setState(132);
			match(T__1);
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -9006638211753040636L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 57L) != 0)) {
				{
				setState(133);
				fun_args();
				}
			}

			setState(136);
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
			setState(138);
			fun_arg();
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(139);
				match(T__4);
				setState(140);
				fun_arg();
				}
				}
				setState(145);
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
		enterRule(_localctx, 26, RULE_fun_arg);
		try {
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new FunArgExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				expr(0);
				}
				break;
			case 2:
				_localctx = new NamedFunArgExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				match(IDENT);
				setState(148);
				match(T__0);
				setState(149);
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
		enterRule(_localctx, 28, RULE_fun_abs);
		try {
			setState(159);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new FunAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(152);
				fun_proto();
				setState(153);
				match(T__5);
				setState(154);
				expr(0);
				}
				break;
			case IDENT:
				_localctx = new FunAbsUnnamedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(156);
				match(IDENT);
				setState(157);
				match(T__5);
				setState(158);
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
		int _startState = 30;
		enterRecursionRule(_localctx, 30, RULE_type, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				_localctx = new TypeWithParenTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(162);
				match(T__1);
				setState(163);
				type(0);
				setState(164);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new PrimitiveTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(166);
				match(PRIMITIVE_TYPES);
				}
				break;
			case 3:
				{
				_localctx = new UndefinedTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(167);
				match(UNDEFINED_TOKEN);
				}
				break;
			case 4:
				{
				_localctx = new TypeAliasTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(168);
				match(IDENT);
				}
				break;
			case 5:
				{
				_localctx = new RecordTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(169);
				record_type();
				}
				break;
			case 6:
				{
				_localctx = new IterableTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(170);
				iterable_type();
				}
				break;
			case 7:
				{
				_localctx = new ListTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(171);
				list_type();
				}
				break;
			case 8:
				{
				_localctx = new FunTypeWithParamsTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(172);
				match(T__1);
				setState(175);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(173);
					type(0);
					}
					break;
				case 2:
					{
					setState(174);
					attr();
					}
					break;
				}
				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(177);
					match(T__4);
					setState(180);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						setState(178);
						type(0);
						}
						break;
					case 2:
						{
						setState(179);
						attr();
						}
						break;
					}
					}
					}
					setState(186);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(187);
				match(T__2);
				setState(188);
				match(T__5);
				setState(189);
				type(3);
				}
				break;
			case 9:
				{
				_localctx = new ExprTypeTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(191);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(199);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new FunTypeTypeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(194);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(195);
					match(T__5);
					setState(196);
					type(3);
					}
					} 
				}
				setState(201);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
		enterRule(_localctx, 32, RULE_record_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(RECORD_TOKEN);
			setState(203);
			match(T__1);
			setState(204);
			attr();
			setState(209);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(205);
				match(T__4);
				setState(206);
				attr();
				}
				}
				setState(211);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
		enterRule(_localctx, 34, RULE_iterable_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(COLLECTION_TOKEN);
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
		enterRule(_localctx, 36, RULE_list_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(LIST_TOKEN);
			setState(220);
			match(T__1);
			setState(221);
			type(0);
			setState(222);
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
		enterRule(_localctx, 38, RULE_expr_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(TYPE_TOKEN);
			setState(225);
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
		int _startState = 40;
		enterRecursionRule(_localctx, 40, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(228);
				match(T__1);
				setState(229);
				expr(0);
				setState(230);
				match(T__2);
				}
				break;
			case 2:
				{
				_localctx = new NumberExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(232);
				number();
				}
				break;
			case 3:
				{
				_localctx = new IfThenElseExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(233);
				if_then_else();
				}
				break;
			case 4:
				{
				_localctx = new ListExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(234);
				lists();
				}
				break;
			case 5:
				{
				_localctx = new RecordExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(235);
				records();
				}
				break;
			case 6:
				{
				_localctx = new BoolConstExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(236);
				match(BOOL_CONST);
				}
				break;
			case 7:
				{
				_localctx = new NullExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(237);
				match(NULL_TOKEN);
				}
				break;
			case 8:
				{
				_localctx = new TrippleStringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(238);
				match(TRIPPLE_STRING);
				}
				break;
			case 9:
				{
				_localctx = new StringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(239);
				match(STRING);
				}
				break;
			case 10:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(240);
				match(NOT_TOKEN);
				setState(241);
				expr(16);
				}
				break;
			case 11:
				{
				_localctx = new MinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(242);
				match(MINUS_TOKEN);
				setState(243);
				expr(12);
				}
				break;
			case 12:
				{
				_localctx = new PlusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244);
				match(PLUS_TOKEN);
				setState(245);
				expr(11);
				}
				break;
			case 13:
				{
				_localctx = new FunAppExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(246);
				fun_app();
				}
				break;
			case 14:
				{
				_localctx = new FunAbsExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(247);
				fun_abs();
				}
				break;
			case 15:
				{
				_localctx = new LetExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(248);
				let();
				}
				break;
			case 16:
				{
				_localctx = new ExprTypeExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(249);
				expr_type();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(284);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(282);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new AndExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(252);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(253);
						match(AND_TOKEN);
						setState(254);
						expr(16);
						}
						break;
					case 2:
						{
						_localctx = new OrExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(255);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(256);
						match(OR_TOKEN);
						setState(257);
						expr(15);
						}
						break;
					case 3:
						{
						_localctx = new CompareExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(258);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(259);
						match(COMPARE_TOKENS);
						setState(260);
						expr(14);
						}
						break;
					case 4:
						{
						_localctx = new MulExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(261);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(262);
						match(MUL_TOKEN);
						setState(263);
						expr(11);
						}
						break;
					case 5:
						{
						_localctx = new DivExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(264);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(265);
						match(DIV_TOKEN);
						setState(266);
						expr(10);
						}
						break;
					case 6:
						{
						_localctx = new ModExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(267);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(268);
						match(MOD_TOKEN);
						setState(269);
						expr(9);
						}
						break;
					case 7:
						{
						_localctx = new PlusExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(270);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(271);
						match(PLUS_TOKEN);
						setState(272);
						expr(8);
						}
						break;
					case 8:
						{
						_localctx = new MinusExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(273);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(274);
						match(MINUS_TOKEN);
						setState(275);
						expr(7);
						}
						break;
					case 9:
						{
						_localctx = new ProjectionExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(276);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(277);
						match(T__6);
						setState(278);
						match(IDENT);
						setState(280);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
						case 1:
							{
							setState(279);
							fun_ar();
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(286);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
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
		enterRule(_localctx, 42, RULE_let);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			match(LET_TOKEN);
			setState(288);
			let_left();
			setState(289);
			match(IN_TOKEN);
			setState(290);
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
		enterRule(_localctx, 44, RULE_let_left);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			let_decl();
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(293);
				match(T__4);
				setState(294);
				let_decl();
				}
				}
				setState(299);
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
		enterRule(_localctx, 46, RULE_let_decl);
		try {
			setState(302);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(300);
				let_bind();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(301);
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
		enterRule(_localctx, 48, RULE_let_bind);
		try {
			setState(313);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(304);
				match(IDENT);
				setState(305);
				match(T__0);
				setState(306);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(307);
				match(IDENT);
				setState(308);
				match(T__3);
				setState(309);
				type(0);
				setState(310);
				match(T__0);
				setState(311);
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
		enterRule(_localctx, 50, RULE_if_then_else);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			match(IF_TOKEN);
			setState(316);
			expr(0);
			setState(317);
			match(THEN_TOKEN);
			setState(318);
			expr(0);
			setState(319);
			match(ELSE_TOKEN);
			setState(320);
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
		enterRule(_localctx, 52, RULE_lists);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(T__7);
			setState(324);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -9006638211753040636L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 57L) != 0)) {
				{
				setState(323);
				lists_element();
				}
			}

			setState(326);
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
		enterRule(_localctx, 54, RULE_lists_element);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			expr(0);
			setState(333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(329);
				match(T__4);
				setState(330);
				expr(0);
				}
				}
				setState(335);
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
		enterRule(_localctx, 56, RULE_records);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
			match(T__9);
			setState(338);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -9006638211753040636L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 57L) != 0)) {
				{
				setState(337);
				record_elements();
				}
			}

			setState(340);
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
		enterRule(_localctx, 58, RULE_record_elements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			record_element();
			setState(347);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(343);
				match(T__4);
				setState(344);
				record_element();
				}
				}
				setState(349);
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
		enterRule(_localctx, 60, RULE_record_element);
		try {
			setState(354);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(350);
				match(IDENT);
				setState(351);
				match(T__3);
				setState(352);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(353);
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
		enterRule(_localctx, 62, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
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
		case 15:
			return type_sempred((TypeContext)_localctx, predIndex);
		case 20:
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
		"\u0004\u0001I\u0167\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0005\u0001E\b\u0001\n\u0001\f\u0001H\t\u0001\u0001\u0001\u0005\u0001"+
		"K\b\u0001\n\u0001\f\u0001N\t\u0001\u0001\u0001\u0003\u0001Q\b\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0003"+
		"\u0003Y\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0003\u0006c\b\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006h\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006m\b\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0005\u0007r\b\u0007\n\u0007\f\u0007u\t\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0003\b|\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0003\u000b\u0087\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0005\f\u008e\b\f\n\f\f\f\u0091"+
		"\t\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0097\b\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003"+
		"\u000e\u00a0\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00b0\b\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00b5\b\u000f\u0005\u000f\u00b7"+
		"\b\u000f\n\u000f\f\u000f\u00ba\t\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0003\u000f\u00c1\b\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0005\u000f\u00c6\b\u000f\n\u000f\f\u000f\u00c9\t\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u00d0"+
		"\b\u0010\n\u0010\f\u0010\u00d3\t\u0010\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014"+
		"\u00fb\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014"+
		"\u0119\b\u0014\u0005\u0014\u011b\b\u0014\n\u0014\f\u0014\u011e\t\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0005\u0016\u0128\b\u0016\n\u0016\f\u0016\u012b"+
		"\t\u0016\u0001\u0017\u0001\u0017\u0003\u0017\u012f\b\u0017\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u013a\b\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a"+
		"\u0001\u001a\u0003\u001a\u0145\b\u001a\u0001\u001a\u0001\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0005\u001b\u014c\b\u001b\n\u001b\f\u001b\u014f"+
		"\t\u001b\u0001\u001c\u0001\u001c\u0003\u001c\u0153\b\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u015a\b\u001d"+
		"\n\u001d\f\u001d\u015d\t\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0003\u001e\u0163\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0000"+
		"\u0002\u001e( \u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*,.02468:<>\u0000\u0001\u0001\u0000*0\u0181"+
		"\u0000@\u0001\u0000\u0000\u0000\u0002P\u0001\u0000\u0000\u0000\u0004R"+
		"\u0001\u0000\u0000\u0000\u0006X\u0001\u0000\u0000\u0000\bZ\u0001\u0000"+
		"\u0000\u0000\n]\u0001\u0000\u0000\u0000\fl\u0001\u0000\u0000\u0000\u000e"+
		"n\u0001\u0000\u0000\u0000\u0010{\u0001\u0000\u0000\u0000\u0012}\u0001"+
		"\u0000\u0000\u0000\u0014\u0081\u0001\u0000\u0000\u0000\u0016\u0084\u0001"+
		"\u0000\u0000\u0000\u0018\u008a\u0001\u0000\u0000\u0000\u001a\u0096\u0001"+
		"\u0000\u0000\u0000\u001c\u009f\u0001\u0000\u0000\u0000\u001e\u00c0\u0001"+
		"\u0000\u0000\u0000 \u00ca\u0001\u0000\u0000\u0000\"\u00d6\u0001\u0000"+
		"\u0000\u0000$\u00db\u0001\u0000\u0000\u0000&\u00e0\u0001\u0000\u0000\u0000"+
		"(\u00fa\u0001\u0000\u0000\u0000*\u011f\u0001\u0000\u0000\u0000,\u0124"+
		"\u0001\u0000\u0000\u0000.\u012e\u0001\u0000\u0000\u00000\u0139\u0001\u0000"+
		"\u0000\u00002\u013b\u0001\u0000\u0000\u00004\u0142\u0001\u0000\u0000\u0000"+
		"6\u0148\u0001\u0000\u0000\u00008\u0150\u0001\u0000\u0000\u0000:\u0156"+
		"\u0001\u0000\u0000\u0000<\u0162\u0001\u0000\u0000\u0000>\u0164\u0001\u0000"+
		"\u0000\u0000@A\u0003\u0002\u0001\u0000AB\u0005\u0000\u0000\u0001B\u0001"+
		"\u0001\u0000\u0000\u0000CE\u0003\u0004\u0002\u0000DC\u0001\u0000\u0000"+
		"\u0000EH\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000"+
		"\u0000\u0000GQ\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IK\u0003"+
		"\u0004\u0002\u0000JI\u0001\u0000\u0000\u0000KN\u0001\u0000\u0000\u0000"+
		"LJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MO\u0001\u0000\u0000"+
		"\u0000NL\u0001\u0000\u0000\u0000OQ\u0003(\u0014\u0000PF\u0001\u0000\u0000"+
		"\u0000PL\u0001\u0000\u0000\u0000Q\u0003\u0001\u0000\u0000\u0000RS\u0003"+
		"\u0006\u0003\u0000ST\u0005\u0001\u0000\u0000TU\u0003(\u0014\u0000U\u0005"+
		"\u0001\u0000\u0000\u0000VY\u0003\b\u0004\u0000WY\u0003\n\u0005\u0000X"+
		"V\u0001\u0000\u0000\u0000XW\u0001\u0000\u0000\u0000Y\u0007\u0001\u0000"+
		"\u0000\u0000Z[\u0005E\u0000\u0000[\\\u0003\f\u0006\u0000\\\t\u0001\u0000"+
		"\u0000\u0000]^\u0005$\u0000\u0000^_\u0003\b\u0004\u0000_\u000b\u0001\u0000"+
		"\u0000\u0000`b\u0005\u0002\u0000\u0000ac\u0003\u000e\u0007\u0000ba\u0001"+
		"\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000"+
		"dm\u0005\u0003\u0000\u0000eg\u0005\u0002\u0000\u0000fh\u0003\u000e\u0007"+
		"\u0000gf\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hi\u0001\u0000"+
		"\u0000\u0000ij\u0005\u0003\u0000\u0000jk\u0005\u0004\u0000\u0000km\u0003"+
		"\u001e\u000f\u0000l`\u0001\u0000\u0000\u0000le\u0001\u0000\u0000\u0000"+
		"m\r\u0001\u0000\u0000\u0000ns\u0003\u0010\b\u0000op\u0005\u0005\u0000"+
		"\u0000pr\u0003\u0010\b\u0000qo\u0001\u0000\u0000\u0000ru\u0001\u0000\u0000"+
		"\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000t\u000f\u0001"+
		"\u0000\u0000\u0000us\u0001\u0000\u0000\u0000v|\u0003\u0012\t\u0000wx\u0003"+
		"\u0012\t\u0000xy\u0005\u0001\u0000\u0000yz\u0003(\u0014\u0000z|\u0001"+
		"\u0000\u0000\u0000{v\u0001\u0000\u0000\u0000{w\u0001\u0000\u0000\u0000"+
		"|\u0011\u0001\u0000\u0000\u0000}~\u0005E\u0000\u0000~\u007f\u0005\u0004"+
		"\u0000\u0000\u007f\u0080\u0003\u001e\u000f\u0000\u0080\u0013\u0001\u0000"+
		"\u0000\u0000\u0081\u0082\u0005E\u0000\u0000\u0082\u0083\u0003\u0016\u000b"+
		"\u0000\u0083\u0015\u0001\u0000\u0000\u0000\u0084\u0086\u0005\u0002\u0000"+
		"\u0000\u0085\u0087\u0003\u0018\f\u0000\u0086\u0085\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000"+
		"\u0088\u0089\u0005\u0003\u0000\u0000\u0089\u0017\u0001\u0000\u0000\u0000"+
		"\u008a\u008f\u0003\u001a\r\u0000\u008b\u008c\u0005\u0005\u0000\u0000\u008c"+
		"\u008e\u0003\u001a\r\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008e\u0091"+
		"\u0001\u0000\u0000\u0000\u008f\u008d\u0001\u0000\u0000\u0000\u008f\u0090"+
		"\u0001\u0000\u0000\u0000\u0090\u0019\u0001\u0000\u0000\u0000\u0091\u008f"+
		"\u0001\u0000\u0000\u0000\u0092\u0097\u0003(\u0014\u0000\u0093\u0094\u0005"+
		"E\u0000\u0000\u0094\u0095\u0005\u0001\u0000\u0000\u0095\u0097\u0003(\u0014"+
		"\u0000\u0096\u0092\u0001\u0000\u0000\u0000\u0096\u0093\u0001\u0000\u0000"+
		"\u0000\u0097\u001b\u0001\u0000\u0000\u0000\u0098\u0099\u0003\f\u0006\u0000"+
		"\u0099\u009a\u0005\u0006\u0000\u0000\u009a\u009b\u0003(\u0014\u0000\u009b"+
		"\u00a0\u0001\u0000\u0000\u0000\u009c\u009d\u0005E\u0000\u0000\u009d\u009e"+
		"\u0005\u0006\u0000\u0000\u009e\u00a0\u0003(\u0014\u0000\u009f\u0098\u0001"+
		"\u0000\u0000\u0000\u009f\u009c\u0001\u0000\u0000\u0000\u00a0\u001d\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0006\u000f\uffff\uffff\u0000\u00a2\u00a3"+
		"\u0005\u0002\u0000\u0000\u00a3\u00a4\u0003\u001e\u000f\u0000\u00a4\u00a5"+
		"\u0005\u0003\u0000\u0000\u00a5\u00c1\u0001\u0000\u0000\u0000\u00a6\u00c1"+
		"\u0005\f\u0000\u0000\u00a7\u00c1\u0005%\u0000\u0000\u00a8\u00c1\u0005"+
		"E\u0000\u0000\u00a9\u00c1\u0003 \u0010\u0000\u00aa\u00c1\u0003\"\u0011"+
		"\u0000\u00ab\u00c1\u0003$\u0012\u0000\u00ac\u00af\u0005\u0002\u0000\u0000"+
		"\u00ad\u00b0\u0003\u001e\u000f\u0000\u00ae\u00b0\u0003\u0012\t\u0000\u00af"+
		"\u00ad\u0001\u0000\u0000\u0000\u00af\u00ae\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b8\u0001\u0000\u0000\u0000\u00b1\u00b4\u0005\u0005\u0000\u0000\u00b2"+
		"\u00b5\u0003\u001e\u000f\u0000\u00b3\u00b5\u0003\u0012\t\u0000\u00b4\u00b2"+
		"\u0001\u0000\u0000\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b5\u00b7"+
		"\u0001\u0000\u0000\u0000\u00b6\u00b1\u0001\u0000\u0000\u0000\u00b7\u00ba"+
		"\u0001\u0000\u0000\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000\u00b8\u00b9"+
		"\u0001\u0000\u0000\u0000\u00b9\u00bb\u0001\u0000\u0000\u0000\u00ba\u00b8"+
		"\u0001\u0000\u0000\u0000\u00bb\u00bc\u0005\u0003\u0000\u0000\u00bc\u00bd"+
		"\u0005\u0006\u0000\u0000\u00bd\u00be\u0003\u001e\u000f\u0003\u00be\u00c1"+
		"\u0001\u0000\u0000\u0000\u00bf\u00c1\u0003&\u0013\u0000\u00c0\u00a1\u0001"+
		"\u0000\u0000\u0000\u00c0\u00a6\u0001\u0000\u0000\u0000\u00c0\u00a7\u0001"+
		"\u0000\u0000\u0000\u00c0\u00a8\u0001\u0000\u0000\u0000\u00c0\u00a9\u0001"+
		"\u0000\u0000\u0000\u00c0\u00aa\u0001\u0000\u0000\u0000\u00c0\u00ab\u0001"+
		"\u0000\u0000\u0000\u00c0\u00ac\u0001\u0000\u0000\u0000\u00c0\u00bf\u0001"+
		"\u0000\u0000\u0000\u00c1\u00c7\u0001\u0000\u0000\u0000\u00c2\u00c3\n\u0002"+
		"\u0000\u0000\u00c3\u00c4\u0005\u0006\u0000\u0000\u00c4\u00c6\u0003\u001e"+
		"\u000f\u0003\u00c5\u00c2\u0001\u0000\u0000\u0000\u00c6\u00c9\u0001\u0000"+
		"\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000"+
		"\u0000\u0000\u00c8\u001f\u0001\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000"+
		"\u0000\u0000\u00ca\u00cb\u0005\u001d\u0000\u0000\u00cb\u00cc\u0005\u0002"+
		"\u0000\u0000\u00cc\u00d1\u0003\u0012\t\u0000\u00cd\u00ce\u0005\u0005\u0000"+
		"\u0000\u00ce\u00d0\u0003\u0012\t\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000"+
		"\u00d0\u00d3\u0001\u0000\u0000\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000"+
		"\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d4\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d4\u00d5\u0005\u0003\u0000\u0000"+
		"\u00d5!\u0001\u0000\u0000\u0000\u00d6\u00d7\u0005\u001e\u0000\u0000\u00d7"+
		"\u00d8\u0005\u0002\u0000\u0000\u00d8\u00d9\u0003\u001e\u000f\u0000\u00d9"+
		"\u00da\u0005\u0003\u0000\u0000\u00da#\u0001\u0000\u0000\u0000\u00db\u00dc"+
		"\u0005\u001f\u0000\u0000\u00dc\u00dd\u0005\u0002\u0000\u0000\u00dd\u00de"+
		"\u0003\u001e\u000f\u0000\u00de\u00df\u0005\u0003\u0000\u0000\u00df%\u0001"+
		"\u0000\u0000\u0000\u00e0\u00e1\u0005\r\u0000\u0000\u00e1\u00e2\u0003\u001e"+
		"\u000f\u0000\u00e2\'\u0001\u0000\u0000\u0000\u00e3\u00e4\u0006\u0014\uffff"+
		"\uffff\u0000\u00e4\u00e5\u0005\u0002\u0000\u0000\u00e5\u00e6\u0003(\u0014"+
		"\u0000\u00e6\u00e7\u0005\u0003\u0000\u0000\u00e7\u00fb\u0001\u0000\u0000"+
		"\u0000\u00e8\u00fb\u0003>\u001f\u0000\u00e9\u00fb\u00032\u0019\u0000\u00ea"+
		"\u00fb\u00034\u001a\u0000\u00eb\u00fb\u00038\u001c\u0000\u00ec\u00fb\u0005"+
		"@\u0000\u0000\u00ed\u00fb\u0005)\u0000\u0000\u00ee\u00fb\u0005D\u0000"+
		"\u0000\u00ef\u00fb\u0005C\u0000\u0000\u00f0\u00f1\u0005?\u0000\u0000\u00f1"+
		"\u00fb\u0003(\u0014\u0010\u00f2\u00f3\u00059\u0000\u0000\u00f3\u00fb\u0003"+
		"(\u0014\f\u00f4\u00f5\u00058\u0000\u0000\u00f5\u00fb\u0003(\u0014\u000b"+
		"\u00f6\u00fb\u0003\u0014\n\u0000\u00f7\u00fb\u0003\u001c\u000e\u0000\u00f8"+
		"\u00fb\u0003*\u0015\u0000\u00f9\u00fb\u0003&\u0013\u0000\u00fa\u00e3\u0001"+
		"\u0000\u0000\u0000\u00fa\u00e8\u0001\u0000\u0000\u0000\u00fa\u00e9\u0001"+
		"\u0000\u0000\u0000\u00fa\u00ea\u0001\u0000\u0000\u0000\u00fa\u00eb\u0001"+
		"\u0000\u0000\u0000\u00fa\u00ec\u0001\u0000\u0000\u0000\u00fa\u00ed\u0001"+
		"\u0000\u0000\u0000\u00fa\u00ee\u0001\u0000\u0000\u0000\u00fa\u00ef\u0001"+
		"\u0000\u0000\u0000\u00fa\u00f0\u0001\u0000\u0000\u0000\u00fa\u00f2\u0001"+
		"\u0000\u0000\u0000\u00fa\u00f4\u0001\u0000\u0000\u0000\u00fa\u00f6\u0001"+
		"\u0000\u0000\u0000\u00fa\u00f7\u0001\u0000\u0000\u0000\u00fa\u00f8\u0001"+
		"\u0000\u0000\u0000\u00fa\u00f9\u0001\u0000\u0000\u0000\u00fb\u011c\u0001"+
		"\u0000\u0000\u0000\u00fc\u00fd\n\u000f\u0000\u0000\u00fd\u00fe\u0005="+
		"\u0000\u0000\u00fe\u011b\u0003(\u0014\u0010\u00ff\u0100\n\u000e\u0000"+
		"\u0000\u0100\u0101\u0005>\u0000\u0000\u0101\u011b\u0003(\u0014\u000f\u0102"+
		"\u0103\n\r\u0000\u0000\u0103\u0104\u00051\u0000\u0000\u0104\u011b\u0003"+
		"(\u0014\u000e\u0105\u0106\n\n\u0000\u0000\u0106\u0107\u0005:\u0000\u0000"+
		"\u0107\u011b\u0003(\u0014\u000b\u0108\u0109\n\t\u0000\u0000\u0109\u010a"+
		"\u0005;\u0000\u0000\u010a\u011b\u0003(\u0014\n\u010b\u010c\n\b\u0000\u0000"+
		"\u010c\u010d\u0005<\u0000\u0000\u010d\u011b\u0003(\u0014\t\u010e\u010f"+
		"\n\u0007\u0000\u0000\u010f\u0110\u00058\u0000\u0000\u0110\u011b\u0003"+
		"(\u0014\b\u0111\u0112\n\u0006\u0000\u0000\u0112\u0113\u00059\u0000\u0000"+
		"\u0113\u011b\u0003(\u0014\u0007\u0114\u0115\n\u0001\u0000\u0000\u0115"+
		"\u0116\u0005\u0007\u0000\u0000\u0116\u0118\u0005E\u0000\u0000\u0117\u0119"+
		"\u0003\u0016\u000b\u0000\u0118\u0117\u0001\u0000\u0000\u0000\u0118\u0119"+
		"\u0001\u0000\u0000\u0000\u0119\u011b\u0001\u0000\u0000\u0000\u011a\u00fc"+
		"\u0001\u0000\u0000\u0000\u011a\u00ff\u0001\u0000\u0000\u0000\u011a\u0102"+
		"\u0001\u0000\u0000\u0000\u011a\u0105\u0001\u0000\u0000\u0000\u011a\u0108"+
		"\u0001\u0000\u0000\u0000\u011a\u010b\u0001\u0000\u0000\u0000\u011a\u010e"+
		"\u0001\u0000\u0000\u0000\u011a\u0111\u0001\u0000\u0000\u0000\u011a\u0114"+
		"\u0001\u0000\u0000\u0000\u011b\u011e\u0001\u0000\u0000\u0000\u011c\u011a"+
		"\u0001\u0000\u0000\u0000\u011c\u011d\u0001\u0000\u0000\u0000\u011d)\u0001"+
		"\u0000\u0000\u0000\u011e\u011c\u0001\u0000\u0000\u0000\u011f\u0120\u0005"+
		"\"\u0000\u0000\u0120\u0121\u0003,\u0016\u0000\u0121\u0122\u0005#\u0000"+
		"\u0000\u0122\u0123\u0003(\u0014\u0000\u0123+\u0001\u0000\u0000\u0000\u0124"+
		"\u0129\u0003.\u0017\u0000\u0125\u0126\u0005\u0005\u0000\u0000\u0126\u0128"+
		"\u0003.\u0017\u0000\u0127\u0125\u0001\u0000\u0000\u0000\u0128\u012b\u0001"+
		"\u0000\u0000\u0000\u0129\u0127\u0001\u0000\u0000\u0000\u0129\u012a\u0001"+
		"\u0000\u0000\u0000\u012a-\u0001\u0000\u0000\u0000\u012b\u0129\u0001\u0000"+
		"\u0000\u0000\u012c\u012f\u00030\u0018\u0000\u012d\u012f\u0003\u0004\u0002"+
		"\u0000\u012e\u012c\u0001\u0000\u0000\u0000\u012e\u012d\u0001\u0000\u0000"+
		"\u0000\u012f/\u0001\u0000\u0000\u0000\u0130\u0131\u0005E\u0000\u0000\u0131"+
		"\u0132\u0005\u0001\u0000\u0000\u0132\u013a\u0003(\u0014\u0000\u0133\u0134"+
		"\u0005E\u0000\u0000\u0134\u0135\u0005\u0004\u0000\u0000\u0135\u0136\u0003"+
		"\u001e\u000f\u0000\u0136\u0137\u0005\u0001\u0000\u0000\u0137\u0138\u0003"+
		"(\u0014\u0000\u0138\u013a\u0001\u0000\u0000\u0000\u0139\u0130\u0001\u0000"+
		"\u0000\u0000\u0139\u0133\u0001\u0000\u0000\u0000\u013a1\u0001\u0000\u0000"+
		"\u0000\u013b\u013c\u0005&\u0000\u0000\u013c\u013d\u0003(\u0014\u0000\u013d"+
		"\u013e\u0005\'\u0000\u0000\u013e\u013f\u0003(\u0014\u0000\u013f\u0140"+
		"\u0005(\u0000\u0000\u0140\u0141\u0003(\u0014\u0000\u01413\u0001\u0000"+
		"\u0000\u0000\u0142\u0144\u0005\b\u0000\u0000\u0143\u0145\u00036\u001b"+
		"\u0000\u0144\u0143\u0001\u0000\u0000\u0000\u0144\u0145\u0001\u0000\u0000"+
		"\u0000\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u0147\u0005\t\u0000\u0000"+
		"\u01475\u0001\u0000\u0000\u0000\u0148\u014d\u0003(\u0014\u0000\u0149\u014a"+
		"\u0005\u0005\u0000\u0000\u014a\u014c\u0003(\u0014\u0000\u014b\u0149\u0001"+
		"\u0000\u0000\u0000\u014c\u014f\u0001\u0000\u0000\u0000\u014d\u014b\u0001"+
		"\u0000\u0000\u0000\u014d\u014e\u0001\u0000\u0000\u0000\u014e7\u0001\u0000"+
		"\u0000\u0000\u014f\u014d\u0001\u0000\u0000\u0000\u0150\u0152\u0005\n\u0000"+
		"\u0000\u0151\u0153\u0003:\u001d\u0000\u0152\u0151\u0001\u0000\u0000\u0000"+
		"\u0152\u0153\u0001\u0000\u0000\u0000\u0153\u0154\u0001\u0000\u0000\u0000"+
		"\u0154\u0155\u0005\u000b\u0000\u0000\u01559\u0001\u0000\u0000\u0000\u0156"+
		"\u015b\u0003<\u001e\u0000\u0157\u0158\u0005\u0005\u0000\u0000\u0158\u015a"+
		"\u0003<\u001e\u0000\u0159\u0157\u0001\u0000\u0000\u0000\u015a\u015d\u0001"+
		"\u0000\u0000\u0000\u015b\u0159\u0001\u0000\u0000\u0000\u015b\u015c\u0001"+
		"\u0000\u0000\u0000\u015c;\u0001\u0000\u0000\u0000\u015d\u015b\u0001\u0000"+
		"\u0000\u0000\u015e\u015f\u0005E\u0000\u0000\u015f\u0160\u0005\u0004\u0000"+
		"\u0000\u0160\u0163\u0003(\u0014\u0000\u0161\u0163\u0003(\u0014\u0000\u0162"+
		"\u015e\u0001\u0000\u0000\u0000\u0162\u0161\u0001\u0000\u0000\u0000\u0163"+
		"=\u0001\u0000\u0000\u0000\u0164\u0165\u0007\u0000\u0000\u0000\u0165?\u0001"+
		"\u0000\u0000\u0000\u001fFLPXbgls{\u0086\u008f\u0096\u009f\u00af\u00b4"+
		"\u00b8\u00c0\u00c7\u00d1\u00fa\u0118\u011a\u011c\u0129\u012e\u0139\u0144"+
		"\u014d\u0152\u015b\u0162";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}