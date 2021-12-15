package LLVMIR;

import LLVMIR.IRType.*;
import LLVMIR.entity.*;

import java.util.ArrayList;
import java.util.Objects;

public class IRroot {
    public ArrayList<globalVariable> globalVariablesList = new ArrayList<>();
    public ArrayList<classType> classList = new ArrayList<>();
    public ArrayList<func> funcList = new ArrayList<>();

    public IRroot() {}

    public void addGlobalVariable(String globalVariableName , IRType globalVariableType) {
        globalVariablesList.add(new globalVariable(globalVariableName , globalVariableType));
    }

    public void addVariableTypeInClass(String className , IRType variableType) {
        classList.forEach(cd -> {
            if (Objects.equals(cd.className, className)) {
                cd.addIRTypeInClass(variableType);
            }
        });
    }

    public void addClass(String className) {
        classList.add(new classType(className));
    }

    public void addFunc(String funcName) {
        funcList.add(new func(funcName));
    }

    public void addFunc(func function) {
        funcList.add(function);
    }
}
