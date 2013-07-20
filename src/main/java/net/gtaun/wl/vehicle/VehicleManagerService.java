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

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.service.Service;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

public interface VehicleManagerService extends Service
{
	Vehicle createOwnVehicle(Player player, int modelId);
	
	/**
	 * 设置玩家拥有的车辆。
	 * 
	 * @param player
	 * @param vehicle
	 */
	void ownVehicle(Player player, Vehicle vehicle);
	void unownVehicle(Player player);
	
	Vehicle getOwnedVehicle(Player player);
	boolean isOwned(Vehicle vehicle);

	boolean isPlayerLockNos(Player player);
	void setPlayerLockNos(Player player, boolean lock);
	
	boolean isPlayerLockVehicleHealth(Player player);
	void setPlayerLockVehicleHealth(Player player, boolean lock);

	GlobalVehicleStatistic getGlobalVehicleStatistic(int modelId);
	Collection<GlobalVehicleStatistic> getGlobalVehicleStatistics();

	PlayerVehicleStatistic getPlayerVehicleStatistic(Player player, int modelId);
	Collection<PlayerVehicleStatistic> getPlayerVehicleStatistics(Player player);
}
