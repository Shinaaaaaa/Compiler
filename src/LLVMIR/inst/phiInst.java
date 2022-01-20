package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class phiInst extends inst{
    public register reg;
    public entity phiEntity_1 , phiEntity_2;
    public basicBlock phiBlock_1 , phiBlock_2;

    public phiInst(register reg , entity phiEntity_1 , entity phiEntity_2 , basicBlock phiBlock_1 , basicBlock phiBlock_2) {
        this.reg = reg;
        this.phiEntity_1 = phiEntity_1;
        this.phiEntity_2 = phiEntity_2;
        this.phiBlock_1 = phiBlock_1;
        this.phiBlock_2 = phiBlock_2;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = phi i1 ["
                + phiEntity_1.getEntityName() + ", " + phiBlock_1.getLabel() + " ], ["
                + phiEntity_2.getEntityName() + ", " + phiBlock_2.getLabel() + "]";
    }
}
