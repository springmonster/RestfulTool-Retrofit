package core.view.icon;

import core.beans.HttpMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public interface IconType {
    
    /**
     * 默认显示
     *
     * @param method method
     * @return default
     */
    @NotNull
    Icon getDefaultIcon(HttpMethod method);
    
    /**
     * 图标名
     *
     * @return name
     */
    @Override
    @NotNull
    String toString();
}
