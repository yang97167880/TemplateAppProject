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

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.VO.ProductVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.organization.components.ApplyFormFragment;
import com.yiflyplan.app.fragment.organization.components.ComponentsFragment;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * 搜索机构页面
 * */
@Page(name = "查找机构")
public class SearchOrganizationFragment extends BaseFragment implements View.OnClickListener, TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.organization_recyclerView)
    RecyclerView recyclerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.organization_refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.edt_search_organization)
    EditText edtSearchOrganization;


    @BindView(R.id.btn_search_or)
    Button btnSearchOr;

    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 5;
    private List<OrganizationVO> organizationVOS = new ArrayList<>();
    private final String RELATIONSHIPS = "relationships";
    private BroccoliSimpleDelegateAdapter<OrganizationVO> mOrganizationAdapter;
    private String organizationInfo;
    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_organization_list;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);
        mOrganizationAdapter = new BroccoliSimpleDelegateAdapter<OrganizationVO>(R.layout.adapter_search_organization_list_item, new LinearLayoutHelper()) {

            @Override
            protected void onBindData(MyRecyclerViewHolder holder, OrganizationVO model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                       name.setText(model.getOrganizationName());
                    },R.id.or_name);
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        //设置头像
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getOrganizationAvatar()));
                    },R.id.or_avatar);
                    holder.bindDataToViewById(view -> {
                        TextView code = (TextView) view;
                        code.setText(model.getOrganizationAbbreviation());
                    },R.id.or_code);
                    holder.bindDataToViewById(view -> {
                        TextView level = (TextView) view;
                        level.setText(model.getOrganizationLevel());
                    },R.id.or_level);
                    holder.click(R.id.card_view,v ->{
                        openNewPage(ApplyFormFragment.class,"organization",model);
                    });
                }

            }


            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.card_view)
                );
            }
        };
//
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mOrganizationAdapter);
//
        recyclerView.setAdapter(delegateAdapter);
    }


    @Override
    protected void initListeners() {

        btnSearchOr.setOnClickListener(this);
        edtSearchOrganization.addTextChangedListener(this);



        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                organizationInfo = edtSearchOrganization.getText().toString();
                if(pageNo == 1 && organizationInfo.length()!=0){
                    apiGetOrganizationVOList("once");
                }else {
                    organizationVOS.clear();
                    mOrganizationAdapter.refresh(organizationVOS);
                }
                refreshLayout.finishRefresh();
            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                pageNo += 1;
                apiGetOrganizationVOList("more");
                refreshLayout.finishLoadMore();
            }, 500);
        });
    }

    protected void apiGetOrganizationVOList(String flag) {
        LinkedHashMap<String,String> params = new  LinkedHashMap<>();
        params.put("pageNo",String.valueOf(pageNo));
        params.put("pageSize",String.valueOf(pageSize));
        params.put("searchKey",organizationInfo);
            if(pageNo>1 && flag == "refresh"){
                mOrganizationAdapter.refresh(organizationVOS);
            }else{
                MyHttp.postJson("/organization/getAllOrganizationBaseInfo", TokenUtils.getToken(), params, new MyHttp.Callback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void success(JSONObject data) throws JSONException {
                        JSONArray organizationBaseInfo = new JSONArray(data.getString("list"));
                        List<OrganizationVO> voList = new ArrayList<>();
                        voList = ReflectUtil.convertToList(organizationBaseInfo, OrganizationVO.class);
                        totalPage = data.getInt("totalPage");

                        if(organizationBaseInfo.length()==0 && organizationInfo.length()!=0)
                        {
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            tvNoData.setVisibility(View.INVISIBLE);
                        }

                        switch(flag){
                            case "once":
                                organizationVOS.clear();
                                organizationVOS.addAll(voList);
                                break;
                            case "more":
                                if (pageNo <= totalPage) {
                                    organizationVOS.addAll(voList);
                                    mOrganizationAdapter.loadMore(voList);
                                 }
                                break;
                        }
                        mOrganizationAdapter.refresh(organizationVOS);
                    }
                    @Override
                    public void fail(JSONObject error) {
                        refreshLayout.finishRefresh();
                    }
                });
            }

        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_or:
                organizationInfo = edtSearchOrganization.getText().toString();
                if(organizationInfo.length()==0){
                    XToastUtils.toast("请输入您想查找的内容");
                }else {
                    apiGetOrganizationVOList("once");
                    refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
                }

                break;
            default:

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        organizationInfo = s.toString();
        if(s.length()==0){
            organizationVOS.clear();
            mOrganizationAdapter.refresh(organizationVOS);
            tvNoData.setVisibility(View.INVISIBLE);
        }else {
            apiGetOrganizationVOList("once");
            refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}

