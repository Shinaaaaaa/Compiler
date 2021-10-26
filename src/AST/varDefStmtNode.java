package AST;

import Util.position;

public class varDefStmtNode extends StmtNode{
    public varDefNode varDef;

    varDefStmtNode(varDefNode varDef , position pos){
        super(pos);
        this.varDef = varDef;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}