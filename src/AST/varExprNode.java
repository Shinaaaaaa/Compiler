package AST;

import Util.position;

public class varExprNode extends ExprNode{
    public String varName;

    varExprNode(String varName , position pos){
        super(pos);
        this.varName = varName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
