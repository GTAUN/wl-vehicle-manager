/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.vehicle;

import java.io.File;

import net.gtaun.shoebill.common.ConfigurablePlugin;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatisticImpl;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatisticImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.DefaultCreator;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class VehicleManagerPlugin extends ConfigurablePlugin
{
	public static final Logger LOGGER = LoggerFactory.getLogger(VehicleManagerPlugin.class);
	
	
	private VehicleManagerConfig config;
	
	private MongoClient mongoClient;
	private Morphia morphia;
	private Datastore datastore;
	
	private VehicleManagerServiceImpl vehicleManagerSerivce;
	
	
	public VehicleManagerPlugin()
	{
		
	}
	
	@Override
	protected void onEnable() throws Throwable
	{
		config = new VehicleManagerConfig(new File(getDataDir(), "config.yml"));
		
		mongoClient = new MongoClient(config.getDbHost());
		
		morphia = new Morphia();
		morphia.getMapper().getOptions().objectFactory = new DefaultCreator()
		{
            @Override
            protected ClassLoader getClassLoaderForClass(String clazz, DBObject object)
            {
                return getClass().getClassLoader();
            }
        };
		morphia.map(GlobalVehicleStatisticImpl.class);
		morphia.map(PlayerVehicleStatisticImpl.class);
		
		if (config.getDbUser().isEmpty() || config.getDbPass().isEmpty())
		{
			datastore = morphia.createDatastore(mongoClient, config.getDbName());
		}
		else
		{
			datastore = morphia.createDatastore(mongoClient, config.getDbName(), config.getDbUser(), config.getDbPass().toCharArray());
		}
		
		vehicleManagerSerivce = new VehicleManagerServiceImpl(getShoebill(), getEventManager(), this, datastore);
		registerService(VehicleManagerService.class, vehicleManagerSerivce);
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Enabled.");
	}
	
	@Override
	protected void onDisable() throws Throwable
	{
		unregisterService(VehicleManagerService.class);
		
		vehicleManagerSerivce.uninitialize();
		vehicleManagerSerivce = null;
		
		datastore = null;
		morphia = null;
		
		mongoClient.close();
		mongoClient = null;
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Disabled.");
	}
}
