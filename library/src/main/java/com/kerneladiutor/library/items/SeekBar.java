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
 * Created by willi on 12.08.15.
 */

/**
 * A SeekBar like {@link android.widget.SeekBar}
 */
public class SeekBar extends Item {

    public static final String ITEM = "com.kerneladiutor.library.items.ITEM";
    public static final String POSITION = "com.kerneladiutor.library.items.POSITION";

    private final List<String> mItems;
    private String mUnit;

    private String mTitle;
    private String mDescription;

    private int mProgress = -2;

    private PendingIntent mOnChangePendingListener;
    private String mTag;

    public SeekBar(int min, int max) {
        this(min, max, null);
    }

    public SeekBar(int min, int max, String unit) {
        mUnit = unit;
        mItems = new ArrayList<>();
        for (int i = min; i <= max; i++) mItems.add(unit != null ? i + unit : String.valueOf(i));
    }

    public SeekBar(List<String> items) {
        mItems = items;
    }

    public List<String> getItems() {
        return mItems;
    }

    public SeekBar setTitle(String title) {
        mTitle = title;
        update();
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public SeekBar setDescription(String description) {
        mDescription = description;
        update();
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public SeekBar setItem(String item) {
        mProgress = mItems.indexOf(item);
        update();
        return this;
    }

    public SeekBar setItem(int item) {
        mProgress = mItems.indexOf(mUnit != null ? item + mUnit : String.valueOf(item));
        update();
        return this;
    }

    public SeekBar setProgress(int progress) {
        mProgress = progress;
        update();
        return this;
    }

    public int getProgress() {
        return mProgress;
    }

    public SeekBar setOnChangePendingListener(PendingIntent pendingIntent, String tag) {
        mOnChangePendingListener = pendingIntent;
        mTag = tag;
        update();
        return this;
    }

    public PendingIntent getOnChangePendingListener() {
        return mOnChangePendingListener;
    }

    public String getOnChangeTag() {
        return mTag;
    }

    public SeekBar setOnStopPendingListener(PendingIntent pendingListener, String tag) {
        setOnClickPendingListener(pendingListener, tag);
        return this;
    }

    public static String getItemEvent(Intent intent) {
        return intent.getStringExtra(ITEM);
    }

    public static int getPositionEvent(Intent intent) {
        return intent.getIntExtra(POSITION, 0);
    }

    public static final Creator<SeekBar> CREATOR = new Creator<SeekBar>() {
        @Override
        public SeekBar createFromParcel(Parcel parcel) {
            List<String> items = new ArrayList<>();
            parcel.readStringList(items);
            SeekBar seekBar = new SeekBar(items);
            Item.readFromParcel(parcel, seekBar);
            seekBar.setTitle(parcel.readString());
            seekBar.setDescription(parcel.readString());
            seekBar.setProgress(parcel.readInt());
            seekBar.setOnChangePendingListener((PendingIntent) parcel.readParcelable(PendingIntent.class.getClassLoader()),
                    parcel.readString());
            return seekBar;
        }

        @Override
        public SeekBar[] newArray(int i) {
            return new SeekBar[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(mItems);
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getTitle());
        parcel.writeString(getDescription());
        parcel.writeInt(getProgress());
        parcel.writeParcelable(getOnChangePendingListener(), i);
        parcel.writeString(getOnChangeTag());
    }

}
