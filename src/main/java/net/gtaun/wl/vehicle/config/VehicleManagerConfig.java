/**
 * Copyright (C) 2011 JoJLlmAn
 * Copyright (C) 2011-2012 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.wl.vehicle.config;

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