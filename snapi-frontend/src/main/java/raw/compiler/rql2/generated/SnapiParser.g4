parser grammar SnapiParser;
options { tokenVocab=SnapiLexer; }
// ============= program =================
prog: stat EOF
    ;

stat:  method_dec*                                              # FunDecStat
    |  method_dec* expr                                         # FunDecExprStat
    ;


method_dec: ident fun_proto                                     # MethodDec
          ;


fun_dec: ident fun_proto                                        # NormalFun
       | REC_TOKEN ident fun_proto                              # RecFun
       ;

fun_proto: LEFT_PAREN (fun_param (COMMA fun_param)*)?
             COMMA? RIGHT_PAREN (COLON tipe)? EQUALS expr
         ;


fun_param: attr                                                 # FunParamAttr
         | attr EQUALS expr                                     # FunParamAttrExpr
         ;

attr: ident COLON tipe
    | ident
    ;

type_attr: ident COLON tipe;

// the input parameters of a function
fun_ar: LEFT_PAREN fun_args? RIGHT_PAREN;
fun_args: fun_arg (COMMA fun_arg)* COMMA?;
fun_arg: expr                                                   # FunArgExpr
       | ident EQUALS expr                                      # NamedFunArgExpr
       ;

// lambda expression
fun_abs: fun_proto_lambda                                       # FunAbs
       | ident RIGHT_ARROW expr                                 # FunAbsUnnamed
       ;

fun_proto_lambda: LEFT_PAREN (attr (COMMA attr)*)?
                    RIGHT_PAREN (COLON tipe)? RIGHT_ARROW expr # FunProtoLambdaMultiParam
                | attr (COLON tipe)? RIGHT_ARROW expr          # FunProtoLambdaSingleParam
                ;

// ============= types =================
tipe: LEFT_PAREN tipe RIGHT_PAREN                              # TypeWithParenType
    | tipe nullable_tryable                                    # NullableTryableType
    | tipe OR_TOKEN or_type RIGHT_ARROW tipe                   # OrTypeFunType
    | tipe OR_TOKEN or_type                                    # OrTypeType
    | primitive_types                                          # PrimitiveTypeType
    | record_type                                              # RecordTypeType
    | iterable_type                                            # IterableTypeType
    | list_type                                                # ListTypeType
    | ident                                                    # TypeAliasType
    | LEFT_PAREN (tipe | attr) (COMMA (tipe | attr))*
        COMMA? RIGHT_PAREN RIGHT_ARROW tipe                    # FunTypeWithParamsType
    | tipe RIGHT_ARROW tipe                                    # FunTypeType
    | expr_type                                                # ExprTypeType
    ;

or_type: tipe OR_TOKEN or_type
       | tipe
       ;

record_type: RECORD_TOKEN LEFT_PAREN type_attr
               (COMMA type_attr)* COMMA? RIGHT_PAREN;
iterable_type: COLLECTION_TOKEN LEFT_PAREN tipe RIGHT_PAREN;
list_type: LIST_TOKEN LEFT_PAREN tipe RIGHT_PAREN;
expr_type: TYPE_TOKEN tipe;

// ========== expressions ============
expr: LEFT_PAREN expr RIGHT_PAREN                                             # ParenExpr
    | package_idn_exp                                                         # PackageIdnExp
    | let                                                                     # LetExpr
    | fun_abs                                                                 # FunAbsExpr
    | expr_type                                                               # ExprTypeExpr
    | if_then_else                                                            # IfThenElseExpr
    | signed_number                                                           # SignedNumberExpr
    | bool_const                                                              # BoolConstExpr
    | NULL_TOKEN                                                              # NullExpr
    | string_literal                                                          # StringLiteralExpr
    | ident                                                                   # IdentExpr
    | expr fun_ar                                                             # FunAppExpr
    | lists                                                                   # ListExpr
    | records                                                                 # RecordExpr
    | <assoc=right> expr DOT ident fun_ar?                                    # ProjectionExpr
    | MINUS_TOKEN expr                                                        # MinusUnaryExpr
    | PLUS_TOKEN expr                                                         # PlusUnaryExpr
    | expr DIV_TOKEN expr                                                     # DivExpr
    | expr DIV_TOKEN {notifyErrorListeners("Missing right expression");}      # DivExpr
    | expr MUL_TOKEN expr                                                     # MulExpr
    | expr MUL_TOKEN {notifyErrorListeners("Missing right expression");}      # MulExpr
    | expr MOD_TOKEN expr                                                     # ModExpr
    | expr MOD_TOKEN {notifyErrorListeners("Missing right expression");}      # ModExpr
    | expr MINUS_TOKEN expr                                                   # MinusExpr
    | expr MINUS_TOKEN {notifyErrorListeners("Missing right expression");}    # MinusExpr
    | expr PLUS_TOKEN expr                                                    # PlusExpr
    | expr PLUS_TOKEN {notifyErrorListeners("Missing right expression");}     # PlusExpr
    | expr compare_tokens expr                                                # CompareExpr
    | expr compare_tokens {notifyErrorListeners("Missing right expression");} # CompareExpr
    | NOT_TOKEN expr                                                          # NotExpr
    | expr AND_TOKEN expr                                                     # AndExpr
    | expr AND_TOKEN {notifyErrorListeners("Missing right expression");}      # AndExpr
    | expr OR_TOKEN expr                                                      # OrExpr
    | expr OR_TOKEN {notifyErrorListeners("Missing right expression");}       # OrExpr
    ;

let: LET_TOKEN let_left IN_TOKEN expr // do not add a rule for missing expr here. it introduces ambiguity
   ;

let_left: let_decl (COMMA let_decl)* multiple_commas
        | let_decl {notifyErrorListeners("Missing ','");} (let_decl)*
        ;

multiple_commas: COMMA (COMMA)+ {notifyErrorListeners("Occurence of extra commas");}
               | COMMA?
               ;

let_decl: let_bind                                             # LetBind
        | fun_dec                                              # LetFunDec
        ;

let_bind: ident EQUALS expr
        | ident COLON tipe EQUALS expr
        | ident EQUALS {notifyErrorListeners("Missing expression binding");}
        | ident COLON tipe EQUALS {notifyErrorListeners("Missing expression binding");}
        ;

if_then_else: IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN expr
            | IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN {notifyErrorListeners("Missing else expression");}
            | IF_TOKEN expr {notifyErrorListeners("Missing then body");}
            ;

lists: LEFT_SQ_BR (lists_element)? RIGHT_SQ_BR;
lists_element: expr (COMMA expr)* COMMA?;

records: LEFT_CUR_BR (record_elements)? RIGHT_CUR_BR;
record_elements: record_element (COMMA record_element)* COMMA?;
record_element: ident COLON expr
              | expr
              ;

signed_number: (MINUS_TOKEN | PLUS_TOKEN)? number;

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
                | UNDEFINED_TOKEN
                ;

// ============= string =================
string_literal: STRING
              | triple_string_literal;

triple_string_literal: START_TRIPLE_QUOTE (TRIPLE_QUOTED_STRING_CONTENT)*
                              (TRIPLE_QUOTE_END_2
                              | TRIPLE_QUOTE_END_1
                              | TRIPLE_QUOTE_END_0);

// ============= compare =================
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
     | primitive_types
     | LIST_TOKEN
     | RECORD_TOKEN
     | COLLECTION_TOKEN
     ;

// =============== Internal parser ==================
package_idn_exp: DOLLAR_TOKEN PACKAGE_TOKEN LEFT_PAREN string_literal RIGHT_PAREN;

nullable_tryable: LEFT_PAREN nullable_tryable RIGHT_PAREN
                | NULLABLE_TOKEN TRYABLE_TOKEN
                | TRYABLE_TOKEN NULLABLE_TOKEN
                | NULLABLE_TOKEN
                | TRYABLE_TOKEN
                ;