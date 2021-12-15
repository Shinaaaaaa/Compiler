package LLVMIR.IRType;

import LLVMIR.inst.*;
import LLVMIR.entity.*;
import java.util.ArrayList;

public class functionType {
    public ArrayList<entity> parameterLists = new ArrayList<>();
    public IRType returnType;
    public String funcName;

    public functionType(IRType returnType , String funcName) {
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
