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

package com.yiflyplan.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xutil.display.ImageUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.components.DrawablePreviewFragment;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;

import static com.yiflyplan.app.fragment.components.DrawablePreviewFragment.BITMAP;
import static com.yiflyplan.app.fragment.components.DrawablePreviewFragment.DRAWABLE_ID;
import static com.yiflyplan.app.utils.ImageConversionUtil.base64ToBitmap;

@Page(name = "我的二维码")
public class QRCodeFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {
    @BindView(R.id.iv_user_qrcode)
    ImageView ivUserQrcode;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;


    private Bitmap qrCode;
    private String userName;
    private String userAvatar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qrcode;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        userAvatar = bundle.getString("userAvatar");
        userName = bundle.getString("userName");
        RadiusImageView radiusImageView = findViewById(R.id.iv_avatar);
        GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
        lodeImg.loadImage(radiusImageView,userAvatar);

        tvUserName.setText(userName);
        ivUserQrcode.setImageBitmap(qrCode);
    }

    @Override
    protected void initListeners() {
        ivUserQrcode.setTag("user_qrcode_subscription_number");
        ivUserQrcode.setOnClickListener(this);
        ivUserQrcode.setOnLongClickListener(this);

        LinkedHashMap<String,String> params = new  LinkedHashMap<>();
        MyHttp.get("/user/getMyQrCode", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                String qrCodeBase64 = data.getString("qrCode");
                Log.e("img33",qrCodeBase64);
                qrCode = base64ToBitmap(qrCodeBase64,"data:img/jpeg;base64,");
                ivUserQrcode.setImageBitmap(qrCode);
            }
            @Override
            public void fail(JSONObject error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.iv_user_qrcode:
                bundle.putParcelable(BITMAP, qrCode);
                openPage(DrawablePreviewFragment.class, bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof ImageView) {
            showBottomSheetList(v, ImageUtils.drawable2Bitmap(((ImageView) v).getDrawable()));
        }
        return true;
    }

    @NonNull
    private String getImgFilePath(View v) {
        return FileUtils.getDiskFilesDir() + File.separator + v.getTag() + ".png";
    }

    private void showBottomSheetList(View v, final Bitmap bitmap) {
        final String imgPath = getImgFilePath(v);
        new BottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("发送给朋友")
                .addItem("保存图片")
                .addItem("识别图中的二维码")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    boolean result = checkFile(imgPath, bitmap);
                    switch (position) {
                        case 0:
                            if (result) {
//                                SocialShareUtils.sharePicture(getActivity(), PathUtils.getUriForFile(FileUtils.getFileByPath(imgPath)));
                            } else {
                                XToastUtils.toast("图片发送失败!");
                            }
                            break;
                        case 1:
                            if (result) {
                                XToastUtils.toast("图片保存成功:" + imgPath);
                            } else {
                                XToastUtils.toast("图片保存失败!");
                            }
                            break;
                        case 2:
                            if (result) {
                                XQRCode.analyzeQRCode(imgPath, new QRCodeAnalyzeUtils.AnalyzeCallback() {
                                    @Override
                                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
//                                        if (NetworkUtils.isUrlValid(result)) {
//                                            goWeb(result);
//                                        }
                                    }

                                    @Override
                                    public void onAnalyzeFailed() {
                                        XToastUtils.toast("解析二维码失败！");
                                    }
                                });
                            } else {
                                XToastUtils.toast("二维码识别失败!");
                            }
                            break;
                        default:
                            break;
                    }
                })
                .build()
                .show();
    }

    private boolean checkFile(String imgPath, Bitmap bitmap) {
        boolean result = FileUtils.isFileExists(imgPath);
        if (!result) {
            result = ImageUtils.save(bitmap, imgPath, Bitmap.CompressFormat.PNG);
        }
        return result;
    }

//    /**
//     * 请求浏览器
//     *
//     * @param url
//     */
//    public void goWeb(final String url) {
//        Intent intent = new Intent(getContext(), AgentWebActivity.class);
//        intent.putExtra(KEY_URL, url);
//        startActivity(intent);
//    }
}
