package core.annotation;

import core.beans.HttpMethod;
import org.jetbrains.annotations.Nullable;

/**
 * @author Charles.Kuang
 */
public enum FlutterHttpMethodAnnotation {

    /**
     * GetMapping
     */
    GET("GET", HttpMethod.GET),

    /**
     * PostMapping
     */
    POST("POST", HttpMethod.POST),

    /**
     * PutMapping
     */
    PUT("PUT", HttpMethod.PUT),

    /**
     * DeleteMapping
     */
    PATCH("PATCH", HttpMethod.PATCH),

    /**
     * PatchMapping
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