lexer grammar PsSQLCommentsLexer;

LINE_COMMENT_START: '--';
MULTI_LINE_COMMENT_START: '/*';
MULTI_LINE_COMMENT_END: '*/';

TYPE_KW: '@type';
DEFAULT_KW: '@default';
PARAM_KW: '@param';
UNKNOWN_TOKEN: '@' .*?;

NAME: [_a-zA-Z] [_a-zA-Z0-9]*;

ESC_EXPR: '`' .*? '`';
IDENTED: ' '? [\t]+ ;
WS : [ \t\r\n]+ -> skip;




// For cace insensitive. you can write things like fragment
// C A S E and will much any case like CaSe or CASE or case
// fragment A:[aA];
// fragment B:[bB];
// fragment C:[cC];
// fragment D:[dD];
// fragment E:[eE];
// fragment F:[fF];
// fragment G:[gG];
// fragment H:[hH];
// fragment I:[iI];
// fragment J:[jJ];
// fragment K:[kK];
// fragment L:[lL];
// fragment M:[mM];
// fragment N:[nN];
// fragment O:[oO];
// fragment P:[pP];
// fragment Q:[qQ];
// fragment R:[rR];
// fragment S:[sS];
// fragment T:[tT];
// fragment U:[uU];
// fragment V:[vV];
// fragment W:[wW];
// fragment X:[xX];
// fragment Y:[yY];
// fragment Z:[zZ];


// STRING_TOKEN: 'string';
// BYTE_TOKEN:  'byte';
// SHORT_TOKEN: 'short';
// INT_TOKEN: 'int';
// LONG_TOKEN: 'long';
// FLOAT_TOKEN: 'float';
// DOUBLE_TOKEN: 'double';
// DECIMAL_TOKEN: 'decimal';
// BOOL_TOKEN: 'bool';
// TIME_TOKEN: 'time';
// DATE_TOKEN: 'date';
// TIMESTAMP_TOKEN: 'timestamp';

// // Keywords
// LIBRARY_TOKEN: 'library';
// PACKAGE_TOKEN: 'package';

// LET_TOKEN: 'let';
// IN_TOKEN: 'in';

// REC_TOKEN: 'rec';

// UNDEFINED_TOKEN: 'undefined';

// IF_TOKEN: 'if';
// THEN_TOKEN: 'then';
// ELSE_TOKEN: 'else';

// NULL_TOKEN: 'null';

// // Numbers

// BYTE: INTEGER_FR B;
// SHORT: INTEGER_FR S;
// INTEGER: INTEGER_FR;
// LONG: INTEGER_FR L;
// FLOAT: (FLOAT_FR | INTEGER_FR) F;
// DOUBLE: (FLOAT_FR | INTEGER_FR) D?;
// DECIMAL: (FLOAT_FR | INTEGER_FR) Q;

// // Binary Exp

// EQ_TOKEN: '==';
// NEQ_TOKEN: '!=';
// LE_TOKEN: '<=';
// LT_TOKEN: '<';
// GE_TOKEN: '>=';
// GT_TOKEN: '>';

// // Arithmetic
// PLUS_TOKEN: '+';
// MINUS_TOKEN: '-';
// MUL_TOKEN: '*';
// DIV_TOKEN: '/';
// MOD_TOKEN: '%';

// AND_TOKEN: 'and';
// OR_TOKEN:  'or';

// NOT_TOKEN: 'not';

// // Boolean
// TRUE_TOKEN: 'true';
// FALSE_TOKEN: 'false';

// // Strings
// STRING: '"' (UNICODE | ESC | ~["\\])* '"';
// START_TRIPLE_QUOTE: '"""' -> pushMode(INSIDE_TRIPLE_QUOTE);

// // Identifiers
// NON_ESC_IDENTIFIER: [_a-zA-Z] [_a-zA-Z0-9]*;
// ESC_IDENTIFIER: '`' .*? '`';
// WS : [ \t\r\n]+ -> skip;
// LINE_COMMENT : '//' ~('\n'|'\r')* ('\r'? '\n' | EOF) -> channel(HIDDEN) ;

// fragment DIGIT: [0-9];
// fragment EXPONENT: E [+-]? DIGIT+;
// fragment ESC: '\\' (["\\/'bfnrt]) ;
// UNICODE: '\\u' HEX HEX HEX HEX;
// HEX: [0-9a-fA-F];
// fragment INTEGER_FR: DIGIT+;
// fragment FLOAT_FR: DIGIT+ '.' DIGIT* EXPONENT?;


// LEFT_PAREN: '(';
// RIGHT_PAREN: ')';
// COLON: ':';
// COMMA: ',';
// EQUALS: '=';
// RIGHT_ARROW: '->';
// DOT: '.';
// LEFT_CUR_BR: '{';
// RIGHT_CUR_BR: '}';
// LEFT_SQ_BR: '[';
// RIGHT_SQ_BR: ']';

// BINARY_CONST: BINARY_PREFIX [0-9a-fA-F]*;
// fragment BINARY_PREFIX: '0x';

// NULLABLE_TOKEN: '@null';
// TRYABLE_TOKEN: '@try';
// DOLLAR_TOKEN: '$';

// // Switching context to triple quotes, will be usefull for string interpolation too
// mode INSIDE_TRIPLE_QUOTE;
// TRIPLE_QUOTED_STRING_CONTENT : '"' '"'? ~["]  // Match one or two quotes followed by a non-quote
//                              | ~["]           // Match any character that is not a quote
//                              ;
// TRIPLE_QUOTE_END_2: '"""""' -> popMode;
// TRIPLE_QUOTE_END_1: '""""' -> popMode;
// TRIPLE_QUOTE_END_0: '"""' -> popMode;
