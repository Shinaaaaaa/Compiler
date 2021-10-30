package AST;

import Util.position;

public class prefixSelfExprNode extends ExprNode{
    public ExprNode expr;
    public enum prefixSelfOpType{
        selfPlus , selfMinus
    }
    public prefixSelfOpType opcode;

    public prefixSelfExprNode(ExprNode expr , prefixSelfOpType opcode , position pos){
        super(pos);
        this.expr = expr;
        this.opcode = opcode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}