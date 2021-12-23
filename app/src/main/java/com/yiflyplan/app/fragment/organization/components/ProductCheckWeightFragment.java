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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xutil.display.ImageUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.ProductVO;
import com.yiflyplan.app.adapter.VO.UploadData;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.SearchFragment;
import com.yiflyplan.app.fragment.blueTooth.EntryGarbageFragment;
import com.yiflyplan.app.fragment.components.DrawablePreviewFragment;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;

import static com.yiflyplan.app.fragment.blueTooth.BlueToothFragment.CHECK_WEIGHT;
import static com.yiflyplan.app.fragment.blueTooth.BlueToothFragment.UPLOAD;
import static com.yiflyplan.app.fragment.components.DrawablePreviewFragment.BITMAP;
import static com.yiflyplan.app.utils.ImageConversionUtil.base64ToBitmap;

@Page(name = "医废二次核重")
public class ProductCheckWeightFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.old_weight)
    TextView oldWeight;
    @BindView(R.id.new_weight)
    TextView newWeight;
    @BindView(R.id.btn_check_weight)
    SuperButton btnCheckWeight;

    private UploadData nextParams;
    private ProductVO product;


    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private String mac;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice remoteDevice;
    private BluetoothSocket bluetoothSocket;
    private boolean hasConnect = false;

    private AlertDialog entry_info_dialog;
    private TextView entry_message;

    private ScrollView message_scroll;

    private Timer timer = new Timer();

    private final Integer RECEIVE_CODE = 1; //接受数据成功状态码
    private final Integer RE_RECEIVE_CODE = 2;//接受数据失败，冲录入状态码


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_product_weight;
    }

    @Override
    protected void initViews() {
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            throw new AssertionError();
        }
        product = (ProductVO) bundle.getSerializable(CHECK_WEIGHT);
        if (product == null) {
            throw new AssertionError();
        }
        oldWeight.setText(String.valueOf(product.getItemWeight()));

        nextParams = (UploadData) bundle.getSerializable(UPLOAD);
        if (nextParams == null) {
            throw new AssertionError();
        }
        mac = nextParams.getAddress();
        if (mac == null) {
            XToastUtils.info("找不到该蓝牙，请重试...");
            popToBack();
        }
        try {
            initBT();
            createEntryInfoDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    
    @Override
    protected void initListeners() {
        super.initListeners();
        btnCheckWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //上传参数
                UploadDifference();
            }
        });
    }

    //初始化蓝牙
    private void initBT() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            XToastUtils.error("该设备可能不支持蓝牙");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        try {
            //避免用户一直点击连接
            if (!hasConnect) {
                //开始尝试连接
                remoteDevice = bluetoothAdapter.getRemoteDevice(mac);
                bluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                bluetoothSocket.connect();
                hasConnect = true;
                XToastUtils.success("连接成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            XToastUtils.error("连接失败");
            closeBlueSocket();
            Objects.requireNonNull(this).popToBack();
        }
        //接收数据线程接受输入流
        try {
            readThread.setInputStream(bluetoothSocket.getInputStream());
            readThread.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭蓝牙接收器
    private void closeBlueSocket() {
        hasConnect = false;
        try {
            bluetoothSocket.close();
            bluetoothSocket = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothAdapter = null;
        remoteDevice = null;
        closeBlueSocket();
    }

    private void createEntryInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.entry_info_dialog, null);
        entry_message = view.findViewById(R.id.entry_message);
        message_scroll = view.findViewById(R.id.message_scroll);
        builder.setView(view);

        entry_info_dialog = builder.create();
        entry_info_dialog.setCanceledOnTouchOutside(false);
        entry_info_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    entry_info_dialog.dismiss();
                    Objects.requireNonNull(getActivity()).finish();
                    return true;
                }
                return false;
            }
        });
        entry_info_dialog.show();
    }

    private final ReadThread readThread = new ReadThread();

    class ReadThread extends Thread {
        private volatile boolean running;//线程是否在运行
        private InputStream inputStream;
        private StringBuilder bluetoothMessage;
        private boolean receiving;

        ReadThread() {
            this.running = false;
            inputStream = null;
            receiving = true;
        }

        @Override
        public synchronized void run() {
            //接收线程
            try {
                byte[] buffer = new byte[1024];
                byte[] buffer_new = new byte[1024];
                StringBuilder bluetoothMessageTmp = new StringBuilder();
                while (receiving) {
                    //有数据进入，且同时本线程可以接受数据,一直接受到一段时间没有数据进入才认定本次接受完毕
                    while (inputStream.available() != 0 && running) {
                        sleep(1000);
                        int msgNum = inputStream.read(buffer);         //读入数据到buffer并获得信息字数
                        //i指针走得快，realIndex指针走得慢，合并相邻的0x0d 0x0a为0x0a，进行格式转换
                        int realIndex = 0;//格式转换后的真实长度下标
                        for (int i = 0; i < msgNum; ++i, ++realIndex) {
                            if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
                                buffer_new[realIndex] = 0x0a;
                                ++i;
                            } else {
                                buffer_new[realIndex] = buffer[i];
                            }
                        }
                        bluetoothMessageTmp.append(new String(buffer_new, 0, realIndex));   //写入接收缓存

                    }
                    if (bluetoothMessageTmp.toString().length() > 0) {
                        if (checkData(bluetoothMessageTmp.toString())) {
                            bluetoothMessage = bluetoothMessageTmp;
                            //发送显示消息，进行显示刷新
                            Message message = new Message();
                            message.obj = "成功收到重量消息....";
                            message.what = RECEIVE_CODE;
                            handler.sendMessage(message);

                            bluetoothMessageTmp = new StringBuilder();
                        } else {
                            Message message = new Message();
                            message.obj = "医疗废物重量数据格式错误，请重新录入....";
                            message.what = RE_RECEIVE_CODE;
                            handler.sendMessage(message);
                            bluetoothMessageTmp = new StringBuilder();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getBluetoothMessage() {
            return bluetoothMessage == null ? "Empty" : bluetoothMessage.toString();
        }

        void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        void open() {
            if (!running) {
                this.start();
            }
            running = true;
        }

        void close() {
            try {
                this.receiving = false;
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private final MessageReceiveHandler handler = new MessageReceiveHandler();

    @SuppressLint("HandlerLeak")
    class MessageReceiveHandler extends Handler {

        @Override
        public synchronized void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //接收到数据后，刷新用户界面
            String message = entry_message.getText().toString();
            message += "\nserver:~$ ";
            message += msg.obj;

            String weight = readThread.getBluetoothMessage();
            while (weight == null) {
                weight = readThread.getBluetoothMessage();
            }

            entry_message.setText(message);
            message_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            if (msg.what != RE_RECEIVE_CODE) {
                newWeight.setText(readThread.getBluetoothMessage());
            }


            if (msg.what == RECEIVE_CODE) {
                message += "\nserver:~$ ";
                message += "请开始上传差值";
                entry_message.setText(message);
                message_scroll.fullScroll(ScrollView.FOCUS_DOWN);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        entry_info_dialog.dismiss();

                        this.cancel();

                    }
                }, 1000);
            }

        }


    }

    //检验蓝牙接受的数据格式
    private boolean checkData(String data) {
        try {
            if (data.length() > 5) {
                return false;
            }
            Integer.parseInt(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    private void sendMessageToRemoteBluetooth(String message) {
//        try {
//            OutputStream outputStream = bluetoothSocket.getOutputStream();
//            byte[] bytes = message.getBytes();
//            int addSpaceNum = 0;
//            //计算出0x0a个数，为下方准换格式添加0x0d申请新空间
//            for (byte b : bytes) {
//                if (b == 0x0a) {
//                    ++addSpaceNum;
//                }
//            }
//            //申请新空间
//            byte[] newBytes = new byte[bytes.length + addSpaceNum];
//            //开始转换
//            for (int i = 0; i < bytes.length; ++i) {
//                if (bytes[i] == 0x0a) {
//                    newBytes[i] = 0x0d;
//                    newBytes[++i] = 0x0a;
//                } else {
//                    newBytes[i] = bytes[i];
//                }
//            }
//            //转换完成，写入流
//            outputStream.write(newBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


    @Override
    public void onClick(View v) {

    }

    /**
     * 上传医废差值
     */
    private void UploadDifference(){
//        LinkedHashMap<String, String> params = new LinkedHashMap<>();
//        MyHttp.postJson("", TokenUtils.getToken(), params, new MyHttp.Callback() {
//
//            @Override
//            public void success(JSONObject data) throws JSONException {
//
//            }
//
//            @Override
//            public void fail(JSONObject error) throws JSONException {
//
//            }
//        });
    }

}
