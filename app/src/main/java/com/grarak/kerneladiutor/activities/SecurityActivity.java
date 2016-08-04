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

import android.app.KeyguardManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.FingerprintUiHelper;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.mattprecious.swirl.SwirlView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by willi on 27.07.16.
 */
public class SecurityActivity extends BaseActivity {

    public static final String PASSWORD_INTENT = "password";
    private static final String KEY_NAME = "fp_key";
    private static final String SECRET_MESSAGE = "secret_message";

    private View mPasswordWrong;
    private FingerprintManagerCompat mFingerprintManagerCompat;
    private Cipher mCipher;
    private FingerprintUiHelper mFingerprintUiHelper;
    private FingerprintManagerCompat.CryptoObject mCryptoObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        final String password = Utils.decodeString(getIntent().getStringExtra(PASSWORD_INTENT));
        AppCompatEditText editText = (AppCompatEditText) findViewById(R.id.edittext);
        mPasswordWrong = findViewById(R.id.password_wrong);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(password)) {
                    mPasswordWrong.setVisibility(View.GONE);
                    setResult(1);
                    finish();
                } else {
                    mPasswordWrong.setVisibility(editable.toString().isEmpty() ? View.GONE : View.VISIBLE);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Prefs.getBoolean("fingerprint", false, this)) {
            mFingerprintManagerCompat = FingerprintManagerCompat.from(this);
            if (mFingerprintManagerCompat.isHardwareDetected()
                    && mFingerprintManagerCompat.hasEnrolledFingerprints()
                    && getSystemService(KeyguardManager.class).isDeviceSecure()) {
                loadFingerprint();
            }
        }
    }

    private void loadFingerprint() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException
                | NoSuchPaddingException | UnrecoverableKeyException | InvalidKeyException
                | CertificateException | InvalidAlgorithmParameterException | IOException e) {
            return;
        }

        mCryptoObject = new FingerprintManagerCompat.CryptoObject(mCipher);
        FrameLayout fingerprintParent = (FrameLayout) findViewById(R.id.fingerprint_parent);
        final SwirlView swirlView = new SwirlView(this);
        swirlView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        fingerprintParent.addView(swirlView);
        fingerprintParent.setVisibility(View.VISIBLE);

        mFingerprintUiHelper = new FingerprintUiHelper.FingerprintUiHelperBuilder(
                mFingerprintManagerCompat).build(swirlView,
                new FingerprintUiHelper.Callback() {
                    @Override
                    public void onAuthenticated() {
                        try {
                            mCipher.doFinal(SECRET_MESSAGE.getBytes());
                            mPasswordWrong.setVisibility(View.GONE);
                            setResult(1);
                            finish();
                        } catch (IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                            swirlView.setState(SwirlView.State.ERROR);
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
        mFingerprintUiHelper.startListening(mCryptoObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFingerprintUiHelper != null) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFingerprintUiHelper != null) {
            mFingerprintUiHelper.stopListening();
        }
    }

}
