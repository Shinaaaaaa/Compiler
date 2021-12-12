package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class binaryInst extends inst {
    public enum binaryType{
        add , sub , mul , sdiv , srem , shl , ashr , and , or , xor ,
    }
    public register reg;
    public binaryType opType;
    public entity lhs , rhs;

    public binaryInst(basicBlock block_BelongTo , register reg , binaryType opType , entity lhs , entity rhs) {
        super(block_BelongTo);
        this.reg = reg;
        this.opType = opType;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public register getReg() {
        return reg;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = " + opType + " " + reg.getType() + " " + lhs.getEntityName() + " " + rhs.getEntityName();
    }
}
