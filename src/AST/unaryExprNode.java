package AST;

import Util.position;

public class unaryExprNode extends ExprNode{
    public ExprNode lhs , rhs;
    public enum unaryOpType{
        not , tilde ,
        plus , selfPlus ,
        minus , selfMinus
    }
    public unaryOpType opcode;

    unaryExprNode(ExprNode lhs , ExprNode rhs , unaryOpType opcode , position pos){
        super(pos);
        this.lhs = lhs;
        this.rhs = rhs;
        this.opcode = opcode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}