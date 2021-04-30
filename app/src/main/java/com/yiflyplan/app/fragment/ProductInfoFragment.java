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

package com.yiflyplan.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.ProductVO;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.fragment.organization.components.ProductItemCirculationFragment;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

@Page(name = "物品信息")
public class ProductInfoFragment extends BaseFragment {

    @BindView(R.id.product_name)
    TextView productName;

    @BindView(R.id.product_from)
    TextView productFrom;

    @BindView(R.id.item_weight)
    TextView itemWeight;

    @BindView(R.id.item_package)
    TextView itemPackage;

    @BindView(R.id.item_pollution)
    TextView itemPollution;

    @BindView(R.id.item_create)
    TextView itemCreate;

    @BindView(R.id.item_createTime)
    TextView itemCreateTime;

    @BindView(R.id.item_updateName)
    TextView itemUpdateName;

    @BindView(R.id.item_updateTime)
    TextView itemUpdateTime;

    @BindView(R.id.item_Coding)
    TextView itemCoding;

    @BindView(R.id.item_infor)
    TextView itemInfor;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_info;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        assert bundle != null;
        ProductVO productVO = (ProductVO) bundle.getSerializable("productVO");
        if(productVO!=null){
            productName.setText(productVO.getItemTypeName());
            productFrom.setText(productVO.getOrganizationName() + " | " + productVO.getDepartmentName());
            itemWeight.setText(productVO.getItemWeight() + "g");
            itemPackage.setText(productVO.getBagTypeName());
            itemPollution.setText(productVO.getPollutionLevelName());
            itemCreate.setText(productVO.getCreateUserName());
            itemCreateTime.setText("创建时间：" + productVO.getCreateTime());
            itemUpdateName.setText(productVO.getUpdateUserName());
            itemUpdateTime.setText(productVO.getUpdateTime());
            itemCoding.setText(productVO.getItemCoding());
            itemInfor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNewPage(ProductItemCirculationFragment.class, "product", productVO);
                }
            });
        }
    }

    @Override
    protected TitleBar initTitle() {
        return super.initTitle();
    }
}
