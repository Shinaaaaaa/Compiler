package LLVMIR.entity;

import LLVMIR.IRType.*;

public class globalVariable extends entity{
    public String globalVariableName;
    public IRType globalVariableType;
    public entity value;
    
    public globalVariable(boolean isLeftValue , String globalVariableName , IRType regType , entity value) {
        super(isLeftValue);
        this.globalVariableName = globalVariableName;
        this.globalVariableType = regType;
        this.value = value;
    }

    @Override
    public IRType getIRType() {
        return globalVariableType;
    }

    @Override
    public String getType(){
        return globalVariableType.toString();
    }

    @Override
    public String getEntityName() {
        return "@" + globalVariableName;
    }
}
