package AST;

import Util.position;

import java.util.ArrayList;

public class newArrayNode extends ExprNode{
    public singleTypeNode type;
    public int dimension;
    public ArrayList<ExprNode> dimExpr = new ArrayList<>();

    public newArrayNode(singleTypeNode type , int dimension , position pos){
        super(pos);
        this.type = type;
        this.dimension = dimension;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
