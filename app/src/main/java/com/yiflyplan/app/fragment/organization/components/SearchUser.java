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
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.MemberVO;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.UserInfoFragment;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

@Page(name = "搜索", extra = R.drawable.ic_member)
public class SearchUser extends BaseFragment {
    @BindView(R.id.search_user)
    MaterialSearchView mSearchView;

    List<MemberVO> memberVOS;
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
    protected int getLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initViews() {
        mSearchView.setVoiceSearch(false);
        mSearchView.setEllipsize(true);
        initData();
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for(int i=0;i<memberVOS.size();i++){
                    if(query.equals(memberVOS.get(i).getUserName())){
                        LinkedHashMap<String, String> params = new LinkedHashMap<>();
                        params.put("userId", String.valueOf(memberVOS.get(i).getUserId()));
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
                    }
                }

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

    public void initData(){
        Bundle vos = getArguments();
        memberVOS = (List<MemberVO>)vos.getSerializable("vos");
        String[] suggestions = new String[memberVOS.size()];
        for(int i=0;i<memberVOS.size();i++){
            suggestions[i] = memberVOS.get(i).getUserName();
        }
        mSearchView.setSuggestions(suggestions);
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
}
