package LLVMIR.inst;

import LLVMIR.basicBlock;

public abstract class terminalInst extends inst{
    public terminalInst(basicBlock block_BelongTo) {
        super(block_BelongTo);
    }
}
