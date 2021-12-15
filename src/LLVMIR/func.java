package LLVMIR;

import LLVMIR.IRType.*;
import LLVMIR.inst.*;
import LLVMIR.entity.*;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.LinkedList;

public class func {
    public String funcName;
    public IRType returnType;
    public ArrayList<IRType> parameterLists = new ArrayList<>();
    public LinkedList<basicBlock> basicBlockList = new LinkedList<>();

    public func(String funcName) {
        this.funcName = funcName;
    }

    public func(String funcName , IRType returnType , ArrayList<IRType> parameterLists) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.parameterLists = parameterLists;
    }

}
