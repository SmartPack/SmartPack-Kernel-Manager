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

package com.grarak.kerneladiutor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.Control;

import java.io.File;

/**
 * Created by willi on 14.12.14.
 */
public class PathReaderActivity extends ActionBarActivity {

    public enum PATH_TYPE {
        GOVERNOR, IO
    }

    public static final String ARG_TYPE = "type";
    public static final String ARG_TITLE = "title";
    public static final String ARG_PATH = "path";
    public static final String ARG_ERROR = "error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.DARKTHEME) super.setTheme(R.style.AppThemeActionBarDark);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.fragment_layout);

        if (Utils.DARKTHEME)
            findViewById(R.id.content_frame).setBackgroundColor(getResources().getColor(R.color.black));
        getSupportActionBar().setTitle(getIntent().getExtras().getString(ARG_TITLE));
        String path = getIntent().getExtras().getString(ARG_PATH);
        String error = getIntent().getExtras().getString(ARG_ERROR);
        int type = getIntent().getExtras().getInt(ARG_TYPE);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, PathReaderFragment.newInstance
                (path, error, type))
                .commitAllowingStateLoss();

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public static class PathReaderFragment extends RecyclerViewFragment implements View.OnClickListener {

        private String PATH;
        private String ERROR;
        private int TYPE;

        private Handler hand;

        private SwipeRefreshLayout refreshLayout;

        private final String[] FREQ_FILE = new String[]{"hispeed_freq", "optimal_freq", "sync_freq",
                "max_freq_blank", "high_freq_zone", "input_boost_freq"};

        public static PathReaderFragment newInstance(String path, String error, int type) {
            Bundle args = new Bundle();
            args.putString(ARG_PATH, path);
            args.putString(ARG_ERROR, error);
            args.putInt(ARG_TYPE, type);
            PathReaderFragment pathReaderFragment = new PathReaderFragment();
            pathReaderFragment.setArguments(args);
            return pathReaderFragment;
        }

        @Override
        public RecyclerView getRecyclerView() {
            refreshLayout = (SwipeRefreshLayout) getParentView(R.layout.path_read_view).findViewById(R.id.refresh_layout);
            refreshLayout.setColorSchemeResources(R.color.color_primary);
            refreshLayout.setEnabled(false);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    hand.postDelayed(refresh, 500);
                }
            });

            return (RecyclerView) getParentView(R.layout.path_read_view).findViewById(R.id.recycler_view);
        }

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            hand = getHandler();

            PATH = getArguments().getString(ARG_PATH);
            ERROR = getArguments().getString(ARG_ERROR);
            TYPE = getArguments().getInt(ARG_TYPE);

            File[] fileArray = new File(PATH).listFiles();
            if (Utils.existFile(PATH) && fileArray != null) {
                for (File file : fileArray)
                    if (file.isFile()) {
                        String value = Utils.readFile(file.getAbsolutePath());
                        if (value != null) {
                            PopupCardItem.DPopupCard dPopupCard = new PopupCardItem.DPopupCard(null);
                            dPopupCard.setDescription(file.getName());
                            dPopupCard.setItem(value);
                            dPopupCard.setOnClickListener(this);

                            addView(dPopupCard);
                        }
                    }

                // Setup adapter
                if (getCount() < 1) {
                    toast(ERROR);
                    getActivity().finish();
                }
            } else {
                toast(ERROR);
                getActivity().finish();
            }
        }

        private final Runnable refresh = new Runnable() {
            @Override
            public void run() {
                removeAllViews();

                File[] fileArray = new File(PATH).listFiles();
                if (fileArray != null) {
                    for (File file : fileArray) {
                        String value = Utils.readFile(file.getAbsolutePath());
                        if (value != null) {
                            PopupCardItem.DPopupCard dPopupCard = new PopupCardItem.DPopupCard(null);
                            dPopupCard.setDescription(file.getName());
                            dPopupCard.setItem(value);
                            dPopupCard.setOnClickListener(PathReaderFragment.this);

                            addView(dPopupCard);
                        }
                    }
                }
                refreshLayout.setRefreshing(false);
            }
        };

        @Override
        public void onClick(View v) {
            boolean freq = false;
            for (String freqFile : FREQ_FILE)
                if (((PopupCardItem) v).getDescription().equals(freqFile)) {
                    freq = true;
                    break;
                }

            if (freq && TYPE == PATH_TYPE.GOVERNOR.ordinal()) {
                String[] values = new String[CPU.getFreqs().size()];
                for (int i = 0; i < values.length; i++)
                    values[i] = String.valueOf(CPU.getFreqs().get(i));
                showPopupDialog(PATH + "/" + ((PopupCardItem) v).getDescription(), values);
            } else
                showDialog(PATH + "/" + ((PopupCardItem) v).getDescription(),
                        ((PopupCardItem) v).getItem());
        }

        private void showDialog(final String file, String value) {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setPadding(30, 30, 30, 30);

            final EditText editText = new EditText(getActivity());
            editText.setGravity(Gravity.CENTER);
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                    hand.postDelayed(refresh, 500);
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
                    hand.postDelayed(refresh, 500);
                }
            }).show();
        }

        private void toast(final String message) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(message, getActivity());
                }
            });
        }

        @Override
        public void animateRecyclerView() {
        }

    }

}
