package AST;

import Util.position;

public class suiteStmtNode extends StmtNode{
    public suiteNode suite;

    suiteStmtNode(suiteNode suite , position pos){
        super(pos);
        this.suite = suite;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}