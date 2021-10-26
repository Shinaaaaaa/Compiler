package AST;

import Util.position;

public class stringConstNode extends ASTNode{
    public String value;

    stringConstNode(String value , position pos){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
