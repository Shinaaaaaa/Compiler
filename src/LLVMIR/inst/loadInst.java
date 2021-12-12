package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class loadInst extends inst{
    public register reg;
    public register allocaPos;

    public loadInst(basicBlock block_BelongTo , register reg , register allocaPos) {
        super(block_BelongTo);
        this.reg = reg;
        this.allocaPos = allocaPos;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = load " + reg.getType() + ", "
                + allocaPos.getType() + " " + allocaPos.getEntityName() + ", align " + "need to add ???";
    }
}
