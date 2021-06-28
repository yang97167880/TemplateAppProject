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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.gson.JsonObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.actionbar.TitleUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xui.widget.toast.XToast;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yiflyplan.app.MyCP;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.ChartInfo;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.utils.DemoDataProvider;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.MapDataCache;
import com.yiflyplan.app.utils.XToastUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(name = "成员A")
public class ChartRoomFragment extends BaseFragment {

    @BindView(R.id.chatRoom_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.chatRoom_refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.edt_message)
    EditText edtMessage;
    @BindView(R.id.tv_send_message)
    Button tvSendMessage;



    //
    private WebSocketClient chatSocket;
    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    private ContentResolver cr ;
    private static final String ACTION = "com.test.action";


    private List<ChartInfo> chartInfos;
    private SimpleDelegateAdapter<ChartInfo> mChartAdapter;
    private static CurrentUserVO currentUserVO;
    private String content;
    private int SessionId;
    private String userAvatar;
    private String lastDate;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_room;
    }


    @Override
    protected TitleBar initTitle() {
        Bundle bundle = getArguments();
        currentUserVO = (CurrentUserVO) bundle.getSerializable("currentUserVO");
        return TitleUtils.addTitleBarDynamic((ViewGroup) getRootView(),currentUserVO.getUserName(), v -> back());
    }

    @Override
    protected void initViews() {

        chartInfos = new ArrayList<>();

        //获取消息提供者
        cr = getActivity().getContentResolver();


        Bundle bundle = getArguments();
        assert bundle != null;
        currentUserVO = (CurrentUserVO) bundle.getSerializable("currentUserVO");

        userAvatar =  MMKVUtils.getString("userAvatar",null);

        int userId = MMKVUtils.getInt("userId",0);
        int leftUserId = currentUserVO.getUserId();

        checkSession(leftUserId,userId);

        initHandler();

        connectSocket(userId, leftUserId);

        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        mChartAdapter = new BroccoliSimpleDelegateAdapter<ChartInfo>(R.layout.adapter_chart_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyInfo(ChartInfo.class)) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, ChartInfo model, int position) {
                if (model != null) {
                    int avatar_popup_id;
                    int avatar_text_id;
                    if (model.getPosition() == 0) {
                        avatar_popup_id = R.id.avatar_popup_left;
                        avatar_text_id = R.id.avatar_text_left;
                        holder.bindDataToViewById(view -> {
                            RelativeLayout layout = (RelativeLayout) view;
                            layout.setVisibility(View.GONE);
                        }, R.id.right_chart);
                        holder.bindDataToViewById(view -> {
                            LinearLayout layout = (LinearLayout) view;
                            layout.setVisibility(View.VISIBLE);
                        }, R.id.left_chart);
                    } else {
                        avatar_popup_id = R.id.avatar_popup_right;
                        avatar_text_id = R.id.avatar_text_right;
                        holder.bindDataToViewById(view -> {
                            RelativeLayout layout = (RelativeLayout) view;
                            layout.setVisibility(View.VISIBLE);
                        }, R.id.right_chart);
                        holder.bindDataToViewById(view -> {
                            LinearLayout layout = (LinearLayout) view;
                            layout.setVisibility(View.GONE);
                        }, R.id.left_chart);
                    }
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getAvatar()));
                    }, avatar_popup_id);
                    holder.bindDataToViewById(view -> {
                        TextView textview = (TextView) view;
                        textview.setText(model.getContent());
                    }, avatar_text_id);
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
                getMessage(SessionId);
                mChartAdapter.refresh(chartInfos);
                mChartAdapter.setSelectPosition(chartInfos.size()-1);
                recyclerView.scrollToPosition(mChartAdapter.getSelectPosition());

                refreshLayout.finishRefresh();
            }, 1000);
        });

        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果


        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString();
                if (message.length() == 0) {
                    XToastUtils.error("发送内容不能为空");
                } else {



                    if(chatSocket.isOpen()){
                        chatSocket.send(message);
                    }



                    chartInfos.add(new ChartInfo(userAvatar, message, 1));
                    insertMessage(SessionId,message,1);

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
     *
     * @param fromUserId
     * @param toUserId
     */
    private void connectSocket(int fromUserId, int toUserId) {
        System.out.printf("from %s to %s\n", fromUserId, toUserId);
        chatSocket = new WebSocketClient(URI.create(String.format("ws://118.190.97.125:8080/ws/chat/simple/%s/%s", fromUserId, toUserId))) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("WEBSOCKET", "connected");
            }

            @Override
            public void onMessage(String message) {
                if(message!=null) {
                    try {
                        JSONObject result = new JSONObject(message);
                        content = result.getString("content");

                        chartInfos.add(new ChartInfo(currentUserVO.getUserAvatar(), content, 0));
                        insertMessage(SessionId, content, 0);

                        Message msg = Message.obtain();
                        msg.what = 0;
                        mMainHandler.sendMessage(msg);


                        Log.d("WEBSOCKET", content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("WEBSOCKET", message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WEBSOCKET", "closed");
            }

            @Override
            public void onError(Exception ex) {
                ToastUtils.toast("建立失败");
                Log.d("WEBSOCKET", String.valueOf(ex));
            }
        };
        try {
            chatSocket.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//
    }

    /**
     * 初始化handler
     */
    private void initHandler(){
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        getMessage(SessionId);
                        //刷新UI
                        mChartAdapter.refresh(chartInfos);
                        mChartAdapter.setSelectPosition(chartInfos.size()-1);
                        recyclerView.scrollToPosition(mChartAdapter.getSelectPosition());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };
    }



    /**
     * 检查会话是否存在
     * @param userId
     */
    private void checkSession(int userId,int ownId ) {
        Uri myURI = MyCP.Session.CONTENT_URI;

        String[] selectionArgs = {String.valueOf(userId),String.valueOf(ownId)};
        String[] columns = new String[]{MyCP.Session.id};
        String selection = MyCP.Session.userId + "=? and "+MyCP.Session.ownId +"=?";
        Cursor cursor = cr.query(myURI, columns, selection, selectionArgs, null);

        if (cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put(MyCP.Session.ownId, ownId);
            cv.put(MyCP.Session.userId, userId);
            cv.put(MyCP.Session.userAvatar, currentUserVO.getUserAvatar());
            cv.put(MyCP.Session.userName, currentUserVO.getUserName());
            cv.put(MyCP.Session.unreadCount, 0);
            cv.put(MyCP.Session.LastMessage, "有新的未读消息！");

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String createDate = df.format(new Date());
            cv.put(MyCP.Session.LastDate,createDate);
            cr.insert(myURI, cv);

            Cursor cursor1 = cr.query(myURI, columns, selection, selectionArgs, null);
            while (cursor1.moveToNext()) {
                SessionId = cursor1.getInt(cursor1.getColumnIndex(MyCP.Session.id));
            }
            cursor1.close();
        }else {
            while (cursor.moveToNext()) {
                SessionId = cursor.getInt(cursor.getColumnIndex(MyCP.Session.id));
            }
        }
        cursor.close();
    }

    /**
     * 获取聊天记录
     * @param sessionId
     */
    private void getMessage(int sessionId){

        Uri myURI = MyCP.ChatRecords.CONTENT_URI;

        String[] selectionArgs = {String.valueOf(sessionId)};
        String[] columns = new String[]{MyCP.ChatRecords.content,MyCP.ChatRecords.position,MyCP.ChatRecords.createDate};
        String selection = MyCP.ChatRecords.sessionId + "=?";
        Cursor cursor = cr.query(myURI, columns, selection, selectionArgs, null);

        List<ChartInfo> newList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(MyCP.ChatRecords.content));
            int position = cursor.getInt(cursor.getColumnIndex(MyCP.ChatRecords.position));

            lastDate = cursor.getString(cursor.getColumnIndex(MyCP.ChatRecords.createDate));

            if(position == 0){
                newList.add(new ChartInfo(currentUserVO.getUserAvatar(),content,position));
            }else {
                newList.add(new ChartInfo(userAvatar, content, position));
            }
        }

        chartInfos.clear();
        chartInfos.addAll(newList);

        cursor.close();
    }

    /**
     * 插入聊天记录
     * @param sessionId
     * @param content
     * @param position
     */
    private void insertMessage(int sessionId,String content,int position){
        Uri myURI = MyCP.ChatRecords.CONTENT_URI;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String createDate = df.format(new Date());
        lastDate = createDate;
        ContentValues cv = new ContentValues();
        cv.put(MyCP.ChatRecords.sessionId, sessionId);
        cv.put(MyCP.ChatRecords.content, content);
        cv.put(MyCP.ChatRecords.position, position);
        cv.put(MyCP.ChatRecords.createDate, createDate);

        cr.insert(myURI, cv);
    }

    /**
     * 监听返回键
     * @param keyCode
     * @param event
     * @return
     *返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            back();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //发送广播
        Intent intent = new Intent(ACTION);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        back();
    }

    /**
     *返回
     */
    private void back(){
        try {
            chatSocket.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] selectionArg = {String.valueOf(SessionId)};
        String selections = MyCP.Session.id + "=?";

        ContentValues cv = new ContentValues();
        cv.put(MyCP.Session.unreadCount, 0);

        if(chartInfos.size()!=0){
            String lastMessage = chartInfos.get(chartInfos.size()-1).getContent();
            cv.put(MyCP.Session.LastMessage,lastMessage);
            cv.put(MyCP.Session.LastDate, lastDate);
        }else {
            cv.put(MyCP.Session.LastMessage,"可以开始聊天了～");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String lastDate = df.format(new Date());
            cv.put(MyCP.Session.LastDate, lastDate);
        }

        cr.update(MyCP.Session.CONTENT_URI, cv, selections, selectionArg);


        popToBack();
    }
}