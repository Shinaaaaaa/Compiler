package Assembly;

import Assembly.Inst.Inst;

public class AsmBlock {
    public String funcName;
    public int label;
    public AsmBlock prev , next;
    public Inst headInst , tailInst;

    public AsmBlock(String funcName , int label){
        this.funcName = funcName;
        this.label = label;
    }

    public void addInst(Inst inst) {
        if (headInst == null) {
            headInst = tailInst = inst;
        } else {
            tailInst.next = inst;
            inst.prev = tailInst;
            tailInst = tailInst.next;
        }
    }

    public void insertInstPre(Inst insert , Inst i) {
        if (i.prev != null) i.prev.next = insert;
        insert.prev = i.prev;
        insert.next = i;
        i.prev = insert;
    }

    public String getLabel() {
        return "." + funcName + "_" + Integer.toString(label);
    }
}
