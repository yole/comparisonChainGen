package org.jetbrains.comparisonChain;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author yole
 */
public class GenerateTest extends LightCodeInsightFixtureTestCase {
    private static class MyDescriptor extends DefaultLightProjectDescriptor {
        @Override
        public Sdk getSdk() {
            return JavaSdk.getInstance().createJdk("1.7", new File(getSourceRoot(), "mockJDK-1.7").getPath(),
                    false);
        }
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new MyDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return new File(getSourceRoot(), "testData").getPath();
    }

    private static File getSourceRoot() {
        String testOutput = PathManager.getJarPathForClass(GenerateTest.class);
        return new File(testOutput, "../../..");
    }

    public void testSimple() {
        myFixture.configureByFile("before" + getTestName(false) + ".java");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        PsiClass simpleClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        List<PsiField> fields = Collections.singletonList(simpleClass.findFieldByName("foo", false));
        new GenerateAction().generateComparable(simpleClass, fields);
        myFixture.checkResultByFile("after" + getTestName(false) + ".java");
    }

    public void testInspection() {
        myFixture.enableInspections(EqualsCompareConsistencyInspection.class);
        myFixture.testHighlighting(true, false, false, "inspection.java");
    }
}
