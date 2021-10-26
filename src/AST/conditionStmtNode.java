package AST;

import Util.position;

public class conditionStmtNode extends StmtNode{
    public ExprNode condition;
    public StmtNode thenStmt , ifStmt;

    conditionStmtNode(ExprNode condition , StmtNode thenStmt , StmtNode ifStmt , position pos){
        super(pos);
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.ifStmt = ifStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}