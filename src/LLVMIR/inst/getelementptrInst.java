package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class getelementptrInst extends inst {
    public register reg;
    public entity dest;
    public entity index_1 , index_2;

    public getelementptrInst(basicBlock block_BelongTo , register reg , entity dest) {
        super(block_BelongTo);
        this.reg = reg;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = getelementptr " + dest.getType() + ", " +
                dest.getType() + " " + dest.getEntityName() + ", " +
                index_1.getType() + " " + index_1.getEntityName() + ", " +
                index_2.getType() + " " + index_2.getEntityName();
    }
}
