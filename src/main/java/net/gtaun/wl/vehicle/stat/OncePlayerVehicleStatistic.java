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

import java.util.Date;

import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.object.Player;

public interface OncePlayerVehicleStatistic
{
	Player getPlayer();
	boolean isActive();
	PlayerState getType();
	
	int getModelId();
	double getDamageCount();
	long getDriveSecondCount();
	double getDriveOdometer();
	float getCurrentSpeed();
	float getMaxSpeed();
	
	Date getStartTime();
	Date getEndTime();
}
