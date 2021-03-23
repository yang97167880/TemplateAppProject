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

import android.net.Uri;

public class OrganizationInfo  {

    private String title;

    private Uri image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public Uri getImage() {
        return image;
    }

    public OrganizationInfo(String title, int ic_menu_organization) {
        this.title = title;
    }

    public OrganizationInfo(String title, String image) {
        this.title = title;
        this.image = Uri.parse(image);
    }
}
