package com.grarak.kerneladiutor.elements;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

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
            TextView text = (TextView) inflater.inflate(R.layout.list_header, parent, false);
            text.setText(title);
            return text;
        }

    }

}
