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
package com.grarak.kerneladiutor.fragments.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.dialog.Dialog;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;
import com.smartpack.kernelmanager.utils.UpdateCheck;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 22.07.16.
 */
public class AboutFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<String, String> sCredits = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> sTranslations = new LinkedHashMap<>();

    static {
        sCredits.put("Kernel Adiutor,Willi Ye", "https://github.com/Grarak");
        sCredits.put("Auto-flash,Chris Renshaw", "https://github.com/osm0sis");
        sCredits.put("Spectrum,Joe Maples", "https://github.com/frap129/spectrum");
        sCredits.put("AppUpdater,Javier Santos", "https://github.com/javiersantos/AppUpdater");
        sCredits.put("AndroidX,Google", "https://developer.android.com/jetpack/androidx");
        sCredits.put("NavigationView,Google", "https://developer.android.com/reference/com/google/android/material/navigation/NavigationView");
        sCredits.put("CircularReveal,Ozodrukh", "https://github.com/ozodrukh/CircularReveal");
        sCredits.put("DashClock,Roman Nurik", "https://github.com/romannurik/dashclock");
        sCredits.put("Swirl,Matthew Precious", "https://github.com/mattprecious/swirl");
        sCredits.put("CircularImageView,Lopez Mikhael", "https://github.com/lopspower/CircularImageView");
        sCredits.put("Picasso,Square", "https://github.com/square/picasso");
        sCredits.put("Round Corner Progress Bar,Akexorcist", "https://github.com/akexorcist/Android-RoundCornerProgressBar");
        sCredits.put("Platform SDK,CyanogenMod", "https://github.com/CyanogenMod/cm_platform_sdk");
    }

    static {
        sTranslations.put("Chinese (rTW),jason5545", "https://github.com/jason5545");
        sTranslations.put("Chinese (rCN),Roiyaru", "https://github.com/Roiyaru");
        sTranslations.put("Russian,Andrey", "https://github.com/andrey167");
        sTranslations.put("Portuguese (rBr),Lennoard Silva", "https://github.com/Lennoard");
    }

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new InfoFragment());
    }

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        aboutInit(items);
        librariesInit(items);
        translationsInit(items);
    }

    private void aboutInit(List<RecyclerViewItem> items) {

        TitleView about = new TitleView();
        about.setText(getString(R.string.app_name));

        DescriptionView versioninfo = new DescriptionView();
        versioninfo.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        versioninfo.setTitle(Utils.isSPDonated(requireActivity()) ? "Pro. " + getString(R.string.version) : getString(R.string.version));
        versioninfo.setSummary("v" + BuildConfig.VERSION_NAME);

        DescriptionView support = new DescriptionView();
        support.setDrawable(getResources().getDrawable(R.drawable.ic_support));
        support.setTitle(getString(R.string.support));
        support.setSummary(getString(R.string.support_summary));
        support.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (!Utils.isNetworkAvailable(getContext())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                Utils.launchUrl("https://forum.xda-developers.com/android/apps-games/app-smartpack-kernel-manager-t3854717", getActivity());
            }
        });

        DescriptionView sourcecode = new DescriptionView();
        sourcecode.setDrawable(getResources().getDrawable(R.drawable.ic_source));
        sourcecode.setTitle(getString(R.string.source_code));
        sourcecode.setSummary(getString(R.string.source_code_summary));
        sourcecode.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (!Utils.isNetworkAvailable(getContext())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Manager", requireActivity());
            }
        });

        DescriptionView changelogs = new DescriptionView();
        changelogs.setDrawable(getResources().getDrawable(R.drawable.ic_changelog));
        changelogs.setTitle(getString(R.string.change_logs));
        changelogs.setSummary(getString(R.string.change_logs_summary));
        changelogs.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (!Utils.isNetworkAvailable(getContext())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Manager/master/change-logs.md", getActivity());
            }
        });

        DescriptionView updatecheck = new DescriptionView();
        updatecheck.setDrawable(getResources().getDrawable(R.drawable.ic_update));
        updatecheck.setTitle(getString(R.string.check_update));
        updatecheck.setSummary(getString(R.string.check_update_summary));
        updatecheck.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (!Utils.isNetworkAvailable(getContext())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                UpdateCheck.manualUpdateCheck(getActivity());
            }
        });

        SwitchView autoUpdateCheck = new SwitchView();
        autoUpdateCheck.setDrawable(getResources().getDrawable(R.drawable.ic_update));
        autoUpdateCheck.setTitle(getString(R.string.auto_update_check));
        autoUpdateCheck.setSummary(getString(R.string.auto_update_check_summary));
        autoUpdateCheck.setChecked(Prefs.getBoolean("auto_update", true, getActivity()));
        autoUpdateCheck.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchview, boolean isChecked) {
                Prefs.saveBoolean("auto_update", isChecked, getActivity());
            }
        });

        DescriptionView donatetome = new DescriptionView();
        donatetome.setDrawable(getResources().getDrawable(R.drawable.ic_donate));
        donatetome.setTitle(getString(R.string.donate_me));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (!Utils.isNetworkAvailable(getContext())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                Dialog donate_to_me = new Dialog(getActivity());
                donate_to_me.setIcon(R.mipmap.ic_launcher);
                donate_to_me.setTitle(getString(R.string.donate_me));
                if (Utils.isSPDonated(requireActivity())) {
                    donate_to_me.setMessage(getString(R.string.donate_me_message));
                    donate_to_me.setNeutralButton(getString(R.string.donate_nope), (dialogInterface, i) -> {
                    });
                } else {
                    donate_to_me.setMessage(getString(R.string.donate_me_message) + getString(R.string.donate_me_playstore));
                    donate_to_me.setNeutralButton(getString(R.string.purchase_app), (dialogInterface, i) -> {
                        Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.donate", getActivity());
                    });
                }
                donate_to_me.setPositiveButton(getString(R.string.paypal_donation), (dialog1, id1) -> {
                    Utils.launchUrl("https://www.paypal.me/sunilpaulmathew", getActivity());
                });
                donate_to_me.show();
            }
        });

        DescriptionView share = new DescriptionView();
        share.setDrawable(getResources().getDrawable(R.drawable.ic_share));
        share.setTitle(getString(R.string.share_app));
        share.setSummary(getString(R.string.share_app_summary));
        share.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
            @Override
            public void onClick(RecyclerViewItem item) {
                if (!Utils.isNetworkAvailable(getContext())) {
                    Utils.toast(R.string.no_internet, getActivity());
                    return;
                }
                Intent shareapp = new Intent();
                shareapp.setAction(Intent.ACTION_SEND);
                shareapp.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                shareapp.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message, "v" + BuildConfig.VERSION_NAME)
                        + "https://github.com/SmartPack/SmartPack-Kernel-Manager/blob/master/download/com.smartpack.kernelmanager.apk?raw=true");
                shareapp.setType("text/plain");
                Intent shareIntent = Intent.createChooser(shareapp, null);
                startActivity(shareIntent);
            }
        });

        items.add(about);
        items.add(versioninfo);
        items.add(support);
        items.add(sourcecode);
        items.add(changelogs);
        items.add(updatecheck);
        items.add(autoUpdateCheck);
        items.add(donatetome);
        items.add(share);
    }

    private void librariesInit(List<RecyclerViewItem> items) {

        TitleView credits = new TitleView();
        credits.setText(getString(R.string.credits));
        items.add(credits);

        for (final String lib : sCredits.keySet()) {
            DescriptionView descriptionView = new DescriptionView();
            switch (lib.split(",")[1]) {
                case "Willi Ye":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_grarak));
                    break;
                case "Chris Renshaw":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_osm0sis));
                    break;
                case "Joe Maples":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_frap129));
                    break;
                case "Javier Santos":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_javiersantos));
                    break;
                case "Google":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_google));
                    break;
                case "Ozodrukh":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_ozodrukh));
                    break;
                case "Roman Nurik":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_romannurik));
                    break;
                case "Matthew Precious":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_mattprecious));
                    break;
                case "Lopez Mikhael":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_lopspower));
                    break;
                case "Square":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_square));
                    break;
                case "Akexorcist":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_akexorcist));
                    break;
                case "CyanogenMod":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_cyanogenmod));
                    break;
            }
            descriptionView.setTitle(lib.split(",")[1]);
            descriptionView.setSummary(lib.split(",")[0]);
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (!Utils.isNetworkAvailable(getContext())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    Utils.launchUrl(sCredits.get(lib), getActivity());
                }
            });

            items.add(descriptionView);
        }
    }

    private void translationsInit(List<RecyclerViewItem> items) {

        TitleView translators = new TitleView();
        translators.setText(getString(R.string.translators));
        items.add(translators);

        for (final String lib : sTranslations.keySet()) {
            DescriptionView descriptionView = new DescriptionView();
            switch (lib.split(",")[1]) {
                case "jason5545":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_jason5545));
                    break;
                case "Andrey":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_andrey167));
                    break;
                case "Roiyaru":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_roiyaru));
                    break;
                case "Lennoard Silva":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_lennoard));
                    break;
            }
            descriptionView.setTitle(lib.split(",")[1]);
            descriptionView.setSummary(lib.split(",")[0]);
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    if (!Utils.isNetworkAvailable(getContext())) {
                        Utils.toast(R.string.no_internet, getActivity());
                        return;
                    }
                    Utils.launchUrl(sTranslations.get(lib), getActivity());
                }
            });

            items.add(descriptionView);
        }
    }

    public static class InfoFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
            rootView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog licence = new Dialog(getActivity());
                    licence.setIcon(R.mipmap.ic_launcher);
                    licence.setTitle(getString(R.string.licence));
                    licence.setMessage(getString(R.string.licence_message));
                    licence.setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    });

                    licence.show();
                }
            });
            return rootView;
        }
    }

}
