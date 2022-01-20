package LLVMIR.entity;

import LLVMIR.IRType.IRType;
import LLVMIR.IRType.integerType;

public class boolConst extends entity{
    public int value;

    public boolConst(boolean isLeftValue , int value) {
        super(isLeftValue);
        this.value = value;
    }

    @Override
    public IRType getIRType() {
        return new integerType(1);
    }

    @Override
    public String getType() {
        return "i1";
    }

    @Override
    public String getEntityName() {
        if (value == 0) return "false";
        else return "true";
    }
}
