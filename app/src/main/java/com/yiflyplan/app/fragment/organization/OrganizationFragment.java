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

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.organization.components.ComponentsFragment;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * 机构主页
 */
public class OrganizationFragment extends BaseFragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.organization_recyclerView)
    RecyclerView recyclerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.organization_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 15;
    private List<OrganizationVO> organizationVOS = new ArrayList<>();
    private final String RELATIONSHIPS = "relationships";
    private BroccoliSimpleDelegateAdapter<OrganizationVO> mOrganizationAdapter;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_organization_list;
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


        mOrganizationAdapter = new BroccoliSimpleDelegateAdapter<OrganizationVO>(R.layout.adapter_organization_list_item, new LinearLayoutHelper()) {

            @Override
            protected void onBindData(MyRecyclerViewHolder holder, OrganizationVO model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        TextView typeName = (TextView) view;
                        typeName.setText(model.getOrganizationTypeName());
                    }, R.id.or_typeName);
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getOrganizationName());
                    }, R.id.or_name);
                    holder.bindDataToViewById(view -> {
                        TextView time = (TextView) view;
                        time.setText(model.getCreateTime());
                        time.setVisibility(View.GONE);
                    }, R.id.or_time);
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        //设置头像
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getOrganizationAvatar()));

                    }, R.id.or_avatar);
                    holder.bindDataToViewById(view -> {
                        TextView code = (TextView) view;
                        code.setText(model.getOrganizationAbbreviation());
                    }, R.id.or_code);
                    holder.bindDataToViewById(view -> {
                        TextView level = (TextView) view;
                        level.setText(model.getOrganizationLevel());
                    }, R.id.or_level);
                    holder.click(R.id.card_view, v -> {
                        openNewPage(ComponentsFragment.class, "organization", model);
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
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                if(pageNo == 1){
                    apiLodeMoreOrganization();
                }
                mOrganizationAdapter.refresh(organizationVOS);
                refreshLayout.finishRefresh();
            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                apiLodeMoreOrganization();
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    protected void apiLodeMoreOrganization() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        MyHttp.get("/organization/getOrganizationCreateByUser", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray organizations = new JSONArray(data.getString("list"));
                List<OrganizationVO> newList = new ArrayList<>();
                newList = ReflectUtil.convertToList(organizations, OrganizationVO.class);
                if (pageNo <= totalPage) {
                    organizationVOS.addAll(newList);
                    mOrganizationAdapter.loadMore(newList);
                    pageNo += 1;
                }

            }

            @Override
            public void fail(JSONObject error) {
                refreshLayout.finishRefresh();
            }
        });
    }
}



