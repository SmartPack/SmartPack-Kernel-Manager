package com.smartpack.kernelmanager.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Adapted from https://github.com/Grarak/KernelAdiutor by Willi Ye.
 */

public abstract class InputValueView extends RecyclerViewItem {

    private TextView mTitleView;
    private View mValueParent;
    private TextView mValueView;
    private View mProgress;

    private CharSequence mTitle;
    private String mValue;
    private int mValuesRes;

    @Override
    public int getLayoutRes() {
        return R.layout.rv_input_value_view;
    }

    @Override
    public void onCreateView(View view) {
        mTitleView = view.findViewById(R.id.title);
        mValueParent = view.findViewById(R.id.value_parent);
        mValueView = view.findViewById(R.id.value);
        mProgress = view.findViewById(R.id.progress);

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setValue(String value) {
        mValue = value;
        refresh();
    }

    public void setValue(@StringRes int value) {
        mValuesRes = value;
        refresh();
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitleView != null) {
            if (mTitle != null) {
                mTitleView.setText(mTitle);
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }

        if (mValueView != null && (mValue != null || mValuesRes != 0)) {
            if (mValue == null) {
                mValue = mValueView.getContext().getString(mValuesRes);
            }
            mValueView.setText(mValue);
            mValueView.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mValueParent.setVisibility(mValue.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

}
