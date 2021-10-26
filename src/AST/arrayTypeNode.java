package AST;

import Util.position;

public class arrayTypeNode extends variableTypeNode{
    public String typeName;
    public int dimension;

    arrayTypeNode(String typeName , int dimension , position pos){
        super(pos);
        this.typeName = typeName;
        this.dimension = dimension;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
