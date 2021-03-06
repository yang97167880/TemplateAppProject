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

package com.yiflyplan.app.fragment.components;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.preview.view.SmoothImageView;
import com.yiflyplan.app.R;
import com.yiflyplan.app.core.BaseFragment;

import butterknife.BindView;

import static com.yiflyplan.app.fragment.components.DrawablePreviewFragment.DRAWABLE_ID;

@Page(name = "资源图片预览",anim = CoreAnim.none)
public class DrawablePreviewFragment extends BaseFragment {
    public final static String DRAWABLE_ID = "drawable_id";
    public final static String BITMAP = "bitmap";
    @BindView(R.id.photoView)
    SmoothImageView mImageView;

    private int mDrawableId;
    private Bitmap mBitmap;

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initArgs() {
        Bundle args = getArguments();
        if (args != null) {
            mBitmap = args.getParcelable(BITMAP);
            mDrawableId = args.getInt(DRAWABLE_ID, -1);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_drawable_preview;
    }

    @Override
    protected void initViews() {
        if (mDrawableId != -1){
            mImageView.setImageDrawable(ResUtils.getDrawable(getContext(), mDrawableId));
        }
        if (mBitmap != null) {
            mImageView.setImageBitmap(mBitmap);
        }
        mImageView.setMinimumScale(0.5F);
    }

    @Override
    public void onDestroyView() {
        if (mImageView != null) {
            mImageView.setImageBitmap(null);
        }
        super.onDestroyView();
    }
}
