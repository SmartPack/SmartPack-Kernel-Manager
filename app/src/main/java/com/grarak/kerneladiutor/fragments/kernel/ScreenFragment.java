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

package com.grarak.kerneladiutor.fragments.kernel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.DividerCardView;
import com.grarak.kerneladiutor.elements.EditTextCardView;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.elements.SwitchCompatCardItem;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.GammaProfiles;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.WebpageReader;
import com.grarak.kerneladiutor.utils.kernel.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ScreenFragment extends RecyclerViewFragment implements SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener,
        SwitchCompatCardItem.DSwitchCompatCard.OnDSwitchCompatCardListener, EditTextCardView.DEditTextCard.OnDEditTextCardListener,
        PopupCardItem.DPopupCard.OnDPopupCardListener, CardViewItem.DCardView.OnDCardListener {

    private ImageView mPreviewIconView;

    private List<String> mColorCalibrationLimits;
    private SeekBarCardView.DSeekBarCardView[] mColorCalibrationCard;
    private SeekBarCardView.DSeekBarCardView mColorCalibrationMinCard;
    private SwitchCompatCardItem.DSwitchCompatCard mInvertScreenCard;
    private SeekBarCardView.DSeekBarCardView mSaturationIntensityCard;
    private SwitchCompatCardItem.DSwitchCompatCard mGrayscaleModeCard;
    private SeekBarCardView.DSeekBarCardView mScreenHueCard;
    private SeekBarCardView.DSeekBarCardView mScreenValueCard;
    private SeekBarCardView.DSeekBarCardView mScreenContrastCard;
    private SwitchCompatCardItem.DSwitchCompatCard mScreenHBMCard;

    private EditTextCardView.DEditTextCard mKGammaBlueCard;
    private EditTextCardView.DEditTextCard mKGammaGreenCard;
    private EditTextCardView.DEditTextCard mKGammaRedCard;
    private PopupCardItem.DPopupCard mKGammaProfilesCard;

    private EditTextCardView.DEditTextCard mGammaControlRedGreysCard;
    private EditTextCardView.DEditTextCard mGammaControlRedMidsCard;
    private EditTextCardView.DEditTextCard mGammaControlRedBlacksCard;
    private EditTextCardView.DEditTextCard mGammaControlRedWhitesCard;
    private EditTextCardView.DEditTextCard mGammaControlGreenGreysCard;
    private EditTextCardView.DEditTextCard mGammaControlGreenMidsCard;
    private EditTextCardView.DEditTextCard mGammaControlGreenBlacksCard;
    private EditTextCardView.DEditTextCard mGammaControlGreenWhitesCard;
    private EditTextCardView.DEditTextCard mGammaControlBlueGreysCard;
    private EditTextCardView.DEditTextCard mGammaControlBlueMidsCard;
    private EditTextCardView.DEditTextCard mGammaControlBlueBlacksCard;
    private EditTextCardView.DEditTextCard mGammaControlBlueWhitesCard;
    private EditTextCardView.DEditTextCard mGammaControlContrastCard;
    private EditTextCardView.DEditTextCard mGammaControlBrightnessCard;
    private EditTextCardView.DEditTextCard mGammaControlSaturationCard;
    private PopupCardItem.DPopupCard mGammaControlProfilesCard;

    private EditTextCardView.DEditTextCard mDsiPanelBlueNegativeCard;
    private EditTextCardView.DEditTextCard mDsiPanelBluePositiveCard;
    private EditTextCardView.DEditTextCard mDsiPanelGreenNegativeCard;
    private EditTextCardView.DEditTextCard mDsiPanelGreenPositiveCard;
    private EditTextCardView.DEditTextCard mDsiPanelRedNegativeCard;
    private EditTextCardView.DEditTextCard mDsiPanelRedPositiveCard;
    private EditTextCardView.DEditTextCard mDsiPanelWhitePointCard;
    private PopupCardItem.DPopupCard mDsiPanelProfilesCard;

    private CardViewItem.DCardView mAdditionalProfilesCard;

    private SwitchCompatCardItem.DSwitchCompatCard mBackLightDimmerEnableCard;
    private SeekBarCardView.DSeekBarCardView mMinBrightnessCard;
    private SeekBarCardView.DSeekBarCardView mBackLightDimmerThresholdCard;
    private SeekBarCardView.DSeekBarCardView mBackLightDimmerOffsetCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        screenColorInit();
        backlightDimmerInit();
        if (Screen.hasKGamma()) kgammaInit();
        if (Screen.hasGammaControl()) gammacontrolInit();
        if (Screen.hasDsiPanel()) dsipanelInit();
        if (mKGammaProfilesCard != null || mGammaControlProfilesCard != null || mDsiPanelProfilesCard != null)
            additionalProfilesInit();
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);

        mPreviewIconView = new ImageView(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().density * 150));
        mPreviewIconView.setLayoutParams(layoutParams);
        mPreviewIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void screenColorInit() {
        if (Screen.hasColorCalibration()) {
            mPreviewIconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_color_preview));
            CardViewItem.DCardView mPreviewIconCard = new CardViewItem.DCardView();
            mPreviewIconCard.setView(mPreviewIconView);

            addView(mPreviewIconCard);

            List<String> colors = Screen.getColorCalibration();
            mColorCalibrationLimits = Screen.getColorCalibrationLimits();
            mColorCalibrationCard = new SeekBarCardView.DSeekBarCardView[colors.size()];
            for (int i = 0; i < mColorCalibrationCard.length; i++) {
                mColorCalibrationCard[i] = new SeekBarCardView.DSeekBarCardView(Screen.getColorCalibrationLimits());
                mColorCalibrationCard[i].setTitle(getColor(i));
                mColorCalibrationCard[i].setProgress(Screen.getColorCalibrationLimits().indexOf(colors.get(i)));
                mColorCalibrationCard[i].setOnDSeekBarCardListener(this);

                addView(mColorCalibrationCard[i]);
            }
        }

        if (Screen.hasColorCalibrationMin() && mColorCalibrationLimits != null) {
            mColorCalibrationMinCard = new SeekBarCardView.DSeekBarCardView(Screen.getColorCalibrationLimits());
            mColorCalibrationMinCard.setTitle(getString(R.string.min_rgb));
            mColorCalibrationMinCard.setProgress(Screen.getColorCalibrationMin());
            mColorCalibrationMinCard.setOnDSeekBarCardListener(this);

            addView(mColorCalibrationMinCard);
        }

        if (Screen.hasInvertScreen()) {
            mInvertScreenCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mInvertScreenCard.setDescription(getString(R.string.invert_screen));
            mInvertScreenCard.setChecked(Screen.isInvertScreenActive());
            mInvertScreenCard.setOnDSwitchCompatCardListener(this);

            addView(mInvertScreenCard);
        }

        if (Screen.hasSaturationIntensity()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 159; i++)
                list.add(String.valueOf(i));

            int saturation = Screen.getSaturationIntensity();
            mSaturationIntensityCard = new SeekBarCardView.DSeekBarCardView(list);
            mSaturationIntensityCard.setTitle(getString(R.string.saturation_intensity));
            mSaturationIntensityCard.setProgress(saturation == 128 ? 30 : saturation - 225);
            mSaturationIntensityCard.setEnabled(saturation != 128);
            mSaturationIntensityCard.setOnDSeekBarCardListener(this);

            addView(mSaturationIntensityCard);

            mGrayscaleModeCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mGrayscaleModeCard.setDescription(getString(R.string.grayscale_mode));
            mGrayscaleModeCard.setChecked(saturation == 128);
            mGrayscaleModeCard.setOnDSwitchCompatCardListener(this);

            addView(mGrayscaleModeCard);
        }

        if (Screen.hasScreenHue()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 1537; i++)
                list.add(String.valueOf(i));

            mScreenHueCard = new SeekBarCardView.DSeekBarCardView(list);
            mScreenHueCard.setTitle(getString(R.string.screen_hue));
            mScreenHueCard.setDescription(getString(R.string.screen_hue_summary));
            mScreenHueCard.setProgress(Screen.getScreenHue());
            mScreenHueCard.setOnDSeekBarCardListener(this);

            addView(mScreenHueCard);
        }

        if (Screen.hasScreenValue()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 256; i++)
                list.add(String.valueOf(i));

            mScreenValueCard = new SeekBarCardView.DSeekBarCardView(list);
            mScreenValueCard.setTitle(getString(R.string.screen_value));
            mScreenValueCard.setProgress(Screen.getScreenValue() - 128);
            mScreenValueCard.setOnDSeekBarCardListener(this);

            addView(mScreenValueCard);
        }

        if (Screen.hasScreenContrast()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 256; i++)
                list.add(String.valueOf(i));

            mScreenContrastCard = new SeekBarCardView.DSeekBarCardView(list);
            mScreenContrastCard.setTitle(getString(R.string.screen_contrast));
            mScreenContrastCard.setProgress(Screen.getScreenContrast() - 128);
            mScreenContrastCard.setOnDSeekBarCardListener(this);

            addView(mScreenContrastCard);
        }

        if (Screen.hasScreenHBM()) {
            mScreenHBMCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mScreenHBMCard.setDescription(getString(R.string.high_brightness_mode));
            mScreenHBMCard.setChecked(Screen.isInvertScreenActive());
            mScreenHBMCard.setOnDSwitchCompatCardListener(this);

            addView(mScreenHBMCard);
        }
    }

    private void kgammaInit() {
        DividerCardView.DDividerCard mKGammaDividerCard = new DividerCardView.DDividerCard();
        mKGammaDividerCard.setText(getString(R.string.gamma));
        addView(mKGammaDividerCard);

        String blue = Screen.getKGammaBlue();
        mKGammaBlueCard = new EditTextCardView.DEditTextCard();
        mKGammaBlueCard.setTitle(getString(R.string.blue));
        mKGammaBlueCard.setDescription(blue);
        mKGammaBlueCard.setValue(blue);
        mKGammaBlueCard.setOnDEditTextCardListener(this);

        addView(mKGammaBlueCard);

        String green = Screen.getKGammaGreen();
        mKGammaGreenCard = new EditTextCardView.DEditTextCard();
        mKGammaGreenCard.setTitle(getString(R.string.green));
        mKGammaGreenCard.setDescription(green);
        mKGammaGreenCard.setValue(green);
        mKGammaGreenCard.setOnDEditTextCardListener(this);

        addView(mKGammaGreenCard);

        String red = Screen.getKGammaRed();
        mKGammaRedCard = new EditTextCardView.DEditTextCard();
        mKGammaRedCard.setTitle(getString(R.string.red));
        mKGammaRedCard.setDescription(red);
        mKGammaRedCard.setValue(red);
        mKGammaRedCard.setOnDEditTextCardListener(this);

        addView(mKGammaRedCard);

        GammaProfiles.KGammaProfiles kGammaProfiles = Screen.getKGammaProfiles(getActivity());
        if (kGammaProfiles != null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < kGammaProfiles.length(); i++)
                list.add(kGammaProfiles.getName(i));

            mKGammaProfilesCard = new PopupCardItem.DPopupCard(list);
            mKGammaProfilesCard.setTitle(getString(R.string.gamma_profile));
            mKGammaProfilesCard.setDescription(getString(R.string.gamma_profile_summary));
            mKGammaProfilesCard.setItem("");
            mKGammaProfilesCard.setOnDPopupCardListener(this);

            addView(mKGammaProfilesCard);
        }
    }

    private void gammacontrolInit() {
        DividerCardView.DDividerCard mKGammaDividerCard = new DividerCardView.DDividerCard();
        mKGammaDividerCard.setText(getString(R.string.gamma));
        addView(mKGammaDividerCard);

        String redGreys = Screen.getRedGreys();
        mGammaControlRedGreysCard = new EditTextCardView.DEditTextCard();
        mGammaControlRedGreysCard.setTitle(getString(R.string.red_greys));
        mGammaControlRedGreysCard.setDescription(redGreys);
        mGammaControlRedGreysCard.setValue(redGreys);
        mGammaControlRedGreysCard.setOnDEditTextCardListener(this);

        addView(mGammaControlRedGreysCard);

        String redMids = Screen.getRedMids();
        mGammaControlRedMidsCard = new EditTextCardView.DEditTextCard();
        mGammaControlRedMidsCard.setTitle(getString(R.string.red_mids));
        mGammaControlRedMidsCard.setDescription(redMids);
        mGammaControlRedMidsCard.setValue(redMids);
        mGammaControlRedMidsCard.setOnDEditTextCardListener(this);

        addView(mGammaControlRedMidsCard);

        String redBlacks = Screen.getRedBlacks();
        mGammaControlRedBlacksCard = new EditTextCardView.DEditTextCard();
        mGammaControlRedBlacksCard.setTitle(getString(R.string.red_blacks));
        mGammaControlRedBlacksCard.setDescription(redBlacks);
        mGammaControlRedBlacksCard.setValue(redBlacks);
        mGammaControlRedBlacksCard.setOnDEditTextCardListener(this);

        addView(mGammaControlRedBlacksCard);

        String redWhites = Screen.getRedWhites();
        mGammaControlRedWhitesCard = new EditTextCardView.DEditTextCard();
        mGammaControlRedWhitesCard.setTitle(getString(R.string.red_whites));
        mGammaControlRedWhitesCard.setDescription(redWhites);
        mGammaControlRedWhitesCard.setValue(redWhites);
        mGammaControlRedWhitesCard.setOnDEditTextCardListener(this);

        addView(mGammaControlRedWhitesCard);

        String greenGreys = Screen.getGreenGreys();
        mGammaControlGreenGreysCard = new EditTextCardView.DEditTextCard();
        mGammaControlGreenGreysCard.setTitle(getString(R.string.green_greys));
        mGammaControlGreenGreysCard.setDescription(greenGreys);
        mGammaControlGreenGreysCard.setValue(greenGreys);
        mGammaControlGreenGreysCard.setOnDEditTextCardListener(this);

        addView(mGammaControlGreenGreysCard);

        String greenMids = Screen.getGreenMids();
        mGammaControlGreenMidsCard = new EditTextCardView.DEditTextCard();
        mGammaControlGreenMidsCard.setTitle(getString(R.string.green_mids));
        mGammaControlGreenMidsCard.setDescription(greenMids);
        mGammaControlGreenMidsCard.setValue(greenMids);
        mGammaControlGreenMidsCard.setOnDEditTextCardListener(this);

        addView(mGammaControlGreenMidsCard);

        String greenBlacks = Screen.getGreenBlacks();
        mGammaControlGreenBlacksCard = new EditTextCardView.DEditTextCard();
        mGammaControlGreenBlacksCard.setTitle(getString(R.string.green_blacks));
        mGammaControlGreenBlacksCard.setDescription(greenBlacks);
        mGammaControlGreenBlacksCard.setValue(greenBlacks);
        mGammaControlGreenBlacksCard.setOnDEditTextCardListener(this);

        addView(mGammaControlGreenBlacksCard);

        String greenWhites = Screen.getGreenWhites();
        mGammaControlGreenWhitesCard = new EditTextCardView.DEditTextCard();
        mGammaControlGreenWhitesCard.setTitle(getString(R.string.green_whites));
        mGammaControlGreenWhitesCard.setDescription(greenWhites);
        mGammaControlGreenWhitesCard.setValue(greenWhites);
        mGammaControlGreenWhitesCard.setOnDEditTextCardListener(this);

        addView(mGammaControlGreenWhitesCard);

        String blueGreys = Screen.getBlueGreys();
        mGammaControlBlueGreysCard = new EditTextCardView.DEditTextCard();
        mGammaControlBlueGreysCard.setTitle(getString(R.string.blue_greys));
        mGammaControlBlueGreysCard.setDescription(blueGreys);
        mGammaControlBlueGreysCard.setValue(blueGreys);
        mGammaControlBlueGreysCard.setOnDEditTextCardListener(this);

        addView(mGammaControlBlueGreysCard);

        String blueMids = Screen.getBlueMids();
        mGammaControlBlueMidsCard = new EditTextCardView.DEditTextCard();
        mGammaControlBlueMidsCard.setTitle(getString(R.string.blue_mids));
        mGammaControlBlueMidsCard.setDescription(blueMids);
        mGammaControlBlueMidsCard.setValue(blueMids);
        mGammaControlBlueMidsCard.setOnDEditTextCardListener(this);

        addView(mGammaControlBlueMidsCard);

        String blueBlacks = Screen.getBlueBlacks();
        mGammaControlBlueBlacksCard = new EditTextCardView.DEditTextCard();
        mGammaControlBlueBlacksCard.setTitle(getString(R.string.blue_blacks));
        mGammaControlBlueBlacksCard.setDescription(blueBlacks);
        mGammaControlBlueBlacksCard.setValue(blueBlacks);
        mGammaControlBlueBlacksCard.setOnDEditTextCardListener(this);

        addView(mGammaControlBlueBlacksCard);

        String blueWhites = Screen.getBlueWhites();
        mGammaControlBlueWhitesCard = new EditTextCardView.DEditTextCard();
        mGammaControlBlueWhitesCard.setTitle(getString(R.string.blue_whites));
        mGammaControlBlueWhitesCard.setDescription(blueWhites);
        mGammaControlBlueWhitesCard.setValue(blueWhites);
        mGammaControlBlueWhitesCard.setOnDEditTextCardListener(this);

        addView(mGammaControlBlueWhitesCard);

        String contrast = Screen.getGammaContrast();
        mGammaControlContrastCard = new EditTextCardView.DEditTextCard();
        mGammaControlContrastCard.setTitle(getString(R.string.contrast));
        mGammaControlContrastCard.setDescription(contrast);
        mGammaControlContrastCard.setValue(contrast);
        mGammaControlContrastCard.setOnDEditTextCardListener(this);

        addView(mGammaControlContrastCard);

        String brightness = Screen.getGammaBrightness();
        mGammaControlBrightnessCard = new EditTextCardView.DEditTextCard();
        mGammaControlBrightnessCard.setTitle(getString(R.string.brightness));
        mGammaControlBrightnessCard.setDescription(brightness);
        mGammaControlBrightnessCard.setValue(brightness);
        mGammaControlBrightnessCard.setOnDEditTextCardListener(this);

        addView(mGammaControlBrightnessCard);

        String saturation = Screen.getGammaSaturation();
        mGammaControlSaturationCard = new EditTextCardView.DEditTextCard();
        mGammaControlSaturationCard.setTitle(getString(R.string.saturation_intensity));
        mGammaControlSaturationCard.setDescription(saturation);
        mGammaControlSaturationCard.setValue(saturation);
        mGammaControlSaturationCard.setOnDEditTextCardListener(this);

        addView(mGammaControlSaturationCard);

        GammaProfiles.GammaControlProfiles gammaControlProfiles = Screen.getGammaControlProfiles(getActivity());
        if (gammaControlProfiles != null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < gammaControlProfiles.length(); i++)
                list.add(gammaControlProfiles.getName(i));

            mGammaControlProfilesCard = new PopupCardItem.DPopupCard(list);
            mGammaControlProfilesCard.setTitle(getString(R.string.gamma_profile));
            mGammaControlProfilesCard.setDescription(getString(R.string.gamma_profile_summary));
            mGammaControlProfilesCard.setItem("");
            mGammaControlProfilesCard.setOnDPopupCardListener(this);

            addView(mGammaControlProfilesCard);
        }
    }

    private void dsipanelInit() {
        DividerCardView.DDividerCard mKGammaDividerCard = new DividerCardView.DDividerCard();
        mKGammaDividerCard.setText(getString(R.string.gamma));
        addView(mKGammaDividerCard);

        String blueNegative = Screen.getBlueNegative();
        mDsiPanelBlueNegativeCard = new EditTextCardView.DEditTextCard();
        mDsiPanelBlueNegativeCard.setTitle(getString(R.string.blue_negative));
        mDsiPanelBlueNegativeCard.setDescription(blueNegative);
        mDsiPanelBlueNegativeCard.setValue(blueNegative);
        mDsiPanelBlueNegativeCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelBlueNegativeCard);

        String bluePositive = Screen.getBluePositive();
        mDsiPanelBluePositiveCard = new EditTextCardView.DEditTextCard();
        mDsiPanelBluePositiveCard.setTitle(getString(R.string.blue_positive));
        mDsiPanelBluePositiveCard.setDescription(bluePositive);
        mDsiPanelBluePositiveCard.setValue(bluePositive);
        mDsiPanelBluePositiveCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelBluePositiveCard);

        String greenNegative = Screen.getGreenNegative();
        mDsiPanelGreenNegativeCard = new EditTextCardView.DEditTextCard();
        mDsiPanelGreenNegativeCard.setTitle(getString(R.string.green_negative));
        mDsiPanelGreenNegativeCard.setDescription(greenNegative);
        mDsiPanelGreenNegativeCard.setValue(greenNegative);
        mDsiPanelGreenNegativeCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelGreenNegativeCard);

        String greenPositive = Screen.getGreenPositive();
        mDsiPanelGreenPositiveCard = new EditTextCardView.DEditTextCard();
        mDsiPanelGreenPositiveCard.setTitle(getString(R.string.green_positive));
        mDsiPanelGreenPositiveCard.setDescription(greenPositive);
        mDsiPanelGreenPositiveCard.setValue(greenPositive);
        mDsiPanelGreenPositiveCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelGreenPositiveCard);

        String redNegative = Screen.getRedNegative();
        mDsiPanelRedNegativeCard = new EditTextCardView.DEditTextCard();
        mDsiPanelRedNegativeCard.setTitle(getString(R.string.red_negative));
        mDsiPanelRedNegativeCard.setDescription(redNegative);
        mDsiPanelRedNegativeCard.setValue(redNegative);
        mDsiPanelRedNegativeCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelRedNegativeCard);

        String redPositive = Screen.getRedPositive();
        mDsiPanelRedPositiveCard = new EditTextCardView.DEditTextCard();
        mDsiPanelRedPositiveCard.setTitle(getString(R.string.red_positive));
        mDsiPanelRedPositiveCard.setDescription(redPositive);
        mDsiPanelRedPositiveCard.setValue(redPositive);
        mDsiPanelRedPositiveCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelRedPositiveCard);

        String whitePoint = Screen.getWhitePoint();
        mDsiPanelWhitePointCard = new EditTextCardView.DEditTextCard();
        mDsiPanelWhitePointCard.setTitle(getString(R.string.white_point));
        mDsiPanelWhitePointCard.setDescription(whitePoint);
        mDsiPanelWhitePointCard.setValue(whitePoint);
        mDsiPanelWhitePointCard.setOnDEditTextCardListener(this);

        addView(mDsiPanelWhitePointCard);

        GammaProfiles.DsiPanelProfiles dsiPanelProfiles = Screen.getDsiPanelProfiles(getActivity());
        if (dsiPanelProfiles != null) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < dsiPanelProfiles.length(); i++)
                list.add(dsiPanelProfiles.getName(i));

            mDsiPanelProfilesCard = new PopupCardItem.DPopupCard(list);
            mDsiPanelProfilesCard.setTitle(getString(R.string.gamma_profile));
            mDsiPanelProfilesCard.setDescription(getString(R.string.gamma_profile_summary));
            mDsiPanelProfilesCard.setItem("");
            mDsiPanelProfilesCard.setOnDPopupCardListener(this);

            addView(mDsiPanelProfilesCard);
        }
    }

    private void additionalProfilesInit() {
        mAdditionalProfilesCard = new CardViewItem.DCardView();
        mAdditionalProfilesCard.setTitle(getString(R.string.additional_profiles));
        mAdditionalProfilesCard.setDescription(getString(R.string.additional_profiles_summary));
        mAdditionalProfilesCard.setOnDCardListener(this);

        addView(mAdditionalProfilesCard);
    }

    private void backlightDimmerInit() {
        if (Screen.hasBackLightDimmerEnable()) {
            mBackLightDimmerEnableCard = new SwitchCompatCardItem.DSwitchCompatCard();
            mBackLightDimmerEnableCard.setDescription(getString(R.string.backlight_dimmer));
            mBackLightDimmerEnableCard.setChecked(Screen.isBackLightDimmerActive());
            mBackLightDimmerEnableCard.setOnDSwitchCompatCardListener(this);

            addView(mBackLightDimmerEnableCard);
        }

        if (Screen.hasMinBrightness()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= Screen.getMaxMinBrightness(); i++)
                list.add(String.valueOf(i));

            mMinBrightnessCard = new SeekBarCardView.DSeekBarCardView(list);
            mMinBrightnessCard.setTitle(getString(R.string.min_brightness));
            mMinBrightnessCard.setDescription(getString(R.string.min_brightness_summary));
            mMinBrightnessCard.setProgress(Screen.getCurMinBrightness());
            mMinBrightnessCard.setOnDSeekBarCardListener(this);

            addView(mMinBrightnessCard);
        }

        if (Screen.hasBackLightDimmerThreshold()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 51; i++)
                list.add(String.valueOf(i));

            mBackLightDimmerThresholdCard = new SeekBarCardView.DSeekBarCardView(list);
            mBackLightDimmerThresholdCard.setTitle(getString(R.string.backlight_dimmer_threshold));
            mBackLightDimmerThresholdCard.setProgress(Screen.getBackLightDimmerThreshold());
            mBackLightDimmerThresholdCard.setOnDSeekBarCardListener(this);

            addView(mBackLightDimmerThresholdCard);
        }

        if (Screen.hasBackLightDimmerOffset()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 51; i++)
                list.add(String.valueOf(i));

            mBackLightDimmerOffsetCard = new SeekBarCardView.DSeekBarCardView(list);
            mBackLightDimmerOffsetCard.setTitle(getString(R.string.backlight_dimmer_offset));
            mBackLightDimmerOffsetCard.setProgress(Screen.getBackLightDimmerOffset());
            mBackLightDimmerOffsetCard.setOnDSeekBarCardListener(this);

            addView(mBackLightDimmerOffsetCard);
        }
    }

    @Override
    public void onChanged(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mColorCalibrationMinCard) {
            for (SeekBarCardView.DSeekBarCardView seekBarCardView : mColorCalibrationCard)
                if (position > seekBarCardView.getProgress())
                    seekBarCardView.setProgress(position);
        } else {
            if (mColorCalibrationCard != null)
                for (SeekBarCardView.DSeekBarCardView seekBarCardView : mColorCalibrationCard)
                    if (dSeekBarCardView == seekBarCardView) {
                        if (mColorCalibrationMinCard != null)
                            if (position < mColorCalibrationMinCard.getProgress())
                                mColorCalibrationMinCard.setProgress(position);
                        return;
                    }
        }
    }

    @Override
    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
        if (dSeekBarCardView == mMinBrightnessCard)
            Screen.setMinBrightness(position, getActivity());
        else if (dSeekBarCardView == mBackLightDimmerThresholdCard)
            Screen.setBackLightDimmerThreshold(position, getActivity());
        else if (dSeekBarCardView == mBackLightDimmerOffsetCard)
            Screen.setBackLightDimmerOffset(position, getActivity());
        else if (dSeekBarCardView == mColorCalibrationMinCard)
            Screen.setColorCalibrationMin(Utils.stringToInt(mColorCalibrationLimits.get(position)), getActivity());
        else if (dSeekBarCardView == mSaturationIntensityCard)
            Screen.setSaturationIntensity(position + 225, getActivity());
        else if (dSeekBarCardView == mScreenHueCard)
            Screen.setScreenHue(position, getActivity());
        else if (dSeekBarCardView == mScreenValueCard)
            Screen.setScreenValue(position + 128, getActivity());
        else if (dSeekBarCardView == mScreenContrastCard)
            Screen.setScreenContrast(position + 128, getActivity());
        else {
            for (SeekBarCardView.DSeekBarCardView seekBarCardView : mColorCalibrationCard)
                if (dSeekBarCardView == seekBarCardView) {
                    if (mColorCalibrationMinCard != null) {
                        int current = Utils.stringToInt(mColorCalibrationLimits.get(position));
                        if (Screen.getColorCalibrationMin() > current)
                            Screen.setColorCalibrationMin(current, getActivity());
                    }
                    int r = mColorCalibrationCard[0].getProgress();
                    int g = mColorCalibrationCard[1].getProgress();
                    int b = mColorCalibrationCard[2].getProgress();
                    Screen.setColorCalibration(mColorCalibrationLimits.get(r) + " " +
                            mColorCalibrationLimits.get(g) + " " + mColorCalibrationLimits.get(b), getActivity());
                    return;
                }
        }
    }

    @Override
    public void onChecked(SwitchCompatCardItem.DSwitchCompatCard dSwitchCompatCard, boolean checked) {
        if (dSwitchCompatCard == mInvertScreenCard)
            Screen.activateInvertScreen(checked, getActivity());
        else if (dSwitchCompatCard == mGrayscaleModeCard) {
            mSaturationIntensityCard.setEnabled(!checked);
            Screen.activateGrayscaleMode(checked, getActivity());
            if (!checked) mSaturationIntensityCard.setProgress(30);
        } else if (dSwitchCompatCard == mScreenHBMCard)
            Screen.activateScreenHBM(checked, getActivity());
        else if (dSwitchCompatCard == mBackLightDimmerEnableCard)
            Screen.activateBackLightDimmer(checked, getActivity());
    }

    @Override
    public void onApply(EditTextCardView.DEditTextCard dEditTextCard, String value) {
        dEditTextCard.setDescription(value);
        if (dEditTextCard == mKGammaRedCard)
            Screen.setKGammaRed(value, getActivity());
        else if (dEditTextCard == mKGammaGreenCard)
            Screen.setKGammaGreen(value, getActivity());
        else if (dEditTextCard == mKGammaBlueCard)
            Screen.setKGammaBlue(value, getActivity());
        else if (dEditTextCard == mGammaControlRedGreysCard)
            Screen.setRedGreys(value, getActivity());
        else if (dEditTextCard == mGammaControlRedMidsCard)
            Screen.setRedMids(value, getActivity());
        else if (dEditTextCard == mGammaControlRedBlacksCard)
            Screen.setRedBlacks(value, getActivity());
        else if (dEditTextCard == mGammaControlRedWhitesCard)
            Screen.setRedWhites(value, getActivity());
        else if (dEditTextCard == mGammaControlGreenGreysCard)
            Screen.setGreenGreys(value, getActivity());
        else if (dEditTextCard == mGammaControlGreenMidsCard)
            Screen.setGreenMids(value, getActivity());
        else if (dEditTextCard == mGammaControlGreenBlacksCard)
            Screen.setGreenBlacks(value, getActivity());
        else if (dEditTextCard == mGammaControlGreenWhitesCard)
            Screen.setGreenWhites(value, getActivity());
        else if (dEditTextCard == mGammaControlBlueGreysCard)
            Screen.setBlueGreys(value, getActivity());
        else if (dEditTextCard == mGammaControlBlueMidsCard)
            Screen.setBlueMids(value, getActivity());
        else if (dEditTextCard == mGammaControlBlueBlacksCard)
            Screen.setBlueBlacks(value, getActivity());
        else if (dEditTextCard == mGammaControlBlueWhitesCard)
            Screen.setBlueWhites(value, getActivity());
        else if (dEditTextCard == mGammaControlContrastCard)
            Screen.setGammaContrast(value, getActivity());
        else if (dEditTextCard == mGammaControlBrightnessCard)
            Screen.setGammaBrightness(value, getActivity());
        else if (dEditTextCard == mGammaControlSaturationCard)
            Screen.setGammaSaturation(value, getActivity());
        else if (dEditTextCard == mDsiPanelRedPositiveCard)
            Screen.setRedPositive(value, getActivity());
        else if (dEditTextCard == mDsiPanelRedNegativeCard)
            Screen.setRedNegative(value, getActivity());
        else if (dEditTextCard == mDsiPanelGreenPositiveCard)
            Screen.setGreenPositive(value, getActivity());
        else if (dEditTextCard == mDsiPanelGreenNegativeCard)
            Screen.setGreenNegative(value, getActivity());
        else if (dEditTextCard == mDsiPanelBluePositiveCard)
            Screen.setBluePositive(value, getActivity());
        else if (dEditTextCard == mDsiPanelBlueNegativeCard)
            Screen.setBlueNegative(value, getActivity());
        else if (dEditTextCard == mDsiPanelWhitePointCard)
            Screen.setWhitePoint(value, getActivity());
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mKGammaProfilesCard) {
            Screen.setKGammaProfile(position, Screen.getKGammaProfiles(getActivity()), getActivity());
            refreshKGamma();
        } else if (dPopupCard == mGammaControlProfilesCard) {
            Screen.setGammaControlProfile(position, Screen.getGammaControlProfiles(getActivity()), getActivity());
            refreshGammaControl();
        } else if (dPopupCard == mDsiPanelProfilesCard) {
            Screen.setDsiPanelProfile(position, Screen.getDsiPanelProfiles(getActivity()), getActivity());
            refreshDsiPanel();
        }
    }

    @Override
    public void onClick(CardViewItem.DCardView dCardView) {
        if (dCardView == mAdditionalProfilesCard) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            new WebpageReader(new WebpageReader.WebpageCallback() {
                @Override
                public void onCallback(String raw, String html) {
                    progressDialog.dismiss();
                    GammaProfiles gammaProfiles = new GammaProfiles(raw);
                    String path = getActivity().getApplicationContext().getCacheDir() + "/gamma_profiles.json";
                    if (gammaProfiles.readable()) {
                        Utils.writeFile(path, raw, false);
                        showMoreGammaProfiles(gammaProfiles);
                    } else {
                        if (Utils.existFile(path)) {
                            gammaProfiles.refresh(Utils.readFile(path));
                            if (gammaProfiles.readable()) {
                                showMoreGammaProfiles(gammaProfiles);
                                return;
                            }
                        }
                        Utils.toast(getString(R.string.no_internet), getActivity());
                    }
                }
            }).execute(Constants.GAMMA_URL);
        }
    }

    private String getColor(int position) {
        switch (position) {
            case 0:
                return getString(R.string.red);
            case 1:
                return getString(R.string.green);
            case 2:
                return getString(R.string.blue);
            default:
                return null;
        }
    }

    private void showMoreGammaProfiles(GammaProfiles gammaProfiles) {
        GammaProfiles.GammaProfile profile = null;
        int screen = -1;
        if (mKGammaProfilesCard != null) {
            profile = gammaProfiles.getKGamma();
            screen = 0;
        } else if (mGammaControlProfilesCard != null) {
            profile = gammaProfiles.getGammaControl();
            screen = 1;
        } else if (mDsiPanelProfilesCard != null) {
            profile = gammaProfiles.getDsiPanelProfiles();
            screen = 2;
        }

        if (profile == null) Utils.toast(getString(R.string.no_additional_profiles), getActivity());
        else {
            String[] names = new String[profile.length()];
            for (int i = 0; i < names.length; i++)
                names[i] = profile.getName(i);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final int profileType = screen;
            final GammaProfiles.GammaProfile gammaProfile = profile;
            builder.setItems(names, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (profileType) {
                        case 0:
                            Screen.setKGammaProfile(which, (GammaProfiles.KGammaProfiles) gammaProfile, getActivity());
                            refreshKGamma();
                            break;
                        case 1:
                            Screen.setGammaControlProfile(which, (GammaProfiles.GammaControlProfiles) gammaProfile, getActivity());
                            refreshGammaControl();
                            break;
                        case 2:
                            Screen.setDsiPanelProfile(which, (GammaProfiles.DsiPanelProfiles) gammaProfile, getActivity());
                            refreshDsiPanel();
                            break;
                    }
                }
            }).show();
        }
    }

    private void refreshKGamma() {
        for (int i = 0; i < mColorCalibrationCard.length; i++)
            mColorCalibrationCard[i].setProgress(Screen.getColorCalibrationLimits()
                    .indexOf(Screen.getColorCalibration().get(i)));

        String red = Screen.getKGammaRed();
        mKGammaRedCard.setDescription(red);
        mKGammaRedCard.setValue(red);

        String green = Screen.getKGammaGreen();
        mKGammaGreenCard.setDescription(green);
        mKGammaGreenCard.setValue(green);

        String blue = Screen.getKGammaBlue();
        mKGammaBlueCard.setDescription(blue);
        mKGammaBlueCard.setValue(blue);
    }

    private void refreshGammaControl() {
        for (int i = 0; i < mColorCalibrationCard.length; i++)
            mColorCalibrationCard[i].setProgress(Screen.getColorCalibrationLimits()
                    .indexOf(Screen.getColorCalibration().get(i)));

        String redGreys = Screen.getRedGreys();
        mGammaControlRedGreysCard.setDescription(redGreys);
        mGammaControlRedGreysCard.setValue(redGreys);

        String redMids = Screen.getRedMids();
        mGammaControlRedMidsCard.setDescription(redMids);
        mGammaControlRedMidsCard.setValue(redMids);

        String redBlacks = Screen.getRedBlacks();
        mGammaControlRedBlacksCard.setDescription(redBlacks);
        mGammaControlRedBlacksCard.setValue(redBlacks);

        String redWhites = Screen.getRedWhites();
        mGammaControlRedWhitesCard.setDescription(redWhites);
        mGammaControlRedWhitesCard.setValue(redWhites);

        String greenGreys = Screen.getGreenGreys();
        mGammaControlGreenGreysCard.setDescription(greenGreys);
        mGammaControlGreenGreysCard.setValue(greenGreys);

        String greenMids = Screen.getGreenMids();
        mGammaControlGreenMidsCard.setDescription(greenMids);
        mGammaControlGreenMidsCard.setValue(greenMids);

        String greenBlacks = Screen.getGreenBlacks();
        mGammaControlGreenBlacksCard.setDescription(greenBlacks);
        mGammaControlGreenBlacksCard.setValue(greenBlacks);

        String greenWhites = Screen.getGreenWhites();
        mGammaControlGreenWhitesCard.setDescription(greenWhites);
        mGammaControlGreenWhitesCard.setValue(greenWhites);

        String blueGreys = Screen.getBlueGreys();
        mGammaControlBlueGreysCard.setDescription(blueGreys);
        mGammaControlBlueGreysCard.setValue(blueGreys);

        String blueMids = Screen.getBlueMids();
        mGammaControlBlueMidsCard.setDescription(blueMids);
        mGammaControlBlueMidsCard.setValue(blueMids);

        String blueBlacks = Screen.getBlueBlacks();
        mGammaControlBlueBlacksCard.setDescription(blueBlacks);
        mGammaControlBlueBlacksCard.setValue(blueBlacks);

        String blueWhites = Screen.getBlueWhites();
        mGammaControlBlueWhitesCard.setDescription(blueWhites);
        mGammaControlBlueWhitesCard.setValue(blueWhites);

        String contrast = Screen.getGammaContrast();
        mGammaControlContrastCard.setDescription(contrast);
        mGammaControlContrastCard.setValue(contrast);

        String brightness = Screen.getGammaBrightness();
        mGammaControlBrightnessCard.setDescription(brightness);
        mGammaControlBrightnessCard.setValue(brightness);

        String saturation = Screen.getGammaSaturation();
        mGammaControlSaturationCard.setDescription(saturation);
        mGammaControlSaturationCard.setValue(saturation);
    }

    private void refreshDsiPanel() {
        String blueNegative = Screen.getBlueNegative();
        mDsiPanelBlueNegativeCard.setDescription(blueNegative);
        mDsiPanelBlueNegativeCard.setValue(blueNegative);

        String bluePositive = Screen.getBluePositive();
        mDsiPanelBluePositiveCard.setDescription(bluePositive);
        mDsiPanelBluePositiveCard.setValue(bluePositive);

        String greenNegative = Screen.getGreenNegative();
        mDsiPanelGreenNegativeCard.setDescription(greenNegative);
        mDsiPanelGreenNegativeCard.setValue(greenNegative);

        String greenPositive = Screen.getGreenPositive();
        mDsiPanelGreenPositiveCard.setDescription(greenPositive);
        mDsiPanelGreenPositiveCard.setValue(greenPositive);

        String redNegative = Screen.getRedNegative();
        mDsiPanelRedNegativeCard.setDescription(redNegative);
        mDsiPanelRedNegativeCard.setValue(redNegative);

        String redPositive = Screen.getRedPositive();
        mDsiPanelRedPositiveCard.setDescription(redPositive);
        mDsiPanelRedPositiveCard.setValue(redPositive);

        String whitePoint = Screen.getWhitePoint();
        mDsiPanelWhitePointCard.setDescription(whitePoint);
        mDsiPanelWhitePointCard.setValue(whitePoint);
    }

}
