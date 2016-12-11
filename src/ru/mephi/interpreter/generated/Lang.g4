grammar Lang;
@header {
package ru.mephi.interpreter.generated;
}
main: sentence*;
sentence: forEach #ForEachCycle
    | declarePointer SEMI NEWLINE #PointerDeclaration
    | declareVariable SEMI NEWLINE #VariableDeclaration
    | declareArray SEMI NEWLINE #ArrayDeclaration
    | whileCycling #WhileCycle
    | zero #IfZero
    | notZero #IfNotZero
    | assign SEMI NEWLINE #Assigning
    | funcImpl #FunctionImplementation
    | funcCall SEMI NEWLINE #FunctionCall
    | LEFT SEMI NEWLINE #MoveLeft
    | RIGHT SEMI NEWLINE #MoveRight
    | TOP SEMI NEWLINE #MoveTop
    | BOTTOM SEMI NEWLINE #MoveBottom
    | BREAK SEMI NEWLINE #Breaking
    | returnExpr #Returning
    ;
expr: '(' expr ')' #BracedExpr
    | '$' variable #Length
    | expr op=('*'|'/'|'%') expr #MultiOp
    | expr op=('+'|'-') expr #AddOp
    | value #Const
    | expr op=('!='|'<='|'>='|'==') expr #Comparing
    | funcCall #Call
    ;
assign: variable '=' expr;
value: INT #ConstValue
    | arrayElement #ArrayElementValue
    | pointerValue #PointerValueValue
    | pointerAddress #PointerAddressValue
    ;
variable: NAME #NamedVariable
    | arrayElement #ArrayElementVariable
    | pointerValue #PointerValueVariable
    | pointerAddress #PointerAddressVariable
    | declareVariable #DeclaredVariable
    | declarePointer #DeclaredPointer
    ;
argument: INT
    | arrayElement
    | pointerValue
    | pointerAddress;
declareVariable: CONST? VALUE TYPE NAME;
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
forEach: FOR_EACH NAME func=funcCall SEMI NEWLINE;
funcCall: NAME '(' (argument(', 'argument)*)? ')';
functionDeclaration: TYPE funcName=NAME '(' (parameter(', ' parameter)*)? ')';
body: '{' NEWLINE sentence* '}';
whileCycling: whileDeclaration NEWLINE body NEWLINE finishDeclaration NEWLINE body NEWLINE;
zero: zeroCond=zeroDeclaration NEWLINE body NEWLINE;
notZero: notZeroCond=notZeroDeclaration NEWLINE body NEWLINE;
funcImpl: functionDeclaration NEWLINE body NEWLINE;
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
BREAK: 'break';
SEMI: ';';
INT: [-]?[0-9]+;
NEWLINE: [\r\n]+;
NAME: [a-z][a-zA-Z0-9]*;

