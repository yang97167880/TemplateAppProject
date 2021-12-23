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
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yiflyplan.app.R;
import com.yiflyplan.app.adapter.VO.OrganizationVO;
import com.yiflyplan.app.adapter.VO.ProductVO;
import com.yiflyplan.app.adapter.VO.UploadData;
import com.yiflyplan.app.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.yiflyplan.app.adapter.base.broccoli.MyRecyclerViewHolder;
import com.yiflyplan.app.adapter.base.delegate.SimpleDelegateAdapter;
import com.yiflyplan.app.adapter.entity.DeviceEntity;
import com.yiflyplan.app.core.BaseFragment;
import com.yiflyplan.app.fragment.SearchFragment;
import com.yiflyplan.app.fragment.organization.components.ProductCheckWeightFragment;
import com.yiflyplan.app.utils.XToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * @author xhy
 */
@Page(name = "蓝牙")
public class BlueToothFragment extends BaseFragment {
    public static final String UPLOAD = "uploadData";
    public static final String CHECK_WEIGHT = "checkWeight";
    private final Integer REQUEST_CODE = 200;
    private BluetoothAdapter bluetoothAdapter;

    private List<DeviceEntity> pairDataList = new ArrayList<>();
    private List<DeviceEntity> unPairDataList = new ArrayList<>();

    private boolean tryConnect = false;

    @BindView(R.id.bluetooth_card_1)
    RecyclerView bluetoothCard1;
    @BindView(R.id.bluetooth_card_2)
    RecyclerView bluetoothCard2;
    @BindView(R.id.bluetooth_refreshLayout)
    SmartRefreshLayout bluetoothLayout;
    @BindView(R.id.match_bluetooth)
    ButtonView mButton;
    @BindView(R.id.organization)
    TextView organization;
    @BindView(R.id.department)
    TextView department;
    @BindView(R.id.fl_address)
    FrameLayout flAddress;


    private SimpleDelegateAdapter<DeviceEntity> mDeviceAdapter1;
    private SimpleDelegateAdapter<DeviceEntity> mDeviceAdapter2;

    private UploadData uploadData;
    private ProductVO product;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected void initViews() {
        Bundle bundle = this.getArguments();
        product = (ProductVO) bundle.getSerializable("product");

        if (product!=null){
            flAddress.setVisibility(View.GONE);
        }

        uploadData = (UploadData) bundle.getSerializable("uploadData");
        if (uploadData!=null){
            organization.setText(uploadData.getOrganizationName());
            department.setText(uploadData.getDepartmentName());
        }

        initBT();

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
                  holder.bindDataToViewById(view ->{
                      MiniLoadingView loadingView = (MiniLoadingView) view;
                      loadingView.setVisibility(View.GONE);
                  },R.id.match_loading);
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getName());
                    },R.id.bluetooth_name);

                    holder.bindDataToViewById(view -> {
                        TextView address = (TextView) view;
                        address.setText(model.getAddress());
                    },R.id.bluetooth_address);

                    holder.click(R.id.bluetooth_item_view,view -> {
                        if (!tryConnect) {
                            bluetoothAdapter.cancelDiscovery();
//                          XToastUtils.info("正在连接，请等待...",200);
                            holder.bindDataToViewById(v ->{
                                MiniLoadingView loadingView = (MiniLoadingView) v;
                                loadingView.setVisibility(View.VISIBLE);
                            },R.id.match_loading);
                            tryConnect = true;
                            if (uploadData==null){
                                uploadData = new UploadData();
                            }
                            uploadData.setAddress(model.getAddress());
                            if (product == null){
                                openNewPage(EntryGarbageFragment.class,UPLOAD,uploadData);
                            }else {
                                Bundle bundle1 = new Bundle();
                                bundle1.putSerializable(UPLOAD,uploadData);
                                bundle1.putSerializable(CHECK_WEIGHT,product);
                                openPage(ProductCheckWeightFragment.class,bundle1);
                            }
                        } else {
                            XToastUtils.info("请刷新后重试...");
                        }
                    });
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
                    holder.bindDataToViewById(view ->{
                        MiniLoadingView loadingView = (MiniLoadingView) view;
                        loadingView.setVisibility(View.GONE);
                    },R.id.match_loading);
                    holder.bindDataToViewById(view -> {
                        TextView name = (TextView) view;
                        name.setText(model.getName());
                    },R.id.bluetooth_name);

                    holder.bindDataToViewById(view -> {
                        TextView address = (TextView) view;
                        address.setText(model.getAddress());
                    },R.id.bluetooth_address);
                    holder.click(R.id.bluetooth_item_view,view -> {
                        if (!tryConnect) {
                            bluetoothAdapter.cancelDiscovery();
//                          XToastUtils.info("正在连接，请等待...",200);
                            holder.bindDataToViewById(v ->{
                                MiniLoadingView loadingView = (MiniLoadingView) v;
                                loadingView.setVisibility(View.VISIBLE);
                            },R.id.match_loading);
                            tryConnect = true;
                            if (uploadData==null){
                                uploadData = new UploadData();
                            }
                            uploadData.setAddress(model.getAddress());
                            if (product == null){
                                openNewPage(EntryGarbageFragment.class,UPLOAD,uploadData);
                            }else {
                                Bundle bundle1 = new Bundle();
                                bundle1.putSerializable(UPLOAD,uploadData);
                                bundle1.putSerializable(CHECK_WEIGHT,product);
                                openPage(ProductCheckWeightFragment.class,bundle1);
                            }

                        } else {
                            XToastUtils.info("请刷新后重试...");
                            tryConnect = false;
                        }
                    });
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
                    XToastUtils.error("该设备可能不支持蓝牙!");
                    return;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                }
                discoveryDevice();

                mDeviceAdapter1.refresh(unPairDataList);
                mDeviceAdapter2.refresh(pairDataList);
                bluetoothLayout.setEnableRefresh(false);
            },1000);
            bluetoothLayout.finishRefresh();
        });
    }

    private void addDeviceToList(BluetoothDevice device) {
       DeviceEntity deviceEntity = new DeviceEntity(device.getName(), device.getAddress());
        //如果不是配''''''''''''对过的设备
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            if(pairDataList.indexOf(deviceEntity)<0){
                pairDataList.add(deviceEntity);
            }
//            }

        } else {
            //
            if(unPairDataList.indexOf(deviceEntity)<0){
            unPairDataList.add(deviceEntity);
            }
//            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        this.getActivity().unregisterReceiver(receiver);
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //如果找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    addDeviceToList(device);
                } else {
                    XToastUtils.error("设备出错！");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
               // XToastUtils.info("搜索结束");
            }
        }
    };

    private void initBT() {
        this.getActivity().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.getActivity().registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        this.getActivity().registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            XToastUtils.error("该设备可能不支持蓝牙！");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        discoveryDevice();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                discoveryDevice();
            } else {
                this.getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
