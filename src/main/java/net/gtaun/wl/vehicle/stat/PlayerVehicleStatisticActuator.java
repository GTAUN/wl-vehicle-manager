package net.gtaun.wl.vehicle.stat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;

import com.google.code.morphia.Datastore;

public class PlayerVehicleStatisticActuator extends AbstractPlayerVehicleProbe
{
	private final VehicleStatisticManager statisticManager;
	private final Datastore datastore;
	
	private Map<Integer, PlayerVehicleStatisticImpl> vehicleStatistics;
	
	private LinkedList<OncePlayerVehicleStatisticImpl> recordedOnceStatistics;
	private OncePlayerVehicleStatisticImpl nowOnceStatistic;
	
	
	public PlayerVehicleStatisticActuator(Shoebill shoebill, EventManager rootEventManager, Player player, VehicleStatisticManager statisticManager, Datastore datastore)
	{
		super(shoebill, rootEventManager, player);
		this.statisticManager = statisticManager;
		this.datastore = datastore;
		
		vehicleStatistics = new HashMap<>();
		recordedOnceStatistics = new LinkedList<>();
	}
	
	@Override
	protected void onInit()
	{
		load();
		super.onInit();
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle != null)
		{
			nowOnceStatistic = new OncePlayerVehicleStatisticImpl(shoebill, rootEventManager, player);
			nowOnceStatistic.start();
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		save();
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
	
	public OncePlayerVehicleStatistic getCurrentOnceStatistic()
	{
		return nowOnceStatistic;
	}
	
	public List<OncePlayerVehicleStatistic> getRecordedOnceStatistics()
	{
		return Collections.unmodifiableList((List<? extends OncePlayerVehicleStatistic>) recordedOnceStatistics);
	}
	
	@Override
	protected void onDriveVehicle(Vehicle vehicle)
	{
		int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDrive();
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDrive();
		
		nowOnceStatistic = new OncePlayerVehicleStatisticImpl(shoebill, rootEventManager, player);
		nowOnceStatistic.start();
		recordedOnceStatistics.offerFirst(nowOnceStatistic);
	}
	
	@Override
	protected void onLeaveVehicle(Vehicle vehicle)
	{
		nowOnceStatistic.end();
		nowOnceStatistic = null;
	}
	
	@Override
	protected void onVehicleDamage(Vehicle vehicle, float damage)
	{
		final int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDamage(damage);
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDamage(damage);
	}
	
	@Override
	protected void onVehicleTick(Vehicle vehicle)
	{
		final int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDriveTick();
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDriveTick();
	}
	
	@Override
	protected void onVehicleMove(Vehicle vehicle, float distance)
	{
		final int modelId = vehicle.getModelId();
		
		PlayerVehicleStatisticImpl stat = getVehicleStatistic(modelId);
		stat.onDriveMove(distance);
		
		GlobalVehicleStatisticImpl globalStat = statisticManager.getGlobalVehicleStatistic(modelId);
		globalStat.onDriveMove(distance);
	}
}
