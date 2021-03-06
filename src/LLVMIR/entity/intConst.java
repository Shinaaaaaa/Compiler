package LLVMIR.entity;

import LLVMIR.IRType.IRType;
import LLVMIR.IRType.integerType;

public class intConst extends entity{
    public long value;

    public intConst(boolean isLeftValue , long value) {
        super(isLeftValue);
        this.value = value;
    }

    @Override
    public IRType getIRType() {
        return new integerType(32);
    }

    @Override
    public String getType() {
        return "i32";
    }

    @Override
    public String getEntityName() {
        return Long.toString(value);
    }
}
