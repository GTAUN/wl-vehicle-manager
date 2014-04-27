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
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

public class VehicleComponentPaintjobDialog extends WlListDialog
{
	public VehicleComponentPaintjobDialog
	(Player player, EventManager eventManager, AbstractDialog parent, Vehicle vehicle, VehicleManagerServiceImpl service)
	{
		super(player, eventManager);
		setParentDialog(parent);
		
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);

		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		setCaption(stringSet.format("Dialog.VehicleComponentPaintjobDialog.Caption", name));
		
		for (int i=0; i<3; i++)
		{
			int paintjobId = i;
			String item = stringSet.format("Dialog.VehicleComponentPaintjobDialog.Item", paintjobId);
			
			addItem(item, (d) ->
			{
				player.playSound(1134, player.getLocation());
				stringSet.sendMessage(Color.LIGHTBLUE, "PaintMessage", name, paintjobId);
				
				vehicle.setPaintjob(paintjobId);
				showParentDialog();
			});
		}
	}
}
