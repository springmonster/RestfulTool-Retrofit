package core.annotation;

import core.beans.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ZhangYuanSheng
 */
public enum RetrofitHttpMethodAnnotation {

    /**
     * GetMapping
     */
    GET_MAPPING("retrofit2.http.GET", HttpMethod.GET),

    /**
     * PostMapping
     */
    POST_MAPPING("retrofit2.http.POST", HttpMethod.POST),

    /**
     * PutMapping
     */
    PUT_MAPPING("retrofit2.http.PUT", HttpMethod.PUT),

    /**
     * DeleteMapping
     */
    DELETE_MAPPING("retrofit2.http.DELETE", HttpMethod.DELETE),

    /**
     * PatchMapping
     */
    PATCH_MAPPING("retrofit2.http.PATCH", HttpMethod.PATCH),

    /**
     * RequestParam
     */
    REQUEST_PARAM("retrofit2.http.BODY", null);

    private final String qualifiedName;
    private final HttpMethod method;

    RetrofitHttpMethodAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static RetrofitHttpMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (RetrofitHttpMethodAnnotation retrofitHttpMethodAnnotation : RetrofitHttpMethodAnnotation.values()) {
            if (retrofitHttpMethodAnnotation.getQualifiedName().equals(qualifiedName)) {
                return retrofitHttpMethodAnnotation;
            }
        }
        return null;
    }

    @Nullable
    public static RetrofitHttpMethodAnnotation getByShortName(String requestMapping) {
        for (RetrofitHttpMethodAnnotation retrofitHttpMethodAnnotation : RetrofitHttpMethodAnnotation.values()) {
            if (retrofitHttpMethodAnnotation.getQualifiedName().endsWith(requestMapping)) {
                return retrofitHttpMethodAnnotation;
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

    @NotNull
    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }
}