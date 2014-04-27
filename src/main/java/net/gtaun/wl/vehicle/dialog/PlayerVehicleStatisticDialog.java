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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.UnitUtils;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class PlayerVehicleStatisticDialog
{
	public static WlMsgboxDialog create(Player player, EventManager eventManager, AbstractDialog parent, Vehicle vehicle, VehicleManagerServiceImpl service)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);

		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		return WlMsgboxDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption((d) -> stringSet.format("Dialog.PlayerVehicleStatisticDialog.Caption", name, modelId))
			.message((d) ->
			{
				PlayerVehicleStatistic stat = service.getPlayerVehicleStatistic(player, modelId);
				
				String textFormat = stringSet.get("Dialog.PlayerVehicleStatisticDialog.Text");

				double odometer = stat.getDriveOdometer() / 1000.0f;
				double avgSpeed = odometer / stat.getDriveTimeCount() * 60 * 60;
				double avgScrapePer10Minutes = stat.getDamageCount() / 750.0f / stat.getDriveTimeCount() * 60 * 10;
				
				long seconds = stat.getDriveTimeCount() % 60;
				long minutes = (stat.getDriveTimeCount() / 60) % 60;
				long hours = stat.getDriveTimeCount() / 60 / 60;
				String formatedTime = stringSet.format("Time.HMS", hours, minutes, seconds);
				
				String lastUpdateString = stringSet.get("Time.Never");
				Date lastUpdate = stat.getLastUpdate();
				if (lastUpdate != null) lastUpdateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(lastUpdate);
				
				String text = String.format
				(
					textFormat,
					d.getCaption(), stat.getSpawnCount(), stat.getDamageCount()/1000.0f, stat.getDriveCount(), formatedTime,
					odometer, UnitUtils.kmToMi(odometer), avgSpeed, UnitUtils.kmToMi(avgSpeed), avgScrapePer10Minutes,
					lastUpdateString
				);
				
				return text;
			})
			.build();
	}
}
