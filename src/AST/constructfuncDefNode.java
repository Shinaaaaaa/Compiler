package AST;

import Util.position;

public class constructfuncDefNode extends ASTNode{
    public String funcName;
    public suiteNode suite;

    public constructfuncDefNode(String funcName , suiteNode suite , position pos){
        super(pos);
        this.funcName = funcName;
        this.suite = suite;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}