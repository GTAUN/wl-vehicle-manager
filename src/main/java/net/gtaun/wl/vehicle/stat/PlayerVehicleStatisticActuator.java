package net.gtaun.wl.vehicle.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.TimerEventHandler;
import net.gtaun.shoebill.event.VehicleEventHandler;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.timer.TimerTickEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateDamageEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;

import com.google.code.morphia.Datastore;

public class PlayerVehicleStatisticActuator extends PlayerLifecycleObject
{
	private final VehicleStatisticManager statisticManager;
	private final Datastore datastore;
	
	private Map<Integer, PlayerVehicleStatisticImpl> vehicleStatistics;
	private Timer timer;
	
	
	public PlayerVehicleStatisticActuator(Shoebill shoebill, EventManager eventManager, Player player, VehicleStatisticManager statisticManager, Datastore datastore)
	{
		super(shoebill, eventManager, player);
		this.statisticManager = statisticManager;
		this.datastore = datastore;
		
		vehicleStatistics = new HashMap<>();
		timer = shoebill.getSampObjectFactory().createTimer(1000);
	}
	
	@Override
	protected void onInitialize()
	{
		load();
		
		eventManager.registerHandler(PlayerStateChangeEvent.class, player, playerEventHandler, HandlerPriority.MONITOR);

		eventManager.registerHandler(VehicleUpdateEvent.class, vehicleEventHandler, HandlerPriority.MONITOR);
		eventManager.registerHandler(VehicleUpdateDamageEvent.class, vehicleEventHandler, HandlerPriority.MONITOR);
		
		eventManager.registerHandler(TimerTickEvent.class, timer, timerEventHandler, HandlerPriority.MONITOR);
		
		timer.start();
	}
	
	@Override
	protected void onUninitialize()
	{
		save();
		
		timer.destroy();
	}
	
	public void load()
	{
		List<PlayerVehicleStatisticImpl> statistics = datastore.createQuery(PlayerVehicleStatisticImpl.class).asList();
		for (PlayerVehicleStatisticImpl statistic : statistics)
		{
			vehicleStatistics.put(statistic.getModelId(), statistic);
		}
		
		for (int id : VehicleModel.getIds())
		{
			if (vehicleStatistics.containsKey(id)) continue;
			
			PlayerVehicleStatisticImpl statistic = new PlayerVehicleStatisticImpl(player, player.getName(), id);
			vehicleStatistics.put(id, statistic);
		}
	}
	
	public void save()
	{
		datastore.save(vehicleStatistics.values());
	}
	
	public PlayerVehicleStatisticImpl getVehicleStatistic(int modelId)
	{
		PlayerVehicleStatisticImpl statistic = vehicleStatistics.get(modelId);
		return statistic;
	}
	
	public Collection<PlayerVehicleStatisticImpl> getVehicleStatistics()
	{
		return vehicleStatistics.values();
	}
	
	private Location lastVehicleLocation;
	private float lastVehicleHealth;
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerStateChange(PlayerStateChangeEvent event)
		{
			if (player.getState() == PlayerState.DRIVER)
			{
				Vehicle vehicle = player.getVehicle();
				int modelId = vehicle.getModelId();
				
				PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
				GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
				
				lastVehicleHealth = vehicle.getHealth();
				stat.onDrive();
				globalStat.onDrive();
				
				lastVehicleLocation = vehicle.getLocation();
			}
			else
			{
				lastVehicleHealth = 0.0f;
			}
		}
	};
	
	private VehicleEventHandler vehicleEventHandler = new VehicleEventHandler()
	{
		protected void onVehicleUpdate(VehicleUpdateEvent event)
		{
			Vehicle vehicle = event.getVehicle();
			if (vehicle != player.getVehicle()) return;

			final int modelId = vehicle.getModelId();
			PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
			GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
			
			float health = vehicle.getHealth();
			if (lastVehicleHealth > health)
			{
				float damage = lastVehicleHealth - health;
				stat.onDamage(damage);
				globalStat.onDamage(damage);
			}
			lastVehicleHealth = health;
		}
	};
	
	private TimerEventHandler timerEventHandler = new TimerEventHandler()
	{
		@Override
		protected void onTimerTick(TimerTickEvent event)
		{
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null) return;

			final int modelId = vehicle.getModelId();
			PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
			GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
			
			float speed = vehicle.getVelocity().speed3d() * 50;
			if (speed > 0.0f)
			{
				stat.onDriveTick();
				globalStat.onDriveTick();
			}
			
			Location location = vehicle.getLocation();
			float distance = location.distance(lastVehicleLocation);
			if (distance == Float.POSITIVE_INFINITY) distance = 0.0f;
			
			if (distance > 0.0f && distance < 150.0f && distance > speed*2/3)
			{
				stat.onDriveMove(distance);
				globalStat.onDriveMove(distance);
			}
			else
			{
				stat.onDriveMove(speed);
				globalStat.onDriveMove(speed);
			}
			
			lastVehicleLocation = location;
		}
	};
}
