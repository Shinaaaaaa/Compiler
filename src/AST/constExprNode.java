package AST;

import Util.position;

public class constExprNode extends ExprNode{
    public ConstNode Const;

    constExprNode(ConstNode Const , position pos){
        super(pos);
        this.Const = Const;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}