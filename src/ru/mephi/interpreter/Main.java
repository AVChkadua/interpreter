package ru.mephi.interpreter;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.mephi.interpreter.generated.LangLexer;
import ru.mephi.interpreter.generated.LangParser;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ANTLRInputStream stream = new ANTLRFileStream("/home/anton/prog");
        LangLexer lexer = new LangLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LangParser parser = new LangParser(tokens);
        ParseTree tree = parser.main();

        new TreeVisitor().visit(tree);
    }
}
