package core.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public interface RetrofitService {
    
    /**
     * getInstance
     *
     * @param project project
     * @return obj
     */
    static RetrofitService getInstance(@NotNull Project project) {
        return project.getService(RetrofitService.class);
    }
    
    /**
     * setupImpl
     *
     * @param toolWindow toolWindow
     */
    void setupImpl(@NotNull ToolWindow toolWindow);
}
