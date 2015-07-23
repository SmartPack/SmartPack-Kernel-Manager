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

package com.grarak.kerneladiutor.fragments.tools.download;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.grarak.kerneladiutor.KernelActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.cards.download.DownloadCardView;
import com.grarak.kerneladiutor.elements.cards.download.DownloadInfoCardView;
import com.grarak.kerneladiutor.elements.cards.download.FeatureCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.fragments.ViewPagerFragment;
import com.grarak.kerneladiutor.utils.Downloads;
import com.grarak.kerneladiutor.utils.Utils;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

/**
 * Created by willi on 20.06.15.
 */
public class ParentFragment extends ViewPagerFragment {

    public static ParentFragment newInstance(Downloads.KernelContent kernelContent) {
        ParentFragment fragment = new ParentFragment();
        fragment.kernelContent = kernelContent;
        return fragment;
    }

    private static ParentFragment parentFragment;
    private Downloads.KernelContent kernelContent;
    private TextView descriptionText;
    private View viewContainer;
    private View viewContainerBackground;
    private View logoContainer;
    private Toolbar toolbar;
    private Animation animation;

    @Override
    public View getParentView() {
        View view = inflater.inflate(R.layout.download_viewpager, container, false);
        descriptionText = (TextView) view.findViewById(R.id.description);
        viewContainer = view.findViewById(R.id.view_container);
        viewContainerBackground = view.findViewById(R.id.view_container_background);
        return view;
    }

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
        parentFragment = this;

        String description;
        if (kernelContent != null && (description = kernelContent.getShortDescription()) != null)
            descriptionText.setText(Html.fromHtml(description));
        logoContainer = ((KernelActivity) getActivity()).getLogoContainer();
        toolbar = ((KernelActivity) getActivity()).getToolbar();
        animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                descriptionText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewContainerBackground.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (kernelContent != null)
            addFragment(new ViewPagerItem(DownloadRecyclerViewFragment.InfoFragment.newInstance(kernelContent),
                    getString(R.string.information)));

        List<Downloads.Feature> features;
        if (kernelContent != null && (features = kernelContent.getFeatures()).size() > 0)
            addFragment(new ViewPagerItem(DownloadRecyclerViewFragment.FeaturesFragment.newInstance(features),
                    getString(R.string.features)));

        List<Downloads.Download> downloads;
        if (kernelContent != null && (downloads = kernelContent.getDownloads()).size() > 0)
            addFragment(new ViewPagerItem(DownloadRecyclerViewFragment.DownloadFragment.newInstance(downloads),
                    getString(R.string.download)));
    }

    @Override
    public void onSwipe(int page) {
        super.onSwipe(page);
        for (int i = 0; i < getCount(); i++)
            ((DownloadRecyclerViewFragment) getFragment(i)).resetTranslations();
    }

    private static class CustomOnScrollListener extends RecyclerView.OnScrollListener {

        private final Context context;
        private int scrollDistance;
        private int logoViewContainer;
        private int viewContainerOffset;
        private int toolbarOffset;
        private boolean isColored;

        public CustomOnScrollListener() {
            context = parentFragment.logoContainer.getContext();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            scrollDistance += dy;
            logoViewContainer += dy;
            float logoViewTranslation = logoViewContainer / 2;
            int logoContainerHeight = parentFragment.logoContainer.getHeight();
            if (logoViewTranslation > logoContainerHeight)
                logoViewTranslation = logoContainerHeight;
            else if (logoViewTranslation < 0) logoViewTranslation = 0;
            ViewHelper.setTranslationY(parentFragment.logoContainer, -logoViewTranslation);

            viewContainerOffset += dy;
            int viewContainerHeight = parentFragment.viewContainer.getHeight();
            if (viewContainerOffset > viewContainerHeight)
                viewContainerOffset = viewContainerHeight;
            else if (viewContainerOffset < 0) viewContainerOffset = 0;
            ViewHelper.setTranslationY(parentFragment.viewContainer, -viewContainerOffset);

            int toolbarHeight = parentFragment.toolbar.getHeight();
            if (viewContainerOffset >= viewContainerHeight -
                    toolbarHeight - parentFragment.mTabs.getHeight() || dy < 0) {
                toolbarOffset += dy;
                if (toolbarOffset > toolbarHeight)
                    toolbarOffset = toolbarHeight;
                else if (toolbarOffset < 0) toolbarOffset = 0;
                ViewHelper.setTranslationY(parentFragment.toolbar, -toolbarOffset);
            }

            if (!isColored && scrollDistance >= viewContainerHeight - toolbarHeight && dy < 0) {
                parentFragment.toolbar.setBackgroundColor(context.getResources().getColor(R.color.color_primary));
                parentFragment.descriptionText.setVisibility(View.VISIBLE);
                parentFragment.viewContainerBackground.setBackgroundColor(context.getResources()
                        .getColor(R.color.color_primary));
                isColored = true;
            }

            if (isColored && scrollDistance == 0) {
                parentFragment.toolbar.setBackgroundColor(Color.TRANSPARENT);
                parentFragment.viewContainerBackground.startAnimation(parentFragment.animation);
                isColored = false;
            }
        }

        public void reset() {
            scrollDistance = 0;
            logoViewContainer = 0;
            viewContainerOffset = 0;
            toolbarOffset = 0;

            ViewHelper.setTranslationY(parentFragment.logoContainer, 0);
            ViewHelper.setTranslationY(parentFragment.viewContainer, 0);
            ViewHelper.setTranslationY(parentFragment.toolbar, 0);
        }
    }

    public static class DownloadRecyclerViewFragment extends RecyclerViewFragment {

        private CustomOnScrollListener onScrollListener;

        @Override
        public int getSpan() {
            return 1;
        }

        @Override
        public boolean showApplyOnBoot() {
            return false;
        }

        @Override
        public RecyclerView getRecyclerView() {
            View view = getParentView(R.layout.download_recyclerview);
            return (RecyclerView) view.findViewById(R.id.recycler_view);
        }

        @Override
        public void setOnScrollListener(RecyclerView recyclerView) {
            recyclerView.addOnScrollListener(onScrollListener == null ? onScrollListener =
                    new CustomOnScrollListener() : onScrollListener);
        }

        @Override
        public void onViewCreated() {
            super.onViewCreated();
            resetTranslations();
        }

        @Override
        public void resetTranslations() {
            parentFragment.toolbar.setBackgroundColor(Color.TRANSPARENT);
            parentFragment.viewContainerBackground.setBackgroundColor(Color.TRANSPARENT);
            parentFragment.descriptionText.setVisibility(View.GONE);

            int orientation = Utils.getScreenOrientation(getActivity());
            float density = getResources().getDisplayMetrics().density;

            float tabsPadding = orientation == Configuration.ORIENTATION_PORTRAIT ? 0 : density * 48;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) parentFragment.mTabs.getLayoutParams();
            layoutParams.setMargins((int) tabsPadding, 0, (int) tabsPadding, 0);
            parentFragment.mTabs.requestLayout();

            setPaddingRecyclerview(orientation, density);
            if (onScrollListener != null) onScrollListener.reset();
            layoutManager.scrollToPositionWithOffset(0, 0);
        }

        public void setPaddingRecyclerview(int orientation, float density) {
            float recyclerviewPadding = orientation == Configuration.ORIENTATION_PORTRAIT ? 0 : density * 48;
            recyclerView.setPadding((int) recyclerviewPadding, recyclerView.getPaddingTop(), (int) recyclerviewPadding,
                    recyclerView.getPaddingBottom());
        }

        public static class InfoFragment extends ParentFragment.DownloadRecyclerViewFragment {

            public static InfoFragment newInstance(Downloads.KernelContent kernelContent) {
                InfoFragment fragment = new InfoFragment();
                fragment.kernelContent = kernelContent;
                return fragment;
            }

            private Downloads.KernelContent kernelContent;

            @Override
            public void init(Bundle savedInstanceState) {
                super.init(savedInstanceState);

                if (kernelContent != null && kernelContent.getShortDescription() != null
                        && kernelContent.getLongDescription() != null)
                    addView(new DownloadInfoCardView.DDDownloadInfoCard(kernelContent));
            }

        }

        public static class FeaturesFragment extends ParentFragment.DownloadRecyclerViewFragment {

            public static FeaturesFragment newInstance(List<Downloads.Feature> features) {
                FeaturesFragment fragment = new FeaturesFragment();
                fragment.features = features;
                return fragment;
            }

            private List<Downloads.Feature> features;

            @Override
            public void init(Bundle savedInstanceState) {
                super.init(savedInstanceState);

                if (features != null) for (Downloads.Feature feature : features)
                    addView(new FeatureCardView.DFeatureCard(feature));
            }

        }

        public static class DownloadFragment extends ParentFragment.DownloadRecyclerViewFragment {

            public static DownloadFragment newInstance(List<Downloads.Download> downloads) {
                DownloadFragment fragment = new DownloadFragment();
                fragment.downloads = downloads;
                return fragment;
            }

            private List<Downloads.Download> downloads;

            @Override
            public void init(Bundle savedInstanceState) {
                super.init(savedInstanceState);

                if (downloads != null) for (Downloads.Download download : downloads) {
                    if (download.getName() != null && download.getMD5sum() != null)
                        addView(new DownloadCardView.DDownloadCard(download));
                }
            }

        }

    }

}
