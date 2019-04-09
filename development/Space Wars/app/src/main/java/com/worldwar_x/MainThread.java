package com.worldwar_x;

import java.text.DecimalFormat;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;
import android.view.InflateException;
import android.view.SurfaceHolder;

//class to handle threading 
public class MainThread extends Thread {
	//keep a log
	static private final String TAG = MainThread.class.getSimpleName();
	
	//essential variables.
	private SurfaceHolder surfaceholder;//set up the canvas on which to create the game on.
	private MainGamePanel gamepanel; //call the main class to execute the program.
	
	//set the variables to handle FPS
	static private final int MAX_FPS = 50;
	//maximum frames to be skipped
	static private final int MAX_FRAME_SKIPS = 5;
	//frame period
	static private final int FRAME_PERIOD = 1000/ MAX_FPS;
	
	/* Stuff for stats */
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
	// we'll be reading the stats every second
	private final static int 	STAT_INTERVAL = 1000; //ms
	// the average will be calculated by storing 
	// the last n FPSs
	private final static int	FPS_HISTORY_NR = 10;
	// the status time counter
	private long statusIntervalTimer	= 0l;
	// number of frames skipped since the game started
	private long totalFramesSkipped			= 0l;
	// number of frames skipped in a store cycle (1 sec)
	private long framesSkippedPerStatCycle 	= 0l;

	// number of rendered frames in an interval
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	// the last FPS values
	private double 	fpsStore[];
	// the number of times the stat has been read
	private long 	statsCount = 0;
	// last time the status was stored
	private long lastStatusStore = 0;
	// the average FPS since the game started
	private double 	averageFps = 0.0;
	
	//set whether the thread is running ie holds the state of the game.
	private boolean running;
	public boolean setRunning(boolean running){
		return this.running = running;
	}
	
	//constructor
	public MainThread(SurfaceHolder surfaceholder, MainGamePanel gamepanel){
		
		this.surfaceholder = surfaceholder;
		this.gamepanel = gamepanel;
	}
	
	
	//while running is set to true it runs an infinite loop
	@Override
	public void run(){
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		
		initTimingElements();
		
		///to lock fps
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
		
		sleepTime = 0;
		float myFps = 20;
		
		//shot time
		int count;
		while(running){
			//load the game 
		
			canvas = null;
			try{
				
				//lock the canvas for exclusive pixel editing
				canvas = this.surfaceholder.lockCanvas();
				synchronized(surfaceholder){
					//while runGame is true
					if(MainGamePanel.runGame == true){
						this.gamepanel.Exit();
						//at the begining of the game.
						if(MainGamePanel.beginGame == true){
							this.gamepanel.begingame();
						}
					////handle fps
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					
					
					//move the stars
					this.gamepanel.move_stars();
					this.gamepanel.reset_stars();
					//update the explosion
					this.gamepanel.update_explosion();
					//sprite to the blast explasion of the enemies
					this.gamepanel.spriteExplosion();
					//move the bullet
					this.gamepanel.update_bullets();
					this.gamepanel.move_bullets(myFps);
					
					//create the enemy sprite
					//this.gamepanel.update_enemy_sprite();
					//update the game
					this.gamepanel.update_enemies();
					//move the enemies
					this.gamepanel.move_enemies();
					//make the enemies shoot
					this.gamepanel.enemies_shoot();
					this.gamepanel.ebullet_move(myFps);
					
					this.gamepanel.generate_upgrade();
					this.gamepanel.move_upgrade();
					
					this.gamepanel.destroy_force();
					//draw the battleship
					this.gamepanel.onDraw(canvas);
					
					
					
					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					myFps = 1.f / timeDiff;
					
					if(sleepTime > 0){
						//we're ok.	
						try{
							//send the thread for sleep for a short time so as to save battery
							Thread.sleep(sleepTime);
						}catch(InterruptedException e){}
						
						myFps = MAX_FPS;
					}
					
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS){
						//we need to catch up update without rendering
						//update the explosion
						this.gamepanel.update_explosion();
						//sprite to the blast explasion of the enemies
						this.gamepanel.spriteExplosion();
						this.gamepanel.update_bullets();
						this.gamepanel.move_bullets(myFps);						
						this.gamepanel.spriteExplosion();
						
						//update the game
						this.gamepanel.update_enemies();
						//create the enemy sprite
						//this.gamepanel.update_enemy_sprite();
						//move the enemies
						this.gamepanel.move_enemies();
						//make the enemies shoot
						this.gamepanel.enemies_shoot();
						this.gamepanel.ebullet_move(myFps);
						
						//draw the battleship
						this.gamepanel.onDraw(canvas);
						//add frame period to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
					// for statistics
					framesSkippedPerStatCycle += framesSkipped;
					// calling the routine to store the gathered statistics
					storeStats();
					
				}else if(MainGamePanel.runGame == false || MainGamePanel.killGame == true){
					Log.d(TAG, "Game terminated");
					this.gamepanel.drawKill(canvas);
					this.gamepanel.file();
					this.gamepanel.Exit();
					//this.gamepanel.readHigh();
				}
				if(MainGamePanel.paused){
					Log.d(TAG, "PANEL_PAUSED");
					//if game is paused draw the resume button.
					this.gamepanel.drawPause(canvas);
					if(WorldWar_XActivity.telephonecall|| WorldWar_XActivity.alarm){
						WorldWar_XActivity.alarm = false;
						this.gamepanel.callend();
						
					}
				} 
				
				}
			}
			finally{
			}
				//incase of an exception the canvas is not left in an inconsistent state
				if(canvas != null){
					surfaceholder.unlockCanvasAndPost(canvas);
				}
			}
		}
		
	

	private void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;
		// assuming that the sleep works each call to storeStats
		// happens at 1000/FPS so we just add it up
//		statusIntervalTimer += FRAME_PERIOD;
		
		// check the actual time
		statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
		
		if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
			// calculate the actual frames pers status check interval
			double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));
			
			//stores the latest fps in the array
			fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;
			
			// increase the number of times statistics was calculated
			statsCount++;
			
			double totalFps = 0.0;
			// sum up the stored fps values
			for (int i = 0; i < FPS_HISTORY_NR; i++) {
				totalFps += fpsStore[i];
			}
			
			// obtain the average
			if (statsCount < FPS_HISTORY_NR) {
				// in case of the first 10 triggers
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			// saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			// resetting the counters after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;

			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
//			Log.d(TAG, "Average FPS:" + df.format(averageFps));
			gamepanel.setAvgFps("FPS: " + df.format(averageFps));
		}
	}
	
	private void initTimingElements() {
		// initialise timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			fpsStore[i] = 0.0;
		}
		Log.d(TAG + ".initTimingElements()", "Timing elements for stats initialised");
	}
	
	
	
}
