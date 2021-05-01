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

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xuexiang.xpage.annotation.Page;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;

@Page(name="测试页面")
public class TestFragment extends BaseFragment {

    @BindView(R.id.e1)
    EditText e1;
    @BindView(R.id.e2)
    EditText e2;
    @BindView(R.id.e3)
    EditText e3;
    @BindView(R.id.e4)
    EditText e4;
    @BindView(R.id.e5)
    EditText e5;
    @BindView(R.id.e6)
    EditText e6;

    @BindView(R.id.in1)
    Button in1;
    @BindView(R.id.in2)
    Button in2;

    @BindView(R.id.out1)
    Button out1;
    @BindView(R.id.out2)
    Button out2;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void initViews() {
        setIn1Event();
        setIn2Event();
        setOut1Event();
        setOut2Event();
    }

    private void setIn1Event(){
        in1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("itemEncoding",e1.getText().toString());
                params.put("rfid",e2.getText().toString());
                params.put("transitBoxEncoding",e3.getText().toString());

                Log.e("test1",params.toString());
                MyHttp.postForm("/transitBox/putProductIntoTransitBox", TokenUtils.getToken(), params, new MyHttp.Callback(){

                    @Override
                    public void success(JSONObject data) throws JSONException {
                        XToastUtils.success("成功放入转运箱");
                    }

                    @Override
                    public void fail(JSONObject error) throws JSONException {

                    }
                });
            }
        });
    }

    private void setIn2Event(){
        in2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("transitBoxEncoding",e4.getText().toString());
                params.put("rfid",e5.getText().toString());
                params.put("containerImei",e6.getText().toString());

                Log.e("test2",params.toString());
                MyHttp.postForm("/container/organization/putTransitBoxIntoContainer", TokenUtils.getToken(), params, new MyHttp.Callback(){

                    @Override
                    public void success(JSONObject data) throws JSONException {
                        XToastUtils.success("成功放入容器");
                    }

                    @Override
                    public void fail(JSONObject error) throws JSONException {

                    }
                });
            }
        });
    }

    private void setOut1Event(){
        out1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("itemEncoding",e1.getText().toString());
                params.put("rfid",e2.getText().toString());
                params.put("transitBoxEncoding",e3.getText().toString());

                Log.e("test3",params.toString());
                MyHttp.postForm("/transitBox/removeProductFromTransitBox", TokenUtils.getToken(), params, new MyHttp.Callback(){

                    @Override
                    public void success(JSONObject data) throws JSONException {
                        XToastUtils.success("成功从转运箱取出");
                    }

                    @Override
                    public void fail(JSONObject error) throws JSONException {

                    }
                });
            }
        });
    }

    private void setOut2Event(){
        out2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("transitBoxEncoding",e4.getText().toString());
                params.put("rfid",e5.getText().toString());
                params.put("containerImei",e6.getText().toString());

                Log.e("test4",params.toString());
                MyHttp.postForm("/container/organization/removeTransitBoxFromContainer", TokenUtils.getToken(), params, new MyHttp.Callback(){

                    @Override
                    public void success(JSONObject data) throws JSONException {
                        XToastUtils.success("成功从容器取出");
                    }

                    @Override
                    public void fail(JSONObject error) throws JSONException {

                    }
                });
            }
        });
    }
}
