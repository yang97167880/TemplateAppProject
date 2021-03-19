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

package com.yiflyplan.app.core.http;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.yiflyplan.app.MyApp;

public final class MyMessageBox {
    public static void showLongToast(String text) {
        showToast(MyApp.getContext(), text, Toast.LENGTH_LONG);
    }

    public static void showToast(String text) {
        showToast(MyApp.getContext(), text, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String text, int duration) {
        //显示一个简单的文本信息
        Toast toast = Toast.makeText(context, text, duration);
        //在屏幕中间显示
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
