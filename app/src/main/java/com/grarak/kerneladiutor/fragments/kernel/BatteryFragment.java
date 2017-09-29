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
package com.grarak.kerneladiutor.fragments.kernel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.battery.Battery;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.StatsView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class BatteryFragment extends RecyclerViewFragment {

    private StatsView mLevel;
    private StatsView mVoltage;

    private static int sBatteryLevel;
    private static int sBatteryVoltage;

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        levelInit(items);
        voltageInit(items);
        chargeLevelControlInit(items);
        chargeRateInit(items);
        if (Battery.hasBlx()) {
            blxInit(items);
        }
        if (Battery.hasForceFastCharge()) {
            ForceFastChargeInit(items);
        }
        if (Battery.hasChargeLevelControlAC()) {
            ChargeLevelControlACinit(items);
        }
        if (Battery.hasChargeLevelControlUSB()) {
            ChargeLevelControlUSBinit(items);
        }
        if (Battery.hasChargeLevelControlWIRELESS()) {
            ChargeLevelControlWirelessinit(items);
        }
        if (Battery.hasMtpForceFastCharge()) {
            MtpFastChargeInit(items);
        }
        if (Battery.hasScreenCurrentLimit()) {
            ScreenCurrentLimitInit(items);
        }
        if (Battery.hasChargeCustomAC()) {
            WarningChargingInit(items);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (itemsSize() > 2) {
            addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        }
        if (Battery.hasCapacity(getActivity())) {
            addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.capacity),
                    Battery.getCapacity(getActivity()) + getString(R.string.mah)));
        }
    }

    private void levelInit(List<RecyclerViewItem> items) {
        mLevel = new StatsView();
        mLevel.setTitle(getString(R.string.level));

        items.add(mLevel);
    }

    private void voltageInit(List<RecyclerViewItem> items) {
        mVoltage = new StatsView();
        mVoltage.setTitle(getString(R.string.voltage));

        items.add(mVoltage);
    }

    private void chargeLevelControlInit(List<RecyclerViewItem> items) {
        CardView ChargeLevelCard = new CardView(getActivity());
        ChargeLevelCard.setTitle(getString(R.string.charge_levels));
        
        items.add(ChargeLevelCard);
    }
    
    private void ForceFastChargeInit(List<RecyclerViewItem> items) {
            SelectView forceFastCharge = new SelectView();
            forceFastCharge.setTitle(getString(R.string.usb_fast_charge));
            forceFastCharge.setSummary(getString(R.string.usb_fast_charge_summary));
            forceFastCharge.setItems(Battery.enableForceFastCharge(getActivity()));
            forceFastCharge.setItem(Battery.getForceFastCharge());
            forceFastCharge.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Battery.setForceFastCharge(position, getActivity());
            }
        });

            items.add(forceFastCharge);
    }
        
    private void ChargeLevelControlACinit(List<RecyclerViewItem> items) {
            SelectView ACLevelCard = new SelectView();
            ACLevelCard.setTitle(getString(R.string.charge_level_ac));
            ACLevelCard.setSummary(getString(R.string.charge_level_ac_summary));
            ACLevelCard.setItems(Battery.getChargeLevelControlAC());
            ACLevelCard.setItem(Battery.getChargeLevelCustomAC());
            ACLevelCard.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Battery.setChargeLevelControlAC(item, getActivity());
            }
        });
            items.add(ACLevelCard);
    }
            
    private void ChargeLevelControlUSBinit(List<RecyclerViewItem> items) {
            SelectView USBLevelCard = new SelectView();
            USBLevelCard.setTitle(getString(R.string.charge_level_usb));
            USBLevelCard.setSummary(getString(R.string.charge_level_usb_summary));
            USBLevelCard.setItems(Battery.getChargeLevelControlUSB());
            USBLevelCard.setItem(Battery.getChargeLevelCustomUSB());
            USBLevelCard.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Battery.setChargeLevelControlUSB(item, getActivity());
            }
        });
            items.add(USBLevelCard);
    }
    
    private void ChargeLevelControlWirelessinit(List<RecyclerViewItem> items) {
            SelectView WirelessLevelCard = new SelectView();
            WirelessLevelCard.setTitle(getString(R.string.charge_level_wireless));
            WirelessLevelCard.setSummary(getString(R.string.charge_level_wireless_summary));
            WirelessLevelCard.setItems(Battery.getChargeLevelControlWIRELESS());
            WirelessLevelCard.setItem(Battery.getChargeLevelCustomWIRELESS());
            WirelessLevelCard.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Battery.setChargeLevelControlWIRELESS(item, getActivity());
            }
        });
            items.add(WirelessLevelCard);
    }
    
    private void MtpFastChargeInit(List<RecyclerViewItem> items) {
        SwitchView MtpFastCharge = new SwitchView();
        MtpFastCharge.setTitle(getString(R.string.mtp_fast_charge));
        MtpFastCharge.setSummary(getString(R.string.mtp_fast_charge_summary));
        MtpFastCharge.setChecked(Battery.isMtpForceFastChargeEnabled());
        MtpFastCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Battery.enableMtpForceFastCharge(isChecked, getActivity());
            }
        });

        items.add(MtpFastCharge);
    }
    
    private void ScreenCurrentLimitInit(List<RecyclerViewItem> items) {
        SwitchView ScreenLimit = new SwitchView();
        ScreenLimit.setTitle(getString(R.string.screen_limit));
        ScreenLimit.setSummary(getString(R.string.screen_limit_summary));
        ScreenLimit.setChecked(Battery.isScreenCurrentLimit());
        ScreenLimit.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Battery.enableScreenCurrentLimit(isChecked, getActivity());
            }
        });

        items.add(ScreenLimit);
    }
    
    private void WarningChargingInit(List<RecyclerViewItem> items) {
        CardView WarningChargeCard = new CardView(getActivity());
        WarningChargeCard.setTitle(getString(R.string.warning_charge));
        
        if (Battery.hasFailsafe()) {
            SwitchView Failsafe = new SwitchView();
            Failsafe.setTitle(getString(R.string.failsafe));
            Failsafe.setSummary(getString(R.string.failsafe_summary));
            Failsafe.setChecked(Battery.isFailsafe());
            Failsafe.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Battery.enableFailsafe(isChecked, getActivity());
                }
            });

            WarningChargeCard.addItem(Failsafe);
        }
        
        if (Battery.hasChargeLevelCustomAC()) {            
            SeekBarView chargingCustomAC = new SeekBarView();
            chargingCustomAC.setTitle(getString(R.string.charging_custom_ac));
            chargingCustomAC.setSummary(getString(R.string.charging_current_ac_summary));
            chargingCustomAC.setUnit(getString(R.string.ma));
            chargingCustomAC.setMax(2200);
            chargingCustomAC.setMin(100);
            chargingCustomAC.setOffset(10);
            chargingCustomAC.setProgress(Battery.getChargeCustomAC() / 10 - 10 );
            chargingCustomAC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
                
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setChargeControlAC((position + 10) * 10, getActivity());
                }
            });

            WarningChargeCard.addItem(chargingCustomAC);
        }
        
        if (Battery.hasChargeLevelCustomUSB()) {
            SeekBarView chargingCustomUSB = new SeekBarView();
            chargingCustomUSB.setTitle(getString(R.string.charging_custom_usb));
            chargingCustomUSB.setSummary(getString(R.string.charging_current_usb_summary));
            chargingCustomUSB.setUnit(getString(R.string.ma));
            chargingCustomUSB.setMax(1200);
            chargingCustomUSB.setMin(100);
            chargingCustomUSB.setOffset(10);
            chargingCustomUSB.setProgress(Battery.getChargeCustomUSB() / 10 - 10);
            chargingCustomUSB.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setChargeControlUSB((position + 10) * 10, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            WarningChargeCard.addItem(chargingCustomUSB);
        }
        
        if (Battery.hasChargeLevelCustomWireless()) {            
            SeekBarView chargingCustomWireless = new SeekBarView();
            chargingCustomWireless.setTitle(getString(R.string.charging_custom_wireless));
            chargingCustomWireless.setSummary(getString(R.string.charging_current_wireless_summary));
            chargingCustomWireless.setUnit(getString(R.string.ma));
            chargingCustomWireless.setMax(1400);
            chargingCustomWireless.setMin(100);
            chargingCustomWireless.setOffset(10);
            chargingCustomWireless.setProgress(Battery.getChargeCustomWireless() / 10 - 10 );
            chargingCustomWireless.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
                
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setChargeControlWireless((position + 10) * 10, getActivity());
                }
            });

            WarningChargeCard.addItem(chargingCustomWireless);
        }

        if (WarningChargeCard.size() > 0) {
            items.add(WarningChargeCard);
        }
    }
        
    private void blxInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 0; i <= 100; i++) {
            list.add(String.valueOf(i));
        }

        SeekBarView blx = new SeekBarView();
        blx.setTitle(getString(R.string.blx));
        blx.setSummary(getString(R.string.blx_summary));
        blx.setItems(list);
        blx.setProgress(Battery.getBlx());
        blx.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Battery.setBlx(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(blx);
    }

    private void chargeRateInit(List<RecyclerViewItem> items) {
        CardView chargeRateCard = new CardView(getActivity());
        chargeRateCard.setTitle(getString(R.string.charge_rate));

        if (Battery.hasChargeRateEnable()) {
            SwitchView chargeRate = new SwitchView();
            chargeRate.setSummary(getString(R.string.charge_rate));
            chargeRate.setChecked(Battery.isChargeRateEnabled());
            chargeRate.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Battery.enableChargeRate(isChecked, getActivity());
                }
            });

            chargeRateCard.addItem(chargeRate);
        }

        if (Battery.hasChargingCurrent()) {
            SeekBarView chargingCurrent = new SeekBarView();
            chargingCurrent.setTitle(getString(R.string.charging_current));
            chargingCurrent.setSummary(getString(R.string.charging_current_summary));
            chargingCurrent.setUnit(getString(R.string.ma));
            chargingCurrent.setMax(1500);
            chargingCurrent.setMin(100);
            chargingCurrent.setOffset(10);
            chargingCurrent.setProgress(Battery.getChargingCurrent() / 10 - 10);
            chargingCurrent.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Battery.setChargingCurrent((position + 10) * 10, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            chargeRateCard.addItem(chargingCurrent);
        }

        if (chargeRateCard.size() > 0) {
            items.add(chargeRateCard);
        }
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            sBatteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
    };

    @Override
    protected void refresh() {
        super.refresh();
        if (mLevel != null) {
            mLevel.setStat(sBatteryLevel + "%");
        }
        if (mVoltage != null) {
            mVoltage.setStat(sBatteryVoltage + getString(R.string.mv));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mBatteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

}
