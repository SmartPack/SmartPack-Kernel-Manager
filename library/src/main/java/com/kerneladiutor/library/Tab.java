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
import android.os.Parcel;
import android.os.Parcelable;

import com.kerneladiutor.library.items.Divider;
import com.kerneladiutor.library.items.EditText;
import com.kerneladiutor.library.items.Information;
import com.kerneladiutor.library.items.Popup;
import com.kerneladiutor.library.items.Progress;
import com.kerneladiutor.library.items.SeekBar;
import com.kerneladiutor.library.items.Simple;
import com.kerneladiutor.library.items.Switcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 11.08.15.
 */

/**
 * This class is needed to create new tabs in KA Plugins section.
 * Each Tab can has its own items.
 *
 * <p>
 * You can tell KA to create this with {@link PluginManager}.
 */
public class Tab implements Parcelable {

    /**
     * Needed for reading out Packagename
     */
    private Context mContext;

    /**
     * Title of this Tab
     */
    private String mTitle;

    /**
     * Collection of items
     */
    private List<Item> mItems;

    /**
     * Needed for apply on boot functionality
     */
    private String mPackageName;

    /**
     * Needed to monitor values/settings
     */
    private PendingIntent mOnRefreshListenerPending;

    /**
     * Time between {@link #mOnRefreshListenerPending} gets triggered (in milliseconds)
     */
    private int mRefreshTime;

    /**
     * Initialize a new tab
     *
     * @param context is needed to get the Packagename
     */
    public Tab(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
        if (context != null) mPackageName = mContext.getPackageName();
    }

    /**
     * Set title of your tab
     *
     * @param title String of title
     * @return this {@link Tab}
     */
    public Tab setTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * Same as above {@link #setTitle(String)}
     *
     * @param res resource id of your title
     * @return this {@link Tab}
     */
    public Tab setTitle(int res) {
        mTitle = getContext().getString(res);
        return this;
    }

    /**
     * Get your title
     *
     * @return title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Add item to tab
     *
     * @param item {@link Item}
     * @return this {@link Tab}
     */
    public Tab addItem(Item item) {
        mItems.add(item);
        return this;
    }

    /**
     * Add a list of items
     *
     * @param items List of items {@link Item}
     * @return this {@link Tab}
     */
    public Tab addItems(List<Item> items) {
        for (Item item : items) mItems.add(item);
        return this;
    }

    /**
     * Get an item
     *
     * @param position position of item {@link Item}
     * @return item {@link Item}
     */
    public Item getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Count of added items {@link Item}
     *
     * @return count
     */
    public int size() {
        return mItems.size();
    }

    /**
     * Get list of all added items {@link Item}
     *
     * @return list of items {@link Item}
     */
    public List<Item> getAllItems() {
        return mItems;
    }

    /**
     * Define Packagename
     *
     * @param packageName String of Packagename
     * @return this {@link Tab}
     */
    protected Tab setPackageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    /**
     * Get Packagename with title
     *
     * @return Packagename with title
     */
    public String getPackageName() {
        return mPackageName + getTitle();
    }

    /**
     * Get context which was previously defined {@link #Tab(Context)}
     *
     * @return context
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * KA will trigger this pendingintent frequently so you can update your items
     *
     * @param pendingListener pendingintent that will get triggered
     * @param refreshtime     how frequently the pendingintent will get triggered (in milliseconds)
     * @return this {@link Tab}
     */
    public Tab setOnRefreshPendingListener(PendingIntent pendingListener, int refreshtime) {
        mOnRefreshListenerPending = pendingListener;
        mRefreshTime = refreshtime;
        return this;
    }

    /**
     * Get previously defined pendingintent {@link #setOnRefreshPendingListener(PendingIntent, int)}
     *
     * @return pendingintent {@link #mOnRefreshListenerPending}
     */
    public PendingIntent getOnRefreshListenerPending() {
        return mOnRefreshListenerPending;
    }

    /**
     * Get previously defined refreshtime {@link #setOnRefreshPendingListener(PendingIntent, int)}
     *
     * @return refreshtime {@link #mRefreshTime}
     */
    public int getRefreshTime() {
        return mRefreshTime;
    }

    public static final Creator<Tab> CREATOR = new Creator<Tab>() {
        @Override
        public Tab createFromParcel(Parcel parcel) {
            Tab tab = new Tab(null);
            tab.setTitle(parcel.readString());

            List<Simple> simples = new ArrayList<>();
            List<Divider> dividers = new ArrayList<>();
            List<EditText> editTexts = new ArrayList<>();
            List<Information> informations = new ArrayList<>();
            List<Popup> popups = new ArrayList<>();
            List<SeekBar> seekBars = new ArrayList<>();
            List<Switcher> switchers = new ArrayList<>();
            List<Progress> progresses = new ArrayList<>();

            for (Parcelable simple : parcel.readParcelableArray(Simple.class.getClassLoader()))
                simples.add((Simple) simple);
            for (Parcelable divider : parcel.readParcelableArray(Divider.class.getClassLoader()))
                dividers.add((Divider) divider);
            for (Parcelable edittext : parcel.readParcelableArray(EditText.class.getClassLoader()))
                editTexts.add((EditText) edittext);
            for (Parcelable information : parcel.readParcelableArray(Information.class.getClassLoader()))
                informations.add((Information) information);
            for (Parcelable popup : parcel.readParcelableArray(Popup.class.getClassLoader()))
                popups.add((Popup) popup);
            for (Parcelable seekbar : parcel.readParcelableArray(SeekBar.class.getClassLoader()))
                seekBars.add((SeekBar) seekbar);
            for (Parcelable switcher : parcel.readParcelableArray(Switcher.class.getClassLoader()))
                switchers.add((Switcher) switcher);
            for (Parcelable progress : parcel.readParcelableArray(Progress.class.getClassLoader()))
                progresses.add((Progress) progress);

            List<Item> items = new ArrayList<>();
            items.addAll(simples);
            items.addAll(dividers);
            items.addAll(editTexts);
            items.addAll(informations);
            items.addAll(popups);
            items.addAll(seekBars);
            items.addAll(switchers);
            items.addAll(progresses);
            for (int i = 0; i < items.size(); i++)
                for (Item item : items) if (item.getId() == i) tab.addItem(item);

            tab.setPackageName(parcel.readString());
            tab.setOnRefreshPendingListener((PendingIntent) parcel.readParcelable(PendingIntent.class.getClassLoader()),
                    parcel.readInt());

            return tab;
        }

        @Override
        public Tab[] newArray(int i) {
            return new Tab[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(getTitle());

        List<Simple> simples = new ArrayList<>();
        List<Divider> dividers = new ArrayList<>();
        List<EditText> editTexts = new ArrayList<>();
        List<Information> informations = new ArrayList<>();
        List<Popup> popups = new ArrayList<>();
        List<SeekBar> seekBars = new ArrayList<>();
        List<Switcher> switchers = new ArrayList<>();
        List<Progress> progresses = new ArrayList<>();

        for (int i = 0; i < getAllItems().size(); i++) {
            Item item = getAllItems().get(i);
            if (item != null) {
                item.setId(i);
                item.setTab(this);
                if (item instanceof Simple)
                    simples.add((Simple) item);
                else if (item instanceof Divider)
                    dividers.add((Divider) item);
                else if (item instanceof EditText)
                    editTexts.add((EditText) item);
                else if (item instanceof Information)
                    informations.add((Information) item);
                else if (item instanceof Popup)
                    popups.add((Popup) item);
                else if (item instanceof SeekBar)
                    seekBars.add((SeekBar) item);
                else if (item instanceof Switcher)
                    switchers.add((Switcher) item);
                else if (item instanceof Progress)
                    progresses.add((Progress) item);
            }
        }

        parcel.writeParcelableArray(simples.toArray(new Simple[simples.size()]), flag);
        parcel.writeParcelableArray(dividers.toArray(new Divider[dividers.size()]), flag);
        parcel.writeParcelableArray(editTexts.toArray(new EditText[editTexts.size()]), flag);
        parcel.writeParcelableArray(informations.toArray(new Information[informations.size()]), flag);
        parcel.writeParcelableArray(popups.toArray(new Popup[popups.size()]), flag);
        parcel.writeParcelableArray(seekBars.toArray(new SeekBar[seekBars.size()]), flag);
        parcel.writeParcelableArray(switchers.toArray(new Switcher[switchers.size()]), flag);
        parcel.writeParcelableArray(progresses.toArray(new Progress[progresses.size()]), flag);
        parcel.writeString(mPackageName);
        parcel.writeParcelable(mOnRefreshListenerPending, flag);
        parcel.writeInt(mRefreshTime < 500 ? 1000 : mRefreshTime);
    }

}
