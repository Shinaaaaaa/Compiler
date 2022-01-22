package Assembly.Inst;

import Assembly.Operand.*;

public class BinaryImm extends Inst{
    public enum binaryImmType {
        addi , slti , sltiu , andi , ori , xori , slli
    }

    public Reg rd , rs1;
    public Imm imm;
    public binaryImmType type;

    public BinaryImm(Reg rd , Reg rs1 , Imm imm , binaryImmType type){
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "\t" + rd.toString() + ", " + rs1.toString() + ", " + imm.toString();
    }
}
