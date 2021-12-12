package AST.Stmt;

import AST.*;
import Util.position;

public class blockStmtNode extends StmtNode{
    public suiteNode suite;

    public blockStmtNode(suiteNode suite, position pos){
        super(pos);
        this.suite = suite;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}