grammar Snapi;
import SnapiLexerRules;

// ============= program =================
prog: stat EOF
    ;

stat:  method_dec*                                           # FunDecStat
    |  method_dec* expr                                      # FunDecExprStat
    ;


method_dec: IDENT fun_proto '=' expr                         # MethodDec
          ;


fun_dec: IDENT fun_proto '=' expr                           # NormalFun
       | REC_TOKEN IDENT fun_proto '=' expr                 # RecFun
       ;

fun_proto: '(' (fun_param (',' fun_param)*)? ')'            # FunProtoWithoutType
         | '(' (fun_param (',' fun_param)*)? ')' ':' type   # FunProtoWithType
         ;

fun_param: attr                                             # FunParamAttr
         | attr '=' expr                                    # FunParamAttrExpr
         ;

attr: IDENT ':' type
    | IDENT
    ;

type_attr: IDENT ':' type;

// the input parameters of a function
fun_ar: '(' fun_args? ')';
fun_args: fun_arg (',' fun_arg)*;
fun_arg: expr                                               # FunArgExpr
       | IDENT '=' expr                                     # NamedFunArgExpr
       ;

// lambda expression
fun_abs: fun_proto '->' expr                                # FunAbs
       | IDENT '->' expr                                    # FunAbsUnnamed
       ;

// ============= types =================
type: '(' type ')'                                          # TypeWithParenType
    | type OR_TOKEN or_type '->' type                       # OrTypeFunType
    | type OR_TOKEN or_type                                 # OrTypeType
    | primitive_types                                       # PrimitiveTypeType
    | UNDEFINED_TOKEN                                       # UndefinedTypeType
    | IDENT                                                 # TypeAliasType
    | record_type                                           # RecordTypeType
    | iterable_type                                         # IterableTypeType
    | list_type                                             # ListTypeType
    | '(' (type | attr) (',' (type | attr))* ')' '->' type  # FunTypeWithParamsType
    | type '->' type                                        # FunTypeType
    | expr_type                                             # ExprTypeType
    ;

or_type: type OR_TOKEN or_type
       | type
       ;

record_type: RECORD_TOKEN '(' type_attr (',' type_attr)* ')';
iterable_type: COLLECTION_TOKEN '(' type ')';
list_type: LIST_TOKEN '(' type ')';
expr_type: TYPE_TOKEN type;

// ========== expressions ============
expr: '(' expr ')'                                          # ParenExpr
    | number                                                # NumberExpr
    | if_then_else                                          # IfThenElseExpr
    | lists                                                 # ListExpr
    | records                                               # RecordExpr
    | bool_const                                            # BoolConstExpr
    | NULL_TOKEN                                            # NullExpr
    | TRIPPLE_STRING                                        # TrippleStringExpr
    | STRING                                                # StringExpr
    | IDENT                                                 # IdentExpr
    | expr fun_ar                                           # FunAppExpr
    | NOT_TOKEN expr                                        # NotExpr
    | expr AND_TOKEN expr                                   # AndExpr
    | expr OR_TOKEN expr                                    # OrExpr
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
    | <assoc=right> expr '.' IDENT fun_ar?                  # ProjectionExpr  // projection
    // | expr '.'  {notifyErrorListeners("Incomplete projection");}
    ;

let: LET_TOKEN let_left IN_TOKEN expr;

let_left: let_decl (',' let_decl)*
        // | let_decl (let_decl)* {notifyErrorListeners("Missing ','");}
        ;

let_decl: let_bind
        | fun_dec
        ;

let_bind: IDENT '=' expr
        | IDENT ':' type '=' expr
        ;

if_then_else: IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN expr
            // | IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN {notifyErrorListeners("Missing else expr");}
            // | IF_TOKEN expr {notifyErrorListeners("Missing then body");}
            ;

lists: '[' (lists_element)? ']';
lists_element: expr (',' expr)*;

records: '{' (record_elements)? '}';
record_elements: record_element (',' record_element)* ;
record_element: IDENT ':' expr
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


