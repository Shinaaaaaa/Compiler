package AST.ASTtype;

import Util.position;
import Util.Type;

public abstract class variableTypeNode extends returnTypeNode{
    public Type type;
    public variableTypeNode(position pos){
        super(pos);
    }
}