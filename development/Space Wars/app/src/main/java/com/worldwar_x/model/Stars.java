package com.worldwar_x.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Stars {
	
	private float x;
	private float y;
	private float y_speed;
	private int no_of_stars, screenheight , screenwidth, radius;
	Paint paint;
	
	//the constructor
	public Stars(float x, float y, float y_speed,int no_of_stars, int screenheight, int screenwidth, int radius){
		this.x = x;
		this.y = y;
		this.y_speed = y_speed;
		this.no_of_stars = no_of_stars;
		this.screenheight = screenheight;
		this.screenwidth = screenwidth;
		this.radius = radius;
		
	}
	
	//get and set the positions of x and y coordinates
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
	
	//TODO: move the stars 
	public void move_star(){
		this.y += this.y_speed;
	}
	//reset the position of the stars
	public void reset_star(){
		//reset the stars
		if(this.y > this.screenheight-50){
			//set it back to its original position
			this.y -= screenheight-50;
		}
	}
	
	
	//draw the stars.
	public void draw(Canvas canvas){
		paint = new Paint();
		paint.setColor(Color.WHITE);
		
		canvas.drawRect(this.x, this.y, this.x+this.radius, this.y+radius, paint);
	}

}
