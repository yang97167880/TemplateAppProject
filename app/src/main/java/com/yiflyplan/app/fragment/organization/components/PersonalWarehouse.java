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

import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ProductVO;
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

@Page(name = "个人仓库", extra = R.drawable.ic_personal)
public class PersonalWarehouse extends BaseFragment {

    @BindView(R.id.product_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.product_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private BroccoliSimpleDelegateAdapter<ProductVO> mProductAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal;
    }


    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);


        mProductAdapter = new BroccoliSimpleDelegateAdapter<ProductVO>(R.layout.adapter_product_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyInfo(ProductVO.class)){

            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ProductVO model, int position) {
                if(model!=null){
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getTypeName());
                    },R.id.product_name);

                    holder.bindDataToViewById(view -> {
                        TextView from = (TextView) view;
                        from.setText(model.getOrganizationName() + " | " + model.getDepartmentName());
                    },R.id.product_from);

                    holder.bindDataToViewById(view -> {
                        TextView weight = (TextView) view;
                        weight.setText(model.getItemWeight()+"g");
                    },R.id.item_weight);

                    holder.bindDataToViewById(view -> {
                        TextView itemPackage = (TextView) view;
                        itemPackage.setText( model.getBagTypeName());
                    },R.id.item_package);

                    holder.bindDataToViewById(view -> {
                        TextView level = (TextView) view;
                        level.setText(model.getPollutionLevelName());
                    },R.id.item_pollution);

                    holder.bindDataToViewById(view -> {
                        TextView person = (TextView) view;
                        person.setText( model.getCreateUserName());
                    },R.id.item_create);

                    holder.bindDataToViewById(view -> {
                        TextView time = (TextView) view;
                        time.setText("创建时间："+ model.getCreateTime());
                    },R.id.item_createTime);

                    holder.bindDataToViewById(view -> {
                        TextView code = (TextView) view;
                        code.setText(model.getItemCoding());
                    },R.id.item_Coding);
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.product_view)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mProductAdapter);
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
                params.put("queryOwn","true");
                params.put("pageNo","1");
                params.put("pageSize","5");
                MyHttp.postJson("/product/getAllProduct", TokenUtils.getToken(), params, new MyHttp.Callback(){

                    @Override
                    public void success(JSONObject data) throws JSONException {
                        Log.e("RES：",data.toString());
                        JSONArray product = new JSONArray(data.getString("list"));
                        List<ProductVO> voList = new ArrayList<>();
                        for(int i = 0;i<product.length();i++){
                            ProductVO temp = new ProductVO();
                            temp.setTypeName( product.getJSONObject(i).getString("itemTypeName"));
                            temp.setOrganizationName(product.getJSONObject(i).getString("organizationName"));
                            temp.setDepartmentName(product.getJSONObject(i).getString("departmentName"));
                            temp.setItemWeight(product.getJSONObject(i).getInt("itemWeight"));
                            temp.setBagTypeName(product.getJSONObject(i).getString("bagTypeName"));
                            temp.setPollutionLevelName(product.getJSONObject(i).getString("pollutionLevelName"));
                            temp.setCreateUserName(product.getJSONObject(i).getString("createUserName"));
                            temp.setCreateTime(product.getJSONObject(i).getString("createTime"));
                            temp.setItemCoding(product.getJSONObject(i).getString("itemCoding"));
                            voList.add(temp);
                        }
                        mProductAdapter.refresh(voList);
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
