package Backend;

import AST.*;
import AST.Const.*;
import AST.Def.*;
import AST.Expr.*;
import AST.New.*;
import AST.Stmt.*;
import AST.ASTtype.*;
import LLVMIR.IRType.*;
import LLVMIR.IRroot;
import LLVMIR.func;
import Util.*;

import java.util.ArrayList;

public class IRCollector implements ASTVisitor {
    public IRroot irroot;
    private String tmpClassName;
    ArrayList<IRType> parameterList;

    public IRCollector(IRroot irroot) {
        this.irroot = irroot;
        this.tmpClassName = null;
    }

    public arrayType toArrayType(Type type) {
        arrayType array;
        IRType tmp;
        if (type.varTypeTag == Type.var_type.Class) {
            tmp = new classType(type.Identifier);
        }
        else tmp = new integerType(32);
        array = new arrayType(0 , tmp);
        for (int i = 2 ; i <= type.dimension ; i++) {
            array = new arrayType(0 , array);
        }
        return array;
    }

    @Override
    public void visit(programNode it) {
        it.subprogramList.forEach(cd -> {
            if (cd instanceof classDefNode) {
                irroot.addClass(((classDefNode) cd).className);
            }
        });
        it.subprogramList.forEach(cd -> cd.accept(this));
    }

    @Override
    public void visit(classDefStmtNode it) {
        it.classDef.accept(this);
    }

    @Override
    public void visit(classDefNode it) {
        tmpClassName = it.className;
        it.varDefList.forEach(vd -> vd.accept(this));
        it.constructfuncDefList.forEach(cd -> cd.accept(this));
        it.funcDefList.forEach(fd -> fd.accept(this));
        tmpClassName = null;
    }

    @Override
    public void visit(varDefStmtNode it) {
        it.varDef.accept(this);
    };

    @Override
    public void visit(varDefNode it) {
        it.varList.keySet().forEach(kd -> {
            IRType type = null;
            if (it.variableType.type.dimension > 0) {
                type = toArrayType(it.variableType.type);
            }
            else {
                if (it.variableType.type.varTypeTag == Type.var_type.Class) {
                    type = new classType(it.variableType.type.Identifier);
                }
                else {
                    type = new integerType(32);
                }
            }
            if (tmpClassName == null) {
                irroot.addGlobalVariable(kd , new pointerType(type));
            }
            else {
                irroot.addVariableTypeInClass(tmpClassName , type);
            }
        });
    }

    @Override
    public void visit(constructfuncDefNode it) {
        irroot.addFunc(new func(tmpClassName + "." + it.funcName , new classType(tmpClassName) ,null));
    }

    @Override
    public void visit(funcDefNode it){
        ArrayList<IRType> parameter;
        if (it.parameterList != null) {
            it.parameterList.accept(this);
            parameter = parameterList;
        } else parameter = null;
        IRType returnType = null;
        if (!(it.returnType instanceof voidTypeNode)) {
            if (((variableTypeNode)it.returnType).type.dimension > 0) {
                returnType = toArrayType(((variableTypeNode)it.returnType).type);
            }
            else {
                if (((variableTypeNode)it.returnType).type.varTypeTag == Type.var_type.Class) {
                    returnType = new classType(((variableTypeNode)it.returnType).type.Identifier);
                }
                else returnType = new integerType(32);
            }
        }
        if (tmpClassName == null) irroot.addFunc(new func(it.funcName , returnType , parameter));
        else irroot.addFunc(new func(tmpClassName + "." + it.funcName , returnType , parameter));
    }

    @Override
    public void visit(parameterListNode it) {
        parameterList = new ArrayList<>();
        it.parameterList.forEach(kd -> {
            if (kd.b.type.dimension > 0) {
                parameterList.add(toArrayType(kd.b.type));
            }
            else {
                if (kd.b.type.varTypeTag == Type.var_type.Class) {
                    parameterList.add(new classType(kd.b.type.Identifier));
                }
                else parameterList.add(new integerType(32));
            }
        });
    }

    @Override public void visit(blockStmtNode it) {}
    @Override public void visit(suiteNode it) {}
    @Override public void visit(conditionStmtNode it) {}
    @Override public void visit(whileLoopStmtNode it) {}
    @Override public void visit(forLoopStmtNode it) {}
    @Override public void visit(returnStmtNode it) {}
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
    @Override public void visit(newClassNode it) {};

    @Override public void visit(expressionListNode it) {}

    @Override public void visit(singleTypeNode it) {}
    @Override public void visit(arrayTypeNode it) {}
    @Override public void visit(voidTypeNode it) {}

    @Override public void visit(intConstNode it) {}
    @Override public void visit(stringConstNode it) {}
    @Override public void visit(booleanConstNode it) {}
    @Override public void visit(nullConstNode it) {}
}
