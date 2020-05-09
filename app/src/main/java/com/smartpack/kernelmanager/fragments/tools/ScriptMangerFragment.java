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
 */

package com.smartpack.kernelmanager.fragments.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.Menu;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.EditorActivity;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.fragments.SwitcherFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.ScriptManager;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 18, 2019
 * Largely based on the InitdFragment.java from https://github.com/Grarak/KernelAdiutor
 * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/fragments/tools/InitdFragment.java
 */

public class ScriptMangerFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private boolean mLoaded;
    private boolean mPermissionDenied;

    private Dialog mExecuteDialog;
    private Dialog mOptionsDialog;
    private Dialog mDeleteDialog;
    private boolean mShowCreateNameDialog;

    private String mCreateName;

    private String mEditScript;
    private String mPath;

    private DetailsFragment mDetailsFragment;

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getWhiteColoredIcon(R.drawable.ic_add, requireActivity());
    }

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected BaseFragment getForegroundFragment() {
        return mDetailsFragment = new DetailsFragment();
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.script_manger),
                getString(R.string.scripts_manager_summary)));

        addViewPagerFragment(SwitcherFragment.newInstance(
                getString(R.string.apply_on_boot),
                getString(R.string.scripts_onboot_summary),
                Prefs.getBoolean("scripts_onboot", false, getActivity()),
                (compoundButton, b) -> Prefs.saveBoolean("scripts_onboot", b, getActivity()))
        );

        if (mExecuteDialog != null) {
            mExecuteDialog.show();
        }
        if (mOptionsDialog != null) {
            mOptionsDialog.show();
        }
        if (mDeleteDialog != null) {
            mDeleteDialog.show();
        }
        if (mShowCreateNameDialog) {
            showCreateDialog();
        }
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (!mLoaded) {
            mLoaded = true;
            requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
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
                            List<RecyclerViewItem> items = new ArrayList<>();
                            load(items);
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

    private void load(List<RecyclerViewItem> items) {
        final Set<String> onBootScripts = Prefs.getStringSet("on_boot_scripts", new HashSet<>(), requireContext());
        for (final String script : ScriptManager.list()) {
            if (Utils.getExtension(script).equals("sh")) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_file, requireContext()));
                descriptionView.setMenuIcon(getResources().getDrawable(R.drawable.ic_dots));
                descriptionView.setTitle(script);
                descriptionView.setSummary(ScriptManager.scriptFile() + "/" + script);

                if (onBootScripts.contains(script)) {
                    descriptionView.setIndicator(getResources().getDrawable(R.drawable.ic_flash));
                }

                descriptionView.setOnMenuListener((descriptionView1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.execute));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.edit));
                    menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.details));
                    menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.apply_on_boot))
                            .setCheckable(true).setChecked(onBootScripts.contains(script));
                    menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.share));
                    menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.delete));
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case 0:
                                mExecuteDialog = ViewUtils.dialogBuilder(getString(R.string.exceute_question, script),
                                        (dialogInterface, i) -> {
                                        }, (dialogInterface, i) -> execute(script), dialogInterface -> mExecuteDialog = null, getActivity());
                                mExecuteDialog.show();
                                break;
                            case 1:
                                mEditScript = script;
                                Intent intent = new Intent(getActivity(), EditorActivity.class);
                                intent.putExtra(EditorActivity.TITLE_INTENT, script);
                                intent.putExtra(EditorActivity.TEXT_INTENT, ScriptManager.read(script));
                                startActivityForResult(intent, 0);
                                break;
                            case 2:
                                setForegroundText(script);
                                mDetailsFragment.setText(ScriptManager.read(script));
                                showForeground();
                                break;
                            case 3:
                                if (onBootScripts.contains(script)) {
                                    onBootScripts.remove(script);
                                } else {
                                    onBootScripts.add(script);
                                }
                                Prefs.saveStringSet("on_boot_scripts", onBootScripts, requireContext());
                                reload();
                                break;
                            case 4:
                                Utils.shareItem(getActivity(), script, ScriptManager.scriptFile() + "/" + script, getString(R.string.share_script) + "\n\n" +
                                        getString(R.string.share_app_message, "v" + BuildConfig.VERSION_NAME));
                                break;
                            case 5:
                                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                        (dialogInterface, i) -> {
                                        }, (dialogInterface, i) -> {
                                            ScriptManager.delete(script);
                                            reload();
                                        }, dialogInterface -> mDeleteDialog = null, getActivity());
                                mDeleteDialog.show();
                                break;
                        }
                        return false;
                    });
                });

                items.add(descriptionView);
            }
        }
        if (items.size() == 0) {
            if (!Utils.isPackageInstalled("com.smartpack.scriptmanager", requireActivity())) {
                // Advertise Own App
                DescriptionView sm = new DescriptionView();
                sm.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
                sm.setSummary(getString(R.string.scripts_manager_message));
                sm.setFullSpan(true);
                sm.setOnItemClickListener(item -> {
                    Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.scriptmanager", getActivity());
                });
                items.add(sm);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void execute(final String script) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            private String mResult;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.executing) + " " + script + "...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                mResult = ScriptManager.execute(script);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                new Dialog(requireActivity())
                        .setMessage(mResult)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                        })
                        .show();
            }
        }.execute();
    }

    @Override
    public void onPermissionGranted(int request) {
        super.onPermissionGranted(request);
        if (request == 0) {
            mPermissionDenied = false;
            reload();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 0) {
            ScriptManager.write(mEditScript, Objects.requireNonNull(data.getCharSequenceExtra(EditorActivity.TEXT_INTENT)).toString());
            reload();
        } else if (requestCode == 1) {
            ScriptManager.write(mCreateName, Objects.requireNonNull(data.getCharSequenceExtra(EditorActivity.TEXT_INTENT)).toString());
            mCreateName = null;
            reload();
        } else if (requestCode == 2) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getFilePath(file);
            }
            if (!Utils.getExtension(mPath).equals("sh")) {
                Utils.snackbar(getRootView(), getString(R.string.wrong_extension, ".sh"));
                return;
            }
            if (Utils.existFile(ScriptManager.scriptExistsCheck(file.getName()))) {
                Utils.snackbar(getRootView(), getString(R.string.script_exists, file.getName()));
                return;
            }
            if (mPath.contains("(") || mPath.contains(")")) {
                Utils.snackbar(getRootView(), getString(R.string.file_name_error));
            }
            Dialog selectQuestion = new Dialog(requireActivity());
            selectQuestion.setMessage(getString(R.string.select_question, new File(mPath).getName()));
            selectQuestion.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            selectQuestion.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                ScriptManager.importScript(mPath);
                reload();
            });
            selectQuestion.show();
        }
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        if (mPermissionDenied) {
            Utils.snackbar(getRootView(), getString(R.string.permission_denied_write_storage));
            return;
        }

        mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.scripts_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            showCreateDialog();
                            break;
                        case 1:
                            Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, 2);
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> mOptionsDialog = null);
        mOptionsDialog.show();
    }

    private void showCreateDialog() {
        mShowCreateNameDialog = true;
        ViewUtils.dialogEditText(null, (dialogInterface, i) -> {
        }, text -> {
            if (text.isEmpty()) {
                Utils.snackbar(getRootView(), getString(R.string.name_empty));
                return;
            }

            if (!text.endsWith(".sh")) {
                text += ".sh";
            }

            if (text.contains(" ")) {
                text = text.replace(" ", "_");
            }

            if (ScriptManager.list().indexOf(text) > -1) {
                Utils.snackbar(getRootView(), getString(R.string.already_exists, text));
                return;
            }

            mCreateName = text;
            Intent intent = new Intent(getActivity(), EditorActivity.class);
            intent.putExtra(EditorActivity.TITLE_INTENT, mCreateName);
            intent.putExtra(EditorActivity.TEXT_INTENT, "#!/system/bin/sh\n\n");
            startActivityForResult(intent, 1);
        }, getActivity()).setTitle(getString(R.string.name)).setOnDismissListener(
                dialogInterface -> mShowCreateNameDialog = false).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
        mLoaded = false;
    }
}