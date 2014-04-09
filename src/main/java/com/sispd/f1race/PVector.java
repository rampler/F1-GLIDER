package com.sispd.f1race;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PVector implements Serializable {
	private double x;
    private double y;
    private double breakingValue;

    public PVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PVector(double x, double y, double breakingValue) {
        this.x = x;
        this.y = y;
        this.breakingValue = breakingValue;
    }

    public void add(PVector pVector) {
        x += pVector.getX();
        y += pVector.getY();
    }

    public void substract(PVector pVector) {
        x -= pVector.getX();
        y -= pVector.getY();
    }

    public void multiply(double number) {
        x *= number;
        y *= number;
    }

    public void divide(double number) {
        x /= number;
        y /= number;
    }

    public double magnitude() {
        double xPow = Math.pow(x, 2);
        double yPow = Math.pow(y, 2);

        return Math.sqrt(xPow + yPow);
    }

    public void normalize() {
        double m = magnitude();
        if(m != 0) {
            divide(m);
        }
    }

    public void limit(double max) {
        if(magnitude() > max) {
            normalize();
            multiply(max);
        }
    }

    public double heading() {
        return Math.atan2(y, x);
    }

    public PVector getCopy() {
        return new PVector(x, y);
    }

    public double dot(PVector v) {
        return x*v.x + y*v.y;
    }

    public PVector getNewWithCustomLength(double length) {
        PVector tempCopy = getCopy();
        tempCopy.normalize();
        tempCopy.multiply(length);

        return tempCopy;
    }

    public static double distance(PVector pVector1, PVector pVector2) {
        double xAxis = Math.pow(pVector1.getX() - pVector2.getX(), 2);
        double yAxis = Math.pow(pVector1.getY() - pVector2.getY(), 2);

        double distance = Math.sqrt(xAxis + yAxis);

        return distance;
    }

    public static PVector add(PVector pVector1, PVector pVector2) {
        double x = pVector1.getX() + pVector2.getX();
        double y = pVector1.getY() + pVector2.getY();

        return new PVector(x, y);
    }

    public static PVector substract(PVector pVector1, PVector pVector2) {
        double x = pVector1.getX() - pVector2.getX();
        double y = pVector1.getY() - pVector2.getY();

        return new PVector(x, y);
    }

    public static PVector multiply(PVector pVector, double number) {
        double x = pVector.getX() * number;
        double y = pVector.getY() * number;

        return new PVector(x, y);
    }

    public static PVector divide(PVector pVector, double number) {
        double x = pVector.getX() / number;
        double y = pVector.getY() / number;

        return new PVector(x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getBreakingValue() {
        return breakingValue;
    }

    public void setBreakingValue(int breakingValue) {
        this.breakingValue = breakingValue;
    }

    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("00.00");
        return f.format(magnitude());
    }
}
