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

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.button.ButtonView;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.MemberVO;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.DeviceEntity;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.utils.XToastUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * @author xhy
 */
@Page(name = "蓝牙")
public class BlueToothFragment extends BaseFragment {
    private final Integer REQUEST_CODE = 200;
    private BluetoothAdapter bluetoothAdapter;

    private List<DeviceEntity> pairDataList;
    private List<DeviceEntity> unPairDataList;

    private boolean tryConnect = false;

    @BindView(R.id.bluetooth_card_1)
    RecyclerView bluetoothCard1;
    @BindView(R.id.bluetooth_card_2)
    RecyclerView bluetoothCard2;
    @BindView(R.id.bluetooth_refreshLayout)
    SmartRefreshLayout bluetoothLayout;
    @BindView(R.id.match_bluetooth)
    ButtonView mButton;

    private SimpleDelegateAdapter<DeviceEntity> mDeviceAdapter1;
    private SimpleDelegateAdapter<DeviceEntity> mDeviceAdapter2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected void initViews() {
        mButton.setOnClickListener(view -> {
            bluetoothLayout.setEnableRefresh(true);
            bluetoothLayout.autoRefresh();
        });

        VirtualLayoutManager virtualLayoutManager1 = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        RecyclerView.RecycledViewPool viewPool1 = new RecyclerView.RecycledViewPool();
        viewPool1.setMaxRecycledViews(0, 10);

        bluetoothCard1.setLayoutManager(virtualLayoutManager1);
        bluetoothCard1.setRecycledViewPool(viewPool1);

        VirtualLayoutManager virtualLayoutManager2 = new VirtualLayoutManager(Objects.requireNonNull(getContext()));
        RecyclerView.RecycledViewPool viewPool2 = new RecyclerView.RecycledViewPool();
        viewPool2.setMaxRecycledViews(0, 10);
        bluetoothCard2.setLayoutManager(virtualLayoutManager2);
        bluetoothCard2.setRecycledViewPool(viewPool2);

        mDeviceAdapter1 = new BroccoliSimpleDelegateAdapter<DeviceEntity>(R.layout.adapter_bluetooth_item,new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, DeviceEntity model, int position) {
                if(model != null){
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getName());
                    },R.id.bluetooth_name);

                    holder.bindDataToViewById(view -> {
                        TextView address = (TextView) view;
                        address.setText(model.getAddress());
                    },R.id.bluetooth_address);
                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.bluetooth_item_view)
                );
            }
        };

        mDeviceAdapter2 = new BroccoliSimpleDelegateAdapter<DeviceEntity>(R.layout.adapter_bluetooth_item,new LinearLayoutHelper()) {
            @Override
            protected void onBindData(MyRecyclerViewHolder holder, DeviceEntity model, int position) {
                if(model != null){

                }
            }

            @Override
            protected void onBindBroccoli(MyRecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.bluetooth_item_view)
                );
            }
        };

        DelegateAdapter delegateAdapter1 = new DelegateAdapter(virtualLayoutManager1);
        DelegateAdapter delegateAdapter2 = new DelegateAdapter(virtualLayoutManager2);
        delegateAdapter1.addAdapter(mDeviceAdapter1);
        delegateAdapter2.addAdapter(mDeviceAdapter2);
//
        bluetoothCard1.setAdapter(delegateAdapter1);
        bluetoothCard2.setAdapter(delegateAdapter2);
    }
    @Override
    protected void initListeners() {
        //首次刷新
        bluetoothLayout.autoRefresh();
        bluetoothLayout.setOnRefreshListener(bluetoothLayout -> {
            bluetoothLayout.getLayout().postDelayed(() ->{
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (bluetoothAdapter == null) {
                    XToastUtils.info("该设备可能不支持蓝牙!");
                    return;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                }
                discoveryDevice();

                mDeviceAdapter1.refresh(pairDataList);
                mDeviceAdapter2.refresh(unPairDataList);
                bluetoothLayout.setEnableRefresh(false);
            },500);
            bluetoothLayout.finishRefresh();
        });
    }

    private void discoveryDevice() {
        //如果未开启定位
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //开启定位
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }
    }
}
