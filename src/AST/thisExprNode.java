package AST;

import Util.position;

public class thisExprNode extends ExprNode{
    thisExprNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}