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

package com.grarak.kerneladiutor.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootFile;

/**
 * Created by willi on 07.04.15.
 */
public abstract class PathReaderFragment extends RecyclerViewFragment {

    public enum PATH_TYPE {
        GOVERNOR, IO
    }

    private TextView errorText;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public RecyclerView getRecyclerView() {
        errorText = (TextView) getParentView(R.layout.swiperefresh_recyclerview).findViewById(R.id.error_text);
        refreshLayout = (SwipeRefreshLayout) getParentView(R.layout.swiperefresh_recyclerview).findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_primary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHandler().postDelayed(refresh, 500);
            }
        });

        return (RecyclerView) getParentView(R.layout.swiperefresh_recyclerview).findViewById(R.id.recycler_view);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        refresh();
    }

    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            refresh();
            refreshLayout.setRefreshing(false);
        }
    };

    public void reload() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getHandler().postDelayed(refresh, 500);
            }
        });
    }

    private void refresh() {
        removeAllViews();

        final String path = getPath();
        String files[] = new RootFile(path).list();
        if (files != null)
            for (String file : files)
                if (file != null) {
                    String value = Utils.readFile(path + "/" + file);
                    if (value != null && !value.isEmpty() && !value.contains("\n")) {
                        PopupCardItem.DPopupCard mPathCard = new PopupCardItem.DPopupCard(null);
                        mPathCard.setDescription(file);
                        mPathCard.setItem(value);
                        mPathCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean freq = CPU.getFreqs().indexOf(Utils.stringToInt(Utils
                                        .readFile(path + "/" + ((PopupCardItem) v).getDescription()))) > -1;

                                if (freq && getType() == PATH_TYPE.GOVERNOR) {
                                    String[] values = new String[CPU.getFreqs().size()];
                                    for (int i = 0; i < values.length; i++)
                                        values[i] = String.valueOf(CPU.getFreqs().get(i));
                                    showPopupDialog(path + "/" + ((PopupCardItem) v).getDescription(), values);
                                } else
                                    showDialog(path + "/" + ((PopupCardItem) v).getDescription(),
                                            ((PopupCardItem) v).getItem());
                            }
                        });

                        addView(mPathCard);
                    }
                }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getCount() < 1) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(getError(getActivity()));
                    recyclerView.setVisibility(View.GONE);
                } else {
                    errorText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showDialog(final String file, String value) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(30, 30, 30, 30);

        final EditText editText = new EditText(getActivity());
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (!Utils.DARKTHEME)
            editText.setTextColor(getResources().getColor(R.color.black));
        editText.setText(value);

        layout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Control.runCommand(editText.getText().toString(), file, Control.CommandType.GENERIC, getActivity());
                refreshLayout.setRefreshing(true);
                getHandler().postDelayed(refresh, 500);
            }
        }).show();
    }

    private void showPopupDialog(final String file, final String[] values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Control.runCommand(values[which], file, Control.CommandType.GENERIC, getActivity());
                refreshLayout.setRefreshing(true);
                getHandler().postDelayed(refresh, 500);
            }
        }).show();
    }

    @Override
    public void animateRecyclerView() {
    }

    public abstract String getPath();

    public abstract PATH_TYPE getType();

    public abstract String getError(Context context);

}
