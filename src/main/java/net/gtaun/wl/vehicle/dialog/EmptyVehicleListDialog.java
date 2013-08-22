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

package net.gtaun.wl.vehicle.dialog;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.gtaun.shoebill.SampObjectStore;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.Filter;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class EmptyVehicleListDialog extends AbstractPageListDialog
{
	private final VehicleManagerService vehicleManager;
	private final Comparator<Vehicle> comparator;
	private final Filter<Vehicle> filter;
	
	
	public EmptyVehicleListDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, VehicleManagerService vehicleManager, Comparator<Vehicle> comparator, Filter<Vehicle> filter)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.vehicleManager = vehicleManager;
		this.comparator = comparator;
		this.filter = filter;
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
		
		SortedSet<Vehicle> sortedVehicles = new TreeSet<>(comparator);
		for (Vehicle vehicle : vehicles)
		{
			if (occupiedVehicles.contains(vehicle) == false && vehicleManager.isOwned(vehicle) == false && filter.isAcceptable(vehicle))
			{
				sortedVehicles.add(vehicle);
			}
		}
		
		dialogListItems.clear();
		for (final Vehicle vehicle : sortedVehicles)
		{
			final float distance = playerLoc.distance(vehicle.getLocation());
			dialogListItems.add(new DialogListItem()
			{
				@Override
				public String toItemString()
				{
					String format = "%2$s	型号: %3$d	距离: %4$1.0f米";
					if (player.isAdmin()) format = "%2$s (ID: %1$d)		型号: %3$d	距离: %4$1.0f米";
					
					final int modelId = vehicle.getModelId();
					final String modelName = VehicleModel.getName(modelId);
					
					return String.format(format, vehicle.getId(), modelName, modelId, distance);
				}
				
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new VehicleDialog(player, shoebill, rootEventManager, EmptyVehicleListDialog.this, vehicle, vehicleManager).show();
				}
			});
		}

		this.caption = String.format("%1$s: 附近空车列表 (%2$d/%3$d)", "车管", getCurrentPage() + 1, getMaxPage() + 1);
		super.show();
	}
}
