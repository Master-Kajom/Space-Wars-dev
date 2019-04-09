package com.worldwar_x.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//class to create the direction pointers left and right 
public class Dpointers {
	
	int height,width;
	int x;
	int y;
	int screenWidth;
	int screenHeight;
	Bitmap bLeft;
	Bitmap bRight;
	Bitmap bUp;
	Bitmap bDown,lDdiagonal,lUdiagonal,rDdiagonal,rUdiagonal;
	
	public int getX(){
		return this.x;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public void setY(int y){
		this.y = y;
	}
	//constructor
	public Dpointers(int screenWidth,int screenHeight,Bitmap bLeft, Bitmap bRight, Bitmap bUp, Bitmap bDown,
			Bitmap lDdiagonal,Bitmap lUdiagonal,Bitmap rDdiagonal,Bitmap rUdiagonal){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.bLeft = bLeft;
		this.bRight = bRight;
		this.bUp = bUp;
		this.bDown = bDown;
		this.lDdiagonal = lDdiagonal;
		this.lUdiagonal = lUdiagonal;
		this.rDdiagonal = rDdiagonal;
		this.rUdiagonal = rUdiagonal;
		this.height = this.bLeft.getHeight();
		this.width = this.bLeft.getWidth();
		
	}
	
	//move the battleship 
	public void move(){
		
	}
	
	public void draw(Canvas canvas){
		
		canvas.drawBitmap(this.bRight, 0, this.screenHeight / 2 - this.height, null);
		canvas.drawBitmap(this.bLeft, this.screenWidth - this.width, this.screenHeight / 2 - this.height, null);
		canvas.drawBitmap(this.bUp, this.screenWidth / 2, 4, null);
		canvas.drawBitmap(this.bDown, this.screenWidth / 2, this.screenHeight - height, null);
		
		//draw the diagonal direction buttons
		canvas.drawBitmap(this.lUdiagonal, 0, 4, null);//top left
		canvas.drawBitmap(this.rUdiagonal, this.screenWidth - this.width, 4, null);//top right
		canvas.drawBitmap(this.lDdiagonal, 0, this.screenHeight - this.height, null);//bottom left
		canvas.drawBitmap(this.rDdiagonal, this.screenWidth - this.width, this.screenHeight - this.height, null);//bottom right
		
	}

}
