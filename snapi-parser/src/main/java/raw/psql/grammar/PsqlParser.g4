parser grammar PsqlParser;
options { tokenVocab=PsqlLexer; }
// ============= program =================
prog: code EOF
    ;

code: (comment)* stmt (SEMICOLON (comment)* stmt)* (comment)*;

comment: singleline_comment
       | multiline_comment
       ;

multiline_comment: MULTI_LINE_COMMENT_START (multiline_value_comment)* MULTI_LINE_COMMENT_END
                 ;

multiline_value_comment: multiline_param_comment
                       | multiline_type_comment
                       | multiline_default_comment
                       | multiline_return_comment
                       | multiline_unknown_type_comment
                       | multiline_normal_comment_value
                       ;



singleline_comment: LINE_COMMENT_START singleline_value_comment (SL_LINE_COMMENT_END | EOF)
                   ;

singleline_value_comment: singleline_param_comment          #paramComment
                        | singleline_type_comment           #typeComment
                        | singleline_return_comment         #returnComment
                        | singleline_default_comment        #defaultComment
                        | singleline_unknown_type_comment   #unknownTypeComment
                        | singleline_normal_comment_value          #normalComment
                        ;

// single line comments
singleline_param_comment:  SL_PARAM_KW (SL_WORD)+
                    ;

singleline_type_comment: SL_TYPE_KW SL_WORD SL_WORD
                   ;

singleline_default_comment: SL_DEFAULT_KW SL_WORD SL_WORD
                      ;

singleline_return_comment: SL_RETURN_KW (SL_WORD)+
                     ;

singleline_unknown_type_comment: SL_UNKNOWN_TOKEN (SL_WORD)*
                           ;

singleline_normal_comment_value: (SL_WORD)*
             ;

// Multiline comments
multiline_param_comment: ML_PARAM_KW (ML_WORD)+
                       ;

multiline_type_comment: ML_TYPE_KW ML_WORD ML_WORD
                      ;

multiline_default_comment: ML_DEFAULT_KW ML_WORD ML_WORD
                         ;

multiline_return_comment: ML_RETURN_KW (ML_WORD)+
                        ;

multiline_unknown_type_comment: ML_UNKNOWN_TOKEN (ML_WORD)*
                              ;

multiline_normal_comment_value: (ML_WORD)+
                      ;

stmt: (stmt_items)+;

stmt_items: proj                  #projStmt
          | literal               #literalStmt
          | keyword               #keywordStmt
          | binary_exp            #binaryExpStmt
          | idnt                  #idntStmt
          | param                 #paramStmt
          | with_comma_sep        #withCommaSepStmt
          | abstract_stmt_item    #abstractStmtItem
          ;

with_comma_sep: idnt (COMMA idnt)*;

abstract_stmt_item: WORD | STAR;

keyword: KEYWORD;

operator: OPERATOR;

proj: idnt (DOT idnt)+;

idnt: (WORD | DOUBLE_QUOTED_STRING);

param: PARAM | param_with_tipe;

binary_exp: (idnt | param | literal) operator (idnt | param | literal);

literal: string_literal | integer | floating_point | boolean_literal;
string_literal: SINGLE_QUOTED_STRING;
integer: NO_FLOATING_NUMBER;
floating_point: FLOATING_POINT;
boolean_literal: TRUE | FALSE;

tipe: PSQL_TYPE (L_PAREN (integer (COMMA integer)?) R_PAREN)? WITH_TIME_ZONE?;
param_with_tipe: PARAM DOUBLE_COLON tipe;