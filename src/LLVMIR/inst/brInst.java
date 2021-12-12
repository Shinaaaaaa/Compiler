package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class brInst extends terminalInst{
    public entity condition;
    public basicBlock trueBlock , falseBlock;

    public brInst(basicBlock block_BelongTo , entity condition , basicBlock trueBlock , basicBlock falseBlock) {
        super(block_BelongTo);
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    @Override
    public String toString() {
        if (condition == null) return "br label " + trueBlock.toString();
        else return "br " + condition.getType() + " " + condition.getEntityName() +
                ", label " + trueBlock.toString() + ", label " + falseBlock.toString();
    }
}
