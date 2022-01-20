package Backend;

import AST.*;
import AST.Const.*;
import AST.Def.*;
import AST.Expr.*;
import AST.New.*;
import AST.Stmt.*;
import AST.ASTtype.*;
import LLVMIR.IRType.*;
import LLVMIR.entity.*;
import LLVMIR.*;
import LLVMIR.func;
import Util.*;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;

public class IRCollector implements ASTVisitor {
    public IRroot irroot;
    public entityScope eScope;
    private String tmpClassName;
    private IRType tmpIRType;
    private ArrayList<Pair<String , IRType>> parameterList;

    public IRCollector(IRroot irroot) {
        this.irroot = irroot;
        this.tmpClassName = null;
    }

    public void Init() {
        ArrayList<Pair<String , IRType>> parameter;
        func globalInitFunc = new func("__cxx_global_var_init");
        globalInitFunc.Init(new entityScope(eScope));
        irroot.funcList.put("__cxx_global_var_init" , globalInitFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("bytes" , new integerType(32)));
        func mallocFunc = new func("malloc" , new pointerType(new integerType(8)) , parameter);
        irroot.built_in_func.put("malloc" , mallocFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new pointerType(new integerType(8))));
        func printFunc = new func("print" , null , parameter);
        irroot.built_in_func.put("print" , printFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new pointerType(new integerType(8))));
        func printlnFunc = new func("println" , null , parameter);
        irroot.built_in_func.put("println" , printlnFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("n" , new integerType(32)));
        func printIntFunc = new func("printInt" , null , parameter);
        irroot.built_in_func.put("printInt" , printIntFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("n" , new integerType(32)));
        func printlnIntFunc = new func("printlnInt" , null , parameter);
        irroot.built_in_func.put("printlnInt" , printlnIntFunc);

        func getStringFunc = new func("getString" , new pointerType(new integerType(8)) , null);
        irroot.built_in_func.put("getString" , getStringFunc);

        func getIntFunc = new func("getInt" , new integerType(32) , null);
        irroot.built_in_func.put("getInt" , getIntFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new integerType(32)));
        func toStringFunc = new func("toString" , new pointerType(new integerType(8)) , parameter);
        irroot.built_in_func.put("toString" , toStringFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("array" , new pointerType(new integerType(8))));
        func arraySizeFunc = new func("_array_size" , new integerType(32) , parameter);
        irroot.built_in_func.put("_array_size" , arraySizeFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strAddFunc = new func("_str_add" , new pointerType(new integerType(8)) , parameter);
        irroot.built_in_func.put("_str_add" , strAddFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strEqualFunc = new func("_str_euqal" , new integerType(1) , parameter);
        irroot.built_in_func.put("_str_equal" , strEqualFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strNotEqualFunc = new func("_str_notequal" , new integerType(1) , parameter);
        irroot.built_in_func.put("_str_notequal" , strNotEqualFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strLessFunc = new func("_str_less" , new integerType(1) , parameter);
        irroot.built_in_func.put("_str_less" , strLessFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strGreaterFunc = new func("_str_greater" , new integerType(1) , parameter);
        irroot.built_in_func.put("_str_greater" , strGreaterFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strLessEqualFunc = new func("_str_lessEqual" , new integerType(1) , parameter);
        irroot.built_in_func.put("_str_lessEqual" , strLessEqualFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str1" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("str2" , new pointerType(new integerType(8))));
        func strGreaterEqualFunc = new func("_str_greaterEqual" , new integerType(1) , parameter);
        irroot.built_in_func.put("_str_greaterEqual" , strGreaterEqualFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new pointerType(new integerType(8))));
        func strLengthFunc = new func("_str_length" , new integerType(32) , parameter);
        irroot.built_in_func.put("_str_length" , strLengthFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("left" , new integerType(32)));
        parameter.add(new Pair<>("right" , new integerType(32)));
        func strSubstringFunc = new func("_str_substring" , new pointerType(new integerType(8)) , parameter);
        irroot.built_in_func.put("_str_substring" , strSubstringFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new pointerType(new integerType(8))));
        func strParseIntFunc = new func("_str_parseInt" , new integerType(32) , parameter);
        irroot.built_in_func.put("_str_parseInt" , strParseIntFunc);

        parameter = new ArrayList<>();
        parameter.add(new Pair<>("str" , new pointerType(new integerType(8))));
        parameter.add(new Pair<>("pos" , new integerType(32)));
        func strOrdFunc = new func("_str_ord" , new integerType(32) , parameter);
        irroot.built_in_func.put("_str_ord" , strOrdFunc);
    }

    @Override
    public void visit(programNode it) {
        it.subprogramList.forEach(cd -> {
            if (cd instanceof classDefNode) {
                irroot.addClass(((classDefNode) cd).className);
            }
        });
        it.subprogramList.forEach(cd -> {
            if (!(cd instanceof varDefNode)) {
                cd.accept(this);
            }
        });
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
        if (it.constructfuncDefList.size() == 0) {
            ArrayList<Pair<String , IRType>> parameter = new ArrayList<>();
            parameter.add(new Pair<>(tmpClassName , new pointerType(irroot.getClass(tmpClassName))));
            irroot.addFunc(new func(tmpClassName , null , parameter));
        }
        it.funcDefList.forEach(fd -> fd.accept(this));
        tmpClassName = null;
    }

    @Override
    public void visit(varDefStmtNode it) {
        it.varDef.accept(this);
    };

    @Override
    public void visit(varDefNode it) {
        it.variableType.accept(this);
        it.varList.keySet().forEach(kd -> {
            irroot.addVariableTypeInClass(tmpClassName , kd , tmpIRType);
        });
    }

    @Override
    public void visit(constructfuncDefNode it) {
        ArrayList<Pair<String , IRType>> parameter = new ArrayList<>();
        parameter.add(new Pair<>(tmpClassName , new pointerType(irroot.getClass(tmpClassName))));
        irroot.addFunc(new func(tmpClassName , null , parameter));
    }

    @Override
    public void visit(funcDefNode it){
        eScope = new entityScope(eScope);
        ArrayList<Pair<String , IRType>> parameter;
        if (it.parameterList != null) {
            it.parameterList.accept(this);
            parameter = parameterList;
        } else parameter = new ArrayList<>();
        it.returnType.accept(this);
        IRType retType = tmpIRType;
        if (tmpClassName == null) irroot.addFunc(new func(it.funcName , retType , parameter));
        else {
            parameter.add(0 , new Pair<>(tmpClassName , new pointerType(irroot.getClass(tmpClassName))));
            irroot.addFunc(new func(tmpClassName + "." + it.funcName , retType , parameter));
        }
        eScope = eScope.getParentEntityScope();
    }

    @Override
    public void visit(parameterListNode it) {
        parameterList = new ArrayList<>();
        it.parameterList.forEach(kd -> {
            kd.b.accept(this);
            parameterList.add(new Pair<>(kd.a , tmpIRType));
        });
    }

    @Override
    public void visit(singleTypeNode it) {
        if (it.type.varTypeTag == Type.var_type.Int) tmpIRType = new integerType(32);
        else if (it.type.varTypeTag == Type.var_type.Bool) tmpIRType = new integerType(1);
        else if (it.type.varTypeTag == Type.var_type.Class) tmpIRType = new pointerType(irroot.getClass(it.type.Identifier));
        else if (it.type.varTypeTag == Type.var_type.Str) {
            tmpIRType = new pointerType(new integerType(8));
        }
    }

    @Override
    public void visit(arrayTypeNode it) {
        it.vartype.accept(this);
        for (int i = 0 ; i < it.dimension ; i++) {
            tmpIRType = new pointerType(tmpIRType);
        }
    }

    @Override
    public void visit(voidTypeNode it) {
        tmpIRType = null;
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

    @Override public void visit(intConstNode it) {}
    @Override public void visit(stringConstNode it) {}
    @Override public void visit(booleanConstNode it) {}
    @Override public void visit(nullConstNode it) {}
}
