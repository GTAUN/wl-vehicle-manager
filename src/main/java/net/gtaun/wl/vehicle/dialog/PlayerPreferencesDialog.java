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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItemSwitch;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.PlayerPreferences;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

public class PlayerPreferencesDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		PlayerPreferences pref = service.getPlayerPreferences(player);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.get("Dialog.PlayerPreferencesDialog.Caption"))
			.item(ListDialogItemSwitch.create()
				.itemText(stringSet.get("Dialog.PlayerPreferencesDialog.VehicleWidget"))
				.statusSupplier(() -> pref.isVehicleWidgetEnabled())
				.onSelect((i) -> pref.setVehicleWidgetEnabled(!pref.isVehicleWidgetEnabled()))
				.build())
			
			.item(ListDialogItemSwitch.create()
				.itemText(stringSet.get("Dialog.PlayerPreferencesDialog.InfiniteNitrous"))
				.statusSupplier(() -> pref.isInfiniteNitrous())
				.onSelect((i) -> pref.setInfiniteNitrous(!pref.isInfiniteNitrous()))
				.build())
				
			.item(ListDialogItemSwitch.create()
				.itemText(stringSet.get("Dialog.PlayerPreferencesDialog.AutoRepair"))
				.statusSupplier(() -> pref.isAutoRepair())
				.onSelect((i) -> pref.setAutoRepair(!pref.isAutoRepair()))
				.build())
				
			.item(ListDialogItemSwitch.create()
				.itemText(stringSet.get("Dialog.PlayerPreferencesDialog.AutoFlip"))
				.statusSupplier(() -> pref.isAutoFlip())
				.onSelect((i) -> pref.setAutoFlip(!pref.isAutoFlip()))
				.build())
				
			.item(ListDialogItemSwitch.create()
				.itemText(stringSet.get("Dialog.PlayerPreferencesDialog.AutoCarryPassengers"))
				.statusSupplier(() -> pref.isAutoCarryPassengers())
				.onSelect((i) -> pref.setAutoCarryPassengers(!pref.isAutoCarryPassengers()))
				.build())
			
			.onClickOk((d, i) ->
			{
				player.playSound(1083, player.getLocation());
				i.getCurrentDialog().show();
			})
			.build();
	}
}
