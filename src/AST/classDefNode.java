package AST;

import Util.position;

import java.util.ArrayList;

public class classDefNode extends ASTNode{
    public String className;
    public ArrayList<varDefNode> varDefList = new ArrayList<>();
    public ArrayList<funcDefNode> funcDefList = new ArrayList<>();
    public ArrayList<constructfuncDefNode> constructfuncDefList = new ArrayList<>();

    classDefNode(String className , position pos){
        super(pos);
        this.className = className;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}