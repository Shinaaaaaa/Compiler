package AST;

import Util.position;
import java.util.ArrayList;

public class programNode extends ASTNode{
    public ArrayList<subprogramNode> subprogramList = new ArrayList<>();

    public programNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}