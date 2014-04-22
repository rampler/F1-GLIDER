package com.sispd.f1racing.Enums;

/**
 * Driver Team
 * @author Piotr Brudny, Kacper Furmański, Klaudia Kołdarz, Mateusz Kotlarz, Sabina Rydzek
 */
public enum Team{
    Red_Bull(3,1.5), Ferrari(2,2.5), Williams(5,3.5), McLaren(4,3), Force_India(6,2), Mercedes(1,1);

    private int colorNum;
    private double bolidScore;

    Team(int num, double bolidScore){
        this.colorNum = num;
        this.bolidScore = bolidScore;
    }
    public int getColorNum(){ return colorNum; }
    public double getBolidScore(){ return 1/bolidScore; }
}
