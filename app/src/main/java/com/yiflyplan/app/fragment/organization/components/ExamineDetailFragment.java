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

import android.view.View;

import com.xuexiang.xpage.annotation.Page;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;

@Page(name = "机构")
public class ExamineDetailFragment  extends BaseFragment implements View.OnClickListener{
    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_examine_detail;
    }

    @Override
    protected void initViews() {

    }
}
