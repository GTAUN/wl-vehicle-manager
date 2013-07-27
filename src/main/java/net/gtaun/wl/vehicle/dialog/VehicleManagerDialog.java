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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceDescription;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.util.DistanceVehicleFilter;
import net.gtaun.wl.vehicle.util.NearbyVehicleComparator;

public class VehicleManagerDialog extends AbstractListDialog
{
	public VehicleManagerDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager)
	{
		this(player, shoebill, eventManager, null, vehicleManager);
	}
	
	public VehicleManagerDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager, parentDialog);
		
		setCaption("车辆管理系统");

		dialogListItems.add(new DialogListItem("当前车辆 ...")
		{
			@Override
			public boolean isEnabled()
			{
				return player.isInAnyVehicle() && vehicleManager.getOwnedVehicle(player) != player.getVehicle();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				Vehicle vehicle = player.getVehicle();
				if (vehicle != null) new VehicleDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicle, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("我的车辆 ...")
		{
			@Override
			public boolean isEnabled()
			{
				return vehicleManager.getOwnedVehicle(player) != null;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				Vehicle vehicle = vehicleManager.getOwnedVehicle(player);
				if (vehicle != null) new VehicleDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicle, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("刷车 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new VehicleCreateMainDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("搜寻附近空车 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Location loc = player.getLocation();
				new EmptyVehicleListDialog
				(
					player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManager,
					new NearbyVehicleComparator(loc), new DistanceVehicleFilter(loc, 500.0f)
				).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("驾驶和乘坐记录 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new RecordedOnceStatisticDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("个人偏好设置 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new PlayerPreferencesDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("显示个人统计信息")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new PlayerStatisticDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("显示全局统计信息")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new GlobalStatisticDialog(player, shoebill, eventManager, VehicleManagerDialog.this, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("命令帮助信息")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				String caption = String.format("%1$s: %2$s", "车管", "命令帮助信息");
				new MsgboxDialog(player, shoebill, eventManager, VehicleManagerDialog.this, caption, "偷懒中，暂无帮助信息……").show();
			}
		});
		
		dialogListItems.add(new DialogListItem("快捷键帮助信息")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				String caption = String.format("%1$s: %2$s", "车管", "快捷键帮助信息");
				new MsgboxDialog(player, shoebill, eventManager, VehicleManagerDialog.this, caption, "偷懒中，暂无帮助信息……").show();
			}
		});
		
		dialogListItems.add(new DialogListItem("关于车管系统")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Plugin plugin = vehicleManager.getPlugin();
				ResourceDescription desc = plugin.getDescription();
				
				String caption = String.format("%1$s: %2$s", "车管", "关于车管系统");
				String format =
					"--- 新未来世界 车辆管理系统组件 ---\n" +
					"版本: %1$s (Build %2$d)\n" +
					"编译时间: %3$s\n\n" +
					"开发: mk124\n" +
					"功能设计: mk124\n" +
					"设计顾问: 52_PLA(aka. Yin.J), [ITC]1314, [ITC]KTS\n" +
					"数据采集: mk124, 52_PLA\n" +
					"测试: 52_PLA, [ITC]1314, [ITC]KTS, SMALL_KR\n" +
					"感谢: vvg, yezhizhu, Luck, Waunny\n\n" +
					"本组件是新未来世界项目的一部分。\n" +
					"本组件使用 GPL v2 许可证开放源代码。\n" +
					"本组件禁止在任何商业或盈利性服务器上使用。\n";
				String message = String.format(format, desc.getVersion(), desc.getBuildNumber(), desc.getBuildDate());
				
				new MsgboxDialog(player, shoebill, eventManager, VehicleManagerDialog.this, caption, message).show();
			}
		});
	}

	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			showParentDialog();
		}
		
		super.onDialogResponse(event);
	}
}
