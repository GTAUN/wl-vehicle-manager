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

import java.util.Date;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractMsgboxDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class PlayerVehicleStatisticDialog extends AbstractMsgboxDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	
	
	public PlayerVehicleStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, rootEventManager, parentDialog);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
	}
	
	@Override
	public void show()
	{
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		PlayerVehicleStatistic stat = vehicleManager.getPlayerVehicleStatistic(player, modelId);
		
		String caption = String.format("%1$s: %2$s (模型: %3$d) 的个人统计信息", "车管", name, modelId);
		setCaption(caption);
		
		String textFormat = caption + "\n" +
						"累计刷车次数: %1$d\n" +
						"累计损伤花费: %2$1.1f辆\n" +
						"累计驾驶次数: %3$d次\n" +
						"累计驾驶时间: %4$s\n" +
						"累计驾驶里程: %5$1.3f公里\n" +
						"平均驾驶速度: %6$1.2fKM/H\n" +
						"平均爆车速率: %7$1.1f辆 / 10分钟\n" +
						"最后更新时间: %8$s";

		double avgSpeed = stat.getDriveOdometer() / stat.getDriveTimeCount() * 60 * 60 / 1000.0f;
		double avgExpratePer10Minutes = stat.getDamageCount() / 750.0f / stat.getDriveTimeCount() * 60 * 10;
		
		long seconds = stat.getDriveTimeCount() % 60;
		long minutes = (stat.getDriveTimeCount() / 60) % 60;
		long hours = stat.getDriveTimeCount() / 60 / 60;
		String formatedTime = String.format("%1$d小时 %2$d分 %3$d秒", hours, minutes, seconds);
		
		String lastUpdateString = "从未";
		Date lastUpdate = stat.getLastUpdate();
		if (lastUpdate != null) lastUpdateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(lastUpdate);
		
		String text = String.format
		(
			textFormat, stat.getSpawnCount(), stat.getDamageCount()/1000.0f, stat.getDriveCount(), formatedTime,
			stat.getDriveOdometer()/1000.0f, avgSpeed, avgExpratePer10Minutes, lastUpdateString
		);
		
		show(text);
	}
}
