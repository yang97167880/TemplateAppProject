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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.organization.OrganizationFragment;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;

import static com.yiflyplan.app.utils.ImageConversionUtil.base64ToBitmap;

/**
 * 申请加入机构表单页
 */
@Page(name = "机构")
public class ApplyFormFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.or_avatar)
    RadiusImageView orAvatar;

    @BindView(R.id.or_name)
    TextView orName;

    @BindView(R.id.or_city)
    TextView orCity;

    @BindView(R.id.or_code)
    TextView orCode;

    @BindView(R.id.or_typeName)
    TextView orTypeName;

    @BindView(R.id.or_level)
    TextView orLevel;

    @BindView(R.id.btn_apply_or)
    Button btnApplyOr;

    private int organizationId;
    private Boolean belongsTo;
    private OrganizationVO organizationVO;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        organizationVO = (OrganizationVO) bundle.getSerializable("organization");
        organizationId = organizationVO.getOrganizationId();
        RadiusImageView radiusImageView = findViewById(R.id.or_avatar);
        GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
        lodeImg.loadImage(radiusImageView,organizationVO.getOrganizationAvatar());
        orName.setText(organizationVO.getOrganizationName());
        orCity.setText("城市："+organizationVO.getCityName());
        orCode.setText(organizationVO.getOrganizationAbbreviation());
        orTypeName.setText("机构类型："+organizationVO.getOrganizationTypeName());
        orLevel.setText("机构等级："+organizationVO.getOrganizationLevel());


        /**
         * 检查用户是否已经加入该机构
         */

        LinkedHashMap<String,String> params = new  LinkedHashMap<>();
        params.put("organizationId", String.valueOf(organizationId));
        MyHttp.get("/organization/checkUserBelongsToOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                belongsTo = data.getBoolean("belongsTo");
                if (belongsTo) {
                    btnApplyOr.setText(R.string.joined);
                    btnApplyOr.setEnabled(false);
                    btnApplyOr.setBackgroundResource(R.color.xui_btn_gray_select_color);
                }
            }

            @Override
            public void fail(JSONObject error) {

            }
        });
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnApplyOr.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apply_or:

                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("organizationId", String.valueOf(organizationId));
                MyHttp.postJson("/organization/applyToJoinOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
                    @Override
                    public void success(JSONObject data) throws JSONException {
                        btnApplyOr.setText(R.string.check_pending);
                        btnApplyOr.setEnabled(false);
                        btnApplyOr.setBackgroundResource(R.color.xui_btn_gray_select_color);

                    }

                    @Override
                    public void fail(JSONObject error) {
                    }
                });
                break;
            default:
        }
    }
}
