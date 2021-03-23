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

package com.yiflyplan.app.adapter.menu;

import android.view.ViewGroup;

public abstract class DrawerItem<T extends DrawerAdapter.ViewHolder> {

protected boolean isChecked;

public abstract T createViewHolder(ViewGroup parent);

public abstract void bindViewHolder(T holder);

public DrawerItem setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
        }

public boolean isChecked() {
        return isChecked;
        }

public boolean isSelectable() {
        return true;
        }
}
