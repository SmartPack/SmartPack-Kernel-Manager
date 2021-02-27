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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.activities.BillingActivity;
import com.smartpack.kernelmanager.activities.LicenceActivity;
import com.smartpack.kernelmanager.activities.TranslatorActivity;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

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
        sTranslations.put("Korean,SmgKhOaRn", "https://github.com/SmgKhOaRn");
        sTranslations.put("German (rDE),free-bots", "https://github.com/free-bots");
        sTranslations.put("Spanish,Alejandro YT", "");
        sTranslations.put("Chinese (rCN),YFdyh000", "https://github.com/yfdyh000");
        sTranslations.put("Polish,Fruity-0", "https://github.com/Fruity-0");
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void aboutInit(List<RecyclerViewItem> items) {
        TitleView about = new TitleView();
        about.setText(getString(R.string.app_name));

        DescriptionView versioninfo = new DescriptionView();
        versioninfo.setDrawable(getResources().getDrawable(R.drawable.ic_on_boot_notification));
        versioninfo.setTitle(getString(R.string.version));
        versioninfo.setSummary((Utils.isDonated(requireActivity()) ? " Pro v" : "v") + BuildConfig.VERSION_NAME);
        versioninfo.setOnItemClickListener(item -> {
            Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            settings.setData(uri);
            startActivity(settings);
        });

        DescriptionView support = new DescriptionView();
        support.setDrawable(getResources().getDrawable(R.drawable.ic_support));
        support.setTitle(getString(R.string.support));
        support.setSummary(getString(R.string.support_summary));
        support.setOnItemClickListener(item -> Utils.launchUrl("https://t.me/smartpack_kmanager", getActivity()));

        DescriptionView licence = new DescriptionView();
        licence.setDrawable(getResources().getDrawable(R.drawable.ic_gpl));
        licence.setTitle(getString(R.string.licence));
        licence.setSummary(getString(R.string.licence_summary));
        licence.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireActivity(), LicenceActivity.class);
            startActivity(intent);
        });

        DescriptionView sourcecode = new DescriptionView();
        sourcecode.setDrawable(getResources().getDrawable(R.drawable.ic_source));
        sourcecode.setTitle(getString(R.string.source_code));
        sourcecode.setSummary(getString(R.string.source_code_summary));
        sourcecode.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Manager", requireActivity()));

        DescriptionView changelogs = new DescriptionView();
        changelogs.setDrawable(getResources().getDrawable(R.drawable.ic_changelog));
        changelogs.setTitle(getString(R.string.change_logs));
        changelogs.setSummary(getString(R.string.change_logs_summary));
        changelogs.setOnItemClickListener(item -> Utils.launchUrl("https://raw.githubusercontent.com/SmartPack/SmartPack-Kernel-Manager/master/change-logs.md", getActivity()));

        DescriptionView playstore = new DescriptionView();
        playstore.setDrawable(getResources().getDrawable(R.drawable.ic_playstore));
        playstore.setTitle(getString(R.string.playstore));
        playstore.setSummary(getString(R.string.playstore_summary));
        playstore.setOnItemClickListener(item -> {
            if (!Utils.isNetworkAvailable(requireActivity())) {
                Utils.snackbar(getRootView(), getString(R.string.no_internet));
                return;
            }
            Utils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.kernelmanager.release", requireActivity());
        });

        DescriptionView donatetome = new DescriptionView();
        donatetome.setDrawable(getResources().getDrawable(R.drawable.ic_donate));
        donatetome.setTitle(getString(R.string.donations));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireActivity(), BillingActivity.class);
            startActivity(intent);
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
        items.add(licence);
        items.add(support);
        items.add(changelogs);
        items.add(sourcecode);
        items.add(donatetome);
        items.add(share);
        items.add(playstore);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void librariesInit(List<RecyclerViewItem> items) {
        TitleView special_mentions = new TitleView();
        special_mentions.setText(getString(R.string.special_mentions));
        items.add(special_mentions);

        CardView GrarakCard = new CardView(getActivity());
        GrarakCard.setTitle(getString(R.string.grarak));
        GrarakCard.setFullSpan(true);

        DescriptionView Grarak = new DescriptionView();
        Grarak.setDrawable(getResources().getDrawable(R.drawable.ic_grarak));
        Grarak.setSummary(getString(R.string.grarak_summary));
        Grarak.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/Grarak", getActivity()));

        GrarakCard.addItem(Grarak);
        items.add(GrarakCard);

        CardView osm0sisCard = new CardView(getActivity());
        osm0sisCard.setTitle(getString(R.string.osm0sis));
        osm0sisCard.setFullSpan(true);

        DescriptionView osm0sis = new DescriptionView();
        osm0sis.setDrawable(getResources().getDrawable(R.drawable.ic_osm0sis));
        osm0sis.setSummary(getString(R.string.osm0sis_summary));
        osm0sis.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/osm0sis", getActivity()));

        osm0sisCard.addItem(osm0sis);
        items.add(osm0sisCard);

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

    @SuppressLint({"UseCompatLoadingForDrawables", "StaticFieldLeak"})
    private void translationsInit(List<RecyclerViewItem> items) {
        TitleView translators = new TitleView();
        translators.setText(getString(R.string.translators));
        items.add(translators);

        CardView translatorCard = new CardView(getActivity());
        translatorCard.setFullSpan(true);

        DescriptionView translator = new DescriptionView();
        translator.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_language, requireActivity()));
        translator.setSummary(getString(R.string.translators_message));
        translator.setOnItemClickListener(item -> {
            new Dialog(requireActivity()).setItems(getResources().getStringArray(
                    R.array.translator_options), (dialogInterface, i) -> {
                switch (i) {
                    case 0:
                        Utils.launchUrl("https://poeditor.com/join/project?hash=qWFlVfAlp5", requireActivity());
                        break;
                    case 1:
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                showProgressMessage(getString(R.string.importing_string) + ("..."));
                                showProgress();
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                Utils.importTranslation("values", getActivity());
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                hideProgress();
                                Intent intent = new Intent(requireActivity(), TranslatorActivity.class);
                                startActivity(intent);
                            }
                        }.execute();
                        break;
                }
            }).setOnDismissListener(dialogInterface -> {
            }).show();
        });

        translatorCard.addItem(translator);
        items.add(translatorCard);

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
                case "SmgKhOaRn":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_smg));
                    break;
                case "free-bots":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_freebots));
                    break;
                case "Alejandro YT":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_es));
                    break;
                case "YFdyh000":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_yfdyh000));
                    break;
                case "Fruity-0":
                    descriptionView.setDrawable(getResources().getDrawable(R.drawable.ic_fruity0));
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
            return inflater.inflate(R.layout.fragment_info, container, false);
        }
    }

}
