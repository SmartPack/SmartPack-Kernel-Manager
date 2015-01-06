package com.grarak.kerneladiutor.fragments.kernel;

import android.content.Intent;
import android.os.Bundle;

import com.grarak.kerneladiutor.PathReaderActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.IO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 26.12.14.
 */
public class IOFragment extends RecyclerViewFragment implements Constants, PopupCardItem.DPopupCard.OnDPopupCardListener,
        CardViewItem.DCardView.OnDCardListener {

    List<String> readheads = new ArrayList<>();

    private PopupCardItem.DPopupCard mInternalSchedulerCard, mExternalSchedulerCard;

    private CardViewItem.DCardView mInternalTunableCard, mExternalTunableCard;

    private PopupCardItem.DPopupCard mInternalReadAheadCard, mExternalReadAheadCard;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        readheads.clear();
        schedulerInit();
        tunableInit();
        readAheadInit();
    }

    private void schedulerInit() {
        mInternalSchedulerCard = new PopupCardItem.DPopupCard(IO.getSchedulers(IO.StorageType.INTERNAL));
        mInternalSchedulerCard.setTitle(getString(R.string.internal_scheduler));
        mInternalSchedulerCard.setDescription(getString(R.string.internal_scheduler_summary));
        mInternalSchedulerCard.setItem(IO.getScheduler(IO.StorageType.INTERNAL));
        mInternalSchedulerCard.setOnDPopupCardListener(this);

        addView(mInternalSchedulerCard);

        if (IO.hasExternalStorage()) {
            mExternalSchedulerCard = new PopupCardItem.DPopupCard(IO.getSchedulers(IO.StorageType.EXTERNAL));
            mExternalSchedulerCard.setDescription(getString(R.string.external_scheduler));
            mExternalSchedulerCard.setItem(IO.getScheduler(IO.StorageType.EXTERNAL));
            mExternalSchedulerCard.setOnDPopupCardListener(this);

            addView(mExternalSchedulerCard);
        }
    }

    private void tunableInit() {
        mInternalTunableCard = new CardViewItem.DCardView();
        mInternalTunableCard.setTitle(getString(R.string.internal_scheduler_tunable));
        mInternalTunableCard.setDescription(getString(R.string.internal_scheduler_tunable_summary));
        mInternalTunableCard.setOnDCardListener(this);

        addView(mInternalTunableCard);

        if (IO.hasExternalStorage()) {
            mExternalTunableCard = new CardViewItem.DCardView();
            mExternalTunableCard.setDescription(getString(R.string.external_scheduler_tunable));
            mExternalTunableCard.setOnDCardListener(this);

            addView(mExternalTunableCard);
        }
    }

    private void readAheadInit() {
        for (int i = 0; i < 32; i++)
            readheads.add((i * 128 + 128) + getString(R.string.kb));

        mInternalReadAheadCard = new PopupCardItem.DPopupCard(readheads);
        mInternalReadAheadCard.setTitle(getString(R.string.internal_read_ahead));
        mInternalReadAheadCard.setDescription(getString(R.string.internal_read_ahead_summary));
        mInternalReadAheadCard.setItem(IO.getReadahead(IO.StorageType.INTERNAL) + getString(R.string.kb));
        mInternalReadAheadCard.setOnDPopupCardListener(this);

        addView(mInternalReadAheadCard);

        if (IO.hasExternalStorage()) {
            mExternalReadAheadCard = new PopupCardItem.DPopupCard(readheads);
            mExternalReadAheadCard.setDescription(getString(R.string.external_read_ahead));
            mExternalReadAheadCard.setItem(IO.getReadahead(IO.StorageType.EXTERNAL) + getString(R.string.kb));
            mExternalReadAheadCard.setOnDPopupCardListener(this);

            addView(mExternalReadAheadCard);
        }
    }

    @Override
    public void onItemSelected(PopupCardItem.DPopupCard dPopupCard, int position) {
        if (dPopupCard == mInternalSchedulerCard)
            IO.setScheduler(IO.StorageType.INTERNAL, IO.getSchedulers(IO.StorageType.INTERNAL)
                    .get(position), getActivity());
        if (dPopupCard == mExternalSchedulerCard)
            IO.setScheduler(IO.StorageType.EXTERNAL, IO.getSchedulers(IO.StorageType.EXTERNAL)
                    .get(position), getActivity());
        if (dPopupCard == mInternalReadAheadCard)
            IO.setReadahead(IO.StorageType.INTERNAL, Utils.stringToInt(readheads.get(position)
                    .replace(getString(R.string.kb), "")), getActivity());
        if (dPopupCard == mExternalReadAheadCard)
            IO.setReadahead(IO.StorageType.EXTERNAL, Utils.stringToInt(readheads.get(position)
                    .replace(getString(R.string.kb), "")), getActivity());
    }

    @Override
    public void onClick(CardViewItem.DCardView dCardView) {
        if (dCardView == mInternalTunableCard || dCardView == mExternalTunableCard) {
            String scheduler = IO.getScheduler(dCardView == mInternalTunableCard ? IO.StorageType.INTERNAL :
                    IO.StorageType.EXTERNAL);
            Intent i = new Intent(getActivity(), PathReaderActivity.class);
            Bundle args = new Bundle();
            args.putInt(PathReaderActivity.ARG_TYPE, PathReaderActivity.PATH_TYPE.IO.ordinal());
            args.putString(PathReaderActivity.ARG_TITLE, scheduler);
            args.putString(PathReaderActivity.ARG_PATH, dCardView == mInternalTunableCard ?
                    IO_INTERNAL_SCHEDULER_TUNABLE : IO_EXTERNAL_SCHEDULER_TUNABLE);
            args.putString(PathReaderActivity.ARG_ERROR, getString(R.string.not_tunable, scheduler));
            i.putExtras(args);

            startActivity(i);
        }
    }

}
