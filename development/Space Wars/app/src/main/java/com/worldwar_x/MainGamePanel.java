package com.worldwar_x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Random;






import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;
import com.worldwar_x.components.vec2;
import com.worldwar_x.model.Battleship;
import com.worldwar_x.model.Boss;
import com.worldwar_x.model.Bullet;
import com.worldwar_x.model.Dpointers;
import com.worldwar_x.model.Enemy;
import com.worldwar_x.model.Explode;
import com.worldwar_x.model.Explosion;
import com.worldwar_x.model.ForceField;
import com.worldwar_x.model.Stars;
import com.worldwar_x.model.Upgrade;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
//import android.renderscript.Font;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.widget.EditText;
import android.widget.TextView;


public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {
	
	public String DEV_HASH = "3LMT08BOB9RA0R8UXS5D4UCFUGOU9";
	
	////////essential variables
	static MainThread thread;
	//create the force field
	ForceField forcefield = null;
	//for the battleship
	Battleship battleship;
	Explosion[] explosion;
	
	Bitmap pause;
	Bitmap directions;
	Bitmap e,w,n,s,ne,nw,se,sw;
	Bitmap bitmap; // to load pictures e.g battleship
	Bitmap menuButton;
	Bitmap ResumeB;
	Bitmap MainB;
	Bitmap otherCraft;
	
	Bitmap Grenade;
	Bitmap Upgrade;
	Bitmap battleshipPlayer;
	Bitmap enemiesShot;
	Bitmap BossShot;
	Bitmap battleshipshot;
	
	Bitmap enemyCrafts;
	Bitmap enemyBoss;
	
	long diagP;
	
	long diagN;
	long enemyTime;
	long explosionTime;
	//add music to our game.
	static public MediaPlayer ourSong;
	
	//set up the explosion of enemy sound.
	SoundPool sp;
	int explosionS, smallexplosion = 0;
	
	Boss[] boss;
	Upgrade[] blast;
	Upgrade[] upgrade;
	Stars[] stars;
	Enemy[] enemies;
	Bullet[] ebullets;//bullets that the enemies will shoot.
	private long xv,yv;//the bullets velocity both x and y directions
	Bullet[] bullets;// bullets that the battleship will shoot.
	Bullet[] bossbullets;
	Bullet[] diagBossbullets;
	Bullet[] diagPBossbullets;
	
	Dpointers direction;//if pressed move the battleship in that direction(left or right)
	Explode[] explode;//create the explosion.
	private int EXPLOSION_SIZE;
	
	long sufferTime;
	long bossBullet;
	long forceTime;
	long gameTime;
	long enemiesTime;
	long mSystemTime;
	long eSystemTime;
	long bulletTime;
	long bossTime;
	static public boolean runGame;
	static public boolean killGame;
	static public boolean beginGame;
	Context cxt;
	Intent intent;
	
	///variables to the high score in a text file
	EditText sharedData;
	TextView dataResults;
	FileOutputStream fos;
	FileInputStream fis;
	String FILENAME;
	String userfile;
	String collected;
	String highscoreCheck;
	String usernameCheck;
	String user;
	
	boolean xread = true;
	boolean yread = false;
	boolean bread = false;
	static public boolean paused;
	static public boolean drawBoss;
	private boolean letExplosion;
	static public boolean drawExplosion;
	static public boolean drawUpgrade;
	private int upgraded;
	private int highscore;
	private int explodescore;
	private int bossScore;
	private int touchHeight;
	private int touchWidth;
	public boolean explodedOccur;
	static public boolean forceField;
	Paint shield;
	
	private static final int NONE =-1;
	
	//keep count of score + 10 for every kill
	static public int score;
	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
	
	//keep count of lives
	static public int lives;
	private DecimalFormat lv = new DecimalFormat("0.##"); //2 dp
	
	//the score to be displayed;
	private String pscore;
	private String setScore(int score){
		this.pscore = this.df.format(score);
		return this.pscore;
	}
	
	//the lives to be displayed.
	private String plives;
	private String setLives(int lives){
		this.plives = this.lv.format(this.lives);
		return this.plives;
	}
	
	// the fps to be displayed
	private String avgFps;
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}
	
	//get height and with of screen
	DisplayMetrics metrics = this.getResources().getDisplayMetrics();
	private int screenwidth = metrics.widthPixels;
	private int screenheight = metrics.heightPixels;
	
	static public int bossDamage;//damage of the boss at the begining.
	//constructor
	public MainGamePanel(Context context){
		super(context);
		MobileCore.init(context,DEV_HASH, LOG_TYPE.DEBUG, AD_UNITS.STICKEEZ,AD_UNITS.INTERSTITIAL);
		/////get the time when the program starts (used in generating bullets in a sequence)
		//this.beginTime = System.currentTimeMillis();
		
		//adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		this.upgraded = 0;
		
		MainGamePanel.drawUpgrade = false;
		this.letExplosion = false;
		this.cxt = context;
		stars = new Stars[30];
		generate_stars();
		FILENAME = "HIGHSCORE";
		userfile = "USERNAMESs";
		
		//if the file exists //stores the highscore.
		try {
			fos = cxt.openFileOutput(FILENAME, Context.MODE_APPEND);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		collected = "";
		//create the thread for the game loop
		thread = new MainThread(getHolder(),this);
		drawBoss = false;
		forceField = false;
		drawExplosion = false;
		w = BitmapFactory.decodeResource(getResources(), R.drawable.bleft);
		e = BitmapFactory.decodeResource(getResources(), R.drawable.bright);
		n = BitmapFactory.decodeResource(getResources(), R.drawable.bup);
		s = BitmapFactory.decodeResource(getResources(), R.drawable.bdown);
		sw= BitmapFactory.decodeResource(getResources(), R.drawable.lddiagonal);
		nw= BitmapFactory.decodeResource(getResources(), R.drawable.ludiagonal);
		se= BitmapFactory.decodeResource(getResources(), R.drawable.rddiagonal);
		ne= BitmapFactory.decodeResource(getResources(), R.drawable.rudiagonal);
		
		//load the battleship bitmap
		
		menuButton = BitmapFactory.decodeResource(getResources(), R.drawable.main);
				
		pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
		Grenade = BitmapFactory.decodeResource(getResources(), R.drawable.solarflare);
		Upgrade = BitmapFactory.decodeResource(getResources(), R.drawable.forceblast);
		battleshipPlayer = BitmapFactory.decodeResource(getResources(), R.drawable.droid);
		enemyCrafts = BitmapFactory.decodeResource(getResources(), R.drawable.ladybird);
		otherCraft = BitmapFactory.decodeResource(getResources(), R.drawable.bug);
		enemyBoss = BitmapFactory.decodeResource(getResources(), R.drawable.bossbrain);
				
		directions = BitmapFactory.decodeResource(getResources(), R.drawable.bleft);
		battleshipshot = BitmapFactory.decodeResource(getResources(), R.drawable.battleshipbullet);
		enemiesShot = BitmapFactory.decodeResource(getResources(), R.drawable.homingmissile);
		BossShot = BitmapFactory.decodeResource(getResources(), R.drawable.clusterbomb);
		ResumeB = BitmapFactory.decodeResource(getResources(), R.drawable.button);
		MainB = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
		
		///resize the bitmaps according to screen size
		resize();
		
		//handle touch events for direction of bullets.
		touchHeight = directions.getHeight()+2;
		touchWidth = directions.getWidth()+2;
		
		//load the direction pointers
		direction = new Dpointers(this.screenwidth,this.screenheight,e,w,n,s,sw,nw,se,ne);
				
		//initialize the music
		//blast = new Upgrade(40,50,BitmapFactory.decodeResource(getResources(), R.drawable.solarflare));
		ourSong = MediaPlayer.create(context, R.raw.attractmode);
		ourSong.start();
		
		
		EXPLOSION_SIZE = 200;
		explosionTime = 0;
		//load the enemies
		
		boss = new Boss[2];
		blast = new Upgrade[3];
		upgrade = new Upgrade[4];
		explosion = new Explosion[100];
		//number of explosions that can be created
		explode = new Explode[3];
		//an array to hold the enemies
		enemies = new Enemy[10];
		//initialize the SoundPool variable sp.
		sp = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
		//access our sound from the resource
		explosionS = sp.load(context, R.raw.largeexplosion, 1); 
		smallexplosion = sp.load(context, R.raw.smallexplosion, 1);
		//Bullets that the enemies will generate randomly
		
		this.xv = 0;
		this.yv = 1;
		
		shield = new Paint();
		shield.setColor(Color.WHITE);
		//bullets= new Bullets(5,40,this.screenheight-60);
		bullets = new Bullet[13];//five bullets to be generated
		sufferTime = 43000;
		this.diagN = System.currentTimeMillis();
		gameTime = System.currentTimeMillis();//get the game time to handle spawn enemies
		mSystemTime = System.currentTimeMillis();
		eSystemTime = System.currentTimeMillis();
		bulletTime = System.currentTimeMillis();
		bossBullet = System.currentTimeMillis();
		enemiesTime = System.currentTimeMillis();
		enemyTime = System.currentTimeMillis();//handle speed of the enemy.
		bossTime = System.currentTimeMillis();//time for boss to spawn.
		runGame = true;
		killGame = false;
		beginGame = true;
		score = 0;
		explodescore = 0;
		lives = 3;
		bossDamage = 2;//damge of the boss in the begining.
		paused = false;//if game is paused.
		//make gameholder focusable so as to handle events
		setFocusable(true);
		battleship = new Battleship(this.battleshipPlayer,this.screenwidth/2, this.screenheight/2);
		
		crashed();
		cxt.deleteFile(WorldWar_XActivity.Pause);
		
	}
	//spawn according to screen size
	public void resize(){
		float scale = 1.0f;
		double inches = WorldWar_XActivity.widthInches;
		if(this.screenwidth <= 480 && inches < 3){
			scale *= 0.6f;
			ebullets = new Bullet[3];
			bossbullets = new Bullet[3];
			this.diagBossbullets = new Bullet[6];
			this.diagPBossbullets = new Bullet[6];
		}
		else if(this.screenwidth >=480 && this.screenwidth < 600 && inches < 3){
			scale *= 0.8f;
			ebullets = new Bullet[5];
			bossbullets = new Bullet[5];
			this.diagBossbullets = new Bullet[9];
			this.diagPBossbullets = new Bullet[9];
		}
		else if(this.screenwidth >= 480 && inches < 3) {
			scale *= 0.7f;
			ebullets = new Bullet[5];
			bossbullets = new Bullet[5];
			this.diagBossbullets = new Bullet[9];
			this.diagPBossbullets = new Bullet[9];
		}
		else if(this.screenwidth >= 480 && this.screenwidth < 600 && inches >= 3){
			scale *= 0.8f;
			ebullets = new Bullet[6];
			bossbullets = new Bullet[6];
			this.diagBossbullets = new Bullet[9];
			this.diagPBossbullets = new Bullet[9];
		}
		else if(this.screenwidth >= 600 && this.screenwidth < 800 && inches >= 3.5){
			scale *= 1.0f;
			ebullets = new Bullet[7];
			bossbullets = new Bullet[8];
			this.diagBossbullets = new Bullet[11];
			this.diagPBossbullets = new Bullet[11];
		}
		else if(this.screenwidth >= 600 && this.screenwidth < 800 && inches >= 4){
			scale *= 1.2f;
			ebullets = new Bullet[7];
			bossbullets = new Bullet[10];
			this.diagBossbullets = new Bullet[12];
			this.diagPBossbullets = new Bullet[12];
		}
		else if(this.screenwidth >= 800 && inches >= 3.5 && inches < 4.0){
			scale *= 1.2f;
			ebullets = new Bullet[7];
			bossbullets = new Bullet[10];
			this.diagBossbullets = new Bullet[12];
			this.diagPBossbullets = new Bullet[12];
		}
		else if (this.screenwidth >= 800 && inches >= 4 && inches < 5){
			scale *= 1.4f;
			ebullets = new Bullet[7];
			bossbullets = new Bullet[10];
			this.diagBossbullets = new Bullet[12];
			this.diagPBossbullets = new Bullet[12];
		}
		else if(this.screenwidth >= 800 && inches >= 5){
			scale *= 1.6f;
			ebullets = new Bullet[8];
			bossbullets = new Bullet[11];
			this.diagBossbullets = new Bullet[13];
			this.diagPBossbullets = new Bullet[13];
		}
		else{
			scale *= 0.8f;
			ebullets = new Bullet[7];
			bossbullets = new Bullet[5];
			this.diagBossbullets = new Bullet[9];
			this.diagPBossbullets = new Bullet[9];
		}
		
			//double the size of the enemy crafts.
		
			w = Bitmap.createScaledBitmap(this.w,(int) (this.w.getWidth()*scale), (int) (this.w.getHeight()*scale),  true);
			e = Bitmap.createScaledBitmap(this.e,(int)(this.e.getWidth()*scale),(int) (this.e.getHeight()*scale),  true);
			n = Bitmap.createScaledBitmap(this.n, (int)(this.n.getWidth()*scale),(int)(this.n.getHeight()*scale),  true);
			s = Bitmap.createScaledBitmap(this.s,(int) (this.s.getWidth()*scale),(int)(this.s.getHeight()*scale),  true);
			sw= Bitmap.createScaledBitmap(this.sw,(int) (this.sw.getWidth()*scale),(int)(this.sw.getHeight()*scale),  true);
			nw= Bitmap.createScaledBitmap(this.nw,(int) (this.nw.getWidth()*scale),(int)(this.nw.getHeight()*scale),  true);
			se= Bitmap.createScaledBitmap(this.se,(int) (this.se.getWidth()*scale),(int)(this.se.getHeight()*scale), true);
			ne= Bitmap.createScaledBitmap(this.ne,(int)(this.ne.getWidth()*scale),(int) (this.ne.getHeight()*scale),  true);
			
			
			
			pause = Bitmap.createScaledBitmap(this.pause,(int)(this.pause.getWidth()*scale),(int) (this.pause.getHeight()*scale),  true);
			Grenade = Bitmap.createScaledBitmap(this.Grenade,(int)(this.Grenade.getWidth()*scale),(int)( this.Grenade.getHeight()*scale),  true);
			Upgrade = Bitmap.createScaledBitmap(this.Upgrade,(int) (this.Upgrade.getWidth()*scale),(int)(this.Upgrade.getHeight()*scale),  true);
			battleshipPlayer = Bitmap.createScaledBitmap(this.battleshipPlayer,(int) (this.battleshipPlayer.getWidth()*scale),(int)(this.battleshipPlayer.getHeight()*scale),  true);
			enemyCrafts = Bitmap.createScaledBitmap(this.enemyCrafts,(int)(this.enemyCrafts.getWidth()*scale),(int) (this.enemyCrafts.getHeight()*scale),  true);
			otherCraft = Bitmap.createScaledBitmap(this.otherCraft,(int) (this.otherCraft.getWidth()*scale),(int)(this.otherCraft.getHeight()*scale),  true);
			enemyBoss = Bitmap.createScaledBitmap(this.enemyBoss,(int) (this.enemyBoss.getWidth()*scale),(int)(this.enemyBoss.getHeight()*scale),  true);
					
			directions = Bitmap.createScaledBitmap(this.directions,(int) (this.directions.getWidth()*scale),(int)(this.directions.getHeight()*scale),  true);
			battleshipshot = Bitmap.createScaledBitmap(this.battleshipshot,(int)(this.battleshipshot.getWidth()*scale),(int) (this.battleshipshot.getHeight()*scale),  true);
			enemiesShot = Bitmap.createScaledBitmap(this.enemiesShot,(int)(this.enemiesShot.getWidth()*scale),(int) (this.enemiesShot.getHeight()*scale),  true);
			BossShot = Bitmap.createScaledBitmap(this.BossShot,(int)(this.BossShot.getWidth()*scale),(int)( this.BossShot.getHeight()*scale),  true);
			
		
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder){
		thread.setRunning(true);
		thread.start();
		
	}

	

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//wait till thread to finish i.e have a clean shutdown
		boolean retry = true;
		while(retry){
			try{
				thread.join();
				retry=false;
			}catch(InterruptedException e){
				//try again shuttingdown the thread.
			}
		}
		
	}
	

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//if screen is touched
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//check if battleship is touched
			battleship.handleActionDown((int)event.getX(), (int)event.getY());
			//if clicked pause game.
	
			if(event.getY() > getHeight() - touchHeight && event.getX() < this.screenwidth / 2 && MainGamePanel.paused == true){
				MainGamePanel.paused = false;
				MainGamePanel.ourSong.start();
			}
			
			//if pause is touched pause the game.
			if(MainGamePanel.paused == false){
				if(event.getX() > this.screenwidth / 4  && event.getX() < this.screenwidth / 4 + (pause.getWidth()) 
						&& event.getY() < touchHeight){
					MainGamePanel.paused = true;
					MainGamePanel.ourSong.pause();
				}
			}
			
			if(event.getY() > getHeight() - touchHeight && event.getX() > this.screenwidth / 2 && MainGamePanel.paused == true){
				MainGamePanel.ourSong.stop();
				thread.setRunning(false);
				((Activity)getContext()).finish();
			}
		
			//if battleship is touched and dragged, move to new position
			if(runGame == false){
			//TODO Once the game is over
			//if touch is on the lower part of screen exit()
			if(event.getY() > getHeight() - this.screenwidth / 2 ){
				thread.setRunning(false);
				MainGamePanel.ourSong.stop();
				((Activity)getContext()).finish();
			}
			}
			
			
			//if bleft is touched move the battleship to left
			if(event.getX() < touchWidth && event.getY() >= getHeight() / 2 - touchHeight && event.getY() <= getHeight() /2 ){
				//TODO change x direction to a negative value
				this.xv = -1;
				this.yv = 0;
			}
			//if bRight is touched move the battleship to the right
			if(event.getX() > getWidth() - touchWidth && event.getY() >= getHeight() / 2 - touchHeight && event.getY() <= getHeight() /2){
				//TODO change x direction to a negative value
				this.xv = 1;
				this.yv = 0;
			}
			//TODO if bUp is touched move the bullets to that direction
			if(event.getX() < (getWidth() / 2 + touchWidth) && event.getY() <= touchHeight && event.getX() >= (getWidth() /2)){
				
				//TODO change x direction to a negative value
				this.xv = 0;
				this.yv = 1;
			}
			//TODO if bDown is touched move the bullets to that direction
			if(event.getY() > getHeight() - touchHeight && event.getX() <= getWidth()/2+touchWidth && event.getX() >= getWidth() / 2){
				
				//TODO change x direction to a negative value
				this.xv = 0;
				this.yv = -1;
			}
			
			//////////////////TODO move the bullets diagonally
			//TODO if bludiagonal is touched move the bullets to that direction
			if(event.getY() < touchHeight && event.getX() < touchWidth){
				
				//TODO change x direction to a negative value
				this.xv = -1;
				this.yv = 1;
			}
			
			//TODO if bRudiagonal is touched move the bullets to that direction
			if(event.getX() > this.screenwidth - touchWidth  && event.getY() < 30){
				
				//TODO change x direction to a negative value
				this.xv = 1;
				this.yv = 1;
			}
			
			//TODO if bRudiagonal is touched move the bullets to that direction
			if(event.getX() < touchWidth  && event.getY() > this.screenheight - touchHeight){
				
				//TODO change x direction to a negative value
				this.xv = -1;
				this.yv = -1;
			}
			
			//TODO if bRudiagonal is touched move the bullets to that direction
			if(event.getX() > this.screenwidth - touchWidth  && event.getY() > this.screenheight - touchHeight){
				
				//TODO change x direction to a negative value
				this.xv = 1;
				this.yv = -1;
			}
			
			// check if explosion is null or if it is still active
			for(int i=0; i<explode.length; i++){
			if (explode[i] == null || explode[i].getState() == Explode.STATE_DEAD) {
				//restrict the explosions to the screen only not where the battleship is
				if(!battleship.Touched() && this.letExplosion == true && event.getY() < getHeight() - touchHeight && event.getX() > touchWidth &&
						event.getX() < this.screenwidth - touchWidth && event.getY() > touchHeight && MainGamePanel.paused == false){
					
					explode[i] = new Explode(this.screenheight,this.screenwidth,EXPLOSION_SIZE, (int)event.getX(), (int)event.getY(),this);
					this.letExplosion = false;
				}
				break;
			}
			}
			
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(battleship.Touched() && MainGamePanel.paused == false){
				//move it to new position
				battleship.setX((int)event.getX());
				//Not to go on the lower part of the screen.
				if((int)event.getY() < getHeight() - (touchHeight*2)){
					battleship.setY((int)event.getY());
				}else{
					battleship.setY(getHeight() - (touchHeight*2) );
				}if((int)event.getX() < ((touchWidth*2))){
					battleship.setX((touchWidth*2));
				}if((int)event.getX() > getWidth() - (touchWidth*2)){
					battleship.setX(getWidth() - (touchWidth*2));
				}
				//check if forcefield is set to true 
				if(forceField == true){
					//move the force field x and y coordinates too
					forcefield.setX(battleship.getX());
					forcefield.setY(battleship.getY());
				}
			}
			
		}if(event.getAction() == MotionEvent.ACTION_UP){
			if(battleship.Touched()){
				battleship.setTouched(false);
			}
		}
		return true;
		
	}
	
	
	
	
	public int[] getXCoordinates(){
		int[] xcoordinates;
		xcoordinates = new int[enemies.length];
		//get the x and y coordinates of the enemy
		for(int i=0; i<this.enemies.length ; i++){
			if(this.enemies[i] != null){
					//place in the coordinates array variable 
					xcoordinates[i] =(int) this.enemies[i].getX();
			}
		}
		return xcoordinates;
	}
	public int[] getYCoordinates(){
		int[] ycoordinates;
		ycoordinates = new int[enemies.length];
		//get the y coordinates of the enemy
		for(int i=0; i<this.enemies.length; i++){
			if(this.enemies[i] != null){
					ycoordinates[i] = (int) this.enemies[i].getY();
			}
		}
		return ycoordinates;
	}
	
	public int[] getEbitmap(){
		int[] bitmap;
		bitmap = new int[enemies.length];
		//get the bitmap
		for(int i=0; i<this.enemies.length; i++){
			if(this.enemies[i] != null){
				bitmap[i] = this.enemies[i].getRequired();
			}
			
		}
		return bitmap;
	}
	//get the x and y coordinates of the boss
	public int[] bossCoord(){
		int[] bosscoord = new int[2];
		if(drawBoss == true){
			for(int i=0; i<2;i++){
				if(this.boss[0] != null){
					//get the x and y coordinates
					if(i == 0)
						bosscoord[i] =(int) this.boss[0].getX();
					if(i == 1)
						bosscoord[i] = (int) this.boss[0].getY();
				}
			}
		}
		return bosscoord;
	}
	//get the  x and y coordinates of the upgarde
	public int[] shieldCoord(){
		int[] upgradeCoord = new int[2];
		if(MainGamePanel.drawUpgrade == true){
			for(int i=0; i<2;i++){
				if(this.upgrade[0] != null){
					//get the x and y coordinates
					if(i == 0)
						upgradeCoord[i] =(int) this.upgrade[0].getX();
					if(i == 1)
						upgradeCoord[i] = (int) this.upgrade[0].getY();
				}
			}
		}
		return upgradeCoord;
	}
	// get the x and y of the explosion upgrade
	public int[] explodeCoord(){
		int[] upgradeCoord = new int[2];
		if(MainGamePanel.drawExplosion == true){
			for(int i=0; i<2;i++){
				if(this.blast[0] != null){
					//get the x and y coordinates
					if(i == 0)
						upgradeCoord[i] =(int) this.blast[0].getX();
					if(i == 1)
						upgradeCoord[i] = (int) this.blast[0].getY();
				}
			}
		}
		return upgradeCoord;
	}
	
	// get the x and y of the explosion upgrade
		public int[] playerCoord(){
			int[] playerCoord = new int[2];
				for(int i=0; i<2;i++){
					//get the x and y coordinates
					if(i == 0)
						playerCoord[i] =(int) this.battleship.getX();
					if(i == 1)
						playerCoord[i] = (int) this.battleship.getY();
				}
			return playerCoord;
		}
	
	//function to return this activity
	public void callend(){
		thread.setRunning(false);
		MainGamePanel.ourSong.stop();
		((Activity)getContext()).finish();
		
	}
	
	//function to begin the game.
	public void begingame(){
		file();
		beginGame = false;
	}
	
	//Function to cause the app to vibrate
	public void vibrate(){
		Vibrator v = (Vibrator) cxt.getSystemService(Context.VIBRATOR_SERVICE);
		//Vibrate for 300 milliseconds
		v.vibrate(300);
	}
	
	//check file if it crashed 
		public void crashed(){			
			this.user = null;
			int lives = 0;
			int i = 0;
			int o = 0;
			int j = 0;
			int showfield = 0;
			int playerX = this.screenwidth/2;
			int playerY = this.screenheight/2;
			int blast = 0;
			int blastX = 0;
			int blastY = 0;
			int shield = 0;
			int shieldX = 0;
			int shieldY = 0;
			int boss = 0;
			int bossX = 0;
			int bossY = 0;
			int bossdamage = 0;
			int[] container = new int[46];
			int[] x = new int[10];
			int[] y = new int[10];
			int[] b = new int[10];
			String result = "";
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new InputStreamReader(cxt.openFileInput(WorldWar_XActivity.Pause)));
				//StringBuilder sb = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null){
					container[i] = Integer.parseInt(line);
					i++;				
				}
				
			}catch(Exception e){
				e.printStackTrace();
			} 
			//separate the data ,
			for (int k=0; k < 46; k++){
				if(container != null){
					if(k<10){
						//xcoordinates.
						x[k]=container[k];
					}
					if(k>=10 && k<20){
						//ycoordinates
						y[o]=container[k];
						o++;
					}
					if(k>=20 && k<30){
					//bitmap and damage 
						b[j]=container[k];
						j++;
					}
					if(k == 30){
						boss = container[k];
					}
					if(k == 31){
						bossdamage = container[k];
					}
					if(k>=32 && k<34){
						//give the boss its coordinates;
						if(k==32)
							bossX = container[k];
						if(k==33)
							bossY = container[k];
					}
					if(k==34){
						shield = container[k];
					}
					if(k>=35 && k<37){
						//give the shield its coordinates;
						if(k==35)
							shieldX = container[k];
						if(k==36)
							shieldY = container[k];
					}
					if(k==37){
						blast = container[k];
					}
					if(k>=38 && k<40){
						//give the shield its coordinates;
						if(k==38)
							blastX = container[k];
						if(k==39)
							blastY = container[k];
					}
					if(k == 40)
						MainGamePanel.score = container[k];
					if(k == 41)
						lives = container[k];
					if(k == 42)
						showfield = container[k];
					if(k>=43 && k <45){
						//get the player coordinates.
						if(k==43){
							if(container[k] != 0){
								playerX = container[k];
							}
						}
						if(k==44){
							if(container[k] != 0){
								playerY = container[k];
							}
						}
					}
					if(k == 45){
						if(container[k] == 1){
							MainGamePanel.paused = true;					
						}
					}
				}
			}
			
			//assign the right bitmap and also the right damage to receive.
			//spawn the enemies on the screen and put the game on pause.
			for(int c = 0; c < this.enemies.length; c++){
				if(this.enemies[c] == null && x[c] != 0){
					if(b[c] == 1){
						this.enemies[c]= new Enemy(this.touchHeight,this.touchWidth,this.enemyCrafts,x[c],y[c],this.screenwidth,this.screenheight,0.15f,1);				
					}
					else if (b[c] == 2){
						this.enemies[c]= new Enemy(this.touchHeight,this.touchWidth,this.otherCraft,x[c],y[c],this.screenwidth,this.screenheight,0.15f,2);
					}
				}
			}
			
			//spawn the enemy boss if it were created
			if(boss == 5){
				for(int h=0; h<1;h++){
					this.boss[h]= new Boss(bossX,bossY,this.enemyBoss,this.screenwidth,this.screenheight,bossdamage);
					drawBoss = true;
					this.bossScore = 0;
				}
				bossDamage += 2;
			}
			//spawn the shield if it were created
			if(shield == 1 &&  shieldX != 0){
				for(int s=0; s<1 ; s++){
				
					this.upgrade[s] = new  Upgrade(shieldX,shieldY,this.Upgrade);
					this.upgraded = 0;
					MainGamePanel.drawUpgrade = true;
				}
				
			}
			if(blast == 1 && blastX != 0){
				for(int s=0; s<1 ; s++){
					
					this.blast[s] = new  Upgrade(blastX,blastY,this.Grenade);
					this.explodescore = 0;
					MainGamePanel.drawExplosion = true;
				}
				
			}
			if(lives > 0){
				MainGamePanel.lives = lives;
			}
			//spawn the battleship 
			battleship = new Battleship(this.battleshipPlayer,playerX, playerY);
			forceTime = System.currentTimeMillis();
			//spawn the field
			if(showfield == 1){
				MainGamePanel.forceField = true;
				this.shield.setColor(Color.WHITE);
				int height = this.Upgrade.getHeight();
				int width = this.Upgrade.getWidth();
				forcefield = new ForceField(height,width,battleship.getX(),battleship.getY(),10,this.battleshipPlayer);
			}
			//spawn_field();
			
			
		
		}
	
	//function to create and update the filesystem to control highscore.
	public void file(){
			//perform a check
			//first read from file and get the highsore
			FileInputStream fis = null;
			try {
				fis = this.cxt.openFileInput(FILENAME);
				//Read all the byte data
				byte[] dataArray = new byte[fis.available()];
				while(fis.read(dataArray) != -1){
					//read all the data
					this.collected = new String(dataArray);
					fis.close();
					if(this.collected.length()==0){
						this.collected = "0";
						this.highscore = Integer.parseInt(this.collected);
						if(MainGamePanel.score >= this.highscore){
							//overwrite the value in the file
							try {
								fos = this.cxt.openFileOutput(this.FILENAME, Context.MODE_PRIVATE);
								//write the data which is converted into an array of bytes
								String score = this.setScore(this.score);
								fos.write(score.getBytes());
								fos.close();
								return;
							}catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else{
						this.highscore = Integer.parseInt(this.collected);
						if(this.score >= this.highscore){
							//overwrite the value in the file
							try {
								fos = this.cxt.openFileOutput(FILENAME, Context.MODE_PRIVATE);
								//write the data which is converted into an array of bytes
								String score = this.setScore(this.score);
								fos.write(score.getBytes());
								fos.close();
							}catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//Log.d(TAG,"File not found");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//Log.d(TAG, "Input output error");
				e.printStackTrace();
			}finally{
				
			}
		}
	
	public void Exit(){
		if(runGame == false && killGame == true){
			ourSong.stop();
			thread.setRunning(false);
			((Activity)getContext()).finish();
		}
	}

	//generate the stars
	public void generate_stars(){
		Random r = new Random();
		for(int i=0; i<stars.length;i++){
			float x,y;
			x = r.nextInt(screenwidth -60)+30;
			y = r.nextInt(screenheight-50);
			//call the constructor.
			this.stars[i] = new Stars(x,y,0.5f,20,this.screenheight,this.screenwidth,2);
		}
	}
	//move the stars
	//implement the pause 
	public void move_stars(){
		for(int i=0; i<stars.length; i++){
			if(paused == false){
				stars[i].move_star();
			}
			
		}
	}
	//reset the position of the stars
	public void reset_stars(){
		for(int i=0; i<stars.length; i++){
			stars[i].reset_star();
		}
	}

	
	//spawn the upgrade
		public void generate_upgrade(){
			//make the upgrade spawn	
			//this.upgraded = this.score;
			if(this.upgraded >= 60){
				//spawn the upgrade.
				for(int i=0;i<this.upgrade.length; i++){
					if(this.upgrade[i] == null){
						
						Random r = new Random();
						int x = r.nextInt(screenwidth-((touchWidth*2))) + (touchWidth);
						if(x < touchWidth + 10 || x > screenwidth-(touchWidth+10))
							x = screenwidth/2;
							
						upgrade[i] = new Upgrade(x,0,Upgrade);
						this.upgraded = 0;
						this.drawUpgrade = true;
						break;
					}
					
				}
			}
			//spawn the upgrade for explosions
			if(this.explodescore >= 100){
				//spawn the upgrade.
				for(int i=0;i<this.blast.length; i++){
					if(this.blast[i] == null){
						Random r = new Random();
						int x = r.nextInt(this.screenwidth-((touchWidth*2))) + (touchWidth);
						if(x < touchWidth + 10 || x > screenwidth-(touchWidth+10))
							x = screenwidth/2;
						
						blast[i] = new Upgrade(x,0,Grenade);
						this.explodescore = 0;
						this.drawExplosion = true;
						break;
					}
					
				}
			}
		}
	
	//move the upgrade
	public void move_upgrade(){
		if(this.drawUpgrade == true){
			for(int i=0;i<upgrade.length;i++){
				
				if(upgrade[i] != null && paused == false){
					//if the y position is outside destroy the upgrade.
					if(this.upgrade[i].getY() > this.screenheight){
						this.upgrade[i] = null;
					}
				}
				if(upgrade[i] != null && paused == false){
					this.upgrade[i].move();
					//check for collision with the battleship
					if(ucollisionb(upgrade[i],25) == 1){
							forceField = true;
							forceTime = System.currentTimeMillis();
							shield.setColor(Color.WHITE);
							int height = this.Upgrade.getHeight();
							int width = this.Upgrade.getWidth();
							forcefield = new ForceField(height,width,battleship.getX(),battleship.getY(),10,this.battleshipPlayer);
							//destroy the upgrade
							if(this.upgrade[i] != null)
								this.upgrade[i] = null;
						//
					}					
					
				}
		
			}
	
		}
		//TODO: move the upgrade
		if(this.drawExplosion == true){
			for(int i=0;i<blast.length;i++){
				if(upgrade[i] != null && paused == false){
					//if the y position is outside destroy the upgrade.
					if(this.upgrade[i].getY() > this.screenheight){
						//destroy the upgrade if it is picked
						if(this.upgrade != null){
							this.upgrade[i] = null;
						}
					}
				}
				if(blast[i] != null && MainGamePanel.paused == false){
					this.blast[i].move();
					//check for collision with the battleship
					if(ucollisionb(blast[i],15) == 1){
						//destroy the upgrade
						if(this.blast[i] != null)
							this.blast[i] = null;
						//Activate the explosion
						this.letExplosion = true;
					}
				}
				
			}
		}
		
	}
	
	//if upgrade is hit spawn the force field
	//TODO: After 10 seconds make the force field disappear

	
	//destroy the force field
	public void destroy_force(){
		if(forceField == true){
			
			long current = System.currentTimeMillis();
			long diff = current - forceTime;
			if(diff > 10000){
				shield.setColor(Color.RED);
			}
			if(diff > 15000){
				
				//destroy the force field
				forceField = false;
				shield.setColor(Color.WHITE);
			}
		}
	}
	
	//check for collision between battleship and upgrade
	public int ucollisionb(Upgrade upgrade, int dimmens){
		///check for collision
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = upgrade.getX();//x coordinate
		y_shot = upgrade.getY();//y coordinate
		
		int height = Upgrade.getHeight()-5;
		int width = Upgrade.getWidth();;
						
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
						
		shot_min.x = x_shot - (width/2);
		shot_min.y = y_shot - (height/2);
						
		shot_max.x = x_shot + (width/2);
		shot_max.y = y_shot + (height/2);
		
		int eheight = battleshipPlayer.getHeight();
		int ewidth = battleshipPlayer.getWidth();
							
		x_enemy = this.battleship.getX();
		y_enemy = this.battleship.getY();
							
		enemy_min.x = x_enemy - (ewidth/2);
		enemy_min.y = y_enemy - (eheight/2);
							
		enemy_max.x = x_enemy + (ewidth/2);
		enemy_max.y = y_enemy + (eheight/2);
							
		boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
		if(hit)
		{	
			return 1;				
		}
		return 2;
		
	}
	
	//TODO also test for collision with each enemy use nested loops for this.
	public void update_explosion(){
		// update explosions
		for( int i=0; i<explode.length;i++){
			if (explode[i] != null && explode[i].isAlive() && this.paused == false) {
				//create a loop for enemies and pass it as an arguement then test for collision in class Explode.
					this.score = explode[i].update(getHolder().getSurfaceFrame(),this.enemies,this.score,this.explosionS,this.sp);
				}
				
			}
		//return this.score = explode[i].update(getHolder().getSurfaceFrame(),this.enemies,this.score);
		}
	
	
	////make the enemies shoot
	public void enemies_shoot(){
		long t = System.currentTimeMillis();
		long elapsed= t - bulletTime;
		if(elapsed > 1500)
		{
			bulletTime = t;
			//create a nested loop each enemy five shots.
			for(int i=0; i<this.enemies.length; i++){
				for(int x=0; x<this.ebullets.length; x++){
					//generate the shot
					if(this.ebullets[x] == null && this.enemies[i] != null){
						this.ebullets[x] = this.enemies[i].generate_shot(this.enemiesShot,0,-1);
						break;
					}
				}
			}
		}
		
		if(this.drawBoss == true){
			//make the enemy Boss generate a bullet shot.
			long n = System.currentTimeMillis();
			long diff = n - this.diagN;
			if(diff > 1500){
				this.diagN = n;
				for(int i=0; i<this.boss.length; i++){
					for(int x=0; x<this.diagBossbullets.length; x++){
						if(this.diagBossbullets[x] == null && this.boss[i] != null){
							this.diagBossbullets[x] = this.boss[i].generate_shot(BossShot,0,0);
							break;
						}
					}
				}
			}
		}
		
		if(this.drawBoss == true){
			//make the enemy Boss generate a bullet shot.
			long n = System.currentTimeMillis();
			long diff = n - this.bossBullet;
			if(diff > 1500){
				bossBullet = n;
				for(int i=0; i<this.boss.length; i++){
					for(int x=0; x<this.bossbullets.length; x++){
						if(this.bossbullets[x] == null && this.boss[i] != null){
							this.bossbullets[x] = this.boss[i].generate_shot(BossShot,0,0);
							break;
						}
					}
				}
			}
		}
		
		if(this.drawBoss == true){
			//make the enemy Boss generate a bullet shot.
			long n = System.currentTimeMillis();
			long diff = n - this.diagP;
			if(diff > 1500){
				this.diagP = n;
				for(int i=0; i<this.boss.length; i++){
					for(int x=0; x<this.diagPBossbullets.length; x++){
						if(this.diagPBossbullets[x] == null && this.boss[i] != null){
							this.diagPBossbullets[x] = this.boss[i].generate_shot(BossShot,0,0);
							break;
						}
					}
				}
			}
		}
	}
	
	////move the enemy bullets
	public void ebullet_move(double avgFps){
		for(int i=0; i<this.enemies.length; i++){
			for(int x=0; x<this.ebullets.length; x++){
				//generate the shot
				if(this.ebullets[x] != null && this.paused == false){
					this.ebullets[x].move_down(avgFps);
					//check if bullet has hit battleship bullet
					this.ebcollide(ebullets[x]);
					//check if bullet has hit battleship.
					this.etobcollision(ebullets[x]);
					//check if bullet has hit force field
					this.eb_fcollision(ebullets[x]);
				}
				if(this.ebullets[x] != null && this.ebullets[x].getY() > this.screenheight-60){
					//destroy the bullet
					this.ebullets[x] = null;
				}
			}
		}
	}
	
	//destroy the boss bullets if the boss is destroyed
	
	//check for collision between enemy bullet and battleship
	public void etobcollision(Bullet ebullets){
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = ebullets.getX();//x coordinate
		y_shot = ebullets.getY();//y coordinate
						
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
						
		shot_min.x = x_shot - 5;
		shot_min.y = y_shot - 5;
						
		shot_max.x = x_shot + 5;
		shot_max.y = y_shot + 5;
						
							
		x_enemy = this.battleship.getX();
		y_enemy = this.battleship.getY();
							
		enemy_min.x = x_enemy - 14;
		enemy_min.y = y_enemy - 14;
							
		enemy_max.x = x_enemy + 14;
		enemy_max.y = y_enemy + 14;
							
		boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
		if(hit){
			
			//load the bullet hit explosions
			for(int j=0; j<explosion.length; j++){
				//create the explosion
				explosion[j] = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.bullethitb),(int)battleship.getX()-10,(int)battleship.getY()-10);
				explosionTime = System.currentTimeMillis();
				break;
			
			}
			
			lives -= 1;
			vibrate();//vibrate to show loss of life
			if(lives <= 0){
				runGame = false;
				ourSong.stop();
			}
			//score += 10;
			ebullets.setY(-3);//destroy it.
			return;
		} 	
			
	}
	
	//check for collision between enemy boss bullet and the force field.
	public void eb_fcollision(Bullet ebullets){
			if(forceField == true){
				///check for collision
				//get the x and y coordinates
				float x_shot,y_shot,x_enemy,y_enemy;
				x_shot = ebullets.getX();//x coordinate
				y_shot = ebullets.getY();//y coordinate
								
				vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
				vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
								
				shot_min.x = x_shot - 5;
				shot_min.y = y_shot - 5;
								
				shot_max.x = x_shot + 5;
				shot_max.y = y_shot + 5;
				
				int height = battleshipPlayer.getHeight()+20;
				int width = battleshipPlayer.getWidth()+20;
									
				x_enemy = this.battleship.getX();
				y_enemy = this.battleship.getY();
									
				enemy_min.x = x_enemy - (width/2);
				enemy_min.y = y_enemy - (height/2);
									
				enemy_max.x = x_enemy + (width/2);
				enemy_max.y = y_enemy + (height/2);
									
				boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
				if(hit){
					//score += 10;
					//ebullets.setY(0);//destroy it.
					ebullets.setY(screenheight -50);
					return;
				} 
			}
		}
	
	//check if enemy bullet has hit battleship bullet
	public void ebcollide(Bullet ebullets){
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = ebullets.getX();//x coordinate
		y_shot = ebullets.getY();//y coordinate
		//get the height and width coordinates of enemy bullet 
		int height = enemiesShot.getHeight();
		int width = enemiesShot.getWidth();
				
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
				
		shot_min.x = x_shot - (width/2);
		shot_min.y = y_shot - (height/2);
				
		shot_max.x = x_shot + (width/2);
		shot_max.y = y_shot + (height/2);
		
		int eheight = battleshipshot.getHeight();
		int ewidth = battleshipshot.getWidth();
				
		for(int i=0;i<bullets.length;i++)
		{
			if(this.bullets[i] != null){
					
				x_enemy = bullets[i].getX();
				y_enemy = bullets[i].getY();
				
					
				enemy_min.x = x_enemy - (ewidth/2);
				enemy_min.y = y_enemy - (eheight/2);
					
				enemy_max.x = x_enemy + (ewidth/2);
				enemy_max.y = y_enemy + (eheight/2);
					
				boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
				if(hit)
				{
					bullets[i]=null;
					//score += 10;
					ebullets.setY(-3);//destroy it.
					return;
				}
			}
		}
		
	}
	
	
	
	///generate enemies
	public void update_enemies(){
		//TODO : after 2 minutes stop generating enemies enemy from the top only
		if(MainGamePanel.paused == false){
			long current = System.currentTimeMillis();
			long diff = current - enemiesTime;
			int count = 1;// keep the number of boss enemy 
			if(diff > 1000 && diff < 30000){
				if(drawBoss == false){
				//enemiesTime = current;
				//generate enemies sequentially
				long t = System.currentTimeMillis();
				long elapsed = t - eSystemTime;
				float x,y;
				Random r = new Random();
				if(elapsed > 700){
					//generate the ladybirds first 
					eSystemTime = t;
					for(int i = 0; i < this.enemies.length; i++){
						if(this.enemies[i] == null){
							x = r.nextInt(screenwidth-((touchWidth*2)+30)) + (touchWidth);
							y = 15;
							this.enemies[i]= new Enemy(this.touchHeight,this.touchWidth,enemyCrafts,x,y,this.screenwidth,this.screenheight,0.15f,1);
							break;
						}
					}
				}
				}
				
			}
			///after 10 seconds spawn the enemy boss(test)
			/////spawn the boss after every 500 points 
			if(this.bossScore > 100 && diff > 2000){
					//spawn the enemy boss after 2seconds.
					long b = System.currentTimeMillis();
					long belapsed = b - bossTime;
					//spawn the enemy boss.
					if(belapsed > 500){
						bossTime = b;
						for(int i=0; i<count;i++){
							if(this.boss[i] == null){	
								//spawn the enemy boss then break.
								this.boss[i]= new Boss(touchWidth,touchWidth*4,enemyBoss,this.screenwidth,this.screenheight,bossDamage);
								drawBoss = true;
								this.bossScore = 0;
								 
								break;
							}
						}
						count += 1;
						bossDamage += 2;
						if(bossDamage > 25)
							bossDamage = 25;
						this.sufferTime += 1000;
					}
			}
			//TODO : spawn the enemies from all directions ie left , right and up.
			if(diff >30000){
				if(drawBoss == false){
				//generate enemies sequentially
				long t = System.currentTimeMillis();
				long elapsed = t - eSystemTime;
				float x,y;
				Random r = new Random();
				if(elapsed > 500){
					//generate the enemy bugs.
					eSystemTime = t;
					for(int i = 0; i < this.enemies.length; i++){
						if(this.enemies[i] == null){
							//x = 30;
							y = r.nextInt(screenheight / 2 ) + touchWidth+5;
							if(y < (screenheight / 4) + 10)
							{
								x = screenwidth-((touchWidth*2)+10);
							}else{
								x = (touchWidth+5);
							}
							this.enemies[i]= new Enemy(this.touchHeight,this.touchWidth,otherCraft,x,y,this.screenwidth,this.screenheight,0.15f,2);
							break;
						}
					}
				}
				}
				if(diff > 50000){
					//reset the whole thing to restart 
					enemiesTime = current;
				}
				
			}
		}
		
	}
	
	//function to move enemies only
	public void move_enemies(){
		
		for(int i=0; i<enemies.length; i++){
			if(this.enemies[i] != null && this.paused == false){
				long current = System.currentTimeMillis();
				long diff = current - gameTime;
				if(diff > sufferTime){
					if(gameTime > 47000){
						gameTime = 43000;
					}
					gameTime = current;
				}
				//function to move the enemy and contain it in the screen
				this.enemies[i].update(System.currentTimeMillis());
				this.enemies[i].boundary(this.enemyTime,this.gameTime);	
				
				
				//check if enemy has collided with battleship
				if(MainGamePanel.forceField == false){
					if(this.btoecollision(this.battleship,this.enemies[i]) == 1){
						this.enemies[i]=null;
						lives -= 1;// subtract one life
						vibrate();//vibrate to show loss of life
						if(lives <= 0 && MainGamePanel.forceField == false){
							runGame = false;
							ourSong.stop();
							
						}
					}
				}
				//check if enemy has collided with explosion if it has occurred
				
				if(this.enemies[i] != null ){
					//check for collision between enemy and force field if its on.
					if(this.ecollisionf(this.enemies[i]) == 1){
						this.enemies[i]=null;
					}
				}
				
				
				
			}
			//if it get out of the screen, destroy it.
			if(this.enemies[i] != null && enemies[i].getY() < 0){
				this.enemies[i] = null;
			}
		}
		
		//move the enemy boss if it exists.
		if(drawBoss == true){
			//move the enemy boss
			for(int i=0; i<boss.length;i++){
				if(this.boss[i] != null && MainGamePanel.paused == false){
					this.boss[i].boundary();
					//check if enemy boss has collided with battleship player
					if(ebcollisionb(this.boss[i]) == 1){
						//player is dead
						lives -= 1;// subtract one life
						vibrate();//vibrate to show loss of life
						if(lives <= 0){
							runGame = false;
							ourSong.stop();
							
						}
						}
				}
					
				}
			}
		}
	
	//collision btwn enemy boss brain and battleship
	public int ebcollisionb(Boss boss){
		
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = boss.getX();//x coordinate
		y_shot = boss.getY();//y coordinate
		
		int height = enemyBoss.getHeight();
		int width = enemyBoss.getWidth();
						
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
						
		shot_min.x = x_shot - (width/2);
		shot_min.y = y_shot - (height/2);
						
		shot_max.x = x_shot + (width/2);
		shot_max.y = y_shot + (height/2);
						
							
		x_enemy = this.battleship.getX();
		y_enemy = this.battleship.getY();
							
		enemy_min.x = x_enemy - 10;
		enemy_min.y = y_enemy - 10;
							
		enemy_max.x = x_enemy + 10;
		enemy_max.y = y_enemy + 10;
			
			boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
			
			if(hit)
			{	
				return 1;				
			}
	return 2;
	}
	
	
	//check if enemy has collided with forcefield
	public int ecollisionf(Enemy enemy){
		if(forceField == true){
			
			int height = enemyCrafts.getHeight();
			int width = enemyCrafts.getWidth()/4;
			
			//get the x and y coordinates
			float x_shot,y_shot,x_enemy,y_enemy;
			x_shot = enemy.getX()+width;//x coordinate
			y_shot = enemy.getY()+height;//y coordinate
							
			vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
			vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
							
			shot_min.x = x_shot - (width/2);
			shot_min.y = y_shot - (height/2);
							
			shot_max.x = x_shot + (width/2);
			shot_max.y = y_shot + (height/2);
							
								
			x_enemy = this.battleship.getX();
			y_enemy = this.battleship.getY();
			
			int eheight = battleshipPlayer.getHeight()+(height+10);
			int ewidth = battleshipPlayer.getWidth()+(width+15);
								
			enemy_min.x = x_enemy - (ewidth/2);
			enemy_min.y = y_enemy - (eheight/2);
								
			enemy_max.x = x_enemy + (ewidth/2);
			enemy_max.y = y_enemy + (eheight/2);
				
				boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
				
				if(hit)
				{
					//load the bullet hit explosions
					for(int j=0; j<explosion.length; j++){
						//create the explosion
						explosion[j] = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.bullethitl),(int)enemy.getX()+10,(int)enemy.getY()+10);
						explosionTime = System.currentTimeMillis();
						break;
					
					}
					
					return 1;				
				}
			}
		return 2;
		
	}
	
	////check if the enemy has collided with battleship
	public int btoecollision(Battleship battleship,Enemy enemy){		
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = battleship.getX() ;//x coordinate
		y_shot = battleship.getY(); //y coordinate
		//get the height and width coordinates of battleship bitmap.
				
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
				
		shot_min.x = x_shot - 12;
		shot_min.y = y_shot - 12;
				
		shot_max.x = x_shot + 12;
		shot_max.y = y_shot + 12;
		
		//get the height and width values of the enemies bitmap.
		int eheight = enemyCrafts.getHeight();
		int ewidth = enemyCrafts.getWidth()/4;
				
					
		x_enemy = enemy.getX()+(ewidth/2);//shift to rectify
		y_enemy = enemy.getY()+(eheight/2);//shift to rectify
					
		enemy_min.x = x_enemy - (ewidth/2);
		enemy_min.y = y_enemy - (eheight/2);
					
		enemy_max.x = x_enemy + (ewidth/2);
		enemy_max.y = y_enemy + (eheight/2);
					
		boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
					
		if(hit)
		{
						
			return 1;
						
		}
	return 2;
	}
	
	
	
	// when update is called generate a bullet
	public void update_bullets(){
		long t = System.currentTimeMillis();
		long elapsed= t - mSystemTime;
		
		if(elapsed > 700)
		{
			mSystemTime = t;
			for(int i = 0; i< this.bullets.length; i++)
			{
				if(bullets[i] == null)
				{
					bullets[i] = battleship.generateBullet(this.battleshipshot,this.xv,this.yv);
					break;
				}
			}
		}
	}
	
	public void move_bullets(double avgFps){
		for(int i=0; i<this.bullets.length; i++){
			if(this.bullets[i] != null && this.paused == false){
				//move the bullet.
				this.bullets[i].move(avgFps);
				//check if bullet has hit enemy
				bcollision(this.bullets[i]);
				ebcollisionb(this.bullets[i]);
			}
			if(this.bullets[i] != null ){
				if(bullets[i].getY() < 0 || bullets[i].getX() < 0 || bullets[i].getX() > this.screenwidth
						|| bullets[i].getY() > this.screenheight - 50)
				this.bullets[i] = null;
			}
		}
		//make the enemy boss bullets move
			for(int i=0; i<this.bossbullets.length; i++){
				if(this.bossbullets[i] != null && MainGamePanel.paused == false){
					//move the bullet.
					this.bossbullets[i].moveBoss(avgFps);
					//check if bullet has hit enemy
					if(bcollisionb(this.bossbullets[i]) == 1){
						//destroy the bullet and subtract one life
						this.bossbullets[i]=null;
						lives -= 1;// subtract one life
						vibrate();//vibrate to show loss of life
						if(lives <= 0){
							runGame = false;
							ourSong.stop();
							
						}
					}
					//check if the enemy boss bullet has hit force field
					if(this.forceField == true && this.bossbullets[i] != null){
						eb_fcollision(this.bossbullets[i]);
					}
					//check if the enemy boss bullet has hit battleship bullet
					if(this.bossbullets[i] != null){
						ebbcollidebb(this.bossbullets[i]);
					}
				}
				if(this.bossbullets[i] != null ){
					if(bossbullets[i].getY() < 0 || bossbullets[i].getX() < 0 || bossbullets[i].getX() > this.screenwidth
							|| bossbullets[i].getY() > this.screenheight - 50)
					this.bossbullets[i] = null;
				}
			}
		
		//make the enemy boss bullets move
					for(int i=0; i<this.diagBossbullets.length; i++){
						if(this.diagBossbullets[i] != null && MainGamePanel.paused == false){
							//move the bullet.
							this.diagBossbullets[i].diagNBoss(avgFps);
							//check if bullet has hit enemy
							if(bcollisionb(this.diagBossbullets[i]) == 1){
								//destroy the bullet and subtract one life
								this.diagBossbullets[i]=null;
								lives -= 1;// subtract one life
								vibrate();//vibrate to show loss of life
								if(lives <= 0){
									runGame = false;
									ourSong.stop();
									
								}
							}
							//check if the enemy boss bullet has hit force field
							if(this.forceField == true && this.diagBossbullets[i] != null){
								eb_fcollision(this.diagBossbullets[i]);
							}
							//check if the enemy boss bullet has hit battleship bullet
							if(this.diagBossbullets[i] != null){
								ebbcollidebb(this.diagBossbullets[i]);
							}
						}
						if(this.diagBossbullets[i] != null ){
							if(this.diagBossbullets[i].getY() < 0 || this.diagBossbullets[i].getX() < 0 || this.diagBossbullets[i].getX() > this.screenwidth - 30
									|| this.diagBossbullets[i].getY() > this.screenheight - 50 || this.diagBossbullets[i].getX() < 30)
							this.diagBossbullets[i] = null;
						}
					}
				
				
				
				//make the enemy boss bullets move
				
					for(int i=0; i<this.diagPBossbullets.length; i++){
						if(this.diagPBossbullets[i] != null && MainGamePanel.paused == false){
							//move the bullet.
							this.diagPBossbullets[i].diagPBoss(avgFps);
							//check if bullet has hit enemy
							if(bcollisionb(this.diagPBossbullets[i]) == 1){
								//destroy the bullet and subtract one life
								this.diagPBossbullets[i]=null;
								lives -= 1;// subtract one life
								vibrate();//vibrate to show loss of life
								if(lives <= 0){
									runGame = false;
									ourSong.stop();
									
								}
							}
							//check if the enemy boss bullet has hit force field
							if(this.forceField == true && this.diagPBossbullets[i] != null){
								eb_fcollision(this.diagPBossbullets[i]);
							}
							//check if the enemy boss bullet has hit battleship bullet
							if(this.diagPBossbullets[i] != null){
								ebbcollidebb(this.diagPBossbullets[i]);
							}
						}
						if(this.diagPBossbullets[i] != null ){
							if(this.diagPBossbullets[i].getY() < 0 || this.diagPBossbullets[i].getX() < 0 || this.diagPBossbullets[i].getX() > this.screenwidth-30
									|| this.diagPBossbullets[i].getY() > this.screenheight - 50)
							this.diagPBossbullets[i] = null;
						}
					}
				
		
		
		
	}
	//check if enemy boss bullets and battleship bullets
	public void ebbcollidebb(Bullet ebullets){
		//get the x and y coordinates
				float x_shot,y_shot,x_enemy,y_enemy;
				x_shot = ebullets.getX();//x coordinate
				y_shot = ebullets.getY();//y coordinate
				//get the height and width of the bitmap bullet
				int height = enemiesShot.getHeight();
				int width = enemiesShot.getWidth();
						
				vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
				vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
						
				shot_min.x = x_shot - (width/2);
				shot_min.y = y_shot - (height/2);
						
				shot_max.x = x_shot + (width/2);
				shot_max.y = y_shot + (height/2);
				
				int eheight = battleshipshot.getHeight();
				int ewidth = battleshipshot.getWidth();
						
				for(int i=0;i<bullets.length;i++)
				{
					if(this.bullets[i] != null){
							
						x_enemy = bullets[i].getX();
						y_enemy = bullets[i].getY();
							
						enemy_min.x = x_enemy - (ewidth/2);
						enemy_min.y = y_enemy - (eheight/2);
							
						enemy_max.x = x_enemy + (ewidth/2);
						enemy_max.y = y_enemy + (eheight/2);
							
						boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
						if(hit)
						{
							bullets[i]=null;
							//score += 10;
							ebullets.setY(-3);//destroy it.
							return;
						}
					}
				}
		
	}
	
	
	//check for collision between enemy boss bullet and battleship
	public int bcollisionb(Bullet bossbullet){
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = bossbullet.getX() ;//x coordinate
		y_shot = bossbullet.getY() ;//y coordinate
		
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
		
		shot_min.x = x_shot - 5;
		shot_min.y = y_shot - 5;
		
		shot_max.x = x_shot + 5;
		shot_max.y = y_shot + 5;
	
		x_enemy = battleship.getX();//shift to rectify
		y_enemy = battleship.getY();//shift to rectify
			
		enemy_min.x = x_enemy - 12;
		enemy_min.y = y_enemy - 12;
			
		enemy_max.x = x_enemy + 12;
		enemy_max.y = y_enemy + 12;
			
		boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
			
		if(hit)
		{
			return 1;
		}
		return 2;
		
}
	
	////////Test for collision between enemy and battleship bullet 
	//test for collision btw enemy and shot and returns a boolean value.
	/////////////universe function that tests for collision between any objects
	private boolean boxCollide (vec2 bMin1, vec2 bMax1, vec2 bMin2, vec2 bMax2)
	{
		vec2 sumExtents = new vec2(0,0);
		sumExtents.x = (bMax1.x - bMin1.x) + (bMax2.x - bMin2.x);
		sumExtents.y = (bMax1.y - bMin1.y) + (bMax2.y - bMin2.y);
			
		vec2 c2c = new vec2(0,0);//centre to centre.
		c2c.x = (bMin2.x + bMax2.x) -  (bMin1.x + bMax1.x);
		c2c.y = (bMin2.y + bMax2.y) -  (bMin1.y + bMax1.y);
			
		if(Math.abs(c2c.x) < sumExtents.x)
		{
			if(Math.abs(c2c.y)<sumExtents.y)
			{
				return true;
			}
		}
		return false;
	}
	
	//returns a boolean 'hit' value test for collision between Battleship bullet and enemy 
	public void bcollision(Bullet bullet){
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = bullet.getX();//x coordinate
		y_shot = bullet.getY();//y coordinate
		//get the height and width of the bullet shot
		int height = this.battleshipshot.getHeight();
		int width = this.battleshipshot.getWidth();
		
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
		
		shot_min.x = x_shot - (width/2);
		shot_min.y = y_shot - (height/2);
		
		shot_max.x = x_shot + (width/2);
		shot_max.y = y_shot + (height/2);
		
		int eheight = enemyCrafts.getHeight();
		int ewidth = enemyCrafts.getWidth()/4;
		
		
		for(int i=0;i<this.enemies.length;i++)
		{
			if(this.enemies[i] != null){
			
			x_enemy = this.enemies[i].getX()+(eheight/2);//rectify the x position of the enemy
			y_enemy = this.enemies[i].getY()+(ewidth/2);//rectify the y position of the enemy
			//get the height and width coordinates of the enemy bitmaps
			
			
			enemy_min.x = x_enemy - (eheight/2);
			enemy_min.y = y_enemy - (ewidth/2);
			
			enemy_max.x = x_enemy + (eheight/2);
			enemy_max.y = y_enemy + (ewidth/2);
			
			int damage;
			boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
			
			if(hit)
			{
				bullet.setY(-3);//destroy it.
				damage = 1;
				int countDamage = this.enemies[i].setdamage(damage);
				int requiredDamage = this.enemies[i].requiredDamage();
				if(countDamage > requiredDamage ){
					int x,y;
					//TODO get the x and y coordinates first
					x = (int)this.enemies[i].getX();
					y = (int)this.enemies[i].getY();
					this.enemies[i]=null;
					//play the explosion sound
					if(explosionS != 0){
						sp.play(smallexplosion, 1, 1, 0, 0, 1);
					}
					//TODO: create an explosion for a few seconds
					//load the bullet hit explosions
					for(int j=0; j<explosion.length; j++){
						//create the explosion
						explosion[j] = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.bullethitl),x,y);
						explosionTime = System.currentTimeMillis();
						break;
					
					}
					if(requiredDamage >= 2){
						score += 10;
						explodescore += 10;
						this.upgraded += 10;
						this.bossScore += 10;
					}
					if(requiredDamage < 2){
						score += 5;
						explodescore += 5;
						this.upgraded += 5;
						this.bossScore += 5;
					}
					return;
				}
				
			}
			}
		}
		
	}
	
	//collision btwn Battleship bullet and boss
	public void ebcollisionb(Bullet bullet){
		//get the x and y coordinates
		float x_shot,y_shot,x_enemy,y_enemy;
		x_shot = bullet.getX();//x coordinate
		y_shot = bullet.getY();//y coordinate
		
		//int height = battleshipshot.getHeight();
		//int width = battleshipshot.getWidth();
				
		vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
		vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
				
		shot_min.x = x_shot - (5);
		shot_min.y = y_shot - (5);
				
		shot_max.x = x_shot + (5);
		shot_max.y = y_shot + (5);
		
		int eheight = enemyBoss.getHeight();
		int ewidth = enemyBoss.getWidth();
				
		for(int i=0;i<this.boss.length;i++)
		{
			if(this.boss[i] != null){
					
				x_enemy = this.boss[i].getX();
				y_enemy = this.boss[i].getY();
					
				enemy_min.x = x_enemy - (ewidth/2);
				enemy_min.y = y_enemy - (eheight/2);
					
				enemy_max.x = x_enemy + (ewidth/2);
				enemy_max.y = y_enemy + (eheight/2);
					
				int damage;
				boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
					
				if(hit)
				{
					bullet.setY(-3);//destroy it.
					damage = 1;
					int countDamage = this.boss[i].setdamage(damage);
					int requiredDamage = this.boss[i].requiredDamage();
					if(countDamage > requiredDamage ){
					int x,y;
					//TODO get the x and y coordinates first
					x = (int)this.boss[i].getX();
					y = (int)this.boss[i].getY();
					this.boss[i]=null;
					//play the explosion sound
					if(explosionS != 0){
						sp.play(smallexplosion, 1, 1, 0, 0, 1);
					}
					//TODO: create an explosion for a few seconds
					//load the bullet hit explosions
					for(int j=0; j<explosion.length; j++){
						//create the explosion
						explosion[j] = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.bullethitl),x,y);
						explosionTime = System.currentTimeMillis();
						break;
							
					}
					score += 50;
					explodescore += 60;
					this.upgraded += 60;
					this.drawBoss = false;
					return;
					}
						
					}
					}
				}
		
	}
	public void showexplosion(int x, int y){
		//TODO: create an explosion for a few seconds
		//load the bullet hit explosions
		for(int j=0; j<explosion.length; j++){
			//create the explosion
			explosion[j] = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosionhit),x,y);
			explosionTime = System.currentTimeMillis();
			break;
		
		}
	}
	
	//handle the sprite of the explosion
	public void spriteExplosion(){
		for(int i=0; i<explosion.length; i++){
			if(explosion[i] != null){
				//call the sprite		
				explosion[i].update(System.currentTimeMillis());
			}
		}
	}
	
	//display your lives
	private void displayLives(Canvas canvas, int ilives){
		String lives = this.setLives(ilives);
		if (canvas != null && lives != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText("Lives: "+lives, this.getWidth() - 100, this.screenheight - 20, paint);
		}
	}
	
	//display your score
	private void displayScore(Canvas canvas, int iscore){
		String score = this.setScore(iscore);
		if (canvas != null && score != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText("score: " + score, this.getWidth() - 100, this.screenheight - 40, paint);
		}
	}
	
	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		if(forceField == true){
			forcefield.draw(canvas,this.shield);
		}
		if(this.drawBoss == true){
			for(int b=0;b<boss.length;b++){
				if(boss[b] != null){
					boss[b].draw(canvas);
				}
			}
		}
		if(this.drawUpgrade == true){
			for(int m=0;m<upgrade.length;m++){
				if(upgrade[m] != null){
					upgrade[m].draw(canvas);
				}
			}
		}
		if(this.drawExplosion == true){
			for(int k=0;k<blast.length;k++){
				if(blast[k] != null){
					blast[k].draw(canvas);
				}
			}
		}
		//draw the stars first
		for(int z=0; z<stars.length; z++){
			stars[z].draw(canvas);
		}
		//draw the battleship
		battleship.draw(canvas);
		direction.draw(canvas);
		for(int e=0; e<explosion.length; e++){
			long current = System.currentTimeMillis();
			long timeDiff = current - this.explosionTime;
			if(explosion[e] != null){
				explosion[e].draw(canvas);
				//after 1 second destroy the explosion
				if(timeDiff > 600){
					this.explosionTime = current;
					explosion[e] = null;
				}				
					
			}
		}
		
		//draw the explosion
		for(int h=0; h<explode.length;h++){
			if(explode[h]!= null){
				explode[h].draw(canvas);
			}
		}
		for(int i=0; i<this.enemies.length; i++){
			if(this.enemies[i] != null){
				this.enemies[i].draw(canvas);
			}
		}
		displayLives(canvas, this.lives);
		displayFps(canvas, avgFps);
		displayScore(canvas, score);
		//draw the bullets
		for(int i =0; i<bullets.length ; i++){
			if(bullets[i] != null){
				bullets[i].draw(canvas);
			}
		}
		//draw the enemies bullets
		for(int i=0; i<this.enemies.length; i++){
			for(int x=0; x<this.ebullets.length; x++){
				//generate the shot
				if(this.ebullets[x] != null ){
					this.ebullets[x].edraw(canvas);
				}
			}
		}
		
		//draw the enemies bullets
		for(int i=0; i<this.boss.length; i++){
			for(int x=0; x<this.bossbullets.length; x++){
				//generate the shot
				if(this.bossbullets[x] != null ){
					this.bossbullets[x].edraw(canvas);
				}
			}
			for(int x=0; x<this.diagBossbullets.length; x++){
				if(this.diagBossbullets[x] != null){
					this.diagBossbullets[x].edraw(canvas);
				}
			}
			for(int x=0; x<this.diagPBossbullets.length; x++){
				if(this.diagPBossbullets[x] != null){
					this.diagPBossbullets[x].edraw(canvas);
				}
			}
		}
		
		if(MainGamePanel.paused == false){
			canvas.drawBitmap(this.pause, this.screenwidth/4,4, null);
		}
		
		int height = directions.getHeight()+10;
		int width = directions.getWidth()+10;
				
		// display border
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		canvas.drawLine(0, canvas.getHeight() - (height), canvas.getWidth()-1, canvas.getHeight() - (height), paint);
		canvas.drawLine(width, 0, width, this.screenheight-height, paint);
		canvas.drawLine(this.screenwidth - (width), 0, this.screenwidth - (width), this.screenheight-(height), paint);
		//canvas.drawText(this.collected, 40, 80, paint);
		
		
	}
	public void drawPause(Canvas canvas){
		canvas.drawBitmap(this.ResumeB, touchWidth , this.screenheight - touchHeight, null);
		canvas.drawBitmap(this.MainB, this.screenwidth/2+touchWidth,this.screenheight-touchHeight , null);
	}
	
	public void drawKill(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		
		//MobileCore.showStickee(WorldWar_XActivity);
		String over = "Game Over";
		if(this.score > 800){
			WorldWar_XActivity.interstitial = true;
		}
		//String lives = this.setLives(ilives);
		if (canvas != null && over != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			//canvas.drawText(over, this.screenwidth / 2 , this.screenheight / 2, paint);
			//displayLives(canvas, this.lives);
			//String score = "<font size =\"30\"><B>Bold</B></font>";
			
			
			//canvas.drawText("Hello", 0, 300, paint);
			
			
			String score = this.setScore(this.score);
			float textSize = paint.getTextSize();
			paint.setTextSize(textSize * 2);
			canvas.drawText(over, this.screenwidth / 4 , this.screenheight / 2+ this.screenwidth / 10, paint);
			canvas.drawText("Your score: "+score, this.screenwidth / 4, this.screenheight / 2 + this.screenwidth / 6, paint);
			canvas.drawText("High score: "+this.collected, this.screenwidth / 4, this.screenheight / 2 + this.screenwidth / 4, paint);
			paint.setTextSize(textSize);
		}
		//draw the return button at the bottom
		canvas.drawBitmap(menuButton, this.screenwidth / 4, this.screenheight / 2 + this.screenwidth / 2, null);
	}

}
