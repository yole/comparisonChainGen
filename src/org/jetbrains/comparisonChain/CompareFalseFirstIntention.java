package org.jetbrains.comparisonChain;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class CompareFalseFirstIntention implements IntentionAction {
    @NotNull
    @Override
    public String getText() {
        return "Replace with compareFalseFirst()";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        PsiReference referenceAt = file.findReferenceAt(editor.getCaretModel().getOffset());
        if (!(referenceAt instanceof PsiReferenceExpression)) {
            return false;
        }
        if (!"compare".equals(((PsiReferenceExpression) referenceAt).getReferenceName())) {
            return false;
        }
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return true;
        }
        PsiElement target = referenceAt.resolve();
        if (!(target instanceof PsiMethod)) return false;
        PsiMethod method = (PsiMethod) target;
        if (!GenerateAction.COM_GOOGLE_COMMON_COLLECT_COMPARISON_CHAIN.equals(method.getContainingClass().getQualifiedName())) {
            return false;
        }
        PsiParameter[] parameters = method.getParameterList().getParameters();
        return parameters.length == 2 && parameters[0].getType() == PsiType.BOOLEAN;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiReference referenceAt = file.findReferenceAt(editor.getCaretModel().getOffset());
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiJavaCodeReferenceElement newReference = factory.createReferenceFromText("compareFalseFirst",
                referenceAt.getElement());
        ((PsiReferenceExpression) referenceAt).getReferenceNameElement().replace(newReference.getReferenceNameElement());
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
