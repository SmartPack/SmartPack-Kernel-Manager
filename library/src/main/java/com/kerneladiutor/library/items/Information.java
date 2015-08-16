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

/**
 * You can inform the user with this item
 */
public class Information extends Item {

    private String mText;

    public Information setText(String text) {
        mText = text;
        update();
        return this;
    }

    public String getText() {
        return mText;
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel parcel) {
            Information information = new Information();
            Item.readFromParcel(parcel, information);
            information.setText(parcel.readString());
            return information;
        }

        @Override
        public Information[] newArray(int i) {
            return new Information[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getText());
    }

}
