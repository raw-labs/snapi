parser grammar PsSQLCommentsParser;
options { tokenVocab=PsSQLCommentsLexer; }
// ============= program =================
prog: stat EOF
    ;

  ;