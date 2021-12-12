package AST.Stmt;

import AST.*;
import AST.Def.*;
import Util.position;

public class varDefStmtNode extends StmtNode{
    public varDefNode varDef;

    public varDefStmtNode(varDefNode varDef, position pos){
        super(pos);
        this.varDef = varDef;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}