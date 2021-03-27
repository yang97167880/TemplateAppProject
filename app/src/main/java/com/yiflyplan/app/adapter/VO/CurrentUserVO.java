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

    @Override
    public String toString() {
        return "CurrentUserVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", tel='" + tel + '\'' +
                ", cityId='" + cityId + '\'' +
                ", relationships='" + relationships + '\'' +
                ", currentOrganization='" + currentOrganization + '\'' +
                '}';
    }

    private int id;

    private String name;

    private String avatar;

    private String tel;

    private String cityId;

    private List<OrganizationVO> relationships;

    private OrganizationVO currentOrganization;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
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


}
