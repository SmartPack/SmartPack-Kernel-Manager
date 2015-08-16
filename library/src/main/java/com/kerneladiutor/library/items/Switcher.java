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

package com.kerneladiutor.library.items;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Parcel;

import com.kerneladiutor.library.Item;

/**
 * Created by willi on 12.08.15.
 */

/**
 * An on and off switcher
 */
public class Switcher extends Item {

    public static final String CHECKED = "com.kerneladiutor.library.items.CHECKED";

    private String mTitle;
    private String mDescription;
    private boolean mChecked;

    public Switcher setTitle(String title) {
        mTitle = title;
        update();
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Switcher setDescription(String description) {
        mDescription = description;
        update();
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public Switcher setChecked(boolean checked) {
        mChecked = checked;
        update();
        return this;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public Switcher setOnCheckedPendingListener(PendingIntent pendingListener, String tag) {
        setOnClickPendingListener(pendingListener, tag);
        return this;
    }

    public static boolean getCheckedEvent(Intent intent) {
        return intent.getBooleanExtra(CHECKED, false);
    }

    public static final Creator<Switcher> CREATOR = new Creator<Switcher>() {
        @Override
        public Switcher createFromParcel(Parcel parcel) {
            Switcher switcher = new Switcher();
            Item.readFromParcel(parcel, switcher);
            switcher.setTitle(parcel.readString());
            switcher.setDescription(parcel.readString());
            switcher.setChecked(parcel.readByte() == 1);
            return switcher;
        }

        @Override
        public Switcher[] newArray(int i) {
            return new Switcher[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getTitle());
        parcel.writeString(getDescription());
        parcel.writeByte((byte) (isChecked() ? 1 : 0));
    }

}
