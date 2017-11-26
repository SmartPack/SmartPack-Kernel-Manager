/*
 * Copyright (C) 2015-2017 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.fragments.other;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BannerResizerActivity;
import com.grarak.kerneladiutor.activities.MainActivity;
import com.grarak.kerneladiutor.activities.NavigationActivity;
import com.grarak.kerneladiutor.services.boot.ApplyOnBootService;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.utils.root.RootUtils;
import com.grarak.kerneladiutor.views.BorderCircleView;
import com.grarak.kerneladiutor.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 13.08.16.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String KEY_AD_VIEW = "adview";
    private static final String KEY_FORCE_ENGLISH = "forceenglish";
    private static final String KEY_USER_INTERFACE = "user_interface";
    private static final String KEY_DARK_THEME = "darktheme";
    private static final String KEY_MATERIAL_ICON = "materialicon";
    private static final String KEY_BANNER_RESIZER = "banner_resizer";
    private static final String KEY_HIDE_BANNER = "hide_banner";
    private static final String KEY_ACCENT_COLOR = "accent_color";
    private static final String KEY_SECTIONS_ICON = "section_icons";
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

    private String mOldPassword;
    private String mDeletePassword;
    private int mColorSelection = -1;

    public boolean mDelay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.DONATED) {
            Prefs.remove(KEY_HIDE_BANNER, getActivity());
            Prefs.remove(KEY_ACCENT_COLOR, getActivity());
            Prefs.remove(KEY_SECTIONS_ICON, getActivity());
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        rootView.setPadding(rootView.getPaddingLeft(),
                Math.round(ViewUtils.getActionBarSize(getActivity())),
                rootView.getPaddingRight(), rootView.getPaddingBottom());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOldPassword != null) {
            editPasswordDialog(mOldPassword);
        }
        if (mDeletePassword != null) {
            deletePasswordDialog(mDeletePassword);
        }
        if (mColorSelection >= 0) {
            colorDialog(mColorSelection);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                mDelay = false;
            }
        }, mDelay ? 250 : 0);
    }

    private void init() {
        addPreferencesFromResource(R.xml.settings);

        if (Utils.DONATED) {
            getPreferenceScreen().removePreference(findPreference(KEY_AD_VIEW));
        }

        SwitchPreferenceCompat forceEnglish = (SwitchPreferenceCompat) findPreference(KEY_FORCE_ENGLISH);
        if (Resources.getSystem().getConfiguration().locale.getLanguage().startsWith("en")) {
            getPreferenceScreen().removePreference(forceEnglish);
        } else {
            forceEnglish.setOnPreferenceChangeListener(this);
        }

        if (Utils.hideStartActivity()) {
            ((PreferenceCategory) findPreference(KEY_USER_INTERFACE))
                    .removePreference(findPreference(KEY_MATERIAL_ICON));
        } else {
            findPreference(KEY_MATERIAL_ICON).setOnPreferenceChangeListener(this);
        }

        findPreference(KEY_DARK_THEME).setOnPreferenceChangeListener(this);
        findPreference(KEY_BANNER_RESIZER).setOnPreferenceClickListener(this);
        findPreference(KEY_HIDE_BANNER).setOnPreferenceChangeListener(this);
        findPreference(KEY_ACCENT_COLOR).setOnPreferenceClickListener(this);
        findPreference(KEY_SECTIONS_ICON).setOnPreferenceChangeListener(this);
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
        for (NavigationActivity.NavigationFragment navigationFragment : NavigationActivity.sFragments) {
            Fragment fragment = navigationFragment.mFragment;
            int id = navigationFragment.mId;

            if (fragment != null && fragment.getClass() != SettingsFragment.class) {
                SwitchPreferenceCompat switchPreference = new SwitchPreferenceCompat(
                        new ContextThemeWrapper(getActivity(), R.style.Preference_SwitchPreferenceCompat_Material));
                switchPreference.setSummary(getString(id));
                switchPreference.setKey(fragment.getClass().getSimpleName() + "_enabled");
                switchPreference.setChecked(Prefs.getBoolean(fragment.getClass().getSimpleName()
                        + "_enabled", true, getActivity()));
                switchPreference.setOnPreferenceChangeListener(this);
                switchPreference.setPersistent(false);
                sectionsCategory.addPreference(switchPreference);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        boolean checked = (boolean) o;
        String key = preference.getKey();
        switch (key) {
            case KEY_FORCE_ENGLISH:
                if (!checked) {
                    Utils.setLocale(Resources.getSystem().getConfiguration().locale.getLanguage(), getActivity());
                }
            case KEY_DARK_THEME:
                getActivity().finish();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case KEY_MATERIAL_ICON:
                Utils.setStartActivity(checked, getActivity());
                return true;
            case KEY_HIDE_BANNER:
                if (!Utils.DONATED) {
                    ViewUtils.dialogDonate(getActivity()).show();
                    return false;
                }
                return true;
            default:
                if (key.equals(KEY_SECTIONS_ICON) || key.endsWith("_enabled")) {
                    if (key.equals(KEY_SECTIONS_ICON) && !Utils.DONATED) {
                        ViewUtils.dialogDonate(getActivity()).show();
                        return false;
                    }
                    Prefs.saveBoolean(key, checked, getActivity());
                    ((NavigationActivity) getActivity()).appendFragments();
                    return true;
                }
                break;
        }
        return false;
    }

    private static class MessengerHandler extends Handler {

        private final Context mContext;

        private MessengerHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1 && mContext != null) {
                Utils.toast(R.string.nothing_apply, mContext);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case KEY_BANNER_RESIZER:
                if (Utils.DONATED) {
                    Intent intent = new Intent(getActivity(), BannerResizerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    ViewUtils.dialogDonate(getActivity()).show();
                }
                return true;
            case KEY_ACCENT_COLOR:
                if (Utils.DONATED) {
                    List<Integer> sColors = new ArrayList<>();
                    for (int i = 0; i < BorderCircleView.sAccentColors.size(); i++) {
                        sColors.add(BorderCircleView.sAccentColors.keyAt(i));
                    }
                    for (int i = 0; i < sColors.size(); i++) {
                        sColors.set(i, ContextCompat.getColor(getActivity(), sColors.get(i)));
                    }
                    colorDialog(sColors.indexOf(ViewUtils.getThemeAccentColor(getActivity())));
                } else {
                    ViewUtils.dialogDonate(getActivity()).show();
                }
                return true;
            case KEY_APPLY_ON_BOOT_TEST:
                if (Utils.isServiceRunning(ApplyOnBootService.class, getActivity())) {
                    Utils.toast(R.string.apply_on_boot_running, getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), ApplyOnBootService.class);
                    intent.putExtra("messenger", new Messenger(new MessengerHandler(getActivity())));
                    Utils.startService(getActivity(), intent);
                }
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
        int padding = Math.round(getResources().getDimension(R.dimen.dialog_padding));
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

        new Dialog(getActivity()).setView(linearLayout)
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
        int padding = Math.round(getResources().getDimension(R.dimen.dialog_padding));
        linearLayout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText mPassword = new AppCompatEditText(getActivity());
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPassword.setHint(getString(R.string.password));
        linearLayout.addView(mPassword);

        new Dialog(getActivity()).setView(linearLayout)
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

    private void colorDialog(int selection) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) getResources().getDimension(R.dimen.dialog_padding);
        linearLayout.setPadding(padding, padding, padding, padding);

        final List<BorderCircleView> circles = new ArrayList<>();

        LinearLayout subView = null;
        for (int i = 0; i < BorderCircleView.sAccentColors.size(); i++) {
            if (subView == null || i % 5 == 0) {
                subView = new LinearLayout(getActivity());
                subView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(subView);
            }

            BorderCircleView circle = new BorderCircleView(getActivity());
            circle.setChecked(i == selection);
            circle.setBackgroundColor(ContextCompat.getColor(getActivity(),
                    BorderCircleView.sAccentColors.keyAt(i)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            int margin = (int) getResources().getDimension(R.dimen.color_dialog_margin);
            params.setMargins(margin, margin, margin, margin);
            circle.setLayoutParams(params);
            circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (BorderCircleView borderCircleView : circles) {
                        if (v == borderCircleView) {
                            borderCircleView.setChecked(true);
                            mColorSelection = circles.indexOf(borderCircleView);
                        } else {
                            borderCircleView.setChecked(false);
                        }
                    }
                }
            });

            circles.add(circle);
            subView.addView(circle);
        }

        new Dialog(getActivity()).setView(linearLayout)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mColorSelection >= 0) {
                            Prefs.saveString(KEY_ACCENT_COLOR,
                                    BorderCircleView.sAccentColors.valueAt(mColorSelection), getActivity());
                        }
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mColorSelection = -1;
            }
        }).show();
    }

}
