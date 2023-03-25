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
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.smartpack.kernelmanager.BuildConfig;
import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.fragments.BaseFragment;
import com.smartpack.kernelmanager.fragments.RecyclerViewFragment;
import com.smartpack.kernelmanager.utils.Common;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.ViewUtils;
import com.smartpack.kernelmanager.utils.other.Billing;
import com.smartpack.kernelmanager.views.dialog.Dialog;
import com.smartpack.kernelmanager.views.recyclerview.CardView;
import com.smartpack.kernelmanager.views.recyclerview.DescriptionView;
import com.smartpack.kernelmanager.views.recyclerview.RecyclerViewItem;
import com.smartpack.kernelmanager.views.recyclerview.TitleView;

import java.util.List;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.Credits.sCreditsUtils;
import in.sunilpaulmathew.sCommon.TranslatorUtils.sTranslatorUtils;

/**
 * Created by willi on 22.07.16.
 */
public class AboutFragment extends RecyclerViewFragment {

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

    @SuppressLint({"UseCompatLoadingForDrawables", "StringFormatInvalid"})
    private void aboutInit(List<RecyclerViewItem> items) {
        TitleView about = new TitleView();
        about.setText(getString(R.string.app_name));

        DescriptionView versioninfo = new DescriptionView();
        versioninfo.setDrawable(getResources().getDrawable(R.drawable.ic_on_boot_notification));
        versioninfo.setTitle(getString(R.string.version,"").replace(": ", ""));
        versioninfo.setSummary(((Utils.isDonated() || !Utils.isFDroidFlavor(requireActivity())) ? " Pro " : " ") + BuildConfig.VERSION_NAME);
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
        licence.setOnItemClickListener(item -> Utils.launchWebView(getString(R.string.licence), "https://www.gnu.org/licenses/gpl-3.0-standalone.html", requireActivity()));

        DescriptionView sourcecode = new DescriptionView();
        sourcecode.setDrawable(getResources().getDrawable(R.drawable.ic_source));
        sourcecode.setTitle(getString(R.string.source_code));
        sourcecode.setSummary(getString(R.string.source_code_summary));
        sourcecode.setOnItemClickListener(item -> Utils.launchUrl("https://github.com/SmartPack/SmartPack-Kernel-Manager", requireActivity()));

        DescriptionView changelogs = new DescriptionView();
        changelogs.setDrawable(getResources().getDrawable(R.drawable.ic_changelog));
        changelogs.setTitle(getString(R.string.change_logs));
        changelogs.setSummary(getString(R.string.change_logs_summary));
        changelogs.setOnItemClickListener(item -> new Dialog(requireActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name) + "\n" + BuildConfig.VERSION_NAME)
                .setMessage(Utils.getChangelog(getActivity()))
                .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                })
                .setPositiveButton(getString(R.string.more), (dialog1, id1) -> Utils.launchWebView(getString(R.string.change_logs), "https://github.com/SmartPack/SmartPack-Kernel-Manager/raw/master/change-logs.md", getActivity()))
                .show());

        DescriptionView appStore = new DescriptionView();
        appStore.setDrawable(getResources().getDrawable(Utils.isFDroidFlavor(requireActivity()) ? R.drawable.ic_fdroid : R.drawable.ic_playstore));
        appStore.setTitle(getString(Utils.isFDroidFlavor(requireActivity()) ? R.string.fdroid : R.string.playstore));
        appStore.setSummary(getString(Utils.isFDroidFlavor(requireActivity()) ? R.string.fdroid_summary : R.string.playstore_summary));
        appStore.setOnItemClickListener(item -> Utils.launchUrl(Utils.isFDroidFlavor(requireActivity()) ? "https://f-droid.org/packages/com.smartpack.kernelmanager" :
                "https://play.google.com/store/apps/details?id=com.smartpack.kernelmanager.pro", requireActivity()));

        DescriptionView donatetome = new DescriptionView();
        donatetome.setDrawable(getResources().getDrawable(R.drawable.ic_donate));
        donatetome.setTitle(getString(R.string.donations));
        donatetome.setSummary(getString(R.string.donate_me_summary));
        donatetome.setOnItemClickListener(item -> Billing.showDonationMenu(requireActivity()));

        DescriptionView share = new DescriptionView();
        share.setDrawable(getResources().getDrawable(R.drawable.ic_share));
        share.setTitle(getString(R.string.share_app));
        share.setSummary(getString(R.string.share_app_summary));
        share.setOnItemClickListener(item -> {
            Intent shareapp = new Intent();
            shareapp.setAction(Intent.ACTION_SEND);
            shareapp.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareapp.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message,BuildConfig.VERSION_NAME)
                    + (Utils.isFDroidFlavor(requireActivity()) ? " F-Droid: https://f-droid.org/packages/com.smartpack.kernelmanager"
                    : " Google Play: https://play.google.com/store/apps/details?id=com.smartpack.kernelmanager.pro"));
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
        if (Utils.isFDroidFlavor(requireActivity())) {
            items.add(donatetome);
        }
        items.add(share);
        items.add(appStore);
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

        CardView credits = new CardView(requireActivity());
        credits.setTitle(getString(R.string.credits));
        credits.setFullSpan(true);
        DescriptionView creditsSummary = new DescriptionView();
        creditsSummary.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_credits, requireActivity()));
        creditsSummary.setSummary(getString(R.string.credits_summary));
        creditsSummary.setOnItemClickListener(item -> new sCreditsUtils(Common.getCredits(),
                sCommonUtils.getDrawable(R.mipmap.ic_launcher, requireActivity()),
                sCommonUtils.getDrawable(R.drawable.ic_back, requireActivity()),
                ViewUtils.getThemeAccentColor(requireActivity()),
                18, getString(R.string.app_name), "2022-2023, sunilpaulmathew",
                BuildConfig.VERSION_NAME).launchCredits(requireActivity()));
        credits.addItem(creditsSummary);
        items.add(credits);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "StaticFieldLeak"})
    private void translationsInit(List<RecyclerViewItem> items) {
        CardView translatorCard = new CardView(getActivity());
        translatorCard.setTitle(getString(R.string.translations));
        translatorCard.setFullSpan(true);

        DescriptionView translator = new DescriptionView();
        translator.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_translate, requireActivity()));
        translator.setSummary(getString(R.string.translations_summary));
        translator.setOnItemClickListener(item -> new sTranslatorUtils(getString(R.string.smartpack), "https://poeditor.com/join/project?hash=qWFlVfAlp5", requireActivity()).show());

        translatorCard.addItem(translator);
        items.add(translatorCard);
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
