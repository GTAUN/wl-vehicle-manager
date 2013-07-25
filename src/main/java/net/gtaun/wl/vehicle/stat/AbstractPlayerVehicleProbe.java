package net.gtaun.wl.vehicle.stat;

import java.util.HashSet;
import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.PlayerState;
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

public abstract class AbstractPlayerVehicleProbe extends AbstractPlayerContext
{
	private final Timer timer;
	private final Set<PlayerState> allowableStates;

	private Vehicle nowVehicle;
	
	private Location lastVehicleLocation;
	private float lastVehicleHealth;
	
	
	public AbstractPlayerVehicleProbe(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		super(shoebill, rootEventManager, player);
		timer = shoebill.getSampObjectFactory().createTimer(1000);
		
		allowableStates = new HashSet<>();
		allowableStates.add(PlayerState.DRIVER);
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
		
		eventManager.registerHandler(PlayerStateChangeEvent.class, player, playerEventHandler, HandlerPriority.MONITOR);

		eventManager.registerHandler(VehicleUpdateEvent.class, vehicleEventHandler, HandlerPriority.MONITOR);
		eventManager.registerHandler(VehicleUpdateDamageEvent.class, vehicleEventHandler, HandlerPriority.MONITOR);
		
		eventManager.registerHandler(TimerTickEvent.class, timer, timerEventHandler, HandlerPriority.MONITOR);
		
		timer.start();
	}
	
	@Override
	protected void onDestroy()
	{
		timer.destroy();
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerStateChange(PlayerStateChangeEvent event)
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
		}
	};
	
	private VehicleEventHandler vehicleEventHandler = new VehicleEventHandler()
	{
		protected void onVehicleUpdate(VehicleUpdateEvent event)
		{
			Vehicle vehicle = event.getVehicle();
			if (allowableStates.contains(player.getState()) == false || vehicle != player.getVehicle()) return;
			
			AbstractPlayerVehicleProbe.this.onVehicleUpdate(vehicle);
			
			float health = vehicle.getHealth();
			if (lastVehicleHealth > health)
			{
				float damage = lastVehicleHealth - health;
				onVehicleDamage(vehicle, damage);
			}
			lastVehicleHealth = health;
		}
	};
	
	private TimerEventHandler timerEventHandler = new TimerEventHandler()
	{
		@Override
		protected void onTimerTick(TimerTickEvent event)
		{
			if (allowableStates.contains(player.getState()) == false) return;
			
			Vehicle vehicle = player.getVehicle();
			
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
		}
	};

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
