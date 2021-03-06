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

package com.yiflyplan.app.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.debug.I;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.guidview.GuideCaseQueue;
import com.xuexiang.xui.widget.guidview.GuideCaseView;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;
import com.xuexiang.xutil.system.DeviceUtils;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;
import com.yiflyplan.app.MyCP;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.MemberVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.entity.NoticeInfo;
import com.yiflyplan.app.adapter.menu.DrawerAdapter;
import com.yiflyplan.app.adapter.menu.DrawerItem;
import com.yiflyplan.app.adapter.menu.SimpleItem;
import com.yiflyplan.app.adapter.menu.SpaceItem;
import com.yiflyplan.app.core.BaseActivity;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.core.http.MyHttp;
import com.yiflyplan.app.fragment.AboutFragment;
import com.yiflyplan.app.fragment.QRCodeFragment;
import com.yiflyplan.app.fragment.SettingsFragment;
import com.yiflyplan.app.fragment.input.InputFragment;
import com.yiflyplan.app.fragment.notices.ChartRoomFragment;
import com.yiflyplan.app.fragment.notices.NoticesFragment;
import com.yiflyplan.app.fragment.organization.SearchOrganizationFragment;
import com.yiflyplan.app.fragment.organization.components.ApplyFormFragment;
import com.yiflyplan.app.fragment.organization.OrganizationFragment;
import com.yiflyplan.app.fragment.organization.components.ComponentsFragment;
import com.yiflyplan.app.utils.MMKVUtils;
import com.yiflyplan.app.utils.MapDataCache;
import com.yiflyplan.app.utils.ReflectUtil;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.Utils;
import com.yiflyplan.app.utils.VersionUtils;
import com.yiflyplan.app.utils.XToastUtils;
import com.yiflyplan.app.widget.GuideTipsDialog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * ???????????????,?????????????????????Tab??????
 *
 * @author xuexiang
 * @since 2019-07-07 23:53
 */
public class MainActivity extends BaseActivity implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tv_unread_count)
    TextView tvUnreadCount;
    /**
     * ???????????????
     */
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    private String[] mTitles;
    private ContentResolver cr ;

    private final String CURRENTUSER = "currentUser";
    private SlidingRootNav mSlidingRootNav;
    private String[] mMenuTitles;
    private Drawable[] mMenuIcons;
    private DrawerAdapter mAdapter;
    private static final int POS_ORGANIZATION = 0;
    private static final int POS_INPUT = 1;
    private static final int POS_NOTICES = 2;
    private static final int POS_ABOUT = 3;
    private static final int POS_LOGOUT = 5;
    private static final String ACTION = "com.test.action";
    private CurrentUserVO userVO;
    private BaseFragment[] fragments;
    private FragmentAdapter<BaseFragment> adapter;
    private InputFragment inputFragment;
    private NoticesFragment noticesFragment;
    /*user*/
    String userName;
    String userAvatar;
    /*organization*/
    String organizationName;

    List<OrganizationVO> relationships;


    ComponentsFragment componentsFragment;
    BottomSheetDialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //?????????????????????
        cr = getContentResolver();

        //?????????????????????????????????
        //user
        userName = MMKVUtils.getString("userName",null);
        userAvatar=  MMKVUtils.getString("userAvatar",null);
        //organization
        organizationName = MMKVUtils.getString("organizationName",null);

        isUpdate(this);

        MobclickAgent.onProfileSignIn(DeviceUtils.getAndroidID());

        initViews();

        initData();

        initSlidingMenu(savedInstanceState);

        initListeners();

        changeOrganization();
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        initSlidingMenu(null);
    }

    private void initData() {
        String relationStr = MMKVUtils.getString("relationships",null);
        try{
            JSONArray relations = new JSONArray(relationStr);
            relationships = ReflectUtil.convertToList(relations, OrganizationVO.class);
        }catch (Exception e){
            Log.e("JSONErr",e.getMessage());
        }
        mMenuTitles = ResUtils.getStringArray(R.array.menu_titles);
        mMenuIcons = ResUtils.getDrawableArray(this, R.array.menu_icons);


        Intent intent = getIntent();
        int organizationId = intent.getIntExtra("organizationId",-1);
        if(organizationId != -1){
            String organizationName = intent.getStringExtra("organizationName");
            for(int i = 0;i<relationships.size();i++){
                if(relationships.get(i).getOrganizationName().equals(organizationName)){
                    apiChangeOrganization(String.valueOf(organizationId),i);
                }
            }
        }

    }

    private void initViews() {
        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(organizationName);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            @SingleClick
            public void onClick(View v) {
                openMenu();
            }
        });

        inputFragment = new InputFragment();
        noticesFragment = new NoticesFragment();
        //??????????????????
        fragments = new BaseFragment[]{new ComponentsFragment(), inputFragment, noticesFragment};

        adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);

        GuideTipsDialog.showTips(this);
    }


    private void initSlidingMenu(Bundle savedInstanceState) {

        mSlidingRootNav = new SlidingRootNavBuilder(this)
                .withGravity(ResUtils.isRtl() ? SlideGravity.RIGHT : SlideGravity.LEFT)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

//        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.icon_head_default)
//                .diskCacheStrategy(DiskCacheStrategy.ALL);
        TextView Name = findViewById(R.id.tv_name);
        Name.setText(userName);

        TextView organizationText = findViewById(R.id.tv_organization);
        organizationText.setText(organizationName);
        RadiusImageView Avatar = findViewById(R.id.iv_avatar);
        //String url = "https://light-plant.oss-cn-beijing.aliyuncs.com/2021/03/22/2fac6a7f3a764dec8eae65046924296d.jpg";
        //Glide.with(this).load(url).into(userAvatar);
        GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
        lodeImg.loadImage(Avatar, userAvatar);

        LinearLayout mLLMenu = mSlidingRootNav.getLayout().findViewById(R.id.ll_menu);
        final AppCompatImageView ivQrcode = mSlidingRootNav.getLayout().findViewById(R.id.iv_qrcode);
        Bundle bundle = new Bundle();
        bundle.putString("userAvatar", userAvatar);
        bundle.putString("userName", userName);
        ivQrcode.setOnClickListener(v -> openNewPage(QRCodeFragment.class, bundle));

        final AppCompatImageView ivSetting = mSlidingRootNav.getLayout().findViewById(R.id.iv_setting);
        ivSetting.setOnClickListener(v -> openNewPage(SettingsFragment.class));

        mAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_ORGANIZATION).setChecked(true),
                createItemFor(POS_INPUT),
                createItemFor(POS_NOTICES),
                createItemFor(POS_ABOUT),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        mAdapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);

        mAdapter.setSelected(POS_ORGANIZATION);
        mSlidingRootNav.setMenuLocked(false);
        mSlidingRootNav.getLayout().addDragStateListener(new DragStateListener() {
            @Override
            public void onDragStart() {

            }

            @Override
            public void onDragEnd(boolean isMenuOpened) {
                if (isMenuOpened) {
                    if (!GuideCaseView.isShowOnce(MainActivity.this, getString(R.string.guide_key_sliding_root_navigation))) {
                        final GuideCaseView guideStep1 = new GuideCaseView.Builder(MainActivity.this)
                                .title("?????????????????????????????????????????????")
                                .titleSize(18, TypedValue.COMPLEX_UNIT_SP)
                                .focusOn(ivSetting)
                                .build();

                        final GuideCaseView guideStep2 = new GuideCaseView.Builder(MainActivity.this)
                                .title("????????????????????????????????????")
                                .titleSize(18, TypedValue.COMPLEX_UNIT_SP)
                                .focusOn(ivQrcode)
                                .build();

                        new GuideCaseQueue()
                                .add(guideStep1)
                                .add(guideStep2)
                                .show();
                        GuideCaseView.setShowOnce(MainActivity.this, getString(R.string.guide_key_sliding_root_navigation));
                    }
                }
            }
        });
    }

    protected void initListeners() {
        //??????????????????
        viewPager.addOnPageChangeListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                showUpdateDialog(this, null);
                break;
            case R.id.organization_add:
                openNewPage(SearchOrganizationFragment.class);
                break;

            case R.id.organization_change:
                showBottomSheetListDialog();
                break;
            default:
                break;
        }
        return false;
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_header:
                XToastUtils.toast("???????????????");
                break;
            default:
                break;
        }
    }

    //=============????????????????????????===================//
    private void changeOrganization(){

    }


    //=============ViewPager===================//

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = bottomNavigation.getMenu().getItem(position);
        if (position == 0) {
            toolbar.setTitle(organizationName);
        } else {
            toolbar.dismissPopupMenus();
            toolbar.setTitle(item.getTitle());
        }

        item.setChecked(true);

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //================Navigation================//

    /**
     * ???????????????????????????
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            if (index == 0) {
                toolbar.setTitle(organizationName);
                toolbar.dismissPopupMenus();
            } else {
                toolbar.setTitle(menuItem.getTitle());
            }
            viewPager.setCurrentItem(index, false);

            return true;
        }
        return false;
    }


    /**
     * ????????????????????????
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isMenuOpen()) {
                closeMenu();
            } else {
                ClickUtils.exitBy2Click(2000, this);
            }
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("????????????????????????");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }


    @Override
    public void onItemSelected(int position) {
        switch (position) {
            case POS_ORGANIZATION:
            case POS_INPUT:
            case POS_NOTICES:
                if (mMenuTitles.length > 0) {
                    if (position == 0) {
                        toolbar.setTitle(organizationName);
                    } else {
                        toolbar.dismissPopupMenus();
                        toolbar.setTitle(mMenuTitles[position]);
                    }
                    viewPager.setCurrentItem(position, false);
                }
                mSlidingRootNav.closeMenu();
                break;
            case POS_ABOUT:
                openNewPage(AboutFragment.class);
                break;
            case POS_LOGOUT:
                DialogLoader.getInstance().showConfirmDialog(
                        this,
                        getString(R.string.lab_logout_confirm),
                        getString(R.string.lab_yes),
                        (dialog, which) -> {
                            dialog.dismiss();
                            TokenUtils.handleLogoutSuccess();
                            finish();
                        },
                        getString(R.string.lab_no),
                        (dialog, which) -> dialog.dismiss()
                );
                break;
            default:
                break;
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(mMenuIcons[position], mMenuTitles[position])
                .withIconTint(ThemeUtils.resolveColor(this, R.attr.xui_config_color_content_text))
                .withTextTint(ThemeUtils.resolveColor(this, R.attr.xui_config_color_content_text))
                .withSelectedIconTint(ThemeUtils.resolveColor(this, R.attr.colorAccent))
                .withSelectedTextTint(ThemeUtils.resolveColor(this, R.attr.colorAccent));
    }

    public void openMenu() {
        if (mSlidingRootNav != null) {
            mSlidingRootNav.openMenu();
        }
    }

    public void closeMenu() {
        if (mSlidingRootNav != null) {
            mSlidingRootNav.closeMenu();
        }
    }

    public boolean isMenuOpen() {
        if (mSlidingRootNav != null) {
            return mSlidingRootNav.isMenuOpened();
        }
        return false;
    }

    /**
     * ????????????????????????
     */
    private void showInputDialog() {
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.ic_checked)
                .title("??????????????????")
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(
                        getString(R.string.hint_input),
                        null,
                        false,
                        ((dialog, input) -> Log.d("", "")))
                .positiveText(R.string.lab_search)
                .negativeText(R.string.lab_cancel)
                .onPositive((dialog, which) -> {
                    String organizationInfo = dialog.getInputEditText().getText().toString();
                    if (organizationInfo.length() == 0) {
                        XToastUtils.toast("??????????????????");
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("organizationInfo", organizationInfo);
                        openNewPage(ApplyFormFragment.class, bundle);
                    }

                })
                .cancelable(false)
                .show();
    }

    //??????????????????
    private void showBottomSheetListDialog() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_organization_sheet, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView dialogClose = view.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        initDialogList(recyclerView);

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void initDialogList(RecyclerView recyclerView) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        BroccoliSimpleDelegateAdapter<OrganizationVO> mOrganizationAdapter = new BroccoliSimpleDelegateAdapter<OrganizationVO>(R.layout.adapter_sheet_item, new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, OrganizationVO model, int position) {
                if (model != null) {
                    holder.bindDataToViewById(view -> {
                        TextView title = (TextView) view;
                        title.setText(model.getOrganizationName());
                    }, R.id.or_title);
                    holder.bindDataToViewById(view -> {
                        TextView title = (TextView) view;
                        title.setText(model.getOrganizationLevel());
                    }, R.id.or_subtitle);
                    holder.bindDataToViewById(view -> {
                        RadiusImageView avatar = (RadiusImageView) view;
                        //????????????
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getOrganizationAvatar()));
                    }, R.id.or_avatar_1);
                    holder.bindDataToViewById(view -> {
                        TextView now = (TextView) view;
                        if (organizationName.equals(model.getOrganizationName())) {
                            now.setVisibility(View.VISIBLE);
                        } else {
                            now.setVisibility(View.GONE);
                        }
                    }, R.id.or_now);
                    holder.bindDataToViewById(view -> {
                        Button change = (Button) view;
                        if (organizationName.equals(model.getOrganizationName())) {
                            change.setVisibility(View.GONE);
                        } else {
                            change.setVisibility(View.VISIBLE);
                        }
                    }, R.id.or_change);
                    holder.click(R.id.or_change, view -> {
                        dialog.dismiss();
                        apiChangeOrganization(String.valueOf(model.getOrganizationId()),position);
                    });
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.sheet_view)
                );
            }
        };
        recyclerView.setAdapter(mOrganizationAdapter);
        mOrganizationAdapter.refresh(relationships);
    }

    private void apiChangeOrganization(String id, int position) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("organizationId", id);
        MyHttp.get("/user/switchOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                organizationName = relationships.get(position).getOrganizationName();
                int organizationId = relationships.get(position).getOrganizationId();
                toolbar.setTitle(organizationName);

                // componentsFragment.setArguments(organizationBundle);
                // inputFragment.setArguments(organizationBundle);
                XToastUtils.success("???????????????");
                TextView Name = findViewById(R.id.tv_organization);
                Name.setText(organizationName);


                //????????????VO??????
                MMKVUtils.put("organizationName",organizationName);
                MMKVUtils.put("organizationId",organizationId);

                refreshMenu();

            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                XToastUtils.error("?????????????????????????????????????????????");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterDeviceEventReceiver();
    }

    public void isUpdate(Context context){
        MyHttp.getDownload("/system/getNewestAppVersion", TokenUtils.getToken(), new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
               if(!VersionUtils.Version.equals(data.getString("data"))){
                   showUpdateDialog(context,null);
               }
            }

            @Override
            public void fail(JSONObject error) throws JSONException {

            }
        });
    }
    /**
     * ?????????????????????
     *
     * @param context
     * @param submitListener ???????????????
     * @return
     */
    public Dialog showUpdateDialog(Context context, MaterialDialog.SingleButtonCallback submitListener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText(R.string.update_agree).onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                        openNewPage(AboutFragment.class);
                    }
                })
                .negativeText(R.string.update_disagree).onNegative(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
        dialog.setContent(getUpdateContent(context));
        //????????????????????????
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }
    /**
     * @return ????????????
     */
    private static SpannableStringBuilder getUpdateContent(Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder()
                .append("    ???????????????????????????????????????\n");
        return stringBuilder;
    }

    //?????????????????????
    private BroadcastReceiver deviceEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
               return;
            }
            if (ACTION.equals(intent.getAction())) {
                getUnreadCount();
            }
         }
    };



    /**
     * ????????????
     */
    private void refreshMenu(){
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);
    }


    /**
     * ?????????????????????
     */
    private void getUnreadCount(){

        int userId = MMKVUtils.getInt("userId",0);

        Uri myURI = MyCP.Session.CONTENT_URI;

        String[] selectionArgs = {String.valueOf(userId)};
        String[] columns = new String[]{MyCP.Session.id,MyCP.Session.userId,MyCP.Session.unreadCount};
        String selection = MyCP.Session.ownId + "=?";


        Cursor cursor = cr.query(myURI, columns, selection, selectionArgs, null);

        int count = 0;
        while (cursor.moveToNext()) {
            int unreadCount = cursor.getInt(cursor.getColumnIndex(MyCP.Session.unreadCount));
            count += unreadCount;
        }
        cursor.close();

        if(count!=0){
            tvUnreadCount.setVisibility(View.VISIBLE);
            tvUnreadCount.setText(String.valueOf(count));
        }else {
            tvUnreadCount.setVisibility(View.INVISIBLE);
        }

    }



    //??????????????????
    private void registerDeviceEventReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(deviceEventReceiver, intentFilter);
    }

    //??????????????????
    private void unregisterDeviceEventReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceEventReceiver);
    }




    @Override
    protected void onPause() {
        super.onPause();
        unregisterDeviceEventReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerDeviceEventReceiver();
        getUnreadCount();
    }

}