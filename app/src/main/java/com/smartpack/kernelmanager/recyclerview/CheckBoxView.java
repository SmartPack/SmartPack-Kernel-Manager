/*
 * Copyright (C) 2019-2020 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.kernel.wake.S2w;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.ValueView;

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
    private boolean[] mCheckBoxes = new boolean[]{false, false, false, false};
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
        String[] items = mItems.toArray(new String[mItems.size()]);

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
        if ((mCurrValue & 1 ) != 0) mCheckBoxes[0]=true;
        else mCheckBoxes[0] = false;

        if ((mCurrValue & 2 ) != 0) mCheckBoxes[1]=true;
        else mCheckBoxes[1] = false;

        if ((mCurrValue & 4 ) != 0) mCheckBoxes[2]=true;
        else mCheckBoxes[2] = false;

        if ((mCurrValue & 8 ) != 0) mCheckBoxes[3]=true;
        else mCheckBoxes[3] = false;
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
