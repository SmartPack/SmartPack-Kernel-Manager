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
package com.grarak.kerneladiutor.fragments.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Buildprop;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

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
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add));
        DrawableCompat.setTint(drawable, Color.WHITE);
        return drawable;
    }

    @Override
    protected boolean showBottomFab() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(mSearchFragment = SearchFragment.newInstance(mKeyText, mValueText,
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mKeyText = s.toString();
                        reload(false);
                    }
                }, new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mValueText = s.toString();
                        reload(false);
                    }
                }));

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
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            if (read) {
                                mProps = Buildprop.getProps();
                            }
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
                            mLoader = null;
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private void load(final List<RecyclerViewItem> items) {
        if (mProps == null) return;
        String[] titles = mProps.keySet().toArray(new String[mProps.size()]);
        for (int i = 0; i < mProps.size(); i++) {
            final String title = titles[i];
            final String value = mProps.values().toArray(new String[mProps.size()])[i];
            if ((mKeyText != null && !title.contains(mKeyText)
                    || (mValueText != null && !value.contains(mValueText)))) {
                continue;
            }

            int color = ViewUtils.getThemeAccentColor(getActivity());
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
                        "<b><font color=\"" + colorCode + "\">" + mKeyText + "</font></b>")));
            } else {
                descriptionView.setSummary(value);
            }
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    mItemOptionsDialog = new Dialog(getActivity()).setItems(
                            getResources().getStringArray(R.array.build_prop_item_options),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            modify(title, value);
                                            break;
                                        case 1:
                                            delete(title, value);
                                            break;
                                    }
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            mItemOptionsDialog = null;
                        }
                    });
                    mItemOptionsDialog.show();
                }
            });

            items.add(descriptionView);
        }

        Activity activity;
        if (mSearchFragment != null && (activity = getActivity()) != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        mSearchFragment.setCount(items.size());
                    }
                }
            });
        }
    }

    private void modify(final String key, final String value) {
        mKey = key;
        mValue = value;
        ViewUtils.dialogEditTexts(key, value, getString(R.string.key), getString(R.string.value),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }, new ViewUtils.onDialogEditTextsListener() {
                    @Override
                    public void onClick(String text, String text2) {
                        if (text.isEmpty()) {
                            Utils.toast(R.string.key_empty, getActivity());
                            return;
                        }

                        if (key != null) {
                            overwrite(key.trim(), value.trim(), text.trim(), text2.trim());
                        } else {
                            add(text.trim(), text2.trim());
                        }
                    }
                }, getActivity()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mKey = null;
                mValue = null;
            }
        }).show();
    }

    private void delete(final String key, final String value) {
        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                overwrite(key.trim(), value.trim(), "#" + key.trim(), value.trim());
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mDeleteDialog = null;
            }
        }, getActivity()).setTitle(key);
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
        mAddDialog = new Dialog(getActivity()).setItems(getResources().getStringArray(
                R.array.build_prop_add_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mAddDialog = null;
            }
        });
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

        public static SearchFragment newInstance(String keyText, String valueText, TextWatcher keyWatcher,
                                                 TextWatcher valueWatcher) {
            SearchFragment fragment = new SearchFragment();
            fragment.mKeyText = keyText;
            fragment.mValueText = valueText;
            fragment.mKeyWatcher = keyWatcher;
            fragment.mValueWatcher = valueWatcher;
            return fragment;
        }

        private TextView mItemsText;
        private int mItemsCount;
        private String mKeyText;
        private String mValueText;
        private TextWatcher mKeyWatcher;
        private TextWatcher mValueWatcher;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_buildprop_search, container, false);

            AppCompatEditText keyEdit = (AppCompatEditText) rootView.findViewById(R.id.key_edittext);
            AppCompatEditText valueEdit = (AppCompatEditText) rootView.findViewById(R.id.value_edittext);

            keyEdit.addTextChangedListener(mKeyWatcher);
            valueEdit.addTextChangedListener(mValueWatcher);

            if (mKeyText != null) {
                keyEdit.append(mKeyText);
            }
            if (mValueText != null) {
                valueEdit.append(mValueText);
            }

            mItemsText = (TextView) rootView.findViewById(R.id.found_text);
            setCount(mItemsCount);

            return rootView;
        }

        public void setCount(int count) {
            mItemsCount = count;
            if (mItemsText != null && isAdded()) {
                mItemsText.setText(getString(count == 1 ? R.string.item_count : R.string.items_count, count));
            }
        }

    }

}
