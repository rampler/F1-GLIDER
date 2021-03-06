package com.sispd.f1race;

import com.sispd.f1racing.Enums.Dryness;
import com.sispd.f1racing.Enums.Tire;
import com.sispd.f1racing.GUI.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class to view simulation parameters
 * @author Piotr Brudny, Kacper Furmański, Klaudia Kołdarz, Mateusz Kotlarz, Sabina Rydzek
 */
public class ConsoleFrame extends JFrame {
	private JPanel contentPane;
    private Board board;

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    JPanel bolids = new JPanel();
    JComboBox bolids1 = new JComboBox();
    JLabel lblMaxSpeed1 = new JLabel("Max speed:");
    JLabel acceleration1 = new JLabel("Max acceleration:");
    JLabel lblTurnforce1 = new JLabel("Turn force:");
    JLabel lblCurrentSpeed1 = new JLabel("Current speed:");
    JLabel lblCurrentAcceleration1 = new JLabel("Current acceleration:");
    JLabel acceleration2 = new JLabel("Max acceleration:");
    JComboBox bolids2 = new JComboBox();
    JLabel maxspeed2 = new JLabel("Max speed:");
    JLabel turnforce2 = new JLabel("Turn force:");
    JLabel currentspeed2 = new JLabel("Current speed:");
    JLabel curraccel2 = new JLabel("Current acceleration:");
    JPanel settings = new JPanel();
    JPanel settings2 = new JPanel();
    JLabel weather = new JLabel("Weather");
    JLabel debug = new JLabel("Debug");
    JCheckBox rainy = new JCheckBox("rainy");
    JCheckBox velocity = new JCheckBox("velocity");
    JCheckBox acceleration = new JCheckBox("acceleration");
    JCheckBox target = new JCheckBox("target");
    JCheckBox overtake = new JCheckBox("overtake");
    JCheckBox showInfo = new JCheckBox("Show clouds over drivers");
    final JSlider simSpeed = new JSlider(0,5);
    final JSpinner itDelay = new JSpinner(new SpinnerNumberModel(40, 1, 1000, 1));
    final JSpinner refDelay = new JSpinner(new SpinnerNumberModel(300, 1, 5000, 1));
    final JSpinner keepMinimumSpinner = new JSpinner(new SpinnerNumberModel(63, 1, 300, 1));
    ChartGenerator chartGenerator;

    public ConsoleFrame(final GUI parent, ChartGenerator chartGenerator, final Board board) {
        this.chartGenerator =  chartGenerator;
        this.board = board;

		contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		contentPane.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Settings", null, settings2, null);
        settings2.setLayout(new BorderLayout());
        settings2.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(6,2));

        //Track dryness
        String[] list = new String[2];
        int i=0;
        for(Dryness value : Dryness.values())
        {
            list[i] = value.toString();
            i++;
        }
        final JComboBox<String> drynessCB = new JComboBox<String>(list);
        drynessCB.setActionCommand("changedDryness");
        drynessCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Bolid bolid : board.getBolids()){
                    if(drynessCB.getSelectedIndex()==1){
                        bolid.setRain(true);
                        rainy.setSelected(true);
                    }
                    else{
                        bolid.setRain(false);
                        rainy.setSelected(false);
                    }
                }
            }
        });

        //Tires equipped
        list = new String[2];
        i=0;
        for(Tire value : Tire.values())
        {
            list[i] = value.toString();
            i++;
        }
        final JComboBox<String> tiresCB = new JComboBox<String>(list);
        //tiresCB.setActionCommand("changedTires");


        //TimersDelays
        itDelay.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                board.setTimerDelay((int) itDelay.getValue());
            }
        });
        refDelay.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                parent.setTimerDriversDelay((int) refDelay.getValue());
            }
        });
        keepMinimumSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for(Bolid bolid : board.getBolids())
                    bolid.setMinimumSpeed((int)keepMinimumSpinner.getValue()/9);
            }
        });


        optionsPanel.add(new JLabel(" Track dryness: "));
        optionsPanel.add(drynessCB);
        optionsPanel.add(new JLabel(" Tires equipped: "));
        optionsPanel.add(tiresCB);
        optionsPanel.add(new JLabel(" Iteration delay(ms):"));
        optionsPanel.add(itDelay);
        optionsPanel.add(new JLabel(" Results refreshing(ms):"));
        optionsPanel.add(refDelay);
        optionsPanel.add(new JLabel(" Keep minimum speed(km/h):"));
        optionsPanel.add(keepMinimumSpinner);

        optionsPanel.add(new JLabel(" Acceleration(m/s^2): "));
        optionsPanel.add(new JLabel(""));

        //Accelerations
        JPanel accPanel = new JPanel();
        accPanel.setLayout(new GridLayout(4,4));

        final JSpinner g100 = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 100.00, 0.01));
        g100.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for(Bolid bolid : board.getBolids())
                    bolid.setMinimumAcceleration((double) g100.getValue());
            }
        });
        final JSpinner g200 = new JSpinner(new SpinnerNumberModel(12.54, 0.00, 100.00, 0.01));
        g200.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for(Bolid bolid : board.getBolids())
                    bolid.setMaxForceAcceleration((double) g200.getValue() / 25 / 2.28);
            }
        });
        final JSpinner b100 = new JSpinner(new SpinnerNumberModel(0.00, -100.00, 0.00, 0.01));
        b100.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for(Bolid bolid : board.getBolids())
                    bolid.setMaxForceAcceleration(bolid.getMaxForceAcceleration() - ((double) b100.getValue() / 25 / 2.28));
            }
        });
        final JSpinner b200 = new JSpinner(new SpinnerNumberModel(-28.27, -100.00, 0.00, 0.01));
        b200.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for(Bolid bolid : board.getBolids())
                    bolid.setBreakingForceScale(-(double)b200.getValue()/25/2.28/0.49);
            }
        });

        JButton restore = new JButton("Default");
        restore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itDelay.setValue(40);
                refDelay.setValue(300);
                simSpeed.setValue(2);
                tiresCB.setSelectedIndex(0);
                drynessCB.setSelectedIndex(0);
                keepMinimumSpinner.setValue(63);

                //Table of accelerations
                double[][] table = defaultAccelerationTable();
                g100.setValue(table[0][0]);
                g200.setValue(table[0][1]);
                b100.setValue(table[1][0]);
                b200.setValue(table[1][1]);
            }

            /**
             * Returning table of default accelerations values
             * In first row is reaction to gas
             * In second - break
             * In last - without gas or break
             * @return - default acceleration table
             */
            private double[][] defaultAccelerationTable() {
                double[][] table = new double[2][2];
                //gas
                table[0][0] = 0; //0-100
                table[0][1] = 12.54; //100-200
                //break
                table[1][0] = 0; //0-100
                table[1][1] = -28.27; //100-200
                return table;
            }
        });

        accPanel.add(new JLabel(""));
        accPanel.add(new JLabel(""));
        accPanel.add(new JLabel("Min:"));
        accPanel.add(new JLabel("Max:"));

        accPanel.add(new JLabel(""));
        accPanel.add(new JLabel("Gas"));
        accPanel.add(g100);
        accPanel.add(g200);

        accPanel.add(new JLabel(""));
        accPanel.add(new JLabel("Break"));
        accPanel.add(b100);
        accPanel.add(b200);

        accPanel.add(restore);
        accPanel.add(new JLabel(""));
        accPanel.add(new JLabel(""));
        accPanel.add(new JLabel(""));

        JPanel headerPanel = new JPanel(new GridLayout(1,2));

        //Simulation Speed
        Hashtable<Integer, JLabel> hashtable = new Hashtable<>();
        hashtable.put(0, new JLabel("0.25x"));
        hashtable.put(1, new JLabel("0.5x"));
        hashtable.put(2, new JLabel("1x"));
        hashtable.put(3, new JLabel("1.5x"));
        hashtable.put(4, new JLabel("2x"));
        hashtable.put(5, new JLabel("3x"));
        simSpeed.setLabelTable(hashtable);
        simSpeed.setValue(2);
        simSpeed.setPaintLabels(true);
        simSpeed.setSnapToTicks(true);
        simSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                switch (simSpeed.getValue())
                {
                    case 0 : board.setSimulationSpeed(0.25); break;
                    case 1 : board.setSimulationSpeed(0.5); break;
                    case 2 : board.setSimulationSpeed(1); break;
                    case 3 : board.setSimulationSpeed(1.5); break;
                    case 4 : board.setSimulationSpeed(2); break;
                    case 5 : board.setSimulationSpeed(3); break;
                }
            }
        });

        headerPanel.add(new JLabel(" Simulation speed:"));
        headerPanel.add(simSpeed);

        settings2.add(optionsPanel, BorderLayout.CENTER);
        settings2.add(accPanel, BorderLayout.SOUTH);
        settings2.add(headerPanel, BorderLayout.NORTH);

		tabbedPane.addTab("Debug", null, settings, null);
		settings.setLayout(null);

		weather.setBounds(10, 11, 170, 14);
		settings.add(weather);


		debug.setBounds(10, 70, 170, 14);
		settings.add(debug);

		rainy.setBounds(124, 7, 97, 23);
		settings.add(rainy);
		rainy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : board.getBolids()) {
                    bolid.setRain(rainy.isSelected());
                }
            }
        });

		velocity.setBounds(124, 66, 97, 23);
		settings.add(velocity);
        velocity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : board.getBolids()) {
                    bolid.setVelocityMode(velocity.isSelected());
                }
                board.repaint();
            }
        });
		
		acceleration.setBounds(124, 92, 97, 23);
		settings.add(acceleration);
        acceleration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : board.getBolids()) {
                    bolid.setSteerForceMode(acceleration.isSelected());
                }
                board.repaint();
            }
        });
		
		target.setBounds(124, 118, 97, 23);
		settings.add(target);
        target.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : board.getBolids()) {
                    bolid.setTargetMode(target.isSelected());
                }
                board.repaint();
            }
        });
		
		overtake.setBounds(124, 144, 97, 23);
		settings.add(overtake);
        overtake.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : board.getBolids()) {
                    bolid.setOvertakingMode(overtake.isSelected());
                }
                board.repaint();
            }
        });
        
        showInfo.setBounds(124, 170, 170, 23);
        showInfo.setSelected(true);
		settings.add(showInfo);
		showInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.setShowInfo(showInfo.isSelected());
                board.repaint();
            }
        });

        tabbedPane.addTab("Bolids info", null, bolids, null);
        bolids.setLayout(null);

        bolids1.setModel(new DefaultComboBoxModel());
        bolids1.setBounds(10, 11, 170, 20);
        bolids.add(bolids1);

        lblMaxSpeed1.setBounds(10, 46, 170, 14);
        bolids.add(lblMaxSpeed1);

        acceleration1.setBounds(10, 71, 170, 14);
        bolids.add(acceleration1);

        lblTurnforce1.setBounds(10, 96, 170, 14);
        bolids.add(lblTurnforce1);

        lblCurrentSpeed1.setBounds(10, 121, 170, 14);
        bolids.add(lblCurrentSpeed1);

        lblCurrentAcceleration1.setBounds(10, 146, 170, 14);
        bolids.add(lblCurrentAcceleration1);

        acceleration2.setBounds(196, 71, 170, 14);
        bolids.add(acceleration2);

        bolids2.setBounds(196, 11, 170, 20);
        bolids.add(bolids2);

        maxspeed2.setBounds(196, 46, 170, 14);
        bolids.add(maxspeed2);

        turnforce2.setBounds(196, 96, 170, 14);
        bolids.add(turnforce2);

        currentspeed2.setBounds(196, 121, 170, 14);
        bolids.add(currentspeed2);

        curraccel2.setBounds(196, 146, 170, 14);
        bolids.add(curraccel2);

        btnZapiszStatystyki.setEnabled(false);
        btnZapiszStatystyki.setBounds(10, 170, 131, 23);
        btnZapiszStatystyki.addActionListener(new ZapiszStatystyki());

        bolids.add(btnZapiszStatystyki);
	}

    Map<String, Integer> currentBolids = new HashMap<String, Integer>();
    private final JButton btnZapiszStatystyki = new JButton("Zapisz statystyki");

    public void updateBolidsLists() {
        for(Bolid bolid : board.getBolids()) {
            if(!currentBolids.containsKey(bolid.getName())) {
                bolids1.addItem(bolid);
                bolids2.addItem(bolid);
                currentBolids.put(bolid.getName(), bolids1.getItemCount()-1);
            }
        }
    }



    public void updateFields() {
        Bolid bolid1 = (Bolid) bolids1.getSelectedItem();
        Bolid bolid2 = (Bolid) bolids2.getSelectedItem();

        DecimalFormat f = new DecimalFormat("00.00");

        if(bolid1!=null) {
            lblMaxSpeed1.setText("Max speed: " + f.format(bolid1.getMaxSpeed()*25*0.36) + " km/h");
            lblCurrentSpeed1.setText("Current speed: " + f.format(bolid1.getVelocity().magnitude()*25*0.36) + " km/h");
            lblCurrentAcceleration1.setText("Current accel.: " + f.format(bolid1.getAcceleration().magnitude()*25*2.28) + " m/s2");
            lblTurnforce1.setText("Turn force: " + f.format(bolid1.getTurnForce().magnitude()*25*1.28) + " m/s2");
            acceleration1.setText("Max acceleration: " + f.format(bolid1.getMaxForceAcceleration()*25*2.28) + " m/s2");
        }

        if(bolid2!=null) {
            maxspeed2.setText("Max speed: " + f.format(bolid2.getMaxSpeed()*25*0.36) + " km/h");
            currentspeed2.setText("Current speed: " + f.format(bolid2.getVelocity().magnitude()*25*0.36) + " km/h");
           curraccel2.setText("Current accel.: " + f.format(bolid2.getAcceleration().magnitude() * 25 * 2.28) + " m/s2");
           turnforce2.setText("Turn force: " + f.format(bolid2.getTurnForce().magnitude()*25*1.28) + " m/s2");
            acceleration2.setText("Max acceleration: " + f.format(bolid2.getMaxForceAcceleration()*25*2.28) + " m/s2");
        }

        boolean finish = true;
        if (!board.getBolids().isEmpty()) {
            for(Bolid bolid : board.getBolids()) {
               if (bolid.getLaps() < 2) {
                   finish = false;
               }
            }

            if (finish) {
                btnZapiszStatystyki.setEnabled(true);
            }
        }
    }

    class ZapiszStatystyki implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
           chartGenerator.saveImage();
        }
    }


}
