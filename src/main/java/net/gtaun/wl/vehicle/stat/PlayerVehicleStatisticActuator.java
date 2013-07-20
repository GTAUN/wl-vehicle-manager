package net.gtaun.wl.vehicle.stat;

import java.util.HashMap;
import java.util.List;
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
		load();
	}
	
	@Override
	protected void onUninitialize()
	{
		save();
	}
	
	public void load()
	{
		List<PlayerVehicleStatistic> statistics = datastore.createQuery(PlayerVehicleStatistic.class).asList();
		for (PlayerVehicleStatistic statistic : statistics)
		{
			vehicleStatistics.put(statistic.getModelId(), statistic);
		}
		
		for (int id : VehicleModel.getIds())
		{
			if (vehicleStatistics.containsKey(id)) continue;
			
			PlayerVehicleStatistic statistic = new PlayerVehicleStatistic(player, player.getName(), id);
			vehicleStatistics.put(id, statistic);
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
