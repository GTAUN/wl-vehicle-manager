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
