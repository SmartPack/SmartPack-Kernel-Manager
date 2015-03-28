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

package com.grarak.kerneladiutor.fragments.other;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.grarak.kerneladiutor.MainActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.DividerCardView;
import com.grarak.kerneladiutor.elements.ListAdapter;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.services.BootService;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 09.03.15.
 */
public class SettingsFragment extends RecyclerViewFragment {

    @Override
    public boolean showApplyOnBoot() {
        return false;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        darkthemeInit();
        if (!Resources.getSystem().getConfiguration().locale.getLanguage().startsWith("en"))
            forceenglishlanguageInit();
        if (Constants.VERSION_NAME.contains("beta")) betainfoInit();
        applyonbootInit();
        debuggingInit();
    }

    private void darkthemeInit() {
        SwitchCompatCardItem.DSwitchCompatCard mDarkthemeCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mDarkthemeCard.setDescription(getString(R.string.dark_theme));
        mDarkthemeCard.setChecked(Utils.DARKTHEME);
        mDarkthemeCard.setOnDSwitchCompatCardListener(new SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener() {
            @Override
            public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
                Utils.saveBoolean("darktheme", checked, getActivity());
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        addView(mDarkthemeCard);
    }

    private void forceenglishlanguageInit() {
        SwitchCompatCardItem.DSwitchCompatCard mForceEnglishLanguageCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mForceEnglishLanguageCard.setDescription(getString(R.string.force_english_language));
        mForceEnglishLanguageCard.setChecked(Utils.getBoolean("forceenglish", false, getActivity()));
        mForceEnglishLanguageCard.setOnDSwitchCompatCardListener(
                new SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener() {
                    @Override
                    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
                        Utils.saveBoolean("forceenglish", checked, getActivity());
                        if (!checked)
                            Utils.setLocale(Resources.getSystem().getConfiguration().locale.getLanguage(), getActivity());
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });

        addView(mForceEnglishLanguageCard);
    }

    private void betainfoInit() {
        SwitchCompatCardItem.DSwitchCompatCard mBetaInfoCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mBetaInfoCard.setTitle(getString(R.string.beta_info));
        mBetaInfoCard.setDescription(getString(R.string.beta_info_summary));
        mBetaInfoCard.setChecked(Utils.getBoolean("betainfo", true, getActivity()));
        mBetaInfoCard.setOnDSwitchCompatCardListener(
                new SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener() {
                    @Override
                    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
                        Utils.saveBoolean("betainfo", checked, getActivity());
                    }
                });

        addView(mBetaInfoCard);
    }

    private void applyonbootInit() {
        DividerCardView.DDividerCard mApplyonBootDividerCard = new DividerCardView.DDividerCard();
        mApplyonBootDividerCard.setText(getString(R.string.apply_on_boot));

        addView(mApplyonBootDividerCard);

        final List<String> list = new ArrayList<>();
        for (int i = 5; i < 421; i *= 2)
            list.add(i + getString(R.string.sec));

        PopupCardItem.DPopupCard mApplyonbootDelayCard = new PopupCardItem.DPopupCard(list);
        mApplyonbootDelayCard.setDescription(getString(R.string.delay));
        mApplyonbootDelayCard.setItem(Utils.getInt("applyonbootdelay", 5, getActivity()) + getString(R.string.sec));
        mApplyonbootDelayCard.setOnDPopupCardListener(new PopupCardItem.DPopupCard.OnDPopupCardListener() {
            @Override
            public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
                Utils.saveInt("applyonbootdelay", Utils.stringToInt(list.get(position)
                        .replace(getString(R.string.sec), "")), getActivity());
            }
        });

        addView(mApplyonbootDelayCard);

        SwitchCompatCardItem.DSwitchCompatCard mApplyonbootNotificationCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mApplyonbootNotificationCard.setTitle(getString(R.string.notification));
        mApplyonbootNotificationCard.setDescription(getString(R.string.notification_summary));
        mApplyonbootNotificationCard.setChecked(Utils.getBoolean("applyonbootnotification", true, getActivity()));
        mApplyonbootNotificationCard.setOnDSwitchCompatCardListener(
                new SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener() {
                    @Override
                    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
                        Utils.saveBoolean("applyonbootnotification", checked, getActivity());
                    }
                });

        addView(mApplyonbootNotificationCard);

        SwitchCompatCardItem.DSwitchCompatCard mShowToastCard = new SwitchCompatCardItem.DSwitchCompatCard();
        mShowToastCard.setDescription(getString(R.string.show_toast));
        mShowToastCard.setChecked(Utils.getBoolean("applyonbootshowtoast", true, getActivity()));
        mShowToastCard.setOnDSwitchCompatCardListener(new SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener() {
            @Override
            public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
                Utils.saveBoolean("applyonbootshowtoast", checked, getActivity());
            }
        });

        addView(mShowToastCard);

        CardViewItem.DCardView mTestCard = new CardViewItem.DCardView();
        mTestCard.setTitle(getString(R.string.test));
        mTestCard.setDescription(getString(R.string.test_summary));
        mTestCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
            @Override
            public void onClick(CardViewItem.DCardView dCardView) {
                boolean applyonbootenabled = false;
                for (ListAdapter.ListItem item : Constants.ITEMS)
                    if (item.getFragment() != null && Utils.getBoolean(item.getFragment().getClass().getSimpleName()
                            + "onboot", false, getActivity())) {
                        applyonbootenabled = true;
                        break;
                    }
                if (applyonbootenabled)
                    getActivity().startService(new Intent(getActivity(), BootService.class));
                else Utils.toast(getString(R.string.enable_apply_on_boot_first), getActivity());
            }
        });

        addView(mTestCard);
    }

    private void debuggingInit() {
        DividerCardView.DDividerCard mDebuggingDividerCard = new DividerCardView.DDividerCard();
        mDebuggingDividerCard.setText(getString(R.string.debugging));

        addView(mDebuggingDividerCard);

        CardViewItem.DCardView mLogcatCard = new CardViewItem.DCardView();
        mLogcatCard.setTitle(getString(R.string.logcat));
        mLogcatCard.setDescription(getString(R.string.logcat_summary));
        mLogcatCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
            @Override
            public void onClick(CardViewItem.DCardView dCardView) {
                RootUtils.runCommand("logcat -d > /sdcard/logcat.txt");
            }
        });

        addView(mLogcatCard);

        if (Utils.existFile("/proc/last_kmsg")) {
            CardViewItem.DCardView mLastKmsgCard = new CardViewItem.DCardView();
            mLastKmsgCard.setTitle(getString(R.string.last_kmsg));
            mLastKmsgCard.setDescription(getString(R.string.last_kmsg_summary));
            mLastKmsgCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                @Override
                public void onClick(CardViewItem.DCardView dCardView) {
                    RootUtils.runCommand("cat /proc/last_kmsg > /sdcard/last_kmsg.txt");
                }
            });

            addView(mLastKmsgCard);
        }

        CardViewItem.DCardView mDmesgCard = new CardViewItem.DCardView();
        mDmesgCard.setTitle(getString(R.string.driver_message));
        mDmesgCard.setDescription(getString(R.string.driver_message_summary));
        mDmesgCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
            @Override
            public void onClick(CardViewItem.DCardView dCardView) {
                RootUtils.runCommand("dmesg > /sdcard/dmesg.txt");
            }
        });

        addView(mDmesgCard);
    }

}
