package core.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author KuangHaochuan
 */
public interface RestrofitService {

    /**
     * getInstance
     *
     * @param project project
     * @return obj
     */
    static RestrofitService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, RestrofitService.class);
    }

    /**
     * setupImpl
     *
     * @param toolWindow toolWindow
     */
    void setupImpl(@NotNull ToolWindow toolWindow);
}
