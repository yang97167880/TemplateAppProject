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

package com.yiflyplan.app.adapter.VO;

import java.io.Serializable;
import java.security.Timestamp;

public class ProductCirculationVO implements Serializable {
    private String createTime;
    private String createUserAvatar;
    private int createUserId;
    private String createUserName;
    private int id;
    private int itemId;
    private int mainBodyId;
    private String mainBodyName;
    private	int mainBodyType;
    private	String operator;
    private int ownerId;
    private String ownerName;
    private String updateTime;
    private String updateUserAvatar;
    private int updateUserId;
    private String updateUserName;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserAvatar() {
        return createUserAvatar;
    }

    public void setCreateUserAvatar(String createUserAvatar) {
        this.createUserAvatar = createUserAvatar;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getMainBodyId() {
        return mainBodyId;
    }

    public void setMainBodyId(int mainBodyId) {
        this.mainBodyId = mainBodyId;
    }

    public String getMainBodyName() {
        return mainBodyName;
    }

    public void setMainBodyName(String mainBodyName) {
        this.mainBodyName = mainBodyName;
    }

    public int getMainBodyType() {
        return mainBodyType;
    }

    public void setMainBodyType(int mainBodyType) {
        this.mainBodyType = mainBodyType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserAvatar() {
        return updateUserAvatar;
    }

    public void setUpdateUserAvatar(String updateUserAvatar) {
        this.updateUserAvatar = updateUserAvatar;
    }

    public int getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(int updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }
}
