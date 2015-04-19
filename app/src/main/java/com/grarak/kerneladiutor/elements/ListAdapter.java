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

package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ListAdapter {

    public interface ListItem {

        String getTitle();

        BaseFragment getFragment();

        View getView(LayoutInflater inflater, ViewGroup parent);

    }

    public static class Adapter extends ArrayAdapter<ListItem> {

        public Adapter(Context context, List<ListItem> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(LayoutInflater.from(getContext()), parent);
        }

    }

    public static class Item implements ListItem {

        private final String title;
        private final BaseFragment fragment;

        public Item(String title, BaseFragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public BaseFragment getFragment() {
            return fragment;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent) {
            TextView text = (TextView) inflater.inflate(R.layout.list_item, parent, false);
            if (!Utils.DARKTHEME)
                text.setTextColor(parent.getContext().getResources().getColorStateList(R.color.item_textcolor_light));
            text.setText(title);
            return text;
        }

    }

    public static class Header implements ListItem {

        private final String title;

        public Header(String title) {
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_header, parent, false);
            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(title);
            if (Utils.DARKTHEME) view.setBackgroundResource(R.drawable.no_highlight_dark);
            return view;
        }

    }

    public static class MainHeader implements ListItem {

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public View getView(final LayoutInflater inflater, ViewGroup parent) {
            return inflater.inflate(R.layout.header_main, parent, false);
        }

    }

}
