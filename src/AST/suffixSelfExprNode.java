package AST;

import Util.position;

public class suffixSelfExprNode extends ExprNode{
    public ExprNode expr;
    public enum suffixSelfOpType{
        selfPlus , selfMinus
    }
    public suffixSelfOpType opcode;

    public suffixSelfExprNode(ExprNode expr , suffixSelfOpType opcode , position pos){
        super(pos);
        this.expr = expr;
        this.opcode = opcode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}