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
package com.grarak.kerneladiutor.activities.tools.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.activities.NavigationActivity;
import com.grarak.kerneladiutor.database.Settings;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.Control;
import com.grarak.kerneladiutor.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 11.07.16.
 */
public class ProfileActivity extends BaseActivity {

    public static final String POSITION_INTENT = "position";
    public static final String FRAGMENTS_INTENT = "fragments";
    public static final String RESULT_ID_INTENT = "result_id";
    public static final String RESULT_COMMAND_INTENT = "result_command";

    private LinkedHashMap<String, Fragment> mItems = new LinkedHashMap<>();

    private int mProfilePosition;
    private int mMode;
    private boolean mHideWarningDialog;
    private int mCurPosition;

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        ArrayList<NavigationActivity.NavigationFragment> fragments =
                intent.getParcelableArrayListExtra(FRAGMENTS_INTENT);

        for (NavigationActivity.NavigationFragment navigationFragment : fragments) {
            mItems.put(getString(navigationFragment.mId), getFragment(navigationFragment.mId,
                    navigationFragment.mFragmentClass));
        }

        mItems.remove("Performance Tweaks");

        if (mItems.size() < 1) {
            Utils.toast(R.string.sections_disabled, this);
            finish();
            return;
        }

        mProfilePosition = intent.getIntExtra(POSITION_INTENT, -1);
        if (savedInstanceState != null && (mMode = savedInstanceState.getInt("mode")) != 0) {
            if (mMode == 1) {
                initNewMode(savedInstanceState);
            } else {
                currentSettings();
            }
        } else {
            new Dialog(this).setItems(getResources().getStringArray(R.array.profile_modes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    initNewMode(savedInstanceState);
                                    break;
                                case 1:
                                    currentSettings();
                                    break;
                            }
                        }
                    }).setCancelable(false).show();
        }
    }

    public Fragment getFragment(int res, Class<? extends Fragment> fragmentClass) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(res + "_key");
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentClass.getCanonicalName());
        }
        return fragment;
    }

    private void initNewMode(Bundle savedInstanceState) {
        mMode = 1;
        setContentView(R.layout.activity_profile);

        Control.clearProfileCommands();
        Control.setProfileMode(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (savedInstanceState != null) {
            mHideWarningDialog = savedInstanceState.getBoolean("hidewarningdialog");
        }
        if (!mHideWarningDialog) {
            ViewUtils.dialogBuilder(getString(R.string.profile_warning), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mHideWarningDialog = true;
                }
            }, this).show();
        }

        viewPager.setOffscreenPageLimit(mItems.size());
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mItems);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnIntent(Control.getProfileCommands());
            }
        });
    }

    private void currentSettings() {
        mMode = 2;
        ViewUtils.showDialog(getSupportFragmentManager(), CurrentSettingsFragment.newInstance(mItems));
    }

    private void returnIntent(LinkedHashMap<String, String> commandsList) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> commands = new ArrayList<>();
        Collections.addAll(ids, commandsList.keySet().toArray(new String[commands.size()]));
        Collections.addAll(commands, commandsList.values().toArray(new String[commands.size()]));
        if (commands.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra(POSITION_INTENT, mProfilePosition);
            intent.putExtra(RESULT_ID_INTENT, ids);
            intent.putExtra(RESULT_COMMAND_INTENT, commands);
            setResult(0, intent);
            finish();
        } else {
            Utils.toast(R.string.no_changes, ProfileActivity.this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);
        outState.putBoolean("hidewarningdialog", mHideWarningDialog);
    }

    @Override
    public void finish() {
        Control.setProfileMode(false);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) mItems.values().toArray(new Fragment[mItems.size()])[mCurPosition];
        if (!fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private final LinkedHashMap<String, Fragment> mFragments;

        private PagerAdapter(FragmentManager fm, LinkedHashMap<String, Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(mFragments.keySet().toArray(new String[mFragments.size()])[position]);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.keySet().toArray(new String[mFragments.size()])[position];
        }
    }

    public static class CurrentSettingsFragment extends DialogFragment {

        public static CurrentSettingsFragment newInstance(LinkedHashMap<String, Fragment> sections) {
            CurrentSettingsFragment fragment = new CurrentSettingsFragment();
            fragment.mList = sections;
            return fragment;
        }

        private LinkedHashMap<String, Fragment> mList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            setCancelable(false);
            setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_dialog, container, false);

            LinearLayout checkBoxParent = (LinearLayout) rootView.findViewById(R.id.checkbox_parent);
            final HashMap<AppCompatCheckBox, Class> checkBoxes = new HashMap<>();
            for (final String name : mList.keySet()) {
                AppCompatCheckBox compatCheckBox = new AppCompatCheckBox(getActivity());
                compatCheckBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                compatCheckBox.setText(name);
                checkBoxParent.addView(compatCheckBox);

                checkBoxes.put(compatCheckBox, mList.get(name).getClass());
            }

            rootView.findViewById(R.id.select_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (AppCompatCheckBox compatCheckBox : checkBoxes.keySet()) {
                        compatCheckBox.setChecked(true);
                    }
                }
            });

            AppCompatImageButton cancel = (AppCompatImageButton) rootView.findViewById(R.id.cancel);
            DrawableCompat.setTint(cancel.getDrawable(), ViewUtils.getThemeAccentColor(getActivity()));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            AppCompatImageButton done = (AppCompatImageButton) rootView.findViewById(R.id.done);
            DrawableCompat.setTint(done.getDrawable(), ViewUtils.getThemeAccentColor(getActivity()));
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> categories = new ArrayList<>();
                    for (AppCompatCheckBox compatCheckBox : checkBoxes.keySet()) {
                        if (compatCheckBox.isChecked()) {
                            categories.add(ApplyOnBootFragment.getAssignment(checkBoxes.get(compatCheckBox)));
                        }
                    }
                    if (categories.size() < 1) {
                        Utils.toast(R.string.nothing_selected, getActivity());
                        return;
                    }

                    LinkedHashMap<String, String> items = new LinkedHashMap<>();
                    List<Settings.SettingsItem> settingsItems = new Settings(getActivity()).getAllSettings();
                    for (Settings.SettingsItem item : settingsItems) {
                        if (categories.contains(item.getCategory())) {
                            items.put(item.getId(), item.getSetting());
                        }
                    }
                    ((ProfileActivity) getActivity()).returnIntent(items);
                }
            });

            return rootView;
        }
    }

}
