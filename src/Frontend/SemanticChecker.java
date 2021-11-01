package Frontend;

import AST.*;
import Util.*;
import Util.error.semanticError;
import AST.unaryExprNode.unaryOpType;

import java.util.Objects;

public class SemanticChecker implements ASTVisitor {
    public globalScope gScope;
    public Scope tmpScope;
    public Scope pointUseScope;
    public Type ExprType;
    public boolean inFunc = false , hasReturn = false;
    public Type tmpFuncReturnType = null , lambdaFuncReturnType = null;
    public boolean findFuncName = false;

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
        if (funcType.parameterList != null) {
            throw new semanticError("Semantic Error: main function parameter error" , it.pos);
        }
    }

    @Override
    public void visit(classDefStmtNode it) {
        it.classDef.accept(this);
    }

    @Override
    public void visit(classDefNode it) {
        tmpScope = gScope.getClassScope(it.className , it.pos);
        it.funcDefList.forEach(fd -> fd.accept(this));
        it.constructfuncDefList.forEach(fd -> fd.accept(this));
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(varDefStmtNode it) {
        it.varDef.accept(this);
    };

    @Override
    public void visit(varDefNode it) {
        if (it.variableType.type.varTypeTag == Type.var_type.Class && !gScope.containClassName(it.variableType.type.Identifier)) {
            throw new semanticError("Semantic Error: class is not defined" , it.pos);
        }
        it.varList.keySet().forEach(kd -> {
            if (gScope.containClassName(kd)) {
                throw new semanticError("Semantic Error: variable rename with class" , it.pos);
            }
            if (it.varList.get(kd) != null) {
                it.varList.get(kd).accept(this);
                if (!(ExprType.varTypeTag == Type.var_type.Null &&
                        (it.variableType.type.dimension > 0 || it.variableType.type.varTypeTag == Type.var_type.Class))) {
                    if (!ExprType.typeMatchCheck(it.variableType.type , it.pos)) {
                        throw new semanticError("Semantic Error: varDef Type not match" , it.pos);
                    }
                }

            }
            if (it.variableType instanceof arrayTypeNode) {
                Type tmpType = new Type(it.variableType.type.varTypeTag , ((arrayTypeNode) it.variableType).dimension);
                tmpType.Identifier = it.variableType.type.Identifier;
                tmpScope.addVar(kd , tmpType , it.pos);
            }
            else tmpScope.addVar(kd , it.variableType.type , it.pos);
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
        inFunc = true;
        if (it.returnType instanceof voidTypeNode) {
            tmpFuncReturnType = new Type(Type.var_type.Void , 0);
            hasReturn = true;
        } else {
            tmpFuncReturnType = ((variableTypeNode) it.returnType).type;
            hasReturn = false;
        }
        it.suite.accept(this);
        if (!hasReturn && !Objects.equals(it.funcName, "main")) {
            throw new semanticError("Semantic Error: return not exists" , it.pos);
        }
        inFunc = false;
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(parameterListNode it) {
        it.parameterList.forEach(kd -> {
            if (kd.b.type.varTypeTag == Type.var_type.Class && !gScope.containClassName(kd.a)) {
                throw new semanticError("Semantic Error: function parameter is not defined" , it.pos);
            }
            tmpScope.addVar(kd.a , kd.b.type , it.pos);
        });
    }

    @Override public void visit(blockStmtNode it) {
        tmpScope = new Scope(tmpScope);
        it.suite.accept(this);
        tmpScope = tmpScope.parentScope();
    };

    @Override
    public void visit(suiteNode it) {
        it.stmts.forEach(sd -> {
            if (sd != null) sd.accept(this);
        });
    }

    @Override
    public void visit(conditionStmtNode it) {
        it.condition.accept(this);
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
            throw new semanticError("Semantic Error:  conditionStmt condition is not Bool" , it.pos);
        }
        if (it.thenStmt != null) {
            tmpScope = new Scope(tmpScope);
            it.thenStmt.accept(this);
            tmpScope = tmpScope.parentScope();
        }
        if (it.elseStmt != null) {
            tmpScope = new Scope(tmpScope);
            it.elseStmt.accept(this);
            tmpScope = tmpScope.parentScope();
        }
    }

    @Override
    public void visit(whileLoopStmtNode it) {
        if (it.condition == null) {
            throw new semanticError("Semantic Error: while condition cannot be null" , it.pos);
        }
        it.condition.accept(this);
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
            throw new semanticError("Semantic Error:  whileLoopStmt condition is not Bool" , it.pos);
        }
        tmpScope = new Scope(tmpScope);
        tmpScope.inLoop = true;
        if (it.loopStmt != null) {
            it.loopStmt.accept(this);
        }
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(forLoopStmtNode it) {
        tmpScope = new Scope(tmpScope);
        tmpScope.inLoop = true;
        if (it.initExpr != null) {
            it.initExpr.accept(this);
        }
        if (it.conditionExpr != null) {
            it.conditionExpr.accept(this);
            if (!ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
                throw new semanticError("Semantic Error:  forLoopStmt condition is not Bool" , it.pos);
            }
        }
        if (it.incrementExpr != null) {
            it.incrementExpr.accept(this);
        }
        if (it.loopStmt != null) {
            it.loopStmt.accept(this);
        }
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(returnStmtNode it) {
        if (it.value == null) ExprType = new Type(Type.var_type.Void , 0);
        else {
            it.value.accept(this);
        }
        if (tmpScope.lambdaExist) {
            tmpScope.lambdaReturn = true;
            lambdaFuncReturnType = ExprType;
        } else if (!inFunc) {
            throw new semanticError("Semantic Error:  returnStmt in wrong position" , it.pos);
        } else {
            if (!ExprType.typeMatchCheck(tmpFuncReturnType , it.pos)) {
                throw new semanticError("Semantic Error:  return type not match" , it.pos);
            } else {
                hasReturn = true;
            }
        }
    }

    @Override
    public void visit(breakStmtNode it) {
        if (!tmpScope.ifInLoop()) {
            throw new semanticError("Semantic Error:  break not in Loop" , it.pos);
        }
    }

    @Override
    public void visit(continueStmtNode it) {
        if (!tmpScope.ifInLoop()) {
            throw new semanticError("Semantic Error:  continue not in Loop" , it.pos);
        }
    }

    @Override
    public void visit(exprStmtNode it) {
        it.expr.accept(this);
    }

    @Override
    public void visit(varExprNode it) {
        if (findFuncName) {
            Scope classScope;
            if (!(tmpScope instanceof globalScope)) {
                classScope = tmpScope.parentScope();
            } else classScope = tmpScope;
            if (classScope instanceof globalScope && ((globalScope) classScope).containFuncName(it.varName)) {
                ExprType = new Type(((globalScope) classScope).getFuncType(it.varName , it.pos));
            } else if (gScope.containFuncName(it.varName)) {
                ExprType = new Type(gScope.getFuncType(it.varName , it.pos));
            } else {
                throw new semanticError("Semantic Error: funcName not exisits" , it.pos);
            }
            return;
        }
        if (!tmpScope.containsVar(it.varName , true)) {
            throw new semanticError("Semantic Error: not defined" , it.pos);
        }
        ExprType = new Type(tmpScope.getType(it.varName , true));
    }

    @Override
    public void visit(thisExprNode it) {
        Scope judgeScope = tmpScope.parentScope();
        if (!(judgeScope instanceof globalScope) || !((globalScope)judgeScope).classTag) {
            throw new semanticError("Semantic Error: this not in class" , it.pos);
        }
    }

    @Override
    public void visit(indexExprNode it) {
        it.index.accept(this);
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
            throw new semanticError("Semantic Error:  index type is not Int" , it.pos);
        }
        it.array.accept(this);
        ExprType = new Type(ExprType);
        ExprType.dimension--;
        if (ExprType.dimension < 0) {
            throw new semanticError("Semantic Error:  array dimension error" , it.pos);
        }
    }

    @Override
    public void visit(functionExprNode it) {
        findFuncName = true;
        it.funcName.accept(this);
        findFuncName = false;
        Type funcType = ExprType;
        if ((it.exprList == null && funcType.parameterList != null)
                || (it.exprList != null && funcType.parameterList == null)) {
            throw new semanticError("Semantic Error: func parameter & expr not match" , it.pos);
        } else if (funcType.parameterList != null && it.exprList != null) {
            if (funcType.parameterList.size() != it.exprList.exprList.size()) {
                throw new semanticError("Semantic Error: func parameter & expr not match" , it.pos);
            }
            for (int i = 0; i < Objects.requireNonNull(it.exprList).exprList.size() ; ++i) {
                it.exprList.exprList.get(i).accept(this);
                if (it.exprList.exprList.get(i) instanceof thisExprNode) {
                    ExprType = new Type(Type.var_type.Class , 0);
                    ExprType.Identifier = ((globalScope) tmpScope.parentScope()).name;
                }
                if (!ExprType.typeMatchCheck(funcType.parameterList.get(i) , it.pos)) {
                    throw new semanticError("Semantic Error: func parameter & expr not match" , it.pos);
                }
            }
        }
        ExprType = new Type(funcType.funcReturnType);
        ExprType.isLeftValue = false;
    }

    @Override
    public void visit(pointExprNode it) {
        boolean reset = false;
        if (findFuncName) {
            reset = true;
            findFuncName = false;
        }
        it.lhs.accept(this);
        if (!(it.lhs instanceof thisExprNode)) {
            if (ExprType.dimension > 0) {
                ExprType.Identifier = "_Array";
            } else if (ExprType.varTypeTag == Type.var_type.Str) {
                ExprType.Identifier = "_String";
            }
            if (!gScope.containClassName(ExprType.Identifier)) {
                throw new semanticError("Semantic Error: class is not defined" , it.pos);
            }
        }
        if (reset) {
            findFuncName = true;
            reset = false;
        }
        pointUseScope = tmpScope;
        if (it.lhs instanceof thisExprNode) {
            tmpScope = tmpScope.parentScope();
        } else {
            tmpScope = gScope.getClassScope(ExprType.Identifier , it.pos);
        }
        it.rhs.accept(this);
        tmpScope = pointUseScope;
    }

    @Override
    public void visit(unaryExprNode it) {
        it.expr.accept(this);
        if (!ExprType.isLeftValue) {
            throw new semanticError("Semantic Error: wrong left value" , it.pos);
        }
        if (it.opcode == unaryOpType.not) {
            if (!ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
                throw new semanticError("Semantic Error:  unary type error" , it.pos);
            }
        } else {
            if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
                throw new semanticError("Semantic Error:  unary type error" , it.pos);
            }
        }
    }

    @Override
    public void visit(prefixSelfExprNode it) {
        it.expr.accept(this);
        if (!ExprType.isLeftValue) {
            throw new semanticError("Semantic Error: wrong left value" , it.pos);
        }
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
            throw new semanticError("Semantic Error: unary type error" , it.pos);
        }
    }

    @Override
    public void visit(suffixSelfExprNode it) {
        it.expr.accept(this);
        if (!ExprType.isLeftValue) {
            throw new semanticError("Semantic Error: wrong left value" , it.pos);
        }
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
            throw new semanticError("Semantic Error: unary type error" , it.pos);
        }
        ExprType.isLeftValue = false;
    }

    @Override
    public void visit(binaryExprNode it) {
        it.lhs.accept(this);
        Type lhsType = ExprType;
        it.rhs.accept(this);
        Type rhsType = ExprType;
        if (lhsType.dimension > 0) {
            if (it.opcode != binaryExprNode.binaryOpType.equal && it.opcode != binaryExprNode.binaryOpType.notequal) {
                throw new semanticError("Semantic Error: array binaryOpcode error" , it.pos);
            }
            if (rhsType.varTypeTag != Type.var_type.Null) {
                if (!lhsType.typeMatchCheck(rhsType , it.pos)) {
                    throw new semanticError("Semantic Error: rhs & lhs type not match" , it.pos);
                }
            }
            ExprType = new Type(Type.var_type.Bool , 0);
        } else if (lhsType.varTypeTag == Type.var_type.Class) {
            if (it.opcode != binaryExprNode.binaryOpType.equal && it.opcode != binaryExprNode.binaryOpType.notequal) {
                throw new semanticError("Semantic Error: class binaryOpcode error" , it.pos);
            }
            ExprType = new Type(Type.var_type.Bool , 0);
        } else {
            if (lhsType.varTypeTag == Type.var_type.Bool) {
                if (it.opcode != binaryExprNode.binaryOpType.equal
                        && it.opcode != binaryExprNode.binaryOpType.notequal
                        && it.opcode != binaryExprNode.binaryOpType.andand
                        && it.opcode != binaryExprNode.binaryOpType.oror) {
                    throw new semanticError("Semantic Error: bool binaryOpcode error" , it.pos);
                }
                if (!lhsType.typeMatchCheck(rhsType , it.pos)) {
                    throw new semanticError("Semantic Error: rhs & lhs type not match" , it.pos);
                }
                ExprType = new Type(Type.var_type.Bool , 0);
            } else if (lhsType.varTypeTag == Type.var_type.Int) {
                if (!lhsType.typeMatchCheck(rhsType , it.pos)) {
                    throw new semanticError("Semantic Error: rhs & lhs type not match" , it.pos);
                }
                if (it.opcode == binaryExprNode.binaryOpType.andand || it.opcode == binaryExprNode.binaryOpType.oror) {
                    throw new semanticError("Semantic Error: int binaryOpcode error" , it.pos);
                } else if (it.opcode == binaryExprNode.binaryOpType.less
                        || it.opcode == binaryExprNode.binaryOpType.lessEqual
                        || it.opcode == binaryExprNode.binaryOpType.greater
                        || it.opcode == binaryExprNode.binaryOpType.greaterEqual
                        || it.opcode == binaryExprNode.binaryOpType.equal
                        || it.opcode == binaryExprNode.binaryOpType.notequal) {
                    ExprType = new Type(Type.var_type.Bool , 0);
                } else {
                    ExprType = new Type(Type.var_type.Int , 0);
                }
            } else if (lhsType.varTypeTag == Type.var_type.Str) {
                if (!lhsType.typeMatchCheck(rhsType , it.pos)) {
                    throw new semanticError("Semantic Error: rhs & lhs type not match" , it.pos);
                }
                if (it.opcode == binaryExprNode.binaryOpType.add) {
                    ExprType = new Type(Type.var_type.Str , 0);
                } else if (it.opcode == binaryExprNode.binaryOpType.equal
                        || it.opcode == binaryExprNode.binaryOpType.notequal
                        || it.opcode == binaryExprNode.binaryOpType.less
                        || it.opcode == binaryExprNode.binaryOpType.lessEqual
                        || it.opcode == binaryExprNode.binaryOpType.greater
                        || it.opcode == binaryExprNode.binaryOpType.greaterEqual) {
                    ExprType = new Type(Type.var_type.Bool , 0);
                } else {
                    throw new semanticError("Semantic Error: string binaryOpcode error" , it.pos);
                }
            } else if (lhsType.varTypeTag == Type.var_type.Null && rhsType.varTypeTag == Type.var_type.Null) {
                ExprType = new Type(Type.var_type.Bool , 0);
            } else {
                throw new semanticError("Semantic Error: binary exprType error" , it.pos);
            }
        }
    }

    @Override
    public void visit(assignExprNode it) {
        if (it.lhs instanceof thisExprNode) {
            throw new semanticError("Semantic Error: This cannot be assigned" , it.pos);
        }
        it.lhs.accept(this);
        Type lhsType = ExprType;
        if (!lhsType.isLeftValue) {
            throw new semanticError("Semantic Error: wrong left value" , it.pos);
        }
        it.rhs.accept(this);
        Type rhsType = ExprType;
        if (lhsType.varTypeTag == Type.var_type.Null) {
            throw new semanticError("Semantic Error: null cannot be assigned" , it.pos);
        }
        if (it.lhs instanceof booleanConstNode
                || it.lhs instanceof intConstNode
                || it.lhs instanceof stringConstNode) {
            throw new semanticError("Semantic Error: const cannot be assigned" , it.pos);
        }
        if (it.rhs instanceof nullConstNode && (lhsType.dimension > 0 || lhsType.varTypeTag == Type.var_type.Class)) {
            return;
        }
        if (!lhsType.typeMatchCheck(rhsType , it.pos)) {
            throw new semanticError("Semantic Error: rhs & lhs type not match" , it.pos);
        }
    }

    @Override
    public void visit(lambdaExprNode it) {
        tmpScope = new Scope(tmpScope);
        tmpScope.lambdaExist = true;
        lambdaFuncReturnType = null;
        if (it.lambdaParameterList != null && it.lambdaExpressionList != null) {
            if (it.lambdaParameterList.parameterList.size() != it.lambdaExpressionList.exprList.size()) {
                throw new semanticError("Semantic Error: lambda parameter & expr not match" , it.pos);
            }
        } else if ((it.lambdaExpressionList == null && it.lambdaParameterList != null)
                || (it.lambdaExpressionList != null && it.lambdaParameterList == null)) {
            throw new semanticError("Semantic Error: lambda parameter & expr not match" , it.pos);
        }
        if (it.lambdaParameterList != null) {
            it.lambdaParameterList.accept(this);
            for (int i = 0; i < Objects.requireNonNull(it.lambdaExpressionList).exprList.size() ; ++i) {
                it.lambdaExpressionList.exprList.get(i).accept(this);
                if (!ExprType.typeMatchCheck(it.lambdaParameterList.parameterList.get(i).b.type , it.pos)) {
                    throw new semanticError("Semantic Error: lambda parameter & expr not match" , it.pos);
                }
            }
        }
        it.suite.accept(this);
        ExprType = lambdaFuncReturnType;
        if (!tmpScope.lambdaReturn) {
            throw new semanticError("Semantic Error: lambda not exists return" , it.pos);
        }
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(newArrayNode it) {
        it.dimExpr.forEach(cd -> {
            cd.accept(this);
            if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
                throw new semanticError("Semantic Error: new array expr is not Int" , it.pos);
            }
        });
        it.type.accept(this);
        ExprType = new Type(ExprType.varTypeTag , it.dimension);
        if (ExprType.varTypeTag == Type.var_type.Class) ExprType.Identifier = it.type.type.Identifier;
    }

    @Override
    public void visit(newClassNode it) {
        ExprType = new Type(it.type);
    }

    @Override public void visit(expressionListNode it) {}

    @Override
    public void visit(singleTypeNode it) {
        ExprType = new Type(it.type);
    }

    @Override
    public void visit(arrayTypeNode it) {
        ExprType = new Type(it.type);
    }

    @Override
    public void visit(voidTypeNode it) {
        ExprType = new Type(Type.var_type.Void , 0);
    }

    @Override
    public void visit(intConstNode it) {
        ExprType = new Type(Type.var_type.Int , 0);
    }

    @Override
    public void visit(stringConstNode it) {
        ExprType = new Type(Type.var_type.Str , 0);
    }

    @Override
    public void visit(booleanConstNode it) {
        ExprType = new Type(Type.var_type.Bool , 0);
    }

    @Override
    public void visit(nullConstNode it) {
        ExprType = new Type(Type.var_type.Null , 0);
    }
}
