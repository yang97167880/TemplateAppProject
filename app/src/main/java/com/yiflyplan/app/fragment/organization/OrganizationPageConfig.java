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

package com.yiflyplan.app.fragment.organization;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ContainersVO;
import com.yiflyplan.app.adapter.VO.MenuListVO;
import com.yiflyplan.app.adapter.VO.ProductCirculationVO;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TimeCountUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class OrganizationPageConfig {

    private static OrganizationPageConfig sInstance;

    private List<PageInfo> mPages;

    private List<PageInfo> mComponents;

    private List<PageInfo> mUtils;

    private List<PageInfo> mExpands;

    private List<MenuListVO> menuListVOS = new ArrayList<>();



    public OrganizationPageConfig(){


        mPages = new ArrayList<>();
        mComponents = new ArrayList<>();
        mUtils = new ArrayList<>();
        mExpands = new ArrayList<>();



        mComponents.add(new PageInfo("机构成员","com.yiflyplan.app.fragment.organization.components.OrganizationUser","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_member));
        mPages.add(new PageInfo("机构成员", "com.yiflyplan.app.fragment.organization.components.OrganizationUser", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_member));

        mComponents.add(new PageInfo("个人仓库","com.yiflyplan.app.fragment.organization.components.PersonalWarehouse","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_personal));
        mPages.add(new PageInfo("个人仓库", "com.yiflyplan.app.fragment.organization.components.PersonalWarehouse", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_personal));

        mComponents.add(new PageInfo("分享机构","com.yiflyplan.app.fragment.organization.components.Share","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_share));
        mPages.add(new PageInfo("分享机构", "com.yiflyplan.app.fragment.organization.components.Share", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_share));


//        mComponents.add(new PageInfo("RFID烧录","com.yiflyplan.app.fragment.organization.components.Share","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_rfid_card));
//        mPages.add(new PageInfo("RFID烧录", "com.yiflyplan.app.fragment.organization.components.Share", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_rfid_card));


        getMenuList();

//        mComponents.add(new PageInfo("机构仓库","com.yiflyplan.app.fragment.organization.components.OrganizationContainersFragment","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_warehouse));
//        mPages.add(new PageInfo("机构仓库", "com.yiflyplan.app.fragment.organization.components.OrganizationContainersFragment", "{\"\":\"\"}", CoreAnim.slide,  R.drawable.ic_warehouse));
//
//        mComponents.add(new PageInfo("产品转移","com.yiflyplan.app.fragment.organization.components.Transfer","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
//        mPages.add(new PageInfo("产品转移", "com.yiflyplan.app.fragment.organization.components.Transfer", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
//
//        mComponents.add(new PageInfo("产品接收","com.yiflyplan.app.fragment.organization.components.Receive","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
//        mPages.add(new PageInfo("产品接收", "com.yiflyplan.app.fragment.organization.components.Receive", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
//
//        mComponents.add(new PageInfo("审核申请","com.yiflyplan.app.fragment.organization.components.Examine","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));
//        mPages.add(new PageInfo("审核申请", "com.yiflyplan.app.fragment.organization.components.Examine", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));





    }

    /**
     * 获取菜单项
     */
    private void getMenuList() {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        MyHttp.get("/route/getMenuListByOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {

                JSONArray MenuList = new JSONArray(data.getString("list"));

                List<MenuListVO> newList = new ArrayList<>();

                newList = ReflectUtil.convertToList(MenuList, MenuListVO.class);

                menuListVOS.clear();
                menuListVOS.addAll(newList);

                for (MenuListVO menuListVO : menuListVOS){
                    Log.d("getMenuList",menuListVO.getMenuName());
                    Log.d("getMenuList",menuListVO.getMenuPath());
                    switch (menuListVO.getMenuName()){
                        case "机构仓库":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_warehouse));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_warehouse));
                            break;
                        case "产品转移":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
                            break;
                        case "产品接收":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
                            break;
                        case "审核申请":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));
                            break;
                    }
                }

            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                XToastUtils.error(error.getString("message"));
            }
        });

    }


    public static OrganizationPageConfig getInstance() {
        if (sInstance == null) {
            synchronized (OrganizationPageConfig.class) {
                if (sInstance == null) {
                    sInstance = new OrganizationPageConfig( );
                }
            }
        }
        return sInstance;
    }

    public List<PageInfo> getPages() {
        return mPages;
    }

    public List<PageInfo> getComponents() {
        return mComponents;
    }

    public List<PageInfo> getUtils() {
        return mUtils;
    }

    public List<PageInfo> getExpands() {
        return mExpands;
    }
}
