package Assembly.Inst;

import Assembly.Operand.*;

public class Move extends Inst{
    public Reg rd , rs1;

    public Move(Reg rd , Reg rs1){
        this.rd = rd;
        this.rs1 = rs1;
    }

    @Override
    public String toString() {
        return "mv\t" + rd.toString() + ", " + rs1.toString();
    }
}
