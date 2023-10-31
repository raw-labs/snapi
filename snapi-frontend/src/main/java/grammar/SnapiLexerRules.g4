lexer grammar SnapiLexerRules;

// Types

TYPE_TOKEN: 'type';

BOOL_TOKEN: 'bool';
STRING_TOKEN: 'string';
LOCATION_TOKEN: 'location';
BINARY_TOKEN: 'binary';
BYTE_TOKEN:  'byte';
SHORT_TOKEN: 'short';
INT_TOKEN: 'int';
LONG_TOKEN: 'long';
FLOAT_TOKEN: 'float';
DOUBLE_TOKEN: 'double';
DECIMAL_TOKEN: 'decimal';
DATE_TOKEN: 'date';
TIME_TOKEN: 'time';
INTERVAL_TOKEN: 'interval';
TIMESTAMP_TOKEN: 'timestamp';
RECORD_TOKEN: 'record';
COLLECTION_TOKEN: 'collection';
LIST_TOKEN: 'list';

// Keywords
LIBRARY_TOKEN: 'library';
PACKAGE_TOKEN: 'package';

LET_TOKEN: 'let';
IN_TOKEN: 'in';

REC_TOKEN: 'rec';

UNDEFINED_TOKEN: 'undefined';

IF_TOKEN: 'if';
THEN_TOKEN: 'then';
ELSE_TOKEN: 'else';

NULL_TOKEN: 'null';

// Numbers

BYTE: MINUS_TOKEN? INTEGER_FR B;
SHORT: MINUS_TOKEN? INTEGER_FR S;
INTEGER: MINUS_TOKEN? INTEGER_FR;
LONG: MINUS_TOKEN? INTEGER_FR L;
FLOAT: MINUS_TOKEN? (FLOAT_FR | INTEGER_FR) F;
DOUBLE: MINUS_TOKEN? (FLOAT_FR | INTEGER_FR) D?;
DECIMAL: MINUS_TOKEN? (FLOAT_FR | INTEGER_FR) Q;

// Binary Exp

EQ_TOKEN: '==';
NEQ_TOKEN: '!=';
LE_TOKEN: '<=';
LT_TOKEN: '<';
GE_TOKEN: '>=';
GT_TOKEN: '>';

// Arithmetic
PLUS_TOKEN: '+';
MINUS_TOKEN: '-';
MUL_TOKEN: '*';
DIV_TOKEN: '/';
MOD_TOKEN: '%';

AND_TOKEN: 'and';
OR_TOKEN:  'or';

NOT_TOKEN: 'not';

// Boolean
TRUE_TOKEN: 'true';
FALSE_TOKEN: 'false';

// Strings
STRING: '"' (ESC | ~["\\])* '"';
TRIPPLE_STRING: '"""' .*? '"""';

// Identifiers
NON_ESC_IDENTIFIER: [_a-zA-Z] [_a-zA-Z0-9]*;
ESC_IDENTIFIER: '`' .*? '`';
WS : [ \t\r\n]+ -> skip;
LINE_COMMENT : '//' ~('\n'|'\r')* ('\r'? '\n' | EOF) -> channel(HIDDEN) ;

fragment DIGIT: [0-9];
fragment EXPONENT: E [+-]? DIGIT+;
fragment ESC: '\\' (["\\/bfnrt]) ;
fragment INTEGER_FR: DIGIT+;
fragment FLOAT_FR: DIGIT+ '.' DIGIT* EXPONENT?;
// For cace insensitive. you can write things like fragment
// C A S E and will much any case like CaSe or CASE or case
fragment A:[aA];
fragment B:[bB];
fragment C:[cC];
fragment D:[dD];
fragment E:[eE];
fragment F:[fF];
fragment G:[gG];
fragment H:[hH];
fragment I:[iI];
fragment J:[jJ];
fragment K:[kK];
fragment L:[lL];
fragment M:[mM];
fragment N:[nN];
fragment O:[oO];
fragment P:[pP];
fragment Q:[qQ];
fragment R:[rR];
fragment S:[sS];
fragment T:[tT];
fragment U:[uU];
fragment V:[vV];
fragment W:[wW];
fragment X:[xX];
fragment Y:[yY];
fragment Z:[zZ];
//fragment NUMERIC_TYPE_SUFFIX: [fdlsbq];
//fragment UNICODE: 'u' HEX HEX HEX HEX ;
//fragment HEX: [0-9a-fA-F] ;