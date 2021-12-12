package LLVMIR.entity;

import LLVMIR.IRType.*;

public class register extends entity{
    public int regNumber;
    public IRType regType;

    public register(int reg_Number , IRType regType) {
        this.regNumber = reg_Number;
        this.regType = regType;
    }

    public String getType(){
        return regType.toString();
    }

    @Override
    public String getEntityName() {
        return "%" + regNumber;
    }
}
