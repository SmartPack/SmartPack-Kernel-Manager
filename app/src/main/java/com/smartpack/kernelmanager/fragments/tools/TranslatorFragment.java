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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.AsyncTasks;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 01, 2020
 */

public class TranslatorFragment extends RecyclerViewFragment {

    private boolean mPermissionDenied;

    private String mKeyText;

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

        if (!Utils.existFile(getStringPath())) {
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
                    Utils.prepareInternalDataStorage(requireActivity());
                    Utils.create(getStrings(), new File(Environment.getExternalStorageDirectory().toString(), text));
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
        getHandler().postDelayed(() -> {
            clearItems();
            new AsyncTasks() {
                private List<RecyclerViewItem> items;

                @Override
                public void onPreExecute() {
                    showProgress();
                    items = new ArrayList<>();
                }

                @Override
                public void doInBackground() {
                    loadInTo(items);
                }

                @Override
                public void onPostExecute() {
                    for (RecyclerViewItem item : items) {
                        addItem(item);
                    }

                    hideProgress();
                }
            }.execute();
        }, 250);
    }

    private void loadInTo(List<RecyclerViewItem> items) {
        if (!Utils.existFile(getStringPath())) {
            DescriptionView stringEmpty = new DescriptionView();
            stringEmpty.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_language, requireActivity()));
            stringEmpty.setSummary(getString(R.string.reload_message));
            stringEmpty.setFullSpan(true);
            items.add(stringEmpty);
            return;
        }
        try {
            for (String line : Objects.requireNonNull(Utils.readFile(getStringPath())).split("\\r?\\n")) {
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
                        dialogEditText(summary,
                                (dialogInterface, i) -> {
                                }, text -> {
                                    if (text.isEmpty()) {
                                        return;
                                    }
                                    Utils.create(Objects.requireNonNull(Utils.readFile(getStringPath())).replace(finalLine[1], text + "</string>"), new File(getStringPath()));
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

    private String getStrings() {
        List<String> mData = new ArrayList<>();
        if (Utils.existFile(getStringPath())) {
            for (String line : Objects.requireNonNull(Utils.readFile(getStringPath())).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    mData.add(line);
                }
            }
        }
        return "<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n<!--Created by SmartPack-Kernel Manager-->\n\n" +
                mData.toString().replace("[","").replace("]","").replace(",","\n") + "\n</resources>";
    }

    private String getStringPath() {
        return Utils.getInternalDataStorage(requireActivity()) + "/strings.xml";
    }

    private boolean checkIllegalCharacters(String string) {
        String[] array = string.trim().split("\\s+");
        for (String s : array) {
            if (!s.matches("&gt;|&lt;|&amp;") && s.startsWith("&")
                    || s.startsWith("<") && s.length() == 1 || s.startsWith(">") && s.length() == 1
                    || s.startsWith("<b") && s.length() <= 3 || s.startsWith("</") && s.length() <= 4
                    || s.startsWith("<i") && s.length() <= 3 || s.startsWith("\"") || s.startsWith("'"))
                return true;
        }
        return false;
    }

    private Dialog dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                  final ViewUtils.OnDialogEditTextListener onDialogEditTextListener,
                                  Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.START);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (text != null) {
            editText.append(text);
        }
        Snackbar snackBar = Snackbar.make(getRootView(), getString(R.string.illegal_string_warning), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.dismiss, v -> snackBar.dismiss());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (checkIllegalCharacters(Objects.requireNonNull(s.toString()))) {
                    editText.setTextColor(Color.RED);
                    snackBar.show();
                } else {
                    editText.setTextColor(Prefs.getBoolean("darktheme", true, context) ? Color.WHITE : Color.BLACK);
                    snackBar.dismiss();
                }
            }
        });
        layout.addView(editText);

        Dialog dialog = new Dialog(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(context.getString(R.string.ok), (dialog1, which) -> {
                if (checkIllegalCharacters(Objects.requireNonNull(editText.getText()).toString())) {
                    return;
                }
                onDialogEditTextListener.onClick(editText.getText().toString());
            });
            dialog.setOnDismissListener(dialog1 -> {
                if (negativeListener != null) {
                    negativeListener.onClick(dialog1, 0);
                }
            });
        }
        return dialog;
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
                if (Utils.existFile(Utils.getInternalDataStorage(requireActivity()) + "/strings.xml")) {
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.delete_string));
                }
                SubMenu import_string = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.import_string));
                import_string.add(Menu.NONE, 2, Menu.NONE, "Chinese (rTW)");
                import_string.add(Menu.NONE, 3, Menu.NONE, "Chinese (rCN)");
                import_string.add(Menu.NONE, 4, Menu.NONE, "Russian");
                import_string.add(Menu.NONE, 5, Menu.NONE, "Portuguese (rPt)");
                import_string.add(Menu.NONE, 6, Menu.NONE, "Korean");
                import_string.add(Menu.NONE, 7, Menu.NONE, "Ukrainian");
                import_string.add(Menu.NONE, 8, Menu.NONE, "Amharic");
                import_string.add(Menu.NONE, 9, Menu.NONE, "German");
                import_string.add(Menu.NONE, 10, Menu.NONE, "Spanish");
                import_string.add(Menu.NONE, 11, Menu.NONE, "Polish");
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 0) {
                        return false;
                    } else if (item.getItemId() > 1 && Utils.existFile(Utils.getInternalDataStorage(requireActivity()) + "/strings.xml")) {
                        Utils.snackbar(mRootView, getString(R.string.import_string_message));
                        return false;
                    } else if (item.getItemId() == 1) {
                        new Dialog(requireActivity())
                                .setMessage(getString(R.string.delete_string_message))
                                .setNegativeButton(getString(R.string.cancel), (dialogInterface, iv) -> {
                                })
                                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                    Utils.delete(Utils.getInternalDataStorage(requireActivity()) + "/strings.xml");
                                    assert translatorFragment != null;
                                    translatorFragment.reload();
                                })
                                .show();
                    } else {
                        new AsyncTasks() {
                            private String url = null;

                            @Override
                            public void onPreExecute() {
                                assert translatorFragment != null;
                                translatorFragment.showProgressMessage(getString(R.string.importing_string) + ("..."));
                                translatorFragment.showProgress();
                                if (item.getItemId() == 2) {
                                    url = "values-zh-rTW";
                                } else if (item.getItemId() == 3) {
                                    url = "values-zh-rCN";
                                } else if (item.getItemId() == 4) {
                                    url = "values-ru";
                                } else if (item.getItemId() == 5) {
                                    url = "values-pt-rBR";
                                } else if (item.getItemId() == 6) {
                                    url = "values-ko";
                                } else if (item.getItemId() == 7) {
                                    url = "values-uk";
                                } else if (item.getItemId() == 8) {
                                    url = "values-am";
                                } else if (item.getItemId() == 9) {
                                    url = "values-de-rDE";
                                } else if (item.getItemId() == 10) {
                                    url = "values-es";
                                } else if (item.getItemId() == 11) {
                                    url = "values-pl";
                                }
                            }

                            @Override
                            public void doInBackground() {
                                if (url != null) {
                                    Utils.importTranslation(url, getActivity());
                                }
                            }

                            @Override
                            public void onPostExecute() {
                                assert translatorFragment != null;
                                translatorFragment.hideProgress();
                                translatorFragment.reload();
                            }
                        }.execute();
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
        mKeyText = null;
    }

}