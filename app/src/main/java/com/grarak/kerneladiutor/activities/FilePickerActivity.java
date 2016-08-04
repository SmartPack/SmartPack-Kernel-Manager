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
package com.grarak.kerneladiutor.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 04.07.16.
 */
public class FilePickerActivity extends BaseActivity {

    public static final String PATH_INTENT = "path";
    public static final String EXTENSION_INTENT = "extension";
    public static final String RESULT_INTENT = "result";

    private String mPath;
    private String mExtension;
    private FilePickerFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initToolBar();

        mPath = getIntent().getStringExtra(PATH_INTENT);
        mExtension = getIntent().getStringExtra(EXTENSION_INTENT);

        RootFile path = new RootFile(mPath);
        if (!path.exists() || !path.isDirectory()) {
            mPath = "/";
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mFragment
                = (FilePickerFragment) getFragment(), "fragment").commit();
    }

    private Fragment getFragment() {
        Fragment filePickerFragment = getSupportFragmentManager().findFragmentByTag("fragment");
        if (filePickerFragment == null) {
            filePickerFragment = FilePickerFragment.newInstance(mPath, mExtension);
        }
        return filePickerFragment;
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null && !mFragment.mPath.equals("/")) {
            if (mFragment.mLoadAsyncTask == null) {
                mFragment.mPath = new RootFile(mFragment.mPath).getParentFile().toString();
                mFragment.reload();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        getSupportFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

    public static class FilePickerFragment extends RecyclerViewFragment {

        private String mPath;
        private String mExtension;
        private Drawable mDirImage;
        private Drawable mFileImage;
        private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoadAsyncTask;
        private AlertDialog.Builder mPickDialog;

        @Override
        protected boolean showViewPager() {
            return false;
        }

        public static FilePickerFragment newInstance(String path, String extension) {
            Bundle args = new Bundle();
            args.putString(PATH_INTENT, path);
            args.putString(EXTENSION_INTENT, extension);
            FilePickerFragment fragment = new FilePickerFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void init() {
            super.init();
            if (mPath == null) {
                mPath = getArguments().getString(PATH_INTENT);
            }
            if (mExtension == null) {
                mExtension = getArguments().getString(EXTENSION_INTENT);
            }
            if (mDirImage == null) {
                mDirImage = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dir));
                DrawableCompat.setTint(mDirImage, ContextCompat.getColor(getActivity(), R.color.colorAccent));
            }
            if (mFileImage == null) {
                mFileImage = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_file));
                DrawableCompat.setTint(mFileImage, ContextCompat.getColor(getActivity(), R.color.colorAccent));
            }
            if (mPickDialog != null) {
                mPickDialog.show();
            }

            ActionBar actionBar;
            if ((actionBar = ((FilePickerActivity) getActivity()).getSupportActionBar()) != null) {
                actionBar.setTitle(mPath);
            }
        }

        @Override
        protected void addItems(List<RecyclerViewItem> items) {
            load(items);
        }

        @Override
        protected void postInit() {
            super.postInit();
            ActionBar actionBar;
            if ((actionBar = ((FilePickerActivity) getActivity()).getSupportActionBar()) != null) {
                actionBar.setTitle(mPath);
            }
        }

        private void reload() {
            if (mLoadAsyncTask == null) {
                mLoadAsyncTask = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearItems();
                        showProgress();
                    }

                    @Override
                    protected List<RecyclerViewItem> doInBackground(Void... params) {
                        List<RecyclerViewItem> items = new ArrayList<>();
                        load(items);
                        return items;
                    }

                    @Override
                    protected void onPostExecute(List<RecyclerViewItem> items) {
                        super.onPostExecute(items);
                        for (RecyclerViewItem item : items) {
                            addItem(item);
                        }
                        hideProgress();
                        mLoadAsyncTask = null;

                        Activity activity = getActivity();
                        ActionBar actionBar;
                        if (activity != null && (actionBar = ((FilePickerActivity) activity)
                                .getSupportActionBar()) != null) {
                            actionBar.setTitle(mPath);
                        }
                    }
                };
                mLoadAsyncTask.execute();
            }
        }

        private void load(List<RecyclerViewItem> items) {
            RootFile path = new RootFile(mPath).getRealPath();
            mPath = path.toString();

            if (!path.isDirectory()) path = path.getParentFile();
            List<RootFile> dirs = new ArrayList<>();
            List<RootFile> files = new ArrayList<>();
            for (RootFile file : path.listFiles()) {
                if (file.isDirectory()) {
                    dirs.add(file);
                } else {
                    files.add(file);
                }
            }

            for (final RootFile dir : dirs) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setSummary(dir.getName());
                descriptionView.setDrawable(mDirImage);
                descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        mPath = dir.toString();
                        reload();
                    }
                });

                items.add(descriptionView);
            }
            for (final RootFile file : files) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setSummary(file.getName());
                descriptionView.setDrawable(mFileImage);
                descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                    @Override
                    public void onClick(RecyclerViewItem item) {
                        if (mExtension != null && !mExtension.isEmpty() && file.getName() != null
                                && !file.getName().endsWith(mExtension)) {
                            Utils.toast(getString(R.string.wrong_extension, mExtension), getActivity());
                        } else {
                            mPickDialog = ViewUtils.dialogBuilder(getString(R.string.select_question, file.getName()),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.putExtra(RESULT_INTENT, file.toString());
                                            getActivity().setResult(0, intent);
                                            getActivity().finish();
                                        }
                                    }, new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            mPickDialog = null;
                                        }
                                    }, getActivity());
                            mPickDialog.show();
                        }
                    }
                });

                items.add(descriptionView);
            }
        }

    }

}
