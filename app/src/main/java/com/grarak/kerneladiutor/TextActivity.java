package com.grarak.kerneladiutor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.widget.TextView;

public class TextActivity extends ActionBarActivity {

    public static final String ARG_TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        setContentView(text);

        text.setTextSize(20);
        text.setGravity(Gravity.CENTER);
        text.setText(getIntent().getExtras().getString(ARG_TEXT));
        text.setTextColor(getResources().getColor(android.R.color.black));
    }

}
