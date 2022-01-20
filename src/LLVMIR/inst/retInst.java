package LLVMIR.inst;

import LLVMIR.basicBlock;
import LLVMIR.entity.*;

public class retInst extends terminalInst{
    public entity ret;

    public retInst(entity ret) {
        this.ret = ret;
    }

    @Override
    public String toString() {
        if (ret == null) return "ret void";
        else return "ret " + ret.getType() + " " + ret.getEntityName();
    }
}
