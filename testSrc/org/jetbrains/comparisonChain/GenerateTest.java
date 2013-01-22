package org.jetbrains.comparisonChain;

import com.intellij.openapi.application.PathManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author yole
 */
public class GenerateTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        String testOutput = PathManager.getJarPathForClass(GenerateTest.class);
        File sourceRoot = new File(testOutput, "../../..");
        return new File(sourceRoot, "testData").getPath();
    }

    public void testSimple() {
        myFixture.configureByFile("before" + getTestName(false) + ".java");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        PsiClass simpleClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        List<PsiField> fields = Collections.singletonList(simpleClass.findFieldByName("foo", false));
        new GenerateAction().generateComparable(simpleClass, fields);
        myFixture.checkResultByFile("after" + getTestName(false) + ".java");
    }
}
