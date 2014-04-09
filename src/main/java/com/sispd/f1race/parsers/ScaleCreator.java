package com.sispd.f1race.parsers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class ScaleCreator {
    URL brakeScaleResource;
    BufferedImage brakeScaleImage;

    double min = 0;
    double max = 0.5;

    HashMap brakeScale = new HashMap<Integer, Double>();

    public ScaleCreator() {
        brakeScaleResource = getClass().getResource("/scale.png");
        brakeScaleImage = openImage(brakeScaleResource);

        parse();
    }

    private void parse() {
        int size = brakeScaleImage.getWidth();
        double step = (max-min)/size;


        for(int i=0; i<size; i++) {
            int key = brakeScaleImage.getRGB(i,0);
            brakeScale.put(key, step*i);
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

    public HashMap getBrakeScale() {
        return brakeScale;
    }
}
