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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ExamineVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.core.BaseFragment;

import butterknife.BindView;

@Page(name = "加入申请")
public class ExamineDetailFragment  extends BaseFragment implements View.OnClickListener{

    @BindView(R.id.member_avatar)
    RadiusImageView memberAvatar;

    @BindView(R.id.member_name)
    TextView memberName;

    @BindView(R.id.txt_refused)
    TextView txtRefused;

    @BindView(R.id.refused_join_or)
    Button refusedJoinOr;

    @BindView(R.id.agree_join_or)
    Button agreeJoinOr;

    @BindView(R.id.btn_ll)
    LinearLayout linearLayout;

    private ExamineVO examineVO;
    private int ApplyStatus;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_examine_detail;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        if(bundle!=null){
            examineVO = (ExamineVO) bundle.getSerializable("ExamineUserInfo");
            if (examineVO!=null){
                memberName.setText(examineVO.getApplyUserName());
                ApplyStatus = examineVO.getApplyStatus();
                if(ApplyStatus == 2){
                    linearLayout.setVisibility(View.GONE);
                    txtRefused.setText("已同意申请");
                }
            }

        }




    }

    @Override
    protected void initListeners() {
        super.initListeners();
    }

    @Override
    public void onClick(View v) {

    }

}
