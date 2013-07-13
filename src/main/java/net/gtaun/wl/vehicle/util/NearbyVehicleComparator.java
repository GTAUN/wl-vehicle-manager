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

import java.util.Comparator;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;

public class NearbyVehicleComparator implements Comparator<Vehicle>
{
	private final Location location;
	
	
	public NearbyVehicleComparator(Location loc)
	{
		location = loc;
	}

	@Override
	public int compare(Vehicle o1, Vehicle o2)
	{
		return (int) (o1.getLocation().distance(location) - o2.getLocation().distance(location));
	}
}
