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

package com.yiflyplan.app.fragment.input;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.xuexiang.xaop.annotation.IOThread;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.enums.ThreadType;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils;
import com.xuexiang.xutil.app.PathUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yiflyplan.app.fragment.blueTooth.BlueToothFragment;
import com.yiflyplan.app.utils.XToastUtils;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.xuexiang.xaop.consts.PermissionConsts.CAMERA;

/**
 * @author xuexiang
 * @since 2019-10-30 00:19
 */
@Page(anim = CoreAnim.none)
public class InputFragment extends BaseFragment {

    @BindView(R.id.scan_card)
    CardView scanView;

    @BindView(R.id.bluetooth_card)
    CardView blueToothView;

    @BindView(R.id.medical_waste_transfer_information_code)
    EditText medicalWasteTransferInformationCodeEditText;

    @BindView(R.id.btn_query)
    Button queryButton;

    @BindView(R.id.scan_card_instructions)
    TextView scanCardInstructionsTextView;

    @BindView(R.id.scan_card_instructions_info)
    TextView scanCardInstructionsInfoTextView;

    @BindView(R.id.bluetooth_card_instructions)
    TextView bluetoothCardInstructionsTextView;

    @BindView(R.id.bluetooth_card_instructions_info)
    TextView bluetoothCardInstructionsInfoTextView;


    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_IMAGE = 112;
    /**
     * 定制化扫描界面Request Code
     */
    public static final int REQUEST_CUSTOM_SCAN = 113;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_input;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        scanView.setOnClickListener(v -> startScan());
        blueToothView.setOnClickListener(v -> startBlueTooth());
        queryButton.setOnClickListener(v -> startQuery());
        scanCardInstructionsTextView.setOnClickListener(v -> scanCardInstructions());
        bluetoothCardInstructionsTextView.setOnClickListener(v -> bluetoothCardInstructions());
    }


    /**
     * 点击扫码说明
     */
    @SuppressLint("ResourceAsColor")
    private void scanCardInstructions() {
        //设置可见
        scanCardInstructionsInfoTextView.setVisibility(View.VISIBLE);
        bluetoothCardInstructionsInfoTextView.setVisibility(View.INVISIBLE);

        //设置选中颜色
        scanCardInstructionsTextView.setTextColor(getContext().getResources().getColorStateList(R.color.app_color_theme_6));
        bluetoothCardInstructionsTextView.setTextColor(getContext().getResources().getColorStateList(R.color.xui_btn_gray_normal_color));
    }

    /**
     * 点击蓝牙说明
     */
    @SuppressLint("ResourceAsColor")
    private void bluetoothCardInstructions() {
        //设置可见
        bluetoothCardInstructionsInfoTextView.setVisibility(View.VISIBLE);
        scanCardInstructionsInfoTextView.setVisibility(View.INVISIBLE);

        //设置选中颜色
        bluetoothCardInstructionsTextView.setTextColor(getContext().getResources().getColorStateList(R.color.app_color_theme_6));
        scanCardInstructionsTextView.setTextColor(getContext().getResources().getColorStateList(R.color.xui_btn_gray_normal_color));
    }


    /**
     * 开始查询手动输入的物流码
     */
    private void startQuery() {
        String medicalWasteTransferInformationCode = String.valueOf(medicalWasteTransferInformationCodeEditText.getText());
        XToastUtils.toast(medicalWasteTransferInformationCode);

    }

    /**
     * 开启二维码扫描
     */
    @Permission(CAMERA)
    @IOThread(ThreadType.Single)
    private void startScan() {

        XQRCode.startScan(this, REQUEST_CODE);


    }

    /**
     * 开启蓝牙录入
     */
    private void startBlueTooth() {
        openNewPage(BlueToothFragment.class);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CUSTOM_SCAN && resultCode == RESULT_OK) {
            handleScanResult(data);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理二维码扫描结果
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data);
        }

        //选择系统图片并解析
        else if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                getAnalyzeQRCodeResult(uri);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getAnalyzeQRCodeResult(Uri uri) {
        XQRCode.analyzeQRCode(PathUtils.getFilePathByUri(getContext(), uri), new QRCodeAnalyzeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                XToastUtils.toast("解析结果:" + result, Toast.LENGTH_LONG);
            }

            @Override
            public void onAnalyzeFailed() {
                XToastUtils.toast("解析二维码失败", Toast.LENGTH_LONG);
            }
        });
    }


    /**
     * 处理二维码扫描结果
     *
     * @param data
     */
    private void handleScanResult(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    String result = bundle.getString(XQRCode.RESULT_DATA);
                    XToastUtils.toast("解析结果:" + result, Toast.LENGTH_LONG);
                } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
                    XToastUtils.toast("解析二维码失败", Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Permission(CAMERA)
    private void initPermission() {
        XToastUtils.toast("相机权限已获取！");
        XQRCode.setAutoFocusInterval(800);
    }
}
