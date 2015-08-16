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

import android.content.Context;

import com.grarak.kerneladiutor.utils.Utils;
import com.kerneladiutor.library.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 20.06.15.
 */
public class Downloads {

    private String link;

    public Downloads(Context context) {
        try {
            String json = Utils.existFile(context.getFilesDir() + "/downloads.json") ?
                    Tools.readFile(context.getFilesDir() + "/downloads.json", false) :
                    Utils.readAssetFile(context, "downloads.json");
            JSONArray devices = new JSONArray(json);
            for (int i = 0; i < devices.length(); i++) {
                JSONObject device = devices.getJSONObject(i);
                JSONArray vendors = device.getJSONArray("vendor");
                for (int x = 0; x < vendors.length(); x++)
                    if (vendors.getString(x).equals(Utils.getVendorName())) {
                        JSONArray names = device.getJSONArray("device");
                        for (int y = 0; y < names.length(); y++)
                            if (names.getString(y).equals(Utils.getDeviceName()))
                                link = device.getString("link");
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLink() {
        return link;
    }

    public boolean isSupported() {
        return link != null;
    }

    public static class Kernels {

        private JSONArray kernels;

        public Kernels(String json) {
            try {
                kernels = new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getLink(int position) {
            try {
                return kernels.getString(position);
            } catch (JSONException e) {
                return null;
            }
        }

        public int length() {
            return kernels.length();
        }

        public boolean readable() {
            return kernels != null;
        }

    }

    public static class KernelContent {

        private JSONObject kernel;
        private String json;

        public KernelContent(String json) {
            try {
                this.json = json;
                kernel = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return getString("name");
        }

        public String getShortDescription() {
            return getString("short_description");
        }

        public String getLongDescription() {
            return getString("long_description");
        }

        public String getLogo() {
            return getString("logo");
        }

        public String getXDA() {
            return getString("xda");
        }

        public String getGithub() {
            return getString("github");
        }

        public String getGooglePlus() {
            return getString("google_plus");
        }

        public String getPaypal() {
            return getString("paypal");
        }

        public List<Feature> getFeatures() {
            List<Feature> list = new ArrayList<>();
            try {
                JSONArray features = kernel.getJSONArray("features");
                for (int i = 0; i < features.length(); i++)
                    try {
                        list.add(new Feature(features.getJSONObject(i)));
                    } catch (JSONException ignored) {
                        try {
                            list.add(new Feature(features.getString(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        public List<Download> getDownloads() {
            List<Download> list = new ArrayList<>();
            try {
                JSONArray downloads = kernel.getJSONArray("downloads");
                for (int i = 0; i < downloads.length(); i++)
                    list.add(new Download(downloads.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        public String getJSON() {
            return json;
        }

        public boolean readable() {
            return kernel != null;
        }

        private String getString(String name) {
            try {
                return kernel.getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

    }

    public static class Feature {

        private String feature;
        private JSONObject features;

        public Feature(String feature) {
            this.feature = feature;
        }

        public Feature(JSONObject features) {
            this.features = features;
        }

        public String getItem() {
            try {
                if (feature != null) return feature;
                else if (features != null) return features.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public List<String> getItems() {
            List<String> list = new ArrayList<>();
            try {
                JSONArray items = features.getJSONArray("items");
                for (int i = 0; i < items.length(); i++)
                    list.add(items.getString(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        public boolean hasItems() {
            return features != null;
        }

    }

    public static class Download {

        private final JSONObject content;

        public Download(JSONObject content) {
            this.content = content;
        }

        public String getName() {
            return getString("name");
        }

        public String getDescription() {
            return getString("description");
        }

        public String getUrl() {
            return getString("url");
        }

        public String getMD5sum() {
            return getString("md5sum");
        }

        public String getInstallMethod() {
            return getString("install_method");
        }

        public List<String> getChangelogs() {
            List<String> list = new ArrayList<>();
            try {
                JSONArray changes = content.getJSONArray("changelog");
                for (int i = 0; i < changes.length(); i++) list.add(changes.getString(i));
            } catch (JSONException ignored) {
            }
            return list;
        }

        private String getString(String name) {
            try {
                return content.getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

    }

}
