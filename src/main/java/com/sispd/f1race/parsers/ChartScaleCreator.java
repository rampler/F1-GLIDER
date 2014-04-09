package com.sispd.f1race.parsers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class ChartScaleCreator {
    URL brakeScaleResource;
    BufferedImage brakeScaleImage;

    double min = 0;
    double max = 0.5;

    HashMap<Double, Integer> brakeScale = new HashMap<Double, Integer>();

    public ChartScaleCreator(double min, double max) {
        this.min = min;
        this.max = max;

        brakeScaleResource = getClass().getResource("/scale.png");
        brakeScaleImage = openImage(brakeScaleResource);

        parse();
    }

    private void parse() {
        int size = brakeScaleImage.getWidth();
        double step = (max-min)/size;


        for(int i=0; i<size; i++) {
            Integer value = brakeScaleImage.getRGB(i, 0);
            brakeScale.put(step*i, value);
        }
    }

    private BufferedImage openImage(URL ressource) {
        try {
            return ImageIO.read(new File(ressource.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap<Double, Integer> getBrakeScale() {
        return brakeScale;
    }
}
