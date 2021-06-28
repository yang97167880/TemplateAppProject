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
import android.util.Log;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ContainersVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;

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

@Page(name = "停放位置")
public class OrganizationContainersFragment extends BaseFragment {

    @BindView(R.id.containers_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.containers_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private BroccoliSimpleDelegateAdapter<ContainersVO> mContainersAdapter;

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 15;
    private List<ContainersVO> containersVOS = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_organization_containers;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mContainersAdapter = new BroccoliSimpleDelegateAdapter<ContainersVO>(R.layout.adapter_containers_item, new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ContainersVO model, int position) {

                if (model != null) {



                    holder.bindDataToViewById(view -> {
                        TextView tvContainerTypeName = (TextView) view;
                        tvContainerTypeName.setText(model.getContainerTypeName());
                    }, R.id.tv_container_type_name);

                    holder.bindDataToViewById(view -> {
                        TextView tvRemarks = (TextView) view;
                        String remarks = "("+model.getRemarks()+")";
                        tvRemarks.setText(remarks);
                    }, R.id.tv_remarks);

                    holder.click(R.id.containers_view, v -> {
                        openNewPage(TransitBoxFragment.class, "id", model.getId());
                    });
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.containers_view)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mContainersAdapter);
//
        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                if (pageNo == 1) {
                    Bundle bundle = getArguments();
                    int id = bundle.getInt("id");
                    apiLoadMoreContainers(String.valueOf(id));
                }
                mContainersAdapter.refresh(containersVOS);
                refreshLayout.finishRefresh();
            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                Bundle bundle = getArguments();
                int id = bundle.getInt("id");
                apiLoadMoreContainers(String.valueOf(id));
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    protected void apiLoadMoreContainers(String id) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("organizationId", id);
        MyHttp.get("/container/organization/getContainers", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                totalPage = data.getInt("totalPage");
                JSONArray containers = new JSONArray(data.getString("list"));
                List<ContainersVO> newList = new ArrayList<>();
                newList = ReflectUtil.convertToList(containers, ContainersVO.class);
                if(pageNo<=totalPage){
                    containersVOS.addAll(newList);
                    mContainersAdapter.loadMore(newList);
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
