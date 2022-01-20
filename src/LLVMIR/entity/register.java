package LLVMIR.entity;

import LLVMIR.IRType.*;

public class register extends entity{
    public int regNumber;
    public IRType regType;

    public register(boolean isLeftValue , int reg_Number , IRType regType) {
        super(isLeftValue);
        this.regNumber = reg_Number;
        this.regType = regType;
    }

    @Override
    public IRType getIRType() {
        return regType;
    }

    @Override
    public String getType(){
        return regType.toString();
    }

    @Override
    public String getEntityName() {
        return "%" + regNumber;
    }
}
