package com.google.android.gms.ads;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bdcorps.fullthrottlefree.R;
import com.bdcorps.fullthrottlefree.R.id;
import com.bdcorps.fullthrottlefree.R.layout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;

public class AdmobPreference extends Preference {

	public AdmobPreference(Context context) {
		super(context, null);
	}

	public AdmobPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.admob_preference, null);

		AdView adView = (AdView) v.findViewById(R.id.adView);

		// Create ad banner request
		AdRequest adBannerRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("410095009ef12100").build();

		// Begin loading banner
		adView.loadAd(adBannerRequest);

		return v;
	}

}