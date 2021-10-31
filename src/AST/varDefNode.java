package AST;

import Util.position;
import java.util.HashMap;

public class varDefNode extends subprogramNode{
    public variableTypeNode variableType;
    public HashMap<String , ExprNode> varList = new HashMap<>();

    public varDefNode(variableTypeNode varType, position pos){
        super(pos);
        this.variableType = varType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}