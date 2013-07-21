/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.vehicle.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

import org.apache.commons.lang3.ArrayUtils;

public class VehicleCreateMainDialog extends AbstractListDialog
{
	private static final Map<VehicleType, String> TYPE_NAMES = new HashMap<>();
	static
	{
		TYPE_NAMES.put(VehicleType.UNKNOWN,			"其他");
		TYPE_NAMES.put(VehicleType.BICYCLE,			"自行车");
		TYPE_NAMES.put(VehicleType.MOTORBIKE,		"摩托车");
		TYPE_NAMES.put(VehicleType.CAR,				"普通车子");
		TYPE_NAMES.put(VehicleType.TRAILER,			"拖车");
		TYPE_NAMES.put(VehicleType.REMOTE_CONTROL,	"遥控装置");
		TYPE_NAMES.put(VehicleType.TRAIN,			"火车");
		TYPE_NAMES.put(VehicleType.BOAT,			"船只");
		TYPE_NAMES.put(VehicleType.AIRCRAFT,		"飞机");
		TYPE_NAMES.put(VehicleType.HELICOPTER,		"直升机");
		TYPE_NAMES.put(VehicleType.TANK,			"坦克");
	}
	
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
	
	
	private final VehicleManagerService vehicleManager;
	
	
	public VehicleCreateMainDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		
		setCaption(String.format("%1$s: 刷车 - 车辆类型选择", "车管"));
		
		dialogListItems.add(new DialogListItem("列出所有车辆 - 按人气排序")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				int[] vehicleModelIds = ArrayUtils.toPrimitive(VehicleModel.getIds().toArray(new Integer[0]));
				new VehicleCreateSetListDialog(player, shoebill, eventManager, vehicleManager, "所有车辆", vehicleModelIds).show();
				destroy();
			}
		});
		
		for (Entry<String, int[]> entry : COMMON_VEHICLES.entrySet())
		{
			final String setname = entry.getKey();
			final int[] set = entry.getValue();
			
			dialogListItems.add(new DialogListItem("常用列表: " + setname)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new VehicleCreateSetListDialog(player, shoebill, eventManager, vehicleManager, setname, set).show();
					destroy();
				}
			});
		}
		
		for (Map.Entry<VehicleType, String> entry : TYPE_NAMES.entrySet())
		{
			final VehicleType type = entry.getKey();
			final String typename = entry.getValue();
			
			dialogListItems.add(new DialogListItem("类型: " + typename)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new VehicleCreateTypeListDialog(player, shoebill, eventManager, vehicleManager, typename, type).show();
					destroy();
				}
			});
		}
	}

	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			new VehicleManagerDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
