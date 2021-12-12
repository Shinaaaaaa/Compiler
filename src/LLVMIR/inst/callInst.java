package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;
import LLVMIR.IRType.*;

public class callInst extends inst {
    public register reg;
    public Function func;

    public callInst(basicBlock block_BelongTo , register reg , Function func) {
        super(block_BelongTo);
        this.reg = reg;
        this.func = func;
    }

    @Override
    public String toString() {
        if (func.getReturnType() != null) return reg.getEntityName() + " = call " + func.toString();
        else return "call " + func.toString();
    }
}
