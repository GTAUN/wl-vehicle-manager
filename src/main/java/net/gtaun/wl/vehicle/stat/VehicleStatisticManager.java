package net.gtaun.wl.vehicle.stat;

import java.util.HashMap;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;

import com.google.code.morphia.Datastore;

public class VehicleStatisticManager
{
	private final ManagedEventManager eventManager;
	private final PlayerLifecycleHolder playerLifecycleHolder;
	private final Datastore datastore;

	private Map<Integer, VehicleStatistic> globalVehicleStatistics;
	
	
	public VehicleStatisticManager(EventManager rootEventManager, PlayerLifecycleHolder holder, Datastore datastore)
	{
		this.eventManager = new ManagedEventManager(rootEventManager);
		this.playerLifecycleHolder = holder;
		this.datastore = datastore;
		this.globalVehicleStatistics = new HashMap<>();
		
		load();

		PlayerLifecycleObjectFactory<PlayerVehicleStatisticActuator> factory = new PlayerLifecycleObjectFactory<PlayerVehicleStatisticActuator>()
		{
			@Override
			public PlayerVehicleStatisticActuator create(Shoebill shoebill, EventManager eventManager, Player player)
			{
				return new PlayerVehicleStatisticActuator(shoebill, eventManager, player, VehicleStatisticManager.this.datastore);
			}
		};
		playerLifecycleHolder.registerClass(PlayerVehicleStatisticActuator.class, factory);
	}
	
	public void destroy()
	{
		eventManager.cancelAll();
	}
	
	public void load()
	{
		for (int id : VehicleModel.getIds())
		{
			VehicleStatistic statistic = new VehicleStatistic(id);
			globalVehicleStatistics.put(id, statistic);
			datastore.get(statistic);
		}
	}
	
	public void save()
	{
		datastore.save(globalVehicleStatistics.values());
	}
	
	public VehicleStatistic getGlobalVehicleStatistic(int modelId)
	{
		VehicleStatistic statistic = globalVehicleStatistics.get(modelId);
		return statistic;
	}
	
	public PlayerVehicleStatistic getPlayerVehicleStatistic(Player player, int modelId)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return actuator.getVehicleStatistic(modelId);
	}
}
