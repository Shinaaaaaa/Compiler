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
    public Type tmpFuncReturnType = null;
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
                tmpScope.addVar(kd , new Type(it.variableType.type.varTypeTag , ((arrayTypeNode) it.variableType).dimension) , it.pos);
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
        it.suite.accept(this);
    };

    @Override
    public void visit(suiteNode it) {
        it.stmts.forEach(sd -> sd.accept(this));
    }

    @Override
    public void visit(conditionStmtNode it) {
        it.condition.accept(this);
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Bool , 0) , it.pos)) {
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
        it.loopStmt.accept(this);
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
        tmpScope = tmpScope.parentScope();
    }

    @Override
    public void visit(returnStmtNode it) {
        if (it.value == null) ExprType = new Type(Type.var_type.Void , 0);
        else {
            it.value.accept(this);
        }
        if (!inFunc) {
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
            if (tmpScope instanceof globalScope && ((globalScope) tmpScope).containFuncName(it.varName)) {
                ExprType = ((globalScope) tmpScope).getFuncType(it.varName , it.pos);
            } else if (gScope.containFuncName(it.varName)) {
                ExprType = gScope.getFuncType(it.varName , it.pos);
            } else {
                throw new semanticError("Semantic Error: funcName not exisits" , it.pos);
            }
            return;
        }
        if (!tmpScope.containsVar(it.varName , true)) {
            throw new semanticError("Semantic Error: not defined" , it.pos);
        }
        ExprType = tmpScope.getType(it.varName , true);
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
                if (!ExprType.typeMatchCheck(funcType.parameterList.get(i) , it.pos)) {
                    throw new semanticError("Semantic Error: func parameter & expr not match" , it.pos);
                }
            }
        }
        ExprType = new Type(funcType.funcReturnType.varTypeTag , funcType.funcReturnType.dimension);
    }

    @Override
    public void visit(pointExprNode it) {
        boolean reset = false;
        if (findFuncName) {
            reset = true;
            findFuncName = false;
        }
        it.lhs.accept(this);
        if (ExprType.dimension > 0) {
            ExprType.Identifier = "_Array";
        } else if (ExprType.varTypeTag == Type.var_type.Str) {
            ExprType.Identifier = "String";
        }
        if (!gScope.containClassName(ExprType.Identifier)) {
            throw new semanticError("Semantic Error: class is not defined" , it.pos);
        }
        if (reset) {
            findFuncName = true;
        }
        pointUseScope = tmpScope;
        tmpScope = gScope.getClassScope(ExprType.Identifier , it.pos);
        it.rhs.accept(this);
        tmpScope = pointUseScope;
    }

    @Override
    public void visit(unaryExprNode it) {
        it.expr.accept(this);
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
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
            throw new semanticError("Semantic Error: unary type error" , it.pos);
        }
    }

    @Override
    public void visit(suffixSelfExprNode it) {
        it.expr.accept(this);
        if (!ExprType.typeMatchCheck(new Type(Type.var_type.Int , 0) , it.pos)) {
            throw new semanticError("Semantic Error: unary type error" , it.pos);
        }
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
                if (it.opcode != binaryExprNode.binaryOpType.equal && it.opcode != binaryExprNode.binaryOpType.notequal) {
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
            } else {
                throw new semanticError("Semantic Error: binary exprType error" , it.pos);
            }
        }
    }

    @Override
    public void visit(assignExprNode it) {
        it.lhs.accept(this);
        Type lhsType = ExprType;
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

        if (it.parameterList != null && it.expressionList != null) {
            if (it.parameterList.parameterList.size() == it.expressionList.exprList.size()) {
                throw new semanticError("Semantic Error: lambda parameter & expr not match" , it.pos);
            }
        } else if ((it.expressionList == null && it.parameterList != null)
                || (it.expressionList != null && it.parameterList == null)) {
            throw new semanticError("Semantic Error: lambda parameter & expr not match" , it.pos);
        }
        if (it.parameterList != null) {
            it.parameterList.accept(this);
            for (int i = 0; i < Objects.requireNonNull(it.expressionList).exprList.size() ; ++i) {
                it.expressionList.exprList.get(i).accept(this);
                if (!ExprType.typeMatchCheck(it.parameterList.parameterList.get(i).b.type , it.pos)) {
                    throw new semanticError("Semantic Error: lambda parameter & expr not match" , it.pos);
                }
            }
        }
        it.suite.accept(this);
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
        ExprType = it.type;
    }

    @Override public void visit(expressionListNode it) {}

    @Override
    public void visit(singleTypeNode it) {
        ExprType = it.type;
    }

    @Override
    public void visit(arrayTypeNode it) {
        ExprType = it.type;
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
