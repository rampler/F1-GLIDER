package com.sispd.f1racing.Enums;

/**
 * Driver skill enumeration
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public enum DriverSkill {
	MONKEY(0,10), NOVICE(1,8), INTERMEDIATE(2,6), PRO(3,4), EXPERT(4,2), MASTER(5,1);
	
	private int num;
	private double randomMistakeParameter;
	
	DriverSkill(int num, int randomMistakeParameter){ this.num = num; this.randomMistakeParameter = randomMistakeParameter; }
	public int getNum(){ return num; }
	public double getRandomMistakeParameter(){ return 1/randomMistakeParameter; }
}
