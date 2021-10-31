package AST;

public interface ASTVisitor {
    void visit(programNode it);

    void visit(varDefNode it);
    void visit(classDefNode it);
    void visit(funcDefNode it);
    void visit(constructfuncDefNode it);

    void visit(suiteNode it);

    void visit(blockStmtNode it);
    void visit(varDefStmtNode it);
    void visit(classDefStmtNode it);
    void visit(conditionStmtNode it);
    void visit(whileLoopStmtNode it);
    void visit(forLoopStmtNode it);
    void visit(returnStmtNode it);
    void visit(breakStmtNode it);
    void visit(continueStmtNode it);
    void visit(exprStmtNode it);

    //TODO type需要修改 constExpr 部分expr运算
    //TODO lambda 和 new
    void visit(varExprNode it);
    void visit(thisExprNode it);
    void visit(indexExprNode it);
    void visit(functionExprNode it);
    void visit(pointExprNode it);
    void visit(unaryExprNode it);
    void visit(prefixSelfExprNode it);
    void visit(suffixSelfExprNode it);
    void visit(binaryExprNode it);
    void visit(assignExprNode it);
    void visit(lambdaExprNode it);

    void visit(newArrayNode it);
    void visit(newClassNode it);

    void visit(expressionListNode it);
    void visit(parameterListNode it);

    void visit(singleTypeNode it);
    void visit(arrayTypeNode it); //TODO arrayType need to modify
    void visit(voidTypeNode it);

    void visit(intConstNode it);
    void visit(stringConstNode it);
    void visit(booleanConstNode it);
    void visit(nullConstNode it);
}
