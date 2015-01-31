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

import android.os.Bundle;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.utils.kernel.Screen;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class ScreenFragment extends RecyclerViewFragment {

    private SeekBarCardView.DSeekBarCardView[] mColorCalibrationCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (Screen.hasColorCalibration()) {
            List<String> colors = Screen.getColorCalibration();
            mColorCalibrationCard = new SeekBarCardView.DSeekBarCardView[colors.size()];
            for (int i = 0; i < mColorCalibrationCard.length; i++) {
                mColorCalibrationCard[i] = new SeekBarCardView.DSeekBarCardView(Screen.getColorCalibrationLimits());
                mColorCalibrationCard[i].setTitle(getColor(i));
                mColorCalibrationCard[i].setProgress(Screen.getColorCalibrationLimits().indexOf(colors.get(i)));
                mColorCalibrationCard[i].setOnDSeekBarCardListener(new SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener() {
                    @Override
                    public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
                        List<String> colors = Screen.getColorCalibration();
                        List<String> list = Screen.getColorCalibrationLimits();
                        String color = "";

                        for (int i = 0; i < mColorCalibrationCard.length; i++)
                            if (dSeekBarCardView == mColorCalibrationCard[i])
                                color += color.isEmpty() ? list.get(position) : " " + list.get(position);
                            else
                                color += color.isEmpty() ? colors.get(i) : " " + colors.get(i);

                        Screen.setColorCalibration(color, getActivity());
                    }
                });

                addView(mColorCalibrationCard[i]);
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
