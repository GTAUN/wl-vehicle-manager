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
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.VehicleTextUtils;

public class VehicleComponentAddDialog
{	
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, Vehicle vehicle, VehicleManagerServiceImpl service, VehicleComponentSlot slot)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);

		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(modelId, slot);
		String slotName = VehicleTextUtils.getComponentSlotName(stringSet, slot);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.format("Dialog.VehicleComponentAddDialog.Caption", name, slotName))
			.execute((b) ->
			{
				for (int cid : components)
				{
					String componentName = VehicleComponentModel.getName(cid);
					String itemText = stringSet.format("Dialog.VehicleComponentAddDialog.Item", slotName, componentName);
					
					b.item(itemText, (i) ->
					{
						stringSet.sendMessage(Color.LIGHTBLUE, "Dialog.VehicleComponentAddDialog.AddMessage", name, slotName, componentName);
						vehicle.getComponent().add(cid);
					});
				}
			})
			.onClickOk((d, i) ->
			{
				player.playSound(1133, player.getLocation());
				d.showParentDialog();
			})
			.build();
	}
}
