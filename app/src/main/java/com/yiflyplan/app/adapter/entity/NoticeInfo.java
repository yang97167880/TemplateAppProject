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
    private String Avatar;

    /**
     * 最新消息通知
     */
    private String NewMessage;

    /**
     * 最新消息时间
     */
    private String NewDate;

    /**
     * 未读数
     */
    private int UnreadCount;

    /**
     * 用户id
     */
    private int UserId;;



    public NoticeInfo() {

    }

    public NoticeInfo(String nickName, String avatar, String newMessage, String newDate,int unreadCount,int userId) {
        NickName = nickName;
        Avatar = avatar;
        NewMessage = newMessage;
        NewDate = newDate;
        UnreadCount = unreadCount;
        UserId = userId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
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

    public int getUnreadCount() {
        return UnreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        UnreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "NoticeInfo{" +
                "NickName='" + NickName + '\'' +
                ", Avatar='" + Avatar + '\'' +
                ", NewMessage='" + NewMessage + '\'' +
                ", NewDate='" + NewDate + '\'' +
                ", UnreadCount='" + UnreadCount + '\'' +
                '}';
    }


}
