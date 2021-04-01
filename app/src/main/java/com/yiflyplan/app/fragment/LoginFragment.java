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
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.activity.MainActivity;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.utils.MD5Util;
import com.yiflyplan.app.utils.RandomUtils;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 登录页面
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

    private final String CURRENTUSER = "currentUser";
    private String savedVerificationCode;
    private Bitmap verificationCodeImage;
//    @BindView(R.id.btn_get_verify_code)
//    RoundButton btnGetVerifyCode;

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
        titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
            @Override
            public void performAction(View view) {
                String token = RandomUtils.getRandomNumbersAndLetters(16);
                CurrentUserVO userVO = new CurrentUserVO();
                onLoginSuccess(userVO, token);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        getVerifyCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SingleClick
    @OnClick({R.id.btn_login, R.id.tv_other_login, R.id.tv_forget_password, R.id.tv_user_protocol, R.id.tv_privacy_protocol, R.id.et_password_number, R.id.et_verify_code, R.id.code_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.btn_get_verify_code:
//                if (etPhoneNumber.validate()) {
//                    getVerifyCode(etPhoneNumber.getEditValue());
//                }
//                break;
            case R.id.code_image:
                getVerifyCode();
                break;
            case R.id.btn_login:
                if (etPhoneNumber.validate()) {
                    if (etVerifyCode.validate()) {
                        LinkedHashMap<String, String> params = new LinkedHashMap<>();
                        params.put("tel", etPhoneNumber.getText().toString());
                        String md5psd = null;
                        try {
                            md5psd = MD5Util.getMD5(etPasswordNumber.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        params.put("password", md5psd);
                        params.put("verificationCode", etVerifyCode.getText().toString());
                        MyHttp.postJson("/user/login", "", params, new MyHttp.Callback() {
                            @Override
                            public void success(JSONObject data) throws JSONException {
                                Log.e("JSON:", data.toString());
                                CurrentUserVO userVO = new CurrentUserVO();
                                OrganizationVO organizationVO = new OrganizationVO();

                                //用户初始化
                                userVO.setId(data.getInt("userId"));
                                userVO.setName(data.getString("userName"));
                                userVO.setAvatar(data.getString("userAvatar"));
                                userVO.setTel(data.getString("userTel"));
                                userVO.setCityId(data.getString("userCityId"));

                                //机构初始化
                                JSONObject organization = new JSONObject(data.getString("currentOrganization"));
                                organizationVO = ReflectUtil.convertToObject(organization, OrganizationVO.class);
//                                organizationVO.setId(organization.getInt("organizationId"));
//                                organizationVO.setName(organization.getString("organizationName"));
//                                organizationVO.setAvatar(organization.getString("organizationAvatar"));
//                                organizationVO.setAbbreviation(organization.getString("organizationAbbreviation"));
//                                organizationVO.setLevel(organization.getString("organizationLevel"));
//                                organizationVO.setTypeId(organization.getInt("organizationTypeId"));
//                                organizationVO.setTypeName(organization.getString("organizationTypeName"));
//                                organizationVO.setRoleName(organization.getString("roleName"));

                                //机构关系初始化
                                JSONArray relationships = new JSONArray(data.getString("relationships"));
                                List<OrganizationVO> voList = new ArrayList<>();
                                for (int i = 0; i < relationships.length(); i++) {
                                    OrganizationVO temp = new OrganizationVO();
                                    temp.setId(relationships.getJSONObject(i).getInt("organizationId"));
                                    temp.setName(relationships.getJSONObject(i).getString("organizationName"));
                                    temp.setAvatar(relationships.getJSONObject(i).getString("organizationAvatar"));
                                    temp.setAbbreviation(relationships.getJSONObject(i).getString("organizationAbbreviation"));
                                    temp.setLevel(relationships.getJSONObject(i).getString("organizationLevel"));
                                    temp.setTypeId(relationships.getJSONObject(i).getInt("organizationTypeId"));
                                    temp.setTypeName(relationships.getJSONObject(i).getString("organizationTypeName"));
                                    temp.setRoleName(relationships.getJSONObject(i).getString("roleName"));
                                    voList.add(temp);
                                }
                                userVO.setCurrentOrganization(organizationVO);
                                userVO.setRelationships(voList);
                                onLoginSuccess(userVO, data.getString("token"));
                            }

                            @Override
                            public void fail(JSONObject error) throws JSONException {
                                Log.e("TAG1:", error.toString());
                                if (error.getInt("code") == 40004) {
                                    getVerifyCode();
                                }
                            }
                        });
                    }
                }
                break;
            case R.id.et_password_number:
                KeyboardUtils.isSoftInputShow(getActivity());
                KeyboardUtils.showSoftInputForce(getActivity());
                break;
            case R.id.et_verify_code:
                KeyboardUtils.isSoftInputShow(getActivity());
                KeyboardUtils.showSoftInputForce(getActivity());
                break;
            case R.id.tv_other_login:
                XToastUtils.info("其他登录方式");
                break;
            case R.id.tv_forget_password:
                XToastUtils.info("忘记密码");
                break;
            case R.id.tv_user_protocol:
                XToastUtils.info("用户协议");
                break;
            case R.id.tv_privacy_protocol:
                XToastUtils.info("隐私政策");
                break;
            default:
                break;
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

    /**
     * 根据验证码登录
     *
     * @param phoneNumber 手机号
     * @param verifyCode  验证码
     */
//    private void loginByVerifyCode(String phoneNumber, String verifyCode) {
//        // TODO: 2020/8/29 这里只是界面演示而已
//        onLoginSuccess();
//    }

    /**
     * 登录成功的处理
     */
    private void onLoginSuccess(CurrentUserVO userInfo, String token) {
        if (TokenUtils.handleLoginSuccess(token)) {
            popToBack();
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
// 将字符串转换成Bitmap类型
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

