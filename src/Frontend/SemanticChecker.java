package Frontend;

import AST.*;
import Util.*;
import Util.error.semanticError;

public class SemanticChecker implements ASTVisitor {
    public globalScope gScope;
    public Scope tmpScope;
    public Type ExprType;

    public SemanticChecker(globalScope gScope) {
        this.gScope = gScope;
        this.tmpScope = gScope;
    }

    @Override
    public void visit(programNode it) {
        it.subprogramList.forEach(sd -> sd.accept(this));
        if (!gScope.containFuncName("main")) {
            throw new semanticError("Semantic Error: no main function" , it.pos);
        }
        Type funcType = gScope.getFuncType("main" , it.pos);
        if (funcType.funcReturnType.varTypeTag != Type.var_type.Int) {
            throw new semanticError("Semantic Error: main function return type error" , it.pos);
        }
        if (funcType.parameterList.size() != 0) {
            throw new semanticError("Semantic Error: main function parameter error" , it.pos);
        }
    }

    @Override
    public void visit(classDefStmtNode it) {
        tmpScope = gScope.getClassScope(it.className , it.pos);
        it.funcDefList.forEach(fd -> fd.accept(this));
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(varDefStmtNode it) {
        it.varList.keySet().forEach(kd -> {
            if (gScope.containClassName(kd)) {
                throw new semanticError("Semantic Error: variable rename with class" , it.pos);
            }
            if (it.varList.get(kd) != null) {
                it.varList.get(kd).accept(this);
                if (!ExprType.typeMatchCheck(it.varType.type , it.pos)) {
                    throw new semanticError("Semantic Error: varDef Type not match" , it.pos);
                }
            }
            tmpScope.addVar(kd , it.varType.type , it.pos);
        });
    }

    @Override
    public void visit(constructfuncDefNode it) {
        tmpScope = ((globalScope) tmpScope).getFuncScope(it.funcName , it.pos);
        it.suite.accept(this);
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(funcDefNode it) {
        tmpScope = ((globalScope) tmpScope).getFuncScope(it.funcName , it.pos);
        it.suite.accept(this);
        tmpScope = tmpScope.parentScope();
    }

    @Override public void visit(parameterListNode it) {}

    @Override
    public void visit(suiteNode it) {
        it.stmts.forEach(sd -> sd.accept(this));
    }

    @Override
    public void visit(conditionStmtNode it) {
        it.condition.accept(this);
        if (ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
            throw new semanticError("Semantic Error:  conditionStmt condition is not Bool" , it.pos);
        }
        tmpScope = new Scope(tmpScope);
        it.thenStmt.accept(this);
        tmpScope = tmpScope.parentScope();
        if (it.elseStmt != null) {
            tmpScope = new Scope(tmpScope);
            it.elseStmt.accept(this);
            tmpScope = tmpScope.parentScope();
        }
    }

    @Override public void visit(whileLoopStmtNode it) {
        if (it.condition == null) {
            throw new semanticError("Semantic Error: while condition cannot be null" , it.pos);
        }
        it.condition.accept(this);
        if (ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
            throw new semanticError("Semantic Error:  whileLoopStmt condition is not Bool" , it.pos);
        }
        tmpScope = new Scope(tmpScope);
        it.loopStmt.accept(this);
        tmpScope = tmpScope.parentScope();
    }

    @Override public void visit(forLoopStmtNode it) {
        tmpScope = new Scope(tmpScope);
        if (it.initExpr != null) {
            it.initExpr.accept(this);
        }
        if (it.conditionExpr != null) {
            it.conditionExpr.accept(this);
            if (ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
                throw new semanticError("Semantic Error:  forLoopStmt condition is not Bool" , it.pos);
            }
        }
        if (it.incrementExpr != null) {
            it.incrementExpr.accept(this);
        }
        tmpScope = tmpScope.parentScope();
    }

    @Override public void visit(returnStmtNode it) {

    }
    @Override public void visit(breakStmtNode it) {}
    @Override public void visit(continueStmtNode it) {}
    @Override public void visit(exprStmtNode it) {}

    @Override public void visit(varExprNode it) {}
    @Override public void visit(thisExprNode it) {}
    @Override public void visit(indexExprNode it) {}
    @Override public void visit(functionExprNode it) {}
    @Override public void visit(pointExprNode it) {}
    @Override public void visit(unaryExprNode it) {}
    @Override public void visit(prefixSelfExprNode it) {}
    @Override public void visit(suffixSelfExprNode it) {}
    @Override public void visit(binaryExprNode it) {}
    @Override public void visit(assignExprNode it) {}
    @Override public void visit(lambdaExprNode it) {}

    @Override public void visit(newArrayNode it) {}

    @Override public void visit(expressionListNode it) {}

    @Override public void visit(singleTypeNode it) {}
    @Override public void visit(arrayTypeNode it) {}
    @Override public void visit(voidTypeNode it) {}

    @Override public void visit(intConstNode it) {}
    @Override public void visit(stringConstNode it) {}
    @Override public void visit(booleanConstNode it) {}
    @Override public void visit(nullConstNode it) {}
}
