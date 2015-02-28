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
import android.text.InputType;

import com.grarak.kerneladiutor.elements.EditTextCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.kernel.VM;

/**
 * Created by willi on 27.12.14.
 */
public class VMFragment extends RecyclerViewFragment {

    private EditTextCardView.DEditTextCard[] mVMCard;

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
