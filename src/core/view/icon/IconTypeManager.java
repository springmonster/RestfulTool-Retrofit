/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: IconManager
  Author:   ZhangYuanSheng
  Date:     2020/5/31 03:40
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package core.view.icon;

import core.view.icon.impl.DefaultIcon;

/**
 * @version 1.0
 * @Author KuangHaochuan
 */
public class IconTypeManager {

    private static final DefaultIcon DEFAULT_ICON = new DefaultIcon();

    public static IconType getInstance() {
        return DEFAULT_ICON;
    }
}
