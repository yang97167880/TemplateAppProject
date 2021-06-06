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
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.core.webview.AgentWebActivity;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xutil.app.AppUtils;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.VersionUtils;
import com.yiflyplan.app.utils.XToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static java.sql.DriverManager.getConnection;

/**
 * @author xuexiang
 * @since 2019-10-30 00:02
 */
@Page(name = "关于")
public class AboutFragment extends BaseFragment {

    @BindView(R.id.tv_version)
    TextView mVersionTextView;
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @BindView(R.id.tv_copyright)
    TextView mCopyrightTextView;

    String newVersion;
    String appUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initViews() {

        mVersionTextView.setText(String.format("版本号：%s", VersionUtils.Version));

        MyHttp.getDownload("/system/getNewestAppDownloadUrl", TokenUtils.getToken(), new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                appUrl = data.getString("data");
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
        MyHttp.getDownload("/system/getNewestAppVersion", TokenUtils.getToken(), new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                newVersion = data.getString("data");
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_homepage)), v -> AgentWebActivity.goWeb(getContext(), getString(R.string.url_project)))
                .addTo(mAboutGroupListView);
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_update)), v -> {new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if(download()){
                            XToastUtils.success("已是最新版本");
                        }else{
                            XToastUtils.success("更新成功");
                        }
                        Looper.loop();
                    }
                }).start();})
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));
    }

    private boolean download() {
        try {

            URL url = new URL(appUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e("", "文件长度 = " + contentLength);
            //创建文件夹 MyDownLoad，在存储卡下
            String dirName = Environment.getExternalStorageDirectory() + "/";
            //下载后的文件名
            String fileName = dirName + "001";
            File file1 = new File(fileName);
            if (file1.exists()) {

                Log.e("文件-----", "文件已经存在！");
                return fileIsExists(fileName);
            } else {
                //创建字节流
                byte[] bs = new byte[1024];
                int len;
                if(!isGrantExternalRW(this.getActivity())){
                    return false;
                }
                file1.createNewFile();
                OutputStream os = new FileOutputStream(fileName);
                //写数据
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                //完成后关闭流
                Log.e("文件不存在", "下载成功！");
                os.close();
                is.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //动态申请权限
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
