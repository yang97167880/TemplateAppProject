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
import java.util.List;

public class CurrentUserVO implements Serializable {

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getUserCityId() {
        return userCityId;
    }

    public void setUserCityId(String userCityId) {
        this.userCityId = userCityId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public List<OrganizationVO> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<OrganizationVO> relationships) {
        this.relationships = relationships;
    }

    public OrganizationVO getCurrentOrganization() {
        return currentOrganization;
    }

    public void setCurrentOrganization(OrganizationVO currentOrganization) {
        this.currentOrganization = currentOrganization;
    }

    @Override
    public String toString() {
        return "CurrentUserVO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", userTel='" + userTel + '\'' +
                ", userCityId='" + userCityId + '\'' +
                ", platform='" + platform + '\'' +
                ", rfid='" + rfid + '\'' +
                '}';
    }

    private int userId;

    private String userName;

    private String userAvatar;

    private String userTel;

    private String userCityId;

    private String platform;

    private String rfid;

    private List<OrganizationVO> relationships;

    private OrganizationVO currentOrganization;

}
