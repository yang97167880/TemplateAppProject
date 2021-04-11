/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.yiflyplan.app.utils;


import android.content.Context;

import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.data.BaseSPUtil;

/**
 * SharedPreferences管理工具基类
 *
 * @author xuexiang
 * @since 2018/11/27 下午5:16
 */
public final class SettingUtils extends BaseSPUtil {

    private SettingUtils(Context context) {
//        throw new UnsupportedOperationException("u can't instantiate me...");
        super(context);
    }

    private static volatile SettingUtils sInstance = null;

    private final String IS_USE_APP_THEME_KEY = "is_use_app_theme_key";

    private  final String IS_FIRST_OPEN_KEY = "is_first_open_key";

    private  final String IS_AGREE_PRIVACY_KEY = "is_agree_privacy_key";

    /**
     * 获取单例
     *
     * @return
     */
    public static SettingUtils getInstance() {
        if (sInstance == null) {
            synchronized (SettingUtils.class) {
                if (sInstance == null) {
                    sInstance = new SettingUtils(XUtil.getContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * 是否是第一次启动
     */
    public boolean isFirstOpen() {
        return getBoolean(IS_FIRST_OPEN_KEY, true);
    }

    /**
     * 设置是否是第一次启动
     */
    public void setIsFirstOpen(boolean isFirstOpen) {
        MMKVUtils.put(IS_FIRST_OPEN_KEY, isFirstOpen);
    }

    /**
     * @return 是否同意隐私政策
     */
//    public static boolean isAgreePrivacy() {
//        return getBoolean(IS_AGREE_PRIVACY_KEY, false);
//    }

//    public static void setIsAgreePrivacy(boolean isAgreePrivacy) {
//        MMKVUtils.put(IS_AGREE_PRIVACY_KEY, isAgreePrivacy);
//    }

    public boolean isUseAppTheme() {
        return getBoolean(IS_USE_APP_THEME_KEY, true);
    }

}
