package com.grarak.kerneladiutor.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.grarak.kerneladiutor.PathReaderActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.kernel.IO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class IOFragment extends RecyclerViewFragment implements Constants, PopupCardItem.DPopupCard.OnDPopupCardListener,
        CardViewItem.DCardView.OnDCardListener {

    List<String> readheads = new ArrayList<>();

    private PopupCardItem.DPopupCard mInternalScheduler, mExternalScheduler;

    private CardViewItem.DCardView mInternalTunable, mExternalTunable;

    private PopupCardItem.DPopupCard mInternalReadAhead, mExternalReadAhead;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        readheads.clear();
        schedulerInit();
        tunableInit();
        readAheadInit();
    }

    private void schedulerInit() {
        mInternalScheduler = new PopupCardItem.DPopupCard(IO.getSchedulers(IO.StorageType.INTERNAL));
        mInternalScheduler.setTitle(getString(R.string.internal_scheduler));
        mInternalScheduler.setDescription(getString(R.string.internal_scheduler_summary));
        mInternalScheduler.setItem(IO.getScheduler(IO.StorageType.INTERNAL));
        mInternalScheduler.setOnDPopupCardListener(this);

        addView(mInternalScheduler);

        if (IO.hasExternalStorage()) {
            mExternalScheduler = new PopupCardItem.DPopupCard(IO.getSchedulers(IO.StorageType.EXTERNAL));
            mExternalScheduler.setDescription(getString(R.string.external_scheduler));
            mExternalScheduler.setItem(IO.getScheduler(IO.StorageType.EXTERNAL));
            mExternalScheduler.setOnDPopupCardListener(this);

            addView(mExternalScheduler);
        }
    }

    private void tunableInit() {
        mInternalTunable = new CardViewItem.DCardView();
        mInternalTunable.setTitle(getString(R.string.internal_scheduler_tunable));
        mInternalTunable.setDescription(getString(R.string.internal_scheduler_tunable_summary));
        mInternalTunable.setOnDCardListener(this);

        addView(mInternalTunable);

        if (IO.hasExternalStorage()) {
            mExternalTunable = new CardViewItem.DCardView();
            mExternalTunable.setDescription(getString(R.string.external_scheduler_tunable));
            mExternalTunable.setOnDCardListener(this);

            addView(mExternalTunable);
        }
    }

    private void readAheadInit() {
        for (int i = 0; i < 32; i++)
            readheads.add((i * 128 + 128) + getString(R.string.kb));

        mInternalReadAhead = new PopupCardItem.DPopupCard(readheads);
        mInternalReadAhead.setTitle(getString(R.string.internal_read_ahead));
        mInternalReadAhead.setDescription(getString(R.string.internal_read_ahead_summary));
        mInternalReadAhead.setItem(IO.getReadahead(IO.StorageType.INTERNAL) + getString(R.string.kb));
        mInternalReadAhead.setOnDPopupCardListener(this);

        addView(mInternalReadAhead);

        if (IO.hasExternalStorage()) {
            mExternalReadAhead = new PopupCardItem.DPopupCard(readheads);
            mExternalReadAhead.setDescription(getString(R.string.external_read_ahead));
            mExternalReadAhead.setItem(IO.getReadahead(IO.StorageType.EXTERNAL) + getString(R.string.kb));
            mExternalReadAhead.setOnDPopupCardListener(this);

            addView(mExternalReadAhead);
        }
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mInternalScheduler)
            IO.setScheduler(IO.StorageType.INTERNAL, IO.getSchedulers(IO.StorageType.INTERNAL)
                    .get(position), getActivity());
        if (dPopupCard == mExternalScheduler)
            IO.setScheduler(IO.StorageType.EXTERNAL, IO.getSchedulers(IO.StorageType.EXTERNAL)
                    .get(position), getActivity());
        if (dPopupCard == mInternalReadAhead)
            IO.setReadahead(IO.StorageType.INTERNAL, Integer.parseInt(readheads.get(position)
                    .replace(getString(R.string.kb), "")), getActivity());
        if (dPopupCard == mExternalReadAhead)
            IO.setReadahead(IO.StorageType.EXTERNAL, Integer.parseInt(readheads.get(position)
                    .replace(getString(R.string.kb), "")), getActivity());
    }

    @Override
    public void onClick(CardViewItem.DCardView dCardView) {
        if (dCardView == mInternalTunable || dCardView == mExternalTunable) {
            String scheduler = IO.getScheduler(dCardView == mInternalTunable ? IO.StorageType.INTERNAL :
                    IO.StorageType.EXTERNAL);
            Intent i = new Intent(getActivity(), PathReaderActivity.class);
            Bundle args = new Bundle();
            args.putInt(PathReaderActivity.ARG_TYPE, PathReaderActivity.PATH_TYPE.IO.ordinal());
            args.putString(PathReaderActivity.ARG_TITLE, scheduler);
            args.putString(PathReaderActivity.ARG_PATH, dCardView == mInternalTunable ?
                    IO_INTERNAL_SCHEDULER_TUNABLE : IO_EXTERNAL_SCHEDULER_TUNABLE);
            args.putString(PathReaderActivity.ARG_ERROR, getString(R.string.not_tunable, scheduler));
            i.putExtras(args);

            startActivity(i);
        }
    }

}
