package LLVMIR.IRType;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;

public class classType extends IRType {
    public String className;
    public ArrayList<Pair<String , IRType>> typeLists = new ArrayList<>();

    public classType(String className) {
        this.className = className;
    }

    public void addIRTypeInClass(String varName , IRType IRtype){
        typeLists.add(new Pair<>(varName , IRtype));
    }

    @Override
    public String toString() {
        return "%" + className;
    }

    @Override
    public int getBytes() {
        return typeLists.stream().mapToInt(td -> td.b.getBytes()).sum();
    }
}
