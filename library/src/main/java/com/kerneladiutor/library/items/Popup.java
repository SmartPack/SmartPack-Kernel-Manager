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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 11.08.15.
 */

/**
 * This is similar to {@link android.widget.Spinner}
 */
public class Popup extends Item {

    public static final String POSITION = "com.kerneladiutor.library.items.POSITION";
    public static final String ITEM = "com.kerneladiutor.library.items.ITEM";

    private final List<String> mItems;
    private String mTitle;
    private String mDescription;
    private String mItem;

    public Popup(List<String> items) {
        mItems = items;
    }

    public List<String> getItems() {
        return mItems;
    }

    public Popup setTitle(String title) {
        mTitle = title;
        update();
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Popup setDescription(String description) {
        mDescription = description;
        update();
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public Popup setItem(String item) {
        mItem = item;
        update();
        return this;
    }

    public Popup setItem(int position) {
        mItem = getItems().get(position);
        update();
        return this;
    }

    public String getItem() {
        return mItem;
    }

    public Popup setOnItemSelectedPendingListener(PendingIntent pendingIntent, String tag) {
        setOnClickPendingListener(pendingIntent, tag);
        return this;
    }

    public static int getPositionEvent(Intent intent) {
        return intent.getIntExtra(POSITION, 0);
    }

    public static String getItemEvent(Intent intent) {
        return intent.getStringExtra(ITEM);
    }

    public static final Creator<Popup> CREATOR = new Creator<Popup>() {
        @Override
        public Popup createFromParcel(Parcel parcel) {
            List<String> items = new ArrayList<>();
            parcel.readStringList(items);
            Popup popup = new Popup(items);
            Item.readFromParcel(parcel, popup);
            popup.setTitle(parcel.readString()).setDescription(parcel.readString()).setItem(parcel.readString());
            return popup;
        }

        @Override
        public Popup[] newArray(int i) {
            return new Popup[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(getItems());
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getTitle());
        parcel.writeString(getDescription());
        parcel.writeString(getItem());
    }

}
