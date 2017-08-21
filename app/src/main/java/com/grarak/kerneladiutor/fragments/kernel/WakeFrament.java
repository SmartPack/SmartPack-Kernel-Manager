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

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.wake.Dt2s;
import com.grarak.kerneladiutor.utils.kernel.wake.Dt2w;
import com.grarak.kerneladiutor.utils.kernel.wake.Gestures;
import com.grarak.kerneladiutor.utils.kernel.wake.Misc;
import com.grarak.kerneladiutor.utils.kernel.wake.S2s;
import com.grarak.kerneladiutor.utils.kernel.wake.S2w;
import com.grarak.kerneladiutor.utils.kernel.wake.T2w;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 23.06.16.
 */
public class WakeFrament extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Dt2w.supported()) {
            dt2wInit(items);
        }
        s2wInit(items);
        if (T2w.supported()) {
            t2wInit(items);
        }
        if (Dt2s.supported()) {
            dt2sInit(items);
        }
        if (S2s.supported()) {
            s2sInit(items);
        }
        if (Misc.hasWake()) {
            wakeMiscInit(items);
        }
        if (Gestures.supported()) {
            gestureInit(items);
        }
        if (Misc.hasCamera()) {
            cameraInit(items);
        }
        if (Misc.hasPocket()) {
            pocketInit(items);
        }
        if (Misc.hasTimeout()) {
            timeoutInit(items);
        }
        if (Misc.hasChargeTimeout()) {
            chargetimeoutInit(items);
        }
        if (Misc.hasPowerKeySuspend()) {
            powerKeySuspendInit(items);
        }
        if (Misc.hasKeyPowerMode()) {
            keyPowerModeInit(items);
        }
        if (Misc.hasChargingMode()) {
            chargingModeInit(items);
        }
        areaInit(items);
        vibrationInit(items);
    }

    private void dt2wInit(List<RecyclerViewItem> items) {
        SelectView dt2w = new SelectView();
        dt2w.setTitle(getString(R.string.dt2w));
        dt2w.setSummary(getString(R.string.dt2w_summary));
        dt2w.setItems(Dt2w.getMenu(getActivity()));
        dt2w.setItem(Dt2w.get());
        dt2w.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Dt2w.set(position, getActivity());
            }
        });

        items.add(dt2w);
    }

    private void s2wInit(List<RecyclerViewItem> items) {
        if (S2w.supported()) {
            SelectView s2w = new SelectView();
            s2w.setTitle(getString(R.string.s2w));
            s2w.setSummary(getString(R.string.s2w_summary));
            s2w.setItems(S2w.getMenu(getActivity()));
            s2w.setItem(S2w.get());
            s2w.setOnItemSelected(new SelectView.OnItemSelected() {
                @Override
                public void onItemSelected(SelectView selectView, int position, String item) {
                    S2w.set(position, getActivity());
                }
            });

            items.add(s2w);
        }

        if (S2w.hasLenient()) {
            SwitchView lenient = new SwitchView();
            lenient.setTitle(getString(R.string.lenient));
            lenient.setSummary(getString(R.string.lenient_summary));
            lenient.setChecked(S2w.isLenientEnabled());
            lenient.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    S2w.enableLenient(isChecked, getActivity());
                }
            });

            items.add(lenient);
        }
    }

    private void t2wInit(List<RecyclerViewItem> items) {
        SelectView t2w = new SelectView();
        t2w.setTitle(getString(R.string.t2w));
        t2w.setSummary(getString(R.string.t2w_summary));
        t2w.setItems(T2w.getMenu(getActivity()));
        t2w.setItem(T2w.get());
        t2w.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                T2w.set(position, getActivity());
            }
        });

        items.add(t2w);
    }

    private void dt2sInit(List<RecyclerViewItem> items) {
        SelectView dt2s = new SelectView();
        dt2s.setTitle(getString(R.string.dt2s));
        dt2s.setSummary(getString(R.string.dt2s_summary));
        dt2s.setItems(Dt2s.getMenu(getActivity()));
        dt2s.setItem(Dt2s.get());
        dt2s.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Dt2s.set(position, getActivity());
            }
        });

        items.add(dt2s);
    }

    private void s2sInit(List<RecyclerViewItem> items) {
        SelectView s2s = new SelectView();
        s2s.setTitle(getString(R.string.s2s));
        s2s.setSummary(getString(R.string.s2s_summary));
        s2s.setItems(S2s.getMenu(getActivity()));
        s2s.setItem(S2s.get());
        s2s.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                S2s.set(position, getActivity());
            }
        });

        items.add(s2s);
    }

    private void wakeMiscInit(List<RecyclerViewItem> items) {
        SelectView wake = new SelectView();
        wake.setSummary(getString(R.string.wake));
        wake.setItems(Misc.getWakeMenu(getActivity()));
        wake.setItem(Misc.getWake());
        wake.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Misc.setWake(position, getActivity());
            }
        });

        items.add(wake);
    }

    private void gestureInit(List<RecyclerViewItem> items) {
        List<String> gestures = Gestures.getMenu(getActivity());
        for (int i = 0; i < gestures.size(); i++) {
            SwitchView gesture = new SwitchView();
            gesture.setSummary(gestures.get(i));
            gesture.setChecked(Gestures.isEnabled(i));

            final int position = i;
            gesture.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Gestures.enable(isChecked, position, getActivity());
                }
            });

            items.add(gesture);
        }
    }

    private void cameraInit(List<RecyclerViewItem> items) {
        SwitchView camera = new SwitchView();
        camera.setTitle(getString(R.string.camera_gesture));
        camera.setSummary(getString(R.string.camera_gesture_summary));
        camera.setChecked(Misc.isCameraEnabled());
        camera.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableCamera(isChecked, getActivity());
            }
        });

        items.add(camera);
    }

    private void pocketInit(List<RecyclerViewItem> items) {
        SwitchView pocket = new SwitchView();
        pocket.setTitle(getString(R.string.pocket_mode));
        pocket.setSummary(getString(R.string.pocket_mode_summary));
        pocket.setChecked(Misc.isPocketEnabled());
        pocket.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enablePocket(isChecked, getActivity());
            }
        });

        items.add(pocket);
    }

    private void timeoutInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i <= Misc.getTimeoutMax(); i++)
            list.add(i + getString(R.string.min));

        SeekBarView timeout = new SeekBarView();
        timeout.setTitle(getString(R.string.timeout));
        timeout.setSummary(getString(R.string.timeout_summary));
        timeout.setItems(list);
        timeout.setProgress(Misc.getTimeout());
        timeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Misc.setTimeout(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(timeout);
    }

    private void chargetimeoutInit(List<RecyclerViewItem> items) {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.disabled));
        for (int i = 1; i <= 60; i++) {
            list.add(i + getString(R.string.min));
        }

        SeekBarView chargetimeout = new SeekBarView();
        chargetimeout.setTitle(getString(R.string.charge_timeout));
        chargetimeout.setSummary(getString(R.string.charge_timeout_summary));
        chargetimeout.setItems(list);
        chargetimeout.setProgress(Misc.getChargeTimeout());
        chargetimeout.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Misc.setChargeTimeout(position, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(chargetimeout);
    }

    private void powerKeySuspendInit(List<RecyclerViewItem> items) {
        SwitchView powerKeySuspend = new SwitchView();
        powerKeySuspend.setTitle(getString(R.string.power_key_suspend));
        powerKeySuspend.setSummary(getString(R.string.power_key_suspend_summary));
        powerKeySuspend.setChecked(Misc.isPowerKeySuspendEnabled());
        powerKeySuspend.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enablePowerKeySuspend(isChecked, getActivity());
            }
        });

        items.add(powerKeySuspend);
    }

    private void keyPowerModeInit(List<RecyclerViewItem> items) {
        SwitchView keyPowerMode = new SwitchView();
        keyPowerMode.setTitle(getString(R.string.key_power_mode));
        keyPowerMode.setSummary(getString(R.string.key_power_mode_summary));
        keyPowerMode.setChecked(Misc.isKeyPowerModeEnabled());
        keyPowerMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableKeyPowerMode(isChecked, getActivity());
            }
        });

        items.add(keyPowerMode);
    }

    private void chargingModeInit(List<RecyclerViewItem> items) {
        SwitchView chargingMode = new SwitchView();
        chargingMode.setTitle(getString(R.string.charging_mode));
        chargingMode.setSummary(getString(R.string.charging_mode_summary));
        chargingMode.setChecked(Misc.isChargingModeEnabled());
        chargingMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableChargingMode(isChecked, getActivity());
            }
        });

        items.add(chargingMode);
    }

    private void areaInit(List<RecyclerViewItem> items) {
        CardView areaCard = new CardView(getActivity());
        areaCard.setTitle(getString(R.string.area));

        if (Dt2s.hasWidth()) {
            final int w = getResources().getDisplayMetrics().widthPixels;
            SeekBarView width = new SeekBarView();
            width.setTitle(getString(R.string.width));
            width.setUnit(getString(R.string.px));
            width.setMax(w);
            width.setMin(Math.round(w / 28.8f));
            width.setProgress(Dt2s.getWidth() - Math.round(w / 28.8f));
            width.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Dt2s.setWidth(position + Math.round(w / 28.8f), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            areaCard.addItem(width);
        }

        if (Dt2s.hasHeight()) {
            final int h = getResources().getDisplayMetrics().heightPixels;
            SeekBarView height = new SeekBarView();
            height.setTitle(getString(R.string.height));
            height.setUnit(getString(R.string.px));
            height.setMax(h);
            height.setMin(Math.round(h / 51.2f));
            height.setProgress(Dt2s.getHeight() - Math.round(h / 51.2f));
            height.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Dt2s.setHeight(position + Math.round(h / 51.2f), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            areaCard.addItem(height);
        }

        if (areaCard.size() > 0) {
            items.add(areaCard);
        }
    }

    private void vibrationInit(List<RecyclerViewItem> items) {
        if (Misc.hasVibration()) {
            SwitchView vibration = new SwitchView();
            vibration.setSummary(getString(R.string.vibration));
            vibration.setChecked(Misc.isVibrationEnabled());
            vibration.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableVibration(isChecked, getActivity());
                }
            });

            items.add(vibration);
        }

        if (Misc.hasVibVibration()) {
            SeekBarView vibVibration = new SeekBarView();
            vibVibration.setTitle(getString(R.string.vibration_strength));
            vibVibration.setUnit("%");
            vibVibration.setMax(90);
            vibVibration.setProgress(Misc.getVibVibration());
            vibVibration.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setVibVibration(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(vibVibration);
        }
    }

}
