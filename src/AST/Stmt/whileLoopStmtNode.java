package AST.Stmt;

import AST.*;
import AST.Expr.*;
import Util.position;

public class whileLoopStmtNode extends StmtNode{
    public ExprNode condition;
    public StmtNode loopStmt;

    public whileLoopStmtNode(ExprNode condition , StmtNode loopStmt , position pos){
        super(pos);
        this.condition = condition;
        this.loopStmt = loopStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}