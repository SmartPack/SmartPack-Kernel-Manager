package com.grarak.kerneladiutor.fragments;

import android.os.Bundle;
import android.view.View;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.CardViewItem;
import com.grarak.kerneladiutor.elements.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 27.12.14.
 */
public class AboutUsFragment extends RecyclerViewFragment {

    private final String APP_SOURCE = "https://github.com/Grarak/KernelAdiutor";
    private final String DONATE_LINK = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=JSCNTZC4H73JG";

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        licenseInit();
        appSourceInit();
        donateInit();
    }

    private void licenseInit() {
        CardViewItem.DCardView mLicenseCard = new CardViewItem.DCardView();
        mLicenseCard.setTitle(getString(R.string.license));

        View view = inflater.inflate(R.layout.app_license_view, container, false);

        mLicenseCard.setView(view);
        addView(mLicenseCard);
    }

    private void appSourceInit() {
        CardViewItem.DCardView mAppSourceCard = new CardViewItem.DCardView();
        mAppSourceCard.setTitle(getString(R.string.open_source));
        mAppSourceCard.setDescription(getString(R.string.open_source_summary));
        mAppSourceCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
            @Override
            public void onClick(CardViewItem.DCardView dCardView) {
                Utils.launchUrl(getActivity(), APP_SOURCE);
            }
        });

        addView(mAppSourceCard);
    }

    private void donateInit() {
        CardViewItem.DCardView mDonateCard = new CardViewItem.DCardView();
        mDonateCard.setTitle(getString(R.string.donate));
        mDonateCard.setDescription(getString(R.string.donate_summary));
        mDonateCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
            @Override
            public void onClick(CardViewItem.DCardView dCardView) {
                Utils.launchUrl(getActivity(), DONATE_LINK);
            }
        });

        addView(mDonateCard);
    }

}
