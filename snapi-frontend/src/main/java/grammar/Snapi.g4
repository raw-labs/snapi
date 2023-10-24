grammar Snapi;
import SnapiLexerRules;

// ============= program =================
prog: stat EOF
    ;

stat:  fun_dec*                             # FunDecStat
    |  fun_dec* expr                        # FunDecExprStat
    ;

// function definition
fun_dec : fun '=' expr                      # FunDec
        ;

fun: normal_fun                             # NormalFun
   | rec_fun                                # RecFun
   ;

normal_fun: IDENT fun_proto                 # NormalFunProto
          ;

rec_fun: REC_TOKEN normal_fun               # RecFunProto
       ;

fun_proto: '(' fun_params? ')'              # FunProtoWithoutType
         | '(' fun_params? ')' ':' type     # FunProtoWithType
         ;

fun_params: fun_param (',' fun_param)*      # FunParams
          ;

fun_param: attr                             # FunParamAttr
         | attr '=' expr                    # FunParamAttrExpr
         ;

attr: IDENT ':' type                        # AttrWithType
    ;

// the input parameters of a function
fun_app: IDENT fun_ar ;
fun_ar: '(' fun_args? ')';
fun_args: fun_arg (',' fun_arg)*;
fun_arg: expr                               # FunArgExpr
    | IDENT '=' expr                        # NamedFunArgExpr
    ;

// lambda expression
fun_abs: fun_proto '->' expr                # FunAbs
       | IDENT '->' expr                    # FunAbsUnnamed
       ;

// ============= types =================
type: '(' type ')'                                          # TypeWithParenType
    | PRIMITIVE_TYPES                                       # PrimitiveTypeType
    | UNDEFINED_TOKEN                                       # UndefinedTypeType
    | IDENT                                                 # TypeAliasType
    | record_type                                           # RecordTypeType
    | iterable_type                                         # IterableTypeType
    | list_type                                             # ListTypeType
    | '(' (type | attr) (',' (type | attr))* ')' '->' type  # FunTypeWithParamsType
    | type '->' type                                        # FunTypeType
    | expr_type                                             # ExprTypeType
    ;

record_type: RECORD_TOKEN '(' attr (',' attr)* ')';
iterable_type: COLLECTION_TOKEN '(' type ')';
list_type: LIST_TOKEN '(' type ')';
expr_type: TYPE_TOKEN type;

// ========== expressions ============
expr: '(' expr ')'                         # ParenExpr
    | number                               # NumberExpr
    | if_then_else                         # IfThenElseExpr
    | lists                                # ListExpr
    | records                              # RecordExpr
    | BOOL_CONST                           # BoolConstExpr
    | NULL_TOKEN                           # NullExpr
    | TRIPPLE_STRING                       # TrippleStringExpr
    | STRING                               # StringExpr
//    | IDENT                                # IdentExpr // probably will be deleted
    | NOT_TOKEN expr                       # NotExpr
    | expr AND_TOKEN expr                  # AndExpr
    | expr OR_TOKEN expr                   # OrExpr
    | expr COMPARE_TOKENS expr             # CompareExpr
    | MINUS_TOKEN expr                     # MinusExpr
    | PLUS_TOKEN expr                      # PlusExpr
    | expr MUL_TOKEN expr                  # MulExpr
    | expr DIV_TOKEN expr                  # DivExpr
    | expr MOD_TOKEN expr                  # ModExpr
    | expr PLUS_TOKEN expr                 # PlusExpr
    | expr MINUS_TOKEN expr                # MinusExpr
    | fun_app                              # FunAppExpr
    | fun_abs                              # FunAbsExpr
    | let                                  # LetExpr
    | expr_type                            # ExprTypeExpr  // to check if this works correctly with recor(a:int)
    | <assoc=right> expr '.' IDENT fun_ar? # ProjectionExpr  // projection
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
      | DOUBLE
      | DECIMAL
      ;



