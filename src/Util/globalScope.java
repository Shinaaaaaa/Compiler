package Util;

import Util.error.semanticError;

import java.util.HashMap;

public class globalScope extends Scope {
    public String name;
    public boolean classTag = false;
    public HashMap<String , globalScope> classList = new HashMap<>();
    public HashMap<String , Scope> funcList = new HashMap<>();
    public HashMap<String , Type> funcTypeList = new HashMap<>();

    public globalScope(Scope parentScope , String name) {
        super(parentScope);
        this.name = name;
    }

    public void addClass(String className , globalScope classScope , position pos) {
        if (classList.containsKey(className)) {
            throw new semanticError("Semantic Error: class variable redefine with class" , pos);
        } else if (funcList.containsKey(className)) {
            throw new semanticError("Semantic Error: class variable redefine with func" , pos);
        } else if (super.containsVar(className , false)) {
            throw new semanticError("Semantic Error: class variable redefine with variable" , pos);
        } else classList.put(className , classScope);
    }

    public void addFunc(String funcName , Scope funcScope , Type funcType , position pos) {
        if (funcList.containsKey(funcName)) {
            throw new semanticError("Semantic Error: func variable redefine with func" , pos);
        } else if (classList.containsKey(funcName)) {
            throw new semanticError("Semantic Error: func variable redefine with class" , pos);
        } else {
            funcList.put(funcName , funcScope);
            funcTypeList.put(funcName , funcType);
        }
    }

    public globalScope getClassScope(String className , position pos) {
        if (classList.containsKey(className)) return classList.get(className);
        else throw new semanticError("Semantic Error: class name no find" , pos);
    }

    public Scope getFuncScope(String funcName , position pos) {
        if (funcList.containsKey(funcName)) return funcList.get(funcName);
        else throw new semanticError("Semantic Error: function name no find" , pos);
    }

    public Type getFuncType(String funcName , position pos) {
        if (funcTypeList.containsKey(funcName)) return funcTypeList.get(funcName);
        else throw new semanticError("Semantic Error: function name no find" , pos);
    }

    public boolean containClassName(String className) {
        return classList.containsKey(className);
    }

    public boolean containFuncName(String funcName) {
        return funcList.containsKey(funcName);
    }
}