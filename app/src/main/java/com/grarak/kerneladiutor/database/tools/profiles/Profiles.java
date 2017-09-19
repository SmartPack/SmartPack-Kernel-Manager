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
package com.grarak.kerneladiutor.database.tools.profiles;

import android.content.Context;

import com.grarak.kerneladiutor.database.Provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 10.07.16.
 */
public class Profiles extends Provider {

    public static final int VERSION = 1;

    public Profiles(Context context) {
        super(context.getFilesDir() + "/profiles.json", VERSION);
    }

    @Override
    public DBJsonItem getItem(JSONObject item) {
        return new ProfileItem(item);
    }

    public void putProfile(String name, LinkedHashMap<String, String> commands) {
        try {
            JSONObject items = new JSONObject();
            items.put("name", name);

            ProfileItem profileItem = new ProfileItem(items);
            for (String id : commands.keySet()) {
                profileItem.putCommand(new ProfileItem.CommandItem(id, commands.get(id)));
            }

            putItem(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<ProfileItem> getAllProfiles() {
        List<ProfileItem> items = new ArrayList<>();
        for (DBJsonItem jsonItem : getAllItems()) {
            items.add((ProfileItem) jsonItem);
        }
        return items;
    }

    public static class ProfileItem extends DBJsonItem {

        private JSONArray mCommands;

        private ProfileItem(JSONObject object) {
            super(object);
            try {
                mCommands = object.getJSONArray("commands");
            } catch (JSONException ignored) {
                try {
                    mCommands = new JSONArray();
                    object.put("commands", mCommands);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public ProfileItem(String name, List<String> commands) {
            try {
                mCommands = new JSONArray();
                for (String command : commands) {
                    mCommands.put(command);
                }
                getItem().put("name", name);
                getItem().put("commands", mCommands);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return getString("name");
        }

        public List<CommandItem> getCommands() {
            List<CommandItem> list = new ArrayList<>();
            try {
                for (int i = 0; i < mCommands.length(); i++) {
                    CommandItem commandItem = new CommandItem(mCommands.getJSONObject(i));
                    if (commandItem.readable()) {
                        list.add(commandItem);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        public boolean isOnBootEnabled() {
            try {
                return getItem().getBoolean("onboot");
            } catch (JSONException ignored) {
                return false;
            }
        }

        public void enableOnBoot(boolean enable) {
            try {
                getItem().put("onboot", enable);
            } catch (JSONException ignored) {
            }
        }

        public void putCommand(CommandItem commandItem) {
            try {
                JSONObject item = new JSONObject();
                item.put("path", commandItem.getPath());
                item.put("command", commandItem.getCommand());
                mCommands.put(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void delete(CommandItem commandItem) {
            JSONArray newCommands = new JSONArray();
            for (int i = 0; i < mCommands.length(); i++) {
                try {
                    JSONObject object = mCommands.getJSONObject(i);
                    CommandItem item = new CommandItem(object);
                    if (!item.getPath().equals(commandItem.getPath())
                            && !item.getCommand().equals(commandItem.getCommand())) {
                        newCommands.put(object);
                    }
                } catch (JSONException ignored) {
                }
            }
            try {
                getItem().put("commands", mCommands = newCommands);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static class CommandItem {
            private String mPath;
            private String mCommand;

            private CommandItem(JSONObject item) {
                try {
                    mPath = item.getString("path");
                    mCommand = item.getString("command");
                } catch (JSONException ignored) {
                }
            }

            public CommandItem(String path, String command) {
                mPath = path;
                mCommand = command;
            }

            public String getPath() {
                return mPath;
            }

            public String getCommand() {
                return mCommand;
            }

            private boolean readable() {
                return mPath != null && mCommand != null;
            }
        }

    }

}
