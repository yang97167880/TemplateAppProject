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

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ExamineVO;
import com.yiflyplan.app.adapter.VO.MemberVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.LoginFragment;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(name = "审核申请", extra = R.drawable.ic_examine)
public class Examine extends BaseFragment {

    @BindView(R.id.examine_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.examine_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 5;
    private List<ExamineVO> examineVOS = new ArrayList<>();
    private SimpleDelegateAdapter<ExamineVO> mExamineAdapter;

    private String userAvatar;
    private ExamineVO examineVO;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_examine;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);


        mExamineAdapter = new BroccoliSimpleDelegateAdapter<ExamineVO>(R.layout.adapter_examine_item, new LinearLayoutHelper()) {


            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ExamineVO model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();


                        apiGetUserAvatar(model.getApplyUserName());

                        if(userAvatar==null){
                            Log.e("userAvatar","userAvatar==null");
                        }else
                            img.loadImage(avatar, Uri.parse(userAvatar));
                    }, R.id.member_avatar);

                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getApplyUserName());
                    }, R.id.member_name);

                    holder.bindDataToViewById(view -> {
                        if (model.getApplyStatus() == 2){
                            TextView role = (TextView) view;
                            role.setText("已同意");
                        }

                    }, R.id.apply_status);

                    holder.click(R.id.examine_member_view,v ->{
                        openNewPage(ExamineDetailFragment.class,"ExamineUserInfo",model);
                    });
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.examine_member_view)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mExamineAdapter);
//
        recyclerView.setAdapter(delegateAdapter);

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        Bundle build = getArguments();
        int id = build.getInt("id");

        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                if(pageNo == 1){
                    apiGetApprovedOrganizationApply(String.valueOf(id));
                }
                mExamineAdapter.refresh(examineVOS);
                refreshLayout.finishRefresh();
            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                apiGetApprovedOrganizationApply(String.valueOf(id));
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果


    }

    /**
     * 获取机构的已通过的加入申请
     */

    protected void apiGetApprovedOrganizationApply(String id) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("organizationId", id);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));

        MyHttp.postJson("/organization/getApprovedOrganizationApply", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray members = new JSONArray(data.getString("list"));
                Log.e("Res", data.toString());
                List<ExamineVO> newList = new ArrayList<>();
                newList = ReflectUtil.convertToList(members, ExamineVO.class);
                Log.e("Res", newList.toString());
                if (pageNo <= totalPage) {
                    examineVOS.addAll(newList);
                    mExamineAdapter.loadMore(newList);
                    pageNo += 1;
                }
            }

            @Override
            public void fail(JSONObject error) {
                refreshLayout.finishRefresh();
            }
        });


//        /**
//         * 获取机构的处理中的加入申请
//         */
//        MyHttp.postJson("/organization/getProcessingJoinOrganizationApply", TokenUtils.getToken(), params, new MyHttp.Callback() {
//            @Override
//            public void success(JSONObject data) throws JSONException {
//                JSONArray members = new JSONArray(data.getString("list"));
//                Log.e("Processing", data.toString());
//                List<ExamineVO> newList = new ArrayList<>();
//                newList = ReflectUtil.convertToList(members, ExamineVO.class);
//                Log.e("Processing", newList.toString());
//                if (pageNo <= totalPage) {
//                    examineVOS.addAll(newList);
//                    mExamineAdapter.loadMore(newList);
//                    pageNo += 1;
//                }
//            }
//
//            @Override
//            public void fail(JSONObject error) {
//                refreshLayout.finishRefresh();
//            }
//        });
//
//
//
//        /**
//         * 获取机构的未通过的加入申请
//         */
//        MyHttp.postJson("/organization/getRejectedOrganizationApply", TokenUtils.getToken(), params, new MyHttp.Callback() {
//            @Override
//            public void success(JSONObject data) throws JSONException {
//                JSONArray members = new JSONArray(data.getString("list"));
//                Log.e("Rejected", data.toString());
//                List<ExamineVO> newList = new ArrayList<>();
//                newList = ReflectUtil.convertToList(members, ExamineVO.class);
//                Log.e("Rejected", newList.toString());
//                if (pageNo <= totalPage) {
//                    examineVOS.addAll(newList);
//                    mExamineAdapter.loadMore(newList);
//                    pageNo += 1;
//                }
//            }
//
//            @Override
//            public void fail(JSONObject error) {
//                refreshLayout.finishRefresh();
//            }
//        });
    }



    protected void apiGetUserAvatar(String UserName) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("searchKey", "SWE180572");

        MyHttp.postJson("/user/search", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray members = new JSONArray(data.getString("list"));
                Log.e("members",members.toString());
                String uA = members.getJSONObject(0).getString("userAvatar");
                Log.e("uA",uA);
                userAvatar = uA;
                Log.e("userAvatar",userAvatar);
            }

            @Override
            public void fail(JSONObject error) {

            }
        });
    }
}
