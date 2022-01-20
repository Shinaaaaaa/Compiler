package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class brInst extends terminalInst{
    public entity condition;
    public basicBlock trueBlock , falseBlock;

    public brInst(entity condition) {
        this.condition = condition;
    }

    public brInst(basicBlock trueBlock) {
        this.trueBlock = trueBlock;
    }

    public void setTrueBlock(basicBlock trueBlock) {
        this.trueBlock = trueBlock;
    }

    public void setFalseBlock(basicBlock falseBlock) {
        this.falseBlock = falseBlock;
    }

    @Override
    public String toString() {
        if (condition == null) return "br label " + trueBlock.getLabel();
        else return "br " + condition.getType() + " " + condition.getEntityName() +
                ", label " + trueBlock.getLabel() + ", label " + falseBlock.getLabel();
    }
}
