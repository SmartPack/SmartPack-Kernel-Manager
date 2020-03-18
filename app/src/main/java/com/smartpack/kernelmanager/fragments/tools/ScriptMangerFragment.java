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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.appcompat.widget.PopupMenu;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.EditorActivity;
import com.smartpack.kernelmanager.fragments.DescriptionFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.fragments.SwitcherFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.tools.ScriptManager;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected Drawable getTopFabDrawable() {
        return getResources().getDrawable(R.drawable.ic_add);
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
                Prefs.getBoolean("scripts_onboot", false, getActivity()),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Prefs.saveBoolean("scripts_onboot", b, getActivity());
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
        if (!mLoaded) {
            mLoaded = true;
            requestPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void reload() {
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
                            List<RecyclerViewItem> items = new ArrayList<>();
                            load(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);
                            for (RecyclerViewItem item : recyclerViewItems) {
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

    private void load(List<RecyclerViewItem> items) {
        for (final String script : ScriptManager.list()) {
            if (Utils.getExtension(script).equals("sh")) {
                CardView cardView = new CardView(getActivity());
                cardView.setOnMenuListener(new CardView.OnMenuListener() {
                    @Override
                    public void onMenuReady(CardView cardView, PopupMenu popupMenu) {
                        Menu menu = popupMenu.getMenu();
                        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.execute));
                        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.edit));
                        menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.share));
                        menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.delete));

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case 0:
                                        mExecuteDialog = ViewUtils.dialogBuilder(getString(R.string.exceute_question, script),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                }, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        execute(script);
                                                    }
                                                }, new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        mExecuteDialog = null;
                                                    }
                                                }, getActivity());
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
                                        Utils.shareItem(getActivity(), script, ScriptManager.scriptFile() + "/" + script, getString(R.string.share_script) + "\n\n" +
                                                getString(R.string.share_app_message, "v" + BuildConfig.VERSION_NAME));
                                        break;
                                    case 3:
                                        mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                }, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        ScriptManager.delete(script);
                                                        reload();
                                                    }
                                                }, new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        mDeleteDialog = null;
                                                    }
                                                }, getActivity());
                                        mDeleteDialog.show();
                                        break;
                                }
                                return false;
                            }
                        });
                    }
                });

                DescriptionView descriptionView = new DescriptionView();
                descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_file));
                descriptionView.setSummary(script);

                cardView.addItem(descriptionView);
                items.add(cardView);
            }
        }
    }

    private void execute(final String script) {
        new AsyncTask<Void, Void, String>() {

            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.executing) + " " + script + "...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return ScriptManager.execute(script);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (s != null && !s.isEmpty()) {
                    new Dialog(getActivity())
                            .setMessage(s)
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.cancel), (dialog, id) -> {
                            })
                            .show();
                }
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
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 0) {
            ScriptManager.write(mEditScript, data.getCharSequenceExtra(EditorActivity.TEXT_INTENT).toString());
            reload();
        } else if (requestCode == 1) {
            ScriptManager.write(mCreateName, data.getCharSequenceExtra(EditorActivity.TEXT_INTENT).toString());
            mCreateName = null;
            reload();
        } else if (requestCode == 2) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());
            if (Utils.isDocumentsUI(uri)) {
                Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getFilePath(file);
            }
            if (!Utils.getExtension(mPath).equals("sh")) {
                Utils.toast(getString(R.string.wrong_extension, ".sh"), getActivity());
                return;
            }
            if (Utils.existFile(ScriptManager.scriptExistsCheck(file.getName()))) {
                Utils.toast(getString(R.string.script_exists, file.getName()), getActivity());
                return;
            }
            Dialog selectQuestion = new Dialog(getActivity());
            selectQuestion.setMessage(getString(R.string.select_question, file.getName().replace("primary:", "")
                    .replace("file%3A%2F%2F%2F", "").replace("%2F", "/")));
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
            Utils.toast(R.string.permission_denied_write_storage, getActivity());
            return;
        }

        mOptionsDialog = new Dialog(getActivity()).setItems(getResources().getStringArray(
                R.array.scripts_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mOptionsDialog = null;
            }
        });
        mOptionsDialog.show();
    }

    private void showCreateDialog() {
        mShowCreateNameDialog = true;
        ViewUtils.dialogEditText(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }, new ViewUtils.OnDialogEditTextListener() {
            @Override
            public void onClick(String text) {
                if (text.isEmpty()) {
                    Utils.toast(R.string.name_empty, getActivity());
                    return;
                }

                if (!text.endsWith(".sh")) {
                    text += ".sh";
                }

                if (text.contains(" ")) {
                    text = text.replace(" ", "_");
                }

                if (ScriptManager.list().indexOf(text) > -1) {
                    Utils.toast(getString(R.string.already_exists, text), getActivity());
                    return;
                }

                mCreateName = text;
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                intent.putExtra(EditorActivity.TITLE_INTENT, mCreateName);
                intent.putExtra(EditorActivity.TEXT_INTENT, "#!/system/bin/sh\n\n");
                startActivityForResult(intent, 1);
            }
        }, getActivity()).setTitle(getString(R.string.name)).setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mShowCreateNameDialog = false;
                    }
                }).show();
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
