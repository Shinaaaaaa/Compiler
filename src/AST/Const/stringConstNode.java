package AST.Const;

import AST.*;
import AST.Expr.*;
import Util.position;

public class stringConstNode extends ExprNode{
    public String value;

    public stringConstNode(String value , position pos){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
