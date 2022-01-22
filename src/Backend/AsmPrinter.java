package Backend;

import Assembly.AsmBlock;
import Assembly.AsmModule;
import Assembly.Inst.Inst;

import java.io.PrintStream;

public class AsmPrinter {
    public PrintStream pr;
    public AsmModule asmRoot;

    public AsmPrinter(PrintStream pr , AsmModule asmRoot) {
        this.pr = pr;
        this.asmRoot = asmRoot;
    }

    public void printAsm() {
        pr.println("\t.text");
        asmRoot.text.forEach(pd -> {
            pr.println("\t.globl\t" + pd.a);
            pr.println("\t.p2align\t2");
            pr.println("\t.type\t" + pd.a + ",@function");
            pr.println(pd.a + ":");
            AsmBlock block = pd.b.firstBlock;
            while (block != null) {
                if (block != pd.b.firstBlock) {
                    pr.println(block.getLabel() + ":");
                }
                Inst i = block.headInst;
                while (i != null ) {
                    pr.println("\t" + i.toString());
                    i = i.next;
                }
                block = block.next;
            }
            pr.println();
        });
        pr.println();

        pr.println("\t.section\t.bss,\"aw\",@nobits");
        asmRoot.bss.forEach(pd -> {
            pr.println("\t.type\t" + pd + ",@object");
            pr.println("\t.globl\t" + pd);
            pr.println(pd + ":");
            pr.println("\t.word\t0");
            pr.println("\t.size\t" + pd + ", 4");
            pr.println();
        });
        pr.println();

        pr.println("\t.section\t.rodata,\"a\",@progbits");
        asmRoot.rodata.forEach(sd -> {
            pr.println("\t.type\t" + sd.a + ",@object");
            pr.println("\t.globl\t" + sd.a);
            pr.println(sd.a + ": ");
            pr.println("\t.asciz\t" + sd.b);
            pr.println("\t.size\t" + sd.a + ", " + (sd.b.length() + 1));
            pr.println();
        });

        pr.println("\t.section\t\".note.GNU-stack\",\"\",@progbits");
    }
}
