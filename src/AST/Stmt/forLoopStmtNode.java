package AST.Stmt;

import AST.*;
import AST.Expr.*;
import Util.position;

public class forLoopStmtNode extends StmtNode{
    public ExprNode initExpr , conditionExpr , incrementExpr;
    public StmtNode loopStmt;

    public forLoopStmtNode(ExprNode initExpr , ExprNode conditionExpr , ExprNode incrementExpr
            , StmtNode loopStmt , position pos){
        super(pos);
        this.initExpr = initExpr;
        this.conditionExpr = conditionExpr;
        this.incrementExpr = incrementExpr;
        this.loopStmt = loopStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}