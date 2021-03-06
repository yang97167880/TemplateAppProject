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

package com.yiflyplan.app.activity;

import android.util.Log;
import android.view.KeyEvent;

import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.SettingUtils;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 启动页【无需适配屏幕大小】
 *
 * @author xuexiang
 * @since 2019-06-30 17:32
 */
public class SplashActivity extends BaseSplashActivity implements CancelAdapt {

    public final static String KEY_IS_DISPLAY = "key_is_display";
    public final static String KEY_ENABLE_ALPHA_ANIM = "key_enable_alpha_anim";

    public final static String DATA = "data";
    private static final String TOKEN_TIMEOUT = "token已过期";
    private static final String HAS_TOKEN = "未过期";

    private boolean isDisplay = false;
    @Override
    protected long getSplashDurationMillis() {
        return 500;
    }

    /**
     * activity启动后的初始化
     */
    @Override
    protected void onCreateActivity() {
        initSplashView(R.drawable.xui_config_bg_splash);
        startSplash(false);
    }


    /**
     * 启动页结束后的动作
     */
    @Override
    protected void onSplashFinished() {
        loginOrGoMainPage();
    }

    private void loginOrGoMainPage() {
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("token",TokenUtils.getToken());
        MyHttp.postJson("/user/checkToken",TokenUtils.getToken(),params,new MyHttp.Callback(){

            @Override
            public void success(JSONObject data) throws JSONException {
                    if (TokenUtils.hasToken()&&data.getString(DATA).equals(HAS_TOKEN )) {
                        ActivityUtils.startActivity(MainActivity.class);
                    } else {
                        ActivityUtils.startActivity(LoginActivity.class);
                    }
                    finish();
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });

    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }
}
