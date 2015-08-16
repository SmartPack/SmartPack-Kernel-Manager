/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor.fragments.tools;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.EditTextActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.InformationCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.Initd;
import com.kerneladiutor.library.root.RootFile;
import com.kerneladiutor.library.root.RootUtils;
import com.nineoldandroids.view.ViewHelper;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;

/**
 * Created by willi on 25.04.15.
 */
public class InitdFragment extends RecyclerViewFragment {

    private View addButtonBg;
    private AddFloatingActionButton addButton;
    private FabHideScrollListener fabHideScrollListener;

    @Override
    public RecyclerView getRecyclerView() {
        View view = getParentView(R.layout.fab_recyclerview);
        addButtonBg = view.findViewById(R.id.fab2_background);
        addButtonBg.setVisibility(View.VISIBLE);
        addButton = (AddFloatingActionButton) view.findViewById(R.id.fab_view2);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setPadding(30, 20, 30, 20);

                final AppCompatEditText nameEdit = new AppCompatEditText(getActivity());
                nameEdit.setHint(getString(R.string.file_name));
                linearLayout.addView(nameEdit);

                new AlertDialog.Builder(getActivity()).setView(linearLayout)
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getActivity(), EditTextActivity.class);
                                Bundle args = new Bundle();
                                args.putString(EditTextActivity.NAME_ARG, nameEdit.getText().toString());
                                intent.putExtras(args);
                                startActivityForResult(intent, 0);
                            }
                        }).show();
            }
        });
        return (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
        fabView.setVisibility(View.GONE);
        fabView = addButton;

        backgroundView.setVisibility(View.GONE);
        backgroundView = null;

        applyOnBootText.setText(getString(R.string.emulate_initd));
        applyOnBootView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveBoolean("emulateinit.d", isChecked, getActivity());
            }
        });
        applyOnBootView.setChecked(Utils.getBoolean("emulateinit.d", false, getActivity()));
    }

    @Override
    public void setOnScrollListener(RecyclerView recyclerView) {
        super.setOnScrollListener(recyclerView);
        if (!Utils.isTV(getActivity()))
            recyclerView.addOnScrollListener(fabHideScrollListener = new FabHideScrollListener());
    }

    private class FabHideScrollListener extends RecyclerView.OnScrollListener {

        private boolean hide;
        private boolean scrolled;
        private boolean reset;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!reset) {
                hide = dy > -1;
                scrolled = true;
            }
            reset = false;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (scrolled && newState == RecyclerView.SCROLL_STATE_IDLE) {
                final int height = addButtonBg.getHeight();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        float offset = getResources().getDisplayMetrics().density * 10;
                        if (!hide && ViewHelper.getTranslationY(addButtonBg) == height) {
                            for (int i = height; i > 0; i -= offset)
                                try {
                                    move(i);
                                    Thread.sleep(16);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            move(0);
                        } else if (hide && ViewHelper.getTranslationY(addButtonBg) == 0) {
                            for (int i = 0; i < height; i += offset)
                                try {
                                    move(i);
                                    Thread.sleep(16);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            move(height);
                        }
                    }
                }).start();
            }
            scrolled = false;
        }

        private void move(final int translation) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewHelper.setTranslationY(addButtonBg, translation);
                }
            });
        }
    }

    @Override
    public void resetTranslations() {
        super.resetTranslations();
        ViewHelper.setTranslationY(addButtonBg, 0);
        if (fabHideScrollListener != null) {
            fabHideScrollListener.hide = false;
            fabHideScrollListener.reset = true;
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        refresh();
    }

    private void refresh() {
        removeAllViews();

        final InformationCardView.DInformationCard mInformationCard = new InformationCardView.DInformationCard();
        mInformationCard.setText(getString(R.string.emulate_initd_summary));

        addView(mInformationCard);

        for (final String file : Initd.getInitds()) {
            if (file == null || file.isEmpty()) return;
            final CardViewItem.DCardView mInitdCard = new CardViewItem.DCardView();
            mInitdCard.setDescription(file);
            mInitdCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                @Override
                public void onClick(CardViewItem.DCardView dCardView) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.initd_menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    new AlertDialog.Builder(getActivity()).setMessage(Initd.getInitd(file)).show();
                                    break;
                                case 1:
                                    new AsyncTask<Void, Void, String>() {
                                        private ProgressDialog progressDialog;

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            progressDialog = new ProgressDialog(getActivity());
                                            progressDialog.setMessage(getString(R.string.executing));
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                        }

                                        @Override
                                        protected String doInBackground(Void... params) {
                                            return Initd.execute(file);
                                        }

                                        @Override
                                        protected void onPostExecute(String s) {
                                            super.onPostExecute(s);
                                            progressDialog.dismiss();
                                            if (!s.isEmpty())
                                                try {
                                                    new AlertDialog.Builder(getActivity()).setMessage(s).show();
                                                } catch (NullPointerException ignored) {
                                                }
                                        }
                                    }.execute();
                                    break;
                                case 2:
                                    Intent i = new Intent(getActivity(), EditTextActivity.class);
                                    Bundle args = new Bundle();
                                    args.putString(EditTextActivity.NAME_ARG, file);
                                    args.putString(EditTextActivity.TEXT_ARG, Initd.getInitd(file));
                                    i.putExtras(args);
                                    startActivityForResult(i, 1);
                                    break;
                                case 3:
                                    Initd.delete(file);
                                    removeView(mInitdCard);
                                    resetTranslations();
                                    break;
                            }
                        }
                    }).show();
                }
            });

            addView(mInitdCard);
        }
    }

    @Override
    public void postInit(Bundle savedInstanceState) {
        super.postInit(savedInstanceState);
        if (getCount() < 1) Utils.toast(getString(R.string.no_scripts_found), getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String name = data.getExtras().getString("name");
            String text = data.getExtras().getString("text");
            if (text != null) {
                RootFile file = Initd.delete(name);
                RootUtils.mount(true, "/system");
                for (String line : text.split("\\r?\\n")) file.write(line, true);
            }
            if (requestCode == 0) getHandler().post(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        }
    }

}
