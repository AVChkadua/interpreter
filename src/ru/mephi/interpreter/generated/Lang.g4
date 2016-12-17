grammar Lang;
@header {
package ru.mephi.interpreter.generated;
}
main: sentence*;
sentence: assign SEMI #Assigning
    | forEach SEMI #ForEachCycle
    | declarePointer SEMI #PointerDeclaration
    | declareVariable SEMI #VariableDeclaration
    | declareArray SEMI #ArrayDeclaration
    | whileCycling #WhileCycle
    | zero #IfZero
    | notZero #IfNotZero
    | funcImpl #FunctionImplementation
    | funcCall SEMI #FunctionCall
    | LEFT SEMI #MoveLeft
    | RIGHT SEMI #MoveRight
    | TOP SEMI #MoveTop
    | BOTTOM SEMI #MoveBottom
    | BREAK SEMI #Breaking
    | returnExpr SEMI #Returning
    | body #BodyPart
    | print SEMI #Write
    ;
expr: '(' expr ')' #BracedExpr
    | '$' variableWithLength #Length
    | expr op=('*'|'/'|'%') expr #MultiOp
    | expr op=('+'|'-') expr #AddOp
    | value #Const
    | expr op=('!='|'<='|'>='|'==') expr #Comparing
    | funcCall #Call
    ;
assign: declareVariable '=' expr #JustDeclaredVariable
    | declarePointer '=' expr #JustDeclaredPointer
    | declareArray '=' expr #JustDeclaredArray
    | variable '=' expr #ExistingVariable
    ;
value: INT #ConstValue
    | arrayElement #ArrayElementValue
    | NAME #NamedVariableValue
    | pointerValue #PointerValueValue
    | variableAddress #VariableAddressValue
    ;
variableWithLength: NAME;
variable: NAME #NamedVariable
    | arrayElement #ArrayElementVariable
    | pointerValue #PointerValueVariable
    | variableAddress #PointerAddressVariable
    ;
argument: INT
    | arrayElement
    | pointerValue
    | variableAddress;
declareVariable: CONST? VALUE TYPE NAME;
declarePointer: CONST? POINTER CONST? TYPE NAME;
declareArray: CONST? ARRAY_OF TYPE NAME;
pointerValue: '*' NAME;
variableAddress: '&' NAME;
arrayElement: NAME index;
index: '[' expr ']';
whileDeclaration: WHILE '(' expr ')';
finishDeclaration: FINISH;
zeroDeclaration: ZERO '(' expr ')';
notZeroDeclaration: NOT_ZERO '(' expr ')';
forEach: FOR_EACH NAME func=funcCall;
funcCall: NAME '(' (argument(', 'argument)*)? ')';
functionDeclaration: TYPE funcName=NAME '(' (parameter(', ' parameter)*)? ')';
body: '{' sentence* '}';
whileCycling: whileDeclaration body finishDeclaration body;
zero: zeroCond=zeroDeclaration body;
notZero: notZeroCond=notZeroDeclaration body;
funcImpl: functionDeclaration body;
parameter: TYPE NAME;
returnExpr: RETURN expr;
print: PRINT expr;
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
PRINT: 'print';
INT: [-]?[0-9]+;
NEWLINE: [\r\n]+ {skip();};
NAME: [a-z][a-zA-Z0-9]*;

