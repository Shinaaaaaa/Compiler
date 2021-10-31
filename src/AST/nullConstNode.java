package AST;

import Util.position;

public class nullConstNode extends ExprNode{
    public nullConstNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

