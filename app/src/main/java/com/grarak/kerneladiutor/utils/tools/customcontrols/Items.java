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
package com.grarak.kerneladiutor.utils.tools.customcontrols;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.grarak.kerneladiutor.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by willi on 30.06.16.
 */
public class Items {

    public enum Control {
        SWITCH(0, "switch", R.string.control_switch),
        SEEKBAR(1, "seekbar", R.string.control_seekbar),
        GENERIC(2, "generic", R.string.control_generic);

        public int mId;
        public String mName;
        public int mRes;

        Control(int id, String nameText, int res) {
            mId = id;
            mName = nameText;
            mRes = res;
        }

        public static Control getControl(int id) {
            for (Control c : Control.values()) {
                if (c.mId == id) {
                    return c;
                }
            }
            return null;
        }

        public static Control getControl(String name) {
            for (Control c : Control.values()) {
                if (c.mName.equals(name)) {
                    return c;
                }
            }
            return null;
        }

        public int getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public int getRes() {
            return mRes;
        }

    }

    private static final HashMap<Control, ArrayList<Setting>> sControls = new HashMap<>();

    private static final ArrayList<Setting> sSwitchSettings = new ArrayList<>();
    private static final ArrayList<Setting> sSeekBarSettings = new ArrayList<>();
    private static final ArrayList<Setting> sGenericSettings = new ArrayList<>();

    static {
        sSwitchSettings.add(new Setting("id", 0, 0, 0, "switch", null, null, false, false, Setting.Unit.ID));
        sSwitchSettings.add(new Setting("title", R.string.title, "", true, Setting.Unit.STRING));
        sSwitchSettings.add(new Setting("description", R.string.description, "", false, Setting.Unit.STRING));
        sSwitchSettings.add(new Setting("enable", R.string.enabled, R.string.switch_enabled_summary, "#!/system/bin/sh\n\n#echo 1 (Enabled)\n#echo 0 (Disabled)\n\necho 0 #disabled", true, true, Setting.Unit.BOOLEAN));
        sSwitchSettings.add(new Setting("apply", R.string.applying, R.string.switch_apply_summary, "#!/system/bin/sh\n\n#status=$1\n\n#$status is either 1 or 0\n#echo $status > /sys/class/..", true, true, Setting.Unit.APPLY));

        sSeekBarSettings.add(new Setting("id", 0, 0, 0, "seekbar", null, null, false, false, Setting.Unit.ID));
        sSeekBarSettings.add(new Setting("title", R.string.title, "", true, Setting.Unit.STRING));
        sSeekBarSettings.add(new Setting("description", R.string.description, "", false, Setting.Unit.STRING));
        sSeekBarSettings.add(new Setting("min", R.string.seekbar_min, "0", true, Setting.Unit.INTEGER));
        sSeekBarSettings.add(new Setting("max", R.string.seekbar_max, "100", true, Setting.Unit.INTEGER));
        sSeekBarSettings.add(new Setting("progress", R.string.seekbar_progress, R.string.seekbar_progress_summary, "#!/system/bin/sh\n\necho 0", true, true, Setting.Unit.INTEGER));
        sSeekBarSettings.add(new Setting("apply", R.string.applying, R.string.seekbar_apply_summary, "#!/system/bin/sh\n\n#progress=$1\n\n#echo $progress > /sys/class/..", true, true, Setting.Unit.APPLY));

        sGenericSettings.add(new Setting("id", 0, 0, 0, "generic", null, null, false, false, Setting.Unit.ID));
        sGenericSettings.add(new Setting("title", R.string.title, "", true, Setting.Unit.STRING));
        sGenericSettings.add(new Setting("description", R.string.description, "", false, Setting.Unit.STRING));
        sGenericSettings.add(new Setting("value", R.string.generic_value, R.string.generic_value_summary, "#!/system/bin/sh\n\necho foo", true, true, Setting.Unit.STRING));
        sGenericSettings.add(new Setting("apply", R.string.applying, R.string.generic_apply_summary, "#!/system/bin/sh\n\n#value=$1\n\n#echo value > /sys/class/..", true, true, Setting.Unit.APPLY));
    }

    static {
        sControls.put(Control.SWITCH, sSwitchSettings);
        sControls.put(Control.SEEKBAR, sSeekBarSettings);
        sControls.put(Control.GENERIC, sGenericSettings);
    }

    public static ArrayList<Setting> getSettings(Control control) {
        return sControls.get(control);
    }

    public static ArrayList<Setting> getSettings(int control) {
        return getSettings(Control.getControl(control));
    }

    public static class Setting implements Parcelable {

        public enum Unit {
            BOOLEAN(0),
            INTEGER(1),
            APPLY(2),
            ID(3),
            STRING(4);

            private int mId;

            Unit(int id) {
                mId = id;
            }

            public static Unit getUnit(int id) {
                for (Unit u : Unit.values()) {
                    if (u.mId == id) {
                        return u;
                    }
                }
                return null;
            }

            public int getId() {
                return mId;
            }
        }

        private final String mId;
        private final int mUniqueId;
        private final int mName;
        private final int mDescription;
        private final String mNameText;
        private final String mDescriptionText;
        private final String mDefault;
        private final boolean mRequired;
        private final boolean mScript;
        private final Unit mUnit;

        public Setting(String id, int name, String defaulValue, boolean required, Unit unit) {
            this(id, name, 0, defaulValue, required, unit);
        }

        public Setting(String id, int name, int description, String defaulValue, boolean required, Unit unit) {
            this(id, name, description, defaulValue, required, false, unit);
        }

        public Setting(String id, int name, int description, String defaulValue, boolean required, boolean script, Unit unit) {
            this(id, 0, name, description, null, null, defaulValue, required, script, unit);
        }

        public Setting(String id, int uniqueId, int name, int description, String nameText, String descriptionText,
                       String defaultValue, boolean required, boolean script, Unit unit) {
            mId = id;
            mUniqueId = uniqueId;
            mName = name;
            mDescription = description;
            mNameText = nameText;
            mDescriptionText = descriptionText;
            mDefault = defaultValue;
            mRequired = required;
            mScript = script;
            mUnit = unit;
        }

        public String getId() {
            return mId;
        }

        public int getUniqueId() {
            return mUniqueId;
        }

        public CharSequence getName(Context context) {
            return context == null || mName == 0 ? mNameText : context.getString(mName);
        }

        public CharSequence getDescription(Context context) {
            return context == null || mDescription == 0 ? mDescriptionText : context.getString(mDescription);
        }

        public String getDefault() {
            return mDefault;
        }

        public boolean isRequired() {
            return mRequired;
        }

        public boolean isScript() {
            return mScript;
        }

        public Unit getUnit() {
            return mUnit;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mId);
            dest.writeInt(mUniqueId);
            dest.writeInt(mName);
            dest.writeInt(mDescription);
            dest.writeString(mNameText);
            dest.writeString(mDescriptionText);
            dest.writeString(mDefault);
            dest.writeByte((byte) (mRequired ? 1 : 0));
            dest.writeByte((byte) (mScript ? 1 : 0));
            dest.writeInt(mUnit == null ? -1 : mUnit.getId());
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Setting createFromParcel(Parcel source) {
                String id = source.readString();
                int uniqueId = source.readInt();
                int name = source.readInt();
                int description = source.readInt();
                String nameText = source.readString();
                String descriptionText = source.readString();
                String def = source.readString();
                boolean required = source.readByte() == 1;
                boolean script = source.readByte() == 1;
                int unit = source.readInt();
                return new Setting(id, uniqueId, name, description, nameText, descriptionText, def,
                        required, script, Unit.getUnit(unit));
            }

            @Override
            public Setting[] newArray(int size) {
                return new Setting[size];
            }
        };

    }

}
