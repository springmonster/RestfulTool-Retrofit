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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import core.beans.Request;
import core.utils.scanner.RetrofitHelperForAndroid;
import core.utils.scanner.RetrofitHelperForFlutter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Author KuangHaochuan
 */
public class RestUtil {

    private static String BASE_URL = "http://localhost:8080";

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static GlobalSearchScope getModuleScope(@NotNull Module module) {
        return module.getModuleScope();
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
        // Android Retrofit RESTFul方式
        List<Request> retrofitRequestByModuleForAndroid = RetrofitHelperForAndroid.getRetrofitRequestByModule(project, module);
        if (!retrofitRequestByModuleForAndroid.isEmpty()) {
            return retrofitRequestByModuleForAndroid;
        }

        // Flutter  Retrofit RESTFul方式
        List<Request> retrofitRequestByModuleForFlutter = RetrofitHelperForFlutter.getRetrofitRequestByModule(project, module);
        if (!retrofitRequestByModuleForFlutter.isEmpty()) {
            return retrofitRequestByModuleForFlutter;
        }
        return Collections.emptyList();
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
}