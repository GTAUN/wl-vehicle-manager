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

package net.gtaun.wl.vehicle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.common.vehicle.VehicleUtils;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Quaternion;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateDamageEvent;
import net.gtaun.shoebill.event.vehicle.VehicleUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerKeyState;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl.OwnedVehicleLastPassengers;
import net.gtaun.wl.vehicle.event.PlayerPreferencesUpdateEvent;
import net.gtaun.wl.vehicle.textdraw.VehicleWidget;
import net.gtaun.wl.vehicle.util.PlayerOverridePreferences;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.Datastore;

class PlayerVehicleManagerContent extends PlayerLifecycleObject
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


	private final VehicleManagerServiceImpl vehicleManagerService;
	private final Datastore datastore;
	private final Timer timer;

	private final PlayerPreferencesImpl playerPreferences;
	private final PlayerOverridePreferences effectivePlayerPreferences;

	private VehicleWidget vehicleWidget;
	private Vehicle lastDriveVehicle;


	public PlayerVehicleManagerContent(EventManager rootEventManager, final Player player, VehicleManagerServiceImpl vehicleManager, Datastore datastore)
	{
		super(rootEventManager, player);
		this.vehicleManagerService = vehicleManager;
		this.datastore = datastore;

		String uniqueId = player.getName();

		PlayerPreferencesImpl pref = datastore.createQuery(PlayerPreferencesImpl.class).filter("playerUniqueId", uniqueId).get();
		if (pref != null) pref.setContext(eventManagerNode, player);
		else pref = new PlayerPreferencesImpl(eventManagerNode, player, uniqueId);
		playerPreferences = pref;
		effectivePlayerPreferences = new PlayerOverridePreferences(pref, eventManagerNode);

		timer = Timer.create(10000, (factualInterval) ->
		{
			if (player.getState() == PlayerState.DRIVER)
			{
				Vehicle vehicle = player.getVehicle();
				int modelId = vehicle.getModelId();
				if (effectivePlayerPreferences.isInfiniteNitrous() && VehicleComponentModel.isVehicleSupported(modelId, VehicleComponentModel.NITRO_10_TIMES))
				{
					vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
				}
			}
		});
		addDestroyable(timer);

		rootEventManager.registerHandler(PlayerPreferencesUpdateEvent.class, HandlerPriority.NORMAL, Attentions.create().object(effectivePlayerPreferences), (e) ->
		{
			createOrDestroySpeedometerWidget();

			if (effectivePlayerPreferences.isAutoRepair())
			{
				Vehicle vehicle = player.getVehicle();
				if (vehicle == null) return;

				if (vehicle.getHealth() < 1000.0f) vehicle.repair();
			}

			if (effectivePlayerPreferences.isInfiniteNitrous())
			{
				Vehicle vehicle = player.getVehicle();
				if (vehicle == null) return;

				if (VehicleComponentModel.isVehicleSupported(vehicle.getModelId(), VehicleComponentModel.NITRO_10_TIMES))
				{
					vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
				}
			}
		});
	}

	@Override
	protected void onInit()
	{
		eventManagerNode.registerHandler(PlayerTextEvent.class, HandlerPriority.MONITOR, Attentions.create().object(player), (e) ->
		{
			final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();

			Player player = e.getPlayer();
			String text = e.getText();

			if (text.charAt(0) != '\\') return;

			e.disallow();
			e.interrupt();

			if (text.length() < 4)
			{
				player.sendMessage(Color.LIGHTBLUE, stringSet.get(player, "Command.CreateVehicle.Usage"));
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
					player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Command.CreateVehicle.InvalidID", modelId));
					return;
				}
			}
			else if (VEHICLE_SHORT_NAMES.containsKey(name) == false)
			{
				player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Command.CreateVehicle.InvalidAbbr", name));
				return;
			}
			else
			{
				modelId = VEHICLE_SHORT_NAMES.get(name);
			}

			player.playSound(1057);
			Vehicle vehicle = vehicleManagerService.createOwnVehicle(player, modelId);
			vehicle.putPlayer(player, 0);
			player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Command.CreateVehicle.Message", VehicleModel.getName(vehicle.getModelId())));
		});

		eventManagerNode.registerHandler(PlayerKeyStateChangeEvent.class, HandlerPriority.NORMAL, Attentions.create().object(player), (e) ->
		{
			Player player = e.getPlayer();
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
		});

		eventManagerNode.registerHandler(PlayerStateChangeEvent.class, HandlerPriority.NORMAL, Attentions.create().object(player), (e) ->
		{
			Player player = e.getPlayer();
			PlayerState state = player.getState();

			if (state != PlayerState.DRIVER) lastDriveVehicle = null;

			Vehicle vehicle = player.getVehicle();
			if (state == PlayerState.DRIVER && vehicle != null)
			{
				int modelId = vehicle.getModelId();

				if (effectivePlayerPreferences.isAutoCarryPassengers() && lastDriveVehicle != null)
				{
					List<Player> passengers = null;
					if (lastDriveVehicle.isDestroyed())
					{
						OwnedVehicleLastPassengers lastPassengers = vehicleManagerService.getOwnedVehicleLastPassengers(player);
						if (lastPassengers != null && lastPassengers.lastUpdate+2000L > System.currentTimeMillis())
						{
							passengers = lastPassengers.passengers;
						}
					}
					else
					{
						passengers = VehicleUtils.getVehiclePassengers(lastDriveVehicle);
					}

					if (passengers != null)
					{
						int limits = Math.min(passengers.size(), VehicleModel.getSeats(vehicle.getModelId())-1);
						for (int i=0; i<limits; i++) vehicle.putPlayer(passengers.get(i), i+1);
					}
				}
				lastDriveVehicle = vehicle;

				if (effectivePlayerPreferences.isInfiniteNitrous() && VehicleComponentModel.isVehicleSupported(modelId, VehicleComponentModel.NITRO_10_TIMES))
				{
					vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
				}

				if (effectivePlayerPreferences.isAutoRepair() && vehicle.getHealth() < 1000.0f) vehicle.repair();
			}

			createOrDestroySpeedometerWidget();
		});

		eventManagerNode.registerHandler(VehicleUpdateEvent.class, HandlerPriority.BOTTOM, (e) ->
		{
			Vehicle vehicle = e.getVehicle();
			if (player.getState() != PlayerState.DRIVER || vehicle != player.getVehicle()) return;

			int modelId = vehicle.getModelId();
			if (effectivePlayerPreferences.isAutoFlip() && VehicleModel.getType(modelId) != VehicleType.AIRCRAFT)
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

			if (effectivePlayerPreferences.isAutoRepair() && vehicle.getHealth() < 1000.0f) vehicle.repair();
		});

		eventManagerNode.registerHandler(VehicleUpdateDamageEvent.class, HandlerPriority.BOTTOM, (e) ->
		{
			Vehicle vehicle = e.getVehicle();
			if (player.getState() != PlayerState.DRIVER || vehicle != player.getVehicle()) return;

			if (effectivePlayerPreferences.isAutoRepair() && vehicle == player.getVehicle()) vehicle.repair();
		});

		timer.start();
	}

	@Override
	protected void onDestroy()
	{
		if (vehicleWidget != null) vehicleWidget.destroy();
		datastore.save(playerPreferences);
	}

	public PlayerPreferencesImpl getPlayerPreferences()
	{
		return playerPreferences;
	}

	public PlayerOverridePreferences getEffectivePlayerPreferences()
	{
		return effectivePlayerPreferences;
	}

	private void createOrDestroySpeedometerWidget()
	{
		if (vehicleWidget != null)
		{
			vehicleWidget.destroy();
			vehicleWidget = null;
		}

		PlayerState state = player.getState();
		if (effectivePlayerPreferences.isVehicleWidgetEnabled() &&
			(state == PlayerState.DRIVER || state == PlayerState.PASSENGER))
		{
			vehicleWidget = new VehicleWidget(rootEventManager, player, vehicleManagerService);
			vehicleWidget.init();
		}
	}
}
