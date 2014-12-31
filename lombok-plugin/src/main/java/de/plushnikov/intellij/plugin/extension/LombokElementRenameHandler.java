package de.plushnikov.intellij.plugin.extension;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * "Rename"-Handler for methods generated by lombok
 * At this moment it doesn't renamed anything, but forbid default rename operation
 */
public class LombokElementRenameHandler implements RenameHandler {
  public boolean isAvailableOnDataContext(DataContext dataContext) {
    final PsiElement element = getElement(dataContext);
    return element instanceof LombokLightMethodBuilder;
  }

  @Nullable
  private static PsiElement getElement(DataContext dataContext) {
    return LangDataKeys.PSI_ELEMENT.getData(dataContext);
  }

  public boolean isRenaming(DataContext dataContext) {
    return isAvailableOnDataContext(dataContext);
  }

  public void invoke(@NotNull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext) {
    final PsiElement element = getElement(dataContext);
    invokeInner(project, editor, element);
  }

  public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, @Nullable DataContext dataContext) {
    PsiElement element = elements.length == 1 ? elements[0] : null;
    if (element == null) {
      element = getElement(dataContext);
    }
    Editor editor = dataContext == null ? null : PlatformDataKeys.EDITOR.getData(dataContext);
    invokeInner(project, editor, element);
  }

  private void invokeInner(Project project, Editor editor, PsiElement element) {
    String message = RefactoringBundle.getCannotRefactorMessage("This element cannot be renamed.");
    if (!message.isEmpty()) {
      showErrorMessage(project, editor, message);
    }
  }

  void showErrorMessage(Project project, @Nullable Editor editor, String message) {
    CommonRefactoringUtil.showErrorHint(project, editor, message, RefactoringBundle.message("rename.title"), null);
  }
}
