package AST.New;

import AST.*;
import AST.Expr.*;
import Util.position;
import Util.Type;

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
