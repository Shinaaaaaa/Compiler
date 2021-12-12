package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class storeInst extends inst{
    public entity value;
    public register allocaPos;

    public storeInst(basicBlock block_BelongTo , entity value , register allocaPos) {
        super(block_BelongTo);
        this.value = value;
        this.allocaPos = allocaPos;
    }

    @Override
    public String toString() {
        return "store " + value.getType() + " " + value.getEntityName() + ", "
                + allocaPos.getType() + " " + allocaPos.getEntityName() + ", align " + "need to add ???";
    }
}
