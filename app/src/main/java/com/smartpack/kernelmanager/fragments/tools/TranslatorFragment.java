/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
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

package com.smartpack.kernelmanager.fragments.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 01, 2020
 */

public class TranslatorFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private boolean mPermissionDenied;

    private String mKeyText;
    private String mStringPath = Utils.getInternalDataStorage() + "/strings.xml";

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new SearchFragment());
    }

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    @Override
    protected Drawable getBottomFabDrawable() {
        return ViewUtils.getColoredIcon(R.drawable.ic_save, requireActivity());
    }

    @Override
    protected boolean showBottomFab() {
        return true;
    }

    @Override
    protected void onBottomFabClick() {
        super.onBottomFabClick();

        if (mPermissionDenied) {
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
            return;
        }

        if (!Utils.existFile(mStringPath)) {
            Utils.snackbar(getRootView(), getString(R.string.save_string_error));
            return;
        }

        ViewUtils.dialogEditText("string",
                (dialogInterface, i) -> {},
                text -> {
                    if (text.isEmpty()) {
                        Utils.snackbar(getRootView(), getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".xml")) {
                        text += ".xml";
                    }
                    Utils.prepareInternalDataStorage();
                    Utils.create("<!--Created by SmartPack-Kernel Manager-->\n" + Utils.readFile(mStringPath), Environment.getExternalStorageDirectory().toString() + "/" + text);
                    Utils.snackbar(getRootView(), getString(R.string.save_string_message, Environment.getExternalStorageDirectory().toString() + "/" + text));
                }, getActivity()).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
        requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            loadInTo(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);

                            if (isAdded()) {
                                clearItems();
                                for (RecyclerViewItem item : recyclerViewItems) {
                                    addItem(item);
                                }

                                hideProgress();
                                mLoader = null;
                            }
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private void loadInTo(List<RecyclerViewItem> items) {
        if (!Utils.existFile(mStringPath)) {
            DescriptionView stringEmpty = new DescriptionView();
            stringEmpty.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_language, requireActivity()));
            stringEmpty.setSummary(getString(R.string.reload_message));
            stringEmpty.setFullSpan(true);
            items.add(stringEmpty);
            return;
        }
        try {
            for (String line : Objects.requireNonNull(Utils.readFile(mStringPath)).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");

                    String summary = finalLine[1].replace("</string>","");
                    if ((mKeyText != null && !summary.toLowerCase().contains(mKeyText))) {
                        continue;
                    }

                    DescriptionView strings = new DescriptionView();
                    strings.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_arrow, requireContext()));
                    strings.setTitle(finalLine[0].replace("<string name=\"","").replace(" ",""));
                    if (mKeyText != null && !mKeyText.isEmpty()) {
                        strings.setSummary(Utils.htmlFrom(summary.replace(mKeyText,
                                "<b><font color=\"" + "#" + Integer.toHexString(Color.blue(Color.BLUE)) + "\">" + mKeyText + "</font></b>")));
                    } else {
                        strings.setSummary(summary);
                    }

                    strings.setFullSpan(true);
                    strings.setOnItemClickListener(item -> {
                        if (summary.contains("%s") || summary.contains("\n") || summary.contains("%d") ||
                                summary.contains("&amp;") || summary.contains("b>") || summary.contains("i>")) {
                            Utils.snackbar(getRootView(), getString(R.string.edit_string_warning));
                        }
                        ViewUtils.dialogEditText(summary,
                                (dialogInterface, i) -> {
                                }, text -> {
                                    if (text.isEmpty()) {
                                        return;
                                    }
                                    Utils.create(Objects.requireNonNull(Utils.readFile(mStringPath)).replace(finalLine[1], text + "</string>"), mStringPath);
                                    strings.setSummary(text);
                                }, getActivity()).setOnDismissListener(dialogInterface -> {
                        }).show();
                    });

                    items.add(strings);
                }
            }
        } catch (Exception ignored) {
        }

    }

    @Override
    public void onPermissionDenied(int request) {
        super.onPermissionDenied(request);
        if (request == 0) {
            mPermissionDenied = true;
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
        }
    }

    public static class SearchFragment extends BaseFragment {

        private View mRootView;

        @SuppressLint("StaticFieldLeak")
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Fragment fragment = getParentFragment();
            if (!(fragment instanceof TranslatorFragment)) {
                assert fragment != null;
                fragment = fragment.getParentFragment();
            }
            final TranslatorFragment translatorFragment = (TranslatorFragment) fragment;

            mRootView = inflater.inflate(R.layout.fragment_strings_search, container, false);

            AppCompatImageButton settings = mRootView.findViewById(R.id.settings_icon);
            settings.setImageDrawable(ViewUtils.getWhiteColoredIcon(R.drawable.ic_settings, requireActivity()));
            settings.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(requireActivity(), settings);
                Menu menu = popupMenu.getMenu();
                if (Utils.existFile(Utils.getInternalDataStorage() + "/strings.xml")) {
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.delete_string));
                }
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.import_string));
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 0) {
                        new Dialog(requireActivity())
                                .setMessage(getString(R.string.delete_string_message))
                                .setNegativeButton(getString(R.string.cancel), (dialogInterface, iv) -> {
                                })
                                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                    Utils.delete(Utils.getInternalDataStorage() + "/strings.xml");
                                    assert translatorFragment != null;
                                    translatorFragment.reload();
                                })
                                .show();
                    } else if (item.getItemId() == 1) {
                        if (Utils.existFile(Utils.getInternalDataStorage() + "/strings.xml")) {
                            Utils.snackbar(mRootView, getString(R.string.import_string_message));
                        } else if (!Utils.isNetworkAvailable(requireActivity())) {
                            Utils.snackbar(mRootView, getString(R.string.no_internet));
                        } else {
                            ViewUtils.dialogEditText(getString(R.string.import_string_hint),
                                    (dialogInterface, i) -> {
                                    }, text -> {
                                        if (text.isEmpty()) {
                                            return;
                                        }
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected void onPreExecute() {
                                                super.onPreExecute();
                                                assert translatorFragment != null;
                                                translatorFragment.showProgressMessage(getString(R.string.importing_string) + ("..."));
                                            }

                                            @Override
                                            protected Void doInBackground(Void... voids) {
                                                Utils.downloadFile(Utils.getInternalDataStorage() + "/strings.xml", text
                                                        .replace("/blob/", "/raw/"), getActivity());
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void aVoid) {
                                                super.onPostExecute(aVoid);
                                                assert translatorFragment != null;
                                                translatorFragment.hideProgressMessage();
                                                translatorFragment.reload();
                                            }
                                        }.execute();
                                    }, getActivity()).setOnDismissListener(dialogInterface -> {
                            }).show();
                        }
                    }
                    return false;
                });
                popupMenu.show();
            });

            AppCompatEditText keyEdit = mRootView.findViewById(R.id.searchText);
            keyEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    assert translatorFragment != null;
                    translatorFragment.mKeyText = s.toString().toLowerCase();
                    translatorFragment.reload();
                }
            });

            assert translatorFragment != null;
            if (translatorFragment.mKeyText != null) {
                keyEdit.append(translatorFragment.mKeyText);
            }

            return mRootView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
        mKeyText = null;
    }

}