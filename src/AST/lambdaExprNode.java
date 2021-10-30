package AST;

import Util.position;

import java.util.ArrayList;

public class lambdaExprNode extends ASTNode{
    public parameterListNode parameterList;
    public suiteNode suite;
    public expressionListNode expressionList;

    public lambdaExprNode(parameterListNode parameterList , suiteNode suite ,
                          expressionListNode expressionList , position pos){
        super(pos);
        this.parameterList = parameterList;
        this.suite = suite;
        this.expressionList = expressionList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
