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
