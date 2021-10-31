package AST;

import Util.position;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;

public class parameterListNode extends ASTNode{
    public ArrayList<Pair<String , variableTypeNode>> parameterList = new ArrayList<>();

    public parameterListNode(position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}