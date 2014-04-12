package com.sispd.f1race;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import com.sispd.f1racing.Enums.DriverSkill;

/**
 * @author Piotr Brudny, Kacper Furmański, Klaudia Kołdarz, Mateusz Kotlarz, Sabina Rydzek
 */
public class Bolid {
    private PVector location;
    private PVector velocity;
    private PVector target;
    private PVector steerForce;
    private PVector acceleration;
    private PVector turnForce;
    private PVector overtakeVector;
    
    private Bolid bolidToFollow;
    private Bolid bolidToOvertake;
    private Bolid bolidOnSide;

    private int lastPointOnPathVisited;
    private int lastlastPointOnPathVisited = 0;
    private int laps = 0;
    private int step = 0;
    private int startDelay = 25;
    private int color;
    private int bolidSize;
    private int bolidNumber;
    
    private long actualLapTime;
	private long bestLapTime;
    
    private double maxSpeed;
    private double maxForceAcceleration;
    private double steerForceMultiplier;
    private double stopAccelerationInTurnLevel;
    private double oldMaxForceAcceleration;
    private double oldSteerForceMultiplier;

    private Path path;
    
    private List<Bolid> bolids;
    private String name;
    private BufferedImage bufferedImage;

    private boolean isRain = false;
    private boolean crashed = false;
    private boolean startPhase = true;
    private boolean overtakingMode = false;
    private boolean weatherChanged = false;
    private boolean velocityMode = false;
    private boolean turnForceMode = false;
    private boolean steerForceMode = false;
    private boolean targetMode = false;
    
    private DriverSkill driverSkill;

    
    //Getters
    public PVector getVelocity(){ return velocity; }
    public PVector getAcceleration(){ return acceleration; }
    public PVector getTurnForce(){ return turnForce; }
    public PVector getLocation(){ return location; }
    public int getBolidNumber(){ return bolidNumber; }
    public int getLaps(){ return laps; }
    public long getActualLapTime(){ return actualLapTime; }
    public long getBestLapTime(){ return bestLapTime; }
    public double getMaxSpeed(){ return maxSpeed; }
    public double getMaxForceAcceleration(){ return maxForceAcceleration; }
    public String getName(){ return name; }
    public boolean isCrashed(){ return crashed; }
    public DriverSkill getDriverSkill(){ return driverSkill; }
    
    //Setters
    public void setRain(boolean isRain){ this.isRain = isRain; }
    public void setVelocityMode(boolean velocityMode){ this.velocityMode = velocityMode; }
    public void setSteerForceMode(boolean steerForceMode){ this.steerForceMode = steerForceMode; }
    public void setTargetMode(boolean targetMode){ this.targetMode = targetMode; }
    public void setOvertakingMode(boolean overtakingMode){ this.overtakingMode = overtakingMode; }

    //Constructor
    public Bolid(Path path, int bolidSize, int bolidNumber, DriverSkill driverSkill, double maxSpeed, double maxForceAcceleration, double steerForceMultiplier, double stopAccelerationInTurnLevel, int colorNumber, List<Bolid> bolids, String name) {
        this.path = path;

        double x = path.get(100*(11-bolidNumber)).getX();
        double y = path.get(100*(11-bolidNumber)).getY();

        startDelay += (bolidNumber+1)*4;

        location = new PVector(x,y);
        velocity = new PVector(-0.001,0);
        acceleration = new PVector(0,0);
        turnForce = new PVector(0,0);
        steerForce = new PVector(0,0);
        this.bolidSize = bolidSize;  // 3
        this.bolidNumber = bolidNumber;
        this.driverSkill = driverSkill;

        this.maxSpeed = maxSpeed; // 35.5
        this.maxForceAcceleration = maxForceAcceleration; // 0.22
        this.steerForceMultiplier = steerForceMultiplier; // 0.05
        this.stopAccelerationInTurnLevel = stopAccelerationInTurnLevel; // 0.05

        this.color = colorNumber;
        this.bolids = bolids;
        this.name = name;
        
        this.actualLapTime = 0;
        this.bestLapTime = 1000000;
    }

    public boolean isOcuppied(PVector point) {
        if(point==null)
            return false;

        Polygon polygon = createPolygon(1);
        if(polygon.contains(point.getX(), point.getY()))
            return true;

        return false;
    }

    public boolean isCrash(Polygon point) {
        if(point==null)
            return false;

        Polygon polygon = createPolygon(1);
        if(polygon.intersects(point.getBounds2D()))
            return true;

        return false;
    }

    public void follow() {
        int viewDistance = (int) Math.pow(velocity.magnitude(), 1.5);
        PVector predictedDirection = predictBolidDirection(viewDistance);
        PVector target = findTargetPoint(path, predictedDirection);

        if(target!=null) {
            seek(target);
        }
    }

    private PVector predictBolidDirection(int viewDistance) {
        PVector prediction = velocity.getNewWithCustomLength(viewDistance);

        return PVector.add(location, prediction);
    }

    private PVector findTargetPoint(Path path, PVector predictedDirection) {
        int from = 0;
        int to = path.size();

        if(target != null) {
            from = Math.max(lastPointOnPathVisited-50, 0);
            to = (lastPointOnPathVisited + 500);
        }

        target = null;

        double shortestDistance = 1000000;

        for (int i = from; i<to; i++) {
            int firstPointNumber = (i) % path.size();
            int secondPointNumber = (i+10) % path.size();
            int thirdPointNumber = (i+20) % path.size();

            PVector a = path.get(firstPointNumber);
            PVector b = path.get(secondPointNumber);

            PVector normalPoint = calculateNormalPoint(predictedDirection, a, b);
            PVector direction = PVector.substract(b, a);

            if (isNormalPointOnPath(a, b, normalPoint))   {
                normalPoint = b.getCopy();
                a = path.get(secondPointNumber);
                b = path.get(thirdPointNumber);

                direction = PVector.substract(b, a);
            }

            double distance = PVector.distance(predictedDirection, normalPoint);

            if (distance < shortestDistance) {
                shortestDistance = distance;

                direction.normalize();
                direction.multiply(1);

                target = normalPoint.getCopy();
                target.add(direction);

                lastPointOnPathVisited = secondPointNumber;
                if (lastlastPointOnPathVisited > lastPointOnPathVisited + 50) {
                    laps++;
                    
                    if(actualLapTime < bestLapTime && laps != 0) bestLapTime = actualLapTime;
            		actualLapTime = 0;
                }
                lastlastPointOnPathVisited = lastPointOnPathVisited;
            }
        }

        return target;
    }

    private boolean isNormalPointOnPath(PVector a, PVector b, PVector normalPoint) {
        return  normalPoint.getX() < Math.min(a.getX(), b.getX()) ||
                normalPoint.getX() > Math.max(a.getX(), b.getX()) ||
                normalPoint.getY() < Math.min(a.getY(), b.getY()) ||
                normalPoint.getY() > Math.max(a.getY(), b.getY());
    }

    public PVector calculateNormalPoint(PVector outsideLinePoint, PVector startPoint, PVector endPoint) {
        PVector ap = PVector.substract(outsideLinePoint, startPoint);
        PVector ab = PVector.substract(endPoint, startPoint);

        ab.normalize();
        ab.multiply(ap.dot(ab));

        return PVector.add(startPoint, ab);
    }

    void seek(PVector target) {
        bolidToFollow = shouldFollowCar();
        bolidOnSide = lookLeftRight();

        if(bolidToFollow!=null && bolidToFollow!=bolidToOvertake || velocity.magnitude()<15) {
            overtakingMode = false;
        }

        if(bolidToFollow!=null && bolidToFollow.overtakingMode) {
            velocity.limit(bolidToFollow.velocity.magnitude());
        }

        if(!overtakingMode && bolidToFollow!=null && bolidOnSide==null) {
            bolidToOvertake = bolidToFollow;
            overtakingMode = true;
        }

        if(!overtakingMode && bolidOnSide!=null) {
            double velocityBolidOnSide = bolidOnSide.velocity.magnitude()*0.8;
            velocity.limit(velocityBolidOnSide);
        }

        if(overtakingMode && !isOvertakeDone()) {
            if(overtakeVector==null)
                overtakeVector = turnForce.getNewWithCustomLength(26);

            if(overtakeVector.heading() - turnForce.heading() < 1.4 && overtakeVector.heading() - turnForce.heading() > -1.4)
                overtakeVector = turnForce.getNewWithCustomLength(26);

            target.add(overtakeVector);
        } else {
            bolidToOvertake = null;
            overtakeVector = null;
            overtakingMode = false;
        }

        checkIfCrash();
        calculateSteerForce(target);
        updateVelocity();

        keepMinimumSpeed(6);
    }

    private void updateVelocity() {
        velocity.add(steerForce);
        velocity.limit(maxSpeed);

        if(step<startDelay) {
            velocity.multiply(0);
        }
    }

    private void calculateSteerForce(PVector target) {
        PVector normalPoint = calculateNormalPoint(target, location, PVector.add(location, velocity));

        turnForce = PVector.substract(target, normalPoint);
        turnForce.multiply(steerForceMultiplier);

        caclucateAcceleration();

        steerForce = PVector.add(turnForce, acceleration);

    }

    private void keepMinimumSpeed(int minimumSpeed) {
        if(step<startDelay+25) {
            startPhase = false;
        }

        if(!startPhase && velocity.magnitude()<minimumSpeed) {
            velocity = velocity.getNewWithCustomLength(minimumSpeed);
        }
    }

    private void checkIfCrash() {
        Polygon myPolygon = createPolygonWithNoMove(0.1);

        for(Bolid bolid : bolids) {
            if(bolid!=this && bolid.isCrash(myPolygon)) {
                crashed = true;
                bolid.crashed = true;
            }
        }
    }

    private Bolid shouldFollowCar() {
        Polygon myPolygon = createPolygonWithNoMove(1);
        PVector moveVector = velocity.getNewWithCustomLength(25);
        myPolygon.translate((int) (moveVector.getX()), (int) (moveVector.getY()));

        for(Bolid bolid : bolids) {
            if(bolid!=this && bolid.isCrash(myPolygon)) {
                return bolid;
            }
        }

        return null;
    }

    private boolean isOvertakeDone() {
        if(overtakeVector==null) return false;

        Polygon myPolygon = createPolygonWithNoMove(3);
        PVector sideVector = overtakeVector.getNewWithCustomLength(-25);

        myPolygon.translate((int) (sideVector.getX()), (int) (sideVector.getY()));

        if(!bolidToOvertake.isCrash(myPolygon)) return true;
        return false;
    }

    private Bolid lookLeftRight() {
        if(turnForce==null)
            return null;

        Polygon lookLeft = createPolygonWithNoMove(1);
        Polygon lookRight = createPolygonWithNoMove(1);

        PVector left = turnForce.getNewWithCustomLength(25);
        turnForce.multiply(-1);
        PVector right = turnForce.getNewWithCustomLength(25);

        lookLeft.translate((int) (left.getX()), (int) (left.getY()));
        lookRight.translate((int) (right.getX()), (int) (right.getY()));

        for(Bolid bolid : bolids) {
            if(bolid!=this && bolid.isCrash(lookLeft))
                return bolid;

            if(bolid!=this && bolid.isCrash(lookRight))
                return bolid;
        }

        return null;
    }

    private void caclucateAcceleration() {
        double breakingValue = path.get(lastPointOnPathVisited).getBreakingValue();
        double speed = velocity.magnitude();

        boolean shouldBrake = breakingValue>0;
        boolean isInTurn = turnForce.magnitude()>stopAccelerationInTurnLevel;
        boolean isOver200kmph = speed>21;

        if(!shouldBrake && !isInTurn && isOver200kmph) {
            double accelerationOver200kmph = calculateAccelerationOver200kmph(speed);

            acceleration = velocity.getNewWithCustomLength(accelerationOver200kmph);
        } else if(!shouldBrake && !isInTurn && !isOver200kmph) {
            acceleration = velocity.getNewWithCustomLength(maxForceAcceleration);
        } else if(isInTurn) {
            acceleration = velocity.getNewWithCustomLength(0);
        }

        if(shouldBrake) {
            acceleration = velocity.getNewWithCustomLength(-breakingValue);
        }
    }

    private double calculateAccelerationOver200kmph(double x) {
        return 7.59532
        - 1.97603*x
        + 0.204439*Math.pow(x, 2)
        - 0.0105423*Math.pow(x, 3)
        + 0.000287946*Math.pow(x, 4)
        - 3.99166961958746 * Math.pow(10, -6) *Math.pow(x, 5)
        + 2.213567806471638 * Math.pow(10, -8) *Math.pow(x, 6);
    }
    
    public void update(int timerDelay) {
    	
        if(isRain && !weatherChanged) {
            oldMaxForceAcceleration = maxForceAcceleration;
            oldSteerForceMultiplier = steerForceMultiplier;

            maxForceAcceleration*=0.7;
            steerForceMultiplier*=0.3;

            weatherChanged = true;
        }

        if(!isRain && weatherChanged) {
            maxForceAcceleration = oldMaxForceAcceleration;
            steerForceMultiplier = oldSteerForceMultiplier;

            weatherChanged = false;
        }

        location.add(velocity);
        steerForce.multiply(0);

        step++;
        actualLapTime+=timerDelay;
    }

    private Polygon createPolygon(double scale) {
        Point2D firstPoint = new Point.Double(-24*scale, -9*scale);
        Point2D secondPoint = new Point.Double(24*scale, -9*scale);
        Point2D thirdPoint = new Point.Double(24*scale, 9*scale);
        Point2D fourthPoint = new Point.Double(-24*scale, 9*scale);

        double theta = velocity.heading();

        int locationX = (int) (location.getX() * scale);
        int locationY = (int) (location.getY() * scale);

        rotatePoint(theta, firstPoint);
        rotatePoint(theta, secondPoint);
        rotatePoint(theta, thirdPoint);
        rotatePoint(theta, fourthPoint);

        Polygon polygon = new Polygon();
        polygon.addPoint((int) Math.round(firstPoint.getX()), (int) Math.round(firstPoint.getY()));
        polygon.addPoint((int) Math.round(secondPoint.getX()), (int) Math.round(secondPoint.getY()));
        polygon.addPoint((int) Math.round(thirdPoint.getX()), (int) Math.round(thirdPoint.getY()));
        polygon.addPoint((int) Math.round(fourthPoint.getX()), (int) Math.round(fourthPoint.getY()));

        polygon.translate(locationX, locationY);

        return polygon;
    }

    private Polygon createPolygonWithNoMove(double scale) {
        Point2D firstPoint = new Point.Double(-24*scale, -9*scale);
        Point2D secondPoint = new Point.Double(24*scale, -9*scale);
        Point2D thirdPoint = new Point.Double(24*scale, 9*scale);
        Point2D fourthPoint = new Point.Double(-24*scale, 9*scale);

        double theta = velocity.heading();

        int locationX = (int) (location.getX());
        int locationY = (int) (location.getY());

        rotatePoint(theta, firstPoint);
        rotatePoint(theta, secondPoint);
        rotatePoint(theta, thirdPoint);
        rotatePoint(theta, fourthPoint);

        Polygon polygon = new Polygon();
        polygon.addPoint((int) Math.round(firstPoint.getX()), (int) Math.round(firstPoint.getY()));
        polygon.addPoint((int) Math.round(secondPoint.getX()), (int) Math.round(secondPoint.getY()));
        polygon.addPoint((int) Math.round(thirdPoint.getX()), (int) Math.round(thirdPoint.getY()));
        polygon.addPoint((int) Math.round(fourthPoint.getX()), (int) Math.round(fourthPoint.getY()));

        polygon.translate(locationX, locationY);

        return polygon;
    }

    /**
     * Display bolid on board
     * @param g - graphics
     * @param scale - actual scale
     * @throws IOException
     */
    public void display(Graphics g, double scale) throws IOException {
        int locationX = (int) (location.getX() * scale);
        int locationY = (int) (location.getY() * scale);

        g.setColor(Color.BLACK);

        if (overtakingMode) {
            if(overtakeVector!=null) {
                g.setColor(Color.ORANGE);
                g.drawLine(locationX, locationY, (int) (locationX + overtakeVector.getX()), (int) (locationY + overtakeVector.getY()));
            }
        }
        if (velocityMode) {
            g.setColor(Color.GREEN);
            g.drawLine(locationX, locationY, (int) (locationX + velocity.getX()), (int) (locationY + velocity.getY()));
        }

        if (turnForceMode) {
            if(turnForce!=null) {
                g.setColor(Color.ORANGE);
                g.drawLine(locationX, locationY, (int) (locationX + turnForce.getX() * 30), (int) (locationY + turnForce.getY() * 30));
            }
        }

        if (steerForceMode) {
            g.setColor(Color.MAGENTA);
            g.drawLine(locationX, locationY, (int) (locationX + steerForce.getX() * 30), (int) (locationY + steerForce.getY() * 30));
        }

        if (targetMode) {
            if(target!=null) {
                g.setColor(Color.YELLOW);
                //((Graphics2D) g).setTransform(customTransform);
                g.drawOval((int) (target.getX() * scale - 3), (int) (target.getY() * scale - 3), 6, 6);
            }
        }

        if(bufferedImage == null)
        {
            URL pathSerialized = getClass().getResource("/bolid"+ color +".png");
            bufferedImage = ImageIO.read(new File(pathSerialized.getFile()));
        }
        double theta = velocity.heading() + Math.PI/2;

        Point2D movePoint = new Point.Double(-1.5, -4);
        rotatePoint(theta, movePoint);

        AffineTransform at = new AffineTransform();
        at.translate(locationX+ movePoint.getX(), locationY+movePoint.getY());
        at.scale(scale/4, scale/4);
        at.rotate(theta, 60*scale/4, 145*scale/4);

        ((Graphics2D) g).drawImage(bufferedImage, at, null);
    }

    private void rotatePoint(double theta, Point2D point) {
        AffineTransform.getRotateInstance(theta, 0, 0).transform(point, point);
    }

    @Override
    public String toString() { return name; }
}
