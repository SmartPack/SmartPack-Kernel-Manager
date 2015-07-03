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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.InformationCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.tools.Initd;

/**
 * Created by willi on 25.04.15.
 */
public class InitdFragment extends RecyclerViewFragment {

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
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
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (!Utils.getBoolean("hideinfocardinit.d", false, getActivity())) {
            final InformationCardView.DInformationCard mInformationCard = new InformationCardView.DInformationCard();
            mInformationCard.setText(getString(R.string.emulate_initd_summary));
            mInformationCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeView(mInformationCard);
                    Utils.saveBoolean("hideinfocardinit.d", true, getActivity());
                }
            });

            addView(mInformationCard);
        }

        for (final String file : Initd.getInitds()) {
            if (file == null || file.isEmpty()) return;
            CardViewItem.DCardView mInitdCard = new CardViewItem.DCardView();
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
        int count = Utils.getBoolean("hideinfocardinit.d", false, getActivity()) ? 1 : 2;
        if (getCount() < count) Utils.toast(getString(R.string.no_scripts_found), getActivity());
    }
}
