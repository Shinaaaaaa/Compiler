package AST;

import Util.position;

public class intConstNode extends ExprNode{
    public int value;
    public intConstNode(int value , position pos){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
