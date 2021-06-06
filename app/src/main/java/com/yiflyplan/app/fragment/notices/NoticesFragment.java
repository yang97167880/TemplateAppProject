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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yiflyplan.app.MyCP;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.ChartInfo;
import com.yiflyplan.app.adapter.entity.NoticeInfo;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.UserInfoFragment;
import com.yiflyplan.app.utils.DemoDataProvider;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.MapDataCache;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * {@link XUIGroupListView} 的使用示例
 *
 * @author xuexiang
 * @since 2019/1/3 上午11:38
 */
@Page(name = "Session",anim = CoreAnim.none)
public class NoticesFragment extends BaseFragment {


    @BindView(R.id.notices_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.notices_refreshLayout)
    SmartRefreshLayout refreshLayout;

    private WebSocketClient chatSocket;
    private SimpleDelegateAdapter<NoticeInfo> mNoticeAdapter;
    private ContentResolver cr ;
    private List<NoticeInfo> noticeInfos = new ArrayList<>();

    private int SessionId = 0;
    private Handler mMainHandler;

    private int userId;

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

        //获取消息提供者
        cr = getActivity().getContentResolver();

         userId = MMKVUtils.getInt("userId",0);

        initHandler();

        connectSocket(userId);

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
                        RadiusImageView avatar = (RadiusImageView) view;
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getAvatar()));
                    }, R.id.user_avatar);

                    holder.bindDataToViewById(view -> {
                        TextView nickname = (TextView) view;
                        nickname.setText(model.getNickName());
                    }, R.id.tv_nickname);

                    holder.bindDataToViewById(view -> {
                        TextView message = (TextView) view;
                        message.setText(model.getNewMessage());
                    }, R.id.tv_new_message);

                    holder.bindDataToViewById(view -> {
                        TextView newDate = (TextView) view;
                        newDate.setText(model.getNewDate());
                    }, R.id.tv_new_date);

                    holder.bindDataToViewById(view -> {
                        TextView unreadCount = (TextView) view;
                        int count = model.getUnreadCount();
                        if(count == 0){
                            unreadCount.setVisibility(View.INVISIBLE);
                        }else {
                            unreadCount.setVisibility(View.VISIBLE);
                            unreadCount.setText(String.valueOf(model.getUnreadCount()));
                        }
                    }, R.id.tv_unread_count);



                    holder.click(R.id.notices_view_item,v -> {
                        CurrentUserVO currentUserVO = new CurrentUserVO();
                        currentUserVO.setUserName(model.getNickName());
                        currentUserVO.setUserAvatar(model.getAvatar());
                        currentUserVO.setUserId(model.getUserId());
                        openNewPage(ChartRoomFragment.class,"currentUserVO", currentUserVO);
                    });
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
//                user = (CurrentUserVO) MapDataCache.getCache("user", null);
//                int userId = user.getUserId();

                getSessionList(userId);
                mNoticeAdapter.refresh(noticeInfos);
                refreshLayout.finishRefresh();
            }, 1000);
        });


        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    /**
     * 连接Socket
     *
     * @param userId
     */
    private void connectSocket(int userId) {
        System.out.printf("unread %s\n", userId);
        chatSocket = new WebSocketClient(URI.create(String.format("ws://118.190.97.125:8080/ws/chat/unread/%s", userId))) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("WEBSOCKET", "connected");
            }

            @Override
            public void onMessage(String message) {
                try {
                    JSONObject result = new JSONObject(message);

                    int fromUserId = result.getInt("fromUserId");
                    int toUserId = result.getInt("toUserId");
                    checkSession(fromUserId,toUserId);
                    String newContent = result.getString("content");
                    String date = result.getString("sendTime");
                    String newCreateDate = date.substring(0,10);
                    int unreadCount = result.getInt("unreadCount");


                    String[] selectionArg = {String.valueOf(SessionId)};
                    String selections = MyCP.Session.id + "=?";

                    ContentValues cv = new ContentValues();
                    cv.put(MyCP.Session.LastMessage, newContent);
                    cv.put(MyCP.Session.LastDate, newCreateDate);
                    cv.put(MyCP.Session.unreadCount, unreadCount);
                    cr.update(MyCP.Session.CONTENT_URI, cv, selections, selectionArg);


                    Message msg = Message.obtain();
                    msg.what = 0;
                    mMainHandler.sendMessage(msg);

                    Log.d("WEBSOCKET", result.toString());
//                    ToastUtils.toast(result.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.d("WEBSOCKET", message);
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
//                        user = (CurrentUserVO) MapDataCache.getCache("user", null);
//                        int userId = user.getUserId();
                        getSessionList(userId);
                        //刷新UI
                        mNoticeAdapter.refresh(noticeInfos);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };

    }

    /**
     * 获取聊天列表
     * @param ownId
     */
    private void getSessionList(int ownId){

        Uri myURI = MyCP.Session.CONTENT_URI;

        String[] selectionArgs = {String.valueOf(ownId)};
        String[] columns = new String[]{MyCP.Session.id,MyCP.Session.userId,MyCP.Session.userAvatar,MyCP.Session.userName,MyCP.Session.unreadCount,MyCP.Session.LastMessage,MyCP.Session.LastDate};
        String selection = MyCP.Session.ownId + "=?";
        Cursor cursor = cr.query(myURI, columns, selection, selectionArgs, null);

        List<NoticeInfo> newList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MyCP.Session.id));
            if(id == SessionId && SessionId!=0){
                SessionId = 0;
            }else {
                getMessage(id);
            }

        }

        cursor.close();


        Cursor cursor1 = cr.query(myURI, columns, selection, selectionArgs, null);

        while (cursor1.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndex(MyCP.Session.id));
            int userId = cursor1.getInt(cursor1.getColumnIndex(MyCP.Session.userId));
            String userAvatar = cursor1.getString(cursor1.getColumnIndex(MyCP.Session.userAvatar));
            String userName = cursor1.getString(cursor1.getColumnIndex(MyCP.Session.userName));
            int unreadCount = cursor1.getInt(cursor1.getColumnIndex(MyCP.Session.unreadCount));
            String lastMessage = cursor1.getString(cursor1.getColumnIndex(MyCP.Session.LastMessage));
            String lastDate = cursor1.getString(cursor1.getColumnIndex(MyCP.Session.LastDate));

            newList.add(new NoticeInfo(userName,userAvatar,lastMessage,lastDate,unreadCount,userId));




        }

        noticeInfos.clear();
        noticeInfos.addAll(newList);


        cursor1.close();
    }

    /**
     * 获取最后一条消息
     * @param sessionId
     */
    private void getMessage(int sessionId){
        Uri myURI = MyCP.ChatRecords.CONTENT_URI;

        String[] selectionArgs = {String.valueOf(sessionId)};
        String[] columns = new String[]{MyCP.ChatRecords.content,MyCP.ChatRecords.createDate};
        String selection = MyCP.ChatRecords.sessionId + "=?";
        Cursor cursor = cr.query(myURI, columns, selection, selectionArgs, null);

        String lastMessage = "开始聊天";
        String lastDate = "2021-10-10";

        while (cursor.moveToNext()) {
            lastMessage = cursor.getString(cursor.getColumnIndex(MyCP.ChatRecords.content));
            String date = cursor.getString(cursor.getColumnIndex(MyCP.ChatRecords.createDate));
            lastDate = date.substring(0,10);
        }

        String[] selectionArg = {String.valueOf(sessionId)};
        String selections = MyCP.Session.id + "=?";

        ContentValues cv = new ContentValues();
        cv.put(MyCP.Session.LastMessage, lastMessage);
        cv.put(MyCP.Session.LastDate, lastDate);
        cr.update(MyCP.Session.CONTENT_URI, cv, selections, selectionArg);

        cursor.close();
    }

    /**
     * 检查会话是否存在
     * @param userId
     */
    private void checkSession(int userId,int ownId) {
        Uri myURI = MyCP.Session.CONTENT_URI;

        String[] selectionArgs = {String.valueOf(userId),String.valueOf(ownId)};
        String[] columns = new String[]{MyCP.Session.id};
        String selection = MyCP.Session.userId + "=? and "+MyCP.Session.ownId +"=?";
        Cursor cursor = cr.query(myURI, columns, selection, selectionArgs, null);

        if (cursor.getCount() == 0) {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("userId", String.valueOf(userId));
            MyHttp.get("/user/getUserInfo", TokenUtils.getToken(), params, new MyHttp.Callback() {
                @Override
                public void success(JSONObject data) {
                    CurrentUserVO currentUserVO = ReflectUtil.convertToObject(data, CurrentUserVO.class);

                    ContentValues cv = new ContentValues();
                    cv.put(MyCP.Session.ownId, ownId);
                    cv.put(MyCP.Session.userId, userId);
                    cv.put(MyCP.Session.userAvatar, currentUserVO.getUserAvatar());
                    cv.put(MyCP.Session.userName, currentUserVO.getUserName());
                    cv.put(MyCP.Session.unreadCount, 0);
                    cv.put(MyCP.Session.LastMessage, "有新的未读消息");

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String createDate = df.format(new Date());
                    cv.put(MyCP.Session.LastDate,createDate);
                    cr.insert(myURI, cv);

                    Cursor cursor1 = cr.query(myURI, columns, selection, selectionArgs, null);
                    while (cursor1.moveToNext()) {
                        SessionId = cursor1.getInt(cursor1.getColumnIndex(MyCP.Session.id));
                    }
                    cursor1.close();
                }
                @Override
                public void fail(JSONObject error) throws JSONException {
                    XToastUtils.error(error.getString("message"));
                }
            });

        }else {
            while (cursor.moveToNext()) {
                SessionId = cursor.getInt(cursor.getColumnIndex(MyCP.Session.id));
            }
        }
        cursor.close();
    }

}
