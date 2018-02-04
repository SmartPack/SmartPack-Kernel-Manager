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
package com.grarak.kerneladiutor.utils.kernel.screen;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 02.06.16.
 */
public class Gamma {

    private static final String K_GAMMA_R = "/sys/devices/platform/mipi_lgit.1537/kgamma_r";
    private static final String K_GAMMA_G = "/sys/devices/platform/mipi_lgit.1537/kgamma_g";
    private static final String K_GAMMA_B = "/sys/devices/platform/mipi_lgit.1537/kgamma_b";

    private static final String K_GAMMA_RED = "/sys/devices/platform/mipi_lgit.1537/kgamma_red";
    private static final String K_GAMMA_GREEN = "/sys/devices/platform/mipi_lgit.1537/kgamma_green";
    private static final String K_GAMMA_BLUE = "/sys/devices/platform/mipi_lgit.1537/kgamma_blue";

    private static final String GAMMACONTROL = "/sys/class/misc/gammacontrol";
    private static final String GAMMACONTROL_RED_GREYS = GAMMACONTROL + "/red_greys";
    private static final String GAMMACONTROL_RED_MIDS = GAMMACONTROL + "/red_mids";
    private static final String GAMMACONTROL_RED_BLACKS = GAMMACONTROL + "/red_blacks";
    private static final String GAMMACONTROL_RED_WHITES = GAMMACONTROL + "/red_whites";
    private static final String GAMMACONTROL_GREEN_GREYS = GAMMACONTROL + "/green_greys";
    private static final String GAMMACONTROL_GREEN_MIDS = GAMMACONTROL + "/green_mids";
    private static final String GAMMACONTROL_GREEN_BLACKS = GAMMACONTROL + "/green_blacks";
    private static final String GAMMACONTROL_GREEN_WHITES = GAMMACONTROL + "/green_whites";
    private static final String GAMMACONTROL_BLUE_GREYS = GAMMACONTROL + "/blue_greys";
    private static final String GAMMACONTROL_BLUE_MIDS = GAMMACONTROL + "/blue_mids";
    private static final String GAMMACONTROL_BLUE_BLACKS = GAMMACONTROL + "/blue_blacks";
    private static final String GAMMACONTROL_BLUE_WHITES = GAMMACONTROL + "/blue_whites";
    private static final String GAMMACONTROL_CONTRAST = GAMMACONTROL + "/contrast";
    private static final String GAMMACONTROL_BRIGHTNESS = GAMMACONTROL + "/brightness";
    private static final String GAMMACONTROL_SATURATION = GAMMACONTROL + "/saturation";

    private static final String DSI_PANEL_RP = "/sys/module/dsi_panel/kgamma_rp";
    private static final String DSI_PANEL_RN = "/sys/module/dsi_panel/kgamma_rn";
    private static final String DSI_PANEL_GP = "/sys/module/dsi_panel/kgamma_gp";
    private static final String DSI_PANEL_GN = "/sys/module/dsi_panel/kgamma_gn";
    private static final String DSI_PANEL_BP = "/sys/module/dsi_panel/kgamma_bp";
    private static final String DSI_PANEL_BN = "/sys/module/dsi_panel/kgamma_bn";
    private static final String DSI_PANEL_W = "/sys/module/dsi_panel/kgamma_w";

    private static final List<String> sKGammaFiles = new ArrayList<>();
    private static final List<String> sGammaControlFiles = new ArrayList<>();
    private static final List<String> sDsiPanelFiles = new ArrayList<>();

    static {
        sKGammaFiles.add(K_GAMMA_R);
        sKGammaFiles.add(K_GAMMA_G);
        sKGammaFiles.add(K_GAMMA_B);
        sKGammaFiles.add(K_GAMMA_RED);
        sKGammaFiles.add(K_GAMMA_GREEN);
        sKGammaFiles.add(K_GAMMA_BLUE);

        sGammaControlFiles.add(GAMMACONTROL_RED_GREYS);
        sGammaControlFiles.add(GAMMACONTROL_RED_MIDS);
        sGammaControlFiles.add(GAMMACONTROL_RED_BLACKS);
        sGammaControlFiles.add(GAMMACONTROL_RED_WHITES);
        sGammaControlFiles.add(GAMMACONTROL_GREEN_GREYS);
        sGammaControlFiles.add(GAMMACONTROL_GREEN_MIDS);
        sGammaControlFiles.add(GAMMACONTROL_GREEN_BLACKS);
        sGammaControlFiles.add(GAMMACONTROL_GREEN_WHITES);
        sGammaControlFiles.add(GAMMACONTROL_BLUE_GREYS);
        sGammaControlFiles.add(GAMMACONTROL_BLUE_MIDS);
        sGammaControlFiles.add(GAMMACONTROL_BLUE_BLACKS);
        sGammaControlFiles.add(GAMMACONTROL_BLUE_WHITES);
        sGammaControlFiles.add(GAMMACONTROL_CONTRAST);
        sGammaControlFiles.add(GAMMACONTROL_BRIGHTNESS);
        sGammaControlFiles.add(GAMMACONTROL_SATURATION);

        sDsiPanelFiles.add(DSI_PANEL_RP);
        sDsiPanelFiles.add(DSI_PANEL_RN);
        sDsiPanelFiles.add(DSI_PANEL_GP);
        sDsiPanelFiles.add(DSI_PANEL_GN);
        sDsiPanelFiles.add(DSI_PANEL_BP);
        sDsiPanelFiles.add(DSI_PANEL_BN);
        sDsiPanelFiles.add(DSI_PANEL_W);

    }

    private static GammaProfiles GAMMA_PROFILES;

    public static void setDsiPanelProfile(int profile, GammaProfiles.DsiPanelProfiles dsiPanelProfiles, Context context) {
        setBlueNegative(dsiPanelProfiles.getBlueNegative(profile), context);
        setBluePositive(dsiPanelProfiles.getBluePositive(profile), context);
        setGreenNegative(dsiPanelProfiles.getGreenNegative(profile), context);
        setGreenPositive(dsiPanelProfiles.getGreenPositive(profile), context);
        setRedNegative(dsiPanelProfiles.getRedNegative(profile), context);
        setRedPositive(dsiPanelProfiles.getRedPositive(profile), context);
        setWhitePoint(dsiPanelProfiles.getWhitePoint(profile), context);
    }

    public static void setWhitePoint(String value, Context context) {
        run(Control.write(value, DSI_PANEL_W), DSI_PANEL_W, context);
    }

    public static void setBlueNegative(String value, Context context) {
        run(Control.write(value, DSI_PANEL_BN), DSI_PANEL_BN, context);
    }

    public static void setBluePositive(String value, Context context) {
        run(Control.write(value, DSI_PANEL_BP), DSI_PANEL_BP, context);
    }

    public static void setGreenNegative(String value, Context context) {
        run(Control.write(value, DSI_PANEL_GN), DSI_PANEL_GN, context);
    }

    public static void setGreenPositive(String value, Context context) {
        run(Control.write(value, DSI_PANEL_GP), DSI_PANEL_GP, context);
    }

    public static void setRedNegative(String value, Context context) {
        run(Control.write(value, DSI_PANEL_RN), DSI_PANEL_RN, context);
    }

    public static void setRedPositive(String value, Context context) {
        run(Control.write(value, DSI_PANEL_RP), DSI_PANEL_RP, context);
    }

    public static GammaProfiles.DsiPanelProfiles getDsiPanelProfiles(Context context) {
        if (GAMMA_PROFILES == null) {
            GAMMA_PROFILES = new GammaProfiles(Utils.readAssetFile(context, "gamma_profiles.json"));
        }
        return GAMMA_PROFILES.getDsiPanelProfiles();
    }

    public static String getWhitePoint() {
        return Utils.readFile(DSI_PANEL_W);
    }

    public static String getBlueNegative() {
        return Utils.readFile(DSI_PANEL_BN);
    }

    public static String getBluePositive() {
        return Utils.readFile(DSI_PANEL_BP);
    }

    public static String getGreenNegative() {
        return Utils.readFile(DSI_PANEL_GN);
    }

    public static String getGreenPositive() {
        return Utils.readFile(DSI_PANEL_GP);
    }

    public static String getRedNegative() {
        return Utils.readFile(DSI_PANEL_RN);
    }

    public static String getRedPositive() {
        return Utils.readFile(DSI_PANEL_RP);
    }

    public static boolean hasDsiPanel() {
        for (String file : sDsiPanelFiles) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public static void setGammaControlProfile(int profile, GammaProfiles.GammaControlProfiles gammaControlProfiles,
                                              Context context) {
        if (Calibration.getInstance().hasColors()) {
            Calibration.getInstance().setColors(gammaControlProfiles.getKCAL(profile), context);
        }
        setRedGreys(gammaControlProfiles.getRedGreys(profile), context);
        setRedMids(gammaControlProfiles.getRedMids(profile), context);
        setRedBlacks(gammaControlProfiles.getRedBlacks(profile), context);
        setRedWhites(gammaControlProfiles.getRedWhites(profile), context);
        setGreenGreys(gammaControlProfiles.getGreenGreys(profile), context);
        setGreenMids(gammaControlProfiles.getGreenMids(profile), context);
        setGreenBlacks(gammaControlProfiles.getGreenBlacks(profile), context);
        setGreenWhites(gammaControlProfiles.getGreenWhites(profile), context);
        setBlueGreys(gammaControlProfiles.getBlueGreys(profile), context);
        setBlueMids(gammaControlProfiles.getBlueMids(profile), context);
        setBlueBlacks(gammaControlProfiles.getBlueBlacks(profile), context);
        setBlueWhites(gammaControlProfiles.getBlueWhites(profile), context);
        setGammaContrast(gammaControlProfiles.getContrast(profile), context);
        setGammaBrightness(gammaControlProfiles.getBrightness(profile), context);
        setGammaSaturation(gammaControlProfiles.getSaturation(profile), context);
    }

    public static void setGammaSaturation(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_SATURATION), GAMMACONTROL_SATURATION, context);
    }

    public static void setGammaBrightness(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_BRIGHTNESS), GAMMACONTROL_BRIGHTNESS, context);
    }

    public static void setGammaContrast(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_CONTRAST), GAMMACONTROL_CONTRAST, context);
    }

    public static void setBlueWhites(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_BLUE_WHITES), GAMMACONTROL_BLUE_WHITES, context);
    }

    public static void setBlueBlacks(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_BLUE_BLACKS), GAMMACONTROL_BLUE_BLACKS, context);
    }

    public static void setBlueMids(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_BLUE_MIDS), GAMMACONTROL_BLUE_MIDS, context);
    }

    public static void setBlueGreys(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_BLUE_GREYS), GAMMACONTROL_BLUE_GREYS, context);
    }

    public static void setGreenWhites(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_GREEN_WHITES), GAMMACONTROL_GREEN_WHITES, context);
    }

    public static void setGreenBlacks(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_GREEN_BLACKS), GAMMACONTROL_GREEN_BLACKS, context);
    }

    public static void setGreenMids(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_GREEN_MIDS), GAMMACONTROL_GREEN_MIDS, context);
    }

    public static void setGreenGreys(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_GREEN_GREYS), GAMMACONTROL_GREEN_GREYS, context);
    }

    public static void setRedWhites(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_RED_WHITES), GAMMACONTROL_RED_WHITES, context);
    }

    public static void setRedBlacks(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_RED_BLACKS), GAMMACONTROL_RED_BLACKS, context);
    }

    public static void setRedMids(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_RED_MIDS), GAMMACONTROL_RED_MIDS, context);
    }

    public static void setRedGreys(String value, Context context) {
        run(Control.write(value, GAMMACONTROL_RED_GREYS), GAMMACONTROL_RED_GREYS, context);
    }

    public static GammaProfiles.GammaControlProfiles getGammaControlProfiles(Context context) {
        if (GAMMA_PROFILES == null)
            GAMMA_PROFILES = new GammaProfiles(Utils.readAssetFile(context, "gamma_profiles.json"));
        return GAMMA_PROFILES.getGammaControl();
    }

    public static String getGammaSaturation() {
        return Utils.readFile(GAMMACONTROL_SATURATION);
    }

    public static String getGammaBrightness() {
        return Utils.readFile(GAMMACONTROL_BRIGHTNESS);
    }

    public static String getGammaContrast() {
        return Utils.readFile(GAMMACONTROL_CONTRAST);
    }

    public static String getBlueWhites() {
        return Utils.readFile(GAMMACONTROL_BLUE_WHITES);
    }

    public static String getBlueBlacks() {
        return Utils.readFile(GAMMACONTROL_BLUE_BLACKS);
    }

    public static String getBlueMids() {
        return Utils.readFile(GAMMACONTROL_BLUE_MIDS);
    }

    public static String getBlueGreys() {
        return Utils.readFile(GAMMACONTROL_BLUE_GREYS);
    }

    public static String getGreenWhites() {
        return Utils.readFile(GAMMACONTROL_GREEN_WHITES);
    }

    public static String getGreenBlacks() {
        return Utils.readFile(GAMMACONTROL_GREEN_BLACKS);
    }

    public static String getGreenMids() {
        return Utils.readFile(GAMMACONTROL_GREEN_MIDS);
    }

    public static String getGreenGreys() {
        return Utils.readFile(GAMMACONTROL_GREEN_GREYS);
    }

    public static String getRedWhites() {
        return Utils.readFile(GAMMACONTROL_RED_WHITES);
    }

    public static String getRedBlacks() {
        return Utils.readFile(GAMMACONTROL_RED_BLACKS);
    }

    public static String getRedMids() {
        return Utils.readFile(GAMMACONTROL_RED_MIDS);
    }

    public static String getRedGreys() {
        return Utils.readFile(GAMMACONTROL_RED_GREYS);
    }

    public static boolean hasGammaControl() {
        for (String file : sGammaControlFiles) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public static void setKGammaProfile(int profile, GammaProfiles.KGammaProfiles kGammaProfiles, Context context) {
        if (Calibration.getInstance().hasColors()) {
            Calibration.getInstance().setColors(kGammaProfiles.getKCAL(profile), context);
        }
        setKGammaBlue(kGammaProfiles.getGammaBlue(profile), context);
        setKGammaGreen(kGammaProfiles.getGammaGreen(profile), context);
        setKGammaRed(kGammaProfiles.getGammaRed(profile), context);
    }

    public static void setKGammaBlue(String value, Context context) {
        if (Utils.existFile(K_GAMMA_BLUE)) {
            run(Control.write(value, K_GAMMA_BLUE), K_GAMMA_BLUE, context);
        } else {
            run(Control.write(value, K_GAMMA_B), K_GAMMA_B, context);
        }
    }

    public static void setKGammaGreen(String value, Context context) {
        if (Utils.existFile(K_GAMMA_GREEN)) {
            run(Control.write(value, K_GAMMA_GREEN), K_GAMMA_GREEN, context);
        } else {
            run(Control.write(value, K_GAMMA_G), K_GAMMA_G, context);
        }
    }

    public static void setKGammaRed(String value, Context context) {
        if (Utils.existFile(K_GAMMA_RED)) {
            run(Control.write(value, K_GAMMA_RED), K_GAMMA_RED, context);
        } else {
            run(Control.write(value, K_GAMMA_R), K_GAMMA_R, context);
        }
    }

    public static GammaProfiles.KGammaProfiles getKGammaProfiles(Context context) {
        if (GAMMA_PROFILES == null) {
            GAMMA_PROFILES = new GammaProfiles(Utils.readAssetFile(context, "gamma_profiles.json"));
        }
        return GAMMA_PROFILES.getKGamma();
    }

    public static String getKGammaBlue() {
        if (Utils.existFile(K_GAMMA_BLUE)) return Utils.readFile(K_GAMMA_BLUE);
        return Utils.readFile(K_GAMMA_B);
    }

    public static String getKGammaGreen() {
        if (Utils.existFile(K_GAMMA_GREEN)) return Utils.readFile(K_GAMMA_GREEN);
        return Utils.readFile(K_GAMMA_G);
    }

    public static String getKGammaRed() {
        if (Utils.existFile(K_GAMMA_RED)) return Utils.readFile(K_GAMMA_RED);
        return Utils.readFile(K_GAMMA_R);
    }

    public static boolean hasKGamma() {
        for (String file : sKGammaFiles) {
            if (Utils.existFile(file)) {
                return true;
            }
        }
        return false;
    }

    public static boolean supported() {
        return hasKGamma() || hasGammaControl() || hasDsiPanel();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.SCREEN, id, context);
    }

}
