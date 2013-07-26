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

package net.gtaun.wl.vehicle.util;

import net.gtaun.shoebill.common.Filter;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;

public class DistanceVehicleFilter implements Filter<Vehicle>
{
	private final Location location;
	private final float distance;
	
	
	public DistanceVehicleFilter(Location location, float distance)
	{
		this.location = location;
		this.distance = distance;
	}

	@Override
	public boolean isAcceptable(Vehicle vehicle)
	{
		return vehicle.getLocation().distance(location) <= distance;
	}
}
