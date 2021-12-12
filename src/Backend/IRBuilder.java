package Backend;

import AST.*;
import AST.Const.*;
import AST.Def.*;
import AST.Expr.*;
import AST.New.*;
import AST.Stmt.*;
import AST.ASTtype.*;
import LLVMIR.*;
import LLVMIR.entity.*;
import LLVMIR.inst.*;
import LLVMIR.IRType.*;

public class IRBuilder implements ASTVisitor {


    @Override
    public void visit(programNode it) {

    }

    @Override
    public void visit(classDefStmtNode it) {
        it.classDef.accept(this);
    }

    @Override
    public void visit(classDefNode it) {

    }

    @Override
    public void visit(varDefStmtNode it) {

    };

    @Override
    public void visit(varDefNode it) {

    }

    @Override
    public void visit(constructfuncDefNode it) {

    }

    @Override
    public void visit(funcDefNode it) {

    }

    @Override
    public void visit(parameterListNode it) {

    }

    @Override public void visit(blockStmtNode it) {

    };

    @Override
    public void visit(suiteNode it) {

    }

    @Override
    public void visit(conditionStmtNode it) {

    }

    @Override
    public void visit(whileLoopStmtNode it) {

    }

    @Override
    public void visit(forLoopStmtNode it) {

    }

    @Override
    public void visit(returnStmtNode it) {

    }

    @Override
    public void visit(breakStmtNode it) {

    }

    @Override
    public void visit(continueStmtNode it) {

    }

    @Override
    public void visit(exprStmtNode it) {

    }

    @Override
    public void visit(varExprNode it) {

    }

    @Override
    public void visit(thisExprNode it) {

    }

    @Override
    public void visit(indexExprNode it) {

    }

    @Override
    public void visit(functionExprNode it) {

    }

    @Override
    public void visit(pointExprNode it) {

    }

    @Override
    public void visit(unaryExprNode it) {

    }

    @Override
    public void visit(prefixSelfExprNode it) {

    }

    @Override
    public void visit(suffixSelfExprNode it) {

    }

    @Override
    public void visit(binaryExprNode it) {

    }

    @Override
    public void visit(assignExprNode it) {

    }

    @Override
    public void visit(lambdaExprNode it) {

    }

    @Override
    public void visit(newArrayNode it) {

    }

    @Override
    public void visit(newClassNode it) {

    }

    @Override public void visit(expressionListNode it) {}

    @Override
    public void visit(singleTypeNode it) {

    }

    @Override
    public void visit(arrayTypeNode it) {

    }

    @Override
    public void visit(voidTypeNode it) {

    }

    @Override
    public void visit(intConstNode it) {

    }

    @Override
    public void visit(stringConstNode it) {

    }

    @Override
    public void visit(booleanConstNode it) {

    }

    @Override
    public void visit(nullConstNode it) {

    }
}
