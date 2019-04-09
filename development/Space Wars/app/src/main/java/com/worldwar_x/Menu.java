package com.worldwar_x;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ironsource.mobilcore.CallbackResponse;
import com.ironsource.mobilcore.CallbackResponse.TYPE;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.AD_UNIT_SHOW_TRIGGER;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Menu extends ListActivity {
	
	FileInputStream fis;
	FileOutputStream fos;
	boolean exist =  false;
	
	String user;
	
	static public Boolean crash;
	String classes[] = {"Play Game", "High Score","How to Play"};
	//String classes[] = {"Play Game","How to Play"};
	public String DEV_HASH = "3LMT08BOB9RA0R8UXS5D4UCFUGOU9";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.menu);
		MobileCore.init(this,DEV_HASH, LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL);
		
		//AdView mAdView = (AdView) findViewById(R.id.adView);
       // AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
		//showInterstitial();
		//MobileCore.showInterstitial(this, AD_UNIT_SHOW_TRIGGER , callbackResponse);
		//MobileCore.init(this,DEV_HASH, LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL);
		//MobileCore.setNativeAdsBannerSupport(true);
		
		//read contents from pause file if it exists then delete it.
		try {
			fos = openFileOutput(WorldWar_XActivity.Pause, Context.MODE_APPEND);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//make the list of Menus to click on
		setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, classes));
		MobileCore.showStickee(Menu.this);
	}
	
	//for ads
	CallbackResponse callbackResponse = new CallbackResponse() {

        @Override
        public void onConfirmation(TYPE confirmationType) {
        }
    };
	
	public void crashed(){
		this.user = null;
		//load the score from file and display it on the text view
		try {
			fis = openFileInput(WorldWar_XActivity.Pause);
			//Read all the byte data
			byte[] dataArray = new byte[fis.available()];
			while(fis.read(dataArray) != -1){
				//read all the data
				this.user = new String(dataArray);
				if(this.user.length()==0 ){
					exist = false;
					return;
					
					//do nothing
				}else{
			//////////////////////TODO////////////////////////
					//get the coordinates of enemies score and lives and spawn them.
					this.user = new String(dataArray);
					exist = true;
					//put the values of wher to spawn the enemies here the enemies here
					
				}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fis.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}
	
	/*private void showInterstitial() {
		//Button forceShowButton = (Button) findViewById(R.id.mc_button_show_interstitial);
		//forceShowButton.setOnClickListener(new OnClickListener() {
			//@Override
			//public void onClick(View v) {
		MobileCore.showInterstitial(this, null);
					//MobileCore.showInterstitial(Menu.this, AD_UNIT_SHOW_TRIGGER.BUTTON_CLICK, null);
			//}
		//});
	}*/
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	

	@Override
	protected void onStart() {
		//crashed();
		// TODO Auto-generated method stub
		super.onStart();
		MobileCore.showStickee(this);
		crashed();
		if(exist){
			Class ourClass = null;
			try {
				ourClass = Class.forName("com.worldwar_x.WorldWar_XActivity");
			
				Intent ourIntent =new Intent(Menu.this, ourClass);
				startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(WorldWar_XActivity.interstitial == false){
			
			MobileCore.showStickee(this);
		}else{
			
			MobileCore.showInterstitial(this, null);
		}
		/*crashed();
		if(exist){
			Class ourClass = null;
			try {
				ourClass = Class.forName("com.worldwar_x.WorldWar_XActivity");
			
				Intent ourIntent =new Intent(Menu.this, ourClass);
				startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}*/
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub		
		super.onListItemClick(l, v, position, id);
		String cheese = null;
		if(classes[position] == "Play Game"){
			cheese = "WorldWar_XActivity";
		}
		if(classes[position] == "High Score"){
			cheese = "Highscore";
		}
		if(classes[position] == "How to Play"){
			cheese = "HowToPlay";
		}
		Class ourClass = null;
		try {
			ourClass = Class.forName("com.space_wars_x." + cheese);
		
			Intent ourIntent =new Intent(Menu.this, ourClass);
			startActivity(ourIntent);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
	
}
