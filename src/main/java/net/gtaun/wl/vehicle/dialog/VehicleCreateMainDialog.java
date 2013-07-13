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

import java.util.HashMap;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManager;

public class VehicleCreateMainDialog extends AbstractListDialog
{
	private static final Map<VehicleType, String> TYPE_NAMES = new HashMap<>();
	static
	{
		TYPE_NAMES.put(VehicleType.UNKNOWN,			"其他");
		TYPE_NAMES.put(VehicleType.BICYCLE,			"自行车");
		TYPE_NAMES.put(VehicleType.MOTORBIKE,		"摩托车");
		TYPE_NAMES.put(VehicleType.CAR,				"普通车子");
		TYPE_NAMES.put(VehicleType.TRAILER,			"拖车");
		TYPE_NAMES.put(VehicleType.REMOTE_CONTROL,	"遥控装置");
		TYPE_NAMES.put(VehicleType.TRAIN,			"火车");
		TYPE_NAMES.put(VehicleType.BOAT,			"船只");
		TYPE_NAMES.put(VehicleType.AIRCRAFT,		"飞机");
		TYPE_NAMES.put(VehicleType.HELICOPTER,		"直升机");
		TYPE_NAMES.put(VehicleType.TANK,			"坦克");
	}
	
	
	private final VehicleManager vehicleManager;
	
	
	public VehicleCreateMainDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManager vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		
		setCaption("刷车 - 车辆类型选择");
		
		for (Map.Entry<VehicleType, String> entry : TYPE_NAMES.entrySet())
		{
			final VehicleType type = entry.getKey();
			final String typename = entry.getValue();
			
			dialogListItems.add(new DialogListItem(typename)
			{
				@Override
				public void onItemSelect()
				{
					new VehicleCreateTypeListDialog(player, shoebill, eventManager, vehicleManager, typename, type);
					destroy();
				}
			});
		}
	}
	
	@Override
	protected void onDialogCancel(DialogCancelEvent event)
	{
		new VehicleCreateMainDialog(player, shoebill, rootEventManager, vehicleManager).show();
		super.onDialogCancel(event);
	}
}
