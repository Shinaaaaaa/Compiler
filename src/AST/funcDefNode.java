package AST;

import Util.position;

public class funcDefNode extends subprogramNode{
    public returnTypeNode returnType;
    public String funcName;
    public parameterListNode parameterList;
    public suiteNode suite;

    public funcDefNode(returnTypeNode returnType , String funcName , parameterListNode parameterList
               , suiteNode suite , position pos){
        super(pos);
        this.returnType = returnType;
        this.funcName = funcName;
        this.parameterList = parameterList;
        this.suite = suite;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}