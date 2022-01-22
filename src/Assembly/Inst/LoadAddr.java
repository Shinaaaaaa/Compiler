package Assembly.Inst;

import Assembly.Operand.*;

public class LoadAddr extends Inst{
    public String address;
    public Reg rd;

    public LoadAddr(String address , Reg rd){
        this.address = address;
        this.rd = rd;
    }

    @Override
    public String toString() {
        return "la\t" + rd.toString() + ", " + address;
    }
}
