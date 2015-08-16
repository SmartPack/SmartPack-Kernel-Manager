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

package com.grarak.kerneladiutor.utils.json;

import android.util.Log;

import com.grarak.kerneladiutor.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by willi on 30.03.15.
 */
public class GammaProfiles {

    private JSONObject JSON;
    private KGammaProfiles kgammaProfiles;
    private GammaControlProfiles gammaControlProfiles;
    private DsiPanelProfiles dsiPanelProfiles;

    public GammaProfiles(String json) {
        try {
            JSON = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(Constants.TAG, "Failed to read gamma profiles");
            e.printStackTrace();
        }
    }

    public KGammaProfiles getKGamma() {
        try {
            if (kgammaProfiles == null && JSON != null)
                kgammaProfiles = new KGammaProfiles(JSON.getJSONArray("k_gamma"));
            return kgammaProfiles;
        } catch (JSONException e) {
            return null;
        }
    }

    public GammaControlProfiles getGammaControl() {
        try {
            if (gammaControlProfiles == null && JSON != null)
                gammaControlProfiles = new GammaControlProfiles(JSON.getJSONArray("gammacontrol"));
            return gammaControlProfiles;
        } catch (JSONException e) {
            return null;
        }
    }

    public DsiPanelProfiles getDsiPanelProfiles() {
        try {
            if (dsiPanelProfiles == null && JSON != null)
                dsiPanelProfiles = new DsiPanelProfiles(JSON.getJSONArray("dsi_panel"));
            return dsiPanelProfiles;
        } catch (JSONException e) {
            return null;
        }
    }

    public void refresh(String json) {
        try {
            JSON = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(Constants.TAG, "Failed to read gamma profiles");
            JSON = null;
        }
    }

    public boolean readable() {
        return JSON != null;
    }

    public interface GammaProfile {
        String getName(int position);

        int length();
    }

    public static class KGammaProfiles implements GammaProfile {

        private final JSONArray JSON;

        public KGammaProfiles(JSONArray kgammaArray) {
            JSON = kgammaArray;
        }

        public String getGammaRed(int position) {
            return getString("gamma_r", position);
        }

        public String getGammaGreen(int position) {
            return getString("gamma_g", position);
        }

        public String getGammaBlue(int position) {
            return getString("gamma_b", position);
        }

        public String getKCAL(int position) {
            return getString("kcal", position);
        }

        @Override
        public String getName(int position) {
            return getString("name", position);
        }

        private String getString(String name, int position) {
            try {
                return JSON.getJSONObject(position).getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public int length() {
            return JSON.length();
        }

    }

    public static class GammaControlProfiles implements GammaProfile {

        private final JSONArray JSON;

        public GammaControlProfiles(JSONArray gammacontrolArray) {
            JSON = gammacontrolArray;
        }

        public String getSaturation(int position) {
            return getString("saturation", position);
        }

        public String getBrightness(int position) {
            return getString("brightness", position);
        }

        public String getContrast(int position) {
            return getString("contrast", position);
        }

        public String getBlueWhites(int position) {
            return getString("blue_whites", position);
        }

        public String getBlueBlacks(int position) {
            return getString("blue_blacks", position);
        }

        public String getBlueMids(int position) {
            return getString("blue_mids", position);
        }

        public String getBlueGreys(int position) {
            return getString("blue_greys", position);
        }

        public String getGreenWhites(int position) {
            return getString("green_whites", position);
        }

        public String getGreenBlacks(int position) {
            return getString("green_blacks", position);
        }

        public String getGreenMids(int position) {
            return getString("green_mids", position);
        }

        public String getGreenGreys(int position) {
            return getString("green_greys", position);
        }

        public String getRedWhites(int position) {
            return getString("red_whites", position);
        }

        public String getRedBlacks(int position) {
            return getString("red_blacks", position);
        }

        public String getRedMids(int position) {
            return getString("red_mids", position);
        }

        public String getRedGreys(int position) {
            return getString("red_greys", position);
        }

        public String getKCAL(int position) {
            return getString("kcal", position);
        }

        @Override
        public String getName(int position) {
            return getString("name", position);
        }

        private String getString(String name, int position) {
            try {
                return JSON.getJSONObject(position).getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public int length() {
            return JSON.length();
        }

    }

    public static class DsiPanelProfiles implements GammaProfile {

        private final JSONArray JSON;

        public DsiPanelProfiles(JSONArray dsiPanelArray) {
            JSON = dsiPanelArray;
        }

        public String getWhitePoint(int position) {
            return getString("white_point", position);
        }

        public String getBlueNegative(int position) {
            return getString("blue_negative", position);
        }

        public String getBluePositive(int position) {
            return getString("blue_positive", position);
        }

        public String getGreenNegative(int position) {
            return getString("green_negative", position);
        }

        public String getGreenPositive(int position) {
            return getString("green_positive", position);
        }

        public String getRedNegative(int position) {
            return getString("red_negative", position);
        }

        public String getRedPositive(int position) {
            return getString("red_positive", position);
        }

        @Override
        public String getName(int position) {
            return getString("name", position);
        }

        private String getString(String name, int position) {
            try {
                return JSON.getJSONObject(position).getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public int length() {
            return JSON.length();
        }

    }

}
