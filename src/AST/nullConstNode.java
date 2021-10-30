package AST;

import Util.position;

public class nullConstNode extends ASTNode{
    public nullConstNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

