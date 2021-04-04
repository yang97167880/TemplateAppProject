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

public class ProductVO implements Serializable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(int typeId) {
        this.itemTypeId = typeId;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public int getPollutionLevelId() {
        return pollutionLevelId;
    }

    public void setPollutionLevelId(int pollutionLevelId) {
        this.pollutionLevelId = pollutionLevelId;
    }

    public String getPollutionLevelName() {
        return pollutionLevelName;
    }

    public void setPollutionLevelName(String pollutionLevelName) {
        this.pollutionLevelName = pollutionLevelName;
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

    public int getBagTypeId() {
        return bagTypeId;
    }

    public void setBagTypeId(int bagTypeId) {
        this.bagTypeId = bagTypeId;
    }

    public String getBagTypeName() {
        return bagTypeName;
    }

    public void setBagTypeName(String bagTypeName) {
        this.bagTypeName = bagTypeName;
    }

    public int getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(int itemWeight) {
        this.itemWeight = itemWeight;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getItemCoding() {
        return itemCoding;
    }

    public void setItemCoding(String itemCoding) {
        this.itemCoding = itemCoding;
    }

    public int getCurrentTransitId() {
        return currentTransitId;
    }

    public void setCurrentTransitId(int currentTransitId) {
        this.currentTransitId = currentTransitId;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ProductVO{" +
                "id=" + id +
                ", itemTypeId=" + itemTypeId +
                ", itemTypeName='" + itemTypeId + '\'' +
                ", pollutionLevelId=" + pollutionLevelId +
                ", pollutionLevelName='" + pollutionLevelName + '\'' +
                ", organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", bagTypeId=" + bagTypeId +
                ", bagTypeName='" + bagTypeName + '\'' +
                ", itemWeight=" + itemWeight +
                ", departmentName='" + departmentName + '\'' +
                ", itemCoding='" + itemCoding + '\'' +
                ", currentTransitId=" + currentTransitId +
                ", createUserId=" + createUserId +
                ", createUserName='" + createUserName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateUserId=" + updateUserId +
                ", updateUserName='" + updateUserName + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }

    private int id;

     private int itemTypeId;

     private String itemTypeName;

     private int pollutionLevelId;

     private String pollutionLevelName;

     private int organizationId;

     private String organizationName;

     private int bagTypeId;

     private String bagTypeName;

     private int itemWeight;

     private String departmentName;

     private String itemCoding;

    private int currentTransitId;

    private int createUserId;

    private String createUserName;

    private String createTime;

    private int updateUserId;

    private String updateUserName;

    private String updateTime;

}
