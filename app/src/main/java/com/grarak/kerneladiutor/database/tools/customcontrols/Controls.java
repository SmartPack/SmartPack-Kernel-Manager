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
package com.grarak.kerneladiutor.database.tools.customcontrols;

import android.content.Context;

import com.grarak.kerneladiutor.database.Provider;
import com.grarak.kerneladiutor.utils.tools.customcontrols.Items;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 02.07.16.
 */
public class Controls extends Provider {

    public static final int VERSION = 1;

    public Controls(Context context) {
        super(context.getFilesDir() + "/controls.json", 1);
    }

    @Override
    public DBJsonItem getItem(JSONObject item) {
        return new ControlItem(item);
    }

    public void putItem(HashMap<String, Object> items) {
        JSONObject object = new JSONObject();
        for (String key : items.keySet()) {
            try {
                object.put(key, items.get(key));
            } catch (JSONException ignored) {
            }
        }
        putItem(object);
    }

    public List<ControlItem> getAllControls() {
        List<ControlItem> items = new ArrayList<>();
        for (DBJsonItem item : getAllItems()) {
            items.add(new ControlItem(item.getItem()));
        }
        return items;
    }

    public static class ControlItem extends DBJsonItem {

        private ControlItem(JSONObject object) {
            super(object);
        }

        public String getId() {
            return getString("id");
        }

        public int getUniqueId() {
            try {
                return getItem().getInt("uniqueId");
            } catch (JSONException ignored) {
                return 0;
            }
        }

        public String getTitle() {
            return getString("title");
        }

        public String getDescription() {
            return getString("description");
        }

        public String getApply() {
            return getString("apply");
        }

        public Items.Control getControl() {
            return Items.Control.getControl(getString("id"));
        }

        public boolean isOnBootEnabled() {
            try {
                return getItem().getBoolean("onboot");
            } catch (JSONException ignored) {
                return false;
            }
        }

        public String getArguments() {
            return getString("arguments");
        }

        public void enableOnBoot(boolean enable) {
            try {
                getItem().put("onboot", enable);
            } catch (JSONException ignored) {
            }
        }

        public void setArguments(String arguments) {
            try {
                getItem().put("arguments", arguments);
            } catch (JSONException ignored) {
            }
        }

    }

}
