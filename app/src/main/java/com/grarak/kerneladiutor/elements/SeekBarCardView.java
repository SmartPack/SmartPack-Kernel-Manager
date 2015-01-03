package com.grarak.kerneladiutor.elements;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class SeekBarCardView extends BaseCardView {

    private HeaderCardView headerCardView;
    private TextView descriptionView;
    private TextView valueView;
    private SeekBar seekBarView;

    private String title;
    private String description;
    private int progress;

    private OnSeekBarCardListener onSeekBarCardListener;

    public SeekBarCardView(Context context, final List<String> list) {
        super(context, R.layout.seekbar_cardview);

        seekBarView.setMax(list.size() - 1);
        seekBarView.setProgress(progress);
        seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (valueView != null) valueView.setText(list.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress = seekBar.getProgress();
                if (onSeekBarCardListener != null)
                    onSeekBarCardListener.onStop(SeekBarCardView.this, seekBar.getProgress());
            }
        });

        headerCardView = new HeaderCardView(getContext());
        addHeader(headerCardView);
        if (title != null) headerCardView.setText(title);
        if (description != null) descriptionView.setText(description);
    }

    @Override
    protected void setUpInnerLayout(View view) {
        super.setUpInnerLayout(view);

        descriptionView = (TextView) view.findViewById(R.id.description_view);
        valueView = (TextView) view.findViewById(R.id.value_view);
        seekBarView = (SeekBar) view.findViewById(R.id.seekbar_view);

        view.findViewById(R.id.button_minus).setOnClickListener(new OnClickListener() {
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

        view.findViewById(R.id.button_plus).setOnClickListener(new OnClickListener() {
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

    }

    public void setTitle(String title) {
        this.title = title;
        if (headerCardView != null) headerCardView.setText(title);
    }

    public void setDescription(String description) {
        this.description = description;
        if (descriptionView != null) descriptionView.setText(description);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (seekBarView != null) seekBarView.setProgress(progress);
    }

    public void setOnSeekBarCardListener(OnSeekBarCardListener onSeekBarCardListener) {
        this.onSeekBarCardListener = onSeekBarCardListener;
    }

    public interface OnSeekBarCardListener {
        public void onStop(SeekBarCardView seekBarCardView, int progress);
    }

    public static class DSeekBarCardView implements DAdapter.DView {

        private final List<String> list;

        private SeekBarCardView seekBarCardView;

        private String title;
        private String description;
        private int progress;

        private OnDSeekBarCardListener onDSeekBarCardListener;

        public DSeekBarCardView(String[] array) {
            this(new ArrayList<>(Arrays.asList(array)));
        }

        public DSeekBarCardView(List<String> list) {
            this.list = list;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup) {
            return new Holder(new SeekBarCardView(viewGroup.getContext(), list));
        }

        @Override
        public void onBindViewHolder(Holder viewHolder) {
            seekBarCardView = (SeekBarCardView) viewHolder.view;

            if (title != null) seekBarCardView.setTitle(title);
            if (description != null) seekBarCardView.setDescription(description);
            seekBarCardView.setProgress(progress);

            seekBarCardView.setOnSeekBarCardListener(new SeekBarCardView.OnSeekBarCardListener() {
                @Override
                public void onStop(SeekBarCardView seekBarCardView, int progress) {
                    DSeekBarCardView.this.progress = progress;
                    if (onDSeekBarCardListener != null)
                        onDSeekBarCardListener.onStop(DSeekBarCardView.this, progress);
                }
            });
        }

        public void setTitle(String title) {
            this.title = title;
            if (seekBarCardView != null) seekBarCardView.setTitle(title);
        }

        public void setDescription(String description) {
            this.description = description;
            if (seekBarCardView != null) seekBarCardView.setDescription(description);
        }

        public void setProgress(int progress) {
            this.progress = progress;
            if (seekBarCardView != null) seekBarCardView.setProgress(progress);
        }

        public void setOnDSeekBarCardListener(OnDSeekBarCardListener onDSeekBarCardListener) {
            this.onDSeekBarCardListener = onDSeekBarCardListener;
        }

        public interface OnDSeekBarCardListener {
            public void onStop(DSeekBarCardView dSeekBarCardView, int position);
        }

    }

}
