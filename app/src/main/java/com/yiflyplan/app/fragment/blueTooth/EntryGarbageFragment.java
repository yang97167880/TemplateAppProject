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
@Page(name = "????????????")
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

    private String[] mTypeOption;
    private String[] mTypeOptionId;
    private String[] mPackageOption;
    private String[] mPackageOptionId;
    private String[] mPollutionOption;
    private String[] mPollutionOptionId;

    private int typeSelectOption = -1;
    private int packageSelectOption = -1;
    private int pollutionSelectOption = -1;

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP??????UUID???
    private final static String TYPE_DEFAULT = "?????????????????????";
    private final static String PACKAGE_DEFAULT = "?????????????????????";
    private final static String POLLUTION_DEFAULT = "?????????????????????";
    private String mac;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice remoteDevice;
    private BluetoothSocket bluetoothSocket;
    private boolean hasConnect = false;

    private AlertDialog entry_info_dialog;
    private TextView entry_message;

    private ScrollView message_scroll;

    private Timer timer = new Timer();

    private final Integer RECEIVE_CODE = 1; //???????????????????????????
    private final Integer RE_RECEIVE_CODE = 2;//???????????????????????????????????????

    SearchFragment.UploadData nextParams;

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
            XToastUtils.info("??????????????????????????????...");
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

    //???????????????
    private void initBT() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            XToastUtils.error("??????????????????????????????");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        try {
            //??????????????????????????????
            if (!hasConnect) {
                //??????????????????
                remoteDevice = bluetoothAdapter.getRemoteDevice(mac);
                bluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                bluetoothSocket.connect();
                hasConnect = true;
                XToastUtils.success("????????????");
            }

        } catch (Exception e) {
            e.printStackTrace();
            XToastUtils.error("????????????");
            closeBlueSocket();
            Objects.requireNonNull(this).popToBack();
        }
        //?????????????????????????????????
        try {
            readThread.setInputStream(bluetoothSocket.getInputStream());
            readThread.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //?????????????????????
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
        private volatile boolean running;//?????????????????????
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
            //????????????
            try {
                byte[] buffer = new byte[1024];
                byte[] buffer_new = new byte[1024];
                StringBuilder bluetoothMessageTmp = new StringBuilder();
                while (receiving) {
                    //??????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????
                    while (inputStream.available() != 0 && running) {
                        sleep(1000);
                        int msgNum = inputStream.read(buffer);         //???????????????buffer?????????????????????
                        //i??????????????????realIndex?????????????????????????????????0x0d 0x0a???0x0a?????????????????????
                        int realIndex = 0;//????????????????????????????????????
                        for (int i = 0; i < msgNum; ++i, ++realIndex) {
                            if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
                                buffer_new[realIndex] = 0x0a;
                                ++i;
                            } else {
                                buffer_new[realIndex] = buffer[i];
                            }
                        }
                        bluetoothMessageTmp.append(new String(buffer_new, 0, realIndex));   //??????????????????

                    }
                    if (bluetoothMessageTmp.toString().length() > 0) {
                        if (checkData(bluetoothMessageTmp.toString())) {
                            bluetoothMessage = bluetoothMessageTmp;
                            //???????????????????????????????????????
                            Message message = new Message();
                            message.obj = "????????????????????????....";
                            message.what = RECEIVE_CODE;
                            handler.sendMessage(message);

                            bluetoothMessageTmp = new StringBuilder();
                        } else {
                            Message message = new Message();
                            message.obj = "??????????????????????????????????????????????????????....";
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
            //???????????????????????????????????????
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
                message += "???????????????????????????";
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

    //?????????????????????????????????
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
                    //???????????????????????????????????????
                    Message message = new Message();
                    message.obj = "??????????????????....";
                    handler.sendMessage(message);

                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("departmentId", String.valueOf(nextParams.getDepartmentId()));
                    params.put("organizationId", String.valueOf(nextParams.getOrganizationId()));
                    params.put("itemWeight", garbageWeight.getText().toString());
                    params.put("itemTypeId", mTypeOptionId[typeSelectOption]);
                    params.put("bagTypeId", mPackageOptionId[packageSelectOption]);
                    params.put("pollutionLevelId", mPollutionOptionId[pollutionSelectOption]);
//                    Toast.makeText(EntryGarbageActivity.this, "?????????...", Toast.LENGTH_SHORT).show();
                    MyHttp.postJson("/product/createProduct", TokenUtils.getToken(), params, new MyHttp.Callback() {
                        @Override
                        public void success(JSONObject data) throws JSONException {
                            sendMessageToRemoteBluetooth(data.getString("itemEnCoding"));
//                            Toast.makeText(EntryGarbageActivity.this, "????????????,?????????...", Toast.LENGTH_SHORT).show();

                            //???????????????????????????????????????
                            Message message = new Message();
                            message.obj = "????????????...";
                            handler.sendMessage(message);
                            message = new Message();
                            message.obj = "????????????????????????/????????????????????????";
                            handler.sendMessage(message);
                            // resetThisPage();
                        }

                        @Override
                        public void fail(JSONObject error) {
                            Message message = new Message();
                            message.obj = "????????????????????????....";
                            handler.sendMessage(message);
                        }
                    });


                } else {
                    XToastUtils.info("????????????????????????");
                }

            }
        });
    }

    private void sendMessageToRemoteBluetooth(String message) {
        try {
            OutputStream outputStream = bluetoothSocket.getOutputStream();
            byte[] bytes = message.getBytes();
            int addSpaceNum = 0;
            //?????????0x0a????????????????????????????????????0x0d???????????????
            for (byte b : bytes) {
                if (b == 0x0a) {
                    ++addSpaceNum;
                }
            }
            //???????????????
            byte[] newBytes = new byte[bytes.length + addSpaceNum];
            //????????????
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] == 0x0a) {
                    newBytes[i] = 0x0d;
                    newBytes[++i] = 0x0a;
                } else {
                    newBytes[i] = bytes[i];
                }
            }
            //????????????????????????
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
     * ????????????
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
     * ????????????
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
     * ????????????
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
