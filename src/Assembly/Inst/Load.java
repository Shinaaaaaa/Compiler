package Assembly.Inst;

import Assembly.Operand.*;

public class Load extends Inst{
    public enum loadType {
        lb , lh , lw
    }

    public Imm imm;
    public Reg rd , rs1;
    public loadType type;

    public Load(Imm imm , Reg rd , Reg rs1 , loadType type){
        this.imm = imm;
        this.rd = rd;
        this.rs1 = rs1;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "\t" + rd.toString() + ", " + imm.toString() + "(" + rs1.toString() + ")";
    }
}
