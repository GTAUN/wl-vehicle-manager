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
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceDescription;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.DistanceVehicleFilter;
import net.gtaun.wl.vehicle.util.NearbyVehicleComparator;

public class VehicleManagerDialog extends AbstractListDialog
{
	public VehicleManagerDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerServiceImpl vehicleManagerService)
	{
		this(player, shoebill, eventManager, null, vehicleManagerService);
	}
	
	public VehicleManagerDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		this.caption = stringSet.get(player, "Dialog.VehicleManagerDialog.Caption");

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.CurrentVehicle"))
		{
			@Override
			public boolean isEnabled()
			{
				return player.isInAnyVehicle() && vehicleManagerService.getOwnedVehicle(player) != player.getVehicle();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				Vehicle vehicle = player.getVehicle();
				if (vehicle != null) new VehicleDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicle, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.MyVehicle"))
		{
			@Override
			public boolean isEnabled()
			{
				return vehicleManagerService.getOwnedVehicle(player) != null;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				Vehicle vehicle = vehicleManagerService.getOwnedVehicle(player);
				if (vehicle != null) new VehicleDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicle, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.CreateVehicle"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new VehicleCreateMainDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.NearbyEmptyVehicle"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Location loc = player.getLocation();
				new EmptyVehicleListDialog
				(
					player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManagerService,
					new NearbyVehicleComparator(loc), new DistanceVehicleFilter(loc, 500.0f)
				).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.DrivingAndRidingRecord"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new DrivingRecordStatisticDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.PersonalPreferences"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new PlayerPreferencesDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.PersonalStatistics"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new PlayerStatisticDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.GlobalStatistics"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new GlobalStatisticDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManagerService).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.Help"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				String caption = stringSet.get(player, "Dialog.HelpDialog.Caption");
				new MsgboxDialog(player, shoebill, eventManager, VehicleManagerDialog.this, caption, stringSet.get(player, "Dialog.HelpDialog.Text")).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleManagerDialog.About"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Plugin plugin = vehicleManagerService.getPlugin();
				ResourceDescription desc = plugin.getDescription();
				
				String caption = stringSet.get(player, "Dialog.AboutDialog.Caption");
				String format = stringSet.get(player, "Dialog.AboutDialog.Text");
				String message = String.format(format, desc.getVersion(), desc.getBuildNumber(), desc.getBuildDate());
				
				new MsgboxDialog(player, shoebill, eventManager, VehicleManagerDialog.this, caption, message).show();
			}
		});
	}
}
