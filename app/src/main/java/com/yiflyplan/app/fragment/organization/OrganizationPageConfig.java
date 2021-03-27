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

package com.yiflyplan.app.fragment.organization;

import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.model.PageInfo;
import com.yiflyplan.app.R;

import java.util.ArrayList;
import java.util.List;

public class OrganizationPageConfig {

    private static OrganizationPageConfig sInstance;

    private List<PageInfo> mPages;

    private List<PageInfo> mComponents;

    private List<PageInfo> mUtils;

    private List<PageInfo> mExpands;

    public OrganizationPageConfig(){
        mPages = new ArrayList<>();
        mComponents = new ArrayList<>();
        mUtils = new ArrayList<>();
        mExpands = new ArrayList<>();

        mComponents.add(new PageInfo("机构成员","com.yiflyplan.app.fragment.organization.components.OrganizationUser","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_member));
        mPages.add(new PageInfo("机构成员", "com.yiflyplan.app.fragment.organization.components.OrganizationUser", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_member));

        mComponents.add(new PageInfo("个人仓库","com.yiflyplan.app.fragment.organization.components.OrganizationUser","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_personal));
        mPages.add(new PageInfo("个人仓库", "com.yiflyplan.app.fragment.organization.components.PersonalWarehouse", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_personal));

        mComponents.add(new PageInfo("机构仓库","com.yiflyplan.app.fragment.organization.components.OrganizationWarehouse","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_warehouse));
        mPages.add(new PageInfo("机构仓库", "com.yiflyplan.app.fragment.organization.components.OrganizationWarehouse", "{\"\":\"\"}", CoreAnim.slide,  R.drawable.ic_warehouse));

        mComponents.add(new PageInfo("产品转移","com.yiflyplan.app.fragment.organization.components.Transfer","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
        mPages.add(new PageInfo("产品转移", "com.yiflyplan.app.fragment.organization.components.Transfer", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));

        mComponents.add(new PageInfo("产品接收","com.yiflyplan.app.fragment.organization.components.Receive","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
        mPages.add(new PageInfo("产品接收", "com.yiflyplan.app.fragment.organization.components.Receive", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));

        mComponents.add(new PageInfo("审核申请","com.yiflyplan.app.fragment.organization.components.Examine","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));
        mPages.add(new PageInfo("审核申请", "com.yiflyplan.app.fragment.organization.components.Examine", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));

        mComponents.add(new PageInfo("分享机构","com.yiflyplan.app.fragment.organization.components.Share","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_share));
        mPages.add(new PageInfo("分享机构", "com.yiflyplan.app.fragment.organization.components.Share", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_share));
    }

    public static OrganizationPageConfig getInstance() {
        if (sInstance == null) {
            synchronized (OrganizationPageConfig.class) {
                if (sInstance == null) {
                    sInstance = new OrganizationPageConfig();
                }
            }
        }
        return sInstance;
    }

    public List<PageInfo> getPages() {
        return mPages;
    }

    public List<PageInfo> getComponents() {
        return mComponents;
    }

    public List<PageInfo> getUtils() {
        return mUtils;
    }

    public List<PageInfo> getExpands() {
        return mExpands;
    }
}
