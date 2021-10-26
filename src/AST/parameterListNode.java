package AST;

import Util.position;
import java.util.HashMap;

public class parameterListNode extends ASTNode{
    public HashMap<variableTypeNode , String> parameterList = new HashMap<>();

    parameterListNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}