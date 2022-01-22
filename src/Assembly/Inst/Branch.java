package Assembly.Inst;

import Assembly.Operand.*;

public class Branch extends Inst{
    public enum branchType {
        beqz , bnez , blt  , bltu , bge , bgeu
    }

    public Reg rs1, rs2;
    public branchType type;
    public String dest;

    public Branch(Reg rs1 , Reg rs2 , branchType type , String dest){
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.type = type;
        this.dest = dest;
    }

    @Override
    public String toString() {
        String str;
        if (type == branchType.beqz) str = "beqz ";
        else if (type == branchType.bnez) str = "bnez ";
        else if (type == branchType.blt) str = "blt ";
        else if (type == branchType.bltu) str = "bltu ";
        else if (type == branchType.bge) str = "bge ";
        else str = "bgeu ";
        str +=  rs1.toString();
        if (rs2 != null) {
            str += ", " + rs2.toString();
        }
        str += ", " + dest;
        return str;
    }
}
