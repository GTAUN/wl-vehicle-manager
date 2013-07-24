package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import net.gtaun.shoebill.object.Player;

public interface OncePlayerVehicleStatistic
{
	Player getPlayer();
	boolean isActive();
	
	int getModelId();
	double getDamageCount();
	long getDriveSecondCount();
	double getDriveOdometer();
	float getMaxSpeed();
	
	Date getStartTime();
	Date getEndTime();
	Date getLastUpdate();
}
