package joelbits.preprocessor.parsers;

import com.github.javaparser.ast.*;
import com.github.javaparser.utils.Log;
import static joelbits.model.ast.protobuf.ASTProtos.Namespace;
import static joelbits.model.ast.protobuf.ASTProtos.Declaration;

import joelbits.preprocessor.parsers.types.ParserType;
import joelbits.preprocessor.parsers.utils.ASTNodeCreater;
import joelbits.preprocessor.parsers.visitors.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Use this class to load a Java file and parse it as desired.
 */
public final class JavaParser implements Parser {
    private CompilationUnit compilationUnit;
    private final List<String> imports = new ArrayList<>();
    private final List<Namespace> namespaces = new ArrayList<>();
    private final List<Declaration> nestedDeclarations = new ArrayList<>();

    /**
     *  Receives a snapshot of a file and loads that file in the parser. Then parses the class into an AST.
     *
     * @param file    current revision of the file to parse
     */
    @Override
    public byte[] parse(File file) throws Exception {
        loadFile(file);

        compilationUnit.accept(new ImportVisitor(), imports);
        List<Declaration> declarations = new ArrayList<>();
        compilationUnit.accept(new ClassOrInterfaceVisitor(declarations), nestedDeclarations);
        compilationUnit.accept(new NamespaceVisitor(namespaces), declarations);

        Log.info("Parsing of " + file.getName() + " completed");

        return ASTNodeCreater.createAstRoot(imports, namespaces).toByteArray();
    }

    private void loadFile(File file) throws Exception {
        clearData();
        try (FileInputStream in = new FileInputStream(file)) {
            compilationUnit = com.github.javaparser.JavaParser.parse(in);
            Log.info("Loaded " + file.getName());
        }
    }

    private void clearData() {
        imports.clear();
        namespaces.clear();
        nestedDeclarations.clear();
    }

    @Override
    public boolean hasBenchmarks(File file) throws Exception {
        loadFile(file);
        return compilationUnit.getImports().stream()
                .map(ImportDeclaration::getNameAsString)
                .anyMatch(i -> i.toUpperCase().contains(ParserType.JMH.name()));
    }

    @Override
    public String toString() {
        return "JavaParser";
    }
}