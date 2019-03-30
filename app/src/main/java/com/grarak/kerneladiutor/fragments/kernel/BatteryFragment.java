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

import android.text.InputType;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.DescriptionFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.battery.Battery;
import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
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

    private StatsView mBatteryInfo;
    private StatsView mChargingStatus;

    @Override
    protected void init() {
        super.init();

        mBattery = Battery.getInstance(requireActivity());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        mBatteryInfo = new StatsView();
        if (Battery.hasBatteryLevel() || Battery.hasBatteryVoltage() || Battery.hasBatteryHealth()) {
            items.add(mBatteryInfo);
        }
        mChargingStatus = new StatsView();
        if (Battery.haschargingstatus()) {
            items.add(mChargingStatus);
        }
        if (mBattery.hasbatterychargelimit() || mBattery.hasFastCharge() || mBattery.haschargeLevel() || mBattery.hasBlx() || mBattery.hasOPOTGSwitch() || mBattery.hasThunderCharge()) {
            acciInit(items);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
        addViewPagerFragment(DescriptionFragment.newInstance(getString(R.string.capacity),
                mBattery.getCapacity() + getString(R.string.mah)));
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
            if (mBattery.hasForceFastCharge() || mBattery.hasUSBFastCharge()) {
                SwitchView forceFastCharge = new SwitchView();
                forceFastCharge.setTitle(getString(R.string.fast_charge));
                forceFastCharge.setSummary(getString(R.string.usb_fast_charge_summary));
		if (mBattery.hasForceFastCharge()) {
		    forceFastCharge.setChecked(mBattery.isForceFastChargeEnabled());
		} else {
		    forceFastCharge.setChecked(mBattery.isUSBFastChargeEnabled());
		}
                forceFastCharge.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		    @Override
		    public void onChanged(SwitchView switchView, boolean isChecked) {
			if (mBattery.hasForceFastCharge()) {
			    mBattery.ForceFastChargeenable(isChecked, getActivity());
			} else {
			    mBattery.USBFastChargeenable(isChecked, getActivity());
			}
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
		    getHandler().postDelayed(() -> {
		    ACLevelCard.setItem(mBattery.getFastChargeCustomAC());
		    },
	        500);
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
		    getHandler().postDelayed(() -> {
		    USBLevelCard.setItem(mBattery.getFastChargeCustomUSB());
		    },
	        500);
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
		    getHandler().postDelayed(() -> {
		    WirelessLevelCard.setItem(mBattery.getFastChargeCustomWIRELESS());
		    },
	        500);
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
		    getHandler().postDelayed(() -> {
		    chargeLevelAC.setProgress(mBattery.getchargeLevelAC() / 25 );
		    },
	        500);
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
		    getHandler().postDelayed(() -> {
		    chargeLevelUSB.setProgress(mBattery.getchargeLevelUSB() / 25 );
		    },
	        500);
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
		    getHandler().postDelayed(() -> {
		    chargeLevelWL.setProgress(mBattery.getchargeLevelWL() / 25 );
		    },
	        500);
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

	if (mBattery.hasOPOTGSwitch()) {
            SwitchView OnePlusOTG = new SwitchView();
            OnePlusOTG.setTitle(getString(R.string.otg_enable));
            OnePlusOTG.setSummary(getString(R.string.otg_enable_summary));
            OnePlusOTG.setChecked(mBattery.isOPOTGEnabled());
            OnePlusOTG.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
		    mBattery.OPOTGenable(isChecked, getActivity());
		}
            });
            acci.addItem(OnePlusOTG);
	}

        if (mBattery.hasThunderChargeEnable()) {
            SwitchView enable = new SwitchView();
	    enable.setTitle(getString(R.string.thunder_charge));
            enable.setSummary(getString(R.string.thunder_charge_summary));
            enable.setChecked(mBattery.isThunderChargeEnabled());
            enable.addOnSwitchListener(new SwitchView.OnSwitchListener() {
		@Override
		public void onChanged(SwitchView switchView, boolean isChecked) {
		    mBattery.enableThunderCharge(isChecked, getActivity());
		}
            });

            acci.addItem(enable);
        }

	if (mBattery.hasThunderChargeAC()) {
            GenericSelectView acharge = new GenericSelectView();
            acharge.setTitle(getString(R.string.charge_level_ac));
            acharge.setSummary(getString(R.string.charge_level_summary));
            acharge.setValue(mBattery.getThunderChargeAC());
            acharge.setInputType(InputType.TYPE_CLASS_NUMBER);
            acharge.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mBattery.setThunderChargeAC(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    acharge.setValue(mBattery.getThunderChargeAC());
		    },
	        500);
                }
            });

            acci.addItem(acharge);
	}

	if (mBattery.hasThunderChargeUSB()) {
            GenericSelectView usbcharge = new GenericSelectView();
            usbcharge.setTitle(getString(R.string.charge_level_usb));
            usbcharge.setSummary(getString(R.string.charge_level_summary));
            usbcharge.setValue(mBattery.getThunderChargeUSB());
            usbcharge.setInputType(InputType.TYPE_CLASS_NUMBER);
            usbcharge.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    mBattery.setThunderChargeUSB(value, getActivity());
                    genericSelectView.setValue(value);
		    getHandler().postDelayed(() -> {
		    usbcharge.setValue(mBattery.getThunderChargeUSB());
		    },
	        500);
                }
            });

            acci.addItem(usbcharge);
	}

        if (acci.size() > 0) {
            items.add(acci);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (mChargingStatus != null) {
	    float chargingrate = Battery.getchargingstatus();
	    if (Battery.isDischarging()){		
		mChargingStatus.setTitle("Discharge Rate");
		if (chargingrate >= 10000) {
		    mChargingStatus.setStat(String.valueOf((chargingrate / 1000) * -1) + (" mA"));
		} else if (chargingrate <= 0) {
		    mChargingStatus.setStat(String.valueOf(chargingrate / 1000) + (" mA"));
		} else {
		    mChargingStatus.setStat(String.valueOf(chargingrate * -1) + (" mA"));
		}		
	    } else if (chargingrate >= 10000) {
		if (Battery.isACCharging()) {
		    mChargingStatus.setTitle("Charge Rate (AC)");
		} else if (Battery.isUSBCharging()) {
		    mChargingStatus.setTitle("Charge Rate (USB)");
		} else {
		    mChargingStatus.setTitle("Charge Rate");
		}
		mChargingStatus.setStat(String.valueOf(chargingrate / 1000) + (" mA"));
	    } else if (chargingrate <= 0) {
		if (Battery.isDASHCharging()) {
		    mChargingStatus.setTitle("Charge Rate (Dash)");
		} else if (Battery.isACCharging()) {
		    mChargingStatus.setTitle("Charge Rate (AC)");
		} else if (Battery.isUSBCharging()) {
		    mChargingStatus.setTitle("Charge Rate (USB)");
		} else {
		    mChargingStatus.setTitle("Charge Rate");
		}
		mChargingStatus.setStat(String.valueOf((chargingrate / 1000) * -1) + (" mA"));
	    } else {
		if (Battery.ChargingType() == 3) {
		    mChargingStatus.setTitle("Charge Rate (AC)");
		} else if (Battery.ChargingType() == 4) {
		    mChargingStatus.setTitle("Charge Rate (USB)");
		} else if (Battery.ChargingType() == 10) {
		    mChargingStatus.setTitle("Charge Rate (Wireless)");
		} else {
		    mChargingStatus.setTitle("Charge Rate");
		}
		mChargingStatus.setStat(String.valueOf(chargingrate) + (" mA"));
	    }
	}
        if (mBatteryInfo != null) {
	    float level = Battery.BatteryLevel();
	    float voltage = Battery.BatteryVoltage();
	    if (Battery.hasBatteryHealth()) {
	    	mBatteryInfo.setTitle(getString(R.string.battery) + (" (Health: ") + (Battery.BatteryHealth()) + (")"));
	    } else {
	    	mBatteryInfo.setTitle(getString(R.string.battery));
	    }
	    if (Battery.hasBatteryLevel() && Battery.hasBatteryVoltage()) {
	    	mBatteryInfo.setStat(("LEVEL: ") + String.valueOf(level).replace(".0", "") + (" %  -  VOLTAGE: ") + String.valueOf(voltage / 1000) + (" mV"));
	    } else if (Battery.hasBatteryLevel() && !(Battery.hasBatteryVoltage())) {
	    	mBatteryInfo.setStat(("LEVEL: ") + String.valueOf(level).replace(".0", "") + (" %"));
	    } else if (!(Battery.hasBatteryLevel()) && Battery.hasBatteryVoltage()) {
	    	mBatteryInfo.setStat(("VOLTAGE: ") + String.valueOf(voltage / 1000) + (" mV"));
	    }
	}
    }

}
