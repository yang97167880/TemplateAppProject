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
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.yiflyplan.app.fragment.LoginFragment.stringtoBitmap;
import static com.yiflyplan.app.utils.TextUtil.disallowSpacesUtil;

@Page(name = "注册")
public class RegisteredFragment  extends BaseFragment {


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
    @BindView(R.id.et_verify_code)
    MaterialEditText etVerifyCode;
    @BindView(R.id.code_image)
    ImageView codeImage;

    private String savedVerificationCode;
    private Bitmap verificationCodeImage;
    private Uri mImageUri;
    private File file;


    public static final int TITLE_SIZE = 50;
    public static final int TITLE_BAR_HEIGHT = 120;
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registered;
    }

    @Override
    protected void initViews() {
        getVerifyCode();
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
    @OnClick({R.id.btn_register, R.id.iv_avatar,R.id.et_verify_code, R.id.code_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.code_image:
                getVerifyCode();
                break;
            case R.id.btn_register:
                if (mImageUri!=null){
                    if (etPhoneNumber.validate()) {
                        if(etUserName.isEmpty()){
                            XToastUtils.toast("用户名不能为空");
                        }else if(etUserName.validate()) {
                            if(etPasswordNumber.validate()){
                                if(etConfirmPasswordNumber.getText().toString().equals(etPasswordNumber.getText().toString())){
                                    if (etVerifyCode.validate()) {
                                        List<FormField> formFields = new ArrayList<>();

                                        FormField formField1 = new FormField();
                                        formField1.setFieldName("password");
                                        formField1.setFieldValue(etPasswordNumber.getText().toString());

                                        FormField formField2 = new FormField();
                                        formField2.setFieldName("passwordAgain");
                                        formField2.setFieldValue(etConfirmPasswordNumber.getText().toString());

                                        FormField formField3 = new FormField();
                                        formField3.setFieldName("tel");
                                        formField3.setFieldValue(String.valueOf(etPhoneNumber.getText()));

                                        FormField formField4 = new FormField();
                                        formField4.setFieldName("userName");
                                        formField4.setFieldValue(String.valueOf(etUserName.getText()));


                                        FormField formField5 = new FormField();
                                        formField5.setFieldName("verificationCode");
                                        formField5.setFieldValue(String.valueOf(etVerifyCode.getText()));

                                        FormField formField6 = new FormField();
                                        formField6.setFieldName("userAvatar");
                                        formField6.setExtras(new FormField.Pair("filename",file.getName()));
                                        formField6.setContentType("application/x-jpg");
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
                                                XToastUtils.toast("注册成功");
                                                openNewPage(LoginFragment.class);
                                            }

                                            @Override
                                            public void fail(JSONObject error) throws JSONException {
                                                if (error.getInt("code") == 40004) {
                                                    getVerifyCode();
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    XToastUtils.toast("重复密码与原密码不匹配");
                                }
                            }
                        }
                    }
                }else {
                    XToastUtils.toast("请更改头像");
                }

                break;
            case R.id.et_verify_code:
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
     * 弹窗
     * @param v
     */
    private void showBottomSheetList(View v) {
           new BottomListSheetBuilder(getActivity())
                .addItem("拍照")
                .addItem("从相册选择")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    switch (position) {
                        case 0:
                            // 拍照及文件权限申请
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                    || ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // 权限还没有授予，需要在这里写申请权限的代码
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
                            } else {
                                // 权限已经申请，直接拍照
                                dialog.dismiss();
                                imageCapture();
                            }

                            break;
                        case 1:
                            // 文件权限申请
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                //权限还没有授予，需要在这里写申请权限的代码
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                            } else {
                                // 如果权限已经申请过，直接进行图片选择
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                // 判断系统中是否有处理该 Intent 的 Activity
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                                }
                                else {
                                    Toast.makeText(getActivity(), "未找到图片查看器", Toast.LENGTH_SHORT).show();
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
     * 处理回调结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 大图切割
                case REQUEST_BIG_IMAGE_CUTTING:
                    RadiusImageView radiusImageView = findViewById(R.id.iv_avatar);
                    GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
                    lodeImg.loadImage(radiusImageView,mImageUri);
                    break;
                // 相册选取
                case REQUEST_IMAGE_GET:
                    try {
                        startBigPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                // 拍照
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startBigPhotoZoom(temp);
            }
        }
    }



    /**
     * 处理权限回调结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    // 判断系统中是否有处理该 Intent 的 Activity
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    } else {
                        XToastUtils.toast("未找到图片查看器");
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
     * 判断系统及拍照
     */
    private void imageCapture(){
        Intent intent;
        Uri pictureUri;
        File pictureFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(getContext(),
                    "com.yiflyplan.app.fileProvider", pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    /**
     * 大图模式切割图片
     * 直接创建一个文件将切割后的图片写入
     */
    public void startBigPhotoZoom(File inputFile) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(getContext(), inputFile), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }




    public void startBigPhotoZoom(Uri uri) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
            mImageUri = imageUri; // 将 uri 传出，方便设置到视图中
        }

        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

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



    /**
     * 获取验证码
     */
    private void getVerifyCode() {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("type", "0");
        MyHttp.get("/captcha/getRegisteredVerificationCode", "", params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                savedVerificationCode = data.toString();
                verificationCodeImage = stringtoBitmap(savedVerificationCode);
                codeImage.setImageBitmap(verificationCodeImage);
            }
            @Override
            public void fail(JSONObject error) {
                codeImage.setImageResource(R.drawable.ic_img);
                Log.e("TAG:", error.toString());

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
