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
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

public class VehicleResprayDialog extends AbstractListDialog
{
	public VehicleResprayDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManagerService, final int start, final int end)
	{
		this(player, shoebill, eventManager, parentDialog, vehicle, vehicleManagerService, start, end, -1);
	}
	
	public VehicleResprayDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManagerService, final int start, final int end, final int color1)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		if (vehicle == null)
		{
			destroy();
			return;
		}
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		String type = stringSet.get(player, "Component.Color.Primary");
		if (color1 != -1) type = stringSet.get(player, "Component.Color.Secondary");
		
		this.caption = stringSet.format(player, "Dialog.VehicleResprayDialog.Caption", name, type, modelId, vehicle.getHealth()/10);
		
		for (int i=start; i<end; i++)
		{
			final int idx = i;
			
			String item = stringSet.format(player, "Dialog.VehicleResprayDialog.Item", new Color(VehicleResprayGroupDialog.VEHICLE_COLOR_TABLE_RGBA[idx]).toEmbeddingString(), idx);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					if (color1 == -1) new VehicleResprayGroupDialog(player, shoebill, eventManager, VehicleResprayDialog.this, vehicle, vehicleManagerService, idx).show();
					else player.getVehicle().setColor(color1, idx);
				}
			});
		}
	}
}
