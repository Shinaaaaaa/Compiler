package LLVMIR;

import LLVMIR.Inst.*;
import java.util.ArrayList;

public class basicBlock {
    public ArrayList<inst> instList = new ArrayList<>();
    public String label;
    public terminalInst terminal;
}
