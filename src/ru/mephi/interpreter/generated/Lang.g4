grammar Lang;
@header {
package ru.mephi.interpreter.generated;
}
main: funcImpl+;
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
    | PORTAL SEMI #CreatePortal
    | TELEPORT SEMI #Teleport
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
    | CAN_GO_LEFT #CanMoveLeft
    | CAN_GO_RIGHT #CanMoveRight
    | CAN_GO_TOP #CanMoveTop
    | CAN_GO_BOTTOM #CanMoveBottom
    | VISITED_LEFT #VisitedLeft
    | VISITED_RIGHT #VisitedRight
    | VISITED_TOP #VisitedTop
    | VISITED_BOTTOM #VisitedBottom
    | IS_AT_EXIT #IsAtExit
    | NOT_AT_EXIT #NotAtExit
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
    | variable;
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
CAN_GO_TOP: 'can_go_top';
CAN_GO_BOTTOM: 'can_go_bottom';
CAN_GO_LEFT: 'can_go_left';
CAN_GO_RIGHT: 'can_go_right';
VISITED_LEFT: 'visited_left';
VISITED_RIGHT: 'visited_right';
VISITED_TOP: 'visited_top';
VISITED_BOTTOM: 'visited_bottom';
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
IS_AT_EXIT: 'exit?';
NOT_AT_EXIT: 'not_exit?';
INT: [-]?[0-9]+;
NEWLINE: [\r\n]+ {skip();};
NAME: [a-z][a-zA-Z0-9]*;

