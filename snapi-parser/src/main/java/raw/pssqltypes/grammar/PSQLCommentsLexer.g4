lexer grammar PSQLCommentsLexer;

LINE_COMMENT_START: '--';

MULTI_LINE_COMMENT_START: '/*';
MULTI_LINE_COMMENT_END: '*/';

TYPE_KW: '@type';
DEFAULT_KW: '@default';
PARAM_KW: '@param';
RETURN_KW: '@return';
UNKNOWN_TOKEN: '@' .*?;

NAME: [_a-zA-Z] [_a-zA-Z0-9]*;
TYPE: [a-zA-Z] [a-zA-Z]*;
TYPE_WITH_PAREN: [a-zA-Z] [a-zA-Z0-9]* '(' .*? ')';


LINE_COMMENT_END: '\n' | '\r' | '\r\n';

WS : [ \t]+ -> skip;