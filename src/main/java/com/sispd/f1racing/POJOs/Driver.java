package com.sispd.f1racing.POJOs;

import com.sispd.f1racing.Enums.DriverSkill;
import com.sispd.f1racing.Enums.Team;

/**
 * Information about driver
 * @author Piotr Brudny, Kacper Furmański, Klaudia Kołdarz, Mateusz Kotlarz, Sabina Rydzek
 */
public class Driver {
	
	private String name;
	private DriverSkill driverSkill;
    private Team team;
	
	public Driver(String name, DriverSkill driverSkill, Team team)
	{
		this.name = name;
		this.driverSkill = driverSkill;
        this.team = team;
	}
	
	public String getName(){ return name; }
	public DriverSkill getDriverSkill(){ return driverSkill; }
    public Team getTeam(){ return team; }

}
