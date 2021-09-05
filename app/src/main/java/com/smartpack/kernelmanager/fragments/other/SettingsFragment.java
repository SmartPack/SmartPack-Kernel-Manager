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
package com.smartpack.kernelmanager.fragments.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.BannerResizerActivity;
import com.smartpack.kernelmanager.activities.MainActivity;
import com.smartpack.kernelmanager.activities.NavigationActivity;
import com.smartpack.kernelmanager.services.boot.ApplyOnBootService;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.UpdateCheck;
import com.smartpack.kernelmanager.views.BorderCircleView;
import com.smartpack.kernelmanager.views.dialog.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by willi on 13.08.16.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String KEY_APP_SETTINGS = "app_settings";
    private static final String KEY_UPDATE_CHECK_AUTO = "auto_update";
    private static final String KEY_UPDATE_CHECK = "update_check";
    private static final String KEY_RESET_SETTINGS = "reset_settings";
    private static final String KEY_FORCE_ENGLISH = "forceenglish";
    private static final String KEY_USER_INTERFACE = "user_interface";
    private static final String KEY_DARK_THEME = "darktheme";
    private static final String KEY_MATERIAL_ICON = "materialicon";
    private static final String KEY_BANNER_RESIZER = "banner_resizer";
    private static final String KEY_HIDE_BANNER = "hide_banner";
    private static final String KEY_ACCENT_COLOR = "accent_color";
    private static final String KEY_SECTIONS_ICON = "section_icons";
    private static final String KEY_ENABLE_ON_BOOT = "enable_onboot";
    private static final String KEY_APPLY_ON_BOOT_TEST = "applyonboottest";
    private static final String KEY_TASKER_TOAST = "showtaskertoast";
    private static final String KEY_SECURITY_CATEGORY = "security_category";
    private static final String KEY_BIOMETRIC = "biometric";
    private static final String KEY_SECTIONS = "sections";
    private static final String KEY_DEFAULT_SECTIONS = "default_section";

    private View mRootView;

    private int mColorSelection = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        assert mRootView != null;
        mRootView.setPadding(mRootView.getPaddingLeft(),
                Math.round(ViewUtils.getActionBarSize(requireActivity())),
                mRootView.getPaddingRight(), mRootView.getPaddingBottom());
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mColorSelection >= 0) {
            colorDialog(mColorSelection);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);

        findPreference(KEY_UPDATE_CHECK_AUTO).setOnPreferenceChangeListener(this);
        findPreference(KEY_UPDATE_CHECK).setOnPreferenceClickListener(this);
        findPreference(KEY_RESET_SETTINGS).setOnPreferenceClickListener(this);
        SwitchPreferenceCompat forceEnglish = findPreference(KEY_FORCE_ENGLISH);
        if (java.util.Locale.getDefault().getLanguage().startsWith("en")) {
            getPreferenceScreen().removePreference(forceEnglish);
        } else {
            assert forceEnglish != null;
            forceEnglish.setOnPreferenceChangeListener(this);
        }

        if (Utils.hideStartActivity()) {
            ((PreferenceCategory) Objects.requireNonNull(findPreference(KEY_USER_INTERFACE)))
                    .removePreference(Objects.requireNonNull(findPreference(KEY_MATERIAL_ICON)));
        } else {
            findPreference(KEY_MATERIAL_ICON).setOnPreferenceChangeListener(this);
        }

        findPreference(KEY_DARK_THEME).setOnPreferenceChangeListener(this);
        findPreference(KEY_BANNER_RESIZER).setOnPreferenceClickListener(this);
        findPreference(KEY_HIDE_BANNER).setOnPreferenceChangeListener(this);
        findPreference(KEY_ACCENT_COLOR).setOnPreferenceClickListener(this);
        findPreference(KEY_SECTIONS_ICON).setOnPreferenceChangeListener(this);
        findPreference(KEY_ENABLE_ON_BOOT).setOnPreferenceChangeListener(this);
        findPreference(KEY_APPLY_ON_BOOT_TEST).setOnPreferenceClickListener(this);
        findPreference(KEY_TASKER_TOAST).setOnPreferenceClickListener(this);

        if (!Utils.isFingerprintAvailable(getActivity())) {
            PreferenceCategory sectionSecurity = findPreference(KEY_SECURITY_CATEGORY);
            assert sectionSecurity != null;
            getPreferenceScreen().removePreference(sectionSecurity);
        }

        if (!UpdateCheck.isSignatureMatched(getActivity()) || Utils.isFDroidFlavor(requireActivity())) {
            PreferenceCategory appSettings = findPreference(KEY_APP_SETTINGS);
            if (appSettings != null) {
                getPreferenceScreen().removePreference(appSettings);
            }
        }

        NavigationActivity activity = (NavigationActivity) requireActivity();
        PreferenceCategory sectionsCategory = findPreference(KEY_SECTIONS);

        ListPreference defaultSection = findPreference(KEY_DEFAULT_SECTIONS);
        List<CharSequence> defaultSections = new ArrayList<>();
        List<CharSequence> defaultSectionsValues = new ArrayList<>();

        for (NavigationActivity.NavigationFragment navigationFragment : activity.getFragments()) {
            Class<? extends Fragment> fragmentClass = navigationFragment.mFragmentClass;
            int id = navigationFragment.mId;

            if (fragmentClass != null && fragmentClass != SettingsFragment.class) {
                @SuppressLint("PrivateResource") SwitchPreferenceCompat switchPreference = new SwitchPreferenceCompat(
                        new ContextThemeWrapper(getActivity(), R.style.Preference_SwitchPreferenceCompat_Material));
                switchPreference.setSummary(getString(id));
                switchPreference.setKey(fragmentClass.getSimpleName() + "_enabled");
                switchPreference.setChecked(Prefs.getBoolean(fragmentClass.getSimpleName()
                        + "_enabled", true, getActivity()));
                switchPreference.setOnPreferenceChangeListener(this);
                switchPreference.setPersistent(false);
                assert sectionsCategory != null;
                sectionsCategory.addPreference(switchPreference);

                defaultSections.add(getString(id));
                defaultSectionsValues.add(fragmentClass.getSimpleName());
            }
        }

        assert defaultSection != null;
        defaultSection.setEntries(defaultSections.toArray(new CharSequence[0]));
        defaultSection.setEntryValues(defaultSectionsValues.toArray(new CharSequence[0]));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        boolean checked = (boolean) o;
        String key = preference.getKey();
        switch (key) {
            case KEY_UPDATE_CHECK_AUTO:
                Prefs.saveBoolean("auto_update", checked, getActivity());
                return true;
            case KEY_FORCE_ENGLISH:
            case KEY_DARK_THEME:
                relaunchActivity();
                return true;
            case KEY_MATERIAL_ICON:
                Utils.setStartActivity(checked, requireActivity());
                return true;
            case KEY_HIDE_BANNER:
                return true;
            case KEY_ENABLE_ON_BOOT:
                Prefs.saveBoolean("enable_onboot", checked, getActivity());
                if (!Prefs.getBoolean("enable_onboot", true, getActivity())) {
                    Utils.snackbar(mRootView, getString(R.string.enable_onboot_message));
                }
                return true;
            case KEY_BIOMETRIC:
                Prefs.saveBoolean("use_biometric", checked, getActivity());
                return true;
            case KEY_TASKER_TOAST:
                Prefs.saveBoolean("showtaskertoast", checked, getActivity());
                return true;
            default:
                if (key.equals(KEY_SECTIONS_ICON) || key.endsWith("_enabled")) {
                    Prefs.saveBoolean(key, checked, getActivity());
                    ((NavigationActivity) requireActivity()).appendFragments();
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
        public void handleMessage(@NonNull Message msg) {
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
            case KEY_UPDATE_CHECK:
                if (!Utils.isNetworkAvailable(requireActivity())) {
                    Utils.snackbar(mRootView, getString(R.string.no_internet));
                } else if (Build.VERSION.SDK_INT >= 23) {
                    UpdateCheck.isManualUpdate(true);
                    new UpdateCheck().initialize(0, requireActivity());
                }
                return true;
            case KEY_RESET_SETTINGS:
                new Dialog(requireActivity())
                        .setMessage(getString(R.string.reset_settings_message))
                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                        })
                        .setPositiveButton(getString(R.string.yes), (dialog1, id1) -> {
                            resetSettings();
                        })
                        .show();
                return true;
            case KEY_BANNER_RESIZER:
                if (Utils.getOrientation(requireActivity()) == Configuration.ORIENTATION_PORTRAIT) {
                    Intent intent = new Intent(getActivity(), BannerResizerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Utils.snackbar(mRootView, getString(R.string.banner_resizer_message));
                }
                return true;
            case KEY_ACCENT_COLOR:
                List<Integer> sColors = new ArrayList<>();
                for (int i = 0; i < BorderCircleView.sAccentColors.size(); i++) {
                    sColors.add(BorderCircleView.sAccentColors.keyAt(i));
                }
                for (int i = 0; i < sColors.size(); i++) {
                    sColors.set(i, ContextCompat.getColor(requireActivity(), sColors.get(i)));
                }
                colorDialog(sColors.indexOf(ViewUtils.getThemeAccentColor(requireActivity())));
                return true;
            case KEY_APPLY_ON_BOOT_TEST:
                if (Utils.isServiceRunning(ApplyOnBootService.class, requireActivity())) {
                    Utils.snackbar(mRootView, getString(R.string.apply_on_boot_running));
                } else {
                    Intent intent = new Intent(getActivity(), ApplyOnBootService.class);
                    intent.putExtra("messenger", new Messenger(new MessengerHandler(getActivity())));
                    Utils.startService(getActivity(), intent);
                }
                return true;
        }
        return false;
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
                subView = new LinearLayout(requireActivity());
                subView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(subView);
            }

            BorderCircleView circle = new BorderCircleView(requireActivity());
            circle.setChecked(i == selection);
            circle.setCircleColor(ContextCompat.getColor(requireActivity(),
                    BorderCircleView.sAccentColors.keyAt(i)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            int margin = (int) getResources().getDimension(R.dimen.color_dialog_margin);
            params.setMargins(margin, margin, margin, margin);
            circle.setLayoutParams(params);
            circle.setOnClickListener(v -> {
                for (BorderCircleView borderCircleView : circles) {
                    if (v == borderCircleView) {
                        borderCircleView.setChecked(true);
                        mColorSelection = circles.indexOf(borderCircleView);
                    } else {
                        borderCircleView.setChecked(false);
                    }
                }
            });

            circles.add(circle);
            subView.addView(circle);
        }

        new Dialog(requireActivity()).setView(linearLayout)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    if (mColorSelection >= 0) {
                        Prefs.saveString(KEY_ACCENT_COLOR,
                                BorderCircleView.sAccentColors.valueAt(mColorSelection), getActivity());
                    }
                    relaunchActivity();
                }).setOnDismissListener(dialog -> mColorSelection = -1).show();
    }

    @Override
    @SuppressLint("RestrictedApi")
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new PreferenceGroupAdapter(preferenceScreen) {
            @Override
            public void onBindViewHolder(@NonNull PreferenceViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                Preference preference = getItem(position);
                if (preference instanceof PreferenceCategory)
                    setZeroPaddingToLayoutChildren(holder.itemView);
                else {
                    View iconFrame = holder.itemView.findViewById(R.id.icon_frame);
                    if (iconFrame != null) {
                        iconFrame.setVisibility(preference.getIcon() == null ? View.GONE : View.VISIBLE);
                    }
                }
            }
        };
    }

    private void setZeroPaddingToLayoutChildren(View view) {
        if (!(view instanceof ViewGroup))
            return;
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            setZeroPaddingToLayoutChildren(viewGroup.getChildAt(i));
            viewGroup.setPaddingRelative(0, viewGroup.getPaddingTop(), viewGroup.getPaddingEnd(), viewGroup.getPaddingBottom());
        }
    }

    public void resetSettings() {
        RootUtils.runCommand("rm -rf /data/data/" + requireActivity().getPackageName());
        RootUtils.runCommand("pm clear " + requireActivity().getPackageName() + " && am start -n " + requireActivity().getPackageName() + "/com.smartpack.kernelmanager.activities.MainActivity");
    }

    private void relaunchActivity() {
        requireActivity().finish();
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("selection", SettingsFragment.class.getCanonicalName());
        startActivity(intent);
    }

}