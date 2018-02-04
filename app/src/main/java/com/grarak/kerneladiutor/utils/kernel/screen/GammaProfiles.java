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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by willi on 30.03.15.
 */
public class GammaProfiles {

    private static final String TAG = GammaProfiles.class.getSimpleName();

    private JSONObject JSON;
    private KGammaProfiles kgammaProfiles;
    private GammaControlProfiles gammaControlProfiles;
    private DsiPanelProfiles dsiPanelProfiles;

    GammaProfiles(String json) {
        try {
            JSON = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read gamma profiles");
            e.printStackTrace();
        }
    }

    KGammaProfiles getKGamma() {
        try {
            if (kgammaProfiles == null && JSON != null) {
                kgammaProfiles = new KGammaProfiles(JSON.getJSONArray("k_gamma"));
            }
            return kgammaProfiles;
        } catch (JSONException e) {
            return null;
        }
    }

    GammaControlProfiles getGammaControl() {
        try {
            if (gammaControlProfiles == null && JSON != null) {
                gammaControlProfiles = new GammaControlProfiles(JSON.getJSONArray("gammacontrol"));
            }
            return gammaControlProfiles;
        } catch (JSONException e) {
            return null;
        }
    }

    DsiPanelProfiles getDsiPanelProfiles() {
        try {
            if (dsiPanelProfiles == null && JSON != null) {
                dsiPanelProfiles = new DsiPanelProfiles(JSON.getJSONArray("dsi_panel"));
            }
            return dsiPanelProfiles;
        } catch (JSONException e) {
            return null;
        }
    }

    public void refresh(String json) {
        try {
            JSON = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read gamma profiles");
            JSON = null;
        }
    }

    public boolean readable() {
        return JSON != null;
    }

    public static abstract class GammaProfile {

        private final JSONArray JSON;

        GammaProfile(JSONArray jsonArray) {
            JSON = jsonArray;
        }

        public String getName(int position) {
            return getString("name", position);
        }

        protected String getString(String name, int position) {
            try {
                return JSON.getJSONObject(position).getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

        public int length() {
            return JSON.length();
        }
    }

    public static class KGammaProfiles extends GammaProfile {

        KGammaProfiles(JSONArray jsonArray) {
            super(jsonArray);
        }

        String getGammaRed(int position) {
            return getString("gamma_r", position);
        }

        String getGammaGreen(int position) {
            return getString("gamma_g", position);
        }

        String getGammaBlue(int position) {
            return getString("gamma_b", position);
        }

        String getKCAL(int position) {
            return getString("kcal", position);
        }

    }

    public static class GammaControlProfiles extends GammaProfile {

        GammaControlProfiles(JSONArray jsonArray) {
            super(jsonArray);
        }

        String getSaturation(int position) {
            return getString("saturation", position);
        }

        String getBrightness(int position) {
            return getString("brightness", position);
        }

        String getContrast(int position) {
            return getString("contrast", position);
        }

        String getBlueWhites(int position) {
            return getString("blue_whites", position);
        }

        String getBlueBlacks(int position) {
            return getString("blue_blacks", position);
        }

        String getBlueMids(int position) {
            return getString("blue_mids", position);
        }

        String getBlueGreys(int position) {
            return getString("blue_greys", position);
        }

        String getGreenWhites(int position) {
            return getString("green_whites", position);
        }

        String getGreenBlacks(int position) {
            return getString("green_blacks", position);
        }

        String getGreenMids(int position) {
            return getString("green_mids", position);
        }

        String getGreenGreys(int position) {
            return getString("green_greys", position);
        }

        String getRedWhites(int position) {
            return getString("red_whites", position);
        }

        String getRedBlacks(int position) {
            return getString("red_blacks", position);
        }

        String getRedMids(int position) {
            return getString("red_mids", position);
        }

        String getRedGreys(int position) {
            return getString("red_greys", position);
        }

        String getKCAL(int position) {
            return getString("kcal", position);
        }

    }

    public static class DsiPanelProfiles extends GammaProfile {

        DsiPanelProfiles(JSONArray jsonArray) {
            super(jsonArray);
        }

        String getWhitePoint(int position) {
            return getString("white_point", position);
        }

        String getBlueNegative(int position) {
            return getString("blue_negative", position);
        }

        String getBluePositive(int position) {
            return getString("blue_positive", position);
        }

        String getGreenNegative(int position) {
            return getString("green_negative", position);
        }

        String getGreenPositive(int position) {
            return getString("green_positive", position);
        }

        String getRedNegative(int position) {
            return getString("red_negative", position);
        }

        String getRedPositive(int position) {
            return getString("red_positive", position);
        }

    }

}
