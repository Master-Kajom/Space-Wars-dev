package com.worldwar_x.model;

import com.worldwar_x.MainGamePanel;
import com.worldwar_x.components.vec2;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.SoundPool;
import android.util.Log;

//an array of particles so as to create the explosion.
public class Explode {
private static final String TAG = Explosion.class.getSimpleName();
	
	public static final int STATE_ALIVE 	= 0;	// at least 1 particle is alive
	public static final int STATE_DEAD 		= 1;	// all particles are dead
	MainGamePanel gamepanel;
	
	private Particles[] particles;			// particles in the explosion
	private int x, y;						// the explosion's origin
	private float gravity;					// the gravity of the explosion (+ upward, - down)
	private float wind;						// speed of wind on horizontal
	private int size;						// number of particles
	private int state;						// whether it's still active or not
	
	private long gameTime;
	
	public Explode(float screenheight,float screenwidth,int particleNr, int x, int y, MainGamePanel gamepanel) {
		Log.d(TAG, "Explosion created at " + x + "," + y);
		this.state = STATE_ALIVE;
		this.particles = new Particles[particleNr];
	 	for (int i = 0; i < this.particles.length; i++) {
			Particles p = new Particles(screenheight,screenwidth,x, y);
			this.particles[i] = p;
		}
	 	this.size = particleNr;
	 	this.gameTime = System.currentTimeMillis();
	 	this.gamepanel = gamepanel;
	}
	
	public Particles[] getParticles() {
		return particles;
	}
	public void setParticles(Particles[] particles) {
		this.particles = particles;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public float getGravity() {
		return gravity;
	}
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}
	public float getWind() {
		return wind;
	}
	public void setWind(float wind) {
		this.wind = wind;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	// helper methods -------------------------
	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}
	public boolean isDead() {
		return this.state == STATE_DEAD;
	}

	public void update() {
		/*if (this.state != STATE_DEAD) {
			boolean isDead = true;
			for (int i = 0; i < this.particles.length; i++) {
				if (this.particles[i].isAlive()) {
					this.particles[i].update();
					isDead = false;
				}
			}
			if (isDead)
				this.state = STATE_DEAD; 
		}*/
	}
	
	
/////////////universe function that tests for collision between any objects
private boolean boxCollide (vec2 bMin1, vec2 bMax1, vec2 bMin2, vec2 bMax2)
	{
		vec2 sumExtents = new vec2(0,0);
		sumExtents.x = (bMax1.x - bMin1.x) + (bMax2.x - bMin2.x);
		sumExtents.y = (bMax1.y - bMin1.y) + (bMax2.y - bMin2.y);
			
		vec2 c2c = new vec2(0,0);//centre to centre.
		c2c.x = (bMin2.x + bMax2.x) -  (bMin1.x + bMax1.x);
		c2c.y = (bMin2.y + bMax2.y) -  (bMin1.y + bMax1.y);
			
		if(Math.abs(c2c.x) < sumExtents.x)
		{
			if(Math.abs(c2c.y)<sumExtents.y)
			{
				return true;
			}
		}
		return false;
	}

//returns a boolean 'hit' value test for collision between Battleship bullet and enemy 
	//@SuppressWarnings("unused")
	@SuppressWarnings("unused")
	public int bcollision(Particles particles, Enemy[] enemies,int score, int explosionS, SoundPool sp){
			//get the x and y coordinates
			float x_shot,y_shot,x_enemy,y_enemy;
			x_shot = particles.getX();//x coordinate
			y_shot = particles.getY();//y coordinate
			
			vec2 shot_min = new vec2(0,0), shot_max = new vec2(0,0);
			vec2 enemy_min = new vec2(0,0), enemy_max = new vec2(0,0);
			
			shot_min.x = x_shot - 5;
			shot_min.y = y_shot - 5;
			
			shot_max.x = x_shot + 5;
			shot_max.y = y_shot + 5;
			
			for(int i=0;i<enemies.length;i++)
			{
				if(enemies[i] != null){
				
				x_enemy = enemies[i].getX();
				y_enemy = enemies[i].getY();
				
				enemy_min.x = x_enemy - 15;
				enemy_min.y = y_enemy - 15;
				
				enemy_max.x = x_enemy + 15;
				enemy_max.y = y_enemy + 15;
				
				boolean hit = boxCollide(shot_min, shot_max, enemy_min, enemy_max);
				
				if(hit)
				{
					int x,y;
					//TODO get the x and y coordinates first
					x = (int)enemies[i].getX();
					y = (int)enemies[i].getY();
					//TODO pass score as an arguement from class maingamepanel
					//TODO pass the explosion sprite when enemy is hit.
					this.particles[i].isDead();
					enemies[i] = null;
					//create the explosion
					if(explosionS != 0){
						sp.play(explosionS, 1, 1, 0, 0, 1);
					}
					
					this.gamepanel.showexplosion(x, y);
					score = score + 10;
					return score;
				}
				}
				//return;
			}
			return score;
	}
	
	public int update(Rect container,Enemy[] enemies,int score,int explosionS, SoundPool sp) {
		if (this.state != STATE_DEAD) {
			boolean isDead = true;
			for (int i = 0; i < this.particles.length; i++) {
				if (this.particles[i].isAlive()) {
					this.particles[i].update(container);
//					this.particles[i].update();
					score = bcollision(particles[i],enemies,score,explosionS,sp);
					isDead = false;				
				}
			}
			if (isDead)
				this.state = STATE_DEAD; 
		}
		
		return score;
	}

	public void draw(Canvas canvas) {
		for(int i = 0; i < this.particles.length; i++) {
			if (this.particles[i].isAlive()) {
				this.particles[i].draw(canvas);
			}
		}
	}
}