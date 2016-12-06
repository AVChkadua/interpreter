grammar Lang;
main: sentence*;
sentence: LEFT SEMI NEWLINE
    | RIGHT SEMI NEWLINE
    | TOP SEMI NEWLINE
    | BOTTOM SEMI NEWLINE
    | forEach
    | declarePointer SEMI NEWLINE
    | declareVariable SEMI NEWLINE
    | declareArray SEMI NEWLINE
    | whileCycle
    | zero
    | notZero
    | assign SEMI NEWLINE
    | function
    | functionCall SEMI NEWLINE;
expr: '$' variable
    | expr '*' expr
    | expr '/' expr
    | expr '%' expr
    | expr '+' expr
    | expr '-' expr
    | value
    | expr '!=' expr
    | expr '<=' expr
    | expr '>=' expr
    | '(' expr ')'
    | functionCall;
assign: variable '=' expr;
value: INT
    | variable;
variable: NAME
    | arrayElement
    | pointerValue
    | pointerAddress
    | declareVariable
    | declarePointer;
argument: INT
    | arrayElement
    | pointerValue
    | pointerAddress;
declareVariable: CONST? VALUE NAME;
declarePointer: CONST? POINTER CONST? TYPE? NAME;
declareArray: CONST? ARRAY_OF TYPE NAME index;
pointerValue: '*' NAME;
pointerAddress: '&' NAME
    | NAME;
arrayElement: NAME index;
index: '[' expr ']';
whileDeclaration: WHILE '(' expr ')';
finishDeclaration: FINISH;
zeroDeclaration: ZERO '(' expr ')';
notZeroDeclaration: NOT_ZERO '(' expr ')';
forEach: FOR_EACH NAME functionCall SEMI NEWLINE;
functionCall: NAME '(' (argument(', 'argument)*)? ')';
functionDeclaration: TYPE NAME '(' (parameter(', ' parameter)*)? ')';
body: '{' NEWLINE sentence* returnExpr '}';
whileCycle: whileDeclaration NEWLINE body NEWLINE finishDeclaration NEWLINE body NEWLINE;
zero: zeroDeclaration NEWLINE body NEWLINE;
notZero: notZeroDeclaration NEWLINE body NEWLINE;
function: functionDeclaration NEWLINE body NEWLINE;
parameter: TYPE NAME;
returnExpr: RETURN expr SEMI NEWLINE;
SPACE: (' ')+ {skip();};
TYPE: 'int'
    | 'byte'
    | 'long';
TOP: 'top';
BOTTOM: 'bottom';
LEFT: 'left';
RIGHT: 'right';
PORTAL: 'portal';
TELEPORT: 'teleport';
CONST: 'const';
VALUE: 'value';
POINTER: 'pointer';
ARRAY_OF: 'array_of';
WHILE: 'while';
FINISH: 'finish';
ZERO: 'zero?';
NOT_ZERO: 'notzero?';
NOT: 'not';
FOR_EACH: 'foreach';
RETURN: 'return';
SEMI: ';';
INT: [-]?[0-9]+;
NEWLINE: [\r\n]+;
NAME: [a-z][a-zA-Z0-9]*;

