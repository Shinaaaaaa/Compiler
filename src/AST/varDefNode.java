package AST;

import Util.position;
import java.util.HashMap;

public class varDefNode extends ASTNode{
    public variableTypeNode varType;
    public HashMap<String , variableTypeNode> varList = new HashMap<>();

    varDefNode(variableTypeNode varType , position pos){
        super(pos);
        this.varType = varType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}