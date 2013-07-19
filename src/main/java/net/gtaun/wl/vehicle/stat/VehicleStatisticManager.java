package net.gtaun.wl.vehicle.stat;

import java.util.HashMap;
import java.util.Map;

import net.gtaun.shoebill.constant.VehicleModel;

import com.google.code.morphia.Datastore;

public class VehicleStatisticManager
{
	private final Datastore datastore;
	private Map<Integer, VehicleStatistic> globalVehicleStatistic;
	
	
	public VehicleStatisticManager(Datastore datastore)
	{
		this.datastore = datastore;
		this.globalVehicleStatistic = new HashMap<>();
		
		load();
	}
	
	public void load()
	{
		for (int id : VehicleModel.getIds())
		{
			VehicleStatistic statistic = new VehicleStatistic(id);
			globalVehicleStatistic.put(id, statistic);
			datastore.get(statistic);
		}
	}
	
	public void save()
	{
		datastore.save(globalVehicleStatistic.values());
	}
}
