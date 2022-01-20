package LLVMIR.inst;

import LLVMIR.entity.*;

public class storeInst extends inst{
    public entity value;
    public entity storePos;

    public storeInst(entity value , entity storePos) {
        this.value = value;
        this.storePos = storePos;
    }

    @Override
    public String toString() {
        return "store " + value.getType() + " " + value.getEntityName() + ", "
                + storePos.getType() + " " + storePos.getEntityName();
    }
}
