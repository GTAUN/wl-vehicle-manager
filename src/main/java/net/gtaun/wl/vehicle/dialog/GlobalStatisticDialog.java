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

import java.util.Collection;
import java.util.Date;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractMsgboxDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class GlobalStatisticDialog extends AbstractMsgboxDialog
{
	private final VehicleManagerService vehicleManager;
	
	
	public GlobalStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, rootEventManager);
		this.vehicleManager = vehicleManager;
	}
	
	@Override
	public void show()
	{
		long spawnCount = 0, driveCount = 0, driveSecondCount = 0;
		double damageCount = 0.0f, driveOdometer = 0.0f;
		Date lastUpdate = new Date(0);
		
		Collection<GlobalVehicleStatistic> stats = vehicleManager.getGlobalVehicleStatistics();
		for (GlobalVehicleStatistic stat : stats)
		{
			spawnCount += stat.getSpawnCount();
			driveCount += stat.getDriveCount();
			driveSecondCount += stat.getDriveTimeCount();
			damageCount += stat.getDamageCount();
			driveOdometer += stat.getDriveOdometer();
			
			Date update = stat.getLastUpdate();
			if (update != null && lastUpdate.before(update)) lastUpdate = update;
		}
		
		this.caption = String.format("%1$s: 所有车辆的全局统计信息", "车管");
		
		String textFormat = caption + "\n" +
						"累计刷车次数: %1$d\n" +
						"累计损伤花费: %2$1.1f辆\n" +
						"累计驾驶次数: %3$d次\n" +
						"累计驾驶时间: %4$s\n" +
						"累计驾驶里程: %5$1.3f公里\n" +
						"平均驾驶速度: %6$1.2fKM/H\n" +
						"平均爆车速率: %7$1.1f辆 / 10分钟\n" +
						"最后更新时间: %8$s";

		double avgSpeed = driveOdometer / driveSecondCount * 60 * 60 / 1000.0f;
		double avgExpratePer10Minutes = damageCount / 750.0f / driveSecondCount * 60 * 10;
		
		long seconds = driveSecondCount % 60;
		long minutes = (driveSecondCount / 60) % 60;
		long hours = driveSecondCount / 60 / 60;
		String formatedTime = String.format("%1$d小时 %2$d分 %3$d秒", hours, minutes, seconds);

		String lastUpdateString = "从未";
		if (lastUpdate != null) lastUpdateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(lastUpdate);
		
		String text = String.format
		(
			textFormat, spawnCount, damageCount/1000.0f, driveCount, formatedTime,
			driveOdometer/1000.0f, avgSpeed, avgExpratePer10Minutes, lastUpdateString
		);
		
		show(text);
	}
}
