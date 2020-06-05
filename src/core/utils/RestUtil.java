/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: RestUtil
  Author:   ZhangYuanSheng
  Date:     2020/5/4 15:14
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package core.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.intellij.lang.jvm.annotation.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import core.beans.HttpMethod;
import core.beans.Request;
import core.utils.scanner.RetrofitHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.caches.KotlinShortNamesCache;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class RestUtil {

    private static final int REQUEST_TIMEOUT = 1000 * 10;
    private static String BASE_URL = "http://localhost:8080";

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * 发送http请求
     *
     * @param method 请求方式
     * @param url    地址
     * @param head   请求头
     * @param body   请求体
     * @return 返回结果
     */
    public static String sendRequest(HttpMethod method, String url, String head, String body) {
        String resp;
        try {
            HttpRequest request = HttpUtil.createRequest(Method.valueOf(method.name()), url);

            if (head != null && !"".equals(head.trim())) {
                tempDataCoverToMap(head).forEach(request::header);
            }
            if (body != null && !"".equals(body.trim())) {
                tempDataCoverToMap(body).forEach(request::form);
            }

            resp = request.timeout(REQUEST_TIMEOUT).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
        }
        return resp;
    }

    @NotNull
    @Contract(pure = true)
    public static Map<String, String> tempDataCoverToMap(String tempData) {
        Map<String, String> map = new HashMap<>();

        if (tempData != null && !"".equals((tempData = tempData.trim()))) {
            String[] items = tempData.split("\n");
            for (String item : items) {
                String[] data = item.split(":");
                if (data.length == 2) {
                    map.put(data[0].trim(), data[1].trim());
                }
            }
        }

        return map;
    }

    /**
     * 获取所有的Request
     *
     * @param project project
     * @return map-{key: moduleName, value: itemRequestList}
     */
    @NotNull
    public static Map<String, List<Request>> getAllRequest(@NotNull Project project) {
        return getAllRequest(project, false);
    }

    /**
     * 获取所有的Request
     *
     * @param hasEmpty 是否生成包含空Request的moduleName
     * @param project  project
     * @return map-{key: moduleName, value: itemRequestList}
     */
    @NotNull
    public static Map<String, List<Request>> getAllRequest(@NotNull Project project, boolean hasEmpty) {
        Map<String, List<Request>> map = new HashMap<>();

        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<Request> requests = getAllRequestByModule(project, module);
            if (!hasEmpty && requests.isEmpty()) {
                continue;
            }
            map.put(module.getName(), requests);
        }
        return map;
    }

    /**
     * 获取选中module的所有Request
     *
     * @param project project
     * @param module  module
     * @return list
     */
    @NotNull
    public static List<Request> getAllRequestByModule(@NotNull Project project, @NotNull Module module) {
        // Retrofit RESTFul方式
        List<Request> retrofitRequestByModule = RetrofitHelper.getRetrofitRequestByModule(project, module);
        if (!retrofitRequestByModule.isEmpty()) {
            return retrofitRequestByModule;
        }
        return Collections.emptyList();
    }

    /**
     * 获取方法参数
     *
     * @param method method
     */
    @NotNull
    public static String getRequestParamsTempData(@NotNull PsiMethod method) {
        StringBuilder tempData = new StringBuilder();

        PsiParameterList parameterList = method.getParameterList();
        if (!parameterList.isEmpty()) {
            for (PsiParameter parameter : parameterList.getParameters()) {
                PsiAnnotation[] parameterAnnotations = parameter.getAnnotations();
                String parameterName = parameter.getName();
                PsiType parameterType = parameter.getType();

                boolean flag = true;

                for (PsiAnnotation parameterAnnotation : parameterAnnotations) {
                    List<JvmAnnotationAttribute> attributes = parameterAnnotation.getAttributes();
                    for (JvmAnnotationAttribute attribute : attributes) {
                        String name = attribute.getAttributeName();
                        if (!("name".equals(name) || "value".equals(name))) {
                            continue;
                        }
                        Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
                        if (value instanceof String) {
                            flag = !flag;
                        }
                    }
                }

                Object data = RestUtil.getTypeDefaultData(method, parameterType);

                if (data != null) {
                    tempData.append(data).append("\n");
                }
            }
        }
        return tempData.toString();
    }

    /**
     * 获取拼接的url
     *
     * @param baseUrl
     * @param path
     * @return
     */
    @NotNull
    public static String getRequestUrl(String baseUrl, String path) {
        StringBuilder url = new StringBuilder();
        if (!path.startsWith("http") && !path.startsWith("https")) {
            url.append(baseUrl);
            if (!path.startsWith("/")) {
                url.append("/");
            }
        }
        url.append(path);
        return url.toString();
    }

    public static GlobalSearchScope getModuleScope(@NotNull Module module) {
        return module.getModuleScope();
    }

    @Nullable
    private static Object getTypeDefaultData(@NotNull PsiMethod method, PsiType parameterType) {
        if (parameterType instanceof PsiArrayType) {
            return "[]";
        } else if (parameterType instanceof PsiClassReferenceType) {
            // Object | String | Integer | List<?> | Map<K, V>
            PsiClassReferenceType type = (PsiClassReferenceType) parameterType;

            GlobalSearchScope resolveScope = type.getResolveScope();
            PsiFile[] psiFilesJava = FilenameIndex.getFilesByName(
                    method.getProject(),
                    type.getName() + ".java",
                    resolveScope
            );

            PsiFile[] psiFilesKotlin = FilenameIndex.getFilesByName(
                    method.getProject(),
                    type.getName() + ".kt",
                    resolveScope
            );

            if (psiFilesJava.length > 0) {
                for (PsiFile psiFile : psiFilesJava) {
                    if (psiFile instanceof PsiJavaFile) {
                        PsiClass[] fileClasses = ((PsiJavaFile) psiFile).getClasses();
                        StringBuilder item = new StringBuilder();
                        for (PsiClass psiClass : fileClasses) {
                            if (type.getReference().getQualifiedName().equals(psiClass.getQualifiedName())) {
                                PsiField[] fields = psiClass.getFields();
                                for (PsiField field : fields) {
                                    String fieldName = field.getName();
                                    for (int i = 0; i < field.getAnnotations().length; i++) {
                                        for (int i1 = 0; i1 < field.getAnnotations()[i].getAttributes().size(); i1++) {
                                            fieldName = field.getAnnotations()[i].getParameterList().getAttributes()[0].getLiteralValue();
                                            break;
                                        }
                                    }
                                    Object defaultData = getTypeDefaultData(method, field.getType());
                                    item.append(fieldName).append(": ").append(defaultData).append("\n");
                                }
                                break;
                            }
                        }
                        return item.toString();
                    }
                }
            } else if (psiFilesKotlin.length > 0) {
                @NotNull PsiClass[] psiClasses = KotlinShortNamesCache.getInstance(method.getProject()).getClassesByName(type.getName(), resolveScope);

                StringBuilder item = new StringBuilder();

                for (PsiClass psiClass : psiClasses) {
                    if (type.getReference().getQualifiedName().equals(psiClass.getQualifiedName())) {
                        PsiField[] fields = psiClass.getFields();
                        for (PsiField field : fields) {
                            String fieldName = field.getName();
                            for (int i = 0; i < field.getAnnotations().length; i++) {
                                for (int i1 = 0; i1 < field.getAnnotations()[i].getAttributes().size(); i1++) {
                                    fieldName = field.getAnnotations()[i].getParameterList().getAttributes()[0].getLiteralValue();
                                    break;
                                }
                            }
                            Object defaultData = getTypeDefaultData(method, field.getType());
                            item.append(fieldName).append(": ").append(defaultData).append("\n");
                        }
                        break;
                    }
                }
                return item.toString();
            }
        } else if (parameterType instanceof PsiPrimitiveType) {
            // int | char | boolean
            PsiPrimitiveType type = (PsiPrimitiveType) parameterType;
            return getDefaultData(type.getName());
        }
        return null;
    }

    @Contract(pure = true)
    private static Object getDefaultData(@NotNull String classType) {
        Object data = null;
        switch (classType) {
            case "String":
                data = "demoData";
                break;
            case "char":
            case "Char":
                data = 'A';
                break;
            case "byte":
            case "short":
            case "int":
            case "long":
            case "Byte":
            case "Short":
            case "Integer":
            case "Long":
                data = 0;
                break;
            case "float":
            case "double":
            case "Float":
            case "Double":
                data = 0.0;
                break;
            case "boolean":
            case "Boolean":
                data = true;
                break;
            default:
                break;
        }
        return data;
    }

    /**
     * 获取属性值
     *
     * @param attributeValue Psi属性
     * @return {Object | List}
     */
    @Nullable
    public static Object getAttributeValue(JvmAnnotationAttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        if (attributeValue instanceof JvmAnnotationConstantValue) {
            return ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
        } else if (attributeValue instanceof JvmAnnotationEnumFieldValue) {
            return ((JvmAnnotationEnumFieldValue) attributeValue).getFieldName();
        } else if (attributeValue instanceof JvmAnnotationArrayValue) {
            List<JvmAnnotationAttributeValue> values = ((JvmAnnotationArrayValue) attributeValue).getValues();
            List<Object> list = new ArrayList<>(values.size());
            for (JvmAnnotationAttributeValue value : values) {
                Object o = getAttributeValue(value);
                if (o != null) {
                    list.add(o);
                } else {
                    // 如果是jar包里的JvmAnnotationConstantValue则无法正常获取值
                    try {
                        Class<? extends JvmAnnotationAttributeValue> clazz = value.getClass();
                        Field myElement = clazz.getSuperclass().getDeclaredField("myElement");
                        myElement.setAccessible(true);
                        Object elObj = myElement.get(value);
                        if (elObj instanceof PsiExpression) {
                            PsiExpression expression = (PsiExpression) elObj;
                            list.add(expression.getText());
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
            return list;
        } else if (attributeValue instanceof JvmAnnotationClassValue) {
            return ((JvmAnnotationClassValue) attributeValue).getQualifiedName();
        }
        return null;
    }
}