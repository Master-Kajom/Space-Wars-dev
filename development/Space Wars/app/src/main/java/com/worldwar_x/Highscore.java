package com.worldwar_x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//class to check highscore and import it to a remote server mysql.


public class Highscore extends Activity implements OnClickListener{
	boolean connected;
	//declare the essential variables
	Button saveUser;
	Button upload;
	EditText userText;//input the value of the username
	TextView resultImport;
	TextView resultView;
	TextView dataResults;
	TextView userResults;
	TextView headline;
	FileOutputStream fos;
	String FILENAME = "HIGHSCORE";
	String userfile = "USERNAMESs";
	String username;
	String collected;
	
	//boolean connected;
	String outcome;
	String position;
	
	ProgressDialog barProgressDialog;
	Handler updateBarHandler;
	public String DEV_HASH = "3LMT08BOB9RA0R8UXS5D4UCFUGOU9";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		MobileCore.init(this,DEV_HASH, LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL);		
		setContentView(R.layout.score);
		/*AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
		try {
			fos = openFileOutput(userfile, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos = openFileOutput(FILENAME, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		headline = (TextView) findViewById(R.id.textView2);
		upload = (Button) findViewById(R.id.bUpload);
		upload.setVisibility(View.INVISIBLE);
		saveUser = (Button) findViewById(R.id.btnUser);
		userText = (EditText) findViewById(R.id.etusername);
		resultView = (TextView) findViewById(R.id.tvCrap);
		userResults = (TextView) findViewById(R.id.tvusername);
		saveUser.setOnClickListener(this);
		upload.setOnClickListener(this);
		setupVariables();
		//access the database
		
		this.position = "";
		this.outcome = "";
		
		updateBarHandler = new Handler();
		MobileCore.showStickee(Highscore.this);
	}
	
	//read from a fucking file
	public void openfile(){
		collected = null;
		//load the score from file and display it on the text view
		FileInputStream fis = null;
		try {
		 fis = openFileInput(FILENAME);
		//Read all the byte data
		byte[] dataArray = new byte[fis.available()];
		while(fis.read(dataArray) != -1){
			//read all the data
			this.collected = new String(dataArray);
			if(this.collected.length()==0 ){				
				this.collected = "0";
				//overwrite the value in the file
				try {
					fos = openFileOutput(this.FILENAME, Context.MODE_PRIVATE);
					
					fos.write(this.collected.getBytes());
					fos.close();
					
					return;
				}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.collected = new String(dataArray);
			}else{
			//read all the data
				this.collected = new String(dataArray);
			}
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try {
			fis.close();
			//return collected;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
	
	public void setupVariables(){
		dataResults = (TextView) findViewById(R.id.tvScore);
		collected = null;
		//load the score from file and display it on the text view
		FileInputStream fis = null;
		try {
		 fis = openFileInput(FILENAME);
		//Read all the byte data
		byte[] dataArray = new byte[fis.available()];
		while(fis.read(dataArray) != -1){
			//read all the data
			this.collected = new String(dataArray);
			if(this.collected.length()==0 ){				
				this.collected = "0";
				//overwrite the value in the file
				try {
					fos = openFileOutput(this.FILENAME, Context.MODE_PRIVATE);
					
					fos.write(this.collected.getBytes());
					fos.close();
					
					return;
				}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.collected = new String(dataArray);
			}else{
			//read all the data
				this.collected = new String(dataArray);
			}
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try {
			fis.close();
			//return collected;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		//load.setOnClickListener(this);
	/////////////read and write username on userfile on the textfile ////should be done only once.	
		////////read from userfile the value of the username.
		this.username = null;
		//load the score from file and display it on the text view
		try {
		 fis = openFileInput(userfile);
		//Read all the byte data
		byte[] dataArray = new byte[fis.available()];
		while(fis.read(dataArray) != -1){
			//read all the data
			this.username = new String(dataArray);
			if(this.username.length()==0 ){				
				this.username = "Unknown user, ";
				//overwrite the value in the file
				try {
					fos = openFileOutput(this.userfile, Context.MODE_PRIVATE);
					
					fos.write(this.username.getBytes());
					fos.close();
					return;
				}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				this.username = new String(dataArray);
				
			}
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try {
			fis.close();			
			dataResults.setText(this.username + " "+" your High score is:  "+this.collected);
			//return collected;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	if(this.username.equals("Unknown user, ")){
		//don't show the interface
		
	}else{
		//TODO : set the editbox to invisible.
		userText.setVisibility(View.GONE);
		//set the text above to be invisible too.
		headline.setVisibility(View.GONE);
		//set the save button to invisible too.
		saveUser.setVisibility(View.GONE);
		upload.setVisibility(View.VISIBLE);
	}
		
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		/*MobileCore.showInterstitial(this, null);
		MobileCore.showStickee(Highscore.this);*/
		super.onStart();
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		//if button is clicked save the username into the database
		case R.id.btnUser:
			Random r = new Random();
			int x = r.nextInt(100);
			if(userText.getText().toString().contains(" ") || userText.getText().toString().length() == 0){
				this.userResults.setText("Do not leave spaces limit your username to one word only");
			}else{
				String data = userText.getText().toString();
				data = data+x;
				try {
					fos = openFileOutput(this.userfile, Context.MODE_PRIVATE);
					//write the data which is converted into an array of bytes
					fos.write(data.getBytes());
					fos.close();
					userResults.setText("Your username is: " + data);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				//after saving the username into database read again 
				setupVariables();
				}
			break;
		case R.id.bUpload:
			loadSomeStuff load = new loadSomeStuff();
			load.execute(username,collected);
			break;
	}
		
	}
	
public class loadSomeStuff extends AsyncTask<String, Integer, String>{
	final ProgressDialog ringProgressDialog = ProgressDialog.show(Highscore.this, "Please wait..", "Uploading...", true);
	
	protected void onPreExecute(){
		//example of setting up something
		
		
		ringProgressDialog.setCancelable(true);
	}
		
		@Override
		protected String doInBackground(String... arg0) {
			
					try {
					// TODO Auto-generated method stub
					
					///write your time consuming task
					
					String result = "";
					InputStream isr = null;
					try{
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost("http://postnnewsit.com/ajax/worldwarx.php?username="+username+"&score="+collected);
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						isr = entity.getContent();				
					}
					catch(Exception e){
						Log.e("log_tag", "Error in http connection " + e.toString());
						position = "Could not connect to the database, internet should be on.";
					}
					
					//convert response to string
					try{
						BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"),8);
						StringBuilder sb = new StringBuilder();
						String line = null;
						while((line = reader.readLine()) != null){
							sb.append(line + "\n");
							
						}
						isr.close();
						result=sb.toString();
					}
					catch(Exception e){
						Log.e("Log.tag", "Error converting result " + e.toString());
					}
					
					//parse json data
					try{
						String s = username;
						JSONArray jArray = new JSONArray(result);
						JSONObject cred = new JSONObject();
						
						for(int i=0; i<jArray.length();i++){
							JSONObject json = jArray.getJSONObject(i);
							
							int z = i +1;
							
							/*
							 *+ "Username : "+json.getString("username")+
										"Score : "+json.getInt("score")+"\n" */
							//make it display only what you wanna see
							if(username.equals(json.getString("username"))){
								//print the crap
								s=s +",  Your High Score Position is: " +z+"\n\n";
							}
									
						}
						position = s;
						
					}
					catch(Exception e){
						//TODO handle exception
						Log.e("log.tag", "Error Parsing Data " + e.toString());
					}
					
					
					
					
					Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ringProgressDialog.dismiss();
				
			// TODO Auto-generated method stub
			return position;
		}
		
		

		@Override
		protected void onPostExecute(String result) {
				resultView.setText(result);
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
			
		
	}


		
}
