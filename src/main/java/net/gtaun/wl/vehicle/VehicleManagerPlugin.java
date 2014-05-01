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
import java.util.Arrays;

import net.gtaun.shoebill.common.ConfigurablePlugin;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatisticImpl;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatisticImpl;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

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
		
		if (config.getDbUser().isEmpty() || config.getDbPass().isEmpty())
		{
			mongoClient = new MongoClient(config.getDbHost());
		}
		else
		{
			mongoClient = new MongoClient
			(
				Arrays.asList(new ServerAddress(config.getDbHost())),
				Arrays.asList(MongoCredential.createMongoCRCredential(config.getDbName(), config.getDbName(), config.getDbPass().toCharArray()))
			);
		}
		
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
		
		datastore = morphia.createDatastore(mongoClient, config.getDbName());
		
		vehicleManagerSerivce = new VehicleManagerServiceImpl(getEventManager(), this, datastore);
		registerService(VehicleManagerService.class, vehicleManagerSerivce);
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Enabled.");
	}
	
	@Override
	protected void onDisable() throws Throwable
	{
		unregisterService(VehicleManagerService.class);
		
		vehicleManagerSerivce.destroy();
		vehicleManagerSerivce = null;
		
		datastore = null;
		morphia = null;
		
		mongoClient.close();
		mongoClient = null;
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Disabled.");
	}
}
