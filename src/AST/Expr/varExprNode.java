package AST.Expr;

import AST.*;
import Util.position;

public class varExprNode extends ExprNode{
    public String varName;

    public varExprNode(String varName , position pos){
        super(pos);
        this.varName = varName;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
