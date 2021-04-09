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

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yiflyplan.app.R;
import com.yiflyplan.app.activity.MainActivity;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.WidgetItemAdapter;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.fragment.organization.components.Examine;
import com.yiflyplan.app.fragment.organization.components.OrganizationUser;
import com.yiflyplan.app.fragment.organization.components.OrganizationWarehouse;
import com.yiflyplan.app.fragment.organization.components.PersonalWarehouse;
import com.yiflyplan.app.fragment.organization.components.Receive;
import com.yiflyplan.app.fragment.organization.components.Share;
import com.yiflyplan.app.fragment.organization.components.Transfer;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public abstract class BaseHomeFragment extends BaseFragment implements RecyclerViewHolder.OnItemClickListener<PageInfo> {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private OrganizationVO organizationVO;

    @Override
    protected TitleBar initTitle() { return null; }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_organization_container;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        organizationVO = (OrganizationVO) bundle.getSerializable("organization");
        Log.e("VO",String.valueOf(organizationVO));
        initRecyclerView();
    }

    private void initRecyclerView() {
        WidgetUtils.initGridRecyclerView(mRecyclerView, 3, DensityUtils.dp2px(2));

        WidgetItemAdapter mWidgetItemAdapter = new WidgetItemAdapter(sortPageInfo(getPageContents()));
        mWidgetItemAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mWidgetItemAdapter);
    }

    /**
     * @return 页面内容
     */
    protected abstract List<PageInfo> getPageContents();

    /**
     * 进行排序
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
            switch (widgetInfo.getName()){
                case "机构成员":
                    openNewPage(OrganizationUser.class,"id",organizationVO.getOrganizationId());
                    break;
                case "个人仓库":
                    openNewPage(PersonalWarehouse.class,"id",organizationVO.getOrganizationId());
                    break;
                case "审核申请":
                    openNewPage(Examine.class,"id",organizationVO.getOrganizationId());
                    break;
                case "机构仓库":
                    openNewPage(OrganizationWarehouse.class,"id",organizationVO.getOrganizationId());
                    break;
                case "分享机构":
                    openNewPage(Share.class,"id",organizationVO.getOrganizationId());
                    break;
                case "产品转移":
                    openNewPage(Transfer.class,"id",organizationVO.getOrganizationId());
                    break;
                case "产品接收":
                    openNewPage(Receive.class,"id",organizationVO.getOrganizationId());
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
        //屏幕旋转时刷新一下title
        super.onConfigurationChanged(newConfig);
        ViewGroup root = (ViewGroup) getRootView();
        if (root.getChildAt(0) instanceof TitleBar) {
            root.removeViewAt(0);
            initTitle();
        }
    }
}
