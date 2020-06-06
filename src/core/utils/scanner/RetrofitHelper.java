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
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.jetbrains.lang.dart.psi.DartClass;
import com.jetbrains.lang.dart.psi.DartComponent;
import com.jetbrains.lang.dart.psi.DartFile;
import com.jetbrains.lang.dart.psi.DartMetadata;
import core.annotation.FlutterHttpMethodAnnotation;
import core.beans.Request;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

                        requests.add(new Request(
                                byQualifiedName.getMethod(),
                                path,
                                dartComponent
                        ));
                    }
                }
            }
        });

        return requests;
    }
}
