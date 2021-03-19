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

/**
 * 用户消息
 */
public class NoticeInfo {
    /**
     * 用户昵称
     */
    private String NickName;

    /**
     * 用户头像
     */
    private String Avator;

    /**
     * 最新消息通知
     */
    private String NewMessage;

    /**
     * 最新消息时间
     */
    private String NewDate;

    /**
     * 跳转路径
     */
    private String MoveTo;

    public NoticeInfo() {

    }

    public NoticeInfo(String nickName, String avator, String newMessage, String newDate) {
        NickName = nickName;
        Avator = avator;
        NewMessage = newMessage;
        NewDate = newDate;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getAvator() {
        return Avator;
    }

    public void setAvator(String avator) {
        Avator = avator;
    }

    public String getNewMessage() {
        return NewMessage;
    }

    public void setNewMessage(String newMessage) {
        NewMessage = newMessage;
    }

    public String getNewDate() {
        return NewDate;
    }

    public void setNewDate(String newDate) {
        NewDate = newDate;
    }

    public String getMoveTo() {
        return MoveTo;
    }

    public void setMoveTo(String moveTo) {
        MoveTo = moveTo;
    }

    @Override
    public String toString() {
        return "NoticeInfo{" +
                "NickName='" + NickName + '\'' +
                ", Avator='" + Avator + '\'' +
                ", NewMessage='" + NewMessage + '\'' +
                ", NewDate='" + NewDate + '\'' +
                ", MoveTo='" + MoveTo + '\'' +
                '}';
    }


}
