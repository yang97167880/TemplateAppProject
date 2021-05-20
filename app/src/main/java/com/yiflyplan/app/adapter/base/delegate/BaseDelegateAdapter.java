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

package com.yiflyplan.app.adapter.base.delegate;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.imageview.edit.ViewType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * 通用的DelegateAdapter适配器
 *
 * @author xuexiang
 * @since 2020/3/20 12:44 AM
 */
public abstract class BaseDelegateAdapter<T, H extends RecyclerView.ViewHolder> extends XDelegateAdapter<T, H> {
    private Class<H> vClass;

    public BaseDelegateAdapter(Class<H> cls) {
        super();
        this.vClass = cls;

    }

    public BaseDelegateAdapter(Collection<T> list, Class<H> cls) {
        super(list);
        this.vClass = cls;
    }

    public BaseDelegateAdapter(T[] data, Class<H> cls) {
        super(data);
        this.vClass = cls;
    }


    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    protected abstract int getItemLayoutId(int viewType);

    @NonNull
    @Override
    protected H getViewHolder(@NonNull ViewGroup parent, int viewType) throws Exception {
        try {
            View view = inflateView(parent, getItemLayoutId(viewType));
            return vClass.getConstructor(View.class).newInstance(view);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new Exception("找不到该View");
    }

}
