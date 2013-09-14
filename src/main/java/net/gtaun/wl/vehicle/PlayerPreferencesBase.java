package net.gtaun.wl.vehicle;

import net.gtaun.shoebill.object.Player;

public interface PlayerPreferencesBase
{
	Player getPlayer();
	
	boolean isInfiniteNitrous();
	boolean isAutoRepair();
	boolean isAutoFlip();
	boolean isAutoCarryPassengers();
	boolean isVehicleWidgetEnabled();
}
