package LLVMIR.IRType;

import LLVMIR.inst.*;
import LLVMIR.entity.*;
import java.util.ArrayList;

public class Function extends IRType {
    public ArrayList<entity> parameterLists = new ArrayList<>();
    public IRType returnType;
    public String funcName;
    public ArrayList<inst> instLists = new ArrayList<>();

    public Function(IRType returnType , String funcName) {
        this.returnType = returnType;
        this.funcName = funcName;
    }

    public String getReturnType() {
        if (returnType != null) return returnType.toString();
        else return null;
    }

    public void addParameterInFunc(entity parameter){
        parameterLists.add(parameter);
    }

    public void addInstInFunc(inst funcInst){
        instLists.add(funcInst);
    }

    @Override
    public String toString() {
        StringBuilder parameterStr = new StringBuilder();
        for (int i = 0 ; i < parameterLists.size() - 1 ; i++) {
            parameterStr.append(parameterLists.get(i).getType());
            parameterStr.append(" ");
            parameterStr.append(parameterLists.get(i).getEntityName());
            parameterStr.append(",");
        }
        if (parameterLists.get(parameterLists.size() - 1) != null) {
            parameterStr.append(parameterLists.get(parameterLists.size() - 1).getType());
            parameterStr.append(" ");
            parameterStr.append(parameterLists.get(parameterLists.size() - 1).getEntityName());
        }
        return getReturnType() + " @" + funcName + "(" + parameterStr + ")";
    }
}
