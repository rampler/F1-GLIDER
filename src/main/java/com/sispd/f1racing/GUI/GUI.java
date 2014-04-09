package com.sispd.f1racing.GUI;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sispd.f1race.Board;
import com.sispd.f1race.ConsoleFrame;

/**
 * Program's GUI with all ActionListeners
 * @author Sabina Rydzek, Kacper Furmañski, Mateusz Kotlarz
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
		trackCB.setBounds(10, 291, 131, 20);
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
		autoscrollBtn.setSize(30, 100);
		autoscrollBtn.setEnabled(false);
		autoscrollBtn.setActionCommand("autoscrollBtn");
		autoscrollBtn.addActionListener(this);
        
		//Zoom
        zoom = new JSlider(0,3);
        Hashtable<Integer, JLabel> hashtable = new Hashtable<>();
        hashtable.put(0, new JLabel("50%"));
        hashtable.put(1, new JLabel("100%"));
        hashtable.put(2, new JLabel("150%"));
        hashtable.put(3, new JLabel("200%"));
        zoom.setLabelTable(hashtable);
        zoom.setPaintLabels(true);
        zoom.setSnapToTicks(true);
        zoom.addChangeListener(this);
		
		//Control Panel
		buttonPanel.add(trackCB);
		buttonPanel.add(simulation);
		buttonPanel.add(Box.createHorizontalStrut(50));
		buttonPanel.add(start);
		buttonPanel.add(clear);
		buttonPanel.add(Box.createHorizontalStrut(50));
		buttonPanel.add(exit);
		buttonPanel.add(Box.createHorizontalStrut(50));
		buttonPanel.add(new JLabel("Autoscroll:"));
		buttonPanel.add(autoscroll);
		buttonPanel.add(autoscrollBtn);
		buttonPanel.add(new JLabel("Zoom:"));
		buttonPanel.add(zoom);
		buttonPanel.add(Box.createHorizontalStrut(50));
		buttonPanel.add(drivers);
		buttonPanel.add(result);
	        
		
		//Board creating
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(1363,729));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.getWidth();
		screenHeight = screenSize.getHeight();
		
		board = new Board((int) screenWidth, (int)screenHeight, scrollPane);
		board.create();
		scrollPane.getViewport().add(board);
		
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
		}
	}
	
	/**
	 * Implemented from ChangeListener - Slider and Spinners actions
	 * Changed Zoom
	 * @param e
	 */
	public void stateChanged(ChangeEvent e) { }
	
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
