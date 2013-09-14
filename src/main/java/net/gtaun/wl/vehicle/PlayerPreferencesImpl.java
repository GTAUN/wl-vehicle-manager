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

import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.event.PlayerPreferencesUpdateEvent;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("VehicleManagerPlayerPreferences")
public final class PlayerPreferencesImpl implements PlayerPreferences
{
	@Transient private EventManager eventManager;
	@Transient private Player player;
	
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
	
	PlayerPreferencesImpl(EventManager eventManager, Player player, String playerUniqueId)
	{
		this();
		this.eventManager = eventManager;
		this.player = player;
		this.playerUniqueId = playerUniqueId;
	}
	
	public void setContext(EventManager eventManager, Player player)
	{
		this.eventManager = eventManager;
		this.player = player;
	}
	
	private void dispatchUpdateEvent()
	{
		PlayerPreferencesUpdateEvent event = new PlayerPreferencesUpdateEvent(this);
		eventManager.dispatchEvent(event, player, this);
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
		if (unlimitedNOS == enabled) return;
		this.unlimitedNOS = enabled;
		dispatchUpdateEvent();
	}

	@Override
	public boolean isAutoRepair()
	{
		return autoRepair;
	}

	@Override
	public void setAutoRepair(boolean enabled)
	{
		if (autoRepair == enabled) return;
		this.autoRepair = enabled;
		dispatchUpdateEvent();
	}

	@Override
	public boolean isAutoFlip()
	{
		return autoFlip;
	}

	@Override
	public void setAutoFlip(boolean enabled)
	{
		if (autoFlip == enabled) return;
		this.autoFlip = enabled;
		dispatchUpdateEvent();
	}

	@Override
	public boolean isAutoCarryPassengers()
	{
		return autoCarryPassengers;
	}

	@Override
	public void setAutoCarryPassengers(boolean enabled)
	{
		if (autoCarryPassengers == enabled) return;
		this.autoCarryPassengers = enabled;
		dispatchUpdateEvent();
	}

	@Override
	public boolean isVehicleWidgetEnabled()
	{
		return speedometerWidgetEnabled;
	}
	
	@Override
	public void setVehicleWidgetEnabled(boolean enabled)
	{
		if (speedometerWidgetEnabled == enabled) return;
		speedometerWidgetEnabled = enabled;
		dispatchUpdateEvent();
	}
}
