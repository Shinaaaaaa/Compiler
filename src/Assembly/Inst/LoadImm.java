package Assembly.Inst;

import Assembly.Operand.*;

public class LoadImm extends Inst{
    public Imm imm;
    public Reg rd;

    public LoadImm(Imm imm , Reg rd){
        this.imm = imm;
        this.rd = rd;
    }

    @Override
    public String toString() {
        return "li\t" + rd.toString() + ", " + imm.toString();
    }
}
