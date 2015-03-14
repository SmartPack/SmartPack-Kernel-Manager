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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.TintEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.utils.tools.Buildprop;

import java.util.List;

/**
 * Created by willi on 31.12.14.
 */
public class BuildpropFragment extends RecyclerViewFragment implements View.OnClickListener {

    private Handler hand;

    private SwipeRefreshLayout refreshLayout;
    private FloatingActionsMenu floatingActionsMenu;
    private List<Buildprop.BuildpropItems> buildpropItem;

    private MenuItem searchItem;

    @Override
    public RecyclerView getRecyclerView() {
        refreshLayout = (SwipeRefreshLayout) getParentView(R.layout.path_read_view).findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hand.postDelayed(refresh, 500);
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) getParentView(R.layout.path_read_view).findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addKeyDialog(null, null, false);
            }
        });

        FloatingActionButton backupButton = (FloatingActionButton) getParentView(R.layout.path_read_view).findViewById(R.id.backup_button);
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup();
            }
        });

        floatingActionsMenu = (FloatingActionsMenu) getParentView(R.layout.path_read_view).findViewById(R.id.fab_menu);
        floatingActionsMenu.setVisibility(View.VISIBLE);
        animateFab();
        return (RecyclerView) getParentView(R.layout.path_read_view).findViewById(R.id.recycler_view);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        hand = getHandler();

        buildpropItem = Buildprop.getProps();
        for (int i = 0; i < buildpropItem.size(); i++) {
            PopupCardItem.DPopupCard mPropCard = new PopupCardItem.DPopupCard(null);
            mPropCard.setDescription(buildpropItem.get(i).getKey());
            mPropCard.setItem(buildpropItem.get(i).getValue());
            mPropCard.setOnClickListener(this);

            addView(mPropCard);
        }
    }

    @Override
    public void onClick(final View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setItems(getResources().getStringArray(R.array.build_prop_menu),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PopupCardItem popupCardItem = (PopupCardItem) v;
                        switch (which) {
                            case 0:
                                addKeyDialog(popupCardItem.getDescription(),
                                        popupCardItem.getItem(), true);
                                break;
                            case 1:
                                deleteDialog(popupCardItem.getDescription(),
                                        popupCardItem.getItem());
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
                PopupCardItem.DPopupCard mPropCard = new PopupCardItem.DPopupCard(null);
                mPropCard.setDescription(buildpropItem.get(i).getKey());
                mPropCard.setItem(buildpropItem.get(i).getValue());
                mPropCard.setOnClickListener(BuildpropFragment.this);

                addView(mPropCard);
            }

            refreshLayout.setRefreshing(false);
        }
    };

    private void addKeyDialog(final String key, final String value, final boolean modify) {
        LinearLayout dialogLayout = new LinearLayout(getActivity());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setGravity(Gravity.CENTER);
        dialogLayout.setPadding(30, 20, 30, 20);

        final TintEditText keyEdit = new TintEditText(getActivity());
        keyEdit.setTextColor(getResources().getColor(Utils.DARKTHEME ? R.color.textcolor_dark : R.color.black));

        if (modify) keyEdit.setText(key.trim());
        else keyEdit.setHint(getString(R.string.key));

        final TintEditText valueEdit = new TintEditText(getActivity());
        valueEdit.setTextColor(getResources().getColor(Utils.DARKTHEME ? R.color.textcolor_dark : R.color.black));

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.delete_prop, key)).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                overwrite(key.trim(), value.trim(), "#" + key.trim(), value.trim());
            }
        }).show();
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
        SearchView searchView = new SearchView(((ActionBarActivity) getActivity())
                .getSupportActionBar().getThemedContext());
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
                for (Buildprop.BuildpropItems item : buildpropItem)
                    if (item.getKey().contains(newText)) {
                        PopupCardItem.DPopupCard mPopupCard = new PopupCardItem.DPopupCard(null);
                        mPopupCard.setDescription(item.getKey());
                        mPopupCard.setItem(item.getValue());
                        mPopupCard.setOnClickListener(BuildpropFragment.this);

                        addView(mPopupCard);
                    }
                return true;
            }
        });
    }

    private void animateFab() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        animation.setDuration(1500);
        if (floatingActionsMenu != null) floatingActionsMenu.startAnimation(animation);
    }

}
