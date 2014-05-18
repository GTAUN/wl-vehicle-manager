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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog.AbstractListDialogBuilder;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.VehicleTextUtils;

public class VehicleComponentDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, Vehicle vehicle, VehicleManagerServiceImpl service)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption((d) -> stringSet.format("Dialog.VehicleComponentDialog.Caption", name, modelId, vehicle.getHealth()/10))
			.execute((b) ->
			{
				String paintjobItem = stringSet.get("Component.Paintjob");
				
				// XXX: Buggy Eclipse JDT Compiler
				if (VehicleModel.isPaintjobSupported(modelId)) ((AbstractListDialogBuilder<?, ?>) b).item(paintjobItem, (i) ->
				{
					new VehicleComponentPaintjobDialog(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
				});
				
				for (VehicleComponentSlot slot : VehicleComponentModel.getVehicleSupportedSlots(modelId))
				{
					Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(modelId, slot);
					String slotName = VehicleTextUtils.getComponentSlotName(stringSet, slot);
					String curName = VehicleComponentModel.getName(vehicle.getComponent().get(slot));
					
					String itemText = stringSet.format("Dialog.VehicleComponentDialog.Item", slotName, curName, components.size());
					
					// XXX: Buggy Eclipse JDT Compiler
					((AbstractListDialogBuilder<?, ?>) b).item(itemText, (i) ->
					{
						VehicleComponentAddDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service, slot).show();
					});
				}
			})
			.onClickOk((d, i) ->
			{
				player.playSound(1083);
			})
			.onShow((d) ->
			{
				if (player.getVehicle() == vehicle) return;
				
				Location loc = vehicle.getLocation();
				player.setCameraLookAt(loc);
				loc.setZ(loc.getZ() + 10.0f);
				player.setCameraPosition(loc);
			})
			.onClose((d, t) ->
			{
				if (player.getVehicle() == vehicle) return;
				player.setCameraBehind();
			})
			.build();
	}
}
