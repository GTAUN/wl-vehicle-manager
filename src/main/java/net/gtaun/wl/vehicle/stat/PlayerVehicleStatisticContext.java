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

package net.gtaun.wl.vehicle.stat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.common.vehicle.VehicleUtils;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic.StatisticType;

import org.mongodb.morphia.Datastore;

public class PlayerVehicleStatisticContext extends AbstractPlayerVehicleProbe
{
	private final VehicleStatisticManager statisticManager;
	private final Datastore datastore;
	
	private Map<Integer, PlayerVehicleStatisticImpl> vehicleStatistics;
	
	private LinkedList<OncePlayerVehicleStatisticImpl> recordedOnceStatistics;
	private OncePlayerVehicleStatisticImpl nowOnceStatistic;
	
	
	public PlayerVehicleStatisticContext(EventManager rootEventManager, Player player, VehicleStatisticManager statisticManager, Datastore datastore)
	{
		super(rootEventManager, player);
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
			nowOnceStatistic = new OncePlayerVehicleStatisticImpl(rootEventManager, player, type);
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

	private void tryStartOnceStatistic()
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		if (nowOnceStatistic != null) endOnceStatistic();
		
		StatisticType type = VehicleUtils.isVehicleDriver(vehicle, player) ? StatisticType.DRIVER : StatisticType.PASSENGER;
		nowOnceStatistic = new OncePlayerVehicleStatisticImpl(rootEventManager, player, type);
		nowOnceStatistic.start();
		recordedOnceStatistics.offerFirst(nowOnceStatistic);
	}
	
	private void endOnceStatistic()
	{
		if (nowOnceStatistic == null) return;
		
		nowOnceStatistic.end();
		nowOnceStatistic = null;
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
		endOnceStatistic();
		
		nowOnceStatistic = new OncePlayerVehicleStatisticImpl(rootEventManager, player, StatisticType.RACING);
		nowOnceStatistic.start();
		recordedOnceStatistics.offerFirst(nowOnceStatistic);
		
		return nowOnceStatistic;
	}
	
	public void endRacingStatistic()
	{
		endOnceStatistic();
		tryStartOnceStatistic();
	}
	
	public boolean isRacingStatistic()
	{
		if (nowOnceStatistic == null) return false;
		return nowOnceStatistic.getType() == StatisticType.RACING;
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
			tryStartOnceStatistic();
		}
	}
	
	@Override
	protected void onBecomePassenger(Vehicle vehicle)
	{
		if (nowOnceStatistic == null || nowOnceStatistic.getType() != StatisticType.RACING)
		{
			tryStartOnceStatistic();
		}
	}
	
	@Override
	protected void onLeaveVehicle(Vehicle vehicle)
	{
		if (nowOnceStatistic != null && nowOnceStatistic.getType() != StatisticType.RACING)
		{
			endOnceStatistic();
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
