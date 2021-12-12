import java.io.FileInputStream;
import java.io.InputStream;
import AST.*;
import Frontend.*;
import Parser.*;
import Util.*;
import Util.error.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws Exception{
        String name = "test.mx";
        InputStream input = new FileInputStream(name);
//        InputStream input = System.in;
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
        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}
