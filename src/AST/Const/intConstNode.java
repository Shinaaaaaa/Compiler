package AST.Const;

import AST.*;
import AST.Expr.*;
import Util.position;

public class intConstNode extends ExprNode{
    public long value;
    public intConstNode(long value , position pos){
        super(pos);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
