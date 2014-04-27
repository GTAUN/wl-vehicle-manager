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

import java.util.Date;
import java.util.List;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

import org.apache.commons.lang3.time.DateFormatUtils;

public class DrivingRecordStatisticDialog extends WlPageListDialog
{
	public DrivingRecordStatisticDialog(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service)
	{
		super(player, eventManager);
		setParentDialog(parent);
		
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		setCaption(stringSet.get("Dialog.DrivingRecordStatisticDialog.Caption"));
		
		service.getPlayerRecordedOnceStatistics(player).forEach((stat) ->
		{
			List<Integer> modelIds = stat.getModelIds();
			int modelId = modelIds.get(0);
			String modelName = modelIds.size() == 1 ? VehicleModel.getName(modelId) : stringSet.get("Statistic.MoveWay.Multiple");
			String startTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getStartTime());
			String endTimeStr = stringSet.get("Time.NA");
			
			Date endTime = stat.getEndTime();
			if (endTime != null) endTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getEndTime());
			
			String type;
			switch (stat.getType())
			{
			case DRIVER:	type = stringSet.get("Statistic.Type.Driver");		break;
			case PASSENGER:	type = stringSet.get("Statistic.Type.Passenger");	break;
			case RACING:	type = stringSet.get("Statistic.Type.Racing");		break;
			default:		type = stringSet.get("Statistic.Type.Unknown");		break;
			}
			
			String item = stringSet.format("Dialog.DrivingRecordStatisticDialog.Item", type, modelName, modelId, startTimeStr, endTimeStr);
			addItem(item, (i) ->
			{
				OnceStatisticDialog.create(player, eventManager, this, service, stat).show();
			});
		});
	}
}
