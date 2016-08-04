/*
 * Copyright (C) 2015 The Android Open Source Project
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
 * limitations under the License
 */
package com.grarak.kerneladiutor.utils;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.mattprecious.swirl.SwirlView;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
public class FingerprintUiHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private boolean mListening;
    private final FingerprintManagerCompat mFingerprintManagerCompat;
    private final SwirlView mSwirlView;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;

    boolean mSelfCancelled;

    /**
     * Builder class for {@link FingerprintUiHelper} in which injected fields from Dagger
     * holds its fields and takes other arguments in the {@link #build} method.
     */
    public static class FingerprintUiHelperBuilder {
        private final FingerprintManagerCompat mFingerprintManagerCompat;

        public FingerprintUiHelperBuilder(FingerprintManagerCompat fingerprintManagerCompat) {
            mFingerprintManagerCompat = fingerprintManagerCompat;
        }

        public FingerprintUiHelper build(SwirlView swirlView, Callback callback) {
            return new FingerprintUiHelper(mFingerprintManagerCompat, swirlView, callback);
        }
    }

    /**
     * Constructor for {@link FingerprintUiHelper}. This method is expected to be called from
     * only the {@link FingerprintUiHelperBuilder} class.
     */
    private FingerprintUiHelper(FingerprintManagerCompat fingerprintManagerCompat, SwirlView swirlView,
                                Callback callback) {
        mFingerprintManagerCompat = fingerprintManagerCompat;
        mSwirlView = swirlView;
        mCallback = callback;
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!mListening) {
            mListening = true;
            mCancellationSignal = new CancellationSignal();
            mSelfCancelled = false;
            mFingerprintManagerCompat
                    .authenticate(cryptoObject, 0, mCancellationSignal, this, null);
            mSwirlView.setState(SwirlView.State.ON);
        }
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            mListening = false;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError();
            mCallback.onError();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError();
    }

    @Override
    public void onAuthenticationFailed() {
        showError();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mSwirlView.setState(SwirlView.State.OFF);
        mSwirlView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, 100);
    }

    private void showError() {
        mSwirlView.setState(SwirlView.State.ERROR);
    }

    public interface Callback {
        void onAuthenticated();

        void onError();
    }
}
