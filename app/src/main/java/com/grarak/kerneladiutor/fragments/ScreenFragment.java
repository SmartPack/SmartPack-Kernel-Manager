package com.grarak.kerneladiutor.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.Screen;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ScreenFragment extends RecyclerViewFragment {

    private SeekBarCardView.DSeekBarCardView[] mColorCalibration;

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
        return Utils.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Screen.hasColorCalibration()) {
            List<String> colors = Screen.getColorCalibration();
            mColorCalibration = new SeekBarCardView.DSeekBarCardView[colors.size()];
            for (int i = 0; i < mColorCalibration.length; i++) {
                mColorCalibration[i] = new SeekBarCardView.DSeekBarCardView(Screen.getLimits());
                mColorCalibration[i].setTitle(getColor(i));
                mColorCalibration[i].setProgress(Screen.getLimits().indexOf(colors.get(i)));
                mColorCalibration[i].setOnDSeekBarCardListener(new SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener() {
                    @Override
                    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
                        List<String> colors = Screen.getColorCalibration();
                        List<String> list = Screen.getLimits();
                        String color = "";

                        for (int i = 0; i < mColorCalibration.length; i++)
                            if (dSeekBarCardView == mColorCalibration[i])
                                color += color.isEmpty() ? list.get(position) : " " + list.get(position);
                            else
                                color += color.isEmpty() ? colors.get(i) : " " + colors.get(i);

                        Screen.setColorCalibration(color, getActivity());
                    }
                });

                addView(mColorCalibration[i]);
            }
        }
    }

    private String getColor(int position) {
        switch (position) {
            case 0:
                return getString(R.string.red);
            case 1:
                return getString(R.string.green);
            case 2:
                return getString(R.string.blue);
            default:
                return null;
        }
    }
}
