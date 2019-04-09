package com.worldwar_x.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//its a rectangle around the battleship
public class ForceField {
	
	Bitmap bitmap;
	float x;
	float y;
	int radius;
	Paint paint;
	float height;
	float width;
	
	public ForceField(float height,float width,float x, float y, int radius,Bitmap bitmap){
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.height  = (bitmap.getHeight()/2)+20;
		this.width = (bitmap.getWidth()/2)+20;
		this.bitmap = bitmap;
		
	}
	//get the set the coordinates of x and y
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

	
	public void draw(Canvas canvas,Paint paint){
		//canvas.drawRect(this.x, this.y,this.x + this.radius, this.y + this.radius, paint);
		canvas.drawLine(this.x-this.width, this.y-this.height, this.x-this.width, this.y+this.height, paint);
		canvas.drawLine(this.x-this.width, this.y+this.height, this.x+this.width, this.y+this.height, paint);
		canvas.drawLine(this.x+this.width, this.y+this.height, this.x+this.width, this.y-this.height, paint);
		canvas.drawLine(this.x+this.width, this.y-this.height, this.x-this.width, this.y-this.height, paint);
		
	}

}
