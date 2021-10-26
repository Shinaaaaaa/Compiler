package AST;

import Util.position;

public class funcDefNode extends ASTNode{
    public returnTypeNode returnType;
    public String funcName;
    public parameterListNode parameterList;
    public suiteNode suite;

    funcDefNode(returnTypeNode returnType , String funcName , parameterListNode parameterList
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