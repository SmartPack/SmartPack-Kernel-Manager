package com.grarak.kerneladiutor.elements;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ListAdapter {

    public interface ListItem {

        public Button getItem();

        public String getTitle();

        public Fragment getFragment();

        public View getView(LayoutInflater inflater, ViewGroup parent);

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
        private final Fragment fragment;

        public Item(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        @Override
        public Button getItem() {
            return null;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public Fragment getFragment() {
            return fragment;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent) {
            TextView text = (TextView) inflater.inflate(R.layout.list_item, parent, false);
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
        public Button getItem() {
            return null;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public Fragment getFragment() {
            return null;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_header, parent, false);
            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(title);
            return view;
        }

    }

    public static class MainHeader implements ListItem {

        @Override
        public Button getItem() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public Fragment getFragment() {
            return null;
        }

        @Override
        public View getView(final LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.header_main, parent, false);

            SwitchCompat mApplyOnBootSwitch = (SwitchCompat) view.findViewById(R.id.apply_on_boot_switch);
            mApplyOnBootSwitch.setChecked(Utils.getBoolean("applyonboot", false, inflater.getContext()));
            mApplyOnBootSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.saveBoolean("applyonboot", ((SwitchCompat) v).isChecked(), inflater.getContext());
                }
            });

            return view;
        }

    }

}
