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

package com.yiflyplan.app.fragment.notices;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.NoticeInfo;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.utils.DemoDataProvider;

import java.util.Objects;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * {@link XUIGroupListView} 的使用示例
 *
 * @author xuexiang
 * @since 2019/1/3 上午11:38
 */
@Page(anim = CoreAnim.none)
public class NoticesFragment extends BaseFragment {


    @BindView(R.id.notices_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.notices_refreshLayout)
    SmartRefreshLayout refreshLayout;


    private SimpleDelegateAdapter<NoticeInfo> mNoticeAdapter;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notices;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);
//
//
        mNoticeAdapter = new BroccoliSimpleDelegateAdapter<NoticeInfo>(R.layout.adapter_notices_view_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyInfo(NoticeInfo.class)) {


            @Override
            protected void onBindData(MyRecyclerViewHolder holder, NoticeInfo model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        SuperTextView noticeView = (SuperTextView) view;
                        noticeView.setLeftTopString(model.getNickName() );
                        noticeView.setLeftBottomString(model.getNewMessage() );
                        noticeView.setRightTopString(model.getNewDate());
                        noticeView.setLeftIcon(R.drawable.icon_head_default);
                    }, R.id.notices_view_item);

                    holder.click(R.id.notices_view_item,v -> openNewPage(ChartRoomFragment.class));
                }

            }


            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.notices_view_item)
                );
            }
        };
//
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mNoticeAdapter);
//
        recyclerView.setAdapter(delegateAdapter);
    }


    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mNoticeAdapter.refresh(DemoDataProvider.getDemoNoticeInfos());
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mNoticeAdapter.loadMore(DemoDataProvider.getDemoNoticeInfos());
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

}
