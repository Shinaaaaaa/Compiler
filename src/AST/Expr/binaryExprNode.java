package AST.Expr;

import AST.*;
import Util.position;

public class binaryExprNode extends ExprNode{
    public ExprNode lhs , rhs;
    public enum binaryOpType{
        mul , div , mod ,
        add , sub ,
        leftShift , rightShift ,
        less , greater , lessEqual , greaterEqual ,
        equal , notequal ,
        and ,
        caret ,
        or ,
        andand ,
        oror
    }
    public binaryOpType opcode;

    public binaryExprNode(ExprNode lhs , ExprNode rhs , binaryOpType opcode , position pos){
        super(pos);
        this.lhs = lhs;
        this.rhs = rhs;
        this.opcode = opcode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}