package LLVMIR.inst;

import LLVMIR.*;
import LLVMIR.entity.*;

public class icmpInst extends inst {
    public enum icmpType{
        eq , ne , sgt , sge , slt , sle
    }

    public register reg;
    public icmpType opType;
    public entity lhs , rhs;

    public icmpInst(register reg , icmpType opType , entity lhs , entity rhs) {
        this.reg = reg;
        this.opType = opType;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return reg.getEntityName() + " = icmp " + opType + " " +
                lhs.getType() + " " + lhs.getEntityName() + ", " + rhs.getEntityName();
    }
}
