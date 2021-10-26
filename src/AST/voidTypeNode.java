package AST;

import Util.position;

public class voidTypeNode extends returnTypeNode{
    voidTypeNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
