package net.gtaun.wl.vehicle;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.data.Quaternion;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.TimerEventHandler;
import net.gtaun.shoebill.event.VehicleEventHandler;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.timer.TimerTickEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateDamageEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerKeyState;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;

class PlayerVehicleActuator extends PlayerLifecycleObject
{
	private final Timer timer;
	
	boolean isLockNOS;
	boolean isAutoRepair;
	boolean isAutoFlip;
	
	
	public PlayerVehicleActuator(Shoebill shoebill, EventManager eventManager, Player player)
	{
		super(shoebill, eventManager, player);
		timer = shoebill.getSampObjectFactory().createTimer(10000);
		addDestroyable(timer);
	}

	@Override
	protected void onInitialize()
	{
		eventManager.registerHandler(TimerTickEvent.class, timer, timerEventHandler, HandlerPriority.NORMAL);
		
		eventManager.registerHandler(PlayerKeyStateChangeEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerStateChangeEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		
		eventManager.registerHandler(VehicleUpdateEvent.class, vehicleEventHandler, HandlerPriority.BOTTOM);
		eventManager.registerHandler(VehicleUpdateDamageEvent.class, vehicleEventHandler, HandlerPriority.BOTTOM);
		timer.start();
	}

	@Override
	protected void onUninitialize()
	{
		
	}
	
	private TimerEventHandler timerEventHandler = new TimerEventHandler()
	{
		protected void onTimerTick(TimerTickEvent event)
		{
			if (player.getState() == PlayerState.DRIVER)
			{
				Vehicle vehicle = player.getVehicle();
				int modelId = vehicle.getModelId();
				if (isLockNOS && VehicleComponentModel.isVehicleSupported(modelId, VehicleComponentModel.NITRO_10_TIMES))
				{
					vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
				}
			}
		}
	};
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerStateChange(PlayerStateChangeEvent event)
		{
			Player player = event.getPlayer();
			if (player.getState() == PlayerState.DRIVER)
			{
				Vehicle vehicle = player.getVehicle();
				int modelId = vehicle.getModelId();
				
				if (isLockNOS && VehicleComponentModel.isVehicleSupported(modelId, VehicleComponentModel.NITRO_10_TIMES))
				{
					vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
				}
				
				if (isAutoRepair && vehicle.getHealth() < 1000.0f) vehicle.repair();
			}
		}
		
		protected void onPlayerKeyStateChange(PlayerKeyStateChangeEvent event)
		{
			Player player = event.getPlayer();
			PlayerKeyState state = player.getKeyState();
			
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null || player.getState() != PlayerState.DRIVER) return;

			if (state.isKeyPressed(PlayerKey.ACTION))
			{
				if (state.isKeyPressed(PlayerKey.ANALOG_LEFT))
				{
					int color1 = vehicle.getColor1() - 1;
					if (color1 < 0) color1 += 256;
					vehicle.setColor(color1, vehicle.getColor2());
				}
				else if (state.isKeyPressed(PlayerKey.ANALOG_RIGHT))
				{
					int color1 = (vehicle.getColor1() + 1) % 256;
					vehicle.setColor(color1, vehicle.getColor2());
				}
				else if (state.isKeyPressed(PlayerKey.ANALOG_UP))
				{
					int color2 = vehicle.getColor2() - 1;
					if (color2 < 0) color2 += 256;
					vehicle.setColor(vehicle.getColor1(), color2);
				}
				else if (state.isKeyPressed(PlayerKey.ANALOG_DOWN))
				{
					int color2 = (vehicle.getColor2() + 1) % 256;
					vehicle.setColor(vehicle.getColor1(), color2);
				}
			}
		}
	};
	
	private VehicleEventHandler vehicleEventHandler = new VehicleEventHandler()
	{
		protected void onVehicleUpdate(VehicleUpdateEvent event)
		{
			Vehicle vehicle = event.getVehicle();
			if (player.getState() != PlayerState.DRIVER || vehicle != player.getVehicle()) return;

			if (isAutoFlip)
			{
				Quaternion quat = vehicle.getRotationQuat();
				final float w = quat.getW();
				final float x = quat.getX();
				final float y = quat.getY();
				final float z = quat.getZ();
				
				float t11 = 2*(w*x+y*z);
				float t12 = 1-2*(x*x+y*y);
				float rx = (float) Math.atan2(t11,t12);

				Velocity velocity = vehicle.getVelocity();
				if( (vehicle.getHealth() < 250.0f || velocity.speed3d()*50 < 5.0f) &&
					(rx < -Math.PI/4*3 || rx > Math.PI/4*3) )
				{
					vehicle.setLocation(vehicle.getLocation());
					vehicle.setVelocity(velocity);
				}
			}
			
			if (isAutoRepair && vehicle.getHealth() < 1000.0f) vehicle.repair();
		}
		
		protected void onVehicleUpdateDamage(VehicleUpdateDamageEvent event)
		{
			Vehicle vehicle = event.getVehicle();
			if (player.getState() != PlayerState.DRIVER || vehicle != player.getVehicle()) return;
			
			if (isAutoRepair && vehicle == player.getVehicle()) vehicle.repair();
		}
	};
}
