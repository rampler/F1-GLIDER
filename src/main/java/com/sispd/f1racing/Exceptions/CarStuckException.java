package com.sispd.f1racing.Exceptions;

import com.sispd.f1racing.POJOs.Car;
import com.sispd.f1racing.POJOs.Point;

/**
 * Exception thrown when car stack (speed=0 and acceleration < 0)
 * Contain stacked car
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public class CarStuckException extends Exception {

	private static final long serialVersionUID = 1L;
	private Car car;
	private Point point;
	
	public CarStuckException(Car car, Point point) { 
		this.car = car;
		this.point = point;
	}
	
	public Car getCar(){ return car; }
	public Point getPoint(){ return point; }
}
