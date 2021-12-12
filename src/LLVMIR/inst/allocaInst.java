package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class allocaInst extends inst {
    public register reg;

    public allocaInst(basicBlock block_BelongTo , register reg) {
        super(block_BelongTo);
        this.reg = reg;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = alloca " + reg.getType() + ", align " + "need to add ???";
    }
}
