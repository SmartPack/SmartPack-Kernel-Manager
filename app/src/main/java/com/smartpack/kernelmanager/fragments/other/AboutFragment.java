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
package com.smartpack.kernelmanager.fragments.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.SwitchView;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;
import com.smartpack.kernelmanager.utils.tools.UpdateCheck;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by willi on 22.07.16.
 */
public class AboutFragment extends RecyclerViewFragment {

    private static final LinkedHashMap<String, String> sCredits = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> sTranslations = new LinkedHashMap<>();

    static {
        sCredits.put("libsu,topjohnwu", "https://github.com/topjohnwu");
        sCredits.put("Spectrum,Joe Maples", "https://github.com/frap129/spectrum");
        sCredits.put("AndroidX,Google", "https://developer.android.com/jetpack/androidx");
        sCredits.put("NavigationView,Google", "https://developer.android.com/reference/com/google/android/material/navigation/NavigationView");
        sCredits.put("CircularReveal,Ozodrukh", "https://github.com/ozodrukh/CircularReveal");
        sCredits.put("DashClock,Roman Nurik", "https://github.com/romannurik/dashclock");
        sCredits.put("Swirl,Matthew Precious", "https://github.com/mattprecious/swirl");
        sCredits.put("CircularImageView,Lopez Mikhael", "https://github.com/lopspower/CircularImageView");
        sCredits.put("Picasso,Square", "https://github.com/square/picasso");
        sCredits.put("Platform SDK,CyanogenMod", "https://github.com/CyanogenMod/cm_platform_sdk");
        sCredits.put("Round Corner Progress Bar,Akexorcist", "https://github.com/akexorcist/Android-RoundCornerProgressBar");
        sCredits.put("App Icon,Toxinpiper", "https://t.me/toxinpiper");
    }

    static {
        sTranslations.put("Chinese (rTW),jason5545", "https://github.com/jason5545");
        sTranslations.put("Chinese (rCN),Roiyaru", "https://github.com/Roiyaru");
        sTranslations.put("Russian,Andrey", "https://github.com/andrey167");
        sTranslations.put("Portuguese (rBr),Lennoard Silva", "https://github.com/Lennoard");
        sTranslations.put("Russian/Ukrainian,kiratt", "http://4pda.ru/forum/index.php?showuser=5859577");
        sTranslations.put("Amharic,Mikesew1320", "https://github.com/Mikesew1320");
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
        versioninfo.setDrawable(getResources().getDrawable(R.drawable.ic_on_boot_notification));
        versioninfo.setTitle(Utils.isSPDonated(requireActivity()) ? "Pro. " + getString(R.string.version) : getString(R.string.version));
        versioninfo.setSummary("beta_v" + BuildConfig.VERSION_NAME);

        DescriptionView support = new DescriptionView();
        support.setDrawable(getResources().getDrawable(R.drawable.ic_support));
        support.setTitle(getString(R.string.support));
        support.setSummary(getString(R.string.support_summary));
        support.setOnItemClickListener(item -> Utils.launchUrl("https://t.me/smartpack_kmanager", getActivity()));

        DescriptionView sourcecode = new DescriptionView();
        sourcecode.setDrawable(getResources().getDrawable(R.drawable.ic_source));
        sourcecode.setTitle(getString(R.string.source_code));
        sourcecode.setSummary(getString(R.string.source_code_summary));
        sourcecode.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Manager/tree/beta", requireActivity()));

        DescriptionView changelogs = new DescriptionView();
        changelogs.setDrawable(getResources().getDrawable(R.drawable.ic_changelog));
        changelogs.setTitle(getString(R.string.change_logs));
        changelogs.setSummary(getString(R.string.change_logs_summary));
        changelogs.setOnItemClickListener(item -> Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Manager/beta/change-logs.md", getActivity()));

        DescriptionView playstore = new DescriptionView();
        playstore.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
        playstore.setTitle(getString(R.string.playstore));
        playstore.setSummary(getString(R.string.playstore_summary));
        playstore.setOnItemClickListener(item -> {
            if (!Utils.isNetworkAvailable(requireActivity())) {
                Utils.snackbar(getRootView(), getString(R.string.no_internet));
                return;
            }
            Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.kernelmanager", requireActivity());
        });

        DescriptionView updatecheck = new DescriptionView();
        updatecheck.setDrawable(getResources().getDrawable(R.drawable.ic_update));
        updatecheck.setTitle(getString(R.string.check_update));
        updatecheck.setSummary(getString(R.string.check_update_summary));
        updatecheck.setOnItemClickListener(item -> {
            if (!Utils.isNetworkAvailable(requireActivity())) {
                Utils.snackbar(getRootView(), getString(R.string.no_internet));
                return;
            }
            UpdateCheck.manualUpdateCheck(getActivity());
        });

        SwitchView autoUpdateCheck = new SwitchView();
        autoUpdateCheck.setDrawable(getResources().getDrawable(R.drawable.ic_update));
        autoUpdateCheck.setSummary(getString(R.string.auto_update_check));
        autoUpdateCheck.setChecked(Prefs.getBoolean("auto_update", true, getActivity()));
        autoUpdateCheck.addOnSwitchListener((switchview, isChecked) -> Prefs.saveBoolean("auto_update", isChecked, getActivity()));

        DescriptionView donatetome = new DescriptionView();
        donatetome.setDrawable(getResources().getDrawable(R.drawable.ic_donate));
        donatetome.setTitle(getString(R.string.donate_me));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(item -> {
            Dialog donate_to_me = new Dialog(requireActivity());
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
                Utils.launchUrl("https://www.paypal.me/menacherry", getActivity());
            });
            donate_to_me.show();
        });

        DescriptionView share = new DescriptionView();
        share.setDrawable(getResources().getDrawable(R.drawable.ic_share));
        share.setTitle(getString(R.string.share_app));
        share.setSummary(getString(R.string.share_app_summary));
        share.setOnItemClickListener(item -> {
            Intent shareapp = new Intent();
            shareapp.setAction(Intent.ACTION_SEND);
            shareapp.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareapp.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message, "v" + BuildConfig.VERSION_NAME));
            shareapp.setType("text/plain");
            Intent shareIntent = Intent.createChooser(shareapp, null);
            startActivity(shareIntent);
        });

        items.add(about);
        items.add(versioninfo);
        items.add(sourcecode);
        items.add(support);
        items.add(changelogs);
        items.add(playstore);
        if (!Utils.isPlayStoreInstalled(requireActivity())) {
            items.add(updatecheck);
            if (Utils.isDownloadBinaries()) {
                items.add(autoUpdateCheck);
            }
        }
        items.add(donatetome);
        items.add(share);
    }

    private void librariesInit(List<RecyclerViewItem> items) {
        TitleView special_mentions = new TitleView();
        special_mentions.setText(getString(R.string.special_mentions));
        items.add(special_mentions);

        DescriptionView Grarak = new DescriptionView();
        Grarak.setDrawable(getResources().getDrawable(R.drawable.ic_grarak));
        Grarak.setTitle(getString(R.string.grarak));
        Grarak.setSummary(getString(R.string.grarak_summary));
        Grarak.setFullSpan(true);
        Grarak.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/Grarak", getActivity()));

        items.add(Grarak);

        DescriptionView osm0sis = new DescriptionView();
        osm0sis.setDrawable(getResources().getDrawable(R.drawable.ic_osm0sis));
        osm0sis.setTitle(getString(R.string.osm0sis));
        osm0sis.setSummary(getString(R.string.osm0sis_summary));
        osm0sis.setFullSpan(true);
        osm0sis.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/osm0sis", getActivity()));

        items.add(osm0sis);

        TitleView credits = new TitleView();
        credits.setText(getString(R.string.credits));
        items.add(credits);

        for (final String lib : sCredits.keySet()) {
            DescriptionView descriptionView = new DescriptionView();
            switch (lib.split(",")[1]) {
                case "topjohnwu":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_topjohnwu));
                    break;
                case "Joe Maples":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_frap129));
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
                case "Toxinpiper":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_on_boot_notification));
                    break;
            }
            descriptionView.setTitle(lib.split(",")[1]);
            descriptionView.setSummary(lib.split(",")[0]);
            descriptionView.setOnItemClickListener(item -> Utils.launchUrl(sCredits.get(lib), getActivity()));

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
                case "kiratt":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_kiratt));
                    break;
                case "Mikesew1320":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_mikesew));
                    break;
            }
            descriptionView.setTitle(lib.split(",")[1]);
            descriptionView.setSummary(lib.split(",")[0]);
            descriptionView.setOnItemClickListener(item -> Utils.launchUrl(sTranslations.get(lib), getActivity()));

            items.add(descriptionView);
        }
    }

    public static class InfoFragment extends BaseFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
            rootView.findViewById(R.id.image).setOnClickListener(view -> {
                Dialog licence = new Dialog(requireActivity());
                licence.setIcon(R.mipmap.ic_launcher);
                licence.setTitle(getString(R.string.licence));
                licence.setMessage(getString(R.string.licence_message));
                licence.setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });

                licence.show();
            });
            return rootView;
        }
    }

}
