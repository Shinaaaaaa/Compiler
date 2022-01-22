package LLVMIR;

import LLVMIR.IRType.*;
import LLVMIR.inst.*;
import LLVMIR.entity.*;
import org.antlr.v4.runtime.misc.Pair;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.LinkedList;

public class func {
    public String funcName;
    public IRType returnType;
    public ArrayList<Pair<String , IRType>> parameter;
    public ArrayList<Pair<IRType , register>> parameterReg = new ArrayList<>();

    public LinkedList<basicBlock> basicBlockList = new LinkedList<>();
    private int entityNum;

    public func(String funcName) {
        this.funcName = funcName;
        this.entityNum = 0;
    }

    public func(String funcName , IRType returnType , ArrayList<Pair<String , IRType>> parameter) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.parameter = parameter;
        this.entityNum = 0;
    }

    public void Init(entityScope eScope) {
        if (parameter != null) entityNum = parameter.size();
        addLastBlock();
        if (parameter != null) {
            for (int i = 0 ; i < parameter.size() ; ++i) {
                parameterReg.add(new Pair<>(parameter.get(i).b , new register(false , i , parameter.get(i).b)));
            }
            parameter.forEach(pd -> {
                register reg = getNewRegisterInFunc(new pointerType(pd.b));
                reg.isLeftValue = true;
                eScope.addEntity(pd.a , reg);
                addInstInLastBlock(new allocaInst(reg));
            });
        }
        if (returnType != null) {
            register reg_return = getNewRegisterInFunc(new pointerType(returnType));
            eScope.addEntity("_reg_return" , reg_return);
            addInstInLastBlock(new allocaInst(reg_return));
        }
        if (parameter != null) {
            for (int i = 0 ; i < parameter.size() ; i++) {
                register storeReg = (register) eScope.getEntity(parameter.get(i).a);
                addInstInLastBlock(new storeInst(new register(true , i , parameter.get(i).b) , storeReg));
            }
        }
    }

    public int getEntityNum() {
        return entityNum;
    }

    public register getNewRegisterInFunc(IRType regType) {
        register reg = new register(false , entityNum , regType);
        entityNum++;
        return reg;
    }

    public void addLastBlock() {
        basicBlockList.add(new basicBlock(Integer.toString(entityNum)));
        entityNum++;
    }

    public basicBlock getLastBlock() {
        return basicBlockList.getLast();
    }

    public void addInstInLastBlock(inst i) {
        basicBlockList.getLast().addInst(i);
    }

    public void addTerminalInstInLastBlock(terminalInst i) {
        basicBlockList.getLast().addInst(i);
    }
}
