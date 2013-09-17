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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class PlayerVehicleStatisticDialog extends AbstractMsgboxDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerServiceImpl vehicleManagerService;
	
	
	public PlayerVehicleStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, rootEventManager, parentDialog);
		this.vehicle = vehicle;
		this.vehicleManagerService = vehicleManagerService;
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		PlayerVehicleStatistic stat = vehicleManagerService.getPlayerVehicleStatistic(player, modelId);
		
		this.caption = stringSet.format(player, "Dialog.PlayerVehicleStatisticDialog.Caption", name, modelId);
		String textFormat = stringSet.get(player, "Dialog.PlayerVehicleStatisticDialog.Text");

		double avgSpeed = stat.getDriveOdometer() / stat.getDriveTimeCount() * 60 * 60 / 1000.0f;
		double avgExpratePer10Minutes = stat.getDamageCount() / 750.0f / stat.getDriveTimeCount() * 60 * 10;
		
		long seconds = stat.getDriveTimeCount() % 60;
		long minutes = (stat.getDriveTimeCount() / 60) % 60;
		long hours = stat.getDriveTimeCount() / 60 / 60;
		String formatedTime = stringSet.format(player, "Time.HMS", hours, minutes, seconds);
		
		String lastUpdateString = stringSet.get(player, "Time.Never");
		Date lastUpdate = stat.getLastUpdate();
		if (lastUpdate != null) lastUpdateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(lastUpdate);
		
		String text = String.format
		(
			textFormat, caption, stat.getSpawnCount(), stat.getDamageCount()/1000.0f, stat.getDriveCount(), formatedTime,
			stat.getDriveOdometer()/1000.0f, avgSpeed, avgExpratePer10Minutes, lastUpdateString
		);
		
		show(text);
	}
}
