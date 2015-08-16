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

import android.content.Intent;
import android.os.Parcel;

import com.kerneladiutor.library.Item;

/**
 * Created by willi on 12.08.15.
 */

/**
 * Works like {@link android.widget.EditText}
 * So the user can write anything they want
 */
public class EditText extends Item {

    public static final String VALUE = "com.kerneladiutor.library.items.VALUE";

    private String mTitle;
    private String mValue;
    private int mInputType = -1;

    public EditText setTitle(String title) {
        mTitle = title;
        update();
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public EditText setValue(String value) {
        mValue = value;
        update();
        return this;
    }

    public String getValue() {
        return mValue;
    }

    public EditText setInputType(int inputType) {
        mInputType = inputType;
        update();
        return this;
    }

    public int getInputType() {
        return mInputType;
    }

    public static final Creator<EditText> CREATOR = new Creator<EditText>() {
        @Override
        public EditText createFromParcel(Parcel parcel) {
            EditText editText = new EditText();
            Item.readFromParcel(parcel, editText);
            editText.setTitle(parcel.readString());
            editText.setValue(parcel.readString());
            editText.setInputType(parcel.readInt());
            return editText;
        }

        @Override
        public EditText[] newArray(int i) {
            return new EditText[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Item.writeToParcel(parcel, i, this);
        parcel.writeString(getTitle());
        parcel.writeString(getValue());
        parcel.writeInt(getInputType());
    }

    public static String getValueEvent(Intent intent) {
        return intent.getStringExtra(VALUE);
    }

}
