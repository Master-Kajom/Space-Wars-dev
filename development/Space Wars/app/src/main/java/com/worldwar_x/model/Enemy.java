package com.worldwar_x.model;

import com.worldwar_x.WorldWar_XActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

//produce an enemy
public class Enemy {
	
	Paint paint;
	private Bitmap bitmap;// picture of the enemy(later going to be a sprite class)
	private Rect sourceRect;//rectangle to contain the animation bitmap.
	private int frameNr;// number of frames in the animation.
	private int currentFrame;// the current frame(position)
	private long frameTicker;// The time of the last frame update;
	private int framePeriod;// milliseconds between each frame(1000/fps);
	
	private int spriteWidth;//width of the rectangle to cut of the original bitmap
	private int spriteHeight;// height of the rectangle to cut of the original bitmap
	
	
	private float x;//the x coordinate
	private float y;//the y coordinate
	
	public final float MAX_SPEED ;
	private double xv;
	private double yv;
	private int damage;
	private int required;
	
	private int screenWidth;
	private int screenHeight;
	
	private int height;
	private int width;
	
	//calculate speed randomly.
	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	//constructor
	public Enemy(int height,int width,Bitmap bitmap,float x, float y,int screenWidth, int screenHeight, float MAX_SPEED, int damage){
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
		this.MAX_SPEED = MAX_SPEED;
		
		this.currentFrame = 0;
		this.frameNr = 4;
		spriteWidth = bitmap.getWidth() / 4;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0,0,spriteWidth,spriteHeight);
		framePeriod = 1000 / 4 ; //fps is taken to be 5.
		frameTicker = 0l;
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.xv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		this.yv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		
		
		if(this.yv < 0){
			//make it be greator than zero
			this.yv *= -1;
		}
		// smoothing out the diagonal speed
		if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
			xv *= 0.7;
			yv *= 0.7;
		}
		this.required= damage;
		this.damage = 0;
		
		this.height = height+3;
		this.width = width+3;
	}
	///////set and get the bitmap
	public Bitmap getBitmap(){
		return this.bitmap;
	}
	
	
	public int getRequired(){
		return this.required;
	}
	//////set and get the coordinates of the enemy position
	public float getX(){
		return this.x;
	}
	public void setX(float x){
		this.x = x;
	}
	public float getY(){
		return this.y;
	}
	public void setY(float y){
		this.y = y;
	}
	
	//move the enemies.
	public void update(){
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.x += this.xv*vel*10.0;
		this.y += this.yv*vel*10.0;
	}
	
	//handle the sprite of the enemies
	public void update(long gameTime){
		if(gameTime > frameTicker + framePeriod){
			frameTicker = gameTime;
			//increment the frame
			currentFrame++;
			if(currentFrame >= frameNr){
				//reset
				currentFrame = 0;
			}
		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left  + spriteWidth;
	}
	
	//check if enemy has reach boundary
	public void boundary(long enemyTime,long gameTime){
		
		long current = System.currentTimeMillis();
		long diff = current - gameTime;
		//using gameTime control rate of speed.
		if(diff > 41000){
			gameTime = current;
			//TODO: after 5 seconds start increasing the speed
			long t = System.currentTimeMillis();
			long elapsed = t - enemyTime;
			if(elapsed > 3000){
				enemyTime = t;
				//if speeds are greater than 4 reset to 0.5
				if(this.xv > 0.3){
					this.xv = 0.3;				
				}
				if(this.yv > 0.25){
					this.yv = 0.25;
				}
				//if it's positive increase value by 0.1
				if(this.xv < 0){
					this.xv -= 0.001;
				}else{
					this.xv += 0.001;
				}
				if(this.yv < 0){
					this.yv -= 0.0001;
				}else{
					this.yv += 0.0001;
				}
				
			}
		
		}
		
		if(this.y < 15){ 
			if(this.x >= this.screenWidth-(width*2) || this.x <= (width)){
				this.xv *= -1;
			}
			if(this.y >= this.screenHeight - (height*2)){
				this.yv *= -1;
			}
		}else{
			//move the enemy diagonally and bounce when they hit the boundary
			if(this.x >= this.screenWidth-(width*2) || this.x <= (width)){
				this.xv *= -1;
				//this.yv *= -1;
			}
			if(this.y >= this.screenHeight - (height*2)){
				this.yv *= -1;
				//this.xv *= -1;
			}		
		}
		
		update();
	}
	//get damage
	public int getdamge(){
		return this.damage;
	}
	public int requiredDamage(){
		return this.required;
	}
	
	//check if enemy has been hit twice.
	public int setdamage(int damage){
		this.damage += damage;
		return this.damage;
	}
	
	
	//create class to let the enemy generate a shoot.
	public Bullet generate_shot(Bitmap bitmap,double xv,double yv){
		Bullet shoot = new Bullet(getX(),getY(),bitmap,xv,yv);
		return shoot;
	}
	
	//draw the enemy on canvas
	public void draw(Canvas canvas){
		//canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getHeight() / 2, null);
		// where to draw the sprite
		Rect destRect = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX() + this.spriteWidth,(int) this.getY() + this.spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
		
	}

}
