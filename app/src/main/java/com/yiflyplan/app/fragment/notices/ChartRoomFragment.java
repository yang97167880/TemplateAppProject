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

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xpage.utils.TitleUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xui.widget.toast.XToast;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.ChartInfo;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.utils.DemoDataProvider;
import com.yiflyplan.app.utils.MapDataCache;
import com.yiflyplan.app.utils.XToastUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(name="成员A")
public class ChartRoomFragment extends BaseFragment {

    @BindView(R.id.chatRoom_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.chatRoom_refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.edt_message)
    EditText edtMessage;
    @BindView(R.id.tv_send_message)
    Button tvSendMessage;


    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    // Socket变量
    private Socket socket;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream is ;

    // 输入流读取器对象
    InputStreamReader isr ;
    BufferedReader br ;

    // 接收服务器发送过来的消息
    String response;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;



    private List<ChartInfo> chartInfos = new ArrayList<>();
    private SimpleDelegateAdapter<ChartInfo> mChartAdapter;
    private static CurrentUserVO currentUserVO;
    private static CurrentUserVO user;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_room;
    }


    @Override
    protected TitleBar initTitleBar() {
        Bundle bundle = getArguments();
        currentUserVO = (CurrentUserVO) bundle.getSerializable("currentUserVO");
        if (currentUserVO != null) {
            return TitleUtils.addTitleBarDynamic((ViewGroup) mRootView,currentUserVO.getUserName(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popToBack();
                }
            });
        }else {
            return super.initTitleBar();
        }
    }

    @Override
    protected void initViews() {

        Bundle bundle = getArguments();
        currentUserVO = (CurrentUserVO) bundle.getSerializable("currentUserVO");

        user = (CurrentUserVO)MapDataCache.getCache("user",null);
        int userId = user.getUserId();
        int leftUserId = currentUserVO.getUserId();


        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();


// 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        chartInfos.add(new ChartInfo(currentUserVO.getUserAvatar(),response,0));
                        mChartAdapter.refresh(chartInfos);
                        mChartAdapter.setSelectPosition(chartInfos.size()-1);
                        recyclerView.scrollToPosition(mChartAdapter.getSelectPosition());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };

        connectSocket(userId,leftUserId);
//        getMessage();




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
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getAvatar()));
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
                mChartAdapter.refresh(chartInfos);

//                mChartAdapter.refresh(DemoDataProvider.getDemoChartInfos());
                refreshLayout.finishRefresh();
            }, 1000);
        });

        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
//                mChartAdapter.loadMore(chartInfos);

//                mChartAdapter.loadMore(DemoDataProvider.getDemoChartInfos());
                refreshLayout.finishLoadMore();
            }, 1000);
        });

        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果


        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString();
                if(message.length()==0){
                    XToastUtils.error("发送内容不能为空");
                }else {

                    // 利用线程池直接开启一个线程 & 执行该线程
                    mThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 步骤1：从Socket 获得输出流对象OutputStream
                                // 该对象作用：发送数据
                                outputStream = socket.getOutputStream();

                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write((message+"\n").getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                                // 步骤3：发送数据到服务端
                                outputStream.flush();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });


                    chartInfos.add(new ChartInfo(user.getUserAvatar(),message,1));
                    mChartAdapter.refresh(chartInfos);
                    mChartAdapter.setSelectPosition(chartInfos.size()-1);
                    recyclerView.scrollToPosition(mChartAdapter.getSelectPosition());
                    edtMessage.setText("");
                }

            }
        });
    }


    /**
     * 连接Socket
     * @param fromUserId
     * @param toUserId
     */
    private void connectSocket(int fromUserId,int toUserId){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket  = new Socket("118.190.97.125",8080);

                    // 判断客户端和服务器是否连接成功
                    System.out.println(socket.isConnected());
                    Log.d("======", String.valueOf(socket.isConnected()));


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getMessage(){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 步骤1：创建输入流对象InputStream
                    is = socket.getInputStream();

                    if(is!=null){
                        // 步骤2：创建输入流读取器对象 并传入输入流对象
                        // 该对象作用：获取服务器返回的数据
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);

                        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                        response = br.readLine();

                        // 步骤4:通知主线程,将接收的消息显示到界面
                        Message msg = Message.obtain();
                        msg.what = 0;
                        mMainHandler.sendMessage(msg);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
