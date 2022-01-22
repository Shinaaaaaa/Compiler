package Assembly;

import Assembly.Operand.*;
import LLVMIR.entity.*;
import java.util.ArrayList;
import java.util.HashSet;

public class AsmFunc {
    public String funcName;
    public int labelCnt;
    public AsmBlock firstBlock;
    public ArrayList<Reg> paraList = new ArrayList<>();
    public HashSet<Integer> stack = new HashSet<>();

    public AsmFunc(String funcName){
        this.funcName = funcName;
        labelCnt = 0;
        firstBlock = new AsmBlock(funcName , useLabel());
    }

    public int useLabel() {
        return labelCnt++;
    }

    public void addVarInStack(register reg) {
        stack.add(reg.regNumber);
    }

    public boolean containsVarInStack(register reg) {
        return stack.contains(reg.regNumber);
    }
}
