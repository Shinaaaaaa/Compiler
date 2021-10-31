package AST;

import Util.position;

public class classDefStmtNode extends StmtNode{
    public classDefNode classDef;

    public classDefStmtNode(classDefNode classDef , position pos){
        super(pos);
        this.classDef = classDef;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}