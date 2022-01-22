import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

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
//        PrintStream ll_output = new PrintStream("test.ll");
        InputStream input = System.in;
        PrintStream asm_output = new PrintStream("output.s");

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

            IRroot irroot = new IRroot();
            entityScope eScope = new entityScope(null);
            IRCollector irCollector = new IRCollector(irroot);
            irCollector.Init();
            irCollector.visit(program);
            new IRBuilder(irroot , eScope).visit(program);
//            new IRPrinter(ll_output , irroot).printIR();

            AsmModule asmRoot = new AsmModule();
            new AsmBuilder(irroot , asmRoot).buildAsm();
            new AsmPrinter(asm_output , asmRoot).printAsm();
        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}
