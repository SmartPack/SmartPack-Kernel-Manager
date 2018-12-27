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
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
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
        if (mBattery.hasbatterychargelimit() || mBattery.hasFastCharge() || mBattery.haschargeLevel() || mBattery.hasBlx()) {
            acciInit(items);
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

    private void acciInit(List<RecyclerViewItem> items) {
        CardView acci = new CardView(getActivity());
        acci.setTitle(getString(R.string.acci));

        if (mBattery.hasbatterychargelimit()) {
            SwitchView enablecharging = new SwitchView();
            enablecharging.setTitle(getString(R.string.charging_enable));
            enablecharging.setSummary(getString(R.string.charging_enable_summary));
            enablecharging.setChecked(mBattery.batterychargelimitenabled());
            enablecharging.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
			mBattery.enablebatterychargelimit(isChecked, getActivity());
		}
            });

            acci.addItem(enablecharging);
        }

	if ((mBattery.hasForceFastCharge()) && (mBattery.hasFastChargeControlAC()) && (mBattery.hasFastChargeControlUSB())) {
            // Initialize Advanced Charge Control Interface 
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
            acci.addItem(forceFastCharge);

        } else {
            // Initialize (only) USB Fast Charge
            if (mBattery.hasForceFastCharge()) {
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
                acci.addItem(forceFastCharge);
            }
	}

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
            acci.addItem(ACLevelCard);

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
            acci.addItem(USBLevelCard);
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
            acci.addItem(WirelessLevelCard);
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
            acci.addItem(MtpFastCharge);
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
            acci.addItem(ScreenLimit);
	}

        if (mBattery.haschargeLevelAC() || mBattery.haschargeLevelUSB() || mBattery.haschargeLevelWL()) {
            DescriptionView stockchargelogic = new DescriptionView();
            stockchargelogic.setTitle(("(") + getString(R.string.stockchargelogic) + (")"));
            acci.addItem(stockchargelogic);
        }

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

            acci.addItem(chargeLevelAC);
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

            acci.addItem(chargeLevelUSB);
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

            acci.addItem(chargeLevelWL);
        }

	if (mBattery.hasBlx()) {
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

            acci.addItem(blx);
	}

        if (acci.size() > 0) {
            items.add(acci);
        }
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
		if (mBattery.isDischarging()){
			mChargingStatus.setTitle("Charge Rate");
			mChargingStatus.setStat(0.0 + (" mA"));
		} else if (mBattery.getchargingstatus() >= 10000) {
			float chargingrate = (mBattery.getchargingstatus() / 1000);
			if (mBattery.isACCharging()) {
				mChargingStatus.setTitle("Charge Rate (AC)");
			} else if (mBattery.isUSBCharging()) {
				mChargingStatus.setTitle("Charge Rate (USB)");
			} else {
				mChargingStatus.setTitle("Charge Rate");
			}
			mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
		} else if (mBattery.getchargingstatus() <= 0) {
			float chargingrate = ((mBattery.getchargingstatus() / 1000) * -1);
			if (mBattery.isDASHCharging()) {
				mChargingStatus.setTitle("Charge Rate (Dash)");
			} else if (mBattery.isACCharging()) {
				mChargingStatus.setTitle("Charge Rate (AC)");
			} else if (mBattery.isUSBCharging()) {
				mChargingStatus.setTitle("Charge Rate (USB)");
			} else {
				mChargingStatus.setTitle("Charge Rate");
			}
			mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
		} else {
			float chargingrate = mBattery.getchargingstatus();
			if (mBattery.ChargingType() == 3) {
				mChargingStatus.setTitle("Charge Rate (AC)");
				mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			} else if (mBattery.ChargingType() == 4) {
				mChargingStatus.setTitle("Charge Rate (USB)");
				mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			} else if (mBattery.ChargingType() == 10) {
				mChargingStatus.setTitle("Charge Rate (Wireless)");
				mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
			} else {
				mChargingStatus.setTitle("Charge Rate");
				mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
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
