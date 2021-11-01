package Frontend;

import AST.*;
import Util.*;
import Util.error.semanticError;

import java.util.ArrayList;
import java.util.Objects;

public class SymbolCollector implements ASTVisitor {
    public globalScope gScope;
    public Scope tmpScope;

    public SymbolCollector(globalScope gScope) {
        this.gScope = gScope;
        this.tmpScope = gScope;
    }

    public void Init() {
        position p = new position(0 ,0);
        ArrayList<Type> parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Str , 0));
        Type funcType = new Type("print" , new Type(Type.var_type.Void , 0) , parameterList);
        Scope funcScope = new Scope(gScope);
        funcScope.addVar("str" , new Type(Type.var_type.Str , 0) , p);
        gScope.addFunc("print" , funcScope , funcType , p);

        parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Str , 0));
        funcType = new Type("println" , new Type(Type.var_type.Void , 0) , parameterList);
        funcScope = new Scope(gScope);
        funcScope.addVar("str" , new Type(Type.var_type.Str , 0) , p);
        gScope.addFunc("println" , funcScope , funcType , p);

        parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Int , 0));
        funcType = new Type("printInt" , new Type(Type.var_type.Void , 0) , parameterList);
        funcScope = new Scope(gScope);
        funcScope.addVar("n" , new Type(Type.var_type.Int , 0) , p);
        gScope.addFunc("printInt" , funcScope , funcType , p);

        parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Int , 0));
        funcType = new Type("printlnInt" , new Type(Type.var_type.Void , 0) , parameterList);
        funcScope = new Scope(gScope);
        funcScope.addVar("n" , new Type(Type.var_type.Int , 0) , p);
        gScope.addFunc("printlnInt" , funcScope , funcType , p);

        funcScope = new Scope(gScope);
        funcType = new Type("getString" , new Type(Type.var_type.Str , 0) , null);
        gScope.addFunc("getString" , funcScope , funcType , p);

        funcScope = new Scope(gScope);
        funcType = new Type("getInt" , new Type(Type.var_type.Int , 0) , null);
        gScope.addFunc("getInt" , funcScope , funcType , p);

        parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Int , 0));
        funcType = new Type("toString" , new Type(Type.var_type.Str , 0) , parameterList);
        funcScope = new Scope(gScope);
        funcScope.addVar("i" , new Type(Type.var_type.Int , 0) , p);
        gScope.addFunc("toString" , funcScope , funcType , p);

        globalScope newArrayScope = new globalScope(gScope , "_Array");
        gScope.addClass("_Array" , newArrayScope , p);

        funcScope = new Scope(gScope);
        funcType = new Type("size" , new Type(Type.var_type.Int , 0) , null);
        newArrayScope.addFunc("size" , funcScope , funcType , p);

        globalScope newStringScope = new globalScope(gScope , "_String");
        gScope.addClass("_String" , newStringScope , p);

        funcScope = new Scope(gScope);
        funcType = new Type("length" , new Type(Type.var_type.Int , 0) , null);
        newStringScope.addFunc("length" , funcScope , funcType , p);

        parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Int , 0));
        parameterList.add(new Type(Type.var_type.Int , 0));
        funcType = new Type("substring" , new Type(Type.var_type.Str , 0) , parameterList);
        funcScope = new Scope(gScope);
        funcScope.addVar("left" , new Type(Type.var_type.Int , 0) , p);
        funcScope.addVar("right" , new Type(Type.var_type.Int , 0) , p);
        newStringScope.addFunc("substring" , funcScope , funcType , p);

        funcScope = new Scope(gScope);
        funcType = new Type("parseInt" , new Type(Type.var_type.Int , 0) , null);
        newStringScope.addFunc("parseInt" , funcScope , funcType , p);

        parameterList = new ArrayList<>();
        parameterList.add(new Type(Type.var_type.Int , 0));
        funcType = new Type("ord" , new Type(Type.var_type.Int , 0) , parameterList);
        funcScope = new Scope(gScope);
        funcScope.addVar("pos" , new Type(Type.var_type.Int , 0) , p);
        newStringScope.addFunc("ord" , funcScope , funcType , p);
    }

    @Override
    public void visit(programNode it) {
        it.subprogramList.forEach(cd -> {
            if (cd instanceof classDefNode) {
                globalScope newClassScope = new globalScope(gScope , ((classDefNode) cd).className);
                newClassScope.classTag = true;
                gScope.addClass(((classDefNode) cd).className, newClassScope, cd.pos);
            }
        });

        it.subprogramList.forEach(cd -> {
            if (cd instanceof classDefNode) {
                cd.accept(this);
            }
        });

        it.subprogramList.forEach(cd -> {
            if (cd instanceof funcDefNode) {
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
        tmpScope = gScope.getClassScope(it.className , it.pos);
        it.varDefList.forEach(vd -> vd.accept(this));
        it.constructfuncDefList.forEach(cd -> cd.accept(this));
        it.funcDefList.forEach(fd -> {
            if (Objects.equals(it.className, fd.funcName)) {
                throw new semanticError("Semantic Error: invalid function name" , fd.pos);
            }
            fd.accept(this);
        });
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(varDefStmtNode it) {
        it.varDef.accept(this);
    };

    @Override
    public void visit(varDefNode it) {
        it.varList.keySet().forEach(kd -> {
            if (gScope.containClassName(kd)) {
                throw new semanticError("Semantic Error: variable rename with class" , it.pos);
            }
            else tmpScope.addVar(kd , it.variableType.type , it.pos);
        });
    }

    @Override
    public void visit(constructfuncDefNode it) {
        if (!Objects.equals(it.funcName, ((globalScope) tmpScope).name)) {
            throw new semanticError("Semantic Error: construct function name is not same with class name" , it.pos);
        } else {
            Scope newFuncScope = new Scope(tmpScope);
            Type funcType = new Type(it.funcName , null , null);
            ((globalScope) tmpScope).addFunc(it.funcName , newFuncScope , funcType , it.pos);
        }
    }

    @Override
    public void visit(funcDefNode it){
        Scope newFuncScope = new Scope(tmpScope);
        tmpScope = newFuncScope;
        ArrayList<Type> parameterList;
        if (it.parameterList != null) {
            it.parameterList.accept(this);
            parameterList = new ArrayList<>();
            it.parameterList.parameterList.forEach(cd -> parameterList.add(cd.b.type));
        } else parameterList = null;
        tmpScope = tmpScope.parentScope();
        Type funcType;
        if (it.returnType instanceof voidTypeNode) {
            funcType = new Type(it.funcName , new Type(Type.var_type.Void , 0) , parameterList);
        } else {
            funcType = new Type(it.funcName , ((variableTypeNode)it.returnType).type , parameterList);
        }

        ((globalScope) tmpScope).addFunc(it.funcName , newFuncScope , funcType , it.pos);
    }

    @Override
    public void visit(parameterListNode it) {
        it.parameterList.forEach(kd -> {
            if (kd.b.type.varTypeTag == Type.var_type.Class && !gScope.containClassName(kd.b.type.Identifier)) {
                throw new semanticError("Semantic Error: function parameter is not defined" , it.pos);
            }
            tmpScope.addVar(kd.a , kd.b.type , it.pos);
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
