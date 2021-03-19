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

package com.yiflyplan.app.adapter.entity;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;

public class ChartInfo {
    /**
     * 用户头像
     */
    private int avatar;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 当前时间
     */
    private String time;

    /**
     * 位置
     */
    private int position;

    private MyRecyclerViewHolder holder;

    public ChartInfo(){}

    public ChartInfo(int avatar, String content,int position) {
        this.avatar = avatar;
        this.content = content;
        this.position = position;
    }

    public ChartInfo(int avatar, String content, String time, int position) {
        this.avatar = avatar;
        this.content = content;
        this.time = time;
        this.position = position;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    @Override
    public String toString() {
        return "ChartInfo{" +
                "avatar='" + avatar + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", posiation='" + position + '\'' +
                '}';
    }

}
