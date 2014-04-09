package com.sispd.f1racing.POJOs;

import java.util.Arrays;

import com.sispd.f1racing.Enums.Direction;
import com.sispd.f1racing.Enums.Dryness;
import com.sispd.f1racing.Enums.SurfaceType;
import com.sispd.f1racing.Exceptions.BarrierCrashException;
import com.sispd.f1racing.Exceptions.CarStuckException;
import com.sispd.f1racing.Exceptions.CarsCollisionException;

/**
 * Track point.
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public class Point {
	
	private Point[] neighbors;
	private Direction direction;
	private SurfaceType type;
	private Car car;
	private boolean blocked;
	private final int MAX_SPEED = 302;
	private final double KERS_POWER_PARAMETER = 1.1;
	
	//Constructors
	public Point() {
		direction = Direction.NONE;
		type = SurfaceType.GRASS;
		car = null;
		blocked = false;
	}
	
	/**
	 * Next iteration of simulation
	 * @throws CarsCollisionException - collision on track between two cars
	 * @throws CarStuckException - car stack on track
	 * @throws BarrierCrashException - car hits barrier
	 */
	public void nextIteraton(Dryness trackDryness, int timerDelay, double[][] accelerationTable) throws CarsCollisionException, CarStuckException, BarrierCrashException
	{
		if(!blocked) 
		{
			//Check collision
			for(Point neighbor : neighbors) if(neighbor.isCarCenter() && neighbor.getCar().getAngle() != car.getAngle()) throw new CarsCollisionException(car, neighbor.getCar(), this, neighbor);
			
			//Calculating distance and speed in m/s
			double speed_m_s = car.getSpeed()*10/36;
			double distance = car.getTempDistance()+speed_m_s/(1000/timerDelay);
			double acc = car.getAcceleration();
			if(car.isKersActivated() && acc > 0) acc *= KERS_POWER_PARAMETER;
			
			/*Decrease KERS iterations - if not activated nothing happens
			  and actualize lap time*/
			car.nextIteration(timerDelay);
			
			//Setting random mistake
			double random = (Math.random()*car.getDriverSkills().getRandomMistakeParameter())/2;
			
			//Checking next turn and rivals ahead - from driver visibility
			Direction carActualDirection = Direction.getDirectionFromAngle(car.getAngle());
			Direction nextDirection = direction;
			if(nextDirection == Direction.NONE) nextDirection = carActualDirection;
			Direction visibilityDirection = carActualDirection;
			int rivalAheadDistance = 8, rivalAheadOffset = 8;
			
			Point[][] visibility = car.getVisibility();
			if(visibility.length == 10) //TOP,LEFT,RIGHT,BOTTOM
			{
				double[] medianTable = new double[10];
				for(int i=0; i<10; i++)
				{
					int[] table = new int[2*i+3];
					for(int j=0; j<2*i+3; j++) 
					{
						table[j] = visibility[i][j].getDirection().getNum();
						if(visibility[i][j].isCarCenter() && (rivalAheadDistance > i)) 
						{
							rivalAheadDistance = i;
							rivalAheadOffset = j-(i+1);
						}
					}
					Arrays.sort(table);
					int k=0;
					while(k < table.length-1 && table[k] == -1) k++;
					medianTable[i] = table[(k+table.length)/2];//*((5-i)/5);
				}
				double num = 0;
				int howManyZero = 0, howManyNone = 0;
				for(int i=0; i<10; i++) 
				{
					if(medianTable[i] == -1) howManyNone++;
					else if(medianTable[i] == 0) howManyZero++;
					else num += medianTable[i];
				}
				if(howManyZero < (10-howManyNone)/2) visibilityDirection = Direction.getDirectionFromNum((int) Math.round(num/(10-howManyNone)));
				else visibilityDirection = Direction.TOP_LEFT;
			}
			else //TOP_LEFT,TOP_RIGHT,BOTTOM_LEFT,BOTTOM_RIGHT
			{
				double[] medianTable = new double[12];
				for(int i=0; i<12; i++)
				{
					int[] table = new int[i+2];
					for(int j=0; j<i+2; j++)
					{
						table[j] = visibility[i][j].getDirection().getNum();
						if(visibility[i][j].isCarCenter() && (rivalAheadDistance > i)) 
						{
							rivalAheadDistance = i;
							rivalAheadOffset = Math.round(j-((i+1)*0.5f));
						}
					}
					Arrays.sort(table);
					int k=0;
					while(k < table.length-1 && table[k] == -1) k++;
					medianTable[i] = table[(k+table.length)/2];//*((7-i)/7);
				}
				double num = 0;
				int howManyZero = 0, howManyNone = 0;
				for(int i=0; i<12; i++) 	
				{
					if(medianTable[i] == -1) howManyNone++;
					else if(medianTable[i] == 0) howManyZero++;
					else num += medianTable[i];
				}
				if(howManyZero < (12-howManyNone)/2) visibilityDirection = Direction.getDirectionFromNum((int) Math.round(num/(12-howManyNone)));
				else visibilityDirection = Direction.TOP_LEFT;
			}
			
			//If rival ahead then break
			if(car.getSpeed() >=50 && !(rivalAheadOffset > 2 || (rivalAheadOffset == 0 && rivalAheadDistance > 7)))
			{
				//Calculating new acceleration - brake - mistake included
				if(car.getSpeed() <= 100) acc = accelerationTable[1][0];
				else if(car.getSpeed() <= 200) acc = accelerationTable[1][1];
				else acc = accelerationTable[1][2];
			}
			
			//Calculating new speed - mistake included
			if(acc >= 0 || car.getSpeed() >= 50)
			{
				speed_m_s += acc/(1000/timerDelay);
				if(speed_m_s < 0) speed_m_s = 0; 
				double speed_km_h = speed_m_s*36/10;
				if(speed_km_h  <= MAX_SPEED) car.setSpeed(speed_km_h);
				else car.setSpeed(MAX_SPEED-random);
			}
			
			//Stack Exception
			if(car.getSpeed() < 50 && car.getAcceleration() < 0 && direction == Direction.NONE && (type == SurfaceType.GRASS || type == SurfaceType.SAND)) throw new CarStuckException(this.car, this);
			
			//If distance is > 2.6m(point size in reality) then send car to next point
			if(distance >= 2.6) 
			{
				distance -= 2.6;
				car.setTempDistance(distance);
				
				//Add lap and KERS Percent
				if(type == SurfaceType.START_LINE) car.addLap();
				if(car.getAcceleration() < 0) car.addKersSystemPercent((-1*car.getAcceleration())/75);
				
				//If rival on left or right then change direction to make some free space
				for(int i=0; i<neighbors.length; i++)
					if(neighbors[i].isCarCenter()) nextDirection = Direction.getAvoidingDirection(i, nextDirection);
				
				//Calculating acceleration
				double newAcceleration;
				if((nextDirection.getNum()%2 == 0) //Rival on nextDirection
						&& ((nextDirection.getNum() == 0 && (neighbors[0].isCarCenter() || neighbors[7].isCarCenter() || neighbors[6].isCarCenter())) 
								|| (nextDirection.getNum() != 0 && (neighbors[nextDirection.getNum()].isCarCenter() || neighbors[nextDirection.getNum()-1].isCarCenter() || neighbors[nextDirection.getNum()-2].isCarCenter()))))
				{
					nextDirection = carActualDirection;
					//Calculating new acceleration - brake - mistake included
					if(car.getSpeed() <= 100) newAcceleration = accelerationTable[1][0]+random;
					else if(car.getSpeed() <= 200) newAcceleration = accelerationTable[1][1]+random;
					else newAcceleration = accelerationTable[1][2]+random;
				}
				else
				{
					if(visibilityDirection == Direction.getDirectionFromAngle(car.getAngle()))
					{
						//Calculating new acceleration - gas - mistake included
						if(car.getSpeed() <= 100) newAcceleration = accelerationTable[0][0]-random;
						else if(car.getSpeed() <= 200) newAcceleration = accelerationTable[0][1]-random;
						else newAcceleration = accelerationTable[0][2]-random;
						
						//Activating KERS
						if(car.getKersSystemPercent() == 100)
						{
							boolean roadAhead = true;
							int i=0;
							if(car.getVisibility().length == 5){
								int j=5;
								while(roadAhead && i<j){
									if(car.getVisibility()[i][i+1].getType() != SurfaceType.ROAD) roadAhead = false;
									i++;
								}
							}
							else{
								int j=3;
								while(roadAhead && i<j){
									if(car.getVisibility()[2*i+1][i+1].getType() != SurfaceType.ROAD) roadAhead = false;
									i++;
								}
							}
							if(roadAhead) car.activateKers(timerDelay);
						}
					}
					else if(car.getSpeed() < 200 && (Math.abs(visibilityDirection.getNum() - carActualDirection.getNum()) == 1 || Math.abs(visibilityDirection.getNum() - carActualDirection.getNum()) == 7))
					{
						//Calculating new acceleration - none
						if(car.getSpeed() <= 100) newAcceleration = accelerationTable[2][0];
						else if(car.getSpeed() <= 200) newAcceleration = accelerationTable[2][1];
						else newAcceleration = accelerationTable[2][2];
					}
					else
					{
						//Calculating new acceleration - brake - mistake included
						if(car.getSpeed() <= 100) newAcceleration = accelerationTable[1][0];
						else if(car.getSpeed() <= 200) newAcceleration = accelerationTable[1][1];
						else newAcceleration = accelerationTable[1][2];
					}
				}
				
				if(!trackDryness.toString().equals(car.getTireType().toString()) && newAcceleration > 0) newAcceleration *= car.getTireType().getAdhensionOnElse();
				else if(newAcceleration > 0) newAcceleration *= car.getTireType().getAdhensionOnSame();
				car.setAcceleration(newAcceleration*trackDryness.getAdhension()-type.getFriction());
				
				//If speed is too high car can't change direction fast
				if(!(Math.abs(nextDirection.getNum() - carActualDirection.getNum()) <= 1 || Math.abs(nextDirection.getNum() - carActualDirection.getNum()) == 7))
				{
					if(car.getSpeed() > 200) nextDirection = carActualDirection; 
					if(car.getSpeed() > 100)
					{
						if(nextDirection.getNum() < carActualDirection.getNum()) nextDirection = Direction.getDirectionFromNum(carActualDirection.getNum()-1);
						else if(carActualDirection.getNum() == 0 && nextDirection.getNum() > 5) nextDirection = Direction.getDirectionFromNum(7);
						else nextDirection = Direction.getDirectionFromNum(carActualDirection.getNum()+1);
					}
				}
				
				//Sending car to the next point
				if(neighbors[nextDirection.getNum()].getType() == SurfaceType.BARRIER) throw new BarrierCrashException(this.car, this);
				if(neighbors[nextDirection.getNum()].getCar() == null)
				{
					car.setAngle(nextDirection.getAngle());
					neighbors[nextDirection.getNum()].setCar(car);
					car = null;
				}
				else throw new CarsCollisionException(this.car, neighbors[nextDirection.getNum()].getCar(), this, neighbors[nextDirection.getNum()]);
			}
			else car.setTempDistance(distance);
		}
		else blocked = false;
	}
	
	//Getters
	public SurfaceType getType(){ return type; }
	public Direction getDirection(){ return direction; }
	public Point[] getNeighbors(){ return neighbors; }
	public Car getCar(){ return car; }
	
	//Is's
	public boolean isCarCenter(){ if(car != null) return true; return false; }
	
	//Setters
	public void setNeighbors(Point[] neighbors){ this.neighbors = neighbors; }
	
	/**
	 * Set Car and block point
	 * @param car
	 */
	public void setCar(Car car){ this.car = car; blocked = true; }
	
	public void setDirection(Direction direction){ this.direction = direction; }
	public void setDirection(String direction){ this.direction = Direction.valueOf(direction); }
	
	public void setType(SurfaceType type){ this.type = type;}
	public void setType(String type){ this.type = SurfaceType.valueOf(type);}
	
	public void unblock(){ blocked = false; }
}