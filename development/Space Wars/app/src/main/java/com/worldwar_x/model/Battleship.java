package com.worldwar_x.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//class to create and handle the battleship
public class Battleship {
	//essential variables
	Paint paint;
	private Bitmap bitmap; // the battleship picture
	private int x; //get the x coordinate
	private int y; //get the y coordinate
	private boolean touched;//checks if battleship is dragged to a new position.
	
	//constructor
	public Battleship(Bitmap bitmap, int x, int y){
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}
	/////set the values of x and y coordinates
	public void setX(int x){
		this.x = x;
	}
	public float getX(){
		return this.x;
	}
	public void setY(int y){
		this.y = y;
	}
	public float getY(){
		return this.y;
	}
	
	////check if battleship is touched
	public boolean Touched(){
		return this.touched;
	}
	public void setTouched(boolean touched){
		this.touched = touched;
	}
	
	public Bullet generateBullet(Bitmap bitmap,double xv,double yv){
		Bullet bullet = new Bullet(getX(),getY(),bitmap,xv,yv);
		return bullet;
	}
	
	public void handleActionDown(int eventX, int eventY){
		if(eventX >= (x - this.bitmap.getWidth()/2) && eventX <= (x + this.bitmap.getWidth())){
			if(eventY >= (y - this.bitmap.getHeight()/2) && eventY <= (y + this.bitmap.getHeight())){
				//the battleship is touched
				setTouched(true);
			}else{
				setTouched(false);
			}
		}else{
			setTouched(false);
		}
	}
	
	//move battleship according to direction given
	public void setdirection(int direction){
		this.x += direction; 
	}
	
	public void draw(Canvas canvas){
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		
	}

}
