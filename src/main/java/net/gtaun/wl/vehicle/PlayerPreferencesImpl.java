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

package net.gtaun.wl.vehicle;

import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("VehicleManagerPlayerPreferences")
public final class PlayerPreferencesImpl implements PlayerPreferences
{
	interface VehicleWidgetSwitchCallback
	{
		void onSwitch();
	}
	
	
	@Transient private Player player;
	@Transient private VehicleWidgetSwitchCallback vehicleWidgetSwitchCallback;
	
	@Id private ObjectId objectId;
	
	@Indexed private String playerUniqueId;		// 玩家唯一ID
	
	private boolean unlimitedNOS;
	private boolean autoRepair;
	private boolean autoFlip;
	private boolean autoCarryPassengers;
	private boolean speedometerWidgetEnabled;
	
	
	protected PlayerPreferencesImpl()
	{
		autoCarryPassengers = true;
		speedometerWidgetEnabled = true;
	}
	
	PlayerPreferencesImpl(Player player, String playerUniqueId)
	{
		this();
		this.player = player;
		this.playerUniqueId = playerUniqueId;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void setVehicleWidgetSwitchCallback(VehicleWidgetSwitchCallback widgetSwitchCallback)
	{
		this.vehicleWidgetSwitchCallback = widgetSwitchCallback;
	}
	
	@Override
	public Player getPlayer()
	{
		return player;
	}

	@Override
	public boolean isUnlimitedNOS()
	{
		return unlimitedNOS;
	}

	@Override
	public void setUnlimitedNOS(boolean enabled)
	{
		this.unlimitedNOS = enabled;
		if (unlimitedNOS)
		{
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null) return;
			
			if (VehicleComponentModel.isVehicleSupported(vehicle.getModelId(), VehicleComponentModel.NITRO_10_TIMES))
			{
				vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
			}
		}
	}

	@Override
	public boolean isAutoRepair()
	{
		return autoRepair;
	}

	@Override
	public void setAutoRepair(boolean enabled)
	{
		this.autoRepair = enabled;
		if (autoRepair)
		{
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null) return;
			
			if (vehicle.getHealth() < 1000.0f) vehicle.repair();
		}
	}

	@Override
	public boolean isAutoFlip()
	{
		return autoFlip;
	}

	@Override
	public void setAutoFlip(boolean enabled)
	{
		this.autoFlip = enabled;
	}

	@Override
	public boolean isAutoCarryPassengers()
	{
		return autoCarryPassengers;
	}

	@Override
	public void setAutoCarryPassengers(boolean enabled)
	{
		this.autoCarryPassengers = enabled;
	}

	@Override
	public boolean isVehicleWidgetEnabled()
	{
		return speedometerWidgetEnabled;
	}
	
	@Override
	public void setVehicleWidgetEnabled(boolean enabled)
	{
		this.speedometerWidgetEnabled = enabled;
		vehicleWidgetSwitchCallback.onSwitch();
	}
}
