package com.bdcorps.fullthrottlefree;

import rajawali.wallpaper.Wallpaper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

@SuppressWarnings("deprecation")
public class Settings extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	 private InterstitialAd interstitial;
	 
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(
				Wallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.settings);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
						
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		
		
		// Admob Interstitial
		interstitial = new InterstitialAd(this);
		  interstitial.setAdUnitId("ca-app-pub-1049137905806014/8891643282");

			AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice("410095009ef12100").build();
			
		  interstitial.loadAd(adRequest);
		  interstitial.setAdListener(new AdListener() {
		      public void onAdLoaded() {
		          displayInterstitial();
		      }
		  });
		  }

		  public void displayInterstitial() {
		  if (interstitial.isLoaded()) {
		      interstitial.show();
		  }
		  }
	

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("img2_01")){
			Globals.id_1=Globals.temp;
		}else if (key.equals("img2_02")){
			Globals.id_2=Globals.temp;
		}
	}
}