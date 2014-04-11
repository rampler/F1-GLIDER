package com.sispd.f1race;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class Path implements Serializable {
    private ArrayList<PVector> points;

    double breakingMax = Integer.MIN_VALUE;
    double breakingMin = Integer.MAX_VALUE;

    public int width;
    public int height;
    public int startX;
    public int startY;
    public int radius;
    public String name;


    public Path(int width, int height, int startX, int startY, int radius, String name) {
        points = new ArrayList<PVector>(100000);

        this.radius = radius;
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.name = name;

    }

    public void addPoint(double x, double y, double breakingValue) {
        PVector point = new PVector(x,y,breakingValue);
        points.add(point);

        breakingMax = Math.max(breakingMax, breakingValue);
        breakingMin = Math.min(breakingMin, breakingValue);
    }

    void display(Graphics g, int interval, double scale, AffineTransform customTransform) throws IOException {
        g.setColor(new Color(213, 213, 213));

        Polygon polygon = new Polygon();

        for(int i=0; i<points.size(); i+=interval) {
            PVector point = points.get(i);

            int scaledX = (int) (point.getX() * scale);
            int scaledY = (int) (point.getY() * scale);

            polygon.addPoint(scaledX, scaledY);
        }

        int lineWidth = (int) (radius*scale);

        ((Graphics2D) g).setStroke(new BasicStroke(lineWidth+3));
        g.setColor(Color.WHITE);

        ((Graphics2D) g).setTransform(customTransform);
        g.drawPolygon(polygon);

        URL pathSerialized = getClass().getResource("/track.jpg");
        BufferedImage bufferedImage = ImageIO.read(new File(pathSerialized.getFile()));
        Rectangle anchorRect = new Rectangle(0, 0, 8, 8);
        TexturePaint paint = new TexturePaint(bufferedImage, anchorRect);
        ((Graphics2D) g).setPaint(paint);

        ((Graphics2D) g).setStroke(new BasicStroke(lineWidth));
        ((Graphics2D) g).setTransform(customTransform);

        g.drawPolygon(polygon);
    }

    public int size() {
        return points.size();
    }

    public double calculateLength() {
        double sum = 0;

        for(int i=0; i<points.size()-1; i++) {
            PVector first = points.get(i);
            PVector second = points.get(i+1);

            sum += PVector.distance(first, second);
        }

        return sum;
    }

    public PVector get(int i) {
        return points.get(i);
    }

    public ArrayList<PVector> getPoints() {
        return points;
    }

    public double getRadius() {
        return radius;
    }
}
