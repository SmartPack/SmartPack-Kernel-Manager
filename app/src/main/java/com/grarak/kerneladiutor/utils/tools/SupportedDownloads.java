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
package com.grarak.kerneladiutor.utils.tools;

import android.content.Context;

import com.grarak.kerneladiutor.utils.Device;
import com.grarak.kerneladiutor.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 06.07.16.
 */
public class SupportedDownloads {

    private String mLink;

    public SupportedDownloads(Context context) {
        try {
            String json = Utils.existFile(context.getFilesDir() + "/downloads.json") ?
                    Utils.readFile(context.getFilesDir() + "/downloads.json", false) :
                    Utils.readAssetFile(context, "downloads.json");
            JSONArray devices = new JSONArray(json);
            for (int i = 0; i < devices.length(); i++) {
                JSONObject device = devices.getJSONObject(i);
                JSONArray vendors = device.getJSONArray("vendor");
                for (int x = 0; x < vendors.length(); x++) {
                    if (vendors.getString(x).equals(Device.getVendor())) {
                        JSONArray names = device.getJSONArray("device");
                        for (int y = 0; y < names.length(); y++) {
                            if (names.getString(y).equals(Device.getDeviceName())) {
                                mLink = device.getString("link");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utils.toast("Failed to read downloads.json " + e.getMessage(), context);
        }
    }

    public String getLink() {
        return mLink;
    }

    public static class Kernels {

        private JSONArray mKernels;

        public Kernels(String json) {
            try {
                mKernels = new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getLink(int position) {
            try {
                return mKernels.getString(position);
            } catch (JSONException e) {
                return null;
            }
        }

        public int length() {
            return mKernels.length();
        }

        public boolean readable() {
            return mKernels != null;
        }

    }

    public static class KernelContent {

        private JSONObject mKernel;
        private String mJson;

        public KernelContent(String json) {
            try {
                mJson = json;
                mKernel = new JSONObject(json);
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

        public String getGitHub() {
            return getString("github");
        }

        public String getGooglePlus() {
            return getString("google_plus");
        }

        public String getPayPal() {
            return getString("paypal");
        }

        public List<Feature> getFeatures() {
            List<Feature> list = new ArrayList<>();
            try {
                JSONArray features = mKernel.getJSONArray("features");
                for (int i = 0; i < features.length(); i++) {
                    try {
                        list.add(new Feature(features.getJSONObject(i)));
                    } catch (JSONException ignored) {
                        try {
                            list.add(new Feature(features.getString(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                JSONArray downloads = mKernel.getJSONArray("downloads");
                for (int i = 0; i < downloads.length(); i++) {
                    list.add(new Download(downloads.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        public String getJSON() {
            return mJson;
        }

        public boolean readable() {
            return mKernel != null;
        }

        private String getString(String name) {
            try {
                return mKernel.getString(name);
            } catch (JSONException e) {
                return null;
            }
        }

        public static class Feature {

            private String mFeature;
            private JSONObject mFeatures;

            public Feature(String feature) {
                mFeature = feature;
            }

            public Feature(JSONObject features) {
                mFeatures = features;
            }

            public String getItem() {
                try {
                    if (mFeature != null) {
                        return mFeature;
                    } else if (mFeatures != null) {
                        return mFeatures.getString("name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public List<String> getItems() {
                List<String> list = new ArrayList<>();
                try {
                    JSONArray items = mFeatures.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++)
                        list.add(items.getString(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return list;
            }

            public boolean hasItems() {
                return mFeatures != null;
            }

        }

        public static class Download {

            private final JSONObject mContent;

            public Download(JSONObject content) {
                mContent = content;
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
                    JSONArray changes = mContent.getJSONArray("changelog");
                    for (int i = 0; i < changes.length(); i++) {
                        list.add(changes.getString(i));
                    }
                } catch (JSONException ignored) {
                }
                return list;
            }

            private String getString(String name) {
                try {
                    return mContent.getString(name);
                } catch (JSONException e) {
                    return null;
                }
            }

        }
    }

}
