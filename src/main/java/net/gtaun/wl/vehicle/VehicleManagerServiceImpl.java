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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.vehicle.dialog.VehicleManagerDialog;
import net.gtaun.wl.vehicle.stat.VehicleStatisticManager;

import com.google.code.morphia.Datastore;

public class VehicleManagerServiceImpl implements VehicleManagerService
{
	private final Shoebill shoebill;
	private final EventManager rootEventManager;
	private final Datastore datastore;
	
	private final ManagedEventManager eventManager;
	private final PlayerLifecycleHolder playerLifecycleHolder;
	
	private boolean isCommandEnabled = true;
	private String commandOperation = "/v";
	
	private VehicleStatisticManager statisticManager;
	
	private Map<Player, Vehicle> playerOwnedVehicles;
	private Set<Vehicle> ownedVehicles;
	
	
	public VehicleManagerServiceImpl(Shoebill shoebill, EventManager rootEventManager, Datastore datastore)
	{
		this.shoebill = shoebill;
		this.rootEventManager = rootEventManager;
		this.datastore = datastore;
		
		eventManager = new ManagedEventManager(rootEventManager);
		playerLifecycleHolder = new PlayerLifecycleHolder(shoebill, eventManager);
		
		statisticManager = new VehicleStatisticManager(shoebill, eventManager, playerLifecycleHolder, this.datastore);
		
		playerOwnedVehicles = new HashMap<>();
		ownedVehicles = new HashSet<>();
		
		initialize();
	}
	
	private void initialize()
	{
		playerLifecycleHolder.registerClass(PlayerVehicleActuator.class);
		
		eventManager.registerHandler(PlayerConnectEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerCommandEvent.class, playerEventHandler, HandlerPriority.NORMAL);
	}

	public void uninitialize()
	{
		playerLifecycleHolder.destroy();
		statisticManager.destroy();
		eventManager.cancelAll();
	}

	@Override
	public void ownVehicle(Player player, Vehicle vehicle)
	{
		if (playerOwnedVehicles.containsKey(player)) unownVehicle(player);
		
		playerOwnedVehicles.put(player, vehicle);
		ownedVehicles.add(vehicle);
	}
	
	@Override
	public void unownVehicle(Player player)
	{
		Vehicle vehicle = playerOwnedVehicles.get(player);
		if (vehicle == null) return;
		
		playerOwnedVehicles.remove(player);
		ownedVehicles.remove(vehicle);
		
		if (vehicle.isStatic() == false) vehicle.destroy();
	}
	
	@Override
	public Vehicle getOwnedVehicle(Player player)
	{
		Vehicle vehicle = playerOwnedVehicles.get(player);
		if (vehicle == null) return null;
		
		if (vehicle.isDestroyed())
		{
			unownVehicle(player);
			vehicle = null;
		}
		
		return vehicle;
	}

	@Override
	public boolean isOwned(Vehicle vehicle)
	{
		return ownedVehicles.contains(vehicle);
	}
	
	@Override
	public boolean isPlayerLockNos(Player player)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		return actuator.isLockNOS;
	}
	
	@Override
	public void setPlayerLockNos(Player player, boolean lock)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		actuator.isLockNOS = lock;
	}
	
	@Override
	public boolean isPlayerLockVehicleHealth(Player player)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		return actuator.isLockVHP;
	}

	@Override
	public void setPlayerLockVehicleHealth(Player player, boolean lock)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		actuator.isLockVHP = lock;
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerConnect(PlayerConnectEvent event)
		{
			
		}
		
		protected void onPlayerDisconnect(PlayerDisconnectEvent event)
		{
			Player player = event.getPlayer();
			unownVehicle(player);
		}
		
		protected void onPlayerCommand(PlayerCommandEvent event)
		{
			if (isCommandEnabled == false) return;
			
			Player player = event.getPlayer();
			
			String command = event.getCommand();
			String[] splits = command.split(" ", 2);
			
			String operation = splits[0].toLowerCase();
			Queue<String> args = new LinkedList<>();
			
			if (splits.length > 1)
			{
				String[] argsArray = splits[1].split(" ");
				args.addAll(Arrays.asList(argsArray));
			}
			
			if (operation.equals(commandOperation))
			{
				if (args.size() > 1)
				{
					player.sendMessage(Color.YELLOW, "Usage: " + commandOperation + " [VehicleID]");
					event.setProcessed();
					return;
				}
				
				new VehicleManagerDialog(player, shoebill, rootEventManager, VehicleManagerServiceImpl.this).show();
				event.setProcessed();
				return;
			}
		}
	};
}