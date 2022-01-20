package LLVMIR.entity;

import LLVMIR.IRType.IRType;
import LLVMIR.IRType.integerType;

public class nullConst extends entity{
    IRType nullType;

    public nullConst(IRType nullType) {
        super(false);
        this.nullType = nullType;
    }

    @Override
    public IRType getIRType() {
        return nullType;
    }

    @Override
    public String getType() {
        return nullType.toString();
    }

    @Override
    public String getEntityName() {
        return "null";
    }
}
