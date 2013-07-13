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
import net.gtaun.shoebill.common.VehicleUtils;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManager;

public class VehicleDialog extends AbstractListDialog
{
	private final Vehicle vehicle;
	private final VehicleManager vehicleManager;
	
	
	public VehicleDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final Vehicle vehicle, final VehicleManager vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
		
		if (vehicle == null)
		{
			destroy();
			return;
		}
		
		dialogListItems.add(new DialogListItem("占用")
		{
			@Override
			public boolean isEnabled()
			{
				if (vehicleManager.isOwned(vehicle)) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				vehicleManager.ownVehicle(player, vehicle);
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("修复")
		{
			@Override
			public boolean isEnabled()
			{
				if (VehicleUtils.isVehicleDriver(vehicle, player) == false) return false;
				
				VehicleDamage damage = vehicle.getDamage();
				if (damage.getDoors() != 0) return true;
				if (damage.getLights() != 0) return true;
				if (damage.getPanels() != 0) return true;
				if (damage.getTires() != 0) return true;
				if (vehicle.getHealth() < 100.0f) return true;
				return false;
			}
			
			@Override
			public void onItemSelect()
			{
				vehicle.repair();
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("翻转")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public void onItemSelect()
			{
				vehicle.setLocation(vehicle.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("改变颜色")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public void onItemSelect()
			{
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("踢掉乘客")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.getVehiclePassengers(vehicle).isEmpty() == false;
			}
			
			@Override
			public void onItemSelect()
			{
				for (Player passenger : VehicleUtils.getVehiclePassengers(vehicle))
				{
					passenger.removeFromVehicle();
				}
				
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("锁车")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public boolean isSwitched()
			{
				return vehicle.getState().getDoors() != 0;
			}
			
			@Override
			public void onItemSelect()
			{
				vehicle.getState().setDoors(vehicle.getState().getDoors() ^ 1);
				show();
			}
		});
	}
	
	@Override
	public void show()
	{
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		setCaption(String.format("车辆 %1$s 菜单 - 车辆ID：%2$d, 模型：%3$d, HP：%4$0.1f", name, vehicle.getModelId(), modelId, vehicle.getHealth()));
		super.show();
	}

	@Override
	protected void onDialogCancel(DialogCancelEvent event)
	{
		new VehicleManagerDialog(player, shoebill, rootEventManager, vehicleManager).show();
		super.onDialogCancel(event);
	}
}
