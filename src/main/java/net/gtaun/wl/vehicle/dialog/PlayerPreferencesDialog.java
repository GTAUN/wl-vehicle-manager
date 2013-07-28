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
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.vehicle.PlayerPreferences;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class PlayerPreferencesDialog extends AbstractListDialog
{
	public PlayerPreferencesDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager, parentDialog);
		
		this.caption = String.format("%1$s: 个人偏好设置", "车管");
		
		final PlayerPreferences pref = vehicleManager.getPlayerPreferences(player);

		dialogListItems.add(new DialogListItemSwitch("显示速度计挂件")
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isSpeedometerWidgetEnabled();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setSpeedometerWidgetEnabled(!pref.isSpeedometerWidgetEnabled());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("无限氮气加速")
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isUnlimitedNOS();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setUnlimitedNOS(!pref.isUnlimitedNOS());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("自动修复车辆")
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isAutoRepair();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setAutoRepair(!pref.isAutoRepair());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("自动翻转车辆")
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isAutoFlip();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setAutoFlip(!pref.isAutoFlip());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("自动携带乘客")
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isAutoCarryPassengers();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setAutoCarryPassengers(!pref.isAutoCarryPassengers());
				show();
			}
		});
	}
}
