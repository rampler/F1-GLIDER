package com.sispd.f1racing.Exceptions;

import com.sispd.f1racing.POJOs.Car;
import com.sispd.f1racing.POJOs.Point;

/**
 * Exception thrown when there were collisions on track
 * Contain two broken cars
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public class CarsCollisionException extends Exception {

	private static final long serialVersionUID = 1L;
	private Car car, car2;
	private Point point1, point2;
	
	public CarsCollisionException(Car car, Car car2, Point point, Point neighbor)
	{
		this.car = car;
		this.car2 = car2;
		this.point1 = point;
		this.point2 = neighbor;
	}
	
	public Car getCar1(){ return car; }
	public Car getCar2(){ return car2; }
	public Point getPoint1(){ return point1; }
	public Point getPoint2(){ return point2; }
}
