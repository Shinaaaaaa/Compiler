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
import Util.*;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class IRBuilder implements ASTVisitor {
    public IRroot irroot;
    public entityScope eScope;
    private String tmpClassName;
    private String varString;
    private func tmpFuncType;
    private entity exprEntity;
    private IRType tmpIRType;
    private boolean callFunc;
    private String callFuncName;
    private boolean pointUse;
    private int stringCount;
    private boolean boolControl;
    private ArrayList<entity> callFuncParaList;
    private ArrayList<basicBlock> returnBlockList;
    private ArrayList<basicBlock> breakBlockList = new ArrayList<>();
    private ArrayList<basicBlock> continueBlockList = new ArrayList<>();

    register mallocArray(ArrayList<entity> sizeList , IRType baseType , int depth) {
        assert baseType instanceof pointerType;
        int baseByte = 8;
        register mallocReg;
        if (((pointerType) baseType).pointerType instanceof integerType) {
            baseByte = 4;
        }
        if (!(sizeList.get(depth) instanceof intConst)) {
            register mulReg = tmpFuncType.getNewRegisterInFunc(new integerType(32));
            tmpFuncType.addInstInLastBlock(new binaryInst(mulReg , binaryInst.binaryType.mul , sizeList.get(depth) , new intConst(false , baseByte)));
            register addReg = tmpFuncType.getNewRegisterInFunc(new integerType(32));
            tmpFuncType.addInstInLastBlock(new binaryInst(addReg , binaryInst.binaryType.add , mulReg , new intConst(false , 4)));
            IRType returnType = new pointerType(new integerType(8));
            ArrayList<entity> bytes = new ArrayList<>();
            bytes.add(addReg);
            mallocReg = tmpFuncType.getNewRegisterInFunc(returnType);
            tmpFuncType.addInstInLastBlock(new callInst(mallocReg , new functionType(returnType , "malloc" , bytes)));
        } else {
            IRType returnType = new pointerType(new integerType(8));
            ArrayList<entity> bytes = new ArrayList<>();
            bytes.add(new intConst(false , 4 + baseByte * ((intConst)sizeList.get(depth)).value));
            mallocReg = tmpFuncType.getNewRegisterInFunc(returnType);
            tmpFuncType.addInstInLastBlock(new callInst(mallocReg , new functionType(returnType , "malloc" , bytes)));
        }
        register mallocBitcastReg = tmpFuncType.getNewRegisterInFunc(new pointerType(new integerType(32)));
        tmpFuncType.addInstInLastBlock(new bitcastInst(mallocReg , mallocBitcastReg));
        tmpFuncType.addInstInLastBlock(new storeInst(sizeList.get(depth) , mallocBitcastReg));
        register arrayBeginReg = tmpFuncType.getNewRegisterInFunc(new pointerType(new integerType(8)));
        tmpFuncType.addInstInLastBlock(new getelementptrInst(arrayBeginReg , mallocReg , new intConst(false , 4)));
        register arrayBitcastReg = tmpFuncType.getNewRegisterInFunc(baseType);
        tmpFuncType.addInstInLastBlock(new bitcastInst(arrayBeginReg , arrayBitcastReg));
        if (depth == sizeList.size() - 1) return arrayBitcastReg;
        else {
            register pointerReg = tmpFuncType.getNewRegisterInFunc(new pointerType(new integerType(32)));
            tmpFuncType.addInstInLastBlock(new allocaInst(pointerReg));
            tmpFuncType.addInstInLastBlock(new storeInst(new intConst(false , 0) , pointerReg));
            basicBlock initBlock = tmpFuncType.getLastBlock();
            tmpFuncType.addLastBlock();
            basicBlock conditionBlock = tmpFuncType.getLastBlock();
            register condLoadReg = tmpFuncType.getNewRegisterInFunc(new integerType(32));
            tmpFuncType.addInstInLastBlock(new loadInst(pointerReg , condLoadReg));
            register condResultReg = tmpFuncType.getNewRegisterInFunc(new integerType(1));
            tmpFuncType.addInstInLastBlock(new icmpInst(condResultReg , icmpInst.icmpType.slt , condLoadReg , sizeList.get(depth)));
            tmpFuncType.addLastBlock();
            basicBlock loopBlock = tmpFuncType.getLastBlock();
            register mallocReturnReg = mallocArray(sizeList , ((pointerType)baseType).pointerType , depth + 1);
            basicBlock loopLastBlock = tmpFuncType.getLastBlock();
            register loopStoreReg = tmpFuncType.getNewRegisterInFunc(baseType);
            tmpFuncType.addInstInLastBlock(new getelementptrInst(loopStoreReg , arrayBitcastReg , condLoadReg));
            tmpFuncType.addInstInLastBlock(new storeInst(mallocReturnReg , loopStoreReg));
            tmpFuncType.addLastBlock();
            basicBlock incrementBlock = tmpFuncType.getLastBlock();
            register increasePointerReg = tmpFuncType.getNewRegisterInFunc(new integerType(32));
            tmpFuncType.addInstInLastBlock(new binaryInst(increasePointerReg , binaryInst.binaryType.add , condLoadReg , new intConst(false , 1)));
            tmpFuncType.addInstInLastBlock(new storeInst(increasePointerReg , pointerReg));
            tmpFuncType.addLastBlock();

            initBlock.addTerminalInst(new brInst(conditionBlock));
            conditionBlock.addPreSucceedBlock(initBlock);
            conditionBlock.addPreSucceedBlock(incrementBlock);
            brInst conditionBr = new brInst(condResultReg);
            conditionBr.setTrueBlock(loopBlock);
            conditionBr.setFalseBlock(tmpFuncType.getLastBlock());
            conditionBlock.addTerminalInst(conditionBr);
            loopBlock.addPreSucceedBlock(conditionBlock);
            loopLastBlock.addTerminalInst(new brInst(incrementBlock));
            incrementBlock.addPreSucceedBlock(loopLastBlock);
            incrementBlock.addTerminalInst(new brInst(conditionBlock));
            tmpFuncType.getLastBlock().addPreSucceedBlock(conditionBlock);
            return arrayBitcastReg;
        }
    }

    public IRBuilder(IRroot irroot , entityScope eScope) {
        this.irroot = irroot;
        this.eScope = eScope;
        tmpFuncType = irroot.funcList.get("__cxx_global_var_init");
    }

    @Override
    public void visit(programNode it) {
        it.subprogramList.forEach(sd -> {
            if (sd instanceof varDefNode) sd.accept(this);
        });
        it.subprogramList.forEach(sd -> {
            if (!(sd instanceof varDefNode)) sd.accept(this);
        });
    }

    @Override
    public void visit(classDefStmtNode it) {
        it.classDef.accept(this);
    }

    @Override
    public void visit(classDefNode it) {
        tmpClassName = it.className;
        it.constructfuncDefList.forEach(cd -> cd.accept(this));
        if (it.constructfuncDefList.size() == 0) {
            eScope = new entityScope(eScope);
            tmpFuncType = irroot.funcList.get(tmpClassName);
            tmpFuncType.Init(eScope);
            tmpFuncType.addInstInLastBlock(new retInst(null));
            tmpFuncType = null;
            eScope = eScope.getParentEntityScope();
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
            if (tmpFuncType == irroot.funcList.get("__cxx_global_var_init")) {
                exprEntity = null;
                if (it.varList.get(kd) != null) {
                    if (it.varList.get(kd) instanceof nullConstNode) {
                        exprEntity = new nullConst(tmpIRType);
                    }
                    else {
                        if (tmpIRType instanceof integerType) {
                            if (((integerType) tmpIRType).isBool()) boolControl = true;
                        }
                        it.varList.get(kd).accept(this);
                        boolControl = false;
                        if (exprEntity.isLeftValue) {
                            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                            exprEntity = tmp;
                        }
                    }
                    globalVariable globalVar = new globalVariable(true , kd , new pointerType(tmpIRType) , exprEntity);
                    eScope.addEntity(kd , globalVar);
                    irroot.addGlobalVariable(kd , globalVar);
                    tmpFuncType.addInstInLastBlock(new storeInst(exprEntity , globalVar));
                }
                else {
                    globalVariable globalVar = new globalVariable(true , kd , new pointerType(tmpIRType) , exprEntity);
                    eScope.addEntity(kd , globalVar);
                    irroot.addGlobalVariable(kd , globalVar);
                }
            }
            else {
                register reg = tmpFuncType.getNewRegisterInFunc(new pointerType(tmpIRType));
                reg.isLeftValue = true;
                eScope.addEntity(kd , reg);
                tmpFuncType.addInstInLastBlock(new allocaInst(reg));
                if (it.varList.get(kd) != null) {
                    if (it.varList.get(kd) instanceof nullConstNode) {
                        exprEntity = new nullConst(tmpIRType);
                    }
                    else {
                        if (tmpIRType instanceof integerType) {
                            if (((integerType) tmpIRType).isBool()) boolControl = true;
                        }
                        it.varList.get(kd).accept(this);
                        boolControl = false;
                        if (exprEntity.isLeftValue) {
                            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                            exprEntity = tmp;
                        }
                    }
                    tmpFuncType.addInstInLastBlock(new storeInst(exprEntity , reg));
                }
            }
        });
    }

    @Override
    public void visit(constructfuncDefNode it) {
        eScope = new entityScope(eScope);
        tmpFuncType = irroot.funcList.get(tmpClassName);
        tmpFuncType.Init(eScope);
        it.suite.accept(this);
        tmpFuncType.addInstInLastBlock(new retInst(null));
        tmpFuncType = null;
        eScope = eScope.getParentEntityScope();
    }

    @Override
    public void visit(funcDefNode it) {
        eScope = new entityScope(eScope);
        returnBlockList = new ArrayList<>();
        if (tmpClassName == null) tmpFuncType = irroot.funcList.get(it.funcName);
        else tmpFuncType = irroot.funcList.get(tmpClassName + "." + it.funcName);
        tmpFuncType.Init(eScope);
        if (Objects.equals(tmpFuncType.funcName, "main")) {
            tmpFuncType.addInstInLastBlock(new callInst(null , new functionType(null , "__cxx_global_var_init" , null)));
        }
        it.suite.accept(this);


        basicBlock preBlock = tmpFuncType.getLastBlock();
        tmpFuncType.addLastBlock();
        basicBlock retBlock = tmpFuncType.getLastBlock();
        preBlock.addTerminalInst(new brInst(retBlock));
        retBlock.addPreSucceedBlock(preBlock);


        returnBlockList.forEach(rd -> {
            rd.addTerminalInst(new brInst(retBlock));
            retBlock.addPreSucceedBlock(rd);
        });
        if (tmpFuncType.returnType == null) {
            tmpFuncType.addInstInLastBlock(new retInst(null));
        }
        else {
            register ret_reg = tmpFuncType.getNewRegisterInFunc(tmpFuncType.returnType);
            tmpFuncType.addInstInLastBlock(new loadInst(eScope.getEntity("_reg_return") , ret_reg));
            tmpFuncType.addInstInLastBlock(new retInst(ret_reg));
        }
        tmpFuncType = null;
        eScope = eScope.getParentEntityScope();
    }

    @Override public void visit(parameterListNode it) {}

    @Override
    public void visit(blockStmtNode it) {
        eScope = new entityScope(eScope);
        it.suite.accept(this);
        eScope = eScope.getParentEntityScope();
    };

    @Override
    public void visit(suiteNode it) {
        for (int i = 0 ; i < it.stmts.size() ; i++) {
            it.stmts.get(i).accept(this);
            if (it.stmts.get(i) instanceof breakStmtNode
                    || it.stmts.get(i) instanceof continueStmtNode
                    || it.stmts.get(i) instanceof returnStmtNode) {
                break;
            }
        }
    }

    @Override
    public void visit(conditionStmtNode it) {
        boolControl = true;
        it.condition.accept(this);
        boolControl = false;
        basicBlock conditionBlock = tmpFuncType.getLastBlock();
        if (exprEntity.isLeftValue) {
            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
            exprEntity = tmp;
        }
        brInst conditionBr = new brInst(exprEntity);

        tmpFuncType.addLastBlock();
        basicBlock thenBlock = tmpFuncType.getLastBlock();
        eScope = new entityScope(eScope);
        it.thenStmt.accept(this);
        eScope = eScope.getParentEntityScope();
        basicBlock thenLastBlock = tmpFuncType.getLastBlock();
        basicBlock elseBlock = null;
        basicBlock elseLastBlock = null;

        if (it.elseStmt != null) {
            tmpFuncType.addLastBlock();
            elseBlock = tmpFuncType.getLastBlock();
            eScope = new entityScope(eScope);
            it.elseStmt.accept(this);
            eScope = eScope.getParentEntityScope();
            elseLastBlock = tmpFuncType.getLastBlock();
        }

        tmpFuncType.addLastBlock();
        if (elseBlock != null) {
            conditionBr.setTrueBlock(thenBlock);
            conditionBr.setFalseBlock(elseBlock);
            conditionBlock.addTerminalInst(conditionBr);
            thenLastBlock.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
            thenBlock.addPreSucceedBlock(conditionBlock);
            elseLastBlock.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
            elseBlock.addPreSucceedBlock(conditionBlock);
            tmpFuncType.getLastBlock().addPreSucceedBlock(thenLastBlock);
            tmpFuncType.getLastBlock().addPreSucceedBlock(elseLastBlock);
        } else {
            conditionBr.setTrueBlock(thenBlock);
            conditionBr.setFalseBlock(tmpFuncType.getLastBlock());
            conditionBlock.addTerminalInst(conditionBr);
            thenLastBlock.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
            thenBlock.addPreSucceedBlock(conditionBlock);
            tmpFuncType.getLastBlock().addPreSucceedBlock(conditionBlock);
            tmpFuncType.getLastBlock().addPreSucceedBlock(thenLastBlock);
        }
    }

    @Override
    public void visit(whileLoopStmtNode it) {
        ArrayList<basicBlock> tmpBreakBlockList = breakBlockList;
        ArrayList<basicBlock> tmpContinueBlockList = continueBlockList;
        breakBlockList = new ArrayList<>();
        continueBlockList = new ArrayList<>();

        basicBlock preBlock = tmpFuncType.getLastBlock();

        tmpFuncType.addLastBlock();
        basicBlock conditionBlock = tmpFuncType.getLastBlock();
        it.condition.accept(this);
        if (exprEntity.isLeftValue) {
            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
            exprEntity = tmp;
        }
        brInst conditionBr = new brInst(exprEntity);

        tmpFuncType.addLastBlock();
        basicBlock loopBlock = tmpFuncType.getLastBlock();
        eScope = new entityScope(eScope);
        it.loopStmt.accept(this);
        eScope = eScope.getParentEntityScope();
        basicBlock loopLastBlock = tmpFuncType.getLastBlock();

        tmpFuncType.addLastBlock();
        preBlock.addTerminalInst(new brInst(conditionBlock));
        conditionBr.setTrueBlock(loopBlock);
        conditionBr.setFalseBlock(tmpFuncType.getLastBlock());
        conditionBlock.addTerminalInst(conditionBr);
        conditionBlock.addPreSucceedBlock(preBlock);
        loopLastBlock.addTerminalInst(new brInst(conditionBlock));
        loopBlock.addPreSucceedBlock(conditionBlock);
        tmpFuncType.getLastBlock().addPreSucceedBlock(conditionBlock);

        breakBlockList.forEach(bd -> {
            bd.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
            tmpFuncType.getLastBlock().addPreSucceedBlock(bd);
        });
        continueBlockList.forEach(cd -> {
            cd.addTerminalInst(new brInst(conditionBlock));
            conditionBlock.addPreSucceedBlock(cd);
        });
        breakBlockList = tmpBreakBlockList;
        continueBlockList = tmpContinueBlockList;
    }

    @Override
    public void visit(forLoopStmtNode it) {
        ArrayList<basicBlock> tmpBreakBlockList = breakBlockList;
        ArrayList<basicBlock> tmpContinueBlockList = continueBlockList;
        breakBlockList = new ArrayList<>();
        continueBlockList = new ArrayList<>();

        eScope = new entityScope(eScope);
        basicBlock preBlock = tmpFuncType.getLastBlock();
        if (it.initExpr != null) it.initExpr.accept(this);

        tmpFuncType.addLastBlock();
        basicBlock conditionBlock = tmpFuncType.getLastBlock();
        brInst conditionBr = null;
        if (it.conditionExpr != null) {
            it.conditionExpr.accept(this);
            if (exprEntity.isLeftValue) {
                register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                exprEntity = tmp;
            }
            conditionBr = new brInst(exprEntity);
        }

        tmpFuncType.addLastBlock();
        basicBlock loopBlock = tmpFuncType.getLastBlock();
        if (it.loopStmt != null) {
            it.loopStmt.accept(this);
        }
        eScope = eScope.getParentEntityScope();
        basicBlock loopLastBlock = tmpFuncType.getLastBlock();

        tmpFuncType.addLastBlock();
        basicBlock incrementBlock = tmpFuncType.getLastBlock();
        if (it.incrementExpr != null) it.incrementExpr.accept(this);

        tmpFuncType.addLastBlock();
        preBlock.addTerminalInst(new brInst(conditionBlock));
        if (conditionBr != null) {
            conditionBr.setTrueBlock(loopBlock);
            conditionBr.setFalseBlock(tmpFuncType.getLastBlock());
        } else {
            conditionBr = new brInst(loopBlock);
        }
        conditionBlock.addTerminalInst(conditionBr);
        conditionBlock.addPreSucceedBlock(preBlock);
        conditionBlock.addPreSucceedBlock(incrementBlock);
        loopLastBlock.addTerminalInst(new brInst(incrementBlock));
        loopBlock.addPreSucceedBlock(conditionBlock);
        incrementBlock.addTerminalInst(new brInst(conditionBlock));
        incrementBlock.addPreSucceedBlock(loopLastBlock);
        tmpFuncType.getLastBlock().addPreSucceedBlock(conditionBlock);

        breakBlockList.forEach(bd -> {
            bd.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
            tmpFuncType.getLastBlock().addPreSucceedBlock(bd);
        });
        continueBlockList.forEach(cd -> {
            cd.addTerminalInst(new brInst(incrementBlock));
            conditionBlock.addPreSucceedBlock(cd);
        });
        breakBlockList = tmpBreakBlockList;
        continueBlockList = tmpContinueBlockList;
    }

    @Override
    public void visit(returnStmtNode it) {
        if (it.value != null) {
            entity returnReg = eScope.getEntity("_reg_return");
            if (it.value instanceof nullConstNode) {
                exprEntity = new nullConst(((pointerType)returnReg.getIRType()).pointerType);
            } else {
                it.value.accept(this);
                if (exprEntity.isLeftValue) {
                    register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                    exprEntity = tmp;
                }
            }
            tmpFuncType.addInstInLastBlock(new storeInst(exprEntity , returnReg));
        }
        returnBlockList.add(tmpFuncType.getLastBlock());
    }

    @Override
    public void visit(breakStmtNode it) {
        breakBlockList.add(tmpFuncType.getLastBlock());
    }

    @Override
    public void visit(continueStmtNode it) {
        continueBlockList.add(tmpFuncType.getLastBlock());
    }

    @Override
    public void visit(exprStmtNode it) {
        it.expr.accept(this);
    }

    @Override
    public void visit(varExprNode it) {
        if (pointUse) varString = it.varName;
        else if (!callFunc) {
            if (eScope.containEntity(it.varName)) exprEntity = eScope.getEntity(it.varName);
            else if (tmpClassName != null) {
                classType hideClassType = irroot.getClass(tmpClassName);
                entity hideClassPara = eScope.getEntity(tmpClassName);
                if (hideClassPara.isLeftValue) {
                    register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)hideClassPara.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(hideClassPara , loadReg));
                    hideClassPara = loadReg;
                }
                int pointer = 0;
                for (; pointer < hideClassType.typeLists.size() ; ++pointer) {
                    if (Objects.equals(hideClassType.typeLists.get(pointer).a, it.varName)) break;
                }
                register getelementptrReg = tmpFuncType.getNewRegisterInFunc(new pointerType(hideClassType.typeLists.get(pointer).b));
                getelementptrReg.isLeftValue = true;
                tmpFuncType.addInstInLastBlock(new getelementptrInst(getelementptrReg , hideClassPara , new intConst(false , 0) , new intConst(false , pointer)));
                exprEntity = getelementptrReg;
            }
        }
        else callFuncName = it.varName;
    }

    @Override
    public void visit(thisExprNode it) {
        if (!callFunc) exprEntity = eScope.getEntity(tmpClassName);
    }

    @Override
    public void visit(indexExprNode it) {
        it.array.accept(this);
        if (exprEntity.isLeftValue) {
            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
            exprEntity = tmp;
        }
        entity arrayReg = exprEntity;
        it.index.accept(this);
        if (exprEntity.isLeftValue) {
            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
            exprEntity = tmp;
        }
        entity indexReg = exprEntity;
        register destReg = tmpFuncType.getNewRegisterInFunc(arrayReg.getIRType());
        destReg.isLeftValue = true;
        tmpFuncType.addInstInLastBlock(new getelementptrInst(destReg , arrayReg , indexReg));
        exprEntity = destReg;
    }

    @Override
    public void visit(functionExprNode it) {
        String funcName;
        callFunc = true;
        callFuncName = null;
        callFuncParaList = new ArrayList<>();
        it.funcName.accept(this);
        funcName = callFuncName;
        callFunc = false;
        if (it.funcName instanceof pointExprNode) {
            if (Objects.equals(funcName, "_array_size")) {
                if (exprEntity.isLeftValue) {
                    register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                    exprEntity = tmp;
                }
                register bitcastReg = tmpFuncType.getNewRegisterInFunc(new pointerType(new integerType(8)));
                tmpFuncType.addInstInLastBlock(new bitcastInst(exprEntity , bitcastReg));
                callFuncParaList.add(bitcastReg);
            }
            else if (Objects.equals(funcName, "_str_length") ||
                    Objects.equals(funcName, "_str_substring") ||
                    Objects.equals(funcName, "_str_parseInt") ||
                    Objects.equals(funcName, "_str_ord")) {
                if (exprEntity.isLeftValue) {
                    register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                    exprEntity = tmp;
                }
                callFuncParaList.add(exprEntity);
            }
            else {
                if (exprEntity.isLeftValue) {
                    register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                    exprEntity = tmp;
                }
                callFuncParaList.add(exprEntity);
            }
        }
        if (it.exprList != null) it.exprList.accept(this);
        IRType returnType;
        if (irroot.containsFunc(funcName)) returnType = irroot.getFunc(funcName).returnType;
        else {
            funcName = tmpClassName + "." + funcName;
            returnType = irroot.getFunc(funcName).returnType;
            entity hideClassPara = eScope.getEntity(tmpClassName);
            if (hideClassPara.isLeftValue) {
                register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)hideClassPara.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(hideClassPara , loadReg));
                hideClassPara = loadReg;
            }
            callFuncParaList.add( 0 , hideClassPara);
        }
        ArrayList<entity> funcParaList = new ArrayList<>(callFuncParaList);
        callFuncParaList.clear();
        functionType funcType = new functionType(returnType , funcName , funcParaList);
        if (returnType == null) {
            tmpFuncType.addInstInLastBlock(new callInst(null , funcType));
        }
        else {
            register tmp = tmpFuncType.getNewRegisterInFunc(returnType);
            tmpFuncType.addInstInLastBlock(new callInst(tmp , funcType));
            exprEntity = tmp;
        }
    }

    @Override
    public void visit(pointExprNode it) {
        boolean reset = false;
        if (callFunc) {
            callFunc = false;
            reset = true;
        }
        it.lhs.accept(this);
        if (exprEntity.isLeftValue) {
            register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
            exprEntity = loadReg;
        }
        entity lhsEntity = exprEntity;
        IRType lhsType = lhsEntity.getIRType();
        assert lhsType instanceof pointerType;
        if (reset) {
            if (((pointerType) lhsType).pointerType instanceof classType) callFuncName = ((classType) ((pointerType) lhsType).pointerType).className + ".";
            else if (((pointerType) lhsType).isString()) callFuncName = "_str_";
            else callFuncName = "_array_";
        }
        pointUse = true;
        it.rhs.accept(this);
        pointUse = false;
        if (reset) {
            callFuncName += varString;
            exprEntity = lhsEntity;
        }
        else {
            int pointer = 0;
            for (; pointer < ((classType) ((pointerType) lhsType).pointerType).typeLists.size() ; ++pointer) {
                if (Objects.equals(((classType) ((pointerType) lhsType).pointerType).typeLists.get(pointer).a, varString)) break;
            }
            register getelementptrReg = tmpFuncType.getNewRegisterInFunc(new pointerType (((classType) ((pointerType) lhsType).pointerType).typeLists.get(pointer).b));
            getelementptrReg.isLeftValue = true;
            tmpFuncType.addInstInLastBlock(new getelementptrInst(getelementptrReg , lhsEntity , new intConst(false , 0) , new intConst(false , pointer)));
            exprEntity = getelementptrReg;
        }
    }

    @Override
    public void visit(unaryExprNode it) {
        it.expr.accept(this);
        if (exprEntity.isLeftValue) {
            register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
            tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
            exprEntity = tmp;
        }
        if (it.opcode == unaryExprNode.unaryOpType.not) {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.xor , exprEntity , new boolConst(false , 1)));
            exprEntity = tmp;
        }
        else if (it.opcode == unaryExprNode.unaryOpType.tilde) {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.xor , exprEntity , new intConst(false ,-1)));
            exprEntity = tmp;
        }
        else if (it.opcode == unaryExprNode.unaryOpType.minus) {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.sub , new intConst(false ,0) , exprEntity));
            exprEntity = tmp;
        }
    }

    @Override
    public void visit(prefixSelfExprNode it) {
        it.expr.accept(this);
        entity leftEntity = exprEntity;
        register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
        tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
        exprEntity = loadReg;
        if (it.opcode == prefixSelfExprNode.prefixSelfOpType.selfPlus) {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.add , exprEntity , new intConst(false, 1)));
            tmpFuncType.addInstInLastBlock(new storeInst(tmp , leftEntity));
            exprEntity = leftEntity;
        }
        else {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.sub , exprEntity , new intConst(false, 1)));
            tmpFuncType.addInstInLastBlock(new storeInst(tmp , leftEntity));
            exprEntity = leftEntity;
        }
    }

    @Override
    public void visit(suffixSelfExprNode it) {
        it.expr.accept(this);
        entity leftEntity = exprEntity;
        register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
        tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
        exprEntity = loadReg;
        if (it.opcode == suffixSelfExprNode.suffixSelfOpType.selfPlus) {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.add , exprEntity , new intConst(false, 1)));
            tmpFuncType.addInstInLastBlock(new storeInst(tmp , leftEntity));
            exprEntity = loadReg;
        }
        else {
            register tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.sub , exprEntity , new intConst(false, 1)));
            tmpFuncType.addInstInLastBlock(new storeInst(tmp , leftEntity));
            exprEntity = loadReg;
        }
    }

    @Override
    public void visit(binaryExprNode it) {
        if (boolControl && (it.opcode == binaryExprNode.binaryOpType.andand || it.opcode == binaryExprNode.binaryOpType.oror)) {
            it.lhs.accept(this);
            if (exprEntity.isLeftValue) {
                register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
                exprEntity = loadReg;
            }
            register cmpReg = tmpFuncType.getNewRegisterInFunc(new integerType(1));
            tmpFuncType.addInstInLastBlock(new icmpInst(cmpReg , icmpInst.icmpType.ne , exprEntity , new boolConst(false , 0)));
            basicBlock lhsLastBlock = tmpFuncType.getLastBlock();
            brInst lhsBr = new brInst(cmpReg);
            tmpFuncType.addLastBlock();
            basicBlock rhsBlock = tmpFuncType.getLastBlock();
            it.rhs.accept(this);
            if (exprEntity.isLeftValue) {
                register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
                exprEntity = loadReg;
            }
            basicBlock rhsLastBlock = tmpFuncType.getLastBlock();
            tmpFuncType.addLastBlock();
            if (it.opcode == binaryExprNode.binaryOpType.andand) {
                register phiReg = tmpFuncType.getNewRegisterInFunc(new integerType(1));
                tmpFuncType.addInstInLastBlock(new phiInst(phiReg , new boolConst(false , 0) , exprEntity , lhsLastBlock , rhsLastBlock));
                lhsBr.setTrueBlock(rhsBlock);
                rhsBlock.addPreSucceedBlock(lhsLastBlock);
                lhsBr.setFalseBlock(tmpFuncType.getLastBlock());
                tmpFuncType.getLastBlock().addPreSucceedBlock(lhsLastBlock);
                lhsLastBlock.addTerminalInst(lhsBr);
                rhsLastBlock.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
                tmpFuncType.getLastBlock().addPreSucceedBlock(rhsLastBlock);
                exprEntity = phiReg;
            }
            else {
                register phiReg = tmpFuncType.getNewRegisterInFunc(new integerType(1));
                tmpFuncType.addInstInLastBlock(new phiInst(phiReg , new boolConst(false , 1) , exprEntity , lhsLastBlock , rhsLastBlock));
                lhsBr.setTrueBlock(tmpFuncType.getLastBlock());
                tmpFuncType.getLastBlock().addPreSucceedBlock(lhsLastBlock);
                lhsBr.setFalseBlock(rhsBlock);
                rhsBlock.addPreSucceedBlock(lhsLastBlock);
                lhsLastBlock.addTerminalInst(lhsBr);
                rhsLastBlock.addTerminalInst(new brInst(tmpFuncType.getLastBlock()));
                tmpFuncType.getLastBlock().addPreSucceedBlock(rhsLastBlock);
                exprEntity = phiReg;
            }
        }
        else {
            entity lhsEntity , rhsEntity;
            if (!(it.lhs instanceof nullConstNode)) {
                it.lhs.accept(this);
                if (exprEntity.isLeftValue) {
                    register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
                    exprEntity = loadReg;
                }
            }
            lhsEntity = exprEntity;
            if (!(it.rhs instanceof nullConstNode)) {
                it.rhs.accept(this);
                if (exprEntity.isLeftValue) {
                    register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                    tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
                    exprEntity = loadReg;
                }
            }
            rhsEntity = exprEntity;
            register tmp;
            if (it.opcode == binaryExprNode.binaryOpType.less ||
                    it.opcode == binaryExprNode.binaryOpType.lessEqual ||
                    it.opcode == binaryExprNode.binaryOpType.greater ||
                    it.opcode == binaryExprNode.binaryOpType.greaterEqual ||
                    it.opcode == binaryExprNode.binaryOpType.equal ||
                    it.opcode == binaryExprNode.binaryOpType.notequal) {
                if (it.rhs instanceof nullConstNode) rhsEntity = new nullConst(lhsEntity.getIRType());
                else if (it.lhs instanceof nullConstNode) lhsEntity = new nullConst(rhsEntity.getIRType());
                tmp = tmpFuncType.getNewRegisterInFunc(new integerType(1));
            }
            else tmp = tmpFuncType.getNewRegisterInFunc(exprEntity.getIRType());
            boolean strFlag = lhsEntity.getIRType() instanceof pointerType && ((pointerType) lhsEntity.getIRType()).isString();
            if (it.opcode == binaryExprNode.binaryOpType.mul) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.mul , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.div) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.sdiv , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.mod) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.srem , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.add) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strAdd = new functionType(new pointerType(new integerType(8)) , "_str_add" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strAdd));
                }
                else tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.add , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.sub) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.sub , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.leftShift) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.shl , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.rightShift) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.ashr , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.less) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strLess = new functionType(new integerType(1) , "_str_less" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strLess));
                }
                else tmpFuncType.addInstInLastBlock(new icmpInst(tmp , icmpInst.icmpType.slt , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.greater) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strGreater = new functionType(new integerType(1) , "_str_greater" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strGreater));
                }
                else tmpFuncType.addInstInLastBlock(new icmpInst(tmp , icmpInst.icmpType.sgt , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.lessEqual) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strLessEqual = new functionType(new integerType(1) , "_str_lessEqual" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strLessEqual));
                }
                else tmpFuncType.addInstInLastBlock(new icmpInst(tmp , icmpInst.icmpType.sle , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.greaterEqual) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strGreaterEqaul = new functionType(new integerType(1) , "_str_greaterEqual" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strGreaterEqaul));
                }
                else tmpFuncType.addInstInLastBlock(new icmpInst(tmp , icmpInst.icmpType.sge , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.equal) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strEqual = new functionType(new integerType(1) , "_str_equal" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strEqual));
                }
                else tmpFuncType.addInstInLastBlock(new icmpInst(tmp , icmpInst.icmpType.eq , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.notequal) {
                if (strFlag) {
                    ArrayList<entity> paraList = new ArrayList<>();
                    paraList.add(lhsEntity);
                    paraList.add(rhsEntity);
                    functionType strNotEqual = new functionType(new integerType(1) , "_str_notequal" , paraList);
                    tmpFuncType.addInstInLastBlock(new callInst(tmp , strNotEqual));
                }
                else tmpFuncType.addInstInLastBlock(new icmpInst(tmp , icmpInst.icmpType.ne , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.and) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.and , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.caret) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.xor , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.or) tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.or , lhsEntity , rhsEntity));
            else if (it.opcode == binaryExprNode.binaryOpType.andand) {
                tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.and , lhsEntity , rhsEntity));
            }
            else if (it.opcode == binaryExprNode.binaryOpType.oror) {
                tmpFuncType.addInstInLastBlock(new binaryInst(tmp , binaryInst.binaryType.or , lhsEntity , rhsEntity));
            }
            exprEntity = tmp;
        }
    }

    @Override
    public void visit(assignExprNode it) {
        it.lhs.accept(this);
        entity lhsEntity = exprEntity;
        if (!(it.rhs instanceof nullConstNode)) {
            if (lhsEntity.getIRType() instanceof pointerType) {
                if (((pointerType) lhsEntity.getIRType()).pointerType instanceof integerType) {
                    if (((integerType) ((pointerType) lhsEntity.getIRType()).pointerType).isBool()) boolControl = true;
                }
            }
            it.rhs.accept(this);
            boolControl = false;
            if (exprEntity.isLeftValue) {
                register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
                exprEntity = loadReg;
            }
        }
        entity rhsEntity = exprEntity;
        if (it.rhs instanceof nullConstNode) {
            assert  lhsEntity.getIRType() instanceof pointerType;
            rhsEntity = new nullConst(((pointerType) lhsEntity.getIRType()).pointerType);
        }
        tmpFuncType.addInstInLastBlock(new storeInst(rhsEntity , lhsEntity));
    }

    @Override
    public void visit(newArrayNode it) {
        ArrayList<entity> sizeList = new ArrayList<>();
        it.dimExpr.forEach(dd -> {
            dd.accept(this);
            if (exprEntity.isLeftValue) {
                register loadReg = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , loadReg));
                exprEntity = loadReg;
            }
            sizeList.add(exprEntity);
        });
        IRType baseType;
        if (it.type.type.varTypeTag == Type.var_type.Int) baseType = new integerType(32);
        else if (it.type.type.varTypeTag == Type.var_type.Bool) baseType = new integerType(1);
        else if (it.type.type.varTypeTag == Type.var_type.Str) baseType = new pointerType(new integerType(8));
        else baseType = new pointerType(irroot.getClass(it.type.type.Identifier));
        for (int i = 0 ; i < it.dimension ; ++i) {
            baseType = new pointerType(baseType);
        }
        exprEntity = mallocArray(sizeList , baseType , 0);
    }

    @Override
    public void visit(newClassNode it) {
        IRType classType = irroot.getClass(it.type.Identifier);
        ArrayList<entity> bytes =  new ArrayList<>();
        bytes.add(new intConst(false , classType.getBytes()));
        IRType returnType = new pointerType(new integerType(8));
        register mallocReg = tmpFuncType.getNewRegisterInFunc(returnType);
        tmpFuncType.addInstInLastBlock(new callInst(mallocReg , new functionType(returnType , "malloc" , bytes)));
        register bitcastReg = tmpFuncType.getNewRegisterInFunc(new pointerType(classType));
        tmpFuncType.addInstInLastBlock(new bitcastInst(mallocReg , bitcastReg));
        ArrayList<entity> paraList = new ArrayList<>();
        paraList.add(bitcastReg);
        functionType constructFunc = new functionType(null , it.type.Identifier , paraList);
        tmpFuncType.addInstInLastBlock(new callInst(null , constructFunc));
        exprEntity = bitcastReg;
    }

    @Override public void visit(expressionListNode it) {
        ArrayList<entity> tmpParaList = callFuncParaList;
        it.exprList.forEach(cd -> {
            cd.accept(this);
            if (exprEntity.isLeftValue) {
                register tmp = tmpFuncType.getNewRegisterInFunc(((pointerType)exprEntity.getIRType()).pointerType);
                tmpFuncType.addInstInLastBlock(new loadInst(exprEntity , tmp));
                exprEntity = tmp;
            }
            tmpParaList.add(exprEntity);
        });
        callFuncParaList = tmpParaList;
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

    @Override
    public void visit(intConstNode it) {
        exprEntity = new intConst(false , it.value);
    }

    @Override
    public void visit(stringConstNode it) {
        stringConst str = new stringConst(false , it.value);
        globalVariable globalVar = new globalVariable(true , "_stringVariable" + Integer.toString(stringCount) , new pointerType(str.getIRType()) , str);
        irroot.addGlobalConst("_stringVariable" + Integer.toString(stringCount) , globalVar);
        irroot.addStringConst("_stringVariable" + Integer.toString(stringCount) , "\"" + it.value + "\"");
        stringCount++;
        register stringBitcastReg = tmpFuncType.getNewRegisterInFunc(new pointerType(new integerType(8)));
        tmpFuncType.addInstInLastBlock(new bitcastInst(globalVar , stringBitcastReg));
        exprEntity = stringBitcastReg;
    }

    @Override
    public void visit(booleanConstNode it) {
        if (it.value) exprEntity = new boolConst(false , 1);
        else exprEntity = new boolConst(false , 0);
    }

    @Override
    public void visit(nullConstNode it) {
        exprEntity = null;
    }


    @Override public void visit(lambdaExprNode it) {}
}
