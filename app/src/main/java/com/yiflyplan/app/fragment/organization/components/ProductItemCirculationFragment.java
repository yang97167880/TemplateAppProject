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

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.yiflyplan.app.adapter.VO.ProductCirculationVO;
import com.yiflyplan.app.adapter.VO.ProductVO;
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

@Page(name = "????????????")
public class ProductItemCirculationFragment extends BaseFragment {

    @BindView(R.id.circulation_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.circulation_refreshLayout)
    SmartRefreshLayout refreshLayout;


    private final static String TAKEOUT = "??????";
    private final static String DEPOSIT = "??????";
    private final static String PRODUCE = "??????";

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 5;
    private BroccoliSimpleDelegateAdapter<ProductCirculationVO> mProductAdapter;
    private List<ProductCirculationVO> productVOS = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_item_circulation;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mProductAdapter = new BroccoliSimpleDelegateAdapter<ProductCirculationVO>(R.layout.adapter_circulation_item, new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ProductCirculationVO model, int position) {

                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        TextView mainBodyName = (TextView) view;
                        mainBodyName.setText(model.getMainBodyName());
                    }, R.id.mainBodyName);

                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getCreateUserAvatar()));
                    }, R.id.create_member_avatar);

                     holder.bindDataToViewById(view -> {
                        TextView memberName = (TextView) view;
                        memberName.setText(model.getCreateUserName());
                    }, R.id.create_member_name);


                    holder.bindDataToViewById(view -> {
                        TextView time = (TextView) view;
                        time.setText(model.getCreateTime());
                    }, R.id.create_time);


                    holder.bindDataToViewById(view -> {
                        TextView createUserRfid = (TextView) view;
                        createUserRfid.setText(model.getCreateUserRfid());
                    }, R.id.create_user_rfid);



                    holder.bindDataToViewById(v -> {
                        if(model.getOperator().equals(DEPOSIT) || model.getOperator().equals(PRODUCE)) {
                            holder.bindDataToViewById(view -> {
                                TextView operator = (TextView) view;
                                operator.setText(model.getOperator());
                            }, R.id.tv_operator);

                            LinearLayout linearLayout = (LinearLayout) v;
                            linearLayout.setVisibility(View.GONE);
                        }
                    },R.id.ll_take_out);

                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getUpdateUserAvatar()));
                    }, R.id.update_member_avatar);

                    holder.bindDataToViewById(view -> {
                        TextView memberName = (TextView) view;
                        memberName.setText(model.getUpdateUserName());
                    }, R.id.update_member_name);


                    holder.bindDataToViewById(view -> {
                        TextView time = (TextView) view;
                        time.setText(model.getUpdateTime());
                    }, R.id.update_time);

                    holder.bindDataToViewById(view -> {
                        TextView updateUserRfid = (TextView) view;
                        updateUserRfid.setText(model.getUpdateUserRfid());
                    }, R.id.update_user_rfid);

                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.product_circulation_view)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mProductAdapter);
//
        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected TitleBar initTitle() {
        return super.initTitle();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //????????????
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 ????????????
            refreshLayout.getLayout().postDelayed(() -> {
                if (pageNo == 1) {
                    Bundle bundle = getArguments();
                    ProductVO product = (ProductVO) bundle.getSerializable("product");
                    apiGetCirculationInfo(String.valueOf(product.getId()));
                }
                mProductAdapter.refresh(productVOS);
                refreshLayout.finishRefresh();
            }, 500);
        });
        //????????????
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 ????????????
            refreshLayout.getLayout().postDelayed(() -> {
                Bundle bundle = getArguments();
                ProductVO product = (ProductVO) bundle.getSerializable("product");
                apiGetCirculationInfo(String.valueOf(product.getId()));
                refreshLayout.finishLoadMore();
            }, 500);
        });
        refreshLayout.autoRefresh();//????????????????????????????????????????????????
    }

    protected void apiGetCirculationInfo(String id) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("itemId", id);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        MyHttp.postJson("/product/getProductItemCirculation", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                totalPage = data.getInt("totalPage");
                JSONArray product = new JSONArray(data.getString("list"));
                List<ProductCirculationVO> newList = new ArrayList<>();
                newList = ReflectUtil.convertToList(product, ProductCirculationVO.class);
                if(pageNo<=totalPage){
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
