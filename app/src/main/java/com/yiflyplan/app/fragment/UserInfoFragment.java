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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.bundle.PersonalWareHouseBundle;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.notices.ChartRoomFragment;
import com.yiflyplan.app.fragment.organization.components.PersonalWarehouse;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.MapDataCache;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

@Page(name = "用户信息")
public class UserInfoFragment extends BaseFragment {

    @BindView(R.id.iv_avatar)
    ImageView ivUserAvatar;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    @BindView(R.id.tv_user_tel)
    TextView tvUserTel;

    @BindView(R.id.tv_user_rfid)
    TextView tvUserRfid;

    @BindView(R.id.tv_city_name)
    TextView tvCityName;

    @BindView(R.id.rl_ware_house)
    RelativeLayout llWareHouse;

    @BindView(R.id.btn_init_chat)
    Button btnInitChat;


    private int userId;
    private CurrentUserVO currentUserVO;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initViews() {

        userId = MMKVUtils.getInt("userId",0);

        Bundle bundle = getArguments();
        currentUserVO = (CurrentUserVO) bundle.getSerializable("currentUserVO");
        if (currentUserVO != null) {
            RadiusImageView radiusImageView = findViewById(R.id.iv_avatar);
            GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
            lodeImg.loadImage(radiusImageView, currentUserVO.getUserAvatar());

            tvUserName.setText(currentUserVO.getUserName());
            tvUserTel.setText(currentUserVO.getUserTel());
            tvCityName.setText(currentUserVO.getUserCityName());
            tvUserRfid.setText(currentUserVO.getRfid());
            llWareHouse.setOnClickListener(view -> {
                PersonalWareHouseBundle personalWareHouseBundle=new PersonalWareHouseBundle();
                int id = (int) MapDataCache.getCache("organizationId",null);
                personalWareHouseBundle.setOrganizationId(id);
                personalWareHouseBundle.setUserId(currentUserVO.getUserId());
                openNewPage(PersonalWarehouse.class,"personalWareHouseBundle",personalWareHouseBundle);
            });

            if (currentUserVO.getUserId() == userId){
                btnInitChat.setVisibility(View.GONE);
            }

            btnInitChat.setOnClickListener(view -> {
                openNewPage(ChartRoomFragment.class,"currentUserVO", currentUserVO);
            });
        }
    }

    @Override
    protected TitleBar initTitle() {
        return super.initTitle();

    }


}
