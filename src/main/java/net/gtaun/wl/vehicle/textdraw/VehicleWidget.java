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

package net.gtaun.wl.vehicle.textdraw;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.ColorUtils;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.UnitUtils;
import net.gtaun.wl.common.textdraw.TextDrawUtils;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic.StatisticType;

public class VehicleWidget extends AbstractPlayerContext
{
	private final VehicleManagerServiceImpl vehicleManagerService;
	
	private Timer timer;

	private PlayerTextdraw speedDisplay;
	private PlayerTextdraw unitDisplay;
	private PlayerTextdraw otherInfo;
	private PlayerTextdraw healthBar;
	
	
	public VehicleWidget(Shoebill shoebill, EventManager rootEventManager, Player player, VehicleManagerServiceImpl vehicleManagerService)
	{
		super(shoebill, rootEventManager, player);
		this.vehicleManagerService = vehicleManagerService;
	}

	@Override
	protected void onInit()
	{
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		speedDisplay = TextDrawUtils.createPlayerText(factory, player, 625, 410, "0");
		speedDisplay.setAlignment(TextDrawAlign.RIGHT);
		speedDisplay.setFont(TextDrawFont.PRICEDOWN);
		speedDisplay.setLetterSize(1.2f, 3.75f);
		speedDisplay.setShadowSize(2);
		speedDisplay.show();
		
		unitDisplay = TextDrawUtils.createPlayerText(factory, player, 635, 445, stringSet.get(player, "Textdraw.VehicleWidget.SpeedUnit"));
		unitDisplay.setAlignment(TextDrawAlign.RIGHT);
		unitDisplay.setFont(TextDrawFont.BANK_GOTHIC);
		unitDisplay.setLetterSize(0.3f, 1.2f);
		unitDisplay.setShadowSize(1);
		unitDisplay.show();
		
		otherInfo = TextDrawUtils.createPlayerText(factory, player, 638, 460, "-");
		otherInfo.setAlignment(TextDrawAlign.RIGHT);
		otherInfo.setFont(TextDrawFont.FONT2);
		otherInfo.setLetterSize(0.25f, 0.8f);
		otherInfo.setShadowSize(1);
		otherInfo.show();
		
		Color healthBarColor = new Color(255, 0, 0, 64);
		
		healthBar = TextDrawUtils.createPlayerText(factory, player, 0, 478, " ");
		healthBar.setUseBox(true);
		healthBar.setBoxColor(healthBarColor);
		healthBar.setLetterSize(1.0f, 0.5f);
		healthBar.show();
		
		timer = factory.createTimer(100);
		timer.setCallback(new TimerCallback()
		{
			@Override
			public void onTick(int factualInterval)
			{
				update();
			}
		});
		timer.start();

		addDestroyable(otherInfo);
		addDestroyable(unitDisplay);
		addDestroyable(speedDisplay);
		addDestroyable(healthBar);
		addDestroyable(timer);
		
		update();
	}

	@Override
	protected void onDestroy()
	{
		
	}
	
	private void update()
	{
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		OncePlayerVehicleStatistic stat = vehicleManagerService.getPlayerCurrentOnceStatistic(player);
		if (stat == null) return;
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		final float spd = stat.getCurrentSpeed() * 60 * 60 / 1000.0f;
		final float avgSpd = (float) (stat.getDriveOdometer() / stat.getDriveSecondCount()) * 60 * 60 / 1000.0f;
		final float maxSpd = stat.getMaxSpeed() * 60 * 60 / 1000.0f;
		final float dmg = (float) (stat.getDamageCount() / 10.0f);
		final float dist = (float) (stat.getDriveOdometer() / 1000.0f);
		final float vhp = vehicle.getHealth() / 10.0f;
		
		long seconds = stat.getDriveSecondCount() % 60;
		long minutes = (stat.getDriveSecondCount() / 60) % 60;
		long hours = stat.getDriveSecondCount() / 60 / 60;
		String formatedTime = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);

		String offMark = stringSet.get(player, "Textdraw.VehicleWidget.StatusMark.OffMark");
		boolean isAutoRepair = vehicleManagerService.getEffectivePlayerPreferences(player).isAutoRepair();
		String autoRepair = isAutoRepair ? stringSet.get(player, "Textdraw.VehicleWidget.StatusMark.AutoRepair") : offMark;
		String infiniteNitrous = vehicleManagerService.getEffectivePlayerPreferences(player).isInfiniteNitrous() ? stringSet.get(player, "Textdraw.VehicleWidget.StatusMark.InfiniteNitrous") : offMark;
		String autoFlip = vehicleManagerService.getEffectivePlayerPreferences(player).isAutoFlip() ? stringSet.get(player, "Textdraw.VehicleWidget.StatusMark.AutoFlip") : offMark;
		String lockDoor = vehicle.getState().getDoors()!=0 ? stringSet.get(player, "Textdraw.VehicleWidget.StatusMark.LockDoors") : offMark;

		speedDisplay.setText(stringSet.format(player, "Textdraw.VehicleWidget.SpeedFormat", spd, UnitUtils.kmToMi(spd)));
		
		String extMessage = "";
		if (stat.getType() == StatisticType.RACING) extMessage += stringSet.get(player, "Textdraw.VehicleWidget.RacingExtMessage") + " ";
		
		if (timer.getCount() % 5 == 0)
		{
			String formatKey = "Textdraw.VehicleWidget.BottomFormat";
			if (!isAutoRepair) formatKey = "Textdraw.VehicleWidget.IncludeHealthFormat";
			String text = stringSet.format(player, formatKey,
				formatedTime, dmg, vhp, dist, UnitUtils.kmToMi(dist),
				avgSpd, UnitUtils.kmToMi(avgSpd), maxSpd,  UnitUtils.kmToMi(maxSpd), autoRepair,
				infiniteNitrous, autoFlip, lockDoor, extMessage);
			otherInfo.setText(text);
		}

		Color healthBarColorRed = new Color(255, 0, 0, 160);
		Color healthBarColorGreen = new Color(0, 255, 0, 48);
		
		if (!isAutoRepair)
		{
			healthBar.setBoxColor(ColorUtils.colorBlend(healthBarColorRed, healthBarColorGreen, (int)(255*((vhp-25.0f)/75.0f))));
			healthBar.setTextSize(640.0f*((vhp-25.0f)/75.0f), 2.0f);
			healthBar.show();
		}
		else
		{
			if (healthBar.isShowed()) healthBar.hide();
		}
	}
}

