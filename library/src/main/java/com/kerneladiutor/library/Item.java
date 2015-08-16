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

package com.kerneladiutor.library;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by willi on 11.08.15.
 */

/**
 * This parent class allows you to add settings/cards to your Tab {@link Tab}.
 *
 * <p>
 * There are following Items available:
 * - Simple {@link com.kerneladiutor.library.items.Simple}
 * - Divider {@link com.kerneladiutor.library.items.Divider}
 * - EditText {@link com.kerneladiutor.library.items.EditText}
 * - Information {@link com.kerneladiutor.library.items.Information}
 * - Popup {@link com.kerneladiutor.library.items.Popup}
 * - SeekBar {@link com.kerneladiutor.library.items.SeekBar}
 * - Switcher {@link com.kerneladiutor.library.items.Switcher}
 * - Progress {@link com.kerneladiutor.library.items.Progress}
 *
 * <p>
 * For more information please read the comments in each class
 */
public abstract class Item implements Parcelable {

    /**
     * Whether the item takes up the whole weight of the device
     */
    private boolean mFullSpan;

    /**
     * An unique tag, so it's possible to distinguish items
     */
    private String mTag;

    /**
     * A pedingintent which will get triggered when the item gets touched
     */
    private PendingIntent mPendingIntent;

    /**
     * An to remember in which position the item was
     */
    private int id;

    /**
     * When triggering a the refresh pendingintent, we need to pass over the parent tab
     */
    private Tab mTab;

    /**
     * Needed to send broadcasts
     */
    private Context mContext;

    /**
     * See {@link #mFullSpan}
     *
     * @param fullSpan true = use whole weight, false = share with other items
     * @return this {@link Item}
     */
    public Item setFullSpan(boolean fullSpan) {
        mFullSpan = fullSpan;
        update();
        return this;
    }

    /**
     * See {@link #mFullSpan}
     *
     * @return {@link #mFullSpan}
     */
    public boolean isFullSpan() {
        return mFullSpan;
    }

    /**
     * See {@link #mPendingIntent}
     *
     * @param pendingIntent pendingintent which will get triggered
     * @param tag           an unique tag to identify this item
     * @return this {@link Item}
     */
    public Item setOnClickPendingListener(PendingIntent pendingIntent, String tag) {
        mPendingIntent = pendingIntent;
        mTag = tag;
        update();
        return this;
    }

    /**
     * Get onclick pendingintent
     *
     * @return pendintent
     */
    public PendingIntent getOnClickPendingListener() {
        return mPendingIntent;
    }

    /**
     * Get unique tag
     *
     * @return tag
     */
    public String getTag() {
        return mTag;
    }

    /**
     * See {@link #id}
     *
     * @param i position of item
     * @return this {@link Item}
     */
    protected Item setId(int i) {
        id = i;
        return this;
    }

    /**
     * Get id aka position
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns Tag which is stored in intent
     *
     * @param intent intent that contains Tag
     * @return Tag
     */
    public static String getTagEvent(Intent intent) {
        return intent.getStringExtra(com.kerneladiutor.library.action.Intent.TAG);
    }

    /**
     * Returns Tab which is stored in intent
     *
     * @param intent intent that contains Tab
     * @return Tab
     */
    public static Tab getTabEvent(Intent intent) {
        return intent.getParcelableExtra(com.kerneladiutor.library.action.Intent.TAB);
    }

    /**
     * See {@link #mTab}
     *
     * @param tab parent Tab
     * @return this {@link Item}
     */
    protected Item setTab(Tab tab) {
        mTab = tab;
        mContext = tab.getContext();
        return this;
    }

    /**
     * Gets called when specific things change, for example {@link #setFullSpan(boolean)}
     *
     * @return this {@link Item}
     */
    protected Item update() {
        if (mTab != null) {
            Intent intent = new Intent(com.kerneladiutor.library.action.Intent.RECEIVE_UPDATE);
            intent.putExtra(com.kerneladiutor.library.action.Intent.TAB, mTab);
            intent.putExtra(com.kerneladiutor.library.action.Intent.ITEM, this);
            mContext.sendBroadcast(intent);
        }
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static void readFromParcel(Parcel parcel, Item item) {
        item.setFullSpan(parcel.readByte() == 1);
        item.setOnClickPendingListener((PendingIntent) parcel.readParcelable(PendingIntent.class.getClassLoader()), parcel.readString());
        item.setId(parcel.readInt());
    }

    public static void writeToParcel(Parcel parcel, int i, Item item) {
        parcel.writeByte((byte) (item.isFullSpan() ? 1 : i));
        parcel.writeParcelable(item.getOnClickPendingListener(), i);
        parcel.writeString(item.getTag());
        parcel.writeInt(item.getId());
    }

}
