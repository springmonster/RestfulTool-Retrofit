package core.beans;

import com.intellij.psi.PsiMethod;
import com.jetbrains.lang.dart.psi.DartComponent;
import core.view.icon.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public class Request {
    
    private HttpMethod method;
    private String path;
    private Icon icon;
    
    private PsiMethod psiMethod;
    private DartComponent dartComponent;
    
    public Request(HttpMethod method, @Nullable String path, @Nullable PsiMethod psiMethod) {
        this.setMethod(method);
        if (path != null) {
            this.setPath(path);
        }
        this.psiMethod = psiMethod;
    }
    
    public Request(HttpMethod method, @Nullable String path, @Nullable DartComponent dartComponent) {
        this.setMethod(method);
        if (path != null) {
            this.setPath(path);
        }
        this.dartComponent = dartComponent;
    }
    
    public void navigate(boolean requestFocus) {
        if (psiMethod != null) {
            psiMethod.navigate(requestFocus);
        }
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
