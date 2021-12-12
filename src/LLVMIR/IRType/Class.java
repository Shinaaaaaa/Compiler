package LLVMIR.IRType;

import java.util.ArrayList;

public class Class extends IRType {
    public ArrayList<IRType> typeLists = new ArrayList<>();
    public String className;

    public Class(String className) {
        this.className = className;
    }

    public void addIRTypeInCass(IRType IRtype){
        typeLists.add(IRtype);
    }

    @Override
    public String toString() {
        return "%struct." + className;
    }
}
