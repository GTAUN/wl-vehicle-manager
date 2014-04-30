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

import java.util.function.Predicate;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Vehicle;

public class VehicleFilters
{
	public static Predicate<Vehicle> distance(Location loc, float distance)
	{
		return (v) -> v.getLocation().distance(loc) <= distance;
	}
}
