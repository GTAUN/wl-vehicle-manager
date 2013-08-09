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
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractMsgboxDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class OnceStatisticDialog extends AbstractMsgboxDialog
{
	private final OncePlayerVehicleStatistic stat;
	
	
	public OnceStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, VehicleManagerService vehicleManager, OncePlayerVehicleStatistic stat)
	{
		super(player, shoebill, rootEventManager, parentDialog);
		this.stat = stat;
	}
	
	@Override
	public void show()
	{
		int modelId = stat.getModelId();
		String name = VehicleModel.getName(modelId);

		String startTimeStr = "N/A";
		Date startTime = stat.getStartTime();
		if (startTime != null) startTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(startTime);
		
		String endTimeStr = "N/A";
		Date endTime = stat.getEndTime();
		if (endTime != null) endTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(endTime);
		
		String type;
		switch (stat.getType())
		{
		case DRIVER:		type = "驾驶";	break;
		case PASSENGER:		type = "乘坐";	break;
		case RACING:		type = "赛车";	break;
		default:			type = "未知";	break;
		}
		
		this.caption = String.format("%1$s: %2$s (%3$d) 的%4$s记录信息 (%5$s~%6$s)", "车管", name, modelId, type, startTimeStr, endTimeStr);
		
		String textFormat = caption + "\n" +
						"累计损伤花费: %2$1.1f辆\n" +
						"累计%1$s时间: %3$s\n" +
						"累计%1$s里程: %4$1.3f公里\n" +
						"平均%1$s速度: %5$1.2fKM/H\n" +
						"最高%1$s速度: %6$1.2fKM/H\n" +
						"平均爆车速率: %7$1.1f辆 / 10分钟\n" +
						"开始%1$s时间: %8$s\n" + 
						"停止%1$s时间: %9$s";

		double avgSpeed = stat.getDriveOdometer() / stat.getDriveSecondCount() * 60 * 60 / 1000.0f;
		double maxSpeed = stat.getMaxSpeed() * 60 * 60 / 1000.0f;
		double avgExpratePer10Minutes = stat.getDamageCount() / 750.0f / stat.getDriveSecondCount() * 60 * 10;
		
		long seconds = stat.getDriveSecondCount() % 60;
		long minutes = (stat.getDriveSecondCount() / 60) % 60;
		long hours = stat.getDriveSecondCount() / 60 / 60;
		String formatedTime = String.format("%1$d小时 %2$d分 %3$d秒", hours, minutes, seconds);
		
		String text = String.format
		(
			textFormat, type, stat.getDamageCount()/1000.0f, formatedTime, stat.getDriveOdometer()/1000.0f,
			avgSpeed, maxSpeed, avgExpratePer10Minutes, startTimeStr, endTimeStr
		);
		
		show(text);
	}
}
