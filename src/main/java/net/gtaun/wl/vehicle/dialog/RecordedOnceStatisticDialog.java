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
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class RecordedOnceStatisticDialog extends AbstractPageListDialog
{
	public RecordedOnceStatisticDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.caption = String.format("%1$s: 驾驶和乘坐记录", "车管");
		
		List<OncePlayerVehicleStatistic> stats = vehicleManager.getPlayerRecordedOnceStatistics(player);
		
		for (final OncePlayerVehicleStatistic stat : stats)
		{
			List<Integer> modelIds = stat.getModelIds();
			final int modelId = modelIds.get(0);
			final String modelName = modelIds.size() == 1 ? VehicleModel.getName(modelId) : "多种移动方式";
			String startTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getStartTime());
			String endTimeStr = "未知";
			
			Date endTime = stat.getEndTime();
			if (endTime != null) endTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getEndTime());
			
			String type;
			switch (stat.getType())
			{
			case DRIVER:		type = "驾驶";	break;
			case PASSENGER:		type = "乘坐";	break;
			case RACING:		type = "赛车";	break;
			default:			type = "未知";	break;
			}
			
			String item = String.format("[%1$s记录] %2$s (%3$d) - %4$s ~ %5$s", type, modelName, modelId, startTimeStr, endTimeStr);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					new OnceStatisticDialog(player, shoebill, eventManager, RecordedOnceStatisticDialog.this, vehicleManager, stat).show();
				}
			});
		}
	}
}
