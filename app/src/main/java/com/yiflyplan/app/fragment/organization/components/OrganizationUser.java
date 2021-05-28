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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.MemberVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.UserInfoFragment;
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

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(name = "机构成员", extra = R.drawable.ic_member)
public class OrganizationUser extends BaseFragment {

    @BindView(R.id.member_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.member_refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.member_search_view)
    MaterialSearchView mSearchView;
    @BindView(R.id.member_count)
    TextView memberCount;

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 15;
    private List<MemberVO> memberVOS = new ArrayList<>();
    private SimpleDelegateAdapter<MemberVO> mMemberAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_member;
    }

    @Override
    protected void initViews() {
        initSearchView();
        search.setOnClickListener(v -> {
            mSearchView.showSearch();
        });
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mMemberAdapter = new BroccoliSimpleDelegateAdapter<MemberVO>(R.layout.adapter_member, new LinearLayoutHelper()) {

            @Override
            protected void onBindData(MyRecyclerViewHolder holder, MemberVO model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getUserAvatar()));
                    }, R.id.member_avatar);

                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getUserName());
                    }, R.id.member_name);

                    holder.bindDataToViewById(view -> {
                        TextView role = (TextView) view;
                        role.setText(model.getRoleName());
                    }, R.id.member_roleName);
                    holder.click(R.id.member_view, view -> {
                        LinkedHashMap<String, String> params = new LinkedHashMap<>();
                        params.put("userId", String.valueOf(model.getUserId()));
                        MyHttp.get("/user/getUserInfo", TokenUtils.getToken(), params, new MyHttp.Callback() {
                            @Override
                            public void success(JSONObject data) {
                                CurrentUserVO currentUserVO = ReflectUtil.convertToObject(data, CurrentUserVO.class);

                                openNewPage(UserInfoFragment.class, "currentUserVO", currentUserVO);
                            }

                            @Override
                            public void fail(JSONObject error) throws JSONException {
                                XToastUtils.error(error.getString("message"));
                            }
                        });

                    });
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

        recyclerView.setAdapter(delegateAdapter);
    }

    protected void initSearchView() {
        mSearchView.setBackIcon(getResources().getDrawable(R.drawable.ic_login_close));
        mSearchView.setVoiceSearch(false);
        mSearchView.setEllipsize(true);
        // mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                SnackbarUtils.Long(mSearchView, "Query: " + query).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
              //  MyHttp.post("/user/search",);
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        mSearchView.setSubmitOnClick(true);
    }

    @Override
    public void onDestroyView() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initListeners() {

        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                if (pageNo == 1) {
                    Bundle build = getArguments();
                    int id = build.getInt("id");
                    apiLoadMoreMember(String.valueOf(id));
                }
                mMemberAdapter.refresh(memberVOS);
                refreshLayout.finishRefresh();
            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                Bundle build = getArguments();
                int id = build.getInt("id");
                apiLoadMoreMember(String.valueOf(id));
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    protected void apiLoadMoreMember(String id) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("organizationId", id);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));

        MyHttp.postJson("/organization/getMembersOfAnOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray members = new JSONArray(data.getString("list"));
                List<MemberVO> newList = new ArrayList<>();
                newList = ReflectUtil.convertToList(members, MemberVO.class);
                memberCount.setText("成员数 (" + members.length() + "人)");
                if (pageNo <= totalPage) {
                    memberVOS.addAll(newList);
                    mMemberAdapter.loadMore(newList);
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
