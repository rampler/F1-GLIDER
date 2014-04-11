package com.sispd.f1race;

import com.sispd.f1racing.Enums.DriverSkill;
import com.sispd.f1racing.POJOs.Driver;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.regex.MatchResult;

public class Board extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
    private final Board thisBoard;
    private double scale, zoom = 1, simulationSpeed = 1;
    private final Timer timer;
    //private Date date;
    private BufferedImage background;
    private List<Bolid> bolids = new ArrayList<Bolid>(25);
    private Path path;
    private ConsoleFrame consoleFrame;
    private boolean debugMode = false;
    private boolean showInfo = true;
    private final int SCREEN_WIDTH, SCREEN_HEIGHT;
    private int bolidSize = 2, timerDelay = 40;
    private String track = "bahrain";
    private ChartGenerator chartGenerator;
    private ComponentHandler componentHandler = new ComponentHandler();
    private AffineTransform customTransform = new AffineTransform();
    private final JScrollPane parent;
    private boolean autoscroll = false;
    private int autoscrollCarNumber = 1;

    //Getters
    public List<Bolid> getBolids(){ return bolids; }
    public ChartGenerator getChartGenerator(){ return chartGenerator; }
    public boolean isTimerRunning(){ return timer.isRunning(); }
    public ComponentHandler getComponentHandler(){ return componentHandler; }
    public double getZoom(){ return zoom; }

    //Setters
    public void setTrack(String track){ this.track = track.toLowerCase(); this.componentHandler.componentResized(null); }
    public void setConsoleFrame(ConsoleFrame consoleFrame){ this.consoleFrame = consoleFrame; }
    public void setShowInfo(boolean showInfo){ this.showInfo = showInfo; }
    public void setTimerDelay(int timerDelay){ }
    public void startTimer(){ timer.start(); }
    public void stopTimer(){ timer.stop();}
    public void setZoom(double zoom){ this.zoom = zoom; }
    public void setAutoscroll(boolean autoscroll){ this.autoscroll = autoscroll; }
    public void setAutoscrollCarNumber(int carNumber){ autoscrollCarNumber = carNumber; }

    //Constructor
    public Board(int screenWidth, int screenHeight, JScrollPane parent) {
        super(true);
        this.addComponentListener(componentHandler);
        timer = new Timer((int)(timerDelay/simulationSpeed), this);
        this.SCREEN_WIDTH = screenWidth;
        this.SCREEN_HEIGHT = screenHeight;
        this.parent = parent;
        this.thisBoard = this;
        deserializePath();
        //printPathStats();
        chartGenerator = new ChartGenerator(path.width, path.height, 0.1, path.name + " velocity chart");
        addBolids();
    }

    /**
     * Reset simulation
     */
    public void reset() {
        bolids.clear();

        //TODO to erase
        if(consoleFrame != null)
        {
	        consoleFrame.bolids1.removeAllItems();
	        consoleFrame.bolids2.removeAllItems();
	        consoleFrame.currentBolids.clear();
            consoleFrame.updateBolidsLists();
            consoleFrame.bolids1.removeAllItems();
            consoleFrame.bolids2.removeAllItems();
            consoleFrame.currentBolids.clear();
            consoleFrame.updateBolidsLists();
        }

        deserializePath();
        //printPathStats();

        chartGenerator.saveImage();
        chartGenerator = new ChartGenerator(path.width, path.height, 0.1, path.name + " velocity chart");
        componentHandler.componentResized(null);
        addBolids();
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

    /**
     * Loading drivers from file
     * @param filename - path to file
     * @return List of Drivers
     */
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

    /**
     * Add bolids to simulation
     */
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

    /**
     * Painting board
     * @param g - graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(background, 0, 0, this);

            for(Bolid bolid : bolids) {
                    bolid.display(g, scale, customTransform);

                g.setColor(Color.BLACK);

                URL pathSerializedPopup = getClass().getResource("/popup.png");       //TODO można wczytywać tylko raz, a nie przy każdym repaint
                BufferedImage bufferedImagePopup = ImageIO.read(new File(pathSerializedPopup.getFile()));

                if(showInfo)
                {
	                int x = (int) (bolid.getLocation().getX()*scale);
	                int y = (int) (bolid.getLocation().getY()*scale);

	                g.drawImage(bufferedImagePopup, x-50, y-50, null);
	                g.drawString(bolid.getName(), x-45, y-35);

	                DecimalFormat f = new DecimalFormat("00.00");
	                g.drawString(f.format(bolid.getVelocity().magnitude()*25*0.36) + " km/h", x-48, y-20);
                }
            }
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return prefered size of board
     * @return
     */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(new Dimension((int)((SCREEN_WIDTH-20)*zoom), (int)((SCREEN_HEIGHT-(int)(SCREEN_HEIGHT/15.36)-20)*zoom)));
    }

    /**
     * Action performed by timer
     * @param e
     */
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
        	//TODO Zbliżenie na kolizję
        	JOptionPane.showMessageDialog(this.getParent(), "Collision on track:"+collisionString+"!");
        }

        for(Bolid bolid : bolids) {
            bolid.update(timerDelay);
            bolid.follow();
            if(autoscroll && autoscrollCarNumber == bolid.getBolidNumber())
            {
                //TODO to repair
                parent.getHorizontalScrollBar().setValue((int)((bolid.getLocation().getX()/path.width)*(parent.getHorizontalScrollBar().getMaximum()-(parent.getHorizontalScrollBar().getMaximum()/zoom)/2)-(parent.getHorizontalScrollBar().getMaximum()/zoom)/2));
                parent.getVerticalScrollBar().setValue((int)((bolid.getLocation().getY()/path.height)*(parent.getVerticalScrollBar().getMaximum()-(parent.getVerticalScrollBar().getMaximum()/zoom)/2)-(parent.getVerticalScrollBar().getMaximum()/zoom)/2));
                System.out.println((bolid.getLocation().getX()/path.width)*parent.getHorizontalScrollBar().getMaximum()+" "+bolid.getLocation().getX()+" "+path.width+" ");
            }

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

    /**
     * Component responsible by resizing board and repaint background
     */
    public class ComponentHandler extends ComponentAdapter {
        private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        private final GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

        private BufferedImage bufferedImage;
        private URL trackPath;
        private Rectangle anchorRect;
        private TexturePaint paint;

        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            try
            {
                int w = getWidth();
                int h = getHeight();

            	background = gc.createCompatibleImage(w, h, Transparency.OPAQUE);
            	Graphics2D g = background.createGraphics();
                URL pathSerialized;

                if(track.equals("bahrain"))  pathSerialized = getClass().getResource("/sand.jpg");
                else pathSerialized = getClass().getResource("/grass.jpg");

                if(bufferedImage == null || !trackPath.equals(pathSerialized))
                {
                	bufferedImage = ImageIO.read(new File(pathSerialized.getFile()));
                	trackPath = pathSerialized;
                	anchorRect = new Rectangle(0, 0, 150, 150);
                    paint = new TexturePaint(bufferedImage, anchorRect);
                }

                g.setPaint(paint);
                g.fillRect(0, 0, w, h);
                scale = Math.min( (double) w/path.width, (double) h/path.height);
                path.display(g, 100, scale, customTransform);
                g.dispose();
            }
            catch (IOException e1) { JOptionPane.showMessageDialog(thisBoard, "Background cannot be loaded!");}
        }
    }

}