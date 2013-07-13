/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.vehicle.dialog;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.gtaun.shoebill.SampObjectStore;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManager;

public class EmptyVehicleListDialog extends AbstractPageListDialog
{
	private VehicleManager vehicleManager;
	private Comparator<Vehicle> vehicleComparator;
	
	
	public EmptyVehicleListDialog(Player player, Shoebill shoebill, EventManager eventManager, VehicleManager vehicleManager, Comparator<Vehicle> comparator)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		this.vehicleComparator = comparator;
	}
	
	@Override
	public void show()
	{
		SampObjectStore store = shoebill.getSampObjectStore();
		Collection<Vehicle> vehicles = store.getVehicles();
		Collection<Player> players = store.getPlayers();
		
		Location playerLoc = player.getLocation();
		
		Set<Vehicle> occupiedVehicles = new HashSet<>();
		for (Player player : players) if (player.isInAnyVehicle()) occupiedVehicles.add(player.getVehicle());
		
		SortedSet<Vehicle> sortedVehicles = new TreeSet<>(vehicleComparator);
		for (Vehicle vehicle : vehicles)
		{
			if (occupiedVehicles.contains(vehicle) == false && vehicleManager.isOwned(vehicle) == false) sortedVehicles.add(vehicle);
		}
		
		dialogListItems.clear();
		for (final Vehicle vehicle : sortedVehicles)
		{
			final float distance = playerLoc.distance(vehicle.getLocation());
			final String item = "ID: " + vehicle.getId() + "		车辆型号: " + vehicle.getModelId() + "	距离: " + distance;
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					new VehicleDialog(player, shoebill, rootEventManager, vehicle, vehicleManager).show();
					destroy();
				}
			});
		}

		setCaption("空车列表 - " + (getCurrentPage()+1) + "/" + (getMaxPage()+1));
		super.show();
	}

	@Override
	protected void onDialogCancel(DialogCancelEvent event)
	{
		new VehicleManagerDialog(player, shoebill, rootEventManager, vehicleManager).show();
		super.onDialogCancel(event);
	}
}
