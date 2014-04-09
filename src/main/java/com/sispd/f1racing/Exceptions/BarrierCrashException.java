package com.sispd.f1racing.Exceptions;

import com.sispd.f1racing.POJOs.Car;
import com.sispd.f1racing.POJOs.Point;

/**
 * Exception thrown when car hits barrier
 * Contain broken car
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public class BarrierCrashException extends Exception {

	private static final long serialVersionUID = 1L;
	private Car car;
	private Point point;
	
	public BarrierCrashException(Car car, Point point) {
		this.car = car;
		this.point = point;
	}
	
	public Car getCar(){ return car; }
	public Point getPoint(){ return point; }
}
