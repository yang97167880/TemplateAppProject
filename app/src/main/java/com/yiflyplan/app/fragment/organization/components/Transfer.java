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

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ProductVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
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

import static com.yiflyplan.app.utils.ImageConversionUtil.base64ToBitmap;

@Page(name = "产品转移", extra = R.drawable.ic_transfer)
public class Transfer extends BaseFragment {
    @BindView(R.id.product_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.product_refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 5;
    private List<ProductVO> productVOS = new ArrayList<>();
    private int visible = View.GONE;
    private int num = 0;
    private List<String> organizationId = new ArrayList<>();
    private List<Integer> checkId = new ArrayList<>();
    private BroccoliSimpleDelegateAdapter<ProductVO> mProductAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transfer;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mProductAdapter = new BroccoliSimpleDelegateAdapter<ProductVO>(R.layout.adapter_product_item, new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ProductVO model, int position) {
                if (model != null) {
                    holder.click(R.id.item_checked, v -> {
                        CheckBox box = (CheckBox) v;
                        if (box.isChecked()) {
                            num++;
                            checkId.add(position);
                            organizationId.add(String.valueOf(model.getId()));
                        } else {
                            num = num - 1;
                            checkId.remove(position);
                            organizationId.remove(String.valueOf(model.getId()));
                        }
                        ImageView imageView = new ImageView(getContext());
                        imageView.setImageResource(R.drawable.ic_box);
                        imageView.setPadding(10, 10, 15, 15);
                        SnackbarUtils.Indefinite(v, "选择了" + num + "个物品").info().addView(imageView, 0)
                                .backColor(Color.WHITE)
                                .actionColor(ResUtils.getColor(R.color.colorAccent))
                                .messageColor(ResUtils.getColor(R.color.colorAccent))
                                .setAction("取消", view -> XToastUtils.toast("点击了取消！"))
                                .setAction("转移", view -> {
                                    if(organizationId.size() == 0){
                                        XToastUtils.toast("没有选择要转移的物品！");
                                    }else{
                                        openNewPage(ProductQRCode.class,"ids",organizationId);
                                    }
                                }).show();
                    });
                    holder.bindDataToViewById(view -> {
                        LinearLayout expandInfo = (LinearLayout) view;
                        expandInfo.setVisibility(View.GONE);
                        holder.click(R.id.expand, v -> {
                            if (expandInfo.getVisibility() == View.GONE) {
                                expandInfo.setVisibility(View.VISIBLE);
                            } else {
                                expandInfo.setVisibility(View.GONE);
                            }
                        });
                    }, R.id.expand_info);
                    holder.bindDataToViewById(view -> {
                        CheckBox checkBox = (CheckBox) view;
                        if(checkId.indexOf( position )>0 -1){
                            checkBox.setChecked(true);
                        }else{
                            checkBox.setChecked(false);
                        }
                    },R.id.item_checked);
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getItemTypeName());
                    }, R.id.product_name);

                    holder.bindDataToViewById(view -> {
                        TextView from = (TextView) view;
                        from.setText(model.getOrganizationName() + " | " + model.getDepartmentName());
                    }, R.id.product_from);

                    holder.bindDataToViewById(view -> {
                        TextView weight = (TextView) view;
                        weight.setText(model.getItemWeight() + "g");
                    }, R.id.item_weight);

                    holder.bindDataToViewById(view -> {
                        TextView itemPackage = (TextView) view;
                        itemPackage.setText(model.getBagTypeName());
                    }, R.id.item_package);

                    holder.bindDataToViewById(view -> {
                        TextView level = (TextView) view;
                        level.setText(model.getPollutionLevelName());
                    }, R.id.item_pollution);

                    holder.bindDataToViewById(view -> {
                        TextView person = (TextView) view;
                        person.setText(model.getCreateUserName());
                    }, R.id.item_create);

                    holder.bindDataToViewById(view -> {
                        TextView time = (TextView) view;
                        time.setText("创建时间：" + model.getCreateTime());
                    }, R.id.item_createTime);
                    holder.bindDataToViewById(view -> {
                        TextView updateName = (TextView) view;
                        updateName.setText(model.getUpdateUserName());
                    }, R.id.item_updateName);
                    holder.bindDataToViewById(view -> {
                        TextView updateTime = (TextView) view;
                        updateTime.setText(model.getUpdateTime());
                    }, R.id.item_updateTime);
                    holder.bindDataToViewById(view -> {
                        TextView code = (TextView) view;
                        code.setText(model.getItemCoding());
                    }, R.id.item_Coding);

                    holder.bindDataToViewById(view -> {
                        TextView info = (TextView) view;
                        info.setVisibility(View.INVISIBLE);
                    }, R.id.item_infor);
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
                if (pageNo == 1) {
                    Bundle bundle = getArguments();
                    int id = bundle.getInt("id");
                    apiLoadMoreProduct(String.valueOf(id));
                }
                mProductAdapter.refresh(productVOS);
                refreshLayout.finishRefresh();
            }, 500);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                Bundle bundle = getArguments();
                int id = bundle.getInt("id");
                apiLoadMoreProduct(String.valueOf(id));
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    protected void apiLoadMoreProduct(String id) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("queryOwn", "true");
        params.put("organizationId", id);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        MyHttp.postJson("/product/getAllProduct", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                totalPage = data.getInt("totalPage");
                JSONArray product = new JSONArray(data.getString("list"));
                List<ProductVO> newList = new ArrayList<>();
                newList = ReflectUtil.convertToList(product, ProductVO.class);
                if (pageNo <= totalPage) {
                    productVOS.addAll(newList);
                    mProductAdapter.loadMore(newList);
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