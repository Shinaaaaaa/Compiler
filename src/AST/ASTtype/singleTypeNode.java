package AST.ASTtype;

import AST.*;
import Util.position;
import Util.Type;

public class singleTypeNode extends variableTypeNode{
    public singleTypeNode(Type type , position pos){
        super(pos);
        super.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
