package com.grarak.kerneladiutor.fragments.kernel;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;

import com.grarak.kerneladiutor.elements.EditTextCardView;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.VM;

/**
 * Created by willi on 27.12.14.
 */
public class VMFragment extends RecyclerViewFragment {

    private EditTextCardView.DEditTextCard[] mVMCard;
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

        mVMCard = new EditTextCardView.DEditTextCard[VM.getVMfiles().size()];
        for (int i = 0; i < mVMCard.length; i++) {
            String value = VM.getVMValue(VM.getVMfiles().get(i));
            mVMCard[i] = new EditTextCardView.DEditTextCard();
            mVMCard[i].setTitle(VM.getVMfiles().get(i).replace("_", " "));
            mVMCard[i].setDescription(value);
            mVMCard[i].setValue(value);
            mVMCard[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            mVMCard[i].setOnDEditTextCardListener(new EditTextCardView.DEditTextCard.OnDEditTextCardListener() {
                @Override
                public void onApply(EditTextCardView.DEditTextCard dEditTextCard, String value) {
                    for (int i = 0; i < mVMCard.length; i++)
                        if (dEditTextCard == mVMCard[i]) {
                            dEditTextCard.setDescription(value);
                            dEditTextCard.setValue(value);

                            VM.setVM(value, VM.getVMfiles().get(i), getActivity());

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(500);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (int i = 0; i < mVMCard.length; i++) {
                                                    String value = VM.getVMValue(VM.getVMfiles().get(i));
                                                    mVMCard[i].setDescription(value);
                                                    mVMCard[i].setValue(value);
                                                }
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                }
            });

            addView(mVMCard[i]);
        }
    }
}
