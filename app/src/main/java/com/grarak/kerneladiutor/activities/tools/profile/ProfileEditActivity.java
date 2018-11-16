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
package com.grarak.kerneladiutor.activities.tools.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.database.tools.profiles.Profiles;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 15.08.16.
 */

public class ProfileEditActivity extends BaseActivity {

    public static final String POSITION_INTENT = "position";

    private static boolean sChanged;
    private int mPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sChanged = false;

        mPosition = getIntent().getIntExtra(POSITION_INTENT, 0);

        setContentView(R.layout.activity_fragments);
        initToolBar();

        getSupportActionBar().setTitle(getString(R.string.edit));

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                getFragment(), "fragment").commit();
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = ProfileEditFragment.newInstance(mPosition);
        }
        return fragment;
    }

    @Override
    public void finish() {
        if (sChanged) {
            setResult(0, new Intent());
        }
        sChanged = false;
        super.finish();
    }

    public static class ProfileEditFragment extends RecyclerViewFragment {

        public static ProfileEditFragment newInstance(int position) {
            Bundle args = new Bundle();
            args.putInt(POSITION_INTENT, position);
            ProfileEditFragment fragment = new ProfileEditFragment();
            fragment.setArguments(args);
            return fragment;
        }

        private Profiles mProfiles;
        private Profiles.ProfileItem mItem;
        private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

        private Dialog mDeleteDialog;

        @Override
        protected boolean showViewPager() {
            return false;
        }

        @Override
        protected void init() {
            super.init();

            if (mDeleteDialog != null) {
                mDeleteDialog.show();
            }

            if (mProfiles == null) {
                mProfiles = new Profiles(getActivity());
            }
            if (mItem == null) {
                mItem = mProfiles.getAllProfiles().get(getArguments()
                        .getInt(POSITION_INTENT));
                if (mItem.getCommands().size() < 1) {
                    Utils.toast(R.string.profile_empty, getActivity());
                    getActivity().finish();
                }
            }
        }

        @Override
        protected void addItems(List<RecyclerViewItem> items) {
            load(items);
        }

        private void reload() {
            if (mLoader == null) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearItems();
                        mLoader = new UILoader(ProfileEditFragment.this);
                        mLoader.execute();
                    }
                }, 250);
            }
        }

        private void load(List<RecyclerViewItem> items) {
            for (final Profiles.ProfileItem.CommandItem commandItem : mItem.getCommands()) {
                final DescriptionView descriptionView = new DescriptionView();
                descriptionView.setTitle(commandItem.getPath());
                descriptionView.setSummary(commandItem.getCommand());
                descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.delete_question,
                                descriptionView.getTitle()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sChanged = true;
                                mItem.delete(commandItem);
                                mProfiles.commit();
                                reload();
                            }
                        }, new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mDeleteDialog = null;
                            }
                        }, getActivity());
                        mDeleteDialog.show();
                    }
                });

                items.add(descriptionView);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mProfiles = null;
            mItem = null;
            mLoader = null;
        }

        private static class UILoader extends AsyncTask<Void, Void, List<RecyclerViewItem>> {

            private WeakReference<ProfileEditFragment> mRefFragment;

            private UILoader(ProfileEditFragment fragment) {
                mRefFragment = new WeakReference<>(fragment);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRefFragment.get().showProgress();
            }

            @Override
            protected List<RecyclerViewItem> doInBackground(Void... params) {
                List<RecyclerViewItem> items = new ArrayList<>();
                mRefFragment.get().load(items);
                return items;
            }

            @Override
            protected void onPostExecute(List<RecyclerViewItem> items) {
                super.onPostExecute(items);
                ProfileEditFragment fragment = mRefFragment.get();
                for (RecyclerViewItem item : items) {
                    fragment.addItem(item);
                }
                fragment.hideProgress();
                fragment.mLoader = null;
            }
        }
    }

}
