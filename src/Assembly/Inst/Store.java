package Assembly.Inst;

import Assembly.Operand.*;

public class Store extends Inst{
    public enum storeType {
        sb , sh , sw
    }

    public Imm imm;
    public Reg rs1, rs2;
    public storeType type;

    public Store(Imm imm , Reg rs1 , Reg rs2 , storeType type){
        this.imm = imm;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "\t" + rs2.toString() + ", " + imm.toString() + "(" + rs1.toString() + ")";
    }
}
