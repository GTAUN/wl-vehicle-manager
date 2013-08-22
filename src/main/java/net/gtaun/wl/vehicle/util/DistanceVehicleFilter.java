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
