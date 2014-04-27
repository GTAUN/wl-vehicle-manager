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

import net.gtaun.shoebill.common.Filter;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.UnitUtils;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;


public class EmptyVehicleListDialog extends WlPageListDialog
{
	private final VehicleManagerServiceImpl service;
	private final Comparator<Vehicle> comparator;
	private final Filter<Vehicle> filter;
	
	private final PlayerStringSet stringSet;
	
	
	public EmptyVehicleListDialog(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service, Comparator<Vehicle> comparator, Filter<Vehicle> filter)
	{
		super(player, eventManager);
		setParentDialog(parent);
		
		this.service = service;
		this.comparator = comparator;
		this.filter = filter;
		this.stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		setCaption(() -> stringSet.format("Dialog.EmptyVehicleListDialog.Caption", getCurrentPage() + 1, getMaxPage() + 1));
	}
	
	@Override
	public void show()
	{
		Collection<Vehicle> vehicles = Vehicle.get();
		Collection<Player> players = Player.get();
		
		Location playerLoc = player.getLocation();
		
		Set<Vehicle> occupiedVehicles = new HashSet<>();
		for (Player player : players) if (player.isInAnyVehicle()) occupiedVehicles.add(player.getVehicle());
		
		SortedSet<Vehicle> sortedVehicles = new TreeSet<>(comparator);
		for (Vehicle vehicle : vehicles)
		{
			if (occupiedVehicles.contains(vehicle) == false && service.isOwned(vehicle) == false && filter.isAcceptable(vehicle))
			{
				sortedVehicles.add(vehicle);
			}
		}
		
		items.clear();
		for (Vehicle vehicle : sortedVehicles)
		{
			float distance = playerLoc.distance(vehicle.getLocation());
			addItem(() ->
				{
					String format = stringSet.get("Dialog.EmptyVehicleListDialog.Item");
					if (player.isAdmin()) format = stringSet.get("Dialog.EmptyVehicleListDialog.ItemAdmin");
					
					int modelId = vehicle.getModelId();
					String modelName = VehicleModel.getName(modelId);
					
					return String.format(format, vehicle.getId(), modelName, modelId, distance, UnitUtils.meterToYard(distance));
				}, (i) ->
				{
					player.playSound(1083);
					VehicleDialog.create(player, eventManagerNode.getParent(), this, vehicle, service).show();
				});
		}

		super.show();
	}
}
