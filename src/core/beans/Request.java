/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: Request
  Author:   ZhangYuanSheng
  Date:     2020/5/2 00:43
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package core.beans;

import com.jetbrains.lang.dart.psi.DartComponent;
import core.view.icon.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class Request {

    private HttpMethod method;
    private String path;
    private Icon icon;

    private final DartComponent dartComponent;

    public Request(HttpMethod method, @Nullable String path, @Nullable DartComponent dartComponent) {
        this.setMethod(method);
        if (path != null) {
            this.setPath(path);
        }
        this.dartComponent = dartComponent;
    }

    public void navigate(boolean requestFocus) {
        if (dartComponent != null) {
            dartComponent.navigate(requestFocus);
        }
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
        this.icon = Icons.getMethodIcon(method);
    }

    public Icon getIcon() {
        return icon;
    }

    public Icon getSelectIcon() {
        return Icons.getMethodIcon(this.method);
    }

    public String getPath() {
        return path;
    }

    public void setPath(@NotNull String path) {
        path = path.trim();
        if (!path.startsWith("/") && !path.contains("https") && !path.contains("http")) {
            path = "/" + path;
        }
        this.path = path;
    }

    @Override
    public String toString() {
        return "[" + method + "]" + path + "(" + icon + ")";
    }
}
