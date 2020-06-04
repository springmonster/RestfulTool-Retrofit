/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: AppSetting
  Author:   ZhangYuanSheng
  Date:     2020/5/27 18:27
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package core.beans;

import core.view.icon.IconTypeManager;
import core.view.icon.impl.DefaultIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class AppSetting {

    /**
     * 图标的类型具体实现类的className
     */
    @NotNull
    public String iconTypeClass = "";

    public void initValue() {
        this.iconTypeClass = IconTypeManager.formatClass(DefaultIcon.class);
    }

    public boolean isModified(@Nullable AppSetting setting) {
        if (setting == null) {
            return false;
        }
        return !this.iconTypeClass.equals(setting.iconTypeClass);
    }

    public void applySetting(@Nullable AppSetting setting) {
        if (setting == null) {
            return;
        }
        this.iconTypeClass = setting.iconTypeClass;
    }
}
