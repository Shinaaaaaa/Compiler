package AST;

import Util.position;
import java.util.HashMap;

public class varDefStmtNode extends subprogramNode{
    public variableTypeNode varType;
    public HashMap<String , ExprNode> varList = new HashMap<>();

    public varDefStmtNode(variableTypeNode varType, position pos){
        super(pos);
        this.varType = varType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}