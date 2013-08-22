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

package net.gtaun.wl.vehicle.stat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.util.event.EventManager;

import com.google.code.morphia.Datastore;

public class VehicleStatisticManager extends AbstractShoebillContext
{
	private final PlayerLifecycleHolder playerLifecycleHolder;
	private final Datastore datastore;

	private Map<Integer, GlobalVehicleStatisticImpl> globalVehicleStatistics;
	private Timer saveTimer;
	
	
	public VehicleStatisticManager(Shoebill shoebill, EventManager rootEventManager, PlayerLifecycleHolder holder, Datastore datastore)
	{
		super(shoebill, rootEventManager);
		this.playerLifecycleHolder = holder;
		this.datastore = datastore;
		this.globalVehicleStatistics = new HashMap<>();
		
		onInit();
	}
	
	@Override
	protected void onInit()
	{
		load();

		PlayerLifecycleObjectFactory<PlayerVehicleStatisticActuator> factory = new PlayerLifecycleObjectFactory<PlayerVehicleStatisticActuator>()
		{
			@Override
			public PlayerVehicleStatisticActuator create(Shoebill shoebill, EventManager eventManager, Player player)
			{
				return new PlayerVehicleStatisticActuator(shoebill, eventManager, player, VehicleStatisticManager.this, VehicleStatisticManager.this.datastore);
			}
		};
		playerLifecycleHolder.registerClass(PlayerVehicleStatisticActuator.class, factory);
		
		saveTimer = shoebill.getSampObjectFactory().createTimer(1000*60*5);
		saveTimer.setCallback(new TimerCallback()
		{
			@Override
			public void onTick(int factualInterval)
			{
				save();
			}
		});
		saveTimer.start();
		
		addDestroyable(saveTimer);		
	}
	
	protected void onDestroy()
	{
		save();
	}
	
	public void load()
	{
		List<GlobalVehicleStatisticImpl> statistics = datastore.createQuery(GlobalVehicleStatisticImpl.class).asList();
		for (GlobalVehicleStatisticImpl statistic : statistics)
		{
			globalVehicleStatistics.put(statistic.getModelId(), statistic);
		}
		
		for (int id : VehicleModel.getIds())
		{
			if (globalVehicleStatistics.containsKey(id)) continue;
			
			GlobalVehicleStatisticImpl statistic = new GlobalVehicleStatisticImpl(id);
			globalVehicleStatistics.put(id, statistic);
		}
	}
	
	public void save()
	{
		datastore.save(globalVehicleStatistics.values());
		
		Collection<PlayerVehicleStatisticActuator> actuators = playerLifecycleHolder.getObjects(PlayerVehicleStatisticActuator.class);
		for (PlayerVehicleStatisticActuator actuator : actuators) actuator.save();
	}

	public GlobalVehicleStatisticImpl getGlobalVehicleStatistic(int modelId)
	{
		GlobalVehicleStatisticImpl statistic = globalVehicleStatistics.get(modelId);
		return statistic;
	}
	
	public Collection<GlobalVehicleStatistic> getGlobalVehicleStatistics()
	{
		return Collections.unmodifiableCollection((Collection<? extends GlobalVehicleStatistic>) globalVehicleStatistics.values());
	}
	
	public PlayerVehicleStatisticImpl getPlayerVehicleStatistic(Player player, int modelId)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return actuator.getVehicleStatistic(modelId);
	}
	
	public Collection<PlayerVehicleStatistic> getPlayerVehicleStatistics(Player player)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return Collections.unmodifiableCollection((Collection<? extends PlayerVehicleStatistic>) actuator.getVehicleStatistics());
	}

	public OncePlayerVehicleStatistic getPlayerCurrentOnceStatistic(Player player)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return actuator.getCurrentOnceStatistic();
	}
	
	public List<OncePlayerVehicleStatistic> getPlayerRecordedOnceStatistics(Player player)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return actuator.getRecordedOnceStatistics();
	}
	
	public OncePlayerVehicleStatisticImpl startRacingStatistic(Player player)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return actuator.startRacingStatistic();
	}
	
	public void endRacingStatistic(Player player)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		actuator.endRacingStatistic();
	}
	
	public boolean isRacingStatistic(Player player)
	{
		PlayerVehicleStatisticActuator actuator = playerLifecycleHolder.getObject(player, PlayerVehicleStatisticActuator.class);
		return actuator.isRacingStatistic();
	}
}
