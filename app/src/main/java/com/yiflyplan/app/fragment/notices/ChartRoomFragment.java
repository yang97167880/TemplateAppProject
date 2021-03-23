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

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.ChartInfo;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.utils.DemoDataProvider;

import java.util.Objects;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(anim = CoreAnim.none)
public class ChartRoomFragment extends BaseFragment {

    @BindView(R.id.chatRoom_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.chatRoom_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private SimpleDelegateAdapter<ChartInfo> mChartAdapter;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @SuppressLint("ResourceAsColor")
    @Override
    protected TitleBar initTitle() {
       return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_room;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mChartAdapter = new BroccoliSimpleDelegateAdapter<ChartInfo>(R.layout.adapter_chart_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyInfo(ChartInfo.class)) {

            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ChartInfo model, int position) {
                if(model != null){
//                    int position  = model.getPosition()==0?View.GONE:View.VISIBLE;
//                    holder.bindDataToViewById(view ->{
//
//                    },R.id.right_chart);
                    int avatar_popup_id;
                    int avatar_text_id;
                    if(model.getPosition() == 0){
                        avatar_popup_id = R.id.avatar_popup_left;
                        avatar_text_id = R.id.avatar_text_left;
                        holder.bindDataToViewById(view -> {
                            RelativeLayout layout = (RelativeLayout) view;
                            layout.setVisibility(View.GONE);
                        },R.id.right_chart);
                        holder.bindDataToViewById(view -> {
                            LinearLayout layout = (LinearLayout) view;
                            layout.setVisibility(View.VISIBLE);
                        },R.id.left_chart);
                    }else{
                        avatar_popup_id = R.id.avatar_popup_right;
                        avatar_text_id = R.id.avatar_text_right;
                        holder.bindDataToViewById(view -> {
                            RelativeLayout layout = (RelativeLayout) view;
                            layout.setVisibility(View.VISIBLE);
                        },R.id.right_chart);
                        holder.bindDataToViewById(view -> {
                            LinearLayout layout = (LinearLayout) view;
                            layout.setVisibility(View.GONE);
                        },R.id.left_chart);
                    }
                    holder.bindDataToViewById(view -> {
                        ImageView Iview = (ImageView) view;
                        Iview.setImageResource(model.getAvatar());
                    },avatar_popup_id);
                    holder.bindDataToViewById(view -> {
                        TextView textview = (TextView) view;
                        textview.setText(model.getContent());
                    },avatar_text_id);
                }
            }



            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.notices_view_item)
                );
            }
        };
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mChartAdapter);
        recyclerView.setAdapter(delegateAdapter);

    }
    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mChartAdapter.refresh(DemoDataProvider.getDemoChartInfos());
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mChartAdapter.loadMore(DemoDataProvider.getDemoChartInfos());
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }
}
