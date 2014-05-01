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
import net.gtaun.wl.common.UnitUtils;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class OnceStatisticDialog
{
	public static WlMsgboxDialog create(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service, OncePlayerVehicleStatistic stat)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);

		List<Integer> modelIds = stat.getModelIds();
		int modelId = modelIds.get(0);
		String name = modelIds.size() == 1 ? VehicleModel.getName(modelId) : stringSet.get("Statistic.MoveWay.Multiple");
	
		String type;
		switch (stat.getType())
		{
		case DRIVER:	type = stringSet.get("Statistic.Type.Driver");		break;
		case PASSENGER:	type = stringSet.get("Statistic.Type.Passenger");	break;
		case RACING:	type = stringSet.get("Statistic.Type.Racing");		break;
		default:		type = stringSet.get("Statistic.Type.Unknown");		break;
		}

		Date startTime = stat.getStartTime();
		String startTimeStr = (startTime == null) ?
			stringSet.get("Time.NA") : DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(startTime);
		
		Date endTime = stat.getEndTime();
		String endTimeStr = (endTime == null) ?
			stringSet.get("Time.NA") : DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(endTime);
		
		return WlMsgboxDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption((d) -> stringSet.format("Dialog.OnceStatisticDialog.Caption", name, modelId, type, startTimeStr, endTimeStr))
			.message((d) ->
			{
				double odometer = stat.getDriveOdometer() / 1000.0f;
				double avgSpeed = odometer / stat.getDriveSecondCount() * 60 * 60;
				double maxSpeed = stat.getMaxSpeed() * 60 * 60 / 1000.0f;
				double avgScrapePer10Minutes = stat.getDamageCount() / 750.0f / stat.getDriveSecondCount() * 60 * 10;
				
				long seconds = stat.getDriveSecondCount() % 60;
				long minutes = (stat.getDriveSecondCount() / 60) % 60;
				long hours = stat.getDriveSecondCount() / 60 / 60;
				String formatedTime = stringSet.format("Time.HMS", hours, minutes, seconds);
				
				String modelIdMessage = "";
				for (int mid : modelIds)
				{
					if (mid != 0) modelIdMessage += VehicleModel.getName(mid) + "(" + mid + "), ";
					else modelIdMessage += stringSet.get("Statistic.MoveWay.OnFoot") + ", ";
				}
				modelIdMessage = modelIdMessage.substring(0, modelIdMessage.length()-2);
				
				String textFormat = stringSet.get("Dialog.OnceStatisticDialog.Text");
				String text = String.format
				(
					textFormat,
					d.getCaption(), type, modelIdMessage, stat.getDamageCount()/1000.0f, formatedTime,
					odometer, UnitUtils.kmToMi(odometer), avgSpeed, UnitUtils.kmToMi(avgSpeed), maxSpeed,
					UnitUtils.kmToMi(maxSpeed), avgScrapePer10Minutes, startTimeStr, endTimeStr
				);
				
				return text;
			})
			.build();
	}
}
