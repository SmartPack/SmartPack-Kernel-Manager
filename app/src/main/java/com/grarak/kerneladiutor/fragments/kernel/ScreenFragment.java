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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.screen.Calibration;
import com.grarak.kerneladiutor.utils.kernel.screen.Gamma;
import com.grarak.kerneladiutor.utils.kernel.screen.GammaProfiles;
import com.grarak.kerneladiutor.utils.kernel.screen.Misc;
import com.grarak.kerneladiutor.views.ColorTable;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DropDownView;
import com.grarak.kerneladiutor.views.recyclerview.GenericSelectView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 31.05.16.
 */
public class ScreenFragment extends RecyclerViewFragment {

    private SeekBarView mColors[];
    private SeekBarView mMinColor;

    private GenericSelectView mKGammaBlue;
    private GenericSelectView mKGammaGreen;
    private GenericSelectView mKGammaRed;

    private GenericSelectView mGammaControlRedGreys;
    private GenericSelectView mGammaControlRedMids;
    private GenericSelectView mGammaControlRedBlacks;
    private GenericSelectView mGammaControlRedWhites;
    private GenericSelectView mGammaControlGreenGreys;
    private GenericSelectView mGammaControlGreenMids;
    private GenericSelectView mGammaControlGreenBlacks;
    private GenericSelectView mGammaControlGreenWhites;
    private GenericSelectView mGammaControlBlueGreys;
    private GenericSelectView mGammaControlBlueMids;
    private GenericSelectView mGammaControlBlueBlacks;
    private GenericSelectView mGammaControlBlueWhites;
    private GenericSelectView mGammaControlContrast;
    private GenericSelectView mGammaControlBrightness;
    private GenericSelectView mGammaControlSaturation;

    private GenericSelectView mDsiPanelBlueNegative;
    private GenericSelectView mDsiPanelBluePositive;
    private GenericSelectView mDsiPanelGreenNegative;
    private GenericSelectView mDsiPanelGreenPositive;
    private GenericSelectView mDsiPanelRedNegative;
    private GenericSelectView mDsiPanelRedPositive;
    private GenericSelectView mDsiPanelWhitePoint;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new ColorTableFragment());
        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.SCREEN));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        screenColorInit(items);
        List<RecyclerViewItem> gammas = new ArrayList<>();
        if (Gamma.hasKGamma()) {
            kgammaInit(gammas);
        } else if (Gamma.hasGammaControl()) {
            gammacontrolInit(gammas);
        } else if (Gamma.hasDsiPanel()) {
            dsipanelInit(gammas);
        }
        if (gammas.size() > 0) {
            CardView gamma = new CardView(getActivity());
            gamma.setTitle(getString(R.string.gamma));
            for (RecyclerViewItem item : gammas) {
                gamma.addItem(item);
            }
            items.add(gamma);
        }
        lcdBackLightInit(items);
        backlightDimmerInit(items);
        if (Misc.hasNegativeToggle()) {
            negativeToggleInit(items);
        }
        mdnieGlobalInit(items);
        if (Misc.hasGloveMode()) {
            gloveModeInit(items);
        }
    }

    private void screenColorInit(List<RecyclerViewItem> items) {
        if (Calibration.hasColors()) {

            CardView screenColor = new CardView(getActivity());
            screenColor.setTitle(getString(R.string.screen_color));

            List<String> colors = Calibration.getColors();
            final List<String> limits = Calibration.getLimits();
            mColors = new SeekBarView[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
                mColors[i] = new SeekBarView();
                mColors[i].setTitle(getResources().getStringArray(R.array.colors)[i]);
                mColors[i].setItems(limits);
                mColors[i].setProgress(limits.indexOf(colors.get(i)));
                mColors[i].setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                        if (mMinColor != null && position < mMinColor.getProgress()) {
                            mMinColor.setProgress(position);
                        }
                    }

                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        if (mMinColor != null) {
                            int current = Utils.strToInt(Calibration.getLimits().get(position));
                            if (Calibration.getMinColor() > current) {
                                Calibration.setMinColor(current, getActivity());
                            }
                        }

                        int r = mColors[0].getProgress();
                        int g = mColors[1].getProgress();
                        int b = mColors[2].getProgress();
                        Calibration.setColors(limits.get(r) + " " + limits.get(g) + " " + limits.get(b),
                                getActivity());
                    }
                });

                screenColor.addItem(mColors[i]);
            }

            items.add(screenColor);

            if (Calibration.hasMinColor()) {
                mMinColor = new SeekBarView();
                mMinColor.setTitle(getString(R.string.min_rgb));
                mMinColor.setItems(Calibration.getLimits());
                mMinColor.setProgress(Calibration.getLimits().indexOf(String.valueOf(Calibration.getMinColor())));
                mMinColor.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                    @Override
                    public void onStop(SeekBarView seekBarView, int position, String value) {
                        Calibration.setMinColor(Utils.strToInt(value), getActivity());

                        StringBuilder colors = new StringBuilder();
                        for (String color : Calibration.getColors()) {
                            if (Utils.strToInt(value) > Utils.strToInt(color)) {
                                colors.append(value).append(" ");
                            } else {
                                colors.append(color).append(" ");
                            }
                        }
                        colors.setLength(colors.length() - 1);
                        Calibration.setColors(colors.toString(), getActivity());
                    }

                    @Override
                    public void onMove(SeekBarView seekBarView, int position, String value) {
                        for (SeekBarView color : mColors) {
                            if (position > color.getProgress()) {
                                color.setProgress(position);
                            }
                        }
                    }
                });

                items.add(mMinColor);
            }
        }

        if (Calibration.hasInvertScreen()) {
            SwitchView invertScreen = new SwitchView();
            invertScreen.setSummary(getString(R.string.invert_screen));
            invertScreen.setChecked(Calibration.isInvertScreenEnabled());
            invertScreen.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Calibration.enableInvertScreen(isChecked, getActivity());
                }
            });

            items.add(invertScreen);
        }

        if (Calibration.hasSaturationIntensity()) {
            int saturation = Calibration.getSaturationIntensity();
            final SeekBarView saturationIntensity = new SeekBarView();
            saturationIntensity.setTitle(getString(R.string.saturation_intensity));
            saturationIntensity.setMax(158);
            saturationIntensity.setProgress(saturation == 128 ? 30 : saturation - 225);
            saturationIntensity.setEnabled(saturation != 128);
            saturationIntensity.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Calibration.setSaturationIntensity(position + 225, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(saturationIntensity);

            SwitchView grayscaleMode = new SwitchView();
            grayscaleMode.setSummary(getString(R.string.grayscale_mode));
            grayscaleMode.setChecked(saturation == 128);
            grayscaleMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    saturationIntensity.setEnabled(!isChecked);
                    Calibration.enableGrayscaleMode(isChecked, getActivity());
                    if (!isChecked) {
                        saturationIntensity.setProgress(30);
                    }
                }
            });

            items.add(grayscaleMode);
        }

        if (Calibration.hasScreenHue()) {
            SeekBarView screenHue = new SeekBarView();
            screenHue.setTitle(getString(R.string.screen_hue));
            screenHue.setSummary(getString(R.string.screen_hue_summary));
            screenHue.setMax(1536);
            screenHue.setProgress(Calibration.getScreenHue());
            screenHue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Calibration.setScreenHue(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenHue);
        }

        if (Calibration.hasScreenValue()) {
            SeekBarView screenValue = new SeekBarView();
            screenValue.setTitle(getString(R.string.screen_value));
            screenValue.setMax(255);
            screenValue.setProgress(Calibration.getScreenValue() - 128);
            screenValue.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Calibration.setScreenValue(position + 128, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenValue);
        }

        if (Calibration.hasScreenContrast()) {
            SeekBarView screenContrast = new SeekBarView();
            screenContrast.setTitle(getString(R.string.screen_contrast));
            screenContrast.setMax(255);
            screenContrast.setProgress(Calibration.getScreenContrast() - 128);
            screenContrast.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Calibration.setScreenContrast(position + 128, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            items.add(screenContrast);
        }

        if (Calibration.hasScreenHBM()) {
            SwitchView screenHBM = new SwitchView();
            screenHBM.setSummary(getString(R.string.high_brightness_mode));
            screenHBM.setChecked(Calibration.isScreenHBMEnabled());
            screenHBM.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Calibration.enableScreenHBM(isChecked, getActivity());
                }
            });

            items.add(screenHBM);
        }

        if (Calibration.hasSRGB()) {
            SwitchView sRGB = new SwitchView();
            sRGB.setSummary(getString(R.string.srgb));
            sRGB.setChecked(Calibration.isSRGBEnabled());
            sRGB.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Calibration.enableSRGB(isChecked, getActivity());
                }
            });

            items.add(sRGB);
        }
    }

    private void kgammaInit(List<RecyclerViewItem> items) {
        if (mKGammaBlue == null) {
            mKGammaBlue = new GenericSelectView();
            mKGammaBlue.setSummary(getString(R.string.blue));
            mKGammaBlue.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setKGammaBlue(value, getActivity());
                    kgammaInit(null);
                }
            });
        }
        String blue = Gamma.getKGammaBlue();
        mKGammaBlue.setValue(blue);
        mKGammaBlue.setValueRaw(blue);

        if (items != null) {
            items.add(mKGammaBlue);
        }

        if (mKGammaGreen == null) {
            mKGammaGreen = new GenericSelectView();
            mKGammaGreen.setSummary(getString(R.string.green));
            mKGammaGreen.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setKGammaGreen(value, getActivity());
                    kgammaInit(null);
                }
            });
        }
        String green = Gamma.getKGammaGreen();
        mKGammaGreen.setValue(green);
        mKGammaGreen.setValueRaw(green);

        if (items != null) {
            items.add(mKGammaGreen);
        }

        if (mKGammaRed == null) {
            mKGammaRed = new GenericSelectView();
            mKGammaRed.setSummary(getString(R.string.red));
            mKGammaRed.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setKGammaRed(value, getActivity());
                    kgammaInit(null);
                }
            });
        }
        String red = Gamma.getKGammaRed();
        mKGammaRed.setValue(red);
        mKGammaRed.setValueRaw(red);

        if (items != null) {
            items.add(mKGammaRed);
        }

        if (items != null) {
            List<String> profileList = new ArrayList<>();
            final GammaProfiles.KGammaProfiles gammaProfiles = Gamma.getKGammaProfiles(getActivity());
            for (int i = 0; i < gammaProfiles.length(); i++) {
                profileList.add(gammaProfiles.getName(i));
            }

            DropDownView profiles = new DropDownView();
            profiles.setTitle(getString(R.string.profile));
            profiles.setSummary(getString(R.string.gamma_profiles_summary));
            profiles.setItems(profileList);
            profiles.setSelection(Prefs.getInt("kgamma_profile", -1, getActivity()));
            profiles.setOnDropDownListener(new DropDownView.OnDropDownListener() {
                @Override
                public void onSelect(DropDownView dropDownView, int position, String value) {
                    Gamma.setKGammaProfile(position, gammaProfiles, getActivity());
                    kgammaInit(null);
                    Prefs.saveInt("kgamma_profile", position, getActivity());
                }
            });

            items.add(profiles);
        }
    }

    private void gammacontrolInit(List<RecyclerViewItem> items) {
        if (mGammaControlRedGreys == null) {
            mGammaControlRedGreys = new GenericSelectView();
            mGammaControlRedGreys.setSummary(getString(R.string.red_greys));
            mGammaControlRedGreys.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedGreys(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redGreys = Gamma.getRedGreys();
        mGammaControlRedGreys.setValue(redGreys);
        mGammaControlRedGreys.setValueRaw(redGreys);

        if (items != null) {
            items.add(mGammaControlRedGreys);
        }

        if (mGammaControlRedMids == null) {
            mGammaControlRedMids = new GenericSelectView();
            mGammaControlRedMids.setSummary(getString(R.string.red_mids));
            mGammaControlRedMids.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedMids(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redMids = Gamma.getRedMids();
        mGammaControlRedMids.setValue(redMids);
        mGammaControlRedMids.setValueRaw(redMids);

        if (items != null) {
            items.add(mGammaControlRedMids);
        }

        if (mGammaControlRedBlacks == null) {
            mGammaControlRedBlacks = new GenericSelectView();
            mGammaControlRedBlacks.setSummary(getString(R.string.red_blacks));
            mGammaControlRedBlacks.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedBlacks(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redBlacks = Gamma.getRedBlacks();
        mGammaControlRedBlacks.setValue(redBlacks);
        mGammaControlRedBlacks.setValueRaw(redBlacks);

        if (items != null) {
            items.add(mGammaControlRedBlacks);
        }

        if (mGammaControlRedWhites == null) {
            mGammaControlRedWhites = new GenericSelectView();
            mGammaControlRedWhites.setSummary(getString(R.string.red_whites));
            mGammaControlRedWhites.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedWhites(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String redWhites = Gamma.getRedWhites();
        mGammaControlRedWhites.setValue(redWhites);
        mGammaControlRedWhites.setValueRaw(redWhites);

        if (items != null) {
            items.add(mGammaControlRedWhites);
        }

        if (mGammaControlGreenGreys == null) {
            mGammaControlGreenGreys = new GenericSelectView();
            mGammaControlGreenGreys.setSummary(getString(R.string.green_greys));
            mGammaControlGreenGreys.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenGreys(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenGreys = Gamma.getGreenGreys();
        mGammaControlGreenGreys.setValue(greenGreys);
        mGammaControlGreenGreys.setValueRaw(greenGreys);

        if (items != null) {
            items.add(mGammaControlGreenGreys);
        }

        if (mGammaControlGreenMids == null) {
            mGammaControlGreenMids = new GenericSelectView();
            mGammaControlGreenMids.setSummary(getString(R.string.green_mids));
            mGammaControlGreenMids.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenMids(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenMids = Gamma.getGreenMids();
        mGammaControlGreenMids.setValue(greenMids);
        mGammaControlGreenMids.setValueRaw(greenMids);

        if (items != null) {
            items.add(mGammaControlGreenMids);
        }

        if (mGammaControlGreenBlacks == null) {
            mGammaControlGreenBlacks = new GenericSelectView();
            mGammaControlGreenBlacks.setSummary(getString(R.string.green_blacks));
            mGammaControlGreenBlacks.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenBlacks(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenBlacks = Gamma.getGreenBlacks();
        mGammaControlGreenBlacks.setValue(greenBlacks);
        mGammaControlGreenBlacks.setValueRaw(greenBlacks);

        if (items != null) {
            items.add(mGammaControlGreenBlacks);
        }

        if (mGammaControlGreenWhites == null) {
            mGammaControlGreenWhites = new GenericSelectView();
            mGammaControlGreenWhites.setSummary(getString(R.string.green_whites));
            mGammaControlGreenWhites.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenWhites(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String greenWhites = Gamma.getGreenWhites();
        mGammaControlGreenWhites.setValue(greenWhites);
        mGammaControlGreenWhites.setValueRaw(greenWhites);

        if (items != null) {
            items.add(mGammaControlGreenWhites);
        }

        if (mGammaControlBlueGreys == null) {
            mGammaControlBlueGreys = new GenericSelectView();
            mGammaControlBlueGreys.setSummary(getString(R.string.blue_greys));
            mGammaControlBlueGreys.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueGreys(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueGreys = Gamma.getBlueGreys();
        mGammaControlBlueGreys.setValue(blueGreys);
        mGammaControlBlueGreys.setValueRaw(blueGreys);

        if (items != null) {
            items.add(mGammaControlBlueGreys);
        }

        if (mGammaControlBlueMids == null) {
            mGammaControlBlueMids = new GenericSelectView();
            mGammaControlBlueMids.setSummary(getString(R.string.blue_mids));
            mGammaControlBlueMids.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueMids(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueMids = Gamma.getBlueMids();
        mGammaControlBlueMids.setValue(blueMids);
        mGammaControlBlueMids.setValueRaw(blueMids);

        if (items != null) {
            items.add(mGammaControlBlueMids);
        }

        if (mGammaControlBlueBlacks == null) {
            mGammaControlBlueBlacks = new GenericSelectView();
            mGammaControlBlueBlacks.setSummary(getString(R.string.blue_blacks));
            mGammaControlBlueBlacks.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueBlacks(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueBlacks = Gamma.getBlueBlacks();
        mGammaControlBlueBlacks.setValue(blueBlacks);
        mGammaControlBlueBlacks.setValueRaw(blueBlacks);

        if (items != null) {
            items.add(mGammaControlBlueBlacks);
        }

        if (mGammaControlBlueWhites == null) {
            mGammaControlBlueWhites = new GenericSelectView();
            mGammaControlBlueWhites.setSummary(getString(R.string.blue_whites));
            mGammaControlBlueWhites.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueWhites(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String blueWhites = Gamma.getBlueWhites();
        mGammaControlBlueWhites.setValue(blueWhites);
        mGammaControlBlueWhites.setValueRaw(blueWhites);

        if (items != null) {
            items.add(mGammaControlBlueWhites);
        }

        if (mGammaControlContrast == null) {
            mGammaControlContrast = new GenericSelectView();
            mGammaControlContrast.setSummary(getString(R.string.contrast));
            mGammaControlContrast.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGammaContrast(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String contrast = Gamma.getGammaContrast();
        mGammaControlContrast.setValue(contrast);
        mGammaControlContrast.setValueRaw(contrast);

        if (items != null) {
            items.add(mGammaControlContrast);
        }

        if (mGammaControlBrightness == null) {
            mGammaControlBrightness = new GenericSelectView();
            mGammaControlBrightness.setSummary(getString(R.string.brightness));
            mGammaControlBrightness.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGammaBrightness(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String brightness = Gamma.getGammaBrightness();
        mGammaControlBrightness.setValue(brightness);
        mGammaControlBrightness.setValueRaw(brightness);

        if (items != null) {
            items.add(mGammaControlBrightness);
        }

        if (mGammaControlSaturation == null) {
            mGammaControlSaturation = new GenericSelectView();
            mGammaControlSaturation.setSummary(getString(R.string.saturation_intensity));
            mGammaControlSaturation.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGammaSaturation(value, getActivity());
                    gammacontrolInit(null);
                }
            });
        }

        String saturation = Gamma.getGammaSaturation();
        mGammaControlSaturation.setValue(saturation);
        mGammaControlSaturation.setValueRaw(saturation);

        if (items != null) {
            items.add(mGammaControlSaturation);
        }

        if (items != null) {
            List<String> profileList = new ArrayList<>();
            final GammaProfiles.GammaControlProfiles gammaProfiles = Gamma.getGammaControlProfiles(getActivity());
            for (int i = 0; i < gammaProfiles.length(); i++) {
                profileList.add(gammaProfiles.getName(i));
            }

            DropDownView profiles = new DropDownView();
            profiles.setTitle(getString(R.string.profile));
            profiles.setSummary(getString(R.string.gamma_profiles_summary));
            profiles.setItems(profileList);
            profiles.setSelection(Prefs.getInt("gamma_control_profile", -1, getActivity()));
            profiles.setOnDropDownListener(new DropDownView.OnDropDownListener() {
                @Override
                public void onSelect(DropDownView dropDownView, int position, String value) {
                    Gamma.setGammaControlProfile(position, gammaProfiles, getActivity());
                    gammacontrolInit(null);
                    Prefs.saveInt("gamma_control_profile", position, getActivity());
                }
            });

            items.add(profiles);
        }
    }

    private void dsipanelInit(List<RecyclerViewItem> items) {
        if (mDsiPanelBlueNegative == null) {
            mDsiPanelBlueNegative = new GenericSelectView();
            mDsiPanelBlueNegative.setSummary(getString(R.string.blue_negative));
            mDsiPanelBlueNegative.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBlueNegative(value, getActivity());
                }
            });
        }

        String blueNegative = Gamma.getBlueNegative();
        mDsiPanelBlueNegative.setValue(blueNegative);
        mDsiPanelBlueNegative.setValueRaw(blueNegative);

        if (items != null) {
            items.add(mDsiPanelBlueNegative);
        }

        if (mDsiPanelBluePositive == null) {
            mDsiPanelBluePositive = new GenericSelectView();
            mDsiPanelBluePositive.setSummary(getString(R.string.blue_positive));
            mDsiPanelBluePositive.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setBluePositive(value, getActivity());
                }
            });
        }

        String bluePositive = Gamma.getBluePositive();
        mDsiPanelBluePositive.setValue(bluePositive);
        mDsiPanelBluePositive.setValueRaw(bluePositive);

        if (items != null) {
            items.add(mDsiPanelBluePositive);
        }

        if (mDsiPanelGreenNegative == null) {
            mDsiPanelGreenNegative = new GenericSelectView();
            mDsiPanelGreenNegative.setSummary(getString(R.string.green_negative));
            mDsiPanelGreenNegative.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenNegative(value, getActivity());
                }
            });
        }

        String greenNegative = Gamma.getGreenNegative();
        mDsiPanelGreenNegative.setValue(greenNegative);
        mDsiPanelGreenNegative.setValueRaw(greenNegative);

        if (items != null) {
            items.add(mDsiPanelGreenNegative);
        }

        if (mDsiPanelGreenPositive == null) {
            mDsiPanelGreenPositive = new GenericSelectView();
            mDsiPanelGreenPositive.setSummary(getString(R.string.green_positive));
            mDsiPanelGreenPositive.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setGreenPositive(value, getActivity());
                }
            });
        }

        String greenPositive = Gamma.getGreenPositive();
        mDsiPanelGreenPositive.setValue(greenPositive);
        mDsiPanelGreenPositive.setValueRaw(greenPositive);

        if (items != null) {
            items.add(mDsiPanelGreenPositive);
        }

        if (mDsiPanelRedNegative == null) {
            mDsiPanelRedNegative = new GenericSelectView();
            mDsiPanelRedNegative.setSummary(getString(R.string.red_negative));
            mDsiPanelRedNegative.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedNegative(value, getActivity());
                }
            });
        }

        String redNegative = Gamma.getRedNegative();
        mDsiPanelRedNegative.setValue(redNegative);
        mDsiPanelRedNegative.setValueRaw(redNegative);

        if (items != null) {
            items.add(mDsiPanelRedNegative);
        }

        if (mDsiPanelRedPositive == null) {
            mDsiPanelRedPositive = new GenericSelectView();
            mDsiPanelRedPositive.setSummary(getString(R.string.red_positive));
            mDsiPanelRedPositive.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setRedPositive(value, getActivity());
                }
            });
        }

        String redPositive = Gamma.getRedPositive();
        mDsiPanelRedPositive.setValue(redPositive);
        mDsiPanelRedPositive.setValueRaw(redPositive);

        if (items != null) {
            items.add(mDsiPanelRedPositive);
        }

        if (mDsiPanelWhitePoint == null) {
            mDsiPanelWhitePoint = new GenericSelectView();
            mDsiPanelWhitePoint.setSummary(getString(R.string.white_point));
            mDsiPanelWhitePoint.setOnGenericValueListener(new GenericSelectView.OnGenericValueListener() {
                @Override
                public void onGenericValueSelected(GenericSelectView genericSelectView, String value) {
                    Gamma.setWhitePoint(value, getActivity());
                }
            });
        }

        String whitePoint = Gamma.getWhitePoint();
        mDsiPanelWhitePoint.setValue(whitePoint);
        mDsiPanelWhitePoint.setValueRaw(whitePoint);

        if (items != null) {
            items.add(mDsiPanelWhitePoint);
        }

        if (items != null) {
            List<String> profileList = new ArrayList<>();
            final GammaProfiles.DsiPanelProfiles gammaProfiles = Gamma.getDsiPanelProfiles(getActivity());
            for (int i = 0; i < gammaProfiles.length(); i++) {
                profileList.add(gammaProfiles.getName(i));
            }

            DropDownView profiles = new DropDownView();
            profiles.setTitle(getString(R.string.profile));
            profiles.setSummary(getString(R.string.gamma_profiles_summary));
            profiles.setItems(profileList);
            profiles.setSelection(Prefs.getInt("dsi_panel_profile", -1, getActivity()));
            profiles.setOnDropDownListener(new DropDownView.OnDropDownListener() {
                @Override
                public void onSelect(DropDownView dropDownView, int position, String value) {
                    Gamma.setDsiPanelProfile(position, gammaProfiles, getActivity());
                    dsipanelInit(null);
                    Prefs.saveInt("dsi_panel_profile", position, getActivity());
                }
            });

            items.add(profiles);
        }
    }

    private void lcdBackLightInit(List<RecyclerViewItem> items) {
        CardView lcdBackLightCard = new CardView(getActivity());
        lcdBackLightCard.setTitle(getString(R.string.lcd_backlight));

        if (Misc.hasBrightnessMode()) {
            SwitchView brightnessMode = new SwitchView();
            brightnessMode.setSummary(getString(R.string.brightness_mode));
            brightnessMode.setChecked(Misc.isBrightnessModeEnabled());
            brightnessMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableBrightnessMode(isChecked, getActivity());
                }
            });

            lcdBackLightCard.addItem(brightnessMode);
        }

        if (Misc.hasLcdMinBrightness()) {
            SeekBarView lcdMinBrightness = new SeekBarView();
            lcdMinBrightness.setTitle(getString(R.string.min_brightness));
            lcdMinBrightness.setSummary(getString(R.string.min_brightness_summary));
            lcdMinBrightness.setMax(114);
            lcdMinBrightness.setMin(2);
            lcdMinBrightness.setProgress(Misc.getLcdMinBrightness() - 2);
            lcdMinBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setLcdMinBrightness(position + 2, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            lcdBackLightCard.addItem(lcdMinBrightness);
        }

        if (Misc.hasLcdMaxBrightness()) {
            SeekBarView lcdMaxBrightness = new SeekBarView();
            lcdMaxBrightness.setTitle(getString(R.string.max_brightness));
            lcdMaxBrightness.setSummary(getString(R.string.max_brightness_summary));
            lcdMaxBrightness.setMax(114);
            lcdMaxBrightness.setMin(2);
            lcdMaxBrightness.setProgress(Misc.getLcdMaxBrightness() - 2);
            lcdMaxBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setLcdMaxBrightness(position + 2, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            lcdBackLightCard.addItem(lcdMaxBrightness);
        }

        if (lcdBackLightCard.size() > 0) {
            items.add(lcdBackLightCard);
        }
    }

    private void backlightDimmerInit(List<RecyclerViewItem> items) {
        CardView backLightDimmerCard = new CardView(getActivity());
        backLightDimmerCard.setTitle(getString(R.string.backlight_dimmer));

        if (Misc.hasBackLightDimmerEnable()) {
            SwitchView backLightDimmer = new SwitchView();
            backLightDimmer.setSummary(getString(R.string.backlight_dimmer));
            backLightDimmer.setChecked(Misc.isBackLightDimmerEnabled());
            backLightDimmer.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableBackLightDimmer(isChecked, getActivity());
                }
            });

            backLightDimmerCard.addItem(backLightDimmer);
        }

        if (Misc.hasMinBrightness()) {
            SeekBarView minBrightness = new SeekBarView();
            minBrightness.setTitle(getString(R.string.min_brightness));
            minBrightness.setSummary(getString(R.string.min_brightness_summary));
            minBrightness.setMax(Misc.getMaxMinBrightness());
            minBrightness.setProgress(Misc.getCurMinBrightness());
            minBrightness.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setMinBrightness(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            backLightDimmerCard.addItem(minBrightness);
        }

        if (Misc.hasBackLightDimmerThreshold()) {
            SeekBarView threshold = new SeekBarView();
            threshold.setTitle(getString(R.string.threshold));
            threshold.setMax(50);
            threshold.setProgress(Misc.getBackLightDimmerThreshold());
            threshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setBackLightDimmerThreshold(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            backLightDimmerCard.addItem(threshold);
        }

        if (Misc.hasBackLightDimmerOffset()) {
            SeekBarView dimmerOffset = new SeekBarView();
            dimmerOffset.setTitle(getString(R.string.offset));
            dimmerOffset.setMax(50);
            dimmerOffset.setProgress(Misc.getBackLightDimmerOffset());
            dimmerOffset.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    Misc.setBackLightDimmerOffset(position, getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            backLightDimmerCard.addItem(dimmerOffset);
        }

        if (backLightDimmerCard.size() > 0) {
            items.add(backLightDimmerCard);
        }
    }

    private void negativeToggleInit(List<RecyclerViewItem> items) {
        SwitchView negative = new SwitchView();
        negative.setTitle(getString(R.string.negative_toggle));
        negative.setSummary(getString(R.string.negative_toggle_summary));
        negative.setChecked(Misc.isNegativeToggleEnabled());
        negative.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableNegativeToggle(isChecked, getActivity());
            }
        });

        items.add(negative);
    }

    private void mdnieGlobalInit(List<RecyclerViewItem> items) {
        CardView mdnieCard = new CardView(getActivity());
        mdnieCard.setTitle(getString(R.string.mdnie_global_controls));

        if (Misc.hasRegisterHook()) {
            SwitchView registerHook = new SwitchView();
            registerHook.setTitle(getString(R.string.register_hook));
            registerHook.setSummary(getString(R.string.register_hook_summary));
            registerHook.setChecked(Misc.isRegisterHookEnabled());
            registerHook.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableRegisterHook(isChecked, getActivity());
                }
            });

            mdnieCard.addItem(registerHook);
        }

        if (Misc.hasMasterSequence()) {
            SwitchView masterSequence = new SwitchView();
            masterSequence.setTitle(getString(R.string.master_sequence));
            masterSequence.setSummary(getString(R.string.master_sequence_summary));
            masterSequence.setChecked(Misc.isMasterSequenceEnable());
            masterSequence.addOnSwitchListener(new SwitchView.OnSwitchListener() {
                @Override
                public void onChanged(SwitchView switchView, boolean isChecked) {
                    Misc.enableMasterSequence(isChecked, getActivity());
                }
            });

            mdnieCard.addItem(masterSequence);
        }

        if (mdnieCard.size() > 0) {
            items.add(mdnieCard);
        }
    }

    private void gloveModeInit(List<RecyclerViewItem> items) {
        SwitchView glove = new SwitchView();
        glove.setTitle(getString(R.string.glove_mode));
        glove.setSummary(getString(R.string.glove_mode_summary));
        glove.setChecked(Misc.isGloveModeEnabled());
        glove.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Misc.enableGloveMode(isChecked, getActivity());
            }
        });

        items.add(glove);
    }

    public static class ColorTableFragment extends BaseFragment {
        @Override
        protected boolean retainInstance() {
            return false;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return new ColorTable(getActivity());
        }
    }

}
