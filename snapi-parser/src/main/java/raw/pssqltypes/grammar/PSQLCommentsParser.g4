parser grammar PSQLCommentsParser;
options { tokenVocab=PSQLCommentsLexer; }
// ============= program =================
prog: code EOF
    ;

code: (comment)*
    ;

comment: param_comment          #paramComment
       | type_comment           #typeComment
    //    | return_comment         #returnComment
    //    | default_comment        #defaultComment
    //    | unknown_type_comment   #unknownTypeComment
    //    | normal_comment         #normalComment
       ;

param_comment: LINE_COMMENT_START PARAM_KW NAME (~LINE_COMMENT_END)*? LINE_COMMENT_END
             ;

type_comment: LINE_COMMENT_START TYPE_KW NAME (~LINE_COMMENT_END)*? LINE_COMMENT_END
            ;

default_comment: LINE_COMMENT_START DEFAULT_KW NAME (~LINE_COMMENT_END)*? LINE_COMMENT_END
            ;

return_comment: LINE_COMMENT_START RETURN_KW (~LINE_COMMENT_END)*? LINE_COMMENT_END
            ;

unknown_type_comment: LINE_COMMENT_START UNKNOWN_TOKEN (~LINE_COMMENT_END)*? LINE_COMMENT_END
                    ;

normal_comment: LINE_COMMENT_START (~LINE_COMMENT_END)*? LINE_COMMENT_END
    ;

stmt: .+? | (~('--'))* LINE_COMMENT_END
          ;

// stmt: SELECT .*? FROM .*? (WHERE .*?)?
//     ;


