package LLVMIR.IRType;

import LLVMIR.inst.*;
import LLVMIR.entity.*;
import java.util.ArrayList;

public class functionType {
    public IRType returnType;
    public String funcName;
    public ArrayList<entity> parameterLists = new ArrayList<>();

    public functionType(IRType returnType , String funcName , ArrayList<entity> parameterLists) {
        this.returnType = returnType;
        this.funcName = funcName;
        this.parameterLists = parameterLists;
    }

    public String getReturnType() {
        if (returnType != null) return returnType.toString();
        else return null;
    }

    public void addParameterInFunc(entity parameter){
        parameterLists.add(parameter);
    }

    @Override
    public String toString() {
        StringBuilder parameterStr = new StringBuilder();
        if (parameterLists != null && parameterLists.size() > 0) {
            for (int i = 0 ; i < parameterLists.size() - 1 ; i++) {
                parameterStr.append(parameterLists.get(i).getType());
                parameterStr.append(" ");
                parameterStr.append(parameterLists.get(i).getEntityName());
                parameterStr.append(", ");
            }
            if (parameterLists.get(parameterLists.size() - 1) != null) {
                parameterStr.append(parameterLists.get(parameterLists.size() - 1).getType());
                parameterStr.append(" ");
                parameterStr.append(parameterLists.get(parameterLists.size() - 1).getEntityName());
            }
        }
        String retType;
        if (getReturnType() == null) retType = "void";
        else retType = getReturnType();
        return retType + " @" + funcName + "(" + parameterStr + ")";
    }
}
