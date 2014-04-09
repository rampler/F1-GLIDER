package com.sispd.f1race;

import com.sispd.f1race.parsers.ChartScaleCreator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ChartGenerator {
    int width;
    int height;
    double scale;
    String name;

    double[][] table;

    ChartScaleCreator chartScaleCreator;

    public ChartGenerator(int width, int height, double scale, String name) {
        this.width = (int) (width*scale);
        this.height = (int) (height*scale);
        this.scale = scale;
        this.name = name;

        table = new double[this.width+10][this.height+10];
    }

    public void updateTable(int width, int height, double value) {
        int widthScaled = (int) (width*scale);
        int heightScaled = (int) (height*scale);

        if(table[widthScaled][heightScaled]==0) {
            table[widthScaled][heightScaled] = value;
        } else {
            table[widthScaled][heightScaled] = (table[widthScaled][heightScaled] + value)/2;
        }
    }

    public void saveImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double[] minMax = getMinMaxValue();
        chartScaleCreator = new ChartScaleCreator(minMax[0], minMax[1]);
        keysList.addAll(chartScaleCreator.getBrakeScale().keySet());
        Collections.sort(keysList);

        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                int RGB = calculateCorrespondingRGB(table[i][j]);

                image.setRGB(i, j, RGB);
            }
        }

        try {
            File outputfile = new File("./"+ name +".png");
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] getMinMaxValue() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                if(table[i][j]>max) {
                    max = table[i][j];
                } else if(min!=0 && table[i][j]<min) {
                    min = table[i][j];
                }
            }
        }

        double[] minMax = {min, max};
        return minMax;
    }

    ArrayList<Double> keysList = new ArrayList<Double>();

    private int calculateCorrespondingRGB(double value) {
        if(value==0) {
            return Color.WHITE.getRGB();
        }

        for(Double key : keysList) {
            if(value < key && chartScaleCreator.getBrakeScale().containsKey(key)) {
                return chartScaleCreator.getBrakeScale().get(key);
            }
        }

        return Color.WHITE.getRGB();
    }


}
