package com.sispd.f1racing.Enums;

/**
 * Direction contain unique representation number.
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
 *
 */
public enum Direction {
	NONE(-1,0), TOP_LEFT(0,315), TOP(1,0), TOP_RIGHT(2,45), LEFT(7,270), RIGHT(3,90), BOTTOM_LEFT(6,225), BOTTOM(5,180), BOTTOM_RIGHT(4,135);
	
	private int num;
	private int angle;
	
	Direction(int num, int angle){ this.num = num; this.angle = angle;}
	public int getNum(){ return num; }
	public int getAngle(){ return angle; }
	/**
	 * Get Direction form car angle
	 * @param angle - car angle
	 * @return - Direction
	 */
	public static Direction getDirectionFromAngle(int angle)
	{
		if(angle < 45) return TOP;
		if(angle < 90) return TOP_RIGHT;
		if(angle < 135) return RIGHT;
		if(angle < 180) return BOTTOM_RIGHT;
		if(angle < 225) return BOTTOM;
		if(angle < 270) return BOTTOM_LEFT;
		if(angle < 315) return LEFT;
		if(angle < 360) return TOP_LEFT;
		return NONE;
	}
	/**
	 * Get Direction form his number
	 * @param num - number in array
	 * @return - Direction
	 */
	public static Direction getDirectionFromNum(int num)
	{
		if(num == 0) return TOP_LEFT;
		if(num == 1) return TOP;
		if(num == 2) return TOP_RIGHT;
		if(num == 3) return RIGHT;
		if(num == 4) return BOTTOM_RIGHT;
		if(num == 5) return BOTTOM;
		if(num == 6) return BOTTOM_LEFT;
		if(num == 7) return LEFT;
		return NONE;
	}
	/**
	 * Get avoiding rival car Direction
	 * @param neighborNum
	 * @param nextDirection
	 * @return Direction
	 */
	public static Direction getAvoidingDirection(int neighborNum, Direction nextDirection)
	{
		if(nextDirection == TOP && (neighborNum == 0 || neighborNum == 7 || neighborNum == 6)) return TOP_RIGHT;
		if(nextDirection == TOP && (neighborNum == 2 || neighborNum == 3 || neighborNum == 4)) return TOP_LEFT;
		if(nextDirection == TOP_RIGHT && (neighborNum == 1 || neighborNum == 7)) return RIGHT;
		if(nextDirection == TOP_RIGHT && (neighborNum == 3 || neighborNum == 5)) return TOP;
		if(nextDirection == RIGHT && (neighborNum == 0 || neighborNum == 1 || neighborNum == 2)) return BOTTOM_RIGHT;
		if(nextDirection == RIGHT && (neighborNum == 6 || neighborNum == 5 || neighborNum == 4)) return TOP_RIGHT;
		if(nextDirection == BOTTOM_RIGHT && (neighborNum == 5 || neighborNum == 7)) return RIGHT;
		if(nextDirection == BOTTOM_RIGHT && (neighborNum == 3 || neighborNum == 1)) return BOTTOM;
		if(nextDirection == BOTTOM && (neighborNum == 0 || neighborNum == 7 || neighborNum == 6)) return BOTTOM_RIGHT;
		if(nextDirection == BOTTOM && (neighborNum == 2 || neighborNum == 3 || neighborNum == 4)) return BOTTOM_LEFT;
		if(nextDirection == BOTTOM_LEFT && (neighborNum == 5 || neighborNum == 3)) return LEFT;
		if(nextDirection == BOTTOM_LEFT && (neighborNum == 7 || neighborNum == 1)) return BOTTOM;
		if(nextDirection == LEFT && (neighborNum == 0 || neighborNum == 1 || neighborNum == 2)) return BOTTOM_LEFT;
		if(nextDirection == LEFT && (neighborNum == 6 || neighborNum == 5 || neighborNum == 4)) return TOP_LEFT;
		if(nextDirection == TOP_LEFT && (neighborNum == 1 || neighborNum == 3)) return LEFT;
		if(nextDirection == TOP_LEFT && (neighborNum == 5 || neighborNum == 7)) return TOP;
		return nextDirection;
	}
}
