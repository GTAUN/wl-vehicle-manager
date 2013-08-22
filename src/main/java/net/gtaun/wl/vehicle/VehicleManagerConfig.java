/**
 * WL Vehicle Manager Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.vehicle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.gtaun.shoebill.util.config.YamlConfiguration;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VehicleManagerConfig
{
	private String dbHost;
	private String dbName;
	private String dbUser;
	private String dbPass;
	private String menuCommand;
	private String myVehicleMenuCommand;
	private float nearbyVehicleDistance;
	
	public VehicleManagerConfig(File file) throws FileNotFoundException
	{
		YamlConfiguration config = new YamlConfiguration(new File("config.yml"));
		config.setDefault("mongodb.host", "localhost:27017");
		config.setDefault("mongodb.dbName", "VehicleManager");
		config.setDefault("mongodb.user", "");
		config.setDefault("mongodb.pass", "");	
		config.setDefault("menuCommand.vehicleManager", "/v");
		config.setDefault("menuCommand.myVehicle", "/myveh");
		config.setDefault("nearbyVehicleDialog.distance", "500.0");
		
		config.read(new FileInputStream(file));

		dbHost = config.getString("mongodb.host");
		dbName = config.getString("mongodb.dbName");
		dbUser = config.getString("mongodb.user");
		dbPass = config.getString("mongodb.pass");
		menuCommand = config.getString("menuCommand.vehicleManager");
		myVehicleMenuCommand = config.getString("menuCommand.myVehicle");
		nearbyVehicleDistance = config.getFloat("nearbyVehicleDialog.distance");
	}
	
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
	public String getDbHost()
	{
		return dbHost;
	}
	
	public String getDbName()
	{
		return dbName;
	}
	
	public String getDbUser()
	{
		return dbUser;
	}
	
	public String getDbPass()
	{
		return dbPass;
	}
	
	public String getMenuCommand()
	{
		return menuCommand;
	}
	
	public String getMyVehicleMenuCommand()
	{
		return myVehicleMenuCommand;
	}
	
	public float getNearbyVehicleDistance()
	{
		return nearbyVehicleDistance;
	}
}