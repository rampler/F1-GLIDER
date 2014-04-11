package com.sispd.f1racing.GUI;

import com.sispd.f1race.Board;
import com.sispd.f1race.Bolid;
import com.sispd.f1race.ConsoleFrame;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;

/**
 * Program's GUI with all ActionListeners
 * @author Sabina Rydzek, Kacper Furma�ski, Mateusz Kotlarz
 *
 */
public class GUI extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private Board board;
	
	private JButton exit, simulation, start, clear, drivers, result, autoscrollBtn;
	private JComboBox<String> drynessCB, tiresCB, trackCB;
	private JScrollPane scrollPane;
	private JPanel buttonPanel;
	private JSlider zoom, simSpeed;
	private JFrame driversWindow, paramWindow, resultWindow;
	private JSpinner g100, g200, g300, b100, b200, b300, n100, n200, n300, itDelay, refDelay;
	private JTable table, tableResult;
	private JCheckBox autoscroll;
	private Container parent;
	
	private double screenWidth, screenHeight;
	private Timer timer, timerDrivers;
	private int timerDelay = 20, timerDriversDelay = 300;
	private boolean notStarted = true;
	private double simulationSpeed = 1;

	/**
	 * Initialize GUI
	 * @param container
	 */
	public GUI(Container container) {
		//GUI Layout
		parent = container;
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.setSize(new Dimension(1024, 768));
		//Timers
		timer = new Timer((int)(timerDelay/simulationSpeed), this);
		timer.stop();
		timerDrivers = new Timer(timerDriversDelay, this);
		timerDrivers.stop();
		
		//Buttons
		buttonPanel = new JPanel();
		
        trackCB = new JComboBox<String>();
        trackCB.setModel(new DefaultComboBoxModel(new String[] {"Bahrain", "Silverstone"}));
		trackCB.setActionCommand("changeTrack");
		trackCB.addActionListener(this);
		
		start = new JButton("Start");
		start.setActionCommand("start");
		start.addActionListener(this);
		
		clear = new JButton("Clear");
		clear.setActionCommand("clear");
		clear.addActionListener(this);
		
		exit = new JButton("Exit");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		
		
		drivers = new JButton("Drivers");
		drivers.setActionCommand("drivers");
		drivers.addActionListener(this);
		table = new JTable();
		
		result = new JButton("Result");
		result.setActionCommand("result");
		result.addActionListener(this);
		tableResult = new JTable();
		
		simulation = new JButton("Simulation Parameters");
		simulation.setActionCommand("parameters");
		simulation.addActionListener(this);
		
		//Autoscroll
		autoscroll = new JCheckBox();
		autoscroll.setActionCommand("autoscroll");
		autoscroll.addActionListener(this);
		
		autoscrollBtn = new JButton("1");
		autoscrollBtn.setEnabled(false);
		autoscrollBtn.setActionCommand("autoscrollBtn");
		autoscrollBtn.addActionListener(this);
        
		//Zoom
        zoom = new JSlider(0,4);
        Hashtable<Integer, JLabel> hashtable = new Hashtable<>();
        hashtable.put(0, new JLabel("1x"));
        hashtable.put(1, new JLabel("1.5x"));
        hashtable.put(2, new JLabel("2x"));
        hashtable.put(3, new JLabel("3x"));
        hashtable.put(4, new JLabel("4x"));
        zoom.setLabelTable(hashtable);
        zoom.setPaintLabels(true);
        zoom.setValue(0);
        zoom.addChangeListener(this);

        //Dane rozdzielczości monitora
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.getWidth();
        screenHeight = screenSize.getHeight();
        double wspWidth = screenWidth/1366,  wspHeight = screenHeight/768;

		//Control Panel
		buttonPanel.add(trackCB);
		buttonPanel.add(simulation);
		buttonPanel.add(Box.createHorizontalStrut((int)(50*wspWidth)));
		buttonPanel.add(start);
		buttonPanel.add(clear);
		buttonPanel.add(Box.createHorizontalStrut((int)(50*wspWidth)));
		buttonPanel.add(exit);
		buttonPanel.add(Box.createHorizontalStrut((int)(30*wspWidth)));
		buttonPanel.add(new JLabel("Autoscroll:"));
		buttonPanel.add(autoscroll);
		buttonPanel.add(autoscrollBtn);
        buttonPanel.add(Box.createHorizontalStrut((int)(20*wspWidth)));
		buttonPanel.add(new JLabel("Zoom:"));
		buttonPanel.add(zoom);
		buttonPanel.add(Box.createHorizontalStrut((int)(50*wspWidth)));
		buttonPanel.add(drivers);
		buttonPanel.add(result);

        //Scaled Sizes
        trackCB.setPreferredSize(new Dimension((int)(90*wspWidth),(int)(20*wspHeight)));
        simulation.setPreferredSize(new Dimension((int)(150*wspWidth),(int)(20*wspHeight)));
        start.setPreferredSize(new Dimension((int) (70 * wspWidth), (int) (20 * wspHeight)));
        clear.setPreferredSize(new Dimension((int)(70*wspWidth),(int)(20*wspHeight)));
        exit.setPreferredSize(new Dimension((int)(70*wspWidth),(int)(20*wspHeight)));
        autoscroll.setPreferredSize(new Dimension((int)(30*wspWidth),(int)(20*wspHeight)));
        autoscrollBtn.setPreferredSize(new Dimension((int)(50*wspWidth),(int)(20*wspHeight)));
        zoom.setPreferredSize(new Dimension((int)(250*wspWidth),(int)(40*wspHeight)));
        drivers.setPreferredSize(new Dimension((int)(70*wspWidth),(int)(20*wspHeight)));
        result.setPreferredSize(new Dimension((int)(70*wspWidth),(int)(20*wspHeight)));

        if(screenHeight/screenHeight > 1.5) buttonPanel.setPreferredSize(new Dimension((int)screenWidth,(int)(screenHeight/15.36)));

		//Board creating
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(1363,729));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		
		board = new Board((int) screenWidth, (int)screenHeight, scrollPane);
		scrollPane.getViewport().add(board);
        board.setPreferredSize(new Dimension((int) screenWidth-20, (int)screenHeight-(int)(screenHeight/15.36)-20));

		container.add(scrollPane, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Implemented from ActionListener - Buttons and timers actions
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(timer)) {  } 
		else if(e.getSource().equals(timerDrivers))
		{ 
			boolean none = true;
			if(driversWindow != null && driversWindow.isVisible()) { TablesRefresher.refreshDriversTableModel(board, table); none = false;  }
			if(resultWindow != null && resultWindow.isVisible()) {  TablesRefresher.refreshResultTableModel(board, tableResult); none = false; }
			if(none) timerDrivers.stop();
		}
		else 
		{
			String command = e.getActionCommand();
			if (command.equals("exit")) {
				timer.stop();
				timerDrivers.stop();
				board.stopTimer();
				for(Frame frame : Frame.getFrames())
					frame.dispose();		
			}
			else if(command.equals("parameters")){ parametersButtonAction(); }
			else if(command.equals("start"))
			{ 
				if(!board.isTimerRunning()) 
				{ 
					board.startTimer();  
					notStarted = false; 
					start.setText("Pause");
				}
				else{ board.stopTimer(); start.setText("Start"); }
			}
			else if(command.equals("clear")){ clearSimulationWindow(); }
			else if(command.equals("drivers")){ showDriversWindow(); }
			else if(command.equals("result")){ showResultWindow(); }
			else if(command.equals("changeTrack")){ board.setTrack((String) trackCB.getSelectedItem()); clearSimulationWindow(); }
            else if(command.equals("autoscroll"))
            {
                if(autoscroll.isSelected()) { board.setAutoscroll(true); autoscrollBtn.setEnabled(true); }
                else { board.setAutoscroll(false); autoscrollBtn.setEnabled(false); }
            }
            else if(command.equals("autoscrollBtn"))
            {
                int next = Integer.parseInt(autoscrollBtn.getText());
                List<Bolid> cars = board.getBolids();
                if(cars.size() == 0) autoscroll.setSelected(false);
                else
                {
                    int i=0, j=1;
                    boolean end = false;
                    while(j< 10 && !end)        //TODO [TEST]sprawdzić czy to 10 nie przeszkadza
                    {
                        while(i< cars.size() && cars.get(i).getBolidNumber() != next+j) i++;
                        if(i != cars.size()) end = true;
                        else {j++; i=0; }
                    }
                    if(j==10) next = cars.get(0).getBolidNumber();
                    else next = next+j;
                    autoscrollBtn.setText(next+"");
                    board.setAutoscrollCarNumber(next);
                }
            }
		}
	}

    /**
     * Implemented from ChangeListener - Slider and Spinners actions
     * Changed Zoom
     * @param e
     */
    public void stateChanged(ChangeEvent e) {
        if(e.getSource().equals(zoom))
        {
            int x = (int)(scrollPane.getHorizontalScrollBar().getValue()/(2*(board.getZoom()/0.5)) + scrollPane.getWidth()/(2*(board.getZoom()/0.5))/2);   //TODO centrowanie kamery
            int y = (int)(scrollPane.getVerticalScrollBar().getValue()/(2*(board.getZoom()/0.5)) + scrollPane.getHeight()/(2*(board.getZoom()/0.5))/2);
            switch(zoom.getValue())
            {
                case 0: board.setZoom(1); board.getComponentHandler().componentResized(null); board.repaint(); break;
                case 1: board.setZoom(1.5); board.getComponentHandler().componentResized(null); board.repaint(); break;
                case 2: board.setZoom(2); board.getComponentHandler().componentResized(null); board.repaint(); break;
                case 3: board.setZoom(3); board.getComponentHandler().componentResized(null); board.repaint(); break;
                case 4: board.setZoom(4); board.getComponentHandler().componentResized(null); board.repaint(); break;
            }
            scrollPane.revalidate();
            scrollPane.getHorizontalScrollBar().setValue((int)((x-scrollPane.getWidth()/(2*(board.getZoom()/0.5))/2)*(2*(board.getZoom()/0.5))));
            scrollPane.getVerticalScrollBar().setValue((int)((y-scrollPane.getHeight()/(2*(board.getZoom()/0.5))/2)*(2*(board.getZoom()/0.5))));
        }
    }
	
	/**
	 * Clear simulation window
	 */
	private void clearSimulationWindow()
	{
		if(board.isTimerRunning()) { board.stopTimer(); start.setText("Start"); }
		board.reset();
		notStarted = true;
		TablesRefresher.createResultTableModel(board, tableResult);
	}
	
	/**
	 * Show drivers information window
	 */
	private void showDriversWindow()
	{
		if(driversWindow == null)
		{
			driversWindow = new JFrame("Drivers");
			driversWindow.setUndecorated(true);
			driversWindow.setAlwaysOnTop(true);
			driversWindow.setLayout(new BorderLayout());
			driversWindow.setBounds((int)(screenWidth-455-19), (int)(screenHeight-218-(buttonPanel.getHeight()+19)), 455, 218);
			JPanel mainPanel = new JPanel(new BorderLayout());
			timerDrivers.start();
			
			//Add Table
			JScrollPane scroll = new JScrollPane(table);
			mainPanel.add(scroll, BorderLayout.CENTER);
			driversWindow.add(mainPanel, BorderLayout.CENTER);
			driversWindow.setVisible(true);
		}
		else if(!driversWindow.isVisible()){ driversWindow.setVisible(true); timerDrivers.start(); }
		else {driversWindow.setVisible(false); }
	}
	
	/**
	 * Show results window
	 */
	private void showResultWindow()
	{
		if(resultWindow == null)
		{
			resultWindow = new JFrame("Results");
			resultWindow.setUndecorated(true);
			resultWindow.setAlwaysOnTop(true);
			resultWindow.setLayout(new BorderLayout());
			resultWindow.setBounds(0, 0, 455, 218);
			JPanel mainPanel = new JPanel(new BorderLayout());
			timerDrivers.start();
			
			//Add Table
			JScrollPane scroll = new JScrollPane(tableResult);
			mainPanel.add(scroll, BorderLayout.CENTER);
			resultWindow.add(mainPanel, BorderLayout.CENTER);
			resultWindow.setVisible(true);
		}
		else if(!resultWindow.isVisible()){ resultWindow.setVisible(true); timerDrivers.start(); }
		else {resultWindow.setVisible(false); }
	}
	
	/**
	 * Show parameters window
	 */
	private void parametersButtonAction()
	{
		if(paramWindow == null)
		{
			paramWindow = new ConsoleFrame(board.getBolids(), board.getChartGenerator(), board);
			paramWindow.setUndecorated(true);
			paramWindow.setAlwaysOnTop(true);
			paramWindow.setBounds(0, (int)(screenHeight-329-(buttonPanel.getHeight()+19)), 390, 329);
			paramWindow.setVisible(true);
			board.setConsoleFrame((ConsoleFrame) paramWindow);
		}
		else if(!paramWindow.isVisible()) paramWindow.setVisible(true);
		else paramWindow.setVisible(false);
	}
	
}
