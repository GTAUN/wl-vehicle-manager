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
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleResprayDialog extends AbstractListDialog
{
	public VehicleResprayDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerService vehicleManager, final int start, final int end)
	{
		this(player, shoebill, eventManager, parentDialog, vehicle, vehicleManager, start, end, -1);
	}
	
	public VehicleResprayDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerService vehicleManager, final int start, final int end, final int color1)
	{
		super(player, shoebill, eventManager, parentDialog);
		
		if (vehicle == null)
		{
			destroy();
			return;
		}
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		String type = "主颜色";
		if (color1 != -1) type = "子颜色";
		
		this.caption = String.format("%1$s: 选择 %2$s 的%3$s (模型: %4$d, HP: %5$1.0f％)", "车管", name, type, modelId, vehicle.getHealth()/10);
		
		for (int i=start; i<end; i++)
		{
			final int idx = i;
			
			String item = String.format("\t\t\t%1$s▇{FFFFFF} 颜色: %2$d %1$s▇", new Color(VehicleResprayGroupDialog.VEHICLE_COLOR_TABLE_RGBA[idx]).toEmbeddingString(), idx);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					if (color1 == -1) new VehicleResprayGroupDialog(player, shoebill, eventManager, VehicleResprayDialog.this, vehicle, vehicleManager, idx).show();
					else player.getVehicle().setColor(color1, idx);
				}
			});
		}
	}
}
