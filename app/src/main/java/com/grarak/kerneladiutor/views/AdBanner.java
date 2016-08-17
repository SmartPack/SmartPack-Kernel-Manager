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
package com.grarak.kerneladiutor.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 08.08.16.
 */
public class AdBanner extends LinearLayout {

    public static final String ADS_FETCH = "https://raw.githubusercontent.com/Grarak/KernelAdiutor/master/ads/ads.json";

    private boolean mLoaded;
    private boolean mLoading;
    private boolean mGHLoaded;
    private View mProgress;
    private View mAdText;
    private ImageView mGHAdImage;
    private LinearLayout mAdParent;

    public AdBanner(Context context) {
        this(context, null);
    }

    public AdBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Utils.DONATED) return;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdBanner, defStyleAttr, 0);
        String unitId = a.getString(R.styleable.AdBanner_unitId);
        a.recycle();
        if (unitId == null || unitId.isEmpty()) {
            throw new IllegalStateException("unitId must be defined");
        }

        LayoutInflater.from(context).inflate(R.layout.adbanner_view, this);

        mProgress = findViewById(R.id.progress);
        mAdText = findViewById(R.id.ad_text);
        mGHAdImage = (ImageView) findViewById(R.id.gh_ad);
        mAdParent = (LinearLayout) findViewById(R.id.ad_parent);

        AdView adView = new AdView(context);
        adView.setAdUnitId(unitId);
        adView.setAdSize(AdSize.BANNER);
        mAdParent.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mGHAdImage.setVisibility(GONE);
                mProgress.setVisibility(GONE);
                mAdText.setVisibility(VISIBLE);
                mAdParent.setVisibility(GONE);
                mLoading = false;
                mLoaded = false;
                loadGHAd();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mGHAdImage.setVisibility(GONE);
                mProgress.setVisibility(GONE);
                mAdText.setVisibility(GONE);
                mAdParent.setVisibility(VISIBLE);
                mLoading = false;
                mLoaded = true;
            }
        });

        if (!mLoaded) {
            try {
                mLoading = true;
                adView.loadAd(new AdRequest.Builder().build());
            } catch (Exception ignored) {
                loadGHAd();
            }
        }

        findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.dialogDonate(view.getContext()).show();
            }
        });
    }

    public void loadGHAd() {
        if (mLoaded || mLoading || mGHLoaded) return;

        String json = Utils.readFile(getContext().getFilesDir() + "/ghads.json", false);
        GHAds ghAds;
        List<GHAds.GHAd> ghAdList;
        if (json != null && !json.isEmpty()
                && ((ghAds = new GHAds(json)).readable()
                && (ghAdList = ghAds.getAllAds()) != null)) {
            GHAds.GHAd ad = null;
            int min = -1;
            for (GHAds.GHAd ghAd : ghAdList) {
                int shown = Prefs.getInt(ghAd.getName() + "_shown", 0, getContext());
                if (min < 0 || shown < min) {
                    min = shown;
                    ad = ghAd;
                }
            }

            if (ad == null) {
                ad = ghAdList.get(0);
            }

            final String name = ad.getName();
            final String link = ad.getLink();
            final int totalShown = min + 1;
            Picasso.with(getContext()).load(ad.getBanner()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mGHAdImage.setVisibility(VISIBLE);
                    mProgress.setVisibility(GONE);
                    mAdText.setVisibility(GONE);
                    mAdParent.setVisibility(GONE);
                    mGHAdImage.setImageBitmap(bitmap);
                    Prefs.saveInt(name + "_shown", totalShown, getContext());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    mGHAdImage.setVisibility(GONE);
                    mProgress.setVisibility(GONE);
                    mAdText.setVisibility(VISIBLE);
                    mAdParent.setVisibility(GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    mGHAdImage.setVisibility(GONE);
                    mAdParent.setVisibility(GONE);
                    mProgress.setVisibility(VISIBLE);
                    mAdText.setVisibility(VISIBLE);
                }
            });

            mGHAdImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext()).setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.gh_ad))
                            .setPositiveButton(getString(R.string.open_ad_anyway),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utils.launchUrl(link, getContext());
                                        }
                                    }).show();
                }
            });
            mGHAdImage.setVisibility(VISIBLE);
            mProgress.setVisibility(GONE);
            mAdText.setVisibility(GONE);
            mAdParent.setVisibility(GONE);
            mGHLoaded = true;
        } else {
            mGHAdImage.setVisibility(GONE);
            mProgress.setVisibility(GONE);
            mAdParent.setVisibility(GONE);
            mAdText.setVisibility(VISIBLE);
        }
    }


    private String getString(int res) {
        return getContext().getString(res);
    }

    public static class GHAds {

        private final String mJson;
        private JSONArray mAds;

        public GHAds(String json) {
            mJson = json;
            try {
                mAds = new JSONArray(json);
            } catch (JSONException ignored) {
            }
        }

        public void cache(Context context) {
            Utils.writeFile(context.getFilesDir() + "/ghads.json", mJson, false, false);
        }

        private List<GHAd> getAllAds() {
            List<GHAd> list = new ArrayList<>();
            for (int i = 0; i < mAds.length(); i++) {
                try {
                    list.add(new GHAd(mAds.getJSONObject(i)));
                } catch (JSONException ignored) {
                    return null;
                }
            }
            return list;
        }

        public boolean readable() {
            return mAds != null;
        }

        private static class GHAd {
            private final JSONObject mAd;

            private GHAd(JSONObject ad) {
                mAd = ad;
            }

            private String getLink() {
                return getString("link");
            }

            private String getBanner() {
                return getString("banner");
            }

            private String getName() {
                return getString("name");
            }

            private String getString(String key) {
                try {
                    return mAd.getString(key);
                } catch (JSONException ignored) {
                    return null;
                }
            }
        }

    }

}
