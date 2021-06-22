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

public class TransitBoxVO implements Serializable {
    private int id;
    private int transitBoxCurrentWeight;
    private boolean transitBoxSaturationState;
    private int transitBoxTypeId;
    private String transitBoxTypeName;
    private String transitEncoding;
    private Number weightRatio;

    @Override
    public String toString() {
        return "TransitBoxVO{" +
                "id=" + id +
                ", transitBoxCurrentWeight='" + transitBoxCurrentWeight + '\'' +
                ", transitBoxSaturationState='" + transitBoxSaturationState + '\'' +
                ", transitBoxTypeId='" + transitBoxTypeId + '\'' +
                ", transitBoxTypeName='" + transitBoxTypeName + '\'' +
                ", transitEncoding='" + transitEncoding + '\'' +
                ", weightRatio=" + weightRatio +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransitBoxCurrentWeight() {
        return transitBoxCurrentWeight;
    }

    public void setTransitBoxCurrentWeight(int transitBoxCurrentWeight) {
        this.transitBoxCurrentWeight = transitBoxCurrentWeight;
    }

    public boolean isTransitBoxSaturationState() {
        return transitBoxSaturationState;
    }

    public void setTransitBoxSaturationState(boolean transitBoxSaturationState) {
        this.transitBoxSaturationState = transitBoxSaturationState;
    }

    public int getTransitBoxTypeId() {
        return transitBoxTypeId;
    }

    public void setTransitBoxTypeId(int transitBoxTypeId) {
        this.transitBoxTypeId = transitBoxTypeId;
    }

    public String getTransitBoxTypeName() {
        return transitBoxTypeName;
    }

    public void setTransitBoxTypeName(String transitBoxTypeName) {
        this.transitBoxTypeName = transitBoxTypeName;
    }

    public String getTransitEncoding() {
        return transitEncoding;
    }

    public void setTransitEncoding(String transitEncoding) {
        this.transitEncoding = transitEncoding;
    }

    public Number getWeightRatio() {
        return weightRatio;
    }

    public void setWeightRatio(Number weightRatio) {
        this.weightRatio = weightRatio;
    }

}
