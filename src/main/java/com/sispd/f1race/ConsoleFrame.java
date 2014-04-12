package com.sispd.f1race;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 * Class to view simulation parameters
 * @author Piotr Brudny, Kacper Furmański, Klaudia Kołdarz, Mateusz Kotlarz, Sabina Rydzek
 */
public class ConsoleFrame extends JFrame {
	private JPanel contentPane;
    private List<Bolid> bolidsArray;
    Board animationTest;

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
    JLabel weather = new JLabel("Weather");
    JLabel debug = new JLabel("Debug");
    JCheckBox rainy = new JCheckBox("rainy");
    JCheckBox velocity = new JCheckBox("velocity");
    JCheckBox acceleration = new JCheckBox("acceleration");
    JCheckBox target = new JCheckBox("target");
    JCheckBox overtake = new JCheckBox("overtake");
    JCheckBox showInfo = new JCheckBox("Show clouds over drivers");
    ChartGenerator chartGenerator;




    public ConsoleFrame(final List<Bolid> bolidsArray, ChartGenerator chartGenerator, final Board animationTest) {
        this.bolidsArray = bolidsArray;
        this.chartGenerator =  chartGenerator;
        this.animationTest = animationTest;

		contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		contentPane.add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Bolids", null, bolids, null);
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
		btnZapiszStatystyki.setBounds(10, 257, 131, 23);
        btnZapiszStatystyki.addActionListener(new ZapiszStatystyki());
		
		bolids.add(btnZapiszStatystyki);

		tabbedPane.addTab("Settings", null, settings, null);
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
                for (Bolid bolid : bolidsArray) {
                    bolid.setRain(rainy.isSelected());
                }
            }
        });

		velocity.setBounds(124, 66, 97, 23);
		settings.add(velocity);
        velocity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : bolidsArray) {
                    bolid.setVelocityMode(velocity.isSelected());
                }
                animationTest.repaint();
            }
        });
		
		acceleration.setBounds(124, 92, 97, 23);
		settings.add(acceleration);
        acceleration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : bolidsArray) {
                    bolid.setSteerForceMode(acceleration.isSelected());
                }
                animationTest.repaint();
            }
        });
		
		target.setBounds(124, 118, 97, 23);
		settings.add(target);
        target.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : bolidsArray) {
                    bolid.setTargetMode(target.isSelected());
                }
                animationTest.repaint();
            }
        });
		
		overtake.setBounds(124, 144, 97, 23);
		settings.add(overtake);
        overtake.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Bolid bolid : bolidsArray) {
                    bolid.setOvertakingMode(overtake.isSelected());
                }
                animationTest.repaint();
            }
        });
        
        showInfo.setBounds(124, 170, 170, 23);
        showInfo.setSelected(true);
		settings.add(showInfo);
		showInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationTest.setShowInfo(showInfo.isSelected());
                animationTest.repaint();
            }
        });
	}

    Map<String, Integer> currentBolids = new HashMap<String, Integer>();
    private final JButton btnZapiszStatystyki = new JButton("Zapisz statystyki");

    public void updateBolidsLists() {
        for(Bolid bolid : bolidsArray) {
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
        if (!bolidsArray.isEmpty()) {
            for(Bolid bolid : bolidsArray) {
               if (bolid.getLaps() < 3) {
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
