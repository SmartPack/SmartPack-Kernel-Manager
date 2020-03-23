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
package com.smartpack.kernelmanager.fragments.tools;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatEditText;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.Buildprop;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 10.07.16.
 */
public class BuildpropFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;
    private LinkedHashMap<String, String> mProps;
    private String mKeyText;
    private String mValueText;

    private SearchFragment mSearchFragment;
    private Dialog mAddDialog;
    private Dialog mItemOptionsDialog;
    private Dialog mDeleteDialog;

    private String mKey;
    private String mValue;

    @Override
    protected Drawable getBottomFabDrawable() {
        return getResources().getDrawable(R.drawable.ic_add);
    }

    @Override
    protected boolean showBottomFab() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(mSearchFragment = new SearchFragment());

        if (mAddDialog != null) {
            mAddDialog.show();
        }
        if (mItemOptionsDialog != null) {
            mItemOptionsDialog.show();
        }
        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }
        if (mKey != null) {
            modify(mKey, mValue);
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mProps = Buildprop.getProps();
        load(items);
    }

    private void reload(final boolean read) {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearItems();
                    mLoader = new UILoader(BuildpropFragment.this, read);
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private static class UILoader extends AsyncTask<Void, Void, List<RecyclerViewItem>> {

        private WeakReference<BuildpropFragment> mRefFragment;
        private boolean mRead;

        private UILoader(BuildpropFragment fragment, boolean read) {
            mRefFragment = new WeakReference<>(fragment);
            mRead = read;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefFragment.get().showProgress();
        }

        @Override
        protected List<RecyclerViewItem> doInBackground(Void... voids) {
            BuildpropFragment fragment = mRefFragment.get();
            if (mRead) {
                fragment.mProps = Buildprop.getProps();
            }
            List<RecyclerViewItem> items = new ArrayList<>();
            fragment.load(items);
            return items;
        }

        @Override
        protected void onPostExecute(List<RecyclerViewItem> items) {
            super.onPostExecute(items);
            BuildpropFragment fragment = mRefFragment.get();

            for (RecyclerViewItem item : items) {
                fragment.addItem(item);
            }
            fragment.hideProgress();
            fragment.mLoader = null;
        }
    }

    private void load(final List<RecyclerViewItem> items) {
        if (mProps == null) return;
        String[] titles = mProps.keySet().toArray(new String[0]);
        for (int i = 0; i < mProps.size(); i++) {
            final String title = titles[i];
            final String value = mProps.values().toArray(new String[0])[i];
            if ((mKeyText != null && !title.contains(mKeyText)
                    || (mValueText != null && !value.contains(mValueText)))) {
                continue;
            }

            int color = ViewUtils.getThemeAccentColor(requireActivity());
            String colorCode = "#"
                    + Integer.toHexString(Color.red(color))
                    + Integer.toHexString(Color.green(color))
                    + Integer.toHexString(Color.blue(color));

            DescriptionView descriptionView = new DescriptionView();
            if (mKeyText != null && !mKeyText.isEmpty()) {
                descriptionView.setTitle(Utils.htmlFrom(title.replace(mKeyText,
                        "<b><font color=\"" + colorCode + "\">" + mKeyText + "</font></b>")));
            } else {
                descriptionView.setTitle(title);
            }
            if (mValueText != null && !mValueText.isEmpty()) {
                descriptionView.setSummary(Utils.htmlFrom(value.replace(mValueText,
                        "<b><font color=\"" + colorCode + "\">" + mValueText + "</font></b>")));
            } else {
                descriptionView.setSummary(value);
            }
            descriptionView.setOnItemClickListener(item -> {
                mItemOptionsDialog = new Dialog(requireActivity()).setItems(
                        getResources().getStringArray(R.array.build_prop_item_options),
                        (dialogInterface, i1) -> {
                            switch (i1) {
                                case 0:
                                    modify(title, value);
                                    break;
                                case 1:
                                    delete(title, value);
                                    break;
                            }
                        }).setOnDismissListener(dialogInterface -> mItemOptionsDialog = null);
                mItemOptionsDialog.show();
            });

            items.add(descriptionView);
        }

        Activity activity;
        if (mSearchFragment != null && (activity = getActivity()) != null) {
            activity.runOnUiThread(() -> {
                if (isAdded()) {
                    SearchFragment fragment = (SearchFragment)
                            getViewPagerFragment(0);
                    fragment.setCount(items.size());
                }
            });
        }
    }

    private void modify(final String key, final String value) {
        mKey = key;
        mValue = value;
        ViewUtils.dialogEditTexts(key, value, getString(R.string.key), getString(R.string.value),
                (dialogInterface, i) -> {
                }, (text, text2) -> {
                    if (text.isEmpty()) {
                        Utils.toast(R.string.key_empty, getActivity());
                        return;
                    }

                    if (key != null) {
                        overwrite(key.trim(), value.trim(), text.trim(), text2.trim());
                    } else {
                        add(text.trim(), text2.trim());
                    }
                }, getActivity()).setOnDismissListener(dialogInterface -> {
                    mKey = null;
                    mValue = null;
                }).show();
    }

    private void delete(final String key, final String value) {
        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question), (dialogInterface, i) -> {
        }, (dialogInterface, i) -> overwrite(key.trim(), value.trim(), "#" + key.trim(), value.trim()), dialogInterface -> mDeleteDialog = null, getActivity()).setTitle(key);
        mDeleteDialog.show();
    }

    private void add(String key, String value) {
        Buildprop.addKey(key, value);
        reload(true);
    }

    private void overwrite(String oldKey, String oldValue, String newKey, String newValue) {
        Buildprop.overwrite(oldKey, oldValue, newKey, newValue);
        reload(true);
    }

    @Override
    protected void onBottomFabClick() {
        super.onBottomFabClick();
        mAddDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.build_prop_add_options), (dialog, which) -> {
                    switch (which) {
                        case 0:
                            modify(null, null);
                            break;
                        case 1:
                            Buildprop.backup();
                            Utils.toast(getString(R.string.backup_item, Buildprop.BUILD_PROP,
                                    Utils.getInternalDataStorage()), getActivity(), Toast.LENGTH_LONG);
                            break;
                    }
                }).setOnDismissListener(dialog -> mAddDialog = null);
        mAddDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RootUtils.mount(false, "/system");
        if (mLoader != null) {
            mLoader.cancel(true);
        }
        mKeyText = null;
        mValueText = null;
    }

    public static class SearchFragment extends BaseFragment {

        private TextView mItemsText;
        private int mItemsCount;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            Fragment fragment = getParentFragment();
            if (!(fragment instanceof BuildpropFragment)) {
                assert fragment != null;
                fragment = fragment.getParentFragment();
            }
            final BuildpropFragment buildpropFragment = (BuildpropFragment) fragment;

            View rootView = inflater.inflate(R.layout.fragment_buildprop_search, container, false);

            AppCompatEditText keyEdit = rootView.findViewById(R.id.key_edittext);
            AppCompatEditText valueEdit = rootView.findViewById(R.id.value_edittext);

            keyEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    assert buildpropFragment != null;
                    buildpropFragment.mKeyText = s.toString();
                    buildpropFragment.reload(false);
                }
            });
            valueEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    assert buildpropFragment != null;
                    buildpropFragment.mValueText = s.toString();
                    buildpropFragment.reload(false);
                }
            });

            assert buildpropFragment != null;
            if (buildpropFragment.mKeyText != null) {
                keyEdit.append(buildpropFragment.mKeyText);
            }
            if (buildpropFragment.mKeyText != null) {
                valueEdit.append(buildpropFragment.mKeyText);
            }

            mItemsText = rootView.findViewById(R.id.found_text);
            setCount(mItemsCount);

            return rootView;
        }

        void setCount(int count) {
            mItemsCount = count;
            if (mItemsText != null && isAdded()) {
                mItemsText.setText(getString(count == 1 ? R.string.item_count : R.string.items_count, count));
            }
        }

    }

}
