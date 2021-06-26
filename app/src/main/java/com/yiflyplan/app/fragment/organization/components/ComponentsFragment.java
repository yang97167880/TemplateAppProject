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

package com.yiflyplan.app.fragment.organization.components;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.model.PageInfo;
import com.yiflyplan.app.fragment.organization.BaseHomeFragment;
import com.yiflyplan.app.fragment.organization.OrganizationPageConfig;

import java.util.List;

@Page(name = " ")
public class ComponentsFragment extends BaseHomeFragment {

    @Override
    protected List<PageInfo> getPageContents() {
        OrganizationPageConfig organizationPageConfig = new OrganizationPageConfig();
        return OrganizationPageConfig.getInstance().getComponents();
    }

}
