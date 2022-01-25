package core.view.icon.impl;

import core.beans.HttpMethod;
import core.view.icon.IconType;
import core.view.icon.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public class DefaultIcon implements IconType {
    
    public static final Icon GET = Icons.load("/icons/method/default/GET.png");
    public static final Icon POST = Icons.load("/icons/method/default/POST.png");
    public static final Icon DELETE = Icons.load("/icons/method/default/DELETE.png");
    public static final Icon PUT = Icons.load("/icons/method/default/PUT.png");
    public static final Icon PATCH = Icons.load("/icons/method/default/PATCH.png");
    public static final Icon HEAD = Icons.load("/icons/method/default/HEAD.png");
    
    private static final Map<HttpMethod, Icon> ICONS;
    
    static {
        ICONS = new HashMap<>(HttpMethod.values().length);
        ICONS.put(HttpMethod.GET, GET);
        ICONS.put(HttpMethod.POST, POST);
        ICONS.put(HttpMethod.DELETE, DELETE);
        ICONS.put(HttpMethod.PUT, PUT);
        ICONS.put(HttpMethod.PATCH, PATCH);
        ICONS.put(HttpMethod.HEAD, HEAD);
    }
    
    @NotNull
    @Override
    public Icon getDefaultIcon(HttpMethod method) {
        return ICONS.get(method);
    }
    
    @NotNull
    @Override
    public String toString() {
        return "Default";
    }
}
