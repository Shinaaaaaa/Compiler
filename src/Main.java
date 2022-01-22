import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;

import AST.*;
import Assembly.AsmModule;
import Backend.*;
import Frontend.*;
import LLVMIR.*;
import LLVMIR.entity.*;
import Parser.*;
import Util.*;
import Util.error.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws Exception{
//        String name = "test.mx";
//        InputStream input = new FileInputStream(name);

        InputStream input = System.in;
        PrintStream asm_output = System.out;

        boolean SemanticSwitch = false;
        boolean AssemblySwitch = false;
        for (int i = 0; i < args.length; ++i){
            if (args[i].charAt(0) == '-'){
                if (Objects.equals(args[i] , "-fsyntax-only"))
                    SemanticSwitch = true;
                else if (Objects.equals(args[i], "-S"))
                    AssemblySwitch = true;
                else if (Objects.equals(args[i], "-o"))
                    asm_output = new PrintStream(new FileOutputStream(args[i + 1]));
            }
        }

        try {
            programNode program;
            globalScope gScope = new globalScope(null , "global");
            MxLexer lexer = new MxLexer(CharStreams.fromStream(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());
            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            ParseTree parseTreeRoot = parser.program();

            ASTBuilder builder = new ASTBuilder(gScope);
            program = (programNode) builder.visit(parseTreeRoot);
            SymbolCollector collector = new SymbolCollector(gScope);
            collector.Init();
            collector.visit(program);
            new SemanticChecker(gScope).visit(program);

            if (!SemanticSwitch) {
                IRroot irroot = new IRroot();
                entityScope eScope = new entityScope(null);
                IRCollector irCollector = new IRCollector(irroot);
                irCollector.Init();
                irCollector.visit(program);
                new IRBuilder(irroot , eScope).visit(program);
//              PrintStream ll_output = new PrintStream("test.ll");
//              new IRPrinter(ll_output , irroot).printIR();

                AsmModule asmRoot = new AsmModule();
                new AsmBuilder(irroot , asmRoot).buildAsm();
                new AsmPrinter(asm_output , asmRoot).printAsm();
            }
        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}
