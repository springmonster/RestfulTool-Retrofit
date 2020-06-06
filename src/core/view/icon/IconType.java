/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: IconType
  Author:   ZhangYuanSheng
  Date:     2020/5/31 01:22
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package core.view.icon;

import core.beans.HttpMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public interface IconType {

    /**
     * 默认显示
     *
     * @param method method
     * @return default
     */
    @NotNull
    Icon getDefaultIcon(HttpMethod method);

    /**
     * 图标名
     *
     * @return name
     */
    @Override
    @NotNull
    String toString();
}
