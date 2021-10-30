package AST;

import Util.Type;
import Util.position;

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
