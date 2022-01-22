package Assembly.Inst;

import Assembly.Operand.*;

public class BinaryReg extends Inst{
    public enum binaryRegType {
        add , sub , mul , div , rem ,
        sll , sra , slt , sltu ,
        or , and , xor
    }

    public Reg rd , rs1 , rs2;
    public binaryRegType type;

    public BinaryReg(Reg rd , Reg rs1 , Reg rs2 , binaryRegType type){
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "\t" + rd.toString() + ", " + rs1.toString() + ", " + rs2.toString();
    }
}
