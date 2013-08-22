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
