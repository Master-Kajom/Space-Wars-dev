package com.worldwar_x.model;

import com.worldwar_x.WorldWar_XActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//class to generate a bullet
public class Bullet {
	
	private float x; // the x coordinate of a bullet
	private float y; // the y coordinate of a bullet
	private Paint paint;
	private double xv;
	private double yv;
	Bitmap bitmap;
	
	//constructor
	public Bullet(float x, float y,Bitmap bitmap,double xv,double yv){
		this.x = x;
		this.y = y;
		this.xv = xv;
		this.yv = yv;
		this.bitmap = bitmap;
	}
	//set and get the value of velocity
	public double getYV(){
		return this.yv;
	}
	public void setYV(double yv){
		this.yv = yv;
	}
	public double getXV(){
		return this.xv;
	}
	public void setXV(double xv){
		this.xv = xv;
	}
	
	//set and get the bullet coordinates
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
	
	public void diagPBoss(double avgFps){
		//if(avgFps < 5.0) avgFps = 5.0;
		//if(avgFps > 50) avgFps = 50.0;
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.y += 0.1*vel*10.0;
		this.x += 0.01*vel*10.0;
	}
	
	public void diagNBoss(double avgFps){
		//if(avgFps < 5.0) avgFps = 5.0;
		//if(avgFps > 50) avgFps = 50.0;
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.y += 0.1*vel*10 ;
		this.x -= 0.01*vel*10;
	}
	
	public void moveBoss(double avgFps){
		//this.y += 1;
		//if(avgFps < 5.0) avgFps = 5.0;
		//if(avgFps > 50) avgFps = 50.0;
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.y += 0.2*vel*10.0;
		double xp = (double)y * 0.5*vel*10.0;
		x = (int)(Math.sin(xp) * 5) + x;
	}
	
	//move the bullet upwards
	public void move(double avgFps){
		double vel = 20.0/25;
		if(yv != 0){
			this.y -= yv*vel*10.0;	
		}else{
			this.x += xv*vel*10.0;
		}
		if(xv < 0 && yv != 0){
			//move both the x and y.
			this.x += xv*vel*10.0;
			this.y -= yv*vel*10.0;
		}
		if(xv > 0 && yv != 0){
			//move both the x and y.
			this.x += xv*vel*10.0;
			this.y -= yv*vel*10.0;
		}
		
	}
	//move the the bullets downwards
	public void move_down(double avgFps){
		//if(avgFps < 5.0) avgFps = 5.0;
		//if(avgFps > 50) avgFps = 50.0;
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.y += 0.023*vel*10.0;
		
	}
	
	public void draw(Canvas canvas){
		//instead of paint use a bitmap to represent the bullet.
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		
	}
	//draw the enemy bullets
	public void edraw(Canvas canvas){
		//instead of paint use a bitmap to represent the bullet.
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
	}

}
