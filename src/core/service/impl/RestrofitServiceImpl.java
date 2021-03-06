package core.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import core.service.RestrofitService;
import core.view.window.frame.RightToolJPanel;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author KuangHaochuan
 */
public class RestrofitServiceImpl implements RestrofitService {

    private final Project project;

    public RestrofitServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public void setupImpl(@NotNull ToolWindow toolWindow) {
        RightToolJPanel view = new RightToolJPanel(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, "", false);

        toolWindow.getContentManager().addContent(content);
    }
}
