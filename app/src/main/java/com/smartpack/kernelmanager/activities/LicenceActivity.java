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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.smartpack.kernelmanager.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 29, 2020
 */

public class LicenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(v -> onBackPressed());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LicenceFragment()).commit();

        mBack.setOnClickListener(v -> onBackPressed());
    }

    public static class LicenceFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            WebView mWebView = new WebView(requireActivity());
            mWebView.loadUrl("https://www.gnu.org/licenses/gpl-3.0-standalone.html");
            mWebView.setWebViewClient(new WebViewClient());

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

            return mWebView;
        }

    }

}