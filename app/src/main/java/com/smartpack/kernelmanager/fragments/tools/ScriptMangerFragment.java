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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Menu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.ApplyScriptActivity;
import com.smartpack.kernelmanager.activities.ForegroundActivity;
import com.smartpack.kernelmanager.activities.ScriptEditorActivity;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.fragments.SwitcherFragment;
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.Scripts;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 18, 2019
 * Largely based on the InitdFragment.java from https://github.com/Grarak/KernelAdiutor
 * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/fragments/tools/InitdFragment.java
 */

public class ScriptMangerFragment extends RecyclerViewFragment {

    private boolean mShowCreateNameDialog;

    private Dialog mDeleteDialog, mExecuteDialog, mOptionsDialog;

    private String mCreateName;

    @Override
    protected Drawable getTopFabDrawable() {
        return ViewUtils.getColoredIcon(R.drawable.ic_add, requireActivity());
    }

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.script_manger),
                getString(R.string.scripts_manager_summary)));

        addViewPagerFragment(SwitcherFragment.newInstance(
                getString(R.string.apply_on_boot),
                getString(R.string.scripts_onboot_summary),
                Prefs.getBoolean("enable_onboot", true, getActivity()) &&
                        Prefs.getBoolean("scripts_onboot", false, getActivity()),
                (compoundButton, b) -> {
                    if (Prefs.getBoolean("enable_onboot", true, getActivity())) {
                        Prefs.saveBoolean("scripts_onboot", b, getActivity());
                    } else {
                        Utils.snackbar(getRootView(), getString(R.string.enable_onboot_message));
                    }
                })
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
        reload();
    }

    private void reload() {
        getHandler().postDelayed(() -> {
            clearItems();
            new sExecutor() {
                private List<RecyclerViewItem> items;

                @Override
                public void onPreExecute() {
                    showProgress();
                    items = new ArrayList<>();
                }

                @Override
                public void doInBackground() {
                    load(items);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void load(List<RecyclerViewItem> items) {
        final Set<String> onBootScripts = Prefs.getStringSet("on_boot_scripts", new HashSet<>(), requireContext());
        for (final String script : Scripts.list()) {
            if (Utils.getExtension(script).equals("sh")) {
                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_file, requireContext()));
                descriptionView.setMenuIcon(ViewUtils.getWhiteColoredIcon(R.drawable.ic_dots, requireActivity()));
                descriptionView.setTitle(script);
                descriptionView.setSummary(Scripts.scriptFile() + "/" + script);

                if (Prefs.getBoolean("enable_onboot", true, getActivity()) && onBootScripts.contains(script)) {
                    descriptionView.setIndicator(ViewUtils.getColoredIcon(R.drawable.ic_flash, requireActivity()));
                }

                descriptionView.setOnMenuListener((descriptionView1, popupMenu) -> {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.execute));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.edit));
                    menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.details));
                    if (Prefs.getBoolean("enable_onboot", true, getActivity())) {
                        menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.on_boot))
                                .setCheckable(true).setChecked(onBootScripts.contains(script));
                    }
                    menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.quick_tile)).setCheckable(true)
                            .setChecked(Prefs.getString("apply_tile", null, getActivity()) != null &&
                                    Prefs.getString("apply_tile", null, getActivity()).equals(script));
                    menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.share));
                    menu.add(Menu.NONE, 6, Menu.NONE, getString(R.string.delete));
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case 0:
                                mExecuteDialog = ViewUtils.dialogBuilder(getString(R.string.exceute_question, script),
                                        (dialogInterface, i) -> {
                                        }, (dialogInterface, i) -> execute(script), dialogInterface -> mExecuteDialog = null, getActivity());
                                mExecuteDialog.show();
                                break;
                            case 1:
                                Intent intent = new Intent(getActivity(), ScriptEditorActivity.class);
                                intent.putExtra(ScriptEditorActivity.TITLE_INTENT, script);
                                intent.putExtra(ScriptEditorActivity.TEXT_INTENT, Scripts.read(script));
                                startActivity(intent);
                                break;
                            case 2:
                                Common.setDetailsTitle(script.replace(".sh","").toUpperCase());
                                Common.setDetailsTxt(Scripts.read(script));
                                Intent details = new Intent(getActivity(), ForegroundActivity.class);
                                startActivity(details);
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
                                if (Prefs.getString("apply_tile", null, getActivity()) != null &&
                                        Prefs.getString("apply_tile", null, getActivity()).equals(script)) {
                                    Prefs.saveString("apply_tile", null, getActivity());
                                } else {
                                    Prefs.saveString("apply_tile", script, getActivity());
                                }
                                reload();
                                break;
                            case 5:
                                Utils.shareItem(getActivity(), script, Scripts.scriptFile() + "/" + script, getString(R.string.share_script)
                                        + "\n\n" + getString(R.string.share_app_message, BuildConfig.VERSION_NAME));
                                break;
                            case 6:
                                mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                        (dialogInterface, i) -> {
                                        }, (dialogInterface, i) -> {
                                            Scripts.delete(script);
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
    }

    @SuppressLint("StaticFieldLeak")
    private void execute(final String script) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                Scripts.mApplying = true;
                Scripts.mScriptName = script;
                Scripts.mOutput = new ArrayList<>();
                Intent applyIntent = new Intent(requireActivity(), ApplyScriptActivity.class);
                startActivity(applyIntent);
            }

            @Override
            public void doInBackground() {
                Scripts.execute(script);
            }

            @Override
            public void onPostExecute() {
                Scripts.mApplying = false;
            }
        }.execute();
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        mOptionsDialog = new Dialog(requireActivity()).setItems(getResources().getStringArray(
                R.array.scripts_options), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            showCreateDialog();
                            break;
                        case 1:
                            FilePicker filePicker = new FilePicker(importScript, requireActivity());
                            filePicker.setExtension("sh");
                            filePicker.setPath(Environment.getExternalStorageDirectory().toString());
                            filePicker.setAccentColor(ViewUtils.getThemeAccentColor(requireContext()));
                            filePicker.launch();
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

            if (Utils.existFile(Scripts.scriptExistsCheck(text))) {
                Utils.snackbar(getRootView(), getString(R.string.already_exists, text));
                return;
            }

            mCreateName = text;
            Intent intent = new Intent(getActivity(), ScriptEditorActivity.class);
            intent.putExtra(ScriptEditorActivity.TITLE_INTENT, mCreateName);
            intent.putExtra(ScriptEditorActivity.TEXT_INTENT, "#!/system/bin/sh\n\n");
            createScript.launch(intent);
        }, getActivity()).setTitle(getString(R.string.name)).setOnDismissListener(
                dialogInterface -> mShowCreateNameDialog = false).show();
    }

    ActivityResultLauncher<Intent> createScript = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Scripts.write(mCreateName, Objects.requireNonNull(result.getData().getCharSequenceExtra(ScriptEditorActivity.TEXT_INTENT)).toString());
                    mCreateName = null;
                    reload();
                }
            }
    );

    ActivityResultLauncher<Intent> importScript = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && FilePicker.getSelectedFile().exists()) {
                    File mSelectedFile = FilePicker.getSelectedFile();
                    if (!Utils.getExtension(mSelectedFile.getAbsolutePath()).equals("sh")) {
                        Utils.snackbar(getRootView(), getString(R.string.wrong_extension, ".sh"));
                        return;
                    }
                    if (Utils.existFile(Scripts.scriptExistsCheck(mSelectedFile.getName()))) {
                        Utils.snackbar(getRootView(), getString(R.string.script_exists, mSelectedFile.getName()));
                        return;
                    }
                    Dialog selectQuestion = new Dialog(requireActivity());
                    selectQuestion.setMessage(getString(R.string.select_question, mSelectedFile.getName()));
                    selectQuestion.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    });
                    selectQuestion.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        Scripts.importScript(mSelectedFile);
                        reload();
                    });
                    selectQuestion.show();
                }
            }
    );

}