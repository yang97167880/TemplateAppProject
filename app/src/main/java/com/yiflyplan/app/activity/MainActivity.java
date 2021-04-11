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

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
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
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.guidview.GuideCaseQueue;
import com.xuexiang.xui.widget.guidview.GuideCaseView;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.impl.GlideImageLoadStrategy;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;
import com.xuexiang.xutil.system.DeviceUtils;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.CurrentUserVO;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
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
import com.yiflyplan.app.fragment.notices.NoticesFragment;
import com.yiflyplan.app.fragment.organization.SearchOrganizationFragment;
import com.yiflyplan.app.fragment.organization.components.ApplyFormFragment;
import com.yiflyplan.app.fragment.organization.OrganizationFragment;
import com.yiflyplan.app.fragment.organization.components.ComponentsFragment;
import com.yiflyplan.app.utils.MapDataCache;
import com.yiflyplan.app.utils.TokenUtils;
import com.yiflyplan.app.utils.XToastUtils;
import com.yiflyplan.app.widget.GuideTipsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * 程序主页面,只是一个简单的Tab例子
 *
 * @author xuexiang
 * @since 2019-07-07 23:53
 */
public class MainActivity extends BaseActivity implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /**
     * 底部导航栏
     */
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    private String[] mTitles;

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
    private CurrentUserVO userVO;
    OrganizationVO organizationVO;
    List<OrganizationVO> relationships;
    private Bundle organizationBundle;

    ComponentsFragment componentsFragment;
    InputFragment inputFragment;
    BottomSheetDialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取传递过来的用户数据
        Intent intent = this.getIntent();
        userVO = (CurrentUserVO) intent.getSerializableExtra(CURRENTUSER);
        organizationVO = userVO.getCurrentOrganization();
        relationships = userVO.getRelationships();

        organizationBundle = new Bundle();
        organizationBundle.putSerializable("organization", userVO.getCurrentOrganization());

        MobclickAgent.onProfileSignIn(DeviceUtils.getAndroidID());

        initViews();

        initData();

        initSlidingMenu(savedInstanceState);

        initListeners();
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
        mMenuTitles = ResUtils.getStringArray(R.array.menu_titles);
        mMenuIcons = ResUtils.getDrawableArray(this, R.array.menu_icons);
    }

    private void initViews() {
        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(organizationVO.getOrganizationName());
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

        MapDataCache.putCache("organization",organizationVO);
//        componentsFragment = new ComponentsFragment();
//        inputFragment = new InputFragment();
//        componentsFragment.setArguments(organizationBundle);
//        inputFragment.setArguments(organizationBundle);

        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
                new ComponentsFragment(),
                new InputFragment(),
//                new ProfileFragment(),
                new NoticesFragment(),
        };

        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
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
        TextView userName = findViewById(R.id.tv_name);
        userName.setText(userVO.getUserName());

        TextView organizationName = findViewById(R.id.tv_organization);
        organizationName.setText(organizationVO.getOrganizationName());
        RadiusImageView userAvatar = findViewById(R.id.iv_avatar);
        //String url = "https://light-plant.oss-cn-beijing.aliyuncs.com/2021/03/22/2fac6a7f3a764dec8eae65046924296d.jpg";
        //Glide.with(this).load(url).into(userAvatar);
        GlideImageLoadStrategy lodeImg = new GlideImageLoadStrategy();
        lodeImg.loadImage(userAvatar, userVO.getUserAvatar());

        LinearLayout mLLMenu = mSlidingRootNav.getLayout().findViewById(R.id.ll_menu);
        final AppCompatImageView ivQrcode = mSlidingRootNav.getLayout().findViewById(R.id.iv_qrcode);
        Bundle bundle = new Bundle();
        bundle.putString("userAvatar", userVO.getUserAvatar());
        bundle.putString("userName", userVO.getUserName());
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
                                .title("点击进入，可切换主题样式哦～～")
                                .titleSize(18, TypedValue.COMPLEX_UNIT_SP)
                                .focusOn(ivSetting)
                                .build();

                        final GuideCaseView guideStep2 = new GuideCaseView.Builder(MainActivity.this)
                                .title("点击进入，扫码关注哦～～")
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
        //主页事件监听
        viewPager.addOnPageChangeListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_privacy:
//                //Utils.showPrivacyDialog(this, null);
//                break;
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
                XToastUtils.toast("点击头部！");
                break;
            default:
                break;
        }
    }

    //=============ViewPager===================//

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = bottomNavigation.getMenu().getItem(position);
        if (position == 0) {
            toolbar.setTitle(organizationVO.getOrganizationName());
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
     * 底部导航栏点击事件
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            if (index == 0) {
                toolbar.setTitle(organizationVO.getOrganizationName());
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
     * 菜单、返回键响应
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
        XToastUtils.toast("再按一次退出程序");
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
                        toolbar.setTitle(organizationVO.getOrganizationName());
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
     * 带输入框的对话框
     */
    private void showInputDialog() {
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.ic_checked)
                .title("申请加入机构")
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
                        XToastUtils.toast("内容不能为空");
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("organizationInfo", organizationInfo);
                        openNewPage(ApplyFormFragment.class, bundle);
                    }

                })
                .cancelable(false)
                .show();
    }

    //切换机构弹窗
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
                        //设置头像
                        GlideImageLoadStrategy img = new GlideImageLoadStrategy();
                        img.loadImage(avatar, Uri.parse(model.getOrganizationAvatar()));
                    }, R.id.or_avatar_1);
                    holder.bindDataToViewById(view -> {
                        TextView now = (TextView) view;
                        if (organizationVO.getOrganizationName().equals(model.getOrganizationName())) {
                            now.setVisibility(View.VISIBLE);
                        } else {
                            now.setVisibility(View.GONE);
                        }
                    }, R.id.or_now);
                    holder.bindDataToViewById(view -> {
                        Button change = (Button) view;
                        if (organizationVO.getOrganizationName().equals(model.getOrganizationName())) {
                            change.setVisibility(View.GONE);
                        } else {
                            change.setVisibility(View.VISIBLE);
                        }
                    }, R.id.or_change);
                    holder.click(R.id.or_change, view -> {
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
        MyHttp.get("/organization/checkUserBelongsToOrganization", TokenUtils.getToken(), params, new MyHttp.Callback() {
            @Override
            public void success(JSONObject data) throws JSONException {
                organizationVO = relationships.get(position);
                toolbar.setTitle(organizationVO.getOrganizationName());

                organizationBundle = new Bundle();
                organizationBundle.putSerializable("organization", organizationVO);

               // componentsFragment.setArguments(organizationBundle);
               // inputFragment.setArguments(organizationBundle);
                XToastUtils.success("切换成功！");
                TextView organizationName = findViewById(R.id.tv_organization);
                organizationName.setText(organizationVO.getOrganizationName());
                dialog.dismiss();

                //更新全局VO对象
                MapDataCache.putCache("organization",organizationVO);

            }

            @Override
            public void fail(JSONObject error) throws JSONException {
                XToastUtils.error("您没有加入该机构，请刷新检查！");
            }
        });
    }
}

