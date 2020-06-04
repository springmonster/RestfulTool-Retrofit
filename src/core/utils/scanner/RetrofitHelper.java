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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.jetbrains.lang.dart.psi.DartClass;
import com.jetbrains.lang.dart.psi.DartComponent;
import com.jetbrains.lang.dart.psi.DartFile;
import com.jetbrains.lang.dart.psi.DartMetadata;
import core.annotation.FlutterHttpMethodAnnotation;
import core.beans.Request;
import core.utils.RestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.caches.KotlinShortNamesCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Charles.Kuang
 * @version 1.0
 */
public class RetrofitHelper {

    @NotNull
    public static List<Request> getRetrofitRequestByModule(@NotNull Project project, @NotNull Module module) {
        return getAllRetrofitDartClass(project, module);
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
                System.out.println(psiClassList.get(0));
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
                System.out.println(classList.get(0));
            }
        });

        return psiClassList;
    }

    @NotNull
    private static List<Request> getAllRetrofitDartClass(@NotNull Project project, @NotNull Module module) {

        List<Request> requests = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "dart");

        virtualFiles.forEach(virtualFile -> {
            DartFile psiFile = (DartFile) PsiUtilBase.getPsiFile(project, virtualFile);

            String text = psiFile.getText();


            if (text.contains("@RestApi()")) {
                DartClass dartClass = PsiTreeUtil.findChildOfType(psiFile, DartClass.class);
                List<DartComponent> methods = dartClass.getMethods();


                for (int i = 0; i < methods.size(); i++) {
                    DartComponent dartComponent = methods.get(i);
                    List<DartMetadata> metadataList = dartComponent.getMetadataList();

                    for (int j = 0; j < metadataList.size(); j++) {
                        DartMetadata dartMetadata = metadataList.get(j);

                        String httpMethod = dartMetadata.getFirstChild().getNextSibling().getText();

                        FlutterHttpMethodAnnotation byQualifiedName = FlutterHttpMethodAnnotation.getByQualifiedName(httpMethod);

                        if (byQualifiedName == null) {
                            continue;
                        }

                        PsiElement lastChild = dartMetadata.getLastChild();
                        String path = lastChild.getText().substring(2, lastChild.getTextLength() - 2);

                        System.out.println(httpMethod);
                        System.out.println(path);
                        System.out.println("------------");

                        requests.add(new Request(
                                byQualifiedName.getMethod(),
                                path,
                                dartComponent
                        ));
                    }
                    System.out.println("1111 " + methods.get(i).getName());
                }
            }
        });

        return requests;
    }
}
