package core.annotation;

import core.beans.HttpMethod;
import org.jetbrains.annotations.Nullable;

/**
 * @author KuangHaochuan
 */
public enum FlutterHttpMethodAnnotation {

    /**
     * GET
     */
    GET("GET", HttpMethod.GET),

    /**
     * POST
     */
    POST("POST", HttpMethod.POST),

    /**
     * PUT
     */
    PUT("PUT", HttpMethod.PUT),

    /**
     * PATCH
     */
    PATCH("PATCH", HttpMethod.PATCH),

    /**
     * HEAD
     */
    HEAD("HEAD", HttpMethod.HEAD),

    /**
     * Delete
     */
    DELETE("DELETE", HttpMethod.DELETE);

    private final String qualifiedName;
    private final HttpMethod method;

    FlutterHttpMethodAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static FlutterHttpMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (FlutterHttpMethodAnnotation flutterHttpMethodAnnotation : FlutterHttpMethodAnnotation.values()) {
            if (flutterHttpMethodAnnotation.getQualifiedName().equals(qualifiedName)) {
                return flutterHttpMethodAnnotation;
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