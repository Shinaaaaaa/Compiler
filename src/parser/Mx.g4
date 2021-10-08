grammar Mx;

program: (funcDef|classDef ';'|varDef ';')* EOF;

singelType: Int | Bool | String | Id;

arrayType: singelType '[' ']'
         | arrayType '[' ']'
         ;

variableType: singelType
            | arrayType
            ;

returnType: variableType
           | Void
           ;

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
         | varDef ';'                                                           #vardefStmt
         | classDef ';'                                                         #classdefStmt
         | If '(' expression ')' statement (Else statement)?                    #conditionStmt
         | While '(' expression ')' statement                                   #loopStmt
         | For '(' expression? ';' expression? ';' expression? ')' statement    #loopStmt
         | Return expression? ';'                                               #returnStmt
         | Break ';'                                                            #breakStmt
         | Continue ';'                                                         #continueStmt
         | expression ';'                                                       #exprStmt
         | ';'                                                                  #emptyStmt
         ;

expression: primary                                                         #atomExpr
          | This                                                            #thisExpr
          | lambdaexpression                                                #lambdaExpr
          | newexpression                                                   #newEXpr
          | expression '[' expression ']'                                   #indexExpr
          | expression '(' expressionList? ')'                              #functionExpr
          | expression '.' expression                                       #pointExpr
          | <assoc=right> op=('!'|'~') expression                           #unaryExpr
          | <assoc=right> op=('++'|'--') expression                         #unaryExpr
          | <assoc=right> op = ('+' | '-') expression                       #unaryExpr
          | <assoc=right> expression op=('++'|'--')                         #unaryExpr
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

lambdaexpression: '[&]' '('parameterList?')' '->' suite '(' expressionList? ')';

newexpression: New singelType ('['expression']')+ ('['']')*
             | New Id ('('')')?
             ;

parameterList: variableType Id (',' variableType Id)*;

expressionList: expression (',' expression)*;

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

