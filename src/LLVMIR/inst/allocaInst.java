package LLVMIR.inst;

import LLVMIR.IRType.*;
import LLVMIR.entity.*;

public class allocaInst extends inst {
    public register reg;

    public allocaInst(register reg) {
        this.reg = reg;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = alloca " + ((pointerType)reg.regType).pointerType.toString();
    }
}
