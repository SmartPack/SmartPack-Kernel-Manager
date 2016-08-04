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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.statistics.OverallFragment;
import com.grarak.kerneladiutor.services.boot.Service;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 08.05.16.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        initToolBar();

        getFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(), "fragment").commit();
        findViewById(R.id.content_frame).setPadding(0, Math.round(Utils.getActionBarSize(this)), 0, 0);
    }

    private Fragment getFragment() {
        Fragment settingsFragment = getFragmentManager().findFragmentByTag("fragment");
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        return settingsFragment;
    }

    @Override
    public void finish() {
        getFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private static final String KEY_FORCE_ENGLISH = "forceenglish";
        private static final String KEY_DARK_THEME = "darktheme";
        private static final String KEY_APPLY_ON_BOOT_TEST = "applyonboottest";
        private static final String KEY_DEBUGGING_CATEGORY = "debugging_category";
        private static final String KEY_LOGCAT = "logcat";
        private static final String KEY_LAST_KMSG = "lastkmsg";
        private static final String KEY_DMESG = "dmesg";
        private static final String KEY_SECURITY_CATEGORY = "security_category";
        private static final String KEY_SET_PASSWORD = "set_password";
        private static final String KEY_DELETE_PASSWORD = "delete_password";
        private static final String KEY_FINGERPRINT = "fingerprint";
        private static final String KEY_SECTIONS = "sections";

        private Preference mFingerprint;

        private static String mOldPassword;
        private static String mDeletePassword;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            if (mOldPassword != null) {
                editPasswordDialog(mOldPassword);
            }
            if (mDeletePassword != null) {
                deletePasswordDialog(mDeletePassword);
            }

            findPreference(KEY_DARK_THEME).setOnPreferenceChangeListener(this);

            SwitchPreference forceEnglish = (SwitchPreference) findPreference(KEY_FORCE_ENGLISH);
            if (Resources.getSystem().getConfiguration().locale.getLanguage().startsWith("en")) {
                getPreferenceScreen().removePreference(forceEnglish);
            } else {
                forceEnglish.setOnPreferenceChangeListener(this);
            }

            findPreference(KEY_APPLY_ON_BOOT_TEST).setOnPreferenceClickListener(this);
            findPreference(KEY_LOGCAT).setOnPreferenceClickListener(this);

            if (Utils.existFile("/proc/last_kmsg") || Utils.existFile("/sys/fs/pstore/console-ramoops")) {
                findPreference(KEY_LAST_KMSG).setOnPreferenceClickListener(this);
            } else {
                ((PreferenceCategory) findPreference(KEY_DEBUGGING_CATEGORY)).removePreference(
                        findPreference(KEY_LAST_KMSG));
            }

            findPreference(KEY_DMESG).setOnPreferenceClickListener(this);
            findPreference(KEY_SET_PASSWORD).setOnPreferenceClickListener(this);
            findPreference(KEY_DELETE_PASSWORD).setOnPreferenceClickListener(this);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || !FingerprintManagerCompat.from(getActivity()).isHardwareDetected()) {
                ((PreferenceCategory) findPreference(KEY_SECURITY_CATEGORY)).removePreference(
                        findPreference(KEY_FINGERPRINT));
            } else {
                mFingerprint = findPreference(KEY_FINGERPRINT);
                mFingerprint.setEnabled(!Prefs.getString("password", "", getActivity()).isEmpty());
            }

            PreferenceCategory sectionsCategory = (PreferenceCategory) findPreference(KEY_SECTIONS);
            for (int id : NavigationActivity.sFragments.keySet()) {
                if (NavigationActivity.sFragments.get(id) != null
                        || (NavigationActivity.sActivities.containsKey(id)
                        && NavigationActivity.sActivities.get(id) != SettingsActivity.class)) {
                    SwitchPreference switchPreference = new SwitchPreference(getActivity());
                    switchPreference.setSummary(getString(id));
                    switchPreference.setKey(NavigationActivity.sActivities.containsKey(id) ?
                            NavigationActivity.sActivities.get(id).getSimpleName() + "_enabled" :
                            NavigationActivity.sFragments.get(id).getClass().getSimpleName() + "_enabled");
                    switchPreference.setDefaultValue(true);
                    switchPreference.setOnPreferenceChangeListener(this);
                    sectionsCategory.addPreference(switchPreference);
                }
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String key = preference.getKey();
            switch (key) {
                case KEY_DARK_THEME:
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                case KEY_FORCE_ENGLISH:
                    boolean checked = (boolean) o;
                    if (!checked) {
                        Utils.setLocale(Resources.getSystem().getConfiguration().locale.getLanguage(), getActivity());
                    }
                    NavigationActivity.restart();
                    getActivity().recreate();
                    return true;
                default:
                    if (key.endsWith("_enabled")) {
                        return true;
                    }
            }
            return false;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            switch (key) {
                case KEY_APPLY_ON_BOOT_TEST:
                    Intent intent = new Intent(getActivity(), Service.class);
                    intent.putExtra("messenger", new Messenger(new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.arg1 == 1) {
                                Utils.toast(R.string.nothing_apply, getActivity());
                            }
                        }
                    }));
                    getActivity().startService(intent);
                    return true;
                case KEY_LOGCAT:
                    new Execute().execute("logcat -d > /sdcard/logcat.txt");
                    return true;
                case KEY_LAST_KMSG:
                    if (Utils.existFile("/proc/last_kmsg")) {
                        new Execute().execute("cat /proc/last_kmsg > /sdcard/last_kmsg.txt");
                    } else if (Utils.existFile("/sys/fs/pstore/console-ramoops")) {
                        new Execute().execute("cat /sys/fs/pstore/console-ramoops > /sdcard/last_kmsg.txt");
                    }
                    return true;
                case KEY_DMESG:
                    new Execute().execute("dmesg > /sdcard/dmesg.txt");
                    return true;
                case KEY_SET_PASSWORD:
                    editPasswordDialog(Prefs.getString("password", "", getActivity()));
                    return true;
                case KEY_DELETE_PASSWORD:
                    deletePasswordDialog(Prefs.getString("password", "", getActivity()));
                    return true;
            }
            return false;
        }

        private class Execute extends AsyncTask<String, Void, Void> {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.executing));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(String... params) {
                RootUtils.runCommand(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();
            }
        }

        private void editPasswordDialog(final String oldPass) {
            mOldPassword = oldPass;

            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            int padding = Math.round(getResources().getDimension(R.dimen.dialog_edittext_padding));
            linearLayout.setPadding(padding, padding, padding, padding);

            final AppCompatEditText oldPassword = new AppCompatEditText(getActivity());
            if (!oldPass.isEmpty()) {
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                oldPassword.setHint(getString(R.string.old_password));
                linearLayout.addView(oldPassword);
            }

            final AppCompatEditText newPassword = new AppCompatEditText(getActivity());
            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPassword.setHint(getString(R.string.new_password));
            linearLayout.addView(newPassword);

            final AppCompatEditText confirmNewPassword = new AppCompatEditText(getActivity());
            confirmNewPassword.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmNewPassword.setHint(getString(R.string.confirm_new_password));
            linearLayout.addView(confirmNewPassword);

            new AlertDialog.Builder(getActivity()).setView(linearLayout)
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!oldPass.isEmpty() && !oldPassword.getText().toString().equals(Utils
                                    .decodeString(oldPass))) {
                                Utils.toast(getString(R.string.old_password_wrong), getActivity());
                                return;
                            }

                            if (newPassword.getText().toString().isEmpty()) {
                                Utils.toast(getString(R.string.password_empty), getActivity());
                                return;
                            }

                            if (!newPassword.getText().toString().equals(confirmNewPassword.getText()
                                    .toString())) {
                                Utils.toast(getString(R.string.password_not_match), getActivity());
                                return;
                            }

                            if (newPassword.getText().toString().length() > 32) {
                                Utils.toast(getString(R.string.password_too_long), getActivity());
                                return;
                            }

                            Prefs.saveString("password", Utils.encodeString(newPassword.getText()
                                    .toString()), getActivity());
                            if (mFingerprint != null) {
                                mFingerprint.setEnabled(true);
                            }
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mOldPassword = null;
                }
            }).show();
        }

        private void deletePasswordDialog(final String password) {
            if (password.isEmpty()) {
                Utils.toast(getString(R.string.set_password_first), getActivity());
                return;
            }

            mDeletePassword = password;

            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            int padding = Math.round(getResources().getDimension(R.dimen.dialog_edittext_padding));
            linearLayout.setPadding(padding, padding, padding, padding);

            final AppCompatEditText mPassword = new AppCompatEditText(getActivity());
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPassword.setHint(getString(R.string.password));
            linearLayout.addView(mPassword);

            new AlertDialog.Builder(getActivity()).setView(linearLayout)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!mPassword.getText().toString().equals(Utils.decodeString(password))) {
                                Utils.toast(getString(R.string.password_wrong), getActivity());
                                return;
                            }

                            Prefs.saveString("password", "", getActivity());
                            if (mFingerprint != null) {
                                mFingerprint.setEnabled(false);
                            }
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mDeletePassword = null;
                }
            }).show();
        }
    }

}
