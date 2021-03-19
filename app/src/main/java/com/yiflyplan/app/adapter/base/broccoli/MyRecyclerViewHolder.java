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

package com.yiflyplan.app.adapter.base.broccoli;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.widget.imageview.ImageLoader;

/**
 * 自定义RecyclerViewHolder
 */
public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public MyRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    /**
     * 通过id找到View
     *
     * @param viewId
     * @return
     */
    public View findViewById(int viewId) {
        View result = mViews.get(viewId);
        if (result == null) {
            result = itemView.findViewById(viewId);
            mViews.put(viewId, result);
        }
        return result;
    }

    public View findView(int id) {
        return id == 0 ? itemView : findViewById(id);
    }

    /**
     * 设置文字
     *
     * @param id
     * @param sequence
     * @return
     */
    public MyRecyclerViewHolder text(int id, CharSequence sequence) {
        View view = findView(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(sequence);
        }
        return this;
    }

    /**
     * 将数据通过id绑定到视图上
     *
     * @param bindDataOnView
     * @param id
     * @return
     */
    public MyRecyclerViewHolder bindDataToViewById(BindDataOnView bindDataOnView, int id) {
        View view = findViewById(id);
        if (view != null) {
            bindDataOnView.bindData(view);
        }
        return this;
    }

//    private List<View> getAllComponent() {
//        return findAllComponents(itemView);
//    }

//    private List<View> findAllComponents(View view) {
//        List<View> result = new ArrayList<>();
//        if (view instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) view;
//            for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                View childAt = viewGroup.getChildAt(i);
//                result.add(childAt);
//                result.addAll(findAllComponents(childAt));
//            }
//        }
//        return result;
//    }

//    public void bind(T abstractVO) {
//        Class<? extends AbstractVO> voClass = abstractVO.getClass();
//        Field[] fields = voClass.getFields();
//        Field[] declaredFields = voClass.getDeclaredFields();
//        List<Field> fieldList = new ArrayList<>(fields.length + declaredFields.length);
//        fieldList.addAll(Arrays.asList(fields));
//        fieldList.addAll(Arrays.asList(declaredFields));
//        for (Field field : fieldList) {
//            field.setAccessible(true);
//            String name = field.getName();
//            R.class
//        }
//    }

    /**
     * 数据绑定到视图的回调接口
     */
    public interface BindDataOnView {
        void bindData(View view);
    }

    public final Context getContext() {
        return itemView.getContext();
    }

    /**
     * 设置图片
     *
     * @param id
     * @param uri 图片资源
     * @return
     */
    public MyRecyclerViewHolder image(@IdRes int id, Object uri) {
        View view = findView(id);
        if (view instanceof ImageView) {
            ImageLoader.get().loadImage((ImageView) view, uri);
        }
        return this;
    }

    /**
     * 设置控件的点击监听
     *
     * @param id
     * @param listener
     * @return
     */
    public MyRecyclerViewHolder click(@IdRes int id, final View.OnClickListener listener) {
        View view = findView(id);
        if (listener != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }
}
