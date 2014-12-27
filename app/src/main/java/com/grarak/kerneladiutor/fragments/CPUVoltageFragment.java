package com.grarak.kerneladiutor.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;

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

    private EditTextCardView.DEditTextCard[] mVoltage;
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

        mVoltage = new EditTextCardView.DEditTextCard[CPUVoltage.getFreqs().size()];
        for (int i = 0; i < CPUVoltage.getFreqs().size(); i++) {
            mVoltage[i] = new EditTextCardView.DEditTextCard();
            mVoltage[i].setTitle(CPUVoltage.getFreqs().get(i) + getString(R.string.mhz));

            String voltage = CPUVoltage.getVoltages().get(i);
            mVoltage[i].setDescription(voltage + getString(R.string.mv));
            mVoltage[i].setValue(voltage);
            mVoltage[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            mVoltage[i].setOnDEditTextCardListener(new EditTextCardView.DEditTextCard.OnDEditTextCardListener() {
                @Override
                public void onApply(EditTextCardView.DEditTextCard dEditTextCard, String value) {
                    List<String> voltages = CPUVoltage.getVoltages();
                    String voltage = "";

                    for (int i = 0; i < mVoltage.length; i++)
                        if (dEditTextCard == mVoltage[i])
                            voltage += voltage.isEmpty() ? value : " " + value;
                        else voltage += voltage.isEmpty() ? voltages.get(i) : " " + voltages.get(i);

                    dEditTextCard.setDescription(value + getString(R.string.mv));
                    dEditTextCard.setValue(value);

                    CPUVoltage.setVoltage(voltage, getActivity());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<String> voltages = CPUVoltage.getVoltages();
                                        for (int i = 0; i < mVoltage.length; i++) {
                                            mVoltage[i].setDescription(voltages.get(i) + getString(R.string.mv));
                                            mVoltage[i].setValue(voltages.get(i));
                                        }
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });

            addView(mVoltage[i]);
        }

    }

}
