package AST;

import Util.position;

public class intConstNode extends ASTNode{
    public int value;
    intConstNode(int value , position pos){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
