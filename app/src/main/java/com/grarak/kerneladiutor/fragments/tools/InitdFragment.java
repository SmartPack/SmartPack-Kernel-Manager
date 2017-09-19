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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.EditorActivity;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.fragments.SwitcherFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Initd;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 16.07.16.
 */
public class InitdFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;
    private boolean mLoaded;

    private Dialog mExecuteDialog;
    private Dialog mResultDialog;
    private Dialog mDeleteDialog;
    private boolean mShowCreateNameDialog;

    private String mCreateName;

    private String mEditInitd;

    @Override
    protected Drawable getTopFabDrawable() {
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add));
        DrawableCompat.setTint(drawable, Color.WHITE);
        return drawable;
    }

    @Override
    protected boolean showTopFab() {
        return true;
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(SwitcherFragment.newInstance(
                getString(R.string.emulate_initd),
                getString(R.string.emulate_initd_summary),
                Prefs.getBoolean("initd_onboot", false, getActivity()),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Prefs.saveBoolean("initd_onboot", b, getActivity());
                    }
                }));

        if (mExecuteDialog != null) {
            mExecuteDialog.show();
        }
        if (mResultDialog != null) {
            mResultDialog.show();
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
            load(items);
            mLoaded = true;
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
        for (final String initd : Initd.list()) {
            CardView cardView = new CardView(getActivity());
            cardView.setOnMenuListener(new CardView.OnMenuListener() {
                @Override
                public void onMenuReady(CardView cardView, PopupMenu popupMenu) {
                    Menu menu = popupMenu.getMenu();
                    menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.edit));
                    menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.delete));

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 0:
                                    mEditInitd = initd;
                                    Intent intent = new Intent(getActivity(), EditorActivity.class);
                                    intent.putExtra(EditorActivity.TITLE_INTENT, initd);
                                    intent.putExtra(EditorActivity.TEXT_INTENT, Initd.read(initd));
                                    startActivityForResult(intent, 0);
                                    break;
                                case 1:
                                    mDeleteDialog = ViewUtils.dialogBuilder(getString(R.string.sure_question),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            }, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Initd.delete(initd);
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
            descriptionView.setSummary(initd);
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    mExecuteDialog = ViewUtils.dialogBuilder(getString(R.string.exceute_question, initd),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    execute(initd);
                                }
                            }, new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    mExecuteDialog = null;
                                }
                            }, getActivity());
                    mExecuteDialog.show();
                }
            });

            cardView.addItem(descriptionView);
            items.add(cardView);
        }
    }

    private void execute(final String initd) {
        new AsyncTask<Void, Void, String>() {

            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.executing));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return Initd.execute(initd);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (s != null && !s.isEmpty()) {
                    mResultDialog = ViewUtils.dialogBuilder(s, null, null,
                            new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    mResultDialog = null;
                                }
                            }, getActivity()).setTitle(getString(R.string.result));
                    mResultDialog.show();
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        if (requestCode == 0) {
            Initd.write(mEditInitd, data.getCharSequenceExtra(EditorActivity.TEXT_INTENT).toString());
            reload();
        } else if (requestCode == 1) {
            Initd.write(mCreateName, data.getCharSequenceExtra(EditorActivity.TEXT_INTENT).toString());
            mCreateName = null;
            reload();
        }
    }

    @Override
    protected void onTopFabClick() {
        super.onTopFabClick();

        showCreateDialog();
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

                if (Initd.list().indexOf(text) > -1) {
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
        RootUtils.mount(false, "/system");
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
        mLoaded = false;
    }

    @Override
    protected boolean showAd() {
        return true;
    }

}
