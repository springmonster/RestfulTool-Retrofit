/**
 * @author KuangHaochuan
 */
package core.utils.scanner;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import core.annotation.AndroidHttpMethodAnnotation;
import core.beans.HttpMethod;
import core.beans.Request;
import core.utils.RestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.caches.KotlinShortNamesCache;

import java.util.*;

/**
 * @author KuangHaochuan
 * @version 1.0
 */
public class RetrofitHelperForAndroid {

    @NotNull
    public static List<Request> getRetrofitRequestByModule(@NotNull Project project, @NotNull Module module) {
        List<Request> moduleList = new ArrayList<>();

        List<PsiClass> controllers = getAllRetrofitJavaClass(project, module);
        controllers.addAll(getAllRetrofitKotlinClass(project, module));

        if (controllers.isEmpty()) {
            return moduleList;
        }

        for (PsiClass controllerClass : controllers) {
            List<Request> childrenRequests = new ArrayList<>();

            PsiMethod[] psiMethods = controllerClass.getMethods();
            for (PsiMethod psiMethod : psiMethods) {
                childrenRequests.addAll(getRequests(psiMethod));
            }
            moduleList.addAll(childrenRequests);
        }

        return moduleList;
    }

    /**
     * 获取所有的Retrofit Java
     *
     * @param project project
     * @param module  module
     * @return Collection<PsiClass>
     */
    @NotNull
    private static List<PsiClass> getAllRetrofitJavaClass(@NotNull Project project, @NotNull Module module) {
        GlobalSearchScope moduleScope = RestUtil.getModuleScope(module);

        List<PsiClass> psiClasses = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "java", moduleScope);

        virtualFiles.forEach(virtualFile -> {
            List<PsiClass> psiClassList = (List<PsiClass>) JavaShortClassNameIndex.getInstance().get(virtualFile.getNameWithoutExtension(), project, moduleScope);
            if (psiClassList != null && !psiClassList.isEmpty() && psiClassList.get(0).isInterface()) {
                psiClasses.add(psiClassList.get(0));
            }
        });

        return psiClasses;
    }

    /**
     * 获取所有的Retrofit Kotlin
     *
     * @param project project
     * @param module  module
     * @return Collection<PsiClass>
     */
    @NotNull
    private static List<PsiClass> getAllRetrofitKotlinClass(@NotNull Project project, @NotNull Module module) {
        GlobalSearchScope moduleScope = RestUtil.getModuleScope(module);

        List<PsiClass> psiClassList = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "kt", moduleScope);

        virtualFiles.forEach(virtualFile -> {
            PsiClass[] psiClasses = KotlinShortNamesCache.getInstance(project).getClassesByName(virtualFile.getNameWithoutExtension(), moduleScope);
            List<PsiClass> classList = Arrays.asList(psiClasses);
            if (!classList.isEmpty() && classList.get(0).isInterface()) {
                psiClassList.add(classList.get(0));
            }
        });

        return psiClassList;
    }

    /**
     * 获取注解中的参数，生成RequestBean
     *
     * @param annotation annotation
     * @return list
     * @see RetrofitHelperForAndroid#getRequests(PsiMethod)
     */
    @NotNull
    private static List<Request> getRequests(@NotNull PsiAnnotation annotation, @Nullable PsiMethod psiMethod) {
        AndroidHttpMethodAnnotation androidHttpMethodAnnotation = AndroidHttpMethodAnnotation.getByQualifiedName(
                annotation.getQualifiedName()
        );
        if (androidHttpMethodAnnotation == null) {
            return Collections.emptyList();
        }
        Set<HttpMethod> methods = new HashSet<>();
        methods.add(androidHttpMethodAnnotation.getMethod());

        List<String> values = getAnnotationAttributeValues(annotation, "value");
        List<Request> requests = new ArrayList<>(values.size());

        values.forEach(path -> {
            for (HttpMethod method : methods) {
                requests.add(new Request(
                        method,
                        path,
                        psiMethod
                ));
            }
        });
        return requests;
    }

    public static List<String> getAnnotationAttributeValues(PsiAnnotation annotation, String attr) {
        PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue(attr);

        List<String> values = new ArrayList<>();
        //只有注解
        //一个值 class com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
        //多个值  class com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl
        if (value instanceof PsiReferenceExpression) {
            PsiReferenceExpression expression = (PsiReferenceExpression) value;
            values.add(expression.getText());
        } else if (value instanceof PsiLiteralExpression) {
//            values.add(psiNameValuePair.getLiteralValue());
            values.add(((PsiLiteralExpression) value).getValue().toString());
        } else if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();

            for (PsiAnnotationMemberValue initializer : initializers) {
                values.add(initializer.getText().replaceAll("\\\"", ""));
            }
        }

        return values;
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
}