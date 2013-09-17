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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.util.VehicleTextUtils;

import org.apache.commons.lang3.ArrayUtils;

public class VehicleCreateMainDialog extends AbstractListDialog
{
	private static final Map<String, int[]> COMMON_VEHICLES = new HashMap<>();
	static
	{
		// 数据提供 by yinjin116s (aka. 52_PLA)
		COMMON_VEHICLES.put("跑车/肌肉车",	new int[] {602, 429, 402, 506, 477, 439, 480, 555, 475, 603});
		COMMON_VEHICLES.put("超级跑车",		new int[] {541, 415, 411, 451,558});
		COMMON_VEHICLES.put("运动跑车",		new int[] {562, 589, 496, 565, 559, 587, 560, 550});
		COMMON_VEHICLES.put("SUV",			new int[] {579, 400, 500, 444, 556, 557, 470, 505, 495});
		COMMON_VEHICLES.put("低底盘车",		new int[] {536, 575, 534, 567, 576, 412});
		COMMON_VEHICLES.put("卡车",			new int[] {535, 524, 499, 422, 609, 578, 455, 403, 443, 514, 600, 515, 440, 605, 478, 456, 554});
		COMMON_VEHICLES.put("赛车",			new int[] {504, 502, 503});
		COMMON_VEHICLES.put("轿车",			new int[] {566, 533, 445, 401, 518, 527, 542, 507, 585, 526, 466, 492, 474, 546, 517, 410, 516, 467, 426, 436, 547, 405, 580, 549, 540, 491, 421, 529});
		COMMON_VEHICLES.put("旅行车",		new int[] {404, 479, 458, 561});
		COMMON_VEHICLES.put("HPV",			new int[] {418, 482, 582, 413, 459});
		COMMON_VEHICLES.put("特殊车辆",		new int[] {568, 424, 573, 531, 408, 552});
	}
	
	
	public VehicleCreateMainDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		this.caption = stringSet.get(player, "Dialog.VehicleCreateMainDialog.Caption");
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleCreateMainDialog.ItemAll"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				int[] vehicleModelIds = ArrayUtils.toPrimitive(VehicleModel.getIds().toArray(new Integer[0]));
				new VehicleCreateListDialog(player, shoebill, eventManager, VehicleCreateMainDialog.this, vehicleManagerService, stringSet.get(player, "Dialog.VehicleCreateMainDialog.AllVehicleText"), vehicleModelIds).show();
			}
		});
		
		for (Entry<String, int[]> entry : COMMON_VEHICLES.entrySet())
		{
			final String setname = entry.getKey();
			final int[] set = entry.getValue();
			
			final String itemName = stringSet.format(player, "Dialog.VehicleCreateMainDialog.ItemCommon", setname);
			dialogListItems.add(new DialogListItem(itemName)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new VehicleCreateListDialog(player, shoebill, eventManager, VehicleCreateMainDialog.this, vehicleManagerService, itemName, set).show();
				}
			});
		}
		
		for (final VehicleType type : VehicleType.values())
		{			
			final String typename = VehicleTextUtils.getVehicleTypeName(stringSet, player, type);
			final String itemName = stringSet.format(player, "Dialog.VehicleCreateMainDialog.ItemType", typename);
			dialogListItems.add(new DialogListItem(itemName)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					int[] vehicleModelIds = ArrayUtils.toPrimitive(VehicleModel.getIds(type).toArray(new Integer[0]));
					new VehicleCreateListDialog(player, shoebill, eventManager, VehicleCreateMainDialog.this, vehicleManagerService, itemName, vehicleModelIds).show();
				}
			});
		}
	}
}
