parser grammar SnapiParser;
options { tokenVocab=SnapiLexer; }
// ============= program =================
prog: stat EOF
    ;

stat:  method_dec*                                          # FunDecStat
    |  method_dec* expr                                     # FunDecExprStat
    ;


method_dec: ident fun_proto                                 # MethodDec
          ;


fun_dec: ident fun_proto                                    # NormalFun
       | REC_TOKEN ident fun_proto                          # RecFun
       ;

fun_proto: LEFT_PAREN (fun_param (COMMA fun_param)*)? RIGHT_PAREN (COLON tipe)? EQUALS expr
         ;


fun_param: attr                                             # FunParamAttr
         | attr EQUALS expr                                    # FunParamAttrExpr
         ;

attr: ident COLON tipe
    | ident
    ;

type_attr: ident COLON tipe;

// the input parameters of a function
fun_ar: LEFT_PAREN fun_args? RIGHT_PAREN;
fun_args: fun_arg (COMMA fun_arg)*;
fun_arg: expr                                               # FunArgExpr
       | ident EQUALS expr                                     # NamedFunArgExpr
       ;

// lambda expression
fun_abs: fun_proto_lambda                                   # FunAbs
       | ident RIGHT_ARROW expr                                    # FunAbsUnnamed
       ;

fun_proto_lambda: LEFT_PAREN (fun_param (COMMA fun_param)*)? RIGHT_PAREN (COLON tipe)? RIGHT_ARROW expr
         ;

// ============= types =================
tipe: LEFT_PAREN tipe RIGHT_PAREN                                          # TypeWithParenType
    | tipe OR_TOKEN or_type RIGHT_ARROW tipe                       # OrTypeFunType
    | tipe OR_TOKEN or_type                                 # OrTypeType
    | primitive_types                                       # PrimitiveTypeType
    | UNDEFINED_TOKEN                                       # UndefinedTypeType
    | ident                                                 # TypeAliasType
    | record_type                                           # RecordTypeType
    | iterable_type                                         # IterableTypeType
    | list_type                                             # ListTypeType
    | LEFT_PAREN (tipe | attr) (COMMA (tipe | attr))* RIGHT_PAREN RIGHT_ARROW tipe  # FunTypeWithParamsType
    | tipe RIGHT_ARROW tipe                                        # FunTypeType
    | expr_type                                             # ExprTypeType
    ;

or_type: tipe OR_TOKEN or_type
       | tipe
       ;

record_type: RECORD_TOKEN LEFT_PAREN type_attr (COMMA type_attr)* RIGHT_PAREN;
iterable_type: COLLECTION_TOKEN LEFT_PAREN tipe RIGHT_PAREN;
list_type: LIST_TOKEN LEFT_PAREN tipe RIGHT_PAREN;
expr_type: TYPE_TOKEN tipe;

// ========== expressions ============
expr: LEFT_PAREN expr RIGHT_PAREN                                          # ParenExpr
    | number                                                # NumberExpr
    | if_then_else                                          # IfThenElseExpr
    | lists                                                 # ListExpr
    | records                                               # RecordExpr
    | bool_const                                            # BoolConstExpr
    | NULL_TOKEN                                            # NullExpr
    | START_TRIPLE_QUOTE TRIPLE_QUOTED_STRING_CONTENT* END_TRIPLE_QUOTE               # TrippleStringExpr
    | STRING                                                # StringExpr
    | ident                                                 # IdentExpr
    | expr fun_ar                                           # FunAppExpr
    | NOT_TOKEN expr                                        # NotExpr
    | expr AND_TOKEN expr                                   # AndExpr
    | expr OR_TOKEN expr                                    # OrExpr
    | <assoc=right> expr DOT ident fun_ar?                  # ProjectionExpr  // projection
    | expr compare_tokens expr                              # CompareExpr
    | MINUS_TOKEN expr                                      # MinusUnaryExpr
    | PLUS_TOKEN expr                                       # PlusUnaryExpr
    | expr MUL_TOKEN expr                                   # MulExpr
    | expr DIV_TOKEN expr                                   # DivExpr
    | expr MOD_TOKEN expr                                   # ModExpr
    | expr PLUS_TOKEN expr                                  # PlusExpr
    | expr MINUS_TOKEN expr                                 # MinusExpr
    | expr fun_ar                                           # FunAppExpr
    | let                                                   # LetExpr
    | fun_abs                                               # FunAbsExpr
    | expr_type                                             # ExprTypeExpr  // to check if this works correctly with recor(a:int)
    // | expr DOT  {notifyErrorListeners("Incomplete projection");}
    ;

let: LET_TOKEN let_left IN_TOKEN expr;

let_left: let_decl (COMMA let_decl)*
        // | let_decl (let_decl)* {notifyErrorListeners("Missing COMMA");}
        ;

let_decl: let_bind
        | fun_dec
        ;

let_bind: ident EQUALS expr
        | ident COLON tipe EQUALS expr
        ;

if_then_else: IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN expr
            // | IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN {notifyErrorListeners("Missing else expr");}
            // | IF_TOKEN expr {notifyErrorListeners("Missing then body");}
            ;

lists: LEFT_SQ_BR (lists_element)? RIGHT_SQ_BR;
lists_element: expr (COMMA expr)*;

records: LEFT_CUR_BR (record_elements)? RIGHT_CUR_BR;
record_elements: record_element (COMMA record_element)* ;
record_element: ident COLON expr
              | expr
              ;

number: BYTE
      | SHORT
      | INTEGER
      | LONG
      | FLOAT
      | DECIMAL
      | DOUBLE
      ;

primitive_types : BOOL_TOKEN
                | STRING_TOKEN
                | LOCATION_TOKEN
                | BINARY_TOKEN
                | DATE_TOKEN
                | TIME_TOKEN
                | INTERVAL_TOKEN
                | TIMESTAMP_TOKEN
                | BYTE_TOKEN
                | SHORT_TOKEN
                | INT_TOKEN
                | LONG_TOKEN
                | FLOAT_TOKEN
                | DOUBLE_TOKEN
                | DECIMAL_TOKEN
                ;
// Compare
compare_tokens: EQ_TOKEN
              | NEQ_TOKEN
              | LE_TOKEN
              | LT_TOKEN
              | GE_TOKEN
              | GT_TOKEN
              ;

bool_const: TRUE_TOKEN
          | FALSE_TOKEN
          ;

ident: NON_ESC_IDENTIFIER
     | ESC_IDENTIFIER
     ;


