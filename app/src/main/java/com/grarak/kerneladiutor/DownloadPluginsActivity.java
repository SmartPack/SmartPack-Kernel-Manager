/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;

import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.json.Plugins;

/**
 * Created by willi on 15.08.15.
 */
public class DownloadPluginsActivity extends BaseActivity {

    public static final String JSON_ARG = "json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFragment(R.id.content_frame, DownloadPluginFragment.newInstance(getIntent().getExtras().getString(JSON_ARG)));
        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null)
            actionBar.setTitle(getString(R.string.plugins));
    }

    @Override
    public int getParentViewId() {
        return R.layout.activity_fragment;
    }

    @Override
    public View getParentView() {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    public static class DownloadPluginFragment extends RecyclerViewFragment {

        @Override
        public boolean showApplyOnBoot() {
            return false;
        }

        public static DownloadPluginFragment newInstance(String json) {
            Bundle args = new Bundle();
            args.putString(JSON_ARG, json);
            DownloadPluginFragment downloadPluginFragment = new DownloadPluginFragment();
            downloadPluginFragment.setArguments(args);
            return downloadPluginFragment;
        }

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);

            final Plugins plugins = new Plugins(getArguments().getString(JSON_ARG));
            for (int i = 0; i < plugins.size(); i++) {
                final int position = i;
                CardViewItem.DCardView mPluginCard = new CardViewItem.DCardView();
                mPluginCard.setTitle(plugins.getName(position));
                String description = plugins.getDescription(position);
                try {
                    PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(plugins.getPackageName(i), 0);
                    String version = packageInfo.versionName;
                    if (version.equals(plugins.getVersion(i)))
                        description += "<br><br><b><font color=\"#00FF00\">" + getString(R.string.installed) + "</font></b>";
                    else
                        description += "<br><br><b><font color=\"#FFA500\">" + getString(R.string.update_available) + ": "
                                + plugins.getVersion(i) + "</font></b>";
                } catch (PackageManager.NameNotFoundException ignored) {
                    description += "<br><br><b><font color=\"#FF0000\">" + getString(R.string.not_installed) + "</font></b>";
                }

                mPluginCard.setDescription(Html.fromHtml(description));
                mPluginCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                    @Override
                    public void onClick(CardViewItem.DCardView dCardView) {
                        Utils.launchUrl(getActivity(), plugins.getDownloadLink(position));
                    }
                });

                addView(mPluginCard);
            }
        }

    }

}
