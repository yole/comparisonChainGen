package org.jetbrains.comparisonChain;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yole
 */
public class EqualsCompareConsistencyInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new MyVisitor(holder);
    }

    private static class MyVisitor extends JavaElementVisitor {
        private final ProblemsHolder myHolder;

        public MyVisitor(ProblemsHolder holder) {
            myHolder = holder;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            if ("compareTo".equals(method.getName()) && isInheritedFrom(method, CommonClassNames.JAVA_LANG_COMPARABLE)) {
                PsiMethod equals = findEqualsMethod(method.getContainingClass());
                if (equals != null) {
                    List<PsiField> equalsFields = findReferencedFields(equals);
                    List<PsiField> compareToFields = findReferencedFields(method);
                    for (PsiField equalsField : equalsFields) {
                        if (!compareToFields.contains(equalsField)) {
                            myHolder.registerProblem(method.getNameIdentifier(),
                                    "The compareTo() method does not reference '" + equalsField.getName() + "' which is referenced from equals(); inconsistency may result");
                            break;
                        }
                    }
                }

            }
        }

        private List<PsiField> findReferencedFields(final PsiMethod psiMethod) {
            final List<PsiField> result = new ArrayList<PsiField>();
            psiMethod.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
                    PsiElement resolved = reference.resolve();
                    if (resolved instanceof PsiField &&
                            InheritanceUtil.isInheritorOrSelf(psiMethod.getContainingClass(),
                                    ((PsiField) resolved).getContainingClass(), true)) {
                        result.add((PsiField) resolved);

                    }
                }
            });
            return result;
        }

        private PsiMethod findEqualsMethod(PsiClass containingClass) {
            for (PsiMethod equals : containingClass.findMethodsByName("equals", false)) {
                if (isInheritedFrom(equals, CommonClassNames.JAVA_LANG_OBJECT)) {
                    return equals;
                }
            }
            return null;
        }

        private boolean isInheritedFrom(PsiMethod method, String baseClass) {
            for (PsiMethod baseMethod : method.findDeepestSuperMethods()) {
                if (baseClass.equals(baseMethod.getContainingClass().getQualifiedName())) {
                    return true;
                }
            }
            return false;
        }
    }
}
