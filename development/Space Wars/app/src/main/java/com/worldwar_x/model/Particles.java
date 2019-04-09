package com.worldwar_x.model;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

//class to generate particles for the explosion power
public class Particles {
	
	public static final int STATE_ALIVE = 0;	// particle is alive
	public static final int STATE_DEAD = 1;		// particle is dead
	
	public static final int DEFAULT_LIFETIME 	= 200;	// play with this
	public static final int MAX_DIMENSION		= 2;	// the maximum width or height
	public static final int MAX_SPEED			= 3;	// maximum speed (per update)
	
	int max_x,min_x,max_y,min_y;
	
	private int state;			// particle is alive or dead
	private float widht;		// width of the particle
	private float height;		// height of the particle
	private float x, y;			// horizontal and vertical position
	private double xv, yv;		// vertical and horizontal velocity
	private int age;			// current age of the particle
	private int lifetime;		// particle dies when it reaches this value
	private int color;			// the color of the particle
	private Paint paint;		// internal use to avoid instantiation
	private long gameTime;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getWidht() {
		return widht;
	}

	public void setWidht(float widht) {
		this.widht = widht;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public double getXv() {
		return xv;
	}

	public void setXv(double xv) {
		this.xv = xv;
	}

	public double getYv() {
		return yv;
	}

	public void setYv(double yv) {
		this.yv = yv;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	// helper methods -------------------------
	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}
	public boolean isDead() {
		return this.state == STATE_DEAD;
	}

	public Particles(float screenheight,float screenwidth,int x, int y) {
		this.x = x;
		this.y = y;
		this.state = Particles.STATE_ALIVE;
		this.widht = rndInt(1, MAX_DIMENSION);
		this.height = this.widht;
//		this.height = rnd(1, MAX_DIMENSION);
		this.lifetime = DEFAULT_LIFETIME;
		this.gameTime = System.currentTimeMillis();
		this.age = 0;
		this.xv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		this.yv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		//this.xv = 0.5;
		//this.yv = 0.5;
		// smoothing out the diagonal speed
		if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
			xv *= 0.7;
			yv *= 0.7;
		}
		
		max_x = (int) (this.x + screenwidth/4);
		min_x = (int) (this.x - screenwidth/4);
		max_y = (int) (this.y + screenheight/4);
		min_y = (int) (this.y - screenheight/4);
		
		if(min_x < 0){
			min_x = 0;
		}
		if(min_y < 0){
			min_y = 0;
		}
		
		this.color = Color.argb(255, rndInt(0, 255), rndInt(0, 255), rndInt(0, 255));
		this.paint = new Paint(this.color);
	}
	
	/**
	 * Resets the particle
	 * @param x
	 * @param y
	 */
	public void reset(float x, float y) {
		this.state = Particles.STATE_ALIVE;
		this.x = x;
		this.y = y;
		this.age = 0;
	}

	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	public void update() {	
		
		if (this.state != STATE_DEAD) {
			long currentTime = System.currentTimeMillis();
			long timeDiff = currentTime - gameTime;
			this.x += this.xv;
			this.y += this.yv;
			
			//if 100steps are travelled kill the particle
			if(this.getX() >= this.max_x || this.getX() <= this.min_x || this.getY() >= this.max_y || this.getY() <= this.min_y ){
				this.state = STATE_DEAD;
			}
			
			if (this.age >= this.lifetime) {	// reached the end if its life
				this.state = STATE_DEAD;
			}
			
		}
	}
	
	public void update(Rect container) {
		// update with collision
		if (this.isAlive()) {
			if (this.x <= container.left || this.x >= container.right - this.widht) {
				this.xv *= -1;
			}
			// Bottom is 480 and top is 0 !!!
			if (this.y <= container.top || this.y >= container.bottom - this.height) {
				this.yv *= -1;
			}
		}
		update();
	}

	public void draw(Canvas canvas) {
//		paint.setARGB(255, 128, 255, 50);
		paint.setColor(this.color);
		canvas.drawRect(this.x, this.y, this.x + this.widht, this.y + this.height, paint);
//		canvas.drawCircle(x, y, widht, paint);
	}

}
