package LLVMIR.entity;

import LLVMIR.IRType.*;

public class globalVariable extends entity{
    public String globalVariableName;
    public IRType globalVariableType;
    
    public globalVariable(String globalVariableName , IRType regType) {
        this.globalVariableName = globalVariableName;
        this.globalVariableType = regType;
    }

    public String getType(){
        return globalVariableType.toString();
    }

    @Override
    public String getEntityName() {
        return "@" + globalVariableName;
    }
}
