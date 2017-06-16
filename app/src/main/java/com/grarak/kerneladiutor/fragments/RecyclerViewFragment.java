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
package com.grarak.kerneladiutor.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.activities.BaseActivity;
import com.grarak.kerneladiutor.activities.NavigationActivity;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.grarak.kerneladiutor.views.dialog.ViewPagerDialog;
import com.grarak.kerneladiutor.views.recyclerview.AdView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewAdapter;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by willi on 16.04.16.
 */
public abstract class RecyclerViewFragment extends BaseFragment {

    public boolean mDelay;
    private Handler mHandler;

    private View mRootView;

    private List<RecyclerViewItem> mItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Scroller mScroller;

    private AdView mAdView;

    private View mProgress;

    private List<Fragment> mViewPagerFragments;
    private ViewPagerAdapter mViewPagerAdapter;
    private View mViewPagerParent;
    private ViewPager mViewPager;
    private View mViewPagerShadow;
    private CirclePageIndicator mCirclePageIndicator;

    private FloatingActionButton mTopFab;
    private FloatingActionButton mBottomFab;

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolBar;

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    private ValueAnimator mForegroundAnimator;
    private boolean mForegroundVisible;
    private View mForegroundParent;
    private TextView mForegroundText;
    private float mForegroundHeight;
    private CharSequence mForegroundStrText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(hideBanner());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        if (mHandler == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHandler = new Handler();
                }
            });
        }

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);

        if (mViewPagerFragments != null && !hideBanner()) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            for (Fragment fragment : mViewPagerFragments) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.commit();
            mViewPagerFragments.clear();
        } else {
            mViewPagerFragments = new ArrayList<>();
        }
        mViewPagerParent = mRootView.findViewById(R.id.viewpagerparent);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
        mViewPager.setVisibility(View.INVISIBLE);
        mViewPagerShadow = mRootView.findViewById(R.id.viewpager_shadow);
        mViewPagerShadow.setVisibility(View.INVISIBLE);
        mCirclePageIndicator = (CirclePageIndicator) mRootView.findViewById(R.id.indicator);
        resizeBanner();
        mViewPagerParent.setVisibility(View.INVISIBLE);
        ViewUtils.dismissDialog(getChildFragmentManager());

        mProgress = mRootView.findViewById(R.id.progress);

        mAppBarLayout = ((BaseActivity) getActivity()).getAppBarLayout();
        mToolBar = ((BaseActivity) getActivity()).getToolBar();

        if (mAppBarLayout != null && !isForeground()) {
            mAppBarLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAppBarLayout != null && isAdded() && getActivity() != null) {
                        ViewCompat.setElevation(mAppBarLayout, showViewPager() && !hideBanner() ?
                                0 : getResources().getDimension(R.dimen.app_bar_elevation));
                    }
                }
            }, 150);
        }

        mTopFab = (FloatingActionButton) mRootView.findViewById(R.id.top_fab);
        mBottomFab = (FloatingActionButton) mRootView.findViewById(R.id.bottom_fab);

        mRecyclerView.clearOnScrollListeners();
        if (showViewPager() && !hideBanner()) {
            mScroller = new Scroller();
            mRecyclerView.addOnScrollListener(mScroller);
        }
        mRecyclerView.setAdapter(mRecyclerViewAdapter == null ? mRecyclerViewAdapter
                = new RecyclerViewAdapter(mItems, new RecyclerViewAdapter.OnViewChangedListener() {
            @Override
            public void viewChanged() {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded() && getActivity() != null) {
                            adjustScrollPosition();
                        }
                    }
                }, 250);
            }
        }) : mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager = getLayoutManager());

        if (!Utils.DONATED
                && !showTopFab()
                && !isForeground()
                && getActivity() instanceof NavigationActivity
                && showAd()
                && mAdView == null) {
            mAdView = new AdView();
        } else {
            mAdView = null;
        }

        mTopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopFabClick();
            }
        });
        {
            Drawable drawable;
            if ((drawable = getTopFabDrawable()) != null) {
                mTopFab.setImageDrawable(drawable);
            }
        }

        mBottomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomFabClick();
            }
        });
        {
            Drawable drawable;
            if ((drawable = getBottomFabDrawable()) != null) {
                mBottomFab.setImageDrawable(drawable);
            }
        }

        BaseFragment foregroundFragment = getForegroundFragment();
        mForegroundVisible = false;
        if (foregroundFragment != null) {
            mForegroundParent = mRootView.findViewById(R.id.foreground_parent);
            mForegroundText = (TextView) mRootView.findViewById(R.id.foreground_text);
            mForegroundText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissForeground();
                }
            });
            getChildFragmentManager().beginTransaction().replace(R.id.foreground_content,
                    foregroundFragment).commit();
            mForegroundHeight = getResources().getDisplayMetrics().heightPixels;
        }

        if (itemsSize() == 0) {
            mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showProgress();
                    init();
                }

                @Override
                protected List<RecyclerViewItem> doInBackground(Void... params) {
                    if (mDelay && needDelay()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mDelay = false;
                    }
                    if (isAdded() && getActivity() != null) {
                        List<RecyclerViewItem> items = new ArrayList<>();
                        addItems(items);
                        return items;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                    super.onPostExecute(recyclerViewItems);
                    if (isCancelled() || recyclerViewItems == null) return;
                    for (RecyclerViewItem item : recyclerViewItems) {
                        addItem(item);
                    }
                    hideProgress();
                    postInit();
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            Activity activity;
                            if ((activity = getActivity()) != null) {
                                mRecyclerView.scrollToPosition(0);
                                mLayoutManager.scrollToPosition(0);

                                mRecyclerView.startAnimation(AnimationUtils.loadAnimation(
                                        activity, R.anim.slide_in_bottom));
                            }
                        }
                    });
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                int cx = mViewPager.getWidth();

                                SupportAnimator animator = ViewAnimationUtils.createCircularReveal(
                                        mViewPager, cx / 2, 0, 0, cx);
                                animator.addListener(new SupportAnimator.SimpleAnimatorListener() {
                                    @Override
                                    public void onAnimationStart() {
                                        super.onAnimationStart();
                                        mViewPager.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd() {
                                        super.onAnimationEnd();
                                        mViewPagerShadow.setVisibility(View.VISIBLE);
                                    }
                                });
                                animator.setDuration(400);
                                animator.start();
                            }
                        }
                    });
                    mLoader = null;
                }
            };
            mLoader.execute();
        } else {
            showProgress();
            init();
            hideProgress();
            postInit();
            adjustScrollPosition();

            mViewPager.setVisibility(View.VISIBLE);
            mViewPagerShadow.setVisibility(View.VISIBLE);
        }

        return mRootView;
    }

    @Override
    public void onViewFinished() {
        super.onViewFinished();
        if (showViewPager() && !hideBanner()) {
            mViewPager.setAdapter(mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),
                    mViewPagerFragments));
            mCirclePageIndicator.setViewPager(mViewPager);

            setAppBarLayoutAlpha(0);
            adjustScrollPosition();
        } else {
            mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(), isForeground() ? 0 : mToolBar.getHeight(),
                    mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
            mRecyclerView.setClipToPadding(true);
            ViewGroup.LayoutParams layoutParams = mViewPagerParent.getLayoutParams();
            layoutParams.height = 0;
            mViewPagerParent.requestLayout();
            setAppBarLayoutAlpha(255);

            if (hideBanner()) {
                if (showTopFab()) {
                    mTopFab.hide();
                    mTopFab = null;
                } else if (showBottomFab()) {
                    mBottomFab.hide();
                    mBottomFab = null;
                }
            }
        }
    }

    protected void init() {
    }

    protected void postInit() {
        if (getActivity() != null && isAdded()) {
            for (RecyclerViewItem item : mItems) {
                item.onRecyclerViewCreate(getActivity());
            }
        }
    }

    protected void adjustScrollPosition() {
        if (mScroller != null) {
            mScroller.onScrolled(mRecyclerView, 0, 0);
        }
    }

    protected abstract void addItems(List<RecyclerViewItem> items);

    private void setAppBarLayoutAlpha(int alpha) {
        if (isForeground()) return;
        Activity activity;
        if ((activity = getActivity()) != null && mAppBarLayout != null && mToolBar != null) {
            int colorPrimary = ViewUtils.getColorPrimaryColor(activity);
            mAppBarLayout.setBackgroundDrawable(new ColorDrawable(Color.argb(alpha, Color.red(colorPrimary),
                    Color.green(colorPrimary), Color.blue(colorPrimary))));
            mToolBar.setTitleTextColor(Color.argb(alpha, 255, 255, 255));
        }
    }

    protected void addItem(RecyclerViewItem recyclerViewItem) {
        if (mItems.size() == 0 && mAdView != null && !mItems.contains(mAdView)) {
            boolean exists = false;
            for (RecyclerViewItem item : mItems) {
                if (item instanceof AdView) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                mItems.add(mAdView);
            }
        }
        mItems.add(recyclerViewItem);
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyItemInserted(mItems.size() - 1);
        }
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) mLayoutManager).setSpanCount(getSpanCount());
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
    }

    public void resizeBanner() {
        if (showViewPager() && !hideBanner() && Utils.DONATED) {
            ViewGroup.LayoutParams layoutParams = mViewPagerParent.getLayoutParams();
            layoutParams.height = getBannerHeight();
            mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(), layoutParams.height,
                    mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
            mViewPagerParent.requestLayout();
        }
    }

    private int getBannerHeight() {
        int min = Math.round(getResources().getDimension(R.dimen.banner_min_height));
        int max = Math.round(getResources().getDimension(R.dimen.banner_max_height));

        int height = Prefs.getInt("banner_size", Math.round(getResources().getDimension(
                R.dimen.banner_default_height)), getActivity());
        if (height > max) {
            height = max;
            Prefs.saveInt("banner_size", max, getActivity());
        } else if (height < min) {
            height = min;
            Prefs.saveInt("banner_size", min, getActivity());
        }
        return height;
    }

    protected void removeItem(RecyclerViewItem recyclerViewItem) {
        int position = mItems.indexOf(recyclerViewItem);
        if (position >= 0) {
            mItems.remove(recyclerViewItem);
            if (mRecyclerViewAdapter != null) {
                mRecyclerViewAdapter.notifyItemRemoved(position);
            }
        }
    }

    protected void clearItems() {
        mItems.clear();
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyDataSetChanged();
            mRecyclerView.setLayoutManager(mLayoutManager = getLayoutManager());
            adjustScrollPosition();
        }
    }

    public int getSpanCount() {
        Activity activity;
        if ((activity = getActivity()) != null) {
            int span = Utils.isTablet(activity) ? Utils.getOrientation(activity) ==
                    Configuration.ORIENTATION_LANDSCAPE ? 3 : 2 : Utils.getOrientation(activity) ==
                    Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
            if (itemsSize() != 0 && span > itemsSize()) {
                span = itemsSize();
            }
            return span;
        }
        return 1;
    }

    public int itemsSize() {
        return mAdView != null && mItems.contains(mAdView) ? mItems.size() - 1 : mItems.size();
    }

    protected void addViewPagerFragment(BaseFragment fragment) {
        mViewPagerFragments.add(fragment);
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.notifyDataSetChanged();
        }
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;

        public ViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }
    }

    private class Scroller extends RecyclerView.OnScrollListener {

        private int mScrollDistance;
        private int mAppBarLayoutDistance;
        private boolean mFade = true;
        private ValueAnimator mAlphaAnimator;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            View firstItem = mRecyclerView.getChildAt(0);
            if (firstItem == null) {
                if (mRecyclerViewAdapter != null) {
                    firstItem = mRecyclerViewAdapter.getFirstItem();
                }
                if (firstItem == null) {
                    return;
                }
            }

            mScrollDistance = -firstItem.getTop() + mRecyclerView.getPaddingTop();

            int appBarHeight = 0;
            if (mAppBarLayout != null) {
                appBarHeight = mAppBarLayout.getHeight();
            }

            if (mScrollDistance > mViewPagerParent.getHeight() - appBarHeight) {
                mAppBarLayoutDistance += dy;
                fadeAppBarLayout(false);
                if (mTopFab != null && showTopFab()) {
                    mTopFab.hide();
                }
            } else {
                fadeAppBarLayout(true);
                if (mTopFab != null && showTopFab()) {
                    mTopFab.show();
                }
            }

            if (mAppBarLayout != null) {
                if (mAppBarLayoutDistance > mAppBarLayout.getHeight()) {
                    mAppBarLayoutDistance = mAppBarLayout.getHeight();
                } else if (mAppBarLayoutDistance < 0) {
                    mAppBarLayoutDistance = 0;
                }
                mAppBarLayout.setTranslationY(-mAppBarLayoutDistance);
            }

            mViewPagerParent.setTranslationY(-mScrollDistance);
            if (mTopFab != null) {
                mTopFab.setTranslationY(-mScrollDistance);
            }

            if (showBottomFab() && autoHideBottomFab()) {
                if (dy <= 0) {
                    if (mBottomFab.getVisibility() != View.VISIBLE) {
                        mBottomFab.show();
                    }
                } else if (mBottomFab.getVisibility() == View.VISIBLE) {
                    mBottomFab.hide();
                }
            }
        }

        private void fadeAppBarLayout(boolean fade) {
            if (mFade != fade) {
                mFade = fade;

                if (mAlphaAnimator != null) {
                    mAlphaAnimator.cancel();
                }

                mAlphaAnimator = ValueAnimator.ofFloat(fade ? 1f : 0f, fade ? 0f : 1f);
                mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setAppBarLayoutAlpha(Math.round(255 * (float) animation.getAnimatedValue()));
                    }
                });
                mAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mAlphaAnimator = null;
                    }
                });
                mAlphaAnimator.start();
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (mAppBarLayout == null || newState != 0 || mAppBarLayoutDistance == 0
                    || (mAppBarLayoutDistance == mAppBarLayout.getHeight() && mScrollDistance != 0)) {
                return;
            }

            boolean show = mAppBarLayoutDistance < mAppBarLayout.getHeight() * 0.5f
                    || mScrollDistance <= mViewPagerParent.getHeight();
            ValueAnimator animator = ValueAnimator.ofInt(mAppBarLayoutDistance, show ? 0 : mAppBarLayout.getHeight());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAppBarLayoutDistance = (int) animation.getAnimatedValue();
                    mAppBarLayout.setTranslationY(-mAppBarLayoutDistance);
                }
            });
            animator.start();
        }
    }

    protected void showProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    mProgress.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    if (mTopFab != null && showTopFab()) {
                        mTopFab.hide();
                    }
                    if (mBottomFab != null && showBottomFab()) {
                        mBottomFab.hide();
                    }
                }
            }
        });
    }

    protected void hideProgress() {
        mProgress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mViewPagerParent.setVisibility(View.VISIBLE);
        if (mTopFab != null && showTopFab()) {
            mTopFab.show();
        }
        if (mBottomFab != null && showBottomFab()) {
            mBottomFab.show();
        }
        adjustScrollPosition();
    }

    protected boolean isForeground() {
        return false;
    }

    protected BaseFragment getForegroundFragment() {
        return null;
    }

    public void setForegroundText(CharSequence text) {
        mForegroundStrText = text;
    }

    public void showForeground() {
        if (mForegroundStrText != null) {
            mForegroundText.setText(mForegroundStrText);
        }
        if (mForegroundAnimator != null) mForegroundAnimator.cancel();
        mForegroundAnimator = ValueAnimator.ofFloat(mForegroundHeight, 0f);
        mForegroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mForegroundParent.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        mForegroundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mForegroundParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mForegroundVisible = true;
                mForegroundAnimator = null;
            }
        });
        mForegroundAnimator.start();
    }

    public void dismissForeground() {
        float translation = mForegroundParent.getTranslationY();
        mForegroundAnimator = ValueAnimator.ofFloat(translation, mForegroundHeight);
        mForegroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mForegroundParent.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        mForegroundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mForegroundParent.setVisibility(View.GONE);
                mForegroundVisible = false;
                mForegroundAnimator = null;
            }
        });
        mForegroundAnimator.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!hideBanner()) return;

        if (showViewPager()) {
            menu.add(0, 0, Menu.NONE, R.string.options)
                    .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher_preview))
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        if (showTopFab()) {
            menu.add(0, 1, Menu.NONE, R.string.more)
                    .setIcon(getTopFabDrawable())
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else if (showBottomFab()) {
            menu.add(0, 1, Menu.NONE, R.string.more)
                    .setIcon(getBottomFabDrawable())
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                ViewUtils.showDialog(getChildFragmentManager(),
                        ViewPagerDialog.newInstance(getBannerHeight(), mViewPagerFragments));
                return true;
            case 1:
                if (showTopFab()) {
                    onTopFabClick();
                } else if (showBottomFab()) {
                    onBottomFabClick();
                }
                return true;
        }
        return false;
    }

    private boolean hideBanner() {
        return Prefs.getBoolean("hide_banner", false, getActivity())
                && getActivity() instanceof NavigationActivity
                && Utils.DONATED;
    }

    protected boolean showViewPager() {
        return true;
    }

    protected boolean showTopFab() {
        return false;
    }

    protected Drawable getTopFabDrawable() {
        return null;
    }

    protected void onTopFabClick() {
    }

    protected boolean showBottomFab() {
        return false;
    }

    protected Drawable getBottomFabDrawable() {
        return null;
    }

    protected void onBottomFabClick() {
    }

    protected boolean autoHideBottomFab() {
        return true;
    }

    protected FloatingActionButton getBottomFab() {
        return mBottomFab;
    }

    protected View getRootView() {
        return mRootView;
    }

    protected boolean needDelay() {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (mForegroundVisible) {
            dismissForeground();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRefresh);
        for (RecyclerViewItem item : mItems) {
            item.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRefresh);
        for (RecyclerViewItem item : mItems) {
            item.onPause();
        }
    }

    protected boolean showAd() {
        return true;
    }

    public void ghAdReady() {
        if (mAdView != null) {
            mAdView.ghReady();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mItems.clear();
        mRecyclerViewAdapter = null;
        setAppBarLayoutAlpha(255);
        if (mAppBarLayout != null && !isForeground()) {
            mAppBarLayout.setTranslationY(0);
            ViewCompat.setElevation(mAppBarLayout, 0);
        }
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mRefresh);
        }
        mAdView = null;
        for (RecyclerViewItem item : mItems) {
            item.onDestroy();
        }
    }

    protected Handler getHandler() {
        return mHandler;
    }

    protected void refresh() {
    }

    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            refresh();
            mHandler.postDelayed(this, 1000);
        }
    };

}
