package joelbits.preprocessor.parsers.visitors;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos.Variable;
import joelbits.model.ast.protobuf.ASTProtos.Modifier;
import joelbits.model.ast.protobuf.ASTProtos.Declaration;
import joelbits.model.ast.protobuf.ASTProtos.Method;
import joelbits.preprocessor.parsers.utils.ASTNodeCreater;

import java.util.ArrayList;
import java.util.List;

public class ClassOrInterfaceVisitor extends VoidVisitorAdapter<List<Declaration>> {
    private List<Declaration> namespaceDeclarations;

    public ClassOrInterfaceVisitor() {}

    public ClassOrInterfaceVisitor(List<Declaration> namespaceDeclarations) {
        this.namespaceDeclarations = namespaceDeclarations;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, List<Declaration> nestedDeclarations) {
        List<Variable> allFields = new ArrayList<>();
        List<Method> allMethods = new ArrayList<>();

        for (BodyDeclaration member : declaration.getMembers()) {
            if (member.isMethodDeclaration()) {
                member.accept(new MethodVisitor(), allMethods);
            }
            if (member.isFieldDeclaration()) {
                member.accept(new FieldVisitor(), allFields);
            }
            if (member.isClassOrInterfaceDeclaration()) {
                member.accept(new ClassOrInterfaceVisitor(), nestedDeclarations);
            }
        }

        List<Modifier> modifiers = new ArrayList<>();
        declaration.accept(new DeclarationModifierVisitor(), modifiers);

        if (declaration.isTopLevelType()) {
            namespaceDeclarations.add(ASTNodeCreater.createNamespaceDeclaration(declaration, allFields, allMethods, modifiers, nestedDeclarations));
            nestedDeclarations.clear();
        } else {
            nestedDeclarations.add(ASTNodeCreater.createNestedDeclaration(declaration, allFields, allMethods, modifiers));
        }
    }
}