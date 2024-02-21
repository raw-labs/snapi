parser grammar PsqlParser;
options { tokenVocab=PsqlLexer; }
// ============= program =================
prog: code EOF
    ;

code: (comment | stmt)*
    ;

comment: single_line_comment
       | multiline_comment
       ;

multiline_comment: MULTI_LINE_COMMENT_START (multiple_value_comment)* MULTI_LINE_COMMENT_END
                 ;

multiple_value_comment: multiline_param_comment
                      | multiline_type_comment
                      | multiline_default_comment
                      | multiline_return_comment
                      | multiline_unknown_type_comment
                      | multiple_comment_value
                      ;



single_line_comment: LINE_COMMENT_START single_value_comment (SL_LINE_COMMENT_END | EOF)
                   ;

single_value_comment: single_param_comment          #paramComment
             | single_type_comment           #typeComment
             | single_return_comment         #returnComment
             | single_default_comment        #defaultComment
             | single_unknown_type_comment   #unknownTypeComment
             | normal_comment_value          #normalComment
             ;

// single line comments
single_param_comment:  SL_PARAM_KW (SL_WORD)+
                    ;

single_type_comment: SL_TYPE_KW SL_WORD SL_WORD
                   ;

single_default_comment: SL_DEFAULT_KW SL_WORD SL_WORD
                      ;

single_return_comment: SL_RETURN_KW (SL_WORD)+
                     ;

single_unknown_type_comment: SL_UNKNOWN_TOKEN (SL_WORD)*
                           ;

normal_comment_value: (SL_WORD)*
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

multiple_comment_value: (ML_WORD)+
                      ;

stmt: (WORD | proj | SINGLE_QUOTED_STRING | STAR)+;

proj: idnt (DOT idnt)*;

idnt: (WORD | DOUBLE_QUOTED_STRING | PARAM);

