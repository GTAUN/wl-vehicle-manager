package net.gtaun.wl.vehicle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Quaternion;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.TimerEventHandler;
import net.gtaun.shoebill.event.VehicleEventHandler;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
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
	private static final Map<String, Integer> VEHICLE_SHORT_NAMES = createVehicleShortNames();
	private static Map<String, Integer> createVehicleShortNames()
	{
		Map<String, Integer> map = new HashMap<>();
		for (int id : VehicleModel.getIds())
		{
			String name = VehicleModel.getName(id);
			name = StringUtils.replace(name, " ", "");
			name = StringUtils.replace(name, "-", "");
			name = name.substring(0, 3).toUpperCase();
			map.put(name, id);
		}
		
		return Collections.unmodifiableMap(map);
	}
	
	
	private final VehicleManagerService vehicleManager;
	private final Timer timer;
	
	private boolean isLockNOS;
	private boolean isAutoRepair;
	private boolean isAutoFlip;
	
	
	public PlayerVehicleActuator(Shoebill shoebill, EventManager eventManager, Player player, VehicleManagerService vehicleManager)
	{
		super(shoebill, eventManager, player);
		this.vehicleManager = vehicleManager;
		
		timer = shoebill.getSampObjectFactory().createTimer(10000);
		addDestroyable(timer);
	}

	@Override
	protected void onInitialize()
	{
		eventManager.registerHandler(TimerTickEvent.class, timer, timerEventHandler, HandlerPriority.NORMAL);
		
		eventManager.registerHandler(PlayerTextEvent.class, player, playerEventHandler, HandlerPriority.MONITOR);
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
	
	public boolean isUnlimitedNOS()
	{
		return isLockNOS;
	}
	
	public boolean isAutoRepair()
	{
		return isAutoRepair;
	}
	
	public boolean isAutoFlip()
	{
		return isAutoFlip;
	}
	
	public void setUnlimitedNOS(boolean enabled)
	{
		this.isLockNOS = enabled;
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		if (isLockNOS && VehicleComponentModel.isVehicleSupported(vehicle.getModelId(), VehicleComponentModel.NITRO_10_TIMES))
		{
			vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
		}
	}
	
	public void setAutoRepair(boolean enabled)
	{
		this.isAutoRepair = enabled;

		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		if (isAutoRepair && vehicle.getHealth() < 1000.0f) vehicle.repair();
	}
	
	public void setAutoFlip(boolean enabled)
	{
		this.isAutoFlip = enabled;
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
		protected void onPlayerText(PlayerTextEvent event)
		{
			Player player = event.getPlayer();
			String text = event.getText();
			
			if (text.charAt(0) != '\\') return;
			
			event.disallow();
			event.interrupt();
			
			if (text.length() < 4)
			{
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 快捷刷车命令用法：\\[车辆模型ID] 或者 \\[车辆名字前三位] ，比如 \\411 或者 \\tur 。", "车管");
				return;
			}
			
			String name = text.substring(1);
			name = StringUtils.replace(name, " ", "");
			name = StringUtils.replace(name, "-", "");
			name = name.substring(0, Math.min(name.length(), 3)).toUpperCase();
			
			int modelId;
			if (StringUtils.isNumeric(name))
			{
				modelId = Integer.parseInt(name);
				if (VehicleModel.isVaildId(modelId) == false)
				{
					player.sendMessage(Color.LIGHTBLUE, "%1$s: 不是合法的车辆模型ID %2$d 。", "车管", modelId);
					return;
				}
			}
			else if (VEHICLE_SHORT_NAMES.containsKey(name) == false)
			{
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 不存在这个车辆简写名称 %2$s 。", "车管", name);
				return;
			}
			else
			{
				modelId = VEHICLE_SHORT_NAMES.get(name);
			}
			
			player.playSound(1057, player.getLocation());
			Vehicle vehicle = vehicleManager.createOwnVehicle(player, modelId);
			vehicle.putPlayer(player, 0);
			player.sendMessage(Color.LIGHTBLUE, "%1$s: 您的专属座驾 %2$s 已创建！", "车管", VehicleModel.getName(vehicle.getModelId()));
		}
		
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

			int modelId = vehicle.getModelId();
			if (isAutoFlip && VehicleModel.getType(modelId) != VehicleType.AIRCRAFT)
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
