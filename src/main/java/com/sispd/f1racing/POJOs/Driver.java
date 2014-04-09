package com.sispd.f1racing.POJOs;

import com.sispd.f1racing.Enums.DriverSkill;

public class Driver {
	
	private String name;
	private DriverSkill driverSkill;
	
	public Driver(String name, DriverSkill driverSkill)
	{
		this.name = name;
		this.driverSkill = driverSkill;
	}
	
	public String getName(){ return name; }
	public DriverSkill getDriverSkill(){ return driverSkill; }

}
