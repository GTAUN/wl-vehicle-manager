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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.vehicle.VehicleUtils;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.service.Service;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.gamemode.event.MainMenuDialogExtendEvent;
import net.gtaun.wl.lang.LanguageService;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.dialog.VehicleDialog;
import net.gtaun.wl.vehicle.dialog.VehicleManagerDialog;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatisticImpl;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.VehicleStatisticManager;

import org.mongodb.morphia.Datastore;

public class VehicleManagerServiceImpl extends AbstractShoebillContext implements VehicleManagerService
{
	class OwnedVehicleLastPassengers
	{
		final long lastUpdate;
		final List<Player> passengers;
		
		private OwnedVehicleLastPassengers(long lastUpdate, List<Player> passengers)
		{
			this.lastUpdate = lastUpdate;
			this.passengers = passengers;
		}
	}
	
	
	private final VehicleManagerPlugin plugin;
	private final Datastore datastore;
	
	private final EventManagerNode eventManager;
	private final PlayerLifecycleHolder playerLifecycleHolder;
	
	private final LocalizedStringSet localizedStringSet;
	
	private boolean isCommandEnabled = true;
	private String commandOperation = "/v";
	
	private VehicleStatisticManager statisticManager;
	
	private Map<Player, Vehicle> playerOwnedVehicles;
	private Set<Vehicle> ownedVehicles;
	
	private Map<Player, OwnedVehicleLastPassengers> playerOwnedVehicleLastPassengers;
	
	
	public VehicleManagerServiceImpl(EventManager rootEventManager, VehicleManagerPlugin plugin, Datastore datastore)
	{
		super(rootEventManager);
		this.plugin = plugin;
		this.datastore = datastore;
		
		eventManager = rootEventManager.createChildNode();
		playerLifecycleHolder = new PlayerLifecycleHolder(eventManager);
		
		LanguageService languageService = Service.get(LanguageService.class);
		localizedStringSet = languageService.createStringSet(new File(plugin.getDataDir(), "text"));
		
		statisticManager = new VehicleStatisticManager(eventManager, playerLifecycleHolder, this.datastore);
		
		playerOwnedVehicles = new HashMap<>();
		ownedVehicles = new HashSet<>();
		
		playerOwnedVehicleLastPassengers = new WeakHashMap<>();
		
		init();
	}
	
	protected void onInit()
	{
		playerLifecycleHolder.registerClass(PlayerVehicleManagerContext.class, (eventManager, player) ->
			new PlayerVehicleManagerContext(eventManager, player, VehicleManagerServiceImpl.this, datastore));
		
		eventManager.registerHandler(PlayerConnectEvent.class, HandlerPriority.NORMAL, (e) ->
		{
			
		});
		
		eventManager.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.NORMAL, (e) ->
		{
			Player player = e.getPlayer();
			unownVehicle(player);
		});
		
		eventManager.registerHandler(PlayerCommandEvent.class, HandlerPriority.NORMAL, (e) ->
		{
			if (isCommandEnabled == false) return;
			
			Player player = e.getPlayer();
			
			String command = e.getCommand();
			String[] splits = command.split(" ", 2);
			
			String operation = splits[0].toLowerCase();
			Queue<String> args = new LinkedList<>();
			
			if (splits.length > 1)
			{
				String[] argsArray = splits[1].split(" ");
				args.addAll(Arrays.asList(argsArray));
			}
			
			if (operation.equals(commandOperation))
			{
				showMainDialog(player, null);
				e.setProcessed();
				return;
			}
		});
		
		eventManager.registerHandler(MainMenuDialogExtendEvent.class, HandlerPriority.NORMAL, (e) ->
		{
			Player player = e.getPlayer();
			WlListDialog dialog = e.getDialog();
			
			dialog.getItems().add(ListDialogItem.create()
				.itemText(localizedStringSet.get(player, "Dialog.VehicleManagerDialog.CurrentVehicle"))
				.enabled(() -> player.isInAnyVehicle() && getOwnedVehicle(player) != player.getVehicle())
				.onSelect((i) ->
				{
					player.playSound(1083, player.getLocation());
					Vehicle vehicle = player.getVehicle();
					if (vehicle != null) VehicleDialog.create(player, eventManager, dialog, vehicle, VehicleManagerServiceImpl.this).show();
				})
				.build());
			
			dialog.getItems().add(ListDialogItem.create()
				.itemText(localizedStringSet.get(player, "Dialog.VehicleManagerDialog.MyVehicle"))
				.enabled(() -> getOwnedVehicle(player) != null)
				.onSelect((i) ->
				{
					player.playSound(1083, player.getLocation());
					Vehicle vehicle = getOwnedVehicle(player);
					if (vehicle != null) VehicleDialog.create(player, eventManager, dialog, vehicle, VehicleManagerServiceImpl.this).show();
				})
				.build());
			
			dialog.getItems().add(ListDialogItem.create()
				.itemText(localizedStringSet.get(player, "Name.Full"))
				.onSelect((i) ->
				{
					player.playSound(1083, player.getLocation());
					showMainDialog(player, dialog);
				})
				.build());
		});

		addDestroyable(playerLifecycleHolder);
		addDestroyable(statisticManager);
	}

	protected void onDestroy()
	{
		
	}
	
	public LocalizedStringSet getLocalizedStringSet()
	{
		return localizedStringSet;
	}
	
	public OwnedVehicleLastPassengers getOwnedVehicleLastPassengers(Player player)
	{
		return playerOwnedVehicleLastPassengers.get(player);
	}
	
	@Override
	public Plugin getPlugin()
	{
		return plugin;
	}
	
	@Override
	public void showMainDialog(Player player, AbstractDialog parentDialog)
	{
		VehicleManagerDialog.create(player, rootEventManager, parentDialog, this).show();
	}
	
	@Override
	public Vehicle createOwnVehicle(Player player, int modelId)
	{
		Random random = new Random();
		
		Vehicle prevVehicle = player.getVehicle();

		List<Player> passengers = null;
		Velocity velocity = null;
		if (prevVehicle != null && prevVehicle.isDestroyed() == false)
		{
			passengers = VehicleUtils.getVehiclePassengers(prevVehicle);
			velocity = prevVehicle.getVelocity();
		}
		
		Vehicle vehicle = Vehicle.create(modelId, player.getLocation(), random.nextInt(256), random.nextInt(256), 3600);
		ownVehicle(player, vehicle);
		
		PlayerPreferences pref = getPlayerPreferences(player);
		if (pref.isAutoCarryPassengers())
		{
			if (passengers == null)
			{
				OwnedVehicleLastPassengers lastPassengers = playerOwnedVehicleLastPassengers.get(player);
				if (lastPassengers != null && lastPassengers.lastUpdate+2000L > System.currentTimeMillis())
				{
					passengers = lastPassengers.passengers;
				}
			}
			if (passengers != null)
			{
				int limits = Math.min(passengers.size(), VehicleModel.getSeats(vehicle.getModelId())-1);
				for (int i=0; i<limits; i++) vehicle.putPlayer(passengers.get(i), i+1);
			}
		}
		
		if (velocity != null) vehicle.setVelocity(velocity);
		
		statisticManager.getPlayerVehicleStatistic(player, modelId).onSpawn();
		statisticManager.getGlobalVehicleStatistic(modelId).onSpawn();
		return vehicle;
	}

	@Override
	public void ownVehicle(Player player, Vehicle vehicle)
	{
		if (playerOwnedVehicles.containsKey(player)) unownVehicle(player);
		
		playerOwnedVehicles.put(player, vehicle);
		ownedVehicles.add(vehicle);
	}
	
	@Override
	public void unownVehicle(Player player)
	{
		Vehicle vehicle = playerOwnedVehicles.get(player);
		if (vehicle == null) return;
		
		playerOwnedVehicles.remove(player);
		ownedVehicles.remove(vehicle);
		
		if (vehicle.isStatic() == false)
		{
			playerOwnedVehicleLastPassengers.put(player, new OwnedVehicleLastPassengers(System.currentTimeMillis(), VehicleUtils.getVehiclePassengers(vehicle)));
			vehicle.destroy();
		}
	}
	
	@Override
	public Vehicle getOwnedVehicle(Player player)
	{
		Vehicle vehicle = playerOwnedVehicles.get(player);
		if (vehicle == null) return null;
		
		if (vehicle.isDestroyed())
		{
			unownVehicle(player);
			vehicle = null;
		}
		
		return vehicle;
	}

	@Override
	public boolean isOwned(Vehicle vehicle)
	{
		return ownedVehicles.contains(vehicle);
	}

	@Override
	public GlobalVehicleStatistic getGlobalVehicleStatistic(int modelId)
	{
		return statisticManager.getGlobalVehicleStatistic(modelId);
	}

	@Override
	public Collection<GlobalVehicleStatistic> getGlobalVehicleStatistics()
	{
		return statisticManager.getGlobalVehicleStatistics();
	}

	@Override
	public PlayerVehicleStatistic getPlayerVehicleStatistic(Player player, int modelId)
	{
		return statisticManager.getPlayerVehicleStatistic(player, modelId);
	}

	@Override
	public Collection<PlayerVehicleStatistic> getPlayerVehicleStatistics(Player player)
	{
		return statisticManager.getPlayerVehicleStatistics(player);
	}
	
	@Override
	public OncePlayerVehicleStatistic getPlayerCurrentOnceStatistic(Player player)
	{
		return statisticManager.getPlayerCurrentOnceStatistic(player);
	}
	
	@Override
	public List<OncePlayerVehicleStatistic> getPlayerRecordedOnceStatistics(Player player)
	{
		return statisticManager.getPlayerRecordedOnceStatistics(player);
	}
	
	@Override
	public OncePlayerVehicleStatisticImpl startRacingStatistic(Player player)
	{
		return statisticManager.startRacingStatistic(player);
	}
	
	@Override
	public void endRacingStatistic(Player player)
	{
		statisticManager.endRacingStatistic(player);
	}
	
	@Override
	public boolean isRacingStatistic(Player player)
	{
		return statisticManager.isRacingStatistic(player);
	}
	
	@Override
	public PlayerPreferences getPlayerPreferences(Player player)
	{
		PlayerVehicleManagerContext context = playerLifecycleHolder.getObject(player, PlayerVehicleManagerContext.class);
		return context.getPlayerPreferences();
	}
	
	@Override
	public PlayerPreferencesBase getEffectivePlayerPreferences(Player player)
	{
		PlayerVehicleManagerContext context = playerLifecycleHolder.getObject(player, PlayerVehicleManagerContext.class);
		return context.getEffectivePlayerPreferences();
	}

	@Override
	public void addOverrideLimit(Player player, PlayerOverrideLimit limit)
	{
		PlayerVehicleManagerContext context = playerLifecycleHolder.getObject(player, PlayerVehicleManagerContext.class);
		context.getEffectivePlayerPreferences().addLimit(limit);
	}
	
	@Override
	public void removeOverrideLimit(Player player, PlayerOverrideLimit limit)
	{
		PlayerVehicleManagerContext context = playerLifecycleHolder.getObject(player, PlayerVehicleManagerContext.class);
		context.getEffectivePlayerPreferences().removeLimit(limit);
	}
	
	@Override
	public boolean hasOverrideLimit(Player player, PlayerOverrideLimit limit)
	{
		PlayerVehicleManagerContext context = playerLifecycleHolder.getObject(player, PlayerVehicleManagerContext.class);
		return context.getEffectivePlayerPreferences().hasLimit(limit);
	}
	
	@Override
	public void clearOverrideLimits(Player player)
	{
		PlayerVehicleManagerContext context = playerLifecycleHolder.getObject(player, PlayerVehicleManagerContext.class);
		context.getEffectivePlayerPreferences().clearLimits();
	}
}
