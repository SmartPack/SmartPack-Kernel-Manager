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
package com.grarak.kerneladiutor.utils.other;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 23.07.16.
 */
public class Contributors {

    private JSONArray mContributorsJSON;

    public Contributors(String json) {
        if (json != null && json.isEmpty()) {
            return;
        }
        try {
            mContributorsJSON = new JSONArray(json);
        } catch (JSONException ignored) {
        }
    }

    public List<Contributor> getContributors() {
        List<Contributor> contributors = new ArrayList<>();
        for (int i = 0; i < length(); i++) {
            try {
                contributors.add(new Contributor(mContributorsJSON.getJSONObject(i)));
            } catch (JSONException ignored) {
            }
        }
        return contributors;
    }

    public int length() {
        return mContributorsJSON.length();
    }

    public boolean readable() {
        return mContributorsJSON != null;
    }

    public static class Contributor {

        private final JSONObject mContributorJSON;

        public Contributor(JSONObject object) {
            mContributorJSON = object;
        }

        public int getContributions() {
            return getInt("contributions");
        }

        public String getHtmlUrl() {
            return getString("html_url");
        }

        public String getAvatarUrl() {
            return getString("avatar_url");
        }

        public String getLogin() {
            return getString("login");
        }

        private String getString(String key) {
            try {
                return mContributorJSON.getString(key);
            } catch (JSONException ignored) {
                return null;
            }
        }

        private int getInt(String key) {
            try {
                return mContributorJSON.getInt(key);
            } catch (JSONException ignored) {
                return 0;
            }
        }

    }

}
