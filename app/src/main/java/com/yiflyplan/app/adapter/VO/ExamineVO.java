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

import androidx.annotation.NonNull;

public class ExamineVO implements Serializable {

    private int applyStatus;
    private int applyUserId;
    private String applyUserName;
    private int id;
    private int operaUserId;
    private String operaUserName;
    private int organizationId;


    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public int getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(int applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperaUserId() {
        return operaUserId;
    }

    public void setOperaUserId(int operaUserId) {
        this.operaUserId = operaUserId;
    }

    public String getOperaUserName() {
        return operaUserName;
    }

    public void setOperaUserName(String operaUserName) {
        this.operaUserName = operaUserName;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    @NonNull
    @Override
    public String toString() {
        return "ExamineVO{" +
                "applyStatus=" + applyStatus +
                ", applyUserId='" + applyUserId + '\'' +
                ", applyUserName='" + applyUserName + '\'' +
                ", id='" + id + '\'' +
                ", operaUserId='" + operaUserId + '\'' +
                ", operaUserName='" + operaUserName + '\'' +
                ", organizationId='" + organizationId + '\'' +
                '}';
    }
}
