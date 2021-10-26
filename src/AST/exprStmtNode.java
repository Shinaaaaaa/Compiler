package AST;

import Util.position;

public class exprStmtNode extends StmtNode{
    public ExprNode expr;

    exprStmtNode(ExprNode expr , position pos){
        super(pos);
        this.expr = expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}