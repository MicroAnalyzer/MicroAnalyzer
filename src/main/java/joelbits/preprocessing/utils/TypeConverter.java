package joelbits.preprocessing.utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.protobuf.ASTProtos.DeclarationType;
import joelbits.model.ast.protobuf.ASTProtos.Modifier.ModifierType;
import joelbits.model.ast.protobuf.ASTProtos.Modifier.VisibilityType;
import joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import joelbits.preprocessing.parsers.utils.ASTNodeCreator;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class TypeConverter {
    public static ModifierType getModifierType(String modifier) {
        if (ModifierType.STATIC.name().equals(modifier.toUpperCase())) {
            return ModifierType.STATIC;
        } else if (ModifierType.FINAL.name().equals(modifier.toUpperCase())) {
            return ModifierType.FINAL;
        } else if (ModifierType.ABSTRACT.name().equals(modifier.toUpperCase())) {
            return ModifierType.ABSTRACT;
        } else if (ModifierType.SYNCHRONIZED.name().equals(modifier.toUpperCase())) {
            return ModifierType.SYNCHRONIZED;
        }

        String[] visibilityModifiers = Arrays.asList(VisibilityType.values()).stream().map(Enum::name).toArray(String[]::new);
        if (Arrays.asList(visibilityModifiers).contains(modifier.toUpperCase())) {
            return ModifierType.VISIBILITY;
        }

        return ModifierType.OTHER;
    }

    public static ExpressionType getExpressionType(Expression expression) {
        if (expression.isLiteralExpr()) {
            return ExpressionType.LITERAL;
        } else if (expression.isAssignExpr()) {
            return ExpressionType.ASSIGN;
        } else if (expression.isMethodCallExpr()) {
            return ExpressionType.METHODCALL;
        } else if (expression.isObjectCreationExpr()) {
            return ExpressionType.NEW;
        } else if (expression.isVariableDeclarationExpr()) {
            return ExpressionType.VARIABLE_DECLARATION;
        }

        return ExpressionType.OTHER;
    }

    public static DeclarationType getDeclarationType(ClassOrInterfaceDeclaration declaration) {
        if (declaration.isInterface()) {
            return DeclarationType.INTERFACE;
        } else if (declaration.isAnnotationDeclaration()) {
            return DeclarationType.ANNOTATION;
        } else if (declaration.isEnumDeclaration()) {
            return DeclarationType.ENUM;
        } else if (declaration.isGeneric()) {
            return DeclarationType.GENERIC;
        } else if (declaration.isInnerClass() || declaration.isLocalClassDeclaration() || declaration.isClassOrInterfaceDeclaration()) {
            return DeclarationType.CLASS;
        }

        return DeclarationType.OTHER;
    }

    public static List<ASTProtos.Modifier> convertModifiers(EnumSet<Modifier> modifiers) {
        List<ASTProtos.Modifier> argumentModifiers = new ArrayList<>();
        for (Modifier modifier : modifiers) {
            argumentModifiers.add(ASTNodeCreator.createModifier(modifier.name()));
        }

        return argumentModifiers;
    }

    public static List<String> convertAnnotationMembers(AnnotationExpr annotationExpr) {
        List<String> membersAndValues = new ArrayList<>();

        if (annotationExpr.isNormalAnnotationExpr()) {
            NodeList<MemberValuePair> pairs = annotationExpr.asNormalAnnotationExpr().getPairs();
            for (MemberValuePair pair : pairs) {
                String memberName = handleAbsentMembers(annotationExpr, pair.getNameAsString());
                membersAndValues.add(memberName + " " + pair.getValue().toString());
            }
        } else if (annotationExpr.isSingleMemberAnnotationExpr()) {
            SingleMemberAnnotationExpr singleMember = annotationExpr.asSingleMemberAnnotationExpr();
            String memberName = handleAbsentMembers(annotationExpr, singleMember.getNameAsString());
            membersAndValues.add(memberName + " " + singleMember.getMemberValue().toString());
        }

        return membersAndValues;
    }

    /**
     * If an annotation only contains a single value (e.g., an Enum) and no member name the member name attribute
     * should be blank since no member exist.
     *
     * @param annotationExpr        the object corresponding to the annotation
     * @param memberName            the extracted member name
     * @return                      member name if a member exist in annotation, otherwise empty string
     */
    private static String handleAbsentMembers(AnnotationExpr annotationExpr, String memberName) {
        if (memberName.equals(annotationExpr.getNameAsString())) {
            memberName = StringUtils.EMPTY;
        }
        return memberName;
    }

    /**
     * Since some sources may have different ChangeTypes than used in the Protocol Buffer, they have to
     * be mapped to corresponding ChangeType.
     *
     * @param type          the ChangeType of the parsed source
     * @return              the ChangeType used in the Project Protocol Buffer message
     */
    public static String convertChangeType(String type) {
        switch(type.toUpperCase()) {
            case "MODIFY":
                return "MODIFIED";
            case "ADD":
                return "ADDED";
            case "DELETE":
                return "DELETED";
            default:
                return type.toUpperCase();
        }
    }
}
