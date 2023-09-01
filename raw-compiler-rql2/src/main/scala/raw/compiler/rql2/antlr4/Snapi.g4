grammar Snapi;
import SnapiLexerRules;

// ============= program =================
prog: stat EOF;

stat:  fun_dec*
    |  fun_dec* expr
    ;

// function definition
fun_dec : fun '=' expr;
fun: normal_fun
    | rec_fun
    ;
normal_fun: ident fun_proto ;
rec_fun: REC_TOKEN normal_fun ;
fun_proto: '(' fun_params? ')' ;
fun_params: fun_param (',' fun_param)*;

fun_param: attr
        | attr '=' expr
        ;

attr: ident ':' type;

// the input parameters of a function
fun_app: ident fun_ar ;
fun_ar: '(' fun_args? ')';
fun_args: fun_arg (',' fun_arg)*;
fun_arg: expr
    | ident '=' expr
    | fun_abs
    ;

// lambda expression
fun_abs: fun_proto '->' expr
       | ident '->' expr
       ;

// ============= types =================
type: pure_type (OR_TOKEN pure_type)*;
pure_type: premetive_type
    | UNDEFINED_TOKEN
    | ident // Type Alias
    | record_type
    | iterable_type
    | list_type
    | fun_type
    | expr_type
    ;


record_type: RECORD_TOKEN '(' attr (',' attr)* ')';
iterable_type: COLLECTION_TOKEN '(' type ')';
list_type: LIST_TOKEN '(' type ')';
expr_type: TYPE_TOKEN type; // Why do we have to use TOKEN_TYPE here? Why not just type?
typealias_type: TYPE_TOKEN type '=' type;

fun_type: '(' (fun_params | type)? ')' '->' type;


// ========== expressions ============
expr: number
    | if_then_else
    | lists
    | records
    | bool_const
    | NULL_TOKEN
    | TRIPPLE_STRING
    | STRING
    | '(' expr ')'
    | bool_const
    | ident
    | NOT_TOKEN expr
    | expr AND_TOKEN expr
    | expr OR_TOKEN expr
    | expr compare_tokens expr
    | MINUS_TOKEN expr
    | PLUS_TOKEN expr
    | expr MUL_TOKEN expr
    | expr DIV_TOKEN expr
    | expr MOD_TOKEN expr
    | expr PLUS_TOKEN expr
    | expr MINUS_TOKEN expr
    | fun_app
    | let
    | expr_type // to check if this works correctly with recor(a:int)
    | <assoc=right> expr '.' ident fun_ar?  // projection
    // | expr '.'  {notifyErrorListeners("Incomplete projection");}
    ;

let: LET_TOKEN let_left IN_TOKEN expr;

let_left: let_decl (',' let_decl)*
        // | let_decl (let_decl)* {notifyErrorListeners("Missing ','");}
        ;

let_decl: let_bind
        | fun_dec
        ;

let_bind: ident '=' expr | ident ':' type '=' expr;

if_then_else: IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN expr
            // | IF_TOKEN expr THEN_TOKEN expr ELSE_TOKEN {notifyErrorListeners("Missing else expr");}
            // | IF_TOKEN expr {notifyErrorListeners("Missing then body");}
            ;

lists: '[' (lists_element)? ']';
lists_element: expr (',' expr)*;

records: '{' (record_elements)? '}';
record_elements: record_element (',' record_element)* ;
record_element: ident ':' expr | expr;

ident: NON_ESC_IDENTIFIER | ESC_IDENTIFIER;

number: BYTE
    | SHORT
    | INTEGER
    | LONG
    | FLOAT
    | DOUBLE
    | DECIMAL;

number_type: BYTE_TOKEN
    | SHORT_TOKEN
    | INT_TOKEN
    | LONG_TOKEN
    | FLOAT_TOKEN
    | DOUBLE_TOKEN
    | DECIMAL_TOKEN;

premetive_type: BOOL_TOKEN
              | STRING_TOKEN
              | LOCATION_TOKEN
              | BINARY_TOKEN
              | number_type
              | temporal_type
              | REGEX_TOKEN;

temporal_type: DATE_TOKEN
    | TIME_TOKEN
    | INTERVAL_TOKEN
    | TIMESTAMP_TOKEN;


compare_tokens: EQ_TOKEN
             | NEQ_TOKEN
             | LE_TOKEN
             | LT_TOKEN
             | GE_TOKEN
             | GT_TOKEN;

bool_const: TRUE_TOKEN
          | FALSE_TOKEN;