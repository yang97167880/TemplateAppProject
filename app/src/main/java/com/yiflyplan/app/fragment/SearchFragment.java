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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ContainersVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.blueTooth.BlueToothFragment;
import com.yiflyplan.app.fragment.organization.components.TransitBoxFragment;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import me.samlss.broccoli.Broccoli;

/**
 * @author admin
 */
@Page(name = "????????????")
public class SearchFragment extends BaseFragment {
    @BindView(R.id.search_view)
    MaterialSearchView mSearchView;

    @BindView(R.id.match_start)
    ButtonView mMatchStart;

    @BindView(R.id.department)
    TextView departmentView;
    @BindView(R.id.organization)
    TextView organizationView;

    @BindView(R.id.department_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.department_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private BroccoliSimpleDelegateAdapter<String> mUploadDataAdapter;

    private List<String> departmentList = new ArrayList<>();



    String organizationName;
    int organizationId;
    UploadData uploadData;

    String[] departments;
    String[] departmentsId;

    private final static String NO_SET = "????????????";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_action_search_white) {

            @Override
            @SingleClick
            public void performAction(View view) {
                mSearchView.showSearch();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        organizationName = MMKVUtils.getString("organizationName",null);
        organizationId = MMKVUtils.getInt("organizationId",0);
        organizationView.setText(organizationName);

        uploadData = new UploadData();
        uploadData.setOrganizationId( String.valueOf(organizationId ));
        uploadData.setOrganizationName(organizationName);
        getAllDepartment();

        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mUploadDataAdapter = new BroccoliSimpleDelegateAdapter<String>(R.layout.adapter_department_item, new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, String model, int position) {

                if (model != null) {

                    holder.bindDataToViewById(view -> {
                        TextView organization = (TextView) view;
                        organization.setText(organizationName);
                    }, R.id.organization);

                    holder.bindDataToViewById(view -> {
                        TextView department = (TextView) view;
                        department.setText(model);
                    }, R.id.department);

                    holder.click(R.id.department_view, v -> {
                        departmentList.remove(model);
                        departmentList.add(0,model);
                        mUploadDataAdapter.refresh(departmentList);

                        Gson gson=new Gson();
                        String stringJson=gson.toJson(departmentList);
                        MMKVUtils.put("department",stringJson);

                        SnackbarUtils.Short(mSearchView, "????????????????????????" + model).show();
                        departmentView.setText(model);

                        for(int i = 0;i<departments.length;i++){
                            if(departments[i].equals(model)){
                                uploadData.setDepartmentId(departmentsId[i]);
                            }
                        }
                        uploadData.setDepartmentName(model);

                    });
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.department_view)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mUploadDataAdapter);
        recyclerView.setAdapter(delegateAdapter);


        mSearchView.setVoiceSearch(false);
        mSearchView.setEllipsize(true);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for(int i = 0;i<departments.length;i++){
                    if(departments[i].equals(query)){
                        uploadData.setDepartmentId(departmentsId[i]);
                    }
                }
                if(uploadData.getDepartmentId() == null){
                    XToastUtils.error("??????????????????");
                }else{

                    SnackbarUtils.Short(mSearchView, "????????????????????????" + query).show();
                    departmentView.setText(query);
                    uploadData.setDepartmentName(query);

                    if(departmentList!=null && departmentList.contains(query)){
                        if(!departmentList.get(0).equals(query)){
                            departmentList.remove(query);
                            departmentList.add(0,query);
                        }
                    }else {
                        departmentList.add(query);
                    }
                    mUploadDataAdapter.refresh(departmentList);

                    Gson gson=new Gson();
                    String stringJson=gson.toJson(departmentList);
                    MMKVUtils.put("department",stringJson);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
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
    protected void initListeners() {
        //????????????
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 ????????????
            refreshLayout.getLayout().postDelayed(() -> {
                getDepartmentCacheList();
                mUploadDataAdapter.refresh(departmentList);
                refreshLayout.finishRefresh();
            }, 500);
        });
        refreshLayout.autoRefresh();//????????????????????????????????????????????????

    }


    @Override
    public void onDestroyView() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        super.onDestroyView();
    }

    @OnClick(R.id.match_start)
    void toSearchActivity(View v) {
        if(departmentView.getText().toString().equals(NO_SET)){
            XToastUtils.error("??????????????????");
        }else{
            openNewPage(BlueToothFragment.class, "uploadData", uploadData);
        }
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




    public static class UploadData implements Serializable {

        @Override
        public String toString() {
            return "UploadData{" +
                    "address='" + address + '\'' +
                    ", departmentId=" + departmentId +
                    ", departmentName='" + departmentName + '\'' +
                    ", organizationId=" + organizationId +
                    ", organizationName='" + organizationName + '\'' +
                    '}';
        }

        private String address;
        private String departmentId;
        private String departmentName;
        private String organizationId;
        private String organizationName;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public String getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(String organizationId) {
            this.organizationId = organizationId;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
    }

    private void getDepartmentCacheList(){

        String stringJson= MMKVUtils.getString("department",null);
        Gson gson=new Gson();
        List<String> list =  gson.fromJson(stringJson, ArrayList.class);

        departmentList.clear();
        if(stringJson!=null){
            departmentList.addAll(list);
        }

    }

    private void getAllDepartment() {
        MyHttp.getJsonList("/department/getAllDepartment", TokenUtils.getToken(), new MyHttp.Callback() {

            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray list = new JSONArray(data.getString("data"));
                departments = new String[list.length()];
                departmentsId = new String[list.length()];
                for (int i = 0; i < list.length(); i++) {
                    departments[i] = list.getJSONObject(i).getString("departmentName");
                    departmentsId[i] = list.getJSONObject(i).getString("id");
                }
                mSearchView.setSuggestions(departments);
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
    }

}
