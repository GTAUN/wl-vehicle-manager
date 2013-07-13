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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.util.NearbyVehicleComparator;

public class VehicleManagerDialog extends AbstractListDialog
{
	public VehicleManagerDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		setCaption("车辆管理系统");

		dialogListItems.add(new DialogListItem("当前车辆 ...")
		{
			@Override
			public boolean isEnabled()
			{
				return player.isInAnyVehicle();
			}
			
			@Override
			public void onItemSelect()
			{
				Vehicle vehicle = player.getVehicle();
				if (vehicle != null) new VehicleDialog(player, shoebill, eventManager, vehicle, vehicleManager).show();
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem("我的车辆 ...")
		{
			@Override
			public boolean isEnabled()
			{
				return vehicleManager.getOwnedVehicle(player) != null;
			}
			
			@Override
			public void onItemSelect()
			{
				Vehicle vehicle = vehicleManager.getOwnedVehicle(player);
				if (vehicle != null) new VehicleDialog(player, shoebill, eventManager, vehicle, vehicleManager).show();
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem("刷车 ...")
		{
			@Override
			public void onItemSelect()
			{
				new VehicleCreateMainDialog(player, shoebill, eventManager, vehicleManager).show();
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem("搜寻附近空车 ...")
		{
			@Override
			public void onItemSelect()
			{
				new EmptyVehicleListDialog
				(
					player, shoebill, eventManager, vehicleManager,
					new NearbyVehicleComparator(player.getLocation())
				).show();
				destroy();
			}
		});
	}
}
