package core.annotation;

import core.beans.HttpMethod;
import org.jetbrains.annotations.Nullable;

/**
 * @author KuangHaochuan
 */
public enum AndroidHttpMethodAnnotation {

    HEAD("retrofit2.http.HEAD", HttpMethod.HEAD),

    GET("retrofit2.http.GET", HttpMethod.GET),

    POST("retrofit2.http.POST", HttpMethod.POST),

    PUT("retrofit2.http.PUT", HttpMethod.PUT),

    PATCH("retrofit2.http.PATCH", HttpMethod.PATCH),

    DELETE("retrofit2.http.DELETE", HttpMethod.DELETE);

    private final String qualifiedName;
    private final HttpMethod method;

    AndroidHttpMethodAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static AndroidHttpMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (AndroidHttpMethodAnnotation androidHttpMethodAnnotation : AndroidHttpMethodAnnotation.values()) {
            if (androidHttpMethodAnnotation.getQualifiedName().equals(qualifiedName)) {
                return androidHttpMethodAnnotation;
            }
        }
        return null;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }
}