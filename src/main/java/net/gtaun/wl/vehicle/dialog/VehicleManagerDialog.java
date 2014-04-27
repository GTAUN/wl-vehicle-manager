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
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceDescription;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.DistanceVehicleFilter;
import net.gtaun.wl.vehicle.util.NearbyVehicleComparator;

public class VehicleManagerDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.get("Dialog.VehicleManagerDialog.Caption"))
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.CurrentVehicle"),
				() -> player.isInAnyVehicle() && service.getOwnedVehicle(player) != player.getVehicle(),
				(i) ->
				{
					Vehicle vehicle = player.getVehicle();
					if (vehicle != null) VehicleDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
				})
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.MyVehicle"),
				() -> service.getOwnedVehicle(player) != null,
				(i) ->
				{
					Vehicle vehicle = service.getOwnedVehicle(player);
					if (vehicle != null) VehicleDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
				})
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.CreateVehicle"), (i) ->
				new VehicleCreateMainDialog(player, eventManager, i.getCurrentDialog(), service).show())
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.NearbyEmptyVehicle"), (i) ->
			{
				Location loc = player.getLocation();
				new EmptyVehicleListDialog(
					player, eventManager, i.getCurrentDialog(), service,
					new NearbyVehicleComparator(loc), new DistanceVehicleFilter(loc, 500.0f)
				).show();
			})
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.DrivingAndRidingRecord"), (i) ->
				new DrivingRecordStatisticDialog(player, eventManager, i.getCurrentDialog(), service).show())
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.PersonalPreferences"), (i) ->
				PlayerPreferencesDialog.create(player, eventManager, i.getCurrentDialog(), service).show())
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.PersonalStatistics"), (i) ->
				PlayerStatisticDialog.create(player, eventManager, i.getCurrentDialog(), service).show())
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.GlobalStatistics"), (i) ->
				GlobalStatisticDialog.create(player, eventManager, i.getCurrentDialog(), service).show())
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.Help"), (i) ->
			{
				MsgboxDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(stringSet.get("Dialog.HelpDialog.Caption"))
					.message(stringSet.get("Dialog.HelpDialog.Text"))
					.build().show();
			})
			
			.item(stringSet.get("Dialog.VehicleManagerDialog.About"), (i) ->
			{
				Plugin plugin = service.getPlugin();
				ResourceDescription desc = plugin.getDescription();
				
				String caption = stringSet.get("Dialog.AboutDialog.Caption");
				String format = stringSet.get("Dialog.AboutDialog.Text");
				String message = String.format(format, desc.getVersion(), desc.getBuildNumber(), desc.getBuildDate());
				
				MsgboxDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(message)
					.build().show();
			})
			.onClickOk((d, i) -> player.playSound(1083, player.getLocation()))
			.build();
	}
}
