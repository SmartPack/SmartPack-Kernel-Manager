/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.views.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.kernel.wake.S2w;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 03, 2018
 *
 * Based on the original implementation by MoroGoku
 */

public class CheckBoxView extends ValueView {

    private View mView;
    private Dialog mDialog;
    private List<String> mItems = new ArrayList<>();
    private final boolean[] mCheckBoxes = new boolean[]{false, false, false, false};
    private int mCurrValue = S2w.getInstance().get();


    @Override
    public void onRecyclerViewCreate(Activity activity) {
        super.onRecyclerViewCreate(activity);

        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void onCreateView(View view) {
        mView = view;
        super.onCreateView(view);
    }

    public void setItem(String item) {
        setValue(item);
    }

    public void setItem(int position) {
        if (position >= 0 && position < mItems.size()) {
            setValue(mItems.get(position));
        } else {
            setValue(R.string.not_in_range);
        }
    }

    public void setItems(List<String> items) {
        mItems = items;
        refresh();
    }

    private void showDialog(Context context) {
        String[] items = mItems.toArray(new String[0]);

        setCheckBoxesFromInt();
        mDialog = new Dialog(context)

                .setMultiChoiceItems(items, mCheckBoxes,
                        (dialog, which, isChecked) -> {
                            mCheckBoxes[which] = isChecked;
                        })

                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    setIntValueFromCheckBoxes(context);
                    setItem(S2w.getInstance().getStringValue(context, mCurrValue));
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {

                })
                .setOnDismissListener(dialog -> mDialog = null);
        if (getTitle() != null) {
            mDialog.setTitle(getTitle());
        }
        mDialog.show();
    }

    private void setCheckBoxesFromInt(){
        mCheckBoxes[0]= (mCurrValue & 1) != 0;
        mCheckBoxes[1]= (mCurrValue & 2) != 0;
        mCheckBoxes[2]= (mCurrValue & 4) != 0;
        mCheckBoxes[3]= (mCurrValue & 8) != 0;
    }

    private void setIntValueFromCheckBoxes(Context context){
        mCurrValue = 0;
        if(mCheckBoxes[0]) mCurrValue+=1;
        if(mCheckBoxes[1]) mCurrValue+=2;
        if(mCheckBoxes[2]) mCurrValue+=4;
        if(mCheckBoxes[3]) mCurrValue+=8;

        S2w.getInstance().set(mCurrValue, context);
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mView != null && getValue() != null) {
            mView.setOnClickListener(v -> showDialog(v.getContext()));
        }
    }

}