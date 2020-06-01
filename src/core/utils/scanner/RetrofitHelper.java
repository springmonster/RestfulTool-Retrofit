/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: SpringHelper
  Author:   ZhangYuanSheng
  Date:     2020/5/28 21:08
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package core.utils.scanner;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import core.annotation.RetrofitHttpMethodAnnotation;
import core.beans.HttpMethod;
import core.beans.Request;
import core.utils.RestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class RetrofitHelper {

    @NotNull
    public static List<Request> getRetrofitRequestByModule(@NotNull Project project, @NotNull Module module) {
        List<Request> moduleList = new ArrayList<>(0);

        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (controllers.isEmpty()) {
            return moduleList;
        }

        for (PsiClass controllerClass : controllers) {
            List<Request> parentRequests = new ArrayList<>(0);
            List<Request> childrenRequests = new ArrayList<>();

            PsiMethod[] psiMethods = controllerClass.getMethods();
            for (PsiMethod psiMethod : psiMethods) {
                childrenRequests.addAll(getRequests(psiMethod));
            }
            if (parentRequests.isEmpty()) {
                moduleList.addAll(childrenRequests);
            } else {
                parentRequests.forEach(parentRequest -> childrenRequests.forEach(childrenRequest -> {
                    Request request = childrenRequest.copyWithParent(parentRequest);
                    moduleList.add(request);
                }));
            }
        }
        return moduleList;
    }

    /**
     * 获取所有的控制器类
     *
     * @param project project
     * @param module  module
     * @return Collection<PsiClass>
     */
    @NotNull
    private static List<PsiClass> getAllControllerClass(@NotNull Project project, @NotNull Module module) {
        List<PsiClass> psiClasses = new ArrayList<>();

        GlobalSearchScope moduleScope = RestUtil.getModuleScope(module);
        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "java", moduleScope);
        virtualFiles.forEach(new Consumer<VirtualFile>() {
            @Override
            public void accept(VirtualFile virtualFile) {
                Collection<PsiClass> psiClasses1 = JavaShortClassNameIndex.getInstance().get(virtualFile.getNameWithoutExtension(), project, moduleScope);
                if (psiClasses1 != null) {
                    Object[] objects = psiClasses1.toArray();
                    if (objects.length >= 1) {
                        psiClasses.add((PsiClass) objects[0]);
                        System.out.println(virtualFile.getName());
                        System.out.println((PsiClass) objects[0]);
                    }
                }
            }
        });

        return psiClasses;
    }

    /**
     * 获取注解中的参数，生成RequestBean
     *
     * @param annotation annotation
     * @return list
     * @see RetrofitHelper#getRequests(PsiMethod)
     */
    @NotNull
    private static List<Request> getRequests(@NotNull PsiAnnotation annotation, @Nullable PsiMethod psiMethod) {
        RetrofitHttpMethodAnnotation retrofitHttpMethodAnnotation = RetrofitHttpMethodAnnotation.getByQualifiedName(
                annotation.getQualifiedName()
        );
        if (retrofitHttpMethodAnnotation == null) {
            return Collections.emptyList();
        }
        Set<HttpMethod> methods = new HashSet<>();
        methods.add(retrofitHttpMethodAnnotation.getMethod());
        List<String> paths = new ArrayList<>();

        // 是否为隐式的path（未定义value或者path）
        boolean hasImplicitPath = true;
        List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            String name = attribute.getAttributeName();

            if (methods.contains(HttpMethod.REQUEST) && "method".equals(name)) {
                // method可能为数组
                Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
                if (value instanceof String) {
                    methods.add(HttpMethod.valueOf((String) value));
                } else if (value instanceof List) {
                    //noinspection unchecked,rawtypes
                    List<String> list = (List) value;
                    for (String item : list) {
                        if (item != null) {
                            item = item.substring(item.lastIndexOf(".") + 1);
                            methods.add(HttpMethod.valueOf(item));
                        }
                    }
                }
            }

            boolean flag = false;
            for (String path : new String[]{"value", "path"}) {
                if (path.equals(name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                continue;
            }
            Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
            if (value instanceof String) {
                paths.add(((String) value));
            } else if (value instanceof List) {
                //noinspection unchecked,rawtypes
                List<Object> list = (List) value;
                list.forEach(item -> paths.add((String) item));
            }
            hasImplicitPath = false;
        }
        if (hasImplicitPath) {
            if (psiMethod != null) {
                paths.add("/");
            }
        }

        List<Request> requests = new ArrayList<>(paths.size());

        paths.forEach(path -> {
            for (HttpMethod method : methods) {
                if (method.equals(HttpMethod.REQUEST) && methods.size() > 1) {
                    continue;
                }
                requests.add(new Request(
                        method,
                        path,
                        psiMethod
                ));
            }
        });
        return requests;
    }

    /**
     * 获取方法中的参数请求，生成RequestBean
     *
     * @param method Psi方法
     * @return list
     */
    @NotNull
    private static List<Request> getRequests(@NotNull PsiMethod method) {
        List<Request> requests = new ArrayList<>();
        PsiAnnotation[] annotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            requests.addAll(getRequests(annotation, method));
        }

        return requests;
    }

    enum Control {

        /**
         * <p>@Controller</p>
         */
        Controller("Controller"),

        /**
         * <p>@RestController</p>
         */
        RestController("RestController"),

        API("ApiDefinition");

        private final String name;

        Control(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
