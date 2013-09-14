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
import net.gtaun.wl.common.textdraw.TextDrawUtils;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic.StatisticType;

public class VehicleWidget extends AbstractPlayerContext
{
	private final VehicleManagerService vehicleManager;
	
	private Timer timer;

	private PlayerTextdraw speedDisplay;
	private PlayerTextdraw unitDisplay;
	private PlayerTextdraw otherInfo;
	private PlayerTextdraw healthBar;
	
	
	public VehicleWidget(Shoebill shoebill, EventManager rootEventManager, Player player, VehicleManagerService vehicleManager)
	{
		super(shoebill, rootEventManager, player);
		this.vehicleManager = vehicleManager;
	}

	@Override
	protected void onInit()
	{
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		speedDisplay = TextDrawUtils.createPlayerText(factory, player, 625, 410, "0");
		speedDisplay.setAlignment(TextDrawAlign.RIGHT);
		speedDisplay.setFont(TextDrawFont.PRICEDOWN);
		speedDisplay.setLetterSize(1.2f, 3.75f);
		speedDisplay.setShadowSize(2);
		speedDisplay.show();
		
		unitDisplay = TextDrawUtils.createPlayerText(factory, player, 635, 445, "KPH");
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
		OncePlayerVehicleStatistic stat = vehicleManager.getPlayerCurrentOnceStatistic(player);
		if (stat == null) return;
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		final float spd = stat.getCurrentSpeed() * 60 * 60 / 1000.0f;
		final float avgSpd = (float) (stat.getDriveOdometer() / stat.getDriveSecondCount()) * 60 * 60 / 1000.0f;
		final float maxSpd = stat.getMaxSpeed() * 60 * 60 / 1000.0f;
		final float dmg = (float) (stat.getDamageCount() / 10.0f);
		final float metres = (float) (stat.getDriveOdometer() / 1000.0f);
		final float vhp = vehicle.getHealth() / 10.0f;
		
		long seconds = stat.getDriveSecondCount() % 60;
		long minutes = (stat.getDriveSecondCount() / 60) % 60;
		long hours = stat.getDriveSecondCount() / 60 / 60;
		String formatedTime = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);

		boolean isAutoRepair = vehicleManager.getEffectivePlayerPreferences(player).isAutoRepair();
		String autoRepair = isAutoRepair ? "~g~R" : "~w~-";
		String unlimitedNOS = vehicleManager.getEffectivePlayerPreferences(player).isUnlimitedNOS() ? "~r~N" : "~w~-";
		String autoFlip = vehicleManager.getEffectivePlayerPreferences(player).isAutoFlip() ? "~b~F" : "~w~-";
		String lockDoor = vehicle.getState().getDoors()!=0 ? "~y~D" : "~w~-";

		speedDisplay.setText(String.format("%1$1.0f", spd));
		
		String extMessage = "";
		if (stat.getType() == StatisticType.RACING) extMessage += "[RACING] ";
		
		if (timer.getCount() % 5 == 0)
		{
			String format = "%11$s%1$s [%7$s%8$s%9$s%10$s~w~]~n~Dmg: ~p~~h~%2$1.0f%%~w~ - Odo: ~g~~h~%4$1.2fKM~w~ - Avg/Max: ~y~~h~%5$1.1f~w~/~r~~h~%6$1.1f~w~ KPH";
			if (!isAutoRepair) format = "%11$s%1$s [%7$s%8$s%9$s%10$s~w~]~n~VHP: ~b~~h~%3$1.1f%%~w~ - Odo: ~g~~h~%4$1.2fKM~w~ - Avg/Max: ~y~~h~%5$1.1f~w~/~r~~h~%6$1.1f~w~ KPH";
			String text = String.format(format, formatedTime, dmg, vhp, metres, avgSpd, maxSpd, autoRepair, unlimitedNOS, autoFlip, lockDoor, extMessage);
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

