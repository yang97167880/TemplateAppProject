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

import androidx.annotation.Nullable;

public class DeviceEntity {
    private String name;
    private String address;

    public DeviceEntity(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name==null||name.trim().length()==0?"Unknown Device":name;
    }


    public String getAddress() {
        return address;
    }



    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null){return false;}
        if (obj instanceof DeviceEntity) {
            DeviceEntity entity = (DeviceEntity) obj;
            if (entity.getName() == null || entity.getAddress() == null) {return false;}
            if (entity.getName().trim().length()<=0||entity.getAddress().trim().length()<=0){return false;}
            return entity.getName().equals(this.getName())
                    && entity.getAddress().equals(this.getAddress());
        }
        return false;

    }
}
