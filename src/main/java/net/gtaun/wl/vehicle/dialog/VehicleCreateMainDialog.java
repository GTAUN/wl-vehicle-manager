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

import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.VehicleTextUtils;

import org.apache.commons.lang3.ArrayUtils;

public class VehicleCreateMainDialog extends WlListDialog
{
	public VehicleCreateMainDialog(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service)
	{
		super(player, eventManager);
		setParentDialog(parent);

		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		setCaption(stringSet.get("Dialog.VehicleCreateMainDialog.Caption"));
		setClickOkHandler((d, i) -> player.playSound(1083));

		addItem(stringSet.get("Dialog.VehicleCreateMainDialog.ItemAll"), (i) ->
		{
			int[] vehicleModelIds = ArrayUtils.toPrimitive(VehicleModel.getIds().toArray(new Integer[0]));
			new VehicleCreateListDialog(player, eventManager, this, service, stringSet.get("Dialog.VehicleCreateMainDialog.AllVehicleText"), vehicleModelIds).show();
		});

		for (Map.Entry<String, List<Integer>> entry : service.getCommonVehicles().entrySet())
		{
			String setName = stringSet.get(entry.getKey());
			int[] set = ArrayUtils.toPrimitive(entry.getValue().toArray(new Integer[0]));

			String itemName = stringSet.format("Dialog.VehicleCreateMainDialog.ItemCommon", setName);

			addItem(itemName, (d) ->
			{
				new VehicleCreateListDialog(player, eventManager, this, service, itemName, set).show();
			});
		}

		for (VehicleType type : VehicleType.values())
		{
			String typeName = VehicleTextUtils.getVehicleTypeName(stringSet, type);
			String itemName = stringSet.format("Dialog.VehicleCreateMainDialog.ItemType", typeName);

			addItem(itemName, (d) ->
			{
				int[] vehicleModelIds = ArrayUtils.toPrimitive(VehicleModel.getIds(type).toArray(new Integer[0]));
				new VehicleCreateListDialog(player, eventManager, VehicleCreateMainDialog.this, service, itemName, vehicleModelIds).show();
			});
		}
	}
}
