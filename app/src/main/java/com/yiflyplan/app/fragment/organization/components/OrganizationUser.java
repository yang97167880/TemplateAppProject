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

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.MemberVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
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

@Page(name = "机构成员", extra = R.drawable.ic_member)
public class OrganizationUser extends BaseFragment {

    @BindView(R.id.member_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.member_refreshLayout)
    SmartRefreshLayout refreshLayout;


    private SimpleDelegateAdapter<MemberVO> mMemberAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_member;
    }

    @Override
    protected void initViews() {

        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mMemberAdapter = new BroccoliSimpleDelegateAdapter<MemberVO>(R.layout.adapter_member,new LinearLayoutHelper(), DemoDataProvider.getEmptyInfo(MemberVO.class)){

            @Override
            protected void onBindData(MyRecyclerViewHolder holder, MemberVO model, int position) {
                if(model!=null){
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getAvatar()));
                    },R.id.member_avatar);

                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getName());
                    },R.id.member_name);

                    holder.bindDataToViewById(view -> {
                        TextView phone = (TextView) view;
                        phone.setText("联系电话："+ model.getTel());
                    },R.id.member_phone);
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.member_view)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mMemberAdapter);
//
        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            Bundle build = getArguments();
            int id = build.getInt("id");
            refreshLayout.getLayout().postDelayed(() -> {
                LinkedHashMap<String,String> params = new  LinkedHashMap<>();
                params.put("organizationId",String.valueOf(id));
                params.put("pageNo","1");
                params.put("pageSize","5");
                MyHttp.postJson("/organization/getMembersOfAnOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
                    @Override
                    public void success(JSONObject data) throws JSONException {
                        JSONArray members = new JSONArray(data.getString("list"));
                        List<MemberVO> voList = new ArrayList<>();
                        for(int i = 0;i<members.length();i++){
                            MemberVO temp = new MemberVO();
                            temp.setId( members.getJSONObject(i).getInt("userId"));
                            temp.setName(members.getJSONObject(i).getString("userName"));
                            temp.setAvatar(members.getJSONObject(i).getString("userAvatar"));
                            temp.setRoleName(members.getJSONObject(i).getString("roleName"));
                            temp.setCityId(members.getJSONObject(i).getInt("userCityId"));
                            temp.setTel(members.getJSONObject(i).getString("userTel"));
                            //temp.setRoleName(organizations.getJSONObject(i).getString("roleName"));
                            voList.add(temp);
                        }
                        mMemberAdapter.refresh(voList);
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void fail(JSONObject error) {

                    }
                });

            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
//                mNoticeAdapter.loadMore();
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

}
