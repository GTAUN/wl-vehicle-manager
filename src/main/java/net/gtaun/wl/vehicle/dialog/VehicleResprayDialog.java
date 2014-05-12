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

public class VehicleResprayDialog
{
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, Vehicle vehicle, VehicleManagerServiceImpl service, int start, int end, int color1)
	{
		if (vehicle == null) return null;
		
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		String type = stringSet.get("Component.Color.Primary");
		if (color1 != -1) type = stringSet.get("Component.Color.Secondary");
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.format("Dialog.VehicleResprayDialog.Caption", name, type, modelId, vehicle.getHealth()/10))
			.execute((b) ->
			{
				for (int i=start; i<end; i++)
				{
					final int idx = i;
					
					String item = stringSet.format("Dialog.VehicleResprayDialog.Item", new Color(VehicleResprayGroupDialog.VEHICLE_COLOR_TABLE_RGBA[idx]).toEmbeddingString(), idx);
					b.item(item, (listItem) ->
					{
						if (color1 == -1) VehicleResprayGroupDialog.create(player, eventManager, listItem.getCurrentDialog(), vehicle, service, idx).show();
						else player.getVehicle().setColor(color1, idx);
					});
				}
			})
			.onClickOk((d, i) -> player.playSound(1083))
			.build();
	}
}
