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

import java.util.HashSet;
import java.util.Set;

import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

public abstract class AbstractPlayerVehicleProbe extends PlayerLifecycleObject
{
	private final Timer timer;
	private final Set<PlayerState> allowableStates;

	private Vehicle nowVehicle;
	
	private Location lastVehicleLocation;
	private float lastVehicleHealth;
	
	
	public AbstractPlayerVehicleProbe(EventManager rootEventManager, Player player)
	{
		super(rootEventManager, player);

		allowableStates = new HashSet<>();
		allowableStates.add(PlayerState.DRIVER);
		
		timer = Timer.create(1000, (factualInterval) ->
		{
			if (allowableStates.contains(player.getState()) == false) return;
			
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null) return;
			
			float speed = vehicle.getVelocity().speed3d() * 50;
			if (speed > 0.02f)
			{
				onVehicleTick(vehicle);
			}
			
			Location location = vehicle.getLocation();
			float distance = location.distance(lastVehicleLocation);
			if (distance == Float.POSITIVE_INFINITY) distance = 0.0f;
			
			if (distance > 0.0f && distance < 150.0f && distance > speed*2/3)
			{
				onVehicleMove(vehicle, distance);
			}
			else
			{
				onVehicleMove(vehicle, speed);
			}
			
			lastVehicleLocation = location;
		});
	}
	
	protected void allowState(PlayerState state)
	{
		allowableStates.add(state);
	}
	
	private void setNowVehicle(Vehicle vehicle)
	{
		if (nowVehicle != null) onLeaveVehicle(nowVehicle);
		if (vehicle != null)
		{
			lastVehicleLocation = vehicle.getLocation();
			lastVehicleHealth = vehicle.getHealth();
			
			PlayerState state = player.getState();
			
			if (state == PlayerState.DRIVER)
			{
				onDriveVehicle(vehicle);
			}
			else if (state == PlayerState.PASSENGER)
			{
				onBecomePassenger(vehicle);
			}
		}
		else
		{
			lastVehicleHealth = 0.0f;
		}
		
		nowVehicle = vehicle;
	}
	
	@Override
	protected void onInit()
	{
		setNowVehicle(player.getVehicle());
		
		eventManager.registerHandler(PlayerStateChangeEvent.class, HandlerPriority.MONITOR, Attentions.create().object(player), (e) ->
		{
			if (allowableStates.contains(player.getState()))
			{
				Vehicle vehicle = player.getVehicle();
				if (vehicle != nowVehicle) setNowVehicle(vehicle);
			}
			else
			{
				setNowVehicle(null);
			}
		});

		eventManager.registerHandler(VehicleUpdateEvent.class, HandlerPriority.MONITOR, (e) ->
		{
			Vehicle vehicle = e.getVehicle();
			if (allowableStates.contains(player.getState()) == false || vehicle != player.getVehicle()) return;
			
			AbstractPlayerVehicleProbe.this.onVehicleUpdate(vehicle);
			
			float health = vehicle.getHealth();
			if (lastVehicleHealth > health)
			{
				float damage = lastVehicleHealth - health;
				if (damage <= 1000.0f) onVehicleDamage(vehicle, damage);
			}
			lastVehicleHealth = health;
		});
		
		timer.start();
	}
	
	@Override
	protected void onDestroy()
	{
		timer.destroy();
	}

	protected void onVehicleUpdate(Vehicle vehicle)
	{
		
	}

	protected void onDriveVehicle(Vehicle vehicle)
	{
		
	}

	protected void onBecomePassenger(Vehicle vehicle)
	{
		
	}
	
	protected void onLeaveVehicle(Vehicle vehicle)
	{
		
	}
	
	protected void onVehicleMove(Vehicle vehicle, float distance)
	{
		
	}
	
	protected void onVehicleTick(Vehicle vehicle)
	{
		
	}
	
	protected void onVehicleDamage(Vehicle vehicle, float damage)
	{
		
	}
}
