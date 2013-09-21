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

import java.util.Collection;
import java.util.Date;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.UnitUtils;
import net.gtaun.wl.common.dialog.AbstractMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class PlayerStatisticDialog extends AbstractMsgboxDialog
{
	private final VehicleManagerServiceImpl vehicleManagerService;
	
	
	public PlayerStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, final VehicleManagerServiceImpl vehicleManagerService)
	{
		super(player, shoebill, rootEventManager, parentDialog);
		this.vehicleManagerService = vehicleManagerService;
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		long spawnCount = 0, driveCount = 0, driveSecondCount = 0;
		double damageCount = 0.0f, driveOdometer = 0.0f;
		Date lastUpdate = new Date(0);
		
		Collection<PlayerVehicleStatistic> stats = vehicleManagerService.getPlayerVehicleStatistics(player);
		for (PlayerVehicleStatistic stat : stats)
		{
			spawnCount += stat.getSpawnCount();
			driveCount += stat.getDriveCount();
			driveSecondCount += stat.getDriveTimeCount();
			damageCount += stat.getDamageCount();
			driveOdometer += stat.getDriveOdometer();
			
			Date update = stat.getLastUpdate();
			if (update != null && lastUpdate.before(update)) lastUpdate = update;
		}
		
		this.caption = stringSet.get(player, "Dialog.PlayerStatisticDialog.Caption");
		String textFormat = stringSet.get(player, "Dialog.PlayerStatisticDialog.Text");

		double odometer = driveOdometer / 1000.0f;
		double avgSpeed = odometer / driveSecondCount * 60 * 60;
		double avgScrapePer10Minutes = damageCount / 750.0f / driveSecondCount * 60 * 10;
		
		long seconds = driveSecondCount % 60;
		long minutes = (driveSecondCount / 60) % 60;
		long hours = driveSecondCount / 60 / 60;
		String formatedTime = stringSet.format(player, "Time.HMS", hours, minutes, seconds);

		String lastUpdateString = stringSet.get(player, "Time.Never");
		if (lastUpdate != null) lastUpdateString = DateFormatUtils.ISO_DATETIME_FORMAT.format(lastUpdate);
		
		String text = String.format
		(
			textFormat,
			caption, spawnCount, damageCount/1000.0f, driveCount, formatedTime,
			odometer, UnitUtils.kmToMi(odometer), avgSpeed, UnitUtils.kmToMi(avgSpeed), avgScrapePer10Minutes,
			lastUpdateString
		);
		
		show(text);
	}
}
