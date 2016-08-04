/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.views.recyclerview.customcontrols;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

/**
 * Created by willi on 01.07.16.
 */
public class CodeView extends RecyclerViewItem {

    private TextView mTitleView;
    private TextView mSummaryView;
    private TextView mRequiredText;
    private TextView mCodeView;
    private AppCompatButton mTestButtonView;

    private CharSequence mTitle;
    private CharSequence mSummary;
    private boolean mRequired;
    private CharSequence mCode;
    private boolean mTesting;
    private String mOutput;
    private View mOutputParent;

    private Thread mScriptThread;
    private OnTestListener mOnTestListener;

    public interface OnTestListener {
        void onTestResult(CodeView codeView, String output);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_code_view;
    }

    @Override
    public void onCreateView(final View view) {
        mTitleView = (TextView) view.findViewById(R.id.title);
        mSummaryView = (TextView) view.findViewById(R.id.summary);
        mRequiredText = (TextView) view.findViewById(R.id.required_text);
        mCodeView = (TextView) view.findViewById(R.id.code);
        mTestButtonView = (AppCompatButton) view.findViewById(R.id.test_button);
        mOutputParent = view.findViewById(R.id.output_parent);

        final View progress = view.findViewById(R.id.progress);
        final TextView outputTextView = (TextView) view.findViewById(R.id.output);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScriptThread != null) return;
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onClick(CodeView.this);
                }
            }
        });
        mTestButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScriptThread != null) return;
                mOutputParent.setVisibility(View.VISIBLE);
                outputTextView.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                mScriptThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mOutput = RootUtils.runScript(mCode.toString());
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                outputTextView.setVisibility(View.VISIBLE);
                                outputTextView.setText(mOutput);
                                mScriptThread = null;
                                if (mOnTestListener != null) {
                                    mOnTestListener.onTestResult(CodeView.this, mOutput);
                                }
                            }
                        });
                    }
                });
                mScriptThread.start();
            }
        });

        if (Utils.DARK_THEME) {
            mTitleView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        }

        super.onCreateView(view);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        refresh();
    }

    public void setSummary(CharSequence summary) {
        mSummary = summary;
        refresh();
    }

    public void setRequired(boolean required) {
        mRequired = required;
        refresh();
    }

    public void setCode(CharSequence code) {
        mCode = code;
        refresh();
    }

    public void setTesting(boolean enabled) {
        mTesting = enabled;
        refresh();
    }

    public void resetTest() {
        mOutput = null;
        refresh();
    }

    public void setOnTestListener(OnTestListener onTestListener) {
        mOnTestListener = onTestListener;
    }

    public CharSequence getCode() {
        return mCode;
    }

    public String getOutput() {
        return mOutput;
    }

    @Override
    protected void refresh() {
        super.refresh();

        if (mTitleView != null && mTitle != null) {
            mTitleView.setText(mTitle);
        }

        if (mSummaryView != null && mSummary != null) {
            mSummaryView.setText(mSummary);
        }

        if (mRequiredText != null) {
            mRequiredText.setVisibility(mRequired ? View.VISIBLE : View.GONE);
        }

        if (mCodeView != null && mCode != null) {
            mCodeView.setText(mCode);
        }

        if (mTestButtonView != null) {
            mTestButtonView.setVisibility(mTesting ? View.VISIBLE : View.INVISIBLE);
        }

        if (mOutput == null && mOutputParent != null) {
            mOutputParent.setVisibility(View.GONE);
        }
    }

}
