package Backend;

import LLVMIR.*;
import LLVMIR.entity.*;
import LLVMIR.inst.*;
import LLVMIR.IRType.*;

import java.io.PrintStream;

public class IRPrinter {
    public PrintStream pr;
    public IRroot irroot;

    public IRPrinter(PrintStream pr , IRroot irroot) {
        this.pr = pr;
        this.irroot = irroot;
    };

    public void printIR() {
        irroot.globalVariablesList.values().forEach(vd -> {
            String printGlobalVar;
            printGlobalVar = vd.getEntityName() + " = dso_local global ";
            assert vd.getIRType() instanceof pointerType;
            if (((pointerType) vd.getIRType()).pointerType instanceof integerType) {
                printGlobalVar += ((pointerType) vd.getIRType()).pointerType.toString() + " 0";
                pr.println(printGlobalVar);
            } else {
                printGlobalVar += ((pointerType) vd.getIRType()).pointerType.toString() + " null";
                pr.println(printGlobalVar);
            }
        });
        irroot.globalConst.values().forEach(vd -> {
            String printGlobalVar;
            assert vd.getIRType() instanceof pointerType;
            printGlobalVar = vd.getEntityName() + " = constant ";
            printGlobalVar += ((pointerType) vd.getIRType()).pointerType.toString() + " ";
            printGlobalVar += "c\"" + ((stringConst) vd.value).value + "\\00\"";
            pr.println(printGlobalVar);

        });
        pr.println();

        irroot.classList.values().forEach(vd -> {
            StringBuilder printClass;
            printClass = new StringBuilder("%" + vd.className + " = type {");
            for (int i = 0 ; i < vd.typeLists.size() - 1 ; ++i) {
                printClass.append(vd.typeLists.get(i).b.toString());
                printClass.append(", ");
            }
            printClass.append(vd.typeLists.get(vd.typeLists.size() - 1).b.toString());
            printClass.append(" }");
            pr.println(printClass);
            pr.println();
        });

        irroot.funcList.get("__cxx_global_var_init").addInstInLastBlock(new retInst(null));
        irroot.funcList.values().forEach(fd -> {
            StringBuilder printFunc;
            printFunc = new StringBuilder("define dso_local ");
            if (fd.returnType != null) printFunc.append(fd.returnType.toString());
            else printFunc.append("void");
            printFunc.append(" @").append(fd.funcName).append("(");
            if (fd.parameterReg.size() > 0) {
                for (int i = 0 ; i < fd.parameterReg.size() - 1 ; ++i) {
                    printFunc.append(fd.parameterReg.get(i).a.toString()).append(" ").append(fd.parameterReg.get(i).b.getEntityName());
                    printFunc.append(", ");
                }
                printFunc.append(fd.parameterReg.get(fd.parameterReg.size() - 1).a.toString()).append(" ").append(fd.parameterReg.get(fd.parameterReg.size() - 1).b.getEntityName());
            }
            printFunc.append(") {");
            pr.println(printFunc);

            fd.basicBlockList.forEach(bd -> {
                if (bd != fd.basicBlockList.getFirst()) {
                    StringBuilder printBlockBegin;
                    printBlockBegin = new StringBuilder(bd.label + ":                                                ; preds = ");
                    for (int i = 0 ; i < bd.preSucceedBlockList.size() - 1 ; ++i) {
                        printBlockBegin.append(bd.preSucceedBlockList.get(i).getLabel()).append(", ");
                    }
                    printBlockBegin.append(bd.preSucceedBlockList.get(bd.preSucceedBlockList.size() - 1).getLabel());
                    pr.println(printBlockBegin);
                }
                bd.instList.forEach(id -> {
                    pr.print("\t");
                    pr.println(id.toString());
                });
                if (bd.terminal != null) {
                    pr.print("\t");
                    pr.println(bd.terminal.toString());
                }
            });
            pr.println("}");
            pr.println();
        });

        irroot.built_in_func.values().forEach(vd -> {
            StringBuilder printFunc;
            printFunc = new StringBuilder("declare dso_local ");
            if (vd.returnType != null) printFunc.append(vd.returnType.toString());
            else printFunc.append("void");
            printFunc.append(" @").append(vd.funcName).append("(");
            if (vd.parameter != null) {
                for (int i = 0 ; i < vd.parameter.size() - 1 ; ++i) {
                    printFunc.append(vd.parameter.get(i).b.toString());
                    printFunc.append(", ");
                }
                printFunc.append(vd.parameter.get(vd.parameter.size() - 1).b.toString());
            }
            printFunc.append(")");
            pr.println(printFunc);
        });
    }
}
