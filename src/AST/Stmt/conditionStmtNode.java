package AST.Stmt;

import AST.*;
import AST.Expr.*;
import Util.position;

public class conditionStmtNode extends StmtNode{
    public ExprNode condition;
    public StmtNode thenStmt , elseStmt;

    public conditionStmtNode(ExprNode condition , StmtNode thenStmt , StmtNode elseStmt , position pos){
        super(pos);
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}