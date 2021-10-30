grammar Mx;

program: (subprogram)* EOF;

subprogram:funcDef | classDef ';' | varDef ';';

varDef: variableType Id ('=' expression)? (',' Id ('=' expression)?)*;

funcDef: returnType Id '('parameterList?')' suite;

constructfuncDef: Id '('')' suite;

classDef
        : Class Id '{'
        (varDef';'|funcDef|constructfuncDef)*
        '}'

        ;

suite: '{' statement* '}';

statement
         : suite                                                                #blockStmt
         | varDef ';'                                                           #varDefStmt
         | classDef ';'                                                         #classDefStmt
         | If '(' expression ')' statement (Else statement)?                    #conditionStmt
         | While '(' expression ')' statement                                   #whileLoopStmt
         | For '(' expression? ';' expression? ';' expression? ')' statement    #forLoopStmt
         | Return expression? ';'                                               #returnStmt
         | Break ';'                                                            #breakStmt
         | Continue ';'                                                         #continueStmt
         | expression ';'                                                       #exprStmt
         | ';'                                                                  #emptyStmt
         ;

expression: primary                                                         #atomExpr
          | This                                                            #thisExpr
          | '[&]' '('parameterList?')' '->' suite '(' expressionList? ')'   #lambdaExpr
          | newexpression                                                   #newExpr
          | expression '[' expression ']'                                   #indexExpr
          | expression '(' expressionList? ')'                              #functionExpr
          | expression '.' expression                                       #pointExpr
          | <assoc=right> op=('!'|'~') expression                           #unaryExpr
          | <assoc=right> op = ('+' | '-') expression                       #unaryExpr
          | <assoc=right> op=('++'|'--') expression                         #prefixSelfExpr
          | expression op=('++'|'--')                                       #suffixSelfExpr
          | expression op = ('*' | '/' | '%') expression                    #binaryExpr
          | expression op = ('+' | '-') expression                          #binaryExpr
          | expression op = ('<<' | '>>') expression                        #binaryExpr
          | expression op = ('<' | '<=' | '>' | '>=') expression            #binaryExpr
          | expression op = ('==' | '!=') expression                        #binaryExpr
          | expression op = '&' expression                                  #binaryExpr
          | expression op = '^' expression                                  #binaryExpr
          | expression op = '|' expression                                  #binaryExpr
          | expression op = '&&' expression                                 #binaryExpr
          | expression op = '||' expression                                 #binaryExpr
          | <assoc=right> expression '=' expression                         #assignExpr
          ;

newexpression: New singleType ('['expression']')+ ('['']')*                      #newArray
             | New Id ('('')')?                                                  #newClass
             | New singleType ('['expression']')+ ('['']')+ ('['expression']')+  #wrongNew_1
             | New singleType ('['']')+ ('['expression']')*                      #wrongNew_2
             ;

parameterList: variableType Id (',' variableType Id)*;

expressionList: expression (',' expression)*;

singleType: Int | Bool | String | Id;

arrayType: singleType '[' ']'
         | arrayType '[' ']'
         ;

variableType: singleType
            | arrayType
            ;

returnType: variableType
           | Void
           ;

primary: '(' expression ')'
       | Id
       | literal
       ;

literal: IntConst
       | StringConst
       | True
       | False
       | Null
       ;

//
Int: 'int';
Bool: 'bool';
String: 'string';
Null: 'null';
Void: 'void';
True: 'true';
False: 'false';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';
New: 'new';
Class: 'class';
This: 'this';

Dot : '.';
LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
SelfPlus : '++';
Minus : '-';
SelfMinus : '--';

Mul : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semicolon : ';';
Comma : ',';

Assign : '=';
Equal : '==';
NotEqual : '!=';

BackSlash : '\\\\';
DbQuotation : '\\"';

Id: [a-zA-Z][a-zA-Z0-9_]*;

IntConst: [1-9][0-9]* | '0';
StringConst: '"' ('\\\\' | '\\"' |.)*? '"';

BlockComment
            : '/*' .*? '*/'
            -> skip
            ;

LineComment
            : '//' ~[\r\n]*
            -> skip
            ;

WhiteSpace: [ \t]+ -> skip;
NewLine: ('\r' '\n'? | '\n') -> skip;