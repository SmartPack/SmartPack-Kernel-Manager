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

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.DividerCardView;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
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

        betainfoInit();
        applyonbootInit();
        debuggingInit();
    }

    private void betainfoInit() {
        if (Constants.VERSION_NAME.contains("beta")) {
            SwitchCompatCardItem.DSwitchCompatCard mBetaInfoCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mBetaInfoCard.setTitle(getString(R.string.beta_info));
            mBetaInfoCard.setDescription(getString(R.string.beta_info_summary));
            mBetaInfoCard.setChecked(Utils.getBoolean("betainfo", true, getActivity()));
            mBetaInfoCard.setOnDSwitchCompatCardListener(new SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener() {
                @Override
                public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
                    Utils.saveBoolean("betainfo", checked, getActivity());
                }
            });

            addView(mBetaInfoCard);
        }
    }

    private void applyonbootInit() {
        DividerCardView.DDividerCard mApplyonBootDividerCard = new DividerCardView.DDividerCard();
        mApplyonBootDividerCard.setText(getString(R.string.apply_on_boot));

        addView(mApplyonBootDividerCard);

        final List<String> list = new ArrayList<>();
        for (int i = 15; i < 481; i *= 2)
            list.add(i + getString(R.string.sec));

        PopupCardItem.DPopupCard mApplyonbootDelayCard = new PopupCardItem.DPopupCard(list);
        mApplyonbootDelayCard.setDescription(getString(R.string.delay));
        mApplyonbootDelayCard.setItem(Utils.getInt("applyonbootdelay", 15, getActivity()) + getString(R.string.sec));
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
    }

}
