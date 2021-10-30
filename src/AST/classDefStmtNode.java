package AST;

import Util.position;

import java.util.ArrayList;

public class classDefStmtNode extends subprogramNode{
    public String className;
    public ArrayList<varDefStmtNode> varDefList = new ArrayList<>();
    public ArrayList<funcDefNode> funcDefList = new ArrayList<>();
    public ArrayList<constructfuncDefNode> constructfuncDefList = new ArrayList<>();

    public classDefStmtNode(String className , position pos){
        super(pos);
        this.className = className;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}