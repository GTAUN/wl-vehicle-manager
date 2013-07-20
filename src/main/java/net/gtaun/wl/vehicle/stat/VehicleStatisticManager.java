package net.gtaun.wl.vehicle.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.event.TimerEventHandler;
import net.gtaun.shoebill.event.timer.TimerTickEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;

import com.google.code.morphia.Datastore;

public class VehicleStatisticManager
{
	private final ManagedEventManager eventManager;
	private final PlayerLifecycleHolder playerLifecycleHolder;
	private final Datastore datastore;

	private Map<Integer, VehicleStatistic> globalVehicleStatistics;
	private Timer saveTimer;
	
	
	public VehicleStatisticManager(Shoebill shoebill, EventManager rootEventManager, PlayerLifecycleHolder holder, Datastore datastore)
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
		
		saveTimer = shoebill.getSampObjectFactory().createTimer(1000*60*5);
		saveTimer.start();
		
		eventManager.registerHandler(TimerTickEvent.class, saveTimer, saveTimerEventHandler, HandlerPriority.NORMAL);
	}
	
	public void destroy()
	{
		eventManager.cancelAll();
		saveTimer.destroy();
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
		
		Collection<PlayerVehicleStatisticActuator> actuators = playerLifecycleHolder.getObjects(PlayerVehicleStatisticActuator.class);
		for (PlayerVehicleStatisticActuator actuator : actuators) actuator.save();
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
	
	private TimerEventHandler saveTimerEventHandler = new TimerEventHandler()
	{
		protected void onTimerTick(TimerTickEvent event)
		{
			save();
		}
	};
}
