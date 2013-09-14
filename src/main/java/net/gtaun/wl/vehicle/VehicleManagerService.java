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

import java.util.Collection;
import java.util.List;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.service.Service;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

public interface VehicleManagerService extends Service
{
	Plugin getPlugin();
	
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
	PlayerPreferencesBase getEffectivePlayerPreferences(Player player);
	
	void addOverrideLimit(Player player, PlayerOverrideLimit limit);
	void removeOverrideLimit(Player player, PlayerOverrideLimit limit);
	boolean hasOverrideLimit(Player player, PlayerOverrideLimit limit);
	void clearOverrideLimits(Player player);

	GlobalVehicleStatistic getGlobalVehicleStatistic(int modelId);
	Collection<GlobalVehicleStatistic> getGlobalVehicleStatistics();

	PlayerVehicleStatistic getPlayerVehicleStatistic(Player player, int modelId);
	Collection<PlayerVehicleStatistic> getPlayerVehicleStatistics(Player player);

	OncePlayerVehicleStatistic getPlayerCurrentOnceStatistic(Player player);
	List<OncePlayerVehicleStatistic> getPlayerRecordedOnceStatistics(Player player);
	
	OncePlayerVehicleStatistic startRacingStatistic(Player player);
	void endRacingStatistic(Player player);
	boolean isRacingStatistic(Player player);
}
