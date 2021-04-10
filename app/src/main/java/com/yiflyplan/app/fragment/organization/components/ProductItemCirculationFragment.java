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

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ProductVO;
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

@Page(name = "流转信息")
public class ProductItemCirculationFragment extends BaseFragment {

    private int totalPage = 1;
    private int pageNo = 1;
    private int pageSize = 5;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_item_circulation;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected TitleBar initTitle() {
        return super.initTitle();
    }

    @Override
    protected void initListeners() {
        Bundle bundle = getArguments();
        ProductVO product = (ProductVO) bundle.getSerializable("product");

        apiGetCirculationInfo(String.valueOf(product.getId()));

        super.initListeners();


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

                Log.d("circulation",product.toString());
//                List<ProductVO> newList = new ArrayList<>();
//                newList = ReflectUtil.convertToList(product, ProductVO.class);
//                if(pageNo<=totalPage){
//                    productVOS.addAll(newList);
//                    mProductAdapter.loadMore(newList);
//                    pageNo += 1;
//                }

            }

            @Override
            public void fail(JSONObject error) {
//                refreshLayout.finishRefresh();
            }
        });
    }
}
