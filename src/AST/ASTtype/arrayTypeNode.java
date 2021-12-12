package AST.ASTtype;

import AST.*;
import Util.Type;
import Util.position;

public class arrayTypeNode extends variableTypeNode{
    public variableTypeNode vartype;
    public int dimension;

    public arrayTypeNode(variableTypeNode vartype , position pos){
        super(pos);
        if (vartype instanceof arrayTypeNode) {
            this.vartype = ((arrayTypeNode) vartype).vartype;
            this.dimension = ((arrayTypeNode) vartype).dimension + 1;
            super.type = new Type(vartype.type.varTypeTag , this.dimension);
            if (vartype.type.Identifier != null) super.type.Identifier = vartype.type.Identifier;
        } else {
            this.vartype = vartype;
            this.dimension = 1;
            super.type = new Type(vartype.type.varTypeTag , 1);
            if (vartype.type.Identifier != null) super.type.Identifier = vartype.type.Identifier;
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
