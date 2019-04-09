package com.worldwar_x.model;

import com.worldwar_x.WorldWar_XActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//class to create and handle boss enemy
public class Boss {
	
	float x;
	float y;
	Bitmap bitmap;
	Paint paint;
	float xv;
	float yv;
	int screenwidth;
	int screenheight;
	int damage;
	int required;
	
	public Boss(float x, float y, Bitmap bitmap,int screenwidth,int screenheight,int damage){
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
		this.screenwidth = screenwidth;
		this.screenheight = screenheight;
		this.yv = 0;
		this.xv = 0.1f;
		this.damage = 0;
		this.required = damage;
	}
	
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
	public void update(){
		// move the boss.
		double vel = 20.0/25;
		vel *= WorldWar_XActivity.density_factor;
		this.x += xv*vel*10.0;
		//this.x += xv;
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
	
	public void boundary(){
		if(this.y < 15){ 
			if(this.x >= this.screenwidth-50 || this.x <= 1){
				this.xv *= -1;
			}
			if(this.y >= this.screenheight - 70){
				this.yv *= -1;
			}
		}else{
			//move the enemy diagonally and bounce when they hit the boundary
			if(this.x >= this.screenwidth-50 || this.x <= 1){
				this.xv *= -1;
				this.yv *= -1;
			}
			if(this.y >= this.screenheight - 70){
				this.yv *= -1;
				this.xv *= -1;
			}			
		}
		
		update();
	}
	
	//create class to let the enemy generate a shoot.
	public Bullet generate_shot(Bitmap bitmap,double xv,double yv){
		Bullet shoot = new Bullet(getX(),getY()+10,bitmap,xv,yv);
		return shoot;
	}
	
	public void draw(Canvas canvas){
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
	}

}
