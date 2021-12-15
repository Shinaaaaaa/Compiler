package LLVMIR.IRType;

import java.util.ArrayList;

public class classType extends IRType {
    public String className;
    public ArrayList<IRType> typeLists = new ArrayList<>();

    public classType(String className) {
        this.className = className;
    }

    public void addIRTypeInClass(IRType IRtype){
        typeLists.add(IRtype);
    }

    @Override
    public String toString() {
        return "%struct." + className;
    }
}
