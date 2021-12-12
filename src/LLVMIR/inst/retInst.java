package LLVMIR.inst;

import LLVMIR.basicBlock;
import LLVMIR.entity.*;

public class retInst extends terminalInst{
    public entity ret;

    public retInst(basicBlock block_BelongTo , entity ret) {
        super(block_BelongTo);
        this.ret = ret;
    }

    @Override
    public String toString() {
        return "ret " + ret.getType() + " " + ret.getEntityName();
    }
}
