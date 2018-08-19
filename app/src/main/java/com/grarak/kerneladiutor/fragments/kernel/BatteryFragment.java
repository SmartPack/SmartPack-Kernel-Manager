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
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.StatsView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class BatteryFragment extends RecyclerViewFragment {

    private Battery mBattery;

    private StatsView mLevel;
    private StatsView mVoltage;
    private StatsView mChargingStatus;

    private int mBatteryLevel;
    private int mBatteryVoltage;

    @Override
    protected void init() {
        super.init();

        mBattery = Battery.getInstance(requireActivity());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        levelInit(items);
        voltageInit(items);
        mChargingStatus = new StatsView();
        if (Battery.haschargingstatus()) {
        items.add(mChargingStatus);
        }
        if (mBattery.hasbatterychargelimit()) {
            bclInit(items);
        }
        if (mBattery.hasBlx()) {
            blxInit(items);
        }
        if (mBattery.hasForceFastCharge()) {
            fastChargeInit(items);
        }
        if (mBattery.haschargeLevelAC()) {
            chargeLevelInit(items);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (itemsSize() > 2) {
            addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        }
        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.capacity),
                mBattery.getCapacity() + getString(R.string.mah)));
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

    private void bclInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> bcl = new ArrayList<>();

        if (mBattery.hasbatterychargelimit()) {
		SwitchView enablecharging = new SwitchView();
		enablecharging.setTitle(getString(R.string.charging_enable));
		enablecharging.setSummary(getString(R.string.charging_enable_summary));
		if (mBattery.batterychargelimitenabled()) {
			enablecharging.setChecked(mBattery.batterychargelimitenabled());
			enablecharging.addOnSwitchListener(new SwitchView.OnSwitchListener() {
			    @Override
			    public void onChanged(SwitchView switchView, boolean isChecked) {
				mBattery.enablebatterychargelimit(isChecked, getActivity());
			    }
			});
		} else if (mBattery.op5tbatterychargelimitenabled()) {
			enablecharging.setChecked(mBattery.op5tbatterychargelimitenabled());
			enablecharging.addOnSwitchListener(new SwitchView.OnSwitchListener() {
			    @Override
			    public void onChanged(SwitchView switchView, boolean isChecked) {
				mBattery.enableop5tbatterychargelimit(isChecked, getActivity());
			    }
			});
		}

		bcl.add(enablecharging);
        }

        if (bcl.size() > 0) {
            items.addAll(bcl);
        }
    }

    private void fastChargeInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> fastCharge = new ArrayList<>();

        TitleView title = new TitleView();
        title.setText(getString(R.string.acci));

	if (mBattery.hasForceFastCharge()) {
	if (Device.isOnePlusdumpling()) {
		SwitchView forceFastCharge = new SwitchView();
		forceFastCharge.setTitle(getString(R.string.fast_charge));
		forceFastCharge.setSummary(getString(R.string.usb_fast_charge_summary));
		forceFastCharge.setChecked(mBattery.isForceFastChargeEnabled());
		forceFastCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		    @Override
		    public void onChanged(SwitchView switchView, boolean isChecked) {
		        mBattery.ForceFastChargeenable(isChecked, getActivity());
		    }
		});
            fastCharge.add(forceFastCharge);

        } else {
            SelectView forceFastCharge = new SelectView();
            forceFastCharge.setTitle(getString(R.string.fast_charge));
            forceFastCharge.setSummary(getString(R.string.fast_charge_summary));
            forceFastCharge.setItems(mBattery.enableForceFastCharge(getActivity()));
            forceFastCharge.setItem(mBattery.getForceFastCharge());
            forceFastCharge.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                mBattery.setForceFastCharge(position, getActivity());
            }
        });

            fastCharge.add(forceFastCharge);
	}

    }

        if (mBattery.iscustomodenabled()) {
		if (mBattery.hasFastChargeControlAC()) {
		    SelectView ACLevelCard = new SelectView();
		    ACLevelCard.setTitle(getString(R.string.charge_level_ac));
		    ACLevelCard.setSummary(getString(R.string.charge_level_summary));
		    ACLevelCard.setItems(mBattery.getFastChargeControlAC());
		    ACLevelCard.setItem(mBattery.getFastChargeCustomAC());
		    ACLevelCard.setOnItemSelected(new SelectView.OnItemSelected() {
		    @Override
		    public void onItemSelected(SelectView selectView, int position, String item) {
		        mBattery.setFastChargeControlAC(item, getActivity());
		    }
		});
		    fastCharge.add(ACLevelCard);

	    }
		    
		if (mBattery.hasFastChargeControlUSB()) {
		    SelectView USBLevelCard = new SelectView();
		    USBLevelCard.setTitle(getString(R.string.charge_level_usb));
		    USBLevelCard.setSummary(getString(R.string.charge_level_summary));
		    USBLevelCard.setItems(mBattery.getFastChargeControlUSB());
		    USBLevelCard.setItem(mBattery.getFastChargeCustomUSB());
		    USBLevelCard.setOnItemSelected(new SelectView.OnItemSelected() {
		    @Override
		    public void onItemSelected(SelectView selectView, int position, String item) {
		        mBattery.setFastChargeControlUSB(item, getActivity());
		    }
		});
		    fastCharge.add(USBLevelCard);
	    }
	    
		if (mBattery.hasFastChargeControlWIRELESS()) {
		    SelectView WirelessLevelCard = new SelectView();
		    WirelessLevelCard.setTitle(getString(R.string.charge_level_wireless));
		    WirelessLevelCard.setSummary(getString(R.string.charge_level_summary));
		    WirelessLevelCard.setItems(mBattery.getFastChargeControlWIRELESS());
		    WirelessLevelCard.setItem(mBattery.getFastChargeCustomWIRELESS());
		    WirelessLevelCard.setOnItemSelected(new SelectView.OnItemSelected() {
		    @Override
		    public void onItemSelected(SelectView selectView, int position, String item) {
		        mBattery.setFastChargeControlWIRELESS(item, getActivity());
		   }
		});
		    fastCharge.add(WirelessLevelCard);
	    }
        }

        if (mBattery.hasMtpForceFastCharge()) {
		SwitchView MtpFastCharge = new SwitchView();
		MtpFastCharge.setTitle(getString(R.string.mtp_fast_charge));
		MtpFastCharge.setSummary(getString(R.string.mtp_fast_charge_summary));
		MtpFastCharge.setChecked(mBattery.isMtpForceFastChargeEnabled());
		MtpFastCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		    @Override
		    public void onChanged(SwitchView switchView, boolean isChecked) {
		        mBattery.enableMtpForceFastCharge(isChecked, getActivity());
		    }
		});

		    fastCharge.add(MtpFastCharge);
        }
    
        if (mBattery.hasScreenCurrentLimit()) {
		SwitchView ScreenLimit = new SwitchView();
		ScreenLimit.setTitle(getString(R.string.screen_limit));
		ScreenLimit.setSummary(getString(R.string.screen_limit_summary));
		ScreenLimit.setChecked(mBattery.isScreenCurrentLimit());
		ScreenLimit.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		    @Override
		    public void onChanged(SwitchView switchView, boolean isChecked) {
		        mBattery.enableScreenCurrentLimit(isChecked, getActivity());
		    }
		});

		    fastCharge.add(ScreenLimit);
        }

        if (fastCharge.size() > 0) {
            items.add(title);
            items.addAll(fastCharge);
        }
    }

    private void chargeLevelInit(List<RecyclerViewItem> items) {
        List<RecyclerViewItem> chargeLevel = new ArrayList<>();

        TitleView title = new TitleView();
        title.setText(getString(R.string.acci));

        DescriptionView stockchargelogic = new DescriptionView();
        stockchargelogic.setSummary(getString(R.string.stockchargelogic));

        chargeLevel.add(stockchargelogic);

        if (mBattery.haschargeLevelAC()) {
            SeekBarView chargeLevelAC = new SeekBarView();
            chargeLevelAC.setTitle(getString(R.string.charge_level_ac));
            chargeLevelAC.setUnit(getString(R.string.ma));
            chargeLevelAC.setMax(2000);
            chargeLevelAC.setOffset(25);
            chargeLevelAC.setProgress(mBattery.getchargeLevelAC() / 25 );
            chargeLevelAC.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setchargeLevelAC((position * 25), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            chargeLevel.add(chargeLevelAC);
        }

        if (mBattery.haschargeLevelUSB()) {
            SeekBarView chargeLevelUSB = new SeekBarView();
            chargeLevelUSB.setTitle(getString(R.string.charge_level_usb));
            chargeLevelUSB.setUnit(getString(R.string.ma));
            chargeLevelUSB.setMax(1600);
            chargeLevelUSB.setOffset(25);
            chargeLevelUSB.setProgress(mBattery.getchargeLevelUSB() / 25 );
            chargeLevelUSB.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setchargeLevelUSB((position * 25), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            chargeLevel.add(chargeLevelUSB);
        }

        if (mBattery.haschargeLevelWL()) {
            SeekBarView chargeLevelWL = new SeekBarView();
            chargeLevelWL.setTitle(getString(R.string.charge_level_wireless));
            chargeLevelWL.setUnit(getString(R.string.ma));
            chargeLevelWL.setMax(1600);
            chargeLevelWL.setOffset(25);
            chargeLevelWL.setProgress(mBattery.getchargeLevelWL() / 25 );
            chargeLevelWL.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mBattery.setchargeLevelWL((position * 25), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            chargeLevel.add(chargeLevelWL);
        }

        if (chargeLevel.size() > 0) {
            items.add(title);
            items.addAll(chargeLevel);
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
        blx.setProgress(mBattery.getBlx());
        blx.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                mBattery.setBlx(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(blx);
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mBatteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
    };

    @Override
    protected void refresh() {
        super.refresh();
        if (mLevel != null) {
            mLevel.setStat(mBatteryLevel + "%");
        }
        if (mVoltage != null) {
            mVoltage.setStat(mBatteryVoltage + " mV");
        }
        if (mChargingStatus != null) {
		if (Device.isOnePlusdumpling()) {
		    float chargingrate = mBattery.getchargeInfo();
		    if (mBattery.isDischarging()){
			mChargingStatus.setTitle("Charge Rate");
			mChargingStatus.setStat(0.0 + (" mA"));
		    } else {
			mChargingStatus.setTitle("Charge Rate");
			 mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
		    }
		} else {
		    float chargingrate = mBattery.getchargingstatus();
		    if (mBattery.isDischarging()){
			mChargingStatus.setTitle("Charge Rate");
			mChargingStatus.setStat(0.0 + (" mA"));
		    }
		    else {
			if (mBattery.accharge()){
		            mChargingStatus.setTitle("Charge Rate (AC)");
		            mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			} else if (mBattery.usbcharge()){
		            mChargingStatus.setTitle("Charge Rate (USB)");
		            mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			} else if (mBattery.wlcharge()){
		            mChargingStatus.setTitle("Charge Rate (Wireless)");
		            mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			} else {
		            mChargingStatus.setTitle("Charge Rate");
		            mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			}
		    }
		}
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            requireActivity().unregisterReceiver(mBatteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

}
