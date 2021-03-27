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
import com.yiflyplan.app.utils.DemoDataProvider;
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
 * */
public class OrganizationFragment extends BaseFragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.organization_recyclerView)
    RecyclerView recyclerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.organization_refreshLayout)
    SmartRefreshLayout refreshLayout;


    private final String RELATIONSHIPS = "relationships";
    private BroccoliSimpleDelegateAdapter<OrganizationVO> mNoticeAdapter;

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
//
//
//        Bundle bundle = getArguments();
//        List<CurrentUserVO> list = (List<CurrentUserVO>) bundle.getSerializable("currentUser");
        mNoticeAdapter = new BroccoliSimpleDelegateAdapter<OrganizationVO>(R.layout.adapter_organization_list_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyInfo(OrganizationVO.class)) {


            @Override
            protected void onBindData(MyRecyclerViewHolder holder, OrganizationVO model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        TextView roleName = (TextView) view;
                        roleName.setText(model.getRoleName());
                    },R.id.or_roleName);
                    holder.bindDataToViewById(view -> {
                        TextView typeName = (TextView) view;
                        typeName.setText(model.getTypeName());
                    },R.id.or_typeName);
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                       name.setText(model.getName());
                    },R.id.or_name);
                    holder.bindDataToViewById(view -> {
                        TextView time = (TextView) view;
                        time.setText(model.getTime());
                        time.setVisibility(View.GONE);
                    },R.id.or_time);
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        //设置头像
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getAvatar()));

                    },R.id.or_avatar);
                    holder.bindDataToViewById(view -> {
                        TextView code = (TextView) view;
                        code.setText(model.getAbbreviation());
                    },R.id.or_code);
                    holder.bindDataToViewById(view -> {
                        TextView level = (TextView) view;
                        level.setText(model.getLevel());
                    },R.id.or_level);
                    //holder.click(R.id.card_view,v -> openNewPage(ChartRoomFragment.class));
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
        delegateAdapter.addAdapter(mNoticeAdapter);
//
        recyclerView.setAdapter(delegateAdapter);
    }


    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                LinkedHashMap<String,String> params = new  LinkedHashMap<>();
                params.put("pageNo","1");
                params.put("pageSize","5");
                MyHttp.get("/organization/getOrganizationCreateByUser", TokenUtils.getToken(), params, new MyHttp.Callback() {
                    @Override
                    public void success(JSONObject data) throws JSONException {
                        JSONArray organizations = new JSONArray(data.getString("list"));
                        List<OrganizationVO> voList = new ArrayList<>();
                        for(int i = 0;i<organizations.length();i++){
                            OrganizationVO temp = new OrganizationVO();
                            temp.setId( organizations.getJSONObject(i).getInt("id"));
                            temp.setName(organizations.getJSONObject(i).getString("organizationName"));
                            temp.setAvatar(organizations.getJSONObject(i).getString("organizationAvatar"));
                            temp.setAbbreviation(organizations.getJSONObject(i).getString("organizationAbbreviation"));
                            temp.setLevel(organizations.getJSONObject(i).getString("organizationLevel"));
                            temp.setTypeId(organizations.getJSONObject(i).getInt("organizationTypeId"));
                            temp.setTypeName(organizations.getJSONObject(i).getString("organizationTypeName"));
                            temp.setTime(organizations.getJSONObject(i).getString("createTime"));
                            //temp.setRoleName(organizations.getJSONObject(i).getString("roleName"));
                            voList.add(temp);
                        }
                mNoticeAdapter.refresh(voList);
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void fail(JSONObject error) {

                    }
                });

            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
//                mNoticeAdapter.loadMore();
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    private void initAPIData(){

    }
}

