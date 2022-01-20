package LLVMIR.inst;

import LLVMIR.entity.*;
import LLVMIR.IRType.*;

public class bitcastInst extends inst {
    public entity to;
    public entity from;

    public bitcastInst(entity from , entity to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return to.getEntityName() + " = bitcast " + from.getType() + " " + from.getEntityName() + " to " + to.getType();
    }
}
