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
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.VehicleTextUtils;

public class VehicleComponentAddDialog extends AbstractListDialog
{
	private final VehicleManagerServiceImpl vehicleManagerService;
	private final Vehicle vehicle;
	private final VehicleComponentSlot componentSlot;
	
	
	public VehicleComponentAddDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManagerService, final VehicleComponentSlot slot)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.vehicleManagerService = vehicleManagerService;
		this.vehicle = vehicle;
		this.componentSlot = slot;
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();

		final int modelId = vehicle.getModelId();
		final String name = VehicleModel.getName(modelId);
		
		final Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(modelId, slot);
		final String slotName = VehicleTextUtils.getComponentSlotName(stringSet, player, slot);
		
		for (final int cid : components)
		{
			final String componentName = VehicleComponentModel.getName(cid);
			final String item = stringSet.format(player, "Dialog.VehicleComponentAddDialog.Item", slotName, componentName);
			
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1133, player.getLocation());
					player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Dialog.VehicleComponentAddDialog.AddMessage", name, slotName, componentName));
					
					vehicle.getComponent().add(cid);
					showParentDialog();
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
		String slotName = VehicleTextUtils.getComponentSlotName(stringSet, player, componentSlot);
		
		this.caption = stringSet.format(player, "Dialog.VehicleComponentAddDialog.Caption", name, slotName);
		super.show();
	}
}
