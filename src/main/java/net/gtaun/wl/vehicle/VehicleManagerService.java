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

import java.util.Collection;
import java.util.List;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.service.Service;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

public interface VehicleManagerService extends Service
{
	Vehicle createOwnVehicle(Player player, int modelId);
	
	/**
	 * 设置玩家拥有的车辆。
	 * 
	 * @param player 目标玩家
	 * @param vehicle 目标车辆
	 */
	void ownVehicle(Player player, Vehicle vehicle);
	void unownVehicle(Player player);
	
	Vehicle getOwnedVehicle(Player player);
	boolean isOwned(Vehicle vehicle);

	PlayerPreferences getPlayerPreferences(Player player);

	GlobalVehicleStatistic getGlobalVehicleStatistic(int modelId);
	Collection<GlobalVehicleStatistic> getGlobalVehicleStatistics();

	PlayerVehicleStatistic getPlayerVehicleStatistic(Player player, int modelId);
	Collection<PlayerVehicleStatistic> getPlayerVehicleStatistics(Player player);

	OncePlayerVehicleStatistic getPlayerCurrentOnceStatistic(Player player);
	List<OncePlayerVehicleStatistic> getPlayerRecordedOnceStatistics(Player player);
}
