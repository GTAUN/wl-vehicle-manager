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
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleCreateTypeListDialog extends AbstractPageListDialog
{
	private final VehicleManagerService vehicleManager;
	
	
	public VehicleCreateTypeListDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager, final String typename, final VehicleType type)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		
		setCaption(String.format("刷车 - 车辆类型选择 - 类型：%1$s", typename));
		
		for(final int modelId : VehicleModel.getIds(type))
		{
			final String name = VehicleModel.getName(modelId);
			final int seats = VehicleModel.getSeats(modelId);
			
			dialogListItems.add(new DialogListItem(String.format("%1$s (座位数: %2$d)", name, seats))
			{
				@Override
				public void onItemSelect()
				{
					Vehicle vehicle = shoebill.getSampObjectFactory().createVehicle(modelId, player.getLocation(), 0.0f, 0, 0, 3600);
					vehicleManager.ownVehicle(player, vehicle);
					destroy();
				}
			});
		}
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			new VehicleCreateMainDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
