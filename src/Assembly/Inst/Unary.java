package Assembly.Inst;

import Assembly.Operand.*;

public class Unary extends Inst{
    public enum unaryType {
        seqz, snez, sgtz, sltz
    }

    public Reg rd , rs1;
    public unaryType type;

    public Unary(Reg rd , Reg rs1 , unaryType type){
        this.rd = rd;
        this.rs1 = rs1;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "\t" + rd.toString() + ", " + rs1.toString();
    }
}
