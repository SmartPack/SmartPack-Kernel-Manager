package com.grarak.kerneladiutor.fragments.kernel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.CheckBoxCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.Battery;

/**
 * Created by willi on 03.01.15.
 */
public class BatteryFragment extends RecyclerViewFragment implements CheckBoxCardItem.DCheckBoxCard.OnDCheckBoxCardListener {

    private CardViewItem.DCardView mBatteryLevelCard, mBatteryVoltageCard, mBatteryTemperature;

    private CheckBoxCardItem.DCheckBoxCard mForceFastChargeCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        batteryLevelInit();
        batteryVoltageInit();
        batteryTemperatureInit();
        if (Battery.hasForceFastCharge()) forceFastChargeInit();

        getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void batteryLevelInit() {
        mBatteryLevelCard = new CardViewItem.DCardView();
        mBatteryLevelCard.setTitle(getString(R.string.battery_level));

        addView(mBatteryLevelCard);
    }

    private void batteryVoltageInit() {
        mBatteryVoltageCard = new CardViewItem.DCardView();
        mBatteryVoltageCard.setTitle(getString(R.string.battery_voltage));

        addView(mBatteryVoltageCard);
    }

    private void batteryTemperatureInit() {
        mBatteryTemperature = new CardViewItem.DCardView();
        mBatteryTemperature.setTitle(getString(R.string.battery_temperature));

        addView(mBatteryTemperature);
    }

    private void forceFastChargeInit() {
        mForceFastChargeCard = new CheckBoxCardItem.DCheckBoxCard();
        mForceFastChargeCard.setTitle(getString(R.string.usb_fast_charge));
        mForceFastChargeCard.setDescription(getString(R.string.usb_fast_charge_summary));
        mForceFastChargeCard.setChecked(Battery.isForceFastChargeActive());
        mForceFastChargeCard.setOnDCheckBoxCardListener(this);

        addView(mForceFastChargeCard);
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            if (mBatteryLevelCard != null) mBatteryLevelCard.setDescription(level + "%");
            if (mBatteryVoltageCard != null)
                mBatteryVoltageCard.setDescription(voltage + getString(R.string.mv));
            if (mBatteryTemperature != null)
                mBatteryTemperature.setDescription((float) temperature / 10 + "°C");
        }
    };

    @Override
    public void onChecked(CheckBoxCardItem.DCheckBoxCard dCheckBoxCard, boolean checked) {
        if (dCheckBoxCard == mForceFastChargeCard)
            Battery.activateForceFastCharge(checked, getActivity());
    }

}
