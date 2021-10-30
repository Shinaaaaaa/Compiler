package Util;

import Util.error.semanticError;

import java.util.ArrayList;
import java.util.Objects;

public class Type {
    public enum var_type {
        Int , Bool , Str , Identifier , Func , Class , Void
    }
    public var_type varTypeTag;
    public String Identifier;
    public int dimension;
    public Type funcReturnType;
    public ArrayList<Type> parameterList;

    public Type(var_type varType , int dimension) {
        this.varTypeTag = varType;
        this.dimension = dimension;
    }

    public Type(String Identifier , int dimension){
        this.varTypeTag = var_type.Class;
        this.Identifier = Identifier;
        this.dimension = dimension;
    }

    public Type(String funcName , Type funcReturnType , ArrayList<Type> parameterList){
        this.varTypeTag = var_type.Func;
        this.Identifier = funcName;
        this.funcReturnType = funcReturnType;
        this.parameterList = parameterList;
        this.dimension = 0;
    }

    public boolean typeMatchCheck(Type t , position pos) {
        if (this.varTypeTag != t.varTypeTag) {
            return false;
        }
        if (this.varTypeTag == var_type.Class) {
            if (!Objects.equals(this.Identifier, t.Identifier)) {
                return false;
            }
        }
        if (this.dimension != t.dimension) {
            return false;
        }
        return true;
    }
}