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

public class VehicleComponentPaintjobDialog extends AbstractListDialog
{
	private final VehicleManagerServiceImpl vehicleManagerService;
	private final Vehicle vehicle;
	
	
	public VehicleComponentPaintjobDialog
	(final Player player, final Shoebill shoebill, final EventManager rootEventManager, final AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, rootEventManager, parentDialog);
		this.vehicleManagerService = vehicleManagerService;
		this.vehicle = vehicle;
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();

		final int modelId = vehicle.getModelId();
		final String name = VehicleModel.getName(modelId);
		
		for (int i=0; i<3; i++)
		{
			final int paintjobId = i;
			final String item = stringSet.format(player, "Dialog.VehicleComponentPaintjobDialog.Item", paintjobId);
			
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1134, player.getLocation());
					player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "PaintMessage", name, paintjobId));
					
					vehicle.setPaintjob(paintjobId);
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
		
		this.caption = stringSet.format(player, "Dialog.VehicleComponentPaintjobDialog.Caption", name);
		super.show();
	}
}
