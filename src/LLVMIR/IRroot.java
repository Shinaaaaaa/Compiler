package LLVMIR;

import LLVMIR.IRType.*;
import LLVMIR.entity.*;
import LLVMIR.inst.*;

import java.util.HashMap;

public class IRroot {
    public HashMap<String , globalVariable> globalVariablesList = new HashMap<>();
    public HashMap<String , globalVariable> globalConst = new HashMap<>();
    public HashMap<String , String> stringConstForAsm = new HashMap<>();
    public HashMap<String , classType> classList = new HashMap<>();
    public HashMap<String , func> funcList = new HashMap<>();
    public HashMap<String , func> built_in_func = new HashMap<>();

    public IRroot() {}

    public void addGlobalVariable(String globalVariableName , globalVariable globalVar) {
        globalVariablesList.put(globalVariableName , globalVar);
    }

    public void addGlobalConst(String globalConstName , globalVariable Const) {
        globalConst.put(globalConstName , Const);
    }

    public void addStringConst(String globalConstName , String Const) {
        stringConstForAsm.put(globalConstName , Const);
    }

    public void addVariableTypeInClass(String className , String varName , IRType variableType) {
        classList.get(className).addIRTypeInClass(varName , variableType);
    }

    public void addClass(String className) {
        classList.put(className , new classType(className));
    }

    public classType getClass(String className) {
        return classList.get(className);
    }

    public void addFunc(func function) {
        funcList.put(function.funcName , function);
    }

    public func getFunc(String funcName) {
        if (funcList.containsKey(funcName)) return funcList.get(funcName);
        else return built_in_func.get(funcName);
    }

    public boolean containsFunc(String funcName) {
        return funcList.containsKey(funcName) || built_in_func.containsKey(funcName);
    }
}
