package com.worldwar_x.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Explosion {
	
	private Rect sourceRect;//rectangle to contain the animation bitmap.
	private int frameNr;// number of frames in the animation.
	private int currentFrame;// the current frame(position)
	private long frameTicker;// The time of the last frame update;
	private int framePeriod;// milliseconds between each frame(1000/fps);
	
	private int spriteWidth;//width of the rectangle to cut of the original bitmap
	private int spriteHeight;// height of the rectangle to cut of the original bitmap
	
	int x;
	int y;
	Bitmap bitmap;
	
	public Explosion(Bitmap bitmap,int x, int y){
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		
		this.currentFrame = 0;
		this.frameNr = 4;
		spriteWidth = bitmap.getWidth() / 4;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0,0,spriteWidth,spriteHeight);
		framePeriod = 1000 / 4 ; //fps is taken to be 5.
		frameTicker = 0l;
		
		
	}
	
	//method to handle the sprite explosion
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
	
	//set and get the x and y coordinates.
	public int getY(){
		return this.y;
	}
	public void setY(int y){
		this.y = y;
	}
	public int getX(){
		return this.x;
	}
	public void setX(int x){
		this.x = x;
	}
	
	
	//draw
	public void draw(Canvas canvas){
		//canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getHeight() / 2, null);
		// where to draw the sprite
		Rect destRect = new Rect((int)this.getX(),(int)this.getY(),(int)this.getX() + this.spriteWidth,(int) this.getY() + this.spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
	}

}
