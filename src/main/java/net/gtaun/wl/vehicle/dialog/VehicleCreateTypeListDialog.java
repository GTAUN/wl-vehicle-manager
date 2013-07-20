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
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleCreateTypeListDialog extends AbstractPageListDialog
{
	private final VehicleManagerService vehicleManager;
	private final String typeName;
	
	
	public VehicleCreateTypeListDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager, final String typename, final VehicleType type)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		this.typeName = typename;
		
		for(final int modelId : VehicleModel.getIds(type))
		{
			final String name = VehicleModel.getName(modelId);
			final int seats = VehicleModel.getSeats(modelId);
			
			dialogListItems.add(new DialogListItem(String.format("%1$d - %2$s (座位数: %3$d)", modelId, name, seats))
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1057, player.getLocation());
					
					Vehicle vehicle = vehicleManager.createOwnVehicle(player, modelId);
					vehicle.putPlayer(player, 0);
					player.sendMessage(Color.LIGHTBLUE, "%1$s: 您的专属座驾 %2$s 已创建！", "车管", VehicleModel.getName(vehicle.getModelId()));
					destroy();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		setCaption(String.format("%1$s: 刷车 - 车辆类型选择 - 类型：%2$s (%3$d/%4$d)", "车管", typeName, getCurrentPage() + 1, getMaxPage() + 1));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			new VehicleCreateMainDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
