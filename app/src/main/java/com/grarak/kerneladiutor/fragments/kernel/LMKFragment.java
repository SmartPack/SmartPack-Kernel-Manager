package com.grarak.kerneladiutor.fragments.kernel;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.elements.SeekBarCardView;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.LMK;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class LMKFragment extends RecyclerViewFragment implements Constants {

    private SeekBarCardView.DSeekBarCardView[] mMinFree;
    private CardViewItem.DCardView[] mProfile;

    private List<String> values = new ArrayList<>(), modifiedvalues = new ArrayList<>();

    private final String[] mProfileValues = new String[]{
            "512,1024,1280,2048,3072,4096", "1024,2048,2560,4096,6144,8192", "1024,2048,4096,8192,12288,16384",
            "2048,4096,8192,16384,24576,32768", "4096,8192,16384,32768,49152,65536"};

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

        values.clear();
        modifiedvalues.clear();
        for (int x = 0; x < 257; x++) {
            modifiedvalues.add(x + getString(R.string.mb));
            values.add(String.valueOf(x * 256));
        }

        List<String> minfrees = LMK.getMinFrees();
        mMinFree = new SeekBarCardView.DSeekBarCardView[minfrees.size()];
        for (int i = 0; i < minfrees.size(); i++) {
            mMinFree[i] = new SeekBarCardView.DSeekBarCardView(modifiedvalues);
            mMinFree[i].setTitle(getResources().getStringArray(R.array.lmk_names)[i]);
            mMinFree[i].setProgress(modifiedvalues.indexOf(LMK.getMinFree(minfrees, i) / 256 + getString(R.string.mb)));
            mMinFree[i].setOnDSeekBarCardListener(new SeekBarCardView.DSeekBarCardView.OnDSeekBarCardListener() {
                @Override
                public void onStop(SeekBarCardView.DSeekBarCardView dSeekBarCardView, int position) {
                    List<String> minFrees = LMK.getMinFrees();
                    String minFree = "";

                    for (int i = 0; i < mMinFree.length; i++)
                        if (dSeekBarCardView == mMinFree[i])
                            minFree += minFree.isEmpty() ? values.get(position) : "," + values.get(position);
                        else
                            minFree += minFree.isEmpty() ? minFrees.get(i) : "," + minFrees.get(i);

                    LMK.setMinFree(minFree, getActivity());
                    refresh();
                }
            });

            addView(mMinFree[i]);
        }

        mProfile = new CardViewItem.DCardView[mProfileValues.length];
        for (int i = 0; i < mProfileValues.length; i++) {
            mProfile[i] = new CardViewItem.DCardView();
            mProfile[i].setTitle(getResources().getStringArray(R.array.lmk_profiles)[i]);
            mProfile[i].setDescription(mProfileValues[i]);
            mProfile[i].setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                @Override
                public void onClick(CardViewItem.DCardView dCardView) {
                    for (CardViewItem.DCardView profile : mProfile)
                        if (dCardView == profile) {
                            RootUtils.runCommand("echo " + dCardView.getDescription() + " > " + LMK_MINFREE);
                            refresh();
                        }
                }
            });

            addView(mProfile[i]);
        }
    }

    private void refresh() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            List<String> minfrees = LMK.getMinFrees();
                            for (int i = 0; i < minfrees.size(); i++)
                                mMinFree[i].setProgress(modifiedvalues.indexOf(LMK.getMinFree(minfrees, i) / 256
                                        + getString(R.string.mb)));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
