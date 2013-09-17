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

import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.VehicleTextUtils;

public class VehicleComponentDialog extends AbstractListDialog
{
	private final VehicleManagerServiceImpl vehicleManagerService;
	private final Vehicle vehicle;
	
	
	public VehicleComponentDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.vehicleManagerService = vehicleManagerService;
		this.vehicle = vehicle;
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();

		final int vehcileModelId = vehicle.getModelId();
		
		final String paintjobItem = stringSet.get(player, "Component.Paintjob");
		if (VehicleModel.isPrintjobSupported(vehcileModelId)) dialogListItems.add(new DialogListItem(paintjobItem)
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new VehicleComponentPaintjobDialog(player, shoebill, eventManager, VehicleComponentDialog.this, vehicle, vehicleManagerService).show();
			}
		});
		
		for (final VehicleComponentSlot slot : VehicleComponentModel.getVehicleSupportedSlots(vehcileModelId))
		{
			final Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(vehcileModelId, slot);
			final String slotName = VehicleTextUtils.getComponentSlotName(stringSet, player, slot);
			final String curName = VehicleComponentModel.getName(vehicle.getComponent().get(slot));
			
			final String item = stringSet.format(player, "Dialog.VehicleComponentDialog.Item", slotName, curName, components.size());
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new VehicleComponentAddDialog(player, shoebill, rootEventManager, VehicleComponentDialog.this, vehicle, vehicleManagerService, slot).show();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		if (player.getVehicle() != vehicle)
		{
			Location loc = vehicle.getLocation();
			player.setCameraLookAt(loc);
			loc.setZ(loc.getZ() + 10.0f);
			player.setCameraPosition(loc);
		}
		
		this.caption = stringSet.format(player, "Dialog.VehicleComponentDialog.Caption", name, modelId, vehicle.getHealth()/10);
		super.show();
	}
}
