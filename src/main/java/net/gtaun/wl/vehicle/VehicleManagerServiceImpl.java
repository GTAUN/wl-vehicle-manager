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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
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
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;
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
		PlayerLifecycleObjectFactory<PlayerVehicleActuator> factory = new PlayerLifecycleObjectFactory<PlayerVehicleActuator>()
		{
			@Override
			public PlayerVehicleActuator create(Shoebill shoebill, EventManager eventManager, Player player)
			{
				return new PlayerVehicleActuator(shoebill, eventManager, player, VehicleManagerServiceImpl.this);
			}
		};
		playerLifecycleHolder.registerClass(PlayerVehicleActuator.class, factory);
		
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
	public Vehicle createOwnVehicle(Player player, int modelId)
	{
		Random random = new Random();
		
		Vehicle vehicle = shoebill.getSampObjectFactory().createVehicle(modelId, player.getLocation(), random.nextInt(256), random.nextInt(256), 3600);
		ownVehicle(player, vehicle);
		
		statisticManager.getPlayerVehicleStatistic(player, modelId).onSpawn();
		statisticManager.getGlobalVehicleStatistic(modelId).onSpawn();
		return vehicle;
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
	public GlobalVehicleStatistic getGlobalVehicleStatistic(int modelId)
	{
		return statisticManager.getGlobalVehicleStatistic(modelId);
	}

	@Override
	public Collection<GlobalVehicleStatistic> getGlobalVehicleStatistics()
	{
		return statisticManager.getGlobalVehicleStatistics();
	}

	@Override
	public PlayerVehicleStatistic getPlayerVehicleStatistic(Player player, int modelId)
	{
		return statisticManager.getPlayerVehicleStatistic(player, modelId);
	}

	@Override
	public Collection<PlayerVehicleStatistic> getPlayerVehicleStatistics(Player player)
	{
		return statisticManager.getPlayerVehicleStatistics(player);
	}
	
	@Override
	public OncePlayerVehicleStatistic getPlayerNowOnceStatistic(Player player)
	{
		return statisticManager.getPlayerNowOnceStatistic(player);
	}
	
	@Override
	public List<OncePlayerVehicleStatistic> getPlayerRecordedOnceStatistics(Player player)
	{
		return statisticManager.getPlayerRecordedOnceStatistics(player);
	}
	
	@Override
	public boolean isPlayerUnlimitedNOS(Player player)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		return actuator.isUnlimitedNOS();
	}
	
	@Override
	public void setPlayerUnlimitedNOS(Player player, boolean enabled)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		actuator.setUnlimitedNOS(enabled);
	}
	
	@Override
	public boolean isPlayerAutoRepair(Player player)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		return actuator.isAutoRepair();
	}

	@Override
	public void setPlayerAutoRepair(Player player, boolean enabled)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		actuator.setAutoRepair(enabled);
	}
	
	@Override
	public boolean isPlayerAutoFlip(Player player)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		return actuator.isAutoFlip();
	}
	
	public void setPlayerAutoFlip(Player player, boolean enabled)
	{
		PlayerVehicleActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleActuator.class);
		actuator.setAutoFlip(enabled);
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
