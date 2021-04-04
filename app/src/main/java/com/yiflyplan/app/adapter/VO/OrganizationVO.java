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

public class OrganizationVO implements Serializable {


    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationAvatar() {
        return organizationAvatar;
    }

    public void setOrganizationAvatar(String organizationAvatar) {
        this.organizationAvatar = organizationAvatar;
    }

    public String getOrganizationCityId() {
        return organizationCityId;
    }

    public void setOrganizationCityId(String organizationCityId) {
        this.organizationCityId = organizationCityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(String organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public String getOrganizationAbbreviation() {
        return "编号：" + organizationAbbreviation;
    }

    public void setOrganizationAbbreviation(String organizationAbbreviation) {
        this.organizationAbbreviation = organizationAbbreviation;
    }

    public int getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(int organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public String getOrganizationTypeName() {
        return organizationTypeName;
    }

    public void setOrganizationTypeName(String organizationTypeName) {
        this.organizationTypeName = organizationTypeName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private int organizationId;

    private String organizationName;

    private String organizationAvatar;

    private String organizationCityId;

    private String cityName;

    private String organizationLevel;

    private String organizationAbbreviation;

    private int organizationTypeId;

    private String organizationTypeName;

    private String createTime;

    @Override
    public String toString() {
        return "OrganizationVO{" +
                "organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", organizationAvatar='" + organizationAvatar + '\'' +
                ", organizationCityId='" + organizationCityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", organizationLevel='" + organizationLevel + '\'' +
                ", organizationAbbreviation='" + organizationAbbreviation + '\'' +
                ", organizationTypeId=" + organizationTypeId +
                ", organizationTypeName='" + organizationTypeName + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
