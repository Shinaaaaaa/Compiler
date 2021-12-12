package LLVMIR.inst;
import LLVMIR.*;

public abstract class inst {
    public basicBlock block_BelongTo;

    public inst(basicBlock block_BelongTo) {
        this.block_BelongTo = block_BelongTo;
    }

    abstract public String toString();
}
