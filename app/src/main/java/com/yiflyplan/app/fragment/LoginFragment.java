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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.activity.MainActivity;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.MD5Util;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TimeCountUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * ????????????
 *
 * @author xuexiang
 * @since 2019-11-17 22:15
 */
@Page(anim = CoreAnim.none)
public class LoginFragment extends BaseFragment {

    @BindView(R.id.et_phone_number)
    MaterialEditText etPhoneNumber;
    @BindView(R.id.et_password_number)
    MaterialEditText etPasswordNumber;
    @BindView(R.id.et_verify_code)
    MaterialEditText etVerifyCode;
    @BindView(R.id.code_image)
    ImageView codeImage;
    @BindView(R.id.et_dynamic_code)
    MaterialEditText etDynamicCode;
    @BindView(R.id.btn_get_dynamic_code)
    Button btnGetDynamicCode;

    @BindView(R.id.fl_verify_code)
    FrameLayout flVerifyCode;

    @BindView(R.id.fl_dynamic_code)
    FrameLayout flDynamicCode;

    @BindView(R.id.fl_password)
    FrameLayout flPassword;

    @BindView(R.id.tv_other_login)
    XUIAlphaTextView tvOtherLogin;

    private final String CURRENTUSER = "currentUser";
    private String savedVerificationCode;
    private Bitmap verificationCodeImage;


    private boolean isMessageLogin = false;
    private static final int MILLIS_IN_FUTURE = 300;
    private static final String MESSAGE_LOGIN = "????????????";
    private static final String PASSWORD_LOGIN = "??????????????????";

    private CountDownButtonHelper mCountDownHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        return titleBar;
    }

    @Override
    protected void initViews() {
        getVerifyCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SingleClick
    @OnClick({R.id.et_dynamic_code,R.id.btn_get_dynamic_code,R.id.btn_login, R.id.tv_register, R.id.tv_other_login, R.id.tv_forget_password, R.id.tv_user_protocol, R.id.tv_privacy_protocol, R.id.et_password_number, R.id.et_verify_code, R.id.code_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_dynamic_code:
                if(etPhoneNumber.getText().toString().length() == 0){
                    XToastUtils.error("???????????????????????????????????????");
                }else {
                    getDynamicCode(etPhoneNumber.getText().toString());
                }
                break;

            case R.id.code_image:
                getVerifyCode();
                break;
            case R.id.btn_login:
                    if (etPhoneNumber.validate()) {
                        if(!isMessageLogin){
                            if (etVerifyCode.validate()) {
                                passwordLogin();
                            }
                        }else {
                            if (etDynamicCode.validate()) {
                                messageLogin();
                            }
                        }
                    }

                break;
            case R.id.et_password_number:
            case R.id.et_dynamic_code:
            case R.id.et_verify_code:
                KeyboardUtils.isSoftInputShow(getActivity());
                KeyboardUtils.showSoftInputForce(getActivity());
                break;
            case R.id.tv_register:
                openNewPage(RegisteredFragment.class);
                break;
            case R.id.tv_other_login:
                if(!isMessageLogin){
                    XToastUtils.info(PASSWORD_LOGIN);
                    tvOtherLogin.setText(PASSWORD_LOGIN);
                    flVerifyCode.setVisibility(View.GONE);
                    flPassword.setVisibility(View.GONE);
                    flDynamicCode.setVisibility(View.VISIBLE);
                    isMessageLogin = true;
                }else {
                    XToastUtils.info(MESSAGE_LOGIN);
                    tvOtherLogin.setText(MESSAGE_LOGIN);
                    flVerifyCode.setVisibility(View.VISIBLE);
                    flPassword.setVisibility(View.VISIBLE);
                    flDynamicCode.setVisibility(View.GONE);
                    isMessageLogin = false;
                }
                break;
            case R.id.tv_forget_password:
                XToastUtils.info("????????????");
                break;
            case R.id.tv_user_protocol:
                XToastUtils.info("????????????");
                break;
            case R.id.tv_privacy_protocol:
                XToastUtils.info("????????????");
                break;
            default:
                break;
        }
    }

    /**
     * ????????????
     */
    private void messageLogin(){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tel", String.valueOf(etPhoneNumber.getText()));
        params.put("dynamicCode", String.valueOf(etDynamicCode.getText()));
        MyHttp.postJson("/user/dynamicLogin", "", params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                initUserData(data);
            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                Log.e("TAG1:", error.toString());
                if (error.getInt("code") == 20002) {
                    openNewPage(RegisteredFragment.class);
                }
            }
        });

    }

    /**
     * ????????????
     */
    private void passwordLogin(){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tel", String.valueOf(etPhoneNumber.getText()));
        String md5psd = null;
        try {
            md5psd = MD5Util.getMD5(String.valueOf(etPasswordNumber.getText()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("password", md5psd);
        params.put("verificationCode", String.valueOf(etVerifyCode.getText()));
        MyHttp.postJson("/user/login", "", params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                initUserData(data);
            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                Log.e("TAG1:", error.toString());
                if (error.getInt("code") == 40004) {
                    getVerifyCode();
                }
                if (error.getInt("code") == 20002) {
                    openNewPage(RegisteredFragment.class);
                }
            }
        });
    }

    /**
     * ?????????????????????
     */

    private void initUserData(JSONObject data) throws JSONException {
        Log.e("JSON:", data.toString());
        CurrentUserVO userVO = ReflectUtil.convertToObject(data, CurrentUserVO.class);
        MMKVUtils.put("userId",userVO.getUserId());
        MMKVUtils.put("userName",userVO.getUserName());
        MMKVUtils.put("userAvatar",userVO.getUserAvatar());
        MMKVUtils.put("organizationId",userVO.getCurrentOrganization().getOrganizationId());
        MMKVUtils.put("organizationName",userVO.getCurrentOrganization().getOrganizationName());
        MMKVUtils.put("relationships",data.getString("relationships"));
        //???????????????
        onLoginSuccess(userVO, data.getString("token"));

    }

    /**
     * ???????????????
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

    /**
     * ?????????????????????
     */
    private void getDynamicCode(String tel) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tel",tel);
        MyHttp.get("/captcha/getLoginDynamicCode", "", params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                XToastUtils.info("??????????????????");
                TimeCountUtil timeCount = new TimeCountUtil(MILLIS_IN_FUTURE * 1000, 1000,btnGetDynamicCode);
                timeCount.start();
            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                XToastUtils.error(error.getString("message"));
            }
        });

    }

    /**
     * ?????????????????????
     */
    private void onLoginSuccess(CurrentUserVO userInfo, String token) {
        if (TokenUtils.handleLoginSuccess(token)) {
            popToBack();
            MMKVUtils.put("user", userInfo);
            ActivityUtils.startActivity(MainActivity.class, CURRENTUSER, userInfo);
        }
    }

    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }

    public static Bitmap stringtoBitmap(String string) {
// ?????????????????????Bitmap??????
        Bitmap bitmap = null;
        try {
            JSONObject str = new JSONObject(string);
            string = str.getString("image");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string.substring(21), Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

