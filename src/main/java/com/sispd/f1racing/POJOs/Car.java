package com.sispd.f1racing.POJOs;

import com.sispd.f1racing.Enums.DriverSkill;
import com.sispd.f1racing.Enums.Tire;

/**
 * Car class contains all car parameters.
 * @author Sabina Rydzek, Kacper Furma�ski, Mateusz Kotlarz
 *
 */
public class Car {

	private final int POS_START_X, POS_START_Y;
    private final Driver DRIVER;
	private final int NUMBER;
	private int angle;
	private double kersSystemPercent;
	private int kersIterations = 0;
	private int laps;
	private long actualLapTime;
	private long bestLapTime;
	private double acceleration;
	private double speed;
	private double tempDistance;
	private Tire tireType;
	private Point[][] visibility;
	
	/**
	 * Setting up initial parameters
	 * @param driver
	 * @param posStartX
	 * @param posStartY
	 */
	public Car(Driver driver, int posStartX, int posStartY, int number, double acceleration)
	{
		DRIVER = driver;
		POS_START_X  = posStartX;
		POS_START_Y = posStartY;
		NUMBER = number;
		tireType = Tire.DRY;
		kersSystemPercent = 0;
		speed = 0;
		this.acceleration = acceleration;
		laps = 0;
		angle = 0;
		tempDistance = 0;
		actualLapTime = 0;
		bestLapTime = 1000000;
	}
	
	//Getters
	public int getKersSystemPercent(){ return (int) kersSystemPercent; }
	public int getLaps(){ return laps; }
	public int getPosStartX(){ return POS_START_X; }
	public int getPosStartY(){ return POS_START_Y; }
	public int getAngle(){ return angle; }
	public int getNumber(){ return NUMBER; }
	public long getActualLapTime(){ return actualLapTime; }
	public long getBestLapTime(){ return bestLapTime; }
	public double getAcceleration(){ return acceleration; }
	public double getSpeed(){ return speed; }
	public double getTempDistance(){ return tempDistance; }
	public String getDriverName(){ return DRIVER.getName(); }
	public Tire getTireType(){ return tireType; }
	public DriverSkill getDriverSkills(){ return DRIVER.getDriverSkill(); }
	public Point[][] getVisibility(){ return visibility; }
    public Driver getDriver(){ return DRIVER; }
	
	//Is's
	public boolean isKersActivated(){ if(kersIterations > 0) return true; return false; }
	
	//Setters
	public void setSpeed(double speed){ this.speed = speed; }
	public void setAcceleration(double acceleration){ this.acceleration = acceleration; }
	public void setTireType(Tire tireType){ this.tireType = tireType; }
	public void setAngle(int angle){ this.angle = angle; }
	public void setVisibility(Point[][] visibility){ this.visibility = visibility; }
	public void setTempDistance(double tempDistance){ this.tempDistance = tempDistance; }
	
	/**
	 * Add percent to KERS Progress
	 * @param percent
	 */
	public void addKersSystemPercent(double percent){ 
		if(this.kersSystemPercent+percent < 100 && percent >= 0) this.kersSystemPercent += percent; 
		else this.kersSystemPercent = 100;
	}
	
	/**
	 * If car starts new lap, add lap and check bestLapTime
	 */
	public void addLap()
	{ 
		this.laps++; 
		if(actualLapTime < bestLapTime && laps != 1) bestLapTime = actualLapTime;
		actualLapTime = 0;
	}
	
	/**
	 * Activate KERS for 6,7s
	 * KERS Percent Progress change from 100% to 0%
	 */
	public void activateKers(int timerDelay){ kersIterations = 6700/timerDelay; kersSystemPercent = 0; }
	
	/**
	 * Decrease KERS iterations and actualize Lap Time
	 */
	public void nextIteration(int timerDelay)
	{ 
		if(kersIterations > 0) kersIterations--;
		actualLapTime += timerDelay;
	}
}
