// Generated from PsqlParser.g4 by ANTLR 4.13.1
package raw.client.sql.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class PsqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LINE_COMMENT_START=1, MULTI_LINE_COMMENT_START=2, QUOTE=3, DOT=4, STAR=5, 
		COMMA=6, L_SQ_BRACKET=7, R_SQ_BRACKET=8, L_PAREN=9, R_PAREN=10, SEMICOLON=11, 
		WITH_TIME_ZONE=12, EQ=13, LE=14, GT=15, LEQ=16, GEQ=17, NEQ1=18, NEQ2=19, 
		PLUS=20, MINUS=21, DIV=22, MOD_OP=23, EXP_OP=24, SQ_ROOT=25, CUBE_ROOT=26, 
		FACTORIAL=27, FACTORIAS=28, BITWISE_AND=29, BITWISE_OR=30, BITWISE_XOR=31, 
		BITWISE_NOT=32, BITWISE_SHIFT_LEFT=33, BITWISE_SHIFT_RIGHT=34, CONCAT=35, 
		REGEX_CASE_INSENSITIVE_MATCH=36, REGEX_CASE_INSENSITIVE_NOT_MATCH=37, 
		UNICODE=38, DOUBLE_COLON=39, NO_FLOATING_NUMBER=40, FLOATING_POINT=41, 
		PARAM=42, DOUBLE_QUOTED_STRING=43, SINGLE_QUOTED_STRING=44, TICKS_QUOTED_STRING=45, 
		BIGSERIAL=46, SMALLSERIAL=47, BOX=48, BYTEA=49, CIDR=50, CIRCLE=51, INET=52, 
		JSONB=53, LINE=54, LSEG=55, MACADDR=56, MACADDR8=57, MONEY=58, PG_LSN=59, 
		PG_SNAPSHOT=60, POINT=61, POLYGON=62, TSQUERY=63, TSVECTOR=64, TXID_SNAPSHOT=65, 
		UUID=66, SERIAL=67, CHARACTER_VARYING=68, BIT_VARYING=69, INT_8=70, SERIAL_8=71, 
		VARBIT=72, BOOL=73, CHAR=74, FLOAT_8=75, INT_4=76, INT=77, DECIMAL=78, 
		FLOAT_4=79, INT_2=80, SERIAL_2=81, SERIAL_4=82, TIMESTAMPTZ=83, A_KW=84, 
		ABORT=85, ABS=86, ABSENT=87, ABSOLUTE=88, ACCESS=89, ACCORDING=90, ACOS=91, 
		ACTION=92, ADA=93, ADD=94, ADMIN=95, AFTER=96, AGGREGATE=97, ALL=98, ALLOCATE=99, 
		ALSO=100, ALTER=101, ALWAYS=102, ANALYSE=103, ANALYZE=104, AND=105, ANY=106, 
		ANY_VALUE=107, ARE=108, ARRAY=109, ARRAY_AGG=110, ARRAY_MAX_CARDINALITY=111, 
		AS=112, ASC=113, ASENSITIVE=114, ASIN=115, ASSERTION=116, ASSIGNMENT=117, 
		ASYMMETRIC=118, AT=119, ATAN=120, ATOMIC=121, ATTACH=122, ATTRIBUTE=123, 
		ATTRIBUTES=124, AUTHORIZATION=125, AVG=126, BACKWARD=127, BASE64=128, 
		BEFORE=129, BEGIN=130, BEGIN_FRAME=131, BEGIN_PARTITION=132, BERNOULLI=133, 
		BETWEEN=134, BIGINT=135, BINARY=136, BIT=137, BIT_LENGTH=138, BLOB=139, 
		BLOCKED=140, BOM=141, BOOLEAN=142, BOTH=143, BREADTH=144, BTRIM=145, BY=146, 
		C_KW=147, CACHE=148, CALL=149, CALLED=150, CARDINALITY=151, CASCADE=152, 
		CASCADED=153, CASE=154, CAST=155, CATALOG=156, CATALOG_NAME=157, CEIL=158, 
		CEILING=159, CHAIN=160, CHAINING=161, CHARACTER=162, CHARACTERISTICS=163, 
		CHARACTERS=164, CHARACTER_LENGTH=165, CHARACTER_SET_CATALOG=166, CHARACTER_SET_NAME=167, 
		CHARACTER_SET_SCHEMA=168, CHAR_LENGTH=169, CHECK=170, CHECKPOINT=171, 
		CLASS=172, CLASSIFIER=173, CLASS_ORIGIN=174, CLOB=175, CLOSE=176, CLUSTER=177, 
		COALESCE=178, COBOL=179, COLLATE=180, COLLATION=181, COLLATION_CATALOG=182, 
		COLLATION_NAME=183, COLLATION_SCHEMA=184, COLLECT=185, COLUMN=186, COLUMNS=187, 
		COLUMN_NAME=188, COMMAND_FUNCTION=189, COMMAND_FUNCTION_CODE=190, COMMENT=191, 
		COMMENTS=192, COMMIT=193, COMMITTED=194, COMPRESSION=195, CONCURRENTLY=196, 
		CONDITION=197, CONDITIONAL=198, CONDITION_NUMBER=199, CONFIGURATION=200, 
		CONFLICT=201, CONNECT=202, CONNECTION=203, CONNECTION_NAME=204, CONSTRAINT=205, 
		CONSTRAINTS=206, CONSTRAINT_CATALOG=207, CONSTRAINT_NAME=208, CONSTRAINT_SCHEMA=209, 
		CONSTRUCTOR=210, CONTAINS=211, CONTENT=212, CONTINUE=213, CONTROL=214, 
		CONVERSION=215, CONVERT=216, COPARTITION=217, COPY=218, CORR=219, CORRESPONDING=220, 
		COS=221, COSH=222, COST=223, COUNT=224, COVAR_POP=225, COVAR_SAMP=226, 
		CREATE=227, CROSS=228, CSV=229, CUBE=230, CUME_DIST=231, CURRENT=232, 
		CURRENT_CATALOG=233, CURRENT_DATE=234, CURRENT_DEFAULT_TRANSFORM_GROUP=235, 
		CURRENT_PATH=236, CURRENT_ROLE=237, CURRENT_ROW=238, CURRENT_SCHEMA=239, 
		CURRENT_TIME=240, CURRENT_TIMESTAMP=241, CURRENT_TRANSFORM_GROUP_FOR_TYPE=242, 
		CURRENT_USER=243, CURSOR=244, CURSOR_NAME=245, CYCLE=246, DATA=247, DATABASE=248, 
		DATALINK=249, DATE=250, DATETIME_INTERVAL_CODE=251, DATETIME_INTERVAL_PRECISION=252, 
		DAY=253, DB=254, DEALLOCATE=255, DEC=256, DECFLOAT=257, DECLARE=258, DEFAULT=259, 
		DEFAULTS=260, DEFERRABLE=261, DEFERRED=262, DEFINE=263, DEFINED=264, DEFINER=265, 
		DEGREE=266, DELETE=267, DELIMITER=268, DELIMITERS=269, DENSE_RANK=270, 
		DEPENDS=271, DEPTH=272, DEREF=273, DERIVED=274, DESC=275, DESCRIBE=276, 
		DESCRIPTOR=277, DETACH=278, DETERMINISTIC=279, DIAGNOSTICS=280, DICTIONARY=281, 
		DISABLE=282, DISCARD=283, DISCONNECT=284, DISPATCH=285, DISTINCT=286, 
		DLNEWCOPY=287, DLPREVIOUSCOPY=288, DLURLCOMPLETE=289, DLURLCOMPLETEONLY=290, 
		DLURLCOMPLETEWRITE=291, DLURLPATH=292, DLURLPATHONLY=293, DLURLPATHWRITE=294, 
		DLURLSCHEME=295, DLURLSERVER=296, DLVALUE=297, DO=298, DOCUMENT=299, DOMAIN=300, 
		DOUBLE=301, DROP=302, DYNAMIC=303, DYNAMIC_FUNCTION=304, DYNAMIC_FUNCTION_CODE=305, 
		EACH=306, ELEMENT=307, ELSE=308, EMPTY=309, ENABLE=310, ENCODING=311, 
		ENCRYPTED=312, END=313, END_EXEC=314, END_FRAME=315, END_PARTITION=316, 
		ENFORCED=317, ENUM=318, EQUALS=319, ERROR=320, ESCAPE=321, EVENT=322, 
		EVERY=323, EXCEPT=324, EXCEPTION=325, EXCLUDE=326, EXCLUDING=327, EXCLUSIVE=328, 
		EXEC=329, EXECUTE=330, EXISTS=331, EXP=332, EXPLAIN=333, EXPRESSION=334, 
		EXTENSION=335, EXTERNAL=336, EXTRACT=337, FALSE=338, FAMILY=339, FETCH=340, 
		FILE=341, FILTER=342, FINAL=343, FINALIZE=344, FINISH=345, FIRST=346, 
		FIRST_VALUE=347, FLAG=348, FLOAT=349, FLOOR=350, FOLLOWING=351, FOR=352, 
		FORCE=353, FOREIGN=354, FORMAT=355, FORTRAN=356, FORWARD=357, FOUND=358, 
		FRAME_ROW=359, FREE=360, FREEZE=361, FROM=362, FS=363, FULFILL=364, FULL=365, 
		FUNCTION=366, FUNCTIONS=367, FUSION=368, G_KW=369, GENERAL=370, GENERATED=371, 
		GET=372, GLOBAL=373, GO=374, GOTO=375, GRANT=376, GRANTED=377, GREATEST=378, 
		GROUP=379, GROUPING=380, GROUPS=381, HANDLER=382, HAVING=383, HEADER=384, 
		HEX=385, HIERARCHY=386, HOLD=387, HOUR=388, ID=389, IDENTITY=390, IF=391, 
		IGNORE=392, ILIKE=393, IMMEDIATE=394, IMMEDIATELY=395, IMMUTABLE=396, 
		IMPLEMENTATION=397, IMPLICIT=398, IMPORT=399, IN=400, INCLUDE=401, INCLUDING=402, 
		INCREMENT=403, INDENT=404, INDEX=405, INDEXES=406, INDICATOR=407, INHERIT=408, 
		INHERITS=409, INITIAL=410, INITIALLY=411, INLINE=412, INNER=413, INOUT=414, 
		INPUT=415, INSENSITIVE=416, INSERT=417, INSTANCE=418, INSTANTIABLE=419, 
		INSTEAD=420, INTEGER=421, INTEGRITY=422, INTERSECT=423, INTERSECTION=424, 
		INTERVAL=425, INTO=426, INVOKER=427, IS=428, ISNULL=429, ISOLATION=430, 
		JOIN=431, JSON=432, JSON_ARRAY=433, JSON_ARRAYAGG=434, JSON_EXISTS=435, 
		JSON_OBJECT=436, JSON_OBJECTAGG=437, JSON_QUERY=438, JSON_SCALAR=439, 
		JSON_SERIALIZE=440, JSON_TABLE=441, JSON_TABLE_PRIMITIVE=442, JSON_VALUE=443, 
		K_KW=444, KEEP=445, KEY=446, KEYS=447, KEY_MEMBER=448, KEY_TYPE=449, LABEL=450, 
		LAG=451, LANGUAGE=452, LARGE=453, LAST=454, LAST_VALUE=455, LATERAL=456, 
		LEAD=457, LEADING=458, LEAKPROOF=459, LEAST=460, LEFT=461, LENGTH=462, 
		LEVEL=463, LIBRARY=464, LIKE=465, LIKE_REGEX=466, LIMIT=467, LINK=468, 
		LISTAGG=469, LISTEN=470, LN=471, LOAD=472, LOCAL=473, LOCALTIME=474, LOCALTIMESTAMP=475, 
		LOCATION=476, LOCATOR=477, LOCK=478, LOCKED=479, LOG=480, LOG10=481, LOGGED=482, 
		LOWER=483, LPAD=484, LTRIM=485, M_KW=486, MAP=487, MAPPING=488, MATCH=489, 
		MATCHED=490, MATCHES=491, MATCH_NUMBER=492, MATCH_RECOGNIZE=493, MATERIALIZED=494, 
		MAX=495, MAXVALUE=496, MEASURES=497, MEMBER=498, MERGE=499, MESSAGE_LENGTH=500, 
		MESSAGE_OCTET_LENGTH=501, MESSAGE_TEXT=502, METHOD=503, MIN=504, MINUTE=505, 
		MINVALUE=506, MOD=507, MODE=508, MODIFIES=509, MODULE=510, MONTH=511, 
		MORE_KW=512, MOVE=513, MULTISET=514, MUMPS=515, NAME=516, NAMES=517, NAMESPACE=518, 
		NATIONAL=519, NATURAL=520, NCHAR=521, NCLOB=522, NESTED=523, NESTING=524, 
		NEW=525, NEXT=526, NFC=527, NFD=528, NFKC=529, NFKD=530, NIL=531, NO=532, 
		NONE=533, NORMALIZE=534, NORMALIZED=535, NOT=536, NOTHING=537, NOTIFY=538, 
		NOTNULL=539, NOWAIT=540, NTH_VALUE=541, NTILE=542, NULL=543, NULLABLE=544, 
		NULLIF=545, NULLS=546, NULL_ORDERING=547, NUMBER=548, NUMERIC=549, OBJECT=550, 
		OCCURRENCE=551, OCCURRENCES_REGEX=552, OCTETS=553, OCTET_LENGTH=554, OF=555, 
		OFF=556, OFFSET=557, OIDS=558, OLD=559, OMIT=560, ON=561, ONE=562, ONLY=563, 
		OPEN=564, OPERATOR_KW=565, OPTION=566, OPTIONS=567, OR=568, ORDER=569, 
		ORDERING=570, ORDINALITY=571, OTHERS=572, OUT=573, OUTER=574, OUTPUT=575, 
		OVER=576, OVERFLOW=577, OVERLAPS=578, OVERLAY=579, OVERRIDING=580, OWNED=581, 
		OWNER=582, P_KW=583, PAD=584, PARALLEL=585, PARAMETER=586, PARAMETER_MODE=587, 
		PARAMETER_NAME=588, PARAMETER_ORDINAL_POSITION=589, PARAMETER_SPECIFIC_CATALOG=590, 
		PARAMETER_SPECIFIC_NAME=591, PARAMETER_SPECIFIC_SCHEMA=592, PARSER=593, 
		PARTIAL=594, PARTITION=595, PASCAL=596, PASS=597, PASSING=598, PASSTHROUGH=599, 
		PASSWORD=600, PAST=601, PATH=602, PATTERN=603, PER=604, PERCENT=605, PERCENTILE_CONT=606, 
		PERCENTILE_DISC=607, PERCENT_RANK=608, PERIOD=609, PERMISSION=610, PERMUTE=611, 
		PIPE=612, PLACING=613, PLAN=614, PLANS=615, PLI=616, POLICY=617, PORTION=618, 
		POSITION=619, POSITION_REGEX=620, POWER=621, PRECEDES=622, PRECEDING=623, 
		PRECISION=624, PREPARE=625, PREPARED=626, PRESERVE=627, PREV=628, PRIMARY=629, 
		PRIOR=630, PRIVATE=631, PRIVILEGES=632, PROCEDURAL=633, PROCEDURE=634, 
		PROCEDURES=635, PROGRAM=636, PRUNE=637, PTF=638, PUBLIC=639, PUBLICATION=640, 
		QUOTE_KW=641, QUOTES=642, RANGE=643, RANK=644, READ=645, READS=646, REAL=647, 
		REASSIGN=648, RECHECK=649, RECOVERY=650, RECURSIVE=651, REF=652, REFERENCES=653, 
		REFERENCING=654, REFRESH=655, REGR_AVGX=656, REGR_AVGY=657, REGR_COUNT=658, 
		REGR_INTERCEPT=659, REGR_R2=660, REGR_SLOPE=661, REGR_SXX=662, REGR_SXY=663, 
		REGR_SYY=664, REINDEX=665, RELATIVE=666, RELEASE=667, RENAME=668, REPEATABLE=669, 
		REPLACE=670, REPLICA=671, REQUIRING=672, RESET=673, RESPECT=674, RESTART=675, 
		RESTORE=676, RESTRICT=677, RESULT=678, RETURN=679, RETURNED_CARDINALITY=680, 
		RETURNED_LENGTH=681, RETURNED_OCTET_LENGTH=682, RETURNED_SQLSTATE=683, 
		RETURNING=684, RETURNS=685, REVOKE=686, RIGHT=687, ROLE=688, ROLLBACK=689, 
		ROLLUP=690, ROUTINE=691, ROUTINES=692, ROUTINE_CATALOG=693, ROUTINE_NAME=694, 
		ROUTINE_SCHEMA=695, ROW=696, ROWS=697, ROW_COUNT=698, ROW_NUMBER=699, 
		RPAD=700, RTRIM=701, RULE=702, RUNNING=703, SAVEPOINT=704, SCALAR=705, 
		SCALE=706, SCHEMA=707, SCHEMAS=708, SCHEMA_NAME=709, SCOPE=710, SCOPE_CATALOG=711, 
		SCOPE_NAME=712, SCOPE_SCHEMA=713, SCROLL=714, SEARCH=715, SECOND=716, 
		SECTION=717, SECURITY=718, SEEK=719, SELECT=720, SELECTIVE=721, SELF=722, 
		SEMANTICS=723, SENSITIVE=724, SEQUENCE=725, SEQUENCES=726, SERIALIZABLE=727, 
		SERVER=728, SERVER_NAME=729, SESSION=730, SESSION_USER=731, SET=732, SETOF=733, 
		SETS=734, SHARE=735, SHOW=736, SIMILAR=737, SIMPLE=738, SIN=739, SINH=740, 
		SIZE=741, SKIP_KW=742, SMALLINT=743, SNAPSHOT=744, SOME=745, SORT_DIRECTION=746, 
		SOURCE=747, SPACE=748, SPECIFIC=749, SPECIFICTYPE=750, SPECIFIC_NAME=751, 
		SQL=752, SQLCODE=753, SQLERROR=754, SQLEXCEPTION=755, SQLSTATE=756, SQLWARNING=757, 
		SQRT=758, STABLE=759, STANDALONE=760, START=761, STATE=762, STATEMENT=763, 
		STATIC=764, STATISTICS=765, STDDEV_POP=766, STDDEV_SAMP=767, STDIN=768, 
		STDOUT=769, STORAGE=770, STORED=771, STRICT=772, STRING=773, STRIP=774, 
		STRUCTURE=775, STYLE=776, SUBCLASS_ORIGIN=777, SUBMULTISET=778, SUBSCRIPTION=779, 
		SUBSET=780, SUBSTRING=781, SUBSTRING_REGEX=782, SUCCEEDS=783, SUM=784, 
		SUPPORT=785, SYMMETRIC=786, SYSID=787, SYSTEM=788, SYSTEM_TIME=789, SYSTEM_USER=790, 
		T_KW=791, TABLE=792, TABLES=793, TABLESAMPLE=794, TABLESPACE=795, TABLE_NAME=796, 
		TAN=797, TANH=798, TEMP=799, TEMPLATE=800, TEMPORARY=801, TEXT=802, THEN=803, 
		THROUGH=804, TIES=805, TIME=806, TIMESTAMP=807, TIMEZONE_HOUR=808, TIMEZONE_MINUTE=809, 
		TO=810, TOKEN=811, TOP_LEVEL_COUNT=812, TRAILING=813, TRANSACTION=814, 
		TRANSACTIONS_COMMITTED=815, TRANSACTIONS_ROLLED_BACK=816, TRANSACTION_ACTIVE=817, 
		TRANSFORM=818, TRANSFORMS=819, TRANSLATE=820, TRANSLATE_REGEX=821, TRANSLATION=822, 
		TREAT=823, TRIGGER=824, TRIGGER_CATALOG=825, TRIGGER_NAME=826, TRIGGER_SCHEMA=827, 
		TRIM=828, TRIM_ARRAY=829, TRUE=830, TRUNCATE=831, TRUSTED=832, TYPE=833, 
		TYPES=834, UESCAPE=835, UNBOUNDED=836, UNCOMMITTED=837, UNCONDITIONAL=838, 
		UNDER=839, UNENCRYPTED=840, UNION=841, UNIQUE=842, UNKNOWN=843, UNLINK=844, 
		UNLISTEN=845, UNLOGGED=846, UNMATCHED=847, UNNAMED=848, UNNEST=849, UNTIL=850, 
		UNTYPED=851, UPDATE=852, UPPER=853, URI=854, USAGE=855, USER=856, USER_DEFINED_TYPE_CATALOG=857, 
		USER_DEFINED_TYPE_CODE=858, USER_DEFINED_TYPE_NAME=859, USER_DEFINED_TYPE_SCHEMA=860, 
		USING=861, UTF16=862, UTF32=863, UTF8=864, VACUUM=865, VALID=866, VALIDATE=867, 
		VALIDATOR=868, VALUE=869, VALUES=870, VALUE_OF=871, VARBINARY=872, VARCHAR=873, 
		VARIADIC=874, VARYING=875, VAR_POP=876, VAR_SAMP=877, VERBOSE=878, VERSION=879, 
		VERSIONING=880, VIEW=881, VIEWS=882, VOLATILE=883, WHEN=884, WHENEVER=885, 
		WHERE=886, WHITESPACE=887, WIDTH_BUCKET=888, WINDOW=889, WITH=890, WITHIN=891, 
		WITHOUT=892, WORK=893, WRAPPER=894, WRITE=895, XML=896, XMLAGG=897, XMLATTRIBUTES=898, 
		XMLBINARY=899, XMLCAST=900, XMLCOMMENT=901, XMLCONCAT=902, XMLDECLARATION=903, 
		XMLDOCUMENT=904, XMLELEMENT=905, XMLEXISTS=906, XMLFOREST=907, XMLITERATE=908, 
		XMLNAMESPACES=909, XMLPARSE=910, XMLPI=911, XMLQUERY=912, XMLROOT=913, 
		XMLSCHEMA=914, XMLSERIALIZE=915, XMLTABLE=916, XMLTEXT=917, XMLVALIDATE=918, 
		YEAR=919, YES=920, ZONE=921, WORD=922, UNKNOWN_WORD=923, WS=924, SL_TYPE_KW=925, 
		SL_DEFAULT_KW=926, SL_PARAM_KW=927, SL_RETURN_KW=928, SL_UNKNOWN_TOKEN=929, 
		SL_WORD=930, SL_SPACES=931, SL_LINE_COMMENT_END=932, SL_WS=933, ML_TYPE_KW=934, 
		ML_DEFAULT_KW=935, ML_PARAM_KW=936, ML_RETURN_KW=937, ML_UNKNOWN_TOKEN=938, 
		ML_WORD=939, ML_WS=940, MULTI_LINE_COMMENT_END=941, ML_STAR=942, UNKNOWN_WORD_END=943, 
		IN_UNKNOWN_WORD=944;
	public static final int
		RULE_prog = 0, RULE_code = 1, RULE_comment = 2, RULE_singleline_comment = 3, 
		RULE_singleline_value_comment = 4, RULE_singleline_param_comment = 5, 
		RULE_singleline_type_comment = 6, RULE_singleline_default_comment = 7, 
		RULE_singleline_return_comment = 8, RULE_singleline_unknown_type_comment = 9, 
		RULE_singleline_normal_comment_value = 10, RULE_multiline_comment = 11, 
		RULE_multiline_value_comment = 12, RULE_multiline_param_comment = 13, 
		RULE_multiline_type_comment = 14, RULE_multiline_default_comment = 15, 
		RULE_multiline_return_comment = 16, RULE_multiline_unknown_type_comment = 17, 
		RULE_multiline_normal_comment_value = 18, RULE_multiline_word_or_star = 19, 
		RULE_stmt = 20, RULE_stmt_items = 21, RULE_reserved_keyword = 22, RULE_non_reserved_keyword = 23, 
		RULE_operator = 24, RULE_proj = 25, RULE_idnt = 26, RULE_literal = 27, 
		RULE_tipe = 28, RULE_psql_type = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "code", "comment", "singleline_comment", "singleline_value_comment", 
			"singleline_param_comment", "singleline_type_comment", "singleline_default_comment", 
			"singleline_return_comment", "singleline_unknown_type_comment", "singleline_normal_comment_value", 
			"multiline_comment", "multiline_value_comment", "multiline_param_comment", 
			"multiline_type_comment", "multiline_default_comment", "multiline_return_comment", 
			"multiline_unknown_type_comment", "multiline_normal_comment_value", "multiline_word_or_star", 
			"stmt", "stmt_items", "reserved_keyword", "non_reserved_keyword", "operator", 
			"proj", "idnt", "literal", "tipe", "psql_type"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'--'", "'/*'", "'''", "'.'", null, "','", "'['", "']'", "'('", 
			"')'", "';'", null, "'='", "'<'", "'>'", "'<='", "'>='", "'<>'", "'!='", 
			"'+'", "'-'", "'/'", "'%'", "'^'", "'|/'", "'||/'", "'!'", "'!!'", "'&'", 
			"'|'", "'#'", "'~'", "'<<'", "'>>'", "'||'", "'~*'", "'!~*'", "'U&'", 
			"'::'", null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "'*/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LINE_COMMENT_START", "MULTI_LINE_COMMENT_START", "QUOTE", "DOT", 
			"STAR", "COMMA", "L_SQ_BRACKET", "R_SQ_BRACKET", "L_PAREN", "R_PAREN", 
			"SEMICOLON", "WITH_TIME_ZONE", "EQ", "LE", "GT", "LEQ", "GEQ", "NEQ1", 
			"NEQ2", "PLUS", "MINUS", "DIV", "MOD_OP", "EXP_OP", "SQ_ROOT", "CUBE_ROOT", 
			"FACTORIAL", "FACTORIAS", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", 
			"BITWISE_NOT", "BITWISE_SHIFT_LEFT", "BITWISE_SHIFT_RIGHT", "CONCAT", 
			"REGEX_CASE_INSENSITIVE_MATCH", "REGEX_CASE_INSENSITIVE_NOT_MATCH", "UNICODE", 
			"DOUBLE_COLON", "NO_FLOATING_NUMBER", "FLOATING_POINT", "PARAM", "DOUBLE_QUOTED_STRING", 
			"SINGLE_QUOTED_STRING", "TICKS_QUOTED_STRING", "BIGSERIAL", "SMALLSERIAL", 
			"BOX", "BYTEA", "CIDR", "CIRCLE", "INET", "JSONB", "LINE", "LSEG", "MACADDR", 
			"MACADDR8", "MONEY", "PG_LSN", "PG_SNAPSHOT", "POINT", "POLYGON", "TSQUERY", 
			"TSVECTOR", "TXID_SNAPSHOT", "UUID", "SERIAL", "CHARACTER_VARYING", "BIT_VARYING", 
			"INT_8", "SERIAL_8", "VARBIT", "BOOL", "CHAR", "FLOAT_8", "INT_4", "INT", 
			"DECIMAL", "FLOAT_4", "INT_2", "SERIAL_2", "SERIAL_4", "TIMESTAMPTZ", 
			"A_KW", "ABORT", "ABS", "ABSENT", "ABSOLUTE", "ACCESS", "ACCORDING", 
			"ACOS", "ACTION", "ADA", "ADD", "ADMIN", "AFTER", "AGGREGATE", "ALL", 
			"ALLOCATE", "ALSO", "ALTER", "ALWAYS", "ANALYSE", "ANALYZE", "AND", "ANY", 
			"ANY_VALUE", "ARE", "ARRAY", "ARRAY_AGG", "ARRAY_MAX_CARDINALITY", "AS", 
			"ASC", "ASENSITIVE", "ASIN", "ASSERTION", "ASSIGNMENT", "ASYMMETRIC", 
			"AT", "ATAN", "ATOMIC", "ATTACH", "ATTRIBUTE", "ATTRIBUTES", "AUTHORIZATION", 
			"AVG", "BACKWARD", "BASE64", "BEFORE", "BEGIN", "BEGIN_FRAME", "BEGIN_PARTITION", 
			"BERNOULLI", "BETWEEN", "BIGINT", "BINARY", "BIT", "BIT_LENGTH", "BLOB", 
			"BLOCKED", "BOM", "BOOLEAN", "BOTH", "BREADTH", "BTRIM", "BY", "C_KW", 
			"CACHE", "CALL", "CALLED", "CARDINALITY", "CASCADE", "CASCADED", "CASE", 
			"CAST", "CATALOG", "CATALOG_NAME", "CEIL", "CEILING", "CHAIN", "CHAINING", 
			"CHARACTER", "CHARACTERISTICS", "CHARACTERS", "CHARACTER_LENGTH", "CHARACTER_SET_CATALOG", 
			"CHARACTER_SET_NAME", "CHARACTER_SET_SCHEMA", "CHAR_LENGTH", "CHECK", 
			"CHECKPOINT", "CLASS", "CLASSIFIER", "CLASS_ORIGIN", "CLOB", "CLOSE", 
			"CLUSTER", "COALESCE", "COBOL", "COLLATE", "COLLATION", "COLLATION_CATALOG", 
			"COLLATION_NAME", "COLLATION_SCHEMA", "COLLECT", "COLUMN", "COLUMNS", 
			"COLUMN_NAME", "COMMAND_FUNCTION", "COMMAND_FUNCTION_CODE", "COMMENT", 
			"COMMENTS", "COMMIT", "COMMITTED", "COMPRESSION", "CONCURRENTLY", "CONDITION", 
			"CONDITIONAL", "CONDITION_NUMBER", "CONFIGURATION", "CONFLICT", "CONNECT", 
			"CONNECTION", "CONNECTION_NAME", "CONSTRAINT", "CONSTRAINTS", "CONSTRAINT_CATALOG", 
			"CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONSTRUCTOR", "CONTAINS", "CONTENT", 
			"CONTINUE", "CONTROL", "CONVERSION", "CONVERT", "COPARTITION", "COPY", 
			"CORR", "CORRESPONDING", "COS", "COSH", "COST", "COUNT", "COVAR_POP", 
			"COVAR_SAMP", "CREATE", "CROSS", "CSV", "CUBE", "CUME_DIST", "CURRENT", 
			"CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", 
			"CURRENT_PATH", "CURRENT_ROLE", "CURRENT_ROW", "CURRENT_SCHEMA", "CURRENT_TIME", 
			"CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", 
			"CURSOR", "CURSOR_NAME", "CYCLE", "DATA", "DATABASE", "DATALINK", "DATE", 
			"DATETIME_INTERVAL_CODE", "DATETIME_INTERVAL_PRECISION", "DAY", "DB", 
			"DEALLOCATE", "DEC", "DECFLOAT", "DECLARE", "DEFAULT", "DEFAULTS", "DEFERRABLE", 
			"DEFERRED", "DEFINE", "DEFINED", "DEFINER", "DEGREE", "DELETE", "DELIMITER", 
			"DELIMITERS", "DENSE_RANK", "DEPENDS", "DEPTH", "DEREF", "DERIVED", "DESC", 
			"DESCRIBE", "DESCRIPTOR", "DETACH", "DETERMINISTIC", "DIAGNOSTICS", "DICTIONARY", 
			"DISABLE", "DISCARD", "DISCONNECT", "DISPATCH", "DISTINCT", "DLNEWCOPY", 
			"DLPREVIOUSCOPY", "DLURLCOMPLETE", "DLURLCOMPLETEONLY", "DLURLCOMPLETEWRITE", 
			"DLURLPATH", "DLURLPATHONLY", "DLURLPATHWRITE", "DLURLSCHEME", "DLURLSERVER", 
			"DLVALUE", "DO", "DOCUMENT", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "DYNAMIC_FUNCTION", 
			"DYNAMIC_FUNCTION_CODE", "EACH", "ELEMENT", "ELSE", "EMPTY", "ENABLE", 
			"ENCODING", "ENCRYPTED", "END", "END_EXEC", "END_FRAME", "END_PARTITION", 
			"ENFORCED", "ENUM", "EQUALS", "ERROR", "ESCAPE", "EVENT", "EVERY", "EXCEPT", 
			"EXCEPTION", "EXCLUDE", "EXCLUDING", "EXCLUSIVE", "EXEC", "EXECUTE", 
			"EXISTS", "EXP", "EXPLAIN", "EXPRESSION", "EXTENSION", "EXTERNAL", "EXTRACT", 
			"FALSE", "FAMILY", "FETCH", "FILE", "FILTER", "FINAL", "FINALIZE", "FINISH", 
			"FIRST", "FIRST_VALUE", "FLAG", "FLOAT", "FLOOR", "FOLLOWING", "FOR", 
			"FORCE", "FOREIGN", "FORMAT", "FORTRAN", "FORWARD", "FOUND", "FRAME_ROW", 
			"FREE", "FREEZE", "FROM", "FS", "FULFILL", "FULL", "FUNCTION", "FUNCTIONS", 
			"FUSION", "G_KW", "GENERAL", "GENERATED", "GET", "GLOBAL", "GO", "GOTO", 
			"GRANT", "GRANTED", "GREATEST", "GROUP", "GROUPING", "GROUPS", "HANDLER", 
			"HAVING", "HEADER", "HEX", "HIERARCHY", "HOLD", "HOUR", "ID", "IDENTITY", 
			"IF", "IGNORE", "ILIKE", "IMMEDIATE", "IMMEDIATELY", "IMMUTABLE", "IMPLEMENTATION", 
			"IMPLICIT", "IMPORT", "IN", "INCLUDE", "INCLUDING", "INCREMENT", "INDENT", 
			"INDEX", "INDEXES", "INDICATOR", "INHERIT", "INHERITS", "INITIAL", "INITIALLY", 
			"INLINE", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INSTANCE", 
			"INSTANTIABLE", "INSTEAD", "INTEGER", "INTEGRITY", "INTERSECT", "INTERSECTION", 
			"INTERVAL", "INTO", "INVOKER", "IS", "ISNULL", "ISOLATION", "JOIN", "JSON", 
			"JSON_ARRAY", "JSON_ARRAYAGG", "JSON_EXISTS", "JSON_OBJECT", "JSON_OBJECTAGG", 
			"JSON_QUERY", "JSON_SCALAR", "JSON_SERIALIZE", "JSON_TABLE", "JSON_TABLE_PRIMITIVE", 
			"JSON_VALUE", "K_KW", "KEEP", "KEY", "KEYS", "KEY_MEMBER", "KEY_TYPE", 
			"LABEL", "LAG", "LANGUAGE", "LARGE", "LAST", "LAST_VALUE", "LATERAL", 
			"LEAD", "LEADING", "LEAKPROOF", "LEAST", "LEFT", "LENGTH", "LEVEL", "LIBRARY", 
			"LIKE", "LIKE_REGEX", "LIMIT", "LINK", "LISTAGG", "LISTEN", "LN", "LOAD", 
			"LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCATOR", "LOCK", 
			"LOCKED", "LOG", "LOG10", "LOGGED", "LOWER", "LPAD", "LTRIM", "M_KW", 
			"MAP", "MAPPING", "MATCH", "MATCHED", "MATCHES", "MATCH_NUMBER", "MATCH_RECOGNIZE", 
			"MATERIALIZED", "MAX", "MAXVALUE", "MEASURES", "MEMBER", "MERGE", "MESSAGE_LENGTH", 
			"MESSAGE_OCTET_LENGTH", "MESSAGE_TEXT", "METHOD", "MIN", "MINUTE", "MINVALUE", 
			"MOD", "MODE", "MODIFIES", "MODULE", "MONTH", "MORE_KW", "MOVE", "MULTISET", 
			"MUMPS", "NAME", "NAMES", "NAMESPACE", "NATIONAL", "NATURAL", "NCHAR", 
			"NCLOB", "NESTED", "NESTING", "NEW", "NEXT", "NFC", "NFD", "NFKC", "NFKD", 
			"NIL", "NO", "NONE", "NORMALIZE", "NORMALIZED", "NOT", "NOTHING", "NOTIFY", 
			"NOTNULL", "NOWAIT", "NTH_VALUE", "NTILE", "NULL", "NULLABLE", "NULLIF", 
			"NULLS", "NULL_ORDERING", "NUMBER", "NUMERIC", "OBJECT", "OCCURRENCE", 
			"OCCURRENCES_REGEX", "OCTETS", "OCTET_LENGTH", "OF", "OFF", "OFFSET", 
			"OIDS", "OLD", "OMIT", "ON", "ONE", "ONLY", "OPEN", "OPERATOR_KW", "OPTION", 
			"OPTIONS", "OR", "ORDER", "ORDERING", "ORDINALITY", "OTHERS", "OUT", 
			"OUTER", "OUTPUT", "OVER", "OVERFLOW", "OVERLAPS", "OVERLAY", "OVERRIDING", 
			"OWNED", "OWNER", "P_KW", "PAD", "PARALLEL", "PARAMETER", "PARAMETER_MODE", 
			"PARAMETER_NAME", "PARAMETER_ORDINAL_POSITION", "PARAMETER_SPECIFIC_CATALOG", 
			"PARAMETER_SPECIFIC_NAME", "PARAMETER_SPECIFIC_SCHEMA", "PARSER", "PARTIAL", 
			"PARTITION", "PASCAL", "PASS", "PASSING", "PASSTHROUGH", "PASSWORD", 
			"PAST", "PATH", "PATTERN", "PER", "PERCENT", "PERCENTILE_CONT", "PERCENTILE_DISC", 
			"PERCENT_RANK", "PERIOD", "PERMISSION", "PERMUTE", "PIPE", "PLACING", 
			"PLAN", "PLANS", "PLI", "POLICY", "PORTION", "POSITION", "POSITION_REGEX", 
			"POWER", "PRECEDES", "PRECEDING", "PRECISION", "PREPARE", "PREPARED", 
			"PRESERVE", "PREV", "PRIMARY", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURAL", 
			"PROCEDURE", "PROCEDURES", "PROGRAM", "PRUNE", "PTF", "PUBLIC", "PUBLICATION", 
			"QUOTE_KW", "QUOTES", "RANGE", "RANK", "READ", "READS", "REAL", "REASSIGN", 
			"RECHECK", "RECOVERY", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", 
			"REFRESH", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", 
			"REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "REINDEX", 
			"RELATIVE", "RELEASE", "RENAME", "REPEATABLE", "REPLACE", "REPLICA", 
			"REQUIRING", "RESET", "RESPECT", "RESTART", "RESTORE", "RESTRICT", "RESULT", 
			"RETURN", "RETURNED_CARDINALITY", "RETURNED_LENGTH", "RETURNED_OCTET_LENGTH", 
			"RETURNED_SQLSTATE", "RETURNING", "RETURNS", "REVOKE", "RIGHT", "ROLE", 
			"ROLLBACK", "ROLLUP", "ROUTINE", "ROUTINES", "ROUTINE_CATALOG", "ROUTINE_NAME", 
			"ROUTINE_SCHEMA", "ROW", "ROWS", "ROW_COUNT", "ROW_NUMBER", "RPAD", "RTRIM", 
			"RULE", "RUNNING", "SAVEPOINT", "SCALAR", "SCALE", "SCHEMA", "SCHEMAS", 
			"SCHEMA_NAME", "SCOPE", "SCOPE_CATALOG", "SCOPE_NAME", "SCOPE_SCHEMA", 
			"SCROLL", "SEARCH", "SECOND", "SECTION", "SECURITY", "SEEK", "SELECT", 
			"SELECTIVE", "SELF", "SEMANTICS", "SENSITIVE", "SEQUENCE", "SEQUENCES", 
			"SERIALIZABLE", "SERVER", "SERVER_NAME", "SESSION", "SESSION_USER", "SET", 
			"SETOF", "SETS", "SHARE", "SHOW", "SIMILAR", "SIMPLE", "SIN", "SINH", 
			"SIZE", "SKIP_KW", "SMALLINT", "SNAPSHOT", "SOME", "SORT_DIRECTION", 
			"SOURCE", "SPACE", "SPECIFIC", "SPECIFICTYPE", "SPECIFIC_NAME", "SQL", 
			"SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", 
			"STABLE", "STANDALONE", "START", "STATE", "STATEMENT", "STATIC", "STATISTICS", 
			"STDDEV_POP", "STDDEV_SAMP", "STDIN", "STDOUT", "STORAGE", "STORED", 
			"STRICT", "STRING", "STRIP", "STRUCTURE", "STYLE", "SUBCLASS_ORIGIN", 
			"SUBMULTISET", "SUBSCRIPTION", "SUBSET", "SUBSTRING", "SUBSTRING_REGEX", 
			"SUCCEEDS", "SUM", "SUPPORT", "SYMMETRIC", "SYSID", "SYSTEM", "SYSTEM_TIME", 
			"SYSTEM_USER", "T_KW", "TABLE", "TABLES", "TABLESAMPLE", "TABLESPACE", 
			"TABLE_NAME", "TAN", "TANH", "TEMP", "TEMPLATE", "TEMPORARY", "TEXT", 
			"THEN", "THROUGH", "TIES", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", 
			"TO", "TOKEN", "TOP_LEVEL_COUNT", "TRAILING", "TRANSACTION", "TRANSACTIONS_COMMITTED", 
			"TRANSACTIONS_ROLLED_BACK", "TRANSACTION_ACTIVE", "TRANSFORM", "TRANSFORMS", 
			"TRANSLATE", "TRANSLATE_REGEX", "TRANSLATION", "TREAT", "TRIGGER", "TRIGGER_CATALOG", 
			"TRIGGER_NAME", "TRIGGER_SCHEMA", "TRIM", "TRIM_ARRAY", "TRUE", "TRUNCATE", 
			"TRUSTED", "TYPE", "TYPES", "UESCAPE", "UNBOUNDED", "UNCOMMITTED", "UNCONDITIONAL", 
			"UNDER", "UNENCRYPTED", "UNION", "UNIQUE", "UNKNOWN", "UNLINK", "UNLISTEN", 
			"UNLOGGED", "UNMATCHED", "UNNAMED", "UNNEST", "UNTIL", "UNTYPED", "UPDATE", 
			"UPPER", "URI", "USAGE", "USER", "USER_DEFINED_TYPE_CATALOG", "USER_DEFINED_TYPE_CODE", 
			"USER_DEFINED_TYPE_NAME", "USER_DEFINED_TYPE_SCHEMA", "USING", "UTF16", 
			"UTF32", "UTF8", "VACUUM", "VALID", "VALIDATE", "VALIDATOR", "VALUE", 
			"VALUES", "VALUE_OF", "VARBINARY", "VARCHAR", "VARIADIC", "VARYING", 
			"VAR_POP", "VAR_SAMP", "VERBOSE", "VERSION", "VERSIONING", "VIEW", "VIEWS", 
			"VOLATILE", "WHEN", "WHENEVER", "WHERE", "WHITESPACE", "WIDTH_BUCKET", 
			"WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRAPPER", "WRITE", "XML", 
			"XMLAGG", "XMLATTRIBUTES", "XMLBINARY", "XMLCAST", "XMLCOMMENT", "XMLCONCAT", 
			"XMLDECLARATION", "XMLDOCUMENT", "XMLELEMENT", "XMLEXISTS", "XMLFOREST", 
			"XMLITERATE", "XMLNAMESPACES", "XMLPARSE", "XMLPI", "XMLQUERY", "XMLROOT", 
			"XMLSCHEMA", "XMLSERIALIZE", "XMLTABLE", "XMLTEXT", "XMLVALIDATE", "YEAR", 
			"YES", "ZONE", "WORD", "UNKNOWN_WORD", "WS", "SL_TYPE_KW", "SL_DEFAULT_KW", 
			"SL_PARAM_KW", "SL_RETURN_KW", "SL_UNKNOWN_TOKEN", "SL_WORD", "SL_SPACES", 
			"SL_LINE_COMMENT_END", "SL_WS", "ML_TYPE_KW", "ML_DEFAULT_KW", "ML_PARAM_KW", 
			"ML_RETURN_KW", "ML_UNKNOWN_TOKEN", "ML_WORD", "ML_WS", "MULTI_LINE_COMMENT_END", 
			"ML_STAR", "UNKNOWN_WORD_END", "IN_UNKNOWN_WORD"
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
	public String getGrammarFileName() { return "PsqlParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PsqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgContext extends ParserRuleContext {
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PsqlParser.EOF, 0); }
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			code();
			setState(61);
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
	public static class CodeContext extends ParserRuleContext {
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public List<TerminalNode> SEMICOLON() { return getTokens(PsqlParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(PsqlParser.SEMICOLON, i);
		}
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_code);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			stmt();
			setState(68);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(64);
					match(SEMICOLON);
					setState(65);
					stmt();
					}
					} 
				}
				setState(70);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(71);
				match(SEMICOLON);
				}
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
	public static class CommentContext extends ParserRuleContext {
		public Singleline_commentContext singleline_comment() {
			return getRuleContext(Singleline_commentContext.class,0);
		}
		public Multiline_commentContext multiline_comment() {
			return getRuleContext(Multiline_commentContext.class,0);
		}
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_comment);
		try {
			setState(76);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LINE_COMMENT_START:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				singleline_comment();
				}
				break;
			case MULTI_LINE_COMMENT_START:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				multiline_comment();
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
	public static class Singleline_commentContext extends ParserRuleContext {
		public TerminalNode LINE_COMMENT_START() { return getToken(PsqlParser.LINE_COMMENT_START, 0); }
		public Singleline_value_commentContext singleline_value_comment() {
			return getRuleContext(Singleline_value_commentContext.class,0);
		}
		public TerminalNode SL_LINE_COMMENT_END() { return getToken(PsqlParser.SL_LINE_COMMENT_END, 0); }
		public TerminalNode EOF() { return getToken(PsqlParser.EOF, 0); }
		public Singleline_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_commentContext singleline_comment() throws RecognitionException {
		Singleline_commentContext _localctx = new Singleline_commentContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_singleline_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(LINE_COMMENT_START);
			setState(79);
			singleline_value_comment();
			setState(80);
			_la = _input.LA(1);
			if ( !(_la==EOF || _la==SL_LINE_COMMENT_END) ) {
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
	public static class Singleline_value_commentContext extends ParserRuleContext {
		public Singleline_value_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_value_comment; }
	 
		public Singleline_value_commentContext() { }
		public void copyFrom(Singleline_value_commentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleReturnCommentContext extends Singleline_value_commentContext {
		public Singleline_return_commentContext singleline_return_comment() {
			return getRuleContext(Singleline_return_commentContext.class,0);
		}
		public SingleReturnCommentContext(Singleline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleReturnComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleReturnComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleReturnComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleParamCommentContext extends Singleline_value_commentContext {
		public Singleline_param_commentContext singleline_param_comment() {
			return getRuleContext(Singleline_param_commentContext.class,0);
		}
		public SingleParamCommentContext(Singleline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleParamComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleParamComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleParamComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleUnknownTypeCommentContext extends Singleline_value_commentContext {
		public Singleline_unknown_type_commentContext singleline_unknown_type_comment() {
			return getRuleContext(Singleline_unknown_type_commentContext.class,0);
		}
		public SingleUnknownTypeCommentContext(Singleline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleUnknownTypeComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleUnknownTypeComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleUnknownTypeComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleTypeCommentContext extends Singleline_value_commentContext {
		public Singleline_type_commentContext singleline_type_comment() {
			return getRuleContext(Singleline_type_commentContext.class,0);
		}
		public SingleTypeCommentContext(Singleline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleTypeComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleTypeComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleTypeComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleNormalCommentContext extends Singleline_value_commentContext {
		public Singleline_normal_comment_valueContext singleline_normal_comment_value() {
			return getRuleContext(Singleline_normal_comment_valueContext.class,0);
		}
		public SingleNormalCommentContext(Singleline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleNormalComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleNormalComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleNormalComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleDefaultCommentContext extends Singleline_value_commentContext {
		public Singleline_default_commentContext singleline_default_comment() {
			return getRuleContext(Singleline_default_commentContext.class,0);
		}
		public SingleDefaultCommentContext(Singleline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleDefaultComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleDefaultComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleDefaultComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_value_commentContext singleline_value_comment() throws RecognitionException {
		Singleline_value_commentContext _localctx = new Singleline_value_commentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_singleline_value_comment);
		try {
			setState(88);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SL_PARAM_KW:
				_localctx = new SingleParamCommentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				singleline_param_comment();
				}
				break;
			case SL_TYPE_KW:
				_localctx = new SingleTypeCommentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(83);
				singleline_type_comment();
				}
				break;
			case SL_RETURN_KW:
				_localctx = new SingleReturnCommentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(84);
				singleline_return_comment();
				}
				break;
			case SL_DEFAULT_KW:
				_localctx = new SingleDefaultCommentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(85);
				singleline_default_comment();
				}
				break;
			case SL_UNKNOWN_TOKEN:
				_localctx = new SingleUnknownTypeCommentContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(86);
				singleline_unknown_type_comment();
				}
				break;
			case EOF:
			case SL_WORD:
			case SL_LINE_COMMENT_END:
				_localctx = new SingleNormalCommentContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(87);
				singleline_normal_comment_value();
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
	public static class Singleline_param_commentContext extends ParserRuleContext {
		public TerminalNode SL_PARAM_KW() { return getToken(PsqlParser.SL_PARAM_KW, 0); }
		public List<TerminalNode> SL_WORD() { return getTokens(PsqlParser.SL_WORD); }
		public TerminalNode SL_WORD(int i) {
			return getToken(PsqlParser.SL_WORD, i);
		}
		public List<TerminalNode> SL_LINE_COMMENT_END() { return getTokens(PsqlParser.SL_LINE_COMMENT_END); }
		public TerminalNode SL_LINE_COMMENT_END(int i) {
			return getToken(PsqlParser.SL_LINE_COMMENT_END, i);
		}
		public List<TerminalNode> LINE_COMMENT_START() { return getTokens(PsqlParser.LINE_COMMENT_START); }
		public TerminalNode LINE_COMMENT_START(int i) {
			return getToken(PsqlParser.LINE_COMMENT_START, i);
		}
		public List<TerminalNode> SL_SPACES() { return getTokens(PsqlParser.SL_SPACES); }
		public TerminalNode SL_SPACES(int i) {
			return getToken(PsqlParser.SL_SPACES, i);
		}
		public List<Singleline_normal_comment_valueContext> singleline_normal_comment_value() {
			return getRuleContexts(Singleline_normal_comment_valueContext.class);
		}
		public Singleline_normal_comment_valueContext singleline_normal_comment_value(int i) {
			return getRuleContext(Singleline_normal_comment_valueContext.class,i);
		}
		public Singleline_param_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_param_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_param_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_param_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_param_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_param_commentContext singleline_param_comment() throws RecognitionException {
		Singleline_param_commentContext _localctx = new Singleline_param_commentContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_singleline_param_comment);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(SL_PARAM_KW);
			setState(92); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(91);
				match(SL_WORD);
				}
				}
				setState(94); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SL_WORD );
			setState(102);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(96);
					match(SL_LINE_COMMENT_END);
					setState(97);
					match(LINE_COMMENT_START);
					setState(98);
					match(SL_SPACES);
					setState(99);
					singleline_normal_comment_value();
					}
					} 
				}
				setState(104);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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
	public static class Singleline_type_commentContext extends ParserRuleContext {
		public TerminalNode SL_TYPE_KW() { return getToken(PsqlParser.SL_TYPE_KW, 0); }
		public List<TerminalNode> SL_WORD() { return getTokens(PsqlParser.SL_WORD); }
		public TerminalNode SL_WORD(int i) {
			return getToken(PsqlParser.SL_WORD, i);
		}
		public Singleline_type_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_type_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_type_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_type_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_type_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_type_commentContext singleline_type_comment() throws RecognitionException {
		Singleline_type_commentContext _localctx = new Singleline_type_commentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_singleline_type_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(SL_TYPE_KW);
			setState(107); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(106);
				match(SL_WORD);
				}
				}
				setState(109); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SL_WORD );
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
	public static class Singleline_default_commentContext extends ParserRuleContext {
		public TerminalNode SL_DEFAULT_KW() { return getToken(PsqlParser.SL_DEFAULT_KW, 0); }
		public List<TerminalNode> SL_WORD() { return getTokens(PsqlParser.SL_WORD); }
		public TerminalNode SL_WORD(int i) {
			return getToken(PsqlParser.SL_WORD, i);
		}
		public Singleline_default_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_default_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_default_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_default_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_default_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_default_commentContext singleline_default_comment() throws RecognitionException {
		Singleline_default_commentContext _localctx = new Singleline_default_commentContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_singleline_default_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(SL_DEFAULT_KW);
			setState(112);
			match(SL_WORD);
			setState(113);
			match(SL_WORD);
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
	public static class Singleline_return_commentContext extends ParserRuleContext {
		public TerminalNode SL_RETURN_KW() { return getToken(PsqlParser.SL_RETURN_KW, 0); }
		public List<TerminalNode> SL_WORD() { return getTokens(PsqlParser.SL_WORD); }
		public TerminalNode SL_WORD(int i) {
			return getToken(PsqlParser.SL_WORD, i);
		}
		public List<TerminalNode> SL_LINE_COMMENT_END() { return getTokens(PsqlParser.SL_LINE_COMMENT_END); }
		public TerminalNode SL_LINE_COMMENT_END(int i) {
			return getToken(PsqlParser.SL_LINE_COMMENT_END, i);
		}
		public List<TerminalNode> LINE_COMMENT_START() { return getTokens(PsqlParser.LINE_COMMENT_START); }
		public TerminalNode LINE_COMMENT_START(int i) {
			return getToken(PsqlParser.LINE_COMMENT_START, i);
		}
		public List<TerminalNode> SL_SPACES() { return getTokens(PsqlParser.SL_SPACES); }
		public TerminalNode SL_SPACES(int i) {
			return getToken(PsqlParser.SL_SPACES, i);
		}
		public List<Singleline_normal_comment_valueContext> singleline_normal_comment_value() {
			return getRuleContexts(Singleline_normal_comment_valueContext.class);
		}
		public Singleline_normal_comment_valueContext singleline_normal_comment_value(int i) {
			return getRuleContext(Singleline_normal_comment_valueContext.class,i);
		}
		public Singleline_return_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_return_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_return_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_return_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_return_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_return_commentContext singleline_return_comment() throws RecognitionException {
		Singleline_return_commentContext _localctx = new Singleline_return_commentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_singleline_return_comment);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(SL_RETURN_KW);
			setState(117); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(116);
				match(SL_WORD);
				}
				}
				setState(119); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SL_WORD );
			setState(127);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(121);
					match(SL_LINE_COMMENT_END);
					setState(122);
					match(LINE_COMMENT_START);
					setState(123);
					match(SL_SPACES);
					setState(124);
					singleline_normal_comment_value();
					}
					} 
				}
				setState(129);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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
	public static class Singleline_unknown_type_commentContext extends ParserRuleContext {
		public TerminalNode SL_UNKNOWN_TOKEN() { return getToken(PsqlParser.SL_UNKNOWN_TOKEN, 0); }
		public List<TerminalNode> SL_WORD() { return getTokens(PsqlParser.SL_WORD); }
		public TerminalNode SL_WORD(int i) {
			return getToken(PsqlParser.SL_WORD, i);
		}
		public Singleline_unknown_type_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_unknown_type_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_unknown_type_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_unknown_type_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_unknown_type_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_unknown_type_commentContext singleline_unknown_type_comment() throws RecognitionException {
		Singleline_unknown_type_commentContext _localctx = new Singleline_unknown_type_commentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_singleline_unknown_type_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(SL_UNKNOWN_TOKEN);
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SL_WORD) {
				{
				{
				setState(131);
				match(SL_WORD);
				}
				}
				setState(136);
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
	public static class Singleline_normal_comment_valueContext extends ParserRuleContext {
		public List<TerminalNode> SL_WORD() { return getTokens(PsqlParser.SL_WORD); }
		public TerminalNode SL_WORD(int i) {
			return getToken(PsqlParser.SL_WORD, i);
		}
		public Singleline_normal_comment_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleline_normal_comment_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterSingleline_normal_comment_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitSingleline_normal_comment_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitSingleline_normal_comment_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Singleline_normal_comment_valueContext singleline_normal_comment_value() throws RecognitionException {
		Singleline_normal_comment_valueContext _localctx = new Singleline_normal_comment_valueContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_singleline_normal_comment_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SL_WORD) {
				{
				{
				setState(137);
				match(SL_WORD);
				}
				}
				setState(142);
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
	public static class Multiline_commentContext extends ParserRuleContext {
		public TerminalNode MULTI_LINE_COMMENT_START() { return getToken(PsqlParser.MULTI_LINE_COMMENT_START, 0); }
		public TerminalNode MULTI_LINE_COMMENT_END() { return getToken(PsqlParser.MULTI_LINE_COMMENT_END, 0); }
		public List<Multiline_value_commentContext> multiline_value_comment() {
			return getRuleContexts(Multiline_value_commentContext.class);
		}
		public Multiline_value_commentContext multiline_value_comment(int i) {
			return getRuleContext(Multiline_value_commentContext.class,i);
		}
		public Multiline_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_commentContext multiline_comment() throws RecognitionException {
		Multiline_commentContext _localctx = new Multiline_commentContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_multiline_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(MULTI_LINE_COMMENT_START);
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 934)) & ~0x3f) == 0 && ((1L << (_la - 934)) & 319L) != 0)) {
				{
				{
				setState(144);
				multiline_value_comment();
				}
				}
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(150);
			match(MULTI_LINE_COMMENT_END);
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
	public static class Multiline_value_commentContext extends ParserRuleContext {
		public Multiline_value_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_value_comment; }
	 
		public Multiline_value_commentContext() { }
		public void copyFrom(Multiline_value_commentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultilineUnknownTypeCommentContext extends Multiline_value_commentContext {
		public Multiline_unknown_type_commentContext multiline_unknown_type_comment() {
			return getRuleContext(Multiline_unknown_type_commentContext.class,0);
		}
		public MultilineUnknownTypeCommentContext(Multiline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultilineUnknownTypeComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultilineUnknownTypeComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultilineUnknownTypeComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultilineNormalCommentContext extends Multiline_value_commentContext {
		public Multiline_normal_comment_valueContext multiline_normal_comment_value() {
			return getRuleContext(Multiline_normal_comment_valueContext.class,0);
		}
		public MultilineNormalCommentContext(Multiline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultilineNormalComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultilineNormalComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultilineNormalComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultilineTypeCommentContext extends Multiline_value_commentContext {
		public Multiline_type_commentContext multiline_type_comment() {
			return getRuleContext(Multiline_type_commentContext.class,0);
		}
		public MultilineTypeCommentContext(Multiline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultilineTypeComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultilineTypeComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultilineTypeComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultilineDefaultCommentContext extends Multiline_value_commentContext {
		public Multiline_default_commentContext multiline_default_comment() {
			return getRuleContext(Multiline_default_commentContext.class,0);
		}
		public MultilineDefaultCommentContext(Multiline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultilineDefaultComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultilineDefaultComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultilineDefaultComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultilineReturnCommentContext extends Multiline_value_commentContext {
		public Multiline_return_commentContext multiline_return_comment() {
			return getRuleContext(Multiline_return_commentContext.class,0);
		}
		public MultilineReturnCommentContext(Multiline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultilineReturnComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultilineReturnComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultilineReturnComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultilineParamCommentContext extends Multiline_value_commentContext {
		public Multiline_param_commentContext multiline_param_comment() {
			return getRuleContext(Multiline_param_commentContext.class,0);
		}
		public MultilineParamCommentContext(Multiline_value_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultilineParamComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultilineParamComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultilineParamComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_value_commentContext multiline_value_comment() throws RecognitionException {
		Multiline_value_commentContext _localctx = new Multiline_value_commentContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_multiline_value_comment);
		try {
			setState(158);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ML_PARAM_KW:
				_localctx = new MultilineParamCommentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(152);
				multiline_param_comment();
				}
				break;
			case ML_TYPE_KW:
				_localctx = new MultilineTypeCommentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(153);
				multiline_type_comment();
				}
				break;
			case ML_DEFAULT_KW:
				_localctx = new MultilineDefaultCommentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(154);
				multiline_default_comment();
				}
				break;
			case ML_RETURN_KW:
				_localctx = new MultilineReturnCommentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(155);
				multiline_return_comment();
				}
				break;
			case ML_UNKNOWN_TOKEN:
				_localctx = new MultilineUnknownTypeCommentContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(156);
				multiline_unknown_type_comment();
				}
				break;
			case ML_WORD:
			case ML_STAR:
				_localctx = new MultilineNormalCommentContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(157);
				multiline_normal_comment_value();
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
	public static class Multiline_param_commentContext extends ParserRuleContext {
		public TerminalNode ML_PARAM_KW() { return getToken(PsqlParser.ML_PARAM_KW, 0); }
		public List<Multiline_word_or_starContext> multiline_word_or_star() {
			return getRuleContexts(Multiline_word_or_starContext.class);
		}
		public Multiline_word_or_starContext multiline_word_or_star(int i) {
			return getRuleContext(Multiline_word_or_starContext.class,i);
		}
		public Multiline_param_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_param_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_param_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_param_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_param_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_param_commentContext multiline_param_comment() throws RecognitionException {
		Multiline_param_commentContext _localctx = new Multiline_param_commentContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_multiline_param_comment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(ML_PARAM_KW);
			setState(162); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(161);
					multiline_word_or_star();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(164); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
	public static class Multiline_type_commentContext extends ParserRuleContext {
		public TerminalNode ML_TYPE_KW() { return getToken(PsqlParser.ML_TYPE_KW, 0); }
		public List<Multiline_word_or_starContext> multiline_word_or_star() {
			return getRuleContexts(Multiline_word_or_starContext.class);
		}
		public Multiline_word_or_starContext multiline_word_or_star(int i) {
			return getRuleContext(Multiline_word_or_starContext.class,i);
		}
		public Multiline_type_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_type_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_type_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_type_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_type_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_type_commentContext multiline_type_comment() throws RecognitionException {
		Multiline_type_commentContext _localctx = new Multiline_type_commentContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_multiline_type_comment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(ML_TYPE_KW);
			setState(168); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(167);
					multiline_word_or_star();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(170); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
	public static class Multiline_default_commentContext extends ParserRuleContext {
		public TerminalNode ML_DEFAULT_KW() { return getToken(PsqlParser.ML_DEFAULT_KW, 0); }
		public List<Multiline_word_or_starContext> multiline_word_or_star() {
			return getRuleContexts(Multiline_word_or_starContext.class);
		}
		public Multiline_word_or_starContext multiline_word_or_star(int i) {
			return getRuleContext(Multiline_word_or_starContext.class,i);
		}
		public Multiline_default_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_default_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_default_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_default_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_default_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_default_commentContext multiline_default_comment() throws RecognitionException {
		Multiline_default_commentContext _localctx = new Multiline_default_commentContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_multiline_default_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(ML_DEFAULT_KW);
			setState(173);
			multiline_word_or_star();
			setState(174);
			multiline_word_or_star();
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
	public static class Multiline_return_commentContext extends ParserRuleContext {
		public TerminalNode ML_RETURN_KW() { return getToken(PsqlParser.ML_RETURN_KW, 0); }
		public List<Multiline_word_or_starContext> multiline_word_or_star() {
			return getRuleContexts(Multiline_word_or_starContext.class);
		}
		public Multiline_word_or_starContext multiline_word_or_star(int i) {
			return getRuleContext(Multiline_word_or_starContext.class,i);
		}
		public Multiline_return_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_return_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_return_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_return_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_return_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_return_commentContext multiline_return_comment() throws RecognitionException {
		Multiline_return_commentContext _localctx = new Multiline_return_commentContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_multiline_return_comment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(ML_RETURN_KW);
			setState(178); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(177);
					multiline_word_or_star();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(180); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
	public static class Multiline_unknown_type_commentContext extends ParserRuleContext {
		public TerminalNode ML_UNKNOWN_TOKEN() { return getToken(PsqlParser.ML_UNKNOWN_TOKEN, 0); }
		public List<Multiline_word_or_starContext> multiline_word_or_star() {
			return getRuleContexts(Multiline_word_or_starContext.class);
		}
		public Multiline_word_or_starContext multiline_word_or_star(int i) {
			return getRuleContext(Multiline_word_or_starContext.class,i);
		}
		public Multiline_unknown_type_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_unknown_type_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_unknown_type_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_unknown_type_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_unknown_type_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_unknown_type_commentContext multiline_unknown_type_comment() throws RecognitionException {
		Multiline_unknown_type_commentContext _localctx = new Multiline_unknown_type_commentContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_multiline_unknown_type_comment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(ML_UNKNOWN_TOKEN);
			setState(186);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(183);
					multiline_word_or_star();
					}
					} 
				}
				setState(188);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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
	public static class Multiline_normal_comment_valueContext extends ParserRuleContext {
		public List<Multiline_word_or_starContext> multiline_word_or_star() {
			return getRuleContexts(Multiline_word_or_starContext.class);
		}
		public Multiline_word_or_starContext multiline_word_or_star(int i) {
			return getRuleContext(Multiline_word_or_starContext.class,i);
		}
		public Multiline_normal_comment_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_normal_comment_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_normal_comment_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_normal_comment_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_normal_comment_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_normal_comment_valueContext multiline_normal_comment_value() throws RecognitionException {
		Multiline_normal_comment_valueContext _localctx = new Multiline_normal_comment_valueContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_multiline_normal_comment_value);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(190); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(189);
					multiline_word_or_star();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(192); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
	public static class Multiline_word_or_starContext extends ParserRuleContext {
		public TerminalNode ML_WORD() { return getToken(PsqlParser.ML_WORD, 0); }
		public TerminalNode ML_STAR() { return getToken(PsqlParser.ML_STAR, 0); }
		public Multiline_word_or_starContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiline_word_or_star; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMultiline_word_or_star(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMultiline_word_or_star(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMultiline_word_or_star(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multiline_word_or_starContext multiline_word_or_star() throws RecognitionException {
		Multiline_word_or_starContext _localctx = new Multiline_word_or_starContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_multiline_word_or_star);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			_la = _input.LA(1);
			if ( !(_la==ML_WORD || _la==ML_STAR) ) {
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
	public static class StmtContext extends ParserRuleContext {
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	 
		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StmtItemsContext extends StmtContext {
		public List<Stmt_itemsContext> stmt_items() {
			return getRuleContexts(Stmt_itemsContext.class);
		}
		public Stmt_itemsContext stmt_items(int i) {
			return getRuleContext(Stmt_itemsContext.class,i);
		}
		public StmtItemsContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterStmtItems(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitStmtItems(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitStmtItems(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenStmtContext extends StmtContext {
		public TerminalNode L_PAREN() { return getToken(PsqlParser.L_PAREN, 0); }
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public TerminalNode R_PAREN() { return getToken(PsqlParser.R_PAREN, 0); }
		public ParenStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterParenStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitParenStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitParenStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenStmtSqureBrContext extends StmtContext {
		public TerminalNode L_SQ_BRACKET() { return getToken(PsqlParser.L_SQ_BRACKET, 0); }
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public TerminalNode R_SQ_BRACKET() { return getToken(PsqlParser.R_SQ_BRACKET, 0); }
		public ParenStmtSqureBrContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterParenStmtSqureBr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitParenStmtSqureBr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitParenStmtSqureBr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_stmt);
		int _la;
		try {
			setState(209);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				_localctx = new ParenStmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(196);
				match(L_PAREN);
				setState(197);
				stmt();
				setState(198);
				match(R_PAREN);
				}
				break;
			case 2:
				_localctx = new ParenStmtSqureBrContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(200);
				match(L_SQ_BRACKET);
				setState(201);
				stmt();
				setState(202);
				match(R_SQ_BRACKET);
				}
				break;
			case 3:
				_localctx = new StmtItemsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(205); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(204);
					stmt_items(0);
					}
					}
					setState(207); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & -35734127910226L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -1L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & -1L) != 0) || ((((_la - 448)) & ~0x3f) == 0 && ((1L << (_la - 448)) & -1L) != 0) || ((((_la - 512)) & ~0x3f) == 0 && ((1L << (_la - 512)) & -1L) != 0) || ((((_la - 576)) & ~0x3f) == 0 && ((1L << (_la - 576)) & -1L) != 0) || ((((_la - 640)) & ~0x3f) == 0 && ((1L << (_la - 640)) & -3L) != 0) || ((((_la - 704)) & ~0x3f) == 0 && ((1L << (_la - 704)) & -1L) != 0) || ((((_la - 768)) & ~0x3f) == 0 && ((1L << (_la - 768)) & -1L) != 0) || ((((_la - 832)) & ~0x3f) == 0 && ((1L << (_la - 832)) & -1L) != 0) || ((((_la - 896)) & ~0x3f) == 0 && ((1L << (_la - 896)) & 268435455L) != 0) );
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
	public static class Stmt_itemsContext extends ParserRuleContext {
		public Stmt_itemsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt_items; }
	 
		public Stmt_itemsContext() { }
		public void copyFrom(Stmt_itemsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IdntStmtContext extends Stmt_itemsContext {
		public IdntContext idnt() {
			return getRuleContext(IdntContext.class,0);
		}
		public IdntStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterIdntStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitIdntStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitIdntStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunCallStmtContext extends Stmt_itemsContext {
		public TerminalNode L_PAREN() { return getToken(PsqlParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(PsqlParser.R_PAREN, 0); }
		public Reserved_keywordContext reserved_keyword() {
			return getRuleContext(Reserved_keywordContext.class,0);
		}
		public IdntContext idnt() {
			return getRuleContext(IdntContext.class,0);
		}
		public Stmt_itemsContext stmt_items() {
			return getRuleContext(Stmt_itemsContext.class,0);
		}
		public FunCallStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterFunCallStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitFunCallStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitFunCallStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParamStmtContext extends Stmt_itemsContext {
		public TerminalNode PARAM() { return getToken(PsqlParser.PARAM, 0); }
		public ParamStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterParamStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitParamStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitParamStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CommentStmtContext extends Stmt_itemsContext {
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public CommentStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterCommentStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitCommentStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitCommentStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class KeywordStmtContext extends Stmt_itemsContext {
		public Reserved_keywordContext reserved_keyword() {
			return getRuleContext(Reserved_keywordContext.class,0);
		}
		public KeywordStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterKeywordStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitKeywordStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitKeywordStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OperatorStmtContext extends Stmt_itemsContext {
		public OperatorContext operator() {
			return getRuleContext(OperatorContext.class,0);
		}
		public OperatorStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterOperatorStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitOperatorStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitOperatorStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CommaSeparatedContext extends Stmt_itemsContext {
		public List<Stmt_itemsContext> stmt_items() {
			return getRuleContexts(Stmt_itemsContext.class);
		}
		public Stmt_itemsContext stmt_items(int i) {
			return getRuleContext(Stmt_itemsContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PsqlParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PsqlParser.COMMA, i);
		}
		public CommaSeparatedContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterCommaSeparated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitCommaSeparated(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitCommaSeparated(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypeStmtContext extends Stmt_itemsContext {
		public TipeContext tipe() {
			return getRuleContext(TipeContext.class,0);
		}
		public TypeStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterTypeStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitTypeStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitTypeStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralStmtContext extends Stmt_itemsContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterLiteralStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitLiteralStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitLiteralStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypeCastContext extends Stmt_itemsContext {
		public List<Stmt_itemsContext> stmt_items() {
			return getRuleContexts(Stmt_itemsContext.class);
		}
		public Stmt_itemsContext stmt_items(int i) {
			return getRuleContext(Stmt_itemsContext.class,i);
		}
		public TerminalNode DOUBLE_COLON() { return getToken(PsqlParser.DOUBLE_COLON, 0); }
		public TypeCastContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterTypeCast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitTypeCast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitTypeCast(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ProjStmtContext extends Stmt_itemsContext {
		public ProjContext proj() {
			return getRuleContext(ProjContext.class,0);
		}
		public ProjStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterProjStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitProjStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitProjStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NestedStmtContext extends Stmt_itemsContext {
		public TerminalNode L_PAREN() { return getToken(PsqlParser.L_PAREN, 0); }
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public TerminalNode R_PAREN() { return getToken(PsqlParser.R_PAREN, 0); }
		public NestedStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterNestedStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitNestedStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitNestedStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnknownStmtContext extends Stmt_itemsContext {
		public TerminalNode UNKNOWN_WORD() { return getToken(PsqlParser.UNKNOWN_WORD, 0); }
		public TerminalNode IN_UNKNOWN_WORD() { return getToken(PsqlParser.IN_UNKNOWN_WORD, 0); }
		public TerminalNode UNKNOWN_WORD_END() { return getToken(PsqlParser.UNKNOWN_WORD_END, 0); }
		public TerminalNode EOF() { return getToken(PsqlParser.EOF, 0); }
		public UnknownStmtContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterUnknownStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitUnknownStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitUnknownStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NestedStmtSqureBrContext extends Stmt_itemsContext {
		public TerminalNode L_SQ_BRACKET() { return getToken(PsqlParser.L_SQ_BRACKET, 0); }
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public TerminalNode R_SQ_BRACKET() { return getToken(PsqlParser.R_SQ_BRACKET, 0); }
		public NestedStmtSqureBrContext(Stmt_itemsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterNestedStmtSqureBr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitNestedStmtSqureBr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitNestedStmtSqureBr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Stmt_itemsContext stmt_items() throws RecognitionException {
		return stmt_items(0);
	}

	private Stmt_itemsContext stmt_items(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Stmt_itemsContext _localctx = new Stmt_itemsContext(_ctx, _parentState);
		Stmt_itemsContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_stmt_items, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				_localctx = new NestedStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(212);
				match(L_PAREN);
				setState(213);
				stmt();
				setState(214);
				match(R_PAREN);
				}
				break;
			case 2:
				{
				_localctx = new NestedStmtSqureBrContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(216);
				match(L_SQ_BRACKET);
				setState(217);
				stmt();
				setState(218);
				match(R_SQ_BRACKET);
				}
				break;
			case 3:
				{
				_localctx = new ProjStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(220);
				proj();
				}
				break;
			case 4:
				{
				_localctx = new LiteralStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(221);
				literal();
				}
				break;
			case 5:
				{
				_localctx = new FunCallStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(224);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STAR:
				case UNICODE:
				case ABS:
				case ABSENT:
				case ABSOLUTE:
				case ACOS:
				case ACTION:
				case ADD:
				case ALL:
				case ALLOCATE:
				case ALTER:
				case ANALYSE:
				case ANALYZE:
				case ANY:
				case ANY_VALUE:
				case ARE:
				case ARRAY:
				case ARRAY_AGG:
				case ARRAY_MAX_CARDINALITY:
				case AS:
				case ASC:
				case ASENSITIVE:
				case ASIN:
				case ASSERTION:
				case ASYMMETRIC:
				case AT:
				case ATAN:
				case ATOMIC:
				case AUTHORIZATION:
				case AVG:
				case BEGIN:
				case BEGIN_FRAME:
				case BEGIN_PARTITION:
				case BETWEEN:
				case BINARY:
				case BIT_LENGTH:
				case BLOB:
				case BOTH:
				case BTRIM:
				case BY:
				case CALL:
				case CALLED:
				case CARDINALITY:
				case CASCADE:
				case CASCADED:
				case CASE:
				case CAST:
				case CATALOG:
				case CEIL:
				case CEILING:
				case CHARACTER_LENGTH:
				case CHAR_LENGTH:
				case CHECK:
				case CLASSIFIER:
				case CLOB:
				case CLOSE:
				case COALESCE:
				case COLLATE:
				case COLLATION:
				case COLLECT:
				case COLUMN:
				case COMMIT:
				case CONDITION:
				case CONNECT:
				case CONNECTION:
				case CONSTRAINT:
				case CONSTRAINTS:
				case CONTAINS:
				case CONTINUE:
				case CONVERT:
				case COPY:
				case CORR:
				case CORRESPONDING:
				case COS:
				case COSH:
				case COUNT:
				case COVAR_POP:
				case COVAR_SAMP:
				case CREATE:
				case CROSS:
				case CUBE:
				case CUME_DIST:
				case CURRENT:
				case CURRENT_CATALOG:
				case CURRENT_DATE:
				case CURRENT_DEFAULT_TRANSFORM_GROUP:
				case CURRENT_PATH:
				case CURRENT_ROLE:
				case CURRENT_ROW:
				case CURRENT_SCHEMA:
				case CURRENT_TIME:
				case CURRENT_TIMESTAMP:
				case CURRENT_TRANSFORM_GROUP_FOR_TYPE:
				case CURRENT_USER:
				case CURSOR:
				case CYCLE:
				case DATALINK:
				case DAY:
				case DEALLOCATE:
				case DEC:
				case DECFLOAT:
				case DECLARE:
				case DEFAULT:
				case DEFERRABLE:
				case DEFERRED:
				case DEFINE:
				case DELETE:
				case DENSE_RANK:
				case DEREF:
				case DESC:
				case DESCRIBE:
				case DESCRIPTOR:
				case DETERMINISTIC:
				case DIAGNOSTICS:
				case DISCONNECT:
				case DISTINCT:
				case DLNEWCOPY:
				case DLPREVIOUSCOPY:
				case DLURLCOMPLETE:
				case DLURLCOMPLETEONLY:
				case DLURLCOMPLETEWRITE:
				case DLURLPATH:
				case DLURLPATHONLY:
				case DLURLPATHWRITE:
				case DLURLSCHEME:
				case DLURLSERVER:
				case DLVALUE:
				case DO:
				case DOMAIN:
				case DROP:
				case DYNAMIC:
				case EACH:
				case ELEMENT:
				case ELSE:
				case EMPTY:
				case END:
				case END_EXEC:
				case END_FRAME:
				case END_PARTITION:
				case EQUALS:
				case ESCAPE:
				case EVERY:
				case EXCEPT:
				case EXCEPTION:
				case EXEC:
				case EXECUTE:
				case EXISTS:
				case EXP:
				case EXTERNAL:
				case EXTRACT:
				case FALSE:
				case FETCH:
				case FILTER:
				case FIRST:
				case FIRST_VALUE:
				case FLOAT:
				case FLOOR:
				case FOR:
				case FOREIGN:
				case FOUND:
				case FRAME_ROW:
				case FREE:
				case FROM:
				case FULL:
				case FUNCTION:
				case FUSION:
				case GET:
				case GLOBAL:
				case GO:
				case GOTO:
				case GRANT:
				case GREATEST:
				case GROUP:
				case GROUPING:
				case GROUPS:
				case HAVING:
				case HOLD:
				case HOUR:
				case IDENTITY:
				case IMMEDIATE:
				case IMPORT:
				case IN:
				case INDICATOR:
				case INITIAL:
				case INITIALLY:
				case INNER:
				case INOUT:
				case INPUT:
				case INSENSITIVE:
				case INSERT:
				case INTERSECT:
				case INTERSECTION:
				case INTO:
				case IS:
				case ISOLATION:
				case JOIN:
				case JSON_ARRAY:
				case JSON_ARRAYAGG:
				case JSON_EXISTS:
				case JSON_OBJECT:
				case JSON_OBJECTAGG:
				case JSON_QUERY:
				case JSON_SCALAR:
				case JSON_SERIALIZE:
				case JSON_TABLE:
				case JSON_TABLE_PRIMITIVE:
				case JSON_VALUE:
				case KEY:
				case LAG:
				case LANGUAGE:
				case LARGE:
				case LAST:
				case LAST_VALUE:
				case LATERAL:
				case LEAD:
				case LEADING:
				case LEAST:
				case LEFT:
				case LEVEL:
				case LIKE_REGEX:
				case LISTAGG:
				case LN:
				case LOCAL:
				case LOCALTIME:
				case LOCALTIMESTAMP:
				case LOG:
				case LOG10:
				case LOWER:
				case LPAD:
				case LTRIM:
				case MATCH:
				case MATCHES:
				case MATCH_NUMBER:
				case MATCH_RECOGNIZE:
				case MAX:
				case MEMBER:
				case MERGE:
				case METHOD:
				case MIN:
				case MINUTE:
				case MOD:
				case MODIFIES:
				case MODULE:
				case MONTH:
				case MULTISET:
				case NAMES:
				case NATIONAL:
				case NATURAL:
				case NCHAR:
				case NCLOB:
				case NEW:
				case NEXT:
				case NO:
				case NONE:
				case NORMALIZE:
				case NTH_VALUE:
				case NTILE:
				case NULL:
				case NULLIF:
				case OCCURRENCES_REGEX:
				case OCTET_LENGTH:
				case OF:
				case OFFSET:
				case OLD:
				case OMIT:
				case ON:
				case ONE:
				case ONLY:
				case OPEN:
				case OPTION:
				case ORDER:
				case OUT:
				case OUTER:
				case OUTPUT:
				case OVER:
				case OVERLAPS:
				case OVERLAY:
				case PAD:
				case PARAMETER:
				case PARTIAL:
				case PARTITION:
				case PATTERN:
				case PER:
				case PERCENT:
				case PERCENTILE_CONT:
				case PERCENTILE_DISC:
				case PERCENT_RANK:
				case PERIOD:
				case PLACING:
				case PORTION:
				case POSITION:
				case POSITION_REGEX:
				case POWER:
				case PRECEDES:
				case PRECISION:
				case PREPARE:
				case PRESERVE:
				case PRIMARY:
				case PRIOR:
				case PRIVILEGES:
				case PROCEDURE:
				case PTF:
				case PUBLIC:
				case RANGE:
				case RANK:
				case READ:
				case READS:
				case RECURSIVE:
				case REF:
				case REFERENCES:
				case REFERENCING:
				case REGR_AVGX:
				case REGR_AVGY:
				case REGR_COUNT:
				case REGR_INTERCEPT:
				case REGR_R2:
				case REGR_SLOPE:
				case REGR_SXX:
				case REGR_SXY:
				case REGR_SYY:
				case RELATIVE:
				case RELEASE:
				case RESTRICT:
				case RESULT:
				case RETURN:
				case RETURNS:
				case REVOKE:
				case RIGHT:
				case ROLLBACK:
				case ROLLUP:
				case ROW:
				case ROWS:
				case ROW_NUMBER:
				case RPAD:
				case RTRIM:
				case RUNNING:
				case SAVEPOINT:
				case SCHEMA:
				case SCOPE:
				case SCROLL:
				case SEARCH:
				case SECOND:
				case SECTION:
				case SEEK:
				case SELECT:
				case SENSITIVE:
				case SESSION:
				case SESSION_USER:
				case SET:
				case SHOW:
				case SIMILAR:
				case SIN:
				case SINH:
				case SIZE:
				case SKIP_KW:
				case SOME:
				case SPACE:
				case SPECIFIC:
				case SPECIFICTYPE:
				case SQL:
				case SQLCODE:
				case SQLERROR:
				case SQLEXCEPTION:
				case SQLSTATE:
				case SQLWARNING:
				case SQRT:
				case START:
				case STATIC:
				case STDDEV_POP:
				case STDDEV_SAMP:
				case SUBMULTISET:
				case SUBSET:
				case SUBSTRING:
				case SUBSTRING_REGEX:
				case SUCCEEDS:
				case SUM:
				case SYMMETRIC:
				case SYSTEM:
				case SYSTEM_TIME:
				case SYSTEM_USER:
				case TABLE:
				case TABLESAMPLE:
				case TAN:
				case TANH:
				case TEMPORARY:
				case THEN:
				case TIMEZONE_HOUR:
				case TIMEZONE_MINUTE:
				case TO:
				case TRAILING:
				case TRANSACTION:
				case TRANSLATE:
				case TRANSLATE_REGEX:
				case TRANSLATION:
				case TREAT:
				case TRIGGER:
				case TRIM:
				case TRIM_ARRAY:
				case TRUE:
				case TRUNCATE:
				case UESCAPE:
				case UNION:
				case UNIQUE:
				case UNKNOWN:
				case UNNEST:
				case UPDATE:
				case UPPER:
				case USAGE:
				case USER:
				case USING:
				case VALUE:
				case VALUES:
				case VALUE_OF:
				case VARBINARY:
				case VARIADIC:
				case VARYING:
				case VAR_POP:
				case VAR_SAMP:
				case VERSIONING:
				case VIEW:
				case WHEN:
				case WHENEVER:
				case WHERE:
				case WIDTH_BUCKET:
				case WINDOW:
				case WITH:
				case WITHIN:
				case WITHOUT:
				case WORK:
				case WRITE:
				case XMLAGG:
				case XMLATTRIBUTES:
				case XMLBINARY:
				case XMLCAST:
				case XMLCOMMENT:
				case XMLCONCAT:
				case XMLDOCUMENT:
				case XMLELEMENT:
				case XMLEXISTS:
				case XMLFOREST:
				case XMLITERATE:
				case XMLNAMESPACES:
				case XMLPARSE:
				case XMLPI:
				case XMLQUERY:
				case XMLSERIALIZE:
				case XMLTABLE:
				case XMLTEXT:
				case XMLVALIDATE:
				case YEAR:
				case ZONE:
					{
					setState(222);
					reserved_keyword();
					}
					break;
				case QUOTE:
				case DOUBLE_QUOTED_STRING:
				case A_KW:
				case ABORT:
				case ACCESS:
				case ACCORDING:
				case ADA:
				case ADMIN:
				case AFTER:
				case AGGREGATE:
				case ALSO:
				case ALWAYS:
				case ASSIGNMENT:
				case ATTACH:
				case ATTRIBUTE:
				case ATTRIBUTES:
				case BACKWARD:
				case BASE64:
				case BEFORE:
				case BERNOULLI:
				case BLOCKED:
				case BOM:
				case BREADTH:
				case C_KW:
				case CACHE:
				case CATALOG_NAME:
				case CHAIN:
				case CHAINING:
				case CHARACTERISTICS:
				case CHARACTERS:
				case CHARACTER_SET_CATALOG:
				case CHARACTER_SET_NAME:
				case CHARACTER_SET_SCHEMA:
				case CHECKPOINT:
				case CLASS:
				case CLASS_ORIGIN:
				case CLUSTER:
				case COBOL:
				case COLLATION_CATALOG:
				case COLLATION_NAME:
				case COLLATION_SCHEMA:
				case COLUMNS:
				case COLUMN_NAME:
				case COMMAND_FUNCTION:
				case COMMAND_FUNCTION_CODE:
				case COMMENT:
				case COMMENTS:
				case COMMITTED:
				case COMPRESSION:
				case CONCURRENTLY:
				case CONDITIONAL:
				case CONDITION_NUMBER:
				case CONFIGURATION:
				case CONFLICT:
				case CONNECTION_NAME:
				case CONSTRAINT_CATALOG:
				case CONSTRAINT_NAME:
				case CONSTRAINT_SCHEMA:
				case CONSTRUCTOR:
				case CONTENT:
				case CONTROL:
				case CONVERSION:
				case COPARTITION:
				case COST:
				case CSV:
				case CURSOR_NAME:
				case DATA:
				case DATABASE:
				case DATETIME_INTERVAL_CODE:
				case DATETIME_INTERVAL_PRECISION:
				case DB:
				case DEFAULTS:
				case DEFINED:
				case DEFINER:
				case DEGREE:
				case DELIMITER:
				case DELIMITERS:
				case DEPENDS:
				case DEPTH:
				case DERIVED:
				case DETACH:
				case DICTIONARY:
				case DISABLE:
				case DISCARD:
				case DISPATCH:
				case DOCUMENT:
				case DYNAMIC_FUNCTION:
				case DYNAMIC_FUNCTION_CODE:
				case ENABLE:
				case ENCODING:
				case ENCRYPTED:
				case ENFORCED:
				case ENUM:
				case ERROR:
				case EVENT:
				case EXCLUDE:
				case EXCLUDING:
				case EXCLUSIVE:
				case EXPLAIN:
				case EXPRESSION:
				case EXTENSION:
				case FAMILY:
				case FILE:
				case FINAL:
				case FINALIZE:
				case FINISH:
				case FLAG:
				case FOLLOWING:
				case FORCE:
				case FORMAT:
				case FORTRAN:
				case FORWARD:
				case FREEZE:
				case FS:
				case FULFILL:
				case FUNCTIONS:
				case G_KW:
				case GENERAL:
				case GENERATED:
				case GRANTED:
				case HANDLER:
				case HEADER:
				case HEX:
				case HIERARCHY:
				case ID:
				case IF:
				case IGNORE:
				case IMMEDIATELY:
				case IMMUTABLE:
				case IMPLEMENTATION:
				case IMPLICIT:
				case INCLUDE:
				case INCLUDING:
				case INCREMENT:
				case INDENT:
				case INDEX:
				case INDEXES:
				case INHERIT:
				case INHERITS:
				case INLINE:
				case INSTANCE:
				case INSTANTIABLE:
				case INSTEAD:
				case INTEGRITY:
				case INVOKER:
				case ISNULL:
				case K_KW:
				case KEEP:
				case KEYS:
				case KEY_MEMBER:
				case KEY_TYPE:
				case LABEL:
				case LEAKPROOF:
				case LENGTH:
				case LIBRARY:
				case LIMIT:
				case LINK:
				case LISTEN:
				case LOAD:
				case LOCATION:
				case LOCATOR:
				case LOCK:
				case LOCKED:
				case LOGGED:
				case M_KW:
				case MAP:
				case MAPPING:
				case MATCHED:
				case MATERIALIZED:
				case MAXVALUE:
				case MEASURES:
				case MESSAGE_LENGTH:
				case MESSAGE_OCTET_LENGTH:
				case MESSAGE_TEXT:
				case MINVALUE:
				case MODE:
				case MORE_KW:
				case MOVE:
				case MUMPS:
				case NAME:
				case NAMESPACE:
				case NESTED:
				case NESTING:
				case NFC:
				case NFD:
				case NFKC:
				case NFKD:
				case NIL:
				case NORMALIZED:
				case NOTHING:
				case NOTIFY:
				case NOTNULL:
				case NOWAIT:
				case NULLABLE:
				case NULLS:
				case NULL_ORDERING:
				case NUMBER:
				case OBJECT:
				case OCCURRENCE:
				case OCTETS:
				case OFF:
				case OIDS:
				case OPERATOR_KW:
				case OPTIONS:
				case ORDERING:
				case ORDINALITY:
				case OTHERS:
				case OVERFLOW:
				case OVERRIDING:
				case OWNED:
				case OWNER:
				case P_KW:
				case PARALLEL:
				case PARAMETER_MODE:
				case PARAMETER_NAME:
				case PARAMETER_ORDINAL_POSITION:
				case PARAMETER_SPECIFIC_CATALOG:
				case PARAMETER_SPECIFIC_NAME:
				case PARAMETER_SPECIFIC_SCHEMA:
				case PARSER:
				case PASCAL:
				case PASS:
				case PASSING:
				case PASSTHROUGH:
				case PASSWORD:
				case PAST:
				case PERMISSION:
				case PERMUTE:
				case PIPE:
				case PLAN:
				case PLANS:
				case PLI:
				case POLICY:
				case PRECEDING:
				case PREPARED:
				case PREV:
				case PRIVATE:
				case PROCEDURAL:
				case PROCEDURES:
				case PROGRAM:
				case PRUNE:
				case PUBLICATION:
				case QUOTES:
				case REASSIGN:
				case RECHECK:
				case RECOVERY:
				case REFRESH:
				case REINDEX:
				case RENAME:
				case REPEATABLE:
				case REPLACE:
				case REPLICA:
				case REQUIRING:
				case RESET:
				case RESPECT:
				case RESTART:
				case RESTORE:
				case RETURNED_CARDINALITY:
				case RETURNED_LENGTH:
				case RETURNED_OCTET_LENGTH:
				case RETURNED_SQLSTATE:
				case RETURNING:
				case ROLE:
				case ROUTINE:
				case ROUTINES:
				case ROUTINE_CATALOG:
				case ROUTINE_NAME:
				case ROUTINE_SCHEMA:
				case ROW_COUNT:
				case RULE:
				case SCALAR:
				case SCALE:
				case SCHEMAS:
				case SCHEMA_NAME:
				case SCOPE_CATALOG:
				case SCOPE_NAME:
				case SCOPE_SCHEMA:
				case SECURITY:
				case SELECTIVE:
				case SELF:
				case SEMANTICS:
				case SEQUENCE:
				case SEQUENCES:
				case SERIALIZABLE:
				case SERVER:
				case SERVER_NAME:
				case SETOF:
				case SETS:
				case SHARE:
				case SIMPLE:
				case SNAPSHOT:
				case SORT_DIRECTION:
				case SOURCE:
				case SPECIFIC_NAME:
				case STABLE:
				case STANDALONE:
				case STATE:
				case STATEMENT:
				case STATISTICS:
				case STDIN:
				case STDOUT:
				case STORAGE:
				case STORED:
				case STRICT:
				case STRING:
				case STRIP:
				case STRUCTURE:
				case STYLE:
				case SUBCLASS_ORIGIN:
				case SUBSCRIPTION:
				case SUPPORT:
				case SYSID:
				case T_KW:
				case TABLES:
				case TABLESPACE:
				case TABLE_NAME:
				case TEMP:
				case TEMPLATE:
				case THROUGH:
				case TIES:
				case TOKEN:
				case TOP_LEVEL_COUNT:
				case TRANSACTIONS_COMMITTED:
				case TRANSACTIONS_ROLLED_BACK:
				case TRANSACTION_ACTIVE:
				case TRANSFORM:
				case TRANSFORMS:
				case TRIGGER_CATALOG:
				case TRIGGER_NAME:
				case TRIGGER_SCHEMA:
				case TRUSTED:
				case TYPE:
				case TYPES:
				case UNBOUNDED:
				case UNCOMMITTED:
				case UNCONDITIONAL:
				case UNDER:
				case UNENCRYPTED:
				case UNLINK:
				case UNLISTEN:
				case UNLOGGED:
				case UNMATCHED:
				case UNNAMED:
				case UNTIL:
				case UNTYPED:
				case URI:
				case USER_DEFINED_TYPE_CATALOG:
				case USER_DEFINED_TYPE_CODE:
				case USER_DEFINED_TYPE_NAME:
				case USER_DEFINED_TYPE_SCHEMA:
				case UTF16:
				case UTF32:
				case UTF8:
				case VACUUM:
				case VALID:
				case VALIDATE:
				case VALIDATOR:
				case VERBOSE:
				case VERSION:
				case VIEWS:
				case VOLATILE:
				case WHITESPACE:
				case WRAPPER:
				case XMLDECLARATION:
				case XMLROOT:
				case XMLSCHEMA:
				case YES:
				case WORD:
					{
					setState(223);
					idnt();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(226);
				match(L_PAREN);
				setState(228);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -35734127910226L) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & -1L) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & -1L) != 0) || ((((_la - 192)) & ~0x3f) == 0 && ((1L << (_la - 192)) & -1L) != 0) || ((((_la - 256)) & ~0x3f) == 0 && ((1L << (_la - 256)) & -1L) != 0) || ((((_la - 320)) & ~0x3f) == 0 && ((1L << (_la - 320)) & -1L) != 0) || ((((_la - 384)) & ~0x3f) == 0 && ((1L << (_la - 384)) & -1L) != 0) || ((((_la - 448)) & ~0x3f) == 0 && ((1L << (_la - 448)) & -1L) != 0) || ((((_la - 512)) & ~0x3f) == 0 && ((1L << (_la - 512)) & -1L) != 0) || ((((_la - 576)) & ~0x3f) == 0 && ((1L << (_la - 576)) & -1L) != 0) || ((((_la - 640)) & ~0x3f) == 0 && ((1L << (_la - 640)) & -3L) != 0) || ((((_la - 704)) & ~0x3f) == 0 && ((1L << (_la - 704)) & -1L) != 0) || ((((_la - 768)) & ~0x3f) == 0 && ((1L << (_la - 768)) & -1L) != 0) || ((((_la - 832)) & ~0x3f) == 0 && ((1L << (_la - 832)) & -1L) != 0) || ((((_la - 896)) & ~0x3f) == 0 && ((1L << (_la - 896)) & 268435455L) != 0)) {
					{
					setState(227);
					stmt_items(0);
					}
				}

				setState(230);
				match(R_PAREN);
				}
				break;
			case 6:
				{
				_localctx = new KeywordStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(232);
				reserved_keyword();
				}
				break;
			case 7:
				{
				_localctx = new TypeStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(233);
				tipe();
				}
				break;
			case 8:
				{
				_localctx = new IdntStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(234);
				idnt();
				}
				break;
			case 9:
				{
				_localctx = new ParamStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(235);
				match(PARAM);
				}
				break;
			case 10:
				{
				_localctx = new OperatorStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(236);
				operator();
				}
				break;
			case 11:
				{
				_localctx = new CommentStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(237);
				comment();
				}
				break;
			case 12:
				{
				_localctx = new UnknownStmtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(238);
				match(UNKNOWN_WORD);
				setState(240);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(239);
					match(IN_UNKNOWN_WORD);
					}
					break;
				}
				setState(243);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(242);
					_la = _input.LA(1);
					if ( !(_la==EOF || _la==UNKNOWN_WORD_END) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(259);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(257);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
					case 1:
						{
						_localctx = new TypeCastContext(new Stmt_itemsContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_stmt_items);
						setState(247);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(248);
						match(DOUBLE_COLON);
						setState(249);
						stmt_items(5);
						}
						break;
					case 2:
						{
						_localctx = new CommaSeparatedContext(new Stmt_itemsContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_stmt_items);
						setState(250);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(253); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(251);
								match(COMMA);
								setState(252);
								stmt_items(0);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(255); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						}
						break;
					}
					} 
				}
				setState(261);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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
	public static class Reserved_keywordContext extends ParserRuleContext {
		public TerminalNode ABS() { return getToken(PsqlParser.ABS, 0); }
		public TerminalNode ABSENT() { return getToken(PsqlParser.ABSENT, 0); }
		public TerminalNode ABSOLUTE() { return getToken(PsqlParser.ABSOLUTE, 0); }
		public TerminalNode ACOS() { return getToken(PsqlParser.ACOS, 0); }
		public TerminalNode ACTION() { return getToken(PsqlParser.ACTION, 0); }
		public TerminalNode ADD() { return getToken(PsqlParser.ADD, 0); }
		public TerminalNode ALL() { return getToken(PsqlParser.ALL, 0); }
		public TerminalNode ALLOCATE() { return getToken(PsqlParser.ALLOCATE, 0); }
		public TerminalNode ALTER() { return getToken(PsqlParser.ALTER, 0); }
		public TerminalNode ANALYSE() { return getToken(PsqlParser.ANALYSE, 0); }
		public TerminalNode ANALYZE() { return getToken(PsqlParser.ANALYZE, 0); }
		public TerminalNode ANY() { return getToken(PsqlParser.ANY, 0); }
		public TerminalNode ANY_VALUE() { return getToken(PsqlParser.ANY_VALUE, 0); }
		public TerminalNode ARE() { return getToken(PsqlParser.ARE, 0); }
		public TerminalNode ARRAY() { return getToken(PsqlParser.ARRAY, 0); }
		public TerminalNode ARRAY_AGG() { return getToken(PsqlParser.ARRAY_AGG, 0); }
		public TerminalNode ARRAY_MAX_CARDINALITY() { return getToken(PsqlParser.ARRAY_MAX_CARDINALITY, 0); }
		public TerminalNode AS() { return getToken(PsqlParser.AS, 0); }
		public TerminalNode ASC() { return getToken(PsqlParser.ASC, 0); }
		public TerminalNode ASENSITIVE() { return getToken(PsqlParser.ASENSITIVE, 0); }
		public TerminalNode ASIN() { return getToken(PsqlParser.ASIN, 0); }
		public TerminalNode ASSERTION() { return getToken(PsqlParser.ASSERTION, 0); }
		public TerminalNode ASYMMETRIC() { return getToken(PsqlParser.ASYMMETRIC, 0); }
		public TerminalNode AT() { return getToken(PsqlParser.AT, 0); }
		public TerminalNode ATAN() { return getToken(PsqlParser.ATAN, 0); }
		public TerminalNode ATOMIC() { return getToken(PsqlParser.ATOMIC, 0); }
		public TerminalNode AUTHORIZATION() { return getToken(PsqlParser.AUTHORIZATION, 0); }
		public TerminalNode AVG() { return getToken(PsqlParser.AVG, 0); }
		public TerminalNode BEGIN() { return getToken(PsqlParser.BEGIN, 0); }
		public TerminalNode BEGIN_FRAME() { return getToken(PsqlParser.BEGIN_FRAME, 0); }
		public TerminalNode BEGIN_PARTITION() { return getToken(PsqlParser.BEGIN_PARTITION, 0); }
		public TerminalNode BETWEEN() { return getToken(PsqlParser.BETWEEN, 0); }
		public TerminalNode BINARY() { return getToken(PsqlParser.BINARY, 0); }
		public TerminalNode BIT_LENGTH() { return getToken(PsqlParser.BIT_LENGTH, 0); }
		public TerminalNode BLOB() { return getToken(PsqlParser.BLOB, 0); }
		public TerminalNode BOTH() { return getToken(PsqlParser.BOTH, 0); }
		public TerminalNode BTRIM() { return getToken(PsqlParser.BTRIM, 0); }
		public TerminalNode BY() { return getToken(PsqlParser.BY, 0); }
		public TerminalNode CALL() { return getToken(PsqlParser.CALL, 0); }
		public TerminalNode CALLED() { return getToken(PsqlParser.CALLED, 0); }
		public TerminalNode CARDINALITY() { return getToken(PsqlParser.CARDINALITY, 0); }
		public TerminalNode CASCADE() { return getToken(PsqlParser.CASCADE, 0); }
		public TerminalNode CASCADED() { return getToken(PsqlParser.CASCADED, 0); }
		public TerminalNode CASE() { return getToken(PsqlParser.CASE, 0); }
		public TerminalNode CAST() { return getToken(PsqlParser.CAST, 0); }
		public TerminalNode CATALOG() { return getToken(PsqlParser.CATALOG, 0); }
		public TerminalNode CEIL() { return getToken(PsqlParser.CEIL, 0); }
		public TerminalNode CEILING() { return getToken(PsqlParser.CEILING, 0); }
		public TerminalNode CHARACTER_LENGTH() { return getToken(PsqlParser.CHARACTER_LENGTH, 0); }
		public TerminalNode CHAR_LENGTH() { return getToken(PsqlParser.CHAR_LENGTH, 0); }
		public TerminalNode CHECK() { return getToken(PsqlParser.CHECK, 0); }
		public TerminalNode CLASSIFIER() { return getToken(PsqlParser.CLASSIFIER, 0); }
		public TerminalNode CLOB() { return getToken(PsqlParser.CLOB, 0); }
		public TerminalNode CLOSE() { return getToken(PsqlParser.CLOSE, 0); }
		public TerminalNode COALESCE() { return getToken(PsqlParser.COALESCE, 0); }
		public TerminalNode COLLATE() { return getToken(PsqlParser.COLLATE, 0); }
		public TerminalNode COLLATION() { return getToken(PsqlParser.COLLATION, 0); }
		public TerminalNode COLLECT() { return getToken(PsqlParser.COLLECT, 0); }
		public TerminalNode COLUMN() { return getToken(PsqlParser.COLUMN, 0); }
		public TerminalNode COMMIT() { return getToken(PsqlParser.COMMIT, 0); }
		public TerminalNode CONDITION() { return getToken(PsqlParser.CONDITION, 0); }
		public TerminalNode CONNECT() { return getToken(PsqlParser.CONNECT, 0); }
		public TerminalNode CONNECTION() { return getToken(PsqlParser.CONNECTION, 0); }
		public TerminalNode CONSTRAINT() { return getToken(PsqlParser.CONSTRAINT, 0); }
		public TerminalNode CONSTRAINTS() { return getToken(PsqlParser.CONSTRAINTS, 0); }
		public TerminalNode CONTAINS() { return getToken(PsqlParser.CONTAINS, 0); }
		public TerminalNode CONTINUE() { return getToken(PsqlParser.CONTINUE, 0); }
		public TerminalNode CONVERT() { return getToken(PsqlParser.CONVERT, 0); }
		public TerminalNode COPY() { return getToken(PsqlParser.COPY, 0); }
		public TerminalNode CORR() { return getToken(PsqlParser.CORR, 0); }
		public TerminalNode CORRESPONDING() { return getToken(PsqlParser.CORRESPONDING, 0); }
		public TerminalNode COS() { return getToken(PsqlParser.COS, 0); }
		public TerminalNode COSH() { return getToken(PsqlParser.COSH, 0); }
		public TerminalNode COUNT() { return getToken(PsqlParser.COUNT, 0); }
		public TerminalNode COVAR_POP() { return getToken(PsqlParser.COVAR_POP, 0); }
		public TerminalNode COVAR_SAMP() { return getToken(PsqlParser.COVAR_SAMP, 0); }
		public TerminalNode CREATE() { return getToken(PsqlParser.CREATE, 0); }
		public TerminalNode CROSS() { return getToken(PsqlParser.CROSS, 0); }
		public TerminalNode CUBE() { return getToken(PsqlParser.CUBE, 0); }
		public TerminalNode CUME_DIST() { return getToken(PsqlParser.CUME_DIST, 0); }
		public TerminalNode CURRENT() { return getToken(PsqlParser.CURRENT, 0); }
		public TerminalNode CURRENT_CATALOG() { return getToken(PsqlParser.CURRENT_CATALOG, 0); }
		public TerminalNode CURRENT_DATE() { return getToken(PsqlParser.CURRENT_DATE, 0); }
		public TerminalNode CURRENT_DEFAULT_TRANSFORM_GROUP() { return getToken(PsqlParser.CURRENT_DEFAULT_TRANSFORM_GROUP, 0); }
		public TerminalNode CURRENT_PATH() { return getToken(PsqlParser.CURRENT_PATH, 0); }
		public TerminalNode CURRENT_ROLE() { return getToken(PsqlParser.CURRENT_ROLE, 0); }
		public TerminalNode CURRENT_ROW() { return getToken(PsqlParser.CURRENT_ROW, 0); }
		public TerminalNode CURRENT_SCHEMA() { return getToken(PsqlParser.CURRENT_SCHEMA, 0); }
		public TerminalNode CURRENT_TIME() { return getToken(PsqlParser.CURRENT_TIME, 0); }
		public TerminalNode CURRENT_TIMESTAMP() { return getToken(PsqlParser.CURRENT_TIMESTAMP, 0); }
		public TerminalNode CURRENT_TRANSFORM_GROUP_FOR_TYPE() { return getToken(PsqlParser.CURRENT_TRANSFORM_GROUP_FOR_TYPE, 0); }
		public TerminalNode CURRENT_USER() { return getToken(PsqlParser.CURRENT_USER, 0); }
		public TerminalNode CURSOR() { return getToken(PsqlParser.CURSOR, 0); }
		public TerminalNode CYCLE() { return getToken(PsqlParser.CYCLE, 0); }
		public TerminalNode DATALINK() { return getToken(PsqlParser.DATALINK, 0); }
		public TerminalNode DAY() { return getToken(PsqlParser.DAY, 0); }
		public TerminalNode DEALLOCATE() { return getToken(PsqlParser.DEALLOCATE, 0); }
		public TerminalNode DEC() { return getToken(PsqlParser.DEC, 0); }
		public TerminalNode DECFLOAT() { return getToken(PsqlParser.DECFLOAT, 0); }
		public TerminalNode DECLARE() { return getToken(PsqlParser.DECLARE, 0); }
		public TerminalNode DEFAULT() { return getToken(PsqlParser.DEFAULT, 0); }
		public TerminalNode DEFERRABLE() { return getToken(PsqlParser.DEFERRABLE, 0); }
		public TerminalNode DEFERRED() { return getToken(PsqlParser.DEFERRED, 0); }
		public TerminalNode DEFINE() { return getToken(PsqlParser.DEFINE, 0); }
		public TerminalNode DELETE() { return getToken(PsqlParser.DELETE, 0); }
		public TerminalNode DENSE_RANK() { return getToken(PsqlParser.DENSE_RANK, 0); }
		public TerminalNode DEREF() { return getToken(PsqlParser.DEREF, 0); }
		public TerminalNode DESC() { return getToken(PsqlParser.DESC, 0); }
		public TerminalNode DESCRIBE() { return getToken(PsqlParser.DESCRIBE, 0); }
		public TerminalNode DESCRIPTOR() { return getToken(PsqlParser.DESCRIPTOR, 0); }
		public TerminalNode DETERMINISTIC() { return getToken(PsqlParser.DETERMINISTIC, 0); }
		public TerminalNode DIAGNOSTICS() { return getToken(PsqlParser.DIAGNOSTICS, 0); }
		public TerminalNode DISCONNECT() { return getToken(PsqlParser.DISCONNECT, 0); }
		public TerminalNode DISTINCT() { return getToken(PsqlParser.DISTINCT, 0); }
		public TerminalNode DLNEWCOPY() { return getToken(PsqlParser.DLNEWCOPY, 0); }
		public TerminalNode DLPREVIOUSCOPY() { return getToken(PsqlParser.DLPREVIOUSCOPY, 0); }
		public TerminalNode DLURLCOMPLETE() { return getToken(PsqlParser.DLURLCOMPLETE, 0); }
		public TerminalNode DLURLCOMPLETEONLY() { return getToken(PsqlParser.DLURLCOMPLETEONLY, 0); }
		public TerminalNode DLURLCOMPLETEWRITE() { return getToken(PsqlParser.DLURLCOMPLETEWRITE, 0); }
		public TerminalNode DLURLPATH() { return getToken(PsqlParser.DLURLPATH, 0); }
		public TerminalNode DLURLPATHONLY() { return getToken(PsqlParser.DLURLPATHONLY, 0); }
		public TerminalNode DLURLPATHWRITE() { return getToken(PsqlParser.DLURLPATHWRITE, 0); }
		public TerminalNode DLURLSCHEME() { return getToken(PsqlParser.DLURLSCHEME, 0); }
		public TerminalNode DLURLSERVER() { return getToken(PsqlParser.DLURLSERVER, 0); }
		public TerminalNode DLVALUE() { return getToken(PsqlParser.DLVALUE, 0); }
		public TerminalNode DO() { return getToken(PsqlParser.DO, 0); }
		public TerminalNode DOMAIN() { return getToken(PsqlParser.DOMAIN, 0); }
		public TerminalNode DROP() { return getToken(PsqlParser.DROP, 0); }
		public TerminalNode DYNAMIC() { return getToken(PsqlParser.DYNAMIC, 0); }
		public TerminalNode EACH() { return getToken(PsqlParser.EACH, 0); }
		public TerminalNode ELEMENT() { return getToken(PsqlParser.ELEMENT, 0); }
		public TerminalNode ELSE() { return getToken(PsqlParser.ELSE, 0); }
		public TerminalNode EMPTY() { return getToken(PsqlParser.EMPTY, 0); }
		public TerminalNode END() { return getToken(PsqlParser.END, 0); }
		public TerminalNode END_EXEC() { return getToken(PsqlParser.END_EXEC, 0); }
		public TerminalNode END_FRAME() { return getToken(PsqlParser.END_FRAME, 0); }
		public TerminalNode END_PARTITION() { return getToken(PsqlParser.END_PARTITION, 0); }
		public TerminalNode EQUALS() { return getToken(PsqlParser.EQUALS, 0); }
		public TerminalNode ESCAPE() { return getToken(PsqlParser.ESCAPE, 0); }
		public TerminalNode EVERY() { return getToken(PsqlParser.EVERY, 0); }
		public TerminalNode EXCEPT() { return getToken(PsqlParser.EXCEPT, 0); }
		public TerminalNode EXCEPTION() { return getToken(PsqlParser.EXCEPTION, 0); }
		public TerminalNode EXEC() { return getToken(PsqlParser.EXEC, 0); }
		public TerminalNode EXECUTE() { return getToken(PsqlParser.EXECUTE, 0); }
		public TerminalNode EXISTS() { return getToken(PsqlParser.EXISTS, 0); }
		public TerminalNode EXP() { return getToken(PsqlParser.EXP, 0); }
		public TerminalNode EXTERNAL() { return getToken(PsqlParser.EXTERNAL, 0); }
		public TerminalNode EXTRACT() { return getToken(PsqlParser.EXTRACT, 0); }
		public TerminalNode FALSE() { return getToken(PsqlParser.FALSE, 0); }
		public TerminalNode FETCH() { return getToken(PsqlParser.FETCH, 0); }
		public TerminalNode FILTER() { return getToken(PsqlParser.FILTER, 0); }
		public TerminalNode FIRST() { return getToken(PsqlParser.FIRST, 0); }
		public TerminalNode FIRST_VALUE() { return getToken(PsqlParser.FIRST_VALUE, 0); }
		public TerminalNode FLOAT() { return getToken(PsqlParser.FLOAT, 0); }
		public TerminalNode FLOOR() { return getToken(PsqlParser.FLOOR, 0); }
		public TerminalNode FOR() { return getToken(PsqlParser.FOR, 0); }
		public TerminalNode FOREIGN() { return getToken(PsqlParser.FOREIGN, 0); }
		public TerminalNode FOUND() { return getToken(PsqlParser.FOUND, 0); }
		public TerminalNode FRAME_ROW() { return getToken(PsqlParser.FRAME_ROW, 0); }
		public TerminalNode FREE() { return getToken(PsqlParser.FREE, 0); }
		public TerminalNode FROM() { return getToken(PsqlParser.FROM, 0); }
		public TerminalNode FULL() { return getToken(PsqlParser.FULL, 0); }
		public TerminalNode FUNCTION() { return getToken(PsqlParser.FUNCTION, 0); }
		public TerminalNode FUSION() { return getToken(PsqlParser.FUSION, 0); }
		public TerminalNode GET() { return getToken(PsqlParser.GET, 0); }
		public TerminalNode GLOBAL() { return getToken(PsqlParser.GLOBAL, 0); }
		public TerminalNode GO() { return getToken(PsqlParser.GO, 0); }
		public TerminalNode GOTO() { return getToken(PsqlParser.GOTO, 0); }
		public TerminalNode GRANT() { return getToken(PsqlParser.GRANT, 0); }
		public TerminalNode GREATEST() { return getToken(PsqlParser.GREATEST, 0); }
		public TerminalNode GROUP() { return getToken(PsqlParser.GROUP, 0); }
		public TerminalNode GROUPING() { return getToken(PsqlParser.GROUPING, 0); }
		public TerminalNode GROUPS() { return getToken(PsqlParser.GROUPS, 0); }
		public TerminalNode HAVING() { return getToken(PsqlParser.HAVING, 0); }
		public TerminalNode HOLD() { return getToken(PsqlParser.HOLD, 0); }
		public TerminalNode HOUR() { return getToken(PsqlParser.HOUR, 0); }
		public TerminalNode IDENTITY() { return getToken(PsqlParser.IDENTITY, 0); }
		public TerminalNode IMMEDIATE() { return getToken(PsqlParser.IMMEDIATE, 0); }
		public TerminalNode IMPORT() { return getToken(PsqlParser.IMPORT, 0); }
		public TerminalNode IN() { return getToken(PsqlParser.IN, 0); }
		public TerminalNode INDICATOR() { return getToken(PsqlParser.INDICATOR, 0); }
		public TerminalNode INITIAL() { return getToken(PsqlParser.INITIAL, 0); }
		public TerminalNode INITIALLY() { return getToken(PsqlParser.INITIALLY, 0); }
		public TerminalNode INNER() { return getToken(PsqlParser.INNER, 0); }
		public TerminalNode INOUT() { return getToken(PsqlParser.INOUT, 0); }
		public TerminalNode INPUT() { return getToken(PsqlParser.INPUT, 0); }
		public TerminalNode INSENSITIVE() { return getToken(PsqlParser.INSENSITIVE, 0); }
		public TerminalNode INSERT() { return getToken(PsqlParser.INSERT, 0); }
		public TerminalNode INTERSECT() { return getToken(PsqlParser.INTERSECT, 0); }
		public TerminalNode INTERSECTION() { return getToken(PsqlParser.INTERSECTION, 0); }
		public TerminalNode INTO() { return getToken(PsqlParser.INTO, 0); }
		public TerminalNode IS() { return getToken(PsqlParser.IS, 0); }
		public TerminalNode ISOLATION() { return getToken(PsqlParser.ISOLATION, 0); }
		public TerminalNode JOIN() { return getToken(PsqlParser.JOIN, 0); }
		public TerminalNode JSON_ARRAY() { return getToken(PsqlParser.JSON_ARRAY, 0); }
		public TerminalNode JSON_ARRAYAGG() { return getToken(PsqlParser.JSON_ARRAYAGG, 0); }
		public TerminalNode JSON_EXISTS() { return getToken(PsqlParser.JSON_EXISTS, 0); }
		public TerminalNode JSON_OBJECT() { return getToken(PsqlParser.JSON_OBJECT, 0); }
		public TerminalNode JSON_OBJECTAGG() { return getToken(PsqlParser.JSON_OBJECTAGG, 0); }
		public TerminalNode JSON_QUERY() { return getToken(PsqlParser.JSON_QUERY, 0); }
		public TerminalNode JSON_SCALAR() { return getToken(PsqlParser.JSON_SCALAR, 0); }
		public TerminalNode JSON_SERIALIZE() { return getToken(PsqlParser.JSON_SERIALIZE, 0); }
		public TerminalNode JSON_TABLE() { return getToken(PsqlParser.JSON_TABLE, 0); }
		public TerminalNode JSON_TABLE_PRIMITIVE() { return getToken(PsqlParser.JSON_TABLE_PRIMITIVE, 0); }
		public TerminalNode JSON_VALUE() { return getToken(PsqlParser.JSON_VALUE, 0); }
		public TerminalNode KEY() { return getToken(PsqlParser.KEY, 0); }
		public TerminalNode LAG() { return getToken(PsqlParser.LAG, 0); }
		public TerminalNode LANGUAGE() { return getToken(PsqlParser.LANGUAGE, 0); }
		public TerminalNode LARGE() { return getToken(PsqlParser.LARGE, 0); }
		public TerminalNode LAST() { return getToken(PsqlParser.LAST, 0); }
		public TerminalNode LAST_VALUE() { return getToken(PsqlParser.LAST_VALUE, 0); }
		public TerminalNode LATERAL() { return getToken(PsqlParser.LATERAL, 0); }
		public TerminalNode LEAD() { return getToken(PsqlParser.LEAD, 0); }
		public TerminalNode LEADING() { return getToken(PsqlParser.LEADING, 0); }
		public TerminalNode LEAST() { return getToken(PsqlParser.LEAST, 0); }
		public TerminalNode LEFT() { return getToken(PsqlParser.LEFT, 0); }
		public TerminalNode LEVEL() { return getToken(PsqlParser.LEVEL, 0); }
		public TerminalNode LIKE_REGEX() { return getToken(PsqlParser.LIKE_REGEX, 0); }
		public TerminalNode LISTAGG() { return getToken(PsqlParser.LISTAGG, 0); }
		public TerminalNode LN() { return getToken(PsqlParser.LN, 0); }
		public TerminalNode LOCAL() { return getToken(PsqlParser.LOCAL, 0); }
		public TerminalNode LOCALTIME() { return getToken(PsqlParser.LOCALTIME, 0); }
		public TerminalNode LOCALTIMESTAMP() { return getToken(PsqlParser.LOCALTIMESTAMP, 0); }
		public TerminalNode LOG() { return getToken(PsqlParser.LOG, 0); }
		public TerminalNode LOG10() { return getToken(PsqlParser.LOG10, 0); }
		public TerminalNode LOWER() { return getToken(PsqlParser.LOWER, 0); }
		public TerminalNode LPAD() { return getToken(PsqlParser.LPAD, 0); }
		public TerminalNode LTRIM() { return getToken(PsqlParser.LTRIM, 0); }
		public TerminalNode MATCH() { return getToken(PsqlParser.MATCH, 0); }
		public TerminalNode MATCHES() { return getToken(PsqlParser.MATCHES, 0); }
		public TerminalNode MATCH_NUMBER() { return getToken(PsqlParser.MATCH_NUMBER, 0); }
		public TerminalNode MATCH_RECOGNIZE() { return getToken(PsqlParser.MATCH_RECOGNIZE, 0); }
		public TerminalNode MAX() { return getToken(PsqlParser.MAX, 0); }
		public TerminalNode MEMBER() { return getToken(PsqlParser.MEMBER, 0); }
		public TerminalNode MERGE() { return getToken(PsqlParser.MERGE, 0); }
		public TerminalNode METHOD() { return getToken(PsqlParser.METHOD, 0); }
		public TerminalNode MIN() { return getToken(PsqlParser.MIN, 0); }
		public TerminalNode MINUTE() { return getToken(PsqlParser.MINUTE, 0); }
		public TerminalNode MOD() { return getToken(PsqlParser.MOD, 0); }
		public TerminalNode MODIFIES() { return getToken(PsqlParser.MODIFIES, 0); }
		public TerminalNode MODULE() { return getToken(PsqlParser.MODULE, 0); }
		public TerminalNode MONTH() { return getToken(PsqlParser.MONTH, 0); }
		public TerminalNode MULTISET() { return getToken(PsqlParser.MULTISET, 0); }
		public TerminalNode NAMES() { return getToken(PsqlParser.NAMES, 0); }
		public TerminalNode NATIONAL() { return getToken(PsqlParser.NATIONAL, 0); }
		public TerminalNode NATURAL() { return getToken(PsqlParser.NATURAL, 0); }
		public TerminalNode NCHAR() { return getToken(PsqlParser.NCHAR, 0); }
		public TerminalNode NCLOB() { return getToken(PsqlParser.NCLOB, 0); }
		public TerminalNode NEW() { return getToken(PsqlParser.NEW, 0); }
		public TerminalNode NEXT() { return getToken(PsqlParser.NEXT, 0); }
		public TerminalNode NO() { return getToken(PsqlParser.NO, 0); }
		public TerminalNode NONE() { return getToken(PsqlParser.NONE, 0); }
		public TerminalNode NORMALIZE() { return getToken(PsqlParser.NORMALIZE, 0); }
		public TerminalNode NTH_VALUE() { return getToken(PsqlParser.NTH_VALUE, 0); }
		public TerminalNode NTILE() { return getToken(PsqlParser.NTILE, 0); }
		public TerminalNode NULL() { return getToken(PsqlParser.NULL, 0); }
		public TerminalNode NULLIF() { return getToken(PsqlParser.NULLIF, 0); }
		public TerminalNode OCCURRENCES_REGEX() { return getToken(PsqlParser.OCCURRENCES_REGEX, 0); }
		public TerminalNode OCTET_LENGTH() { return getToken(PsqlParser.OCTET_LENGTH, 0); }
		public TerminalNode OF() { return getToken(PsqlParser.OF, 0); }
		public TerminalNode OFFSET() { return getToken(PsqlParser.OFFSET, 0); }
		public TerminalNode OLD() { return getToken(PsqlParser.OLD, 0); }
		public TerminalNode OMIT() { return getToken(PsqlParser.OMIT, 0); }
		public TerminalNode ON() { return getToken(PsqlParser.ON, 0); }
		public TerminalNode ONE() { return getToken(PsqlParser.ONE, 0); }
		public TerminalNode ONLY() { return getToken(PsqlParser.ONLY, 0); }
		public TerminalNode OPEN() { return getToken(PsqlParser.OPEN, 0); }
		public TerminalNode OPTION() { return getToken(PsqlParser.OPTION, 0); }
		public TerminalNode ORDER() { return getToken(PsqlParser.ORDER, 0); }
		public TerminalNode OUT() { return getToken(PsqlParser.OUT, 0); }
		public TerminalNode OUTER() { return getToken(PsqlParser.OUTER, 0); }
		public TerminalNode OUTPUT() { return getToken(PsqlParser.OUTPUT, 0); }
		public TerminalNode OVER() { return getToken(PsqlParser.OVER, 0); }
		public TerminalNode OVERLAPS() { return getToken(PsqlParser.OVERLAPS, 0); }
		public TerminalNode OVERLAY() { return getToken(PsqlParser.OVERLAY, 0); }
		public TerminalNode PAD() { return getToken(PsqlParser.PAD, 0); }
		public TerminalNode PARAMETER() { return getToken(PsqlParser.PARAMETER, 0); }
		public TerminalNode PARTIAL() { return getToken(PsqlParser.PARTIAL, 0); }
		public TerminalNode PARTITION() { return getToken(PsqlParser.PARTITION, 0); }
		public TerminalNode PATTERN() { return getToken(PsqlParser.PATTERN, 0); }
		public TerminalNode PER() { return getToken(PsqlParser.PER, 0); }
		public TerminalNode PERCENT() { return getToken(PsqlParser.PERCENT, 0); }
		public TerminalNode PERCENTILE_CONT() { return getToken(PsqlParser.PERCENTILE_CONT, 0); }
		public TerminalNode PERCENTILE_DISC() { return getToken(PsqlParser.PERCENTILE_DISC, 0); }
		public TerminalNode PERCENT_RANK() { return getToken(PsqlParser.PERCENT_RANK, 0); }
		public TerminalNode PERIOD() { return getToken(PsqlParser.PERIOD, 0); }
		public TerminalNode PLACING() { return getToken(PsqlParser.PLACING, 0); }
		public TerminalNode PORTION() { return getToken(PsqlParser.PORTION, 0); }
		public TerminalNode POSITION() { return getToken(PsqlParser.POSITION, 0); }
		public TerminalNode POSITION_REGEX() { return getToken(PsqlParser.POSITION_REGEX, 0); }
		public TerminalNode POWER() { return getToken(PsqlParser.POWER, 0); }
		public TerminalNode PRECEDES() { return getToken(PsqlParser.PRECEDES, 0); }
		public TerminalNode PRECISION() { return getToken(PsqlParser.PRECISION, 0); }
		public TerminalNode PREPARE() { return getToken(PsqlParser.PREPARE, 0); }
		public TerminalNode PRESERVE() { return getToken(PsqlParser.PRESERVE, 0); }
		public TerminalNode PRIMARY() { return getToken(PsqlParser.PRIMARY, 0); }
		public TerminalNode PRIOR() { return getToken(PsqlParser.PRIOR, 0); }
		public TerminalNode PRIVILEGES() { return getToken(PsqlParser.PRIVILEGES, 0); }
		public TerminalNode PROCEDURE() { return getToken(PsqlParser.PROCEDURE, 0); }
		public TerminalNode PTF() { return getToken(PsqlParser.PTF, 0); }
		public TerminalNode PUBLIC() { return getToken(PsqlParser.PUBLIC, 0); }
		public TerminalNode RANGE() { return getToken(PsqlParser.RANGE, 0); }
		public TerminalNode RANK() { return getToken(PsqlParser.RANK, 0); }
		public TerminalNode READ() { return getToken(PsqlParser.READ, 0); }
		public TerminalNode READS() { return getToken(PsqlParser.READS, 0); }
		public TerminalNode RECURSIVE() { return getToken(PsqlParser.RECURSIVE, 0); }
		public TerminalNode REF() { return getToken(PsqlParser.REF, 0); }
		public TerminalNode REFERENCES() { return getToken(PsqlParser.REFERENCES, 0); }
		public TerminalNode REFERENCING() { return getToken(PsqlParser.REFERENCING, 0); }
		public TerminalNode REGR_AVGX() { return getToken(PsqlParser.REGR_AVGX, 0); }
		public TerminalNode REGR_AVGY() { return getToken(PsqlParser.REGR_AVGY, 0); }
		public TerminalNode REGR_COUNT() { return getToken(PsqlParser.REGR_COUNT, 0); }
		public TerminalNode REGR_INTERCEPT() { return getToken(PsqlParser.REGR_INTERCEPT, 0); }
		public TerminalNode REGR_R2() { return getToken(PsqlParser.REGR_R2, 0); }
		public TerminalNode REGR_SLOPE() { return getToken(PsqlParser.REGR_SLOPE, 0); }
		public TerminalNode REGR_SXX() { return getToken(PsqlParser.REGR_SXX, 0); }
		public TerminalNode REGR_SXY() { return getToken(PsqlParser.REGR_SXY, 0); }
		public TerminalNode REGR_SYY() { return getToken(PsqlParser.REGR_SYY, 0); }
		public TerminalNode RELATIVE() { return getToken(PsqlParser.RELATIVE, 0); }
		public TerminalNode RELEASE() { return getToken(PsqlParser.RELEASE, 0); }
		public TerminalNode RESTRICT() { return getToken(PsqlParser.RESTRICT, 0); }
		public TerminalNode RESULT() { return getToken(PsqlParser.RESULT, 0); }
		public TerminalNode RETURN() { return getToken(PsqlParser.RETURN, 0); }
		public TerminalNode RETURNS() { return getToken(PsqlParser.RETURNS, 0); }
		public TerminalNode REVOKE() { return getToken(PsqlParser.REVOKE, 0); }
		public TerminalNode RIGHT() { return getToken(PsqlParser.RIGHT, 0); }
		public TerminalNode ROLLBACK() { return getToken(PsqlParser.ROLLBACK, 0); }
		public TerminalNode ROLLUP() { return getToken(PsqlParser.ROLLUP, 0); }
		public TerminalNode ROW() { return getToken(PsqlParser.ROW, 0); }
		public TerminalNode ROWS() { return getToken(PsqlParser.ROWS, 0); }
		public TerminalNode ROW_NUMBER() { return getToken(PsqlParser.ROW_NUMBER, 0); }
		public TerminalNode RPAD() { return getToken(PsqlParser.RPAD, 0); }
		public TerminalNode RTRIM() { return getToken(PsqlParser.RTRIM, 0); }
		public TerminalNode RUNNING() { return getToken(PsqlParser.RUNNING, 0); }
		public TerminalNode SAVEPOINT() { return getToken(PsqlParser.SAVEPOINT, 0); }
		public TerminalNode SCHEMA() { return getToken(PsqlParser.SCHEMA, 0); }
		public TerminalNode SCOPE() { return getToken(PsqlParser.SCOPE, 0); }
		public TerminalNode SCROLL() { return getToken(PsqlParser.SCROLL, 0); }
		public TerminalNode SEARCH() { return getToken(PsqlParser.SEARCH, 0); }
		public TerminalNode SECOND() { return getToken(PsqlParser.SECOND, 0); }
		public TerminalNode SECTION() { return getToken(PsqlParser.SECTION, 0); }
		public TerminalNode SEEK() { return getToken(PsqlParser.SEEK, 0); }
		public TerminalNode SELECT() { return getToken(PsqlParser.SELECT, 0); }
		public TerminalNode SENSITIVE() { return getToken(PsqlParser.SENSITIVE, 0); }
		public TerminalNode SESSION() { return getToken(PsqlParser.SESSION, 0); }
		public TerminalNode SESSION_USER() { return getToken(PsqlParser.SESSION_USER, 0); }
		public TerminalNode SET() { return getToken(PsqlParser.SET, 0); }
		public TerminalNode SHOW() { return getToken(PsqlParser.SHOW, 0); }
		public TerminalNode SIMILAR() { return getToken(PsqlParser.SIMILAR, 0); }
		public TerminalNode SIN() { return getToken(PsqlParser.SIN, 0); }
		public TerminalNode SINH() { return getToken(PsqlParser.SINH, 0); }
		public TerminalNode SIZE() { return getToken(PsqlParser.SIZE, 0); }
		public TerminalNode SKIP_KW() { return getToken(PsqlParser.SKIP_KW, 0); }
		public TerminalNode SOME() { return getToken(PsqlParser.SOME, 0); }
		public TerminalNode SPACE() { return getToken(PsqlParser.SPACE, 0); }
		public TerminalNode SPECIFIC() { return getToken(PsqlParser.SPECIFIC, 0); }
		public TerminalNode SPECIFICTYPE() { return getToken(PsqlParser.SPECIFICTYPE, 0); }
		public TerminalNode SQL() { return getToken(PsqlParser.SQL, 0); }
		public TerminalNode SQLCODE() { return getToken(PsqlParser.SQLCODE, 0); }
		public TerminalNode SQLERROR() { return getToken(PsqlParser.SQLERROR, 0); }
		public TerminalNode SQLEXCEPTION() { return getToken(PsqlParser.SQLEXCEPTION, 0); }
		public TerminalNode SQLSTATE() { return getToken(PsqlParser.SQLSTATE, 0); }
		public TerminalNode SQLWARNING() { return getToken(PsqlParser.SQLWARNING, 0); }
		public TerminalNode SQRT() { return getToken(PsqlParser.SQRT, 0); }
		public TerminalNode START() { return getToken(PsqlParser.START, 0); }
		public TerminalNode STATIC() { return getToken(PsqlParser.STATIC, 0); }
		public TerminalNode STDDEV_POP() { return getToken(PsqlParser.STDDEV_POP, 0); }
		public TerminalNode STDDEV_SAMP() { return getToken(PsqlParser.STDDEV_SAMP, 0); }
		public TerminalNode SUBMULTISET() { return getToken(PsqlParser.SUBMULTISET, 0); }
		public TerminalNode SUBSET() { return getToken(PsqlParser.SUBSET, 0); }
		public TerminalNode SUBSTRING() { return getToken(PsqlParser.SUBSTRING, 0); }
		public TerminalNode SUBSTRING_REGEX() { return getToken(PsqlParser.SUBSTRING_REGEX, 0); }
		public TerminalNode SUCCEEDS() { return getToken(PsqlParser.SUCCEEDS, 0); }
		public TerminalNode SUM() { return getToken(PsqlParser.SUM, 0); }
		public TerminalNode SYMMETRIC() { return getToken(PsqlParser.SYMMETRIC, 0); }
		public TerminalNode SYSTEM() { return getToken(PsqlParser.SYSTEM, 0); }
		public TerminalNode SYSTEM_TIME() { return getToken(PsqlParser.SYSTEM_TIME, 0); }
		public TerminalNode SYSTEM_USER() { return getToken(PsqlParser.SYSTEM_USER, 0); }
		public TerminalNode TABLE() { return getToken(PsqlParser.TABLE, 0); }
		public TerminalNode TABLESAMPLE() { return getToken(PsqlParser.TABLESAMPLE, 0); }
		public TerminalNode TAN() { return getToken(PsqlParser.TAN, 0); }
		public TerminalNode TANH() { return getToken(PsqlParser.TANH, 0); }
		public TerminalNode TEMPORARY() { return getToken(PsqlParser.TEMPORARY, 0); }
		public TerminalNode THEN() { return getToken(PsqlParser.THEN, 0); }
		public TerminalNode TIMEZONE_HOUR() { return getToken(PsqlParser.TIMEZONE_HOUR, 0); }
		public TerminalNode TIMEZONE_MINUTE() { return getToken(PsqlParser.TIMEZONE_MINUTE, 0); }
		public TerminalNode TO() { return getToken(PsqlParser.TO, 0); }
		public TerminalNode TRAILING() { return getToken(PsqlParser.TRAILING, 0); }
		public TerminalNode TRANSACTION() { return getToken(PsqlParser.TRANSACTION, 0); }
		public TerminalNode TRANSLATE() { return getToken(PsqlParser.TRANSLATE, 0); }
		public TerminalNode TRANSLATE_REGEX() { return getToken(PsqlParser.TRANSLATE_REGEX, 0); }
		public TerminalNode TRANSLATION() { return getToken(PsqlParser.TRANSLATION, 0); }
		public TerminalNode TREAT() { return getToken(PsqlParser.TREAT, 0); }
		public TerminalNode TRIGGER() { return getToken(PsqlParser.TRIGGER, 0); }
		public TerminalNode TRIM() { return getToken(PsqlParser.TRIM, 0); }
		public TerminalNode TRIM_ARRAY() { return getToken(PsqlParser.TRIM_ARRAY, 0); }
		public TerminalNode TRUE() { return getToken(PsqlParser.TRUE, 0); }
		public TerminalNode TRUNCATE() { return getToken(PsqlParser.TRUNCATE, 0); }
		public TerminalNode UESCAPE() { return getToken(PsqlParser.UESCAPE, 0); }
		public TerminalNode UNION() { return getToken(PsqlParser.UNION, 0); }
		public TerminalNode UNIQUE() { return getToken(PsqlParser.UNIQUE, 0); }
		public TerminalNode UNKNOWN() { return getToken(PsqlParser.UNKNOWN, 0); }
		public TerminalNode UNNEST() { return getToken(PsqlParser.UNNEST, 0); }
		public TerminalNode UPDATE() { return getToken(PsqlParser.UPDATE, 0); }
		public TerminalNode UPPER() { return getToken(PsqlParser.UPPER, 0); }
		public TerminalNode USAGE() { return getToken(PsqlParser.USAGE, 0); }
		public TerminalNode USER() { return getToken(PsqlParser.USER, 0); }
		public TerminalNode USING() { return getToken(PsqlParser.USING, 0); }
		public TerminalNode VALUE() { return getToken(PsqlParser.VALUE, 0); }
		public TerminalNode VALUES() { return getToken(PsqlParser.VALUES, 0); }
		public TerminalNode VALUE_OF() { return getToken(PsqlParser.VALUE_OF, 0); }
		public TerminalNode VARBINARY() { return getToken(PsqlParser.VARBINARY, 0); }
		public TerminalNode VARIADIC() { return getToken(PsqlParser.VARIADIC, 0); }
		public TerminalNode VARYING() { return getToken(PsqlParser.VARYING, 0); }
		public TerminalNode VAR_POP() { return getToken(PsqlParser.VAR_POP, 0); }
		public TerminalNode VAR_SAMP() { return getToken(PsqlParser.VAR_SAMP, 0); }
		public TerminalNode VERSIONING() { return getToken(PsqlParser.VERSIONING, 0); }
		public TerminalNode VIEW() { return getToken(PsqlParser.VIEW, 0); }
		public TerminalNode WHEN() { return getToken(PsqlParser.WHEN, 0); }
		public TerminalNode WHENEVER() { return getToken(PsqlParser.WHENEVER, 0); }
		public TerminalNode WHERE() { return getToken(PsqlParser.WHERE, 0); }
		public TerminalNode WIDTH_BUCKET() { return getToken(PsqlParser.WIDTH_BUCKET, 0); }
		public TerminalNode WINDOW() { return getToken(PsqlParser.WINDOW, 0); }
		public TerminalNode WITH() { return getToken(PsqlParser.WITH, 0); }
		public TerminalNode WITHIN() { return getToken(PsqlParser.WITHIN, 0); }
		public TerminalNode WITHOUT() { return getToken(PsqlParser.WITHOUT, 0); }
		public TerminalNode WORK() { return getToken(PsqlParser.WORK, 0); }
		public TerminalNode WRITE() { return getToken(PsqlParser.WRITE, 0); }
		public TerminalNode XMLAGG() { return getToken(PsqlParser.XMLAGG, 0); }
		public TerminalNode XMLATTRIBUTES() { return getToken(PsqlParser.XMLATTRIBUTES, 0); }
		public TerminalNode XMLBINARY() { return getToken(PsqlParser.XMLBINARY, 0); }
		public TerminalNode XMLCAST() { return getToken(PsqlParser.XMLCAST, 0); }
		public TerminalNode XMLCOMMENT() { return getToken(PsqlParser.XMLCOMMENT, 0); }
		public TerminalNode XMLCONCAT() { return getToken(PsqlParser.XMLCONCAT, 0); }
		public TerminalNode XMLDOCUMENT() { return getToken(PsqlParser.XMLDOCUMENT, 0); }
		public TerminalNode XMLELEMENT() { return getToken(PsqlParser.XMLELEMENT, 0); }
		public TerminalNode XMLEXISTS() { return getToken(PsqlParser.XMLEXISTS, 0); }
		public TerminalNode XMLFOREST() { return getToken(PsqlParser.XMLFOREST, 0); }
		public TerminalNode XMLITERATE() { return getToken(PsqlParser.XMLITERATE, 0); }
		public TerminalNode XMLNAMESPACES() { return getToken(PsqlParser.XMLNAMESPACES, 0); }
		public TerminalNode XMLPARSE() { return getToken(PsqlParser.XMLPARSE, 0); }
		public TerminalNode XMLPI() { return getToken(PsqlParser.XMLPI, 0); }
		public TerminalNode XMLQUERY() { return getToken(PsqlParser.XMLQUERY, 0); }
		public TerminalNode XMLSERIALIZE() { return getToken(PsqlParser.XMLSERIALIZE, 0); }
		public TerminalNode XMLTABLE() { return getToken(PsqlParser.XMLTABLE, 0); }
		public TerminalNode XMLTEXT() { return getToken(PsqlParser.XMLTEXT, 0); }
		public TerminalNode XMLVALIDATE() { return getToken(PsqlParser.XMLVALIDATE, 0); }
		public TerminalNode YEAR() { return getToken(PsqlParser.YEAR, 0); }
		public TerminalNode ZONE() { return getToken(PsqlParser.ZONE, 0); }
		public TerminalNode STAR() { return getToken(PsqlParser.STAR, 0); }
		public TerminalNode UNICODE() { return getToken(PsqlParser.UNICODE, 0); }
		public Reserved_keywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reserved_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterReserved_keyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitReserved_keyword(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitReserved_keyword(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Reserved_keywordContext reserved_keyword() throws RecognitionException {
		Reserved_keywordContext _localctx = new Reserved_keywordContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_reserved_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			_la = _input.LA(1);
			if ( !(_la==STAR || _la==UNICODE || ((((_la - 86)) & ~0x3f) == 0 && ((1L << (_la - 86)) & -7334831556961848985L) != 0) || ((((_la - 150)) & ~0x3f) == 0 && ((1L << (_la - 150)) & -6795782197441166465L) != 0) || ((((_la - 216)) & ~0x3f) == 0 && ((1L << (_la - 216)) & -5023501903657443459L) != 0) || ((((_la - 280)) & ~0x3f) == 0 && ((1L << (_la - 280)) & 6277519381901082577L) != 0) || ((((_la - 346)) & ~0x3f) == 0 && ((1L << (_la - 346)) & 2333170475160138075L) != 0) || ((((_la - 410)) & ~0x3f) == 0 && ((1L << (_la - 410)) & -6256064945499840261L) != 0) || ((((_la - 474)) & ~0x3f) == 0 && ((1L << (_la - 474)) & 2024905946608602819L) != 0) || ((((_la - 541)) & ~0x3f) == 0 && ((1L << (_la - 541)) & -4584619963138086889L) != 0) || ((((_la - 605)) & ~0x3f) == 0 && ((1L << (_la - 605)) & 8069258413262758175L) != 0) || ((((_la - 677)) & ~0x3f) == 0 && ((1L << (_la - 677)) & -2819097363625789689L) != 0) || ((((_la - 741)) & ~0x3f) == 0 && ((1L << (_la - 741)) & 5993076830986566547L) != 0) || ((((_la - 808)) & ~0x3f) == 0 && ((1L << (_la - 808)) & -2296358561632882585L) != 0) || ((((_la - 872)) & ~0x3f) == 0 && ((1L << (_la - 872)) & 837825691874109L) != 0)) ) {
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
	public static class Non_reserved_keywordContext extends ParserRuleContext {
		public TerminalNode A_KW() { return getToken(PsqlParser.A_KW, 0); }
		public TerminalNode ABORT() { return getToken(PsqlParser.ABORT, 0); }
		public TerminalNode ACCESS() { return getToken(PsqlParser.ACCESS, 0); }
		public TerminalNode ACCORDING() { return getToken(PsqlParser.ACCORDING, 0); }
		public TerminalNode ADA() { return getToken(PsqlParser.ADA, 0); }
		public TerminalNode ADMIN() { return getToken(PsqlParser.ADMIN, 0); }
		public TerminalNode AFTER() { return getToken(PsqlParser.AFTER, 0); }
		public TerminalNode AGGREGATE() { return getToken(PsqlParser.AGGREGATE, 0); }
		public TerminalNode ALSO() { return getToken(PsqlParser.ALSO, 0); }
		public TerminalNode ALWAYS() { return getToken(PsqlParser.ALWAYS, 0); }
		public TerminalNode ASSIGNMENT() { return getToken(PsqlParser.ASSIGNMENT, 0); }
		public TerminalNode ATTACH() { return getToken(PsqlParser.ATTACH, 0); }
		public TerminalNode ATTRIBUTE() { return getToken(PsqlParser.ATTRIBUTE, 0); }
		public TerminalNode ATTRIBUTES() { return getToken(PsqlParser.ATTRIBUTES, 0); }
		public TerminalNode BACKWARD() { return getToken(PsqlParser.BACKWARD, 0); }
		public TerminalNode BASE64() { return getToken(PsqlParser.BASE64, 0); }
		public TerminalNode BEFORE() { return getToken(PsqlParser.BEFORE, 0); }
		public TerminalNode BERNOULLI() { return getToken(PsqlParser.BERNOULLI, 0); }
		public TerminalNode BLOCKED() { return getToken(PsqlParser.BLOCKED, 0); }
		public TerminalNode BOM() { return getToken(PsqlParser.BOM, 0); }
		public TerminalNode BREADTH() { return getToken(PsqlParser.BREADTH, 0); }
		public TerminalNode C_KW() { return getToken(PsqlParser.C_KW, 0); }
		public TerminalNode CACHE() { return getToken(PsqlParser.CACHE, 0); }
		public TerminalNode CATALOG_NAME() { return getToken(PsqlParser.CATALOG_NAME, 0); }
		public TerminalNode CHAIN() { return getToken(PsqlParser.CHAIN, 0); }
		public TerminalNode CHAINING() { return getToken(PsqlParser.CHAINING, 0); }
		public TerminalNode CHARACTERISTICS() { return getToken(PsqlParser.CHARACTERISTICS, 0); }
		public TerminalNode CHARACTERS() { return getToken(PsqlParser.CHARACTERS, 0); }
		public TerminalNode CHARACTER_SET_CATALOG() { return getToken(PsqlParser.CHARACTER_SET_CATALOG, 0); }
		public TerminalNode CHARACTER_SET_NAME() { return getToken(PsqlParser.CHARACTER_SET_NAME, 0); }
		public TerminalNode CHARACTER_SET_SCHEMA() { return getToken(PsqlParser.CHARACTER_SET_SCHEMA, 0); }
		public TerminalNode CHECKPOINT() { return getToken(PsqlParser.CHECKPOINT, 0); }
		public TerminalNode CLASS() { return getToken(PsqlParser.CLASS, 0); }
		public TerminalNode CLASS_ORIGIN() { return getToken(PsqlParser.CLASS_ORIGIN, 0); }
		public TerminalNode CLUSTER() { return getToken(PsqlParser.CLUSTER, 0); }
		public TerminalNode COBOL() { return getToken(PsqlParser.COBOL, 0); }
		public TerminalNode COLLATION_CATALOG() { return getToken(PsqlParser.COLLATION_CATALOG, 0); }
		public TerminalNode COLLATION_NAME() { return getToken(PsqlParser.COLLATION_NAME, 0); }
		public TerminalNode COLLATION_SCHEMA() { return getToken(PsqlParser.COLLATION_SCHEMA, 0); }
		public TerminalNode COLUMNS() { return getToken(PsqlParser.COLUMNS, 0); }
		public TerminalNode COLUMN_NAME() { return getToken(PsqlParser.COLUMN_NAME, 0); }
		public TerminalNode COMMAND_FUNCTION() { return getToken(PsqlParser.COMMAND_FUNCTION, 0); }
		public TerminalNode COMMAND_FUNCTION_CODE() { return getToken(PsqlParser.COMMAND_FUNCTION_CODE, 0); }
		public TerminalNode COMMENT() { return getToken(PsqlParser.COMMENT, 0); }
		public TerminalNode COMMENTS() { return getToken(PsqlParser.COMMENTS, 0); }
		public TerminalNode COMMITTED() { return getToken(PsqlParser.COMMITTED, 0); }
		public TerminalNode COMPRESSION() { return getToken(PsqlParser.COMPRESSION, 0); }
		public TerminalNode CONCURRENTLY() { return getToken(PsqlParser.CONCURRENTLY, 0); }
		public TerminalNode CONDITIONAL() { return getToken(PsqlParser.CONDITIONAL, 0); }
		public TerminalNode CONDITION_NUMBER() { return getToken(PsqlParser.CONDITION_NUMBER, 0); }
		public TerminalNode CONFIGURATION() { return getToken(PsqlParser.CONFIGURATION, 0); }
		public TerminalNode CONFLICT() { return getToken(PsqlParser.CONFLICT, 0); }
		public TerminalNode CONNECTION_NAME() { return getToken(PsqlParser.CONNECTION_NAME, 0); }
		public TerminalNode CONSTRAINT_CATALOG() { return getToken(PsqlParser.CONSTRAINT_CATALOG, 0); }
		public TerminalNode CONSTRAINT_NAME() { return getToken(PsqlParser.CONSTRAINT_NAME, 0); }
		public TerminalNode CONSTRAINT_SCHEMA() { return getToken(PsqlParser.CONSTRAINT_SCHEMA, 0); }
		public TerminalNode CONSTRUCTOR() { return getToken(PsqlParser.CONSTRUCTOR, 0); }
		public TerminalNode CONTENT() { return getToken(PsqlParser.CONTENT, 0); }
		public TerminalNode CONTROL() { return getToken(PsqlParser.CONTROL, 0); }
		public TerminalNode CONVERSION() { return getToken(PsqlParser.CONVERSION, 0); }
		public TerminalNode COPARTITION() { return getToken(PsqlParser.COPARTITION, 0); }
		public TerminalNode COST() { return getToken(PsqlParser.COST, 0); }
		public TerminalNode CSV() { return getToken(PsqlParser.CSV, 0); }
		public TerminalNode CURSOR_NAME() { return getToken(PsqlParser.CURSOR_NAME, 0); }
		public TerminalNode DATA() { return getToken(PsqlParser.DATA, 0); }
		public TerminalNode DATABASE() { return getToken(PsqlParser.DATABASE, 0); }
		public TerminalNode DATETIME_INTERVAL_CODE() { return getToken(PsqlParser.DATETIME_INTERVAL_CODE, 0); }
		public TerminalNode DATETIME_INTERVAL_PRECISION() { return getToken(PsqlParser.DATETIME_INTERVAL_PRECISION, 0); }
		public TerminalNode DB() { return getToken(PsqlParser.DB, 0); }
		public TerminalNode DEFAULTS() { return getToken(PsqlParser.DEFAULTS, 0); }
		public TerminalNode DEFINED() { return getToken(PsqlParser.DEFINED, 0); }
		public TerminalNode DEFINER() { return getToken(PsqlParser.DEFINER, 0); }
		public TerminalNode DEGREE() { return getToken(PsqlParser.DEGREE, 0); }
		public TerminalNode DELIMITER() { return getToken(PsqlParser.DELIMITER, 0); }
		public TerminalNode DELIMITERS() { return getToken(PsqlParser.DELIMITERS, 0); }
		public TerminalNode DEPENDS() { return getToken(PsqlParser.DEPENDS, 0); }
		public TerminalNode DEPTH() { return getToken(PsqlParser.DEPTH, 0); }
		public TerminalNode DERIVED() { return getToken(PsqlParser.DERIVED, 0); }
		public TerminalNode DETACH() { return getToken(PsqlParser.DETACH, 0); }
		public TerminalNode DICTIONARY() { return getToken(PsqlParser.DICTIONARY, 0); }
		public TerminalNode DISABLE() { return getToken(PsqlParser.DISABLE, 0); }
		public TerminalNode DISCARD() { return getToken(PsqlParser.DISCARD, 0); }
		public TerminalNode DISPATCH() { return getToken(PsqlParser.DISPATCH, 0); }
		public TerminalNode DOCUMENT() { return getToken(PsqlParser.DOCUMENT, 0); }
		public TerminalNode DYNAMIC_FUNCTION() { return getToken(PsqlParser.DYNAMIC_FUNCTION, 0); }
		public TerminalNode DYNAMIC_FUNCTION_CODE() { return getToken(PsqlParser.DYNAMIC_FUNCTION_CODE, 0); }
		public TerminalNode ENABLE() { return getToken(PsqlParser.ENABLE, 0); }
		public TerminalNode ENCODING() { return getToken(PsqlParser.ENCODING, 0); }
		public TerminalNode ENCRYPTED() { return getToken(PsqlParser.ENCRYPTED, 0); }
		public TerminalNode ENFORCED() { return getToken(PsqlParser.ENFORCED, 0); }
		public TerminalNode ENUM() { return getToken(PsqlParser.ENUM, 0); }
		public TerminalNode ERROR() { return getToken(PsqlParser.ERROR, 0); }
		public TerminalNode EVENT() { return getToken(PsqlParser.EVENT, 0); }
		public TerminalNode EXCLUDE() { return getToken(PsqlParser.EXCLUDE, 0); }
		public TerminalNode EXCLUDING() { return getToken(PsqlParser.EXCLUDING, 0); }
		public TerminalNode EXCLUSIVE() { return getToken(PsqlParser.EXCLUSIVE, 0); }
		public TerminalNode EXPLAIN() { return getToken(PsqlParser.EXPLAIN, 0); }
		public TerminalNode EXPRESSION() { return getToken(PsqlParser.EXPRESSION, 0); }
		public TerminalNode EXTENSION() { return getToken(PsqlParser.EXTENSION, 0); }
		public TerminalNode FAMILY() { return getToken(PsqlParser.FAMILY, 0); }
		public TerminalNode FILE() { return getToken(PsqlParser.FILE, 0); }
		public TerminalNode FINAL() { return getToken(PsqlParser.FINAL, 0); }
		public TerminalNode FINALIZE() { return getToken(PsqlParser.FINALIZE, 0); }
		public TerminalNode FINISH() { return getToken(PsqlParser.FINISH, 0); }
		public TerminalNode FLAG() { return getToken(PsqlParser.FLAG, 0); }
		public TerminalNode FOLLOWING() { return getToken(PsqlParser.FOLLOWING, 0); }
		public TerminalNode FORCE() { return getToken(PsqlParser.FORCE, 0); }
		public TerminalNode FORMAT() { return getToken(PsqlParser.FORMAT, 0); }
		public TerminalNode FORTRAN() { return getToken(PsqlParser.FORTRAN, 0); }
		public TerminalNode FORWARD() { return getToken(PsqlParser.FORWARD, 0); }
		public TerminalNode FREEZE() { return getToken(PsqlParser.FREEZE, 0); }
		public TerminalNode FS() { return getToken(PsqlParser.FS, 0); }
		public TerminalNode FULFILL() { return getToken(PsqlParser.FULFILL, 0); }
		public TerminalNode FUNCTIONS() { return getToken(PsqlParser.FUNCTIONS, 0); }
		public TerminalNode G_KW() { return getToken(PsqlParser.G_KW, 0); }
		public TerminalNode GENERAL() { return getToken(PsqlParser.GENERAL, 0); }
		public TerminalNode GENERATED() { return getToken(PsqlParser.GENERATED, 0); }
		public TerminalNode GRANTED() { return getToken(PsqlParser.GRANTED, 0); }
		public TerminalNode HANDLER() { return getToken(PsqlParser.HANDLER, 0); }
		public TerminalNode HEADER() { return getToken(PsqlParser.HEADER, 0); }
		public TerminalNode HEX() { return getToken(PsqlParser.HEX, 0); }
		public TerminalNode HIERARCHY() { return getToken(PsqlParser.HIERARCHY, 0); }
		public TerminalNode ID() { return getToken(PsqlParser.ID, 0); }
		public TerminalNode IF() { return getToken(PsqlParser.IF, 0); }
		public TerminalNode IGNORE() { return getToken(PsqlParser.IGNORE, 0); }
		public TerminalNode IMMEDIATELY() { return getToken(PsqlParser.IMMEDIATELY, 0); }
		public TerminalNode IMMUTABLE() { return getToken(PsqlParser.IMMUTABLE, 0); }
		public TerminalNode IMPLEMENTATION() { return getToken(PsqlParser.IMPLEMENTATION, 0); }
		public TerminalNode IMPLICIT() { return getToken(PsqlParser.IMPLICIT, 0); }
		public TerminalNode INCLUDE() { return getToken(PsqlParser.INCLUDE, 0); }
		public TerminalNode INCLUDING() { return getToken(PsqlParser.INCLUDING, 0); }
		public TerminalNode INCREMENT() { return getToken(PsqlParser.INCREMENT, 0); }
		public TerminalNode INDENT() { return getToken(PsqlParser.INDENT, 0); }
		public TerminalNode INDEX() { return getToken(PsqlParser.INDEX, 0); }
		public TerminalNode INDEXES() { return getToken(PsqlParser.INDEXES, 0); }
		public TerminalNode INHERIT() { return getToken(PsqlParser.INHERIT, 0); }
		public TerminalNode INHERITS() { return getToken(PsqlParser.INHERITS, 0); }
		public TerminalNode INLINE() { return getToken(PsqlParser.INLINE, 0); }
		public TerminalNode INSTANCE() { return getToken(PsqlParser.INSTANCE, 0); }
		public TerminalNode INSTANTIABLE() { return getToken(PsqlParser.INSTANTIABLE, 0); }
		public TerminalNode INSTEAD() { return getToken(PsqlParser.INSTEAD, 0); }
		public TerminalNode INTEGRITY() { return getToken(PsqlParser.INTEGRITY, 0); }
		public TerminalNode INVOKER() { return getToken(PsqlParser.INVOKER, 0); }
		public TerminalNode ISNULL() { return getToken(PsqlParser.ISNULL, 0); }
		public TerminalNode K_KW() { return getToken(PsqlParser.K_KW, 0); }
		public TerminalNode KEEP() { return getToken(PsqlParser.KEEP, 0); }
		public TerminalNode KEYS() { return getToken(PsqlParser.KEYS, 0); }
		public TerminalNode KEY_MEMBER() { return getToken(PsqlParser.KEY_MEMBER, 0); }
		public TerminalNode KEY_TYPE() { return getToken(PsqlParser.KEY_TYPE, 0); }
		public TerminalNode LABEL() { return getToken(PsqlParser.LABEL, 0); }
		public TerminalNode LEAKPROOF() { return getToken(PsqlParser.LEAKPROOF, 0); }
		public TerminalNode LENGTH() { return getToken(PsqlParser.LENGTH, 0); }
		public TerminalNode LIBRARY() { return getToken(PsqlParser.LIBRARY, 0); }
		public TerminalNode LIMIT() { return getToken(PsqlParser.LIMIT, 0); }
		public TerminalNode LINK() { return getToken(PsqlParser.LINK, 0); }
		public TerminalNode LISTEN() { return getToken(PsqlParser.LISTEN, 0); }
		public TerminalNode LOAD() { return getToken(PsqlParser.LOAD, 0); }
		public TerminalNode LOCATION() { return getToken(PsqlParser.LOCATION, 0); }
		public TerminalNode LOCATOR() { return getToken(PsqlParser.LOCATOR, 0); }
		public TerminalNode LOCK() { return getToken(PsqlParser.LOCK, 0); }
		public TerminalNode LOCKED() { return getToken(PsqlParser.LOCKED, 0); }
		public TerminalNode LOGGED() { return getToken(PsqlParser.LOGGED, 0); }
		public TerminalNode M_KW() { return getToken(PsqlParser.M_KW, 0); }
		public TerminalNode MAP() { return getToken(PsqlParser.MAP, 0); }
		public TerminalNode MAPPING() { return getToken(PsqlParser.MAPPING, 0); }
		public TerminalNode MATCHED() { return getToken(PsqlParser.MATCHED, 0); }
		public TerminalNode MATERIALIZED() { return getToken(PsqlParser.MATERIALIZED, 0); }
		public TerminalNode MAXVALUE() { return getToken(PsqlParser.MAXVALUE, 0); }
		public TerminalNode MEASURES() { return getToken(PsqlParser.MEASURES, 0); }
		public TerminalNode MESSAGE_LENGTH() { return getToken(PsqlParser.MESSAGE_LENGTH, 0); }
		public TerminalNode MESSAGE_OCTET_LENGTH() { return getToken(PsqlParser.MESSAGE_OCTET_LENGTH, 0); }
		public TerminalNode MESSAGE_TEXT() { return getToken(PsqlParser.MESSAGE_TEXT, 0); }
		public TerminalNode MINVALUE() { return getToken(PsqlParser.MINVALUE, 0); }
		public TerminalNode MODE() { return getToken(PsqlParser.MODE, 0); }
		public TerminalNode MORE_KW() { return getToken(PsqlParser.MORE_KW, 0); }
		public TerminalNode MOVE() { return getToken(PsqlParser.MOVE, 0); }
		public TerminalNode MUMPS() { return getToken(PsqlParser.MUMPS, 0); }
		public TerminalNode NAME() { return getToken(PsqlParser.NAME, 0); }
		public TerminalNode NAMESPACE() { return getToken(PsqlParser.NAMESPACE, 0); }
		public TerminalNode NESTED() { return getToken(PsqlParser.NESTED, 0); }
		public TerminalNode NESTING() { return getToken(PsqlParser.NESTING, 0); }
		public TerminalNode NFC() { return getToken(PsqlParser.NFC, 0); }
		public TerminalNode NFD() { return getToken(PsqlParser.NFD, 0); }
		public TerminalNode NFKC() { return getToken(PsqlParser.NFKC, 0); }
		public TerminalNode NFKD() { return getToken(PsqlParser.NFKD, 0); }
		public TerminalNode NIL() { return getToken(PsqlParser.NIL, 0); }
		public TerminalNode NORMALIZED() { return getToken(PsqlParser.NORMALIZED, 0); }
		public TerminalNode NOTHING() { return getToken(PsqlParser.NOTHING, 0); }
		public TerminalNode NOTIFY() { return getToken(PsqlParser.NOTIFY, 0); }
		public TerminalNode NOTNULL() { return getToken(PsqlParser.NOTNULL, 0); }
		public TerminalNode NOWAIT() { return getToken(PsqlParser.NOWAIT, 0); }
		public TerminalNode NULLABLE() { return getToken(PsqlParser.NULLABLE, 0); }
		public TerminalNode NULLS() { return getToken(PsqlParser.NULLS, 0); }
		public TerminalNode NULL_ORDERING() { return getToken(PsqlParser.NULL_ORDERING, 0); }
		public TerminalNode NUMBER() { return getToken(PsqlParser.NUMBER, 0); }
		public TerminalNode OBJECT() { return getToken(PsqlParser.OBJECT, 0); }
		public TerminalNode OCCURRENCE() { return getToken(PsqlParser.OCCURRENCE, 0); }
		public TerminalNode OCTETS() { return getToken(PsqlParser.OCTETS, 0); }
		public TerminalNode OFF() { return getToken(PsqlParser.OFF, 0); }
		public TerminalNode OIDS() { return getToken(PsqlParser.OIDS, 0); }
		public TerminalNode OPERATOR_KW() { return getToken(PsqlParser.OPERATOR_KW, 0); }
		public TerminalNode OPTIONS() { return getToken(PsqlParser.OPTIONS, 0); }
		public TerminalNode ORDERING() { return getToken(PsqlParser.ORDERING, 0); }
		public TerminalNode ORDINALITY() { return getToken(PsqlParser.ORDINALITY, 0); }
		public TerminalNode OTHERS() { return getToken(PsqlParser.OTHERS, 0); }
		public TerminalNode OVERFLOW() { return getToken(PsqlParser.OVERFLOW, 0); }
		public TerminalNode OVERRIDING() { return getToken(PsqlParser.OVERRIDING, 0); }
		public TerminalNode OWNED() { return getToken(PsqlParser.OWNED, 0); }
		public TerminalNode OWNER() { return getToken(PsqlParser.OWNER, 0); }
		public TerminalNode P_KW() { return getToken(PsqlParser.P_KW, 0); }
		public TerminalNode PARALLEL() { return getToken(PsqlParser.PARALLEL, 0); }
		public TerminalNode PARAMETER_MODE() { return getToken(PsqlParser.PARAMETER_MODE, 0); }
		public TerminalNode PARAMETER_NAME() { return getToken(PsqlParser.PARAMETER_NAME, 0); }
		public TerminalNode PARAMETER_ORDINAL_POSITION() { return getToken(PsqlParser.PARAMETER_ORDINAL_POSITION, 0); }
		public TerminalNode PARAMETER_SPECIFIC_CATALOG() { return getToken(PsqlParser.PARAMETER_SPECIFIC_CATALOG, 0); }
		public TerminalNode PARAMETER_SPECIFIC_NAME() { return getToken(PsqlParser.PARAMETER_SPECIFIC_NAME, 0); }
		public TerminalNode PARAMETER_SPECIFIC_SCHEMA() { return getToken(PsqlParser.PARAMETER_SPECIFIC_SCHEMA, 0); }
		public TerminalNode PARSER() { return getToken(PsqlParser.PARSER, 0); }
		public TerminalNode PASCAL() { return getToken(PsqlParser.PASCAL, 0); }
		public TerminalNode PASS() { return getToken(PsqlParser.PASS, 0); }
		public TerminalNode PASSING() { return getToken(PsqlParser.PASSING, 0); }
		public TerminalNode PASSTHROUGH() { return getToken(PsqlParser.PASSTHROUGH, 0); }
		public TerminalNode PASSWORD() { return getToken(PsqlParser.PASSWORD, 0); }
		public TerminalNode PAST() { return getToken(PsqlParser.PAST, 0); }
		public TerminalNode PERMISSION() { return getToken(PsqlParser.PERMISSION, 0); }
		public TerminalNode PERMUTE() { return getToken(PsqlParser.PERMUTE, 0); }
		public TerminalNode PIPE() { return getToken(PsqlParser.PIPE, 0); }
		public TerminalNode PLAN() { return getToken(PsqlParser.PLAN, 0); }
		public TerminalNode PLANS() { return getToken(PsqlParser.PLANS, 0); }
		public TerminalNode PLI() { return getToken(PsqlParser.PLI, 0); }
		public TerminalNode POLICY() { return getToken(PsqlParser.POLICY, 0); }
		public TerminalNode PRECEDING() { return getToken(PsqlParser.PRECEDING, 0); }
		public TerminalNode PREPARED() { return getToken(PsqlParser.PREPARED, 0); }
		public TerminalNode PREV() { return getToken(PsqlParser.PREV, 0); }
		public TerminalNode PRIVATE() { return getToken(PsqlParser.PRIVATE, 0); }
		public TerminalNode PROCEDURAL() { return getToken(PsqlParser.PROCEDURAL, 0); }
		public TerminalNode PROCEDURES() { return getToken(PsqlParser.PROCEDURES, 0); }
		public TerminalNode PROGRAM() { return getToken(PsqlParser.PROGRAM, 0); }
		public TerminalNode PRUNE() { return getToken(PsqlParser.PRUNE, 0); }
		public TerminalNode PUBLICATION() { return getToken(PsqlParser.PUBLICATION, 0); }
		public TerminalNode QUOTE() { return getToken(PsqlParser.QUOTE, 0); }
		public TerminalNode QUOTES() { return getToken(PsqlParser.QUOTES, 0); }
		public TerminalNode REASSIGN() { return getToken(PsqlParser.REASSIGN, 0); }
		public TerminalNode RECHECK() { return getToken(PsqlParser.RECHECK, 0); }
		public TerminalNode RECOVERY() { return getToken(PsqlParser.RECOVERY, 0); }
		public TerminalNode REFRESH() { return getToken(PsqlParser.REFRESH, 0); }
		public TerminalNode REINDEX() { return getToken(PsqlParser.REINDEX, 0); }
		public TerminalNode RENAME() { return getToken(PsqlParser.RENAME, 0); }
		public TerminalNode REPEATABLE() { return getToken(PsqlParser.REPEATABLE, 0); }
		public TerminalNode REPLACE() { return getToken(PsqlParser.REPLACE, 0); }
		public TerminalNode REPLICA() { return getToken(PsqlParser.REPLICA, 0); }
		public TerminalNode REQUIRING() { return getToken(PsqlParser.REQUIRING, 0); }
		public TerminalNode RESET() { return getToken(PsqlParser.RESET, 0); }
		public TerminalNode RESPECT() { return getToken(PsqlParser.RESPECT, 0); }
		public TerminalNode RESTART() { return getToken(PsqlParser.RESTART, 0); }
		public TerminalNode RESTORE() { return getToken(PsqlParser.RESTORE, 0); }
		public TerminalNode RETURNED_CARDINALITY() { return getToken(PsqlParser.RETURNED_CARDINALITY, 0); }
		public TerminalNode RETURNED_LENGTH() { return getToken(PsqlParser.RETURNED_LENGTH, 0); }
		public TerminalNode RETURNED_OCTET_LENGTH() { return getToken(PsqlParser.RETURNED_OCTET_LENGTH, 0); }
		public TerminalNode RETURNED_SQLSTATE() { return getToken(PsqlParser.RETURNED_SQLSTATE, 0); }
		public TerminalNode RETURNING() { return getToken(PsqlParser.RETURNING, 0); }
		public TerminalNode ROLE() { return getToken(PsqlParser.ROLE, 0); }
		public TerminalNode ROUTINE() { return getToken(PsqlParser.ROUTINE, 0); }
		public TerminalNode ROUTINES() { return getToken(PsqlParser.ROUTINES, 0); }
		public TerminalNode ROUTINE_CATALOG() { return getToken(PsqlParser.ROUTINE_CATALOG, 0); }
		public TerminalNode ROUTINE_NAME() { return getToken(PsqlParser.ROUTINE_NAME, 0); }
		public TerminalNode ROUTINE_SCHEMA() { return getToken(PsqlParser.ROUTINE_SCHEMA, 0); }
		public TerminalNode ROW_COUNT() { return getToken(PsqlParser.ROW_COUNT, 0); }
		public TerminalNode RULE() { return getToken(PsqlParser.RULE, 0); }
		public TerminalNode SCALAR() { return getToken(PsqlParser.SCALAR, 0); }
		public TerminalNode SCALE() { return getToken(PsqlParser.SCALE, 0); }
		public TerminalNode SCHEMAS() { return getToken(PsqlParser.SCHEMAS, 0); }
		public TerminalNode SCHEMA_NAME() { return getToken(PsqlParser.SCHEMA_NAME, 0); }
		public TerminalNode SCOPE_CATALOG() { return getToken(PsqlParser.SCOPE_CATALOG, 0); }
		public TerminalNode SCOPE_NAME() { return getToken(PsqlParser.SCOPE_NAME, 0); }
		public TerminalNode SCOPE_SCHEMA() { return getToken(PsqlParser.SCOPE_SCHEMA, 0); }
		public TerminalNode SECURITY() { return getToken(PsqlParser.SECURITY, 0); }
		public TerminalNode SELECTIVE() { return getToken(PsqlParser.SELECTIVE, 0); }
		public TerminalNode SELF() { return getToken(PsqlParser.SELF, 0); }
		public TerminalNode SEMANTICS() { return getToken(PsqlParser.SEMANTICS, 0); }
		public TerminalNode SEQUENCE() { return getToken(PsqlParser.SEQUENCE, 0); }
		public TerminalNode SEQUENCES() { return getToken(PsqlParser.SEQUENCES, 0); }
		public TerminalNode SERIALIZABLE() { return getToken(PsqlParser.SERIALIZABLE, 0); }
		public TerminalNode SERVER() { return getToken(PsqlParser.SERVER, 0); }
		public TerminalNode SERVER_NAME() { return getToken(PsqlParser.SERVER_NAME, 0); }
		public TerminalNode SETOF() { return getToken(PsqlParser.SETOF, 0); }
		public TerminalNode SETS() { return getToken(PsqlParser.SETS, 0); }
		public TerminalNode SHARE() { return getToken(PsqlParser.SHARE, 0); }
		public TerminalNode SIMPLE() { return getToken(PsqlParser.SIMPLE, 0); }
		public TerminalNode SNAPSHOT() { return getToken(PsqlParser.SNAPSHOT, 0); }
		public TerminalNode SORT_DIRECTION() { return getToken(PsqlParser.SORT_DIRECTION, 0); }
		public TerminalNode SOURCE() { return getToken(PsqlParser.SOURCE, 0); }
		public TerminalNode SPECIFIC_NAME() { return getToken(PsqlParser.SPECIFIC_NAME, 0); }
		public TerminalNode STABLE() { return getToken(PsqlParser.STABLE, 0); }
		public TerminalNode STANDALONE() { return getToken(PsqlParser.STANDALONE, 0); }
		public TerminalNode STATE() { return getToken(PsqlParser.STATE, 0); }
		public TerminalNode STATEMENT() { return getToken(PsqlParser.STATEMENT, 0); }
		public TerminalNode STATISTICS() { return getToken(PsqlParser.STATISTICS, 0); }
		public TerminalNode STDIN() { return getToken(PsqlParser.STDIN, 0); }
		public TerminalNode STDOUT() { return getToken(PsqlParser.STDOUT, 0); }
		public TerminalNode STORAGE() { return getToken(PsqlParser.STORAGE, 0); }
		public TerminalNode STORED() { return getToken(PsqlParser.STORED, 0); }
		public TerminalNode STRICT() { return getToken(PsqlParser.STRICT, 0); }
		public TerminalNode STRING() { return getToken(PsqlParser.STRING, 0); }
		public TerminalNode STRIP() { return getToken(PsqlParser.STRIP, 0); }
		public TerminalNode STRUCTURE() { return getToken(PsqlParser.STRUCTURE, 0); }
		public TerminalNode STYLE() { return getToken(PsqlParser.STYLE, 0); }
		public TerminalNode SUBCLASS_ORIGIN() { return getToken(PsqlParser.SUBCLASS_ORIGIN, 0); }
		public TerminalNode SUBSCRIPTION() { return getToken(PsqlParser.SUBSCRIPTION, 0); }
		public TerminalNode SUPPORT() { return getToken(PsqlParser.SUPPORT, 0); }
		public TerminalNode SYSID() { return getToken(PsqlParser.SYSID, 0); }
		public TerminalNode T_KW() { return getToken(PsqlParser.T_KW, 0); }
		public TerminalNode TABLES() { return getToken(PsqlParser.TABLES, 0); }
		public TerminalNode TABLESPACE() { return getToken(PsqlParser.TABLESPACE, 0); }
		public TerminalNode TABLE_NAME() { return getToken(PsqlParser.TABLE_NAME, 0); }
		public TerminalNode TEMP() { return getToken(PsqlParser.TEMP, 0); }
		public TerminalNode TEMPLATE() { return getToken(PsqlParser.TEMPLATE, 0); }
		public TerminalNode THROUGH() { return getToken(PsqlParser.THROUGH, 0); }
		public TerminalNode TIES() { return getToken(PsqlParser.TIES, 0); }
		public TerminalNode TOKEN() { return getToken(PsqlParser.TOKEN, 0); }
		public TerminalNode TOP_LEVEL_COUNT() { return getToken(PsqlParser.TOP_LEVEL_COUNT, 0); }
		public TerminalNode TRANSACTIONS_COMMITTED() { return getToken(PsqlParser.TRANSACTIONS_COMMITTED, 0); }
		public TerminalNode TRANSACTIONS_ROLLED_BACK() { return getToken(PsqlParser.TRANSACTIONS_ROLLED_BACK, 0); }
		public TerminalNode TRANSACTION_ACTIVE() { return getToken(PsqlParser.TRANSACTION_ACTIVE, 0); }
		public TerminalNode TRANSFORM() { return getToken(PsqlParser.TRANSFORM, 0); }
		public TerminalNode TRANSFORMS() { return getToken(PsqlParser.TRANSFORMS, 0); }
		public TerminalNode TRIGGER_CATALOG() { return getToken(PsqlParser.TRIGGER_CATALOG, 0); }
		public TerminalNode TRIGGER_NAME() { return getToken(PsqlParser.TRIGGER_NAME, 0); }
		public TerminalNode TRIGGER_SCHEMA() { return getToken(PsqlParser.TRIGGER_SCHEMA, 0); }
		public TerminalNode TRUSTED() { return getToken(PsqlParser.TRUSTED, 0); }
		public TerminalNode TYPE() { return getToken(PsqlParser.TYPE, 0); }
		public TerminalNode TYPES() { return getToken(PsqlParser.TYPES, 0); }
		public TerminalNode UNBOUNDED() { return getToken(PsqlParser.UNBOUNDED, 0); }
		public TerminalNode UNCOMMITTED() { return getToken(PsqlParser.UNCOMMITTED, 0); }
		public TerminalNode UNCONDITIONAL() { return getToken(PsqlParser.UNCONDITIONAL, 0); }
		public TerminalNode UNDER() { return getToken(PsqlParser.UNDER, 0); }
		public TerminalNode UNENCRYPTED() { return getToken(PsqlParser.UNENCRYPTED, 0); }
		public TerminalNode UNLINK() { return getToken(PsqlParser.UNLINK, 0); }
		public TerminalNode UNLISTEN() { return getToken(PsqlParser.UNLISTEN, 0); }
		public TerminalNode UNLOGGED() { return getToken(PsqlParser.UNLOGGED, 0); }
		public TerminalNode UNMATCHED() { return getToken(PsqlParser.UNMATCHED, 0); }
		public TerminalNode UNNAMED() { return getToken(PsqlParser.UNNAMED, 0); }
		public TerminalNode UNTIL() { return getToken(PsqlParser.UNTIL, 0); }
		public TerminalNode UNTYPED() { return getToken(PsqlParser.UNTYPED, 0); }
		public TerminalNode URI() { return getToken(PsqlParser.URI, 0); }
		public TerminalNode USER_DEFINED_TYPE_CATALOG() { return getToken(PsqlParser.USER_DEFINED_TYPE_CATALOG, 0); }
		public TerminalNode USER_DEFINED_TYPE_CODE() { return getToken(PsqlParser.USER_DEFINED_TYPE_CODE, 0); }
		public TerminalNode USER_DEFINED_TYPE_NAME() { return getToken(PsqlParser.USER_DEFINED_TYPE_NAME, 0); }
		public TerminalNode USER_DEFINED_TYPE_SCHEMA() { return getToken(PsqlParser.USER_DEFINED_TYPE_SCHEMA, 0); }
		public TerminalNode UTF16() { return getToken(PsqlParser.UTF16, 0); }
		public TerminalNode UTF32() { return getToken(PsqlParser.UTF32, 0); }
		public TerminalNode UTF8() { return getToken(PsqlParser.UTF8, 0); }
		public TerminalNode VACUUM() { return getToken(PsqlParser.VACUUM, 0); }
		public TerminalNode VALID() { return getToken(PsqlParser.VALID, 0); }
		public TerminalNode VALIDATE() { return getToken(PsqlParser.VALIDATE, 0); }
		public TerminalNode VALIDATOR() { return getToken(PsqlParser.VALIDATOR, 0); }
		public TerminalNode VERBOSE() { return getToken(PsqlParser.VERBOSE, 0); }
		public TerminalNode VERSION() { return getToken(PsqlParser.VERSION, 0); }
		public TerminalNode VIEWS() { return getToken(PsqlParser.VIEWS, 0); }
		public TerminalNode VOLATILE() { return getToken(PsqlParser.VOLATILE, 0); }
		public TerminalNode WHITESPACE() { return getToken(PsqlParser.WHITESPACE, 0); }
		public TerminalNode WRAPPER() { return getToken(PsqlParser.WRAPPER, 0); }
		public TerminalNode XMLDECLARATION() { return getToken(PsqlParser.XMLDECLARATION, 0); }
		public TerminalNode XMLROOT() { return getToken(PsqlParser.XMLROOT, 0); }
		public TerminalNode XMLSCHEMA() { return getToken(PsqlParser.XMLSCHEMA, 0); }
		public TerminalNode YES() { return getToken(PsqlParser.YES, 0); }
		public Non_reserved_keywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_non_reserved_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterNon_reserved_keyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitNon_reserved_keyword(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitNon_reserved_keyword(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Non_reserved_keywordContext non_reserved_keyword() throws RecognitionException {
		Non_reserved_keywordContext _localctx = new Non_reserved_keywordContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_non_reserved_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			_la = _input.LA(1);
			if ( !(_la==QUOTE || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & -7853651294793942429L) != 0) || ((((_la - 148)) & ~0x3f) == 0 && ((1L << (_la - 148)) & 8736384716055097857L) != 0) || ((((_la - 212)) & ~0x3f) == 0 && ((1L << (_la - 212)) & 6589053888802981933L) != 0) || ((((_la - 278)) & ~0x3f) == 0 && ((1L << (_la - 278)) & -6663333453903167303L) != 0) || ((((_la - 343)) & ~0x3f) == 0 && ((1L << (_la - 343)) & -219745627478395609L) != 0) || ((((_la - 408)) & ~0x3f) == 0 && ((1L << (_la - 408)) & 6433400520197037075L) != 0) || ((((_la - 472)) & ~0x3f) == 0 && ((1L << (_la - 472)) & -8099623786434411279L) != 0) || ((((_la - 537)) & ~0x3f) == 0 && ((1L << (_la - 537)) & -433056886776303985L) != 0) || ((((_la - 601)) & ~0x3f) == 0 && ((1L << (_la - 601)) & 19002435506925057L) != 0) || ((((_la - 665)) & ~0x3f) == 0 && ((1L << (_la - 665)) & -638988730944745479L) != 0) || ((((_la - 729)) & ~0x3f) == 0 && ((1L << (_la - 729)) & 4973662386436604529L) != 0) || ((((_la - 793)) & ~0x3f) == 0 && ((1L << (_la - 793)) & 2808270925035477197L) != 0) || ((((_la - 857)) & ~0x3f) == 0 && ((1L << (_la - 857)) & -9007128747377160209L) != 0)) ) {
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
	public static class OperatorContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(PsqlParser.EQ, 0); }
		public TerminalNode LE() { return getToken(PsqlParser.LE, 0); }
		public TerminalNode GT() { return getToken(PsqlParser.GT, 0); }
		public TerminalNode LEQ() { return getToken(PsqlParser.LEQ, 0); }
		public TerminalNode GEQ() { return getToken(PsqlParser.GEQ, 0); }
		public TerminalNode NEQ1() { return getToken(PsqlParser.NEQ1, 0); }
		public TerminalNode NEQ2() { return getToken(PsqlParser.NEQ2, 0); }
		public TerminalNode LIKE() { return getToken(PsqlParser.LIKE, 0); }
		public TerminalNode ILIKE() { return getToken(PsqlParser.ILIKE, 0); }
		public TerminalNode AND() { return getToken(PsqlParser.AND, 0); }
		public TerminalNode OR() { return getToken(PsqlParser.OR, 0); }
		public TerminalNode NOT() { return getToken(PsqlParser.NOT, 0); }
		public TerminalNode STAR() { return getToken(PsqlParser.STAR, 0); }
		public TerminalNode PLUS() { return getToken(PsqlParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(PsqlParser.MINUS, 0); }
		public TerminalNode DIV() { return getToken(PsqlParser.DIV, 0); }
		public TerminalNode MOD_OP() { return getToken(PsqlParser.MOD_OP, 0); }
		public TerminalNode EXP_OP() { return getToken(PsqlParser.EXP_OP, 0); }
		public TerminalNode SQ_ROOT() { return getToken(PsqlParser.SQ_ROOT, 0); }
		public TerminalNode CUBE_ROOT() { return getToken(PsqlParser.CUBE_ROOT, 0); }
		public TerminalNode FACTORIAL() { return getToken(PsqlParser.FACTORIAL, 0); }
		public TerminalNode FACTORIAS() { return getToken(PsqlParser.FACTORIAS, 0); }
		public TerminalNode BITWISE_AND() { return getToken(PsqlParser.BITWISE_AND, 0); }
		public TerminalNode BITWISE_OR() { return getToken(PsqlParser.BITWISE_OR, 0); }
		public TerminalNode BITWISE_XOR() { return getToken(PsqlParser.BITWISE_XOR, 0); }
		public TerminalNode BITWISE_NOT() { return getToken(PsqlParser.BITWISE_NOT, 0); }
		public TerminalNode BITWISE_SHIFT_LEFT() { return getToken(PsqlParser.BITWISE_SHIFT_LEFT, 0); }
		public TerminalNode BITWISE_SHIFT_RIGHT() { return getToken(PsqlParser.BITWISE_SHIFT_RIGHT, 0); }
		public TerminalNode CONCAT() { return getToken(PsqlParser.CONCAT, 0); }
		public TerminalNode REGEX_CASE_INSENSITIVE_MATCH() { return getToken(PsqlParser.REGEX_CASE_INSENSITIVE_MATCH, 0); }
		public TerminalNode REGEX_CASE_INSENSITIVE_NOT_MATCH() { return getToken(PsqlParser.REGEX_CASE_INSENSITIVE_NOT_MATCH, 0); }
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 274877898784L) != 0) || _la==AND || _la==ILIKE || _la==LIKE || _la==NOT || _la==OR) ) {
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
	public static class ProjContext extends ParserRuleContext {
		public ProjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_proj; }
	 
		public ProjContext() { }
		public void copyFrom(ProjContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ProperProjContext extends ProjContext {
		public List<IdntContext> idnt() {
			return getRuleContexts(IdntContext.class);
		}
		public IdntContext idnt(int i) {
			return getRuleContext(IdntContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(PsqlParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(PsqlParser.DOT, i);
		}
		public ProperProjContext(ProjContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterProperProj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitProperProj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitProperProj(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MissingIdenProjContext extends ProjContext {
		public IdntContext idnt() {
			return getRuleContext(IdntContext.class,0);
		}
		public TerminalNode DOT() { return getToken(PsqlParser.DOT, 0); }
		public MissingIdenProjContext(ProjContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterMissingIdenProj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitMissingIdenProj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitMissingIdenProj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProjContext proj() throws RecognitionException {
		ProjContext _localctx = new ProjContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_proj);
		try {
			int _alt;
			setState(279);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				_localctx = new ProperProjContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(268);
				idnt();
				setState(271); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(269);
						match(DOT);
						setState(270);
						idnt();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(273); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				_localctx = new MissingIdenProjContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(275);
				idnt();
				setState(276);
				match(DOT);
				notifyErrorListeners("Missing identifier after '.'");
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
	public static class IdntContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(PsqlParser.WORD, 0); }
		public TerminalNode DOUBLE_QUOTED_STRING() { return getToken(PsqlParser.DOUBLE_QUOTED_STRING, 0); }
		public Non_reserved_keywordContext non_reserved_keyword() {
			return getRuleContext(Non_reserved_keywordContext.class,0);
		}
		public IdntContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idnt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterIdnt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitIdnt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitIdnt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdntContext idnt() throws RecognitionException {
		IdntContext _localctx = new IdntContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_idnt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WORD:
				{
				setState(281);
				match(WORD);
				}
				break;
			case DOUBLE_QUOTED_STRING:
				{
				setState(282);
				match(DOUBLE_QUOTED_STRING);
				}
				break;
			case QUOTE:
			case A_KW:
			case ABORT:
			case ACCESS:
			case ACCORDING:
			case ADA:
			case ADMIN:
			case AFTER:
			case AGGREGATE:
			case ALSO:
			case ALWAYS:
			case ASSIGNMENT:
			case ATTACH:
			case ATTRIBUTE:
			case ATTRIBUTES:
			case BACKWARD:
			case BASE64:
			case BEFORE:
			case BERNOULLI:
			case BLOCKED:
			case BOM:
			case BREADTH:
			case C_KW:
			case CACHE:
			case CATALOG_NAME:
			case CHAIN:
			case CHAINING:
			case CHARACTERISTICS:
			case CHARACTERS:
			case CHARACTER_SET_CATALOG:
			case CHARACTER_SET_NAME:
			case CHARACTER_SET_SCHEMA:
			case CHECKPOINT:
			case CLASS:
			case CLASS_ORIGIN:
			case CLUSTER:
			case COBOL:
			case COLLATION_CATALOG:
			case COLLATION_NAME:
			case COLLATION_SCHEMA:
			case COLUMNS:
			case COLUMN_NAME:
			case COMMAND_FUNCTION:
			case COMMAND_FUNCTION_CODE:
			case COMMENT:
			case COMMENTS:
			case COMMITTED:
			case COMPRESSION:
			case CONCURRENTLY:
			case CONDITIONAL:
			case CONDITION_NUMBER:
			case CONFIGURATION:
			case CONFLICT:
			case CONNECTION_NAME:
			case CONSTRAINT_CATALOG:
			case CONSTRAINT_NAME:
			case CONSTRAINT_SCHEMA:
			case CONSTRUCTOR:
			case CONTENT:
			case CONTROL:
			case CONVERSION:
			case COPARTITION:
			case COST:
			case CSV:
			case CURSOR_NAME:
			case DATA:
			case DATABASE:
			case DATETIME_INTERVAL_CODE:
			case DATETIME_INTERVAL_PRECISION:
			case DB:
			case DEFAULTS:
			case DEFINED:
			case DEFINER:
			case DEGREE:
			case DELIMITER:
			case DELIMITERS:
			case DEPENDS:
			case DEPTH:
			case DERIVED:
			case DETACH:
			case DICTIONARY:
			case DISABLE:
			case DISCARD:
			case DISPATCH:
			case DOCUMENT:
			case DYNAMIC_FUNCTION:
			case DYNAMIC_FUNCTION_CODE:
			case ENABLE:
			case ENCODING:
			case ENCRYPTED:
			case ENFORCED:
			case ENUM:
			case ERROR:
			case EVENT:
			case EXCLUDE:
			case EXCLUDING:
			case EXCLUSIVE:
			case EXPLAIN:
			case EXPRESSION:
			case EXTENSION:
			case FAMILY:
			case FILE:
			case FINAL:
			case FINALIZE:
			case FINISH:
			case FLAG:
			case FOLLOWING:
			case FORCE:
			case FORMAT:
			case FORTRAN:
			case FORWARD:
			case FREEZE:
			case FS:
			case FULFILL:
			case FUNCTIONS:
			case G_KW:
			case GENERAL:
			case GENERATED:
			case GRANTED:
			case HANDLER:
			case HEADER:
			case HEX:
			case HIERARCHY:
			case ID:
			case IF:
			case IGNORE:
			case IMMEDIATELY:
			case IMMUTABLE:
			case IMPLEMENTATION:
			case IMPLICIT:
			case INCLUDE:
			case INCLUDING:
			case INCREMENT:
			case INDENT:
			case INDEX:
			case INDEXES:
			case INHERIT:
			case INHERITS:
			case INLINE:
			case INSTANCE:
			case INSTANTIABLE:
			case INSTEAD:
			case INTEGRITY:
			case INVOKER:
			case ISNULL:
			case K_KW:
			case KEEP:
			case KEYS:
			case KEY_MEMBER:
			case KEY_TYPE:
			case LABEL:
			case LEAKPROOF:
			case LENGTH:
			case LIBRARY:
			case LIMIT:
			case LINK:
			case LISTEN:
			case LOAD:
			case LOCATION:
			case LOCATOR:
			case LOCK:
			case LOCKED:
			case LOGGED:
			case M_KW:
			case MAP:
			case MAPPING:
			case MATCHED:
			case MATERIALIZED:
			case MAXVALUE:
			case MEASURES:
			case MESSAGE_LENGTH:
			case MESSAGE_OCTET_LENGTH:
			case MESSAGE_TEXT:
			case MINVALUE:
			case MODE:
			case MORE_KW:
			case MOVE:
			case MUMPS:
			case NAME:
			case NAMESPACE:
			case NESTED:
			case NESTING:
			case NFC:
			case NFD:
			case NFKC:
			case NFKD:
			case NIL:
			case NORMALIZED:
			case NOTHING:
			case NOTIFY:
			case NOTNULL:
			case NOWAIT:
			case NULLABLE:
			case NULLS:
			case NULL_ORDERING:
			case NUMBER:
			case OBJECT:
			case OCCURRENCE:
			case OCTETS:
			case OFF:
			case OIDS:
			case OPERATOR_KW:
			case OPTIONS:
			case ORDERING:
			case ORDINALITY:
			case OTHERS:
			case OVERFLOW:
			case OVERRIDING:
			case OWNED:
			case OWNER:
			case P_KW:
			case PARALLEL:
			case PARAMETER_MODE:
			case PARAMETER_NAME:
			case PARAMETER_ORDINAL_POSITION:
			case PARAMETER_SPECIFIC_CATALOG:
			case PARAMETER_SPECIFIC_NAME:
			case PARAMETER_SPECIFIC_SCHEMA:
			case PARSER:
			case PASCAL:
			case PASS:
			case PASSING:
			case PASSTHROUGH:
			case PASSWORD:
			case PAST:
			case PERMISSION:
			case PERMUTE:
			case PIPE:
			case PLAN:
			case PLANS:
			case PLI:
			case POLICY:
			case PRECEDING:
			case PREPARED:
			case PREV:
			case PRIVATE:
			case PROCEDURAL:
			case PROCEDURES:
			case PROGRAM:
			case PRUNE:
			case PUBLICATION:
			case QUOTES:
			case REASSIGN:
			case RECHECK:
			case RECOVERY:
			case REFRESH:
			case REINDEX:
			case RENAME:
			case REPEATABLE:
			case REPLACE:
			case REPLICA:
			case REQUIRING:
			case RESET:
			case RESPECT:
			case RESTART:
			case RESTORE:
			case RETURNED_CARDINALITY:
			case RETURNED_LENGTH:
			case RETURNED_OCTET_LENGTH:
			case RETURNED_SQLSTATE:
			case RETURNING:
			case ROLE:
			case ROUTINE:
			case ROUTINES:
			case ROUTINE_CATALOG:
			case ROUTINE_NAME:
			case ROUTINE_SCHEMA:
			case ROW_COUNT:
			case RULE:
			case SCALAR:
			case SCALE:
			case SCHEMAS:
			case SCHEMA_NAME:
			case SCOPE_CATALOG:
			case SCOPE_NAME:
			case SCOPE_SCHEMA:
			case SECURITY:
			case SELECTIVE:
			case SELF:
			case SEMANTICS:
			case SEQUENCE:
			case SEQUENCES:
			case SERIALIZABLE:
			case SERVER:
			case SERVER_NAME:
			case SETOF:
			case SETS:
			case SHARE:
			case SIMPLE:
			case SNAPSHOT:
			case SORT_DIRECTION:
			case SOURCE:
			case SPECIFIC_NAME:
			case STABLE:
			case STANDALONE:
			case STATE:
			case STATEMENT:
			case STATISTICS:
			case STDIN:
			case STDOUT:
			case STORAGE:
			case STORED:
			case STRICT:
			case STRING:
			case STRIP:
			case STRUCTURE:
			case STYLE:
			case SUBCLASS_ORIGIN:
			case SUBSCRIPTION:
			case SUPPORT:
			case SYSID:
			case T_KW:
			case TABLES:
			case TABLESPACE:
			case TABLE_NAME:
			case TEMP:
			case TEMPLATE:
			case THROUGH:
			case TIES:
			case TOKEN:
			case TOP_LEVEL_COUNT:
			case TRANSACTIONS_COMMITTED:
			case TRANSACTIONS_ROLLED_BACK:
			case TRANSACTION_ACTIVE:
			case TRANSFORM:
			case TRANSFORMS:
			case TRIGGER_CATALOG:
			case TRIGGER_NAME:
			case TRIGGER_SCHEMA:
			case TRUSTED:
			case TYPE:
			case TYPES:
			case UNBOUNDED:
			case UNCOMMITTED:
			case UNCONDITIONAL:
			case UNDER:
			case UNENCRYPTED:
			case UNLINK:
			case UNLISTEN:
			case UNLOGGED:
			case UNMATCHED:
			case UNNAMED:
			case UNTIL:
			case UNTYPED:
			case URI:
			case USER_DEFINED_TYPE_CATALOG:
			case USER_DEFINED_TYPE_CODE:
			case USER_DEFINED_TYPE_NAME:
			case USER_DEFINED_TYPE_SCHEMA:
			case UTF16:
			case UTF32:
			case UTF8:
			case VACUUM:
			case VALID:
			case VALIDATE:
			case VALIDATOR:
			case VERBOSE:
			case VERSION:
			case VIEWS:
			case VOLATILE:
			case WHITESPACE:
			case WRAPPER:
			case XMLDECLARATION:
			case XMLROOT:
			case XMLSCHEMA:
			case YES:
				{
				setState(283);
				non_reserved_keyword();
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	 
		public LiteralContext() { }
		public void copyFrom(LiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringLiteralContext extends LiteralContext {
		public TerminalNode SINGLE_QUOTED_STRING() { return getToken(PsqlParser.SINGLE_QUOTED_STRING, 0); }
		public StringLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IntegerLiteralContext extends LiteralContext {
		public TerminalNode NO_FLOATING_NUMBER() { return getToken(PsqlParser.NO_FLOATING_NUMBER, 0); }
		public IntegerLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterIntegerLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitIntegerLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitIntegerLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanLiteralContext extends LiteralContext {
		public TerminalNode TRUE() { return getToken(PsqlParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(PsqlParser.FALSE, 0); }
		public BooleanLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FloatingPointLiteralContext extends LiteralContext {
		public TerminalNode FLOATING_POINT() { return getToken(PsqlParser.FLOATING_POINT, 0); }
		public FloatingPointLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterFloatingPointLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitFloatingPointLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitFloatingPointLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_literal);
		int _la;
		try {
			setState(290);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SINGLE_QUOTED_STRING:
				_localctx = new StringLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(286);
				match(SINGLE_QUOTED_STRING);
				}
				break;
			case NO_FLOATING_NUMBER:
				_localctx = new IntegerLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(287);
				match(NO_FLOATING_NUMBER);
				}
				break;
			case FLOATING_POINT:
				_localctx = new FloatingPointLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(288);
				match(FLOATING_POINT);
				}
				break;
			case FALSE:
			case TRUE:
				_localctx = new BooleanLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(289);
				_la = _input.LA(1);
				if ( !(_la==FALSE || _la==TRUE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
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
	public static class TipeContext extends ParserRuleContext {
		public Psql_typeContext psql_type() {
			return getRuleContext(Psql_typeContext.class,0);
		}
		public TerminalNode L_PAREN() { return getToken(PsqlParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(PsqlParser.R_PAREN, 0); }
		public TerminalNode WITH_TIME_ZONE() { return getToken(PsqlParser.WITH_TIME_ZONE, 0); }
		public List<TerminalNode> NO_FLOATING_NUMBER() { return getTokens(PsqlParser.NO_FLOATING_NUMBER); }
		public TerminalNode NO_FLOATING_NUMBER(int i) {
			return getToken(PsqlParser.NO_FLOATING_NUMBER, i);
		}
		public TerminalNode COMMA() { return getToken(PsqlParser.COMMA, 0); }
		public TipeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipe; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterTipe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitTipe(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitTipe(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TipeContext tipe() throws RecognitionException {
		TipeContext _localctx = new TipeContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_tipe);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			psql_type();
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(293);
				match(L_PAREN);
				{
				setState(294);
				match(NO_FLOATING_NUMBER);
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(295);
					match(COMMA);
					setState(296);
					match(NO_FLOATING_NUMBER);
					}
				}

				}
				setState(299);
				match(R_PAREN);
				}
				break;
			}
			setState(303);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(302);
				match(WITH_TIME_ZONE);
				}
				break;
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
	public static class Psql_typeContext extends ParserRuleContext {
		public TerminalNode BIGINT() { return getToken(PsqlParser.BIGINT, 0); }
		public TerminalNode BIGSERIAL() { return getToken(PsqlParser.BIGSERIAL, 0); }
		public TerminalNode BIT() { return getToken(PsqlParser.BIT, 0); }
		public TerminalNode BIT_VARYING() { return getToken(PsqlParser.BIT_VARYING, 0); }
		public TerminalNode BOOLEAN() { return getToken(PsqlParser.BOOLEAN, 0); }
		public TerminalNode BOX() { return getToken(PsqlParser.BOX, 0); }
		public TerminalNode BYTEA() { return getToken(PsqlParser.BYTEA, 0); }
		public TerminalNode CHARACTER() { return getToken(PsqlParser.CHARACTER, 0); }
		public TerminalNode CHARACTER_VARYING() { return getToken(PsqlParser.CHARACTER_VARYING, 0); }
		public TerminalNode VARCHAR() { return getToken(PsqlParser.VARCHAR, 0); }
		public TerminalNode CIDR() { return getToken(PsqlParser.CIDR, 0); }
		public TerminalNode CIRCLE() { return getToken(PsqlParser.CIRCLE, 0); }
		public TerminalNode DATE() { return getToken(PsqlParser.DATE, 0); }
		public TerminalNode DOUBLE() { return getToken(PsqlParser.DOUBLE, 0); }
		public TerminalNode INET() { return getToken(PsqlParser.INET, 0); }
		public TerminalNode INTEGER() { return getToken(PsqlParser.INTEGER, 0); }
		public TerminalNode INTERVAL() { return getToken(PsqlParser.INTERVAL, 0); }
		public TerminalNode JSON() { return getToken(PsqlParser.JSON, 0); }
		public TerminalNode JSONB() { return getToken(PsqlParser.JSONB, 0); }
		public TerminalNode LINE() { return getToken(PsqlParser.LINE, 0); }
		public TerminalNode LSEG() { return getToken(PsqlParser.LSEG, 0); }
		public TerminalNode MACADDR() { return getToken(PsqlParser.MACADDR, 0); }
		public TerminalNode MACADDR8() { return getToken(PsqlParser.MACADDR8, 0); }
		public TerminalNode MONEY() { return getToken(PsqlParser.MONEY, 0); }
		public TerminalNode NUMERIC() { return getToken(PsqlParser.NUMERIC, 0); }
		public TerminalNode PATH() { return getToken(PsqlParser.PATH, 0); }
		public TerminalNode PG_LSN() { return getToken(PsqlParser.PG_LSN, 0); }
		public TerminalNode PG_SNAPSHOT() { return getToken(PsqlParser.PG_SNAPSHOT, 0); }
		public TerminalNode POINT() { return getToken(PsqlParser.POINT, 0); }
		public TerminalNode POLYGON() { return getToken(PsqlParser.POLYGON, 0); }
		public TerminalNode REAL() { return getToken(PsqlParser.REAL, 0); }
		public TerminalNode SMALLINT() { return getToken(PsqlParser.SMALLINT, 0); }
		public TerminalNode SMALLSERIAL() { return getToken(PsqlParser.SMALLSERIAL, 0); }
		public TerminalNode SERIAL() { return getToken(PsqlParser.SERIAL, 0); }
		public TerminalNode TEXT() { return getToken(PsqlParser.TEXT, 0); }
		public TerminalNode TIME() { return getToken(PsqlParser.TIME, 0); }
		public TerminalNode TIMESTAMP() { return getToken(PsqlParser.TIMESTAMP, 0); }
		public TerminalNode TSQUERY() { return getToken(PsqlParser.TSQUERY, 0); }
		public TerminalNode TSVECTOR() { return getToken(PsqlParser.TSVECTOR, 0); }
		public TerminalNode TXID_SNAPSHOT() { return getToken(PsqlParser.TXID_SNAPSHOT, 0); }
		public TerminalNode UUID() { return getToken(PsqlParser.UUID, 0); }
		public TerminalNode XML() { return getToken(PsqlParser.XML, 0); }
		public TerminalNode INT_8() { return getToken(PsqlParser.INT_8, 0); }
		public TerminalNode SERIAL_8() { return getToken(PsqlParser.SERIAL_8, 0); }
		public TerminalNode VARBIT() { return getToken(PsqlParser.VARBIT, 0); }
		public TerminalNode BOOL() { return getToken(PsqlParser.BOOL, 0); }
		public TerminalNode CHAR() { return getToken(PsqlParser.CHAR, 0); }
		public TerminalNode FLOAT_8() { return getToken(PsqlParser.FLOAT_8, 0); }
		public TerminalNode INT_4() { return getToken(PsqlParser.INT_4, 0); }
		public TerminalNode INT() { return getToken(PsqlParser.INT, 0); }
		public TerminalNode DECIMAL() { return getToken(PsqlParser.DECIMAL, 0); }
		public TerminalNode FLOAT_4() { return getToken(PsqlParser.FLOAT_4, 0); }
		public TerminalNode INT_2() { return getToken(PsqlParser.INT_2, 0); }
		public TerminalNode SERIAL_2() { return getToken(PsqlParser.SERIAL_2, 0); }
		public TerminalNode SERIAL_4() { return getToken(PsqlParser.SERIAL_4, 0); }
		public TerminalNode TIMESTAMPTZ() { return getToken(PsqlParser.TIMESTAMPTZ, 0); }
		public Psql_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_psql_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).enterPsql_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PsqlParserListener ) ((PsqlParserListener)listener).exitPsql_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PsqlParserVisitor ) return ((PsqlParserVisitor<? extends T>)visitor).visitPsql_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Psql_typeContext psql_type() throws RecognitionException {
		Psql_typeContext _localctx = new Psql_typeContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_psql_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			_la = _input.LA(1);
			if ( !(((((_la - 46)) & ~0x3f) == 0 && ((1L << (_la - 46)) & 274877906943L) != 0) || ((((_la - 135)) & ~0x3f) == 0 && ((1L << (_la - 135)) & 134217861L) != 0) || _la==DATE || _la==DOUBLE || ((((_la - 421)) & ~0x3f) == 0 && ((1L << (_la - 421)) & 2065L) != 0) || _la==NUMERIC || _la==PATH || _la==REAL || ((((_la - 743)) & ~0x3f) == 0 && ((1L << (_la - 743)) & -8646911284551352319L) != 0) || _la==TIMESTAMP || _la==VARCHAR || _la==XML) ) {
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
		case 21:
			return stmt_items_sempred((Stmt_itemsContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean stmt_items_sempred(Stmt_itemsContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 12);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u03b0\u0134\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001C\b"+
		"\u0001\n\u0001\f\u0001F\t\u0001\u0001\u0001\u0003\u0001I\b\u0001\u0001"+
		"\u0002\u0001\u0002\u0003\u0002M\b\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0003\u0004Y\b\u0004\u0001\u0005\u0001\u0005\u0004"+
		"\u0005]\b\u0005\u000b\u0005\f\u0005^\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0005\u0005e\b\u0005\n\u0005\f\u0005h\t\u0005\u0001\u0006"+
		"\u0001\u0006\u0004\u0006l\b\u0006\u000b\u0006\f\u0006m\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0004\bv\b\b\u000b\b\f"+
		"\bw\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b~\b\b\n\b\f\b\u0081\t\b\u0001"+
		"\t\u0001\t\u0005\t\u0085\b\t\n\t\f\t\u0088\t\t\u0001\n\u0005\n\u008b\b"+
		"\n\n\n\f\n\u008e\t\n\u0001\u000b\u0001\u000b\u0005\u000b\u0092\b\u000b"+
		"\n\u000b\f\u000b\u0095\t\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u009f\b\f\u0001\r\u0001\r\u0004"+
		"\r\u00a3\b\r\u000b\r\f\r\u00a4\u0001\u000e\u0001\u000e\u0004\u000e\u00a9"+
		"\b\u000e\u000b\u000e\f\u000e\u00aa\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0004\u0010\u00b3\b\u0010\u000b\u0010"+
		"\f\u0010\u00b4\u0001\u0011\u0001\u0011\u0005\u0011\u00b9\b\u0011\n\u0011"+
		"\f\u0011\u00bc\t\u0011\u0001\u0012\u0004\u0012\u00bf\b\u0012\u000b\u0012"+
		"\f\u0012\u00c0\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0004\u0014\u00ce\b\u0014\u000b\u0014\f\u0014\u00cf\u0003\u0014\u00d2"+
		"\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0003\u0015\u00e1\b\u0015\u0001\u0015\u0001\u0015\u0003"+
		"\u0015\u00e5\b\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003"+
		"\u0015\u00f1\b\u0015\u0001\u0015\u0003\u0015\u00f4\b\u0015\u0003\u0015"+
		"\u00f6\b\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0004\u0015\u00fe\b\u0015\u000b\u0015\f\u0015\u00ff\u0005"+
		"\u0015\u0102\b\u0015\n\u0015\f\u0015\u0105\t\u0015\u0001\u0016\u0001\u0016"+
		"\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0004\u0019\u0110\b\u0019\u000b\u0019\f\u0019\u0111\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0118\b\u0019\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u011d\b\u001a\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0123\b\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u012a\b\u001c\u0001"+
		"\u001c\u0003\u001c\u012d\b\u001c\u0001\u001c\u0003\u001c\u0130\b\u001c"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0000\u0001*\u001e\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:\u0000\b\u0001\u0001\u03a4\u03a4\u0002\u0000\u03ab\u03ab\u03ae"+
		"\u03ae\u0001\u0001\u03af\u03af\u00b3\u0000\u0005\u0005&&VX[\\^^bceegh"+
		"jtvy}~\u0082\u0084\u0086\u0086\u0088\u0088\u008a\u008b\u008f\u008f\u0091"+
		"\u0092\u0095\u009c\u009e\u009f\u00a5\u00a5\u00a9\u00aa\u00ad\u00ad\u00af"+
		"\u00b0\u00b2\u00b2\u00b4\u00b5\u00b9\u00ba\u00c1\u00c1\u00c5\u00c5\u00ca"+
		"\u00cb\u00cd\u00ce\u00d3\u00d3\u00d5\u00d5\u00d8\u00d8\u00da\u00de\u00e0"+
		"\u00e4\u00e6\u00f4\u00f6\u00f6\u00f9\u00f9\u00fd\u00fd\u00ff\u0103\u0105"+
		"\u0107\u010b\u010b\u010e\u010e\u0111\u0111\u0113\u0115\u0117\u0118\u011c"+
		"\u011c\u011e\u012a\u012c\u012c\u012e\u012f\u0132\u0135\u0139\u013c\u013f"+
		"\u013f\u0141\u0141\u0143\u0145\u0149\u014c\u0150\u0152\u0154\u0154\u0156"+
		"\u0156\u015a\u015b\u015d\u015e\u0160\u0160\u0162\u0162\u0166\u0168\u016a"+
		"\u016a\u016d\u016e\u0170\u0170\u0174\u0178\u017a\u017d\u017f\u017f\u0183"+
		"\u0184\u0186\u0186\u018a\u018a\u018f\u0190\u0197\u0197\u019a\u019b\u019d"+
		"\u01a1\u01a7\u01a8\u01aa\u01aa\u01ac\u01ac\u01ae\u01af\u01b1\u01bb\u01be"+
		"\u01be\u01c3\u01ca\u01cc\u01cd\u01cf\u01cf\u01d2\u01d2\u01d5\u01d5\u01d7"+
		"\u01d7\u01d9\u01db\u01e0\u01e1\u01e3\u01e5\u01e9\u01e9\u01eb\u01ed\u01ef"+
		"\u01ef\u01f2\u01f3\u01f7\u01f9\u01fb\u01fb\u01fd\u01ff\u0202\u0202\u0205"+
		"\u0205\u0207\u020a\u020d\u020e\u0214\u0216\u021d\u021f\u0221\u0221\u0228"+
		"\u0228\u022a\u022b\u022d\u022d\u022f\u0234\u0236\u0236\u0239\u0239\u023d"+
		"\u0240\u0242\u0243\u0248\u0248\u024a\u024a\u0252\u0253\u025b\u0261\u0265"+
		"\u0265\u026a\u026e\u0270\u0271\u0273\u0273\u0275\u0276\u0278\u0278\u027a"+
		"\u027a\u027e\u027f\u0283\u0286\u028b\u028e\u0290\u0298\u029a\u029b\u02a5"+
		"\u02a7\u02ad\u02af\u02b1\u02b2\u02b8\u02b9\u02bb\u02bd\u02bf\u02c0\u02c3"+
		"\u02c3\u02c6\u02c6\u02ca\u02cd\u02cf\u02d0\u02d4\u02d4\u02da\u02dc\u02e0"+
		"\u02e1\u02e3\u02e6\u02e9\u02e9\u02ec\u02ee\u02f0\u02f6\u02f9\u02f9\u02fc"+
		"\u02fc\u02fe\u02ff\u030a\u030a\u030c\u0310\u0312\u0312\u0314\u0316\u0318"+
		"\u0318\u031a\u031a\u031d\u031e\u0321\u0321\u0323\u0323\u0328\u032a\u032d"+
		"\u032e\u0334\u0338\u033c\u033f\u0343\u0343\u0349\u034b\u0351\u0351\u0354"+
		"\u0355\u0357\u0358\u035d\u035d\u0365\u0368\u036a\u036d\u0370\u0371\u0374"+
		"\u0376\u0378\u037d\u037f\u037f\u0381\u0386\u0388\u0390\u0393\u0397\u0399"+
		"\u0399\u00ae\u0000\u0003\u0003TUYZ]]_addffuuz|\u007f\u0081\u0085\u0085"+
		"\u008c\u008d\u0090\u0090\u0093\u0094\u009d\u009d\u00a0\u00a1\u00a3\u00a4"+
		"\u00a6\u00a8\u00ab\u00ac\u00ae\u00ae\u00b1\u00b1\u00b3\u00b3\u00b6\u00b8"+
		"\u00bb\u00c0\u00c2\u00c4\u00c6\u00c9\u00cc\u00cc\u00cf\u00d2\u00d4\u00d4"+
		"\u00d6\u00d7\u00d9\u00d9\u00df\u00df\u00e5\u00e5\u00f5\u00f5\u00f7\u00f8"+
		"\u00fb\u00fc\u00fe\u00fe\u0104\u0104\u0108\u010a\u010c\u010d\u010f\u0110"+
		"\u0112\u0112\u0116\u0116\u0119\u011b\u011d\u011d\u012b\u012b\u0130\u0131"+
		"\u0136\u0138\u013d\u013e\u0140\u0140\u0142\u0142\u0146\u0148\u014d\u014f"+
		"\u0153\u0153\u0155\u0155\u0157\u0159\u015c\u015c\u015f\u015f\u0161\u0161"+
		"\u0163\u0165\u0169\u0169\u016b\u016c\u016f\u016f\u0171\u0173\u0179\u0179"+
		"\u017e\u017e\u0180\u0182\u0185\u0185\u0187\u0188\u018b\u018e\u0191\u0196"+
		"\u0198\u0199\u019c\u019c\u01a2\u01a4\u01a6\u01a6\u01ab\u01ab\u01ad\u01ad"+
		"\u01bc\u01bd\u01bf\u01c2\u01cb\u01cb\u01ce\u01ce\u01d0\u01d0\u01d3\u01d4"+
		"\u01d6\u01d6\u01d8\u01d8\u01dc\u01df\u01e2\u01e2\u01e6\u01e8\u01ea\u01ea"+
		"\u01ee\u01ee\u01f0\u01f1\u01f4\u01f6\u01fa\u01fa\u01fc\u01fc\u0200\u0201"+
		"\u0203\u0204\u0206\u0206\u020b\u020c\u020f\u0213\u0217\u0217\u0219\u021c"+
		"\u0220\u0220\u0222\u0224\u0226\u0227\u0229\u0229\u022c\u022c\u022e\u022e"+
		"\u0235\u0235\u0237\u0237\u023a\u023c\u0241\u0241\u0244\u0247\u0249\u0249"+
		"\u024b\u0251\u0254\u0259\u0262\u0264\u0266\u0269\u026f\u026f\u0272\u0272"+
		"\u0274\u0274\u0277\u0277\u0279\u0279\u027b\u027d\u0280\u0280\u0282\u0282"+
		"\u0288\u028a\u028f\u028f\u0299\u0299\u029c\u02a4\u02a8\u02ac\u02b0\u02b0"+
		"\u02b3\u02b7\u02ba\u02ba\u02be\u02be\u02c1\u02c2\u02c4\u02c5\u02c7\u02c9"+
		"\u02ce\u02ce\u02d1\u02d3\u02d5\u02d9\u02dd\u02df\u02e2\u02e2\u02e8\u02e8"+
		"\u02ea\u02eb\u02ef\u02ef\u02f7\u02f8\u02fa\u02fb\u02fd\u02fd\u0300\u0309"+
		"\u030b\u030b\u0311\u0311\u0313\u0313\u0317\u0317\u0319\u0319\u031b\u031c"+
		"\u031f\u0320\u0324\u0325\u032b\u032c\u032f\u0333\u0339\u033b\u0340\u0342"+
		"\u0344\u0348\u034c\u0350\u0352\u0353\u0356\u0356\u0359\u035c\u035e\u0364"+
		"\u036e\u036f\u0372\u0373\u0377\u0377\u037e\u037e\u0387\u0387\u0391\u0392"+
		"\u0398\u0398\u0007\u0000\u0005\u0005\r%ii\u0189\u0189\u01d1\u01d1\u0218"+
		"\u0218\u0238\u0238\u0002\u0000\u0152\u0152\u033e\u033e\u0012\u0000.S\u0087"+
		"\u0087\u0089\u0089\u008e\u008e\u00a2\u00a2\u00fa\u00fa\u012d\u012d\u01a5"+
		"\u01a5\u01a9\u01a9\u01b0\u01b0\u0225\u0225\u025a\u025a\u0287\u0287\u02e7"+
		"\u02e7\u0322\u0322\u0326\u0327\u0369\u0369\u0380\u0380\u014e\u0000<\u0001"+
		"\u0000\u0000\u0000\u0002?\u0001\u0000\u0000\u0000\u0004L\u0001\u0000\u0000"+
		"\u0000\u0006N\u0001\u0000\u0000\u0000\bX\u0001\u0000\u0000\u0000\nZ\u0001"+
		"\u0000\u0000\u0000\fi\u0001\u0000\u0000\u0000\u000eo\u0001\u0000\u0000"+
		"\u0000\u0010s\u0001\u0000\u0000\u0000\u0012\u0082\u0001\u0000\u0000\u0000"+
		"\u0014\u008c\u0001\u0000\u0000\u0000\u0016\u008f\u0001\u0000\u0000\u0000"+
		"\u0018\u009e\u0001\u0000\u0000\u0000\u001a\u00a0\u0001\u0000\u0000\u0000"+
		"\u001c\u00a6\u0001\u0000\u0000\u0000\u001e\u00ac\u0001\u0000\u0000\u0000"+
		" \u00b0\u0001\u0000\u0000\u0000\"\u00b6\u0001\u0000\u0000\u0000$\u00be"+
		"\u0001\u0000\u0000\u0000&\u00c2\u0001\u0000\u0000\u0000(\u00d1\u0001\u0000"+
		"\u0000\u0000*\u00f5\u0001\u0000\u0000\u0000,\u0106\u0001\u0000\u0000\u0000"+
		".\u0108\u0001\u0000\u0000\u00000\u010a\u0001\u0000\u0000\u00002\u0117"+
		"\u0001\u0000\u0000\u00004\u011c\u0001\u0000\u0000\u00006\u0122\u0001\u0000"+
		"\u0000\u00008\u0124\u0001\u0000\u0000\u0000:\u0131\u0001\u0000\u0000\u0000"+
		"<=\u0003\u0002\u0001\u0000=>\u0005\u0000\u0000\u0001>\u0001\u0001\u0000"+
		"\u0000\u0000?D\u0003(\u0014\u0000@A\u0005\u000b\u0000\u0000AC\u0003(\u0014"+
		"\u0000B@\u0001\u0000\u0000\u0000CF\u0001\u0000\u0000\u0000DB\u0001\u0000"+
		"\u0000\u0000DE\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000\u0000FD\u0001"+
		"\u0000\u0000\u0000GI\u0005\u000b\u0000\u0000HG\u0001\u0000\u0000\u0000"+
		"HI\u0001\u0000\u0000\u0000I\u0003\u0001\u0000\u0000\u0000JM\u0003\u0006"+
		"\u0003\u0000KM\u0003\u0016\u000b\u0000LJ\u0001\u0000\u0000\u0000LK\u0001"+
		"\u0000\u0000\u0000M\u0005\u0001\u0000\u0000\u0000NO\u0005\u0001\u0000"+
		"\u0000OP\u0003\b\u0004\u0000PQ\u0007\u0000\u0000\u0000Q\u0007\u0001\u0000"+
		"\u0000\u0000RY\u0003\n\u0005\u0000SY\u0003\f\u0006\u0000TY\u0003\u0010"+
		"\b\u0000UY\u0003\u000e\u0007\u0000VY\u0003\u0012\t\u0000WY\u0003\u0014"+
		"\n\u0000XR\u0001\u0000\u0000\u0000XS\u0001\u0000\u0000\u0000XT\u0001\u0000"+
		"\u0000\u0000XU\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XW\u0001"+
		"\u0000\u0000\u0000Y\t\u0001\u0000\u0000\u0000Z\\\u0005\u039f\u0000\u0000"+
		"[]\u0005\u03a2\u0000\u0000\\[\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000"+
		"\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_f\u0001\u0000"+
		"\u0000\u0000`a\u0005\u03a4\u0000\u0000ab\u0005\u0001\u0000\u0000bc\u0005"+
		"\u03a3\u0000\u0000ce\u0003\u0014\n\u0000d`\u0001\u0000\u0000\u0000eh\u0001"+
		"\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000"+
		"g\u000b\u0001\u0000\u0000\u0000hf\u0001\u0000\u0000\u0000ik\u0005\u039d"+
		"\u0000\u0000jl\u0005\u03a2\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001"+
		"\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000"+
		"n\r\u0001\u0000\u0000\u0000op\u0005\u039e\u0000\u0000pq\u0005\u03a2\u0000"+
		"\u0000qr\u0005\u03a2\u0000\u0000r\u000f\u0001\u0000\u0000\u0000su\u0005"+
		"\u03a0\u0000\u0000tv\u0005\u03a2\u0000\u0000ut\u0001\u0000\u0000\u0000"+
		"vw\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000"+
		"\u0000x\u007f\u0001\u0000\u0000\u0000yz\u0005\u03a4\u0000\u0000z{\u0005"+
		"\u0001\u0000\u0000{|\u0005\u03a3\u0000\u0000|~\u0003\u0014\n\u0000}y\u0001"+
		"\u0000\u0000\u0000~\u0081\u0001\u0000\u0000\u0000\u007f}\u0001\u0000\u0000"+
		"\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0011\u0001\u0000\u0000"+
		"\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0082\u0086\u0005\u03a1\u0000"+
		"\u0000\u0083\u0085\u0005\u03a2\u0000\u0000\u0084\u0083\u0001\u0000\u0000"+
		"\u0000\u0085\u0088\u0001\u0000\u0000\u0000\u0086\u0084\u0001\u0000\u0000"+
		"\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0013\u0001\u0000\u0000"+
		"\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0089\u008b\u0005\u03a2\u0000"+
		"\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008b\u008e\u0001\u0000\u0000"+
		"\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000"+
		"\u0000\u008d\u0015\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000"+
		"\u0000\u008f\u0093\u0005\u0002\u0000\u0000\u0090\u0092\u0003\u0018\f\u0000"+
		"\u0091\u0090\u0001\u0000\u0000\u0000\u0092\u0095\u0001\u0000\u0000\u0000"+
		"\u0093\u0091\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000"+
		"\u0094\u0096\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000"+
		"\u0096\u0097\u0005\u03ad\u0000\u0000\u0097\u0017\u0001\u0000\u0000\u0000"+
		"\u0098\u009f\u0003\u001a\r\u0000\u0099\u009f\u0003\u001c\u000e\u0000\u009a"+
		"\u009f\u0003\u001e\u000f\u0000\u009b\u009f\u0003 \u0010\u0000\u009c\u009f"+
		"\u0003\"\u0011\u0000\u009d\u009f\u0003$\u0012\u0000\u009e\u0098\u0001"+
		"\u0000\u0000\u0000\u009e\u0099\u0001\u0000\u0000\u0000\u009e\u009a\u0001"+
		"\u0000\u0000\u0000\u009e\u009b\u0001\u0000\u0000\u0000\u009e\u009c\u0001"+
		"\u0000\u0000\u0000\u009e\u009d\u0001\u0000\u0000\u0000\u009f\u0019\u0001"+
		"\u0000\u0000\u0000\u00a0\u00a2\u0005\u03a8\u0000\u0000\u00a1\u00a3\u0003"+
		"&\u0013\u0000\u00a2\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000"+
		"\u0000\u0000\u00a4\u00a2\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000"+
		"\u0000\u0000\u00a5\u001b\u0001\u0000\u0000\u0000\u00a6\u00a8\u0005\u03a6"+
		"\u0000\u0000\u00a7\u00a9\u0003&\u0013\u0000\u00a8\u00a7\u0001\u0000\u0000"+
		"\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000"+
		"\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u001d\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ad\u0005\u03a7\u0000\u0000\u00ad\u00ae\u0003&\u0013\u0000"+
		"\u00ae\u00af\u0003&\u0013\u0000\u00af\u001f\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b2\u0005\u03a9\u0000\u0000\u00b1\u00b3\u0003&\u0013\u0000\u00b2\u00b1"+
		"\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b2"+
		"\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5!\u0001"+
		"\u0000\u0000\u0000\u00b6\u00ba\u0005\u03aa\u0000\u0000\u00b7\u00b9\u0003"+
		"&\u0013\u0000\u00b8\u00b7\u0001\u0000\u0000\u0000\u00b9\u00bc\u0001\u0000"+
		"\u0000\u0000\u00ba\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000"+
		"\u0000\u0000\u00bb#\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000"+
		"\u0000\u00bd\u00bf\u0003&\u0013\u0000\u00be\u00bd\u0001\u0000\u0000\u0000"+
		"\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00be\u0001\u0000\u0000\u0000"+
		"\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1%\u0001\u0000\u0000\u0000\u00c2"+
		"\u00c3\u0007\u0001\u0000\u0000\u00c3\'\u0001\u0000\u0000\u0000\u00c4\u00c5"+
		"\u0005\t\u0000\u0000\u00c5\u00c6\u0003(\u0014\u0000\u00c6\u00c7\u0005"+
		"\n\u0000\u0000\u00c7\u00d2\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005\u0007"+
		"\u0000\u0000\u00c9\u00ca\u0003(\u0014\u0000\u00ca\u00cb\u0005\b\u0000"+
		"\u0000\u00cb\u00d2\u0001\u0000\u0000\u0000\u00cc\u00ce\u0003*\u0015\u0000"+
		"\u00cd\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000"+
		"\u00cf\u00cd\u0001\u0000\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000"+
		"\u00d0\u00d2\u0001\u0000\u0000\u0000\u00d1\u00c4\u0001\u0000\u0000\u0000"+
		"\u00d1\u00c8\u0001\u0000\u0000\u0000\u00d1\u00cd\u0001\u0000\u0000\u0000"+
		"\u00d2)\u0001\u0000\u0000\u0000\u00d3\u00d4\u0006\u0015\uffff\uffff\u0000"+
		"\u00d4\u00d5\u0005\t\u0000\u0000\u00d5\u00d6\u0003(\u0014\u0000\u00d6"+
		"\u00d7\u0005\n\u0000\u0000\u00d7\u00f6\u0001\u0000\u0000\u0000\u00d8\u00d9"+
		"\u0005\u0007\u0000\u0000\u00d9\u00da\u0003(\u0014\u0000\u00da\u00db\u0005"+
		"\b\u0000\u0000\u00db\u00f6\u0001\u0000\u0000\u0000\u00dc\u00f6\u00032"+
		"\u0019\u0000\u00dd\u00f6\u00036\u001b\u0000\u00de\u00e1\u0003,\u0016\u0000"+
		"\u00df\u00e1\u00034\u001a\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0"+
		"\u00df\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2"+
		"\u00e4\u0005\t\u0000\u0000\u00e3\u00e5\u0003*\u0015\u0000\u00e4\u00e3"+
		"\u0001\u0000\u0000\u0000\u00e4\u00e5\u0001\u0000\u0000\u0000\u00e5\u00e6"+
		"\u0001\u0000\u0000\u0000\u00e6\u00e7\u0005\n\u0000\u0000\u00e7\u00f6\u0001"+
		"\u0000\u0000\u0000\u00e8\u00f6\u0003,\u0016\u0000\u00e9\u00f6\u00038\u001c"+
		"\u0000\u00ea\u00f6\u00034\u001a\u0000\u00eb\u00f6\u0005*\u0000\u0000\u00ec"+
		"\u00f6\u00030\u0018\u0000\u00ed\u00f6\u0003\u0004\u0002\u0000\u00ee\u00f0"+
		"\u0005\u039b\u0000\u0000\u00ef\u00f1\u0005\u03b0\u0000\u0000\u00f0\u00ef"+
		"\u0001\u0000\u0000\u0000\u00f0\u00f1\u0001\u0000\u0000\u0000\u00f1\u00f3"+
		"\u0001\u0000\u0000\u0000\u00f2\u00f4\u0007\u0002\u0000\u0000\u00f3\u00f2"+
		"\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f6"+
		"\u0001\u0000\u0000\u0000\u00f5\u00d3\u0001\u0000\u0000\u0000\u00f5\u00d8"+
		"\u0001\u0000\u0000\u0000\u00f5\u00dc\u0001\u0000\u0000\u0000\u00f5\u00dd"+
		"\u0001\u0000\u0000\u0000\u00f5\u00e0\u0001\u0000\u0000\u0000\u00f5\u00e8"+
		"\u0001\u0000\u0000\u0000\u00f5\u00e9\u0001\u0000\u0000\u0000\u00f5\u00ea"+
		"\u0001\u0000\u0000\u0000\u00f5\u00eb\u0001\u0000\u0000\u0000\u00f5\u00ec"+
		"\u0001\u0000\u0000\u0000\u00f5\u00ed\u0001\u0000\u0000\u0000\u00f5\u00ee"+
		"\u0001\u0000\u0000\u0000\u00f6\u0103\u0001\u0000\u0000\u0000\u00f7\u00f8"+
		"\n\u0004\u0000\u0000\u00f8\u00f9\u0005\'\u0000\u0000\u00f9\u0102\u0003"+
		"*\u0015\u0005\u00fa\u00fd\n\f\u0000\u0000\u00fb\u00fc\u0005\u0006\u0000"+
		"\u0000\u00fc\u00fe\u0003*\u0015\u0000\u00fd\u00fb\u0001\u0000\u0000\u0000"+
		"\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff\u00fd\u0001\u0000\u0000\u0000"+
		"\u00ff\u0100\u0001\u0000\u0000\u0000\u0100\u0102\u0001\u0000\u0000\u0000"+
		"\u0101\u00f7\u0001\u0000\u0000\u0000\u0101\u00fa\u0001\u0000\u0000\u0000"+
		"\u0102\u0105\u0001\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000"+
		"\u0103\u0104\u0001\u0000\u0000\u0000\u0104+\u0001\u0000\u0000\u0000\u0105"+
		"\u0103\u0001\u0000\u0000\u0000\u0106\u0107\u0007\u0003\u0000\u0000\u0107"+
		"-\u0001\u0000\u0000\u0000\u0108\u0109\u0007\u0004\u0000\u0000\u0109/\u0001"+
		"\u0000\u0000\u0000\u010a\u010b\u0007\u0005\u0000\u0000\u010b1\u0001\u0000"+
		"\u0000\u0000\u010c\u010f\u00034\u001a\u0000\u010d\u010e\u0005\u0004\u0000"+
		"\u0000\u010e\u0110\u00034\u001a\u0000\u010f\u010d\u0001\u0000\u0000\u0000"+
		"\u0110\u0111\u0001\u0000\u0000\u0000\u0111\u010f\u0001\u0000\u0000\u0000"+
		"\u0111\u0112\u0001\u0000\u0000\u0000\u0112\u0118\u0001\u0000\u0000\u0000"+
		"\u0113\u0114\u00034\u001a\u0000\u0114\u0115\u0005\u0004\u0000\u0000\u0115"+
		"\u0116\u0006\u0019\uffff\uffff\u0000\u0116\u0118\u0001\u0000\u0000\u0000"+
		"\u0117\u010c\u0001\u0000\u0000\u0000\u0117\u0113\u0001\u0000\u0000\u0000"+
		"\u01183\u0001\u0000\u0000\u0000\u0119\u011d\u0005\u039a\u0000\u0000\u011a"+
		"\u011d\u0005+\u0000\u0000\u011b\u011d\u0003.\u0017\u0000\u011c\u0119\u0001"+
		"\u0000\u0000\u0000\u011c\u011a\u0001\u0000\u0000\u0000\u011c\u011b\u0001"+
		"\u0000\u0000\u0000\u011d5\u0001\u0000\u0000\u0000\u011e\u0123\u0005,\u0000"+
		"\u0000\u011f\u0123\u0005(\u0000\u0000\u0120\u0123\u0005)\u0000\u0000\u0121"+
		"\u0123\u0007\u0006\u0000\u0000\u0122\u011e\u0001\u0000\u0000\u0000\u0122"+
		"\u011f\u0001\u0000\u0000\u0000\u0122\u0120\u0001\u0000\u0000\u0000\u0122"+
		"\u0121\u0001\u0000\u0000\u0000\u01237\u0001\u0000\u0000\u0000\u0124\u012c"+
		"\u0003:\u001d\u0000\u0125\u0126\u0005\t\u0000\u0000\u0126\u0129\u0005"+
		"(\u0000\u0000\u0127\u0128\u0005\u0006\u0000\u0000\u0128\u012a\u0005(\u0000"+
		"\u0000\u0129\u0127\u0001\u0000\u0000\u0000\u0129\u012a\u0001\u0000\u0000"+
		"\u0000\u012a\u012b\u0001\u0000\u0000\u0000\u012b\u012d\u0005\n\u0000\u0000"+
		"\u012c\u0125\u0001\u0000\u0000\u0000\u012c\u012d\u0001\u0000\u0000\u0000"+
		"\u012d\u012f\u0001\u0000\u0000\u0000\u012e\u0130\u0005\f\u0000\u0000\u012f"+
		"\u012e\u0001\u0000\u0000\u0000\u012f\u0130\u0001\u0000\u0000\u0000\u0130"+
		"9\u0001\u0000\u0000\u0000\u0131\u0132\u0007\u0007\u0000\u0000\u0132;\u0001"+
		"\u0000\u0000\u0000#DHLX^fmw\u007f\u0086\u008c\u0093\u009e\u00a4\u00aa"+
		"\u00b4\u00ba\u00c0\u00cf\u00d1\u00e0\u00e4\u00f0\u00f3\u00f5\u00ff\u0101"+
		"\u0103\u0111\u0117\u011c\u0122\u0129\u012c\u012f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}