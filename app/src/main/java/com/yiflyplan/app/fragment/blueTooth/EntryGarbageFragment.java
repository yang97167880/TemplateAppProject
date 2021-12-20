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

package com.yiflyplan.app.fragment.blueTooth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.JsonArray;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.toast.XToast;
import com.xuexiang.xutil.XUtil;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.SearchFragment;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author xhy
 */
@Page(name = "物品录入")
public class EntryGarbageFragment extends BaseFragment {

    @BindView(R.id.garbage_weight)
    TextView garbageWeight;
    @BindView(R.id.btn_type)
    Button btnType;
    @BindView(R.id.btn_package)
    Button btnPackage;
    @BindView(R.id.btn_pollution)
    Button btnPollution;
    @BindView(R.id.uploadData)
    ButtonView uploadData;
    @BindView(R.id.edt_page_number)
    EditText edtPageNumber;

    private String[] mTypeOption;
    private String[] mTypeOptionId;
    private String[] mPackageOption;
    private String[] mPackageOptionId;
    private String[] mPollutionOption;
    private String[] mPollutionOptionId;

    private int typeSelectOption = -1;
    private int packageSelectOption = -1;
    private int pollutionSelectOption = -1;

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private final static String TYPE_DEFAULT = "请选择垃圾类型";
    private final static String PACKAGE_DEFAULT = "请选择包装类型";
    private final static String POLLUTION_DEFAULT = "请选择污染级别";
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

    SearchFragment.UploadData nextParams;

    private static int SleepTime = 2000;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_entry_garbage;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initViews() {
        getTypeOption();
        getPackageOption();
        getPollutionOption();

        Bundle bundle = this.getArguments();
        if (bundle == null) {
            throw new AssertionError();
        }
        nextParams = (SearchFragment.UploadData) bundle.getSerializable("uploadData");
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
            setUploadEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    Objects.requireNonNull(EntryGarbageFragment.this.getActivity()).finish();
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
                garbageWeight.setText(readThread.getBluetoothMessage());
            }


            if (msg.what == RECEIVE_CODE) {
                message += "\nserver:~$ ";
                message += "请开始选择其他字段";
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

    private void setUploadEvent() {
        uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelectValues()) {
                    entry_info_dialog.show();
                    //发送显示消息，进行显示刷新
                    Message message = new Message();
                    message.obj = "正在上传数据....";
                    handler.sendMessage(message);

                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("departmentId", String.valueOf(nextParams.getDepartmentId()));
                    params.put("organizationId", String.valueOf(nextParams.getOrganizationId()));
                    params.put("itemWeight", garbageWeight.getText().toString());
                    params.put("itemTypeId", mTypeOptionId[typeSelectOption]);
                    params.put("bagTypeId", mPackageOptionId[packageSelectOption]);
                    params.put("pollutionLevelId", mPollutionOptionId[pollutionSelectOption]);
//                    Toast.makeText(EntryGarbageActivity.this, "上传中...", Toast.LENGTH_SHORT).show();
                    MyHttp.postJson("/product/createProduct", TokenUtils.getToken(), params, new MyHttp.Callback() {
                        @Override
                        public void success(JSONObject data) throws JSONException {
                            if(edtPageNumber.getText().toString().equals("")){
                                sendMessageToRemoteBluetooth(data.getString("itemEnCoding"));
                            }else {
                                int printNum = Integer.parseInt(edtPageNumber.getText().toString());
                                if (printNum<=1){
                                    sendMessageToRemoteBluetooth(data.getString("itemEnCoding"));
                                }else {
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            try {
                                                for(int i = 1;i<=printNum;i++){
                                                    Message message = new Message();
                                                    message.obj = "正在发送第"+i+"条数据...";
                                                    handler.sendMessage(message);
                                                    sendMessageToRemoteBluetooth(data.getString("itemEnCoding"));
                                                    Thread.sleep(SleepTime);
                                                }
                                            } catch (InterruptedException | JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();
                                }
                            }
//                            Toast.makeText(EntryGarbageActivity.this, "上传成功,请继续...", Toast.LENGTH_SHORT).show();

                            //发送显示消息，进行显示刷新
                            Message message = new Message();
                            message.obj = "上传成功...";
                            handler.sendMessage(message);
                            message = new Message();
                            message.obj = "【您可以继续录入/退出请按返回键】";
                            handler.sendMessage(message);
                            // resetThisPage();
                        }

                        @Override
                        public void fail(JSONObject error) {
                            Message message = new Message();
                            message.obj = "上传失败，请重试....";
                            handler.sendMessage(message);
                        }
                    });


                } else {
                    XToastUtils.info("请将字段选择完整");
                }

            }
        });
    }

    private void sendMessageToRemoteBluetooth(String message) {
        try {
            OutputStream outputStream = bluetoothSocket.getOutputStream();
            byte[] bytes = message.getBytes();
            int addSpaceNum = 0;
            //计算出0x0a个数，为下方准换格式添加0x0d申请新空间
            for (byte b : bytes) {
                if (b == 0x0a) {
                    ++addSpaceNum;
                }
            }
            //申请新空间
            byte[] newBytes = new byte[bytes.length + addSpaceNum];
            //开始转换
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] == 0x0a) {
                    newBytes[i] = 0x0d;
                    newBytes[++i] = 0x0a;
                } else {
                    newBytes[i] = bytes[i];
                }
            }
            //转换完成，写入流
            outputStream.write(newBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkSelectValues() {

        return !btnType.getText().equals(TYPE_DEFAULT) && !btnPackage.getText().equals(PACKAGE_DEFAULT) && !btnPollution.getText().equals(PACKAGE_DEFAULT);
    }

    @OnClick({R.id.btn_type, R.id.btn_package, R.id.btn_pollution})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_type:
                showTypeView();
                break;
            case R.id.btn_package:
                showPackageView();
                break;
            case R.id.btn_pollution:
                showPollutionView();
                break;
            default:
                break;
        }
    }

    private void getTypeOption() {
        MyHttp.getJsonList("/productType/getAllProductTypes", TokenUtils.getToken(), new MyHttp.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray options = new JSONArray(data.getString("data"));
                mTypeOption = new String[options.length()];
                mTypeOptionId = new String[options.length()];
                for (int i = 0; i < options.length(); i++) {
                    mTypeOption[i] = options.getJSONObject(i).getString("itemTypeName");
                    mTypeOptionId[i] = options.getJSONObject(i).getString("id");
                }
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
    }

    private void getPackageOption() {
        MyHttp.getJsonList("/bagType/getAllBagTypes", TokenUtils.getToken(), new MyHttp.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray options = new JSONArray(data.getString("data"));
                mPackageOption = new String[options.length()];
                mPackageOptionId = new String[options.length()];
                for (int i = 0; i < options.length(); i++) {
                    mPackageOption[i] = options.getJSONObject(i).getString("bagTypeName");
                    mPackageOptionId[i] = options.getJSONObject(i).getString("id");
                }
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
    }

    private void getPollutionOption() {
        MyHttp.getJsonList("/getAllPollutionLevels", TokenUtils.getToken(), new MyHttp.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JSONObject data) throws JSONException {
                JSONArray options = new JSONArray(data.getString("data"));
                mPollutionOption = new String[options.length()];
                mPollutionOptionId = new String[options.length()];
                for (int i = 0; i < options.length(); i++) {
                    mPollutionOption[i] = options.getJSONObject(i).getString("pollutionLevelName");
                    mPollutionOptionId[i] = options.getJSONObject(i).getString("id");
                }
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
    }

    /**
     * 类型选择
     */
    private void showTypeView() {
        OptionsPickerView<Object> pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            btnType.setText(mTypeOption[options1]);
            typeSelectOption = options1;
            return false;
        })
                .setTitleText(getString(R.string.title_type_select))
                .setSelectOptions(typeSelectOption)
                .build();
        pvOptions.setPicker(mTypeOption);
        pvOptions.show();
    }


    /**
     * 包装选择
     */
    private void showPackageView() {
        OptionsPickerView<Object> pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            btnPackage.setText(mPackageOption[options1]);
            packageSelectOption = options1;
            return false;
        })
                .setTitleText(getString(R.string.title_package_select))
                .setSelectOptions(packageSelectOption)
                .build();
        pvOptions.setPicker(mPackageOption);
        pvOptions.show();
    }

    /**
     * 污染选择
     */
    private void showPollutionView() {
        OptionsPickerView<Object> pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            btnPollution.setText(mPollutionOption[options1]);
            pollutionSelectOption = options1;
            return false;
        })
                .setTitleText(getString(R.string.title_pollution_select))
                .setSelectOptions(pollutionSelectOption)
                .build();
        pvOptions.setPicker(mPollutionOption);
        pvOptions.show();
    }
}
