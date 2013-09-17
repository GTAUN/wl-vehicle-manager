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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.PlayerPreferences;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

public class PlayerPreferencesDialog extends AbstractListDialog
{
	public PlayerPreferencesDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		final PlayerPreferences pref = vehicleManagerService.getPlayerPreferences(player);
		
		this.caption = stringSet.get(player, "Dialog.PlayerPreferencesDialog.Caption");
		
		dialogListItems.add(new DialogListItemSwitch(stringSet.get(player, "Dialog.PlayerPreferencesDialog.VehicleWidget"))
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isVehicleWidgetEnabled();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setVehicleWidgetEnabled(!pref.isVehicleWidgetEnabled());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch(stringSet.get(player, "Dialog.PlayerPreferencesDialog.InfiniteNitrous"))
		{
			@Override
			public boolean isSwitched()
			{
				return pref.isInfiniteNitrous();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				pref.setInfiniteNitrous(!pref.isInfiniteNitrous());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch(stringSet.get(player, "Dialog.PlayerPreferencesDialog.AutoRepair"))
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
		
		dialogListItems.add(new DialogListItemSwitch(stringSet.get(player, "Dialog.PlayerPreferencesDialog.AutoFlip"))
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
		
		dialogListItems.add(new DialogListItemSwitch(stringSet.get(player, "Dialog.PlayerPreferencesDialog.AutoCarryPassengers"))
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
