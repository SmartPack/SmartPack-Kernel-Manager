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

import android.os.Parcel;

import com.kerneladiutor.library.Item;

/**
 * Created by willi on 13.08.15.
 */

/**
 * This is similar to {@link android.widget.ProgressBar}
 */
public class Progress extends Item {

    private int mProgress;
    private String mText;
    private int mMax;

    public Progress setProgress(int progress) {
        mProgress = progress;
        update();
        return this;
    }

    public int getProgress() {
        return mProgress;
    }

    public Progress setText(String text) {
        mText = text;
        update();
        return this;
    }

    public String getText() {
        return mText;
    }

    public Progress setMax(int max) {
        mMax = max;
        update();
        return this;
    }

    public int getMax() {
        return mMax;
    }

    public static final Creator<Progress> CREATOR = new Creator<Progress>() {
        @Override
        public Progress createFromParcel(Parcel parcel) {
            Progress progress = new Progress();
            Item.readFromParcel(parcel, progress);
            progress.setText(parcel.readString());
            progress.setProgress(parcel.readInt());
            progress.setMax(parcel.readInt());
            return progress;
        }

        @Override
        public Progress[] newArray(int i) {
            return new Progress[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getText());
        parcel.writeInt(getProgress());
        parcel.writeInt(getMax());
    }

}
