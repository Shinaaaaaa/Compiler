package AST;

import Util.position;

public class pointExprNode extends ExprNode{
    public ExprNode lhs;
    public ExprNode rhs;

    pointExprNode(ExprNode lhs , ExprNode rhs , position pos){
        super(pos);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}