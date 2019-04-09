package com.worldwar_x.model;

import com.worldwar_x.WorldWar_XActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Upgrade {
	
	private float x;
	private float y;
	Bitmap bitmap;
	private double xv;
	private double yv;
	Paint paint;
	
	public Upgrade(float x,float y,Bitmap bitmap){
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
		this.yv = 1;
		this.xv = 0;
	}
	
	//get and set the x and y coordinates
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
	
	//move the upgrade thing 
	public void move(){
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.y += 0.075*vel*10.0;
		//this.y += (float) this.yv;
	}
	
	public void draw(Canvas canvas){
		//draw the upgrade
		canvas.drawBitmap(this.bitmap, x - (this.bitmap.getWidth() / 2), y - (this.bitmap.getHeight() / 2), null);
	}

}
