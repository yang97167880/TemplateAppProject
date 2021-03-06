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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet.BottomListSheetBuilder;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.FormField;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.MD5Util;
import com.yiflyplan.app.utils.TimeCountUtil;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.yiflyplan.app.fragment.LoginFragment.stringtoBitmap;
import static com.yiflyplan.app.utils.TextUtil.disallowSpacesUtil;

@Page(name = "??????")
public class RegisteredFragment extends BaseFragment {


    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.et_phone_number)
    MaterialEditText etPhoneNumber;
    @BindView(R.id.et_password_number)
    MaterialEditText etPasswordNumber;
    @BindView(R.id.et_confirm_password_number)
    MaterialEditText etConfirmPasswordNumber;
    @BindView(R.id.et_user_name)
    MaterialEditText etUserName;
    @BindView(R.id.et_dynamic_code)
    MaterialEditText etDynamicCode;
    @BindView(R.id.btn_get_dynamic_code)
    Button btnGetDynamicCode;


    private Uri mImageUri;
    private File file;


    public static final int TITLE_SIZE = 50;
    public static final int TITLE_BAR_HEIGHT = 120;
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = String.format("file%sicon.jpg", File.separator);
    private static final int MILLIS_IN_FUTURE = 300;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registered;
    }

    @Override
    protected void initViews() {
        disallowSpacesUtil(etUserName);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitleSize(TITLE_SIZE);
        titleBar.setHeight(TITLE_BAR_HEIGHT);
        return titleBar;
    }

    @SuppressLint("NonConstantResourceId")
    @SingleClick
    @OnClick({R.id.btn_register, R.id.iv_avatar, R.id.et_dynamic_code, R.id.btn_get_dynamic_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_dynamic_code:
                if (etPhoneNumber.getText().toString().length() == 0) {
                    XToastUtils.error("???????????????????????????????????????");
                } else {
                    getDynamicCode(etPhoneNumber.getText().toString());
                }
                break;
            case R.id.btn_register:
                if (mImageUri != null) {
                    if (etPhoneNumber.validate()) {
                        if (etUserName.isEmpty()) {
                            XToastUtils.toast("?????????????????????");
                        } else if (etUserName.validate()) {
                            if (etPasswordNumber.validate()) {
                                if (Objects.requireNonNull(etConfirmPasswordNumber.getText()).toString().equals(Objects.requireNonNull(etPasswordNumber.getText()).toString())) {
                                    if (etDynamicCode.validate()) {
                                        List<FormField<?>> formFields = new ArrayList<>();

                                        String md5psd = null;
                                        String md5repeatPsd = null;
                                        try {
                                            md5psd = MD5Util.getMD5(String.valueOf(etPasswordNumber.getText()));
                                            md5repeatPsd = MD5Util.getMD5(String.valueOf(etConfirmPasswordNumber.getText()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        FormField<String> formField1 = new FormField<>();
                                        formField1.setFieldName("password");
                                        formField1.setFieldValue(md5psd);

                                        FormField<String> formField2 = new FormField<>();
                                        formField2.setFieldName("passwordAgain");
                                        formField2.setFieldValue(md5repeatPsd);

                                        FormField<String> formField3 = new FormField<>();
                                        formField3.setFieldName("tel");
                                        formField3.setFieldValue(String.valueOf(etPhoneNumber.getText()));

                                        FormField<String> formField4 = new FormField<>();
                                        formField4.setFieldName("userName");
                                        formField4.setFieldValue(String.valueOf(etUserName.getText()));

                                        FormField<String> formField5 = new FormField<>();
                                        formField5.setFieldName("dynamicCode");
                                        formField5.setFieldValue(String.valueOf(etDynamicCode.getText()));

                                        FormField<File> formField6 = new FormField<>();
                                        formField6.setFieldName("userAvatar");
                                        formField6.setExtras(new FormField.Pair<>("filename", file.getName()));
                                        formField6.setContentType("image/jpeg");

                                        formField6.setFieldValue(file);

                                        formFields.add(formField1);
                                        formFields.add(formField2);
                                        formFields.add(formField3);
                                        formFields.add(formField4);
                                        formFields.add(formField5);
                                        formFields.add(formField6);


                                        MyHttp.postForm("/user/register", "", formFields, new MyHttp.Callback() {
                                            @Override
                                            public void success(JSONObject data) throws JSONException {
                                                XToastUtils.toast("????????????");
                                                openNewPage(LoginFragment.class);
                                            }

                                            @Override
                                            public void fail(JSONObject error) throws JSONException {
                                                XToastUtils.error(error.getString("message"));
                                            }
                                        });
                                    }
                                } else {
                                    XToastUtils.toast("?????????????????????????????????");
                                }
                            }
                        }
                    }
                } else {
                    XToastUtils.toast("???????????????");
                }

                break;
            case R.id.et_dynamic_code:
                KeyboardUtils.isSoftInputShow(getActivity());
                KeyboardUtils.showSoftInputForce(getActivity());
                break;
            case R.id.iv_avatar:
                showBottomSheetList(ivAvatar);
                break;
            default:
                break;
        }
    }


    /**
     * ??????
     *
     * @param v
     */
    private void showBottomSheetList(View v) {
        new BottomListSheetBuilder(getActivity())
                .addItem("??????")
                .addItem("???????????????")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    switch (position) {
                        case 0:
                            // ???????????????????????????
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                    || ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // ???????????????????????????????????????????????????????????????
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
                            } else {
                                // ?????????????????????????????????
                                dialog.dismiss();
                                imageCapture();
                            }

                            break;
                        case 1:
                            // ??????????????????
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                //???????????????????????????????????????????????????????????????
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                            } else {
                                // ??????????????????????????????????????????????????????
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                // ????????????????????????????????? Intent ??? Activity
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                                } else {
                                    Toast.makeText(getActivity(), "????????????????????????", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                })
                .build()
                .show();
    }


    /**
     * ???????????????
     */
    private void getDynamicCode(String tel) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tel", tel);
        MyHttp.get("/captcha/getRegisteredDynamicCode", "", params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                XToastUtils.info("??????????????????");
                TimeCountUtil timeCount = new TimeCountUtil(MILLIS_IN_FUTURE * 1000, 1000, btnGetDynamicCode);
                timeCount.start();
            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                XToastUtils.error(error.getString("message"));
            }
        });

    }

    /**
     * ??????????????????
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ????????????
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // ????????????
                case REQUEST_BIG_IMAGE_CUTTING:
                    RadiusImageView radiusImageView = findViewById(R.id.iv_avatar);
                    GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
                    lodeImg.loadImage(radiusImageView, mImageUri);
                    break;
                // ????????????
                case REQUEST_IMAGE_GET:
                    try {
                        startBigPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                // ??????
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(String.format("%s%s%s", Environment.getExternalStorageDirectory(), File.separator, IMAGE_FILE_NAME));
                    startBigPhotoZoom(temp);
            }
        }
    }


    /**
     * ????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    // ????????????????????????????????? Intent ??? Activity
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    } else {
                        XToastUtils.toast("????????????????????????");
                    }
                } else {
                }
                break;
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageCapture();
                } else {
                }
                break;
        }
    }


    /**
     * ?????????????????????
     */
    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        File pictureFile = new File(String.format("%s%s%s", Environment.getExternalStorageDirectory(), File.separator, IMAGE_FILE_NAME));
        // ??????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(getContext(),
                    "com.yiflyplan.app.provider", pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // ?????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    /**
     * ????????????????????????
     * ???????????????????????????????????????????????????
     */
    public void startBigPhotoZoom(File inputFile) {
        // ?????????????????????
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "?????????????????????");
                } else {
                    Log.e("TAG", "?????????????????????");
                }
            }
            file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // ??? uri ?????????????????????????????????
        }

        // ????????????
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(getContext(), inputFile), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // ???????????????
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // ??????????????????
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // ?????????????????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // ??????????????????
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }


    public void startBigPhotoZoom(Uri uri) {
        // ?????????????????????
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "?????????????????????");
                } else {
                    Log.e("TAG", "?????????????????????");
                }
            }
            file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // ??? uri ?????????????????????????????????
        }

        // ????????????
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // ???????????????
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // ??????????????????
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // ?????????????????????
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // ??????????????????
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
