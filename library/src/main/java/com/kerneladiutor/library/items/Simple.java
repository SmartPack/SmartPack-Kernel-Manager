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
 * Created by willi on 12.08.15.
 */
public class Simple extends Item {

    private String mTitle;
    private String mDescription;

    public Simple setTitle(String title) {
        mTitle = title;
        update();
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Simple setDescription(String description) {
        mDescription = description;
        update();
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public static final Creator<Simple> CREATOR = new Creator<Simple>() {
        @Override
        public Simple createFromParcel(Parcel parcel) {
            Simple simple = new Simple();
            Item.readFromParcel(parcel, simple);
            simple.setTitle(parcel.readString());
            simple.setDescription(parcel.readString());
            return simple;
        }

        @Override
        public Simple[] newArray(int i) {
            return new Simple[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getTitle());
        parcel.writeString(getDescription());
    }

}
