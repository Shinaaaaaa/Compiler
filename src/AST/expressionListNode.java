package AST;

import Util.position;

import java.util.ArrayList;

public class expressionListNode extends ASTNode{
    public ArrayList<ExprNode> exprList = new ArrayList<>();

    public expressionListNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}