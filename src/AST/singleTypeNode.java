package AST;

import Util.position;

public class singleTypeNode extends variableTypeNode{
    public String typeName;

    singleTypeNode(String typeName , position pos){
        super(pos);
        this.typeName = typeName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
