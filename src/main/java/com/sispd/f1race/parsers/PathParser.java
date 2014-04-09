package com.sispd.f1race.parsers;

import com.sispd.f1race.Path;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

public class PathParser implements Serializable {
    private int xStart;
    private int yStart;

    BufferedImage pathImage = null;
    BufferedImage breakingImage = null;

    URL pathResource;
    URL breakingResource;

    Path path;

    HashMap brakeScale;

    String trackName;
    int radius;

    public PathParser(int xStart, int yStart, int radius, String trackName) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.trackName = trackName;
        this.radius = radius;

        pathResource = getClass().getResource("/path_" + trackName + ".png");
        breakingResource = getClass().getResource("/breaking_" + trackName + ".png");

        ScaleCreator scaleCreator = new ScaleCreator();
        brakeScale = scaleCreator.getBrakeScale();
    }

    public Path parse() {
        pathImage = openImage(pathResource);
        breakingImage = openImage(breakingResource);

        path = new Path(pathImage.getWidth(), pathImage.getHeight(), xStart, yStart, radius, trackName);
        importPath();

        return path;
    }

    private BufferedImage openImage(URL ressource) {
        try {
            return ImageIO.read(new File(ressource.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    int x;
    int y;

    int xPrev;
    int yPrev;

    double breakingValue = 0;

    private void importPath() {
        x = xStart;
        y = yStart;
        xPrev = xStart;
        yPrev = yStart;

        int startPixel = pathImage.getRGB(x, y);
        if(startPixel != -16777216)
            System.out.println("Wrong start point");

        while(true) {
            Integer breakingValueRGB = breakingImage.getRGB(x, y);

            if(brakeScale.containsKey(breakingValueRGB))
                breakingValue = (Double) brakeScale.get(breakingValueRGB);

            path.addPoint(x, y, breakingValue);
            findNextPoint();

            if(path.size()>100000)
                break;

            if(x==xStart && y==yStart)
                break;
        }

        System.out.println("Path size: " + path.size());
    }

    private void findNextPoint() {
        if(path.size()==1) {
            x++;
        } else {
            if(isNext(x+1, y-1)) {
                xPrev = x;
                yPrev = y;
                x++; y--; return;
            }
            if(isNext(x+1, y)) {
                xPrev = x;
                yPrev = y;
                x++; return;
            }
            if(isNext(x+1, y+1)) {
                xPrev = x;
                yPrev = y;
                x++; y++; return;
            }
            if(isNext(x, y+1)) {
                xPrev = x;
                yPrev = y;
                y++; return;
            }
            if(isNext(x-1, y-1)) {
                xPrev = x;
                yPrev = y;
                x--; y--; return;
            }
            if(isNext(x-1, y)) {
                xPrev = x;
                yPrev = y;
                x--; return;
            }
            if(isNext(x-1, y+1)) {
                xPrev = x;
                yPrev = y;
                x--; y++; return;
            }
            if(isNext(x, y-1)) {
                xPrev = x;
                yPrev = y;
                y--; return;
            }
        }
    }

    private boolean isNext(int x, int y) {
        boolean isBlack = pathImage.getRGB(x, y)==-16777216;
        boolean isNotPrevious = xPrev!=x || yPrev!=y;

        return isBlack && isNotPrevious;
    }

}
