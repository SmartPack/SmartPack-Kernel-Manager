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

package com.grarak.kerneladiutor.elements.cards;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.grarak.cardview.BaseCardView;
import com.grarak.cardview.HeaderCardView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Utils;

import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class SeekBarCardView extends BaseCardView {

    private final List<String> list;

    private HeaderCardView headerCardView;
    private TextView descriptionView;
    private TextView valueView;
    private SeekBar seekBarView;

    private String title;
    private String description;
    private int progress;
    private boolean enabled = true;

    private OnSeekBarCardListener onSeekBarCardListener;

    public SeekBarCardView(Context context, List<String> list) {
        // Ugly hack, res folders don't do their job
        super(context, R.layout.seekbar_cardview);
        this.list = list;

        seekBarView.setMax(list.size() - 1);
        seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (valueView != null) valueView.setText(SeekBarCardView.this.list.get(progress));
                boolean changed = false;
                if ((SeekBarCardView.this.progress != seekBar.getProgress())) changed = true;
                if (onSeekBarCardListener != null && changed)
                    onSeekBarCardListener.onChanged(SeekBarCardView.this, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                boolean changed = false;
                if ((progress != seekBar.getProgress())) {
                    progress = seekBar.getProgress();
                    changed = true;
                }
                if (onSeekBarCardListener != null && changed)
                    onSeekBarCardListener.onStop(SeekBarCardView.this, seekBar.getProgress());
            }
        });
        seekBarView.setProgress(progress);
        seekBarView.setEnabled(enabled);

        headerCardView = new HeaderCardView(getContext());
        setUpTitle();
        setUpDescription();
    }

    @Override
    public void setUpInnerLayout(View view) {
        descriptionView = (TextView) view.findViewById(R.id.description_view);
        valueView = (TextView) view.findViewById(R.id.value_view);
        seekBarView = (SeekBar) view.findViewById(R.id.seekbar_view);

        valueView.setText(getContext().getString(R.string.current_value_not_supported));

        AppCompatButton minusButton = (AppCompatButton) view.findViewById(R.id.button_minus);
        minusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarView != null) {
                    seekBarView.setProgress(seekBarView.getProgress() - 1);
                    progress = seekBarView.getProgress();
                    if (onSeekBarCardListener != null)
                        onSeekBarCardListener.onStop(SeekBarCardView.this, seekBarView.getProgress());
                }
            }

        });

        AppCompatButton plusButton = (AppCompatButton) view.findViewById(R.id.button_plus);
        plusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarView != null) {
                    seekBarView.setProgress(seekBarView.getProgress() + 1);
                    progress = seekBarView.getProgress();
                    if (onSeekBarCardListener != null)
                        onSeekBarCardListener.onStop(SeekBarCardView.this, seekBarView.getProgress());
                }
            }
        });

        if (Utils.DARKTHEME) {
            int color = getResources().getColor(R.color.textcolor_dark);
            minusButton.setTextColor(color);
            plusButton.setTextColor(color);
        }

        if (Utils.isTV(getContext())) {
            minusButton.setFocusable(true);
            minusButton.setFocusableInTouchMode(true);
            plusButton.setFocusable(true);
            plusButton.setFocusableInTouchMode(true);
            seekBarView.setFocusable(false);
            seekBarView.setFocusableInTouchMode(false);
        }
    }

    @Override
    public void setFocus() {
    }

    public void setTitle(String title) {
        this.title = title;
        setUpTitle();
    }

    public void setDescription(String description) {
        this.description = description;
        setUpDescription();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (seekBarView != null) {
            seekBarView.setProgress(progress);
            if (valueView != null && progress == 0) valueView.setText(list.get(progress));
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (seekBarView != null) seekBarView.setEnabled(enabled);
    }

    private void setUpTitle() {
        if (headerCardView != null) {
            if (title == null) removeHeader();
            else addHeader(headerCardView);
        }
        if (headerCardView != null && title != null)
            headerCardView.setText(title);
    }

    private void setUpDescription() {
        if (descriptionView != null)
            if (description != null) {
                descriptionView.setVisibility(VISIBLE);
                descriptionView.setText(description);
            } else descriptionView.setVisibility(GONE);
    }

    public void setOnSeekBarCardListener(OnSeekBarCardListener onSeekBarCardListener) {
        this.onSeekBarCardListener = onSeekBarCardListener;
    }

    public interface OnSeekBarCardListener {
        void onChanged(SeekBarCardView seekBarCardView, int progress);

        void onStop(SeekBarCardView seekBarCardView, int progress);
    }

    public static class DSeekBarCard implements DAdapter.DView {

        private final List<String> list;

        private SeekBarCardView seekBarCardView;

        private String title;
        private String description;
        private int progress;
        private boolean enabled = true;

        private OnDSeekBarCardListener onDSeekBarCardListener;

        public DSeekBarCard(List<String> list) {
            this.list = list;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(new SeekBarCardView(viewGroup.getContext(), list)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            seekBarCardView = (SeekBarCardView) viewHolder.itemView;

            if (title != null) seekBarCardView.setTitle(title);
            if (description != null) {
                seekBarCardView.setVisibility(VISIBLE);
                seekBarCardView.setDescription(description);
            }
            seekBarCardView.setProgress(progress);
            seekBarCardView.setEnabled(enabled);

            seekBarCardView.setOnSeekBarCardListener(new SeekBarCardView.OnSeekBarCardListener() {
                @Override
                public void onChanged(SeekBarCardView seekBarCardView, int progress) {
                    if (onDSeekBarCardListener != null)
                        onDSeekBarCardListener.onChanged(DSeekBarCard.this, progress);
                }

                @Override
                public void onStop(SeekBarCardView seekBarCardView, int progress) {
                    DSeekBarCard.this.progress = progress;
                    if (onDSeekBarCardListener != null)
                        onDSeekBarCardListener.onStop(DSeekBarCard.this, progress);
                }
            });
        }

        public void setTitle(String title) {
            this.title = title;
            if (seekBarCardView != null) seekBarCardView.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (seekBarCardView != null) {
                seekBarCardView.setVisibility(VISIBLE);
                seekBarCardView.setDescription(description);
            }
        }

        public void setProgress(int progress) {
            this.progress = progress;
            if (seekBarCardView != null) seekBarCardView.setProgress(progress);
        }

        public int getProgress() {
            return progress;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (seekBarCardView != null) seekBarCardView.setEnabled(enabled);
        }

        public void setOnDSeekBarCardListener(OnDSeekBarCardListener onDSeekBarCardListener) {
            this.onDSeekBarCardListener = onDSeekBarCardListener;
        }

        public interface OnDSeekBarCardListener {
            void onChanged(DSeekBarCard dSeekBarCard, int position);

            void onStop(DSeekBarCard dSeekBarCard, int position);
        }

    }

}
