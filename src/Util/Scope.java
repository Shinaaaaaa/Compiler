package Util;

import Util.error.semanticError;

import java.util.HashMap;

public class Scope {
    private HashMap<String , Type> members;
    private Scope parentScope;
    public boolean inLoop = false;
    public boolean lambdaExist = false , lambdaReturn = false;

//    public Scope(HashMap<String , Type> members , Scope parentScope , boolean inLoop) {
//        this.members = new HashMap<>();
//        members.keySet().forEach(kd -> this.members.put(kd , members.get(kd)));
//        this.parentScope = parentScope;
//        this.inLoop = inLoop;
//    }

    public Scope(Scope parentScope) {
        members = new HashMap<>();
        this.parentScope = parentScope;
    }

    public Scope parentScope() {
        return parentScope;
    }

    public void addVar(String name , Type t , position pos) {
        if (members.containsKey(name)) {
            throw new semanticError("Semantic Error: variable redefine" , pos);
        }
        else members.put(name , t);
    }

    public boolean containsVar(String name , boolean lookUpon) {
        if (members.containsKey(name)) return true;
        else if (parentScope != null && lookUpon) return parentScope.containsVar(name, true);
        else return false;
    }

    public Type getType(String name , boolean lookUpon) {
        if (members.containsKey(name)) return members.get(name);
        else if (parentScope != null && lookUpon) return parentScope.getType(name , true);
        else return null;
    }

    public Boolean ifInLoop(){
        if (inLoop) return true;
        if (parentScope != null) return parentScope.ifInLoop();
        else return false;
    }
}
