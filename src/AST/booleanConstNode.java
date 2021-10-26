package AST;

import Util.position;

public class booleanConstNode extends ASTNode{
    public boolean value;
    booleanConstNode(boolean value , position pos){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
