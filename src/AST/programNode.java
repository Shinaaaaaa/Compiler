package AST;

import Util.position;

import java.util.ArrayList;

public class programNode extends ASTNode{
    public ArrayList<funcDefNode> funcDefList = new ArrayList<>();
    public ArrayList<classDefNode> classDefList = new ArrayList<>();
    public ArrayList<varDefNode> varDefList = new ArrayList<>();

    programNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}