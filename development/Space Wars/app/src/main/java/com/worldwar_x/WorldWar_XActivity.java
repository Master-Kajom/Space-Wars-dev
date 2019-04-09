package com.worldwar_x;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.ironsource.mobilcore.AdUnitEventListener;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.AD_UNIT_SHOW_TRIGGER;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;
import com.ironsource.mobilcore.UserProperties;
import com.ironsource.mobilcore.UserProperties.AgeRange;
import com.ironsource.mobilcore.UserProperties.Gender;
import com.worldwar_x.Highscore.loadSomeStuff;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WorldWar_XActivity extends Activity {
	//////handle alarm/////////////
	public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";
	public static final String ALARM_SNOOZE_ACTION = "com.android.deskclock.ALARM_SNOOZE";
	public static final String ALARM_DISMISS_ACTION = "com.android.deskclock.ALARM_DISMISS";
	public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";
	
	public static boolean interstitial = false;

	private Activity mActivity;
	
	static public boolean alarm = false;
	static public boolean alarm4 = false;
	//PhoneStateListener mListener;
	//TelephonyManager mTelMgr;
	boolean xwrite = true;
	boolean ywrite = true;
	boolean bwrite = true;
	String collected;
	static public String Pause = "File";
	FileOutputStream fos;
	static private final String TAG = WorldWar_XActivity.class.getSimpleName();
	private static final String ALERT_DISMISS_ACTION = null;
	Menu menu;
	PhoneStateListener phonestatelistener;
	TelephonyManager telephonymanager;
	static public MainGamePanel cpanel = null;
	Context cxt;
	static public Boolean telephonecall;
	private AudioManager audioManager;
	
	public static float density_factor = 1.f;
	float inches = 0;
	static public double screenInches;
	static public double widthInches;
	public String DEV_HASH = "3LMT08BOB9RA0R8UXS5D4UCFUGOU9";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.e("mobilecore", "Not loading mobilecore");
		//mobile core
		//MobileCore.init(this,*DEV_HASH*,new UserProperties().setGender(Gender.FEMALE).setAgeRange(AgeRange._18_24), LOG_TYPE.DEBUG,*AD_UNITS*); 
		
		//UserProperties user = new UserProperties().setGender(Gender.FEMALE).setAgeRange(AgeRange._18_24);
		//MobileCore.init(mActivity, DEV_HASH, user ,LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL,AD_UNITS.DIRECT_TO_MARKET,AD_UNITS.NATIVE_ADS);
		MobileCore.init(this,DEV_HASH, LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL);
		//MobileCore.setNativeAdsBannerSupport(true);
		
		
		

		// This function will set a button to request showing stickeez and a button for hiding stickeez. If stickeez is ready, it will display.
		// In addition more buttons are assigned to show Stickeez visual status.
		// Note: Stickeez differentiates between the states of showing the stickeez main button and showing the ads offered by stickeez.
		//catchStickeezButtons();

		/*
		 * This function sets the AdUnits Event listener.
		 * The AdUnit event listener will notify when the Ad Unit status have changed.
		 */

		//setAdUnitsEventListener();
		
		/*
		 * These buttons will show native ads Activity implementations 
		 * 
		 */
		
		//setupNativeAds();
		
		//request to turn the title off
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//making it fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 telephonecall = false;
		//set our MainGamePanel as view
		
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		density_factor = displaymetrics.density;
		//inches = displaymetrics.densityDpi;
		int width = displaymetrics.widthPixels;
		int height = displaymetrics.heightPixels;
		double wi = (double)width/(double)displaymetrics.xdpi;
		double hi = (double)height/(double)displaymetrics.ydpi;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		screenInches = Math.sqrt(x+y);
		widthInches = wi;
		
		
		cpanel = new MainGamePanel(this);
		setContentView(cpanel);
		cxt = getBaseContext();
		
		
		//AlarmManager am = (AlarmManager)getSystemService();
		
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
		
		IntentFilter filter = new IntentFilter(ALARM_DISMISS_ACTION);
			filter.addAction(ALARM_SNOOZE_ACTION);
			filter.addAction(ALARM_ALERT_ACTION);
			filter.addAction(ALARM_DONE_ACTION);
		
		registerReceiver(mReceiver, filter);
				
	}
	
		
		
		
		
	    
    
	

	/*public void showNativeAdsWithBanners(View button) {
		Intent i = new Intent(mActivity, BannerNativeAdsActity.class);
		startActivity(i);
	}*/

	
	
	/*
	 * This feature allows you as the developer to know when mobileCore's assets have finished downloading. 
	 * Calling showInterstitial after the resources have finished downloading would display the interstitial 
	 * in a much snappier and smoother way.
	 */
	private void setAdUnitsEventListener() {
		MobileCore.setAdUnitEventListener(new AdUnitEventListener() {

			@Override
			public void onAdUnitEvent(AD_UNITS adUnit, EVENT_TYPE eventType) {

				switch (adUnit) {
				case INTERSTITIAL:
					if (EVENT_TYPE.AD_UNIT_READY == eventType) {
						MobileCore.showInterstitial(mActivity, AD_UNIT_SHOW_TRIGGER.APP_START, null);
					}
					break;
				case STICKEEZ:
					/*
					 * Once Stickeez is ready, calling this method will show the Stickeez on top of the host application. 
					 * If the resources are not ready, nothing will happen
					 */
					if (EVENT_TYPE.AD_UNIT_READY == eventType) {
						MobileCore.showStickee(mActivity);
					}
					break;
				}

			}
		});

	}

	/*private void catchStickeezButtons() {
		Button showStickeez = (Button) findViewById(R.id.mc_button_show_stickeez);
		Button hideStickeez = (Button) findViewById(R.id.mc_button_hide_stickeez);
		Button isStickeezShowing = (Button) findViewById(R.id.mc_button_stickee_showing);
		Button isStickeezShowingOffers = (Button) findViewById(R.id.mc_button_stickee_showing_offers);

		showStickeez.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MobileCore.isStickeeReady()) {
					MobileCore.showStickee(mActivity);
				} else if (MobileCore.isStickeeShowing() || MobileCore.isStickeeShowingOffers()) {
					Toast.makeText(mActivity, "Stickeez is currently showing.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mActivity, "Stickeez isn't ready yet", Toast.LENGTH_SHORT).show();
				}
			}
		});

		hideStickeez.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//This method will return a true value when the Stickee element is currently showing on screen
				if (MobileCore.isStickeeShowing() || MobileCore.isStickeeShowingOffers()) {
					//This method will hide the Stickeez element.
					MobileCore.hideStickee();
				} else {
					Toast.makeText(mActivity, "Stickeez isn't showing.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		isStickeezShowing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mActivity, "Stickeez currently showing = " + MobileCore.isStickeeShowing(), Toast.LENGTH_SHORT).show();
			}
		});

		isStickeezShowingOffers.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mActivity, "Stickeez offers currently showing = " + MobileCore.isStickeeShowingOffers(), Toast.LENGTH_SHORT).show();
			}
		});
	}*/

	
	
	
	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
	//function to write the values in the temporary crash file
	private String pscore;
	private String setScore(int score){
		this.pscore = this.df.format(score);
		return this.pscore;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
	
		@Override
		public void onReceive(Context context, Intent intent) {		
			// TODO Auto-generated method stub
			String action = intent.getAction();
		
			if(action.equals(ALARM_ALERT_ACTION)){
				
				//pause the game
				alarm = true;
				Toast.makeText(cxt, "Alarm is on, press Home Key to Save and quit.", Toast.LENGTH_LONG).show();
				
			}
			else if(action.equals(ALARM_DONE_ACTION)){
				alarm = false;
			}
			else if(action.equals(ALARM_DISMISS_ACTION)){
				Toast.makeText(cxt, "Game Saved", 6000).show();
				//Start new activity
				alarm = false;
			}
			
		}
				
	};
	
	
	//public static void MobileCore.init(Activity activity, String devHash, LOG_TYPE logLevel, MobileCore.AD_UNITS...);
		
		
		
		
		
	

	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		telephonymanager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		phonestatelistener = new PhoneStateListener(){		
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				//i
				
				//if incoming call pause the game else do nothing
				if(state == TelephonyManager.CALL_STATE_RINGING){
					Log.d(TAG,"ringing...");
					//pause the game					
					telephonecall = true;															
				}
				if(state == TelephonyManager.CALL_STATE_IDLE){
					Log.d(TAG,"idle...");
					telephonecall = false;	
				}
				if(state == TelephonyManager.CALL_STATE_OFFHOOK){
					MainGamePanel.paused = true;
					//MainGamePanel.ourSong.pause()						
				}
				// TODO Auto-generated method stub
				super.onCallStateChanged(state, incomingNumber);
			}	
			
			
		
	};
		
	telephonymanager.listen(phonestatelistener,PhoneStateListener.LISTEN_CALL_STATE);
	
	
		
		// TODO Auto-generated method stub
		super.onStart();
	}
		
	//When the user presses the hardware buttons.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			//this.paused = true;
			if(MainGamePanel.runGame == true){
				if(MainGamePanel.paused == false){
					MainGamePanel.paused = true;
					MainGamePanel.ourSong.pause();
					return true;
				}
				else{
					MainGamePanel.paused = false;
					MainGamePanel.ourSong.start();
					return true;
				}
			}
			else{
				//Go to Main menu.
				MainGamePanel.runGame = false;
				MainGamePanel.killGame = true;
			}
		case KeyEvent.KEYCODE_MENU:
			//this.paused = true;
			if(MainGamePanel.paused == false){
				MainGamePanel.paused = true;
				MainGamePanel.ourSong.pause();
				return true;
			}
			else{
				MainGamePanel.paused = false;
				MainGamePanel.ourSong.start();
				return true;
			}
		}

		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy(){
		Log.d(TAG,"Destroying...");
		//this.finish();
		super.onDestroy();
		if(phonestatelistener != null){
			telephonymanager.listen(phonestatelistener, PhoneStateListener.LISTEN_NONE);
		}
		unregisterReceiver(mReceiver);
		
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		
	}




	
	
	
	//detect when the homme key is pressed
	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Home button pressed", Toast.LENGTH_LONG).show();
		//end the game immediately
		MainGamePanel.runGame = false;
		MainGamePanel.killGame = true;
		MainGamePanel.ourSong.stop();
		//assign the values to x and y
		int player[] = cpanel.playerCoord();
		int blast[] = cpanel.explodeCoord();
		int shield[] = cpanel.shieldCoord();
		int boss[] = cpanel.bossCoord();
		int x[] = cpanel.getXCoordinates();	
		int y[] = cpanel.getYCoordinates();
		int[] bitmap = cpanel.getEbitmap();
		//create temporary file to dump the coordinates inside.
		try {
			fos = cxt.openFileOutput(WorldWar_XActivity.Pause, Context.MODE_APPEND);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "Stopping...");
		//this.finish();
		//overwrite the file and place the a value
		
		
		String newline = "\n";
		//overwrite the value in the file
					try {
						fos = openFileOutput(Pause, Context.MODE_APPEND);
					//////////////////TODO////////////////////////////////////	
						//get the positions of each enemy and also their bitmaps. so as to spawn them later on.
				for(int i=0; i<x.length;i++){
					String score = this.setScore(x[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				
				for(int i=0; i<y.length;i++){
					String score = this.setScore(y[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				
				for(int i=0; i<y.length;i++){
					String score = this.setScore(bitmap[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				if(MainGamePanel.drawBoss == true){
					//draw the enemyboss and also get the damage required.
					String score = this.setScore(5);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
					String damage = this.setScore(MainGamePanel.bossDamage);
					fos.write(damage.getBytes());
					fos.write(newline.getBytes());
				}else{
					String score = this.setScore(0);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
					String damage = this.setScore(0);
					fos.write(damage.getBytes());
					fos.write(newline.getBytes());
				}
				for(int i=0; i<boss.length;i++){
					String score = this.setScore(boss[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				if(MainGamePanel.drawUpgrade == true){
					//store the values of the x and y positions and one to represent true
					String score = this.setScore(1);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());					
				}else{
					String score = this.setScore(0);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				///write the value of the shield coordinates
				for(int i=0; i<2;i++){
					String score = this.setScore(shield[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				if(MainGamePanel.drawExplosion == true){
					String score = this.setScore(1);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}else{
					String score = this.setScore(0);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				for(int i=0; i<2;i++){
					String score = this.setScore(blast[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				//write the score 
				if(MainGamePanel.score >= 0){
					String score = this.setScore(MainGamePanel.score);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				//write the lives 
				if(MainGamePanel.lives > 0){
					String score = this.setScore(MainGamePanel.lives);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}else{
					String score = this.setScore(0);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				//if the shield is picked up activate it.
				if(MainGamePanel.forceField == true){
					String score = this.setScore(1);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}else{
					String score = this.setScore(0);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				//get the x and y coords of the battleship
				for(int i=0; i<2;i++){
					String score = this.setScore(player[i]);
					fos.write(score.getBytes());
					fos.write(newline.getBytes());
				}
				//put the game on pause mode
				String pause = this.setScore(1);
				fos.write(pause.getBytes());
				fos.write(newline.getBytes());
				fos.close();
			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					fos.close();
					//return collected;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}
		super.onStop();			
		//super.onUserLeaveHint();
	}
	
	@Override
	protected void onPause() {		
		// TODO Auto-generated method stub
		
			if(MainGamePanel.runGame == true){
				MainGamePanel.paused = true;
				MainGamePanel.ourSong.pause();
			}
			if(alarm){
				//onStop();		
				//moveTaskToBack(true);
				alarm4 = true;
				alarm = false;
				onStop();
			}else{
				super.onPause();
			}
			
	}
	
	@Override
	protected void onStop(){
		if(telephonecall){
		
			
			//assign the values to x and y
			int player[] = cpanel.playerCoord();
			int blast[] = cpanel.explodeCoord();
			int shield[] = cpanel.shieldCoord();
			int boss[] = cpanel.bossCoord();
			int x[] = cpanel.getXCoordinates();	
			int y[] = cpanel.getYCoordinates();
			int[] bitmap = cpanel.getEbitmap();
			//create temporary file to dump the coordinates inside.
			try {
				fos = cxt.openFileOutput(WorldWar_XActivity.Pause, Context.MODE_APPEND);
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "Stopping...");
			//this.finish();
			//overwrite the file and place the a value
			
			
			String newline = "\n";
			//overwrite the value in the file
			try {
					fos = openFileOutput(Pause, Context.MODE_APPEND);
					//////////////////TODO////////////////////////////////////	
					//get the positions of each enemy and also their bitmaps. so as to spawn them later on.
					for(int i=0; i<x.length;i++){
						String score = this.setScore(x[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					
					for(int i=0; i<y.length;i++){
						String score = this.setScore(y[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					
					for(int i=0; i<y.length;i++){
						String score = this.setScore(bitmap[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					if(MainGamePanel.drawBoss == true){
						//draw the enemyboss and also get the damage required.
						String score = this.setScore(5);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
						String damage = this.setScore(MainGamePanel.bossDamage);
						fos.write(damage.getBytes());
						fos.write(newline.getBytes());
					}else{
						String score = this.setScore(0);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
						String damage = this.setScore(0);
						fos.write(damage.getBytes());
						fos.write(newline.getBytes());
					}
					for(int i=0; i<boss.length;i++){
						String score = this.setScore(boss[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					if(MainGamePanel.drawUpgrade == true){
						//store the values of the x and y positions and one to represent true
						String score = this.setScore(1);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());					
					}else{
						String score = this.setScore(0);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					///write the value of the shield coordinates
					for(int i=0; i<2;i++){
						String score = this.setScore(shield[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					if(MainGamePanel.drawExplosion == true){
						String score = this.setScore(1);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}else{
						String score = this.setScore(0);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					for(int i=0; i<2;i++){
						String score = this.setScore(blast[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					//write the score 
					if(MainGamePanel.score >= 0){
						String score = this.setScore(MainGamePanel.score);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					//write the lives 
					if(MainGamePanel.lives > 0){
						String score = this.setScore(MainGamePanel.lives);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}else{
						String score = this.setScore(0);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					//if the shield is picked up activate it.
					if(MainGamePanel.forceField == true){
						String score = this.setScore(1);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}else{
						String score = this.setScore(0);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					//get the x and y coords of the battleship
					for(int i=0; i<2;i++){
						String score = this.setScore(player[i]);
						fos.write(score.getBytes());
						fos.write(newline.getBytes());
					}
					//put the game on pause mode
					String pause = this.setScore(1);
					fos.write(pause.getBytes());
					fos.write(newline.getBytes());
					fos.close();
				}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					try {
						fos.close();
						//if(alarm){
							//alarm4 = false;
							//cpanel.callend();
							
						//}						
						//return collected;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
			}
		}
	if(alarm4) super.onStop();
		else super.onStop();
	}
		
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("paused", MainGamePanel.paused);
		outState.putSerializable("rungame", MainGamePanel.runGame);
		//outState.putSerializable("telephone", telephonecall);
		//outState.putSerializable("song", MainGamePanel.ourSong);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		MainGamePanel.runGame = (Boolean)savedInstanceState.getSerializable("rungame");
		MainGamePanel.paused = (Boolean)savedInstanceState.getSerializable("paused");
		//telephonecall = (Boolean)savedInstanceState.getSerializable("telephone");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	//Function to cause the app to vibrate
	public void vibrate(){
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//Vibrate for 300 milliseconds
		v.vibrate(300);
	}

}
