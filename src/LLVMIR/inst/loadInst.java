package LLVMIR.inst;

import LLVMIR.entity.*;

public class loadInst extends inst{
    public entity from;
    public register destPos;

    public loadInst(entity from , register destPos) {
        this.from = from;
        this.destPos = destPos;
    }

    @Override
    public String toString() {
        return destPos.getEntityName() + " = load " + destPos.getType() + ", "
                + from.getType() + " " + from.getEntityName();
    }
}
