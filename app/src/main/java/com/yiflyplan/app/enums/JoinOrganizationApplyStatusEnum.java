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

package com.yiflyplan.app.enums;

public enum JoinOrganizationApplyStatusEnum {
    REJECT(0, "未通过"),
    UNDER_REVIEW(1, "审核中"),
    APPROVE(2, "已同意"),
    UNKNOWN(3, "异常");
    private final Integer statusCode;
    private final String text;

    JoinOrganizationApplyStatusEnum(Integer statusCode, String text) {
        this.statusCode = statusCode;
        this.text = text;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getText() {
        return text;
    }

    public static String parse(Integer code) {
        for (JoinOrganizationApplyStatusEnum value : JoinOrganizationApplyStatusEnum.values()) {
            if (value.getStatusCode().equals(code)) {
                return value.getText();
            }
        }
        return UNKNOWN.getText();
    }
}
