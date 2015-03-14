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

package com.grarak.kerneladiutor.fragments.kernel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.EditTextCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPUVoltage;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class CPUVoltageFragment extends RecyclerViewFragment {

    private EditTextCardView.DEditTextCard[] mVoltageCard;

    @Override
    public int getSpan() {
        int orientation = Utils.getScreenOrientation(getActivity());
        if (Utils.isTablet(getActivity()))
            return orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 5;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 3;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        mVoltageCard = new EditTextCardView.DEditTextCard[CPUVoltage.getFreqs().size()];
        List<String> voltages = CPUVoltage.getVoltages();
        if (voltages == null) return;

        for (int i = 0; i < CPUVoltage.getFreqs().size(); i++) {
            mVoltageCard[i] = new EditTextCardView.DEditTextCard();
            String freq = CPUVoltage.isFauxVoltage() ? String.valueOf(Utils.stringToInt(CPUVoltage
                    .getFreqs().get(i)) / 1000) : CPUVoltage.getFreqs().get(i);
            mVoltageCard[i].setTitle(freq + getString(R.string.mhz));

            mVoltageCard[i].setDescription(voltages.get(i) + getString(R.string.mv));
            mVoltageCard[i].setValue(voltages.get(i));
            mVoltageCard[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            mVoltageCard[i].setOnDEditTextCardListener(new EditTextCardView.DEditTextCard.OnDEditTextCardListener() {
                @Override
                public void onApply(EditTextCardView.DEditTextCard dEditTextCard, String value) {
                    List<String> freqs = CPUVoltage.getFreqs();

                    for (int i = 0; i < mVoltageCard.length; i++)
                        if (dEditTextCard == mVoltageCard[i])
                            CPUVoltage.setVoltage(freqs.get(i), value, getActivity());

                    dEditTextCard.setDescription(value + getString(R.string.mv));
                    dEditTextCard.setValue(value);

                    refresh();
                }
            });

            addView(mVoltageCard[i]);
        }

    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> voltages = CPUVoltage.getVoltages();
                            if (voltages != null)
                                for (int i = 0; i < mVoltageCard.length; i++) {
                                    mVoltageCard[i].setDescription(voltages.get(i) + getString(R.string.mv));
                                    mVoltageCard[i].setValue(voltages.get(i));
                                }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cpu_voltage_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.global_offset:

                View view = inflater.inflate(R.layout.global_offset_view, container, false);

                final TextView textView = (TextView) view.findViewById(R.id.offset_text);
                if (Utils.DARKTHEME)
                    textView.setTextColor(getResources().getColor(R.color.textcolor_dark));
                textView.setText("0");

                Button minus = (Button) view.findViewById(R.id.button_minus);
                if (!Utils.DARKTHEME) minus.setTextColor(getResources().getColor(R.color.black));
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            textView.setText(String.valueOf(Utils.stringToInt(textView.getText().toString()) - 25));
                        } catch (NumberFormatException e) {
                            textView.setText("0");
                        }
                    }
                });

                Button plus = (Button) view.findViewById(R.id.button_plus);
                if (!Utils.DARKTHEME) plus.setTextColor(getResources().getColor(R.color.black));
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            textView.setText(String.valueOf(Utils.stringToInt(textView.getText().toString()) + 25));
                        } catch (NumberFormatException e) {
                            textView.setText("0");
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view)
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CPUVoltage.setGlobalOffset(textView.getText().toString(), getActivity());

                        refresh();
                    }
                }).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
