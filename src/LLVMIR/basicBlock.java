package LLVMIR;

import LLVMIR.inst.*;
import java.util.ArrayList;

public class basicBlock {
    public String label;
    public terminalInst terminal;
    public ArrayList<inst> instList = new ArrayList<>();

    public basicBlock(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void addTerminalInst(terminalInst terminal) {
        this.terminal = terminal;
    }

    public void addInst(inst i) {
        instList.add(i);
    }
}
