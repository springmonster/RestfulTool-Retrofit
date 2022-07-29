package core.service.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import core.service.RetrofitService;
import core.view.window.frame.RightToolJPanel;
import org.jetbrains.annotations.NotNull;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public class RetrofitServiceImpl implements RetrofitService {
    
    private final Project project;
    
    public RetrofitServiceImpl(Project project) {
        this.project = project;
    }
    
    @Override
    public void setupImpl(@NotNull ToolWindow toolWindow) {
        RightToolJPanel view = new RightToolJPanel(project);
        
        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(view, "", false);
        
        toolWindow.getContentManager().addContent(content);
    }
}
