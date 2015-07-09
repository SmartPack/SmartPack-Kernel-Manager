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

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Buildprop;

import java.util.LinkedHashMap;

/**
 * Created by willi on 31.12.14.
 */
public class BuildpropFragment extends RecyclerViewFragment implements View.OnClickListener {

    private Handler hand;

    private TextView title;
    private SwipeRefreshLayout refreshLayout;
    private LinkedHashMap<String, String> buildpropItem;

    private MenuItem searchItem;

    @Override
    public RecyclerView getRecyclerView() {
        View view = getParentView(R.layout.swiperefresh_fragment);

        title = (TextView) view.findViewById(R.id.title_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hand.postDelayed(refresh, 500);
            }
        });

        view.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addKeyDialog(null, null, false);
            }
        });

        view.findViewById(R.id.backup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup();
            }
        });

        return (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            fabView.setVisibility(View.VISIBLE);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        hand = getHandler();

        buildpropItem = Buildprop.getProps();
        for (int i = 0; i < buildpropItem.size(); i++) {
            PopupCardView.DPopupCard mPropCard = new PopupCardView.DPopupCard(null);
            mPropCard.setDescription((String) buildpropItem.keySet().toArray()[i]);
            mPropCard.setItem((String) buildpropItem.values().toArray()[i]);
            mPropCard.setOnClickListener(this);

            addView(mPropCard);
        }
    }

    @Override
    public void postInit(Bundle savedInstanceState) {
        super.postInit(savedInstanceState);
        title.setText(getString(R.string.items_found, buildpropItem.size()));
    }

    @Override
    public void onClick(final View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setItems(getResources().getStringArray(R.array.build_prop_menu),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PopupCardView popupCardView = (PopupCardView) v;
                        switch (which) {
                            case 0:
                                addKeyDialog(popupCardView.getDescription().toString(),
                                        popupCardView.getItem(), true);
                                break;
                            case 1:
                                deleteDialog(popupCardView.getDescription().toString(),
                                        popupCardView.getItem());
                                break;
                        }
                    }
                }).show();
    }

    private final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if (searchItem != null) MenuItemCompat.collapseActionView(searchItem);

            removeAllViews();
            buildpropItem = Buildprop.getProps();
            for (int i = 0; i < buildpropItem.size(); i++) {
                PopupCardView.DPopupCard mPropCard = new PopupCardView.DPopupCard(null);
                mPropCard.setDescription((String) buildpropItem.keySet().toArray()[i]);
                mPropCard.setItem((String) buildpropItem.values().toArray()[i]);
                mPropCard.setOnClickListener(BuildpropFragment.this);

                addView(mPropCard);
            }

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.items_found, buildpropItem.size()));
                        refreshLayout.setRefreshing(false);
                    }
                });
            } catch (NullPointerException ignored) {
            }
        }
    };

    private void addKeyDialog(final String key, final String value, final boolean modify) {
        LinearLayout dialogLayout = new LinearLayout(getActivity());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setGravity(Gravity.CENTER);
        dialogLayout.setPadding(30, 20, 30, 20);

        final AppCompatEditText keyEdit = new AppCompatEditText(getActivity());
        keyEdit.setTextColor(getResources().getColor(Utils.DARKTHEME ? R.color.white : R.color.black));

        if (modify) keyEdit.setText(key.trim());
        else keyEdit.setHint(getString(R.string.key));

        final AppCompatEditText valueEdit = new AppCompatEditText(getActivity());
        valueEdit.setTextColor(getResources().getColor(Utils.DARKTHEME ? R.color.white : R.color.black));

        if (modify) valueEdit.setText(value.trim());
        else valueEdit.setHint(getString(R.string.value));

        dialogLayout.addView(keyEdit);
        dialogLayout.addView(valueEdit);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogLayout).setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (modify)
                            overwrite(key.trim(), value.trim(), keyEdit.getText().toString().trim(),
                                    valueEdit.getText().toString().trim());
                        else
                            add(keyEdit.getText().toString().trim(), valueEdit.getText().toString().trim());
                    }
                }).show();
    }

    private void deleteDialog(final String key, final String value) {
        Utils.confirmDialog(null, getString(R.string.delete_question, key), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                overwrite(key.trim(), value.trim(), "#" + key.trim(), value.trim());
            }
        }, getActivity());
    }

    private void backup() {
        RootUtils.mount(true, "/system");
        RootUtils.runCommand("cp -f " + Constants.BUILD_PROP + " " + Constants.BUILD_PROP + ".bak");
        Utils.toast(getString(R.string.backup_created, Constants.BUILD_PROP + ".bak"), getActivity());
    }

    private void add(String key, String value) {
        Buildprop.addKey(key, value);
        hand.postDelayed(refresh, 500);
    }

    private void overwrite(String oldKey, String oldValue, String newKey, String newValue) {
        Buildprop.overwrite(oldKey, oldValue, newKey, newValue);
        hand.postDelayed(refresh, 500);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.build_prop_menu, menu);

        searchItem = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(getActionBar().getThemedContext());
        searchView.setQueryHint(getString(R.string.search));

        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                removeAllViews();

                Object[] keys = buildpropItem.keySet().toArray();
                Object[] values = buildpropItem.values().toArray();
                for (int i = 0; i < keys.length; i++)
                    if (((String) keys[i]).contains(newText)) {
                        PopupCardView.DPopupCard mPopupCard = new PopupCardView.DPopupCard(null);
                        mPopupCard.setDescription(newText.isEmpty() ?
                                (String) keys[i] : Html.fromHtml(((String) keys[i]).replace(newText, "" +
                                "<b><font color=\"#2A7289\">" + newText + "</font></b>")));
                        mPopupCard.setItem((String) values[i]);
                        mPopupCard.setOnClickListener(BuildpropFragment.this);

                        addView(mPopupCard);
                    }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                MenuItemCompat.expandActionView(searchItem);
                break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (searchItem != null && MenuItemCompat.isActionViewExpanded(searchItem)) {
            MenuItemCompat.collapseActionView(searchItem);
            return true;
        }
        return false;
    }
}
