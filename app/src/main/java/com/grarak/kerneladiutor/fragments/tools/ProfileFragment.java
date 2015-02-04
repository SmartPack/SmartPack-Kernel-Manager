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
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.ProfileDB;
import com.grarak.kerneladiutor.utils.database.SysDB;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.01.15.
 */
public class ProfileFragment extends RecyclerViewFragment {

    private TextView noProfilesView;
    private FloatingActionButton addButton;

    @Override
    public int getSpan() {
        int orientation = Utils.getScreenOrientation(getActivity());
        if (Utils.isTablet(getActivity()))
            return orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 5;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 3;
    }

    @Override
    public RecyclerView getRecyclerView() {
        noProfilesView = (TextView) getParentView(R.layout.profile_fragment).findViewById(R.id.no_profiles_text);

        addButton = (FloatingActionButton) getParentView(R.layout.profile_fragment).findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysDB sysDB = new SysDB(getActivity());
                sysDB.create();

                final List<SysDB.SysItem> sysItemList = sysDB.getAllSys();
                sysDB.close();

                if (sysItemList.size() < 1) {
                    Utils.toast(getString(R.string.no_settings), getActivity());
                    return;
                }

                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setPadding(30, 20, 30, 20);

                TextView descriptionText = new TextView(getActivity());
                descriptionText.setText(getString(R.string.profile_description));
                linearLayout.addView(descriptionText);

                final EditText profileName = new EditText(getActivity());
                profileName.setTextColor(getResources().getColor(android.R.color.black));
                profileName.setHint(getString(R.string.name));
                linearLayout.addView(profileName);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) profileName.getLayoutParams();
                layoutParams.topMargin = 40;
                profileName.requestLayout();

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
                                        profileDB.create();

                                        List<String> sys = new ArrayList<>();
                                        List<String> commands = new ArrayList<>();
                                        for (SysDB.SysItem sysItem : sysItemList) {
                                            sys.add(sysItem.getSys());
                                            commands.add(sysItem.getCommand());
                                        }

                                        profileDB.insertProfile(profileName.getText().toString(), sys, commands);
                                        profileDB.close();

                                        getHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                create();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }).show();
            }
        });

        animate();
        return (RecyclerView) getParentView(R.layout.profile_fragment).findViewById(R.id.recycler_view);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        create();
    }

    private void create() {
        removeAllViews();
        ProfileDB profileDB = new ProfileDB(getActivity());
        profileDB.create();

        final List<ProfileDB.ProfileItem> profileItems = profileDB.getAllProfiles();
        for (int i = 0; i < profileItems.size(); i++) {
            CardViewItem.DCardView mProfileCard = new CardViewItem.DCardView();
            mProfileCard.setDescription(profileItems.get(i).getName());

            final int position = i;
            mProfileCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                @Override
                public void onClick(CardViewItem.DCardView dCardView) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setItems(getResources().getStringArray(R.array.profile_menu),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, final int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch (which) {
                                                case 0:
                                                    ProfileDB.ProfileItem profileItem = profileItems.get(position);
                                                    for (int i = 0; i < profileItem.getSys().size(); i++) {
                                                        Control.commandSaver(getActivity(), profileItem.getSys().get(i),
                                                                profileItem.getCommands().get(i));
                                                        RootUtils.runCommand(profileItem.getCommands().get(i));
                                                    }
                                                    break;
                                                case 1:
                                                    ProfileDB profileDB = new ProfileDB(getActivity());
                                                    profileDB.create();

                                                    profileDB.deleteItem(profileItems.get(position).getId());

                                                    profileDB.close();

                                                    getHandler().post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            create();
                                                        }
                                                    });
                                                    break;
                                                case 2:
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ScrollView scrollView = new ScrollView(getActivity());
                                                            scrollView.setBackgroundColor(getResources()
                                                                    .getColor(R.color.color_primary_dark));

                                                            String text = "";
                                                            TextView textView = new TextView(getActivity());
                                                            textView.setBackgroundColor(getResources()
                                                                    .getColor(R.color.color_primary_dark));
                                                            textView.setTextColor(getResources().getColor(android.R.color.white));
                                                            textView.setPadding(0, 20, 0, 20);

                                                            ProfileDB.ProfileItem profileItem = profileItems.get(position);
                                                            for (String command : profileItem.getCommands())
                                                                text += text.isEmpty() ? command : "\n" + command;

                                                            textView.setText(text);
                                                            scrollView.addView(textView);

                                                            ScrollView.LayoutParams layoutParams = (ScrollView.LayoutParams)
                                                                    textView.getLayoutParams();
                                                            layoutParams.setMargins(30, 0, 30, 0);
                                                            textView.requestLayout();

                                                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                                            dialog.setView(scrollView).show();
                                                        }
                                                    });
                                                    break;
                                            }
                                        }
                                    }).start();
                                }
                            }).show();
                }
            });

            addView(mProfileCard);
        }
        profileDB.close();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getCount() < 1) {
                    noProfilesView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noProfilesView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void animate() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        animation.setDuration(1500);
        if (addButton != null) addButton.startAnimation(animation);
    }

}
