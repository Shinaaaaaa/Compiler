package AST;

import Util.position;
import Util.Type;

public abstract class ExprNode extends ASTNode{
    public ExprNode(position pos){
        super(pos);
    }
}