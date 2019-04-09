package com.worldwar_x;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

public class HowToPlay extends Activity implements OnClickListener{

	
	public String DEV_HASH = "3LMT08BOB9RA0R8UXS5D4UCFUGOU9";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MobileCore.init(this,DEV_HASH, LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL);
		setContentView(R.layout.howtoplay);
		
		
		MobileCore.showStickee(this);
		/*AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		MobileCore.showStickee(this);
		/*AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
		
	}

	
	@Override
	public void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		MobileCore.showStickee(this);
		/*AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
		
	}
	
}
