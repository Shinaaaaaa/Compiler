package AST;

import Util.position;

public class lambdaExprNode extends ExprNode{
    public parameterListNode lambdaParameterList;
    public suiteNode suite;
    public expressionListNode lambdaExpressionList;

    public lambdaExprNode(parameterListNode parameterList , suiteNode suite ,
                          expressionListNode expressionList , position pos){
        super(pos);
        this.lambdaParameterList = parameterList;
        this.suite = suite;
        this.lambdaExpressionList = expressionList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
