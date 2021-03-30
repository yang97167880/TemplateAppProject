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
import java.util.LinkedHashMap;

import butterknife.BindView;

import static com.yiflyplan.app.fragment.components.DrawablePreviewFragment.DRAWABLE_ID;
import static com.yiflyplan.app.utils.ImageConversionUtil.base64ToBitmap;

@Page(name = "分享机构", extra = R.drawable.ic_share)
public class Share extends BaseFragment implements View.OnClickListener, View.OnLongClickListener {
    @BindView(R.id.iv_organization_qrcode)
    ImageView ivOrganizationQrcode;
    @BindView(R.id.tv_organization_name)
    TextView tvOrganizationName;

    private Bitmap qrCode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share_organization;
    }

    @Override
    protected void initViews() {
        ivOrganizationQrcode.setImageBitmap(qrCode);
    }





    @Override
    protected void initListeners() {
        super.initListeners();
        ivOrganizationQrcode.setOnClickListener(this);
        ivOrganizationQrcode.setOnLongClickListener(this);

        Bundle build = getArguments();
        int id = build.getInt("id");
        LinkedHashMap<String,String> params = new  LinkedHashMap<>();
        params.put("organizationId",String.valueOf(id));
        MyHttp.get("/organization/getTheQrCodeOfTheOrganization",TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                String qrCodeBase64 = data.getString("qrCode");
                Log.e("img",qrCodeBase64);
                qrCode = base64ToBitmap(qrCodeBase64,"data:img/jpeg;base64,");
                ivOrganizationQrcode.setImageBitmap(qrCode);
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
            case R.id.iv_organization_qrcode:
            //    bundle.putParcelable("bitmap", qrCode);
                bundle.putInt(DRAWABLE_ID, R.drawable.img_xui_qq);
                openPage(DrawablePreviewFragment.class, bundle);
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
}
