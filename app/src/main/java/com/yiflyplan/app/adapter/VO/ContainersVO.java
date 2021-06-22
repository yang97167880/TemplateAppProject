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

public class ContainersVO implements Serializable {
    private int containerCapacity;
    private String containerEncoding;
    private String containerImei;
    private int containerTypeId;
    private String containerTypeName;
    private String currentLocation;
    private int id;
    private int organizationId;
    private String organizationName;
    private String remarks;
    private String temperatureHumidity;
    private int updateUserId;
    private String updateUserName;

    @Override
    public String toString() {
        return "ContainersVO{" +
                "containerCapacity=" + containerCapacity +
                ", containerEncoding='" + containerEncoding + '\'' +
                ", containerImei='" + containerImei + '\'' +
                ", containerTypeId='" + containerTypeId + '\'' +
                ", containerTypeName='" + containerTypeName + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", id='" + id + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", organizationName=" + organizationName +
                ", remarks=" + remarks +
                ", temperatureHumidity=" + temperatureHumidity +
                ", updateUserId=" + updateUserId +
                ", updateUserName=" + updateUserName +
                '}';
    }

    public int getContainerCapacity() {
        return containerCapacity;
    }

    public void setContainerCapacity(int containerCapacity) {
        this.containerCapacity = containerCapacity;
    }

    public String getContainerEncoding() {
        return containerEncoding;
    }

    public void setContainerEncoding(String containerEncoding) {
        this.containerEncoding = containerEncoding;
    }

    public String getContainerImei() {
        return containerImei;
    }

    public void setContainerImei(String containerImei) {
        this.containerImei = containerImei;
    }

    public int getContainerTypeId() {
        return containerTypeId;
    }

    public void setContainerTypeId(int containerTypeId) {
        this.containerTypeId = containerTypeId;
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTemperatureHumidity() {
        return temperatureHumidity;
    }

    public void setTemperatureHumidity(String temperatureHumidity) {
        this.temperatureHumidity = temperatureHumidity;
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
