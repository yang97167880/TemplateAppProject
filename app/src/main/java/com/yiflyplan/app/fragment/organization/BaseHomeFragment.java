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
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yiflyplan.app.R;
import com.yiflyplan.app.activity.MainActivity;
import com.yiflyplan.app.adapter.VO.MenuListVO;
import com.yiflyplan.app.adapter.WidgetItemAdapter;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.organization.components.Examine;
import com.yiflyplan.app.fragment.organization.components.OrganizationContainersFragment;
import com.yiflyplan.app.fragment.organization.components.OrganizationUser;
import com.yiflyplan.app.fragment.organization.components.PersonalWarehouse;
import com.yiflyplan.app.fragment.organization.components.Receive;
import com.yiflyplan.app.fragment.organization.components.Share;
import com.yiflyplan.app.fragment.organization.components.Transfer;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public abstract class BaseHomeFragment extends BaseFragment implements RecyclerViewHolder.OnItemClickListener<PageInfo>{
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<PageInfo> mPages;

    private List<PageInfo> mComponents;

    private WidgetItemAdapter mWidgetItemAdapter;

    @Override
    protected TitleBar initTitle() { return null; }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_organization_container;
    }

    @Override
    protected void initViews() {
        initRecyclerView();
    }

    private void initRecyclerView()  {
        getMenuList(this);
    }


    /**
     * ???????????????
     */
    private void getMenuList(RecyclerViewHolder.OnItemClickListener<PageInfo> OnItemClickListener) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        MyHttp.get("/route/getMenuListByOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {

                mPages = new ArrayList<>();
                mComponents = new ArrayList<>();



                mComponents.add(new PageInfo("????????????","com.yiflyplan.app.fragment.organization.components.OrganizationUser","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_member));
                mPages.add(new PageInfo("????????????", "com.yiflyplan.app.fragment.organization.components.OrganizationUser", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_member));

                mComponents.add(new PageInfo("????????????","com.yiflyplan.app.fragment.organization.components.PersonalWarehouse","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_personal));
                mPages.add(new PageInfo("????????????", "com.yiflyplan.app.fragment.organization.components.PersonalWarehouse", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_personal));

                mComponents.add(new PageInfo("????????????","com.yiflyplan.app.fragment.organization.components.Share","{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_share));
                mPages.add(new PageInfo("????????????", "com.yiflyplan.app.fragment.organization.components.Share", "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_share));




                JSONArray MenuList = new JSONArray(data.getString("list"));

                List<MenuListVO> newList = new ArrayList<>();

                newList = ReflectUtil.convertToList(MenuList, MenuListVO.class);

//                menuListVOS.clear();
//                menuListVOS.addAll(newList);

                for (MenuListVO menuListVO : newList){
                    Log.d("getMenuList",menuListVO.getMenuName());
                    Log.d("getMenuList",menuListVO.getMenuPath());
                    switch (menuListVO.getMenuName()){
                        case "????????????":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_warehouse));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_warehouse));
                            break;
                        case "????????????":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_transfer));
                            break;
                        case "????????????":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_receive));
                            break;
                        case "????????????":
                            mComponents.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(),"{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));
                            mPages.add(new PageInfo(menuListVO.getMenuName(),menuListVO.getMenuPath(), "{\"\":\"\"}", CoreAnim.slide, R.drawable.ic_examine));
                            break;
                    }
                }

                WidgetUtils.initGridRecyclerView(mRecyclerView, 3, DensityUtils.dp2px(2));
                mWidgetItemAdapter = new WidgetItemAdapter(sortPageInfo(mComponents));
                mWidgetItemAdapter.setOnItemClickListener(OnItemClickListener);
                mRecyclerView.setAdapter(mWidgetItemAdapter);
            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                XToastUtils.error(error.getString("message"));
            }
        });

    }


    /**
     * @return ????????????
     */
    protected abstract List<PageInfo> getPageContents();

    /**
     * ????????????
     *
     * @param pageInfoList
     * @return
     */
    private List<PageInfo> sortPageInfo(List<PageInfo> pageInfoList) {
        Collections.sort(pageInfoList, (o1, o2) -> o1.getClassPath().compareTo(o2.getClassPath()));
        return pageInfoList;
    }

    @Override
    @SingleClick
    public void onItemClick(View itemView, PageInfo widgetInfo, int pos) {
        if (widgetInfo != null) {
           int organizationId =  MMKVUtils.getInt("organizationId",0);
            switch (widgetInfo.getName()){
                case "????????????":
                    openNewPage(OrganizationUser.class,"id",organizationId);
                    break;
                case "????????????":
                    openNewPage(PersonalWarehouse.class,"id",organizationId);
                    break;
                case "????????????":
                    openNewPage(Examine.class,"id",organizationId);
                    break;
                case "????????????":
                    openNewPage(OrganizationContainersFragment.class,"id",organizationId);
                    break;
                case "????????????":
                    openNewPage(Share.class,"id",organizationId);
                    break;
                case "????????????":
                    openNewPage(Transfer.class,"id",organizationId);
                    break;
                case "????????????":
                    openNewPage(Receive.class,"id",organizationId);
                    break;
                default:
            }

        }
    }

    public MainActivity getContainer() {
        return (MainActivity) getActivity();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //???????????????????????????title
        super.onConfigurationChanged(newConfig);
        ViewGroup root = (ViewGroup) getRootView();
        if (root.getChildAt(0) instanceof TitleBar) {
            root.removeViewAt(0);
            initTitle();
        }
    }
}
