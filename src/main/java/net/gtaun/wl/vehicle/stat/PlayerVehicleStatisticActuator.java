package net.gtaun.wl.vehicle.stat;

import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.Datastore;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public class PlayerVehicleStatisticActuator extends PlayerLifecycleObject
{
	private final Datastore datastore;
	private Map<Integer, PlayerVehicleStatistic> vehicleStatistics;
	
	
	public PlayerVehicleStatisticActuator(Shoebill shoebill, EventManager eventManager, Player player, Datastore datastore)
	{
		super(shoebill, eventManager, player);
		this.datastore = datastore;
		vehicleStatistics = new HashMap<>();
	}
	
	@Override
	protected void onInitialize()
	{
		
	}
	
	@Override
	protected void onUninitialize()
	{
		
	}
	
	public void load()
	{
		for (int id : VehicleModel.getIds())
		{
			PlayerVehicleStatistic statistic = new PlayerVehicleStatistic(player, player.getName(), id);
			vehicleStatistics.put(id, statistic);
			datastore.get(statistic);
		}
	}
	
	public void save()
	{
		datastore.save(vehicleStatistics.values());
	}
	
	public PlayerVehicleStatistic getVehicleStatistic(int modelId)
	{
		PlayerVehicleStatistic statistic = vehicleStatistics.get(modelId);
		return statistic;
	}
}
