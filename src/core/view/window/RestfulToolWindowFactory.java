package core.view.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import core.service.RetrofitService;
import org.jetbrains.annotations.NotNull;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public class RestfulToolWindowFactory implements ToolWindowFactory {
    
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RetrofitService.getInstance(project).setupImpl(toolWindow);
    }
}
