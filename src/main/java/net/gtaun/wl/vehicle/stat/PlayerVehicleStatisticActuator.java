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

package net.gtaun.wl.vehicle.stat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.vehicle.VehicleUtils;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic.StatisticType;

import com.google.code.morphia.Datastore;

public class PlayerVehicleStatisticActuator extends AbstractPlayerVehicleProbe
{
	private final VehicleStatisticManager statisticManager;
	private final Datastore datastore;
	
	private Map<Integer, PlayerVehicleStatisticImpl> vehicleStatistics;
	
	private LinkedList<OncePlayerVehicleStatisticImpl> recordedOnceStatistics;
	private OncePlayerVehicleStatisticImpl nowOnceStatistic;
	
	
	public PlayerVehicleStatisticActuator(Shoebill shoebill, EventManager rootEventManager, Player player, VehicleStatisticManager statisticManager, Datastore datastore)
	{
		super(shoebill, rootEventManager, player);
		this.statisticManager = statisticManager;
		this.datastore = datastore;
		
		vehicleStatistics = new HashMap<>();
		recordedOnceStatistics = new LinkedList<>();
		
		allowState(PlayerState.PASSENGER);
	}
	
	@Override
	protected void onInit()
	{
		load();
		super.onInit();
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle != null)
		{
			StatisticType type = VehicleUtils.isVehicleDriver(vehicle, player) ? StatisticType.DRIVER : StatisticType.PASSENGER;
			nowOnceStatistic = new OncePlayerVehicleStatisticImpl(shoebill, rootEventManager, player, type);
			nowOnceStatistic.start();
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		save();
	}
	
	public void load()
	{
		String uniqueId = player.getName();
		
		List<PlayerVehicleStatisticImpl> statistics = datastore.createQuery(PlayerVehicleStatisticImpl.class).filter("playerUniqueId", uniqueId).asList();
		for (PlayerVehicleStatisticImpl statistic : statistics)
		{
			statistic.setPlayer(player);
			vehicleStatistics.put(statistic.getModelId(), statistic);
		}
		
		for (int id : VehicleModel.getIds())
		{
			if (vehicleStatistics.containsKey(id)) continue;
			
			PlayerVehicleStatisticImpl statistic = new PlayerVehicleStatisticImpl(player, uniqueId, id);
			vehicleStatistics.put(id, statistic);
		}
	}
	
	public void save()
	{
		datastore.save(vehicleStatistics.values());
	}
	
	public PlayerVehicleStatisticImpl getVehicleStatistic(int modelId)
	{
		PlayerVehicleStatisticImpl statistic = vehicleStatistics.get(modelId);
		return statistic;
	}
	
	public Collection<PlayerVehicleStatisticImpl> getVehicleStatistics()
	{
		return vehicleStatistics.values();
	}
	
	public OncePlayerVehicleStatistic getCurrentOnceStatistic()
	{
		return nowOnceStatistic;
	}
	
	public OncePlayerVehicleStatisticImpl startRacingStatistic()
	{
		if (nowOnceStatistic != null) nowOnceStatistic.end();
		
		nowOnceStatistic = new OncePlayerVehicleStatisticImpl(shoebill, rootEventManager, player, StatisticType.RACING);
		nowOnceStatistic.start();
		recordedOnceStatistics.offerFirst(nowOnceStatistic);
		
		return nowOnceStatistic;
	}
	
	public void endRacingStatistic()
	{
		
	}
	
	public boolean isRacingStatistic()
	{
		return false;
	}
	
	public List<OncePlayerVehicleStatistic> getRecordedOnceStatistics()
	{
		return Collections.unmodifiableList((List<? extends OncePlayerVehicleStatistic>) recordedOnceStatistics);
	}
	
	@Override
	protected void onDriveVehicle(Vehicle vehicle)
	{
		int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDrive();
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDrive();
		
		if (nowOnceStatistic == null || nowOnceStatistic.getType() != StatisticType.RACING)
		{
			nowOnceStatistic = new OncePlayerVehicleStatisticImpl(shoebill, rootEventManager, player, StatisticType.DRIVER);
			nowOnceStatistic.start();
			recordedOnceStatistics.offerFirst(nowOnceStatistic);
		}
	}
	
	@Override
	protected void onBecomePassenger(Vehicle vehicle)
	{
		if (nowOnceStatistic == null || nowOnceStatistic.getType() != StatisticType.RACING)
		{
			nowOnceStatistic = new OncePlayerVehicleStatisticImpl(shoebill, rootEventManager, player, StatisticType.PASSENGER);
			nowOnceStatistic.start();
			recordedOnceStatistics.offerFirst(nowOnceStatistic);
		}
	}
	
	@Override
	protected void onLeaveVehicle(Vehicle vehicle)
	{
		if (nowOnceStatistic != null && nowOnceStatistic.getType() != StatisticType.RACING)
		{
			nowOnceStatistic.end();
			nowOnceStatistic = null;
		}
	}
	
	@Override
	protected void onVehicleDamage(Vehicle vehicle, float damage)
	{
		final int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDamage(damage);
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDamage(damage);
	}
	
	@Override
	protected void onVehicleTick(Vehicle vehicle)
	{
		final int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDriveTick();
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDriveTick();
	}
	
	@Override
	protected void onVehicleMove(Vehicle vehicle, float distance)
	{
		final int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDriveMove(distance);
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDriveMove(distance);
	}
}
