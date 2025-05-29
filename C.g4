grammar C;

program
    : (preprocessor | function | struct | global_declaration)* EOF
    ;

preprocessor
    : '#' 'include' ('<' filename '>' | '"' filename '"')
    | '#' 'define' ID (value | macro_function)?
    ;

macro_function
    : '(' ID (',' ID)* ')' value
    ;

value
    : NUMBER
    | STRING
    | CHAR
    | ID
    | expression
    ;

filename
    : ID ('.' ID)?
    ;

function
    : type ID '(' params? ')' block
    ;

params
    : param (',' param)*
    ;

param
    : type ID
    ;

struct
    : 'struct' ID '{' struct_field* '}' ';'
    ;

struct_field
    : type ID ';'
    ;

global_declaration
    : type ID ('=' value)? ';'
    ;

block
    : '{' statement* '}'
    ;

statement
    : declaration ';'
    | assignment ';'
    | if_statement
    | for_loop
    | while_loop
    | return_statement ';'
    | block
    | expression ';'
    ;

declaration
    : type ID ('=' expression)?
    ;

assignment
    : ID '=' expression
    | struct_member_access '=' expression
    ;

if_statement
    : 'if' '(' expression ')' statement ('else' statement)?
    ;

for_loop
    : 'for' '(' (declaration | assignment)? ';' expression? ';' assignment? ')' statement
    ;

while_loop
    : 'while' '(' expression ')' statement
    ;

return_statement
    : 'return' expression?
    ;

expression
    : logical_or
    ;

logical_or
    : logical_and ('||' logical_and)*
    ;

logical_and
    : equality ('&&' equality)*
    ;

equality
    : relational (('==' | '!=') relational)*
    ;

relational
    : additive (('<' | '>' | '<=' | '>=') additive)*
    ;

additive
    : multiplicative (('+' | '-') multiplicative)*
    ;

multiplicative
    : shift (('*' | '/' | '%') shift)*
    ;

shift
    : bitwise_and (('<<' | '>>') bitwise_and)*
    ;

bitwise_and
    : bitwise_xor (('&') bitwise_xor)*
    ;

bitwise_xor
    : bitwise_or (('^') bitwise_or)*
    ;

bitwise_or
    : primary (('|') primary)*
    ;

primary
    : ID
    | NUMBER
    | STRING
    | CHAR
    | '(' expression ')'
    | function_call
    | struct_member_access
    ;

struct_member_access
    : ID '.' ID
    ;

function_call
    : ID '(' args? ')'
    ;

args
    : expression (',' expression)*
    ;

type
    : 'int'
    | 'float'
    | 'char'
    | 'void'
    | 'struct' ID
    | type '*'
    ;

// Лексерные правила
ID          : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER      : [0-9]+ ('.' [0-9]+)? ;
STRING      : '"' (~["\\\r\n] | '\\' .)* '"' ;
CHAR        : '\'' . '\'' ;
DOT         : '.' ;

// Пропускаемые токены
WS          : [ \t\r\n]+ -> skip ;
LINE_COMMENT: '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT: '/*' .*? '*/' -> skip ;
