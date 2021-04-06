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

package com.yiflyplan.app.fragment.blueTooth;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author xhy
 */
@Page(name = "物品录入")
public class EntryGarbageFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_type)
    Button btnType;
    @BindView(R.id.btn_package)
    Button btnPackage;
    @BindView(R.id.btn_pollution)
    Button btnPollution;

    private String[] mTypeOption;
    private String[] mPackageOption;
    private String[] mPollutionOption;

    private int typeSelectOption = 0;
    private int packageSelectOption = 0;
    private int pollutionSelectOption = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_entry_garbage;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initViews() {
        toolbar.setTitle("物品录入");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            @SingleClick
            public void onClick(View v) {
                popToBack();
            }
        });
        //假数据 没上api
        mTypeOption = ResUtils.getStringArray(R.array.menu_titles);
        mPackageOption = ResUtils.getStringArray(R.array.menu_titles);
        mPollutionOption = ResUtils.getStringArray(R.array.menu_titles);
    }

    @OnClick({R.id.btn_type, R.id.btn_package, R.id.btn_pollution})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_type:
                showTypeView();
                break;
            case R.id.btn_package:
                showPackageView();
                break;
            case R.id.btn_pollution:
                showPollutionView();
                break;
            default:
                break;
        }
    }

    /**
     * 类型选择
     */
    private void showTypeView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            btnType.setText(mTypeOption[options1]);
            typeSelectOption = options1;
            return false;
        })
                .setTitleText(getString(R.string.title_type_select))
                .setSelectOptions(typeSelectOption)
                .build();
        pvOptions.setPicker(mTypeOption);
        pvOptions.show();
    }


    /**
     * 包装选择
     */
    private void showPackageView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            btnPackage.setText(mPackageOption[options1]);
            packageSelectOption = options1;
            return false;
        })
                .setTitleText(getString(R.string.title_package_select))
                .setSelectOptions(packageSelectOption)
                .build();
        pvOptions.setPicker(mPackageOption);
        pvOptions.show();
    }

    /**
     * 污染选择
     */
    private void showPollutionView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), (v, options1, options2, options3) -> {
            btnPollution.setText(mPollutionOption[options1]);
            pollutionSelectOption = options1;
            return false;
        })
                .setTitleText(getString(R.string.title_pollution_select))
                .setSelectOptions(pollutionSelectOption)
                .build();
        pvOptions.setPicker(mPollutionOption);
        pvOptions.show();
    }
}
