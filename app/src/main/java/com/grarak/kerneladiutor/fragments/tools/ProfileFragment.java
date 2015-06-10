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

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.services.ProfileWidget;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.CommandDB;
import com.grarak.kerneladiutor.utils.database.ProfileDB;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 31.01.15.
 */
public class ProfileFragment extends RecyclerViewFragment {

    private TextView title;

    @Override
    public int getSpan() {
        int orientation = Utils.getScreenOrientation(getActivity());
        if (Utils.isTablet(getActivity()))
            return orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 5;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 3;
    }

    @Override
    public RecyclerView getRecyclerView() {
        View view = getParentView(R.layout.fab_recyclerview);
        title = (TextView) view.findViewById(R.id.title_view);
        return (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);

        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<CommandDB.CommandItem> commandItems = new CommandDB(getActivity()).getAllCommands();

                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setPadding(30, 20, 30, 20);

                TextView descriptionText = new TextView(getActivity());
                descriptionText.setText(getString(R.string.profile_description));
                linearLayout.addView(descriptionText);

                final AppCompatEditText profileName = new AppCompatEditText(getActivity());
                profileName.setTextColor(getResources().getColor(Utils.DARKTHEME ? R.color.white : R.color.black));
                profileName.setHint(getString(R.string.name));
                linearLayout.addView(profileName);

                ScrollView scrollView = new ScrollView(getActivity());
                scrollView.setPadding(0, 0, 0, 10);
                linearLayout.addView(scrollView);

                LinearLayout checkBoxLayout = new LinearLayout(getActivity());
                checkBoxLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(checkBoxLayout);

                AppCompatButton selectAllButton = new AppCompatButton(getActivity());
                selectAllButton.setText(getString(R.string.select_all));
                checkBoxLayout.addView(selectAllButton);

                boolean load = true;
                String start = getString(R.string.kernel);
                String stop = getString(R.string.tools);
                final LinkedHashMap<Class, AppCompatCheckBox> items = new LinkedHashMap<>();
                for (DAdapter.DView item : Constants.ITEMS) {
                    if (item.getTitle() != null) {
                        if (item.getTitle().equals(start)) load = false;
                        if (item.getTitle().equals(stop)) load = true;
                        if (item.getFragment() != null && !load) {
                            AppCompatCheckBox checkBox = new AppCompatCheckBox(getActivity());
                            checkBox.setText(item.getTitle());
                            checkBoxLayout.addView(checkBox);

                            items.put(item.getFragment().getClass(), checkBox);
                        }
                    }
                }

                selectAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (Object checkbox : items.values().toArray())
                            ((AppCompatCheckBox) checkbox).setChecked(true);
                    }
                });

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setView(linearLayout).setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProfileDB profileDB = new ProfileDB(getActivity());

                                        List<String> applys = new ArrayList<>();
                                        for (int i = 0; i < items.size(); i++)
                                            if (((AppCompatCheckBox) items.values().toArray()[i]).isChecked())
                                                applys.addAll(Utils.getApplys((Class) items.keySet().toArray()[i]));

                                        final LinkedHashMap<String, String> commands = new LinkedHashMap<>();
                                        for (CommandDB.CommandItem commandItem : commandItems)
                                            for (String s : applys) {
                                                String path = commandItem.getPath();
                                                if (s.contains(path) || path.contains(s))
                                                    commands.put(path, commandItem.getCommand());
                                            }

                                        final String name = profileName.getText().toString();
                                        if (!name.isEmpty() && commands.size() > 0)
                                            profileDB.putProfile(name, commands);
                                        profileDB.commit();

                                        getHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (name.isEmpty())
                                                    Utils.toast(getString(R.string.empty_name), getActivity());
                                                else if (commands.size() < 1)
                                                    Utils.toast(getString(R.string.no_settings), getActivity());
                                                else create();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        create();
    }

    private void create() {
        removeAllViews();
        ProfileDB profileDB = new ProfileDB(getActivity());

        final List<ProfileDB.ProfileItem> profileItems = profileDB.getAllProfiles();
        for (int i = 0; i < profileItems.size(); i++) {
            CardViewItem.DCardView mProfileCard = new CardViewItem.DCardView();
            mProfileCard.setDescription(profileItems.get(i).getName());

            final int position = i;
            mProfileCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                @Override
                public void onClick(CardViewItem.DCardView dCardView) {
                    new AlertDialog.Builder(getActivity()).setItems(getResources().getStringArray(R.array.profile_menu),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ProfileDB.ProfileItem profileItem = profileItems.get(position);
                                    switch (which) {
                                        case 0:
                                            List<String> paths = profileItem.getPath();
                                            for (int i = 0; i < paths.size(); i++) {
                                                Control.commandSaver(getActivity(), paths.get(i),
                                                        profileItem.getCommands().get(i));
                                                RootUtils.runCommand(profileItem.getCommands().get(i));
                                            }
                                            break;
                                        case 1:
                                            ProfileDB profileDB = new ProfileDB(getActivity());
                                            profileDB.delete(position);

                                            profileDB.commit();

                                            getHandler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    create();
                                                }
                                            });
                                            break;
                                        case 2:
                                            String text = "";
                                            for (String command : profileItem.getCommands())
                                                text += text.isEmpty() ? command : "\n" + command;
                                            new AlertDialog.Builder(getActivity()).setMessage(text.trim()).show();
                                            break;
                                    }
                                }
                            }).show();
                }
            });

            addView(mProfileCard);
        }

        if (isAdded()) getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(getCount() < 1 ? getString(R.string.no_profiles) : getString(R.string.items_found, getCount()));
            }
        });

        // Update Profilewidget here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), ProfileWidget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.profile_list);
        }
    }

}
