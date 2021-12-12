package AST.Expr;

import AST.*;
import Util.position;

public class unaryExprNode extends ExprNode{
    public ExprNode expr;
    public enum unaryOpType{
        not , tilde ,
        plus , minus
    }
    public unaryOpType opcode;

    public unaryExprNode(ExprNode expr , unaryOpType opcode , position pos){
        super(pos);
        this.expr = expr;
        this.opcode = opcode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}