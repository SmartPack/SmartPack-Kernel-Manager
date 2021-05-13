/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Common;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 29, 2020
 */
public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialTextView mTitle = findViewById(R.id.title);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);

        mTitle.setText(Common.getWebViewTitle());
        mCancel.setOnClickListener(v -> finish());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WebViewFragment()).commit();

        mBack.setOnClickListener(v -> finish());
    }

    public static class WebViewFragment extends Fragment {

        @SuppressLint("SetJavaScriptEnabled")
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View mRootView = inflater.inflate(R.layout.fragment_webview, container, false);

            MaterialTextView mProgress = mRootView.findViewById(R.id.progress_message);
            WebView mWebView = mRootView.findViewById(R.id.webview);

            WebSettings mWebSettings = mWebView.getSettings();
            mWebSettings.setDomStorageEnabled(true);
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setBuiltInZoomControls(true);

            mWebView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    mProgress.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            });

            mWebView.setWebChromeClient(new WebChromeClient() {
                @SuppressLint("SetTextI18n")
                public void onProgressChanged(WebView view, int progress) {
                    mProgress.setText("Loading (" + progress + "%) ...");
                }
            });

            mWebView.loadUrl(Common.getUrl());

            requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        requireActivity().finish();
                    }
                }
            });

            return mRootView;
        }

    }

}