package core.view.icon;

import core.view.icon.impl.DefaultIcon;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public class IconTypeManager {
    
    private static final DefaultIcon DEFAULT_ICON = new DefaultIcon();
    
    public static IconType getInstance() {
        return DEFAULT_ICON;
    }
}
