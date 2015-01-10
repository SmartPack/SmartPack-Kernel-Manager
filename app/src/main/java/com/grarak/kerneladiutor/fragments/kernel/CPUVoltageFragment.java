package com.grarak.kerneladiutor.fragments.kernel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.EditTextCardView;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPUVoltage;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class CPUVoltageFragment extends RecyclerViewFragment {

    private EditTextCardView.DEditTextCard[] mVoltageCard;
    private GridLayoutManager gridLayoutManager;

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        gridLayoutManager = new GridLayoutManager(getActivity(), getSpanCount());
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (gridLayoutManager != null)
            gridLayoutManager.setSpanCount(getSpanCount());
        super.onConfigurationChanged(newConfig);
    }

    private int getSpanCount() {
        return Utils.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        mVoltageCard = new EditTextCardView.DEditTextCard[CPUVoltage.getFreqs().size()];
        for (int i = 0; i < CPUVoltage.getFreqs().size(); i++) {
            mVoltageCard[i] = new EditTextCardView.DEditTextCard();
            String freq = CPUVoltage.isFauxVoltage() ? String.valueOf(Utils.stringToInt(CPUVoltage
                    .getFreqs().get(i)) / 1000) : CPUVoltage.getFreqs().get(i);
            mVoltageCard[i].setTitle(freq + getString(R.string.mhz));

            String voltage = CPUVoltage.getVoltages().get(i);
            mVoltageCard[i].setDescription(voltage + getString(R.string.mv));
            mVoltageCard[i].setValue(voltage);
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
            case R.id.global_adjustment:

                View view = inflater.inflate(R.layout.global_adjustment_view, container, false);

                final TextView textView = (TextView) view.findViewById(R.id.adjust_text);
                textView.setText("0");

                Button minus = (Button) view.findViewById(R.id.button_minus);
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
                        CPUVoltage.setGlobal(textView.getText().toString(), getActivity());

                        refresh();
                    }
                }).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
