package Backend;

import Assembly.AsmBlock;
import Assembly.AsmFunc;
import Assembly.AsmModule;
import Assembly.Inst.*;
import Assembly.Operand.*;
import LLVMIR.*;
import LLVMIR.IRType.IRType;
import LLVMIR.entity.*;
import LLVMIR.inst.*;
import LLVMIR.IRType.*;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AsmBuilder {
    public static ArrayList<String> phyRegName = new ArrayList<>(Arrays.asList(
        "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1",
        "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
        "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"));

    static public String[] calleeSavePRegNames = {
        "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11"
    };

    static public String[] parameterPRegNames = {
        "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7"
    };

    public AsmModule asmRoot = new AsmModule();
    public IRroot irRoot;

    private LinkedHashMap<String , PhyReg> phyRegs = new LinkedHashMap<>();
    private AsmFunc tmpAsmFunction;
    private AsmBlock tmpAsmBlock;
    private basicBlock tmpNextIRBlock;
    private long tmpRegCnt;
    private long sp_offest;
    private HashMap<String , Integer> labelMap;
    private HashMap<Integer , AsmBlock> blockMap;
    private HashMap<Integer , Long> tmpRegAddress;

    public AsmBuilder(IRroot irRoot , AsmModule asmRoot) {
        this.irRoot = irRoot;
        this.asmRoot = asmRoot;
        phyRegName.forEach(pd -> {
            phyRegs.put(pd , new PhyReg(pd));
        });
    }

    public void buildAsm() {
        asmRoot.bss.addAll(irRoot.globalVariablesList.keySet());
        irRoot.stringConstForAsm.keySet().forEach(kd -> {
            asmRoot.rodata.add(new Pair<>(kd , irRoot.stringConstForAsm.get(kd)));
        });
        irRoot.funcList.values().forEach(this::buildFunc);
    }

    void buildFunc(func f) {
        tmpAsmBlock = null;
        tmpNextIRBlock = null;
        tmpAsmFunction = new AsmFunc(f.funcName);
        labelMap = new HashMap<>();
        blockMap = new HashMap<>();
        tmpRegAddress = new HashMap<>();
        for (int i = 0 ; i < f.basicBlockList.size() ; ++i) {
            if (i == 0) {
                tmpAsmBlock = tmpAsmFunction.firstBlock;
                frameInit(f);
            }
            else {
                AsmBlock newAsmBlock = new AsmBlock(f.funcName , tmpAsmFunction.useLabel());
                tmpAsmBlock.next = newAsmBlock;
                newAsmBlock.prev = tmpAsmBlock;
                tmpAsmBlock = newAsmBlock;
            }
            if (i != f.basicBlockList.size() - 1) tmpNextIRBlock = f.basicBlockList.get(i + 1);
            else tmpNextIRBlock = null;
            buildBasicBlock(f.basicBlockList.get(i));
            blockMap.put(tmpAsmBlock.label , tmpAsmBlock);
        }
        asmRoot.text.add(new Pair<>(f.funcName , tmpAsmFunction));
    }

    void buildBasicBlock(basicBlock block) {
        block.instList.forEach(this::buildInst);
        if (block.terminal != null) buildInst(block.terminal);
    }

    void buildInst(inst i) {
        if (i instanceof allocaInst) {
            tmpAsmFunction.addVarInStack(((allocaInst) i).reg);
            addInStack(((allocaInst) i).reg);
        }
        else if (i instanceof binaryInst) {
            PhyReg rs1 = getFreePhyReg();
            PhyReg rs2 = getFreePhyReg();
            PhyReg rd = getFreePhyReg();
            loadPhyRegValue(rs1 , ((binaryInst) i).lhs);
            loadPhyRegValue(rs2 , ((binaryInst) i).rhs);
            BinaryReg.binaryRegType op = null;
            switch (((binaryInst) i).opType) {
                case add: {
                    op =  BinaryReg.binaryRegType.add;
                    break;
                }
                case sub: {
                    op =  BinaryReg.binaryRegType.sub;
                    break;
                }
                case mul: {
                    op =  BinaryReg.binaryRegType.mul;
                    break;
                }
                case sdiv: {
                    op =  BinaryReg.binaryRegType.div;
                    break;
                }
                case srem: {
                    op =  BinaryReg.binaryRegType.rem;
                    break;
                }
                case shl: {
                    op =  BinaryReg.binaryRegType.sll;
                    break;
                }
                case ashr: {
                    op =  BinaryReg.binaryRegType.sra;
                    break;
                }
                case and: {
                    op =  BinaryReg.binaryRegType.and;
                    break;
                }
                case or: {
                    op =  BinaryReg.binaryRegType.or;
                    break;
                }
                case xor: {
                    op =  BinaryReg.binaryRegType.xor;
                    break;
                }
                default: throw new RuntimeException("UnKnown Binary Op");
            }
            tmpAsmBlock.addInst(new BinaryReg(rd , rs1 , rs2 , op));
            addInStack(((binaryInst) i).reg);
            storePhyRegValue(rd , ((binaryInst) i).reg);
            rd.free();
            rs1.free();
            rs2.free();
        }
        else if (i instanceof bitcastInst) {
            PhyReg rd = getFreePhyReg();
            if (((bitcastInst) i).from instanceof register) {
                loadPhyRegValue(rd , ((bitcastInst) i).from);
            } else {
                tmpAsmBlock.addInst(new LoadAddr(((globalVariable) ((bitcastInst) i).from).globalVariableName , rd));
            }
            assert ((bitcastInst) i).to instanceof register;
            addInStack((register) ((bitcastInst) i).to);
            storePhyRegValue(rd , (register) ((bitcastInst) i).to);
            rd.free();
        }
        else if (i instanceof brInst) {
            if (((brInst) i).condition == null) {
                int dest = labelMap.get(((brInst) i).trueBlock.label);
                tmpAsmBlock.addInst(new Jump("." + tmpAsmFunction.funcName + "_" + Integer.toString(dest)));
            } else {
                Branch.branchType op;
                int dest;
                if (tmpNextIRBlock == ((brInst) i).trueBlock) {
                    op = Branch.branchType.beqz;
                    dest = labelMap.get(((brInst) i).falseBlock.label);
                } else {
                    op = Branch.branchType.bnez;
                    dest = labelMap.get(((brInst) i).trueBlock.label);
                }
                PhyReg rs1 = getFreePhyReg();
                loadPhyRegValue(rs1 , ((brInst) i).condition);
                tmpAsmBlock.addInst(new Branch(rs1 , null , op , "." + tmpAsmFunction.funcName + "_" + Integer.toString(dest)));
                rs1.free();
            }
        }
        else if (i instanceof callInst) {
            if (((callInst) i).func.parameterLists != null) {
                for (int pointer = 0 ; pointer < ((callInst) i).func.parameterLists.size() ; ++pointer) {
                    entity para = ((callInst) i).func.parameterLists.get(pointer);
                    if (pointer < 8) {
                        PhyReg reg = phyRegs.get(parameterPRegNames[pointer]);
                        loadPhyRegValue(reg , para);
                    } else {
                        long offset_value = (((callInst) i).func.parameterLists.size() - pointer - 1) * 4L;
                        PhyReg offset = getFreePhyReg();
                        PhyReg address = getFreePhyReg();
                        PhyReg reg = getFreePhyReg();
                        loadPhyRegValue(reg , para);
                        tmpAsmBlock.addInst(new LoadImm(new Imm(offset_value) , offset));
                        tmpAsmBlock.addInst(new BinaryReg(address , phyRegs.get("sp") , offset , BinaryReg.binaryRegType.add));
                        tmpAsmBlock.addInst(new Store(new Imm(0L) , address , reg , Store.storeType.sw));
                        offset.free();
                        address.free();
                        reg.free();
                    }
                }
            }
            tmpAsmBlock.addInst(new Call(((callInst) i).func.funcName));
            if (((callInst) i).func.returnType != null) {
                addInStack(((callInst) i).reg);
                storePhyRegValue(phyRegs.get("a0") , ((callInst) i).reg);
            }
        }
        else if (i instanceof getelementptrInst) {
            PhyReg result = getFreePhyReg();
            PhyReg base = getFreePhyReg();
            PhyReg offsetReg = getFreePhyReg();
            PhyReg llReg = getFreePhyReg();
            entity offset;
            if (((getelementptrInst) i).arrayTag) offset = ((getelementptrInst) i).index_1;
            else offset = ((getelementptrInst) i).index_2;
            loadPhyRegValue(base , ((getelementptrInst) i).pointer);
            if (offset instanceof intConst) {
                long value = ((intConst) offset).value;
                long pOffset = value * 4;
                IRType pType = ((pointerType) ((getelementptrInst) i).pointer.getIRType()).pointerType;
                if (pType instanceof integerType) {
                    if (((integerType) pType).size == 8) pOffset = value;
                }
                tmpAsmBlock.addInst(new LoadImm(new Imm(pOffset) , offsetReg));
                tmpAsmBlock.addInst(new BinaryReg(result , base , offsetReg , BinaryReg.binaryRegType.add));
            } else {
                loadPhyRegValue(offsetReg , offset);
                tmpAsmBlock.addInst(new BinaryImm(llReg , offsetReg , new Imm(2L) , BinaryImm.binaryImmType.slli));
                tmpAsmBlock.addInst(new BinaryReg(result , base , llReg , BinaryReg.binaryRegType.add));
            }
            addInStack(((getelementptrInst) i).reg);
            storePhyRegValue(result , ((getelementptrInst) i).reg);
            result.free();
            base.free();
            offsetReg.free();
            llReg.free();
        }
        else if (i instanceof icmpInst) {
            PhyReg lhs = getFreePhyReg();
            PhyReg rhs = getFreePhyReg();
            PhyReg subReg = getFreePhyReg();
            PhyReg result = getFreePhyReg();
            loadPhyRegValue(lhs , ((icmpInst) i).lhs);
            loadPhyRegValue(rhs , ((icmpInst) i).rhs);
            tmpAsmBlock.addInst(new BinaryReg(subReg , lhs , rhs , BinaryReg.binaryRegType.sub));
            switch (((icmpInst) i).opType) {
                case eq: {
                    tmpAsmBlock.addInst(new Unary(result , subReg , Unary.unaryType.seqz));
                    break;
                }
                case ne: {
                    tmpAsmBlock.addInst(new Unary(result , subReg , Unary.unaryType.snez));
                    break;
                }
                case sgt: {
                    tmpAsmBlock.addInst(new Unary(result , subReg , Unary.unaryType.sgtz));
                    break;
                }
                case slt: {
                    tmpAsmBlock.addInst(new Unary(result , subReg , Unary.unaryType.sltz));
                    break;
                }
                case sge: {
                    PhyReg tmp = getFreePhyReg();
                    tmpAsmBlock.addInst(new Unary(tmp , subReg , Unary.unaryType.sltz));
                    tmpAsmBlock.addInst(new Unary(result , tmp , Unary.unaryType.seqz));
                    tmp.free();
                    break;
                }
                default: {
                    PhyReg tmp = getFreePhyReg();
                    tmpAsmBlock.addInst(new Unary(tmp , subReg , Unary.unaryType.sgtz));
                    tmpAsmBlock.addInst(new Unary(result , tmp , Unary.unaryType.seqz));
                    tmp.free();
                    break;
                }
            }
            addInStack(((icmpInst) i).reg);
            storePhyRegValue(result , ((icmpInst) i).reg);
            lhs.free();
            rhs.free();
            subReg.free();
            result.free();
        }
        else if (i instanceof loadInst) {
            PhyReg result = getFreePhyReg();
            PhyReg address = getFreePhyReg();
            if (((loadInst) i).from instanceof globalVariable) {
                tmpAsmBlock.addInst(new LoadAddr(((globalVariable) ((loadInst) i).from).globalVariableName , address));
                tmpAsmBlock.addInst(new Load(new Imm(0L) , result , address , Load.loadType.lw));
            } else if (!tmpAsmFunction.containsVarInStack((register) ((loadInst) i).from)) {
                loadPhyRegValue(address , ((loadInst) i).from);
                tmpAsmBlock.addInst(new Load(new Imm(0L) , result , address , Load.loadType.lw));
            } else loadPhyRegValue(result , ((loadInst) i).from);
            addInStack(((loadInst) i).destPos);
            storePhyRegValue(result , ((loadInst) i).destPos);
            result.free();
            address.free();
        }
        else if (i instanceof phiInst) {
            AsmBlock block_1 = blockMap.get(labelMap.get(((phiInst) i).phiBlock_1.label));
            AsmBlock block_2 = blockMap.get(labelMap.get(((phiInst) i).phiBlock_2.label));
            entity entity_1 = ((phiInst) i).phiEntity_1;
            entity entity_2 = ((phiInst) i).phiEntity_2;
            assert block_1.tailInst instanceof Branch;
            assert block_2.tailInst instanceof Jump;
            assert entity_1 instanceof boolConst;
            ((PhyReg)((Branch) block_1.tailInst).rs1).busy();
            PhyReg result = getFreePhyReg();
            block_1.insertInstPre(new LoadImm(new Imm(((boolConst) entity_1).value) , result) , block_1.tailInst);
            if (entity_2 instanceof register) {
                PhyReg offset = getFreePhyReg();
                PhyReg address = getFreePhyReg();
                block_2.insertInstPre(new LoadImm(new Imm(tmpRegAddress.get(((register) entity_2).regNumber)) , offset) , block_2.tailInst);
                block_2.insertInstPre(new BinaryReg(address , phyRegs.get("sp") , offset , BinaryReg.binaryRegType.add) , block_2.tailInst);
                block_2.insertInstPre(new Load(new Imm(0L) , result , address , Load.loadType.lw) , block_2.tailInst);
                offset.free();
                address.free();
            } else if (entity_2 instanceof boolConst) {
                block_2.insertInstPre(new LoadImm(new Imm(((boolConst) entity_2).value) , result) , block_2.tailInst);
            } else throw new RuntimeException("Phi entity_2 error !");
            addInStack(((phiInst) i).reg);
            storePhyRegValue(result , ((phiInst) i).reg);
            ((PhyReg)((Branch) block_1.tailInst).rs1).free();
            result.free();
        }
        else if (i instanceof retInst) {
            PhyReg ret = phyRegs.get("a0");
            if (((retInst) i).ret != null) loadPhyRegValue(ret , ((retInst) i).ret);
            releaseReg();
            tmpAsmBlock.addInst(new LoadImm(new Imm(sp_offest) , phyRegs.get("t0")));
            tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("sp") , phyRegs.get("sp") , phyRegs.get("t0") , BinaryReg.binaryRegType.add));
            tmpAsmBlock.addInst(new Return());
        }
        else if (i instanceof storeInst) {
            PhyReg value = getFreePhyReg();
            PhyReg address = getFreePhyReg();
            loadPhyRegValue(value , ((storeInst) i).value);
            if (((storeInst) i).storePos instanceof globalVariable) {
                tmpAsmBlock.addInst(new LoadAddr(((globalVariable) ((storeInst) i).storePos).globalVariableName , address));
                tmpAsmBlock.addInst(new Store(new Imm(0L) , address , value , Store.storeType.sw));
            } else if (!tmpAsmFunction.containsVarInStack((register) ((storeInst) i).storePos)) {
                loadPhyRegValue(address , ((storeInst) i).storePos);
                tmpAsmBlock.addInst(new Store(new Imm(0L) , address , value , Store.storeType.sw));
            } else storePhyRegValue(value , (register) ((storeInst) i).storePos);
            value.free();
            address.free();
        }
    }

    void frameInit(func f) {
        for (int i = 0 ; i < f.basicBlockList.size() ; ++i) {
            labelMap.put(f.basicBlockList.get(i).label , i);
        }
        int calleeRegCnt = 12;
        int raCnt = 1;
        int funcParaCnt = f.parameter == null ? 0 : f.parameter.size();
        int callParaCnt = 50;
        int virtualRegCnt = f.getEntityNum() - f.basicBlockList.size();
        long total = calleeRegCnt + raCnt + funcParaCnt + callParaCnt + virtualRegCnt;
        sp_offest = (total % 4 == 0) ? 4 * total : 16 * (total / 4 + 1);

        tmpAsmBlock.addInst(new LoadImm(new Imm(-sp_offest) , phyRegs.get("t0")));
        tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("sp") , phyRegs.get("sp") , phyRegs.get("t0") , BinaryReg.binaryRegType.add));
        tmpAsmBlock.addInst(new LoadImm(new Imm(sp_offest - 4) , phyRegs.get("t0")));
        tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("t1") , phyRegs.get("t0") , phyRegs.get("sp") , BinaryReg.binaryRegType.add));
        tmpAsmBlock.addInst(new Store( new Imm(0) , phyRegs.get("t1") , phyRegs.get("ra") , Store.storeType.sw));

        int pointer = 2;
        for (String calleeSaveRegName : calleeSavePRegNames) {
            tmpAsmBlock.addInst(new LoadImm(new Imm(sp_offest - 4L * pointer) , phyRegs.get("t0")));
            tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("t1") , phyRegs.get("t0") , phyRegs.get("sp") , BinaryReg.binaryRegType.add));
            tmpAsmBlock.addInst(new Store(new Imm(0) , phyRegs.get("t1") , phyRegs.get(calleeSaveRegName) , Store.storeType.sw));
            pointer++;
        }
        tmpRegCnt = 13;

        for (pointer = 0 ; pointer < f.parameterReg.size() ; pointer++) {
            register Para = f.parameterReg.get(pointer).b;
            addInStack(Para);
            PhyReg tmp;
            if (pointer < 8) {
                tmp = phyRegs.get(parameterPRegNames[pointer]);
                storePhyRegValue(tmp , Para);
            } else {
                tmp = getFreePhyReg();
                tmpAsmBlock.addInst(new LoadImm(new Imm(sp_offest + 4L * (pointer - 8)) , phyRegs.get("t0")));
                tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("t1") , phyRegs.get("t0") , phyRegs.get("sp") , BinaryReg.binaryRegType.add));
                tmpAsmBlock.addInst(new Load(new Imm(0) , tmp , phyRegs.get("t1") , Load.loadType.lw));
                storePhyRegValue(tmp , Para);
                tmp.free();
            }
        }
    }

    void releaseReg() {
        long pointer = 2;
        for (String calleeSaveRegName : calleeSavePRegNames) {
            tmpAsmBlock.addInst(new LoadImm(new Imm(sp_offest - 4 * pointer) , phyRegs.get("t0")));
            tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("t1") , phyRegs.get("sp") , phyRegs.get("t0") , BinaryReg.binaryRegType.add));
            tmpAsmBlock.addInst(new Load(new Imm(0L) , phyRegs.get(calleeSaveRegName) , phyRegs.get("t1") , Load.loadType.lw));
            pointer++;
        }
        tmpAsmBlock.addInst(new LoadImm(new Imm(sp_offest - 4) , phyRegs.get("t0")));
        tmpAsmBlock.addInst(new BinaryReg(phyRegs.get("t1") , phyRegs.get("sp") , phyRegs.get("t0") , BinaryReg.binaryRegType.add));
        tmpAsmBlock.addInst(new Load(new Imm(0L) , phyRegs.get("ra") , phyRegs.get("t1") , Load.loadType.lw));
    }

    PhyReg getFreePhyReg() {
        for (String name : calleeSavePRegNames) {
            if (!phyRegs.get(name).isBusy()) {
                phyRegs.get(name).busy();
                return phyRegs.get(name);
            }
        }
        throw new RuntimeException("no free reg");
    }

    void addInStack(register reg) {
        tmpRegCnt++;
        tmpRegAddress.put(reg.regNumber , sp_offest - tmpRegCnt * 4);
    }

    void loadPhyRegValue(PhyReg phyReg , entity e) {
        if (e instanceof globalVariable) throw new RuntimeException("VirtualReg instanceof globalVar");
        if (e instanceof register) {
            PhyReg offset = getFreePhyReg();
            PhyReg address = getFreePhyReg();
            tmpAsmBlock.addInst(new LoadImm(new Imm(tmpRegAddress.get(((register) e).regNumber)) , offset));
            tmpAsmBlock.addInst(new BinaryReg(address , phyRegs.get("sp") , offset , BinaryReg.binaryRegType.add));
            tmpAsmBlock.addInst(new Load(new Imm(0L) , phyReg , address , Load.loadType.lw));
            offset.free();
            address.free();
        }
        else if (e instanceof intConst) {
            tmpAsmBlock.addInst(new LoadImm(new Imm(((intConst) e).value) , phyReg));
        }
        else if (e instanceof boolConst) {
            tmpAsmBlock.addInst(new LoadImm(new Imm(((boolConst) e).value) , phyReg));
        }
        else if (e instanceof nullConst) {
            tmpAsmBlock.addInst(new Move(phyReg , phyRegs.get("zero")));
        }
        else if (e instanceof stringConst) {
            throw new RuntimeException("VirtualReg instanceof stringConst");
        }
    }

    void storePhyRegValue(PhyReg phyReg , register reg) {
        PhyReg offset = getFreePhyReg();
        PhyReg address = getFreePhyReg();
        tmpAsmBlock.addInst(new LoadImm(new Imm(tmpRegAddress.get(reg.regNumber)) , offset));
        tmpAsmBlock.addInst(new BinaryReg(address , phyRegs.get("sp") , offset , BinaryReg.binaryRegType.add));
        tmpAsmBlock.addInst(new Store(new Imm(0L) , address , phyReg , Store.storeType.sw));
        offset.free();
        address.free();
    }
}
