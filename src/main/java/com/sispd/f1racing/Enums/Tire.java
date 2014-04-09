package com.sispd.f1racing.Enums;

/**
 * Tire types with adhesion
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public enum Tire {
	DRY(0.9,0.7), WET(0.85,0.8);
	
	private double adhesionOnSame, adhensionOnElse;
	Tire(double adhesionOnSame, double adhensionOnElse)
	{ 
		this.adhesionOnSame = adhesionOnSame; 
		this.adhensionOnElse = adhensionOnElse; 
	}
	
	public double getAdhensionOnSame(){ return adhesionOnSame; }
	public double getAdhensionOnElse(){ return adhensionOnElse; }
}
