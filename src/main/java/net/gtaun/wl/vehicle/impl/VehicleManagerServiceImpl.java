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

package net.gtaun.wl.vehicle.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.TimerEventHandler;
import net.gtaun.shoebill.event.VehicleEventHandler;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.timer.TimerTickEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateDamageEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.dialog.VehicleManagerDialog;

public class VehicleManagerServiceImpl implements VehicleManagerService
{
	private class PlayerTimerStore
	{
		private final Player player;
		private final Timer timer;
		private final EventManager eventManager;
		
		private boolean isLockNOS;
		private boolean isLockVHP;
		
		public PlayerTimerStore(Player player)
		{
			eventManager = new ManagedEventManager(VehicleManagerServiceImpl.this.eventManager);
			
			this.player = player;
			timer = shoebill.getSampObjectFactory().createTimer(10000);
			
			eventManager.registerHandler(TimerTickEvent.class, timer, timerEventHandler, HandlerPriority.NORMAL);
			eventManager.registerHandler(VehicleUpdateDamageEvent.class, timer, vehicleEventHandler, HandlerPriority.NORMAL);
			timer.start();
		}
		
		private TimerEventHandler timerEventHandler = new TimerEventHandler()
		{
			protected void onTimerTick(TimerTickEvent event)
			{
				if (player.isInAnyVehicle())
				{
					Vehicle vehicle = player.getVehicle();
					int vehicleModel = vehicle.getModelId();
					if (isLockNOS && VehicleComponentModel.isVehicleSupported(vehicleModel, VehicleComponentModel.NITRO_10_TIMES))
					{
						vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
					}
				}
			}
		};
		
		VehicleEventHandler vehicleEventHandler = new VehicleEventHandler()
		{
			protected void onVehicleUpdateDamage(VehicleUpdateDamageEvent event)
			{
				Vehicle vehicle = event.getVehicle();
				if (isLockVHP && vehicle == player.getVehicle()) vehicle.repair();
			}
		};
		
		public void destroy()
		{
			timer.destroy();
		}
	}
	
	
	private final Shoebill shoebill;
	private final EventManager rootEventManager;
	
	private final ManagedEventManager eventManager;
	
	private boolean isCommandEnabled = true;
	private String commandOperation = "/v";
	
	private Map<Player, Vehicle> playerOwnedVehicles;
	private Set<Vehicle> ownedVehicles;
	
	private Map<Player, PlayerTimerStore> playerTimerStores;
	
	
	public VehicleManagerServiceImpl(Shoebill shoebill, EventManager rootEventManager)
	{
		this.shoebill = shoebill;
		this.rootEventManager = rootEventManager;
		
		eventManager = new ManagedEventManager(rootEventManager);
		
		playerOwnedVehicles = new HashMap<>();
		ownedVehicles = new HashSet<>();
		
		playerTimerStores = new HashMap<>();
		
		initialize();
	}
	
	private void initialize()
	{
		eventManager.registerHandler(PlayerConnectEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerCommandEvent.class, playerEventHandler, HandlerPriority.NORMAL);
	}

	public void uninitialize()
	{
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
		PlayerTimerStore timerStore = playerTimerStores.get(player);
		return timerStore.isLockNOS;
	}
	
	@Override
	public void setPlayerLockNos(Player player, boolean lock)
	{
		PlayerTimerStore timerStore = playerTimerStores.get(player);
		timerStore.isLockNOS = lock;
	}
	
	@Override
	public boolean isPlayerLockVehicleHealth(Player player)
	{
		PlayerTimerStore timerStore = playerTimerStores.get(player);
		return timerStore.isLockVHP;
	}

	@Override
	public void setPlayerLockVehicleHealth(Player player, boolean lock)
	{
		PlayerTimerStore timerStore = playerTimerStores.get(player);
		timerStore.isLockVHP = lock;
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerConnect(PlayerConnectEvent event)
		{
			Player player = event.getPlayer();
			playerTimerStores.put(player, new PlayerTimerStore(player));
		}
		
		protected void onPlayerDisconnect(PlayerDisconnectEvent event)
		{
			Player player = event.getPlayer();
			unownVehicle(player);
			
			playerTimerStores.get(player).destroy();
			playerTimerStores.remove(player);
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
