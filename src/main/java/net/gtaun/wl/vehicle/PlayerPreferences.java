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

package net.gtaun.wl.vehicle;

import net.gtaun.shoebill.object.Player;

public interface PlayerPreferences
{
	Player getPlayer();
	
	boolean isUnlimitedNOS();
	void setUnlimitedNOS(boolean enabled);
	
	boolean isAutoRepair();
	void setAutoRepair(boolean enabled);
	
	boolean isAutoFlip();
	void setAutoFlip(boolean enabled);
	
	boolean isAutoCarryPassengers();
	void setAutoCarryPassengers(boolean enabled);
	
	boolean isSpeedometerWidgetEnabled();
	void setSpeedometerWidgetEnabled(boolean enabled);
}
