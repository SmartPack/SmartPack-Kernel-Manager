/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by willi on 09.08.16.
 */

public class BannerResizerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.DONATED) {
            Utils.toast("nice try", this);
            return;
        }
        setContentView(R.layout.activity_fragments);

        initToolBar();

        getSupportActionBar().setTitle(getString(R.string.banner_resizer));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(),
                BannerResizerFragment.class.getSimpleName()).commit();
        findViewById(R.id.content_frame).setPadding(0, Math.round(ViewUtils.getActionBarSize(this)), 0, 0);
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(BannerResizerFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new BannerResizerFragment();
        }
        return fragment;
    }

    public static class BannerResizerFragment extends BaseFragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_banner_resizer, container, false);

            final int minHeight = Math.round(getResources().getDimension(R.dimen.banner_min_height));
            int defaultHeight = Math.round(getResources().getDimension(R.dimen.banner_default_height));
            int maxHeight = Math.round(getResources().getDimension(R.dimen.banner_max_height));

            final View banner = rootView.findViewById(R.id.banner_view);
            final int px = Prefs.getInt("banner_size", defaultHeight, requireActivity());
            setHeight(banner, px);

            final TextView text = (TextView) rootView.findViewById(R.id.seekbar_text);
            text.setText(Utils.strFormat("%d" + getString(R.string.px), px));

            final DiscreteSeekBar seekBar = (DiscreteSeekBar) rootView.findViewById(R.id.seekbar);
            seekBar.setMax(maxHeight - minHeight);
            seekBar.setProgress(px - minHeight);
            seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    text.setText(Utils.strFormat("%d" + getString(R.string.px), value + minHeight));
                    setHeight(banner, value + minHeight);
                }

                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                }
            });

            rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBar.setProgress(px - minHeight);
                }
            });

            rootView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Prefs.saveInt("banner_size", seekBar.getProgress() + minHeight, requireActivity());
                    getActivity().finish();
                }
            });

            return rootView;
        }

        private int getAdjustedSize(int px) {
            return Math.round(px / 2.8f);
        }

        private void setHeight(View banner, int px) {
            ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
            layoutParams.height = getAdjustedSize(px);
            banner.requestLayout();
        }

    }

}
