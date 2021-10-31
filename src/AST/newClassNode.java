package AST;

import Util.Type;
import Util.position;

public class newClassNode extends ExprNode{
    public Type type;

    public newClassNode(Type type , position pos){
        super(pos);
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
