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

import net.gtaun.shoebill.common.ConfigurablePlugin;
import net.gtaun.wl.vehicle.impl.VehicleManagerServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleManagerPlugin extends ConfigurablePlugin
{
	public static final Logger LOGGER = LoggerFactory.getLogger(VehicleManagerPlugin.class);
	
	
	private VehicleManagerServiceImpl vehicleManagerSerivce;
	
	
	public VehicleManagerPlugin()
	{
		
	}
	
	@Override
	protected void onEnable() throws Throwable
	{
		vehicleManagerSerivce = new VehicleManagerServiceImpl(getShoebill(), getEventManager());
		registerService(VehicleManagerService.class, vehicleManagerSerivce);
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Enabled.");
	}
	
	@Override
	protected void onDisable() throws Throwable
	{
		unregisterService(VehicleManagerService.class);
		
		vehicleManagerSerivce.uninitialize();
		vehicleManagerSerivce = null;
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Disabled.");
	}
}
