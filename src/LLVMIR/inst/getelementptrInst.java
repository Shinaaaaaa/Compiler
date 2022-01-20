package LLVMIR.inst;

import LLVMIR.entity.*;
import LLVMIR.IRType.*;

public class getelementptrInst extends inst {
    public register reg;
    public entity pointer;
    public entity index_1 , index_2;
    public boolean arrayTag;

    public getelementptrInst(register reg , entity pointer , entity index_1) {
        this.reg = reg;
        this.pointer = pointer;
        this.index_1 = index_1;
        this.arrayTag =  true;
    }

    public getelementptrInst(register reg , entity pointer , entity index_1 , entity index_2) {
        this.reg = reg;
        this.pointer = pointer;
        this.index_1 = index_1;
        this.index_2 = index_2;
        this.arrayTag = false;
    }

    @Override
    public String toString() {
        if (arrayTag) return reg.getEntityName() + " = getelementptr " + ((pointerType)pointer.getIRType()).pointerType.toString() + ", " +
                pointer.getType() + " " + pointer.getEntityName() + ", " +
                index_1.getType() + " " + index_1.getEntityName();
        else return reg.getEntityName() + " = getelementptr " + ((pointerType)pointer.getIRType()).pointerType.toString() + ", " +
                pointer.getType() + " " + pointer.getEntityName() + ", " +
                index_1.getType() + " " + index_1.getEntityName() + ", " +
                index_2.getType() + " " + index_2.getEntityName();
    }
}
