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

import java.util.Date;
import java.util.List;

import net.gtaun.shoebill.object.Player;

public interface OncePlayerVehicleStatistic
{
	public enum StatisticType
	{
		DRIVER,
		PASSENGER,
		RACING,
	}
	
	Player getPlayer();
	boolean isActive();
	StatisticType getType();
	
	List<Integer> getModelIds();
	double getDamageCount();
	long getDriveSecondCount();
	double getDriveOdometer();
	float getCurrentSpeed();
	float getMaxSpeed();
	
	Date getStartTime();
	Date getEndTime();
}
