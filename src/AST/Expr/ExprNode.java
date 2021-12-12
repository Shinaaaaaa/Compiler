package AST.Expr;

import AST.*;
import Util.position;

public abstract class ExprNode extends ASTNode{
    public ExprNode(position pos){
        super(pos);
    }
}