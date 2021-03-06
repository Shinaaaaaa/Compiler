package AST.Expr;

import AST.*;
import Util.position;

public class functionExprNode extends ExprNode{
    public ExprNode funcName;
    public expressionListNode exprList;

    public functionExprNode(ExprNode funcName , expressionListNode exprList , position pos){
        super(pos);
        this.funcName = funcName;
        this.exprList = exprList;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}