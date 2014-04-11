package com.sispd.f1race;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.MatchResult;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import com.sispd.f1racing.Enums.DriverSkill;
import com.sispd.f1racing.POJOs.Driver;

public class Board extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
    private double scale;
    private double scaleX = 1, scaleY = 1;
    private final Timer timer;
    //private Date date;
    private BufferedImage background;
    private List<Bolid> bolids = new ArrayList<Bolid>(25);
    private Path path;
    private ConsoleFrame consoleFrame;
    private boolean debugMode = false;
    private boolean showInfo = true;
    private final int SCREEN_WIDTH, SCREEN_HEIGHT;
    private int bolidSize = 2, translateX = 0, translateY = 0, timerDelay = 40, simulationSpeed = 1;
    private String track = "bahrain";
    private ChartGenerator chartGenerator;
    private ComponentHandler componentHandler = new ComponentHandler();
    private MouseControler mouseControler = new MouseControler(this);
    private AffineTransform customTransform = new AffineTransform();
    private final JScrollPane parent;
    private final Board thisBoard;
    
    //Getters
    public List<Bolid> getBolids(){ return bolids; }
    public ChartGenerator getChartGenerator(){ return chartGenerator; }
    public boolean isTimerRunning(){ return timer.isRunning(); }
    
    //Setters
    public void setTrack(String track){ this.track = track.toLowerCase(); this.componentHandler.componentResized(null); }
    public void setConsoleFrame(ConsoleFrame consoleFrame){ this.consoleFrame = consoleFrame; }
    public void setShowInfo(boolean showInfo){ this.showInfo = showInfo; }
    public void setTimerDelay(int timerDelay){ }
    public void startTimer(){ timer.start(); }
    public void stopTimer(){ timer.stop();}
    
    //Constructor
    public Board(int screenWidth, int screenHeight, JScrollPane parent) {
        super(true);
        this.addMouseListener(mouseControler); //TODO nextToDO
        this.addMouseMotionListener(mouseControler);
        this.addMouseWheelListener(mouseControler);
        this.addComponentListener(componentHandler);
        timer = new Timer((int)(timerDelay/simulationSpeed), this);
        this.SCREEN_WIDTH = screenWidth;
        this.SCREEN_HEIGHT = screenHeight;
        this.parent = parent;
        this.thisBoard = this;
    }

    public void create() {
        //consoleFrame.setVisible(true);

        deserializePath();
        //printPathStats();

        chartGenerator = new ChartGenerator(path.width, path.height, 0.1, path.name + " velocity chart");
        //consoleFrame.chartGenerator = chartGenerator;

        addBolids();

        //timer.start();
        //date = new Date();
    }

    public void reset() {
        bolids.clear();

        //TODO to erase
        if(consoleFrame != null)
        {
	        consoleFrame.bolids1.removeAllItems();
	        consoleFrame.bolids2.removeAllItems();
	        consoleFrame.currentBolids.clear();
        }

        deserializePath();
        //printPathStats();

        chartGenerator.saveImage();
        chartGenerator = new ChartGenerator(path.width, path.height, 0.1, path.name + " velocity chart");

        componentHandler.componentResized(null);

        addBolids();
        //date = new Date();

        
        //TODO to erase
        if(consoleFrame != null)
        {
        	consoleFrame.updateBolidsLists();
	        consoleFrame.bolids1.removeAllItems();
	        consoleFrame.bolids2.removeAllItems();
	        consoleFrame.currentBolids.clear();
	        consoleFrame.updateBolidsLists();
        }
        
        this.repaint();
    }

//    private void printPathStats() {
//        System.out.println("Path length: " + path.calculateLength()/10000 + " km");
//        System.out.println("Breaking max: " + path.breakingMax + " breaking min: " + path.breakingMin);
//    }

    private void deserializePath() {
        try {
            URL pathSerialized = getClass().getResource("/" + track + ".path");
            FileInputStream fileIn = new FileInputStream(pathSerialized.getFile());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            path = (Path) in.readObject();
            in.close();
            fileIn.close();
        } catch(IOException i) {
            i.printStackTrace();
        } catch(ClassNotFoundException c) {
            System.out.println("Path class not found");
            c.printStackTrace();
        }
    }
    
    private LinkedList<Driver> loadDrivers(String filename) throws FileNotFoundException
    {
    	LinkedList<Driver> drivers = new LinkedList<Driver>();
    	File file = new File(filename);
    	Scanner in = new Scanner(file);
    	in.findInLine("(\\d+)");
    	MatchResult result = in.match();
    	int driversCount = Integer.parseInt(result.group(1));
    	for(int x=0; x<driversCount; x++)
		{
			in.nextLine();
			in.findInLine("(\\w+);(\\w+)");
			result = in.match();
			drivers.add(new Driver(result.group(1),DriverSkill.valueOf(result.group(2))));
		}
		in.close();
    	return drivers;
    }

    private void addBolids() {
        Random random = new Random();
        try 
        {
			LinkedList<Driver> drivers = loadDrivers("drivers.txt");
			for(Driver driver : drivers) {
	            double maxSpeed = driver.getDriverSkill().getRandomMistakeParameter()*5+32+random.nextDouble();
	            double acceleration = driver.getDriverSkill().getRandomMistakeParameter()*random.nextDouble()*0.15+0.2;
	            double turnForce = driver.getDriverSkill().getRandomMistakeParameter()*random.nextDouble()*0.15+0.05;

	            int colorNumber = random.nextInt(5)+1;

	            Bolid bolid = new Bolid(path, bolidSize, bolids.size()+1, driver.getDriverSkill(), maxSpeed, acceleration, turnForce, 0.05, colorNumber, debugMode, bolids, driver.getName());
	            bolids.add(bolid);
	        }
		} 
        catch (FileNotFoundException e) { JOptionPane.showMessageDialog(this, "Drivers loading problem!"); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(background, 0, 0, this);

            for(Bolid bolid : bolids) {
                    bolid.display(g, scale, customTransform);

                g.setColor(Color.BLACK);

                URL pathSerializedPopup = getClass().getResource("/popup.png");
                BufferedImage bufferedImagePopup = ImageIO.read(new File(pathSerializedPopup.getFile()));

                if(showInfo)
                {
	                int x = (int) (bolid.getLocation().getX()*scale);
	                int y = (int) (bolid.getLocation().getY()*scale);
	
	                ((Graphics2D) g).drawImage(bufferedImagePopup, x-50, y-50, null);
	                g.drawString(bolid.getName(), x-45, y-35);
	
	                DecimalFormat f = new DecimalFormat("00.00");
	                g.drawString(f.format(bolid.getVelocity().magnitude()*25*0.36) + " km/h", x-48, y-20);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        customTransform = new AffineTransform(); //TODO Autoscroll - 2x wolniejsza symulacja - sprawdzone
//        customTransform.translate(translateX, translateY);
//        customTransform.scale(scaleX, scaleY);
//        componentHandler.componentResized(null);

        List<Bolid> bolidsToRemove = new ArrayList<Bolid>();

        for(Bolid bolid : bolids) {
            if(bolid.isCrashed() ){ // || bolid.getLaps()==3) {
                bolidsToRemove.add(bolid);
            }
        }
        
        if(!bolidsToRemove.isEmpty()) 
        {
        	String collisionString = "";
        	for(Bolid bolid : bolidsToRemove) {
        		collisionString+=" "+bolid.getBolidNumber()+". "+bolid.getName();
                bolids.remove(bolid);
            }
        	//TODO Zbli�enie na kolizj�
        	JOptionPane.showMessageDialog(this.getParent(), "Collision on track:"+collisionString+"!");
        }

        for(Bolid bolid : bolids) {
            bolid.update(timerDelay);
            bolid.follow();

            chartGenerator.updateTable((int) (bolid.getLocation().getX()), (int) (bolid.getLocation().getY()), bolid.getVelocity().magnitude());
        }

        //TODO to erase
        if(consoleFrame != null)
        {
	        consoleFrame.updateBolidsLists();
	        consoleFrame.updateFields(timerDelay);
        }
        this.repaint();

    }

    private class ComponentHandler extends ComponentAdapter {

        private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        private final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        
        private BufferedImage bufferedImage;
        private URL trackPath;
        private Rectangle anchorRect;
        private TexturePaint paint;
        

        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            try {
                int w = getWidth();
                int h = getHeight();
                thisBoard.setPreferredSize(new Dimension(w, h));

            	background = gc.createCompatibleImage(w, h, Transparency.OPAQUE);
            	Graphics2D g = background.createGraphics();
                //g.clearRect(0, 0, w, h);


                URL pathSerialized;

                if(track.equals("bahrain"))
                    pathSerialized = getClass().getResource("/sand.jpg");
                else
                    pathSerialized = getClass().getResource("/grass.jpg");


                if(bufferedImage == null || !trackPath.equals(pathSerialized)) 
                {
                	bufferedImage = ImageIO.read(new File(pathSerialized.getFile()));
                	trackPath = pathSerialized;
                	anchorRect = new Rectangle(0, 0, 150, 150);
                    paint = new TexturePaint(bufferedImage, anchorRect);
                }
                
                g.setPaint(paint);

                //g.setTransform(customTransform);
                g.fillRect(0, 0, w, h);

                //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);

                scale = Math.min( (double) w/path.width, (double) h/path.height);

                path.display(g, 100, scale, customTransform);

                g.dispose();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    class MouseControler implements  MouseListener, MouseMotionListener, MouseWheelListener {
        private int lastOffsetX;
        private int lastOffsetY;
        private Board board;
        
        public MouseControler(Board board){ this.board = board; }

        @Override
        public void mousePressed(MouseEvent e) {
            // capture starting point
            lastOffsetX = e.getX();
            lastOffsetY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // new x and y are defined by current mouseControler location subtracted
            // by previously processed mouseControler location
            int newX = e.getX() - lastOffsetX;
            int newY = e.getY() - lastOffsetY;

            // increment last offset to last processed by drag event.
            lastOffsetX += newX;
            lastOffsetY += newY;

            // update the canvas locations
            translateX += newX;
            translateY += newY;

            customTransform = new AffineTransform();
            customTransform.translate(translateX, translateY);
            customTransform.scale(scaleX, scaleY);
            componentHandler.componentResized(null);
            board.repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int direction = e.getWheelRotation();

            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                scaleX += (-0.6 * direction);
                scaleY += (-0.6 * direction);

                scaleX = Math.max(1, scaleX);
                scaleY = Math.max(1, scaleY);
            }

//            translateX += direction * 300;
//            translateY += direction * 400;
//            if(scaleX != 1 && scaleY != 1) 
//        	{
//	        	translateX += direction * (0.8/0.6)*(SCREEN_WIDTH/4); //TODO do poprawy
//	        	translateY += direction * (0.8/0.6)*(SCREEN_HEIGHT/4);
//        	}

            customTransform = new AffineTransform();
            customTransform.translate(translateX, translateY);
            customTransform.scale(scaleX, scaleY);
            componentHandler.componentResized(null);
            board.repaint();
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
        @Override
        public void mouseClicked(MouseEvent e) {

//            Random random = new Random();
//
//            double maxSpeed = random.nextDouble()*5+32;
//            double acceleration = random.nextDouble()*0.15+0.2;
//            double turnForce = random.nextDouble()*0.15+0.05;
//
//            int colorNumber = random.nextInt(5)+1;
//
//            Bolid bolid = new Bolid(path, bolidSize, 10-bolids.size(), DriverSkill.EXPERT, maxSpeed, acceleration, turnForce, 0.05, colorNumber, debugMode, bolids, "bolid"+ bolids.size());
//            bolids.add(bolid);
        }
    }

}