lexer grammar PsqlLexer;

// Comments
LINE_COMMENT_START: '--' -> pushMode(INSIDE_SINGLE_LINE_COMMENT);

MULTI_LINE_COMMENT_START: '/*' -> pushMode(INSIDE_MULTI_LINE_COMMENT);

QUOTE: '\'';
DOT: '.';
STAR: '*';

PARAM: ':' WORD;
WORD: [_a-zA-Z] [_a-zA-Z0-9]*;

// Strings
DOUBLE_QUOTED_STRING: '"' (ESC | ~["\\])* '"';
SINGLE_QUOTED_STRING: '\'' (ESC | ~["\\])* '\'';
TICKS_QUOTED_STRING: '`' (ESC | ~["\\])* '`';

fragment ESC: '\\' (["\\/'bfnrt]) ;
WS : [ \t\r\n]+ -> skip;

mode INSIDE_SINGLE_LINE_COMMENT;
SL_TYPE_KW: '@type';
SL_DEFAULT_KW: '@default';
SL_PARAM_KW: '@param';
SL_RETURN_KW: '@return';
SL_UNKNOWN_TOKEN: '@' .*?;
SL_WORD: [a-zA-Z0-9!@#$%^&*_+'"`.]+ '(' .*? ')' | [a-zA-Z0-9!@#$%^&*_+'"`.]+;

SL_LINE_COMMENT_END: ('\n' | '\r' | '\r\n') -> popMode;

SL_WS : [ ]+ -> skip;


mode INSIDE_MULTI_LINE_COMMENT;
ML_TYPE_KW: '@type';
ML_DEFAULT_KW: '@default';
ML_PARAM_KW: '@param';
ML_RETURN_KW: '@return';
ML_UNKNOWN_TOKEN: '@' .*?;
ML_WORD: [a-zA-Z0-9!@#$%^&*_+'"`.]+ '(' .*? ')' | [a-zA-Z0-9!@#$%^&*_+'"`.]+;

ML_WS : [ \t\r\n]+ -> skip;

MULTI_LINE_COMMENT_END: '*/' -> popMode;